/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderFieldsConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.frontend.helper.ProductHelper;
import com.liferay.commerce.frontend.model.PriceModel;
import com.liferay.commerce.frontend.model.ProductSettingsModel;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalServiceUtil;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Stefano Motta
 */
public class RequestQuoteTag extends IncludeTag {

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

			_commerceChannelId = commerceContext.getCommerceChannelId();
			_commerceAccountId = CommerceUtil.getCommerceAccountId(
				commerceContext);

			CommerceCurrency commerceCurrency =
				commerceContext.getCommerceCurrency();

			_commerceCurrencyCode = commerceCurrency.getCode();

			CPSku cpSku = null;

			if (_cpCatalogEntry != null) {
				cpSku = _cpContentHelper.getDefaultCPSku(_cpCatalogEntry);
			}

			if (cpSku != null) {
				_cpInstanceId = cpSku.getCPInstanceId();
				_disabled = !cpSku.isPurchasable() || (_commerceAccountId == 0);

				PriceModel priceModel = _getPriceModel(
					commerceContext, _cpInstanceId);

				_priceOnApplication = priceModel.isPriceOnApplication();

				JSONArray jsonArray = CPJSONUtil.toJSONArray(
					_cpDefinitionOptionRelLocalService.
						getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
							_cpInstanceId));

				_skuOptions = jsonArray.toString();
			}
			else {
				int cpDefinitionInstancesCount =
					CPInstanceLocalServiceUtil.getCPDefinitionInstancesCount(
						_cpCatalogEntry.getCPDefinitionId(),
						WorkflowConstants.STATUS_APPROVED);

				_priceOnApplication = cpDefinitionInstancesCount > 0;
			}

			CommerceOrderFieldsConfiguration commerceOrderFieldsConfiguration =
				_configurationProvider.getConfiguration(
					CommerceOrderFieldsConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceContext.getCommerceChannelGroupId(),
						CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

			_requestQuoteEnabled =
				commerceOrderFieldsConfiguration.requestQuoteEnabled();

			if (!_priceOnApplication && !_requestQuoteEnabled) {
				return SKIP_BODY;
			}

			AccountEntry accountEntry = commerceContext.getAccountEntry();

			if (accountEntry != null) {
				if (accountEntry.isBusinessAccount()) {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)httpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					_disabled =
						_disabled ||
						!_commerceOrderPortletResourcePermission.contains(
							themeDisplay.getPermissionChecker(),
							accountEntry.getAccountEntryGroupId(),
							CommerceOrderActionKeys.ADD_COMMERCE_ORDER);
				}
				else {
					_disabled =
						_disabled ||
						(accountEntry.isGuestAccount() &&
						 (CommerceChannelConstants.SITE_TYPE_B2B ==
							 commerceContext.getCommerceSiteType()));
				}
			}

			_orderDetailURL = _getOrderDetailURL(
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY));
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

	@Override
	public void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:commerceAccountId",
			_commerceAccountId);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:commerceChannelId",
			_commerceChannelId);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:commerceCurrencyCode",
			_commerceCurrencyCode);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:cpDefinitionId",
			_cpCatalogEntry.getCPDefinitionId());
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:cpInstanceId", _cpInstanceId);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:disabled", _disabled);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:namespace", _namespace);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:orderDetailURL", _orderDetailURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:priceOnApplication",
			_priceOnApplication);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:requestQuoteEnabled",
			_requestQuoteEnabled);
		httpServletRequest.setAttribute(
			"liferay-commerce:request-quote:skuOptions", _skuOptions);
	}

	public void setCpCatalogEntry(CPCatalogEntry cpCatalogEntry) {
		_cpCatalogEntry = cpCatalogEntry;
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

		_commerceOrderPortletResourcePermission =
			ServletContextUtil.getCommerceOrderPortletResourcePermission();
		_configurationProvider = ServletContextUtil.getConfigurationProvider();
		_cpContentHelper = ServletContextUtil.getCPContentHelper();
		_cpDefinitionOptionRelLocalService =
			ServletContextUtil.getCPDefinitionOptionRelLocalService();
		_productHelper = ServletContextUtil.getProductHelper();
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_commerceAccountId = 0;
		_commerceChannelId = 0;
		_commerceCurrencyCode = null;
		_commerceOrderPortletResourcePermission = null;
		_configurationProvider = null;
		_cpCatalogEntry = null;
		_cpContentHelper = null;
		_cpDefinitionOptionRelLocalService = null;
		_cpInstanceId = 0;
		_disabled = false;
		_namespace = StringPool.BLANK;
		_orderDetailURL = null;
		_priceOnApplication = false;
		_productHelper = null;
		_requestQuoteEnabled = false;
		_skuOptions = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	private String _getOrderDetailURL(ThemeDisplay themeDisplay)
		throws PortalException {

		long plid = PortalUtil.getPlidFromPortletId(
			themeDisplay.getScopeGroupId(),
			CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

		if (plid > 0) {
			return PortletURLBuilder.create(
				_getPortletURL(
					themeDisplay.getRequest(),
					CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT)
			).setActionName(
				"/commerce_open_order_content/edit_commerce_order"
			).setCMD(
				"setCurrent"
			).setParameter(
				"commerceOrderId", "{id}"
			).buildString();
		}

		return StringPool.BLANK;
	}

	private PortletURL _getPortletURL(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		long groupId = PortalUtil.getScopeGroupId(httpServletRequest);

		long plid = PortalUtil.getPlidFromPortletId(groupId, portletId);

		if ((plid > 0) || FeatureFlagManagerUtil.isEnabled("LPD-20379")) {
			return PortletURLFactoryUtil.create(
				httpServletRequest, portletId, plid,
				PortletRequest.ACTION_PHASE);
		}

		return PortletURLFactoryUtil.create(
			httpServletRequest, portletId, PortletRequest.ACTION_PHASE);
	}

	private PriceModel _getPriceModel(
			CommerceContext commerceContext, long cpInstanceId)
		throws PortalException {

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ProductSettingsModel productSettingsModel =
			_productHelper.getProductSettingsModel(
				_cpCatalogEntry.getCPDefinitionId(), commerceContext);

		return _productHelper.getPriceModel(
			cpInstanceId, StringPool.BLANK,
			productSettingsModel.getMinQuantity(), StringPool.BLANK,
			commerceContext, themeDisplay.getLocale());
	}

	private static final String _PAGE = "/request_quote/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		RequestQuoteTag.class);

	private long _commerceAccountId;
	private long _commerceChannelId;
	private String _commerceCurrencyCode;
	private PortletResourcePermission _commerceOrderPortletResourcePermission;
	private ConfigurationProvider _configurationProvider;
	private CPCatalogEntry _cpCatalogEntry;
	private CPContentHelper _cpContentHelper;
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;
	private long _cpInstanceId;
	private boolean _disabled;
	private String _namespace = StringPool.BLANK;
	private String _orderDetailURL;
	private boolean _priceOnApplication;
	private ProductHelper _productHelper;
	private boolean _requestQuoteEnabled;
	private String _skuOptions;

}