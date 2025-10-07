/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.optimizer;

import com.liferay.adaptive.media.constants.AMOptimizeImagesBackgroundTaskConstants;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.constants.DLFileEntryConfigurationConstants;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseAMImageOptimizer implements AMImageOptimizer {

	@Override
	public void optimize(long companyId) {
		long previewableProcessorMaxSize =
			dlFileEntryConfigurationProvider.
				getCompanyPreviewableProcessorMaxSize(companyId);

		if (previewableProcessorMaxSize == 0) {
			return;
		}

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			amImageConfigurationHelper.getAMImageConfigurationEntries(
				companyId);

		int count = countExpectedAMImageEntries(companyId);

		if (count == 0) {
			return;
		}

		int total = count * amImageConfigurationEntries.size();

		AtomicInteger successCounter = new AtomicInteger(0);
		AtomicInteger errorCounter = new AtomicInteger(0);

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			_optimize(
				companyId, amImageConfigurationEntry.getUUID(), total,
				successCounter, errorCounter);
		}
	}

	@Override
	public void optimize(long companyId, String configurationEntryUuid) {
		long previewableProcessorMaxSize =
			dlFileEntryConfigurationProvider.
				getCompanyPreviewableProcessorMaxSize(companyId);

		if (previewableProcessorMaxSize == 0) {
			return;
		}

		int total = countExpectedAMImageEntries(companyId);

		if (total == 0) {
			return;
		}

		AtomicInteger successCounter = new AtomicInteger(0);
		AtomicInteger errorCounter = new AtomicInteger(0);

		_optimize(
			companyId, configurationEntryUuid, total, successCounter,
			errorCounter);
	}

	protected abstract int countExpectedAMImageEntries(long companyId);

	protected abstract void forEachFileEntry(
			long companyId, Consumer<DLFileEntry> consumer)
		throws PortalException;

	protected String[] getMimeTypes() {
		return ArrayUtil.filter(
			amImageMimeTypeProvider.getSupportedMimeTypes(),
			amImageValidator::isProcessingSupported);
	}

	@Reference
	protected AMImageConfigurationHelper amImageConfigurationHelper;

	@Reference
	protected AMImageMimeTypeProvider amImageMimeTypeProvider;

	@Reference
	protected AMImageValidator amImageValidator;

	@Reference
	protected AMProcessor<FileVersion> amProcessor;

	@Reference
	protected BackgroundTaskStatusMessageSender
		backgroundTaskStatusMessageSender;

	@Reference
	protected DLFileEntryConfigurationProvider dlFileEntryConfigurationProvider;

	private Consumer<DLFileEntry> _getProcessDLFileEntryConsumer(
		String configurationEntryUuid, int total, AtomicInteger successCounter,
		AtomicInteger errorCounter) {

		Map<Long, Long> groupPreviewableProcessorMaxSizeMap =
			dlFileEntryConfigurationProvider.
				getGroupPreviewableProcessorMaxSizeMap();

		return dlFileEntry -> {
			Long previewableProcessorMaxSize =
				groupPreviewableProcessorMaxSizeMap.get(
					dlFileEntry.getGroupId());

			if ((previewableProcessorMaxSize == null) ||
				(previewableProcessorMaxSize ==
					DLFileEntryConfigurationConstants.
						PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED) ||
				(dlFileEntry.getSize() <= previewableProcessorMaxSize)) {

				_processDLFileEntry(
					configurationEntryUuid, total, successCounter, errorCounter,
					dlFileEntry);
			}
		};
	}

	private void _optimize(
		long companyId, String configurationEntryUuid, int total,
		AtomicInteger successCounter, AtomicInteger errorCounter) {

		try {
			forEachFileEntry(
				companyId,
				_getProcessDLFileEntryConsumer(
					configurationEntryUuid, total, successCounter,
					errorCounter));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _processDLFileEntry(
		String configurationEntryUuid, int total, AtomicInteger successCounter,
		AtomicInteger errorCounter, DLFileEntry dlFileEntry) {

		FileEntry fileEntry = new LiferayFileEntry(dlFileEntry);

		try {
			amProcessor.process(
				fileEntry.getFileVersion(), configurationEntryUuid);

			_sendStatusMessage(
				successCounter.incrementAndGet(), errorCounter.get(), total);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to process file entry " +
						fileEntry.getFileEntryId(),
					exception);
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			_sendStatusMessage(
				successCounter.get(), errorCounter.incrementAndGet(), total);
		}
	}

	private void _sendStatusMessage(int count, int errors, int total) {
		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());

		Class<?> clazz = getClass();

		message.put(
			AMOptimizeImagesBackgroundTaskConstants.CLASS_NAME,
			clazz.getName());

		message.put(AMOptimizeImagesBackgroundTaskConstants.COUNT, count);
		message.put(AMOptimizeImagesBackgroundTaskConstants.ERRORS, errors);
		message.put(AMOptimizeImagesBackgroundTaskConstants.TOTAL, total);
		message.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAMImageOptimizer.class);

}