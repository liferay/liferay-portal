/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.internal.background.task.display.PortletExportImportBackgroundTaskDisplay;
import com.liferay.exportimport.kernel.exception.ExportImportIOException;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 * @author Akos Thurzo
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.exportimport.internal.background.task.PortletImportBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class PortletImportBackgroundTaskExecutor
	extends BaseExportImportBackgroundTaskExecutor {

	public PortletImportBackgroundTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
			new PortletExportImportBackgroundTaskStatusMessageTranslator());

		// Isolation level guarantees this will be serial in a group

		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_GROUP);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			getExportImportConfiguration(backgroundTask);

		List<FileEntry> attachmentsFileEntries = new ArrayList<>(
			backgroundTask.getAttachmentsFileEntries());

		long tempFileEntryId = MapUtil.getLong(
			backgroundTask.getTaskContextMap(), "tempFileEntryId");

		if (tempFileEntryId > 0) {
			try {
				attachmentsFileEntries.add(
					_dlAppLocalService.getFileEntry(tempFileEntryId));
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				throw new PortalException(
					_language.get(
						getLocale(backgroundTask),
						"the-uploaded-file-has-expired-please-upload-it-again"),
					noSuchFileEntryException);
			}
		}

		File file = null;

		for (FileEntry attachmentsFileEntry : attachmentsFileEntries) {
			try {
				file = FileUtil.createTempFile("lar");

				FileUtil.write(file, attachmentsFileEntry.getContentStream());

				TransactionInvokerUtil.invoke(
					transactionConfig,
					new PortletImportCallable(exportImportConfiguration, file));
			}
			catch (IOException ioException) {
				ExportImportIOException exportImportIOException =
					new ExportImportIOException(
						LayoutImportBackgroundTaskExecutor.class.getName(),
						ioException);

				if (Validator.isNotNull(attachmentsFileEntry.getFileName())) {
					exportImportIOException.setFileName(
						attachmentsFileEntry.getFileName());
					exportImportIOException.setType(
						ExportImportIOException.PORTLET_IMPORT_FILE);
				}
				else {
					exportImportIOException.setType(
						ExportImportIOException.PORTLET_IMPORT);
				}

				throw exportImportIOException;
			}
			catch (Throwable throwable) {
				throw new SystemException(throwable);
			}
			finally {
				FileUtil.delete(file);

				PortletFileRepositoryUtil.deletePortletFileEntry(
					attachmentsFileEntry.getFileEntryId());
			}
		}

		int count =
			_exportImportReportEntryLocalService.
				getExportImportReportEntriesCount(
					exportImportConfiguration.getCompanyId(),
					exportImportConfiguration.getExportImportConfigurationId());

		if (count > 0) {
			return BackgroundTaskResult.COMPLETED_WITH_ERRORS;
		}

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return new PortletExportImportBackgroundTaskDisplay(backgroundTask);
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private Language _language;

	private class PortletImportCallable implements Callable<Void> {

		public PortletImportCallable(
			ExportImportConfiguration exportImportConfiguration, File file) {

			_exportImportConfiguration = exportImportConfiguration;
			_file = file;
		}

		@Override
		public Void call() throws PortalException {
			_exportImportLocalService.importPortletDataDeletions(
				_exportImportConfiguration, _file);

			_exportImportLocalService.importPortletInfo(
				_exportImportConfiguration, _file);

			return null;
		}

		private final ExportImportConfiguration _exportImportConfiguration;
		private final File _file;

	}

}