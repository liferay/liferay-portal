/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class GrantAuthorizationCodeTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		Function<WebTarget, Invocation.Builder> invocationBuilderFunction =
			getAuthenticatedInvocationBuilderFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				null);

		Function<Function<WebTarget, Invocation.Builder>, Response>
			codeFunction = getCodeFunction(
				webTarget -> webTarget.queryParam(
					"client_id", _CLIENT_ID
				).queryParam(
					"response_type", "code"
				));

		ExecutorService executorService = Executors.newFixedThreadPool(
			_THREADS_COUNT);

		try {
			for (int i = 0; i < _ATTEMPTS_COUNT; i++) {
				String authorizationCode = parseAuthorizationCodeString(
					codeFunction.apply(invocationBuilderFunction));

				BiFunction<String, Invocation.Builder, Response>
					exchangeAuthorizationCodeBiFunction =
						getExchangeAuthorizationCodeBiFunction(
							authorizationCode, null);

				CyclicBarrier cyclicBarrier = new CyclicBarrier(_THREADS_COUNT);
				List<Future<Response>> futures = new ArrayList<>();

				for (int j = 0; j < _THREADS_COUNT; j++) {
					futures.add(
						executorService.submit(
							() -> {
								Invocation.Builder invocationBuilder =
									getTokenInvocationBuilder(null);

								cyclicBarrier.await();

								return exchangeAuthorizationCodeBiFunction.
									apply(_CLIENT_ID, invocationBuilder);
							}));
				}

				int tokensCount = 0;

				for (Future<Response> future : futures) {
					Response response = future.get();

					if (response.getStatus() == 200) {
						Assert.assertTrue(
							Validator.isNotNull(parseTokenString(response)));

						tokensCount++;
					}
					else {
						Assert.assertEquals(
							"invalid_grant", parseError(response));
					}
				}

				Assert.assertEquals(1, tokensCount);
			}
		}
		finally {
			executorService.shutdownNow();
		}
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new GrantAuthorizationCodeTestPreparatorBundleActivator();
	}

	private static final int _ATTEMPTS_COUNT = 5;

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	private static final int _THREADS_COUNT = 8;

	private User _user;

	private class GrantAuthorizationCodeTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, _CLIENT_ID,
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList("everything"));
		}

	}

}