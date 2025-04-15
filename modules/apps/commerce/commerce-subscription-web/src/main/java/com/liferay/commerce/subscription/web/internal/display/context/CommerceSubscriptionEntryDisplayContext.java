/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.util.CPSubscriptionType;
import com.liferay.commerce.product.util.CPSubscriptionTypeJSPContributor;
import com.liferay.commerce.product.util.CPSubscriptionTypeJSPContributorRegistry;
import com.liferay.commerce.product.util.CPSubscriptionTypeRegistry;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
public class CommerceSubscriptionEntryDisplayContext {

	public CommerceSubscriptionEntryDisplayContext(
		CommercePaymentMethodGroupRelLocalService
			commercePaymentMethodGroupRelLocalService,
		CommerceSubscriptionEntryLocalService
			commerceSubscriptionEntryLocalService,
		CommerceOrderItemLocalService commerceOrderItemLocalService,
		CPSubscriptionTypeJSPContributorRegistry
			cpSubscriptionTypeJSPContributorRegistry,
		CPSubscriptionTypeRegistry cpSubscriptionTypeRegistry,
		HttpServletRequest httpServletRequest,
		PortletResourcePermission portletResourcePermission) {

		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
		_commerceSubscriptionEntryLocalService =
			commerceSubscriptionEntryLocalService;
		_commerceOrderItemLocalService = commerceOrderItemLocalService;
		_cpSubscriptionTypeJSPContributorRegistry =
			cpSubscriptionTypeJSPContributorRegistry;
		_cpSubscriptionTypeRegistry = cpSubscriptionTypeRegistry;
		_httpServletRequest = httpServletRequest;
		_portletResourcePermission = portletResourcePermission;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public String getAccountEntryThumbnailURL() throws PortalException {
		if (_commerceSubscriptionEntry == null) {
			return StringPool.BLANK;
		}

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				_commerceSubscriptionEntry.getCommerceOrderItemId());

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		StringBundler sb = new StringBundler(5);

		sb.append(themeDisplay.getPathImage());
		sb.append("/organization_logo?img_id=");
		sb.append(accountEntry.getLogoId());

		if (accountEntry.getLogoId() > 0) {
			sb.append("&t=");
			sb.append(
				WebServerServletTokenUtil.getToken(accountEntry.getLogoId()));
		}

		return sb.toString();
	}

	public long getCommerceOrderId() throws PortalException {
		CommerceSubscriptionEntry commerceSubscriptionEntry =
			getCommerceSubscriptionEntry();

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceSubscriptionEntry.getCommerceOrderItemId());

		return commerceOrderItem.getCommerceOrderId();
	}

	public CommerceSubscriptionEntry getCommerceSubscriptionEntry() {
		if (_commerceSubscriptionEntry != null) {
			return _commerceSubscriptionEntry;
		}

		long commerceSubscriptionEntryId = ParamUtil.getLong(
			_httpServletRequest, "commerceSubscriptionEntryId");

		if (commerceSubscriptionEntryId > 0) {
			_commerceSubscriptionEntry =
				_commerceSubscriptionEntryLocalService.
					fetchCommerceSubscriptionEntry(commerceSubscriptionEntryId);
		}

		return _commerceSubscriptionEntry;
	}

	public long getCommerceSubscriptionEntryId() {
		CommerceSubscriptionEntry commerceSubscriptionEntry =
			getCommerceSubscriptionEntry();

		if (commerceSubscriptionEntry != null) {
			return commerceSubscriptionEntry.getCommerceSubscriptionEntryId();
		}

		return 0;
	}

	public String getCommerceSubscriptionEntryStartDate() {
		Date showDate = null;

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			getCommerceSubscriptionEntry();

		Date deliveryStartDate =
			commerceSubscriptionEntry.getDeliveryStartDate();
		Date startDate = commerceSubscriptionEntry.getStartDate();

		if ((deliveryStartDate != null) && (startDate != null)) {
			showDate =
				startDate.before(deliveryStartDate) ? startDate :
					deliveryStartDate;
		}
		else if ((deliveryStartDate != null) && (startDate == null)) {
			showDate = deliveryStartDate;
		}
		else if ((deliveryStartDate == null) && (startDate != null)) {
			showDate = startDate;
		}
		else {
			return "";
		}

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.SHORT, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		return dateTimeFormat.format(showDate);
	}

	public CPSubscriptionType getCPSubscriptionType(String subscriptionType) {
		return _cpSubscriptionTypeRegistry.getCPSubscriptionType(
			subscriptionType);
	}

	public CPSubscriptionTypeJSPContributor getCPSubscriptionTypeJSPContributor(
		String subscriptionType) {

		return _cpSubscriptionTypeJSPContributorRegistry.
			getCPSubscriptionTypeJSPContributor(subscriptionType);
	}

	public List<CPSubscriptionType> getCPSubscriptionTypes() {
		return _cpSubscriptionTypeRegistry.getCPSubscriptionTypes();
	}

	public String getEditCommerceOrderURL(long commerceOrderId)
		throws PortalException {

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				_httpServletRequest, themeDisplay.getScopeGroup(),
				CommerceOrder.class.getName(), PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_order/edit_commerce_order"
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setParameter(
			"commerceOrderId",
			() -> {
				String orderId;

				if (commerceOrderId > 0) {
					orderId = String.valueOf(commerceOrderId);
				}
				else {
					orderId = String.valueOf(getCommerceOrderId());
				}

				return orderId;
			}
		).buildString();
	}

	public List<HeaderActionModel> getHeaderActionModels() {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		if (_commerceSubscriptionEntry == null) {
			return headerActionModels;
		}

		RenderResponse renderResponse = _cpRequestHelper.getRenderResponse();

		PortletURL cancelURL = renderResponse.createRenderURL();

		headerActionModels.add(
			new HeaderActionModel(
				null, null, cancelURL.toString(), null, "cancel"));

		headerActionModels.add(
			new HeaderActionModel(
				"btn-primary", renderResponse.getNamespace() + "fm",
				PortletURLBuilder.create(
					getTransitionOrderPortletURL()
				).setParameter(
					"transitionName", "save"
				).buildString(),
				null, "save"));

		return headerActionModels;
	}

	public String getOrderPaymentMethodImage() throws PortalException {
		CommerceSubscriptionEntry commerceSubscriptionEntry =
			getCommerceSubscriptionEntry();

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceSubscriptionEntry.getCommerceOrderItemId());

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		String paymentMethodKey = commerceOrder.getCommercePaymentMethodKey();

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(), paymentMethodKey);

		if (commercePaymentMethodGroupRel == null) {
			return StringPool.BLANK;
		}

		return commercePaymentMethodGroupRel.getImageURL(
			_cpRequestHelper.getThemeDisplay());
	}

	public String getOrderPaymentMethodName() throws PortalException {
		CommerceSubscriptionEntry commerceSubscriptionEntry =
			getCommerceSubscriptionEntry();

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceSubscriptionEntry.getCommerceOrderItemId());

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		String paymentMethodKey = commerceOrder.getCommercePaymentMethodKey();

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(), paymentMethodKey);

		if (commercePaymentMethodGroupRel == null) {
			return StringPool.BLANK;
		}

		return commercePaymentMethodGroupRel.getName(
			_cpRequestHelper.getLocale());
	}

	public int getOrderPaymentStatus() throws PortalException {
		CommerceSubscriptionEntry commerceSubscriptionEntry =
			getCommerceSubscriptionEntry();

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceSubscriptionEntry.getCommerceOrderItemId());

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		return commerceOrder.getPaymentStatus();
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			_cpRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		long commerceSubscriptionEntryId = ParamUtil.getLong(
			_httpServletRequest, "commerceSubscriptionEntryId");

		if (commerceSubscriptionEntryId > 0) {
			portletURL.setParameter(
				"commerceSubscriptionEntryId",
				String.valueOf(commerceSubscriptionEntryId));
		}

		String delta = ParamUtil.getString(_httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			_httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		portletURL.setParameter("navigation", _getNavigation());

		return portletURL;
	}

	public PortletURL getTransitionOrderPortletURL() {
		return PortletURLBuilder.createActionURL(
			_cpRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/commerce_open_order_content/edit_commerce_order"
		).setCMD(
			ActionKeys.UPDATE
		).setRedirect(
			_cpRequestHelper.getCurrentURL()
		).setParameter(
			"commerceSubscriptionEntryId",
			_commerceSubscriptionEntry.getCommerceSubscriptionEntryId()
		).buildPortletURL();
	}

	public boolean hasManageCommerceSubscriptionEntryPermission() {
		return _portletResourcePermission.contains(
			_cpRequestHelper.getPermissionChecker(), null,
			CommerceActionKeys.MANAGE_COMMERCE_SUBSCRIPTIONS);
	}

	public boolean isPaymentMethodActive(String engineKey) {
		try {
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
				_commercePaymentMethodGroupRelLocalService.
					fetchCommercePaymentMethodGroupRel(
						_cpRequestHelper.getCommerceChannelGroupId(),
						engineKey);

			if (commercePaymentMethodGroupRel == null) {
				return false;
			}

			return commercePaymentMethodGroupRel.isActive();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private String _getNavigation() {
		return ParamUtil.getString(_httpServletRequest, "navigation", "all");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceSubscriptionEntryDisplayContext.class);

	private final CommerceOrderItemLocalService _commerceOrderItemLocalService;
	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;
	private CommerceSubscriptionEntry _commerceSubscriptionEntry;
	private final CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;
	private final CPRequestHelper _cpRequestHelper;
	private final CPSubscriptionTypeJSPContributorRegistry
		_cpSubscriptionTypeJSPContributorRegistry;
	private final CPSubscriptionTypeRegistry _cpSubscriptionTypeRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final PortletResourcePermission _portletResourcePermission;

}