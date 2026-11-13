/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.lar.DefaultConfigurationPortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.test.util.vulcan.batch.engine.TestExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEventExtraDataContributor;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class BatchEnginePortletDataHandlerRegistrarTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo({"LPD-56301", "LPD-65119", "LPD-68124", "LPD-80308"})
	public void testBatchEnginePortletDataHandlerRegistration()
		throws Exception {

		String portletId1 = RandomTestUtil.randomString();

		Assert.assertNull(
			_portletDataHandlerProvider.provide(
				TestPropsValues.getCompanyId(), portletId1));

		String portletId2 = RandomTestUtil.randomString();

		Assert.assertNull(
			_portletDataHandlerProvider.provide(
				TestPropsValues.getCompanyId(), portletId2));

		String className1 = RandomTestUtil.randomString();
		String className2 = RandomTestUtil.randomString();
		String className3 = RandomTestUtil.randomString();

		String key1 = RandomTestUtil.randomString();
		String key2 = RandomTestUtil.randomString();
		String key3 = RandomTestUtil.randomString();

		String languageKey1 = RandomTestUtil.randomString();
		String languageKey2 = RandomTestUtil.randomString();
		String languageKey3 = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable1 = _registerServiceWithSafeCloseable(
				Portlet.class,
				new GenericPortlet() {
				},
				MapUtil.singletonDictionary(
					"jakarta.portlet.name", portletId1));
			SafeCloseable safeCloseable2 = _registerServiceWithSafeCloseable(
				Portlet.class,
				new GenericPortlet() {
				},
				MapUtil.singletonDictionary(
					"jakarta.portlet.name", portletId2));
			SafeCloseable safeCloseable3 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className1, null, key1, languageKey1, portletId1),
				HashMapDictionaryBuilder.put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className1
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build());
			SafeCloseable safeCloseable4 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className2, null, key2, languageKey2, portletId1),
				HashMapDictionaryBuilder.put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className2
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build());
			SafeCloseable safeCloseable5 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className2, null, key3, languageKey3, portletId1),
				HashMapDictionaryBuilder.put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className2
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build());
			SafeCloseable safeCloseable6 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className3, null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), portletId2),
				HashMapDictionaryBuilder.put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className3
				).put(
					"companyId", String.valueOf(TestPropsValues.getCompanyId())
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build());
			SafeCloseable safeCloseable7 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className3, null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), portletId2),
				HashMapDictionaryBuilder.put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className3
				).put(
					"companyId", String.valueOf(TestPropsValues.getCompanyId())
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build())) {

			Assert.assertEquals(
				1, _getRegisteredPortletDataHandlersCount(portletId1));
			Assert.assertEquals(
				1, _getRegisteredPortletDataHandlersCount(portletId2));

			Assert.assertNotNull(
				_getRegisteredSystemEventExtraDataContributor(
					TestPropsValues.getCompanyId(), portletId1));
			Assert.assertNotNull(
				_getRegisteredSystemEventExtraDataContributor(
					TestPropsValues.getCompanyId(), portletId2));

			Assert.assertNotNull(
				_getRegisteredSystemEventExtraDataContributor(
					RandomTestUtil.randomLong(), portletId1));
			Assert.assertNull(
				_getRegisteredSystemEventExtraDataContributor(
					RandomTestUtil.randomLong(), portletId2));

			safeCloseable7.close();

			Assert.assertNull(
				_getRegisteredSystemEventExtraDataContributor(
					TestPropsValues.getCompanyId(), portletId2));

			_assertPortletDataHandler(
				TestPropsValues.getCompanyId(), portletId1,
				portletDataHandler ->
					StringUtil.contains(
						ClassUtil.getClassName(portletDataHandler),
						"BatchEnginePortletDataHandler", StringPool.PERIOD) &&
					Arrays.equals(
						new String[] {className1, className2},
						portletDataHandler.getClassNames()) &&
					_hasPortletDataHandlerControls(
						new PortletDataHandlerControl[] {
							new PortletDataHandlerBoolean(
								portletId1, key1, languageKey1, true, false,
								null, key1, null),
							new PortletDataHandlerBoolean(
								portletId1, key2, languageKey2, true, false,
								null, key2, null),
							new PortletDataHandlerBoolean(
								portletId1, key3, languageKey3, true, false,
								null, key3, null)
						},
						portletDataHandler.
							getExportPortletDataHandlerControls()));
			_assertPortletDataHandler(
				TestPropsValues.getCompanyId(), portletId2,
				portletDataHandler ->
					StringUtil.contains(
						ClassUtil.getClassName(portletDataHandler),
						"BatchEnginePortletDataHandler", StringPool.PERIOD) &&
					Arrays.equals(
						new String[] {className3},
						portletDataHandler.getClassNames()) &&
					_hasPortletDataHandlerControls(
						new PortletDataHandlerControl[0],
						portletDataHandler.
							getExportPortletDataHandlerControls()));

			_assertPortletDataHandler(
				RandomTestUtil.randomLong(), portletId1,
				portletDataHandler ->
					StringUtil.contains(
						ClassUtil.getClassName(portletDataHandler),
						"BatchEnginePortletDataHandler", StringPool.PERIOD) &&
					Arrays.equals(
						new String[] {className1, className2},
						portletDataHandler.getClassNames()));
			_assertPortletDataHandler(
				RandomTestUtil.randomLong(), portletId2,
				portletDataHandler -> portletDataHandler instanceof
					DefaultConfigurationPortletDataHandler);

			safeCloseable3.close();

			Assert.assertNotNull(
				_getRegisteredSystemEventExtraDataContributor(
					TestPropsValues.getCompanyId(), portletId1));

			_assertPortletDataHandler(
				TestPropsValues.getCompanyId(), portletId1,
				portletDataHandler ->
					StringUtil.contains(
						ClassUtil.getClassName(portletDataHandler),
						"BatchEnginePortletDataHandler", StringPool.PERIOD) &&
					Arrays.equals(
						new String[] {className2},
						portletDataHandler.getClassNames()) &&
					_hasPortletDataHandlerControls(
						new PortletDataHandlerControl[] {
							new PortletDataHandlerBoolean(
								portletId1, key2, languageKey2, true, false,
								null, key2, null),
							new PortletDataHandlerBoolean(
								portletId1, key3, languageKey3, true, false,
								null, key3, null)
						},
						portletDataHandler.
							getExportPortletDataHandlerControls()));

			safeCloseable4.close();

			Assert.assertNull(
				_getRegisteredSystemEventExtraDataContributor(
					TestPropsValues.getCompanyId(), portletId1));

			_assertPortletDataHandler(
				TestPropsValues.getCompanyId(), portletId1,
				portletDataHandler ->
					StringUtil.contains(
						ClassUtil.getClassName(portletDataHandler),
						"BatchEnginePortletDataHandler", StringPool.PERIOD) &&
					Arrays.equals(
						new String[] {className2},
						portletDataHandler.getClassNames()) &&
					_hasPortletDataHandlerControls(
						new PortletDataHandlerControl[0],
						portletDataHandler.
							getExportPortletDataHandlerControls()));

			safeCloseable5.close();

			_assertPortletDataHandler(
				TestPropsValues.getCompanyId(), portletId1,
				portletDataHandler -> portletDataHandler instanceof
					DefaultConfigurationPortletDataHandler);
		}
	}

	@Test
	@TestInfo("LPD-80308")
	public void testSystemEventExtraDataContributor() throws Exception {
		String className = RandomTestUtil.randomString();
		String key1 = RandomTestUtil.randomString();
		String key2 = RandomTestUtil.randomString();
		String key3 = RandomTestUtil.randomString();
		String portletId = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable1 = _registerServiceWithSafeCloseable(
				Portlet.class,
				new GenericPortlet() {
				},
				MapUtil.singletonDictionary("jakarta.portlet.name", portletId));
			SafeCloseable safeCloseable2 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className,
					baseModel -> {
						TestBaseModel testBaseModel = (TestBaseModel)baseModel;

						return testBaseModel.isApplicable(key1);
					},
					key1, RandomTestUtil.randomString(), portletId),
				HashMapDictionaryBuilder.<String, Object>put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build());
			SafeCloseable safeCloseable3 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className,
					baseModel -> {
						TestBaseModel testBaseModel = (TestBaseModel)baseModel;

						return testBaseModel.isApplicable(key2);
					},
					key2, RandomTestUtil.randomString(), portletId),
				HashMapDictionaryBuilder.<String, Object>put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build());
			SafeCloseable safeCloseable4 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className, null, key3, RandomTestUtil.randomString(),
					portletId),
				HashMapDictionaryBuilder.<String, Object>put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build())) {

			SystemEventExtraDataContributor systemEventExtraDataContributor =
				_getRegisteredSystemEventExtraDataContributor(
					TestPropsValues.getCompanyId(), portletId);

			Assert.assertNotNull(systemEventExtraDataContributor);

			String extraData = "{\"key\":\"value\"}";

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				systemEventExtraDataContributor.contribute(
					new TestBaseModel(key1, className), extraData));

			Assert.assertEquals(key1, jsonObject.get("type"));
			Assert.assertEquals("value", jsonObject.get("key"));

			jsonObject = JSONFactoryUtil.createJSONObject(
				systemEventExtraDataContributor.contribute(
					new TestBaseModel(key2, className), extraData));

			Assert.assertEquals(key2, jsonObject.get("type"));
			Assert.assertEquals("value", jsonObject.get("key"));

			jsonObject = JSONFactoryUtil.createJSONObject(
				systemEventExtraDataContributor.contribute(
					new TestBaseModel(RandomTestUtil.randomString(), className),
					extraData));

			Assert.assertNull(jsonObject.get("type"));
			Assert.assertEquals("value", jsonObject.get("key"));

			jsonObject = JSONFactoryUtil.createJSONObject(
				systemEventExtraDataContributor.contribute(
					new TestBaseModel(key1, RandomTestUtil.randomString()),
					extraData));

			Assert.assertNull(jsonObject.get("type"));
			Assert.assertEquals("value", jsonObject.get("key"));

			jsonObject = JSONFactoryUtil.createJSONObject(
				systemEventExtraDataContributor.contribute(null, extraData));

			Assert.assertNull(jsonObject.get("type"));
			Assert.assertEquals("value", jsonObject.get("key"));

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.exportimport.internal.data.handler." +
						"BatchEnginePortletDataHandler",
					LoggerTestUtil.WARN)) {

				systemEventExtraDataContributor.contribute(
					new TestBaseModel(RandomTestUtil.randomString(), className),
					extraData);

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					StringBundler.concat(
						"ExportImportDescriptor with key ", key3,
						"  declares shared model class ", className,
						" but does not implement ",
						"getApplicableModelFunction"),
					logEntry.getMessage());
			}
		}
	}

	private void _assertPortletDataHandler(
			long companyId, String portletId,
			UnsafeFunction<PortletDataHandler, Boolean, Exception>
				unsafeFunction)
		throws Exception {

		Assert.assertNotNull(
			_getPortletDataHandler(companyId, portletId, unsafeFunction));
	}

	private PortletDataHandler _getPortletDataHandler(
			long companyId, String portletId,
			UnsafeFunction<PortletDataHandler, Boolean, Exception>
				unsafeFunction)
		throws Exception {

		long startTime = System.currentTimeMillis();

		while ((System.currentTimeMillis() - startTime) < 5000) {
			PortletDataHandler portletDataHandler =
				_portletDataHandlerProvider.provide(companyId, portletId);

			if (unsafeFunction.apply(portletDataHandler)) {
				return portletDataHandler;
			}

			Thread.sleep(50);
		}

		return null;
	}

	private int _getRegisteredPortletDataHandlersCount(String portletId)
		throws InvalidSyntaxException {

		Bundle bundle = FrameworkUtil.getBundle(
			BatchEnginePortletDataHandlerRegistrarTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<PortletDataHandler>> serviceReferences =
			bundleContext.getServiceReferences(
				PortletDataHandler.class,
				"(jakarta.portlet.name=" + portletId + ")");

		return serviceReferences.size();
	}

	private SystemEventExtraDataContributor
			_getRegisteredSystemEventExtraDataContributor(
				long companyId, String portletId)
		throws InvalidSyntaxException {

		Bundle bundle = FrameworkUtil.getBundle(
			BatchEnginePortletDataHandlerRegistrarTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<SystemEventExtraDataContributor>>
			serviceReferences = bundleContext.getServiceReferences(
				SystemEventExtraDataContributor.class,
				StringBundler.concat(
					"(&(jakarta.portlet.name=", portletId, ")(|(companyId=",
					companyId, ")(companyId=0)))"));

		if (serviceReferences.isEmpty()) {
			return null;
		}

		return bundleContext.getService(
			serviceReferences.iterator(
			).next());
	}

	private boolean _hasPortletDataHandlerControls(
		PortletDataHandlerControl[] expectedPortletDataHandlerControls,
		PortletDataHandlerControl[] portletDataHandlerControls) {

		if (expectedPortletDataHandlerControls.length !=
				portletDataHandlerControls.length) {

			return false;
		}

		if (portletDataHandlerControls.length == 0) {
			return true;
		}

		for (PortletDataHandlerControl expectedPortletDataHandlerControl :
				expectedPortletDataHandlerControls) {

			for (PortletDataHandlerControl portletDataHandlerControl :
					portletDataHandlerControls) {

				if (Objects.equals(
						expectedPortletDataHandlerControl.getName(),
						portletDataHandlerControl.getName()) &&
					Objects.equals(
						expectedPortletDataHandlerControl.getLabel(),
						portletDataHandlerControl.getLabel()) &&
					(expectedPortletDataHandlerControl.isDisabled() ==
						portletDataHandlerControl.isDisabled())) {

					return true;
				}
			}
		}

		return false;
	}

	private <S> SafeCloseable _registerServiceWithSafeCloseable(
		Class<S> clazz, S service, Dictionary<String, ?> properties) {

		Bundle bundle = FrameworkUtil.getBundle(
			BatchEnginePortletDataHandlerRegistrarTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<S> serviceRegistration =
			bundleContext.registerService(clazz, service, properties);

		AtomicBoolean unregistered = new AtomicBoolean(false);

		return () -> {
			if (unregistered.get()) {
				return;
			}

			unregistered.set(true);

			serviceRegistration.unregister();
		};
	}

	@Inject
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	private static class TestBaseModel implements BaseModel<TestBaseModel> {

		public TestBaseModel(String key, String modelClassName) {
			_key = key;
			_modelClassName = modelClassName;
		}

		@Override
		public Object clone() {
			return null;
		}

		@Override
		public TestBaseModel cloneWithOriginalValues() {
			return null;
		}

		@Override
		public int compareTo(TestBaseModel o) {
			return 0;
		}

		@Override
		public ExpandoBridge getExpandoBridge() {
			return null;
		}

		@Override
		public Map<String, Object> getModelAttributes() {
			return null;
		}

		@Override
		public Class<?> getModelClass() {
			return null;
		}

		@Override
		public String getModelClassName() {
			return _modelClassName;
		}

		@Override
		public Serializable getPrimaryKeyObj() {
			return null;
		}

		public Boolean isApplicable(String key) {
			return Objects.equals(_key, key);
		}

		@Override
		public boolean isCachedModel() {
			return false;
		}

		@Override
		public boolean isEntityCacheEnabled() {
			return false;
		}

		@Override
		public boolean isEscapedModel() {
			return false;
		}

		@Override
		public boolean isFinderCacheEnabled() {
			return false;
		}

		@Override
		public boolean isNew() {
			return false;
		}

		@Override
		public void resetOriginalValues() {
		}

		@Override
		public void setCachedModel(boolean cachedModel) {
		}

		@Override
		public void setExpandoBridgeAttributes(BaseModel<?> baseModel) {
		}

		@Override
		public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		}

		@Override
		public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		}

		@Override
		public void setModelAttributes(Map<String, Object> attributes) {
		}

		@Override
		public void setNew(boolean n) {
		}

		@Override
		public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		}

		@Override
		public CacheModel<TestBaseModel> toCacheModel() {
			return null;
		}

		@Override
		public TestBaseModel toEscapedModel() {
			return null;
		}

		@Override
		public TestBaseModel toUnescapedModel() {
			return null;
		}

		private final String _key;
		private final String _modelClassName;

	}

}