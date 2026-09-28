/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResourceFactory;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Luis Ortiz
 */
public class AddInstanceMVCActionCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_defaultAdminPassword = ReflectionTestUtil.getFieldValue(
			PropsValues.class, "DEFAULT_ADMIN_PASSWORD");

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "DEFAULT_ADMIN_PASSWORD", StringPool.BLANK);

		Mockito.when(
			_componentServiceObjects.getService()
		).thenReturn(
			_portalInstanceResource
		);

		ReflectionTestUtil.setFieldValue(
			_addInstanceMVCActionCommand, "_companyLocalService",
			_companyLocalService);
		ReflectionTestUtil.setFieldValue(
			_addInstanceMVCActionCommand, "_componentServiceObjects",
			_componentServiceObjects);
		ReflectionTestUtil.setFieldValue(
			_addInstanceMVCActionCommand, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_addInstanceMVCActionCommand,
			"_vulcanBatchEngineImportTaskResourceFactory",
			_vulcanBatchEngineImportTaskResourceFactory);

		_setParameter("active", "true");
		_setParameter("defaultAdminEmailAddress", _EMAIL_ADDRESS);
		_setParameter("defaultAdminFirstName", _GIVEN_NAME);
		_setParameter("defaultAdminLastName", _FAMILY_NAME);
		_setParameter("defaultAdminMiddleName", _MIDDLE_NAME);
		_setParameter("defaultAdminPassword", _PASSWORD);
		_setParameter("defaultAdminScreenName", _SCREEN_NAME);
		_setParameter("maxUsers", String.valueOf(_MAX_USERS));
		_setParameter("mx", _DOMAIN);
		_setParameter("siteInitializerKey", _SITE_INITIALIZER_KEY);
		_setParameter("virtualHostname", _VIRTUAL_HOST);
		_setParameter("webId", _PORTAL_INSTANCE_ID);

		Mockito.when(
			_portal.getCompany(_actionRequest)
		).thenReturn(
			Mockito.mock(Company.class)
		);

		Mockito.when(
			_portal.getHttpServletRequest(_actionRequest)
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			_portal.getLocale(_actionRequest)
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_portal.getUser(_actionRequest)
		).thenReturn(
			Mockito.mock(User.class)
		);

		Mockito.when(
			_vulcanBatchEngineImportTaskResourceFactory.create()
		).thenReturn(
			_vulcanBatchEngineImportTaskResource
		);
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "DEFAULT_ADMIN_PASSWORD", _defaultAdminPassword);
	}

	@Test
	public void testAddPortalInstanceForcesTheJSONContentType()
		throws Exception {

		Mockito.when(
			_httpServletRequest.getHeader("X-Other")
		).thenReturn(
			"delegated"
		);

		_addPortalInstance();

		ArgumentCaptor<HttpServletRequest> argumentCaptor =
			ArgumentCaptor.forClass(HttpServletRequest.class);

		Mockito.verify(
			_portalInstanceResource
		).setContextHttpServletRequest(
			argumentCaptor.capture()
		);

		HttpServletRequest httpServletRequest = argumentCaptor.getValue();

		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON,
			httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE));
		Assert.assertEquals(
			"delegated", httpServletRequest.getHeader("X-Other"));
	}

	@Test
	public void testAddPortalInstanceSendsTheAdmin() throws Exception {
		_addPortalInstance();

		Map<String, Object> portalInstanceMap = _capturePortalInstanceMap();

		Map<String, String> adminMap =
			(Map<String, String>)portalInstanceMap.get("admin");

		Assert.assertEquals(_EMAIL_ADDRESS, adminMap.get("emailAddress"));
		Assert.assertEquals(_FAMILY_NAME, adminMap.get("familyName"));
		Assert.assertEquals(_GIVEN_NAME, adminMap.get("givenName"));
		Assert.assertEquals(_MIDDLE_NAME, adminMap.get("middleName"));
		Assert.assertEquals(_PASSWORD, adminMap.get("password"));
		Assert.assertEquals(_SCREEN_NAME, adminMap.get("screenName"));
	}

	@Test
	public void testAddPortalInstanceSendsThePortalInstance() throws Exception {
		_addPortalInstance();

		Map<String, Object> portalInstanceMap = _capturePortalInstanceMap();

		Assert.assertEquals(Boolean.TRUE, portalInstanceMap.get("active"));
		Assert.assertEquals(_DOMAIN, portalInstanceMap.get("domain"));
		Assert.assertEquals(_MAX_USERS, portalInstanceMap.get("maxUsers"));
		Assert.assertEquals(
			_PORTAL_INSTANCE_ID, portalInstanceMap.get("portalInstanceId"));
		Assert.assertEquals(
			_SITE_INITIALIZER_KEY, portalInstanceMap.get("siteInitializerKey"));
		Assert.assertEquals(
			_VIRTUAL_HOST, portalInstanceMap.get("virtualHost"));
	}

	@Test
	public void testAddPortalInstanceSetsThePreferredLocale() throws Exception {
		_addPortalInstance();

		ArgumentCaptor<AcceptLanguage> argumentCaptor = ArgumentCaptor.forClass(
			AcceptLanguage.class);

		Mockito.verify(
			_portalInstanceResource
		).setContextAcceptLanguage(
			argumentCaptor.capture()
		);

		AcceptLanguage acceptLanguage = argumentCaptor.getValue();

		Assert.assertEquals(LocaleUtil.US, acceptLanguage.getPreferredLocale());
	}

	@Test
	public void testAddPortalInstanceSetsTheVulcanBatchEngineResource()
		throws Exception {

		_addPortalInstance();

		Mockito.verify(
			_portalInstanceResource
		).setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource
		);
	}

	@Test
	public void testAddPortalInstanceUngetsTheService() throws Exception {
		_addPortalInstance();

		Mockito.verify(
			_componentServiceObjects
		).ungetService(
			_portalInstanceResource
		);
	}

	@Test
	public void testAddPortalInstanceUngetsTheServiceWhenTheBatchFails()
		throws Exception {

		Mockito.when(
			_portalInstanceResource.postPortalInstanceBatch(
				Mockito.isNull(), Mockito.any())
		).thenThrow(
			new IllegalStateException()
		);

		try {
			_addPortalInstance();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}

		Mockito.verify(
			_componentServiceObjects
		).ungetService(
			_portalInstanceResource
		);
	}

	@Test
	public void testGetErrorMessageKey() {
		Assert.assertEquals(
			"an-unexpected-error-occurred",
			_getErrorMessageKey(new Exception()));
		Assert.assertEquals(
			"please-enter-a-valid-email-address",
			_getErrorMessageKey(new UserEmailAddressException.MustNotBeNull()));
		Assert.assertEquals(
			"please-enter-a-valid-mail-domain",
			_getErrorMessageKey(new CompanyMxException()));
		Assert.assertEquals(
			"please-enter-a-valid-max-users",
			_getErrorMessageKey(new CompanyMaxUsersException()));
		Assert.assertEquals(
			"please-enter-a-valid-password",
			_getErrorMessageKey(
				new UserPasswordException.MustNotBeNull(
					RandomTestUtil.randomLong())));
		Assert.assertEquals(
			"please-enter-a-valid-screen-name",
			_getErrorMessageKey(new UserScreenNameException.MustNotBeNull()));
		Assert.assertEquals(
			"please-enter-a-valid-virtual-host",
			_getErrorMessageKey(new CompanyVirtualHostException()));
		Assert.assertEquals(
			"please-enter-a-valid-web-id",
			_getErrorMessageKey(new CompanyWebIdException()));
	}

	@Test
	public void testGetPortalInstanceMapOmitsABlankSiteInitializerKey()
		throws Exception {

		_setParameter("siteInitializerKey", StringPool.BLANK);

		_addPortalInstance();

		Map<String, Object> portalInstanceMap = _capturePortalInstanceMap();

		Assert.assertFalse(
			portalInstanceMap.toString(),
			portalInstanceMap.containsKey("siteInitializerKey"));
	}

	@Test
	public void testGetPortalInstanceMapOmitsTheAdminWithoutAnEmailAddress()
		throws Exception {

		_setParameter("defaultAdminEmailAddress", null);

		_addPortalInstance();

		Map<String, Object> portalInstanceMap = _capturePortalInstanceMap();

		Assert.assertFalse(
			portalInstanceMap.toString(),
			portalInstanceMap.containsKey("admin"));
	}

	@Test
	public void testValidateAdminIgnoresTheAdminWithADefaultAdminPassword()
		throws Exception {

		_setParameter("defaultAdminEmailAddress", null);
		_setParameter("defaultAdminPassword", null);
		_setParameter("defaultAdminScreenName", null);

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "DEFAULT_ADMIN_PASSWORD",
			RandomTestUtil.randomString());

		_validateAdmin();
	}

	@Test(expected = UserPasswordException.MustNotBeNull.class)
	public void testValidateAdminWithoutAPassword() throws Exception {
		_setParameter("defaultAdminPassword", null);

		_validateAdmin();
	}

	@Test(expected = UserScreenNameException.MustNotBeNull.class)
	public void testValidateAdminWithoutAScreenName() throws Exception {
		_setParameter("defaultAdminScreenName", null);

		_validateAdmin();
	}

	@Test(expected = UserEmailAddressException.MustNotBeNull.class)
	public void testValidateAdminWithoutAnEmailAddress() throws Exception {
		_setParameter("defaultAdminEmailAddress", null);

		_validateAdmin();
	}

	@Test
	public void testValidateCompany() throws Exception {
		ReflectionTestUtil.invoke(
			_addInstanceMVCActionCommand, "_validateCompany",
			new Class<?>[] {ActionRequest.class}, _actionRequest);

		Mockito.verify(
			_companyLocalService
		).validateCompany(
			_PORTAL_INSTANCE_ID, _VIRTUAL_HOST, _DOMAIN, _MAX_USERS
		);
	}

	private void _addPortalInstance() throws Exception {
		ReflectionTestUtil.invoke(
			_addInstanceMVCActionCommand, "_addPortalInstance",
			new Class<?>[] {ActionRequest.class}, _actionRequest);
	}

	private Map<String, Object> _capturePortalInstanceMap() throws Exception {
		ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(
			Object.class);

		Mockito.verify(
			_portalInstanceResource
		).postPortalInstanceBatch(
			Mockito.isNull(), argumentCaptor.capture()
		);

		List<Map<String, Object>> maps =
			(List<Map<String, Object>>)argumentCaptor.getValue();

		Assert.assertEquals(maps.toString(), 1, maps.size());

		return maps.get(0);
	}

	private String _getErrorMessageKey(Exception exception) {
		return ReflectionTestUtil.invoke(
			_addInstanceMVCActionCommand, "_getErrorMessageKey",
			new Class<?>[] {Exception.class}, exception);
	}

	private void _setParameter(String name, String value) {
		Mockito.when(
			_actionRequest.getParameter(name)
		).thenReturn(
			value
		);
	}

	private void _validateAdmin() throws Exception {
		ReflectionTestUtil.invoke(
			_addInstanceMVCActionCommand, "_validateAdmin",
			new Class<?>[] {ActionRequest.class}, _actionRequest);
	}

	private static final String _DOMAIN = RandomTestUtil.randomString();

	private static final String _EMAIL_ADDRESS = RandomTestUtil.randomString();

	private static final String _FAMILY_NAME = RandomTestUtil.randomString();

	private static final String _GIVEN_NAME = RandomTestUtil.randomString();

	private static final int _MAX_USERS = RandomTestUtil.randomInt(1, 100);

	private static final String _MIDDLE_NAME = RandomTestUtil.randomString();

	private static final String _PASSWORD = RandomTestUtil.randomString();

	private static final String _PORTAL_INSTANCE_ID =
		RandomTestUtil.randomString();

	private static final String _SCREEN_NAME = RandomTestUtil.randomString();

	private static final String _SITE_INITIALIZER_KEY =
		RandomTestUtil.randomString();

	private static final String _VIRTUAL_HOST = RandomTestUtil.randomString();

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final AddInstanceMVCActionCommand _addInstanceMVCActionCommand =
		new AddInstanceMVCActionCommand();
	private final CompanyLocalService _companyLocalService = Mockito.mock(
		CompanyLocalService.class);
	private final ComponentServiceObjects<PortalInstanceResource>
		_componentServiceObjects = Mockito.mock(ComponentServiceObjects.class);
	private String _defaultAdminPassword;
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortalInstanceResource _portalInstanceResource = Mockito.mock(
		PortalInstanceResource.class);
	private final VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource = Mockito.mock(
			VulcanBatchEngineImportTaskResource.class);
	private final VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory = Mockito.mock(
			VulcanBatchEngineImportTaskResourceFactory.class);

}