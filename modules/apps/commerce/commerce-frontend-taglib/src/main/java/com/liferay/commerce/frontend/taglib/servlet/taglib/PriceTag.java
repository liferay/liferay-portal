/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.configuration.CommercePriceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.frontend.helper.ProductHelper;
import com.liferay.commerce.frontend.model.PriceModel;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.math.BigDecimal;

import java.util.List;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 * @author Alec Sloan
 */
public class PriceTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		CommerceContext commerceContext =
			(CommerceContext)getRequest().getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		try {
			if ((commerceContext == null) ||
				(commerceContext.getCommerceChannelId() == 0)) {

				return SKIP_BODY;
			}

			long cpInstanceId = 0;

			if (_showDefaultSkuPrice) {
				CPInstance defaultCPInstance =
					_cpContentHelper.getDefaultCPInstance(_cpCatalogEntry);

				if (defaultCPInstance != null) {
					cpInstanceId = defaultCPInstance.getCPInstanceId();
				}
			}
			else {
				List<CPSku> cpSkus = _cpCatalogEntry.getCPSkus();

				if (cpSkus.size() == 1) {
					CPSku cpSku = cpSkus.get(0);

					cpInstanceId = cpSku.getCPInstanceId();
				}
			}

			if (BigDecimalUtil.lte(_quantity, BigDecimal.ZERO)) {
				_quantity = BigDecimal.ONE;
			}

			_displayDiscountLevels = _isDisplayDiscountLevels();
			_netPrice = _isNetPrice(commerceContext.getCommerceChannelId());
			_priceModel = _getPriceModel(commerceContext, cpInstanceId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public CPCatalogEntry getCPCatalogEntry() {
		return _cpCatalogEntry;
	}

	public String getNamespace() {
		return _namespace;
	}

	public BigDecimal getQuantity() {
		return _quantity;
	}

	public String getUnitOfMeasureKey() {
		return _unitOfMeasureKey;
	}

	public boolean isCompact() {
		return _compact;
	}

	public boolean isShowDefaultSkuPrice() {
		return _showDefaultSkuPrice;
	}

	public void setCompact(boolean compact) {
		_compact = compact;
	}

	public void setCPCatalogEntry(CPCatalogEntry cpCatalogEntry) {
		_cpCatalogEntry = cpCatalogEntry;
	}

	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());

		commerceChannelLocalService =
			ServletContextUtil.getCommerceChannelLocalService();
		configurationProvider = ServletContextUtil.getConfigurationProvider();
		_cpContentHelper = ServletContextUtil.getCPContentHelper();
		_cpDefinitionOptionRelLocalService =
			ServletContextUtil.getCPDefinitionOptionRelLocalService();
		_cpInstanceUnitOfMeasureLocalService =
			ServletContextUtil.getCPInstanceUnitOfMeasureLocalService();
		_productHelper = ServletContextUtil.getProductHelper();
	}

	public void setQuantity(BigDecimal quantity) {
		_quantity = quantity;
	}

	public void setShowDefaultSkuPrice(boolean showDefaultSkuPrice) {
		_showDefaultSkuPrice = showDefaultSkuPrice;
	}

	public void setUnitOfMeasureKey(String unitOfMeasureKey) {
		_unitOfMeasureKey = unitOfMeasureKey;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_compact = false;
		_cpCatalogEntry = null;
		_cpContentHelper = null;
		_cpDefinitionOptionRelLocalService = null;
		_cpInstanceUnitOfMeasureLocalService = null;
		_displayDiscountLevels = false;
		_namespace = StringPool.BLANK;
		_netPrice = true;
		_priceModel = null;
		_productHelper = null;
		_quantity = BigDecimal.ZERO;
		_showDefaultSkuPrice = false;
		_unitOfMeasureKey = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute("commerce-ui:price:compact", _compact);
		httpServletRequest.setAttribute(
			"commerce-ui:price:displayDiscountLevels", _displayDiscountLevels);
		httpServletRequest.setAttribute(
			"commerce-ui:price:namespace", _namespace);
		httpServletRequest.setAttribute(
			"commerce-ui:price:netPrice", _netPrice);
		httpServletRequest.setAttribute(
			"commerce-ui:price:priceModel", _priceModel);
	}

	protected CommerceChannelLocalService commerceChannelLocalService;
	protected ConfigurationProvider configurationProvider;

	private PriceModel _getPriceModel(
			CommerceContext commerceContext, long cpInstanceId)
		throws PortalException {

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (cpInstanceId > 0) {
			List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
				_cpInstanceUnitOfMeasureLocalService.
					getActiveCPInstanceUnitOfMeasures(cpInstanceId);

			if (cpInstanceUnitOfMeasures.size() > 1) {
				return _productHelper.getMinPriceModel(
					_cpCatalogEntry.getCPDefinitionId(), commerceContext,
					themeDisplay.getLocale());
			}

			if (cpInstanceUnitOfMeasures.size() == 1) {
				CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
					cpInstanceUnitOfMeasures.get(0);

				_quantity =
					cpInstanceUnitOfMeasure.getIncrementalOrderQuantity();
				_unitOfMeasureKey = cpInstanceUnitOfMeasure.getKey();
			}

			JSONArray jsonArray = CPJSONUtil.toJSONArray(
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
						cpInstanceId));

			return _productHelper.getPriceModel(
				cpInstanceId, jsonArray.toString(), _quantity,
				_unitOfMeasureKey, commerceContext, themeDisplay.getLocale());
		}

		return _productHelper.getMinPriceModel(
			_cpCatalogEntry.getCPDefinitionId(), commerceContext,
			themeDisplay.getLocale());
	}

	private boolean _isDisplayDiscountLevels() throws ConfigurationException {
		CommercePriceConfiguration commercePriceConfiguration =
			configurationProvider.getConfiguration(
				CommercePriceConfiguration.class,
				new SystemSettingsLocator(
					CommerceConstants.SERVICE_NAME_COMMERCE_PRICE));

		return commercePriceConfiguration.displayDiscountLevels();
	}

	private boolean _isNetPrice(long commerceChannelId) {
		CommerceChannel commerceChannel =
			commerceChannelLocalService.fetchCommerceChannel(commerceChannelId);

		if ((commerceChannel != null) &&
			Objects.equals(
				commerceChannel.getPriceDisplayType(),
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			return false;
		}

		return true;
	}

	private static final String _PAGE = "/price/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(PriceTag.class);

	private boolean _compact;
	private CPCatalogEntry _cpCatalogEntry;
	private CPContentHelper _cpContentHelper;
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;
	private boolean _displayDiscountLevels;
	private String _namespace = StringPool.BLANK;
	private boolean _netPrice = true;
	private PriceModel _priceModel;
	private ProductHelper _productHelper;
	private BigDecimal _quantity = BigDecimal.ZERO;
	private boolean _showDefaultSkuPrice;
	private String _unitOfMeasureKey = StringPool.BLANK;

}