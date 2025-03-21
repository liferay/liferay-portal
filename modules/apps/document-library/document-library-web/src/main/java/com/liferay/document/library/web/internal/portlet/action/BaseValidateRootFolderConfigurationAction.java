/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchRepositoryEntryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.PortletPreferencesSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ReadOnlyException;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Sergio González
 */
public abstract class BaseValidateRootFolderConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Validator.isNotNull(cmd)) {
			validate(actionRequest);
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Override
	protected void postProcess(
			long companyId, PortletRequest portletRequest, Settings settings)
		throws PortalException {

		PortletPreferencesSettings portletPreferencesSettings =
			(PortletPreferencesSettings)settings.getParentSettings();

		PortletPreferences portletPreferences =
			portletPreferencesSettings.getPortletPreferences();

		long rootFolderId = GetterUtil.getLong(
			portletPreferences.getValue("rootFolderId", null));
		long selectedRepositoryId = GetterUtil.getLong(
			portletPreferences.getValue("selectedRepositoryId", null));

		try {
			_setPortletPreferences(
				portletPreferences, rootFolderId, selectedRepositoryId);
		}
		catch (ReadOnlyException readOnlyException) {
			throw new SystemException(readOnlyException);
		}
	}

	protected void validate(ActionRequest actionRequest)
		throws PortalException {

		_validateRootFolder(actionRequest);
	}

	@Reference
	protected DLAppLocalService dlAppLocalService;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected RepositoryLocalService repositoryLocalService;

	private void _setPortletPreferences(
			PortletPreferences portletPreferences, long rootFolderId,
			long selectedRepositoryId)
		throws PortalException, ReadOnlyException {

		String rootFolderExternalReferenceCode = StringPool.BLANK;

		if (rootFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			Folder folder = dlAppLocalService.getFolder(rootFolderId);

			rootFolderExternalReferenceCode = folder.getExternalReferenceCode();
		}

		portletPreferences.setValue(
			"rootFolderExternalReferenceCode", rootFolderExternalReferenceCode);

		Group selectedGroup = null;
		String selectedRepositoryExternalReferenceCode = StringPool.BLANK;

		Repository selectedRepository = repositoryLocalService.fetchRepository(
			selectedRepositoryId);

		if (selectedRepository == null) {
			selectedGroup = groupLocalService.getGroup(selectedRepositoryId);
		}
		else {
			selectedGroup = groupLocalService.getGroup(
				selectedRepository.getGroupId());
			selectedRepositoryExternalReferenceCode =
				selectedRepository.getExternalReferenceCode();
		}

		portletPreferences.setValue(
			"selectedGroupExternalReferenceCode",
			selectedGroup.getExternalReferenceCode());
		portletPreferences.setValue(
			"selectedRepositoryExternalReferenceCode",
			selectedRepositoryExternalReferenceCode);

		portletPreferences.reset("rootFolderId");
		portletPreferences.reset("selectedRepositoryId");
	}

	private void _validateRootFolder(ActionRequest actionRequest)
		throws PortalException {

		long rootFolderId = GetterUtil.getLong(
			getParameter(actionRequest, "rootFolderId"));

		if (rootFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			try {
				DLAppLocalServiceUtil.getFolder(rootFolderId);
			}
			catch (NoSuchFolderException | NoSuchRepositoryEntryException
						exception) {

				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				SessionErrors.add(actionRequest, "rootFolderIdInvalid");
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseValidateRootFolderConfigurationAction.class);

}