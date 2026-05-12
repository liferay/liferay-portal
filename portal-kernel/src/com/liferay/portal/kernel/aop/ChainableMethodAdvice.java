/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * Provides method advice to implement an aspect for services.
 *
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 */
public abstract class ChainableMethodAdvice {

	/**
	 * Creates the context to be used when invoking this advice. The context can
	 * be useful for caching information derived from reflective calls on the
	 * method. Returning <code>null</code> disables this advice for all
	 * invocations on the target and method. The context object can be obtained
	 * by calling {@link AopMethodInvocation#getAdviceMethodContext()}. The
	 * context should be immutable as it is reused by concurrent calls to {@link
	 * #invoke(AopMethodInvocation, Object[])}, and it must not hold the target,
	 * which is replaced whenever the advised service is wrapped or unwrapped.
	 *
	 * @param  target the target for the context
	 * @param  method the method for the context
	 * @param  annotations a map of the method's annotations
	 * @return the context object for use during method invocations or
	 *         <code>null</code> to disable this advice for the method
	 */
	public abstract Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations);

	public Object invoke(
			AopMethodInvocation aopMethodInvocation, Object[] arguments)
		throws Throwable {

		Object returnValue = before(aopMethodInvocation, arguments);

		if (returnValue != null) {
			if (returnValue == nullResult) {
				return null;
			}

			return returnValue;
		}

		try {
			returnValue = aopMethodInvocation.proceed(arguments);

			returnValue = afterReturning(
				aopMethodInvocation, arguments, returnValue);
		}
		catch (Throwable throwable) {
			afterThrowing(aopMethodInvocation, arguments, throwable);

			throw throwable;
		}
		finally {
			duringFinally(aopMethodInvocation, arguments);
		}

		return returnValue;
	}

	/**
	 * Returns the result to propagate to the caller, which allows this advice
	 * to replace the intercepted method's return value. An advice that only
	 * observes the result must return <code>result</code> unchanged, and
	 * returning <code>null</code> propagates <code>null</code>. {@link
	 * #nullResult} applies only to {@link #before(AopMethodInvocation,
	 * Object[])}, where <code>null</code> instead means that the invocation
	 * should proceed.
	 *
	 * @param  aopMethodInvocation the method invocation being advised
	 * @param  arguments the arguments the method was invoked with
	 * @param  result the result from the rest of the invocation chain
	 * @return the result to propagate to the caller
	 */
	protected Object afterReturning(
			AopMethodInvocation aopMethodInvocation, Object[] arguments,
			Object result)
		throws Throwable {

		return result;
	}

	protected void afterThrowing(
			AopMethodInvocation aopMethodInvocation, Object[] arguments,
			Throwable throwable)
		throws Throwable {
	}

	protected Object before(
			AopMethodInvocation aopMethodInvocation, Object[] arguments)
		throws Throwable {

		return null;
	}

	protected void duringFinally(
		AopMethodInvocation aopMethodInvocation, Object[] arguments) {
	}

	protected static Object nullResult = new Object();

}