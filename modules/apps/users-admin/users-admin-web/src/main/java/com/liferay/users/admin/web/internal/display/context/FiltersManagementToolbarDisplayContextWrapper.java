/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContextWrapper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemList;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.users.admin.management.toolbar.FilterContributor;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Drew Brokke
 */
public class FiltersManagementToolbarDisplayContextWrapper
	extends ManagementToolbarDisplayContextWrapper {

	public FiltersManagementToolbarDisplayContextWrapper(
		FilterContributor[] filterContributors,
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		ManagementToolbarDisplayContext managementToolbarDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			managementToolbarDisplayContext);

		_filterContributors = filterContributors;
	}

	@Override
	public String getClearResultsURL() {
		String clearResultsURL = super.getClearResultsURL();

		for (FilterContributor filterContributor : _filterContributors) {
			clearResultsURL = HttpComponentsUtil.removeParameter(
				clearResultsURL,
				liferayPortletResponse.getNamespace() +
					filterContributor.getParameter());
		}

		return clearResultsURL;
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		DropdownItemList filterDropdownItems =
			(DropdownItemList)super.getFilterDropdownItems();

		for (FilterContributor filterContributor : _filterContributors) {
			filterDropdownItems.addGroup(
				dropdownGroupItem -> {
					Map<String, String> entriesMap = new LinkedHashMap<>();

					for (String value : filterContributor.getValues()) {
						entriesMap.put(
							filterContributor.getValueLabel(
								httpServletRequest.getLocale(), value),
							value);
					}

					dropdownGroupItem.setDropdownItems(
						getDropdownItems(
							entriesMap, getPortletURL(),
							filterContributor.getParameter(),
							_getCurrentValue(
								httpServletRequest, filterContributor)));
					dropdownGroupItem.setLabel(
						filterContributor.getLabel(
							httpServletRequest.getLocale()));
				});
		}

		return filterDropdownItems;
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		LabelItemList filterLabelItems =
			(LabelItemList)super.getFilterLabelItems();

		for (FilterContributor filterContributor : _filterContributors) {
			String currentValue = _getCurrentValue(
				httpServletRequest, filterContributor);

			if (ArrayUtil.contains(
					filterContributor.getFilterLabelValues(), currentValue)) {

				filterLabelItems.add(
					labelItem -> {
						labelItem.putData(
							"removeLabelURL",
							PortletURLBuilder.create(
								getPortletURL()
							).setParameter(
								filterContributor.getParameter(), (String)null
							).buildString());

						labelItem.setCloseable(true);
						labelItem.setLabel(
							String.format(
								"%s: %s",
								filterContributor.getShortLabel(
									httpServletRequest.getLocale()),
								filterContributor.getValueLabel(
									httpServletRequest.getLocale(),
									currentValue)));
					});
			}
		}

		return filterLabelItems;
	}

	private String _getCurrentValue(
		HttpServletRequest httpServletRequest,
		FilterContributor filterContributor) {

		return ParamUtil.getString(
			httpServletRequest, filterContributor.getParameter(),
			filterContributor.getDefaultValue());
	}

	private final FilterContributor[] _filterContributors;

}