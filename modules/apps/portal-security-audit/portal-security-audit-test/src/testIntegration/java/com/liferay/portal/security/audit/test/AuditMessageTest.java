/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.BigEndianCodec;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.ChecksumUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterChain;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;
import com.liferay.portal.servlet.filters.secure.SecureFilter;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpSession;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Christian Moura
 */
@FeatureFlag("LPD-6417")
@RunWith(Arquillian.class)
public class AuditMessageTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_impersonatedUser = UserTestUtil.addUser();
		_user = TestPropsValues.getUser();
	}

	@After
	public void tearDown() {
		AuditRequestContextThreadLocal.removeAuditRequestContext();

		PrincipalThreadLocal.setName(null);
		PrincipalThreadLocal.setPassword(null);
	}

	@Test
	public void testConstructor() throws Exception {
		_assertImpersonatedUser(
			_getAuditMessage(String.valueOf(_impersonatedUser.getUserId())));
		_assertNoImpersonatedUser(
			_getAuditMessage(String.valueOf(_user.getUserId())));
		_assertNoImpersonatedUser(
			_getAuditMessage(
				String.valueOf(
					_userLocalService.getGuestUserId(_user.getCompanyId()))));
		_assertNoImpersonatedUser(_getAuditMessage(null));

		_testConstructorWhenFeatureFlagIsDisabled();
		_testConstructorWithHeadlessRequest();
		_testConstructorWithHeadlessRequestWithoutCSRFToken();
		_testConstructorWithPortalRequest();
	}

	private void _assertImpersonatedUser(AuditMessage auditMessage) {
		Assert.assertTrue(auditMessage.isImpersonated());
		Assert.assertEquals(
			_impersonatedUser.getEmailAddress(),
			auditMessage.getImpersonatedUserEmailAddress());
		Assert.assertEquals(
			_impersonatedUser.getUserId(),
			auditMessage.getImpersonatedUserId());
		Assert.assertEquals(
			_impersonatedUser.getFullName(),
			auditMessage.getImpersonatedUserName());
	}

	private void _assertNoImpersonatedUser(AuditMessage auditMessage) {
		Assert.assertFalse(auditMessage.isImpersonated());
		Assert.assertNull(auditMessage.getImpersonatedUserEmailAddress());
		Assert.assertEquals(0, auditMessage.getImpersonatedUserId());
		Assert.assertNull(auditMessage.getImpersonatedUserName());
	}

	private AuditMessage _getAuditMessage(
			Filter filter, MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		AuditRequestContextThreadLocal.removeAuditRequestContext();

		PrincipalThreadLocal.setName(null);

		AtomicReference<AuditMessage> auditMessageAtomicReference =
			new AtomicReference<>();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_user.getCompanyId())) {

			InvokerFilterChain invokerFilterChain = new InvokerFilterChain(
				(servletRequest, servletResponse) ->
					auditMessageAtomicReference.set(
						new AuditMessage(
							_user.getCompanyId(), _user.getUserId(),
							_user.getFullName(),
							RandomTestUtil.randomString())));

			invokerFilterChain.addFilter(_auditFilter);
			invokerFilterChain.addFilter(filter);

			invokerFilterChain.doFilter(
				mockHttpServletRequest, new MockHttpServletResponse());
		}

		return auditMessageAtomicReference.get();
	}

	private AuditMessage _getAuditMessage(String principalName) {
		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		auditRequestContext.setRealUserId(_user.getUserId());

		PrincipalThreadLocal.setName(principalName);

		return new AuditMessage(
			_user.getCompanyId(), _user.getUserId(), _user.getFullName(),
			RandomTestUtil.randomString());
	}

	private AuditMessage _getHeadlessAuditMessage(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		AuthVerifierFilter authVerifierFilter = new AuthVerifierFilter();

		MockFilterConfig mockFilterConfig = new MockFilterConfig();

		mockFilterConfig.addInitParameter(
			"auth.verifier.PortalSessionAuthVerifier.urls.includes", "*");

		authVerifierFilter.init(mockFilterConfig);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			return _getAuditMessage(authVerifierFilter, mockHttpServletRequest);
		}
		finally {
			AccessControlUtil.setAccessControlContext(null);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			authVerifierFilter.destroy();
		}
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(
				HttpMethods.GET,
				StringPool.SLASH + RandomTestUtil.randomString());

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _user.getCompanyId());

		byte[] doAsUserIdBytes = new byte[Long.BYTES];

		BigEndianCodec.putLong(
			doAsUserIdBytes, 0, _impersonatedUser.getUserId());

		Company company = _companyLocalService.getCompany(_user.getCompanyId());

		mockHttpServletRequest.setParameter(
			"doAsUserId",
			StringUtil.bytesToHexString(
				ChecksumUtil.appendChecksum(
					EncryptorUtil.encryptUnencoded(
						company.getKeyObj(), doAsUserIdBytes))));

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.USER_ID, _user.getUserId());

		return mockHttpServletRequest;
	}

	private void _testConstructorWhenFeatureFlagIsDisabled() {
		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-6417")) {

			AuditMessage auditMessage = _getAuditMessage(
				String.valueOf(_impersonatedUser.getUserId()));

			_assertNoImpersonatedUser(auditMessage);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			Assert.assertEquals(
				_impersonatedUser.getEmailAddress(),
				additionalInfoJSONObject.getString("doAsUserEmailAddress"));
			Assert.assertEquals(
				String.valueOf(_impersonatedUser.getUserId()),
				additionalInfoJSONObject.getString("doAsUserId"));
			Assert.assertEquals(
				_impersonatedUser.getFullName(),
				additionalInfoJSONObject.getString("doAsUserName"));
		}
	}

	private void _testConstructorWithHeadlessRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"p_auth", AuthTokenUtil.getToken(mockHttpServletRequest));

		_assertImpersonatedUser(
			_getHeadlessAuditMessage(mockHttpServletRequest));
	}

	private void _testConstructorWithHeadlessRequestWithoutCSRFToken()
		throws Exception {

		_assertNoImpersonatedUser(
			_getHeadlessAuditMessage(_getMockHttpServletRequest()));
	}

	private void _testConstructorWithPortalRequest() throws Exception {
		SecureFilter secureFilter = new SecureFilter();

		MockFilterConfig mockFilterConfig = new MockFilterConfig();

		mockFilterConfig.addInitParameter(
			"portal_property_prefix", "main.servlet.");

		secureFilter.init(mockFilterConfig);

		try {
			_assertImpersonatedUser(
				_getAuditMessage(secureFilter, _getMockHttpServletRequest()));
		}
		finally {
			secureFilter.destroy();
		}
	}

	@Inject(filter = "servlet-filter-name=Audit Filter")
	private Filter _auditFilter;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private User _impersonatedUser;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}