/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.frontend.helper.ProductHelper;
import com.liferay.commerce.frontend.model.ProductSettingsModel;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.math.BigDecimal;

import java.util.List;

/**
 * @author Fabio Diego Mastrorilli
 * @author Gianmarco Brunialti Masera
 * @author Ivica Cardic
 */
public class AddToCartTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			if ((commerceContext == null) ||
				(commerceContext.getCommerceChannelId() == 0)) {

				return SKIP_BODY;
			}

			_commerceAccountId = CommerceUtil.getCommerceAccountId(
				commerceContext);

			_commerceChannelGroupId =
				commerceContext.getCommerceChannelGroupId();
			_commerceChannelId = commerceContext.getCommerceChannelId();

			CommerceCurrency commerceCurrency =
				commerceContext.getCommerceCurrency();

			_commerceCurrencyCode = commerceCurrency.getCode();

			CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

			if (commerceOrder != null) {
				_commerceOrderId = commerceOrder.getCommerceOrderId();
			}

			CPSku cpSku = null;

			if (_cpCatalogEntry != null) {
				cpSku = _cpContentHelper.getDefaultCPSku(_cpCatalogEntry);

				_productId = _cpCatalogEntry.getCProductId();

				_productSettingsModel = _productHelper.getProductSettingsModel(
					_cpCatalogEntry.getCPDefinitionId(), commerceContext);

				BigDecimal multipleQuantity =
					_productSettingsModel.getMultipleQuantity();

				BigDecimal[] allowedQuantities = ArrayUtil.filter(
					_productSettingsModel.getAllowedQuantities(),
					quantity ->
						BigDecimalUtil.gte(
							quantity, _productSettingsModel.getMinQuantity()) &&
						BigDecimalUtil.lte(
							quantity, _productSettingsModel.getMaxQuantity()) &&
						BigDecimalUtil.eq(
							quantity.remainder(multipleQuantity),
							BigDecimal.ZERO));

				_productSettingsModel.setAllowedQuantities(allowedQuantities);
			}

			String sku = null;

			if (cpSku != null) {
				_cpInstanceId = cpSku.getCPInstanceId();
				_published = cpSku.isPublished();
				_purchasable = cpSku.isPurchasable();
				sku = cpSku.getSku();

				if (commerceOrder != null) {
					List<CommerceOrderItem> commerceOrderItems =
						_commerceOrderItemLocalService.getCommerceOrderItems(
							commerceOrder.getCommerceOrderId(),
							cpSku.getCPInstanceId(), 0, 1);

					if (!commerceOrderItems.isEmpty()) {
						_inCart = true;
					}
				}

				List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
					_cpInstanceUnitOfMeasureLocalService.
						getActiveCPInstanceUnitOfMeasures(_cpInstanceId);

				if (!cpInstanceUnitOfMeasures.isEmpty()) {
					_cpInstanceUnitOfMeasure = cpInstanceUnitOfMeasures.get(0);
				}
			}

			if (sku != null) {
				BigDecimal stockQuantity =
					_commerceInventoryEngine.getStockQuantity(
						PortalUtil.getCompanyId(httpServletRequest),
						_commerceAccountId, _cpCatalogEntry.getGroupId(),
						commerceContext.getCommerceChannelGroupId(), sku,
						StringPool.BLANK);

				_stockQuantity = stockQuantity.intValue();

				if (Validator.isNull(_skuOptions) || _skuOptions.equals("[]")) {
					JSONArray jsonArray = CPJSONUtil.toJSONArray(
						_cpDefinitionOptionRelLocalService.
							getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
								cpSku.getCPInstanceId()));

					_skuOptions = jsonArray.toString();
				}
			}
			else {
				_disabled = true;
			}

			AccountEntry accountEntry = commerceContext.getAccountEntry();

			if (accountEntry != null) {
				if (accountEntry.isBusinessAccount()) {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)httpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					if (_disabled) {
						_disabled &=
							!_commerceOrderPortletResourcePermission.contains(
								themeDisplay.getPermissionChecker(),
								accountEntry.getAccountEntryGroupId(),
								CommerceOrderActionKeys.ADD_COMMERCE_ORDER);
					}
					else {
						_disabled =
							!_commerceOrderPortletResourcePermission.contains(
								themeDisplay.getPermissionChecker(),
								accountEntry.getAccountEntryGroupId(),
								CommerceOrderActionKeys.ADD_COMMERCE_ORDER);
					}
				}
				else {
					CommerceChannel commerceChannel =
						_commerceChannelLocalService.getCommerceChannel(
							_commerceChannelId);

					CommerceOrderCheckoutConfiguration
						commerceOrderCheckoutConfiguration =
							_configurationProvider.getConfiguration(
								CommerceOrderCheckoutConfiguration.class,
								new GroupServiceSettingsLocator(
									commerceChannel.getGroupId(),
									CommerceConstants.
										SERVICE_NAME_COMMERCE_ORDER));

					if (_disabled) {
						_disabled &=
							accountEntry.isGuestAccount() &&
							(CommerceChannelConstants.SITE_TYPE_B2B ==
								commerceContext.getCommerceSiteType()) &&
							!commerceOrderCheckoutConfiguration.
								guestCheckoutEnabled();
					}
					else {
						_disabled =
							accountEntry.isGuestAccount() &&
							(CommerceChannelConstants.SITE_TYPE_B2B ==
								commerceContext.getCommerceSiteType()) &&
							!commerceOrderCheckoutConfiguration.
								guestCheckoutEnabled();
					}
				}
			}

			int commerceOrderTypesCount =
				_commerceOrderTypeLocalService.getCommerceOrderTypesCount(
					PortalUtil.getCompanyId(httpServletRequest),
					CommerceChannel.class.getName(), _commerceChannelId, true);

			_showOrderTypeModal = commerceOrderTypesCount > 1;

			_showOrderTypeModalURL = _getShowOrderTypeModalURL(
				httpServletRequest);
		}
		catch (Exception exception) {
			_log.error(exception);

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public String getAlignment() {
		return _alignment;
	}

	public CPCatalogEntry getCPCatalogEntry() {
		return _cpCatalogEntry;
	}

	public long getCPInstanceId() {
		return _cpInstanceId;
	}

	public boolean getIconOnly() {
		return _iconOnly;
	}

	public boolean getInline() {
		return _inline;
	}

	public String getNamespace() {
		return _namespace;
	}

	public boolean getPublished() {
		return _published;
	}

	public boolean getPurchasable() {
		return _purchasable;
	}

	public BigDecimal getQuantity() {
		return _quantity;
	}

	public boolean getShowUnitOfMeasureSelector() {
		return _showUnitOfMeasureSelector;
	}

	public String getSize() {
		return _size;
	}

	public String getSkuOptions() {
		return _skuOptions;
	}

	public void setAlignment(String alignment) {
		_alignment = alignment;
	}

	@Override
	public void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:alignment", _alignment);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:commerceAccountId",
			_commerceAccountId);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:commerceChannelGroupId",
			_commerceChannelGroupId);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:commerceChannelId",
			_commerceChannelId);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:commerceCurrencyCode",
			_commerceCurrencyCode);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:commerceOrderId", _commerceOrderId);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:cpInstanceId", _cpInstanceId);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:cpInstanceUnitOfMeasure",
			_cpInstanceUnitOfMeasure);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:disabled", _disabled);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:iconOnly", _iconOnly);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:inCart", _inCart);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:inline", _inline);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:namespace", _namespace);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:productId", _productId);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:productSettingsModel",
			_productSettingsModel);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:published", _published);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:purchasable", _purchasable);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:showOrderTypeModal",
			_showOrderTypeModal);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:showOrderTypeModalURL",
			_showOrderTypeModalURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:showUnitOfMeasureSelector",
			_showUnitOfMeasureSelector);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:size", _size);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:skuOptions", _skuOptions);
		httpServletRequest.setAttribute(
			"liferay-commerce:add-to-cart:stockQuantity", _stockQuantity);
	}

	public void setCPCatalogEntry(CPCatalogEntry cpCatalogEntry) {
		_cpCatalogEntry = cpCatalogEntry;
	}

	public void setCPInstanceId(long cpInstanceId) {
		_cpInstanceId = cpInstanceId;
	}

	public void setIconOnly(boolean iconOnly) {
		_iconOnly = iconOnly;
	}

	public void setInline(boolean inline) {
		_inline = inline;
	}

	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());

		_commerceChannelLocalService =
			ServletContextUtil.getCommerceChannelLocalService();
		_commerceInventoryEngine =
			ServletContextUtil.getCommerceInventoryEngine();
		_commerceOrderHttpHelper =
			ServletContextUtil.getCommerceOrderHttpHelper();
		_commerceOrderItemLocalService =
			ServletContextUtil.getCommerceOrderItemLocalService();
		_commerceOrderPortletResourcePermission =
			ServletContextUtil.getCommerceOrderPortletResourcePermission();
		_commerceOrderTypeLocalService =
			ServletContextUtil.getCommerceOrderTypeLocalService();
		_configurationProvider = ServletContextUtil.getConfigurationProvider();
		_cpContentHelper = ServletContextUtil.getCPContentHelper();
		_cpDefinitionOptionRelLocalService =
			ServletContextUtil.getCPDefinitionOptionRelLocalService();
		_cpInstanceUnitOfMeasureLocalService =
			ServletContextUtil.getCPInstanceUnitOfMeasureLocalService();
		_productHelper = ServletContextUtil.getProductHelper();
	}

	public void setPublished(boolean published) {
		_published = published;
	}

	public void setPurchasable(boolean purchasable) {
		_purchasable = purchasable;
	}

	public void setQuantity(BigDecimal quantity) {
		_quantity = quantity;
	}

	public void setShowUnitOfMeasureSelector(
		boolean showUnitOfMeasureSelector) {

		_showUnitOfMeasureSelector = showUnitOfMeasureSelector;
	}

	public void setSize(String size) {
		_size = size;
	}

	public void setSkuOptions(String skuOptions) {
		_skuOptions = skuOptions;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_alignment = "center";
		_commerceAccountId = 0;
		_commerceChannelGroupId = 0;
		_commerceChannelId = 0;
		_commerceChannelLocalService = null;
		_commerceCurrencyCode = null;
		_commerceInventoryEngine = null;
		_commerceOrderHttpHelper = null;
		_commerceOrderId = 0;
		_commerceOrderItemLocalService = null;
		_commerceOrderPortletResourcePermission = null;
		_commerceOrderTypeLocalService = null;
		_configurationProvider = null;
		_cpCatalogEntry = null;
		_cpContentHelper = null;
		_cpDefinitionOptionRelLocalService = null;
		_cpInstanceId = 0;
		_cpInstanceUnitOfMeasure = null;
		_cpInstanceUnitOfMeasureLocalService = null;
		_disabled = false;
		_iconOnly = false;
		_inCart = false;
		_inline = false;
		_namespace = StringPool.BLANK;
		_productHelper = null;
		_productId = 0;
		_productSettingsModel = null;
		_published = false;
		_purchasable = false;
		_quantity = BigDecimal.ZERO;
		_showOrderTypeModal = false;
		_showOrderTypeModalURL = null;
		_showUnitOfMeasureSelector = false;
		_size = "md";
		_skuOptions = null;
		_stockQuantity = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	private String _getShowOrderTypeModalURL(
		HttpServletRequest httpServletRequest) {

		if (!_showOrderTypeModal) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest,
				CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_order_content/view_commerce_order_order_type_modal"
		).setParameter(
			"addToCart", Boolean.TRUE
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private static final String _PAGE = "/add_to_cart/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(AddToCartTag.class);

	private String _alignment = "center";
	private long _commerceAccountId;
	private long _commerceChannelGroupId;
	private long _commerceChannelId;
	private CommerceChannelLocalService _commerceChannelLocalService;
	private String _commerceCurrencyCode;
	private CommerceInventoryEngine _commerceInventoryEngine;
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;
	private long _commerceOrderId;
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;
	private PortletResourcePermission _commerceOrderPortletResourcePermission;
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;
	private ConfigurationProvider _configurationProvider;
	private CPCatalogEntry _cpCatalogEntry;
	private CPContentHelper _cpContentHelper;
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;
	private long _cpInstanceId;
	private CPInstanceUnitOfMeasure _cpInstanceUnitOfMeasure;
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;
	private boolean _disabled;
	private boolean _iconOnly;
	private boolean _inCart;
	private boolean _inline;
	private String _namespace = StringPool.BLANK;
	private ProductHelper _productHelper;
	private long _productId;
	private ProductSettingsModel _productSettingsModel;
	private boolean _published;
	private boolean _purchasable;
	private BigDecimal _quantity = BigDecimal.ZERO;
	private boolean _showOrderTypeModal;
	private String _showOrderTypeModalURL;
	private boolean _showUnitOfMeasureSelector;
	private String _size = "md";
	private String _skuOptions;
	private int _stockQuantity;

}