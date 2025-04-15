/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.portlet.action;

import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminPortletKeys;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FDSAdminPortletKeys.FDS_ADMIN,
		"mvc.command.name=/frontend_data_set_admin/edit_data_set"
	},
	service = MVCRenderCommand.class
)
public class EditDataSetMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			String dataSetERC = ParamUtil.getString(
				_portal.getHttpServletRequest(renderRequest), "dataSetERC");

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_DATA_SET", _portal.getCompanyId(renderRequest));

			if (objectDefinition == null) {
				throw new NoSuchObjectDefinitionException();
			}

			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				dataSetERC, objectDefinition.getObjectDefinitionId());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_checkPermissions(
				themeDisplay.getPermissionChecker(), objectDefinition,
				objectEntry);

			return "/data_set.jsp";
		}
		catch (NoSuchObjectEntryException | PrincipalException exception) {
			SessionErrors.add(renderRequest, exception.getClass());

			return "/view.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	private void _checkPermissions(
			PermissionChecker permissionChecker,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws PortalException {

		_objectEntryService.checkModelResourcePermission(
			objectDefinition.getObjectDefinitionId(),
			objectEntry.getObjectEntryId(), ActionKeys.UPDATE);

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			_objectEntryService.getModelResourcePermission(
				objectDefinition.getObjectDefinitionId());

		modelResourcePermission.check(
			permissionChecker, objectEntry, ActionKeys.UPDATE);
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

}