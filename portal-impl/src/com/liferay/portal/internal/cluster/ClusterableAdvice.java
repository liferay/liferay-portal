/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.internal.cluster;

import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutorUtil;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.cluster.ClusterableInvokerUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class ClusterableAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		return annotations.get(Clusterable.class);
	}

	@Override
	protected Object afterReturning(
			AopMethodInvocation aopMethodInvocation, Object[] arguments,
			Object result)
		throws Throwable {

		if (!ClusterInvokeThreadLocal.isEnabled()) {
			return result;
		}

		Clusterable clusterable = aopMethodInvocation.getAdviceMethodContext();

		ClusterableInvokerUtil.invokeOnCluster(
			clusterable.acceptor(), aopMethodInvocation.getThis(),
			aopMethodInvocation.getMethod(), arguments);

		return result;
	}

	@Override
	protected Object before(
			AopMethodInvocation aopMethodInvocation, Object[] arguments)
		throws Throwable {

		if (!ClusterInvokeThreadLocal.isEnabled()) {
			return null;
		}

		Clusterable clusterable = aopMethodInvocation.getAdviceMethodContext();

		if (!clusterable.onMaster()) {
			return null;
		}

		Object result = null;

		if (ClusterMasterExecutorUtil.isMaster()) {
			result = aopMethodInvocation.proceed(arguments);
		}
		else {
			result = ClusterableInvokerUtil.invokeOnMaster(
				clusterable.acceptor(), aopMethodInvocation.getThis(),
				aopMethodInvocation.getMethod(), arguments);
		}

		Method method = aopMethodInvocation.getMethod();

		Class<?> returnType = method.getReturnType();

		if (returnType == void.class) {
			result = nullResult;
		}

		return result;
	}

}