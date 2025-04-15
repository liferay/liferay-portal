/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.DuplicateRepositoryNameException;
import com.liferay.document.library.kernel.exception.FolderNameException;
import com.liferay.document.library.kernel.exception.RepositoryNameException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.exception.InvalidRepositoryException;
import com.liferay.portal.kernel.exception.NoSuchRepositoryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RepositoryService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/edit_repository"
	},
	service = MVCActionCommand.class
)
public class EditRepositoryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateRepository(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_unmountRepository(actionRequest);
			}
		}
		catch (NoSuchRepositoryException | PrincipalException exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter(
				"mvcPath", "/document_library/error.jsp");
		}
		catch (InvalidRepositoryException invalidRepositoryException) {
			_log.error(invalidRepositoryException);

			SessionErrors.add(
				actionRequest, invalidRepositoryException.getClass());
		}
		catch (DuplicateFolderNameException | DuplicateRepositoryNameException |
			   FolderNameException | RepositoryNameException exception) {

			SessionErrors.add(actionRequest, exception.getClass());
		}
	}

	private void _unmountRepository(ActionRequest actionRequest)
		throws PortalException {

		long repositoryId = ParamUtil.getLong(actionRequest, "repositoryId");

		_repositoryService.deleteRepository(repositoryId);
	}

	private void _updateRepository(ActionRequest actionRequest)
		throws PortalException {

		long repositoryId = ParamUtil.getLong(actionRequest, "repositoryId");

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");

		if (repositoryId <= 0) {

			// Add repository

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			String className = ParamUtil.getString(actionRequest, "className");

			long classNameId = _portal.getClassNameId(className);

			long folderId = ParamUtil.getLong(actionRequest, "folderId");

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();
			UnicodeProperties typeSettingsUnicodeProperties =
				PropertiesParamUtil.getProperties(actionRequest, "settings--");
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				DLFolder.class.getName(), actionRequest);

			_repositoryService.addRepository(
				null, themeDisplay.getScopeGroupId(), classNameId, folderId,
				name, description, portletDisplay.getId(),
				typeSettingsUnicodeProperties, serviceContext);
		}
		else {

			// Update repository

			_repositoryService.updateRepository(
				repositoryId, name, description);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditRepositoryMVCActionCommand.class);

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryService _repositoryService;

}