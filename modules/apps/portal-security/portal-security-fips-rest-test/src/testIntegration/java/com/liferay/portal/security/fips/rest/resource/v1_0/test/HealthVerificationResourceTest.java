/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Base64;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class HealthVerificationResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyId = TestPropsValues.getCompanyId();
	}

	@Test
	public void testAdminCallerGets409() throws Exception {
		User user = _addUserWithKnownPassword();

		Role role = RoleLocalServiceUtil.getRole(
			_companyId, RoleConstants.ADMINISTRATOR);

		RoleLocalServiceUtil.addUserRole(user.getUserId(), role.getRoleId());

		int responseCode = _invoke(user.getEmailAddress(), _PASSWORD);

		// FIPS is disabled on a normal bundle, so the endpoint reports
		// NOT_APPLICABLE as HTTP 409.

		Assert.assertEquals(HttpURLConnection.HTTP_CONFLICT, responseCode);
	}

	@Test
	public void testUnauthorizedCallerGets403() throws Exception {
		User user = _addUserWithKnownPassword();

		int responseCode = _invoke(user.getEmailAddress(), _PASSWORD);

		Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, responseCode);
	}

	private User _addUserWithKnownPassword() throws Exception {
		return UserTestUtil.addUser(
			CompanyLocalServiceUtil.getCompany(_companyId), _PASSWORD);
	}

	private int _invoke(String emailAddress, String password) throws Exception {
		URL url = new URL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/crypto-health/v1.0/health-verifications");

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestMethod("POST");

		Base64.Encoder encoder = Base64.getEncoder();

		String credentials = emailAddress + ":" + password;

		String encodedCredentials = encoder.encodeToString(
			credentials.getBytes());

		httpURLConnection.setRequestProperty(
			"Authorization", "Basic " + encodedCredentials);

		httpURLConnection.setRequestProperty("Accept", "application/json");

		return httpURLConnection.getResponseCode();
	}

	private static final String _PASSWORD = "test";

	private long _companyId;

}