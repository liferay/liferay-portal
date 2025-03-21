/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.service.CTCollectionTemplateLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/get_template_collaborators"
	},
	service = MVCResourceCommand.class
)
public class GetTemplateCollaboratorsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long ctCollectionTemplateId = ParamUtil.getLong(
			resourceRequest, "ctCollectionTemplateId");

		if (ctCollectionTemplateId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONArray());

			return;
		}

		CTCollectionTemplate ctCollectionTemplate =
			_ctCollectionTemplateLocalService.fetchCTCollectionTemplate(
				ctCollectionTemplateId);

		if (ctCollectionTemplate == null) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONArray());

			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		User ctCollectionTemplateUser = _userLocalService.fetchUser(
			ctCollectionTemplate.getUserId());

		Group group = ctCollectionTemplateUser.getGroup();

		if (group == null) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonArray);

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONObject collaboratorDataJSONObject =
			ctCollectionTemplate.getJSONObject();

		JSONArray roleValuesJSONArray = collaboratorDataJSONObject.getJSONArray(
			"roleValues");

		JSONArray userIdsJSONArray = collaboratorDataJSONObject.getJSONArray(
			"userIds");

		for (int i = 0; i < userIdsJSONArray.length(); i++) {
			long userId = userIdsJSONArray.getLong(i);

			User user = _userLocalService.getUser(userId);

			if (user == null) {
				continue;
			}

			String portraitURL = StringPool.BLANK;

			if (user.getPortraitId() > 0) {
				portraitURL = user.getPortraitURL(themeDisplay);
			}

			long roleValue = roleValuesJSONArray.getLong(i);

			jsonArray.put(
				JSONUtil.put(
					"emailAddress", user.getEmailAddress()
				).put(
					"fullName", user.getFullName()
				).put(
					"isCurrentUser",
					user.getUserId() == themeDisplay.getUserId()
				).put(
					"isOwner", false
				).put(
					"portraitURL", portraitURL
				).put(
					"roleLabel",
					_language.get(
						themeDisplay.getLocale(), _getRoleLabel(roleValue))
				).put(
					"roleValue", roleValue
				).put(
					"userId", user.getUserId()
				));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonArray);
	}

	private String _getRoleLabel(long roleValue) {
		if (roleValue == PublicationRoleConstants.ROLE_ADMIN) {
			return PublicationRoleConstants.LABEL_ADMIN;
		}
		else if (roleValue == PublicationRoleConstants.ROLE_EDITOR) {
			return PublicationRoleConstants.LABEL_EDITOR;
		}
		else if (roleValue == PublicationRoleConstants.ROLE_PUBLISHER) {
			return PublicationRoleConstants.LABEL_PUBLISHER;
		}

		return PublicationRoleConstants.LABEL_VIEWER;
	}

	@Reference
	private CTCollectionTemplateLocalService _ctCollectionTemplateLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private UserLocalService _userLocalService;

}