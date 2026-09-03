/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceOrderAttachmentTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Attachment;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-6252")
@RunWith(Arquillian.class)
public class AttachmentResourceTest extends BaseAttachmentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		CommerceOrderAttachmentTestUtil.initialize(getClass());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				TestPropsValues.getUserId());

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(), "business", 1, serviceContext);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.addCommerceCurrency(
				null, TestPropsValues.getUserId(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), BigDecimal.ONE, new HashMap<>(),
				2, 2, "HALF_EVEN", false, RandomTestUtil.nextDouble(), true);

		_commerceChannelLocalService.addCommerceChannel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, testGroup.getGroupId(),
			RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			commerceCurrency.getCode(), serviceContext);

		_commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			testGroup.getGroupId(), TestPropsValues.getUserId(),
			accountEntry.getAccountEntryId(),
			commerceCurrency.getCommerceCurrencyId());

		_commerceOrder.setOrderStatus(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED);

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);
	}

	@Override
	@Test
	public void testGetOrderAttachment() throws Exception {
		super.testGetOrderAttachment();

		Attachment postAttachment = testGetOrderAttachment_addAttachment();

		Attachment getAttachment = attachmentResource.getOrderAttachment(
			testGetOrderAttachment_getOrderId(), postAttachment.getId());

		Assert.assertEquals(
			"commerce-order-attachment/" + getAttachment.getId(),
			StringUtil.extractLast(getAttachment.getUrl(), "/o/"));
		Assert.assertTrue(
			StringUtil.startsWith(getAttachment.getUrl(), "http"));
	}

	@Override
	@Test
	public void testPatchOrderAttachment() throws Exception {
		Attachment postAttachment = testPatchOrderAttachment_addAttachment();

		Attachment randomPatchAttachment = randomPatchAttachment();

		Attachment patchAttachment = attachmentResource.patchOrderAttachment(
			_commerceOrder.getCommerceOrderId(), postAttachment.getId(),
			randomPatchAttachment);

		Attachment expectedPatchAttachment = postAttachment.clone();

		BeanTestUtil.copyProperties(
			randomPatchAttachment, expectedPatchAttachment);

		Attachment getAttachment = attachmentResource.getOrderAttachment(
			_commerceOrder.getCommerceOrderId(), patchAttachment.getId());

		assertEquals(expectedPatchAttachment, getAttachment);
		assertValid(getAttachment);
	}

	@Override
	@Test
	public void testPatchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode()
		throws Exception {

		Attachment postAttachment =
			testPatchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment();

		Attachment randomPatchAttachment = randomPatchAttachment();

		Attachment patchAttachment =
			attachmentResource.
				patchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
					_commerceOrder.getExternalReferenceCode(),
					postAttachment.getExternalReferenceCode(),
					randomPatchAttachment);

		Attachment expectedPatchAttachment = postAttachment.clone();

		BeanTestUtil.copyProperties(
			randomPatchAttachment, expectedPatchAttachment);

		Attachment getAttachment =
			attachmentResource.
				getOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
					_commerceOrder.getExternalReferenceCode(),
					patchAttachment.getExternalReferenceCode());

		assertEquals(expectedPatchAttachment, getAttachment);
		assertValid(getAttachment);
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"commerceOrderId", "dateCreated", "dateModified", "type"
		};
	}

	@Override
	protected Attachment randomAttachment() throws Exception {
		return new Attachment() {
			{
				attachment = Base64.encode(
					FileUtil.getBytes(
						AttachmentResourceTest.class,
						"dependencies/image.jpg"));
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileName = RandomTestUtil.randomString();
				priority = RandomTestUtil.nextDouble();
				restricted = RandomTestUtil.randomBoolean();
				title = RandomTestUtil.randomString();
				type = "invoice";
			}
		};
	}

	@Override
	protected Attachment testDeleteOrderAttachment_addAttachment()
		throws Exception {

		return testPostOrderAttachment_addAttachment(randomAttachment());
	}

	@Override
	protected Long testDeleteOrderAttachment_getOrderId() throws Exception {
		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Attachment
			testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return testPostOrderByExternalReferenceCodeAttachment_addAttachment(
			randomAttachment());
	}

	@Override
	protected String
			testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Attachment testGetOrderAttachment_addAttachment()
		throws Exception {

		return testPostOrderAttachment_addAttachment(randomAttachment());
	}

	@Override
	protected Long testGetOrderAttachment_getOrderId() throws Exception {
		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Attachment testGetOrderAttachmentsPage_addAttachment(
			Long orderId, Attachment attachment)
		throws Exception {

		return attachmentResource.postOrderAttachment(orderId, attachment);
	}

	@Override
	protected Long testGetOrderAttachmentsPage_getOrderId() throws Exception {
		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Attachment
			testGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return testPostOrderByExternalReferenceCodeAttachment_addAttachment(
			randomAttachment());
	}

	@Override
	protected String
			testGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Attachment
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		return attachmentResource.postOrderByExternalReferenceCodeAttachment(
			externalReferenceCode, attachment);
	}

	@Override
	protected String
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Attachment testGraphQLAttachment_addAttachment()
		throws Exception {

		return testPostOrderAttachment_addAttachment(randomAttachment());
	}

	@Override
	protected Long testGraphQLDeleteOrderAttachment_getOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected String
			testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Long testGraphQLGetOrderAttachment_getOrderId() throws Exception {
		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected String
			testGraphQLGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Attachment
			testGraphQLGetOrderByExternalReferenceCodeAttachmentsPageOrderAttachment_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		return attachmentResource.postOrderByExternalReferenceCodeAttachment(
			externalReferenceCode, attachment);
	}

	@Override
	protected Long testGraphQLOrderAttachment_getOrderId() throws Exception {
		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Long
			testGraphQLPostOrderByExternalReferenceCodeAttachment_getOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Attachment
			testPatchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return testPostOrderByExternalReferenceCodeAttachment_addAttachment(
			randomAttachment());
	}

	@Override
	protected Attachment testPostOrderAttachment_addAttachment(
			Attachment attachment)
		throws Exception {

		return attachmentResource.postOrderAttachment(
			_commerceOrder.getCommerceOrderId(), attachment);
	}

	@Override
	protected Attachment
			testPostOrderByExternalReferenceCodeAttachment_addAttachment(
				Attachment attachment)
		throws Exception {

		return attachmentResource.postOrderByExternalReferenceCodeAttachment(
			_commerceOrder.getExternalReferenceCode(), attachment);
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

}