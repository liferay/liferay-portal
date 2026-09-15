/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.security.auth.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
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
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.security.fips.test.util.FIPSAuditTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 * @author Jorge García Jiménez
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

			_testOnFailureByEmailAddress();
			_testOnFailureByEmailAddressWhenLockedOut();
			_testOnFailureByEmailAddressWithoutCryptoOfficerRole();
		}
	}

	@Test
	public void testOnFailureByScreenName() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_portalInstanceLifecycleListener.portalInstanceRegistered(_company);

			User user = _addCryptoOfficerUser();

			_authenticateByScreenName(user);

			List<JSONObject> jsonObjects = _getAuthAttemptFailureJSONObjects(
				user);

			Assert.assertEquals(jsonObjects.toString(), 1, jsonObjects.size());
		}
	}

	@Test
	public void testOnFailureByUserId() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_portalInstanceLifecycleListener.portalInstanceRegistered(_company);

			User user = _addCryptoOfficerUser();

			_authenticateByUserId(user);

			List<JSONObject> jsonObjects = _getAuthAttemptFailureJSONObjects(
				user);

			Assert.assertEquals(jsonObjects.toString(), 1, jsonObjects.size());
		}
	}

	private User _addCryptoOfficerUser() throws Exception {
		User user = _addUser();

		Role role = _roleLocalService.fetchRole(
			_company.getCompanyId(), RoleConstants.CRYPTO_OFFICER);

		_roleLocalService.addUserRoles(
			user.getUserId(), new long[] {role.getRoleId()});

		return user;
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser(_company);

		_users.add(user);

		return user;
	}

	private void _authenticateByEmailAddress(User user) throws Exception {
		Assert.assertEquals(
			Authenticator.FAILURE,
			_userLocalService.authenticateByEmailAddress(
				_company.getCompanyId(), user.getEmailAddress(),
				RandomTestUtil.randomString(), Collections.emptyMap(),
				Collections.emptyMap(), null));
	}

	private void _authenticateByScreenName(User user) throws Exception {
		Assert.assertEquals(
			Authenticator.FAILURE,
			_userLocalService.authenticateByScreenName(
				_company.getCompanyId(), user.getScreenName(),
				RandomTestUtil.randomString(), Collections.emptyMap(),
				Collections.emptyMap(), null));
	}

	private void _authenticateByUserId(User user) throws Exception {
		Assert.assertEquals(
			Authenticator.FAILURE,
			_userLocalService.authenticateByUserId(
				_company.getCompanyId(), user.getUserId(),
				RandomTestUtil.randomString(), Collections.emptyMap(),
				Collections.emptyMap(), null));
	}

	private List<JSONObject> _getAuthAttemptFailureJSONObjects(User user)
		throws Exception {

		String userId = String.valueOf(user.getUserId());

		return ListUtil.filter(
			FIPSAuditTestUtil.getJSONObjects(),
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

	private void _testOnFailureByEmailAddress() throws Exception {
		User user = _addCryptoOfficerUser();

		for (int i = 0; i < 3; i++) {
			_authenticateByEmailAddress(user);
		}

		List<JSONObject> jsonObjects = _getAuthAttemptFailureJSONObjects(user);

		Assert.assertEquals(jsonObjects.toString(), 3, jsonObjects.size());

		for (int i = 0; i < jsonObjects.size(); i++) {
			JSONObject jsonObject = jsonObjects.get(i);

			Assert.assertEquals("WARNING", jsonObject.getString("severity"));

			JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");

			Assert.assertEquals(
				String.valueOf(user.getUserId()),
				fieldsJSONObject.getString("attempted-user-id"));
			Assert.assertEquals(
				"local", fieldsJSONObject.getString("authentication-method"));
			Assert.assertEquals(
				i + 1, fieldsJSONObject.getInt("consecutive-failure-count"));
			Assert.assertEquals(
				"bad-credential", fieldsJSONObject.getString("failure-reason"));
		}
	}

	private void _testOnFailureByEmailAddressWhenLockedOut() throws Exception {
		User user = _addCryptoOfficerUser();

		_userLocalService.updateLockoutByEmailAddress(
			_company.getCompanyId(), user.getEmailAddress(), true);

		_authenticateByEmailAddress(user);

		List<JSONObject> jsonObjects = _getAuthAttemptFailureJSONObjects(user);

		Assert.assertEquals(jsonObjects.toString(), 1, jsonObjects.size());

		JSONObject jsonObject = jsonObjects.get(0);

		JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");

		Assert.assertEquals(
			"locked", fieldsJSONObject.getString("failure-reason"));
	}

	private void _testOnFailureByEmailAddressWithoutCryptoOfficerRole()
		throws Exception {

		User user = _addUser();

		_authenticateByEmailAddress(user);

		Assert.assertTrue(
			_getAuthAttemptFailureJSONObjects(
				user
			).isEmpty());
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

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}