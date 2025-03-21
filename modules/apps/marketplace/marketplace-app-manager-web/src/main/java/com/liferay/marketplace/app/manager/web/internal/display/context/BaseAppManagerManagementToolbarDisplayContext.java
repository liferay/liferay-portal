/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.BaseManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.marketplace.app.manager.web.internal.constants.BundleStateConstants;
import com.liferay.marketplace.app.manager.web.internal.util.MarketplaceAppManagerUtil;
import com.liferay.marketplace.service.AppLocalServiceUtil;
import com.liferay.marketplace.util.BundleManagerUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pei-Jung Lan
 */
public abstract class BaseAppManagerManagementToolbarDisplayContext
	extends BaseManagementToolbarDisplayContext {

	public BaseAppManagerManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse);
	}

	public String getCategory() {
		if (Validator.isNull(_category)) {
			_category = ParamUtil.getString(
				httpServletRequest, "category", "all-categories");
		}

		return _category;
	}

	public List<DropdownItem> getCategoryDropdownItems() {
		String[] categories = MarketplaceAppManagerUtil.getCategories(
			AppLocalServiceUtil.getApps(QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			BundleManagerUtil.getBundles());

		Map<String, String> categoriesMap = new LinkedHashMap<>();

		for (String category : categories) {
			String kebabCaseCategory = StringUtil.replace(
				StringUtil.toLowerCase(category), CharPool.SPACE,
				CharPool.DASH);

			String translatedCategory = LanguageUtil.get(
				httpServletRequest, kebabCaseCategory, category);

			categoriesMap.put(translatedCategory, category);
		}

		return getDropdownItems(
			categoriesMap,
			PortletURLBuilder.create(
				getPortletURL()
			).setParameter(
				"resetCur", true
			).buildPortletURL(),
			"category", getCategory());
	}

	@Override
	public String getOrderByCol() {
		return ParamUtil.getString(httpServletRequest, "orderByCol", "title");
	}

	@Override
	public abstract PortletURL getPortletURL();

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			"/view_search_results.jsp"
		).buildString();
	}

	public abstract SearchContainer<Object> getSearchContainer()
		throws Exception;

	public String getState() {
		if (Validator.isNull(_state)) {
			_state = ParamUtil.getString(
				httpServletRequest, "state", "all-statuses");
		}

		return _state;
	}

	public List<DropdownItem> getStatusDropdownItems() {
		String[] states = {
			"all-statuses", BundleStateConstants.ACTIVE_LABEL,
			BundleStateConstants.RESOLVED_LABEL,
			BundleStateConstants.INSTALLED_LABEL
		};

		return getDropdownItems(
			getDefaultEntriesMap(states),
			PortletURLBuilder.create(
				getPortletURL()
			).setParameter(
				"resetCur", true
			).buildPortletURL(),
			"state", getState());
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"title"};
	}

	private String _category;
	private String _state;

}