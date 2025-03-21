/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Alicia Garcia
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/update_data_definition"
	},
	service = MVCActionCommand.class
)
public class UpdateDataDefinitionMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			return super.processAction(actionRequest, actionResponse);
		}
		catch (PortletException portletException) {
			if (portletException.getCause() instanceof
					DataDefinitionValidationException) {

				DataDefinitionValidationException
					dataDefinitionValidationException =
						(DataDefinitionValidationException)
							portletException.getCause();

				SessionMessages.add(
					actionRequest,
					_portal.getPortletId(actionRequest) +
						SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

				SessionErrors.add(
					actionRequest, dataDefinitionValidationException.getClass(),
					dataDefinitionValidationException);
			}
			else {
				throw portletException;
			}
		}

		return false;
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		DataDefinitionResource.Builder dataDefinitionResourcedBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinition dataDefinition = DataDefinition.toDTO(
			ParamUtil.getString(actionRequest, "dataDefinition"));

		dataDefinition.setDefaultDataLayout(
			() -> DataLayout.toDTO(
				ParamUtil.getString(actionRequest, "dataLayout")));

		if (ArrayUtil.isEmpty(dataDefinition.getDataDefinitionFields())) {
			throw new DataDefinitionValidationException.MustSetFields();
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourcedBuilder.user(
				themeDisplay.getUser()
			).build();

		dataDefinitionResource.putDataDefinition(
			ParamUtil.getLong(actionRequest, "dataDefinitionId"),
			dataDefinition);
	}

	@Reference
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Reference
	private Portal _portal;

}