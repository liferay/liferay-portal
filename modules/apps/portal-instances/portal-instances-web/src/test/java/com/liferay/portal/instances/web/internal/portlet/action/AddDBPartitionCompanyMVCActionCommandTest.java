/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceImportResource;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResourceFactory;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Jorge Avalos
 */
public class AddDBPartitionCompanyMVCActionCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setParameter("name", _NAME);
		_setParameter("schemaName", _SCHEMA_NAME);
		_setParameter("virtualHostname", _VIRTUAL_HOST);
		_setParameter("webId", _WEB_ID);

		Mockito.when(
			_componentServiceObjects.getService()
		).thenReturn(
			_portalInstanceImportResource
		);

		ReflectionTestUtil.setFieldValue(
			_addDBPartitionCompanyMVCActionCommand, "_componentServiceObjects",
			_componentServiceObjects);
		ReflectionTestUtil.setFieldValue(
			_addDBPartitionCompanyMVCActionCommand, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_addDBPartitionCompanyMVCActionCommand,
			"_vulcanBatchEngineImportTaskResourceFactory",
			_vulcanBatchEngineImportTaskResourceFactory);

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

	@Test
	public void testGetErrorMessageKey() {
		Assert.assertEquals(
			"an-unexpected-error-occurred",
			_getErrorMessageKey(new Exception()));
		Assert.assertEquals(
			"please-enter-a-valid-schema-name",
			_getErrorMessageKey(new IllegalArgumentException()));
	}

	@Test
	public void testImportPortalInstanceForcesTheJSONContentType()
		throws Exception {

		Mockito.when(
			_httpServletRequest.getHeader("X-Other")
		).thenReturn(
			"delegated"
		);

		_importPortalInstance();

		ArgumentCaptor<HttpServletRequest> argumentCaptor =
			ArgumentCaptor.forClass(HttpServletRequest.class);

		Mockito.verify(
			_portalInstanceImportResource
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
	public void testImportPortalInstanceSendsThePortalInstanceImport()
		throws Exception {

		_importPortalInstance();

		ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(
			Object.class);

		Mockito.verify(
			_portalInstanceImportResource
		).postPortalInstanceImportBatch(
			Mockito.isNull(), argumentCaptor.capture()
		);

		List<Map<String, String>> maps =
			(List<Map<String, String>>)argumentCaptor.getValue();

		Assert.assertEquals(maps.toString(), 1, maps.size());

		Map<String, String> map = maps.get(0);

		Assert.assertEquals(_NAME, map.get("name"));
		Assert.assertEquals(_SCHEMA_NAME, map.get("schemaName"));
		Assert.assertEquals(_VIRTUAL_HOST, map.get("virtualHost"));
		Assert.assertEquals(_WEB_ID, map.get("webId"));
	}

	@Test
	public void testImportPortalInstanceSetsThePreferredLocale()
		throws Exception {

		_importPortalInstance();

		ArgumentCaptor<AcceptLanguage> argumentCaptor = ArgumentCaptor.forClass(
			AcceptLanguage.class);

		Mockito.verify(
			_portalInstanceImportResource
		).setContextAcceptLanguage(
			argumentCaptor.capture()
		);

		AcceptLanguage acceptLanguage = argumentCaptor.getValue();

		Assert.assertEquals(LocaleUtil.US, acceptLanguage.getPreferredLocale());
	}

	@Test
	public void testImportPortalInstanceSetsTheVulcanBatchEngineResource()
		throws Exception {

		_importPortalInstance();

		Mockito.verify(
			_portalInstanceImportResource
		).setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource
		);
	}

	@Test
	public void testImportPortalInstanceUngetsTheService() throws Exception {
		_importPortalInstance();

		Mockito.verify(
			_componentServiceObjects
		).ungetService(
			_portalInstanceImportResource
		);
	}

	@Test
	public void testImportPortalInstanceUngetsTheServiceWhenTheBatchFails()
		throws Exception {

		Mockito.when(
			_portalInstanceImportResource.postPortalInstanceImportBatch(
				Mockito.isNull(), Mockito.any())
		).thenThrow(
			new IllegalStateException()
		);

		try {
			_importPortalInstance();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}

		Mockito.verify(
			_componentServiceObjects
		).ungetService(
			_portalInstanceImportResource
		);
	}

	@Test
	public void testValidateSchemaName() {
		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockDefaultCompanyId(RandomTestUtil.randomLong())) {

			_validateSchemaName(_SCHEMA_NAME);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateSchemaNameWithANonnumericCompanyId() {
		_validateSchemaName("lexported_abc");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateSchemaNameWithAnInvalidPrefix() {
		_validateSchemaName(
			RandomTestUtil.randomString() + RandomTestUtil.randomLong());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateSchemaNameWithTheDefaultCompanyId() {
		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockDefaultCompanyId(_COMPANY_ID)) {

			_validateSchemaName(_SCHEMA_NAME);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateSchemaNameWithZeroCompanyId() {
		_validateSchemaName("lexported_0");
	}

	private String _getErrorMessageKey(Exception exception) {
		return ReflectionTestUtil.invoke(
			_addDBPartitionCompanyMVCActionCommand, "_getErrorMessageKey",
			new Class<?>[] {Exception.class}, exception);
	}

	private void _importPortalInstance() throws Exception {
		ReflectionTestUtil.invoke(
			_addDBPartitionCompanyMVCActionCommand, "_importPortalInstance",
			new Class<?>[] {ActionRequest.class}, _actionRequest);
	}

	private MockedStatic<PortalInstancePool> _mockDefaultCompanyId(
		long defaultCompanyId) {

		MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
			Mockito.mockStatic(PortalInstancePool.class);

		portalInstancePoolMockedStatic.when(
			PortalInstancePool::getDefaultCompanyId
		).thenReturn(
			defaultCompanyId
		);

		return portalInstancePoolMockedStatic;
	}

	private void _setParameter(String name, String value) {
		Mockito.when(
			_actionRequest.getParameter(name)
		).thenReturn(
			value
		);
	}

	private void _validateSchemaName(String schemaName) {
		ReflectionTestUtil.invoke(
			_addDBPartitionCompanyMVCActionCommand, "_validateSchemaName",
			new Class<?>[] {String.class}, schemaName);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _NAME = RandomTestUtil.randomString();

	private static final String _SCHEMA_NAME = "lexported_" + _COMPANY_ID;

	private static final String _VIRTUAL_HOST = RandomTestUtil.randomString();

	private static final String _WEB_ID = RandomTestUtil.randomString();

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final AddDBPartitionCompanyMVCActionCommand
		_addDBPartitionCompanyMVCActionCommand =
			new AddDBPartitionCompanyMVCActionCommand();
	private final ComponentServiceObjects<PortalInstanceImportResource>
		_componentServiceObjects = Mockito.mock(ComponentServiceObjects.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortalInstanceImportResource _portalInstanceImportResource =
		Mockito.mock(PortalInstanceImportResource.class);
	private final VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource = Mockito.mock(
			VulcanBatchEngineImportTaskResource.class);
	private final VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory = Mockito.mock(
			VulcanBatchEngineImportTaskResourceFactory.class);

}