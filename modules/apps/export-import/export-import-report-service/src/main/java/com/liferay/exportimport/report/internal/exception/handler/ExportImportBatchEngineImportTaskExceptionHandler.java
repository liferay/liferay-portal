/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.exception.handler;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.exception.handler.BatchEngineImportTaskExceptionHandler;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.internal.util.ExportImportReportEntryUtil;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.lang.reflect.Method;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = BatchEngineImportTaskExceptionHandler.class)
public class ExportImportBatchEngineImportTaskExceptionHandler
	implements BatchEngineImportTaskExceptionHandler {

	@Override
	public void handle(
		BatchEngineImportTask batchEngineImportTask,
		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
		Exception exception, Object item) {

		if (!ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		long groupId = GetterUtil.getLong(
			batchEngineImportTask.getParameterValue("siteId"));

		_exportImportReportEntryLocalService.addErrorExportImportReportEntry(
			groupId, batchEngineImportTask.getCompanyId(),
			_getExternalReferenceCode(item),
			_classNameLocalService.getClassNameId(
				batchEngineImportTask.getParameterValue("itemClassName")),
			_getId(item),
			GetterUtil.getLong(
				ExportImportThreadLocal.getExportImportConfigurationId()),
			exception.getMessage(), _getErrorStackTrace(exception),
			batchEngineImportTask.getParameterValue("itemModelName"),
			ExportImportReportEntryConstants.ORIGIN_BATCH,
			ExportImportReportEntryUtil.getScope(groupId),
			ExportImportReportEntryUtil.getScopeKey(groupId));
	}

	private String _getErrorStackTrace(Throwable throwable) {
		OutputStream outputStream = new ByteArrayOutputStream();

		throwable.printStackTrace(new PrintStream(outputStream));

		return outputStream.toString();
	}

	private String _getExternalReferenceCode(Object item) {
		try {
			Class<?> clazz = item.getClass();

			Method method = clazz.getDeclaredMethod("getExternalReferenceCode");

			return String.valueOf(method.invoke(item));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private long _getId(Object item) {
		try {
			Class<?> clazz = item.getClass();

			Method method = clazz.getDeclaredMethod("getId");

			return GetterUtil.getLong(method.invoke(item));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return 0L;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportBatchEngineImportTaskExceptionHandler.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

}