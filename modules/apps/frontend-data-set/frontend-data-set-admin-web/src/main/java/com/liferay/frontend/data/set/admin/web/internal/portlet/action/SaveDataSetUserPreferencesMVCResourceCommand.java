/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.portlet.action;

import com.liferay.frontend.data.set.constants.FDSAdminPortletKeys;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juanjo Fernández
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FDSAdminPortletKeys.FDS_ADMIN,
		"mvc.command.name=/frontend_data_set_admin/save_data_set_user_preferences"
	},
	service = MVCResourceCommand.class
)
public class SaveDataSetUserPreferencesMVCResourceCommand
	extends BaseTransactionalMVCResourceCommand {

	@Override
	protected void doTransactionalCommand(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		if (user.isGuestUser()) {
			return;
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(resourceRequest));

		JSONObject preferencesJSONObject = _jsonFactory.createJSONObject();

		long companyId = themeDisplay.getCompanyId();

		JSONObject payloadJSONObject = _jsonFactory.createJSONObject(
			ParamUtil.getString(httpServletRequest, "preferences"));

		_checkStartupSnapshotERCUserPreference(
			companyId, payloadJSONObject, preferencesJSONObject, user);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_USER_PREFERENCES", companyId);

		String fdsName = ParamUtil.getString(httpServletRequest, "fdsName");

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		_objectEntryLocalService.addOrUpdateObjectEntry(
			user.getExternalReferenceCode() + StringPool.UNDERLINE + fdsName, 0,
			user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"preferences", preferencesJSONObject.toString()
			).build(),
			serviceContext);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, preferencesJSONObject);
	}

	private void _checkStartupSnapshotERCUserPreference(
			long companyId, JSONObject payloadJSONObject,
			JSONObject preferencesJSONObject, User user)
		throws Exception {

		String startupSnapshotERC = payloadJSONObject.getString(
			"startupSnapshotERC");

		if (Validator.isNull(startupSnapshotERC)) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SNAPSHOT", companyId);

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			startupSnapshotERC, 0, objectDefinition.getObjectDefinitionId());

		if (objectEntry == null) {
			throw new PortalException(
				"Unable to find data set snapshot with external reference " +
					"code " + startupSnapshotERC);
		}

		if ((objectEntry.getUserId() != user.getUserId()) &&
			!_sharingEntryLocalService.hasSharingPermission(
				user.getUserId(),
				_classNameLocalService.getClassNameId(
					objectDefinition.getClassName()),
				objectEntry.getObjectEntryId(), SharingEntryAction.VIEW)) {

			throw new PrincipalException(
				"User does not have permission to access data set snapshot " +
					"with external reference code " + startupSnapshotERC);
		}

		preferencesJSONObject.put("startupSnapshotERC", startupSnapshotERC);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

}