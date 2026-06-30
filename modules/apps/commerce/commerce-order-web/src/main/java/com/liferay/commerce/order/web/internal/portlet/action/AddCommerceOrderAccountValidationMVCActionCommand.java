/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/add_commerce_order_account_validation"
	},
	service = MVCActionCommand.class
)
public class AddCommerceOrderAccountValidationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			ParamUtil.getLong(actionRequest, "commerceOrderId"));

		AccountEntry accountEntry = commerceOrder.getAccountEntry();
		JSONObject jsonObject = JSONUtil.put(
			"billingAddressId", commerceOrder.getBillingAddressId()
		).put(
			"commerceOrderId", commerceOrder.getCommerceOrderId()
		).put(
			"shippingAddressId", commerceOrder.getShippingAddressId()
		);

		if ((accountEntry == null) ||
			_isLastResultSuccess(
				_accountEntryValidatorRegistry.
					getLastAccountEntryValidatorResultsMap(
						accountEntry, jsonObject))) {

			SessionErrors.add(
				actionRequest, "accountValidationsAlreadySucceeded");

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", accountEntry.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		String validationMessage = HtmlUtil.escape(
			ParamUtil.getString(actionRequest, "validationMessage"));

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (AccountEntryValidator accountEntryValidator :
				_accountEntryValidatorRegistry.getAccountEntryValidators()) {

			AccountEntryValidatorConfiguration
				accountEntryValidatorConfiguration =
					accountEntryValidator.getAccountEntryValidatorConfiguration(
						accountEntry.getCompanyId());

			if (!accountEntryValidatorConfiguration.enabled()) {
				continue;
			}

			Class<? extends AccountEntryValidator> accountEntryValidatorClass =
				accountEntryValidator.getClass();

			_objectEntryLocalService.addObjectEntry(
				0, themeDisplay.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"className", accountEntryValidatorClass.getName()
				).put(
					"classPK",
					accountEntryValidator.getClassPK(accountEntry, jsonObject)
				).put(
					"r_accountToAccountValidatorResults_accountEntryId",
					accountEntry.getAccountEntryId()
				).put(
					"resultMessage", validationMessage
				).put(
					"resultStatus", AccountEntryValidatorConstants.RESULT_MANUAL
				).build(),
				serviceContext);
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	private boolean _isLastResultSuccess(
		Map<String, AccountEntryValidatorResult> accountEntryValidatorResults) {

		for (AccountEntryValidatorResult accountEntryValidatorResult :
				accountEntryValidatorResults.values()) {

			if ((accountEntryValidatorResult == null) ||
				(!Objects.equals(
					AccountEntryValidatorConstants.RESULT_SUCCESS,
					accountEntryValidatorResult.getResultStatus()) &&
				 !Objects.equals(
					 AccountEntryValidatorConstants.RESULT_MANUAL,
					 accountEntryValidatorResult.getResultStatus()))) {

				return false;
			}
		}

		return true;
	}

	@Reference
	private AccountEntryValidatorRegistry _accountEntryValidatorRegistry;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}