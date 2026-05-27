/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.web.internal.frontend.data.set.filter.AccountEntryValidatorClassNameSelectionFDSFilter;
import com.liferay.commerce.order.web.internal.frontend.data.set.filter.AccountEntryValidatorResultSelectionFDSFilter;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/view_commerce_order_account_validations"
	},
	service = MVCRenderCommand.class
)
public class ViewCommerceOrderAccountValidationsMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CommerceOrder commerceOrder =
				_commerceOrderService.getCommerceOrder(
					ParamUtil.getLong(renderRequest, "commerceOrderId"));

			AccountEntry accountEntry = commerceOrder.getAccountEntry();

			List<String> validatorClauses = new ArrayList<>();

			JSONObject jsonObject = JSONUtil.put(
				"billingAddressId", commerceOrder.getBillingAddressId()
			).put(
				"commerceOrderId", commerceOrder.getCommerceOrderId()
			).put(
				"shippingAddressId", commerceOrder.getShippingAddressId()
			);

			if (accountEntry != null) {
				for (AccountEntryValidator accountEntryValidator :
						_accountEntryValidatorRegistry.
							getAccountEntryValidators()) {

					AccountEntryValidatorConfiguration
						accountEntryValidatorConfiguration =
							accountEntryValidator.getConfiguration(
								accountEntry.getCompanyId());

					if (!accountEntryValidatorConfiguration.enabled()) {
						continue;
					}

					validatorClauses.add(
						StringBundler.concat(
							"((className eq '",
							accountEntryValidator.getClass(
							).getName(),
							"') and (classPK eq '",
							accountEntryValidator.getKey(
								accountEntry, jsonObject),
							"'))"));
				}
			}

			String filterString = "(className eq '')";

			if (!validatorClauses.isEmpty()) {
				filterString = StringBundler.concat(
					"(", StringUtil.merge(validatorClauses, " or "), ") and ",
					"(r_accountToAccountValidatorResults_accountEntryId eq '",
					accountEntry.getAccountEntryId(), "')");
			}

			renderRequest.setAttribute(
				"accountValidationsAPIURL",
				"/o/account/validator-results?filter=" +
					URLCodec.encodeURL(filterString) +
						"&sort=dateCreated:desc");

			renderRequest.setAttribute(
				"accountValidationsFDSFilters",
				ListUtil.<FDSFilter>fromArray(
					new AccountEntryValidatorClassNameSelectionFDSFilter(
						_accountEntryValidatorRegistry.
							getAccountEntryValidators()),
					new AccountEntryValidatorResultSelectionFDSFilter()));

			renderRequest.setAttribute(
				"showManualValidationForm",
				!_accountEntryValidatorRegistry.isLastResultSuccess(
					accountEntry, jsonObject));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/commerce_order/account_validations.jsp";
	}

	@Reference
	private AccountEntryValidatorRegistry _accountEntryValidatorRegistry;

	@Reference
	private CommerceOrderService _commerceOrderService;

}