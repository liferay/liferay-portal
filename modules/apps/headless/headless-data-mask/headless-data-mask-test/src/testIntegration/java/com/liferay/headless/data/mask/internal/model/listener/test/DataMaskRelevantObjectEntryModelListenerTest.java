/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.headless.data.mask.test.util.DataMaskTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class DataMaskRelevantObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		DataMaskTestUtil.processBatchEngineUnits();
	}

	@Test
	public void testOnBeforeCreate() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", TestPropsValues.getCompanyId());

		AssertUtils.assertFailure(
			ModelListenerException.class,
			"Invalid \"detectionRegex\": Unclosed character class near index " +
				"0\n[\n^",
			() -> _addCustomDataMaskObjectEntry(objectDefinition, "[", null));
		AssertUtils.assertFailure(
			ModelListenerException.class,
			"Invalid \"replacementRegex\": Unclosed character class near " +
				"index 0\n[\n^",
			() -> _addCustomDataMaskObjectEntry(objectDefinition, "\\w+", "["));
		AssertUtils.assertFailure(
			ModelListenerException.class, "Unable to create system data masks",
			() -> _addSystemDataMaskObjectEntry(objectDefinition));

		BatchEngineUnitThreadLocal.setFileName(_DATA_MASK_BATCH_FILE_NAME);

		try {
			ObjectEntry objectEntry = _addSystemDataMaskObjectEntry(
				objectDefinition);

			Assert.assertNotNull(
				_objectEntryLocalService.fetchObjectEntry(
					objectEntry.getObjectEntryId()));
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry objectEntry1 = DataMaskTestUtil.addDataMaskObjectEntry(
			RandomTestUtil.randomString(), "\\d{4}",
			RandomTestUtil.randomString());

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry1.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry1.getObjectEntryId()));

		ObjectEntry objectEntry2 = _getDataMaskObjectEntry("Email Address");

		AssertUtils.assertFailure(
			ModelListenerException.class, "Unable to delete system data masks",
			() -> _objectEntryLocalService.deleteObjectEntry(
				objectEntry2.getObjectEntryId()));
		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry2.getObjectEntryId()));
	}

	@Test
	public void testOnBeforeRemoveWhenCompanyInDeletionProcess()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		long companyId = company.getCompanyId();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			DataMaskTestUtil.processBatchEngineUnits();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", companyId);

		int objectEntriesCount = _objectEntryLocalService.getObjectEntriesCount(
			0, objectDefinition.getObjectDefinitionId());

		Assert.assertTrue(objectEntriesCount > 0);

		_companyLocalService.deleteCompany(company);

		Assert.assertNull(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", companyId));
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		ObjectEntry objectEntry1 = DataMaskTestUtil.addDataMaskObjectEntry(
			RandomTestUtil.randomString(), "\\d{4}",
			RandomTestUtil.randomString());

		String replacementValue = RandomTestUtil.randomString();

		objectEntry1 = _updateDataMaskObjectEntry(
			objectEntry1, replacementValue);

		Map<String, Serializable> values = objectEntry1.getValues();

		Assert.assertEquals(replacementValue, values.get("replacementValue"));

		ObjectEntry finalObjectEntry1 = objectEntry1;

		AssertUtils.assertFailure(
			ModelListenerException.class,
			"Invalid \"detectionRegex\": Unclosed character class near index " +
				"0\n[\n^",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(),
				finalObjectEntry1.getObjectEntryId(), 0,
				HashMapBuilder.putAll(
					finalObjectEntry1.getValues()
				).put(
					"detectionRegex", "["
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ModelListenerException.class, "Unable to update system data masks",
			() -> _updateDataMaskObjectEntry(
				_getDataMaskObjectEntry("Email Address"),
				RandomTestUtil.randomString()));

		BatchEngineUnitThreadLocal.setFileName(RandomTestUtil.randomString());

		try {
			AssertUtils.assertFailure(
				ModelListenerException.class,
				"Unable to update system data masks",
				() -> _updateDataMaskObjectEntry(
					_getDataMaskObjectEntry("Email Address"),
					RandomTestUtil.randomString()));
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}

		ObjectEntry emailAddressDataMaskObjectEntry = _getDataMaskObjectEntry(
			"Email Address");

		values = emailAddressDataMaskObjectEntry.getValues();

		Assert.assertEquals("[EMAIL_ADDRESS]", values.get("replacementValue"));

		BatchEngineUnitThreadLocal.setFileName(_DATA_MASK_BATCH_FILE_NAME);

		try {
			replacementValue = RandomTestUtil.randomString();

			emailAddressDataMaskObjectEntry = _updateDataMaskObjectEntry(
				emailAddressDataMaskObjectEntry, replacementValue);

			values = emailAddressDataMaskObjectEntry.getValues();

			Assert.assertEquals(
				replacementValue, values.get("replacementValue"));
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}

		ObjectEntry objectEntry2 = DataMaskTestUtil.addDataMaskObjectEntry(
			RandomTestUtil.randomString(), "\\d{4}",
			RandomTestUtil.randomString());

		ObjectEntry finalObjectEntry2 = objectEntry2;

		AssertUtils.assertFailure(
			ModelListenerException.class,
			"Unable to convert data mask to system data mask",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(),
				finalObjectEntry2.getObjectEntryId(), 0,
				HashMapBuilder.<String, Serializable>putAll(
					finalObjectEntry2.getValues()
				).put(
					"maskType", "system"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		objectEntry2 = _objectEntryLocalService.getObjectEntry(
			objectEntry2.getObjectEntryId());

		values = objectEntry2.getValues();

		Assert.assertEquals("custom", values.get("maskType"));
	}

	private ObjectEntry _addCustomDataMaskObjectEntry(
			ObjectDefinition objectDefinition, String detectionRegex,
			String replacementRegex)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"detectionRegex", detectionRegex
			).put(
				"maskType", "custom"
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"replacementRegex", replacementRegex
			).put(
				"replacementValue", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addSystemDataMaskObjectEntry(
			ObjectDefinition objectDefinition)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"detectionRegex", "\\d{4}"
			).put(
				"maskType", "system"
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"replacementValue", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _getDataMaskObjectEntry(String name) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", TestPropsValues.getCompanyId());

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (name.equals(values.get("name"))) {
				return objectEntry;
			}
		}

		return null;
	}

	private ObjectEntry _updateDataMaskObjectEntry(
			ObjectEntry objectEntry, String replacementValue)
		throws Exception {

		return _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.<String, Serializable>putAll(
				objectEntry.getValues()
			).put(
				"replacementValue", replacementValue
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private static final String _DATA_MASK_BATCH_FILE_NAME =
		"com.liferay.headless.data.mask.impl_1.0.0 [1]";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}