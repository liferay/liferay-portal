/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;

import jakarta.ws.rs.core.UriInfo;

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

	@FeatureFlag("LPD-35914")
	@Test
	@TestInfo({"LPD-56301", "LPD-65119"})
	public void test() throws Exception {
		String portletId = RandomTestUtil.randomString();

		Assert.assertNull(
			_portletDataHandlerProvider.provide(
				TestPropsValues.getCompanyId(), portletId));

		String className1 = RandomTestUtil.randomString();
		String className2 = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable1 = _registerServiceWithSafeCloseable(
				Portlet.class,
				new GenericPortlet() {
				},
				MapUtil.singletonDictionary("jakarta.portlet.name", portletId));
			SafeCloseable safeCloseable2 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className1, portletId),
				HashMapDictionaryBuilder.put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className1
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build());
			SafeCloseable safeCloseable3 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className2, portletId),
				HashMapDictionaryBuilder.put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className2
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build())) {

			Assert.assertEquals(
				0, _getRegisteredPortletDataHandlersCount(portletId));

			FeatureFlagTestUtil.invokeFeatureFlagListeners(
				TestPropsValues.getCompanyId(), true, "LPD-35914");

			try {
				_assertPortletDataHandler(
					TestPropsValues.getCompanyId(), portletId,
					portletDataHandler ->
						StringUtil.contains(
							ClassUtil.getClassName(portletDataHandler),
							"BatchEnginePortletDataHandler",
							StringPool.PERIOD) &&
						Arrays.equals(
							new String[] {className1, className2},
							portletDataHandler.getClassNames()) &&
						_hasPortletDataHandlerControls(
							new PortletDataHandlerControl[] {
								new PortletDataHandlerBoolean(
									portletId, className1, null, true, false,
									null, className1, null),
								new PortletDataHandlerBoolean(
									portletId, className2, null, true, false,
									null, className2, null)
							},
							portletDataHandler.getExportControls()));

				Assert.assertEquals(
					1, _getRegisteredPortletDataHandlersCount(portletId));

				_assertPortletDataHandler(
					RandomTestUtil.randomLong(), portletId,
					portletDataHandler -> StringUtil.contains(
						ClassUtil.getClassName(portletDataHandler),
						"DefaultPortletDataHandler", StringPool.PERIOD));

				safeCloseable2.close();

				_assertPortletDataHandler(
					TestPropsValues.getCompanyId(), portletId,
					portletDataHandler ->
						StringUtil.contains(
							ClassUtil.getClassName(portletDataHandler),
							"BatchEnginePortletDataHandler",
							StringPool.PERIOD) &&
						Arrays.equals(
							new String[] {className2},
							portletDataHandler.getClassNames()) &&
						_hasPortletDataHandlerControls(
							new PortletDataHandlerControl[0],
							portletDataHandler.getExportControls()));

				safeCloseable3.close();

				_assertPortletDataHandler(
					TestPropsValues.getCompanyId(), portletId,
					portletDataHandler -> StringUtil.contains(
						ClassUtil.getClassName(portletDataHandler),
						"DefaultPortletDataHandler", StringPool.PERIOD));
			}
			finally {
				FeatureFlagTestUtil.invokeFeatureFlagListeners(
					TestPropsValues.getCompanyId(), false, "LPD-35914");
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
						expectedPortletDataHandlerControl.getControlName(),
						portletDataHandlerControl.getControlName()) &&
					Objects.equals(
						expectedPortletDataHandlerControl.getControlLabel(),
						portletDataHandlerControl.getControlLabel()) &&
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

	private static class TestExportImportVulcanBatchEngineTaskItemDelegate
		implements ExportImportVulcanBatchEngineTaskItemDelegate<Object>,
				   VulcanBatchEngineTaskItemDelegate<Object> {

		public TestExportImportVulcanBatchEngineTaskItemDelegate(
			String itemClassName, String portletId) {

			_itemClassName = itemClassName;
			_portletId = portletId;
		}

		@Override
		public void create(
			Collection<Object> items, Map<String, Serializable> parameters) {
		}

		@Override
		public void delete(
			Collection<Object> items, Map<String, Serializable> parameters) {
		}

		@Override
		public EntityModel getEntityModel(
			Map<String, List<String>> multivaluedMap) {

			return null;
		}

		@Override
		public ExportImportDescriptor getExportImportDescriptor() {
			return new ExportImportDescriptor() {

				@Override
				public String getItemClassName() {
					return _itemClassName;
				}

				@Override
				public String getPortletId() {
					return _portletId;
				}

				@Override
				public Scope getScope() {
					return Scope.COMPANY;
				}

			};
		}

		@Override
		public Page<Object> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search) {

			return null;
		}

		@Override
		public void setContextBatchUnsafeBiConsumer(
			UnsafeBiConsumer
				<Collection<Object>, UnsafeFunction<Object, Object, Exception>,
				 Exception> contextBatchUnsafeBiConsumer) {
		}

		@Override
		public void setContextCompany(Company contextCompany) {
		}

		@Override
		public void setContextUriInfo(UriInfo uriInfo) {
		}

		@Override
		public void setContextUser(User contextUser) {
		}

		@Override
		public void setGroupLocalService(GroupLocalService groupLocalService) {
		}

		@Override
		public void setLanguageId(String languageId) {
		}

		@Override
		public void setResourceActionLocalService(
			ResourceActionLocalService resourceActionLocalService) {
		}

		@Override
		public void setResourcePermissionLocalService(
			ResourcePermissionLocalService resourcePermissionLocalService) {
		}

		@Override
		public void setRoleLocalService(RoleLocalService roleLocalService) {
		}

		@Override
		public void update(
			Collection<Object> items, Map<String, Serializable> parameters) {
		}

		private final String _itemClassName;
		private final String _portletId;

	}

}