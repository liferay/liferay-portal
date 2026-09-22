/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.feature.flag.test.util.FeatureFlagTestHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Lima
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class FeatureFlagApplicationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_featureFlagTestHelper = new FeatureFlagTestHelper();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_featureFlagTestHelper.tearDown();
	}

	@Before
	public void setUp() throws Exception {
		_adminUser = TestPropsValues.getUser();

		_regularUser = UserTestUtil.addUser(
			CompanyLocalServiceUtil.getCompany(TestPropsValues.getCompanyId()),
			_REGULAR_USER_PASSWORD);
	}

	@Test
	public void testConfirm() throws Exception {
		_testConfirmWithAuthorizedUser(
			CompanyConstants.SYSTEM,
			FeatureFlagTestHelper.FEATURE_FLAG_KEY_SYSTEM);
		_testConfirmWithAuthorizedUser(
			TestPropsValues.getCompanyId(),
			FeatureFlagTestHelper.FEATURE_FLAG_KEY_1);
		_testConfirmWithCompanyId(CompanyConstants.SYSTEM);
		_testConfirmWithCompanyId(RandomTestUtil.randomLong());
		_testConfirmWithNonexistentKey();
		_testConfirmWithUnauthorizedUser(
			CompanyConstants.SYSTEM,
			FeatureFlagTestHelper.FEATURE_FLAG_KEY_SYSTEM);
		_testConfirmWithUnauthorizedUser(
			TestPropsValues.getCompanyId(),
			FeatureFlagTestHelper.FEATURE_FLAG_KEY_1);
	}

	private Http.Response _getResponse(
			boolean enabled, String key, String password, User user)
		throws Exception {

		return _getResponse(null, enabled, key, password, user);
	}

	private Http.Response _getResponse(
			Long companyId, boolean enabled, String key, String password,
			User user)
		throws Exception {

		String credentials = StringBundler.concat(
			user.getEmailAddress(), StringPool.COLON, password);

		String encodedCredentials = Base64.encode(credentials.getBytes());

		Http.Options options = new Http.Options();

		options.addHeader("Authorization", "Basic " + encodedCredentials);

		if (companyId != null) {
			options.addPart("companyId", String.valueOf(companyId));
		}

		options.addPart("enabled", String.valueOf(enabled));
		options.addPart("key", key);
		options.setLocation(
			StringBundler.concat(
				"http://localhost:", PortalUtil.getPortalServerPort(false),
				"/o/com-liferay-feature-flag-web/set-enabled"));
		options.setPost(true);

		_http.URLtoString(options);

		return options.getResponse();
	}

	private void _testConfirmWithAuthorizedUser(long companyId, String key)
		throws Exception {

		boolean featureFlagValue = _featureFlagTestHelper.getFeatureFlagValue(
			companyId, key);

		Http.Response response = _getResponse(
			!featureFlagValue, key, PropsValues.DEFAULT_ADMIN_PASSWORD,
			_adminUser);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, response.getResponseCode());

		Assert.assertEquals(
			!featureFlagValue,
			_featureFlagTestHelper.getFeatureFlagValue(companyId, key));
	}

	private void _testConfirmWithCompanyId(long companyId) throws Exception {
		String key = FeatureFlagTestHelper.FEATURE_FLAG_KEY_1;

		boolean featureFlagValue = _featureFlagTestHelper.getFeatureFlagValue(
			TestPropsValues.getCompanyId(), key);

		Http.Response response = _getResponse(
			companyId, !featureFlagValue, key,
			PropsValues.DEFAULT_ADMIN_PASSWORD, _adminUser);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, response.getResponseCode());

		Assert.assertEquals(
			!featureFlagValue,
			_featureFlagTestHelper.getFeatureFlagValue(
				TestPropsValues.getCompanyId(), key));
	}

	private void _testConfirmWithNonexistentKey() throws Exception {
		Http.Response response = _getResponse(
			true, RandomTestUtil.randomString(),
			PropsValues.DEFAULT_ADMIN_PASSWORD, _adminUser);

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND, response.getResponseCode());
	}

	private void _testConfirmWithUnauthorizedUser(long companyId, String key)
		throws Exception {

		boolean featureFlagValue = _featureFlagTestHelper.getFeatureFlagValue(
			companyId, key);

		Http.Response response = _getResponse(
			!featureFlagValue, key, _REGULAR_USER_PASSWORD, _regularUser);

		Assert.assertEquals(
			HttpServletResponse.SC_FORBIDDEN, response.getResponseCode());

		Assert.assertEquals(
			featureFlagValue,
			_featureFlagTestHelper.getFeatureFlagValue(companyId, key));
	}

	private static final String _REGULAR_USER_PASSWORD =
		RandomTestUtil.randomString();

	private static FeatureFlagTestHelper _featureFlagTestHelper;

	private User _adminUser;

	@Inject
	private Http _http;

	@DeleteAfterTestRun
	private User _regularUser;

}