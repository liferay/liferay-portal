/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.payment.util.comparator.CommercePaymentMethodGroupRelNameOrderByComparator;
import com.liferay.commerce.payment.web.internal.display.context.helper.CommercePaymentRequestHelper;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Crescenzo Rega
 */
public class CommerceChannelAccountEntryRelDisplayContext {

	public CommerceChannelAccountEntryRelDisplayContext(
			AccountEntryService accountEntryService,
			CommerceChannelService commerceChannelService,
			CommerceChannelAccountEntryRelService
				commerceChannelAccountEntryRelService,
			CommercePaymentMethodGroupRelService
				commercePaymentMethodGroupRelService,
			HttpServletRequest httpServletRequest, Portal portal)
		throws PortalException {

		_accountEntryService = accountEntryService;
		_commerceChannelService = commerceChannelService;
		_commerceChannelAccountEntryRelService =
			commerceChannelAccountEntryRelService;
		_commercePaymentMethodGroupRelService =
			commercePaymentMethodGroupRelService;
		_portal = portal;

		long accountEntryId = ParamUtil.getLong(
			httpServletRequest, "accountEntryId");

		_accountEntry = accountEntryService.getAccountEntry(accountEntryId);

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		_commerceChannel = _commerceChannelService.fetchCommerceChannel(
			commerceChannelId);

		commercePaymentRequestHelper = new CommercePaymentRequestHelper(
			httpServletRequest);

		_locale = portal.getLocale(httpServletRequest);
	}

	public long getAccountEntryId() {
		if (_accountEntry == null) {
			return 0;
		}

		return _accountEntry.getAccountEntryId();
	}

	public long getCommerceChannelId() {
		if (_commerceChannel == null) {
			return 0;
		}

		return _commerceChannel.getCommerceChannelId();
	}

	public CommercePaymentMethodGroupRel getCommercePaymentMethodGroupRel()
		throws PortalException {

		if (_commercePaymentMethodGroupRel != null) {
			return _commercePaymentMethodGroupRel;
		}

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelService.
				fetchCommerceChannelAccountEntryRel(
					_accountEntry.getAccountEntryId(),
					_commerceChannel.getCommerceChannelId(),
					CommerceChannelAccountEntryRelConstants.TYPE_PAYMENT);

		if (commerceChannelAccountEntryRel == null) {
			return null;
		}

		_commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelService.
				fetchCommercePaymentMethodGroupRel(
					commerceChannelAccountEntryRel.getClassPK());

		return _commercePaymentMethodGroupRel;
	}

	public List<CommercePaymentMethodGroupRel>
			getCommercePaymentMethodGroupRels()
		throws PortalException {

		return _commercePaymentMethodGroupRelService.
			getCommercePaymentMethodGroupRels(
				_commerceChannel.getGroupId(), true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				new CommercePaymentMethodGroupRelNameOrderByComparator(
					_locale));
	}

	public boolean isCommercePaymentChecked(String key) {
		if ((_commercePaymentMethodGroupRel == null) &&
			Validator.isBlank(key)) {

			return true;
		}

		if ((_commercePaymentMethodGroupRel != null) &&
			key.equals(
				_commercePaymentMethodGroupRel.getPaymentIntegrationKey())) {

			return true;
		}

		return false;
	}

	protected final CommercePaymentRequestHelper commercePaymentRequestHelper;

	private final AccountEntry _accountEntry;
	private final AccountEntryService _accountEntryService;
	private final CommerceChannel _commerceChannel;
	private final CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;
	private final CommerceChannelService _commerceChannelService;
	private CommercePaymentMethodGroupRel _commercePaymentMethodGroupRel;
	private final CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;
	private final Locale _locale;
	private final Portal _portal;

}