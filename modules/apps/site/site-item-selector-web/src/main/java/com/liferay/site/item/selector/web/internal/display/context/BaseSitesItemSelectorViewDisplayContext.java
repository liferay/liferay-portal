/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.item.selector.web.internal.display.context;

import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.item.selector.display.context.SitesItemSelectorViewDisplayContext;
import com.liferay.site.item.selector.web.internal.constants.SitesItemSelectorWebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Julio Camarero
 */
public abstract class BaseSitesItemSelectorViewDisplayContext
	implements SitesItemSelectorViewDisplayContext {

	public BaseSitesItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest,
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		String itemSelectedEventName, PortletURL portletURL) {

		this.httpServletRequest = httpServletRequest;
		this.groupItemSelectorCriterion = groupItemSelectorCriterion;
		_itemSelectedEventName = itemSelectedEventName;
		this.portletURL = portletURL;
	}

	@Override
	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			httpServletRequest, SitesItemSelectorWebKeys.SITES_ITEM_SELECTOR,
			"icon");

		return _displayStyle;
	}

	@Override
	public GroupItemSelectorCriterion getGroupItemSelectorCriterion() {
		return groupItemSelectorCriterion;
	}

	@Override
	public String getGroupName(Group group) throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return group.getDescriptiveName(themeDisplay.getLocale());
	}

	@Override
	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	@Override
	public PortletRequest getPortletRequest() {
		return (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
	}

	@Override
	public PortletResponse getPortletResponse() {
		return (PortletResponse)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE);
	}

	@Override
	public PortletURL getPortletURL() throws PortletException {
		return PortletURLUtil.clone(
			portletURL,
			PortalUtil.getLiferayPortletResponse(getPortletResponse()));
	}

	@Override
	public boolean isShowChildSitesLink() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	@Override
	public boolean isShowSortFilter() {
		return false;
	}

	protected final GroupItemSelectorCriterion groupItemSelectorCriterion;
	protected final HttpServletRequest httpServletRequest;
	protected final PortletURL portletURL;

	private String _displayStyle;
	private final String _itemSelectedEventName;

}