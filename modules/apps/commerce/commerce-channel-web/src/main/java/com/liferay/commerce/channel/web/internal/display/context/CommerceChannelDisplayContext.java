/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.display.context;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.channel.web.internal.display.context.helper.CommerceChannelRequestHelper;
import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.configuration.CommerceOrderConfiguration;
import com.liferay.commerce.configuration.CommerceOrderFieldsConfiguration;
import com.liferay.commerce.configuration.CommerceOrderImporterDateFormatConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.product.channel.CommerceChannelHealthStatus;
import com.liferay.commerce.product.channel.CommerceChannelHealthStatusRegistry;
import com.liferay.commerce.product.channel.CommerceChannelType;
import com.liferay.commerce.product.channel.CommerceChannelTypeRegistry;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.tax.configuration.CommerceShippingTaxConfiguration;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.marketplace.constants.MarketplaceActionKeys;
import com.liferay.marketplace.constants.MarketplacePortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
public class CommerceChannelDisplayContext
	extends BaseCommerceChannelDisplayContext {

	public CommerceChannelDisplayContext(
		AccountEntryService accountEntryService,
		CommerceChannelHealthStatusRegistry commerceChannelHealthStatusRegistry,
		ModelResourcePermission<CommerceChannel>
			commerceChannelModelResourcePermission,
		CommerceChannelService commerceChannelService,
		CommerceChannelTypeRegistry commerceChannelTypeRegistry,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		ConfigurationProvider configurationProvider,
		CPTaxCategoryLocalService cpTaxCategoryLocalService,
		DLAppLocalService dlAppLocalService,
		HttpServletRequest httpServletRequest, ItemSelector itemSelector,
		Portal portal,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService,
		WorkflowDefinitionManager workflowDefinitionManager) {

		super(httpServletRequest);

		_accountEntryService = accountEntryService;
		_commerceChannelHealthStatusRegistry =
			commerceChannelHealthStatusRegistry;
		_commerceChannelModelResourcePermission =
			commerceChannelModelResourcePermission;
		_commerceChannelService = commerceChannelService;
		_commerceChannelTypeRegistry = commerceChannelTypeRegistry;
		_commerceCurrencyLocalService = commerceCurrencyLocalService;
		_configurationProvider = configurationProvider;
		_cpTaxCategoryLocalService = cpTaxCategoryLocalService;
		_dlAppLocalService = dlAppLocalService;
		_itemSelector = itemSelector;
		_portal = portal;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;
		_workflowDefinitionManager = workflowDefinitionManager;

		_commerceChannelRequestHelper = new CommerceChannelRequestHelper(
			httpServletRequest);
	}

	public FileEntry fetchFileEntry() throws PortalException {
		return _dlAppLocalService.fetchFileEntryByExternalReferenceCode(
			getCommerceChannel().getGroupId(), "ORDER_PRINT_TEMPLATE");
	}

	public int getAccountCartMaxAllowed() throws PortalException {
		CommerceOrderFieldsConfiguration commerceOrderFieldsConfiguration =
			_getCommerceOrderFieldsConfiguration();

		int maxAllowed =
			commerceOrderFieldsConfiguration.accountCartMaxAllowed();

		if (maxAllowed < 0) {
			return 0;
		}

		return maxAllowed;
	}

	public CPTaxCategory getActiveShippingTaxCategory() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceShippingTaxConfiguration commerceShippingTaxConfiguration =
			_configurationProvider.getConfiguration(
				CommerceShippingTaxConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_TAX));

		return _cpTaxCategoryLocalService.fetchCPTaxCategory(
			commerceShippingTaxConfiguration.taxCategoryId());
	}

	public List<WorkflowDefinition> getActiveWorkflowDefinitions()
		throws PortalException {

		return _workflowDefinitionManager.liberalGetActiveWorkflowDefinitions(
			_commerceChannelRequestHelper.getCompanyId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public String getAddChannelURL() throws Exception {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CPPortletKeys.COMMERCE_CHANNELS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_channels/add_commerce_channel"
		).setRedirect(
			getPortletURL()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getAddPaymentMethodURL(String commercePaymentMethodEngineKey)
		throws Exception {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest,
				CommercePaymentMethodGroupRel.class.getName(),
				PortletProvider.Action.EDIT)
		).setMVCRenderCommandName(
			"/commerce_payment_methods/edit_commerce_payment_method_group_rel"
		).setParameter(
			"commerceChannelId", getCommerceChannelId()
		).setParameter(
			"commercePaymentMethodEngineKey", commercePaymentMethodEngineKey
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getChannelURL(CommerceChannel commerceChannel)
		throws PortalException {

		if (commerceChannel == null) {
			return StringPool.BLANK;
		}

		PortletURL portletURL = _portal.getControlPanelPortletURL(
			httpServletRequest, CPPortletKeys.COMMERCE_CHANNELS,
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("backURL", portletURL.toString());
		portletURL.setParameter(
			"commerceChannelId",
			String.valueOf(commerceChannel.getCommerceChannelId()));
		portletURL.setParameter(
			"mvcRenderCommandName", "/commerce_channels/edit_commerce_channel");

		return portletURL.toString();
	}

	public CommerceChannel getCommerceChannel() throws PortalException {
		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		if (commerceChannelId == 0) {
			return null;
		}

		return _commerceChannelService.fetchCommerceChannel(commerceChannelId);
	}

	public long getCommerceChannelId() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		if (commerceChannel == null) {
			return 0;
		}

		return commerceChannel.getCommerceChannelId();
	}

	public List<CommerceChannelType> getCommerceChannelTypes() {
		return _commerceChannelTypeRegistry.getCommerceChannelTypes();
	}

	public List<CommerceCurrency> getCommerceCurrencies()
		throws PortalException {

		return _commerceCurrencyLocalService.getCommerceCurrencies(
			cpRequestHelper.getCompanyId(), true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public int getCommerceSiteType() throws PortalException {
		CommerceAccountGroupServiceConfiguration
			commerceAccountGroupServiceConfiguration =
				_getCommerceAccountGroupServiceConfiguration();

		return commerceAccountGroupServiceConfiguration.commerceSiteType();
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasAddChannelPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddChannelURL());
					dropdownItem.setLabel(
						LanguageUtil.get(httpServletRequest, "add-channel"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	public PortletURL getEditCommerceChannelRenderURL() {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				cpRequestHelper.getRequest(), CPPortletKeys.COMMERCE_CHANNELS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_channels/edit_commerce_channel"
		).buildPortletURL();
	}

	public List<HeaderActionModel> getHeaderActionModels() {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		LiferayPortletResponse liferayPortletResponse =
			_commerceChannelRequestHelper.getLiferayPortletResponse();

		RenderURL renderURL = liferayPortletResponse.createRenderURL();

		headerActionModels.add(
			new HeaderActionModel(null, renderURL.toString(), null, "cancel"));

		headerActionModels.add(
			new HeaderActionModel(
				"btn-primary", liferayPortletResponse.getNamespace() + "fm",
				null, null, "save"));

		return headerActionModels;
	}

	public String getImageItemSelectorURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		FileItemSelectorCriterion fileItemSelectorCriterion =
			new FileItemSelectorCriterion();

		fileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new FileEntryItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "addFileEntry",
				fileItemSelectorCriterion));
	}

	public String getOrderImporterDateFormat() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderImporterDateFormatConfiguration
			commerceOrderImporterDateFormatConfiguration =
				_configurationProvider.getConfiguration(
					CommerceOrderImporterDateFormatConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceChannel.getGroupId(),
						CommerceConstants.
							SERVICE_NAME_COMMERCE_ORDER_IMPORTER_DATE_FORMAT));

		return commerceOrderImporterDateFormatConfiguration.
			orderImporterDateFormat();
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		PortletURL portletURL = super.getPortletURL();

		String filterFields = ParamUtil.getString(
			httpServletRequest, "filterFields");

		if (Validator.isNotNull(filterFields)) {
			portletURL.setParameter("filterFields", filterFields);
		}

		String filtersLabels = ParamUtil.getString(
			httpServletRequest, "filtersLabels");

		if (Validator.isNotNull(filtersLabels)) {
			portletURL.setParameter("filtersLabels", filtersLabels);
		}

		String filtersValues = ParamUtil.getString(
			httpServletRequest, "filtersValues");

		if (Validator.isNotNull(filtersValues)) {
			portletURL.setParameter("filtersValues", filtersValues);
		}

		return portletURL;
	}

	public List<AccountEntry> getSupplierAccountEntries()
		throws PortalException {

		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			_accountEntryService.searchAccountEntries(
				null,
				LinkedHashMapBuilder.<String, Object>put(
					"status", WorkflowConstants.STATUS_APPROVED
				).put(
					"types",
					new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER}
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, "name", false);

		return baseModelSearchResult.getBaseModels();
	}

	public List<CPTaxCategory> getTaxCategories() {
		return _cpTaxCategoryLocalService.getCPTaxCategories(
			_commerceChannelRequestHelper.getCompanyId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public WorkflowDefinitionLink getWorkflowDefinitionLink(long typePK)
		throws PortalException {

		CommerceChannel commerceChannel = getCommerceChannel();

		return _workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
			_commerceChannelRequestHelper.getCompanyId(),
			commerceChannel.getGroupId(), CommerceOrder.class.getName(), 0,
			typePK, true);
	}

	public boolean hasAddChannelPermission() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletResourcePermission portletResourcePermission =
			_commerceChannelModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CPActionKeys.ADD_COMMERCE_CHANNEL);
	}

	public boolean hasAddLayoutPermission() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		return GroupPermissionUtil.contains(
			PermissionThreadLocal.getPermissionChecker(),
			commerceChannel.getSiteGroupId(), ActionKeys.ADD_LAYOUT);
	}

	public boolean hasAddPaymentMethodsPermission() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (PortletPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				MarketplacePortletKeys.PAYMENT_METHODS,
				MarketplaceActionKeys.PURCHASE_AND_INSTALL_PAID_APPS) ||
			PortletPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				MarketplacePortletKeys.PAYMENT_METHODS,
				MarketplaceActionKeys.INSTALL_FREE_BUNDLED_APPS)) {

			return true;
		}

		return PortletPermissionUtil.contains(
			themeDisplay.getPermissionChecker(),
			MarketplacePortletKeys.PAYMENT_METHODS,
			MarketplaceActionKeys.VIEW_APPS);
	}

	public boolean hasManageLinkSupplierPermission() {
		PortletResourcePermission portletResourcePermission =
			_commerceChannelModelResourcePermission.
				getPortletResourcePermission();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CPActionKeys.VIEW_COMMERCE_CHANNELS);
	}

	public boolean hasPermission(long commerceChannelId, String actionId)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _commerceChannelModelResourcePermission.contains(
			themeDisplay.getPermissionChecker(), commerceChannelId, actionId);
	}

	public boolean hasUnsatisfiedCommerceHealthChecks() throws PortalException {
		List<CommerceChannelHealthStatus> commerceChannelHealthStatuses =
			_commerceChannelHealthStatusRegistry.
				getCommerceChannelHealthStatuses();

		for (CommerceChannelHealthStatus commerceChannelHealthStatus :
				commerceChannelHealthStatuses) {

			if (!commerceChannelHealthStatus.isFixed(
					_commerceChannelRequestHelper.getCompanyId(),
					getCommerceChannelId())) {

				return true;
			}
		}

		return false;
	}

	public boolean isCheckoutRequestedDeliveryDateEnabled()
		throws PortalException {

		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.
			checkoutRequestedDeliveryDateEnabled();
	}

	public boolean isGuestCheckoutEnabled() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.guestCheckoutEnabled();
	}

	public boolean isHideShippingPriceZero() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.hideShippingPriceZero();
	}

	public boolean isMultishippingEnabled() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.multishippingEnabled();
	}

	public boolean isOrderSelectionDisabled() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderConfiguration commerceOrderConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderConfiguration.orderSelectionDisabled();
	}

	public boolean isQuickCheckoutEnabled() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.quickCheckoutEnabled();
	}

	public boolean isRequestQuoteEnabled() throws PortalException {
		CommerceOrderFieldsConfiguration commerceOrderFieldsConfiguration =
			_getCommerceOrderFieldsConfiguration();

		return commerceOrderFieldsConfiguration.requestQuoteEnabled();
	}

	public boolean isShowPurchaseOrderNumber() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderFieldsConfiguration commerceOrderFieldsConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderFieldsConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderFieldsConfiguration.showPurchaseOrderNumber();
	}

	public boolean isShowSeparateOrderItems() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.showSeparateOrderItems();
	}

	public boolean isSlowConnectionOrderFlowEnabled() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderConfiguration commerceOrderConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderConfiguration.slowConnectionOrderFlowEnabled();
	}

	public boolean isUndoCartItemDeletionDisabled() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		CommerceOrderConfiguration commerceOrderConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderConfiguration.undoCartItemDeletionDisabled();
	}

	private CommerceAccountGroupServiceConfiguration
			_getCommerceAccountGroupServiceConfiguration()
		throws PortalException {

		if (_commerceAccountGroupServiceConfiguration != null) {
			return _commerceAccountGroupServiceConfiguration;
		}

		CommerceChannel commerceChannel = getCommerceChannel();

		_commerceAccountGroupServiceConfiguration =
			_configurationProvider.getConfiguration(
				CommerceAccountGroupServiceConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		return _commerceAccountGroupServiceConfiguration;
	}

	private CommerceOrderFieldsConfiguration
			_getCommerceOrderFieldsConfiguration()
		throws PortalException {

		if (_commerceOrderFieldsConfiguration != null) {
			return _commerceOrderFieldsConfiguration;
		}

		CommerceChannel commerceChannel = getCommerceChannel();

		_commerceOrderFieldsConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderFieldsConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

		return _commerceOrderFieldsConfiguration;
	}

	private final AccountEntryService _accountEntryService;
	private CommerceAccountGroupServiceConfiguration
		_commerceAccountGroupServiceConfiguration;
	private final CommerceChannelHealthStatusRegistry
		_commerceChannelHealthStatusRegistry;
	private final ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;
	private final CommerceChannelRequestHelper _commerceChannelRequestHelper;
	private final CommerceChannelService _commerceChannelService;
	private final CommerceChannelTypeRegistry _commerceChannelTypeRegistry;
	private final CommerceCurrencyLocalService _commerceCurrencyLocalService;
	private CommerceOrderFieldsConfiguration _commerceOrderFieldsConfiguration;
	private final ConfigurationProvider _configurationProvider;
	private final CPTaxCategoryLocalService _cpTaxCategoryLocalService;
	private final DLAppLocalService _dlAppLocalService;
	private final ItemSelector _itemSelector;
	private final Portal _portal;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;
	private final WorkflowDefinitionManager _workflowDefinitionManager;

}