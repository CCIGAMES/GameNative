/**************************************************************************
 *
 * Copyright 2007-2013 VMware, Inc.
 * All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sub license, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice (including the
 * next paragraph) shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL VMWARE AND/OR ITS SUPPLIERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **************************************************************************/

#include "no_extern_c.h"

#ifndef _C99_COMPAT_H_
#define _C99_COMPAT_H_


/*
 * MSVC hacks.
 */
#if defined(_MSC_VER)

#  if _MSC_VER < 1500
#    error "Microsoft Visual Studio 2008 or higher required"
#  endif

   /*
    * Visual Studio 2012 will complain if we define the `inline` keyword, but
    * actually it only supports the keyword on C++.
    *
    * To avoid this the _ALLOW_KEYWORD_MACROS must be set.
    */
#  if (_MSC_VER >= 1700) && !defined(_ALLOW_KEYWORD_MACROS)
#    define _ALLOW_KEYWORD_MACROS
#  endif

   /*
    * XXX: MSVC has a `__restrict` keyword, but it also has a
    * `__declspec(restrict)` modifier, so it is impossible to define a
    * `restrict` macro without interfering with the latter.  Furthermore the
    * MSVC standard library uses __declspec(restrict) under the _CRTRESTRICT
    * macro.  For now resolve this issue by redefining _CRTRESTRICT, but going
    * forward we should probably should stop using restrict, especially
    * considering that our code does not obbey strict aliasing rules any way.
    */
#  include <crtdefs.h>
#  undef _CRTRESTRICT
#  define _CRTRESTRICT
#endif


/*
 * C99 inline keyword
 */
#ifndef inline
#  ifdef __cplusplus
     /* C++ supports inline keyword */
#  elif defined(__GNUC__)
#    define inline __inline__
#  elif defined(_MSC_VER)
#    define inline __inline
#  elif defined(__ICL)
#    define inline __inline
#  elif defined(__INTEL_COMPILER)
     /* Intel compiler supports inline keyword */
#  elif defined(__WATCOMC__) && (__WATCOMC__ >= 1100)
#    define inline __inline
#  elif (__STDC_VERSION__ >= 199901L)
     /* C99 supports inline keyword */
#  else
#    define inline
#  endif
#endif


/*
 * C99 restrict keyword
 *
 * See also:
 * - http://cellperformance.beyond3d.com/articles/2006/05/demystifying-the-restrict-keyword.html
 */
#ifndef restrict
#  if (__STDC_VERSION__ >= 199901L)
     /* C99 */
#  elif defined(__GNUC__)
#    define restrict __restrict__
#  elif defined(_MSC_VER)
#    define restrict __restrict
#  else
#    define restrict /* */
#  endif
#endif


/*
 * C99 __func__ macro
 */
#ifndef __func__
#  if (__STDC_VERSION__ >= 199901L)
     /* C99 */
#  elif defined(__GNUC__)
#    define __func__ __FUNCTION__
#  elif defined(_MSC_VER)
#    define __func__ __FUNCTION__
#  else
#    define __func__ "<unknown>"
#  endif
#endif


/* Simple test case for debugging */
#if 0
static inline const char *
test_c99_compat_h(const void * restrict a,
                  const void * restrict b)
{
   return __func__;
}
#endif


#endif /* _C99_COMPAT_H_ */
