/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class ObjectEntryVulcanBatchEngineTaskItemDelegateTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		List<ObjectField> objectFields = Arrays.asList(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING, _OBJECT_FIELD_NAME));

		_companyObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				objectFields, ObjectDefinitionConstants.SCOPE_COMPANY,
				TestPropsValues.getUserId());
		_siteObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				objectFields, ObjectDefinitionConstants.SCOPE_SITE,
				TestPropsValues.getUserId());
	}

	@Test
	public void testCreateObjectEntry() throws Exception {

		// Insert strategy

		_testCreateObjectEntry("INSERT", _companyObjectDefinition, null, null);
		_testCreateObjectEntry(
			"INSERT", _siteObjectDefinition,
			String.valueOf(TestPropsValues.getGroupId()), null);

		// Upsert strategy with partial update

		_testCreateObjectEntry(
			"UPSERT", _companyObjectDefinition, null, "PARTIAL_UPDATE");
		_testCreateObjectEntry(
			"UPSERT", _siteObjectDefinition,
			String.valueOf(TestPropsValues.getGroupId()), "PARTIAL_UPDATE");

		// Upsert strategy with update

		_testCreateObjectEntry(
			"UPSERT", _companyObjectDefinition, null, "UPDATE");
		_testCreateObjectEntry(
			"UPSERT", _siteObjectDefinition,
			String.valueOf(TestPropsValues.getGroupId()), "UPDATE");
	}

	@Test
	public void testCreateObjectEntryWithNondefaultStorageType()
		throws Exception {

		// Upsert strategy with partial update

		_testCreateObjectEntryWithNondefaultStorageType(
			"UPSERT", "PARTIAL_UPDATE");

		// Upsert strategy with update

		_testCreateObjectEntryWithNondefaultStorageType("UPSERT", "UPDATE");
	}

	@Test
	public void testUpdateObjectEntry() throws Exception {

		// Partial update strategy

		_testUpdateObjectEntry(
			_companyObjectDefinition, null, "PARTIAL_UPDATE");
		_testUpdateObjectEntry(
			_siteObjectDefinition, String.valueOf(TestPropsValues.getGroupId()),
			"PARTIAL_UPDATE");

		// Update strategy

		_testUpdateObjectEntry(_companyObjectDefinition, null, "UPDATE");
		_testUpdateObjectEntry(
			_siteObjectDefinition, String.valueOf(TestPropsValues.getGroupId()),
			"UPDATE");
	}

	@Test
	public void testUpdateObjectEntryWithNondefaultStorageType()
		throws Exception {

		// Partial update strategy

		_testUpdateObjectEntryWithNondefaultStorageType("PARTIAL_UPDATE");

		// Update strategy

		_testUpdateObjectEntryWithNondefaultStorageType("UPDATE");
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, false, false,
				true, false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				TestObjectEntryManager.STORAGE_TYPE, Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						_OBJECT_FIELD_NAME)),
				Collections.emptyList(), new ServiceContext());

		return ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectEntryResource _getObjectEntryResource(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryVulcanBatchEngineTaskItemDelegateTest.class);

		try (ServiceTrackerMap<String, ObjectEntryResource> serviceTrackerMap =
				ServiceTrackerMapFactory.openSingleValueMap(
					bundle.getBundleContext(), ObjectEntryResource.class,
					"entity.class.name")) {

			ObjectEntryResource objectEntryResource =
				serviceTrackerMap.getService(
					StringBundler.concat(
						ObjectEntry.class.getName(), StringPool.POUND,
						StringUtil.toLowerCase(
							objectDefinition.getShortName())));

			objectEntryResource.setContextAcceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				});
			objectEntryResource.setContextCompany(
				CompanyLocalServiceUtil.getCompany(
					objectDefinition.getCompanyId()));
			objectEntryResource.setContextUser(user);

			Class<?> clazz = objectEntryResource.getClass();

			Method method = clazz.getMethod(
				"setObjectDefinition", ObjectDefinition.class);

			method.invoke(objectEntryResource, objectDefinition);

			return objectEntryResource;
		}
	}

	private void _testCreateObjectEntry(
			String createStrategy, ObjectDefinition objectDefinition,
			String scopeKey, String updateStrategy)
		throws Exception {

		ObjectEntry expectedObjectEntry = new ObjectEntry();

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			expectedObjectEntry.setExternalReferenceCode(
				RandomTestUtil.randomString());
		}
		else {
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
				ObjectEntryTestUtil.addObjectEntry(
					objectDefinition,
					HashMapBuilder.<String, Serializable>put(
						_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
					).build());

			expectedObjectEntry.setExternalReferenceCode(
				serviceBuilderObjectEntry.getExternalReferenceCode());
		}

		String expectedValue = RandomTestUtil.randomString();

		expectedObjectEntry.setProperties(
			HashMapBuilder.<String, Object>put(
				_OBJECT_FIELD_NAME, expectedValue
			).build());

		VulcanBatchEngineTaskItemDelegate<ObjectEntry>
			vulcanBatchEngineTaskItemDelegate =
				(VulcanBatchEngineTaskItemDelegate<ObjectEntry>)
					_getObjectEntryResource(
						objectDefinition, TestPropsValues.getUser());

		vulcanBatchEngineTaskItemDelegate.create(
			Arrays.asList(expectedObjectEntry),
			HashMapBuilder.<String, Serializable>put(
				"createStrategy", () -> createStrategy
			).put(
				"scopeKey", () -> scopeKey
			).put(
				"updateStrategy", updateStrategy
			).build());

		ObjectEntryResource objectEntryResource = _getObjectEntryResource(
			objectDefinition, TestPropsValues.getUser());

		ObjectEntry actualObjectEntry;

		if (scopeKey == null) {
			actualObjectEntry = objectEntryResource.getByExternalReferenceCode(
				expectedObjectEntry.getExternalReferenceCode());
		}
		else {
			actualObjectEntry =
				objectEntryResource.getScopeScopeKeyByExternalReferenceCode(
					scopeKey, expectedObjectEntry.getExternalReferenceCode());
		}

		Map<String, Object> properties = actualObjectEntry.getProperties();

		Assert.assertEquals(
			expectedValue, String.valueOf(properties.get(_OBJECT_FIELD_NAME)));
	}

	private void _testCreateObjectEntryWithNondefaultStorageType(
			String createStrategy, String updateStrategy)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryVulcanBatchEngineTaskItemDelegateTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		TestObjectEntryManager testObjectEntryManager =
			new TestObjectEntryManager();

		ServiceRegistration<ObjectEntryManager> serviceRegistration =
			bundleContext.registerService(
				ObjectEntryManager.class, testObjectEntryManager, null);

		ObjectDefinition objectDefinition = _addObjectDefinition();

		try {
			ObjectEntry objectEntry = new ObjectEntry();

			String externalReferenceCode = RandomTestUtil.randomString();

			objectEntry.setExternalReferenceCode(externalReferenceCode);

			VulcanBatchEngineTaskItemDelegate<ObjectEntry>
				vulcanBatchEngineTaskItemDelegate =
					(VulcanBatchEngineTaskItemDelegate<ObjectEntry>)
						_getObjectEntryResource(
							objectDefinition, TestPropsValues.getUser());

			vulcanBatchEngineTaskItemDelegate.create(
				Arrays.asList(objectEntry),
				HashMapBuilder.<String, Serializable>put(
					"createStrategy", () -> createStrategy
				).put(
					"updateStrategy", updateStrategy
				).build());

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				Assert.assertTrue(
					testObjectEntryManager.isPartialUpdateCalled());
			}
			else {
				Assert.assertTrue(testObjectEntryManager.isUpdateCalled());
			}

			Assert.assertEquals(
				externalReferenceCode,
				testObjectEntryManager.getExternalReferenceCode());
		}
		finally {
			ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());

			serviceRegistration.unregister();
		}
	}

	private void _testUpdateObjectEntry(
			ObjectDefinition objectDefinition, String scopeKey,
			String updateStrategy)
		throws Exception {

		ObjectEntry expectedObjectEntry = new ObjectEntry();

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			ObjectEntryTestUtil.addObjectEntry(
				objectDefinition,
				HashMapBuilder.<String, Serializable>put(
					_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
				).build());

		expectedObjectEntry.setExternalReferenceCode(
			serviceBuilderObjectEntry.getExternalReferenceCode());

		String expectedValue = RandomTestUtil.randomString();

		expectedObjectEntry.setProperties(
			HashMapBuilder.<String, Object>put(
				_OBJECT_FIELD_NAME, expectedValue
			).build());

		VulcanBatchEngineTaskItemDelegate<ObjectEntry>
			vulcanBatchEngineTaskItemDelegate =
				(VulcanBatchEngineTaskItemDelegate<ObjectEntry>)
					_getObjectEntryResource(
						objectDefinition, TestPropsValues.getUser());

		vulcanBatchEngineTaskItemDelegate.update(
			Arrays.asList(expectedObjectEntry),
			HashMapBuilder.<String, Serializable>put(
				"scopeKey", () -> scopeKey
			).put(
				"updateStrategy", updateStrategy
			).build());

		ObjectEntryResource objectEntryResource = _getObjectEntryResource(
			objectDefinition, TestPropsValues.getUser());

		ObjectEntry actualObjectEntry;

		if (scopeKey == null) {
			actualObjectEntry = objectEntryResource.getByExternalReferenceCode(
				serviceBuilderObjectEntry.getExternalReferenceCode());
		}
		else {
			actualObjectEntry =
				objectEntryResource.getScopeScopeKeyByExternalReferenceCode(
					scopeKey,
					serviceBuilderObjectEntry.getExternalReferenceCode());
		}

		Map<String, Object> properties = actualObjectEntry.getProperties();

		Assert.assertEquals(
			expectedValue, String.valueOf(properties.get(_OBJECT_FIELD_NAME)));
	}

	private void _testUpdateObjectEntryWithNondefaultStorageType(
			String updateStrategy)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryVulcanBatchEngineTaskItemDelegateTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		TestObjectEntryManager testObjectEntryManager =
			new TestObjectEntryManager();

		ServiceRegistration<ObjectEntryManager> serviceRegistration =
			bundleContext.registerService(
				ObjectEntryManager.class, testObjectEntryManager, null);

		ObjectDefinition objectDefinition = _addObjectDefinition();

		try {
			ObjectEntry objectEntry = new ObjectEntry();

			String externalReferenceCode = RandomTestUtil.randomString();

			objectEntry.setExternalReferenceCode(externalReferenceCode);

			VulcanBatchEngineTaskItemDelegate<ObjectEntry>
				vulcanBatchEngineTaskItemDelegate =
					(VulcanBatchEngineTaskItemDelegate<ObjectEntry>)
						_getObjectEntryResource(
							objectDefinition, TestPropsValues.getUser());

			vulcanBatchEngineTaskItemDelegate.update(
				Arrays.asList(objectEntry),
				HashMapBuilder.<String, Serializable>put(
					"updateStrategy", updateStrategy
				).build());

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				Assert.assertTrue(
					testObjectEntryManager.isPartialUpdateCalled());
			}
			else {
				Assert.assertTrue(testObjectEntryManager.isUpdateCalled());
			}

			Assert.assertEquals(
				externalReferenceCode,
				testObjectEntryManager.getExternalReferenceCode());
		}
		finally {
			ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());

			serviceRegistration.unregister();
		}
	}

	private static final String _OBJECT_FIELD_NAME =
		"x" + RandomTestUtil.randomString();

	@DeleteAfterTestRun
	private ObjectDefinition _companyObjectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _siteObjectDefinition;

	private static class TestObjectEntryManager implements ObjectEntryManager {

		public static final String STORAGE_TYPE = RandomTestUtil.randomString();

		@Override
		public ObjectEntry addObjectEntry(
				DTOConverterContext dtoConverterContext,
				ObjectDefinition objectDefinition, ObjectEntry objectEntry,
				String scopeKey)
			throws Exception {

			return objectEntry;
		}

		@Override
		public void deleteObjectEntry(
				long companyId, DTOConverterContext dtoConverterContext,
				String externalReferenceCode, ObjectDefinition objectDefinition,
				String scopeKey)
			throws Exception {
		}

		public String getExternalReferenceCode() {
			return _externalReferenceCode;
		}

		@Override
		public Page<ObjectEntry> getObjectEntries(
				long companyId, ObjectDefinition objectDefinition,
				String scopeKey, Aggregation aggregation,
				DTOConverterContext dtoConverterContext, String filterString,
				Pagination pagination, String search, Sort[] sorts)
			throws Exception {

			return Page.of(Collections.emptyList());
		}

		@Override
		public ObjectEntry getObjectEntry(
				long companyId, DTOConverterContext dtoConverterContext,
				String externalReferenceCode, ObjectDefinition objectDefinition,
				String scopeKey)
			throws Exception {

			ObjectEntry objectEntry = new ObjectEntry();

			objectEntry.setExternalReferenceCode(externalReferenceCode);

			return objectEntry;
		}

		@Override
		public String getStorageLabel(Locale locale) {
			return RandomTestUtil.randomString();
		}

		@Override
		public String getStorageType() {
			return STORAGE_TYPE;
		}

		public boolean isPartialUpdateCalled() {
			return _partialUpdateCalled;
		}

		public boolean isUpdateCalled() {
			return _updateCalled;
		}

		@Override
		public ObjectEntry partialUpdateObjectEntry(
				long companyId, DTOConverterContext dtoConverterContext,
				String externalReferenceCode, ObjectDefinition objectDefinition,
				ObjectEntry objectEntry, String scopeKey)
			throws Exception {

			_externalReferenceCode = externalReferenceCode;
			_partialUpdateCalled = true;

			return objectEntry;
		}

		@Override
		public ObjectEntry updateObjectEntry(
				long companyId, DTOConverterContext dtoConverterContext,
				String externalReferenceCode, ObjectDefinition objectDefinition,
				ObjectEntry objectEntry, String scopeKey)
			throws Exception {

			_externalReferenceCode = externalReferenceCode;
			_updateCalled = true;

			return objectEntry;
		}

		private String _externalReferenceCode;
		private boolean _partialUpdateCalled;
		private boolean _updateCalled;

	}

}