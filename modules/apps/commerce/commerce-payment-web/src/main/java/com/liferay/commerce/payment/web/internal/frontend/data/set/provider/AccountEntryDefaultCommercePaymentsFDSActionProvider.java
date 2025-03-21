/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.frontend.data.set.provider;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceAccountActionKeys;
import com.liferay.commerce.payment.web.internal.constants.CommercePaymentMethodGroupRelFDSNames;
import com.liferay.commerce.payment.web.internal.model.PaymentMethod;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "fds.data.provider.key=" + CommercePaymentMethodGroupRelFDSNames.ACCOUNT_ENTRY_DEFAULT_PAYMENTS,
	service = FDSActionProvider.class
)
public class AccountEntryDefaultCommercePaymentsFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		PaymentMethod paymentMethod = (PaymentMethod)model;

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return DropdownItemListBuilder.add(
			() -> _accountEntryModelResourcePermission.contains(
				permissionChecker, paymentMethod.getAccountEntryId(),
				CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS),
			dropdownItem -> {
				dropdownItem.setHref(
					_getAccountEntryDefaultCommercePaymentMethodEditURL(
						paymentMethod.getAccountEntryId(),
						paymentMethod.getCommerceChannelId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, Constants.EDIT));
				dropdownItem.setTarget("modal-lg");
			}
		).build();
	}

	private String _getAccountEntryDefaultCommercePaymentMethodEditURL(
		long accountEntryId, long commerceChannelId,
		HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_payment" +
				"/edit_account_entry_default_commerce_payment_method"
		).setParameter(
			"accountEntryId", accountEntryId
		).setParameter(
			"commerceChannelId", commerceChannelId
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}