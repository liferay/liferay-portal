/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.exception.CommerceOrderGuestCheckoutException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.util.CommerceChannelConfigurationUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gianmarco Brunialti Masera
 */
@FeatureFlag("LPD-109588")
@RunWith(Arquillian.class)
public class CommerceChannelConfigurationUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());
	}

	@Test
	public void testIsGuestBrowsingEnabled() throws Exception {
		Assert.assertTrue(
			CommerceChannelConfigurationUtil.isGuestBrowsingEnabled(
				_commerceChannel.getGroupId()));

		CommerceTestUtil.runWithGuestBrowsingDisabled(
			_commerceChannel.getGroupId(),
			() -> Assert.assertFalse(
				CommerceChannelConfigurationUtil.isGuestBrowsingEnabled(
					_commerceChannel.getGroupId())));
	}

	@FeatureFlag(enable = false, value = "LPD-109588")
	@Test
	public void testIsGuestBrowsingEnabledWhenFeatureFlagIsDisabled()
		throws Exception {

		CommerceTestUtil.runWithGuestBrowsingDisabled(
			_commerceChannel.getGroupId(),
			() -> Assert.assertTrue(
				CommerceChannelConfigurationUtil.isGuestBrowsingEnabled(
					_commerceChannel.getGroupId())));
	}

	@Test
	public void testValidateGuestCheckout() throws Exception {
		User user = TestPropsValues.getUser();

		AccountEntry accountEntry =
			_accountEntryLocalService.getGuestAccountEntry(
				_group.getCompanyId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				user.getUserId(), _commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		CommerceChannelConfigurationUtil.validateGuestCheckout(
			commerceOrder.getCommerceOrderId());

		CommerceTestUtil.runWithGuestBrowsingDisabled(
			_commerceChannel.getGroupId(),
			() -> Assert.assertThrows(
				CommerceOrderGuestCheckoutException.class,
				() -> CommerceChannelConfigurationUtil.validateGuestCheckout(
					commerceOrder.getCommerceOrderId())));
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Group _group;

}