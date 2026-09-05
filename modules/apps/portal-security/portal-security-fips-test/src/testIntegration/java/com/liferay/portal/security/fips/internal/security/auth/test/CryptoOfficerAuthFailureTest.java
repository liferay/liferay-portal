/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.security.auth.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class CryptoOfficerAuthFailureTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
	}

	@Test
	public void testOnFailureByEmailAddress() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_portalInstanceLifecycleListener.portalInstanceRegistered(_company);

			User cryptoOfficerUser = _addUser(true);
			User user = _addUser(false);

			for (int i = 0; i < 3; i++) {
				_authenticateWithWrongPassword(cryptoOfficerUser);
			}

			List<JSONObject> cryptoOfficerJSONObjects =
				_getAuthAttemptFailureJSONObjects(cryptoOfficerUser);

			Assert.assertEquals(
				cryptoOfficerJSONObjects.toString(), 3,
				cryptoOfficerJSONObjects.size());

			for (int i = 0; i < cryptoOfficerJSONObjects.size(); i++) {
				JSONObject jsonObject = cryptoOfficerJSONObjects.get(i);

				Assert.assertEquals(
					"WARNING", jsonObject.getString("severity"));

				JSONObject fieldsJSONObject = jsonObject.getJSONObject(
					"fields");

				Assert.assertEquals(
					String.valueOf(cryptoOfficerUser.getUserId()),
					fieldsJSONObject.getString("attempted-user-id"));
				Assert.assertEquals(
					"local",
					fieldsJSONObject.getString("authentication-method"));
				Assert.assertEquals(
					"bad-credential",
					fieldsJSONObject.getString("failure-reason"));
				Assert.assertEquals(
					i + 1,
					fieldsJSONObject.getInt("consecutive-failure-count"));
			}

			_authenticateWithWrongPassword(user);

			Assert.assertTrue(
				_getAuthAttemptFailureJSONObjects(
					user
				).isEmpty());

			_userLocalService.updateLockoutByEmailAddress(
				_company.getCompanyId(), cryptoOfficerUser.getEmailAddress(),
				true);

			_authenticateWithWrongPassword(cryptoOfficerUser);

			List<JSONObject> updatedJSONObjects =
				_getAuthAttemptFailureJSONObjects(cryptoOfficerUser);

			JSONObject lastJSONObject = updatedJSONObjects.get(
				updatedJSONObjects.size() - 1);

			Assert.assertEquals(
				"locked",
				lastJSONObject.getJSONObject(
					"fields"
				).getString(
					"failure-reason"
				));
		}
	}

	private User _addUser(boolean cryptoOfficer) throws Exception {
		User user = UserTestUtil.addUser(_company);

		if (cryptoOfficer) {
			Role role = _roleLocalService.fetchRole(
				_company.getCompanyId(), RoleConstants.CRYPTO_OFFICER);

			_roleLocalService.addUserRoles(
				user.getUserId(), new long[] {role.getRoleId()});
		}

		return user;
	}

	private void _authenticateWithWrongPassword(User user) throws Exception {
		Map<String, String[]> headerMap = Collections.emptyMap();
		Map<String, String[]> parameterMap = Collections.emptyMap();

		int authResult = _userLocalService.authenticateByEmailAddress(
			_company.getCompanyId(), user.getEmailAddress(),
			RandomTestUtil.randomString(), headerMap, parameterMap, null);

		Assert.assertEquals(Authenticator.FAILURE, authResult);
	}

	private List<JSONObject> _getAuthAttemptFailureJSONObjects(User user)
		throws Exception {

		List<JSONObject> jsonObjects = TransformUtil.unsafeTransform(
			Files.readAllLines(_getFIPSAuditLogPath()),
			JSONFactoryUtil::createJSONObject);

		String userId = String.valueOf(user.getUserId());

		return ListUtil.filter(
			jsonObjects,
			jsonObject -> {
				if (!Objects.equals(
						jsonObject.getString("event-type"),
						"auth-attempt-failure")) {

					return false;
				}

				JSONObject fieldsJSONObject = jsonObject.getJSONObject(
					"fields");

				return Objects.equals(
					userId, fieldsJSONObject.getString("attempted-user-id"));
			});
	}

	private Path _getFIPSAuditLogPath() {
		LocalDate localDate = LocalDate.now(ZoneOffset.UTC);

		return Paths.get(
			PropsValues.LIFERAY_HOME, "logs",
			StringBundler.concat("fips-audit.", localDate, ".ndjson"));
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.security.fips.internal.instance.lifecycle.FIPSPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}