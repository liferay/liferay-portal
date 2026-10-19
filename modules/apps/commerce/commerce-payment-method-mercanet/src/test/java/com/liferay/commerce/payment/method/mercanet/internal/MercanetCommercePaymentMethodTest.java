/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal;

import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.payment.method.mercanet.internal.constants.MercanetCommercePaymentMethodConstants;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.result.CommercePaymentResult;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.math.BigDecimal;

import java.net.URL;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Stefano Motta
 */
public class MercanetCommercePaymentMethodTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCancelPayment() throws Exception {
		long commerceOrderId = RandomTestUtil.randomLong();
		String transactionId = RandomTestUtil.randomString();

		try {
			_mercanetCommercePaymentMethod.cancelPayment(
				new CommercePaymentRequest(
					BigDecimal.TEN, null, commerceOrderId, null, null,
					transactionId));

			Assert.fail();
		}
		catch (ClassCastException classCastException) {
			Assert.assertNotNull(classCastException);
		}

		_assertCommercePaymentResult(
			commerceOrderId,
			_mercanetCommercePaymentMethod.cancelPayment(
				new MercanetCommercePaymentRequest(
					BigDecimal.TEN, null, commerceOrderId, null, null, null,
					transactionId)),
			CommerceOrderPaymentConstants.STATUS_CANCELLED, transactionId);
	}

	@Test
	public void testCompletePayment() throws Exception {
		long commerceOrderId = RandomTestUtil.randomLong();
		String transactionId = RandomTestUtil.randomString();

		try {
			_mercanetCommercePaymentMethod.completePayment(
				new CommercePaymentRequest(
					BigDecimal.TEN, null, commerceOrderId, null, null,
					transactionId));

			Assert.fail();
		}
		catch (ClassCastException classCastException) {
			Assert.assertNotNull(classCastException);
		}

		_assertCommercePaymentResult(
			commerceOrderId,
			_mercanetCommercePaymentMethod.completePayment(
				new MercanetCommercePaymentRequest(
					BigDecimal.TEN, null, commerceOrderId, null, null, null,
					transactionId)),
			CommerceOrderPaymentConstants.STATUS_COMPLETED, transactionId);
	}

	@Test
	public void testGetAutomaticResponseURL() throws Exception {
		Map<String, String[]> parameterMap = _getParameterMap(null);

		Assert.assertEquals(
			StringBundler.concat(
				_BASE_URL, "?groupId=",
				MapUtil.getString(parameterMap, "groupId"),
				"&type=automatic&uuid=",
				MapUtil.getString(parameterMap, "uuid")),
			String.valueOf(
				ReflectionTestUtil.<URL>invoke(
					_mercanetCommercePaymentMethod, "_getAutomaticResponseURL",
					new Class<?>[] {URL.class, Map.class}, new URL(_BASE_URL),
					parameterMap)));
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(
			"mercanet", _mercanetCommercePaymentMethod.getKey());
	}

	@Test
	public void testGetNormalReturnURL() throws Exception {
		Map<String, String[]> parameterMap = _getParameterMap(null);

		Assert.assertEquals(
			StringBundler.concat(
				_BASE_URL, "?groupId=",
				MapUtil.getString(parameterMap, "groupId"), "&redirect=",
				URLCodec.encodeURL(MapUtil.getString(parameterMap, "redirect")),
				"&type=normal&uuid=", MapUtil.getString(parameterMap, "uuid")),
			String.valueOf(
				ReflectionTestUtil.<URL>invoke(
					_mercanetCommercePaymentMethod, "_getNormalReturnURL",
					new Class<?>[] {URL.class, Map.class}, new URL(_BASE_URL),
					parameterMap)));

		parameterMap = _getParameterMap(RandomTestUtil.randomString() + "+/=");

		Assert.assertEquals(
			StringBundler.concat(
				_BASE_URL, "?groupId=",
				MapUtil.getString(parameterMap, "groupId"), "&guestToken=",
				URLCodec.encodeURL(
					MapUtil.getString(parameterMap, "guestToken")),
				"&redirect=",
				URLCodec.encodeURL(MapUtil.getString(parameterMap, "redirect")),
				"&type=normal&uuid=", MapUtil.getString(parameterMap, "uuid")),
			String.valueOf(
				ReflectionTestUtil.<URL>invoke(
					_mercanetCommercePaymentMethod, "_getNormalReturnURL",
					new Class<?>[] {URL.class, Map.class}, new URL(_BASE_URL),
					parameterMap)));
	}

	@Test
	public void testGetPaymentType() {
		Assert.assertEquals(
			CommercePaymentMethodConstants.TYPE_ONLINE_REDIRECT,
			_mercanetCommercePaymentMethod.getPaymentType());
	}

	@Test
	public void testGetServletPath() {
		Assert.assertEquals(
			MercanetCommercePaymentMethodConstants.SERVLET_PATH,
			_mercanetCommercePaymentMethod.getServletPath());
	}

	@Test
	public void testIsCancelEnabled() {
		Assert.assertTrue(_mercanetCommercePaymentMethod.isCancelEnabled());
	}

	@Test
	public void testIsCompleteEnabled() {
		Assert.assertTrue(_mercanetCommercePaymentMethod.isCompleteEnabled());
	}

	@Test
	public void testIsProcessPaymentEnabled() {
		Assert.assertTrue(
			_mercanetCommercePaymentMethod.isProcessPaymentEnabled());
	}

	private void _assertCommercePaymentResult(
		long commerceOrderId, CommercePaymentResult commercePaymentResult,
		int newPaymentStatus, String transactionId) {

		Assert.assertEquals(
			transactionId, commercePaymentResult.getAuthTransactionId());
		Assert.assertEquals(
			commerceOrderId, commercePaymentResult.getCommerceOrderId());
		Assert.assertEquals(
			newPaymentStatus, commercePaymentResult.getNewPaymentStatus());
		Assert.assertNull(commercePaymentResult.getRedirectUrl());
		Assert.assertFalse(commercePaymentResult.isOnlineRedirect());
		Assert.assertTrue(commercePaymentResult.isSuccess());
	}

	private Map<String, String[]> _getParameterMap(String guestToken) {
		return HashMapBuilder.put(
			"groupId",
			new String[] {String.valueOf(RandomTestUtil.randomLong())}
		).put(
			"guestToken",
			() -> {
				if (Validator.isNull(guestToken)) {
					return null;
				}

				return new String[] {guestToken};
			}
		).put(
			"redirect",
			new String[] {
				"https://liferay.com/web/guest/checkout?a=" +
					RandomTestUtil.randomString() + "&b=c"
			}
		).put(
			"uuid", new String[] {RandomTestUtil.randomString()}
		).build();
	}

	private static final String _BASE_URL =
		"https://liferay.com/o/mercanet-payment";

	private final MercanetCommercePaymentMethod _mercanetCommercePaymentMethod =
		new MercanetCommercePaymentMethod();

}