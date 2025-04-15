/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryService;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.commerce.service.CommerceAvailabilityEstimateService;
import com.liferay.commerce.stock.activity.CommerceLowStockActivity;
import com.liferay.commerce.stock.activity.CommerceLowStockActivityRegistry;
import com.liferay.commerce.util.comparator.CommerceAvailabilityEstimatePriorityComparator;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Andrea Sbarra
 */
public class CPConfigurationListDisplayContext {

	public CPConfigurationListDisplayContext(
		CommerceAvailabilityEstimateService commerceAvailabilityEstimateService,
		CommerceCatalogService commerceCatalogService,
		CommerceLowStockActivityRegistry commerceLowStockActivityRegistry,
		CPConfigurationEntryService cpConfigurationEntryService,
		CPConfigurationListService cpConfigurationListService,
		CPDefinitionInventoryEngineRegistry cpDefinitionInventoryEngineRegistry,
		CPDefinitionService cpDefinitionService,
		CPMeasurementUnitLocalService cpMeasurementUnitLocalService,
		CPTaxCategoryLocalService cpTaxCategoryLocalService,
		HttpServletRequest httpServletRequest) {

		this.commerceAvailabilityEstimateService =
			commerceAvailabilityEstimateService;
		this.commerceCatalogService = commerceCatalogService;
		this.commerceLowStockActivityRegistry =
			commerceLowStockActivityRegistry;
		this.cpConfigurationEntryService = cpConfigurationEntryService;
		this.cpConfigurationListService = cpConfigurationListService;
		this.cpDefinitionInventoryEngineRegistry =
			cpDefinitionInventoryEngineRegistry;
		this.cpDefinitionService = cpDefinitionService;
		this.cpMeasurementUnitLocalService = cpMeasurementUnitLocalService;
		this.cpTaxCategoryLocalService = cpTaxCategoryLocalService;
		this.httpServletRequest = httpServletRequest;

		cpRequestHelper = new CPRequestHelper(httpServletRequest);

		liferayPortletResponse = cpRequestHelper.getLiferayPortletResponse();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				_getEditCPConfigurationEntryActionURL("setVisible"), "view",
				"update", "set-as-visible",
				LanguageUtil.get(httpServletRequest, "update"), "update", null),
			new FDSActionDropdownItem(
				_getEditCPConfigurationEntryActionURL("setHidden"), "hidden",
				"update", "set-as-not-visible",
				LanguageUtil.get(httpServletRequest, "update"), "update",
				null));
	}

	public List<CommerceAvailabilityEstimate> getCommerceAvailabilityEstimates()
		throws PortalException {

		return commerceAvailabilityEstimateService.
			getCommerceAvailabilityEstimates(
				cpRequestHelper.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				CommerceAvailabilityEstimatePriorityComparator.getInstance(
					true));
	}

	public String getCommerceCatalogName() throws PortalException {
		CPConfigurationList cpConfigurationList = getCPConfigurationList();

		if (cpConfigurationList == null) {
			return StringPool.BLANK;
		}

		CommerceCatalog commerceCatalog =
			cpConfigurationList.fetchCommerceCatalog();

		if (commerceCatalog == null) {
			return StringPool.BLANK;
		}

		return commerceCatalog.getName();
	}

	public List<CommerceCatalog> getCommerceCatalogs() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return commerceCatalogService.search(
			themeDisplay.getCompanyId(), null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public List<CommerceLowStockActivity> getCommerceLowStockActivities() {
		return commerceLowStockActivityRegistry.getCommerceLowStockActivities();
	}

	public Map<String, Object> getContext() {
		return HashMapBuilder.<String, Object>put(
			"addCPConfigurationListRenderURL",
			() -> PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCRenderCommandName(
				"/cp_configuration_lists/add_cp_configuration_list"
			).setBackURL(
				cpRequestHelper.getCurrentURL()
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"editCPConfigurationListRenderURL",
			() -> PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCRenderCommandName(
				"/cp_configuration_lists/edit_cp_configuration_list"
			).setBackURL(
				cpRequestHelper.getCurrentURL()
			).buildString()
		).put(
			"namespace", liferayPortletResponse.getNamespace()
		).put(
			"windowState", LiferayWindowState.MAXIMIZED.toString()
		).build();
	}

	public CPConfigurationEntry getCPConfigurationEntry()
		throws PortalException {

		if (_cpConfigurationEntry != null) {
			return _cpConfigurationEntry;
		}

		long cpConfigurationEntryId = getCPConfigurationEntryId();

		if (cpConfigurationEntryId == 0) {
			return null;
		}

		_cpConfigurationEntry =
			cpConfigurationEntryService.getCPConfigurationEntry(
				cpConfigurationEntryId);

		return _cpConfigurationEntry;
	}

	public List<FDSActionDropdownItem>
			getCPConfigurationEntryFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, CPConfigurationList.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/cp_configuration_lists/edit_cp_configuration_entry"
				).setParameter(
					"cpConfigurationEntryId", "{id}"
				).setParameter(
					"cpConfigurationListId", getCPConfigurationListId()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				"/o/headless-commerce-admin-catalog/v1.0" +
					"/product-configurations/{id}",
				"trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	public long getCPConfigurationEntryId() throws PortalException {
		if (Objects.equals(
				httpServletRequest.getParameter("screenNavigationCategoryKey"),
				"details")) {

			CPConfigurationList cpConfigurationList = getCPConfigurationList();

			if (cpConfigurationList != null) {
				return cpConfigurationList.getTemplateCPConfigurationEntryId();
			}
		}

		return ParamUtil.getLong(httpServletRequest, "cpConfigurationEntryId");
	}

	public CPConfigurationList getCPConfigurationList() throws PortalException {
		long cpConfigurationListId = getCPConfigurationListId();

		if (cpConfigurationListId == 0) {
			return null;
		}

		return cpConfigurationListService.getCPConfigurationList(
			cpConfigurationListId);
	}

	public List<FDSActionDropdownItem>
			getCPConfigurationListFDSActionDropdownItems()
		throws PortalException {

		StringBundler sb = new StringBundler(
			"/o/headless-commerce-admin-catalog/v1.0" +
				"/product-configuration-lists/{id}");

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, CPConfigurationList.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/cp_configuration_lists/edit_cp_configuration_list"
				).setParameter(
					"cpConfigurationListId", "{id}"
				).setParameter(
					"screenNavigationCategoryKey",
					CPDefinitionScreenNavigationConstants.CATEGORY_KEY_DETAILS
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", null, null),
			new FDSActionDropdownItem(
				sb.toString(), "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	public long getCPConfigurationListId() {
		return ParamUtil.getLong(httpServletRequest, "cpConfigurationListId");
	}

	public List<CPDefinitionInventoryEngine> getCPDefinitionInventoryEngines() {
		return cpDefinitionInventoryEngineRegistry.
			getCPDefinitionInventoryEngines();
	}

	public String getCPMeasurementUnitName(int type) {
		ThemeDisplay themeDisplay = cpRequestHelper.getThemeDisplay();

		CPMeasurementUnit cpMeasurementUnit =
			cpMeasurementUnitLocalService.fetchPrimaryCPMeasurementUnit(
				themeDisplay.getCompanyId(), type);

		if (cpMeasurementUnit != null) {
			return cpMeasurementUnit.getName(themeDisplay.getLanguageId());
		}

		return StringPool.BLANK;
	}

	public List<CPTaxCategory> getCPTaxCategories() {
		ThemeDisplay themeDisplay = cpRequestHelper.getThemeDisplay();

		return cpTaxCategoryLocalService.getCPTaxCategories(
			themeDisplay.getCompanyId());
	}

	public CreationMenu getCreationMenu() throws Exception {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("addCPConfigurationList");
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "add-new-product-configuration"));
				dropdownItem.setTarget("event");
			}
		).build();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		CPConfigurationList cpConfigurationList = getCPConfigurationList();

		if ((cpConfigurationList != null) &&
			Objects.equals(
				httpServletRequest.getParameter("screenNavigationCategoryKey"),
				"details")) {

			headerActionModels.add(
				new HeaderActionModel(
					"btn-primary", liferayPortletResponse.getNamespace() + "fm",
					null, null, "save",
					liferayPortletResponse.getNamespace() + "saveButton"));
		}
		else if ((cpConfigurationList != null) &&
				 Objects.equals(
					 httpServletRequest.getParameter(
						 "screenNavigationCategoryKey"),
					 "qualifiers")) {

			HeaderActionModel saveHeaderActionModel = new HeaderActionModel(
				"btn-primary", liferayPortletResponse.getNamespace() + "fm",
				PortletURLBuilder.createActionURL(
					liferayPortletResponse
				).setActionName(
					"/cp_configuration_lists" +
						"/edit_cp_configuration_list_qualifiers"
				).buildString(),
				null, "save");

			headerActionModels.add(saveHeaderActionModel);
		}

		return headerActionModels;
	}

	public String getParentCPConfigurationListName() throws PortalException {
		CPConfigurationList cpConfigurationList = getCPConfigurationList();

		if (cpConfigurationList == null) {
			return StringPool.BLANK;
		}

		long parentCPConfigurationListId =
			cpConfigurationList.getParentCPConfigurationListId();

		if (parentCPConfigurationListId == 0) {
			return StringPool.BLANK;
		}

		CPConfigurationList parentCPConfigurationList =
			cpConfigurationListService.getCPConfigurationList(
				parentCPConfigurationListId);

		return parentCPConfigurationList.getName();
	}

	public String getProductName() throws PortalException {
		if (_cpDefinition != null) {
			return _cpDefinition.getName(
				LocaleUtil.toLanguageId(cpRequestHelper.getLocale()));
		}

		CPConfigurationEntry cpConfigurationEntry = getCPConfigurationEntry();

		if (cpConfigurationEntry == null) {
			return StringPool.BLANK;
		}

		_cpDefinition = cpDefinitionService.getCPDefinition(
			cpConfigurationEntry.getClassPK());

		return _cpDefinition.getName(
			LocaleUtil.toLanguageId(cpRequestHelper.getLocale()));
	}

	public String getProductTypeName() throws PortalException {
		if (_cpDefinition != null) {
			return _cpDefinition.getProductTypeName();
		}

		CPConfigurationEntry cpConfigurationEntry = getCPConfigurationEntry();

		if (cpConfigurationEntry == null) {
			return StringPool.BLANK;
		}

		_cpDefinition = cpDefinitionService.fetchCPDefinition(
			cpConfigurationEntry.getClassPK());

		if (_cpDefinition == null) {
			return StringPool.BLANK;
		}

		return _cpDefinition.getProductTypeName();
	}

	public long getTemplateCPConfigurationEntryId() throws PortalException {
		CPConfigurationList cpConfigurationList = getCPConfigurationList();

		if (cpConfigurationList == null) {
			return 0;
		}

		return cpConfigurationList.getTemplateCPConfigurationEntryId();
	}

	protected final CommerceAvailabilityEstimateService
		commerceAvailabilityEstimateService;
	protected final CommerceCatalogService commerceCatalogService;
	protected final CommerceLowStockActivityRegistry
		commerceLowStockActivityRegistry;
	protected final CPConfigurationEntryService cpConfigurationEntryService;
	protected final CPConfigurationListService cpConfigurationListService;
	protected final CPDefinitionInventoryEngineRegistry
		cpDefinitionInventoryEngineRegistry;
	protected final CPDefinitionService cpDefinitionService;
	protected final CPMeasurementUnitLocalService cpMeasurementUnitLocalService;
	protected final CPRequestHelper cpRequestHelper;
	protected final CPTaxCategoryLocalService cpTaxCategoryLocalService;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;

	private String _getEditCPConfigurationEntryActionURL(String cmd) {
		return PortletURLBuilder.createActionURL(
			cpRequestHelper.getRenderResponse()
		).setActionName(
			"/cp_configuration_lists/edit_cp_configuration_entry"
		).setCMD(
			cmd
		).buildString();
	}

	private CPConfigurationEntry _cpConfigurationEntry;
	private CPDefinition _cpDefinition;

}