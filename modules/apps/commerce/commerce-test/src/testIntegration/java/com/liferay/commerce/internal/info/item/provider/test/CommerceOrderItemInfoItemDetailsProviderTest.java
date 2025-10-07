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
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.math.BigDecimal;

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
public class CommerceOrderItemInfoItemDetailsProviderTest {

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

		User user = UserTestUtil.addUser();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.getPersonAccountEntry(user.getUserId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				user.getUserId(), commerceChannel.getGroupId(),
				accountEntry.getAccountEntryId(), commerceCurrency.getCode(),
				0);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		CPInstance cpInstance = CPTestUtil.addCPInstance(group.getGroupId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			user.getUserId(), commerceInventoryWarehouse, BigDecimal.valueOf(2),
			cpInstance.getSku(), StringPool.BLANK);

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceChannel.getCommerceChannelId());

		_commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null, BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK,
				new TestCommerceContext(
					null, commerceCurrency, commerceChannel, user, group, null),
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), user.getUserId()));
	}

	@Test
	public void testGetInfoItemDetails() {
		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				CommerceOrderItem.class.getName());

		InfoItemDetails infoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(_commerceOrderItem);

		Assert.assertEquals(
			CommerceOrderItem.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CommerceOrderItem.class.getName(),
				_commerceOrderItem.getCommerceOrderItemId()),
			infoItemDetails.getInfoItemReference());

		Group group = _groupLocalService.fetchGroup(
			_commerceOrderItem.getGroupId());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			group.getGroupId(), ERCInfoItemIdentifier.class,
			_commerceOrderItem);

		Assert.assertEquals(
			CommerceOrderItem.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CommerceOrderItem.class.getName(),
				new ERCInfoItemIdentifier(
					_commerceOrderItem.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());

		infoItemDetails = infoItemDetailsProvider.getInfoItemDetails(
			RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
			_commerceOrderItem);

		Assert.assertEquals(
			CommerceOrderItem.class.getName(), infoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				CommerceOrderItem.class.getName(),
				new ERCInfoItemIdentifier(
					_commerceOrderItem.getExternalReferenceCode(),
					group.getExternalReferenceCode())),
			infoItemDetails.getInfoItemReference());
	}

	private CommerceOrderItem _commerceOrderItem;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}