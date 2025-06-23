/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.exception.handler;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.exception.handler.BatchEngineImportTaskExceptionHandler;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ClassUtil;
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

		long groupId = 0;

		if (batchEngineTaskItemDelegate instanceof
				ExportImportVulcanBatchEngineTaskItemDelegate) {

			ExportImportVulcanBatchEngineTaskItemDelegate<?>
				exportImportVulcanBatchEngineTaskItemDelegate =
					(ExportImportVulcanBatchEngineTaskItemDelegate)
						batchEngineImportTask;

			if (exportImportVulcanBatchEngineTaskItemDelegate.getScope() ==
					ExportImportVulcanBatchEngineTaskItemDelegate.Scope.SITE) {

				groupId = GetterUtil.getLong(
					batchEngineImportTask.getParameterValue("siteId"));
			}
		}

		_exportImportReportEntryLocalService.addErrorExportImportReportEntry(
			groupId, batchEngineImportTask.getCompanyId(),
			_getExternalReferenceCode(item),
			_classNameLocalService.getClassNameId(ClassUtil.getClassName(item)),
			GetterUtil.getLong(
				ExportImportThreadLocal.getExportImportConfigurationId()),
			exception.getMessage(), _getTraceString(exception));
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

	private String _getTraceString(Throwable throwable) {
		OutputStream outputStream = new ByteArrayOutputStream();

		throwable.printStackTrace(new PrintStream(outputStream));

		return outputStream.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportBatchEngineImportTaskExceptionHandler.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

}