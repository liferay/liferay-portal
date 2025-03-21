/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.display.context;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.inventory.configuration.CommerceInventoryGroupConfiguration;
import com.liferay.commerce.inventory.method.CommerceInventoryMethod;
import com.liferay.commerce.inventory.method.CommerceInventoryMethodRegistry;
import com.liferay.commerce.media.CommerceCatalogDefaultImage;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.configuration.AttachmentsConfiguration;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
public class CommerceCatalogDisplayContext {

	public CommerceCatalogDisplayContext(
		AccountEntryService accountEntryService,
		AttachmentsConfiguration attachmentsConfiguration,
		HttpServletRequest httpServletRequest,
		CommerceCatalogDefaultImage commerceCatalogDefaultImage,
		CommerceCatalogService commerceCatalogService,
		ModelResourcePermission<CommerceCatalog>
			commerceCatalogModelResourcePermission,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceInventoryMethodRegistry commerceInventoryMethodRegistry,
		CommercePriceListService commercePriceListService,
		ConfigurationProvider configurationProvider, DLAppService dlAppService,
		ItemSelector itemSelector, Portal portal) {

		_accountEntryService = accountEntryService;
		_attachmentsConfiguration = attachmentsConfiguration;
		_commerceCatalogDefaultImage = commerceCatalogDefaultImage;
		_commerceCatalogService = commerceCatalogService;
		_commerceCatalogModelResourcePermission =
			commerceCatalogModelResourcePermission;
		_commerceCurrencyLocalService = commerceCurrencyLocalService;
		_commerceInventoryMethodRegistry = commerceInventoryMethodRegistry;
		_commercePriceListService = commercePriceListService;
		_configurationProvider = configurationProvider;
		_dlAppService = dlAppService;
		_itemSelector = itemSelector;
		_portal = portal;

		cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public String getAccountEntriesAPIURL() {
		String encodedFilter = URLCodec.encodeURL(
			StringBundler.concat(
				"(status/any(x:(x eq ", WorkflowConstants.STATUS_APPROVED,
				"))) and type eq '",
				AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER,
				StringPool.APOSTROPHE),
			true);

		return "/o/headless-admin-user/v1.0/accounts?filter=" + encodedFilter;
	}

	public AccountEntry getAccountEntry() throws PortalException {
		CommerceCatalog commerceCatalog = getCommerceCatalog();

		if (commerceCatalog == null) {
			return null;
		}

		return _accountEntryService.fetchAccountEntry(
			commerceCatalog.getAccountEntryId());
	}

	public String getAddCommerceCatalogRenderURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			cpRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_catalogs/add_commerce_catalog"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommercePriceList getBaseCommercePriceList(String type)
		throws PortalException {

		CommerceCatalog commerceCatalog = getCommerceCatalog();

		return _commercePriceListService.
			fetchCatalogBaseCommercePriceListByType(
				commerceCatalog.getGroupId(), type);
	}

	public long getBaseCommercePriceListId(String type) throws PortalException {
		CommercePriceList baseCommercePriceList = getBaseCommercePriceList(
			type);

		if (baseCommercePriceList == null) {
			return 0;
		}

		return baseCommercePriceList.getCommercePriceListId();
	}

	public CommerceCatalog getCommerceCatalog() throws PortalException {
		if (_commerceCatalog != null) {
			return _commerceCatalog;
		}

		long commerceCatalogId = ParamUtil.getLong(
			cpRequestHelper.getRequest(), "commerceCatalogId");

		if (commerceCatalogId == 0) {
			return null;
		}

		_commerceCatalog = _commerceCatalogService.fetchCommerceCatalog(
			commerceCatalogId);

		return _commerceCatalog;
	}

	public long getCommerceCatalogId() throws PortalException {
		CommerceCatalog commerceCatalog = getCommerceCatalog();

		if (commerceCatalog == null) {
			return 0;
		}

		return commerceCatalog.getCommerceCatalogId();
	}

	public List<CommerceCurrency> getCommerceCurrencies()
		throws PortalException {

		return _commerceCurrencyLocalService.getCommerceCurrencies(
			cpRequestHelper.getCompanyId(), true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public List<CommerceInventoryMethod> getCommerceInventoryMethods() {
		return _commerceInventoryMethodRegistry.getCommerceInventoryMethods();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (_hasPortletResourcePermission(CPActionKeys.ADD_COMMERCE_CATALOG)) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddCommerceCatalogRenderURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							cpRequestHelper.getRequest(), "add-catalog"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	public AccountEntry getDefaultAccountEntry() throws PortalException {
		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			_accountEntryService.searchAccountEntries(
				null,
				LinkedHashMapBuilder.<String, Object>put(
					"status", WorkflowConstants.STATUS_APPROVED
				).put(
					"types",
					new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER}
				).build(),
				0, 2, "name", false);

		if (baseModelSearchResult.getLength() != 1) {
			return null;
		}

		List<AccountEntry> accountEntries =
			baseModelSearchResult.getBaseModels();

		return accountEntries.get(0);
	}

	public FileEntry getDefaultFileEntry() throws PortalException {
		long fileEntryId = getDefaultFileEntryId();

		if (fileEntryId == 0) {
			return null;
		}

		return _dlAppService.getFileEntry(fileEntryId);
	}

	public long getDefaultFileEntryId() throws PortalException {
		CommerceCatalog commerceCatalog = getCommerceCatalog();

		return _commerceCatalogDefaultImage.getDefaultCatalogFileEntryId(
			commerceCatalog.getGroupId());
	}

	public String getEditCommerceCatalogActionURL() throws Exception {
		CommerceCatalog commerceCatalog = getCommerceCatalog();

		if (commerceCatalog == null) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				cpRequestHelper.getRequest(), CPPortletKeys.COMMERCE_CATALOGS,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_catalogs/edit_commerce_catalog"
		).setCMD(
			Constants.UPDATE
		).setParameter(
			"commerceCatalogId", commerceCatalog.getCommerceCatalogId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public PortletURL getEditCommerceCatalogRenderURL() {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				cpRequestHelper.getRequest(), CPPortletKeys.COMMERCE_CATALOGS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_catalogs/edit_commerce_catalog"
		).buildPortletURL();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		RenderResponse renderResponse = cpRequestHelper.getRenderResponse();

		RenderURL cancelURL = renderResponse.createRenderURL();

		HeaderActionModel cancelHeaderActionModel = new HeaderActionModel(
			null, cancelURL.toString(), null, "cancel");

		headerActionModels.add(cancelHeaderActionModel);

		if (hasModelResourcePermission(
				getCommerceCatalogId(), ActionKeys.UPDATE)) {

			headerActionModels.add(
				new HeaderActionModel(
					"btn-primary", renderResponse.getNamespace() + "fm",
					getEditCommerceCatalogActionURL(), null, "save"));
		}

		return headerActionModels;
	}

	public String[] getImageExtensions() {
		return _attachmentsConfiguration.imageExtensions();
	}

	public String getImageItemSelectorURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		ImageItemSelectorCriterion imageItemSelectorCriterion =
			new ImageItemSelectorCriterion();

		imageItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new FileEntryItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "addFileEntry",
				imageItemSelectorCriterion));
	}

	public long getImageMaxSize() {
		return _attachmentsConfiguration.imageMaxSize();
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			cpRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(
			cpRequestHelper.getRequest(), "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		String filterFields = ParamUtil.getString(
			cpRequestHelper.getRequest(), "filterFields");

		if (Validator.isNotNull(filterFields)) {
			portletURL.setParameter("filterFields", filterFields);
		}

		String filtersLabels = ParamUtil.getString(
			cpRequestHelper.getRequest(), "filtersLabels");

		if (Validator.isNotNull(filtersLabels)) {
			portletURL.setParameter("filtersLabels", filtersLabels);
		}

		String filtersValues = ParamUtil.getString(
			cpRequestHelper.getRequest(), "filtersValues");

		if (Validator.isNotNull(filtersValues)) {
			portletURL.setParameter("filtersValues", filtersValues);
		}

		return portletURL;
	}

	public String getPriceListsAPIURL(String type) throws PortalException {
		String encodedFilter = URLCodec.encodeURL(
			StringBundler.concat(
				"(catalogId/any(x:(x eq ", getCommerceCatalogId(),
				"))) and type eq '", type, StringPool.APOSTROPHE),
			true);

		return "/o/headless-commerce-admin-pricing/v2.0/price-lists?filter=" +
			encodedFilter;
	}

	public boolean hasManageLinkSupplierPermission(String command) {
		if (_hasPortletResourcePermission(
				CPActionKeys.VIEW_COMMERCE_CATALOGS)) {

			if (Constants.UPDATE.equals(command)) {
				return true;
			}
		}
		else {
			if (Constants.ADD.equals(command)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasModelResourcePermission(
			long commerceCatalogId, String actionId)
		throws PortalException {

		return _commerceCatalogModelResourcePermission.contains(
			cpRequestHelper.getPermissionChecker(), commerceCatalogId,
			actionId);
	}

	public boolean isCommerceInventoryMethodSelected(
			long commerceCatalogGroupId, String key)
		throws ConfigurationException {

		CommerceInventoryGroupConfiguration
			commerceInventoryGroupConfiguration =
				_configurationProvider.getGroupConfiguration(
					CommerceInventoryGroupConfiguration.class,
					commerceCatalogGroupId);

		return key.equals(
			commerceInventoryGroupConfiguration.inventoryMethodKey());
	}

	public boolean showBasePriceListInputs() throws PortalException {
		CommercePricingConfiguration commercePricingConfiguration =
			_configurationProvider.getConfiguration(
				CommercePricingConfiguration.class,
				new SystemSettingsLocator(
					CommercePricingConstants.SERVICE_NAME));

		return Objects.equals(
			commercePricingConfiguration.commercePricingCalculationKey(),
			CommercePricingConstants.VERSION_2_0);
	}

	protected final CPRequestHelper cpRequestHelper;

	private boolean _hasPortletResourcePermission(String actionId) {
		PortletResourcePermission portletResourcePermission =
			_commerceCatalogModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			cpRequestHelper.getPermissionChecker(), null, actionId);
	}

	private final AccountEntryService _accountEntryService;
	private final AttachmentsConfiguration _attachmentsConfiguration;
	private CommerceCatalog _commerceCatalog;
	private final CommerceCatalogDefaultImage _commerceCatalogDefaultImage;
	private final ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;
	private final CommerceCatalogService _commerceCatalogService;
	private final CommerceCurrencyLocalService _commerceCurrencyLocalService;
	private final CommerceInventoryMethodRegistry
		_commerceInventoryMethodRegistry;
	private final CommercePriceListService _commercePriceListService;
	private final ConfigurationProvider _configurationProvider;
	private final DLAppService _dlAppService;
	private final ItemSelector _itemSelector;
	private final Portal _portal;

}