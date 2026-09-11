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
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
			_writeEmptyResponse(
				new PrincipalException(), resourceRequest, resourceResponse,
				HttpServletResponse.SC_FORBIDDEN);

			return;
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(resourceRequest));

		String fdsName = ParamUtil.getString(httpServletRequest, "fdsName");

		if (Validator.isNull(fdsName)) {
			_writeEmptyResponse(
				new NullPointerException(), resourceRequest, resourceResponse,
				HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		String preferences = ParamUtil.getString(
			httpServletRequest, "preferences");

		if (Validator.isNull(preferences)) {
			_writeEmptyResponse(
				new NullPointerException(), resourceRequest, resourceResponse,
				HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		JSONObject jsonObject;

		try {
			jsonObject = _jsonFactory.createJSONObject(preferences);
		}
		catch (JSONException jsonException) {
			_writeEmptyResponse(
				jsonException, resourceRequest, resourceResponse,
				HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		long companyId = themeDisplay.getCompanyId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_USER_PREFERENCES", companyId);

		if (objectDefinition == null) {
			_writeEmptyResponse(
				new NullPointerException(), resourceRequest, resourceResponse,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			return;
		}

		JSONObject dataSetUserPreferencesJSONObject =
			_jsonFactory.createJSONObject();

		try {
			_checkInitialDataSetSnapshotERC(
				companyId, dataSetUserPreferencesJSONObject,
				jsonObject.getString("initialDataSetSnapshotERC"), user);
		}
		catch (PrincipalException principalException) {
			_writeEmptyResponse(
				principalException, resourceRequest, resourceResponse,
				HttpServletResponse.SC_FORBIDDEN);

			return;
		}
		catch (JSONException jsonException) {
			_writeEmptyResponse(
				jsonException, resourceRequest, resourceResponse,
				HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		_objectEntryLocalService.addOrUpdateObjectEntry(
			user.getExternalReferenceCode() + StringPool.UNDERLINE + fdsName, 0,
			user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"preferences", dataSetUserPreferencesJSONObject.toString()
			).build(),
			serviceContext);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			dataSetUserPreferencesJSONObject);
	}

	private void _checkInitialDataSetSnapshotERC(
			long companyId, JSONObject dataSetUserPreferencesJSONObject,
			String initialDataSetSnapshotERC, User user)
		throws Exception {

		if (Validator.isNull(initialDataSetSnapshotERC)) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_SNAPSHOT", companyId);

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			initialDataSetSnapshotERC, 0,
			objectDefinition.getObjectDefinitionId());

		if (objectEntry == null) {
			throw new PortalException(
				"Unable to find data set snapshot with external reference " +
					"code " + initialDataSetSnapshotERC);
		}

		if ((objectEntry.getUserId() != user.getUserId()) &&
			!_sharingEntryLocalService.hasSharingPermission(
				user.getUserId(),
				_classNameLocalService.getClassNameId(
					objectDefinition.getClassName()),
				objectEntry.getObjectEntryId(), SharingEntryAction.VIEW)) {

			throw new PrincipalException(
				"User does not have permission to access data set snapshot " +
					"with external reference code " +
						initialDataSetSnapshotERC);
		}

		dataSetUserPreferencesJSONObject.put(
			"initialDataSetSnapshotERC", initialDataSetSnapshotERC);
	}

	private void _writeEmptyResponse(
			Exception exception, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse, int statusCode)
		throws IOException {

		if (_log.isWarnEnabled()) {
			_log.warn(exception);
		}

		resourceResponse.setProperty(
			ResourceResponse.HTTP_STATUS_CODE, String.valueOf(statusCode));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, _jsonFactory.createJSONObject());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SaveDataSetUserPreferencesMVCResourceCommand.class);

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