/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.async.advice.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.async.advice.internal.configuration.AsyncAdviceConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.async.Async;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.portal.async.advice.internal.configuration.AsyncAdviceConfiguration",
	service = ChainableMethodAdvice.class
)
public class AsyncAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		Annotation annotation = annotations.get(Async.class);

		if (annotation == null) {
			return null;
		}

		if (method.getReturnType() != void.class) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Async annotation on method " + method.getName() +
						" does not return void");
			}

			return null;
		}

		String destinationName = null;

		if (_destinationNames != null) {
			Class<?> targetClass = target.getClass();

			destinationName = _destinationNames.get(targetClass.getName());
		}

		if (destinationName == null) {
			return _asyncAdviceConfiguration.defaultDestinationName();
		}

		return destinationName;
	}

	@Activate
	@Modified
	protected void activate(Map<String, String> properties) {
		_asyncAdviceConfiguration = ConfigurableUtil.createConfigurable(
			AsyncAdviceConfiguration.class, properties);

		String[] targetClassNamesToDestinationNames =
			_asyncAdviceConfiguration.targetClassNamesToDestinationNames();

		if (targetClassNamesToDestinationNames == null) {
			return;
		}

		Map<String, String> destinationNames = new HashMap<>();

		for (String targetClassNameToDestinationName :
				targetClassNamesToDestinationNames) {

			int index = targetClassNameToDestinationName.indexOf(
				CharPool.EQUAL);

			if (index <= 0) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid target class name to destination name \"" +
							targetClassNameToDestinationName + "\"");
				}
			}
			else {
				destinationNames.put(
					targetClassNameToDestinationName.substring(0, index),
					targetClassNameToDestinationName.substring(index + 1));
			}
		}

		if (!destinationNames.isEmpty()) {
			_destinationNames = destinationNames;
		}
	}

	@Override
	protected Object before(
		AopMethodInvocation aopMethodInvocation, Object[] arguments) {

		if (AsyncInvokeThreadLocal.isEnabled()) {
			return null;
		}

		String destinationName = aopMethodInvocation.getAdviceMethodContext();

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				Message message = new Message();

				message.setPayload(
					new AsyncCallable(aopMethodInvocation, arguments));

				_messageBus.sendMessage(destinationName, message);

				return null;
			});

		return nullResult;
	}

	private static final Log _log = LogFactoryUtil.getLog(AsyncAdvice.class);

	private volatile AsyncAdviceConfiguration _asyncAdviceConfiguration;
	private volatile Map<String, String> _destinationNames;

	@Reference
	private MessageBus _messageBus;

}