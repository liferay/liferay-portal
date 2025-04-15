/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.portlet.action;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderWebKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.exception.CommerceOrderAccountLimitException;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItemModel;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderNoteLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT_MINI,
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT_TOTAL,
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER_CONTENT,
		"mvc.command.name=/commerce_open_order_content/edit_commerce_order"
	},
	service = MVCActionCommand.class
)
public class EditCommerceOrderMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD)) {
				CommerceOrder commerceOrder = _addCommerceOrder(actionRequest);

				sendRedirect(
					actionRequest, actionResponse,
					_getOrderDetailRedirect(commerceOrder, actionRequest));
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceOrders(actionRequest);

				String orderDetailURL = ParamUtil.getString(
					actionRequest, "orderDetailURL");

				if (Validator.isNotNull(orderDetailURL)) {
					sendRedirect(actionRequest, actionResponse, orderDetailURL);

					return;
				}

				PortletURL openOrdersPortletURL =
					PortletProviderUtil.getPortletURL(
						actionRequest, CommerceOrder.class.getName(),
						PortletProvider.Action.EDIT);

				sendRedirect(
					actionRequest, actionResponse,
					openOrdersPortletURL.toString());
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateCommerceOrder(actionRequest);
			}
			else if (cmd.equals("addBillingAddress")) {
				_addBillingAddress(actionRequest);
			}
			else if (cmd.equals("addShippingAddress")) {
				_addShippingAddress(actionRequest);
			}
			else if (cmd.equals("name")) {
				_updateName(actionRequest);
			}
			else if (cmd.equals("purchaseOrderNumber")) {
				_updatePurchaseOrderNumber(actionRequest);
			}
			else if (cmd.equals("requestedDeliveryDate")) {
				_updateRequestedDeliveryDate(actionRequest);
			}
			else if (cmd.equals("reorder")) {
				CommerceOrder commerceOrder = _reorderCommerceOrder(
					actionRequest);

				_submitCommerceOrder(
					actionRequest, actionResponse, commerceOrder);
			}
			else if (cmd.equals("requestQuote")) {
				_requestQuote(actionRequest);
			}
			else if (cmd.equals("processQuote")) {
				_processQuote(actionRequest);
			}
			else if (cmd.equals("selectBillingAddress")) {
				_selectBillingAddress(actionRequest);
			}
			else if (cmd.equals("selectShippingAddress")) {
				_selectShippingAddress(actionRequest);
			}
			else if (cmd.equals("setCurrent")) {
				long commerceOrderId = ParamUtil.getLong(
					actionRequest, "commerceOrderId");

				setCurrentCommerceOrder(actionRequest, commerceOrderId);

				hideDefaultSuccessMessage(actionRequest);

				HttpServletRequest httpServletRequest =
					_portal.getOriginalServletRequest(
						_portal.getHttpServletRequest(actionRequest));

				boolean skipRedirect = ParamUtil.getBoolean(
					httpServletRequest, "skipRedirect");

				if (skipRedirect) {
					return;
				}

				sendRedirect(
					actionRequest, actionResponse,
					PortletURLBuilder.create(
						PortletProviderUtil.getPortletURL(
							actionRequest, CommerceOrder.class.getName(),
							PortletProvider.Action.EDIT)
					).setMVCRenderCommandName(
						"/commerce_open_order_content/edit_commerce_order"
					).setBackURL(
						ParamUtil.getString(actionRequest, "redirect")
					).setParameter(
						"commerceOrderId", commerceOrderId
					).buildString());
			}
			else if (cmd.equals("transition")) {
				_executeTransition(actionRequest);
			}
			else if (cmd.equals("updateBillingAddress")) {
				_updateBillingAddress(actionRequest);
			}
			else if (cmd.equals("updateShippingAddress")) {
				_updateShippingAddress(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof NoSuchOrderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CommerceOrderValidatorException) {
				CommerceOrderValidatorException
					commerceOrderValidatorException =
						(CommerceOrderValidatorException)exception;

				SessionErrors.add(
					actionRequest, commerceOrderValidatorException.getClass(),
					commerceOrderValidatorException);

				hideDefaultErrorMessage(actionRequest);
			}
			else {
				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
	}

	protected void setCurrentCommerceOrder(
			ActionRequest actionRequest, long commerceOrderId)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		httpServletRequest.setAttribute(
			CommerceOrderWebKeys.MERGE_GUEST_ORDER, Boolean.FALSE);

		CommerceOrder currentCommerceOrder =
			_commerceOrderHttpHelper.getCurrentCommerceOrder(
				httpServletRequest);

		if ((currentCommerceOrder == null) ||
			(commerceOrderId != currentCommerceOrder.getCommerceOrderId())) {

			_commerceOrderHttpHelper.setCurrentCommerceOrder(
				_portal.getHttpServletRequest(actionRequest),
				_commerceOrderService.getCommerceOrder(commerceOrderId));
		}
	}

	private void _addBillingAddress(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		CommerceAddress commerceAddress =
			_commerceAddressService.addCommerceAddress(
				StringPool.BLANK, AccountEntry.class.getName(),
				commerceOrder.getCommerceAccountId(),
				ParamUtil.getLong(actionRequest, "countryId"),
				ParamUtil.getLong(actionRequest, "regionId"),
				ParamUtil.getString(actionRequest, "city"),
				ParamUtil.getString(actionRequest, "description"),
				ParamUtil.getString(actionRequest, "name"),
				ParamUtil.getString(actionRequest, "phoneNumber"),
				ParamUtil.getString(actionRequest, "street1"),
				ParamUtil.getString(actionRequest, "street2"),
				ParamUtil.getString(actionRequest, "street3"), StringPool.BLANK,
				CommerceAddressConstants.ADDRESS_TYPE_BILLING,
				ParamUtil.getString(actionRequest, "zip"),
				ServiceContextFactory.getInstance(
					CommerceAddress.class.getName(), actionRequest));

		_commerceOrderService.updateBillingAddress(
			commerceOrder.getCommerceOrderId(),
			commerceAddress.getCommerceAddressId());
	}

	private CommerceOrder _addCommerceOrder(ActionRequest actionRequest)
		throws Exception {

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry == null) {
			throw new NoSuchEntryException();
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String commerceCurrencyCode = null;

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		if (commerceCurrency != null) {
			commerceCurrencyCode = commerceCurrency.getCode();
		}

		long commerceChannelGroupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				themeDisplay.getScopeGroupId());

		long commerceOrderTypeId = ParamUtil.getLong(
			actionRequest, "commerceOrderTypeId");

		if (commerceOrderTypeId == 0) {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByGroupId(
					commerceChannelGroupId);

			List<CommerceOrderType> commerceOrderTypes =
				_commerceOrderTypeService.getCommerceOrderTypes(
					CommerceChannel.class.getName(),
					commerceChannel.getCommerceChannelId(), true,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			if (!commerceOrderTypes.isEmpty()) {
				CommerceOrderType commerceOrderType = commerceOrderTypes.get(0);

				commerceOrderTypeId =
					commerceOrderType.getCommerceOrderTypeId();
			}
		}

		try {
			return _commerceOrderService.addCommerceOrder(
				commerceChannelGroupId, accountEntry.getAccountEntryId(),
				commerceCurrencyCode, commerceOrderTypeId);
		}
		catch (Exception exception) {
			if (exception instanceof CommerceOrderAccountLimitException) {
				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				return null;
			}

			throw exception;
		}
	}

	private void _addShippingAddress(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		CommerceAddress commerceAddress =
			_commerceAddressService.addCommerceAddress(
				StringPool.BLANK, AccountEntry.class.getName(),
				commerceOrder.getCommerceAccountId(),
				ParamUtil.getLong(actionRequest, "countryId"),
				ParamUtil.getLong(actionRequest, "regionId"),
				ParamUtil.getString(actionRequest, "city"),
				ParamUtil.getString(actionRequest, "description"),
				ParamUtil.getString(actionRequest, "name"),
				ParamUtil.getString(actionRequest, "phoneNumber"),
				ParamUtil.getString(actionRequest, "street1"),
				ParamUtil.getString(actionRequest, "street2"),
				ParamUtil.getString(actionRequest, "street3"), StringPool.BLANK,
				CommerceAddressConstants.ADDRESS_TYPE_SHIPPING,
				ParamUtil.getString(actionRequest, "zip"),
				ServiceContextFactory.getInstance(
					CommerceAddress.class.getName(), actionRequest));

		_commerceOrderService.updateShippingAddress(
			commerceOrder.getCommerceOrderId(),
			commerceAddress.getCommerceAddressId());
	}

	private void _checkoutCommerceOrder(
			ActionRequest actionRequest, long commerceOrderId)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		if (ListUtil.exists(
				commerceOrder.getCommerceOrderItems(),
				CommerceOrderItemModel::isPriceOnApplication)) {

			actionRequest.setAttribute(
				WebKeys.REDIRECT,
				_commerceOrderHttpHelper.getCommerceCartPortletURL(
					_portal.getHttpServletRequest(actionRequest),
					commerceOrder));

			return;
		}

		_commerceAccountHelper.setCurrentCommerceAccount(
			_portal.getHttpServletRequest(actionRequest),
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				_portal.getScopeGroupId(actionRequest)),
			commerceOrder.getCommerceAccountId());

		actionRequest.setAttribute(
			WebKeys.REDIRECT,
			PortletURLBuilder.create(
				_commerceOrderHttpHelper.getCommerceCheckoutPortletURL(
					_portal.getHttpServletRequest(actionRequest))
			).setParameter(
				"commerceOrderId", commerceOrderId
			).buildString());
	}

	private void _deleteCommerceOrders(ActionRequest actionRequest)
		throws Exception {

		long[] deleteCommerceOrderIds = null;

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		if (commerceOrderId > 0) {
			deleteCommerceOrderIds = new long[] {commerceOrderId};
		}
		else {
			deleteCommerceOrderIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "deleteCommerceOrderIds"),
				0L);
		}

		for (long deleteCommerceOrderId : deleteCommerceOrderIds) {
			_commerceOrderHttpHelper.deleteCommerceOrder(
				actionRequest, deleteCommerceOrderId);
		}
	}

	private void _executeTransition(ActionRequest actionRequest)
		throws Exception {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		long workflowTaskId = ParamUtil.getLong(
			actionRequest, "workflowTaskId");
		String transitionName = ParamUtil.getString(
			actionRequest, "transitionName");

		if (workflowTaskId > 0) {
			_executeWorkflowTransition(
				actionRequest, commerceOrderId, transitionName, workflowTaskId);
		}
		else if (transitionName.equals("checkout")) {
			_checkoutCommerceOrder(actionRequest, commerceOrderId);
		}
		else {
			CommerceOrder commerceOrder =
				_commerceOrderService.getCommerceOrder(commerceOrderId);

			int orderStatus = GetterUtil.getInteger(
				transitionName, commerceOrder.getOrderStatus());

			if (transitionName.equals("submit")) {
				orderStatus = CommerceOrderConstants.ORDER_STATUS_IN_PROGRESS;
			}

			_commerceOrderEngine.transitionCommerceOrder(
				commerceOrder, orderStatus, _portal.getUserId(actionRequest),
				true);
		}

		hideDefaultSuccessMessage(actionRequest);
	}

	private void _executeWorkflowTransition(
			ActionRequest actionRequest, long commerceOrderId,
			String transitionName, long workflowTaskId)
		throws Exception {

		String comment = ParamUtil.getString(actionRequest, "comment");

		_commerceOrderService.executeWorkflowTransition(
			commerceOrderId, workflowTaskId, transitionName, comment);
	}

	private String _getOrderDetailRedirect(
			CommerceOrder commerceOrder, ActionRequest actionRequest)
		throws PortalException {

		return _commerceOrderHttpHelper.getCommerceCartPortletURL(
			_portal.getHttpServletRequest(actionRequest), commerceOrder);
	}

	private void _processQuote(ActionRequest actionRequest) throws Exception {
		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		_commerceOrderEngine.transitionCommerceOrder(
			_commerceOrderService.getCommerceOrder(commerceOrderId),
			CommerceOrderConstants.ORDER_STATUS_QUOTE_PROCESSED,
			_portal.getUserId(actionRequest), true);
	}

	private CommerceOrder _reorderCommerceOrder(ActionRequest actionRequest)
		throws Exception {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceOrder commerceOrder =
			_commerceOrderService.reorderCommerceOrder(
				commerceOrderId, commerceContext);

		_commerceAccountHelper.setCurrentCommerceAccount(
			_portal.getHttpServletRequest(actionRequest),
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				_portal.getScopeGroupId(actionRequest)),
			commerceOrder.getCommerceAccountId());
		_commerceOrderHttpHelper.setCurrentCommerceOrder(
			_portal.getHttpServletRequest(actionRequest), commerceOrder);

		return commerceOrder;
	}

	private void _requestQuote(ActionRequest actionRequest) throws Exception {
		_updateCommerceOrderNote(actionRequest);

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		if (commerceOrder.isGuestOrder()) {
			String emailAddress = ParamUtil.getString(
				actionRequest, "emailAddress");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CommerceOrder.class.getName(), actionRequest);

			AccountEntry accountEntry =
				_accountEntryLocalService.addAccountEntry(
					serviceContext.getUserId(),
					AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
					emailAddress, null, null, emailAddress, null, null,
					AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
					WorkflowConstants.STATUS_APPROVED, serviceContext);

			commerceOrder.setCommerceAccountId(
				accountEntry.getAccountEntryId());

			commerceOrder = _commerceOrderService.updateCommerceOrder(
				commerceOrder);
		}

		_commerceOrderEngine.transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_QUOTE_REQUESTED,
			_portal.getUserId(actionRequest), true);

		actionRequest.setAttribute(
			WebKeys.REDIRECT,
			_commerceOrderHttpHelper.getCommerceCartPortletURL(
				_portal.getHttpServletRequest(actionRequest), commerceOrder));
	}

	private void _selectBillingAddress(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		long addressId = ParamUtil.getLong(actionRequest, "addressId");

		_commerceOrderService.updateBillingAddress(commerceOrderId, addressId);
	}

	private void _selectShippingAddress(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		long addressId = ParamUtil.getLong(actionRequest, "addressId");

		_commerceOrderService.updateShippingAddress(commerceOrderId, addressId);
	}

	private void _submitCommerceOrder(
			ActionRequest actionRequest, ActionResponse actionResponse,
			CommerceOrder commerceOrder)
		throws Exception {

		String redirect = null;

		String orderDetailRedirect = _getOrderDetailRedirect(
			commerceOrder, actionRequest);

		if (Validator.isNotNull(orderDetailRedirect)) {
			redirect = orderDetailRedirect;
		}
		else {
			long plid = _portal.getPlidFromPortletId(
				_portal.getScopeGroupId(
					_portal.getLiferayPortletRequest(actionRequest)),
				CommercePortletKeys.COMMERCE_CART_CONTENT);

			LiferayPortletResponse liferayPortletResponse =
				_portal.getLiferayPortletResponse(actionResponse);

			if (plid > 0) {
				redirect = PortletURLBuilder.createLiferayPortletURL(
					liferayPortletResponse, plid,
					CommercePortletKeys.COMMERCE_CART_CONTENT,
					PortletRequest.RENDER_PHASE
				).setParameter(
					"commerceOrderId", commerceOrder.getCommerceOrderId()
				).buildString();
			}
			else {
				redirect = PortletURLBuilder.createLiferayPortletURL(
					liferayPortletResponse,
					CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
					PortletRequest.RENDER_PHASE
				).buildString();
			}
		}

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	private void _updateBillingAddress(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		_commerceOrderService.updateBillingAddress(
			commerceOrderId, ParamUtil.getLong(actionRequest, "countryId"),
			ParamUtil.getLong(actionRequest, "regionId"),
			ParamUtil.getString(actionRequest, "city"),
			ParamUtil.getString(actionRequest, "description"),
			ParamUtil.getString(actionRequest, "name"),
			ParamUtil.getString(actionRequest, "street1"),
			ParamUtil.getString(actionRequest, "street2"),
			ParamUtil.getString(actionRequest, "street3"),
			ParamUtil.getString(actionRequest, "subtype"),
			ParamUtil.getString(actionRequest, "phoneNumber"),
			ParamUtil.getString(actionRequest, "zip"),
			ServiceContextFactory.getInstance(
				CommerceOrder.class.getName(), actionRequest));
	}

	private void _updateCommerceOrder(ActionRequest actionRequest)
		throws Exception {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		long billingAddressId = ParamUtil.getLong(
			actionRequest, "billingAddressId");
		String name = ParamUtil.getString(actionRequest, "name");
		long shippingAddressId = ParamUtil.getLong(
			actionRequest, "shippingAddressId");
		String purchaseOrderNumber = ParamUtil.getString(
			actionRequest, "purchaseOrderNumber");

		_commerceOrderService.updateCommerceOrder(
			commerceOrder.getExternalReferenceCode(), commerceOrderId,
			billingAddressId, commerceOrder.getCommerceShippingMethodId(),
			shippingAddressId, commerceOrder.getAdvanceStatus(),
			commerceOrder.getCommercePaymentMethodKey(), name,
			purchaseOrderNumber, commerceOrder.getShippingAmount(),
			commerceOrder.getShippingOptionName(), commerceOrder.getSubtotal(),
			commerceOrder.getTotal());
	}

	private void _updateCommerceOrderNote(ActionRequest actionRequest)
		throws Exception {

		String content = ParamUtil.getString(actionRequest, "content");

		if (Validator.isNotNull(content)) {
			boolean restricted = ParamUtil.getBoolean(
				actionRequest, "restricted");

			long commerceOrderId = ParamUtil.getLong(
				actionRequest, "commerceOrderId");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CommerceOrderNote.class.getName(), actionRequest);

			_commerceOrderNoteLocalService.addCommerceOrderNote(
				commerceOrderId, content, restricted, serviceContext);
		}
	}

	private void _updateName(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");
		String name = ParamUtil.getString(actionRequest, "name");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		commerceOrder.setName(name);

		_commerceOrderService.updateCommerceOrder(commerceOrder);
	}

	private void _updatePurchaseOrderNumber(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");
		String purchaseOrderNumber = ParamUtil.getString(
			actionRequest, "purchaseOrderNumber");

		_commerceOrderService.updatePurchaseOrderNumber(
			commerceOrderId, purchaseOrderNumber);
	}

	private void _updateRequestedDeliveryDate(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		int requestedDeliveryDateMonth = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateMonth");
		int requestedDeliveryDateDay = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateDay");
		int requestedDeliveryDateYear = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateYear");
		int requestedDeliveryDateHour = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateHour");
		int requestedDeliveryDateMinute = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateMinute");
		int requestedDeliveryDateAmPm = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateAmPm");

		if (requestedDeliveryDateAmPm == Calendar.PM) {
			requestedDeliveryDateHour += 12;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceOrder.class.getName(), actionRequest);

		_commerceOrderService.updateInfo(
			commerceOrder.getCommerceOrderId(), commerceOrder.getPrintedNote(),
			requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear, requestedDeliveryDateHour,
			requestedDeliveryDateMinute, serviceContext);
	}

	private void _updateShippingAddress(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		_commerceOrderService.updateShippingAddress(
			commerceOrderId, ParamUtil.getLong(actionRequest, "countryId"),
			ParamUtil.getLong(actionRequest, "regionId"),
			ParamUtil.getString(actionRequest, "city"),
			ParamUtil.getString(actionRequest, "description"),
			ParamUtil.getString(actionRequest, "name"),
			ParamUtil.getString(actionRequest, "phoneNumber"),
			ParamUtil.getString(actionRequest, "street1"),
			ParamUtil.getString(actionRequest, "street2"),
			ParamUtil.getString(actionRequest, "street3"),
			ParamUtil.getString(actionRequest, "subtype"),
			ParamUtil.getString(actionRequest, "zip"),
			ServiceContextFactory.getInstance(
				CommerceOrder.class.getName(), actionRequest));
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderNoteLocalService _commerceOrderNoteLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private Portal _portal;

}