/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderItemDecimalQuantityConfiguration;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.frontend.helper.CommerceOrderStepTrackerHelper;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.frontend.model.StepModel;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryLocalService;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.order.status.CommerceOrderStatus;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.order.web.internal.constants.CommerceOrderScreenNavigationConstants;
import com.liferay.commerce.order.web.internal.display.context.helper.CommerceOrderRequestHelper;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPMeasurementUnitService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderNoteService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.service.CommerceShipmentService;
import com.liferay.commerce.term.constants.CommerceTermEntryConstants;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Andrea Di Giorgi
 * @author Luca Pellizzon
 * @author Fabio Diego Mastrorilli
 * @author Alessio Antonio Rendina
 */
public class CommerceOrderEditDisplayContext {

	public CommerceOrderEditDisplayContext(
			CommerceChannelLocalService commerceChannelLocalService,
			CommerceNotificationQueueEntryLocalService
				commerceNotificationQueueEntryLocalService,
			CommerceOrderEngine commerceOrderEngine,
			CommerceOrderItemDecimalQuantityConfiguration
				commerceOrderItemDecimalQuantityConfiguration,
			CommerceOrderItemQuantityFormatter
				commerceOrderItemQuantityFormatter,
			CommerceOrderItemService commerceOrderItemService,
			CommerceOrderNoteService commerceOrderNoteService,
			PortletResourcePermission commerceOrderPortletResourcePermission,
			CommerceOrderService commerceOrderService,
			CommerceOrderStatusRegistry commerceOrderStatusRegistry,
			CommerceOrderStepTrackerHelper commerceOrderStepTrackerHelper,
			CommerceOrderTypeService commerceOrderTypeService,
			CommercePaymentMethodGroupRelLocalService
				commercePaymentMethodGroupRelLocalService,
			CommercePriceFormatter commercePriceFormatter,
			CommerceShipmentService commerceShipmentService,
			CommerceTermEntryLocalService commerceTermEntryLocalService,
			CPMeasurementUnitService cpMeasurementUnitService,
			ModelResourcePermission<CommerceOrder> modelResourcePermission,
			RenderRequest renderRequest)
		throws PortalException {

		_commerceChannelLocalService = commerceChannelLocalService;
		_commerceNotificationQueueEntryLocalService =
			commerceNotificationQueueEntryLocalService;
		_commerceOrderEngine = commerceOrderEngine;
		_commerceOrderItemDecimalQuantityConfiguration =
			commerceOrderItemDecimalQuantityConfiguration;
		_commerceOrderItemQuantityFormatter =
			commerceOrderItemQuantityFormatter;
		_commerceOrderItemService = commerceOrderItemService;
		_commerceOrderNoteService = commerceOrderNoteService;
		_commerceOrderPortletResourcePermission =
			commerceOrderPortletResourcePermission;
		_commerceOrderService = commerceOrderService;
		_commerceOrderStatusRegistry = commerceOrderStatusRegistry;
		_commerceOrderStepTrackerHelper = commerceOrderStepTrackerHelper;
		_commerceOrderTypeService = commerceOrderTypeService;
		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
		_commercePriceFormatter = commercePriceFormatter;
		_commerceShipmentService = commerceShipmentService;
		_commerceTermEntryLocalService = commerceTermEntryLocalService;
		_cpMeasurementUnitService = cpMeasurementUnitService;
		_modelResourcePermission = modelResourcePermission;

		long commerceOrderId = ParamUtil.getLong(
			renderRequest, "commerceOrderId");

		if (commerceOrderId > 0) {
			_commerceOrder = commerceOrderService.getCommerceOrder(
				commerceOrderId);
		}
		else {
			_commerceOrder = null;
		}

		_commerceOrderRequestHelper = new CommerceOrderRequestHelper(
			renderRequest);

		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

		_commerceOrderDateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.SHORT, DateFormat.SHORT, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());
	}

	public String getCommerceAccountThumbnailURL() throws PortalException {
		if (_commerceOrder == null) {
			return StringPool.BLANK;
		}

		AccountEntry accountEntry = _commerceOrder.getAccountEntry();

		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

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

	public CreationMenu getCommerceAddressCreationMenu(
			String mvcRenderCommandName)
		throws Exception {

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_commerceOrderRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						mvcRenderCommandName
					).setCMD(
						Constants.ADD
					).setParameter(
						"commerceOrderId", getCommerceOrderId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(
						_commerceOrderRequestHelper.getRequest(),
						"add-new-address"));
			}
		).build();
	}

	public String getCommerceChannelName() throws PortalException {
		CommerceOrder commerceOrder = getCommerceOrder();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		return commerceChannel.getName();
	}

	public PortletURL getCommerceNotificationQueueEntriesPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_commerceOrderRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_order/edit_commerce_order"
		).setRedirect(
			() -> {
				String redirect = ParamUtil.getString(
					_commerceOrderRequestHelper.getRequest(), "redirect");

				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return null;
			}
		).setParameter(
			"commerceOrderId", getCommerceOrderId()
		).setParameter(
			"screenNavigationCategoryKey",
			CommerceOrderScreenNavigationConstants.
				CATEGORY_KEY_COMMERCE_ORDER_EMAILS
		).buildPortletURL();
	}

	public CommerceNotificationQueueEntry getCommerceNotificationQueueEntry()
		throws PortalException {

		long commerceNotificationQueueEntryId = ParamUtil.getLong(
			_commerceOrderRequestHelper.getRequest(),
			"commerceNotificationQueueEntryId");

		if (commerceNotificationQueueEntryId <= 0) {
			return null;
		}

		return _commerceNotificationQueueEntryLocalService.
			getCommerceNotificationQueueEntry(commerceNotificationQueueEntryId);
	}

	public CommerceOrder getCommerceOrder() {
		return _commerceOrder;
	}

	public String getCommerceOrderDateTime(Date date) {
		if ((_commerceOrder == null) || (date == null)) {
			return StringPool.BLANK;
		}

		return _commerceOrderDateTimeFormat.format(date);
	}

	public long getCommerceOrderId() {
		if (_commerceOrder == null) {
			return 0;
		}

		return _commerceOrder.getCommerceOrderId();
	}

	public CommerceOrderItem getCommerceOrderItem() throws PortalException {
		if (_commerceOrderItem != null) {
			return _commerceOrderItem;
		}

		long commerceOrderItemId = ParamUtil.getLong(
			_commerceOrderRequestHelper.getRequest(), "commerceOrderItemId");

		if (commerceOrderItemId > 0) {
			_commerceOrderItem = _commerceOrderItemService.getCommerceOrderItem(
				commerceOrderItemId);
		}

		return _commerceOrderItem;
	}

	public PortletURL getCommerceOrderItemsPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_commerceOrderRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_order/edit_commerce_order"
		).setRedirect(
			() -> {
				String redirect = ParamUtil.getString(
					_commerceOrderRequestHelper.getRequest(), "redirect");

				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return null;
			}
		).setParameter(
			"commerceOrderId", getCommerceOrderId()
		).setParameter(
			"screenNavigationCategoryKey",
			CommerceOrderScreenNavigationConstants.
				CATEGORY_KEY_COMMERCE_ORDER_GENERAL
		).buildPortletURL();
	}

	public List<CommerceOrderNote> getCommerceOrderNotes()
		throws PortalException {

		long commerceOrderId = getCommerceOrderId();

		if (commerceOrderId <= 0) {
			return Collections.emptyList();
		}

		if (hasModelPermission(
				_commerceOrder,
				CommerceOrderActionKeys.
					MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES)) {

			return _commerceOrderNoteService.getCommerceOrderNotes(
				commerceOrderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		return _commerceOrderNoteService.getCommerceOrderNotes(
			commerceOrderId, false);
	}

	public String getCommerceOrderPaymentMethodDescription()
		throws PortalException {

		if ((_commerceOrder == null) ||
			Validator.isNull(_commerceOrder.getCommercePaymentMethodKey())) {

			return StringPool.BLANK;
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			getCommercePaymentMethodGroupRel();

		return commercePaymentMethodGroupRel.getDescription(
			_commerceOrderRequestHelper.getLocale());
	}

	public String getCommerceOrderPaymentMethodName() throws PortalException {
		if ((_commerceOrder == null) ||
			Validator.isNull(_commerceOrder.getCommercePaymentMethodKey())) {

			return StringPool.BLANK;
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			getCommercePaymentMethodGroupRel();

		return commercePaymentMethodGroupRel.getName(
			_commerceOrderRequestHelper.getLocale());
	}

	public PortletURL getCommerceOrderPaymentsPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_commerceOrderRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_order/edit_commerce_order"
		).setRedirect(
			() -> {
				String redirect = ParamUtil.getString(
					_commerceOrderRequestHelper.getRequest(), "redirect");

				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return null;
			}
		).setParameter(
			"commerceOrderId", getCommerceOrderId()
		).setParameter(
			"screenNavigationCategoryKey",
			CommerceOrderScreenNavigationConstants.
				CATEGORY_KEY_COMMERCE_ORDER_PAYMENTS
		).buildPortletURL();
	}

	public String getCommerceOrderTypeName(String languageId)
		throws PortalException {

		CommerceOrder commerceOrder = getCommerceOrder();

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.fetchCommerceOrderType(
				commerceOrder.getCommerceOrderTypeId());

		if (commerceOrderType == null) {
			return StringPool.BLANK;
		}

		return commerceOrderType.getName(languageId);
	}

	public CommercePaymentMethodGroupRel getCommercePaymentMethodGroupRel()
		throws PortalException {

		return _commercePaymentMethodGroupRelLocalService.
			getCommercePaymentMethodGroupRel(
				_commerceOrder.getGroupId(),
				_commerceOrder.getCommercePaymentMethodKey());
	}

	public CommerceShipment getCommerceShipment() throws PortalException {
		if (_commerceShipment != null) {
			return _commerceShipment;
		}

		long commerceShipmentId = ParamUtil.getLong(
			_commerceOrderRequestHelper.getRequest(), "commerceShipmentId");

		if (commerceShipmentId > 0) {
			_commerceShipment = _commerceShipmentService.getCommerceShipment(
				commerceShipmentId);
		}

		return _commerceShipment;
	}

	public PortletURL getCommerceShipmentsPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_commerceOrderRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_order/edit_commerce_order"
		).setRedirect(
			() -> {
				String redirect = ParamUtil.getString(
					_commerceOrderRequestHelper.getRequest(), "redirect");

				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return null;
			}
		).setParameter(
			"commerceOrderId", getCommerceOrderId()
		).setParameter(
			"screenNavigationCategoryKey",
			CommerceOrderScreenNavigationConstants.
				CATEGORY_KEY_COMMERCE_ORDER_SHIPMENTS
		).buildPortletURL();
	}

	public List<CPMeasurementUnit> getCPMeasurementUnits()
		throws PortalException {

		return _cpMeasurementUnitService.getCPMeasurementUnits(
			_commerceOrderRequestHelper.getCompanyId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public List<CommerceTermEntry> getDeliveryTermsEntries() {
		return _commerceTermEntryLocalService.getCommerceTermEntries(
			_commerceOrder.getCompanyId(),
			CommerceTermEntryConstants.TYPE_DELIVERY_TERMS);
	}

	public String getDescriptiveAddress(CommerceAddress commerceAddress) {
		StringBundler sb = new StringBundler(5);

		sb.append(HtmlUtil.escape(commerceAddress.getCity()));
		sb.append(StringPool.COMMA_AND_SPACE);

		try {
			Region region = commerceAddress.getRegion();

			if (region != null) {
				sb.append(HtmlUtil.escape(region.getName()));
				sb.append(StringPool.COMMA_AND_SPACE);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		sb.append(HtmlUtil.escape(commerceAddress.getZip()));

		return sb.toString();
	}

	public String getDescriptiveStreetAddress(CommerceAddress commerceAddress) {
		StringBundler sb = new StringBundler(6);

		sb.append(HtmlUtil.escape(commerceAddress.getStreet1()));
		sb.append(StringPool.COMMA_AND_SPACE);

		if (!Validator.isBlank(commerceAddress.getStreet2())) {
			sb.append(commerceAddress.getStreet2());
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		if (!Validator.isBlank(commerceAddress.getStreet3())) {
			sb.append(commerceAddress.getStreet3());
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		return sb.toString();
	}

	public String getFormattedValue(BigDecimal value) throws PortalException {
		return _commercePriceFormatter.format(
			value, _commerceOrderRequestHelper.getLocale());
	}

	public List<HeaderActionModel> getHeaderActionModels()
		throws PortalException {

		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		CommerceOrderStatus currentCommerceOrderStatus =
			_commerceOrderEngine.getCurrentCommerceOrderStatus(_commerceOrder);

		if ((_commerceOrder == null) || (currentCommerceOrderStatus == null) ||
			!currentCommerceOrderStatus.isComplete(_commerceOrder) ||
			(currentCommerceOrderStatus.getKey() ==
				CommerceOrderConstants.ORDER_STATUS_CANCELLED) ||
			(currentCommerceOrderStatus.getKey() ==
				CommerceOrderConstants.ORDER_STATUS_IN_PROGRESS)) {

			return headerActionModels;
		}

		PortletURL portletURL = getTransitionOrderPortletURL();

		if (currentCommerceOrderStatus.getKey() ==
				CommerceOrderConstants.ORDER_STATUS_PARTIALLY_SHIPPED) {

			headerActionModels.add(
				new HeaderActionModel(
					"btn-primary", null, portletURL.toString(), null,
					"create-shipment"));
		}

		List<CommerceOrderStatus> commerceOrderStatuses =
			_commerceOrderEngine.getNextCommerceOrderStatuses(_commerceOrder);

		for (CommerceOrderStatus commerceOrderStatus : commerceOrderStatuses) {
			if ((commerceOrderStatus.getKey() ==
					CommerceOrderConstants.ORDER_STATUS_SHIPPED) ||
				!commerceOrderStatus.isValidForOrder(_commerceOrder) ||
				!commerceOrderStatus.isTransitionCriteriaMet(_commerceOrder)) {

				continue;
			}

			String label = CommerceOrderConstants.getOrderStatusLabel(
				commerceOrderStatus.getKey());

			if (commerceOrderStatus.getKey() ==
					CommerceOrderConstants.ORDER_STATUS_PARTIALLY_SHIPPED) {

				label = "create-shipment";
			}
			else if (commerceOrderStatus.getKey() ==
						CommerceOrderConstants.ORDER_STATUS_IN_PROGRESS) {

				label = "check-out";

				if (!_commerceOrder.isApproved()) {
					label = "submit";
				}
			}
			else if (commerceOrderStatus.getKey() ==
						CommerceOrderConstants.ORDER_STATUS_PROCESSING) {

				label = "accept-order";
			}
			else if (commerceOrderStatus.getKey() ==
						CommerceOrderConstants.ORDER_STATUS_CANCELLED) {

				label = "cancel";
			}
			else if (commerceOrderStatus.getKey() ==
						CommerceOrderConstants.ORDER_STATUS_ON_HOLD) {

				if (currentCommerceOrderStatus.getKey() ==
						CommerceOrderConstants.ORDER_STATUS_ON_HOLD) {

					label = "release-hold";
				}
				else {
					label = "hold";
				}
			}

			if (Validator.isNull(label)) {
				label = commerceOrderStatus.getLabel(
					_commerceOrderRequestHelper.getLocale());
			}

			String buttonCssClass = "btn-primary";

			if (commerceOrderStatus.getPriority() ==
					CommerceOrderConstants.ORDER_STATUS_ANY) {

				buttonCssClass = "btn-secondary";
			}

			int key = commerceOrderStatus.getKey();

			if (currentCommerceOrderStatus.getKey() ==
					CommerceOrderConstants.ORDER_STATUS_ON_HOLD) {

				key = CommerceOrderConstants.ORDER_STATUS_PROCESSING;
			}

			portletURL.setParameter("transitionName", String.valueOf(key));

			headerActionModels.add(
				new HeaderActionModel(
					buttonCssClass, null, portletURL.toString(), null, label));
		}

		return headerActionModels;
	}

	public List<StepModel> getOrderSteps() throws PortalException {
		return _commerceOrderStepTrackerHelper.getCommerceOrderSteps(
			true, _commerceOrder, _commerceOrderRequestHelper.getLocale());
	}

	public List<CommerceTermEntry> getPaymentTermsEntries() {
		return _commerceTermEntryLocalService.getCommerceTermEntries(
			_commerceOrder.getCompanyId(),
			CommerceTermEntryConstants.TYPE_PAYMENT_TERMS);
	}

	public String getQuantity(CommerceOrderItem commerceOrderItem)
		throws PortalException {

		return _commerceOrderItemQuantityFormatter.format(
			commerceOrderItem, _commerceOrderRequestHelper.getLocale());
	}

	public PortletURL getTransitionOrderPortletURL() {
		return PortletURLBuilder.createActionURL(
			_commerceOrderRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/commerce_order/edit_commerce_order"
		).setCMD(
			"transition"
		).setRedirect(
			_commerceOrderRequestHelper.getCurrentURL()
		).setParameter(
			"commerceOrderId", _commerceOrder.getCommerceOrderId()
		).buildPortletURL();
	}

	public boolean hasManageCommerceOrderDeliveryTermsPermission() {
		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

		return _commerceOrderPortletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_DELIVERY_TERMS);
	}

	public boolean hasManageCommerceOrderPaymentMethodsPermission() {
		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

		return _commerceOrderPortletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_METHODS);
	}

	public boolean hasManageCommerceOrderPaymentStatusesPermission() {
		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

		return _commerceOrderPortletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES);
	}

	public boolean hasManageCommerceOrderPaymentTermsPermission() {
		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

		return _commerceOrderPortletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_TERMS);
	}

	public boolean hasManageCommerceOrderPricesPermission() {
		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

		return _commerceOrderPortletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PRICES);
	}

	public boolean hasModelPermission(
			CommerceOrder commerceOrder, String actionId)
		throws PortalException {

		ThemeDisplay themeDisplay =
			_commerceOrderRequestHelper.getThemeDisplay();

		return _modelResourcePermission.contains(
			themeDisplay.getPermissionChecker(), commerceOrder, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderEditDisplayContext.class);

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final CommerceNotificationQueueEntryLocalService
		_commerceNotificationQueueEntryLocalService;
	private final CommerceOrder _commerceOrder;
	private final Format _commerceOrderDateTimeFormat;
	private final CommerceOrderEngine _commerceOrderEngine;
	private CommerceOrderItem _commerceOrderItem;
	private final CommerceOrderItemDecimalQuantityConfiguration
		_commerceOrderItemDecimalQuantityConfiguration;
	private final CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;
	private final CommerceOrderItemService _commerceOrderItemService;
	private final CommerceOrderNoteService _commerceOrderNoteService;
	private final PortletResourcePermission
		_commerceOrderPortletResourcePermission;
	private final CommerceOrderRequestHelper _commerceOrderRequestHelper;
	private final CommerceOrderService _commerceOrderService;
	private final CommerceOrderStatusRegistry _commerceOrderStatusRegistry;
	private final CommerceOrderStepTrackerHelper
		_commerceOrderStepTrackerHelper;
	private final CommerceOrderTypeService _commerceOrderTypeService;
	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;
	private final CommercePriceFormatter _commercePriceFormatter;
	private CommerceShipment _commerceShipment;
	private final CommerceShipmentService _commerceShipmentService;
	private final CommerceTermEntryLocalService _commerceTermEntryLocalService;
	private final CPMeasurementUnitService _cpMeasurementUnitService;
	private final ModelResourcePermission<CommerceOrder>
		_modelResourcePermission;

}