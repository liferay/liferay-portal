/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.helper;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderWrapper;
import com.liferay.commerce.payment.internal.request.DefaultCommercePaymentRequestProvider;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.request.CommercePaymentRequestProvider;
import com.liferay.commerce.service.CommerceOrderLocalServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.math.BigDecimal;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class CommercePaymentHelperImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetCommercePaymentRequestProviderUnknownKey()
		throws Exception {

		CommercePaymentHelperImpl commercePaymentHelperImpl =
			new CommercePaymentHelperImpl();

		ReflectionTestUtil.setFieldValue(
			commercePaymentHelperImpl, "_defaultCommercePaymentRequestProvider",
			new DefaultCommercePaymentRequestProvider(
				new CommerceOrderLocalServiceWrapper(null) {

					@Override
					public CommerceOrder getCommerceOrder(
						long commerceOrderId) {

						return new CommerceOrderWrapper(null) {

							@Override
							public BigDecimal getTotal() {
								return new BigDecimal("12.5");
							}

						};
					}

				}));

		ReflectionTestUtil.setFieldValue(
			commercePaymentHelperImpl, "_serviceTrackerMap",
			new ServiceTrackerMap<String, CommercePaymentRequestProvider>() {

				@Override
				public void close() {
				}

				@Override
				public boolean containsKey(String key) {
					return false;
				}

				@Override
				public CommercePaymentRequestProvider getService(String key) {
					return null;
				}

				@Override
				public Set<String> keySet() {
					return Collections.emptySet();
				}

				@Override
				public Collection<CommercePaymentRequestProvider> values() {
					return Collections.emptyList();
				}

			});

		CommercePaymentRequestProvider commercePaymentRequestProvider =
			commercePaymentHelperImpl.getCommercePaymentRequestProvider(
				new CommercePaymentMethod() {

					@Override
					public String getDescription(Locale locale) {
						return null;
					}

					@Override
					public String getKey() {
						return RandomTestUtil.randomString();
					}

					@Override
					public String getName(Locale locale) {
						return null;
					}

					@Override
					public int getPaymentType() {
						return 0;
					}

					@Override
					public String getServletPath() {
						return null;
					}

				});

		CommercePaymentRequest commercePaymentRequest =
			commercePaymentRequestProvider.getCommercePaymentRequest(
				null, RandomTestUtil.randomLong(), null, LocaleUtil.US, null,
				RandomTestUtil.randomString());

		Assert.assertEquals(
			new BigDecimal("12.5"), commercePaymentRequest.getAmount());
	}

}