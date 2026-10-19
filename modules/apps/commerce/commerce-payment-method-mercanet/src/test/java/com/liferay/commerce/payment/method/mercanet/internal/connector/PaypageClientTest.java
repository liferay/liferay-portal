/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal.connector;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.worldline.sips.model.InitializationResponse;
import com.worldline.sips.model.PaypageResponse;
import com.worldline.sips.model.ResponseData;
import com.worldline.sips.util.SealCalculator;

import org.apache.commons.codec.digest.DigestUtils;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Stefano Motta
 */
public class PaypageClientTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDecodeResponse() throws Exception {
		PaypageClient paypageClient = new PaypageClient(
			Environment.TEST, _MERCHANT_ID, 1, _SECRET_KEY);

		String transactionReference = RandomTestUtil.randomString();

		String data = StringBundler.concat(
			"merchantId=", _MERCHANT_ID,
			"|responseCode=00|transactionReference=", transactionReference);

		PaypageResponse paypageResponse = paypageClient.decodeResponse(
			HashMapBuilder.put(
				"Data", data
			).put(
				"Seal", DigestUtils.sha256Hex(data + _SECRET_KEY)
			).build());

		ResponseData responseData = paypageResponse.getData();

		Assert.assertEquals(_MERCHANT_ID, responseData.getMerchantId());
		Assert.assertEquals(
			transactionReference, responseData.getTransactionReference());

		_assertDecodeResponseFails(data, paypageClient, null);
		_assertDecodeResponseFails(
			data, paypageClient,
			DigestUtils.sha256Hex(data + RandomTestUtil.randomString()));
		_assertDecodeResponseFails(
			data, paypageClient,
			DigestUtils.sha256Hex(
				StringUtil.replace(data, "responseCode=00", "responseCode=05") +
					_SECRET_KEY));
		_assertDecodeResponseFails(
			data,
			new PaypageClient(
				Environment.TEST, _MERCHANT_ID, 1,
				RandomTestUtil.randomString()),
			DigestUtils.sha256Hex(data + _SECRET_KEY));
	}

	@Test
	public void testPaypageClient() throws Exception {
		_assertPaypageClientFails(
			null, 1, _MERCHANT_ID, "Invalid environment specified",
			_SECRET_KEY);
		_assertPaypageClientFails(
			Environment.TEST, 1, null, "Invalid merchant ID specified",
			_SECRET_KEY);
		_assertPaypageClientFails(
			Environment.TEST, 1, "", "Invalid merchant ID specified",
			_SECRET_KEY);
		_assertPaypageClientFails(
			Environment.TEST, null, _MERCHANT_ID,
			"Invalid key version specified", _SECRET_KEY);
		_assertPaypageClientFails(
			Environment.TEST, 1, _MERCHANT_ID, "Invalid key specified", null);
		_assertPaypageClientFails(
			Environment.TEST, 1, _MERCHANT_ID, "Invalid key specified", "");
	}

	@Test
	public void testVerifySeal() throws Exception {
		PaypageClient paypageClient = new PaypageClient(
			Environment.TEST, _MERCHANT_ID, 1, _SECRET_KEY);

		InitializationResponse initializationResponse =
			new InitializationResponse();

		initializationResponse.setRedirectionData(
			RandomTestUtil.randomString());

		_verifySeal(initializationResponse, paypageClient);

		initializationResponse.setSeal(
			SealCalculator.calculate(
				SealCalculator.getSealString(initializationResponse),
				_SECRET_KEY));

		_verifySeal(initializationResponse, paypageClient);

		initializationResponse.setSeal(
			SealCalculator.calculate(
				SealCalculator.getSealString(initializationResponse),
				RandomTestUtil.randomString()));

		try {
			_verifySeal(initializationResponse, paypageClient);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"The initialization response has been tampered with",
				exception.getMessage());
		}
	}

	private void _assertDecodeResponseFails(
		String data, PaypageClient paypageClient, String seal) {

		try {
			paypageClient.decodeResponse(
				HashMapBuilder.put(
					"Data", data
				).put(
					"Seal", seal
				).build());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"The payment page response has been tampered with",
				exception.getMessage());
		}
	}

	private void _assertPaypageClientFails(
		Environment environment, Integer keyVersion, String merchantId,
		String message, String secretKey) {

		try {
			new PaypageClient(environment, merchantId, keyVersion, secretKey);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(message, exception.getMessage());
		}
	}

	private void _verifySeal(
		InitializationResponse initializationResponse,
		PaypageClient paypageClient) {

		ReflectionTestUtil.invoke(
			paypageClient, "_verifySeal",
			new Class<?>[] {InitializationResponse.class},
			initializationResponse);
	}

	private static final String _MERCHANT_ID = RandomTestUtil.randomString();

	private static final String _SECRET_KEY = RandomTestUtil.randomString();

}