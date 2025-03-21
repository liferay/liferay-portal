/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.web.internal.util.DataDefinitionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leticia Maciel
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/import_and_override_data_definition"
	},
	service = MVCActionCommand.class
)
public class ImportAndOverrideDataDefinitionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			DataDefinitionResource.Builder dataDefinitionResourcedBuilder =
				_dataDefinitionResourceFactory.create();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			DataDefinitionResource dataDefinitionResource =
				dataDefinitionResourcedBuilder.user(
					themeDisplay.getUser()
				).build();

			long dataDefinitionId = ParamUtil.getLong(
				actionRequest, "dataDefinitionId");

			UploadPortletRequest uploadPortletRequest =
				_portal.getUploadPortletRequest(actionRequest);

			DataDefinition dataDefinition = DataDefinition.toDTO(
				FileUtil.read(uploadPortletRequest.getFile("jsonFile")));

			DDMStructure ddmStructure =
				_ddmStructureLocalService.getDDMStructure(dataDefinitionId);

			DataDefinitionUtil.updateDataDefinitionFields(
				dataDefinition, ddmStructure);

			dataDefinition.setExternalReferenceCode(
				ddmStructure::getExternalReferenceCode);

			dataDefinitionResource.putDataDefinition(
				dataDefinitionId, dataDefinition);

			SessionMessages.add(
				actionRequest, "importDataDefinitionSuccessMessage");

			hideDefaultSuccessMessage(actionRequest);
		}
		catch (Exception exception) {
			_log.error(exception);

			SessionErrors.add(
				actionRequest, "importDataDefinitionErrorMessage", exception);

			hideDefaultErrorMessage(actionRequest);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImportAndOverrideDataDefinitionMVCActionCommand.class);

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private Portal _portal;

}