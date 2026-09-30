/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceExportResource;
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
import org.mockito.Mockito;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Jorge Avalos
 */
public class ExportInstanceMVCActionCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_actionRequest.getParameter("portalInstanceId")
		).thenReturn(
			_PORTAL_INSTANCE_ID
		);

		Mockito.when(
			_componentServiceObjects.getService()
		).thenReturn(
			_portalInstanceExportResource
		);

		ReflectionTestUtil.setFieldValue(
			_exportInstanceMVCActionCommand, "_componentServiceObjects",
			_componentServiceObjects);
		ReflectionTestUtil.setFieldValue(
			_exportInstanceMVCActionCommand, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_exportInstanceMVCActionCommand,
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
	public void testExportPortalInstanceForcesTheJSONContentType()
		throws Exception {

		Mockito.when(
			_httpServletRequest.getHeader("X-Other")
		).thenReturn(
			"delegated"
		);

		_exportPortalInstance();

		ArgumentCaptor<HttpServletRequest> argumentCaptor =
			ArgumentCaptor.forClass(HttpServletRequest.class);

		Mockito.verify(
			_portalInstanceExportResource
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
	public void testExportPortalInstanceSendsThePortalInstanceId()
		throws Exception {

		_exportPortalInstance();

		ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(
			Object.class);

		Mockito.verify(
			_portalInstanceExportResource
		).postPortalInstanceExportBatch(
			Mockito.isNull(), argumentCaptor.capture()
		);

		List<Map<String, String>> maps =
			(List<Map<String, String>>)argumentCaptor.getValue();

		Assert.assertEquals(maps.toString(), 1, maps.size());

		Map<String, String> map = maps.get(0);

		Assert.assertEquals(_PORTAL_INSTANCE_ID, map.get("portalInstanceId"));
	}

	@Test
	public void testExportPortalInstanceSetsThePreferredLocale()
		throws Exception {

		_exportPortalInstance();

		ArgumentCaptor<AcceptLanguage> argumentCaptor = ArgumentCaptor.forClass(
			AcceptLanguage.class);

		Mockito.verify(
			_portalInstanceExportResource
		).setContextAcceptLanguage(
			argumentCaptor.capture()
		);

		AcceptLanguage acceptLanguage = argumentCaptor.getValue();

		Assert.assertEquals(LocaleUtil.US, acceptLanguage.getPreferredLocale());
	}

	@Test
	public void testExportPortalInstanceSetsTheVulcanBatchEngineResource()
		throws Exception {

		_exportPortalInstance();

		Mockito.verify(
			_portalInstanceExportResource
		).setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource
		);
	}

	@Test
	public void testExportPortalInstanceUngetsTheService() throws Exception {
		_exportPortalInstance();

		Mockito.verify(
			_componentServiceObjects
		).ungetService(
			_portalInstanceExportResource
		);
	}

	@Test
	public void testExportPortalInstanceUngetsTheServiceWhenTheBatchFails()
		throws Exception {

		Mockito.when(
			_portalInstanceExportResource.postPortalInstanceExportBatch(
				Mockito.isNull(), Mockito.any())
		).thenThrow(
			new IllegalStateException()
		);

		try {
			_exportPortalInstance();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}

		Mockito.verify(
			_componentServiceObjects
		).ungetService(
			_portalInstanceExportResource
		);
	}

	private void _exportPortalInstance() throws Exception {
		ReflectionTestUtil.invoke(
			_exportInstanceMVCActionCommand, "_exportPortalInstance",
			new Class<?>[] {ActionRequest.class}, _actionRequest);
	}

	private static final String _PORTAL_INSTANCE_ID =
		RandomTestUtil.randomString();

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final ComponentServiceObjects<PortalInstanceExportResource>
		_componentServiceObjects = Mockito.mock(ComponentServiceObjects.class);
	private final ExportInstanceMVCActionCommand
		_exportInstanceMVCActionCommand = new ExportInstanceMVCActionCommand();
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortalInstanceExportResource _portalInstanceExportResource =
		Mockito.mock(PortalInstanceExportResource.class);
	private final VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource = Mockito.mock(
			VulcanBatchEngineImportTaskResource.class);
	private final VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory = Mockito.mock(
			VulcanBatchEngineImportTaskResourceFactory.class);

}