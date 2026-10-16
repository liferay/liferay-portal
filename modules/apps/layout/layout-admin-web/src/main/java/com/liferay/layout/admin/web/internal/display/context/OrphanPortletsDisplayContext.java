/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.util.OrphanPortletsUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.PortletTitleComparator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class OrphanPortletsDisplayContext {

	public OrphanPortletsDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		PortletRegistry portletRegistry) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portletRegistry = portletRegistry;
	}

	public String getBackURL() {
		if (Validator.isNotNull(_backURL)) {
			return _backURL;
		}

		_backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		return _backURL;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_liferayPortletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
			"orphan-display-style", "list");

		return _displayStyle;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
			"orphan-order-by-type", "asc");

		return _orderByType;
	}

	public List<Portlet> getOrphanPortlets() {
		return getOrphanPortlets(getSelLayout());
	}

	public List<Portlet> getOrphanPortlets(Layout layout) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(_liferayPortletRequest);

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		return ListUtil.sort(
			OrphanPortletsUtil.getOrphanPortlets(layout, _portletRegistry),
			new PortletTitleComparator(
				httpServletRequest.getServletContext(),
				themeDisplay.getLocale(), orderByAsc));
	}

	public SearchContainer<Portlet> getOrphanPortletsSearchContainer() {
		if (_orphanPortletsSearchContainer != null) {
			return _orphanPortletsSearchContainer;
		}

		SearchContainer<Portlet> orphanPortletsSearchContainer =
			new SearchContainer<>(
				_liferayPortletRequest, getPortletURL(), null,
				"there-are-no-items-to-display");

		orphanPortletsSearchContainer.setDeltaConfigurable(false);
		orphanPortletsSearchContainer.setId("portlets");
		orphanPortletsSearchContainer.setOrderByCol("name");
		orphanPortletsSearchContainer.setOrderByType(getOrderByType());
		orphanPortletsSearchContainer.setResultsAndTotal(getOrphanPortlets());

		Layout selLayout = getSelLayout();

		if (!selLayout.isPortletLayoutPageTemplateEntryLinkActive()) {
			orphanPortletsSearchContainer.setRowChecker(
				new EmptyOnClickRowChecker(_liferayPortletResponse));
		}

		_orphanPortletsSearchContainer = orphanPortletsSearchContainer;

		return _orphanPortletsSearchContainer;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/view_orphan_portlets"
		).setBackURL(
			getBackURL()
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();
	}

	public Layout getSelLayout() {
		if (_selLayout != null) {
			return _selLayout;
		}

		if (getSelPlid() != LayoutConstants.DEFAULT_PLID) {
			_selLayout = LayoutLocalServiceUtil.fetchLayout(getSelPlid());
		}

		return _selLayout;
	}

	public Long getSelPlid() {
		if (_selPlid != null) {
			return _selPlid;
		}

		_selPlid = ParamUtil.getLong(
			_liferayPortletRequest, "selPlid", LayoutConstants.DEFAULT_PLID);

		return _selPlid;
	}

	public String getStatus(Portlet portlet) {
		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(_liferayPortletRequest);

		if (!portlet.isActive()) {
			return LanguageUtil.get(httpServletRequest, "inactive");
		}
		else if (!portlet.isReady()) {
			return LanguageUtil.format(
				httpServletRequest, "is-not-ready", "portlet");
		}
		else if (portlet.isUndeployedPortlet()) {
			return LanguageUtil.get(httpServletRequest, "undeployed");
		}

		return LanguageUtil.get(httpServletRequest, "active");
	}

	private String _backURL;
	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByType;
	private SearchContainer<Portlet> _orphanPortletsSearchContainer;
	private final PortletRegistry _portletRegistry;
	private Layout _selLayout;
	private Long _selPlid;

}