/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderWebKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStepRegistry;
import com.liferay.commerce.util.CommerceOrderInfoItemUtil;
import com.liferay.friendly.url.provider.FriendlyURLSeparatorProvider;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Andrea Di Giorgi
 * @author Gianmarco Brunialti Masera
 */
@Component(service = CommerceOrderHttpHelper.class)
public class CommerceOrderHttpHelperImpl implements CommerceOrderHttpHelper {

	@Override
	public CommerceOrder addCommerceOrder(HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceContext commerceContext = _getCommerceContext(
			httpServletRequest);

		CommerceOrder commerceOrder = null;

		String commerceCurrencyCode = null;

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		if (commerceCurrency != null) {
			commerceCurrencyCode = commerceCurrency.getCode();
		}

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry != null) {
			commerceOrder = _commerceOrderService.addCommerceOrder(
				commerceContext.getCommerceChannelGroupId(),
				accountEntry.getAccountEntryId(), commerceCurrencyCode, 0);
		}

		if (accountEntry == null) {
			throw new CommerceOrderValidatorException(
				Collections.singletonList(
					new CommerceOrderValidatorResult(
						false,
						_getLocalizedMessage(
							_portal.getLocale(httpServletRequest),
							"please-select-a-valid-account"))));
		}

		setCurrentCommerceOrder(httpServletRequest, commerceOrder);

		return commerceOrder;
	}

	@Override
	public void deleteCommerceOrder(
			ActionRequest actionRequest, long commerceOrderId)
		throws PortalException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		httpServletRequest = _portal.getOriginalServletRequest(
			httpServletRequest);

		httpServletRequest.removeAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		HttpSession httpSession = httpServletRequest.getSession();

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		httpSession.removeAttribute(
			CommerceOrder.class.getName() + StringPool.POUND +
				commerceOrder.getGroupId());

		_commerceOrderService.deleteCommerceOrder(commerceOrderId);
	}

	@Override
	public CommerceOrder fetchCommerceOrderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return _commerceOrderLocalService.fetchCommerceOrderByUuidAndGroupId(
			uuid, groupId);
	}

	public String getCommerceCartBaseURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		if (hasCommerceOrderPortlet(
				httpServletRequest,
				CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT)) {

			long plid = PortalUtil.getPlidFromPortletId(
				PortalUtil.getScopeGroupId(httpServletRequest),
				CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

			return PortletURLBuilder.create(
				_portletURLFactory.create(
					httpServletRequest,
					CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT, plid,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/commerce_open_order_content/edit_commerce_order"
			).setBackURL(
				ParamUtil.getString(httpServletRequest, "backURL")
			).buildString();
		}

		return HtmlUtil.escape(
			CommerceOrderInfoItemUtil.getCommerceOrderFriendlyURL(
				_friendlyURLSeparatorProviderSnapshot.get(),
				httpServletRequest));
	}

	@Override
	public String getCommerceCartPortletURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceOrder commerceOrder = getCurrentCommerceOrder(
			httpServletRequest);

		return getCommerceCartPortletURL(httpServletRequest, commerceOrder);
	}

	@Override
	public String getCommerceCartPortletURL(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException {

		long groupId = _portal.getScopeGroupId(httpServletRequest);

		return getCommerceCartPortletURL(
			groupId, httpServletRequest, commerceOrder);
	}

	@Override
	public String getCommerceCartPortletURL(
			long groupId, HttpServletRequest httpServletRequest,
			CommerceOrder commerceOrder)
		throws PortalException {

		if ((commerceOrder != null) &&
			FeatureFlagManagerUtil.isEnabled("LPD-20379")) {

			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				_getLayoutDisplayPageObjectProvider(commerceOrder);

			if ((layoutDisplayPageObjectProvider != null) &&
				AssetDisplayPageUtil.hasAssetDisplayPage(
					groupId, layoutDisplayPageObjectProvider.getClassNameId(),
					layoutDisplayPageObjectProvider.getClassPK(),
					layoutDisplayPageObjectProvider.getClassTypeId())) {

				String commerceOrderFriendlyURL =
					CommerceOrderInfoItemUtil.getCommerceOrderFriendlyURL(
						_friendlyURLSeparatorProviderSnapshot.get(),
						httpServletRequest);

				return commerceOrderFriendlyURL +
					commerceOrder.getCommerceOrderId();
			}
		}

		long plid = _portal.getPlidFromPortletId(
			groupId, CommercePortletKeys.COMMERCE_ORDER_CONTENT);

		if ((plid > 0) && (commerceOrder != null) && !commerceOrder.isOpen()) {
			PortletURL portletURL = _getPortletURL(
				groupId, httpServletRequest,
				CommercePortletKeys.COMMERCE_ORDER_CONTENT);

			if (commerceOrder != null) {
				portletURL.setParameter(
					"mvcRenderCommandName",
					"/commerce_order_content/view_commerce_order_details");
				portletURL.setParameter(
					"backURL",
					ParamUtil.getString(httpServletRequest, "backURL"));
				portletURL.setParameter(
					"commerceOrderUuid",
					String.valueOf(commerceOrder.getUuid()));
				portletURL.setParameter(
					"commerceOrderId",
					String.valueOf(commerceOrder.getCommerceOrderId()));
			}

			return portletURL.toString();
		}

		plid = _portal.getPlidFromPortletId(
			groupId, CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

		if ((plid > 0) && (commerceOrder != null) && commerceOrder.isOpen()) {
			PortletURL portletURL = _getPortletURL(
				groupId, httpServletRequest,
				CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

			if (commerceOrder != null) {
				portletURL.setParameter(
					"mvcRenderCommandName",
					"/commerce_open_order_content/edit_commerce_order");
				portletURL.setParameter(
					"backURL",
					ParamUtil.getString(httpServletRequest, "backURL"));
				portletURL.setParameter(
					"commerceOrderUuid",
					String.valueOf(commerceOrder.getUuid()));
			}

			return portletURL.toString();
		}

		plid = _portal.getPlidFromPortletId(
			groupId, CommercePortletKeys.COMMERCE_CART_CONTENT);

		if (plid > 0) {
			return PortletURLBuilder.create(
				_getPortletURL(
					groupId, httpServletRequest,
					CommercePortletKeys.COMMERCE_CART_CONTENT)
			).setBackURL(
				ParamUtil.getString(httpServletRequest, "backURL")
			).buildString();
		}

		return StringPool.BLANK;
	}

	@Override
	public PortletURL getCommerceCheckoutPortletURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		LiferayPortletURL portletURL = (LiferayPortletURL)_getPortletURL(
			httpServletRequest, CommercePortletKeys.COMMERCE_CHECKOUT);

		CommerceOrder commerceOrder = getCurrentCommerceOrder(
			httpServletRequest);

		if (commerceOrder == null) {
			return portletURL;
		}

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

		if (commerceOrder.isEmpty()) {
			return portletURL;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String checkoutStepName = StringPool.BLANK;

		try {
			List<CommerceCheckoutStep> commerceCheckoutSteps =
				_commerceCheckoutStepRegistry.getCommerceCheckoutSteps(
					httpServletRequest, themeDisplay.getResponse(), true);

			if ((commerceCheckoutSteps != null) &&
				!commerceCheckoutSteps.isEmpty()) {

				CommerceCheckoutStep commerceCheckoutStep =
					commerceCheckoutSteps.get(0);

				checkoutStepName = commerceCheckoutStep.getName();
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new PortalException(exception);
		}

		portletURL.setParameter("checkoutStepName", checkoutStepName);
		portletURL.setParameter("commerceOrderUuid", commerceOrder.getUuid());

		try {
			portletURL.setWindowState(LiferayWindowState.NORMAL);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);

			throw new PortalException(windowStateException);
		}

		if (commerceOrder.getCommerceAccountId() ==
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST) {

			PortletURL checkoutPortletURL = portletURL;

			Layout currentLayout = (Layout)httpServletRequest.getAttribute(
				WebKeys.LAYOUT);

			String friendlyURL = "/authentication";

			Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
				_portal.getScopeGroupId(httpServletRequest), false,
				friendlyURL);

			if (!friendlyURL.equals(currentLayout.getFriendlyURL()) &&
				(layout != null)) {

				portletURL = _portletURLFactory.create(
					httpServletRequest,
					"com_liferay_login_web_portlet_LoginPortlet", layout,
					PortletRequest.RENDER_PHASE);

				CommerceContext commerceContext = _getCommerceContext(
					httpServletRequest);

				int commerceSiteType = commerceContext.getCommerceSiteType();

				if (commerceSiteType ==
						CommerceChannelConstants.SITE_TYPE_B2B) {

					Group group = themeDisplay.getSiteGroup();

					portletURL.setParameter(
						"redirect", group.getDisplayURL(themeDisplay));
				}
				else {
					portletURL.setParameter(
						"redirect", checkoutPortletURL.toString());
				}
			}
			else {
				portletURL.setParameter(
					"continueAsGuest", Boolean.TRUE.toString());

				Cookie cookie = new Cookie(
					CookiesConstants.NAME_COMMERCE_CONTINUE_AS_GUEST,
					Boolean.TRUE.toString());

				String domain = CookiesManagerUtil.getDomain(
					httpServletRequest);

				if (Validator.isNotNull(domain)) {
					cookie.setDomain(domain);
				}

				cookie.setMaxAge(CookiesConstants.MAX_AGE);

				CookiesManagerUtil.addCookie(
					CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
					httpServletRequest, themeDisplay.getResponse());

				portletURL.setParameter(
					"redirect", checkoutPortletURL.toString());
			}
		}

		return portletURL;
	}

	@Override
	public BigDecimal getCommerceOrderItemsQuantity(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceOrder commerceOrder = getCurrentCommerceOrder(
			httpServletRequest);

		if (commerceOrder == null) {
			return BigDecimal.ZERO;
		}

		return _commerceOrderItemService.getCommerceOrderItemsQuantity(
			commerceOrder.getCommerceOrderId());
	}

	@Override
	public String getCookieName(long groupId) {
		return CommerceOrder.class.getName() + StringPool.POUND + groupId;
	}

	@Override
	public CommerceOrder getCurrentCommerceOrder(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		if (GetterUtil.getBoolean(_initialized.get())) {
			return _commerceOrder.get();
		}

		CommerceContext commerceContext = _getCommerceContext(
			httpServletRequest);

		if (commerceContext == null) {
			_setCurrentCommerceOrder(null);

			return null;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		CommerceOrder commerceOrder = (CommerceOrder)httpSession.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER_ON_ACCOUNT_SELECTION);

		if (commerceOrder != null) {
			_setCurrentCommerceOrder(commerceOrder);

			return commerceOrder;
		}

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry == null) {
			_setCurrentCommerceOrder(null);

			return null;
		}

		commerceOrder = (CommerceOrder)httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (commerceOrder == null) {
			long groupId = commerceContext.getCommerceChannelGroupId();

			String uuid = (String)httpSession.getAttribute(
				getCookieName(groupId));

			commerceOrder =
				_commerceOrderLocalService.fetchCommerceOrderByUuidAndGroupId(
					uuid, groupId);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((commerceOrder != null) && !commerceOrder.isOpen()) {
			CookiesManagerUtil.deleteCookies(
				CookiesManagerUtil.getDomain(httpServletRequest),
				httpServletRequest, themeDisplay.getResponse(),
				CommerceOrder.class.getName() + StringPool.POUND +
					commerceOrder.getGroupId());

			httpSession.removeAttribute(
				CommerceOrder.class.getName() + StringPool.POUND +
					commerceOrder.getGroupId());

			commerceOrder = null;
		}

		if (commerceOrder == null) {
			commerceOrder = _getCurrentCommerceOrder(
				commerceContext, httpServletRequest);
		}

		if (commerceOrder != null) {
			if (commerceOrder.isGuestOrder()) {
				commerceOrder = _checkGuestOrder(
					commerceContext, commerceOrder, httpServletRequest,
					themeDisplay);
			}
			else {
				if (commerceOrder.getCommerceAccountId() !=
						accountEntry.getAccountEntryId()) {

					httpSession.removeAttribute(
						CommerceOrder.class.getName() + StringPool.POUND +
							commerceOrder.getGroupId());

					_setCurrentCommerceOrder(null);

					return null;
				}
			}
		}

		if (((commerceOrder != null) && !commerceOrder.isOpen()) ||
			((commerceOrder != null) &&
			 !_commerceOrderModelResourcePermission.contains(
				 PermissionThreadLocal.getPermissionChecker(), commerceOrder,
				 ActionKeys.UPDATE))) {

			_setCurrentCommerceOrder(null);

			return null;
		}

		_setCurrentCommerceOrder(commerceOrder);

		return commerceOrder;
	}

	@Override
	public boolean hasCommerceOrderPortlet(
			HttpServletRequest httpServletRequest, String portletKey)
		throws PortalException {

		if (portletKey.equals(CommercePortletKeys.COMMERCE_CART_CONTENT) ||
			portletKey.equals(
				CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT) ||
			portletKey.equals(CommercePortletKeys.COMMERCE_ORDER_CONTENT)) {

			long groupId = _portal.getScopeGroupId(httpServletRequest);

			if (_portal.getPlidFromPortletId(groupId, portletKey) > 0) {
				return true;
			}

			return false;
		}

		return false;
	}

	@Override
	public boolean hasCommerceOrderReturns(
		HttpServletRequest httpServletRequest) {

		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(httpServletRequest), "LPD-10562")) {

			return false;
		}

		CommerceOrder commerceOrder =
			CommerceOrderInfoItemUtil.getCommerceOrder(
				_commerceOrderService, httpServletRequest);

		if ((commerceOrder != null) && !commerceOrder.isOpen()) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_COMMERCE_RETURN", CompanyThreadLocal.getCompanyId());

			if (objectDefinition == null) {
				return false;
			}

			return ListUtil.exists(
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				objectEntry -> {
					Map<String, Serializable> values = objectEntry.getValues();

					if (commerceOrder.getCommerceOrderId() ==
							GetterUtil.getLong(
								values.get(
									"r_commerceOrderToCommerceReturns" +
										"_commerceOrderId"))) {

						return true;
					}

					return false;
				});
		}

		return false;
	}

	@Override
	public boolean hasCommerceOrderShipments(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceOrder commerceOrder =
			CommerceOrderInfoItemUtil.getCommerceOrder(
				_commerceOrderService, httpServletRequest);

		if ((commerceOrder == null) || commerceOrder.isOpen()) {
			return false;
		}

		List<CommerceShipment> commerceShipment =
			_commerceShipmentLocalService.getCommerceShipments(
				commerceOrder.getCommerceOrderId(), 0, 1);

		return !commerceShipment.isEmpty();
	}

	@Override
	public boolean isGuestCheckoutEnabled(HttpServletRequest httpServletRequest)
		throws PortalException {

		long groupId = _portal.getScopeGroupId(httpServletRequest);

		groupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				groupId);

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					groupId, CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.guestCheckoutEnabled();
	}

	@Override
	public boolean isMultishippingEnabled(
		HttpServletRequest httpServletRequest) {

		try {
			CommerceOrder commerceOrder =
				CommerceOrderInfoItemUtil.getCommerceOrder(
					_commerceOrderService, httpServletRequest);

			if ((commerceOrder == null) ||
				!_commerceOrderModelResourcePermission.contains(
					PermissionThreadLocal.getPermissionChecker(), commerceOrder,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_MULTISHIPPING)) {

				return false;
			}

			CommerceOrderCheckoutConfiguration
				commerceOrderCheckoutConfiguration =
					_configurationProvider.getConfiguration(
						CommerceOrderCheckoutConfiguration.class,
						new GroupServiceSettingsLocator(
							_commerceChannelLocalService.
								getCommerceChannelGroupIdBySiteGroupId(
									_portal.getScopeGroupId(
										httpServletRequest)),
							CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			return commerceOrderCheckoutConfiguration.multishippingEnabled();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Override
	public void setCurrentCommerceOrder(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryService.fetchAccountEntry(
			commerceOrder.getCommerceAccountId());

		if (accountEntry != null) {
			_commerceAccountHelper.setCurrentCommerceAccount(
				httpServletRequest, commerceOrder.getGroupId(),
				accountEntry.getAccountEntryId());
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		commerceOrder = _commerceOrderLocalService.recalculatePrice(
			commerceOrder.getCommerceOrderId(),
			_getCommerceContext(httpServletRequest));

		if ((themeDisplay != null) && !themeDisplay.isSignedIn()) {
			_setGuestCommerceOrder(commerceOrder, themeDisplay);

			_setCurrentCommerceOrder(commerceOrder);

			return;
		}

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

		httpServletRequest = _portal.getOriginalServletRequest(
			httpServletRequest);

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute(
			getCookieName(commerceOrder.getGroupId()), commerceOrder.getUuid());

		_setCurrentCommerceOrder(commerceOrder);
	}

	@Reference
	protected LayoutDisplayPageProviderRegistry
		layoutDisplayPageProviderRegistry;

	private CommerceOrder _checkGuestOrder(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws PortalException {

		if (commerceOrder == null) {
			return null;
		}

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry == null) {
			return null;
		}

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) || user.isGuestUser()) {
			return commerceOrder;
		}

		boolean mergeGuestOrder = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(
				CommerceOrderWebKeys.MERGE_GUEST_ORDER),
			true);

		if (!mergeGuestOrder) {
			return commerceOrder;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		String cookieName = getCookieName(commerceOrder.getGroupId());

		// Remove thread local order when used

		CommerceOrder threadLocalCommerceOrder = _commerceOrder.get();

		if ((threadLocalCommerceOrder != null) &&
			threadLocalCommerceOrder.isGuestOrder()) {

			httpSession.removeAttribute(cookieName);

			_unsetCurrentCommerceOrder();
		}

		CommerceOrder userCommerceOrder =
			_commerceOrderService.fetchCommerceOrder(
				accountEntry.getAccountEntryId(),
				commerceContext.getCommerceChannelGroupId(), user.getUserId(),
				CommerceOrderConstants.ORDER_STATUS_OPEN);

		if (userCommerceOrder == null) {
			httpSession.removeAttribute(cookieName);

			if (themeDisplay != null) {
				CookiesManagerUtil.deleteCookies(
					CookiesManagerUtil.getDomain(httpServletRequest),
					httpServletRequest, themeDisplay.getResponse(), cookieName);
			}

			commerceOrder =
				_commerceOrderLocalService.resetCommerceOrderAddresses(
					commerceOrder.getCommerceOrderId(), true, true);

			return _commerceOrderLocalService.updateAccount(
				commerceOrder.getCommerceOrderId(), user.getUserId(),
				accountEntry.getAccountEntryId());
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		_setCurrentCommerceOrder(userCommerceOrder);

		try {
			httpSession.setAttribute(cookieName, userCommerceOrder.getUuid());

			_commerceOrderLocalService.mergeGuestCommerceOrder(
				user.getUserId(), commerceOrder.getCommerceOrderId(),
				userCommerceOrder.getCommerceOrderId(),
				_getCommerceContext(httpServletRequest), serviceContext);
		}
		finally {
			_unsetCurrentCommerceOrder();
		}

		httpSession.removeAttribute(cookieName);

		return userCommerceOrder;
	}

	private String _fetchCommerceOrderUuid(
		CommerceChannel commerceChannel,
		HttpServletRequest httpServletRequest) {

		if (commerceChannel == null) {
			return null;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		String cookieName = getCookieName(commerceChannel.getGroupId());

		String commerceOrderUuid = (String)httpSession.getAttribute(cookieName);

		if (Validator.isNull(commerceOrderUuid)) {
			commerceOrderUuid = CookiesManagerUtil.getCookieValue(
				cookieName, httpServletRequest, true);
		}

		return commerceOrderUuid;
	}

	private CommerceContext _getCommerceContext(
		HttpServletRequest httpServletRequest) {

		return (CommerceContext)httpServletRequest.getAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT);
	}

	private CommerceOrder _getCurrentCommerceOrder(
			CommerceContext commerceContext,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannel(
				commerceContext.getCommerceChannelId());

		String commerceOrderUuid = _fetchCommerceOrderUuid(
			commerceChannel, httpServletRequest);

		CommerceOrder commerceOrder = _commerceOrder.get();

		if (commerceOrder != null) {
			CommerceOrder persistedCommerceOrder =
				_commerceOrderLocalService.fetchCommerceOrder(
					commerceOrder.getCommerceOrderId());

			if (persistedCommerceOrder != null) {
				commerceOrder = persistedCommerceOrder;

				_setCurrentCommerceOrder(commerceOrder);
			}

			if ((accountEntry == null) ||
				(accountEntry.getAccountEntryId() ==
					AccountConstants.ACCOUNT_ENTRY_ID_GUEST) ||
				(Validator.isNotNull(commerceOrderUuid) &&
				 commerceOrderUuid.equals(commerceOrder.getUuid()) &&
				 (accountEntry.getAccountEntryId() ==
					 commerceOrder.getCommerceAccountId()))) {

				return commerceOrder;
			}
		}

		if ((accountEntry == null) || (commerceChannel == null)) {
			return null;
		}

		if (accountEntry.getAccountEntryId() !=
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST) {

			commerceOrder =
				_commerceOrderLocalService.fetchCommerceOrderByUuidAndGroupId(
					commerceOrderUuid, commerceChannel.getGroupId());

			if ((commerceOrder == null) ||
				(!commerceOrder.isGuestOrder() &&
				 (accountEntry.getAccountEntryId() !=
					 commerceOrder.getCommerceAccountId()))) {

				commerceOrder = _commerceOrderService.fetchCommerceOrder(
					accountEntry.getAccountEntryId(),
					commerceChannel.getGroupId(),
					_portal.getUserId(httpServletRequest),
					CommerceOrderConstants.ORDER_STATUS_OPEN);
			}

			if (commerceOrder != null) {
				_validateCommerceOrderItemVersions(
					commerceOrder, _portal.getLocale(httpServletRequest));

				_setCurrentCommerceOrder(commerceOrder);

				return commerceOrder;
			}
		}

		if (Validator.isNotNull(commerceOrderUuid)) {
			commerceOrder =
				_commerceOrderLocalService.fetchCommerceOrderByUuidAndGroupId(
					commerceOrderUuid, commerceChannel.getGroupId());

			if (commerceOrder != null) {
				_setCurrentCommerceOrder(commerceOrder);
			}
		}

		return commerceOrder;
	}

	private LayoutDisplayPageObjectProvider<?>
		_getLayoutDisplayPageObjectProvider(CommerceOrder commerceOrder) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					CommerceOrder.class.getName());

		InfoItemReference infoItemReference = new InfoItemReference(
			CommerceOrder.class.getName(), commerceOrder.getCommerceOrderId());

		return layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
			infoItemReference);
	}

	private String _getLocalizedMessage(Locale locale, String key) {
		if (locale == null) {
			return key;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, key);
	}

	private PortletURL _getPortletURL(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		long groupId = _portal.getScopeGroupId(httpServletRequest);

		return _getPortletURL(groupId, httpServletRequest, portletId);
	}

	private PortletURL _getPortletURL(
			long groupId, HttpServletRequest httpServletRequest,
			String portletId)
		throws PortalException {

		PortletURL portletURL = null;

		long plid = _portal.getPlidFromPortletId(groupId, portletId);

		if (plid > 0) {
			portletURL = _portletURLFactory.create(
				httpServletRequest, portletId, plid,
				PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = _portletURLFactory.create(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE);
		}

		return portletURL;
	}

	private void _setCurrentCommerceOrder(CommerceOrder commerceOrder) {
		_commerceOrder.set(commerceOrder);
		_initialized.set(Boolean.TRUE);
	}

	private void _setGuestCommerceOrder(
			CommerceOrder commerceOrder, ThemeDisplay themeDisplay)
		throws PortalException {

		User user = themeDisplay.getUser();

		if ((user != null) && !user.isGuestUser()) {
			return;
		}

		long commerceChannelGroupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				themeDisplay.getScopeGroupId());

		Cookie cookie = new Cookie(
			getCookieName(commerceChannelGroupId), commerceOrder.getUuid());

		String domain = CookiesManagerUtil.getDomain(themeDisplay.getRequest());

		if (Validator.isNotNull(domain)) {
			cookie.setDomain(domain);
		}

		cookie.setMaxAge(CookiesConstants.MAX_AGE);

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
			themeDisplay.getRequest(), themeDisplay.getResponse());
	}

	private void _unsetCurrentCommerceOrder() {
		_commerceOrder.remove();
		_initialized.set(Boolean.FALSE);
	}

	private void _validateCommerceOrderItemVersions(
			CommerceOrder commerceOrder, Locale locale)
		throws PortalException {

		VersionCommerceOrderValidatorImpl versionCommerceOrderValidatorImpl =
			new VersionCommerceOrderValidatorImpl();

		versionCommerceOrderValidatorImpl.setCommerceOrderItemLocalService(
			_commerceOrderItemLocalService);
		versionCommerceOrderValidatorImpl.setCPInstanceLocalService(
			_cpInstanceLocalService);

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			versionCommerceOrderValidatorImpl.validate(
				locale, commerceOrderItem);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderHttpHelperImpl.class);

	private static final ThreadLocal<CommerceOrder> _commerceOrder =
		new CentralizedThreadLocal<>(
			CommerceOrderHttpHelperImpl.class.getName() + "._commerceOrder");
	private static final Snapshot<FriendlyURLSeparatorProvider>
		_friendlyURLSeparatorProviderSnapshot = new Snapshot<>(
			CommerceOrderHttpHelperImpl.class,
			FriendlyURLSeparatorProvider.class);
	private static final ThreadLocal<Boolean> _initialized =
		new CentralizedThreadLocal<>(
			CommerceOrderHttpHelperImpl.class.getName() + "._initialized",
			() -> false);

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCheckoutStepRegistry _commerceCheckoutStepRegistry;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

}