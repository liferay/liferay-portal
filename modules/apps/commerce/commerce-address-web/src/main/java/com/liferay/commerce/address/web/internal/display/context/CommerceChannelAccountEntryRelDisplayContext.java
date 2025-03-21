/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.address.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.address.web.internal.display.context.helper.CommerceAddressRequestHelper;
import com.liferay.commerce.constants.CommerceAccountActionKeys;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceChannelAccountEntryRelDisplayContext {

	public CommerceChannelAccountEntryRelDisplayContext(
			ModelResourcePermission<AccountEntry>
				accountEntryModelResourcePermission,
			AccountEntryService accountEntryService,
			CommerceAddressService commerceAddressService,
			CommerceChannelAccountEntryRelService
				commerceChannelAccountEntryRelService,
			CommerceChannelService commerceChannelService,
			HttpServletRequest httpServletRequest, Language language)
		throws PortalException {

		_accountEntryModelResourcePermission =
			accountEntryModelResourcePermission;
		_accountEntryService = accountEntryService;
		_commerceAddressService = commerceAddressService;
		_commerceChannelAccountEntryRelService =
			commerceChannelAccountEntryRelService;
		_commerceChannelService = commerceChannelService;
		_language = language;

		long accountEntryId = ParamUtil.getLong(
			httpServletRequest, "accountEntryId");

		_accountEntry = accountEntryService.getAccountEntry(accountEntryId);

		_commerceAddressRequestHelper = new CommerceAddressRequestHelper(
			httpServletRequest);

		_type = ParamUtil.getInteger(httpServletRequest, "type");
	}

	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel()
		throws PortalException {

		long commerceChannelAccountEntryRelId = ParamUtil.getLong(
			_commerceAddressRequestHelper.getRequest(),
			"commerceChannelAccountEntryRelId");

		return _commerceChannelAccountEntryRelService.
			fetchCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRelId);
	}

	public AccountEntry getAccountEntry() {
		return _accountEntry;
	}

	public String getAddCommerceChannelAccountEntryRelRenderURL(int type) {
		return PortletURLBuilder.createRenderURL(
			_commerceAddressRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_address/edit_account_entry_default_commerce_address"
		).setParameter(
			"accountEntryId", _accountEntry.getAccountEntryId()
		).setParameter(
			"type", type
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getAddressSelectLabel(int type) {
		if (CommerceChannelAccountEntryRelConstants.TYPE_BILLING_ADDRESS ==
				type) {

			return "billing-address";
		}

		if (CommerceChannelAccountEntryRelConstants.TYPE_SHIPPING_ADDRESS ==
				type) {

			return "shipping-address";
		}

		return "address";
	}

	public String getCommerceAddressAPIURL() {
		if (CommerceChannelAccountEntryRelConstants.TYPE_BILLING_ADDRESS ==
				_type) {

			return "/commerce.commerceaddress/get-billing-commerce-addresses";
		}

		if (CommerceChannelAccountEntryRelConstants.TYPE_SHIPPING_ADDRESS ==
				_type) {

			return "/commerce.commerceaddress/get-shipping-commerce-addresses";
		}

		return StringPool.BLANK;
	}

	public List<CommerceAddress> getCommerceAddresses() throws PortalException {
		if (CommerceChannelAccountEntryRelConstants.TYPE_BILLING_ADDRESS ==
				_type) {

			return _commerceAddressService.getBillingCommerceAddresses(
				_commerceAddressRequestHelper.getCompanyId(),
				AccountEntry.class.getName(),
				_accountEntry.getAccountEntryId());
		}

		if (CommerceChannelAccountEntryRelConstants.TYPE_SHIPPING_ADDRESS ==
				_type) {

			return _commerceAddressService.getShippingCommerceAddresses(
				_commerceAddressRequestHelper.getCompanyId(),
				AccountEntry.class.getName(),
				_accountEntry.getAccountEntryId());
		}

		return Collections.emptyList();
	}

	public String getCommerceChannelsEmptyOptionKey() throws PortalException {
		int commerceChannelAccountEntryRelsCount =
			_commerceChannelAccountEntryRelService.
				getCommerceChannelAccountEntryRelsCount(
					_accountEntry.getAccountEntryId(), _type);

		if (commerceChannelAccountEntryRelsCount > 0) {
			return "all-other-channels";
		}

		return "all-channels";
	}

	public CreationMenu getCreationMenu(int type) throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasPermission(CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS)) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						getAddCommerceChannelAccountEntryRelRenderURL(type));
					dropdownItem.setLabel(
						_language.get(
							_commerceAddressRequestHelper.getRequest(),
							"add-default-address"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public List<CommerceChannel> getFilteredCommerceChannels()
		throws PortalException {

		long[] commerceChannelIds = _getFilteredCommerceChannelIds();

		return ListUtil.filter(
			_commerceChannelService.getEligibleCommerceChannels(
				_accountEntry.getAccountEntryId(), null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			commerceChannel -> !ArrayUtil.contains(
				commerceChannelIds, commerceChannel.getCommerceChannelId()));
	}

	public String getModalTitle() {
		if (CommerceChannelAccountEntryRelConstants.TYPE_BILLING_ADDRESS ==
				_type) {

			return _language.get(
				_commerceAddressRequestHelper.getRequest(),
				"set-default-billing-address");
		}

		if (CommerceChannelAccountEntryRelConstants.TYPE_SHIPPING_ADDRESS ==
				_type) {

			return _language.get(
				_commerceAddressRequestHelper.getRequest(),
				"set-default-shipping-address");
		}

		return StringPool.BLANK;
	}

	public int getType() {
		return _type;
	}

	public boolean hasPermission(String actionId) throws PortalException {
		return _accountEntryModelResourcePermission.contains(
			_commerceAddressRequestHelper.getPermissionChecker(),
			_accountEntry.getAccountEntryId(), actionId);
	}

	public boolean isCommerceAddressSelected(long commerceAddressId)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			fetchCommerceChannelAccountEntryRel();

		if (commerceChannelAccountEntryRel == null) {
			return false;
		}

		if (commerceChannelAccountEntryRel.getClassPK() == commerceAddressId) {
			return true;
		}

		return false;
	}

	public boolean isCommerceChannelSelected(long commerceChannelId)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			fetchCommerceChannelAccountEntryRel();

		if (commerceChannelAccountEntryRel == null) {
			if (commerceChannelId == 0) {
				return true;
			}

			return false;
		}

		if (commerceChannelAccountEntryRel.getCommerceChannelId() ==
				commerceChannelId) {

			return true;
		}

		return false;
	}

	private long[] _getFilteredCommerceChannelIds() throws PortalException {
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			fetchCommerceChannelAccountEntryRel();

		long[] commerceChannelIds = TransformUtil.transformToLongArray(
			_commerceChannelAccountEntryRelService.
				getCommerceChannelAccountEntryRels(
					_accountEntry.getAccountEntryId(), _type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null),
			CommerceChannelAccountEntryRel::getCommerceChannelId);

		if (commerceChannelAccountEntryRel == null) {
			return commerceChannelIds;
		}

		return ArrayUtil.filter(
			commerceChannelIds,
			commerceChannelId ->
				commerceChannelAccountEntryRel.getCommerceChannelId() !=
					commerceChannelId);
	}

	private final AccountEntry _accountEntry;
	private final ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;
	private final AccountEntryService _accountEntryService;
	private final CommerceAddressRequestHelper _commerceAddressRequestHelper;
	private final CommerceAddressService _commerceAddressService;
	private final CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;
	private final CommerceChannelService _commerceChannelService;
	private final Language _language;
	private final int _type;

}