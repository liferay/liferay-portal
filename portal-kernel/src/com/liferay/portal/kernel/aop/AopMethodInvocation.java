/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.aop;

import java.lang.reflect.Method;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides immutable AOP method invocation to by used by {@link
 * ChainableMethodAdvice}.
 *
 * @author Preston Crary
 */
@ProviderType
public interface AopMethodInvocation {

	/**
	 * @return The cached context object defined in {@link
	 *         ChainableMethodAdvice#createMethodContext(Object, Method,
	 *         java.util.Map)}
	 */
	public <T> T getAdviceMethodContext();

	/**
	 * @return the <code>Method</code> being advised for this invocation
	 */
	public Method getMethod();

	/**
	 * @return the <code>Object</code> being advised for this invocation
	 */
	public Object getThis();

	/**
	 * @param  arguments the arguments to use when invoking the method
	 * @return the result of the underlying invocation chain
	 */
	public Object proceed(Object[] arguments) throws Throwable;

}