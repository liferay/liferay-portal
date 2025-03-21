/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.constants.SharingPortletKeys;
import com.liferay.sharing.web.internal.constants.SharingWebKeys;
import com.liferay.sharing.web.internal.display.SharingEntryPermissionDisplayAction;
import com.liferay.sharing.web.internal.helper.SharingHelper;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SharingPortletKeys.SHARING,
		"mvc.command.name=/"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		renderRequest.setAttribute(
			SharingWebKeys.SHARING_REACT_DATA,
			HashMapBuilder.<String, Object>put(
				"autocompleteUserURL", _getAutocompleteUserURL(renderResponse)
			).put(
				"classNameId", ParamUtil.getLong(renderRequest, "classNameId")
			).put(
				"classPK", ParamUtil.getLong(renderRequest, "classPK")
			).put(
				"dialogId",
				ParamUtil.getString(
					renderRequest, SharingWebKeys.SHARING_DIALOG_ID)
			).put(
				"portletNamespace", renderResponse.getNamespace()
			).put(
				"shareActionURL", _getShareActionURL(renderResponse)
			).put(
				"sharingEntryPermissionDisplayActionId",
				SharingEntryPermissionDisplayAction.VIEW.getActionId()
			).put(
				"sharingEntryPermissionDisplays",
				_sharingHelper.getSharingEntryPermissionDisplays(
					themeDisplay.getPermissionChecker(),
					ParamUtil.getLong(renderRequest, "classNameId"),
					ParamUtil.getLong(renderRequest, "classPK"),
					themeDisplay.getScopeGroupId(), themeDisplay.getLocale())
			).put(
				"sharingVerifyEmailAddressURL",
				_getSharingVerifyEmailAddressURL(renderResponse)
			).build());

		return "/sharing/view.jsp";
	}

	private String _getAutocompleteUserURL(RenderResponse renderResponse) {
		ResourceURL autocompleteUserURL = renderResponse.createResourceURL();

		autocompleteUserURL.setResourceID("/sharing/autocomplete_user");

		return autocompleteUserURL.toString();
	}

	private String _getShareActionURL(RenderResponse renderResponse) {
		return PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			"/sharing/share_entry"
		).buildString();
	}

	private String _getSharingVerifyEmailAddressURL(
		RenderResponse renderResponse) {

		ResourceURL sharingVerifyEmailAddressURL =
			renderResponse.createResourceURL();

		sharingVerifyEmailAddressURL.setResourceID(
			"/sharing/verify_email_address");

		return sharingVerifyEmailAddressURL.toString();
	}

	@Reference
	private SharingHelper _sharingHelper;

}