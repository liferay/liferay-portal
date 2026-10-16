/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.web.internal.display.context;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Shuyang Zhou
 */
public class OpenIdConnectProviderConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testConstructor() throws Exception {
		_testConstructor(ConfigurationAdminPortletKeys.INSTANCE_SETTINGS);
		_testConstructor(ConfigurationAdminPortletKeys.SYSTEM_SETTINGS);
	}

	private Filter _getFilter(String pid) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("pid", pid);

		new OpenIdConnectProviderConfigurationDisplayContext(
			_configurationAdmin, null, mockHttpServletRequest);

		return _filter;
	}

	private Map<String, Object> _getProperties(String pid) {
		return HashMapBuilder.<String, Object>put(
			Constants.SERVICE_PID, pid
		).put(
			"companyId", _COMPANY_ID
		).build();
	}

	private void _testConstructor(String portletId) throws Exception {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(
			(Portal)ProxyUtil.newProxyInstance(
				Portal.class.getClassLoader(), new Class<?>[] {Portal.class},
				(proxy, method, args) -> {
					String methodName = method.getName();

					if (methodName.equals("getCompanyId")) {
						return _COMPANY_ID;
					}

					if (methodName.equals("getPortletId")) {
						return portletId;
					}

					throw new UnsupportedOperationException(methodName);
				}));

		String factoryPid = RandomTestUtil.randomString();

		Filter filter = _getFilter(factoryPid + "~*");

		Assert.assertFalse(
			filter.toString(),
			filter.matches(
				_getProperties(
					factoryPid + "~" + RandomTestUtil.randomString())));
		Assert.assertTrue(
			filter.toString(),
			filter.matches(_getProperties(factoryPid + "~*")));

		filter = _getFilter("(*)\\");

		Assert.assertFalse(
			filter.toString(), filter.matches(_getProperties("(x)\\")));
		Assert.assertTrue(
			filter.toString(), filter.matches(_getProperties("(*)\\")));
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final ConfigurationAdmin _configurationAdmin =
		new ConfigurationAdmin() {

			@Override
			public Configuration createFactoryConfiguration(String factoryPid) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration createFactoryConfiguration(
				String factoryPid, String location) {

				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration getConfiguration(String pid) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration getConfiguration(String pid, String location) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration getFactoryConfiguration(
				String factoryPid, String name) {

				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration getFactoryConfiguration(
				String factoryPid, String name, String location) {

				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration[] listConfigurations(String filter)
				throws InvalidSyntaxException {

				_filter = FrameworkUtil.createFilter(filter);

				return null;
			}

		};

	private Filter _filter;

}