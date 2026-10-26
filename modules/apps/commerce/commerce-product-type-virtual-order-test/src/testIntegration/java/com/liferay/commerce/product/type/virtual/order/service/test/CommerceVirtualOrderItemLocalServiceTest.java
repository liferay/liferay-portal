/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemUrlException;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemLocalService;
import com.liferay.commerce.product.type.virtual.order.util.CommerceVirtualOrderItemChecker;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryLocalServiceUtil;
import com.liferay.commerce.product.type.virtual.test.util.VirtualCPTypeTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.subscription.CommerceSubscriptionEntryHelper;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CommerceVirtualOrderItemLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());
		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			_commerceCurrency.getCode());
	}

	@After
	public void tearDown() throws Exception {
		for (CommerceOrder commerceOrder : _commerceOrders) {
			_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
		}
	}

	@Test
	public void testAddCommerceVirtualOrderItem() throws Exception {
		frutillaRule.scenario(
			"Add a virtual order item"
		).given(
			"An order item"
		).when(
			"An order is paid"
		).then(
			"I should be able to see the created virtual order item"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME, true,
			true);

		VirtualCPTypeTestUtil.addCPDefinitionVirtualSetting(
			_commerceCatalog.getGroupId(), cpDefinition.getModelClassName(),
			cpDefinition.getCPDefinitionId(), 0,
			CommerceOrderConstants.ORDER_STATUS_PENDING, 0, 0, 0);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(cpDefinition);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				cpDefinition.getGroupId());

		for (CPInstance cpInstance : cpDefinition.getCPInstances()) {
			_commercePriceEntryLocalService.addCommercePriceEntry(
				null, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), BigDecimal.ZERO,
				false, BigDecimal.ZERO, null,
				ServiceContextTestUtil.getServiceContext(_user.getGroupId()));

			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);
		}

		commerceOrder = _setCommerceOrderStatuses(
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId()),
			CommerceOrderPaymentConstants.STATUS_COMPLETED,
			CommerceOrderConstants.ORDER_STATUS_PENDING);

		_commerceVirtualOrderItemChecker.checkCommerceVirtualOrderItems(
			commerceOrder.getCommerceOrderId());

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		List<CommerceVirtualOrderItem> userCommerceVirtualOrderItems =
			_commerceVirtualOrderItemLocalService.getCommerceVirtualOrderItems(
				_commerceChannel.getGroupId(),
				commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			userCommerceVirtualOrderItems.toString(), commerceOrderItems.size(),
			userCommerceVirtualOrderItems.size());

		for (CommerceVirtualOrderItem commerceVirtualOrderItem :
				userCommerceVirtualOrderItems) {

			Assert.assertTrue(commerceVirtualOrderItem.isActive());
			Assert.assertEquals(
				CommerceOrderConstants.ORDER_STATUS_PENDING,
				commerceVirtualOrderItem.getActivationStatus());
			Assert.assertEquals(0L, commerceVirtualOrderItem.getDuration());

			if (!Objects.equals(
					commerceVirtualOrderItem.getActivationStatus(),
					commerceOrder.getOrderStatus())) {

				continue;
			}

			Assert.assertEquals(null, commerceVirtualOrderItem.getEndDate());
		}
	}

	@Test
	public void testAddCommerceVirtualOrderItemWithSubscription()
		throws Exception {

		frutillaRule.scenario(
			"Add a virtual order item"
		).given(
			"A subscription order item"
		).when(
			"An order is paid"
		).then(
			"I should be able to see the created virtual order item"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME, true,
			true);

		VirtualCPTypeTestUtil.addCPDefinitionVirtualSetting(
			_commerceCatalog.getGroupId(), cpDefinition.getModelClassName(),
			cpDefinition.getCPDefinitionId(), 0L,
			CommerceOrderConstants.ORDER_STATUS_PENDING, 0L, 0L, 0L);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(cpDefinition);

		int subscriptionLength = 1;

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				cpDefinition.getGroupId());

		for (CPInstance cpInstance : cpDefinition.getCPInstances()) {
			cpInstance = _setCPInstanceSubscriptionInfo(
				cpInstance, subscriptionLength, "daily");

			_commercePriceEntryLocalService.addCommercePriceEntry(
				null, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), BigDecimal.ZERO,
				false, BigDecimal.ZERO, null,
				ServiceContextTestUtil.getServiceContext(_user.getGroupId()));

			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);
		}

		commerceOrder = _setCommerceOrderStatuses(
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId()),
			CommerceOrderPaymentConstants.STATUS_COMPLETED,
			CommerceOrderConstants.ORDER_STATUS_PENDING);

		_commerceSubscriptionEntryHelper.checkCommerceSubscriptions(
			commerceOrder);

		_commerceVirtualOrderItemChecker.checkCommerceVirtualOrderItems(
			commerceOrder.getCommerceOrderId());

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		List<CommerceVirtualOrderItem> userCommerceVirtualOrderItems =
			_commerceVirtualOrderItemLocalService.getCommerceVirtualOrderItems(
				_commerceChannel.getGroupId(),
				commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			userCommerceVirtualOrderItems.toString(), commerceOrderItems.size(),
			userCommerceVirtualOrderItems.size());

		for (CommerceVirtualOrderItem commerceVirtualOrderItem :
				userCommerceVirtualOrderItems) {

			Assert.assertTrue(commerceVirtualOrderItem.isActive());
			Assert.assertEquals(
				CommerceOrderConstants.ORDER_STATUS_PENDING,
				commerceVirtualOrderItem.getActivationStatus());
			Assert.assertEquals(0L, commerceVirtualOrderItem.getDuration());

			CommerceOrderItem commerceOrderItem =
				commerceVirtualOrderItem.getCommerceOrderItem();

			Assert.assertTrue(commerceOrderItem.isSubscription());
		}
	}

	@Test
	public void testDeleteCPDVirtualFileEntryWithOrder() throws Exception {
		frutillaRule.scenario(
			"Add a virtual order item"
		).given(
			"An order item"
		).when(
			"An order is paid"
		).and(
			"I try to delete the Virtual File Entry"
		).then(
			"The File Entry does not get deleted"
		).and(
			"the order has still access to the file entry"
		);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		DLFolder dlFolder = DLTestUtil.addDLFolder(
			_commerceCatalog.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			VirtualCPTypeTestUtil.addCPDefinitionVirtualSetting(
				_commerceCatalog.getGroupId(), cpDefinition.getModelClassName(),
				cpDefinition.getCPDefinitionId(), dlFileEntry.getFileEntryId(),
				1, 0, 0, 0);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(cpDefinition);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				cpDefinition.getGroupId());

		for (CPInstance cpInstance : cpDefinition.getCPInstances()) {
			_commercePriceEntryLocalService.addCommercePriceEntry(
				null, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), BigDecimal.ZERO,
				false, BigDecimal.ZERO, null,
				ServiceContextTestUtil.getServiceContext(_user.getGroupId()));

			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);
		}

		commerceOrder = _setCommerceOrderStatuses(
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId()),
			CommerceOrderPaymentConstants.STATUS_COMPLETED,
			CommerceOrderConstants.ORDER_STATUS_PENDING);

		_commerceVirtualOrderItemChecker.checkCommerceVirtualOrderItems(
			commerceOrder.getCommerceOrderId());

		List<CommerceVirtualOrderItem> commerceVirtualOrderItems =
			_commerceVirtualOrderItemLocalService.getCommerceVirtualOrderItems(
				_commerceChannel.getGroupId(),
				commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItems.get(0);

		List<CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries =
				commerceVirtualOrderItem.
					getCommerceVirtualOrderItemFileEntries();

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItemFileEntries.get(0);

		Assert.assertNotNull(commerceVirtualOrderItemFileEntry.getFileEntry());

		CPDVirtualSettingFileEntryLocalServiceUtil.
			deleteCPDVirtualSettingFileEntries(
				cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId());

		Assert.assertNotNull(commerceVirtualOrderItemFileEntry.getFileEntry());
	}

	@Test
	public void testGetFile() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME);

		DLFolder dlFolder = DLTestUtil.addDLFolder(
			_commerceCatalog.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		VirtualCPTypeTestUtil.addCPDefinitionVirtualSetting(
			_commerceCatalog.getGroupId(), cpDefinition.getModelClassName(),
			cpDefinition.getCPDefinitionId(), dlFileEntry.getFileEntryId(), 1,
			0, 0, 0);

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(cpDefinition);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrders.add(commerceOrder);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				cpDefinition.getGroupId());

		for (CPInstance cpInstance : cpDefinition.getCPInstances()) {
			_commercePriceEntryLocalService.addCommercePriceEntry(
				null, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), BigDecimal.ZERO,
				false, BigDecimal.ZERO, null,
				ServiceContextTestUtil.getServiceContext(_user.getGroupId()));

			CommerceTestUtil.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), BigDecimal.ONE);
		}

		commerceOrder = _setCommerceOrderStatuses(
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId()),
			CommerceOrderPaymentConstants.STATUS_COMPLETED,
			CommerceOrderConstants.ORDER_STATUS_PENDING);

		_commerceVirtualOrderItemChecker.checkCommerceVirtualOrderItems(
			commerceOrder.getCommerceOrderId());

		List<CommerceVirtualOrderItem> commerceVirtualOrderItems =
			_commerceVirtualOrderItemLocalService.getCommerceVirtualOrderItems(
				_commerceChannel.getGroupId(),
				commerceOrder.getCommerceAccountId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			commerceVirtualOrderItems.get(0);

		CommerceOrderItem commerceOrderItem =
			commerceVirtualOrderItem.getCommerceOrderItem();

		commerceOrderItem.setName(".");

		commerceOrderItem =
			_commerceOrderItemLocalService.updateCommerceOrderItem(
				commerceOrderItem);

		List<CommerceVirtualOrderItemFileEntry>
			commerceVirtualOrderItemFileEntries =
				commerceVirtualOrderItem.
					getCommerceVirtualOrderItemFileEntries();

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			commerceVirtualOrderItemFileEntries.get(0);

		File file = _commerceVirtualOrderItemLocalService.getFile(
			commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
			commerceVirtualOrderItemFileEntry.
				getCommerceVirtualOrderItemFileEntryId());

		try {
			Assert.assertArrayEquals(
				TestDataConstants.TEST_BYTE_ARRAY, FileUtil.getBytes(file));
		}
		finally {
			file.delete();
		}

		String name = RandomTestUtil.randomString();

		commerceOrderItem.setName("../" + name);

		_commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem);

		file = _commerceVirtualOrderItemLocalService.getFile(
			commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
			commerceVirtualOrderItemFileEntry.
				getCommerceVirtualOrderItemFileEntryId());

		try {
			Assert.assertArrayEquals(
				TestDataConstants.TEST_BYTE_ARRAY, FileUtil.getBytes(file));
			Assert.assertEquals(
				name + "." + dlFileEntry.getExtension(), file.getName());

			String canonicalPath = file.getCanonicalPath();

			File tempDir = new File(
				SystemProperties.get(SystemProperties.TMP_DIR));

			Assert.assertTrue(
				canonicalPath.startsWith(
					tempDir.getCanonicalPath() + File.separator));
		}
		finally {
			file.delete();
		}

		_assertGetFileFails(
			commerceVirtualOrderItemFileEntry.
				getCommerceVirtualOrderItemFileEntryId(),
			commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
			"file:/" + RandomTestUtil.randomString());
		_assertGetFileFails(
			commerceVirtualOrderItemFileEntry.
				getCommerceVirtualOrderItemFileEntryId(),
			commerceVirtualOrderItem.getCommerceVirtualOrderItemId(),
			"http://localhost:" + PortalUtil.getPortalServerPort(false));
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertGetFileFails(
			long commerceVirtualOrderItemFileEntryId,
			long commerceVirtualOrderItemId, String url)
		throws Exception {

		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			_commerceVirtualOrderItemFileEntryLocalService.
				getCommerceVirtualOrderItemFileEntry(
					commerceVirtualOrderItemFileEntryId);

		commerceVirtualOrderItemFileEntry.setFileEntryId(0);
		commerceVirtualOrderItemFileEntry.setUrl(url);

		_commerceVirtualOrderItemFileEntryLocalService.
			updateCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntry);

		Assert.assertThrows(
			CommerceVirtualOrderItemUrlException.class,
			() -> _commerceVirtualOrderItemLocalService.getFile(
				commerceVirtualOrderItemId,
				commerceVirtualOrderItemFileEntryId));
	}

	private CPInstance _setCPInstanceSubscriptionInfo(
		CPInstance cpInstance, int subscriptionLength,
		String subscriptionType) {

		cpInstance.setOverrideSubscriptionInfo(true);
		cpInstance.setSubscriptionEnabled(true);
		cpInstance.setSubscriptionLength(subscriptionLength);
		cpInstance.setSubscriptionType(subscriptionType);
		cpInstance.setMaxSubscriptionCycles(1);

		return _cpInstanceLocalService.updateCPInstance(cpInstance);
	}

	private CommerceOrder _setCommerceOrderStatuses(
		CommerceOrder commerceOrder, int paymentStatus, int orderStatus) {

		commerceOrder.setOrderStatus(orderStatus);
		commerceOrder.setPaymentStatus(paymentStatus);

		return _commerceOrderLocalService.updateCommerceOrder(commerceOrder);
	}

	private CommerceCatalog _commerceCatalog;
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceSubscriptionEntryHelper _commerceSubscriptionEntryHelper;

	@Inject
	private CommerceVirtualOrderItemChecker _commerceVirtualOrderItemChecker;

	@Inject
	private CommerceVirtualOrderItemFileEntryLocalService
		_commerceVirtualOrderItemFileEntryLocalService;

	@Inject
	private CommerceVirtualOrderItemLocalService
		_commerceVirtualOrderItemLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	private Group _group;
	private User _user;

}