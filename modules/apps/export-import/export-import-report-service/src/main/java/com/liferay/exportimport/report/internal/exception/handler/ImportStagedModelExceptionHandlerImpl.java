/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.exception.handler;

import com.liferay.exportimport.kernel.exception.handler.ImportStagedModelExceptionHandler;
import com.liferay.exportimport.kernel.lar.ExportImportClassedModelUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.report.internal.util.ExportImportReportEntryUtil;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alvaro Saugar
 */
@Component(service = ImportStagedModelExceptionHandler.class)
public class ImportStagedModelExceptionHandlerImpl
	implements ImportStagedModelExceptionHandler {

	@Override
	public <T extends StagedModel> void handle(
		PortletDataContext portletDataContext,
		PortletDataException portletDataException, T stagedModel) {

		String externalReferenceCode = null;

		if (stagedModel instanceof ExternalReferenceCodeModel) {
			ExternalReferenceCodeModel externalReferenceCodeModel =
				(ExternalReferenceCodeModel)stagedModel;

			externalReferenceCode =
				externalReferenceCodeModel.getExternalReferenceCode();
		}

		Class<?> modelClass = stagedModel.getModelClass();

		long groupId = portletDataContext.getGroupId();

		if (ExportImportReportEntryUtil.isCompanyScoped(
				groupId, _groupLocalService)) {

			groupId = 0;
		}

		_exportImportReportEntryLocalService.addErrorExportImportReportEntry(
			groupId, portletDataContext.getCompanyId(), externalReferenceCode,
			ExportImportClassedModelUtil.getClassNameId(stagedModel),
			ExportImportClassedModelUtil.getClassPK(stagedModel),
			GetterUtil.getLong(portletDataContext.getExportImportProcessId()),
			portletDataException.getMessage(),
			_getErrorStackTrace(portletDataException), modelClass.getName(),
			ExportImportReportEntryUtil.getOrigin());
	}

	private String _getErrorStackTrace(Throwable throwable) {
		OutputStream outputStream = new ByteArrayOutputStream();

		throwable.printStackTrace(new PrintStream(outputStream));

		return outputStream.toString();
	}

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}