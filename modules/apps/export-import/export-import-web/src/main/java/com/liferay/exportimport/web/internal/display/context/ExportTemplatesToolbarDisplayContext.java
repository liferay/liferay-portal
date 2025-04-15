/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.util.comparator.ExportImportConfigurationNameComparator;
import com.liferay.exportimport.web.internal.search.ExportImportConfigurationDisplayTerms;
import com.liferay.exportimport.web.internal.search.ExportImportConfigurationSearchTerms;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.BaseManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.site.display.context.GroupDisplayContextHelper;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Péter Alius
 * @author Péter Borkuti
 */
public class ExportTemplatesToolbarDisplayContext
	extends BaseManagementToolbarDisplayContext {

	public ExportTemplatesToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long liveGroupId,
		Company company, PortletURL iteratorURL) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse);

		searchContainer = _createSearchContainer(
			liveGroupId, company, iteratorURL);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getRenderURL()
		).setMVCPath(
			"/export/export_templates/view_export_configurations.jsp"
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				GroupDisplayContextHelper groupDisplayContextHelper =
					new GroupDisplayContextHelper(httpServletRequest);

				dropdownItem.setHref(
					getRenderURL(), "mvcRenderCommandName",
					"/export_import/edit_export_configuration", Constants.CMD,
					Constants.ADD, "groupId",
					groupDisplayContextHelper.getGroupId(), "liveGroupId",
					groupDisplayContextHelper.getLiveGroupId(), "privateLayout",
					Boolean.FALSE.toString());

				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "new"));
			}
		).build();
	}

	@Override
	public int getItemsTotal() {
		return searchContainer.getTotal();
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getRenderURL()
		).setMVCRenderCommandName(
			"/export_import/view_export_configurations"
		).buildString();
	}

	public SearchContainer<ExportImportConfiguration> getSearchContainer() {
		return searchContainer;
	}

	protected PortletURL getRenderURL() {
		return liferayPortletResponse.createRenderURL();
	}

	protected SearchContainer<ExportImportConfiguration> searchContainer;

	private SearchContainer<ExportImportConfiguration> _createSearchContainer(
		long liveGroupId, Company company, PortletURL iteratorURL) {

		ExportImportConfigurationSearchTerms
			exportImportConfigurationSearchTerms =
				new ExportImportConfigurationSearchTerms(liferayPortletRequest);

		SearchContainer<ExportImportConfiguration> searchContainer =
			new SearchContainer(
				liferayPortletRequest,
				new ExportImportConfigurationDisplayTerms(
					liferayPortletRequest),
				exportImportConfigurationSearchTerms,
				SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA, iteratorURL, null,
				"there-are-no-saved-export-templates");

		searchContainer.setOrderByCol("name");
		searchContainer.setOrderByComparator(
			ExportImportConfigurationNameComparator.getInstance(
				Objects.equals(getOrderByType(), "asc")));
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setResultsAndTotal(
			() ->
				ExportImportConfigurationLocalServiceUtil.
					getExportImportConfigurations(
						company.getCompanyId(), liveGroupId,
						exportImportConfigurationSearchTerms.getKeywords(),
						ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
						searchContainer.getStart(), searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
			ExportImportConfigurationLocalServiceUtil.
				getExportImportConfigurationsCount(
					company.getCompanyId(), liveGroupId,
					exportImportConfigurationSearchTerms.getKeywords(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT));

		return searchContainer;
	}

}