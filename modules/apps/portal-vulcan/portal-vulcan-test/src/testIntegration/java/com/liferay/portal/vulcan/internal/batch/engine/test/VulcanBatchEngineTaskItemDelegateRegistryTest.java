/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.batch.engine.Field;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.OpenAPIResource;
import com.liferay.portal.vulcan.util.OpenAPIUtil;
import com.liferay.portal.vulcan.yaml.YAMLUtil;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Javier de Arcos
 */
@RunWith(Arquillian.class)
public class VulcanBatchEngineTaskItemDelegateRegistryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetEntityClassNames() throws Exception {
		Set<String> entityClassNames =
			_vulcanBatchEngineTaskItemDelegateRegistry.getEntityClassNames(
				TestPropsValues.getCompanyId());

		Assert.assertFalse(entityClassNames.isEmpty());

		Assert.assertTrue(
			entityClassNames.contains(
				"com.liferay.headless.delivery.dto.v1_0.StructuredContent"));
	}

	@Test
	public void testGetVulcanBatchEngineTaskItemDelegate() throws Exception {
		VulcanBatchEngineTaskItemDelegate<?> vulcanBatchEngineTaskItemDelegate =
			_vulcanBatchEngineTaskItemDelegateRegistry.
				getVulcanBatchEngineTaskItemDelegate(
					TestPropsValues.getCompanyId(),
					"com.liferay.headless.delivery.dto.v1_0.StructuredContent");

		Assert.assertNotNull(vulcanBatchEngineTaskItemDelegate);

		Class<?> resourceClass =
			vulcanBatchEngineTaskItemDelegate.getResourceClass();

		Assert.assertEquals(
			"com.liferay.headless.delivery.internal.resource.v1_0." +
				"StructuredContentResourceImpl",
			resourceClass.getName());
	}

	@Test
	public void testIsBatchPlannerExportEnabled() throws Exception {
		_registerVulcanBatchEngineTaskItemDelegate(null, true, false);

		Assert.assertTrue(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerExportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
		Assert.assertFalse(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerImportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
	}

	@Test
	public void testIsBatchPlannerExportEnabledCompanyScoped()
		throws Exception {

		_registerVulcanBatchEngineTaskItemDelegate(
			TestPropsValues.getCompanyId(), true, false);

		Assert.assertTrue(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerExportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
		Assert.assertFalse(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerImportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
	}

	@Test
	public void testIsBatchPlannerExportImportDisabled() throws Exception {
		_registerVulcanBatchEngineTaskItemDelegate(null, false, false);

		Assert.assertFalse(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerExportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
		Assert.assertFalse(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerImportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
	}

	@Test
	public void testIsBatchPlannerExportImportDisabledCompanyScoped()
		throws Exception {

		_registerVulcanBatchEngineTaskItemDelegate(
			TestPropsValues.getCompanyId(), false, false);

		Assert.assertFalse(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerExportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
		Assert.assertFalse(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerImportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
	}

	@Test
	public void testIsBatchPlannerExportImportEnabled() throws Exception {
		_registerVulcanBatchEngineTaskItemDelegate(null, true, true);

		Assert.assertTrue(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerExportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
		Assert.assertTrue(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerImportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
	}

	@Test
	public void testIsBatchPlannerExportImportEnabledCompanyScoped()
		throws Exception {

		_registerVulcanBatchEngineTaskItemDelegate(
			TestPropsValues.getCompanyId(), true, true);

		Assert.assertTrue(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerExportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
		Assert.assertTrue(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerImportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
	}

	@Test
	public void testIsBatchPlannerImportEnabled() throws Exception {
		_registerVulcanBatchEngineTaskItemDelegate(null, false, true);

		Assert.assertFalse(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerExportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
		Assert.assertTrue(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerImportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
	}

	@Test
	public void testIsBatchPlannerImportEnabledCompanyScoped()
		throws Exception {

		_registerVulcanBatchEngineTaskItemDelegate(
			TestPropsValues.getCompanyId(), false, true);

		Assert.assertFalse(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerExportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
		Assert.assertTrue(
			_vulcanBatchEngineTaskItemDelegateRegistry.
				isBatchPlannerImportEnabled(
					TestPropsValues.getCompanyId(),
					TestVulcanBatchEngineTaskItemDelegate.class.getName()));
	}

	@Test
	public void testRegistryAndOpenAPIUtilToGetEntityMetadata()
		throws Exception {

		Set<String> entityClassNames =
			_vulcanBatchEngineTaskItemDelegateRegistry.getEntityClassNames(
				TestPropsValues.getCompanyId());

		Assert.assertTrue(
			entityClassNames.contains(
				"com.liferay.headless.delivery.dto.v1_0.StructuredContent"));

		VulcanBatchEngineTaskItemDelegate<?> vulcanBatchEngineTaskItemDelegate =
			_vulcanBatchEngineTaskItemDelegateRegistry.
				getVulcanBatchEngineTaskItemDelegate(
					TestPropsValues.getCompanyId(),
					"com.liferay.headless.delivery.dto.v1_0.StructuredContent");

		Assert.assertNotNull(vulcanBatchEngineTaskItemDelegate);

		Class<?> resourceClass =
			vulcanBatchEngineTaskItemDelegate.getResourceClass();

		Assert.assertEquals(
			"com.liferay.headless.delivery.internal.resource.v1_0." +
				"StructuredContentResourceImpl",
			resourceClass.getName());

		Assert.assertEquals(
			"v1.0", vulcanBatchEngineTaskItemDelegate.getVersion());

		Response response = _openAPIResource.getOpenAPI(
			Collections.singleton(resourceClass), "yaml");

		Assert.assertEquals(200, response.getStatus());

		OpenAPIYAML openAPIYAML = YAMLUtil.loadOpenAPIYAML(
			(String)response.getEntity());

		Assert.assertNotNull(openAPIYAML);

		List<String> createEntityScopes = OpenAPIUtil.getCreateEntityScopes(
			"StructuredContent", openAPIYAML);

		Assert.assertEquals(
			createEntityScopes.toString(), 3, createEntityScopes.size());
		AssertUtils.assertEquals(
			Arrays.asList("assetLibrary", "site", "structuredContentFolder"),
			createEntityScopes);

		List<String> readEntityScopes = OpenAPIUtil.getReadEntityScopes(
			"StructuredContent", openAPIYAML);

		Assert.assertEquals(
			readEntityScopes.toString(), 4, readEntityScopes.size());
		AssertUtils.assertEquals(
			Arrays.asList(
				"assetLibrary", "contentStructure", "site",
				"structuredContentFolder"),
			readEntityScopes);

		Map<String, Field> dtoEntityFields = OpenAPIUtil.getDTOEntityFields(
			"StructuredContent", openAPIYAML);

		_assertContainsAll(
			dtoEntityFields.keySet(), "contentFields", "description", "id",
			"title");
	}

	private void _assertContainsAll(
		Collection<String> collection, String... keys) {

		for (String key : keys) {
			Assert.assertTrue(collection.contains(key));
		}
	}

	private void _registerVulcanBatchEngineTaskItemDelegate(
		Long companyId, boolean exportEnabled, boolean importEnabled) {

		Bundle bundle = FrameworkUtil.getBundle(
			VulcanBatchEngineTaskItemDelegateRegistryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			VulcanBatchEngineTaskItemDelegate.class,
			new TestVulcanBatchEngineTaskItemDelegate(),
			HashMapDictionaryBuilder.<String, Object>put(
				"batch.engine.entity.class.name",
				TestVulcanBatchEngineTaskItemDelegate.class.getName()
			).put(
				"batch.engine.task.item.delegate", "true"
			).put(
				"batch.planner.export.enabled", String.valueOf(exportEnabled)
			).put(
				"batch.planner.import.enabled", String.valueOf(importEnabled)
			).put(
				"companyId",
				() -> {
					if (companyId != null) {
						return Arrays.asList(String.valueOf(companyId));
					}

					return null;
				}
			).build());
	}

	@Inject
	private OpenAPIResource _openAPIResource;

	private ServiceRegistration<VulcanBatchEngineTaskItemDelegate>
		_serviceRegistration;

	@Inject
	private VulcanBatchEngineTaskItemDelegateRegistry
		_vulcanBatchEngineTaskItemDelegateRegistry;

	private static class TestVulcanBatchEngineTaskItemDelegate
		implements VulcanBatchEngineTaskItemDelegate<Object> {

		@Override
		public void create(
				Collection<Object> items, Map<String, Serializable> parameters)
			throws Exception {
		}

		@Override
		public void delete(
				Collection<Object> items, Map<String, Serializable> parameters)
			throws Exception {
		}

		@Override
		public EntityModel getEntityModel(
				Map<String, List<String>> multivaluedMap)
			throws Exception {

			return null;
		}

		@Override
		public Page<Object> read(
				Filter filter, Pagination pagination, Sort[] sorts,
				Map<String, Serializable> parameters, String search)
			throws Exception {

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
		public void update(
				Collection<Object> items, Map<String, Serializable> parameters)
			throws Exception {
		}

	}

}