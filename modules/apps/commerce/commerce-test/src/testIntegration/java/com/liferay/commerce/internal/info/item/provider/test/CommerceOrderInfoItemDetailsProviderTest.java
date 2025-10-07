/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.info.item.provider.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class CommerceOrderInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getGroupId());

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		AccountEntry accountEntry =
			CommerceAccountTestUtil.getPersonAccountEntry(
				TestPropsValues.getUserId());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			accountEntry.getAccountEntryId(), commerceCurrency.getCode(), 0);
	}

	@Test
	public void testGetInfoItemDetails() {
		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, CommerceOrder.class.getName());

		InfoItemDetails infoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(_commerceOrder);

		Assert.assertEquals(
			CommerceOrder.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CommerceOrder.class.getName(),
				_commerceOrder.getCommerceOrderId()),
			infoItemDetails.getInfoItemReference());

		Group group = _groupLocalService.fetchGroup(
			_commerceOrder.getGroupId());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			group.getGroupId(), ERCInfoItemIdentifier.class, _commerceOrder);

		Assert.assertEquals(
			CommerceOrder.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CommerceOrder.class.getName(),
				new ERCInfoItemIdentifier(
					_commerceOrder.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
			_commerceOrder);

		Assert.assertEquals(
			CommerceOrder.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CommerceOrder.class.getName(),
				new ERCInfoItemIdentifier(
					_commerceOrder.getExternalReferenceCode(),
					group.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());
	}

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}