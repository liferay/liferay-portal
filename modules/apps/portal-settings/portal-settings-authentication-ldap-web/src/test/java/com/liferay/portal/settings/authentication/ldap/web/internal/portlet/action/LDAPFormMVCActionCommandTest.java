/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.ldap.LDAPConfigurationModelListenerException;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.settings.authentication.ldap.web.internal.portlet.constants.LDAPSettingsConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Caio Farias
 */
public class LDAPFormMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Mockito.when(
			portal.getHttpServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		Mockito.when(
			portal.getLiferayPortletRequest(Mockito.any())
		).thenReturn(
			Mockito.mock(LiferayPortletRequest.class)
		);

		Mockito.when(
			portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		Mockito.when(
			portal.getPortletId(Mockito.any(MockHttpServletRequest.class))
		).thenReturn(
			ConfigurationAdminPortletKeys.SYSTEM_SETTINGS
		);

		portalUtil.setPortal(portal);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.isCompanyAdmin(0)
		).thenReturn(
			true
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		_ldapAuthConfigurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		Mockito.when(
			_ldapAuthConfigurationProvider.getConfigurationProperties(0)
		).thenReturn(
			new HashMapDictionary<>()
		);

		_ldapFormMVCActionCommand = new LDAPFormMVCActionCommand();

		ReflectionTestUtil.setFieldValue(
			_ldapFormMVCActionCommand, "_ldapAuthConfigurationProvider",
			_ldapAuthConfigurationProvider);
	}

	@Test
	public void testDoProcessAction() throws Exception {
		LDAPConfigurationModelListenerException
			ldapConfigurationModelListenerException =
				new LDAPConfigurationModelListenerException(
					RandomTestUtil.randomString(), LDAPAuthConfiguration.class,
					getClass(), new Object[] {RandomTestUtil.randomString()},
					RandomTestUtil.randomString(), new HashMapDictionary<>());

		_setUpLDAPAuthConfigurationProvider(
			ldapConfigurationModelListenerException);

		MockActionRequest mockActionRequest = _getMockActionRequest();

		_ldapFormMVCActionCommand.doProcessAction(
			mockActionRequest, new MockActionResponse());

		Assert.assertSame(
			ldapConfigurationModelListenerException,
			SessionErrors.get(
				mockActionRequest,
				LDAPConfigurationModelListenerException.class.getName()));

		_setUpLDAPAuthConfigurationProvider(new PrincipalException());

		SessionErrors.clear(mockActionRequest);

		Assert.assertThrows(
			SystemException.class,
			() -> _ldapFormMVCActionCommand.doProcessAction(
				mockActionRequest, new MockActionResponse()));

		Assert.assertTrue(SessionErrors.isEmpty(mockActionRequest));
	}

	private MockActionRequest _getMockActionRequest() {
		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setParameter(
			Constants.CMD, LDAPSettingsConstants.CMD_UPDATE_AUTH);

		return mockActionRequest;
	}

	private void _setUpLDAPAuthConfigurationProvider(Throwable throwable) {
		Mockito.doThrow(
			new SystemException(throwable)
		).when(
			_ldapAuthConfigurationProvider
		).updateProperties(
			Mockito.eq(0L), Mockito.<Dictionary<String, Object>>any()
		);
	}

	private ConfigurationProvider<LDAPAuthConfiguration>
		_ldapAuthConfigurationProvider;
	private LDAPFormMVCActionCommand _ldapFormMVCActionCommand;

}