/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.user.groups.admin.item.selector.UserGroupItemSelectorCriterion;
import com.liferay.user.groups.admin.item.selector.web.internal.display.context.UserGroupItemSelectorViewDisplayContext;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = ItemSelectorView.class)
public class UserGroupItemSelectorView
	implements ItemSelectorView<UserGroupItemSelectorCriterion> {

	@Override
	public Class<UserGroupItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return UserGroupItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "user-groups");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			UserGroupItemSelectorCriterion userGroupItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		UserGroupItemSelectorViewDisplayContext
			userGroupItemSelectorViewDisplayContext =
				new UserGroupItemSelectorViewDisplayContext(
					_userGroupLocalService, userGroupItemSelectorCriterion,
					(HttpServletRequest)servletRequest, portletURL);

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, userGroupItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new UserGroupSelectorViewDescriptor(
				false,
				userGroupItemSelectorViewDisplayContext.getSearchContainer()));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private ItemSelectorViewDescriptorRenderer<UserGroupItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}