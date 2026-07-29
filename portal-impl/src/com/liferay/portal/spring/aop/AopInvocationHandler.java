/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.aop;

import com.liferay.petra.reflect.AnnotationLocator;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.spring.transaction.TransactionAttributeAdapter;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionInterceptor;
import com.liferay.portal.transaction.TransactionsUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shuyang Zhou
 * @author Preston Crary
 */
public class AopInvocationHandler implements InvocationHandler {

	public Object getTarget() {
		return _target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments)
		throws Throwable {

		AopMethodInvocation aopMethodInvocation = _getAopMethodInvocation(
			method);

		return aopMethodInvocation.proceed(arguments);
	}

	public synchronized void setTarget(Object target) {
		_target = target;

		_aopMethodInvocations.clear();
	}

	protected AopInvocationHandler(
		Object target, ChainableMethodAdvice[] chainableMethodAdvices,
		TransactionExecutor transactionExecutor) {

		_target = target;
		_chainableMethodAdvices = chainableMethodAdvices;

		_transactionInterceptor = new TransactionInterceptor(
			transactionExecutor);
	}

	protected synchronized void reset() {
		_aopMethodInvocations.clear();
	}

	protected synchronized void setChainableMethodAdvices(
		ChainableMethodAdvice[] chainableMethodAdvices) {

		_chainableMethodAdvices = chainableMethodAdvices;

		_aopMethodInvocations.clear();
	}

	private AopMethodInvocation _createAopMethodInvocation(
		Object target, Method method,
		ChainableMethodAdvice[] chainableMethodAdvices,
		TransactionInterceptor transactionInterceptor) {

		AopMethodInvocation aopMethodInvocation = null;

		ChainableMethodAdvice nextChainableMethodAdvice = null;

		Class<?> targetClass = target.getClass();

		Map<Class<? extends Annotation>, Annotation> annotations =
			AnnotationLocator.index(method, targetClass);

		TransactionAttributeAdapter transactionAttributeAdapter =
			transactionInterceptor.createMethodContext(
				target, method, annotations);

		if (transactionAttributeAdapter != null) {
			aopMethodInvocation = new AopMethodInvocationImpl(
				target, method, transactionAttributeAdapter,
				nextChainableMethodAdvice, aopMethodInvocation);

			nextChainableMethodAdvice = transactionInterceptor;
		}

		for (int i = chainableMethodAdvices.length - 1; i >= 0; i--) {
			ChainableMethodAdvice chainableMethodAdvice =
				chainableMethodAdvices[i];

			Object methodContext = chainableMethodAdvice.createMethodContext(
				target, method, annotations);

			if (methodContext != null) {
				aopMethodInvocation = new AopMethodInvocationImpl(
					target, method, methodContext, nextChainableMethodAdvice,
					aopMethodInvocation);

				nextChainableMethodAdvice = chainableMethodAdvice;
			}
		}

		return new AopMethodInvocationImpl(
			target, method, null, nextChainableMethodAdvice,
			aopMethodInvocation);
	}

	private AopMethodInvocation _getAopMethodInvocation(Method method) {
		if (TransactionsUtil.isEnabled()) {
			AopMethodInvocation aopMethodInvocation = _aopMethodInvocations.get(
				method);

			if (aopMethodInvocation == null) {
				synchronized (method) {
					aopMethodInvocation = _aopMethodInvocations.get(method);

					if (aopMethodInvocation == null) {
						aopMethodInvocation = _createAopMethodInvocation(
							_target, method, _chainableMethodAdvices,
							_transactionInterceptor);

						_aopMethodInvocations.put(method, aopMethodInvocation);
					}
				}
			}

			return aopMethodInvocation;
		}

		return new AopMethodInvocationImpl(_target, method, null, null, null);
	}

	private final Map<Method, AopMethodInvocation> _aopMethodInvocations =
		new ConcurrentHashMap<>();
	private ChainableMethodAdvice[] _chainableMethodAdvices;
	private volatile Object _target;
	private final TransactionInterceptor _transactionInterceptor;

}