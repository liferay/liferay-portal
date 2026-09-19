/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.internal;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRegistry;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponseStatus;
import com.liferay.dynamic.data.mapping.data.provider.internal.rest.DDMRESTDataProviderSettings;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormInstanceFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Leonardo Barros
 */
public class DDMDataProviderInvokerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_setUpLanguageUtil();
		_setUpPortalUtil();
		_setUpResourceBundleUtil();
	}

	@Test
	public void testDDMDataProviderInvokeCommand() throws Exception {
		DDMDataProvider ddmDataProvider = Mockito.mock(DDMDataProvider.class);

		DDMDataProviderRequest.Builder builder =
			DDMDataProviderRequest.Builder.newBuilder();

		DDMDataProviderRequest ddmDataProviderRequest = builder.build();

		DDMDataProviderResponse.Builder responseBuilder =
			DDMDataProviderResponse.Builder.newBuilder();

		Mockito.when(
			ddmDataProvider.getData(ddmDataProviderRequest)
		).thenReturn(
			responseBuilder.withOutput(
				"output", "value"
			).build()
		);

		DDMDataProviderInvokeCommand ddmDataProviderInvokeCommand =
			new DDMDataProviderInvokeCommand(
				() -> ddmDataProvider.getData(ddmDataProviderRequest),
				Mockito.mock(DDMRESTDataProviderSettings.class),
				"ddmDataProviderInstanceName");

		DDMDataProviderResponse ddmDataProviderResponse =
			ddmDataProviderInvokeCommand.run();

		Assert.assertEquals(
			"value", ddmDataProviderResponse.getOutput("output", String.class));
	}

	@Test
	public void testDoInvoke() throws Exception {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl = Mockito.mock(
			DDMDataProviderInvokerImpl.class);

		DDMDataProviderRequest.Builder builder =
			DDMDataProviderRequest.Builder.newBuilder();

		DDMDataProviderRequest ddmDataProviderRequest =
			builder.withDDMDataProviderId(
				"2"
			).build();

		Mockito.when(
			ddmDataProviderInvokerImpl.fetchDDMDataProviderInstance("2")
		).thenReturn(
			null
		);

		DDMDataProvider ddmDataProvider = Mockito.mock(DDMDataProvider.class);

		Mockito.when(
			ddmDataProviderInvokerImpl.getDDMDataProvider("2", null)
		).thenReturn(
			ddmDataProvider
		);

		DDMDataProviderResponse.Builder responseBuilder =
			DDMDataProviderResponse.Builder.newBuilder();

		Mockito.when(
			ddmDataProvider.getData(ddmDataProviderRequest)
		).thenReturn(
			responseBuilder.withOutput(
				"output", 2
			).build()
		);

		Mockito.when(
			ddmDataProviderInvokerImpl.doInvoke(ddmDataProviderRequest)
		).thenCallRealMethod();

		DDMDataProviderResponse ddmDataProviderResponse =
			ddmDataProviderInvokerImpl.doInvoke(ddmDataProviderRequest);

		Assert.assertEquals(
			2, (int)ddmDataProviderResponse.getOutput("output", Number.class));
	}

	@Test
	public void testDoInvokeExternal() throws Exception {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl = Mockito.mock(
			DDMDataProviderInvokerImpl.class);

		DDMDataProviderRequest.Builder builder =
			DDMDataProviderRequest.Builder.newBuilder();

		DDMDataProviderRequest ddmDataProviderRequest =
			builder.withDDMDataProviderId(
				"1"
			).build();

		DDMDataProviderInstance ddmDataProviderInstance = Mockito.mock(
			DDMDataProviderInstance.class);

		Mockito.when(
			ddmDataProviderInvokerImpl.fetchDDMDataProviderInstance("1")
		).thenReturn(
			ddmDataProviderInstance
		);

		DDMDataProvider ddmDataProvider = Mockito.mock(DDMDataProvider.class);

		Mockito.when(
			ddmDataProviderInvokerImpl.getDDMDataProvider(
				"1", ddmDataProviderInstance)
		).thenReturn(
			ddmDataProvider
		);

		DDMDataProviderResponse.Builder responseBuilder =
			DDMDataProviderResponse.Builder.newBuilder();

		Mockito.when(
			ddmDataProviderInvokerImpl.doInvokeExternal(
				ddmDataProviderInstance, ddmDataProvider,
				ddmDataProviderRequest)
		).thenReturn(
			responseBuilder.withOutput(
				"test", "value"
			).build()
		);

		Mockito.when(
			ddmDataProviderInvokerImpl.doInvoke(ddmDataProviderRequest)
		).thenCallRealMethod();

		DDMDataProviderResponse ddmDataProviderResponse =
			ddmDataProviderInvokerImpl.doInvoke(ddmDataProviderRequest);

		Assert.assertEquals(
			"value", ddmDataProviderResponse.getOutput("test", String.class));
	}

	@Test
	public void testFetchDDMDataProviderInstance1() throws Exception {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl =
			new DDMDataProviderInvokerImpl();

		DDMDataProviderInstanceService ddmDataProviderInstanceService =
			Mockito.mock(DDMDataProviderInstanceService.class);

		Snapshot snapshot = Mockito.mock(Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			ddmDataProviderInstanceService
		);

		ReflectionTestUtil.setFieldValue(
			ddmDataProviderInvokerImpl,
			"ddmDataProviderInstanceServiceSnapshot", snapshot);

		Assert.assertNull(
			ddmDataProviderInvokerImpl.fetchDDMDataProviderInstance("1"));

		Mockito.verify(
			ddmDataProviderInstanceService, Mockito.times(1)
		).fetchDataProviderInstanceByUuid(
			"1"
		);

		Mockito.verify(
			ddmDataProviderInstanceService, Mockito.times(1)
		).fetchDataProviderInstance(
			1
		);
	}

	@Test
	public void testFetchDDMDataProviderInstance2() throws Exception {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl =
			new DDMDataProviderInvokerImpl();

		DDMDataProviderInstanceService ddmDataProviderInstanceService =
			Mockito.mock(DDMDataProviderInstanceService.class);

		Snapshot snapshot = Mockito.mock(Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			ddmDataProviderInstanceService
		);

		ReflectionTestUtil.setFieldValue(
			ddmDataProviderInvokerImpl,
			"ddmDataProviderInstanceServiceSnapshot", snapshot);

		DDMDataProviderInstance ddmDataProviderInstance = Mockito.mock(
			DDMDataProviderInstance.class);

		Mockito.when(
			ddmDataProviderInstanceService.fetchDataProviderInstance(1)
		).thenReturn(
			ddmDataProviderInstance
		);

		Assert.assertNotNull(
			ddmDataProviderInvokerImpl.fetchDDMDataProviderInstance("1"));

		Mockito.verify(
			ddmDataProviderInstanceService, Mockito.times(1)
		).fetchDataProviderInstanceByUuid(
			"1"
		);

		Mockito.verify(
			ddmDataProviderInstanceService, Mockito.times(1)
		).fetchDataProviderInstance(
			1
		);
	}

	@Test
	public void testFetchDataProviderNotFound() throws Exception {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl =
			new DDMDataProviderInvokerImpl();

		DDMDataProviderInstanceService ddmDataProviderInstanceService =
			Mockito.mock(DDMDataProviderInstanceService.class);

		Mockito.when(
			ddmDataProviderInstanceService.fetchDataProviderInstanceByUuid(
				"test")
		).thenReturn(
			null
		);

		Snapshot snapshot = Mockito.mock(Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			ddmDataProviderInstanceService
		);

		ReflectionTestUtil.setFieldValue(
			ddmDataProviderInvokerImpl,
			"ddmDataProviderInstanceServiceSnapshot", snapshot);

		Assert.assertNull(
			ddmDataProviderInvokerImpl.fetchDDMDataProviderInstance("test"));
	}

	@Test
	public void testGetDDMDataProviderByInstanceId() {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl =
			new DDMDataProviderInvokerImpl();

		DDMDataProviderRegistry ddmDataProviderRegistry = Mockito.mock(
			DDMDataProviderRegistry.class);

		ddmDataProviderInvokerImpl.ddmDataProviderRegistry =
			ddmDataProviderRegistry;

		Mockito.when(
			ddmDataProviderRegistry.getDDMDataProvider(Mockito.anyString())
		).thenReturn(
			null
		);

		DDMDataProvider ddmDataProvider = Mockito.mock(DDMDataProvider.class);

		Mockito.when(
			ddmDataProviderRegistry.getDDMDataProviderByInstanceId("1")
		).thenReturn(
			ddmDataProvider
		);

		DDMDataProvider result = ddmDataProviderInvokerImpl.getDDMDataProvider(
			"1", null);

		Assert.assertNotNull(result);

		Mockito.verify(
			ddmDataProviderRegistry, Mockito.times(1)
		).getDDMDataProviderByInstanceId(
			"1"
		);
	}

	@Test
	public void testGetDDMDataProviderFromTracker() {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl =
			new DDMDataProviderInvokerImpl();

		DDMDataProviderRegistry ddmDataProviderRegistry = Mockito.mock(
			DDMDataProviderRegistry.class);

		ddmDataProviderInvokerImpl.ddmDataProviderRegistry =
			ddmDataProviderRegistry;

		DDMDataProviderInstance ddmDataProviderInstance = Mockito.mock(
			DDMDataProviderInstance.class);

		Mockito.when(
			ddmDataProviderInstance.getType()
		).thenReturn(
			"rest"
		);

		DDMDataProvider ddmDataProvider = Mockito.mock(DDMDataProvider.class);

		Mockito.when(
			ddmDataProviderRegistry.getDDMDataProvider("rest")
		).thenReturn(
			ddmDataProvider
		);

		Assert.assertNotNull(
			ddmDataProviderInvokerImpl.getDDMDataProvider(
				"1", ddmDataProviderInstance));

		Mockito.verify(
			ddmDataProviderRegistry, Mockito.times(1)
		).getDDMDataProvider(
			"rest"
		);
	}

	@Test
	public void testGetHystrixFailureType() {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl =
			new DDMDataProviderInvokerImpl();

		HystrixRuntimeException hystrixRuntimeException =
			new HystrixRuntimeException(
				HystrixRuntimeException.FailureType.TIMEOUT, null, null, null,
				null);

		HystrixRuntimeException.FailureType failureType =
			ddmDataProviderInvokerImpl.getHystrixFailureType(
				hystrixRuntimeException);

		Assert.assertEquals(
			HystrixRuntimeException.FailureType.TIMEOUT, failureType);
	}

	@Test
	public void testInvoke() throws Exception {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl = Mockito.mock(
			DDMDataProviderInvokerImpl.class);

		DDMDataProviderRequest.Builder builder =
			DDMDataProviderRequest.Builder.newBuilder();

		DDMDataProviderRequest ddmDataProviderRequest = builder.build();

		Mockito.when(
			ddmDataProviderInvokerImpl.doInvoke(ddmDataProviderRequest)
		).thenThrow(
			Exception.class
		);

		Mockito.when(
			ddmDataProviderInvokerImpl.createDDMDataProviderErrorResponse(
				Mockito.any(Exception.class))
		).thenCallRealMethod();

		Mockito.when(
			ddmDataProviderInvokerImpl.invoke(ddmDataProviderRequest)
		).thenCallRealMethod();

		DDMDataProviderResponse ddmDataProviderResponse =
			ddmDataProviderInvokerImpl.invoke(ddmDataProviderRequest);

		Assert.assertEquals(
			DDMDataProviderResponseStatus.UNKNOWN_ERROR,
			ddmDataProviderResponse.getStatus());
	}

	@Test
	public void testPrincipalException() {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl =
			new DDMDataProviderInvokerImpl();

		DDMDataProviderResponse ddmDataProviderResponse =
			ddmDataProviderInvokerImpl.createDDMDataProviderErrorResponse(
				new PrincipalException());

		Assert.assertEquals(
			DDMDataProviderResponseStatus.UNAUTHORIZED,
			ddmDataProviderResponse.getStatus());
	}

	@Test
	public void testShortCircuitException() {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl = Mockito.mock(
			DDMDataProviderInvokerImpl.class);

		HystrixRuntimeException hystrixRuntimeException = Mockito.mock(
			HystrixRuntimeException.class);

		Mockito.when(
			ddmDataProviderInvokerImpl.getHystrixFailureType(
				hystrixRuntimeException)
		).thenReturn(
			HystrixRuntimeException.FailureType.SHORTCIRCUIT
		);

		Mockito.when(
			ddmDataProviderInvokerImpl.createDDMDataProviderErrorResponse(
				hystrixRuntimeException)
		).thenCallRealMethod();

		DDMDataProviderResponse ddmDataProviderResponse =
			ddmDataProviderInvokerImpl.createDDMDataProviderErrorResponse(
				hystrixRuntimeException);

		Assert.assertEquals(
			DDMDataProviderResponseStatus.SHORT_CIRCUIT,
			ddmDataProviderResponse.getStatus());
	}

	@Test
	public void testTimeOutChange() {
		DDMDataProvider ddmDataProvider = Mockito.mock(DDMDataProvider.class);

		DDMDataProviderRequest.Builder builder =
			DDMDataProviderRequest.Builder.newBuilder();

		DDMDataProviderRequest ddmDataProviderRequest = builder.build();

		int timeout = 10000;

		DDMDataProviderInvokeCommand ddmDataProviderInvokeCommand =
			new DDMDataProviderInvokeCommand(
				() -> ddmDataProvider.getData(ddmDataProviderRequest),
				_createDDMRESTDataProviderSettingsWithTimeout(timeout),
				"ddmDataProviderInstanceName");

		Assert.assertEquals(
			timeout,
			_getExecutionTimeoutInMilliseconds(ddmDataProviderInvokeCommand));

		timeout = 15000;

		ddmDataProviderInvokeCommand = new DDMDataProviderInvokeCommand(
			() -> ddmDataProvider.getData(ddmDataProviderRequest),
			_createDDMRESTDataProviderSettingsWithTimeout(timeout),
			"ddmDataProviderInstanceName");

		Assert.assertEquals(
			timeout,
			_getExecutionTimeoutInMilliseconds(ddmDataProviderInvokeCommand));
	}

	@Test
	public void testTimeOutException() {
		DDMDataProviderInvokerImpl ddmDataProviderInvokerImpl = Mockito.mock(
			DDMDataProviderInvokerImpl.class);

		HystrixRuntimeException hystrixRuntimeException = Mockito.mock(
			HystrixRuntimeException.class);

		Mockito.when(
			ddmDataProviderInvokerImpl.getHystrixFailureType(
				hystrixRuntimeException)
		).thenReturn(
			HystrixRuntimeException.FailureType.TIMEOUT
		);

		Mockito.when(
			ddmDataProviderInvokerImpl.createDDMDataProviderErrorResponse(
				hystrixRuntimeException)
		).thenCallRealMethod();

		DDMDataProviderResponse ddmDataProviderResponse =
			ddmDataProviderInvokerImpl.createDDMDataProviderErrorResponse(
				hystrixRuntimeException);

		Assert.assertEquals(
			DDMDataProviderResponseStatus.TIMEOUT,
			ddmDataProviderResponse.getStatus());
	}

	private static void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
	}

	private static void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		ResourceBundle resourceBundle = Mockito.mock(ResourceBundle.class);

		Mockito.when(
			portal.getResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			resourceBundle
		);

		portalUtil.setPortal(portal);
	}

	private static void _setUpResourceBundleUtil() {
		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);
	}

	private DDMRESTDataProviderSettings
		_createDDMRESTDataProviderSettingsWithTimeout(int timeout) {

		DDMForm ddmForm = DDMFormFactory.create(
			DDMRESTDataProviderSettings.class);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"timeout", String.valueOf(timeout)));

		return DDMFormInstanceFactory.create(
			DDMRESTDataProviderSettings.class, ddmFormValues);
	}

	private int _getExecutionTimeoutInMilliseconds(
		DDMDataProviderInvokeCommand ddmDataProviderInvokeCommand) {

		HystrixCommandProperties hystrixCommandProperties =
			ddmDataProviderInvokeCommand.getProperties();

		HystrixProperty<Integer> hystrixProperty =
			hystrixCommandProperties.executionTimeoutInMilliseconds();

		return hystrixProperty.get();
	}

}