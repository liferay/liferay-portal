/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;

import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jorge García Jiménez
 */
public class OAuthClientPRLocalMetadataManagementToolbarDisplayContext
	extends BaseManagementToolbarDisplayContext {

	public OAuthClientPRLocalMetadataManagementToolbarDisplayContext(
		PortletURL currentURLObj, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			currentURLObj, liferayPortletRequest.getHttpServletRequest(),
			liferayPortletRequest, liferayPortletResponse);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "deleteOAuthClientPRLocalMetadata");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"deleteOAuthClientPRLocalMetadataURL",
			() -> PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/oauth_client_admin/delete_oauth_client_pr_local_metadata"
			).buildString()
		).build();
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName",
					"/oauth_client_admin/update_oauth_client_pr_local_metadata",
					"redirect", currentURLObj.toString());
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						"add-oauth-client-pr-local-metadata"));
			}
		).build();
	}

	public OrderByComparator<OAuthClientPRLocalMetadata>
		getOrderByComparator() {

		String orderByCol = getOrderByCol();

		String columnName = "createDate";

		if (orderByCol.equals("resource")) {
			columnName = "resource";
		}

		return OrderByComparatorFactoryUtil.create(
			"OAuthClientPRLocalMetadata", columnName,
			Objects.equals(getOrderByType(), "asc"));
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return getOrderByDropdownItems(
			HashMapBuilder.put(
				"createDate", "createDate"
			).put(
				"resource", "resource"
			).build());
	}

}