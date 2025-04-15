/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.configuration.icon;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.web.internal.portlet.action.ActionUtil;
import com.liferay.document.library.web.internal.util.DLFolderUtil;
import com.liferay.document.library.web.internal.util.DLPortletConfigurationIconUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"path=-", "path=/document_library/view",
		"path=/document_library/view_folder"
	},
	service = PortletConfigurationIcon.class
)
public class EditFolderPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "pencil";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "edit");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PortletURL portletURL = PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					portletRequest, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setRedirect(
				themeDisplay.getURLCurrent()
			).buildPortletURL();

			Folder folder = ActionUtil.getFolder(portletRequest);

			if (folder == null) {
				portletURL.setParameter(
					"mvcRenderCommandName", "/document_library/edit_folder");
				portletURL.setParameter(
					"folderId",
					String.valueOf(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
				portletURL.setParameter(
					"repositoryId",
					String.valueOf(themeDisplay.getScopeGroupId()));
				portletURL.setParameter("rootFolder", Boolean.TRUE.toString());
			}
			else {
				if (DLFolderUtil.isRepositoryRoot(folder)) {
					portletURL.setParameter(
						"mvcRenderCommandName",
						"/document_library/edit_repository");
				}
				else {
					portletURL.setParameter(
						"mvcRenderCommandName",
						"/document_library/edit_folder");
				}

				portletURL.setParameter(
					"folderId", String.valueOf(folder.getFolderId()));
				portletURL.setParameter(
					"repositoryId", String.valueOf(folder.getRepositoryId()));
			}

			return portletURL.toString();
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public double getWeight() {
		return 180;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		return DLPortletConfigurationIconUtil.runWithDefaultValueOnError(
			false,
			() -> {
				String navigation = ParamUtil.getString(
					portletRequest, "navigation");

				if (Validator.isNotNull(navigation)) {
					return false;
				}

				Folder folder = ActionUtil.getFolder(portletRequest);

				if ((folder == null) && !_isDLWorkflowEnabled()) {
					return false;
				}

				long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

				if (folder != null) {
					folderId = folder.getFolderId();
				}

				ThemeDisplay themeDisplay =
					(ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				boolean hasAdvancedUpdatePermission = _hasPermission(
					ActionKeys.ADVANCED_UPDATE, folderId, themeDisplay);
				boolean hasUpdatePermission = _hasPermission(
					ActionKeys.UPDATE, folderId, themeDisplay);

				if (hasAdvancedUpdatePermission || hasUpdatePermission) {
					if ((folderId ==
							DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) &&
						!hasAdvancedUpdatePermission) {

						return false;
					}

					return true;
				}

				return false;
			});
	}

	private boolean _hasPermission(
			String actionId, long folderId, ThemeDisplay themeDisplay)
		throws PortalException {

		return ModelResourcePermissionUtil.contains(
			_folderModelResourcePermission, themeDisplay.getPermissionChecker(),
			themeDisplay.getScopeGroupId(), folderId, actionId);
	}

	private boolean _isDLWorkflowEnabled() {
		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				DLFileEntry.class.getName());

		if (workflowHandler == null) {
			return false;
		}

		return true;
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}