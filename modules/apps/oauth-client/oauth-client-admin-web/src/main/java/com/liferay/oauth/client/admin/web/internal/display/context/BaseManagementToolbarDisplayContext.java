/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.oauth.client.admin.web.internal.constants.OAuthClientAdminPortletKeys;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Arthur Chan
 */
public abstract class BaseManagementToolbarDisplayContext {

	public BaseManagementToolbarDisplayContext(
		PortletURL currentURLObj, HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		this.currentURLObj = currentURLObj;
		this.httpServletRequest = httpServletRequest;
		this.liferayPortletRequest = liferayPortletRequest;
		this.liferayPortletResponse = liferayPortletResponse;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			httpServletRequest, OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN,
			"entries-display-style", "list", true);

		return _displayStyle;
	}

	public String getOrderByCol() {
		return ParamUtil.getString(liferayPortletRequest, "orderByCol");
	}

	public String getOrderByType() {
		return ParamUtil.getString(liferayPortletRequest, "orderByType", "asc");
	}

	public PortletURL getSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			getCurrentSortingURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildPortletURL();
	}

	public ViewTypeItemList getViewTypes() {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		int cur = ParamUtil.getInteger(
			httpServletRequest, SearchContainer.DEFAULT_CUR_PARAM);

		if (cur > 0) {
			portletURL.setParameter("cur", String.valueOf(cur));
		}

		int delta = ParamUtil.getInteger(
			httpServletRequest, SearchContainer.DEFAULT_DELTA_PARAM);

		if (delta > 0) {
			portletURL.setParameter("delta", String.valueOf(delta));
		}

		portletURL.setParameter(
			"navigation",
			ParamUtil.getString(liferayPortletRequest, "navigation"));
		portletURL.setParameter("orderByCol", getOrderByCol());
		portletURL.setParameter("orderByType", getOrderByType());

		return new ViewTypeItemList(portletURL, getDisplayStyle()) {
			{
				addListViewTypeItem();

				addTableViewTypeItem();
			}
		};
	}

	protected PortletURL getCurrentSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(currentURLObj, liferayPortletResponse)
		).setParameter(
			SearchContainer.DEFAULT_CUR_PARAM, "0"
		).buildPortletURL();
	}

	protected List<DropdownItem> getOrderByDropdownItems(
		Map<String, String> orderColumnsMap) {

		return new DropdownItemList() {
			{
				for (Map.Entry<String, String> orderByColEntry :
						orderColumnsMap.entrySet()) {

					add(
						dropdownItem -> {
							String orderByCol = orderByColEntry.getKey();

							dropdownItem.setActive(
								orderByCol.equals(getOrderByCol()));
							dropdownItem.setHref(
								getCurrentSortingURL(), "orderByCol",
								orderByCol);

							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest,
									orderByColEntry.getValue()));
						});
				}
			}
		};
	}

	protected PortletURL currentURLObj;
	protected HttpServletRequest httpServletRequest;
	protected LiferayPortletRequest liferayPortletRequest;
	protected LiferayPortletResponse liferayPortletResponse;

	private String _displayStyle;

}