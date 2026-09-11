/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.token.endpoint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.PasswordAuthorizationGrant;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.fips.FIPSApplicationState;
import com.liferay.portal.kernel.security.fips.FIPSApplicationStateMachineUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;

import jakarta.ws.rs.core.Response;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class FIPSFederationTokenAuditTest extends BaseTokenEndpointTestCase {

	@Test
	public void test() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.kernel.internal.log4j.FIPSLog4jUtil",
				LoggerTestUtil.WARN)) {

			Response response = _getTokenResponse();

			Assert.assertEquals(200, response.getStatus());

			List<String> messages = _getFederationTokenRejectedMessages(
				logCapture);

			Assert.assertTrue(messages.isEmpty());

			try (SafeCloseable safeCloseable =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"FIPS_ENABLED", true)) {

				response = _getTokenResponse();

				Assert.assertEquals(401, response.getStatus());

				messages = _getFederationTokenRejectedMessages(logCapture);

				Assert.assertEquals(messages.toString(), 1, messages.size());

				String message = messages.get(0);

				FIPSApplicationState fipsApplicationState =
					FIPSApplicationStateMachineUtil.getFIPSApplicationState();

				Assert.assertTrue(
					message.contains(
						"fips-state=" + fipsApplicationState.name()));

				Assert.assertTrue(
					message.contains("receiving-endpoint=/o/oauth2/token"));
				Assert.assertTrue(message.contains("rejected-value=HS256"));
				Assert.assertTrue(
					message.contains("token-issuer=" + TEST_CLIENT_ID_2));
				Assert.assertTrue(message.contains("token-type=JWT"));
			}
		}
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new TestPreparatorBundleActivator() {
		};
	}

	private List<String> _getFederationTokenRejectedMessages(
		LogCapture logCapture) {

		return TransformUtil.transform(
			logCapture.getMessages(),
			message ->
				message.contains("event-type=federation-token-rejected") ?
					message : null);
	}

	private Response _getTokenResponse() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		return getTokenResponse(
			new PasswordAuthorizationGrant(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			clientAuthentications.get(TEST_CLIENT_ID_2));
	}

}