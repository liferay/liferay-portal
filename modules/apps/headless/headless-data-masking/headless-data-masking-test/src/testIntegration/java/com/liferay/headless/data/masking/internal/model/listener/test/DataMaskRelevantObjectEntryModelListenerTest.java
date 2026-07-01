/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.headless.data.masking.test.util.DataMaskTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
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

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class DataMaskRelevantObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		DataMaskTestUtil.processBatchEngineUnits();
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-63311"), @FeatureFlag("LPD-90204")}
	)
	@Test
	public void testOnBeforeCreate() throws Exception {
		ObjectDefinition objectDefinition = _fetchDataMaskObjectDefinition();

		try {
			_addSystemMask(objectDefinition);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertThat(
				exception.getMessage(),
				CoreMatchers.containsString(_SYSTEM_DATA_MASKS_MESSAGE));
		}

		BatchEngineUnitThreadLocal.setFileName(_DATA_MASKING_BATCH_FILE_NAME);

		ObjectEntry systemMaskObjectEntry = null;

		try {
			systemMaskObjectEntry = _addSystemMask(objectDefinition);

			Assert.assertNotNull(
				_objectEntryLocalService.fetchObjectEntry(
					systemMaskObjectEntry.getObjectEntryId()));
		}
		finally {
			if (systemMaskObjectEntry != null) {
				_objectEntryLocalService.deleteObjectEntry(
					systemMaskObjectEntry.getObjectEntryId());
			}

			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-63311"), @FeatureFlag("LPD-90204")}
	)
	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry customMaskObjectEntry = DataMaskTestUtil.addDataMask(
			RandomTestUtil.randomString(), "\\d{4}",
			RandomTestUtil.randomString());

		_objectEntryLocalService.deleteObjectEntry(
			customMaskObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				customMaskObjectEntry.getObjectEntryId()));

		ObjectEntry emailMaskObjectEntry = _findSystemMask("Email Address");

		try {
			_objectEntryLocalService.deleteObjectEntry(
				emailMaskObjectEntry.getObjectEntryId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertThat(
				exception.getMessage(),
				CoreMatchers.containsString(_SYSTEM_DATA_MASKS_MESSAGE));
		}

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				emailMaskObjectEntry.getObjectEntryId()));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-63311"), @FeatureFlag("LPD-90204")}
	)
	@Test
	public void testOnBeforeUpdate() throws Exception {
		ObjectEntry customMaskObjectEntry = DataMaskTestUtil.addDataMask(
			RandomTestUtil.randomString(), "\\d{4}",
			RandomTestUtil.randomString());

		String replacementValue = RandomTestUtil.randomString();

		ObjectEntry updatedCustomMaskObjectEntry = _updateReplacementValue(
			customMaskObjectEntry, replacementValue);

		Map<String, Serializable> values =
			updatedCustomMaskObjectEntry.getValues();

		Assert.assertEquals(replacementValue, values.get("replacementValue"));

		ObjectEntry emailMaskObjectEntry = _findSystemMask("Email Address");

		try {
			_updateReplacementValue(
				emailMaskObjectEntry, RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertThat(
				exception.getMessage(),
				CoreMatchers.containsString(_SYSTEM_DATA_MASKS_MESSAGE));
		}

		BatchEngineUnitThreadLocal.setFileName(
			"com.liferay.example.impl_1.0.0 [2]");

		try {
			_updateReplacementValue(
				emailMaskObjectEntry, RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertThat(
				exception.getMessage(),
				CoreMatchers.containsString(_SYSTEM_DATA_MASKS_MESSAGE));
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}

		Assert.assertEquals(
			"[EMAIL_ADDRESS]",
			_getReplacementValue(emailMaskObjectEntry.getObjectEntryId()));

		BatchEngineUnitThreadLocal.setFileName(_DATA_MASKING_BATCH_FILE_NAME);

		try {
			String emailReplacementValue = RandomTestUtil.randomString();

			_updateReplacementValue(
				emailMaskObjectEntry, emailReplacementValue);

			Assert.assertEquals(
				emailReplacementValue,
				_getReplacementValue(emailMaskObjectEntry.getObjectEntryId()));

			_updateReplacementValue(
				_objectEntryLocalService.getObjectEntry(
					emailMaskObjectEntry.getObjectEntryId()),
				"[EMAIL_ADDRESS]");
		}
		finally {
			BatchEngineUnitThreadLocal.setFileName(StringPool.BLANK);
		}
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-63311"), @FeatureFlag("LPD-90204")}
	)
	@Test
	public void testOnBeforeUpdateCustomMaskToSystem() throws Exception {
		ObjectEntry customMaskObjectEntry = DataMaskTestUtil.addDataMask(
			RandomTestUtil.randomString(), "\\d{4}",
			RandomTestUtil.randomString());

		try {
			_updateMaskType(customMaskObjectEntry, "system");

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertThat(
				exception.getMessage(),
				CoreMatchers.containsString(_SYSTEM_DATA_MASKS_MESSAGE));
		}

		ObjectEntry reloadedCustomMaskObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				customMaskObjectEntry.getObjectEntryId());

		Map<String, Serializable> values =
			reloadedCustomMaskObjectEntry.getValues();

		Assert.assertEquals("custom", values.get("maskType"));
	}

	private ObjectEntry _addSystemMask(ObjectDefinition objectDefinition)
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

	private ObjectDefinition _fetchDataMaskObjectDefinition() throws Exception {
		return _objectDefinitionLocalService.
			fetchObjectDefinitionByExternalReferenceCode(
				"L_DATA_MASK", TestPropsValues.getCompanyId());
	}

	private ObjectEntry _findSystemMask(String name) throws Exception {
		ObjectDefinition objectDefinition = _fetchDataMaskObjectDefinition();

		if (objectDefinition == null) {
			return null;
		}

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

	private Serializable _getReplacementValue(long objectEntryId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		Map<String, Serializable> values = objectEntry.getValues();

		return values.get("replacementValue");
	}

	private ObjectEntry _updateMaskType(
			ObjectEntry objectEntry, String maskType)
		throws Exception {

		return _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.<String, Serializable>putAll(
				objectEntry.getValues()
			).put(
				"maskType", maskType
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _updateReplacementValue(
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

	private static final String _DATA_MASKING_BATCH_FILE_NAME =
		"com.liferay.headless.data.masking.impl_1.0.0 [1]";

	private static final String _SYSTEM_DATA_MASKS_MESSAGE =
		"system data masks";

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}