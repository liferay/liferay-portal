/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.commerce.checkout.web.internal.util.CommerceOrderUtil;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.util.comparator.CommerceAddressNameComparator;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Andrea Di Giorgi
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
public abstract class BaseAddressCheckoutStepDisplayContext {

	public BaseAddressCheckoutStepDisplayContext(
		AccountEntryLocalService accountEntryLocalService,
		ModelResourcePermission<AccountEntry>
			accountEntryModelResourcePermission,
		AccountRoleLocalService accountRoleLocalService,
		CommerceAddressService commerceAddressService,
		CommerceChannelAccountEntryRelLocalService
			commerceChannelAccountEntryRelLocalService,
		CommerceChannelLocalService commerceChannelLocalService,
		HttpServletRequest httpServletRequest,
		PortletResourcePermission portletResourcePermission) {

		this.accountEntryLocalService = accountEntryLocalService;
		this.accountEntryModelResourcePermission =
			accountEntryModelResourcePermission;
		this.accountRoleLocalService = accountRoleLocalService;
		this.commerceAddressService = commerceAddressService;
		this.commerceChannelAccountEntryRelLocalService =
			commerceChannelAccountEntryRelLocalService;
		this.commerceChannelLocalService = commerceChannelLocalService;
		this.portletResourcePermission = portletResourcePermission;

		_commerceContext = (CommerceContext)httpServletRequest.getAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT);
		_commerceOrder = (CommerceOrder)httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);
	}

	public CommerceAddress getCommerceAddress(long commerceAddressId)
		throws PortalException {

		return commerceAddressService.fetchCommerceAddress(commerceAddressId);
	}

	public List<CommerceAddress> getCommerceAddresses() throws PortalException {
		return commerceAddressService.getCommerceAddressesByCompanyId(
			_commerceOrder.getCompanyId(), AccountEntry.class.getName(),
			_commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS,
			CommerceAddressNameComparator.getInstance(false));
	}

	public long getCommerceAddressId(HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceOrder commerceOrder = getCommerceOrder();

		if (commerceOrder.isGuestOrder()) {
			return 0;
		}

		long commerceAddressId = BeanParamUtil.getLong(
			commerceOrder, httpServletRequest, getParamName());

		CommerceAddress orderCommerceAddress = getCommerceAddress(
			commerceAddressId);

		if ((orderCommerceAddress == null) ||
			(orderCommerceAddress.getClassNameId() != PortalUtil.getClassNameId(
				AccountEntry.class))) {

			CommerceContext commerceContext = getCommerceContext();

			commerceAddressId = getDefaultCommerceAddressId(
				commerceContext.getCommerceChannelId());

			for (CommerceAddress validCommerceAddress :
					getCommerceAddresses()) {

				if (commerceAddressId ==
						validCommerceAddress.getCommerceAddressId()) {

					return commerceAddressId;
				}
			}
		}
		else {
			for (CommerceAddress validCommerceAddress :
					getCommerceAddresses()) {

				if (orderCommerceAddress.isSameAddress(validCommerceAddress)) {
					return validCommerceAddress.getCommerceAddressId();
				}
			}
		}

		return 0;
	}

	public CommerceContext getCommerceContext() {
		return _commerceContext;
	}

	public abstract String getCommerceCountrySelectionColumnName();

	public abstract String getCommerceCountrySelectionMethodName();

	public CommerceOrder getCommerceOrder() {
		return _commerceOrder;
	}

	public abstract long getDefaultCommerceAddressId(long commerceChannelId)
		throws PortalException;

	public abstract String getParamName();

	public abstract String getTitle();

	public boolean hasPermission(
			PermissionChecker permissionChecker, AccountEntry accountEntry,
			String actionId)
		throws PortalException {

		if (accountEntry.isGuestAccount() || accountEntry.isPersonalAccount() ||
			accountEntryModelResourcePermission.contains(
				permissionChecker, accountEntry.getAccountEntryId(),
				actionId)) {

			return true;
		}

		return false;
	}

	public boolean hasViewBillingAddressPermission(
			PermissionChecker permissionChecker, AccountEntry accountEntry)
		throws PortalException {

		if (accountEntry.isGuestAccount() || accountEntry.isPersonalAccount() ||
			portletResourcePermission.contains(
				permissionChecker, accountEntry.getAccountEntryGroup(),
				CommerceOrderActionKeys.VIEW_BILLING_ADDRESS)) {

			return true;
		}

		return false;
	}

	public boolean isCommerceOrderMultishipping() {
		return CommerceOrderUtil.isCommerceOrderMultishipping(_commerceOrder);
	}

	public boolean isShippingUsedAsBilling() throws PortalException {
		AccountEntry accountEntry = _commerceOrder.getAccountEntry();

		CommerceAddress defaultBillingCommerceAddress = null;
		CommerceAddress defaultShippingCommerceAddress = null;

		if (accountEntry != null) {
			CommerceChannel commerceChannel =
				commerceChannelLocalService.getCommerceChannelByOrderGroupId(
					_commerceOrder.getGroupId());

			CommerceChannelAccountEntryRel
				billingAddressCommerceChannelAccountEntryRel =
					commerceChannelAccountEntryRelLocalService.
						fetchCommerceChannelAccountEntryRel(
							accountEntry.getAccountEntryId(),
							commerceChannel.getCommerceChannelId(),
							CommerceChannelAccountEntryRelConstants.
								TYPE_BILLING_ADDRESS);

			if (billingAddressCommerceChannelAccountEntryRel != null) {
				defaultBillingCommerceAddress =
					commerceAddressService.getCommerceAddress(
						billingAddressCommerceChannelAccountEntryRel.
							getClassPK());
			}

			CommerceChannelAccountEntryRel
				shippingAddressCommerceChannelAccountEntryRel =
					commerceChannelAccountEntryRelLocalService.
						fetchCommerceChannelAccountEntryRel(
							accountEntry.getAccountEntryId(),
							commerceChannel.getCommerceChannelId(),
							CommerceChannelAccountEntryRelConstants.
								TYPE_SHIPPING_ADDRESS);

			if (shippingAddressCommerceChannelAccountEntryRel != null) {
				defaultShippingCommerceAddress =
					commerceAddressService.getCommerceAddress(
						shippingAddressCommerceChannelAccountEntryRel.
							getClassPK());
			}
		}

		long defaultBillingCommerceAddressId = 0;
		long defaultShippingCommerceAddressId = 0;

		if (defaultBillingCommerceAddress != null) {
			defaultBillingCommerceAddressId =
				defaultBillingCommerceAddress.getCommerceAddressId();
		}

		if (defaultShippingCommerceAddress != null) {
			defaultShippingCommerceAddressId =
				defaultShippingCommerceAddress.getCommerceAddressId();
		}

		CommerceAddress billingCommerceAddress =
			_commerceOrder.getBillingAddress();
		CommerceAddress shippingCommerceAddress =
			_commerceOrder.getShippingAddress();

		if (((accountEntry != null) &&
			 (defaultBillingCommerceAddressId ==
				 defaultShippingCommerceAddressId) &&
			 (billingCommerceAddress == null) &&
			 (shippingCommerceAddress == null)) ||
			((billingCommerceAddress != null) &&
			 (shippingCommerceAddress != null) &&
			 (billingCommerceAddress.getCommerceAddressId() ==
				 shippingCommerceAddress.getCommerceAddressId())) ||
			((billingCommerceAddress != null) &&
			 (shippingCommerceAddress == null) &&
			 (defaultShippingCommerceAddressId ==
				 billingCommerceAddress.getCommerceAddressId())) ||
			((billingCommerceAddress == null) &&
			 (shippingCommerceAddress != null) &&
			 (defaultBillingCommerceAddressId ==
				 shippingCommerceAddress.getCommerceAddressId()))) {

			return true;
		}

		return false;
	}

	protected final AccountEntryLocalService accountEntryLocalService;
	protected final ModelResourcePermission<AccountEntry>
		accountEntryModelResourcePermission;
	protected final AccountRoleLocalService accountRoleLocalService;
	protected final CommerceAddressService commerceAddressService;
	protected final CommerceChannelAccountEntryRelLocalService
		commerceChannelAccountEntryRelLocalService;
	protected final CommerceChannelLocalService commerceChannelLocalService;
	protected PortletResourcePermission portletResourcePermission;

	private final CommerceContext _commerceContext;
	private final CommerceOrder _commerceOrder;

}