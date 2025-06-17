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
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.user.groups.admin.item.selector.UserGroupSiteMembershipItemSelectorCriterion;
import com.liferay.user.groups.admin.item.selector.web.internal.display.context.UserGroupSiteMembershipItemSelectorViewDisplayContext;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

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
 * @author Eudaldo Alonso
 */
@Component(service = ItemSelectorView.class)
public class UserGroupSiteMembershipItemSelectorView
	implements ItemSelectorView<UserGroupSiteMembershipItemSelectorCriterion> {

	@Override
	public Class<UserGroupSiteMembershipItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return UserGroupSiteMembershipItemSelectorCriterion.class;
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
			UserGroupSiteMembershipItemSelectorCriterion
				userGroupSiteMembershipItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		RenderResponse renderResponse =
			(RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		UserGroupSiteMembershipItemSelectorViewDisplayContext
			userGroupSiteMembershipItemSelectorViewDisplayContext =
				new UserGroupSiteMembershipItemSelectorViewDisplayContext(
					httpServletRequest, portletURL, renderRequest,
					renderResponse);

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			userGroupSiteMembershipItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new UserGroupSelectorViewDescriptor(
				true,
				userGroupSiteMembershipItemSelectorViewDisplayContext.
					getUserGroupSearchContainer()));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<UserGroupSiteMembershipItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

}