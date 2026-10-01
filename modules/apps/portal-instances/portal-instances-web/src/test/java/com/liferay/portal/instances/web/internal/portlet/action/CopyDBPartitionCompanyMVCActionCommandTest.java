/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.headless.portal.instances.resource.v1_0.PortalInstanceCopyResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.CompanyNameException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
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
public class CopyDBPartitionCompanyMVCActionCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setParameter(
			"destinationCompanyId", String.valueOf(_DESTINATION_COMPANY_ID));
		_setParameter("name", _NAME);
		_setParameter("sourceCompanyId", String.valueOf(_SOURCE_COMPANY_ID));
		_setParameter("virtualHostname", _VIRTUAL_HOST);
		_setParameter("webId", _WEB_ID);

		Mockito.when(
			_sourceCompany.getCompanyId()
		).thenReturn(
			_SOURCE_COMPANY_ID
		);

		Mockito.when(
			_sourceCompany.getMx()
		).thenReturn(
			_SOURCE_MX
		);

		Mockito.when(
			_sourceCompany.getWebId()
		).thenReturn(
			_SOURCE_WEB_ID
		);

		Mockito.when(
			_componentServiceObjects.getService()
		).thenReturn(
			_portalInstanceCopyResource
		);

		ReflectionTestUtil.setFieldValue(
			_copyDBPartitionCompanyMVCActionCommand, "_companyLocalService",
			_companyLocalService);
		ReflectionTestUtil.setFieldValue(
			_copyDBPartitionCompanyMVCActionCommand, "_componentServiceObjects",
			_componentServiceObjects);
		ReflectionTestUtil.setFieldValue(
			_copyDBPartitionCompanyMVCActionCommand, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_copyDBPartitionCompanyMVCActionCommand,
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
	public void testCopyPortalInstanceForcesTheJSONContentType()
		throws Exception {

		Mockito.when(
			_httpServletRequest.getHeader("X-Other")
		).thenReturn(
			"delegated"
		);

		_copyPortalInstance();

		ArgumentCaptor<HttpServletRequest> argumentCaptor =
			ArgumentCaptor.forClass(HttpServletRequest.class);

		Mockito.verify(
			_portalInstanceCopyResource
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
	public void testCopyPortalInstanceSendsThePortalInstanceCopy()
		throws Exception {

		_copyPortalInstance();

		ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(
			Object.class);

		Mockito.verify(
			_portalInstanceCopyResource
		).postPortalInstanceCopyBatch(
			Mockito.isNull(), argumentCaptor.capture()
		);

		List<Map<String, Object>> maps =
			(List<Map<String, Object>>)argumentCaptor.getValue();

		Assert.assertEquals(maps.toString(), 1, maps.size());

		Map<String, Object> map = maps.get(0);

		Assert.assertEquals(
			_DESTINATION_COMPANY_ID, map.get("destinationCompanyId"));
		Assert.assertEquals(_NAME, map.get("name"));
		Assert.assertEquals(_SOURCE_WEB_ID, map.get("sourcePortalInstanceId"));
		Assert.assertEquals(_VIRTUAL_HOST, map.get("virtualHost"));
		Assert.assertEquals(_WEB_ID, map.get("webId"));
	}

	@Test
	public void testCopyPortalInstanceSetsThePreferredLocale()
		throws Exception {

		_copyPortalInstance();

		ArgumentCaptor<AcceptLanguage> argumentCaptor = ArgumentCaptor.forClass(
			AcceptLanguage.class);

		Mockito.verify(
			_portalInstanceCopyResource
		).setContextAcceptLanguage(
			argumentCaptor.capture()
		);

		AcceptLanguage acceptLanguage = argumentCaptor.getValue();

		Assert.assertEquals(LocaleUtil.US, acceptLanguage.getPreferredLocale());
	}

	@Test
	public void testCopyPortalInstanceSetsTheVulcanBatchEngineResource()
		throws Exception {

		_copyPortalInstance();

		Mockito.verify(
			_portalInstanceCopyResource
		).setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource
		);
	}

	@Test
	public void testCopyPortalInstanceUngetsTheService() throws Exception {
		_copyPortalInstance();

		Mockito.verify(
			_componentServiceObjects
		).ungetService(
			_portalInstanceCopyResource
		);
	}

	@Test
	public void testCopyPortalInstanceUngetsTheServiceWhenTheBatchFails()
		throws Exception {

		Mockito.when(
			_portalInstanceCopyResource.postPortalInstanceCopyBatch(
				Mockito.isNull(), Mockito.any())
		).thenThrow(
			new IllegalStateException()
		);

		try {
			_copyPortalInstance();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}

		Mockito.verify(
			_componentServiceObjects
		).ungetService(
			_portalInstanceCopyResource
		);
	}

	@Test
	public void testGetDestinationCompanyId() {
		Assert.assertEquals(
			Long.valueOf(_DESTINATION_COMPANY_ID), _getDestinationCompanyId());

		_setParameter(
			"destinationCompanyId",
			StringPool.SPACE + _DESTINATION_COMPANY_ID + StringPool.SPACE);

		Assert.assertEquals(
			Long.valueOf(_DESTINATION_COMPANY_ID), _getDestinationCompanyId());

		_setParameter("destinationCompanyId", StringPool.BLANK);

		Assert.assertNull(_getDestinationCompanyId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetDestinationCompanyIdWithANonnumericValue() {
		_setParameter("destinationCompanyId", "abc");

		_getDestinationCompanyId();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetDestinationCompanyIdWithAnOverflowingValue() {
		_setParameter("destinationCompanyId", "99999999999999999999");

		_getDestinationCompanyId();
	}

	@Test
	public void testGetErrorMessageKey() {
		Assert.assertEquals(
			"an-unexpected-error-occurred",
			_getErrorMessageKey(new Exception()));
		Assert.assertEquals(
			"please-enter-a-valid-destination-company-id",
			_getErrorMessageKey(new IllegalArgumentException()));
		Assert.assertEquals(
			"please-enter-a-valid-name",
			_getErrorMessageKey(new CompanyNameException()));
		Assert.assertEquals(
			"please-enter-a-valid-virtual-host",
			_getErrorMessageKey(new CompanyVirtualHostException()));
		Assert.assertEquals(
			"please-enter-a-valid-web-id",
			_getErrorMessageKey(new CompanyWebIdException()));
		Assert.assertEquals(
			"the-default-instance-cannot-be-copied",
			_getErrorMessageKey(
				new IllegalArgumentException(
					"Company ID " + _SOURCE_COMPANY_ID +
						" is the default company ID")));
	}

	@Test
	public void testValidateCompany() throws Exception {
		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockPortalInstancePool(RandomTestUtil.randomLong())) {

			_validateCompany();
		}

		Mockito.verify(
			_companyLocalService
		).validateCompany(
			_WEB_ID, _VIRTUAL_HOST, _SOURCE_MX, 0
		);
	}

	@Test
	public void testValidateCompanyWithABlankDestinationCompanyId()
		throws Exception {

		_setParameter("destinationCompanyId", StringPool.BLANK);

		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockPortalInstancePool(
					RandomTestUtil.randomLong(), _DESTINATION_COMPANY_ID)) {

			_validateCompany();
		}
	}

	@Test(expected = CompanyNameException.class)
	public void testValidateCompanyWithABlankName() throws Exception {
		_setParameter("name", StringPool.BLANK);

		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockPortalInstancePool(RandomTestUtil.randomLong())) {

			_validateCompany();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateCompanyWithAnExistingDestinationCompanyId()
		throws Exception {

		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockPortalInstancePool(
					RandomTestUtil.randomLong(), _DESTINATION_COMPANY_ID)) {

			_validateCompany();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateCompanyWithAnInvalidDestinationCompanyId()
		throws Exception {

		_setParameter("destinationCompanyId", RandomTestUtil.randomString());

		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockPortalInstancePool(RandomTestUtil.randomLong())) {

			_validateCompany();
		}
	}

	@Test(expected = CompanyWebIdException.class)
	public void testValidateCompanyWithAnInvalidWebId() throws Exception {
		Mockito.doThrow(
			new CompanyWebIdException()
		).when(
			_companyLocalService
		).validateCompany(
			_WEB_ID, _VIRTUAL_HOST, _SOURCE_MX, 0
		);

		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockPortalInstancePool(RandomTestUtil.randomLong())) {

			_validateCompany();
		}
	}

	@Test
	public void testValidateCompanyWithTheDefaultSourceCompany()
		throws Exception {

		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockPortalInstancePool(_SOURCE_COMPANY_ID)) {

			_validateCompany();

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Company ID " + _SOURCE_COMPANY_ID +
					" is the default company ID",
				illegalArgumentException.getMessage());
		}

		Mockito.verifyNoInteractions(_companyLocalService);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateCompanyWithZeroDestinationCompanyId()
		throws Exception {

		_setParameter("destinationCompanyId", "0");

		try (MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				_mockPortalInstancePool(RandomTestUtil.randomLong())) {

			_validateCompany();
		}
	}

	private void _copyPortalInstance() throws Exception {
		ReflectionTestUtil.invoke(
			_copyDBPartitionCompanyMVCActionCommand, "_copyPortalInstance",
			new Class<?>[] {ActionRequest.class, Company.class}, _actionRequest,
			_sourceCompany);
	}

	private Long _getDestinationCompanyId() {
		return ReflectionTestUtil.invoke(
			_copyDBPartitionCompanyMVCActionCommand, "_getDestinationCompanyId",
			new Class<?>[] {ActionRequest.class}, _actionRequest);
	}

	private String _getErrorMessageKey(Exception exception) {
		return ReflectionTestUtil.invoke(
			_copyDBPartitionCompanyMVCActionCommand, "_getErrorMessageKey",
			new Class<?>[] {Exception.class}, exception);
	}

	private MockedStatic<PortalInstancePool> _mockPortalInstancePool(
		long defaultCompanyId, long... companyIds) {

		MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
			Mockito.mockStatic(PortalInstancePool.class);

		portalInstancePoolMockedStatic.when(
			PortalInstancePool::getCompanyIds
		).thenReturn(
			companyIds
		);

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

	private void _validateCompany() throws Exception {
		ReflectionTestUtil.invoke(
			_copyDBPartitionCompanyMVCActionCommand, "_validateCompany",
			new Class<?>[] {ActionRequest.class, Company.class}, _actionRequest,
			_sourceCompany);
	}

	private static final long _DESTINATION_COMPANY_ID =
		RandomTestUtil.randomLong();

	private static final String _NAME = RandomTestUtil.randomString();

	private static final long _SOURCE_COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _SOURCE_MX = RandomTestUtil.randomString();

	private static final String _SOURCE_WEB_ID = RandomTestUtil.randomString();

	private static final String _VIRTUAL_HOST = RandomTestUtil.randomString();

	private static final String _WEB_ID = RandomTestUtil.randomString();

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final CompanyLocalService _companyLocalService = Mockito.mock(
		CompanyLocalService.class);
	private final ComponentServiceObjects<PortalInstanceCopyResource>
		_componentServiceObjects = Mockito.mock(ComponentServiceObjects.class);
	private final CopyDBPartitionCompanyMVCActionCommand
		_copyDBPartitionCompanyMVCActionCommand =
			new CopyDBPartitionCompanyMVCActionCommand();
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortalInstanceCopyResource _portalInstanceCopyResource =
		Mockito.mock(PortalInstanceCopyResource.class);
	private final Company _sourceCompany = Mockito.mock(Company.class);
	private final VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource = Mockito.mock(
			VulcanBatchEngineImportTaskResource.class);
	private final VulcanBatchEngineImportTaskResourceFactory
		_vulcanBatchEngineImportTaskResourceFactory = Mockito.mock(
			VulcanBatchEngineImportTaskResourceFactory.class);

}