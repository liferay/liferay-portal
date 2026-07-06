/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.processor;

import com.liferay.adaptive.media.processor.AMAsyncProcessor;
import com.liferay.adaptive.media.web.internal.constants.AMDestinationNames;
import com.liferay.adaptive.media.web.internal.messaging.AMProcessorCommand;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sergio González
 */
public final class AMAsyncProcessorImpl<M, T>
	implements AMAsyncProcessor<M, T> {

	public static void cleanQueue(
		AMProcessorCommand amProcessorCommand, String modelId) {

		if (amProcessorCommand == AMProcessorCommand.CLEAN_UP) {
			_cleanUpModelIds.remove(modelId);
		}
		else {
			_processModelIds.remove(modelId);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Cleaned queue for model ID ", modelId,
					" and adaptive media processor command ",
					amProcessorCommand));
		}
	}

	public AMAsyncProcessorImpl(Class<M> clazz, MessageBus messageBus) {
		_clazz = clazz;
		_messageBus = messageBus;
	}

	@Override
	public void triggerCleanUp(M model, String modelId) throws PortalException {
		if (Validator.isNotNull(modelId)) {
			if (!_cleanUpModelIds.add(modelId)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Omitted clean up for model ID " + modelId +
							" because it is already in progress");
				}

				return;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Added clean up for model ID " + modelId + " to the queue");
			}
		}

		Message message = new Message();

		message.put("className", _clazz.getName());
		message.put("command", AMProcessorCommand.CLEAN_UP);
		message.put("model", model);

		if (Validator.isNotNull(modelId)) {
			message.put("modelId", modelId);
		}

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				_messageBus.sendMessage(
					AMDestinationNames.ADAPTIVE_MEDIA_PROCESSOR, message);

				return null;
			});
	}

	@Override
	public void triggerProcess(M model, String modelId) throws PortalException {
		if (Validator.isNotNull(modelId)) {
			if (!_processModelIds.add(modelId)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Omitted process for model ID " + modelId +
							" because it is already in progress");
				}

				return;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					"Added process for model ID " + modelId + " to the queue");
			}
		}

		Message message = new Message();

		message.put("className", _clazz.getName());
		message.put("command", AMProcessorCommand.PROCESS);
		message.put("model", model);

		if (Validator.isNotNull(modelId)) {
			message.put("modelId", modelId);
		}

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				_messageBus.sendMessage(
					AMDestinationNames.ADAPTIVE_MEDIA_PROCESSOR, message);

				return null;
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AMAsyncProcessorImpl.class);

	private static final Set<String> _cleanUpModelIds =
		Collections.newSetFromMap(new ConcurrentHashMap<>());
	private static final Set<String> _processModelIds =
		Collections.newSetFromMap(new ConcurrentHashMap<>());

	private final Class<M> _clazz;
	private final MessageBus _messageBus;

}