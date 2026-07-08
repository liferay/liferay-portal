/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.virtualhost.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.virtualhost.VirtualHostFilter;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Zsolt Oláh
 */
@RunWith(Arquillian.class)
public class VirtualHostFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws PortalException {
		_privateLayoutSet = _layoutSetLocalService.getLayoutSet(
			TestPropsValues.getGroupId(), true);
		_publicLayoutSet = _layoutSetLocalService.getLayoutSet(
			TestPropsValues.getGroupId(), false);
	}

	@Before
	public void setUp() {
		_portalUtil.setPortal(
			new PortalImpl() {

				@Override
				public String getPathContext() {
					return GetterUtil.getString(_pathContext);
				}

				@Override
				public String getPathProxy() {
					return GetterUtil.getString(_pathProxy);
				}

			});
	}

	@After
	public void tearDown() {
		_portalUtil.setPortal(_portal);

		_virtualHostFilter.destroy();
	}

	@Test
	public void testProcessFilterDoesNotSetGroupOnRequestForUnknownPath() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			MockHttpServletRequest mockHttpServletRequest = _processFilter(
				null, StringPool.SLASH + RandomTestUtil.randomString(), null);

			Assert.assertNull(
				mockHttpServletRequest.getAttribute(
					WebKeys.FRIENDLY_URL_GROUP));
			Assert.assertNull(
				mockHttpServletRequest.getAttribute(
					WebKeys.GROUP_FRIENDLY_URL));
		}
	}

	@Test(expected = SystemException.class)
	public void testProcessFilterDoesNotSwallowException() {
		_processFilter(
			_publicLayoutSet, "/home",
			new MockServletContext() {

				@Override
				public RequestDispatcher getRequestDispatcher(String path) {
					return new RequestDispatcher() {

						@Override
						public void forward(
							ServletRequest servletRequest,
							ServletResponse servletResponse) {

							throw new SystemException();
						}

						@Override
						public void include(
							ServletRequest servletRequest,
							ServletResponse servletResponse) {
						}

					};
				}

			});
	}

	@Test
	public void testProcessFilterForwardedURL() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_DEFAULT_HOME_URL", StringPool.SLASH)) {

			Assert.assertNotEquals(
				StringPool.SLASH, _getForwardedURL(StringPool.SLASH));
		}
	}

	@Test
	public void testProcessFilterForwardedURLForLanguageIdWithoutTrailingSlash() {
		Assert.assertEquals(
			_getForwardedURL("/en-US/"), _getForwardedURL("/en-US"));
	}

	@Test
	public void testProcessFilterForwardedURLWithPublicServletMappingDisabled() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			String groupFriendlyURL = _getGroupFriendlyURL(_publicLayoutSet);

			Assert.assertEquals(
				"/web" + groupFriendlyURL + "/home",
				_getForwardedURL(groupFriendlyURL + "/home"));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Test
	public void testProcessFilterForwardedURLWithPublicServletMappingDisabledDoesNotAffectPrivateGroupURL() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			String groupFriendlyURL = _getGroupFriendlyURL(_privateLayoutSet);

			Assert.assertEquals(
				"/group" + groupFriendlyURL + "/home",
				_getForwardedURL(_privateLayoutSet, "/home"));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Test
	public void testProcessFilterForwardedURLWithPublicServletMappingDisabledDoesNotAffectVirtualHostURL() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			String forwardedURL = _getForwardedURL("/home");

			String groupFriendlyURL = _getGroupFriendlyURL(_publicLayoutSet);

			Assert.assertEquals(
				"/web" + groupFriendlyURL + "/home", forwardedURL);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Test
	public void testProcessFilterForwardedURLWithPublicServletMappingDisabledWithoutLayoutSet() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			String groupFriendlyURL = _getGroupFriendlyURL(_publicLayoutSet);

			Assert.assertEquals(
				"/web" + groupFriendlyURL + "/home",
				_getForwardedURL(null, groupFriendlyURL + "/home"));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Test
	public void testProcessFilterLastPath() {
		_testProcessFilterLastPath(
			_PATH_PROXY + _PATH_CONTEXT, _PATH_PROXY,
			_PATH_CONTEXT + _LAST_PATH);
		_testProcessFilterLastPath(_PATH_PROXY, StringPool.BLANK, _LAST_PATH);
		_testProcessFilterLastPath(_PATH_PROXY, _PATH_PROXY, _LAST_PATH);
	}

	@Test
	public void testProcessFilterSetsGroupOnRequestWhenLayoutSetMatches()
		throws Exception {

		String groupFriendlyURL = _getGroupFriendlyURL(_publicLayoutSet);

		MockHttpServletRequest mockHttpServletRequest = _processFilter(
			_publicLayoutSet,
			groupFriendlyURL + StringPool.SLASH + RandomTestUtil.randomString(),
			null);

		Group group = (Group)mockHttpServletRequest.getAttribute(
			WebKeys.FRIENDLY_URL_GROUP);

		Assert.assertEquals(_publicLayoutSet.getGroupId(), group.getGroupId());

		Assert.assertEquals(
			groupFriendlyURL,
			mockHttpServletRequest.getAttribute(WebKeys.GROUP_FRIENDLY_URL));
	}

	@Test
	public void testProcessFilterSetsGroupOnRequestWhenPathForwards()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING_ENABLED",
					false)) {

			String groupFriendlyURL = _getGroupFriendlyURL(_publicLayoutSet);

			MockHttpServletRequest mockHttpServletRequest = _processFilter(
				null,
				groupFriendlyURL + StringPool.SLASH +
					RandomTestUtil.randomString(),
				null);

			Group group = (Group)mockHttpServletRequest.getAttribute(
				WebKeys.FRIENDLY_URL_GROUP);

			Assert.assertEquals(
				_publicLayoutSet.getGroupId(), group.getGroupId());

			Assert.assertEquals(
				groupFriendlyURL,
				mockHttpServletRequest.getAttribute(
					WebKeys.GROUP_FRIENDLY_URL));
		}
	}

	private String _getForwardedURL(LayoutSet layoutSet, String requestURI) {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layoutSet, requestURI);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_virtualHostFilter.init(new MockFilterConfig());

		ReflectionTestUtil.invoke(
			_virtualHostFilter, "processFilter",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class,
				FilterChain.class
			},
			mockHttpServletRequest, mockHttpServletResponse,
			new MockFilterChain());

		return mockHttpServletResponse.getForwardedUrl();
	}

	private String _getForwardedURL(String requestURI) {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(requestURI);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_virtualHostFilter.init(new MockFilterConfig());

		ReflectionTestUtil.invoke(
			_virtualHostFilter, "processFilter",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class,
				FilterChain.class
			},
			mockHttpServletRequest, mockHttpServletResponse,
			new MockFilterChain());

		return mockHttpServletResponse.getForwardedUrl();
	}

	private String _getGroupFriendlyURL(LayoutSet layoutSet)
		throws PortalException {

		Group group = layoutSet.getGroup();

		return group.getFriendlyURL();
	}

	private String _getLastPath(String requestURI) {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(requestURI);

		_virtualHostFilter.init(new MockFilterConfig());

		ReflectionTestUtil.invoke(
			_virtualHostFilter, "processFilter",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class,
				FilterChain.class
			},
			mockHttpServletRequest, new MockHttpServletResponse(),
			new MockFilterChain());

		LastPath lastPath = (LastPath)mockHttpServletRequest.getAttribute(
			WebKeys.LAST_PATH);

		if (lastPath != null) {
			return lastPath.getPath();
		}

		return StringPool.BLANK;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		LayoutSet layoutSet, String requestURI) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.VIRTUAL_HOST_LAYOUT_SET, layoutSet);
		mockHttpServletRequest.setRequestURI(requestURI);

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		String requestURI) {

		return _getMockHttpServletRequest(_publicLayoutSet, requestURI);
	}

	private MockHttpServletRequest _processFilter(
		LayoutSet layoutSet, String requestURI, ServletContext servletContext) {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layoutSet, requestURI);

		_virtualHostFilter.init(new MockFilterConfig(servletContext));

		ReflectionTestUtil.invoke(
			_virtualHostFilter, "processFilter",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class,
				FilterChain.class
			},
			mockHttpServletRequest, new MockHttpServletResponse(),
			new MockFilterChain());

		return mockHttpServletRequest;
	}

	private void _testProcessFilterLastPath(
		String pathContext, String pathProxy, String requestURI) {

		_pathContext = pathContext;
		_pathProxy = pathProxy;

		Assert.assertEquals(_LAST_PATH, _getLastPath(requestURI));
	}

	private static final String _LAST_PATH =
		VirtualHostFilterTest._PATH_PROXY + "_last_path";

	private static final String _PATH_CONTEXT = "/context";

	private static final String _PATH_PROXY = "/proxy";

	@Inject
	private static LayoutSetLocalService _layoutSetLocalService;

	private static LayoutSet _privateLayoutSet;
	private static LayoutSet _publicLayoutSet;

	private String _pathContext;
	private String _pathProxy;

	@Inject
	private Portal _portal;

	@Inject
	private PortalUtil _portalUtil;

	private final VirtualHostFilter _virtualHostFilter =
		new VirtualHostFilter();

}