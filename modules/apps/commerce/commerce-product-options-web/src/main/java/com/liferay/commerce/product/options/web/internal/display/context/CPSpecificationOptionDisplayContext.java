/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.display.context;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.options.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.commerce.product.options.web.internal.util.CPOptionsPortletUtil;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Di Giorgi
 */
public class CPSpecificationOptionDisplayContext
	extends BaseCPOptionsDisplayContext<CPSpecificationOption> {

	public CPSpecificationOptionDisplayContext(
			ActionHelper actionHelper, HttpServletRequest httpServletRequest,
			CPOptionCategoryService cpOptionCategoryService,
			CPSpecificationOptionService cpSpecificationOptionService,
			PortletResourcePermission portletResourcePermission)
		throws PortalException {

		super(
			actionHelper, httpServletRequest,
			CPSpecificationOption.class.getSimpleName(),
			portletResourcePermission);

		_cpOptionCategoryService = cpOptionCategoryService;
		_cpSpecificationOptionService = cpSpecificationOptionService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		setDefaultOrderByCol("label");
	}

	public List<CPOptionCategory> getCPOptionCategories()
		throws PortalException {

		BaseModelSearchResult<CPOptionCategory>
			cpOptionCategoryBaseModelSearchResult =
				_cpOptionCategoryService.searchCPOptionCategories(
					cpRequestHelper.getCompanyId(), null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

		return cpOptionCategoryBaseModelSearchResult.getBaseModels();
	}

	public String getCPOptionCategoryTitle(
			CPSpecificationOption cpSpecificationOption)
		throws PortalException {

		CPOptionCategory cpOptionCategory =
			_cpOptionCategoryService.fetchCPOptionCategory(
				cpSpecificationOption.getCPOptionCategoryId());

		if (cpOptionCategory != null) {
			return cpOptionCategory.getTitle(_themeDisplay.getLocale());
		}

		return StringPool.BLANK;
	}

	public CreationMenu getCreationMenu(
		CPSpecificationOption cpSpecificationOption) {

		if (cpSpecificationOption == null) {
			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_getAddSpecificationOptionListTypeDefinitionRenderURL(
						cpSpecificationOption, Constants.ADD));

				dropdownItem.setLabel(
					LanguageUtil.get(
						cpRequestHelper.getRequest(), "create-a-new-picklist"));

				dropdownItem.setTarget("modal");

				dropdownItem.setData(
					HashMapBuilder.<String, Object>put(
						"size", "default"
					).build());
			}
		).addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_getAddSpecificationOptionListTypeDefinitionRenderURL(
						cpSpecificationOption, Constants.ASSIGN));

				dropdownItem.setLabel(
					LanguageUtil.get(
						cpRequestHelper.getRequest(),
						"add-an-existing-picklist"));

				dropdownItem.setTarget("modal");

				dropdownItem.setData(
					HashMapBuilder.<String, Object>put(
						"size", "default"
					).build());
			}
		).build();
	}

	public List<HeaderActionModel> getHeaderActionModels() {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		LiferayPortletResponse liferayPortletResponse =
			cpRequestHelper.getLiferayPortletResponse();

		HeaderActionModel publishHeaderActionModel = new HeaderActionModel(
			"btn-primary", liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/cp_specification_options/edit_cp_specification_option"
			).buildString(),
			liferayPortletResponse.getNamespace() + "publishButton", "publish");

		headerActionModels.add(publishHeaderActionModel);

		return headerActionModels;
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setNavigation(
			_getNavigation()
		).buildPortletURL();
	}

	@Override
	public SearchContainer<CPSpecificationOption> getSearchContainer()
		throws PortalException {

		if (searchContainer != null) {
			return searchContainer;
		}

		searchContainer = new SearchContainer<>(
			liferayPortletRequest, getPortletURL(), null,
			"no-specification-labels-were-found");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByComparator(
			CPOptionsPortletUtil.getCPSpecificationOptionOrderByComparator(
				getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());

		Boolean facetable = null;

		String navigation = _getNavigation();

		if (navigation.equals("no")) {
			facetable = false;
		}
		else if (navigation.equals("yes")) {
			facetable = true;
		}

		searchContainer.setResultsAndTotal(
			_cpSpecificationOptionService.searchCPSpecificationOptions(
				cpRequestHelper.getCompanyId(), facetable, null, getKeywords(),
				searchContainer.getStart(), searchContainer.getEnd(),
				CPOptionsPortletUtil.getCPSpecificationOptionSort(
					getOrderByCol(), getOrderByType())));
		searchContainer.setRowChecker(getRowChecker());

		return searchContainer;
	}

	private String _getAddSpecificationOptionListTypeDefinitionRenderURL(
		CPSpecificationOption cpSpecificationOption, String actionCommand) {

		return PortletURLBuilder.createRenderURL(
			cpRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/cp_specification_options" +
				"/add_cp_specification_option_list_type_definition"
		).setCMD(
			actionCommand
		).setParameter(
			"cpSpecificationOptionId",
			cpSpecificationOption.getCPSpecificationOptionId()
		).setParameter(
			"cpSpecificationOptionTitle",
			cpSpecificationOption.getTitle(_themeDisplay.getLocale())
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private String _getNavigation() {
		return ParamUtil.getString(httpServletRequest, "navigation");
	}

	private final CPOptionCategoryService _cpOptionCategoryService;
	private final CPSpecificationOptionService _cpSpecificationOptionService;
	private final ThemeDisplay _themeDisplay;

}