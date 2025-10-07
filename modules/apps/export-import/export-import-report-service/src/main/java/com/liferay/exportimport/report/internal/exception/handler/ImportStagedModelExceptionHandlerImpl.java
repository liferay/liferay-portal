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
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

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

		try {
			long groupId = portletDataContext.getGroupId();

			Group group = _groupLocalService.getGroup(groupId);

			String scope = ExportImportReportEntryUtil.getScope(group);

			if (StringUtil.equals(
					scope, ObjectDefinitionConstants.SCOPE_COMPANY)) {

				groupId = 0;
			}

			_exportImportReportEntryLocalService.
				addErrorExportImportReportEntry(
					groupId, portletDataContext.getCompanyId(),
					externalReferenceCode,
					ExportImportClassedModelUtil.getClassNameId(stagedModel),
					ExportImportClassedModelUtil.getClassPK(stagedModel),
					GetterUtil.getLong(
						portletDataContext.getExportImportProcessId()),
					portletDataException.getMessage(),
					_getErrorStackTrace(portletDataException),
					modelClass.getName(),
					ExportImportReportEntryUtil.getOrigin(), scope,
					ExportImportReportEntryUtil.getScopeKey(group));
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to add error export import report entry with ",
					"external reference code \"", externalReferenceCode,
					"\" and model name \"", modelClass.getName(), "\""),
				exception);
		}
	}

	private String _getErrorStackTrace(Throwable throwable) {
		OutputStream outputStream = new ByteArrayOutputStream();

		throwable.printStackTrace(new PrintStream(outputStream));

		return outputStream.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImportStagedModelExceptionHandlerImpl.class);

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}