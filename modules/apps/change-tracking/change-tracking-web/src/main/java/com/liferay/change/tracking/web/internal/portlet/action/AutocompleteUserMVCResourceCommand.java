/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.UserScreenNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/autocomplete_user"
	},
	service = MVCResourceCommand.class
)
public class AutocompleteUserMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortalException {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			_getUsersJSONArray(resourceRequest));
	}

	private List<User> _getUsers(
			ResourceRequest resourceRequest, ThemeDisplay themeDisplay)
		throws PortalException {

		String keywords = ParamUtil.getString(resourceRequest, "keywords");

		if (Validator.isNull(keywords)) {
			return Collections.emptyList();
		}

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (UserPermissionUtil.contains(
				permissionChecker, ResourceConstants.PRIMKEY_DNE,
				ActionKeys.VIEW)) {

			return _userLocalService.search(
				themeDisplay.getCompanyId(), keywords,
				WorkflowConstants.STATUS_APPROVED, new LinkedHashMap<>(), 0, 20,
				UserScreenNameComparator.getInstance(true));
		}

		long ctCollectionId = ParamUtil.getLong(
			resourceRequest, "ctCollectionId");

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if (CTCollectionPermission.contains(
				permissionChecker, ctCollection, CTActionKeys.INVITE_USERS)) {

			Role role = _roleLocalService.getRole(
				themeDisplay.getCompanyId(), RoleConstants.PUBLICATIONS_USER);

			return _userLocalService.search(
				themeDisplay.getCompanyId(), keywords,
				WorkflowConstants.STATUS_APPROVED,
				LinkedHashMapBuilder.<String, Object>put(
					"inherit", true
				).put(
					"usersRoles", role.getRoleId()
				).build(),
				0, 20, UserScreenNameComparator.getInstance(true));
		}

		User user = themeDisplay.getUser();

		long[] groupIds = user.getGroupIds();
		long[] userGroupIds = user.getUserGroupIds();

		if (ArrayUtil.isEmpty(groupIds) && ArrayUtil.isEmpty(userGroupIds)) {
			return Collections.emptyList();
		}

		return _userLocalService.searchBySocial(
			themeDisplay.getCompanyId(), groupIds, userGroupIds, keywords, 0,
			20, UserScreenNameComparator.getInstance(true));
	}

	private JSONArray _getUsersJSONArray(ResourceRequest resourceRequest)
		throws PortalException {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		long ctCollectionId = ParamUtil.getLong(
			resourceRequest, "ctCollectionId");

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (User user : _getUsers(resourceRequest, themeDisplay)) {
			if ((user.isGuestUser() ||
				 (themeDisplay.getUserId() == user.getUserId())) &&
				(ctCollectionId != CTConstants.CT_COLLECTION_ID_PRODUCTION)) {

				continue;
			}

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(user);

			String portraitURL = StringPool.BLANK;

			if (user.getPortraitId() > 0) {
				portraitURL = user.getPortraitURL(themeDisplay);
			}

			jsonArray.put(
				JSONUtil.put(
					"emailAddress", user.getEmailAddress()
				).put(
					"fullName", user.getFullName()
				).put(
					"hasPublicationsAccess",
					PortletPermissionUtil.contains(
						permissionChecker, PortletKeys.PORTAL,
						ActionKeys.VIEW_CONTROL_PANEL) &&
					PortletPermissionUtil.contains(
						permissionChecker, CTPortletKeys.PUBLICATIONS,
						ActionKeys.ACCESS_IN_CONTROL_PANEL) &&
					PortletPermissionUtil.contains(
						permissionChecker, CTPortletKeys.PUBLICATIONS,
						ActionKeys.VIEW)
				).put(
					"isOwner",
					(ctCollection != null) &&
					(ctCollection.getUserId() == user.getUserId())
				).put(
					"portraitURL", portraitURL
				).put(
					"userId", user.getUserId()
				));
		}

		return jsonArray;
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}