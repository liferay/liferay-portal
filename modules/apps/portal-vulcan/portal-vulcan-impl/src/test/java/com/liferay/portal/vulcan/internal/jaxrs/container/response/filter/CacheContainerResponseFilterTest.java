/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.internal.configuration.admin.service.HeadlessAPICacheManagedServiceFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Jan Brychta
 */
public class CacheContainerResponseFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		_cacheContainerResponseFilter = new CacheContainerResponseFilter(
			_headlessAPICacheManagedServiceFactory);

		ReflectionTestUtil.setFieldValue(
			_cacheContainerResponseFilter, "_company", _company);
		ReflectionTestUtil.setFieldValue(
			_cacheContainerResponseFilter, "_httpServletRequest",
			_httpServletRequest);
		ReflectionTestUtil.setFieldValue(
			_cacheContainerResponseFilter, "_httpServletResponse",
			_httpServletResponse);
		ReflectionTestUtil.setFieldValue(
			_cacheContainerResponseFilter, "_user", _user);

		Mockito.when(
			_company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_containerRequestContext.getMethod()
		).thenReturn(
			HttpMethod.GET
		);

		Mockito.when(
			_containerRequestContext.getUriInfo()
		).thenReturn(
			_uriInfo
		);

		Mockito.when(
			_containerResponseContext.getHeaders()
		).thenReturn(
			_headers
		);

		Mockito.when(
			_containerResponseContext.getStatusInfo()
		).thenReturn(
			Response.Status.OK
		);

		Mockito.when(
			_headlessAPICacheManagedServiceFactory.getCacheControl(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			"public, max-age=3600"
		);

		Mockito.when(
			_httpServletRequest.getSession(false)
		).thenReturn(
			null
		);

		Mockito.when(
			_uriInfo.getBaseUri()
		).thenReturn(
			URI.create("http://localhost/o/test-app/")
		);

		Mockito.when(
			_uriInfo.getPath()
		).thenReturn(
			"v1.0/test"
		);

		Mockito.when(
			_user.isGuestUser()
		).thenReturn(
			true
		);
	}

	@Test
	public void testFilterWhenBaseURITrailingSlashIsMissing() throws Exception {
		Mockito.when(
			_uriInfo.getBaseUri()
		).thenReturn(
			URI.create("http://localhost/o/test-app")
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertCacheable();
	}

	@Test
	public void testFilterWhenCacheControlIsNull() throws Exception {
		Mockito.when(
			_headlessAPICacheManagedServiceFactory.getCacheControl(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenCompanyIsNull() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_cacheContainerResponseFilter, "_company", null);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenContextPathIsPresent() throws Exception {
		Mockito.when(
			_uriInfo.getBaseUri()
		).thenReturn(
			URI.create("http://localhost/liferay/o/test-app/")
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertCacheable();
	}

	@Test
	public void testFilterWhenHttpSessionIsNotNull() throws Exception {
		Mockito.when(
			_httpServletRequest.getSession(false)
		).thenReturn(
			Mockito.mock(HttpSession.class)
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenMethodIsHead() throws Exception {
		Mockito.when(
			_containerRequestContext.getMethod()
		).thenReturn(
			HttpMethod.HEAD
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertCacheable();
	}

	@Test
	public void testFilterWhenMethodIsPost() throws Exception {
		Mockito.when(
			_containerRequestContext.getMethod()
		).thenReturn(
			HttpMethod.POST
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenProductionMode() throws Exception {
		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertCacheable();
	}

	@Test
	public void testFilterWhenProductionModeIsFalse() throws Exception {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					RandomTestUtil.randomLong())) {

			_cacheContainerResponseFilter.filter(
				_containerRequestContext, _containerResponseContext);
		}

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenResponseIsUnsuccessful() throws Exception {
		Mockito.when(
			_containerResponseContext.getStatusInfo()
		).thenReturn(
			Response.Status.NOT_FOUND
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenSetCookieHeaderIsPresent() throws Exception {
		_headers.putSingle("Set-Cookie", RandomTestUtil.randomString());

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenSetCookieHeaderIsPresentInHttpServletResponse()
		throws Exception {

		Mockito.when(
			_httpServletResponse.containsHeader("Set-Cookie")
		).thenReturn(
			true
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenUserIsNotGuest() throws Exception {
		Mockito.when(
			_user.isGuestUser()
		).thenReturn(
			false
		);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	@Test
	public void testFilterWhenUserIsNull() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_cacheContainerResponseFilter, "_user", null);

		_cacheContainerResponseFilter.filter(
			_containerRequestContext, _containerResponseContext);

		_assertNotCacheable();
	}

	private void _assertCacheable() {
		Assert.assertEquals(
			"public, max-age=3600", _headers.getFirst("Cache-Control"));
		Assert.assertEquals(
			"Accept, Accept-Encoding, Accept-Language, Origin, " +
				"X-Accept-All-Languages, X-Liferay-Accept-All-Languages, " +
					"X-Liferay-Data-Masks",
			_headers.getFirst("Vary"));

		Mockito.verify(
			_headlessAPICacheManagedServiceFactory
		).getCacheControl(
			_COMPANY_ID, "/test-app/v1.0/test"
		);
	}

	private void _assertNotCacheable() {
		Assert.assertEquals(
			"no-cache, no-store", _headers.getFirst("Cache-Control"));
		Assert.assertNull(_headers.getFirst("Vary"));
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private CacheContainerResponseFilter _cacheContainerResponseFilter;

	@Mock
	private Company _company;

	@Mock
	private ContainerRequestContext _containerRequestContext;

	@Mock
	private ContainerResponseContext _containerResponseContext;

	private final MultivaluedMap<String, Object> _headers =
		new MultivaluedHashMap<>();

	@Mock
	private HeadlessAPICacheManagedServiceFactory
		_headlessAPICacheManagedServiceFactory;

	@Mock
	private HttpServletRequest _httpServletRequest;

	@Mock
	private HttpServletResponse _httpServletResponse;

	@Mock
	private UriInfo _uriInfo;

	@Mock
	private User _user;

}