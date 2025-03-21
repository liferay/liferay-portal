/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.portlet.action;

import com.liferay.commerce.constants.CommerceSubscriptionEntryConstants;
import com.liferay.commerce.exception.CommerceSubscriptionEntryNextIterationDateException;
import com.liferay.commerce.exception.CommerceSubscriptionEntrySubscriptionStatusException;
import com.liferay.commerce.exception.CommerceSubscriptionTypeException;
import com.liferay.commerce.exception.NoSuchSubscriptionEntryException;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.payment.engine.CommerceSubscriptionEngine;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.service.CommerceSubscriptionEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Calendar;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_SUBSCRIPTION_ENTRY,
		"mvc.command.name=/commerce_subscription_entry/edit_commerce_subscription_entry"
	},
	service = MVCActionCommand.class
)
public class EditCommerceSubscriptionEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long commerceSubscriptionEntryId = ParamUtil.getLong(
			actionRequest, "commerceSubscriptionEntryId");

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceSubscriptionEntries(
					commerceSubscriptionEntryId, actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateCommerceSubscriptionEntry(
					commerceSubscriptionEntryId, actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CommerceSubscriptionEntryNextIterationDateException ||
				exception instanceof
					CommerceSubscriptionEntrySubscriptionStatusException ||
				exception instanceof CommerceSubscriptionTypeException) {

				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/commerce_subscription_entry" +
						"/edit_commerce_subscription_entry");
			}
			else if (exception instanceof NoSuchSubscriptionEntryException ||
					 exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommerceSubscriptionEntries(
			long commerceSubscriptionEntryId, ActionRequest actionRequest)
		throws Exception {

		long[] deleteCommerceSubscriptionEntryIds = null;

		if (commerceSubscriptionEntryId > 0) {
			deleteCommerceSubscriptionEntryIds = new long[] {
				commerceSubscriptionEntryId
			};
		}
		else {
			deleteCommerceSubscriptionEntryIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommerceSubscriptionEntryIds"),
				0L);
		}

		for (long deleteCommerceSubscriptionEntryId :
				deleteCommerceSubscriptionEntryIds) {

			_commerceSubscriptionEntryService.deleteCommerceSubscriptionEntry(
				deleteCommerceSubscriptionEntryId);
		}
	}

	private void _transitionDeliverySubscription(
			CommerceSubscriptionEntry commerceSubscriptionEntry,
			int deliverySubscriptionStatus)
		throws Exception {

		try {
			if ((commerceSubscriptionEntry != null) &&
				(commerceSubscriptionEntry.getDeliverySubscriptionStatus() !=
					deliverySubscriptionStatus)) {

				if (deliverySubscriptionStatus ==
						CommerceSubscriptionEntryConstants.
							SUBSCRIPTION_STATUS_ACTIVE) {

					_commerceSubscriptionEngine.activateRecurringDelivery(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
				}
				else if (deliverySubscriptionStatus ==
							CommerceSubscriptionEntryConstants.
								SUBSCRIPTION_STATUS_CANCELLED) {

					_commerceSubscriptionEngine.cancelRecurringDelivery(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
				}
				else if (deliverySubscriptionStatus ==
							CommerceSubscriptionEntryConstants.
								SUBSCRIPTION_STATUS_SUSPENDED) {

					_commerceSubscriptionEngine.suspendRecurringDelivery(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
				}
			}
		}
		catch (Exception exception) {
			throw new CommerceSubscriptionEntrySubscriptionStatusException(
				exception);
		}
	}

	private void _transitionPaymentSubscription(
			CommerceSubscriptionEntry commerceSubscriptionEntry,
			int paymentSubscriptionStatus)
		throws Exception {

		try {
			if ((commerceSubscriptionEntry != null) &&
				!Objects.equals(
					commerceSubscriptionEntry.getSubscriptionStatus(),
					paymentSubscriptionStatus)) {

				if ((Objects.equals(
						commerceSubscriptionEntry.getSubscriptionStatus(),
						CommerceSubscriptionEntryConstants.
							SUBSCRIPTION_STATUS_ACTIVE) ||
					 Objects.equals(
						 commerceSubscriptionEntry.getSubscriptionStatus(),
						 CommerceSubscriptionEntryConstants.
							 SUBSCRIPTION_STATUS_CANCELLED) ||
					 Objects.equals(
						 commerceSubscriptionEntry.getSubscriptionStatus(),
						 CommerceSubscriptionEntryConstants.
							 SUBSCRIPTION_STATUS_SUSPENDED)) &&
					Objects.equals(
						paymentSubscriptionStatus,
						CommerceSubscriptionEntryConstants.
							SUBSCRIPTION_STATUS_INACTIVE)) {

					throw new CommerceSubscriptionEntrySubscriptionStatusException();
				}
				else if (Objects.equals(
							commerceSubscriptionEntry.getSubscriptionStatus(),
							CommerceSubscriptionEntryConstants.
								SUBSCRIPTION_STATUS_CANCELLED)) {

					throw new CommerceSubscriptionEntrySubscriptionStatusException();
				}
				else if (Objects.equals(
							paymentSubscriptionStatus,
							CommerceSubscriptionEntryConstants.
								SUBSCRIPTION_STATUS_ACTIVE)) {

					_commerceSubscriptionEngine.activateRecurringPayment(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
				}
				else if (Objects.equals(
							paymentSubscriptionStatus,
							CommerceSubscriptionEntryConstants.
								SUBSCRIPTION_STATUS_CANCELLED)) {

					_commerceSubscriptionEngine.cancelRecurringPayment(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
				}
				else if (Objects.equals(
							paymentSubscriptionStatus,
							CommerceSubscriptionEntryConstants.
								SUBSCRIPTION_STATUS_SUSPENDED)) {

					_commerceSubscriptionEngine.suspendRecurringPayment(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
				}
			}
		}
		catch (Exception exception) {
			throw new CommerceSubscriptionEntrySubscriptionStatusException(
				exception);
		}
	}

	private CommerceSubscriptionEntry _updateCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId, ActionRequest actionRequest)
		throws Exception {

		int subscriptionLength = ParamUtil.getInteger(
			actionRequest, "subscriptionLength");
		String subscriptionType = ParamUtil.getString(
			actionRequest, "subscriptionType");
		UnicodeProperties subscriptionTypeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "subscriptionTypeSettings--");
		long maxSubscriptionCycles = ParamUtil.getLong(
			actionRequest, "maxSubscriptionCycles");
		int subscriptionStatus = ParamUtil.getInteger(
			actionRequest, "subscriptionStatus");

		int nextIterationDateMonth = ParamUtil.getInteger(
			actionRequest, "nextIterationDateMonth");
		int nextIterationDateDay = ParamUtil.getInteger(
			actionRequest, "nextIterationDateDay");
		int nextIterationDateYear = ParamUtil.getInteger(
			actionRequest, "nextIterationDateYear");
		int nextIterationDateHour = ParamUtil.getInteger(
			actionRequest, "nextIterationDateHour");
		int nextIterationDateMinute = ParamUtil.getInteger(
			actionRequest, "nextIterationDateMinute");
		int nextIterationDateAmPm = ParamUtil.getInteger(
			actionRequest, "nextIterationDateAmPm");

		if (nextIterationDateAmPm == Calendar.PM) {
			nextIterationDateHour += 12;
		}

		int deliverySubscriptionLength = ParamUtil.getInteger(
			actionRequest, "deliverySubscriptionLength");
		String deliverySubscriptionType = ParamUtil.getString(
			actionRequest, "deliverySubscriptionType");
		UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "deliverySubscriptionTypeSettings--");
		long deliveryMaxSubscriptionCycles = ParamUtil.getLong(
			actionRequest, "deliveryMaxSubscriptionCycles");
		int deliverySubscriptionStatus = ParamUtil.getInteger(
			actionRequest, "deliverySubscriptionStatus");

		int deliveryNextIterationDateMonth = ParamUtil.getInteger(
			actionRequest, "deliveryNextIterationDateMonth");
		int deliveryNextIterationDateDay = ParamUtil.getInteger(
			actionRequest, "deliveryNextIterationDateDay");
		int deliveryNextIterationDateYear = ParamUtil.getInteger(
			actionRequest, "deliveryNextIterationDateYear");
		int deliveryNextIterationDateHour = ParamUtil.getInteger(
			actionRequest, "deliveryNextIterationDateHour");
		int deliveryNextIterationDateMinute = ParamUtil.getInteger(
			actionRequest, "deliveryNextIterationDateMinute");
		int deliveryNextIterationDateAmPm = ParamUtil.getInteger(
			actionRequest, "deliveryNextIterationDateAmPm");

		if (deliveryNextIterationDateAmPm == Calendar.PM) {
			deliveryNextIterationDateHour += 12;
		}

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_commerceSubscriptionEntryService.fetchCommerceSubscriptionEntry(
				commerceSubscriptionEntryId);

		_transitionPaymentSubscription(
			commerceSubscriptionEntry, subscriptionStatus);

		_transitionDeliverySubscription(
			commerceSubscriptionEntry, deliverySubscriptionStatus);

		return _commerceSubscriptionEntryService.
			updateCommerceSubscriptionEntry(
				commerceSubscriptionEntryId, subscriptionLength,
				subscriptionType, subscriptionTypeSettingsUnicodeProperties,
				maxSubscriptionCycles, subscriptionStatus,
				nextIterationDateMonth, nextIterationDateDay,
				nextIterationDateYear, nextIterationDateHour,
				nextIterationDateMinute, deliverySubscriptionLength,
				deliverySubscriptionType,
				deliverySubscriptionTypeSettingsUnicodeProperties,
				deliveryMaxSubscriptionCycles, deliverySubscriptionStatus,
				deliveryNextIterationDateMonth, deliveryNextIterationDateDay,
				deliveryNextIterationDateYear, deliveryNextIterationDateHour,
				deliveryNextIterationDateMinute);
	}

	@Reference
	private CommerceSubscriptionEngine _commerceSubscriptionEngine;

	@Reference
	private CommerceSubscriptionEntryService _commerceSubscriptionEntryService;

}