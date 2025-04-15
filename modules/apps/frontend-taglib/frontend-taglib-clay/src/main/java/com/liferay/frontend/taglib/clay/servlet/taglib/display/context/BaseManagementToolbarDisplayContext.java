/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class BaseManagementToolbarDisplayContext
	implements ManagementToolbarDisplayContext {

	public BaseManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		this.httpServletRequest = httpServletRequest;
		this.liferayPortletRequest = liferayPortletRequest;
		this.liferayPortletResponse = liferayPortletResponse;

		currentURLObj = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);

		request = httpServletRequest;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #BaseManagementToolbarDisplayContext(HttpServletRequest,
	 *             LiferayPortletRequest, LiferayPortletResponse)}
	 */
	@Deprecated
	public BaseManagementToolbarDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		HttpServletRequest httpServletRequest) {

		this(httpServletRequest, liferayPortletRequest, liferayPortletResponse);
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		return getFilterNavigationDropdownItems();
	}

	@Override
	public String getNamespace() {
		return liferayPortletResponse.getNamespace();
	}

	@Override
	public List<DropdownItem> getOrderDropdownItems() {
		return getOrderByDropdownItems();
	}

	@Override
	public String getSortingOrder() {
		return getOrderByType();
	}

	@Override
	public String getSortingURL() {
		PortletURL sortingURL = getPortletURL();

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			sortingURL.setParameter(getOrderByColParam(), orderByCol);
		}

		sortingURL.setParameter(
			getOrderByTypeParam(),
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc");

		return sortingURL.toString();
	}

	@Override
	public List<ViewTypeItem> getViewTypeItems() {
		if (ArrayUtil.isEmpty(getDisplayViews())) {
			return null;
		}

		return new ViewTypeItemList(getPortletURL(), getDisplayStyle()) {
			{
				if (ArrayUtil.contains(getDisplayViews(), "icon")) {
					addCardViewTypeItem();
				}

				if (ArrayUtil.contains(getDisplayViews(), "descriptive")) {
					addListViewTypeItem();
				}

				if (ArrayUtil.contains(getDisplayViews(), "list")) {
					addTableViewTypeItem();
				}
			}
		};
	}

	protected String getDefaultDisplayStyle() {
		return "list";
	}

	protected Map<String, String> getDefaultEntriesMap(String[] entryKeys) {
		if (ArrayUtil.isEmpty(entryKeys)) {
			return null;
		}

		Map<String, String> entriesMap = new LinkedHashMap<>();

		for (String entryKey : entryKeys) {
			entriesMap.put(entryKey, entryKey);
		}

		return entriesMap;
	}

	protected String getDisplayStyle() {
		return ParamUtil.getString(
			httpServletRequest, "displayStyle", getDefaultDisplayStyle());
	}

	protected String[] getDisplayViews() {
		return new String[0];
	}

	protected List<DropdownItem> getDropdownItems(
		Map<String, String> entriesMap, PortletURL entryURL,
		String parameterName, String parameterValue) {

		if ((entriesMap == null) || entriesMap.isEmpty()) {
			return null;
		}

		return new DropdownItemList() {
			{
				for (Map.Entry<String, String> entry : entriesMap.entrySet()) {
					add(
						dropdownItem -> {
							if (parameterValue != null) {
								dropdownItem.setActive(
									parameterValue.equals(entry.getValue()));
							}

							dropdownItem.setHref(
								entryURL, parameterName, entry.getValue());
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, entry.getKey()));
						});
				}
			}
		};
	}

	protected List<DropdownItem> getFilterNavigationDropdownItems() {
		return getDropdownItems(
			getNavigationEntriesMap(), getPortletURL(), getNavigationParam(),
			getNavigation());
	}

	protected String getFilterNavigationDropdownItemsLabel() {
		return LanguageUtil.get(httpServletRequest, "filter-by");
	}

	protected String getNavigation() {
		return ParamUtil.getString(
			liferayPortletRequest, getNavigationParam(), "all");
	}

	protected Map<String, String> getNavigationEntriesMap() {
		return getDefaultEntriesMap(getNavigationKeys());
	}

	protected String[] getNavigationKeys() {
		return null;
	}

	protected String getNavigationParam() {
		return "navigation";
	}

	protected String getOrderByCol() {
		return ParamUtil.getString(liferayPortletRequest, getOrderByColParam());
	}

	protected String getOrderByColParam() {
		return "orderByCol";
	}

	protected List<DropdownItem> getOrderByDropdownItems() {
		return getDropdownItems(
			getOrderByEntriesMap(), getPortletURL(), getOrderByColParam(),
			getOrderByCol());
	}

	protected String getOrderByDropdownItemsLabel() {
		return LanguageUtil.get(httpServletRequest, "order-by");
	}

	protected Map<String, String> getOrderByEntriesMap() {
		return getDefaultEntriesMap(getOrderByKeys());
	}

	protected String[] getOrderByKeys() {
		return null;
	}

	protected String getOrderByType() {
		return ParamUtil.getString(
			liferayPortletRequest, getOrderByTypeParam(), "asc");
	}

	protected String getOrderByTypeParam() {
		return "orderByType";
	}

	protected PortletURL getPortletURL() {
		try {
			return PortletURLUtil.clone(currentURLObj, liferayPortletResponse);
		}
		catch (PortletException portletException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portletException);
			}

			return PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setParameters(
				currentURLObj.getParameterMap()
			).buildPortletURL();
		}
	}

	protected final PortletURL currentURLObj;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletRequest liferayPortletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #httpServletRequest}
	 */
	@Deprecated
	protected HttpServletRequest request;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseManagementToolbarDisplayContext.class);

}