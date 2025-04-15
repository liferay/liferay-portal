/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Alicia García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/delete_data_definition"
	},
	service = MVCActionCommand.class
)
public class DeleteDataDefinitionMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] deleteDataDefinitionIds = null;

		long dataDefinitionId = ParamUtil.getLong(
			actionRequest, "dataDefinitionId");

		if (dataDefinitionId > 0) {
			deleteDataDefinitionIds = new long[] {dataDefinitionId};
		}
		else {
			deleteDataDefinitionIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				themeDisplay.getUser()
			).build();

		try {
			for (long deleteDataDefinitionId : deleteDataDefinitionIds) {
				dataDefinitionResource.deleteDataDefinition(
					deleteDataDefinitionId);
			}
		}
		finally {
			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			if (Validator.isNotNull(redirect)) {
				actionResponse.sendRedirect(redirect);
			}
		}
	}

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private Portal _portal;

}