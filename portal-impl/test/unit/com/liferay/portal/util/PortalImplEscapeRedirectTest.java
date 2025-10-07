/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.redirect.RedirectURLSettings;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceWrapper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.VirtualHostImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class PortalImplEscapeRedirectTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_serviceRegistration = _bundleContext.registerService(
			RedirectURLSettings.class, _redirectURLSettingsImpl, null);

		_prefsPropsUtilMockedStatic.when(
			() -> PrefsPropsUtil.getString(
				CompanyThreadLocal.getCompanyId(), PropsKeys.CDN_HOST_HTTPS,
				PropsValues.CDN_HOST_HTTPS)
		).thenReturn(
			PropsValues.CDN_HOST_HTTPS
		);

		ReflectionTestUtil.setFieldValue(
			VirtualHostLocalServiceUtil.class, "_service",
			new VirtualHostLocalServiceWrapper() {

				@Override
				public VirtualHost fetchVirtualHost(String hostname) {
					if (hostname.equals(_HOSTNAME_VIRTUAL_HOST)) {
						return new VirtualHostImpl();
					}

					return null;
				}

			});

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setRequest(
			new MockHttpServletRequest() {
				{
					setAttribute(
						WebKeys.THEME_DISPLAY,
						new ThemeDisplay() {
							{
								setPortalDomain(_HOSTNAME_PORTAL_DOMAIN);
							}
						});
				}
			});

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() {
		_prefsPropsUtilMockedStatic.close();

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testEscapeRedirectWithDomains() throws Exception {
		_redirectURLSettingsImpl.allowedDomains = new String[] {
			"google.com", "localhost", "PORTAL_DOMAINS"
		};
		_redirectURLSettingsImpl.securityMode = "domain";

		// Allow request host header

		Assert.assertEquals(
			"https://" + _HOSTNAME_PORTAL_DOMAIN + ":8080",
			_portalImpl.escapeRedirect(
				"https://" + _HOSTNAME_PORTAL_DOMAIN + ":8080"));

		// Allow virtual host

		Assert.assertEquals(
			"https://" + _HOSTNAME_VIRTUAL_HOST + ":8080",
			_portalImpl.escapeRedirect(
				"https://" + _HOSTNAME_VIRTUAL_HOST + ":8080"));

		// Allowed domains

		Assert.assertEquals(
			"http://localhost", _portalImpl.escapeRedirect("http://localhost"));
		Assert.assertEquals(
			"https://localhost:8080/a/b;c=d?e=f&g=h#x=y",
			_portalImpl.escapeRedirect(
				"https://localhost:8080/a/b;c=d?e=f&g=h#x=y"));
		Assert.assertEquals(
			"http://google.com",
			_portalImpl.escapeRedirect("http://google.com"));
		Assert.assertEquals(
			"https://google.com:8080/a/b;c=d?e=f&g=h#x=y",
			_portalImpl.escapeRedirect(
				"https://google.com:8080/a/b;c=d?e=f&g=h#x=y"));
		Assert.assertNull(_portalImpl.escapeRedirect("http://liferay.com"));
		Assert.assertNull(
			_portalImpl.escapeRedirect(
				"https://liferay.com:8080/a/b;c=d?e=f&g=h#x=y"));

		// Disabled domains

		Assert.assertNull(
			_portalImpl.escapeRedirect("https://google.comsuffix"));
		Assert.assertNull(
			_portalImpl.escapeRedirect("https://google.com.suffix"));
		Assert.assertNull(
			_portalImpl.escapeRedirect("https://prefixgoogle.com"));
		Assert.assertNull(
			_portalImpl.escapeRedirect("https://prefix.google.com"));

		// Invalid URLs

		Assert.assertNull(_portalImpl.escapeRedirect("//www.google.com"));
		Assert.assertNull(_portalImpl.escapeRedirect("//www.google.com/"));
		Assert.assertNull(
			_portalImpl.escapeRedirect("//www.google.com//www.google.com"));
		Assert.assertNull(_portalImpl.escapeRedirect("https:google.com"));
		Assert.assertNull(_portalImpl.escapeRedirect(":@liferay.com"));
		Assert.assertNull(_portalImpl.escapeRedirect("http:/web"));
		Assert.assertNull(_portalImpl.escapeRedirect("http:web"));
		Assert.assertNull(
			_portalImpl.escapeRedirect("https://google.com\uFFFD@localhost"));
	}

	@Test
	public void testEscapeRedirectWithEscapingSequenceCharacter() {
		Assert.assertNull(_portalImpl.escapeRedirect("\t//example.com"));
	}

	@Test
	public void testEscapeRedirectWithIPs() throws Exception {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS", 2);
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "DNS_SECURITY_THREAD_LIMIT", 10);

		_redirectURLSettingsImpl.allowedIPs = new String[] {
			"127.0.0.1", "SERVER_IP"
		};
		_redirectURLSettingsImpl.securityMode = "ip";

		try {

			// Absolute URL

			Assert.assertEquals(
				"http://localhost",
				_portalImpl.escapeRedirect("http://localhost"));
			Assert.assertEquals(
				"https://localhost:8080/a/b;c=d?e=f&g=h#x=y",
				_portalImpl.escapeRedirect(
					"https://localhost:8080/a/b;c=d?e=f&g=h#x=y"));

			Set<String> computerAddresses = _portalImpl.getComputerAddresses();

			for (String computerAddress : computerAddresses) {
				Assert.assertEquals(
					"http://" + computerAddress,
					_portalImpl.escapeRedirect("http://" + computerAddress));
				Assert.assertEquals(
					"https://" + computerAddress + "/a/b;c=d?e=f&g=h#x=y",
					_portalImpl.escapeRedirect(
						"https://" + computerAddress + "/a/b;c=d?e=f&g=h#x=y"));
			}

			Assert.assertNull(_portalImpl.escapeRedirect("http://liferay.com"));
			Assert.assertNull(
				_portalImpl.escapeRedirect(
					"https://liferay.com:8080/a/b;c=d?e=f&g=h#x=y"));
			Assert.assertNull(
				_portalImpl.escapeRedirect("http://127.0.0.1suffix"));
			Assert.assertNull(
				_portalImpl.escapeRedirect("http://127.0.0.1.suffix"));
			Assert.assertNull(
				_portalImpl.escapeRedirect("http://prefix127.0.0.1"));
			Assert.assertNull(
				_portalImpl.escapeRedirect("http://prefix.127.0.0.1"));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "DNS_SECURITY_THREAD_LIMIT", 10);
			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "DNS_SECURITY_ADDRESS_TIMEOUT_SECONDS", 2);
		}
	}

	@Test
	public void testEscapeRedirectWithProtocols() throws Exception {
		Assert.assertEquals(
			"http://localhost", _portalImpl.escapeRedirect("http://localhost"));
		Assert.assertEquals(
			"https://localhost",
			_portalImpl.escapeRedirect("https://localhost"));
		Assert.assertNull(_portalImpl.escapeRedirect("file://localhost"));
		Assert.assertNull(_portalImpl.escapeRedirect("ftp://localhost"));
		Assert.assertNull(_portalImpl.escapeRedirect("javascript://localhost"));
	}

	@Test
	public void testEscapeRedirectWithRelativeURL() throws Exception {

		// Relative path

		Assert.assertEquals("/", _portalImpl.escapeRedirect("/"));
		Assert.assertEquals(
			"/web/guest", _portalImpl.escapeRedirect("/web/guest"));
		Assert.assertEquals(
			"/a/b;c=d?e=f&g=h#x=y",
			_portalImpl.escapeRedirect("/a/b;c=d?e=f&g=h#x=y"));
		Assert.assertEquals(
			"liferay.com", _portalImpl.escapeRedirect("liferay.com"));
		Assert.assertEquals("/", _portalImpl.escapeRedirect("/"));
		Assert.assertEquals(
			"/web/guest", _portalImpl.escapeRedirect("/web/guest"));
		Assert.assertEquals(
			"/a/b;c=d?e=f&g=h#x=y",
			_portalImpl.escapeRedirect("/a/b;c=d?e=f&g=h#x=y"));
		Assert.assertEquals(
			"/web/http:", _portalImpl.escapeRedirect("/web/http:"));
		Assert.assertEquals(
			"web/http:", _portalImpl.escapeRedirect("web/http:"));
		Assert.assertEquals(
			"test@google.com", _portalImpl.escapeRedirect("test@google.com"));
		Assert.assertNull(_portalImpl.escapeRedirect("///liferay.com"));
		Assert.assertEquals(
			"user/test/~/control_panel/manage/-/select/image%2Clurl/",
			_portalImpl.escapeRedirect(
				"user/test/~/control_panel/manage/-/select/image%2Clurl/"));
		Assert.assertEquals(
			"user/test/~/control_panel/manage/-/select/image,url/",
			_portalImpl.escapeRedirect(
				"user/test/~/control_panel/manage/-/select/image,url/"));
		Assert.assertEquals(
			"?param1=abc", _portalImpl.escapeRedirect("?param1=abc"));
		Assert.assertEquals("#abc", _portalImpl.escapeRedirect("#abc"));
		Assert.assertEquals("", _portalImpl.escapeRedirect(""));

		// Relative path with protocol

		Assert.assertNull(_portalImpl.escapeRedirect("https:/path"));
		Assert.assertNull(_portalImpl.escapeRedirect("test:/google.com"));
	}

	@Test
	public void testEscapeRedirectWithSubdomains() throws Exception {
		_redirectURLSettingsImpl.allowedDomains = new String[] {
			"*.test.liferay.com", "google.com", "PORTAL_DOMAINS"
		};
		_redirectURLSettingsImpl.securityMode = "domain";

		// Relative path

		Assert.assertEquals("/", _portalImpl.escapeRedirect("/"));
		Assert.assertEquals(
			"/web/guest", _portalImpl.escapeRedirect("/web/guest"));
		Assert.assertEquals(
			"/a/b;c=d?e=f&g=h#x=y",
			_portalImpl.escapeRedirect("/a/b;c=d?e=f&g=h#x=y"));
		Assert.assertEquals(
			"test.liferay.com", _portalImpl.escapeRedirect("test.liferay.com"));

		// Absolute URL

		Assert.assertEquals(
			"http://test.liferay.com",
			_portalImpl.escapeRedirect("http://test.liferay.com"));
		Assert.assertEquals(
			"https://test.liferay.com:8080/a/b;c=d?e=f&g=h#x=y",
			_portalImpl.escapeRedirect(
				"https://test.liferay.com:8080/a/b;c=d?e=f&g=h#x=y"));
		Assert.assertEquals(
			"http://second.test.liferay.com",
			_portalImpl.escapeRedirect("http://second.test.liferay.com"));
		Assert.assertEquals(
			"https://second.test.liferay.com:8080/a;c=d?e=f&g=h#x=y",
			_portalImpl.escapeRedirect(
				"https://second.test.liferay.com:8080/a;c=d?e=f&g=h#x=y"));
		Assert.assertEquals(
			"http://google.com",
			_portalImpl.escapeRedirect("http://google.com"));
		Assert.assertEquals(
			"https://google.com:8080/a/b;c=d?e=f&g=h#x=y",
			_portalImpl.escapeRedirect(
				"https://google.com:8080/a/b;c=d?e=f&g=h#x=y"));
		Assert.assertNull(_portalImpl.escapeRedirect("http://liferay.com"));
		Assert.assertNull(
			_portalImpl.escapeRedirect(
				"https://liferay.com:8080/a/b;c=d?e=f&g=h#x=y"));
		Assert.assertNull(
			_portalImpl.escapeRedirect("http://test.liferay.comsuffix"));
		Assert.assertNull(
			_portalImpl.escapeRedirect("http://test.liferay.com.suffix"));
		Assert.assertNull(
			_portalImpl.escapeRedirect("http://prefixtest.liferay.com"));
	}

	private static final String _HOSTNAME_PORTAL_DOMAIN =
		RandomTestUtil.randomString();

	private static final String _HOSTNAME_VIRTUAL_HOST =
		RandomTestUtil.randomString();

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static ServiceRegistration<RedirectURLSettings>
		_serviceRegistration;

	private final PortalImpl _portalImpl = new PortalImpl();
	private final MockedStatic<PrefsPropsUtil> _prefsPropsUtilMockedStatic =
		Mockito.mockStatic(PrefsPropsUtil.class);
	private final RedirectURLSettingsImpl _redirectURLSettingsImpl =
		new RedirectURLSettingsImpl();

	private static class RedirectURLSettingsImpl
		implements RedirectURLSettings {

		@Override
		public String[] getAllowedDomains(long companyId) {
			return GetterUtil.getStringValues(allowedDomains);
		}

		@Override
		public String[] getAllowedIPs(long companyId) {
			return GetterUtil.getStringValues(allowedIPs);
		}

		@Override
		public String[] getAllowedProtocols(long companyId) {
			return GetterUtil.getStringValues(allowedProtocols);
		}

		@Override
		public String getSecurityMode(long companyId) {
			return GetterUtil.getString(securityMode);
		}

		protected String[] allowedDomains = {"localhost", "PORTAL_DOMAINS"};
		protected String[] allowedIPs = {"127.0.0.1", "SERVER_IP"};
		protected String[] allowedProtocols = {"http", "https"};
		protected String securityMode = "domain";

	}

}