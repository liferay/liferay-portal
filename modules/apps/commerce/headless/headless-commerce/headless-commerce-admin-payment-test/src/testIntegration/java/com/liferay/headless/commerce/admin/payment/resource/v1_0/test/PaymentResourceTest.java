/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.headless.commerce.admin.payment.client.dto.v1_0.Payment;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class PaymentResourceTest extends BasePaymentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		User user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			user.getUserId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testCompany.getCompanyId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, testGroup.getGroupId(),
			RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testGetPaymentsPageWithSortInteger() throws Exception {
		super.testGetPaymentsPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeletePayment() throws Exception {
		super.testGraphQLDeletePayment();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeletePaymentByExternalReferenceCode()
		throws Exception {

		super.testGraphQLDeletePaymentByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPayment() throws Exception {
		super.testGraphQLGetPayment();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPaymentByExternalReferenceCode()
		throws Exception {

		super.testGraphQLGetPaymentByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPaymentByExternalReferenceCodeNotFound()
		throws Exception {

		super.testGraphQLGetPaymentByExternalReferenceCodeNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPaymentNotFound() throws Exception {
		super.testGraphQLGetPaymentNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPaymentsPage() throws Exception {
		super.testGraphQLGetPaymentsPage();
	}

	@Ignore
	@Override
	@Test
	public void testPostPaymentByExternalReferenceCodeRefund()
		throws Exception {

		super.testPostPaymentByExternalReferenceCodeRefund();
	}

	@Ignore
	@Override
	@Test
	public void testPostPaymentRefund() throws Exception {
		super.testPostPaymentRefund();
	}

	@Override
	protected Payment randomPayment() {
		return new Payment() {
			{
				amount = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				callbackURL = RandomTestUtil.randomString();
				cancelURL = RandomTestUtil.randomString();
				channelId = _commerceChannel.getCommerceChannelId();
				comment = RandomTestUtil.randomString();
				currencyCode = _commerceCurrency.getCode();
				currencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				currencyId = _commerceCurrency.getCommerceCurrencyId();
				externalReferenceCode = RandomTestUtil.randomString();
				languageId = RandomTestUtil.randomString();
				paymentIntegrationKey = RandomTestUtil.randomString();
				paymentIntegrationType = RandomTestUtil.randomInt();
				reasonKey = StringPool.BLANK;
				relatedItemId = RandomTestUtil.randomLong();
				relatedItemName = CommerceOrder.class.getName();
				transactionCode = RandomTestUtil.randomString();
				type = CommercePaymentEntryConstants.TYPE_PAYMENT;
			}
		};
	}

	@Override
	protected Payment testDeletePayment_addPayment() throws Exception {
		return _addPayment(randomPayment());
	}

	@Override
	protected Payment testDeletePaymentByExternalReferenceCode_addPayment()
		throws Exception {

		return _addPayment(randomPayment());
	}

	@Override
	protected Payment testGetPayment_addPayment() throws Exception {
		return _addPayment(randomPayment());
	}

	@Override
	protected Payment testGetPaymentByExternalReferenceCode_addPayment()
		throws Exception {

		return _addPayment(randomPayment());
	}

	@Override
	protected Payment testGetPaymentsPage_addPayment(Payment payment)
		throws Exception {

		return _addPayment(payment);
	}

	@Override
	protected Payment testGraphQLPayment_addPayment() throws Exception {
		return _addPayment(randomPayment());
	}

	@Override
	protected Payment testPatchPayment_addPayment() throws Exception {
		return _addPayment(randomPayment());
	}

	@Override
	protected Payment testPatchPaymentByExternalReferenceCode_addPayment()
		throws Exception {

		return _addPayment(randomPayment());
	}

	@Override
	protected Payment testPostPayment_addPayment(Payment payment)
		throws Exception {

		return _addPayment(payment);
	}

	@Override
	protected Payment testPutPaymentByExternalReferenceCode_addPayment()
		throws Exception {

		return _addPayment(randomPayment());
	}

	private Payment _addPayment(Payment payment) throws Exception {
		CommercePaymentEntry commercePaymentEntry = _getCommercePaymentEntry(
			payment);

		return new Payment() {
			{
				amount = commercePaymentEntry.getAmount();
				callbackURL = commercePaymentEntry.getCallbackURL();
				cancelURL = commercePaymentEntry.getCancelURL();
				channelId = commercePaymentEntry.getCommerceChannelId();
				comment = commercePaymentEntry.getNote();
				createDate = commercePaymentEntry.getCreateDate();
				currencyCode = commercePaymentEntry.getCurrencyCode();
				externalReferenceCode = payment.getExternalReferenceCode();
				id = commercePaymentEntry.getCommercePaymentEntryId();
				languageId = commercePaymentEntry.getLanguageId();
				paymentIntegrationKey =
					commercePaymentEntry.getPaymentIntegrationKey();
				paymentIntegrationType =
					commercePaymentEntry.getPaymentIntegrationType();
				reasonKey = commercePaymentEntry.getReasonKey();
				relatedItemId = commercePaymentEntry.getClassPK();
				relatedItemName = commercePaymentEntry.getClassName();
				transactionCode = commercePaymentEntry.getTransactionCode();
				type = commercePaymentEntry.getType();
			}
		};
	}

	private CommercePaymentEntry _getCommercePaymentEntry(Payment payment)
		throws Exception {

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addCommercePaymentEntry(
				_serviceContext.getUserId(),
				_classNameLocalService.getClassNameId(
					payment.getRelatedItemName()),
				GetterUtil.getLong(payment.getRelatedItemId()),
				GetterUtil.getLong(payment.getChannelId()),
				(BigDecimal)GetterUtil.getNumber(payment.getAmount()),
				payment.getCallbackURL(), payment.getCancelURL(),
				payment.getCurrencyCode(), payment.getLanguageId(),
				payment.getPayload(), payment.getComment(),
				payment.getPaymentIntegrationKey(),
				GetterUtil.getInteger(payment.getPaymentIntegrationType()),
				payment.getReasonKey(), payment.getTransactionCode(),
				GetterUtil.getInteger(payment.getType()), _serviceContext);

		commercePaymentEntry.setExternalReferenceCode(
			payment.getExternalReferenceCode());

		return _commercePaymentEntryLocalService.updateCommercePaymentEntry(
			commercePaymentEntry);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

	private ServiceContext _serviceContext;

}