/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.request.filter;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.oauth2.provider.scope.liferay.constants.OAuth2ProviderScopeLiferayConstants;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Nilton Vieira
 */
public class SecurityFilterTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtils.setField(
			_securityFilter, "_httpServletRequest", _httpServletRequest);
		ReflectionTestUtils.setField(
			_securityFilter, "_resourceInfo", _resourceInfo);

		Mockito.when(
			_permissionChecker.getUser()
		).thenReturn(
			_user
		);

		Mockito.when(
			_permissionChecker.isOmniadmin()
		).thenReturn(
			true
		);

		Mockito.when(
			_resourceInfo.getResourceMethod()
		).thenReturn(
			TestResource.class.getMethod("rolesAllowedSiteMember")
		);
	}

	@Test
	public void testFilter() throws Exception {
		try (MockedStatic<AccessControlUtil> accessControlUtilMockedStatic =
				Mockito.mockStatic(AccessControlUtil.class);
			MockedStatic<FaroUserLocalServiceUtil>
				faroUserLocalServiceUtilMockedStatic = Mockito.mockStatic(
					FaroUserLocalServiceUtil.class);
			MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class)) {

			accessControlUtilMockedStatic.when(
				AccessControlUtil::getAccessControlContext
			).thenReturn(
				_accessControlContext
			);

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			_assertAborted(
				_filterWithOAuth2("app-" + RandomTestUtil.randomString()),
				Response.Status.UNAUTHORIZED);
			_assertNotAborted(_filter(HttpServletRequest.BASIC_AUTH));
			_assertNotAborted(_filterWithOAuth2(RandomTestUtil.randomString()));
		}
	}

	@Test
	public void testFilterWithoutOmniadmin() throws Exception {
		Mockito.when(
			_permissionChecker.isOmniadmin()
		).thenReturn(
			false
		);

		try (MockedStatic<FaroUserLocalServiceUtil>
				faroUserLocalServiceUtilMockedStatic = Mockito.mockStatic(
					FaroUserLocalServiceUtil.class);
			MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class);
			MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<PortalUtil> portalUtilMockedStatic =
				Mockito.mockStatic(PortalUtil.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			_assertNotAborted(_filterWithoutOmniadmin("permitAll"));
			_assertAborted(
				_filterWithoutOmniadmin("rolesAllowedSiteMember"),
				Response.Status.FORBIDDEN);
			_assertAborted(
				_filterWithoutOmniadmin("unannotated"),
				Response.Status.FORBIDDEN);
		}
	}

	public static class TestResource {

		@PermitAll
		public void permitAll() {
		}

		@RolesAllowed(RoleConstants.SITE_MEMBER)
		public void rolesAllowedSiteMember() {
		}

		public void unannotated() {
		}

	}

	private void _assertAborted(
		ContainerRequestContext containerRequestContext,
		Response.Status status) {

		ArgumentCaptor<Response> argumentCaptor = ArgumentCaptor.forClass(
			Response.class);

		Mockito.verify(
			containerRequestContext
		).abortWith(
			argumentCaptor.capture()
		);

		Response response = argumentCaptor.getValue();

		Assert.assertEquals(status.getStatusCode(), response.getStatus());
	}

	private void _assertNotAborted(
		ContainerRequestContext containerRequestContext) {

		Mockito.verify(
			containerRequestContext, Mockito.never()
		).abortWith(
			Mockito.any()
		);
	}

	private ContainerRequestContext _filter(String authType) {
		Mockito.when(
			_httpServletRequest.getAuthType()
		).thenReturn(
			authType
		);

		ContainerRequestContext containerRequestContext = Mockito.mock(
			ContainerRequestContext.class);

		_securityFilter.filter(containerRequestContext);

		return containerRequestContext;
	}

	private ContainerRequestContext _filterWithOAuth2(
		String oAuth2ApplicationName) {

		OAuth2Application oAuth2Application = Mockito.mock(
			OAuth2Application.class);

		Mockito.when(
			oAuth2Application.getName()
		).thenReturn(
			oAuth2ApplicationName
		);

		BearerTokenProvider.AccessToken accessToken = Mockito.mock(
			BearerTokenProvider.AccessToken.class);

		Mockito.when(
			accessToken.getOAuth2Application()
		).thenReturn(
			oAuth2Application
		);

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		authVerifierResult.setSettings(
			HashMapBuilder.<String, Object>put(
				BearerTokenProvider.AccessToken.class.getName(), accessToken
			).build());

		_accessControlContext.setAuthVerifierResult(authVerifierResult);

		return _filter(
			OAuth2ProviderScopeLiferayConstants.AUTH_VERIFIER_OAUTH2_TYPE);
	}

	private ContainerRequestContext _filterWithoutOmniadmin(String methodName)
		throws Exception {

		ContainerRequestContext containerRequestContext = Mockito.mock(
			ContainerRequestContext.class);

		UriInfo uriInfo = Mockito.mock(UriInfo.class);

		Mockito.when(
			uriInfo.getPathParameters()
		).thenReturn(
			new MultivaluedHashMap<>()
		);

		Mockito.when(
			containerRequestContext.getUriInfo()
		).thenReturn(
			uriInfo
		);

		Mockito.when(
			_resourceInfo.getResourceMethod()
		).thenReturn(
			TestResource.class.getMethod(methodName)
		);

		_securityFilter.filter(containerRequestContext);

		return containerRequestContext;
	}

	private final AccessControlContext _accessControlContext =
		new AccessControlContext();

	@Mock
	private HttpServletRequest _httpServletRequest;

	@Mock
	private PermissionChecker _permissionChecker;

	@Mock
	private ResourceInfo _resourceInfo;

	private final SecurityFilter _securityFilter = new SecurityFilter();

	@Mock
	private User _user;

}