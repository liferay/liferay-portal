/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.product.navigation.control.menu.InformationMessagesProductNavigationControlMenuEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Víctor Galán
 */
public class LayoutInformationMessagesDisplayContext {

	public LayoutInformationMessagesDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public Map<String, Object> getData() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		boolean showLinkedLayoutMessage = GetterUtil.getBoolean(
			_httpServletRequest.getAttribute(
				InformationMessagesProductNavigationControlMenuEntry.
					INFORMATION_MESSAGES_LINKED_LAYOUT));

		return HashMapBuilder.<String, Object>put(
			"linkedLayoutMessage",
			() -> {
				if (!showLinkedLayoutMessage) {
					return null;
				}

				String message =
					"this-page-is-linked-to-a-site-template-which-does-not-" +
						"allow-modifications-to-it";

				Layout layout = themeDisplay.getLayout();

				Group group = themeDisplay.getScopeGroup();

				if (layout.isLayoutPrototypeLinkActive() &&
					!group.hasStagingGroup()) {

					message = "this-page-is-linked-to-a-page-template";
				}
				else if (_isUserGroupLayout(layout)) {
					message = "this-page-belongs-to-a-user-group";
				}

				return LanguageUtil.get(themeDisplay.getLocale(), message);
			}
		).put(
			"portletNamespace",
			PortalUtil.getPortletNamespace(LayoutAdminPortletKeys.GROUP_PAGES)
		).put(
			"resetPrototypeURL",
			() -> PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"/layout_admin/reset_prototype"
			).setRedirect(
				PortalUtil.getLayoutURL(themeDisplay)
			).setParameter(
				"groupId", themeDisplay.getSiteGroupId()
			).buildString()
		).put(
			"showLinkedLayoutMessage", showLinkedLayoutMessage
		).put(
			"showModifiedLayoutMessage",
			GetterUtil.getBoolean(
				_httpServletRequest.getAttribute(
					InformationMessagesProductNavigationControlMenuEntry.
						INFORMATION_MESSAGES_MODIFIED_LAYOUT))
		).build();
	}

	private boolean _isUserGroupLayout(Layout layout) {
		if (!(layout instanceof VirtualLayout)) {
			return false;
		}

		VirtualLayout virtualLayout = (VirtualLayout)layout;

		Layout sourceLayout = virtualLayout.getSourceLayout();

		Group sourceGroup = sourceLayout.getGroup();

		return sourceGroup.isUserGroup();
	}

	private final HttpServletRequest _httpServletRequest;

}