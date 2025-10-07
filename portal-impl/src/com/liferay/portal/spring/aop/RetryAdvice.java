/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.aop;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.RetryAcceptor;
import com.liferay.portal.kernel.spring.aop.Property;
import com.liferay.portal.kernel.spring.aop.Retry;
import com.liferay.portal.kernel.util.PropsValues;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthew Tambara
 */
public class RetryAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Class<?> targetClass, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		Retry retry = (Retry)annotations.get(Retry.class);

		if (retry == null) {
			return null;
		}

		int retries = retry.retries();

		if (retries < 0) {
			retries = PropsValues.RETRY_ADVICE_MAX_RETRIES;
		}

		Map<String, String> properties = new HashMap<>();

		for (Property property : retry.properties()) {
			properties.put(property.name(), property.value());
		}

		Class<? extends RetryAcceptor> clazz = retry.acceptor();

		try {
			RetryAcceptor retryAcceptor = clazz.newInstance();

			return new RetryContext(retryAcceptor, properties, retries);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			_log.error(reflectiveOperationException);

			return null;
		}
	}

	@Override
	public Object invoke(
			AopMethodInvocation aopMethodInvocation, Object[] arguments)
		throws Throwable {

		RetryContext retryContext =
			aopMethodInvocation.getAdviceMethodContext();

		RetryAcceptor retryAcceptor = retryContext._retryAcceptor;
		Map<String, String> properties = retryContext._properties;

		int retries = retryContext._retries;

		int totalRetries = retries;

		if (retries >= 0) {
			retries++;
		}

		Object returnValue = null;
		Throwable throwable1 = null;

		while ((retries < 0) || (retries-- > 0)) {
			try {
				returnValue = aopMethodInvocation.proceed(arguments);

				if (!retryAcceptor.acceptResult(returnValue, properties)) {
					return returnValue;
				}

				if (_log.isWarnEnabled() && (retries != 0)) {
					String number = String.valueOf(retries);

					if (retries < 0) {
						number = "unlimited";
					}

					_log.warn(
						StringBundler.concat(
							"Retry on ", aopMethodInvocation, " for ", number,
							" more times due to result ", returnValue));
				}
			}
			catch (Throwable throwable2) {
				throwable1 = throwable2;

				if (!retryAcceptor.acceptException(throwable2, properties)) {
					throw throwable2;
				}

				if (_log.isWarnEnabled() && (retries != 0)) {
					String number = String.valueOf(retries);

					if (retries < 0) {
						number = "unlimited";
					}

					_log.warn(
						StringBundler.concat(
							"Retry on ", aopMethodInvocation, " for ", number,
							" more times due to exception ", throwable1),
						throwable1);
				}
			}
		}

		if (throwable1 != null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Give up retrying on ", aopMethodInvocation, " after ",
						totalRetries,
						" retries and rethrow last retry's exception ",
						throwable1),
					throwable1);
			}

			throw throwable1;
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Give up retrying on ", aopMethodInvocation, " after ",
					totalRetries,
					" retries and returning the last retry's result ",
					returnValue));
		}

		return returnValue;
	}

	private static final Log _log = LogFactoryUtil.getLog(RetryAdvice.class);

	private static class RetryContext {

		private RetryContext(
			RetryAcceptor retryAcceptor, Map<String, String> properties,
			int retries) {

			_retryAcceptor = retryAcceptor;
			_properties = properties;
			_retries = retries;
		}

		private final Map<String, String> _properties;
		private final int _retries;
		private final RetryAcceptor _retryAcceptor;

	}

}