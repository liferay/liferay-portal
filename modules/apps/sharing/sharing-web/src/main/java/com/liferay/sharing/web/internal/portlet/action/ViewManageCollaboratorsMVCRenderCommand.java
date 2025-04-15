/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.web.internal.constants.SharingPortletKeys;
import com.liferay.sharing.web.internal.constants.SharingWebKeys;
import com.liferay.sharing.web.internal.display.SharingEntryPermissionDisplay;
import com.liferay.sharing.web.internal.display.SharingEntryPermissionDisplayAction;
import com.liferay.sharing.web.internal.helper.SharingHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.text.DateFormat;
import java.text.Format;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SharingPortletKeys.MANAGE_COLLABORATORS,
		"mvc.command.name=/"
	},
	service = MVCRenderCommand.class
)
public class ViewManageCollaboratorsMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			SharingWebKeys.SHARING_REACT_DATA,
			HashMapBuilder.<String, Object>put(
				"actionUrl", _getActionURL(renderResponse)
			).put(
				"classNameId", ParamUtil.getLong(renderRequest, "classNameId")
			).put(
				"classPK", ParamUtil.getLong(renderRequest, "classPK")
			).put(
				"collaborators",
				_getCollaboratorsJSONArray(
					ParamUtil.getLong(renderRequest, "classNameId"),
					ParamUtil.getLong(renderRequest, "classPK"), renderRequest)
			).put(
				"dialogId",
				ParamUtil.getString(
					renderRequest,
					SharingWebKeys.MANAGE_COLLABORATORS_DIALOG_ID)
			).put(
				"portletNamespace", renderResponse.getNamespace()
			).build());

		return "/manage_collaborators/view.jsp";
	}

	private String _getActionURL(RenderResponse renderResponse) {
		return PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			"/sharing/edit_collaborators"
		).buildString();
	}

	private JSONArray _getCollaboratorsJSONArray(
			long classNameId, long classPK, RenderRequest renderRequest)
		throws PortletException {

		try {
			int sharingEntriesCount =
				_sharingEntryLocalService.getSharingEntriesCount(
					classNameId, classPK);

			if (sharingEntriesCount == 0) {
				return _jsonFactory.createJSONArray();
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			JSONArray collaboratorsJSONArray = _jsonFactory.createJSONArray();

			List<SharingEntry> sharingEntries =
				_sharingEntryLocalService.getSharingEntries(
					classNameId, classPK);

			for (SharingEntry sharingEntry : sharingEntries) {
				User sharingEntryToUser = _userLocalService.fetchUser(
					sharingEntry.getToUserId());

				String portraitURL = StringPool.BLANK;

				if (sharingEntryToUser.getPortraitId() > 0) {
					portraitURL = sharingEntryToUser.getPortraitURL(
						themeDisplay);
				}

				String expirationDateAsText = null;
				String expirationDateTooltip = null;

				Date expirationDate = sharingEntry.getExpirationDate();

				if (expirationDate != null) {
					DateFormat expirationDateFormat =
						DateFormatFactoryUtil.getSimpleDateFormat(
							"yyyy-MM-dd", themeDisplay.getLocale());

					expirationDateAsText = expirationDateFormat.format(
						expirationDate);

					Format expirationDateTooltipDateFormat =
						DateFormatFactoryUtil.getDate(themeDisplay.getLocale());

					expirationDateTooltip = _language.format(
						themeDisplay.getLocale(), "until-x",
						expirationDateTooltipDateFormat.format(expirationDate));
				}

				SharingEntryPermissionDisplayAction
					userSharingEntryPermissionDisplayActionKey =
						_sharingHelper.
							getSharingEntryPermissionDisplayActionKey(
								sharingEntry);

				collaboratorsJSONArray.put(
					JSONUtil.put(
						"fullName", sharingEntryToUser.getFullName()
					).put(
						"portraitURL", portraitURL
					).put(
						"sharingEntryExpirationDate", expirationDateAsText
					).put(
						"sharingEntryExpirationDateTooltip",
						expirationDateTooltip
					).put(
						"sharingEntryId", sharingEntry.getSharingEntryId()
					).put(
						"sharingEntryPermissionActionId",
						userSharingEntryPermissionDisplayActionKey.getActionId()
					).put(
						"sharingEntryPermissionDisplaySelectOptions",
						_getSharingEntryPermissionDisplaySelectOptionsJSONArray(
							renderRequest)
					).put(
						"sharingEntryShareable", sharingEntry.isShareable()
					).put(
						"userId", Long.valueOf(sharingEntryToUser.getUserId())
					));
			}

			return collaboratorsJSONArray;
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	private JSONArray _getSharingEntryPermissionDisplaySelectOptionsJSONArray(
		RenderRequest renderRequest) {

		long classNameId = ParamUtil.getLong(renderRequest, "classNameId");
		long classPK = ParamUtil.getLong(renderRequest, "classPK");

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<SharingEntryPermissionDisplay> sharingEntryPermissionDisplays =
			_sharingHelper.getSharingEntryPermissionDisplays(
				themeDisplay.getPermissionChecker(), classNameId, classPK,
				themeDisplay.getScopeGroupId(), themeDisplay.getLocale());

		JSONArray sharingEntryPermissionDisplaySelectOptionsJSONArray =
			_jsonFactory.createJSONArray();

		for (SharingEntryPermissionDisplay sharingEntryPermissionDisplay :
				sharingEntryPermissionDisplays) {

			sharingEntryPermissionDisplaySelectOptionsJSONArray.put(
				JSONUtil.put(
					"label", sharingEntryPermissionDisplay.getPhrase()
				).put(
					"value",
					sharingEntryPermissionDisplay.
						getSharingEntryPermissionDisplayActionId()
				));
		}

		return sharingEntryPermissionDisplaySelectOptionsJSONArray;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	@Reference
	private SharingHelper _sharingHelper;

	@Reference
	private UserLocalService _userLocalService;

}