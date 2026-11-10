/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommercePaymentEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(),
			_commerceCurrency.getUserId(), _commerceCurrency.getCode());

		_cpInstance = CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
			_commerceCatalog.getGroupId());

		CommerceTestUtil.updateBackOrderCPDefinitionInventory(
			_cpInstance.getCPDefinition());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Test Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_user = UserTestUtil.addUser();

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		CommerceTestUtil.addCommerceOrderItem(
			_commerceOrder.getCommerceOrderId(), _cpInstance.getCPInstanceId(),
			BigDecimal.ONE);

		_commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			_commerceOrder.getCommerceOrderId());

		_commerceOrder.setOrderStatus(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED);

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);
	}

	@Test
	public void testAddOrUpdateCommercePaymentEntry1() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		_commercePaymentEntries.add(commercePaymentEntry);

		Assert.assertEquals(
			1,
			_commercePaymentEntryLocalService.getCommercePaymentEntriesCount());

		commercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertNotNull(commercePaymentEntry);
		Assert.assertEquals(BigDecimal.TEN, commercePaymentEntry.getAmount());
		Assert.assertEquals(
			CommercePaymentEntryConstants.STATUS_PENDING,
			commercePaymentEntry.getPaymentStatus());
		Assert.assertEquals(
			CommercePaymentEntryConstants.TYPE_PAYMENT,
			commercePaymentEntry.getType());
	}

	@Test
	public void testAddOrUpdateCommercePaymentEntry2() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		_commercePaymentEntries.add(commercePaymentEntry);

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_COMPLETED);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				commercePaymentEntry);

		CommercePaymentEntry refundCommercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(
					CommercePaymentEntry.class),
				commercePaymentEntry.getCommercePaymentEntryId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, "product-defect",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_REFUND, _serviceContext);

		_commercePaymentEntries.add(refundCommercePaymentEntry);

		refundCommercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				refundCommercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertNotNull(refundCommercePaymentEntry);
		Assert.assertEquals(
			BigDecimal.TEN, refundCommercePaymentEntry.getAmount());
		Assert.assertEquals(
			CommercePaymentEntryConstants.STATUS_PENDING,
			refundCommercePaymentEntry.getPaymentStatus());
		Assert.assertEquals(
			"product-defect", refundCommercePaymentEntry.getReasonKey());
		Assert.assertEquals(
			CommercePaymentEntryConstants.TYPE_REFUND,
			refundCommercePaymentEntry.getType());

		refundCommercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_COMPLETED);

		refundCommercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				refundCommercePaymentEntry);

		Assert.assertEquals(
			CommercePaymentEntryConstants.STATUS_COMPLETED,
			refundCommercePaymentEntry.getPaymentStatus());
	}

	@Test
	public void testDeleteCommercePaymentEntry() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		Assert.assertEquals(
			1,
			_commercePaymentEntryLocalService.getCommercePaymentEntriesCount());

		_commercePaymentEntryLocalService.deleteCommercePaymentEntry(
			commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertEquals(
			0,
			_commercePaymentEntryLocalService.getCommercePaymentEntriesCount());

		commercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertNull(commercePaymentEntry);
	}

	@Test
	public void testGetRefundCommercePaymentEntries() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_COMPLETED, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		_commercePaymentEntries.add(commercePaymentEntry);

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_COMPLETED);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				commercePaymentEntry);

		CommercePaymentEntry refundCommercePaymentEntry1 =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(
					CommercePaymentEntry.class),
				commercePaymentEntry.getCommercePaymentEntryId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.ONE,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, "product-defect",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_REFUND, _serviceContext);

		_commercePaymentEntries.add(refundCommercePaymentEntry1);

		List<CommercePaymentEntry> refundCommercePaymentEntries =
			_commercePaymentEntryLocalService.getRefundCommercePaymentEntries(
				_commerceOrder.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			refundCommercePaymentEntries.toString(), 1,
			refundCommercePaymentEntries.size());

		CommercePaymentEntry refundCommercePaymentEntry2 =
			refundCommercePaymentEntries.get(0);

		Assert.assertNotNull(refundCommercePaymentEntry2);

		Assert.assertEquals(
			refundCommercePaymentEntry1, refundCommercePaymentEntry2);
	}

	@Test
	public void testGetRefundCommercePaymentEntriesCount() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_COMPLETED, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		_commercePaymentEntries.add(commercePaymentEntry);

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_COMPLETED);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				commercePaymentEntry);

		CommercePaymentEntry refundCommercePaymentEntry1 =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(
					CommercePaymentEntry.class),
				commercePaymentEntry.getCommercePaymentEntryId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.ONE,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, "product-defect",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_REFUND, _serviceContext);

		_commercePaymentEntries.add(refundCommercePaymentEntry1);

		CommercePaymentEntry refundCommercePaymentEntry2 =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(
					CommercePaymentEntry.class),
				commercePaymentEntry.getCommercePaymentEntryId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING,
				"damaged-in-transit", RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_REFUND, _serviceContext);

		_commercePaymentEntries.add(refundCommercePaymentEntry2);

		Assert.assertEquals(
			2,
			_commercePaymentEntryLocalService.
				getRefundCommercePaymentEntriesCount(
					_commerceOrder.getCompanyId(),
					_classNameLocalService.getClassNameId(CommerceOrder.class),
					_commerceOrder.getCommerceOrderId()));
	}

	@Test
	public void testGetRefundedAmount() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_COMPLETED, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		_commercePaymentEntries.add(commercePaymentEntry);

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_COMPLETED);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				commercePaymentEntry);

		CommercePaymentEntry refundCommercePaymentEntry1 =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(
					CommercePaymentEntry.class),
				commercePaymentEntry.getCommercePaymentEntryId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.ONE,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, "product-defect",
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_REFUND, _serviceContext);

		_commercePaymentEntries.add(refundCommercePaymentEntry1);

		CommercePaymentEntry refundCommercePaymentEntry2 =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(
					CommercePaymentEntry.class),
				commercePaymentEntry.getCommercePaymentEntryId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING,
				"damaged-in-transit", RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_REFUND, _serviceContext);

		_commercePaymentEntries.add(refundCommercePaymentEntry2);

		BigDecimal refundedAmount =
			_commercePaymentEntryLocalService.getRefundedAmount(
				_commerceOrder.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			BigDecimal.ZERO, refundedAmount.stripTrailingZeros());

		refundCommercePaymentEntry1.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_REFUNDED);

		_commercePaymentEntryLocalService.updateCommercePaymentEntry(
			refundCommercePaymentEntry1);

		refundedAmount = _commercePaymentEntryLocalService.getRefundedAmount(
			_commerceOrder.getCompanyId(),
			_classNameLocalService.getClassNameId(CommerceOrder.class),
			_commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			BigDecimal.ONE, refundedAmount.stripTrailingZeros());

		refundCommercePaymentEntry2.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_REFUNDED);

		_commercePaymentEntryLocalService.updateCommercePaymentEntry(
			refundCommercePaymentEntry2);

		refundedAmount = _commercePaymentEntryLocalService.getRefundedAmount(
			_commerceOrder.getCompanyId(),
			_classNameLocalService.getClassNameId(CommerceOrder.class),
			_commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			BigDecimal.valueOf(11), refundedAmount.stripTrailingZeros());
	}

	@Test
	public void testUpdateCommercePaymentEntry() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.ONE,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		Assert.assertEquals(BigDecimal.ONE, commercePaymentEntry.getAmount());
		Assert.assertEquals(
			CommercePaymentEntryConstants.STATUS_PENDING,
			commercePaymentEntry.getPaymentStatus());

		_commercePaymentEntryLocalService.updateCommercePaymentEntry(
			commercePaymentEntry.getExternalReferenceCode(),
			commercePaymentEntry.getCommercePaymentEntryId(),
			_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_commerceCurrency.getCode(), RandomTestUtil.randomString(),
			_user.getLanguageId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, 0,
			CommercePaymentEntryConstants.STATUS_COMPLETED, null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			CommercePaymentEntryConstants.TYPE_PAYMENT);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertEquals(BigDecimal.TEN, commercePaymentEntry.getAmount());
		Assert.assertEquals(
			CommercePaymentEntryConstants.STATUS_COMPLETED,
			commercePaymentEntry.getPaymentStatus());
	}

	@Test
	public void testUpdateExternalReferenceCode() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.ONE,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		String externalReferenceCode = RandomTestUtil.randomString();

		_commercePaymentEntryLocalService.updateExternalReferenceCode(
			commercePaymentEntry.getCommercePaymentEntryId(),
			externalReferenceCode);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertNotNull(commercePaymentEntry);
		Assert.assertEquals(
			externalReferenceCode,
			commercePaymentEntry.getExternalReferenceCode());
	}

	@Test
	public void testUpdateNote() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.ONE,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		String note = RandomTestUtil.randomString();

		_commercePaymentEntryLocalService.updateNote(
			commercePaymentEntry.getCommercePaymentEntryId(), note);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertNotNull(commercePaymentEntry);
		Assert.assertEquals(note, commercePaymentEntry.getNote());
	}

	@Test
	public void testUpdateReasonKey() throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_commerceOrder.getCommerceOrderId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_COMPLETED, null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, _serviceContext);

		_commercePaymentEntries.add(commercePaymentEntry);

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_COMPLETED);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				commercePaymentEntry);

		String reasonKey = "product-defect";

		CommercePaymentEntry refundCommercePaymentEntry =
			_commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
				null, _user.getUserId(),
				_classNameLocalService.getClassNameId(
					CommercePaymentEntry.class),
				commercePaymentEntry.getCommercePaymentEntryId(),
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_commerceCurrency.getCode(), RandomTestUtil.randomString(),
				_user.getLanguageId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, 0,
				CommercePaymentEntryConstants.STATUS_PENDING, reasonKey,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_REFUND, _serviceContext);

		_commercePaymentEntries.add(refundCommercePaymentEntry);

		Assert.assertEquals(
			reasonKey, refundCommercePaymentEntry.getReasonKey());

		reasonKey = "damaged-in-transit";

		_commercePaymentEntryLocalService.updateReasonKey(
			refundCommercePaymentEntry.getCommercePaymentEntryId(), reasonKey);

		refundCommercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				refundCommercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertEquals(
			reasonKey, refundCommercePaymentEntry.getReasonKey());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private List<CommercePaymentEntry> _commercePaymentEntries =
		new ArrayList<>();

	@Inject
	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}