/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Matija Petanjek
 */
public class BatchPlannerPlanManagementToolbarDisplayContext
	extends BaseSearchContainerManagementToolbarDisplayContext {

	public BatchPlannerPlanManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCRenderCommandName(
						"/batch_planner/edit_export_batch_planner_plan"
					).setBackURL(
						PortalUtil.getCurrentURL(httpServletRequest)
					).setNavigation(
						"export"
					).buildPortletURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "export-file"));
			}
		).addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCRenderCommandName(
						"/batch_planner/edit_import_batch_planner_plan"
					).setBackURL(
						PortalUtil.getCurrentURL(httpServletRequest)
					).setNavigation(
						"import"
					).buildPortletURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "import-file"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> !Objects.equals(getNavigation(), "all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setDismissible(true);
				labelItem.setLabel(
					String.format(
						"%s: %s",
						LanguageUtil.get(httpServletRequest, "action"),
						LanguageUtil.get(httpServletRequest, getNavigation())));
			}
		).build();
	}

	@Override
	public String getFilterNavigationDropdownItemsLabel() {
		return LanguageUtil.get(httpServletRequest, "filter-by-action");
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String getNavigation() {
		return ParamUtil.getString(
			liferayPortletRequest, getNavigationParam(), "all");
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "export", "import"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"createDate"};
	}

}