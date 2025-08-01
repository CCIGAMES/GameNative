package com.winlator.winhandler;

import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

// import com.winlator.XServerDisplayActivity;
import com.winlator.core.StringUtils;
// import com.winlator.inputcontrols.ControlsProfile;
// import com.winlator.inputcontrols.ExternalController;
import com.winlator.inputcontrols.ExternalController;
import com.winlator.inputcontrols.TouchMouse;
import com.winlator.math.XForm;
import com.winlator.widget.XServerView;
import com.winlator.xserver.Pointer;
import com.winlator.xserver.XKeycode;
import com.winlator.xserver.XServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

public class WinHandler {
    private static final short SERVER_PORT = 7947;
    private static final short CLIENT_PORT = 7946;
    public static final byte DINPUT_MAPPER_TYPE_STANDARD = 0;
    public static final byte DINPUT_MAPPER_TYPE_XINPUT = 1;
    private DatagramSocket socket;
    private final ByteBuffer sendData = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
    private final ByteBuffer receiveData = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
    private final DatagramPacket sendPacket = new DatagramPacket(sendData.array(), 64);
    private final DatagramPacket receivePacket = new DatagramPacket(receiveData.array(), 64);
    private final ArrayDeque<Runnable> actions = new ArrayDeque<>();
    private boolean initReceived = false;
    private boolean running = false;
    private OnGetProcessInfoListener onGetProcessInfoListener;
    private ExternalController currentController;
    private InetAddress localhost;
    private byte dinputMapperType = DINPUT_MAPPER_TYPE_XINPUT;
    // private final XServerDisplayActivity activity;
    private final List<Integer> gamepadClients = new CopyOnWriteArrayList<>();

    private final XServer xServer;
    private final XServerView xServerView;

    // public WinHandler(XServerDisplayActivity activity) {
    //     this.activity = activity;
    // }
    public WinHandler(XServer xServer, XServerView xServerView) {
        this.xServer = xServer;
        this.xServerView = xServerView;
    }

    private boolean sendPacket(int port) {
        try {
            int size = sendData.position();
            if (size == 0) return false;
            sendPacket.setAddress(localhost);
            sendPacket.setPort(port);
            socket.send(sendPacket);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public void exec(String command) {
        command = command.trim();
        if (command.isEmpty()) return;
        String[] cmdList = command.split(" ", 2);
        final String filename = cmdList[0];
        final String parameters = cmdList.length > 1 ? cmdList[1] : "";

        addAction(() -> {
            byte[] filenameBytes = filename.getBytes();
            byte[] parametersBytes = parameters.getBytes();

            sendData.rewind();
            sendData.put(RequestCodes.EXEC);
            sendData.putInt(filenameBytes.length + parametersBytes.length + 8);
            sendData.putInt(filenameBytes.length);
            sendData.putInt(parametersBytes.length);
            sendData.put(filenameBytes);
            sendData.put(parametersBytes);
            sendPacket(CLIENT_PORT);
        });
    }

    public void killProcess(final String processName) {
        addAction(() -> {
            sendData.rewind();
            sendData.put(RequestCodes.KILL_PROCESS);
            byte[] bytes = processName.getBytes();
            sendData.putInt(bytes.length);
            sendData.put(bytes);
            sendPacket(CLIENT_PORT);
        });
    }

    public void listProcesses() {
        addAction(() -> {
            sendData.rewind();
            sendData.put(RequestCodes.LIST_PROCESSES);
            sendData.putInt(0);

            if (!sendPacket(CLIENT_PORT) && onGetProcessInfoListener != null) {
                onGetProcessInfoListener.onGetProcessInfo(0, 0, null);
            }
        });
    }

    public void setProcessAffinity(final String processName, final int affinityMask) {
        addAction(() -> {
            byte[] bytes = processName.getBytes();
            sendData.rewind();
            sendData.put(RequestCodes.SET_PROCESS_AFFINITY);
            sendData.putInt(9 + bytes.length);
            sendData.putInt(0);
            sendData.putInt(affinityMask);
            sendData.put((byte)bytes.length);
            sendData.put(bytes);
            sendPacket(CLIENT_PORT);
        });
    }

    public void setProcessAffinity(final int pid, final int affinityMask) {
        addAction(() -> {
            sendData.rewind();
            sendData.put(RequestCodes.SET_PROCESS_AFFINITY);
            sendData.putInt(9);
            sendData.putInt(pid);
            sendData.putInt(affinityMask);
            sendData.put((byte)0);
            sendPacket(CLIENT_PORT);
        });
    }

    public void mouseEvent(int flags, int dx, int dy, int wheelDelta) {
        if (!initReceived) return;
        addAction(() -> {
            sendData.rewind();
            sendData.put(RequestCodes.MOUSE_EVENT);
            sendData.putInt(10);
            sendData.putInt(flags);
            sendData.putShort((short)dx);
            sendData.putShort((short)dy);
            sendData.putShort((short)wheelDelta);
            sendData.put((byte)((flags & MouseEventFlags.MOVE) != 0 ? 1 : 0)); // cursor pos feedback
            sendPacket(CLIENT_PORT);
        });
    }

    public void keyboardEvent(byte vkey, int flags) {
        if (!initReceived) return;
        addAction(() -> {
            sendData.rewind();
            sendData.put(RequestCodes.KEYBOARD_EVENT);
            sendData.put(vkey);
            sendData.putInt(flags);
            sendPacket(CLIENT_PORT);
        });
    }

    public void bringToFront(final String processName) {
        bringToFront(processName, 0);
    }

    public void bringToFront(final String processName, final long handle) {
        addAction(() -> {
            sendData.rewind();
            sendData.put(RequestCodes.BRING_TO_FRONT);
            byte[] bytes = processName.getBytes();
            sendData.putInt(bytes.length);
            sendData.put(bytes);
            sendData.putLong(handle);
            sendPacket(CLIENT_PORT);
        });
    }

    private void addAction(Runnable action) {
        synchronized (actions) {
            actions.add(action);
            actions.notify();
        }
    }

    public OnGetProcessInfoListener getOnGetProcessInfoListener() {
        return onGetProcessInfoListener;
    }

    public void setOnGetProcessInfoListener(OnGetProcessInfoListener onGetProcessInfoListener) {
        synchronized (actions) {
            this.onGetProcessInfoListener = onGetProcessInfoListener;
        }
    }

    private void startSendThread() {
        Log.d("WinHandler", "Starting send thread...");
        Executors.newSingleThreadExecutor().execute(() -> {
            while (running) {
                synchronized (actions) {
                    // Log.d("WinHandler", "Running actions until empty");
                    while (initReceived && !actions.isEmpty()) actions.poll().run();
                    try {
                        // Log.d("WinHandler", "Waiting for actions");
                        actions.wait();
                    }
                    catch (InterruptedException e) {
                        Log.e("WinHandler", "Sent action was interrupted: " + e);
                    }
                }
            }
        });
    }

    public void stop() {
        Log.d("WinHandler", "Stopping...");
        running = false;

        if (socket != null) {
            socket.close();
            socket = null;
        }

        synchronized (actions) {
            actions.notify();
        }
    }

    private void handleRequest(byte requestCode, final int port) {
        // Log.d("WinHandler", "Handling request: " + requestCode);
        switch (requestCode) {
            case RequestCodes.INIT: {
                Log.d("WinHandler", "Request was init");
                initReceived = true;

                synchronized (actions) {
                    actions.notify();
                }
                break;
            }
            case RequestCodes.GET_PROCESS: {
                Log.d("WinHandler", "Request was get process");
                if (onGetProcessInfoListener == null) return;
                receiveData.position(receiveData.position() + 4);
                int numProcesses = receiveData.getShort();
                int index = receiveData.getShort();
                int pid = receiveData.getInt();
                long memoryUsage = receiveData.getLong();
                int affinityMask = receiveData.getInt();
                boolean wow64Process = receiveData.get() == 1;

                byte[] bytes = new byte[32];
                receiveData.get(bytes);
                String name = StringUtils.fromANSIString(bytes);

                onGetProcessInfoListener.onGetProcessInfo(index, numProcesses, new ProcessInfo(pid, name, memoryUsage, affinityMask, wow64Process));
                break;
            }
            case RequestCodes.GET_GAMEPAD: {
                // Log.d("WinHandler", "Request was get gamepad on port " + port);
                boolean isXInput = receiveData.get() == 1;
                boolean notify = receiveData.get() == 1;
                // final ControlsProfile profile = activity.getInputControlsView().getProfile();
                // boolean useVirtualGamepad = profile != null && profile.isVirtualGamepad();

                // ArrayList<ExternalController> controllers = ExternalController.getControllers();
                // for (ExternalController controller : controllers) {
                //
                // }
                // if (!useVirtualGamepad && (currentController == null || !currentController.isConnected())) {
                if (currentController == null || !currentController.isConnected()) {
                    currentController = ExternalController.getController(0);
                    Log.d("WinHandler", "Setting current external controller to " + currentController);
                }

                final boolean enabled = currentController != null;

                if (enabled && notify) {
                    // Log.d("WinHandler", "Creating gamepad client on port " + port + " if not already existing");
                    if (!gamepadClients.contains(port)) gamepadClients.add(port);
                }
                else gamepadClients.remove(Integer.valueOf(port));

                addAction(() -> {
                    sendData.rewind();
                    sendData.put(RequestCodes.GET_GAMEPAD);

                    if (enabled) {
                        // sendData.putInt(!useVirtualGamepad ? currentController.getDeviceId() : profile.id);
                        sendData.putInt(currentController.getDeviceId());
                        sendData.put(dinputMapperType);
                        // byte[] bytes = (useVirtualGamepad ? profile.getName() : currentController.getName()).getBytes();
                        byte[] bytes = (currentController.getName()).getBytes();
                        sendData.putInt(bytes.length);
                        sendData.put(bytes);
                    }
                    else sendData.putInt(0);

                    sendPacket(port);
                });
                break;
            }
            case RequestCodes.GET_GAMEPAD_STATE: {
                Log.d("WinHandler", "Request was get gamepad state");
                int gamepadId = receiveData.getInt();
                // final ControlsProfile profile = activity.getInputControlsView().getProfile();
                // boolean useVirtualGamepad = profile != null && profile.isVirtualGamepad();
                // final boolean enabled = currentController != null || useVirtualGamepad;
                final boolean enabled = currentController != null;

                if (currentController != null && currentController.getDeviceId() != gamepadId) currentController = null;

                addAction(() -> {
                    sendData.rewind();
                    sendData.put(RequestCodes.GET_GAMEPAD_STATE);
                    sendData.put((byte)(enabled ? 1 : 0));

                    if (enabled) {
                        sendData.putInt(gamepadId);
                        // if (useVirtualGamepad) {
                        //     profile.getGamepadState().writeTo(sendData);
                        // }
                        // else currentController.state.writeTo(sendData);
                        currentController.state.writeTo(sendData);
                    }

                    sendPacket(port);
                });
                break;
            }
            case RequestCodes.RELEASE_GAMEPAD: {
                Log.d("WinHandler", "Request was release gamepad");
                currentController = null;
                gamepadClients.clear();
                break;
            }
            case RequestCodes.CURSOR_POS_FEEDBACK: {
                Log.d("WinHandler", "Request was cursor pos feedback");
                short x = receiveData.getShort();
                short y = receiveData.getShort();
                // XServer xServer = activity.getXServer();
                xServer.pointer.setX(x);
                xServer.pointer.setY(y);
                // activity.getXServerView().requestRender();
                xServerView.requestRender();
                break;
            }
        }
    }

    public void  start() {
        Log.d("WinHandler", "Starting...");
        try {
            localhost = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
            try {
                localhost = InetAddress.getByName("127.0.0.1");
            }
            catch (UnknownHostException ex) {
                Log.e("WinHandler", "Failed to get local host address: " + e);
            }
        }

        running = true;
        startSendThread();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress((InetAddress)null, SERVER_PORT));

                while (running) {
                    // Log.d("WinHandler", "Waiting for packet...");
                    socket.receive(receivePacket);
                    // Log.d("WinHandler", "Received packet, handling request...");

                    synchronized (actions) {
                        receiveData.rewind();
                        byte requestCode = receiveData.get();
                        handleRequest(requestCode, receivePacket.getPort());
                    }
                }
            }
            catch (IOException e) {
                Log.e("WinHandler", "Failed to start: " + e);
                e.printStackTrace();
            }
        });
        Log.d("WinHandler", "Finished start up");
    }

    public void sendGamepadState() {
        // Log.d("WinHandler", "Setting up send gamepad state packet");
        if (!initReceived || gamepadClients.isEmpty()) return;
        // final ControlsProfile profile = activity.getInputControlsView().getProfile();
        // final boolean useVirtualGamepad = profile != null && profile.isVirtualGamepad();
        // final boolean enabled = currentController != null || useVirtualGamepad;
        final boolean enabled = currentController != null;

        for (final int port : gamepadClients) {
            // Log.d("WinHandler", "Creating send gamepad state packet action for gamepad client on port " + port);
            addAction(() -> {
                sendData.rewind();
                sendData.put(RequestCodes.GET_GAMEPAD_STATE);
                sendData.put((byte)(enabled ? 1 : 0));

                if (enabled) {
                    // sendData.putInt(!useVirtualGamepad ? currentController.getDeviceId() : profile.id);
                    sendData.putInt(currentController.getDeviceId());
                    // if (useVirtualGamepad) {
                    //     profile.getGamepadState().writeTo(sendData);
                    // }
                    // else currentController.state.writeTo(sendData);
                    currentController.state.writeTo(sendData);
                }

                // Log.d("WinHandler", "Sending gamepad state packet");
                sendPacket(port);
            });
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        boolean handled = false;
        if (currentController != null && currentController.getDeviceId() == event.getDeviceId()) {
            handled = currentController.updateStateFromMotionEvent(event);
            if (handled) sendGamepadState();
        }
        return handled;
    }

    public boolean onKeyEvent(KeyEvent event) {
        boolean handled = false;
        if (currentController != null && currentController.getDeviceId() == event.getDeviceId() && event.getRepeatCount() == 0) {
            int action = event.getAction();

            if (action == KeyEvent.ACTION_DOWN) {
                handled = currentController.updateStateFromKeyEvent(event);
            }
            else if (action == KeyEvent.ACTION_UP) {
                handled = currentController.updateStateFromKeyEvent(event);
            }

            if (handled) sendGamepadState();
        }
        return handled;
    }

    public byte getDInputMapperType() {
        return dinputMapperType;
    }

    public void setDInputMapperType(byte dinputMapperType) {
        this.dinputMapperType = dinputMapperType;
    }

    public ExternalController getCurrentController() {
        return currentController;
    }
}
