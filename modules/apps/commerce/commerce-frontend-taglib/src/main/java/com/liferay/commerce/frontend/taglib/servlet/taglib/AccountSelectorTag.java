/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.configuration.CommerceOrderFieldsConfiguration;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.frontend.taglib.internal.model.CurrentCommerceAccountModel;
import com.liferay.commerce.frontend.taglib.internal.model.CurrentCommerceOrderModel;
import com.liferay.commerce.frontend.taglib.internal.model.WorkflowStatusModel;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Fabio Diego Mastrorilli
 * @author Gianmarco Brunialti Masera
 */
public class AccountSelectorTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			_commerceChannelGroupId = 0;
			_commerceChannelId = 0;

			if (commerceContext != null) {
				_commerceChannelGroupId =
					commerceContext.getCommerceChannelGroupId();
				_commerceChannelId = commerceContext.getCommerceChannelId();
			}

			if (_commerceChannelId == 0) {
				return super.doStartTag();
			}

			_accountEntry = commerceContext.getAccountEntry();
			_accountEntryAllowedTypes =
				commerceContext.getAccountEntryAllowedTypes();
			_addCommerceOrderURL =
				_commerceOrderHttpHelper.getCommerceCartBaseURL(
					httpServletRequest);
			_checkoutURL = _getCheckoutURL(httpServletRequest);
			_commerceOrder = commerceContext.getCommerceOrder();

			CommerceCurrency commerceCurrency =
				commerceContext.getCommerceCurrency();

			_currencyCode = commerceCurrency.getCode();

			_editOrderURL = _getEditOrderURL(httpServletRequest);
			_setCurrentAccountURL =
				PortalUtil.getPortalURL(httpServletRequest) +
					PortalUtil.getPathContext() +
						"/o/commerce-ui/set-current-account";

			_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			_spritemap = _themeDisplay.getPathThemeSpritemap();
		}
		catch (Exception exception) {
			_log.error(exception);

			_accountEntry = null;
			_accountEntryAllowedTypes = new String[0];
			_addCommerceOrderURL = StringPool.BLANK;
			_commerceChannelGroupId = 0;
			_commerceChannelId = 0;
			_commerceOrder = null;
			_commerceOrderPortletResourcePermission = null;
			_configurationProvider = null;
			_currencyCode = StringPool.BLANK;
			_editOrderURL = StringPool.BLANK;
			_setCurrentAccountURL = StringPool.BLANK;
			_spritemap = StringPool.BLANK;
			_themeDisplay = null;
		}

		return super.doStartTag();
	}

	public String getCssClasses() {
		return _cssClasses;
	}

	public String getSpritemap() {
		return _spritemap;
	}

	public void setCssClasses(String cssClasses) {
		_cssClasses = cssClasses;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());

		_commerceOrderHttpHelper =
			ServletContextUtil.getCommerceOrderHttpHelper();
		_commerceOrderLocalService =
			ServletContextUtil.getCommerceOrderLocalService();
		_commerceOrderPortletResourcePermission =
			ServletContextUtil.getCommerceOrderPortletResourcePermission();
		_commerceOrderTypeLocalService =
			ServletContextUtil.getCommerceOrderTypeLocalService();
		_configurationProvider = ServletContextUtil.getConfigurationProvider();
	}

	public void setSpritemap(String spritemap) {
		_spritemap = spritemap;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_accountEntry = null;
		_accountEntryAllowedTypes = null;
		_addCommerceOrderURL = StringPool.BLANK;
		_checkoutURL = StringPool.BLANK;
		_commerceChannelGroupId = 0;
		_commerceChannelId = 0;
		_commerceOrder = null;
		_commerceOrderHttpHelper = null;
		_commerceOrderLocalService = null;
		_commerceOrderPortletResourcePermission = null;
		_commerceOrderTypeLocalService = null;
		_configurationProvider = null;
		_cssClasses = StringPool.BLANK;
		_currencyCode = StringPool.BLANK;
		_editOrderURL = StringPool.BLANK;
		_setCurrentAccountURL = StringPool.BLANK;
		_spritemap = StringPool.BLANK;
		_themeDisplay = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:accountEntryAllowedTypes",
			_accountEntryAllowedTypes);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:commerceChannelId",
			_commerceChannelId);

		if (_accountEntry != null) {
			String thumbnailUrl = null;

			if (_accountEntry.getLogoId() == 0) {
				thumbnailUrl =
					_themeDisplay.getPathImage() +
						"/organization_logo?img_id=0";
			}
			else {
				thumbnailUrl = StringBundler.concat(
					_themeDisplay.getPathImage(), "/organization_logo?img_id=",
					_accountEntry.getLogoId(), "&t=",
					WebServerServletTokenUtil.getToken(
						_accountEntry.getLogoId()));
			}

			CurrentCommerceAccountModel currentCommerceAccountModel =
				new CurrentCommerceAccountModel(
					_accountEntry.getAccountEntryId(), thumbnailUrl,
					_accountEntry.getName());

			httpServletRequest.setAttribute(
				"liferay-commerce:account-selector:currentCommerceAccount",
				currentCommerceAccountModel);
		}

		if (_commerceOrder != null) {
			String workflowStatusInfoLabel = WorkflowConstants.getStatusLabel(
				_commerceOrder.getStatus());

			WorkflowStatusModel workflowStatusModel = new WorkflowStatusModel(
				_commerceOrder.getStatus(), workflowStatusInfoLabel,
				LanguageUtil.get(
					_themeDisplay.getLocale(), workflowStatusInfoLabel));

			CurrentCommerceOrderModel currentCommerceOrderModel =
				new CurrentCommerceOrderModel(
					_commerceOrder.getCommerceOrderId(), workflowStatusModel);

			httpServletRequest.setAttribute(
				"liferay-commerce:account-selector:currentCommerceOrder",
				currentCommerceOrderModel);
		}

		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:checkoutURL", _checkoutURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:createNewOrderURL",
			_addCommerceOrderURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:cssClasses", _cssClasses);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:currencyCode", _currencyCode);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:hasAddCommerceOrderPermission",
			_hasAddCommerceOrderPermission());
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:hasManageAccountsPermission",
			_hasManageAccountsPermission());
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:orderTypes",
			_getCommerceOrderTypesJSONArray(
				_commerceChannelId, httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:selectOrderURL", _editOrderURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:setCurrentAccountURL",
			_setCurrentAccountURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:spritemap", _spritemap);
	}

	private String _getCheckoutURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		boolean immediateCheckout = GetterUtil.getBoolean(
			httpSession.getAttribute(
				CommerceCheckoutWebKeys.SUFFIX_IMMEDIATE_CHECKOUT));

		if (!immediateCheckout) {
			return StringPool.BLANK;
		}

		httpSession.removeAttribute(
			CommerceCheckoutWebKeys.SUFFIX_IMMEDIATE_CHECKOUT);

		PortletURL commerceCheckoutPortletURL =
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommercePortletKeys.COMMERCE_CHECKOUT,
				PortletProvider.Action.VIEW);

		if (commerceCheckoutPortletURL == null) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			commerceCheckoutPortletURL
		).setMVCRenderCommandName(
			"/commerce_checkout/checkout_redirect"
		).buildString();
	}

	private JSONArray _getCommerceOrderTypesJSONArray(
		long commerceChannelId, HttpServletRequest httpServletRequest) {

		JSONArray commerceOrderTypesJSONArray =
			JSONFactoryUtil.createJSONArray();

		try {
			List<CommerceOrderType> commerceOrderTypes =
				_commerceOrderTypeLocalService.getCommerceOrderTypes(
					PortalUtil.getCompanyId(httpServletRequest),
					CommerceChannel.class.getName(), commerceChannelId, true,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (CommerceOrderType commerceOrderType : commerceOrderTypes) {
				commerceOrderTypesJSONArray.put(
					JSONUtil.put(
						"name_i18n",
						commerceOrderType.getName(
							PortalUtil.getLocale(httpServletRequest))
					).put(
						"orderTypeId",
						commerceOrderType.getCommerceOrderTypeId()
					));
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return commerceOrderTypesJSONArray;
	}

	private String _getEditOrderURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		long plid = PortalUtil.getPlidFromPortletId(
			PortalUtil.getScopeGroupId(httpServletRequest),
			CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

		if ((plid > 0) || FeatureFlagManagerUtil.isEnabled("LPD-20379")) {
			return PortletURLBuilder.create(
				_getPortletURL(
					httpServletRequest,
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

		if (plid > 0) {
			return PortletURLFactoryUtil.create(
				httpServletRequest, portletId, plid,
				PortletRequest.ACTION_PHASE);
		}

		return PortletURLFactoryUtil.create(
			httpServletRequest, portletId, PortletRequest.ACTION_PHASE);
	}

	private boolean _hasAddCommerceOrderPermission() {
		if ((_accountEntry == null) || (_themeDisplay == null)) {
			return false;
		}

		try {
			CommerceOrderFieldsConfiguration commerceOrderFieldsConfiguration =
				_configurationProvider.getConfiguration(
					CommerceOrderFieldsConfiguration.class,
					new GroupServiceSettingsLocator(
						_commerceChannelGroupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

			int commerceOrdersCount =
				(int)_commerceOrderLocalService.getCommerceOrdersCount(
					_accountEntry.getCompanyId(), _commerceChannelGroupId,
					new long[] {_accountEntry.getAccountEntryId()},
					StringPool.BLANK,
					new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN},
					false);

			if ((commerceOrderFieldsConfiguration.accountCartMaxAllowed() >
					0) &&
				(commerceOrdersCount >=
					commerceOrderFieldsConfiguration.accountCartMaxAllowed())) {

				return false;
			}

			CommerceOrderCheckoutConfiguration
				commerceOrderCheckoutConfiguration =
					_configurationProvider.getConfiguration(
						CommerceOrderCheckoutConfiguration.class,
						new GroupServiceSettingsLocator(
							_commerceChannelGroupId,
							CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			if (_accountEntry.isGuestAccount() &&
				commerceOrderCheckoutConfiguration.guestCheckoutEnabled()) {

				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return _commerceOrderPortletResourcePermission.contains(
			_themeDisplay.getPermissionChecker(),
			_accountEntry.getAccountEntryGroupId(),
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER);
	}

	private boolean _hasManageAccountsPermission() {
		if (_themeDisplay == null) {
			return false;
		}

		try {
			ModelResourcePermission<User> userModelResourcePermission =
				_userModelResourcePermissionSnapshot.get();

			return userModelResourcePermission.contains(
				_themeDisplay.getPermissionChecker(), _themeDisplay.getUser(),
				AccountActionKeys.MANAGE_ACCOUNTS);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private static final String _PAGE = "/account_selector/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		AccountSelectorTag.class);

	private static final Snapshot<ModelResourcePermission<User>>
		_userModelResourcePermissionSnapshot = new Snapshot<>(
			ServletContextUtil.class,
			Snapshot.cast(ModelResourcePermission.class),
			"(model.class.name=com.liferay.portal.kernel.model.User)");

	private AccountEntry _accountEntry;
	private String[] _accountEntryAllowedTypes;
	private String _addCommerceOrderURL = StringPool.BLANK;
	private String _checkoutURL = StringPool.BLANK;
	private long _commerceChannelGroupId;
	private long _commerceChannelId;
	private CommerceOrder _commerceOrder;
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;
	private CommerceOrderLocalService _commerceOrderLocalService;
	private PortletResourcePermission _commerceOrderPortletResourcePermission;
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;
	private ConfigurationProvider _configurationProvider;
	private String _cssClasses = StringPool.BLANK;
	private String _currencyCode = StringPool.BLANK;
	private String _editOrderURL = StringPool.BLANK;
	private String _setCurrentAccountURL = StringPool.BLANK;
	private String _spritemap = StringPool.BLANK;
	private ThemeDisplay _themeDisplay;

}