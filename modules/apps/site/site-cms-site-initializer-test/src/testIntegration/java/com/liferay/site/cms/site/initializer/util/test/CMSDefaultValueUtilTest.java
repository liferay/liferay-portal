/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.definition.setting.util.ObjectDefinitionSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSDefaultValueTestUtil;
import com.liferay.site.cms.site.initializer.util.CMSDefaultValueUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-112761")
@RunWith(Arquillian.class)
public class CMSDefaultValueUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMSDefaultValueTestUtil.processBatchEngineUnits(
			CMSDefaultValueUtilTest.class);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_group = _depotEntry.getGroup();

		ObjectEntryFolder rootObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_group.getGroupId(), _group.getCompanyId());

		_parentObjectEntryFolder = _addObjectEntryFolder(
			_group, rootObjectEntryFolder.getObjectEntryFolderId());

		_objectEntryFolder = _addObjectEntryFolder(
			_group, _parentObjectEntryFolder.getObjectEntryFolderId());
	}

	@Test
	public void testAddOrUpdateObjectEntry() throws Exception {
		ObjectEntry objectEntry1 = CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.put(_createJSONObject("title", "Text", "Title 1")),
			_filterFactory, _objectEntryFolder);

		Assert.assertEquals(_group.getGroupId(), objectEntry1.getGroupId());

		Map<String, Serializable> values = objectEntry1.getValues();

		Assert.assertEquals(
			_objectEntryFolder.getExternalReferenceCode(),
			MapUtil.getString(values, "classExternalReferenceCode"));
		Assert.assertEquals(
			ObjectEntryFolder.class.getName(),
			MapUtil.getString(values, "className"));
		Assert.assertEquals(
			_objectEntryFolder.getTreePath(),
			MapUtil.getString(values, "treePath"));

		_assertDefaultValues("title", "Title 1", values);

		ObjectEntry objectEntry2 = CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.put(_createJSONObject("title", "Text", "Title 2")),
			_filterFactory, _objectEntryFolder);

		Assert.assertEquals(
			objectEntry1.getObjectEntryId(), objectEntry2.getObjectEntryId());

		_assertDefaultValues("title", "Title 2", objectEntry2.getValues());

		ObjectEntry objectEntry3 = CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.put(_createJSONObject("title", "Text", "Title 3")),
			_depotEntry, _filterFactory);

		Assert.assertNotEquals(
			objectEntry1.getObjectEntryId(), objectEntry3.getObjectEntryId());
		Assert.assertEquals(_group.getGroupId(), objectEntry3.getGroupId());

		values = objectEntry3.getValues();

		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			MapUtil.getString(values, "classExternalReferenceCode"));
		Assert.assertEquals(
			DepotEntry.class.getName(), MapUtil.getString(values, "className"));

		_assertDefaultValues("title", "Title 3", values);
	}

	@Test
	public void testFetchObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			CMSDefaultValueUtil.fetchObjectDefinition(
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			"space",
			ObjectDefinitionSettingUtil.getValue(
				ObjectDefinitionSettingConstants.NAME_DOMAIN,
				objectDefinition.getObjectDefinitionSettings()));
		Assert.assertEquals(
			"L_CMS_DEFAULT_VALUE", objectDefinition.getExternalReferenceCode());
		Assert.assertEquals(
			ObjectDefinitionConstants.SCOPE_DEPOT, objectDefinition.getScope());
	}

	@Test
	public void testFetchObjectEntry() throws Exception {
		Assert.assertNull(
			CMSDefaultValueUtil.fetchObjectEntry(
				_group.getGroupId(),
				_objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolder.class.getName(), _filterFactory));

		ObjectEntry objectEntry = CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.put(
				_createJSONObject(
					"title", "Text", RandomTestUtil.randomString())),
			_filterFactory, _objectEntryFolder);

		Assert.assertEquals(
			objectEntry,
			CMSDefaultValueUtil.fetchObjectEntry(
				_group.getGroupId(),
				_objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolder.class.getName(), _filterFactory));

		Assert.assertNull(
			CMSDefaultValueUtil.fetchObjectEntry(
				_group.getGroupId(),
				_objectEntryFolder.getExternalReferenceCode(),
				DepotEntry.class.getName(), _filterFactory));
		Assert.assertNull(
			CMSDefaultValueUtil.fetchObjectEntry(
				_group.getGroupId(),
				_parentObjectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolder.class.getName(), _filterFactory));

		String externalReferenceCode = StringBundler.concat(
			RandomTestUtil.randomString(), StringPool.APOSTROPHE,
			RandomTestUtil.randomString());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				externalReferenceCode, _group.getGroupId(),
				_group.getCreatorUserId(),
				_parentObjectEntryFolder.getObjectEntryFolderId(),
				StringPool.BLANK,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		objectEntry = CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.put(
				_createJSONObject(
					"title", "Text", RandomTestUtil.randomString())),
			_filterFactory, objectEntryFolder);

		Assert.assertEquals(
			objectEntry,
			CMSDefaultValueUtil.fetchObjectEntry(
				_group.getGroupId(), externalReferenceCode,
				ObjectEntryFolder.class.getName(), _filterFactory));
	}

	@Test
	public void testGetDefaultValuesJSONArray() throws Exception {
		JSONArray jsonArray = CMSDefaultValueUtil.getDefaultValuesJSONArray(
			_group.getGroupId(), _objectEntryFolder.getExternalReferenceCode(),
			ObjectEntryFolder.class.getName(), _filterFactory);

		Assert.assertEquals(0, jsonArray.length());

		CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.putAll(
				_createJSONObject("description", "LongText", null),
				_createJSONObject("title", "Text", "Title")),
			_filterFactory, _objectEntryFolder);

		jsonArray = CMSDefaultValueUtil.getDefaultValuesJSONArray(
			_group.getGroupId(), _objectEntryFolder.getExternalReferenceCode(),
			ObjectEntryFolder.class.getName(), _filterFactory);

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals("description", jsonObject.getString("name"));
		Assert.assertTrue(jsonObject.isNull("value"));

		jsonObject = jsonArray.getJSONObject(1);

		Assert.assertEquals("title", jsonObject.getString("name"));
		Assert.assertEquals("Title", jsonObject.getString("value"));

		Assert.assertEquals(2, jsonArray.length());
	}

	@Test
	public void testGetInheritedDefaultValues() throws Exception {
		Assert.assertEquals(
			Collections.emptyMap(),
			CMSDefaultValueUtil.getInheritedDefaultValues(
				0, _filterFactory,
				_objectEntryFolder.getObjectEntryFolderId()));

		Map<String, JSONObject> defaultValues =
			CMSDefaultValueUtil.getInheritedDefaultValues(
				_group.getGroupId(), _filterFactory,
				_objectEntryFolder.getObjectEntryFolderId());

		Assert.assertEquals(defaultValues.toString(), 0, defaultValues.size());

		CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.putAll(
				_createJSONObject(
					"category", "Picklist", "space"
				).put(
					"listTypeDefinitionERC", "list1"
				),
				_createJSONObject("count", "Integer", 1),
				_createJSONObject(
					"description", "LongText",
					JSONUtil.put(
						"en_US", "Space description"
					).put(
						"it_IT", "Descrizione dello spazio"
					)),
				_createJSONObject("flag", "Boolean", true),
				_createJSONObject("title", "Text", "Space title")),
			_depotEntry, _filterFactory);

		defaultValues = CMSDefaultValueUtil.getInheritedDefaultValues(
			_group.getGroupId(), _filterFactory,
			_objectEntryFolder.getObjectEntryFolderId());

		Assert.assertEquals(defaultValues.toString(), 5, defaultValues.size());

		_assertInheritedDefaultValue(
			DepotEntry.class.getName(), defaultValues,
			_group.getExternalReferenceCode(), "title", "Text", "Space title");

		CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.putAll(
				_createJSONObject(
					"category", "Picklist", "parent"
				).put(
					"listTypeDefinitionERC", "list2"
				),
				_createJSONObject("title", "Text", "Parent title")),
			_filterFactory, _parentObjectEntryFolder);

		CMSDefaultValueUtil.addOrUpdateObjectEntry(
			TestPropsValues.getUserId(),
			JSONUtil.putAll(
				_createJSONObject("count", "Integer", 2),
				_createJSONObject(
					"description", "LongText",
					JSONUtil.put(
						"en_US", "Folder description"
					).put(
						"it_IT", StringPool.BLANK
					)),
				_createJSONObject("flag", "Boolean", null),
				_createJSONObject("title", "Text", StringPool.BLANK)),
			_filterFactory, _objectEntryFolder);

		defaultValues = CMSDefaultValueUtil.getInheritedDefaultValues(
			_group.getGroupId(), _filterFactory,
			_objectEntryFolder.getObjectEntryFolderId());

		JSONObject jsonObject = defaultValues.get(
			CMSDefaultValueUtil.getObjectFieldKey(
				"list1", "category", "Picklist"));

		Assert.assertEquals("space", jsonObject.getString("value"));

		jsonObject = defaultValues.get(
			CMSDefaultValueUtil.getObjectFieldKey(
				"list2", "category", "Picklist"));

		Assert.assertEquals("parent", jsonObject.getString("value"));

		_assertInheritedDefaultValue(
			ObjectEntryFolder.class.getName(), defaultValues,
			_objectEntryFolder.getExternalReferenceCode(), "count", "Integer",
			2);

		jsonObject = defaultValues.get(
			CMSDefaultValueUtil.getObjectFieldKey(
				null, "description", "LongText"));

		JSONObject valueJSONObject = jsonObject.getJSONObject("value");

		Assert.assertEquals(
			"Folder description", valueJSONObject.getString("en_US"));
		Assert.assertFalse(valueJSONObject.has("it_IT"));

		Assert.assertEquals(defaultValues.toString(), 6, defaultValues.size());

		_assertInheritedDefaultValue(
			DepotEntry.class.getName(), defaultValues,
			_group.getExternalReferenceCode(), "flag", "Boolean", true);
		_assertInheritedDefaultValue(
			ObjectEntryFolder.class.getName(), defaultValues,
			_parentObjectEntryFolder.getExternalReferenceCode(), "title",
			"Text", "Parent title");

		defaultValues = CMSDefaultValueUtil.getInheritedDefaultValues(
			_group.getGroupId(), _filterFactory,
			_parentObjectEntryFolder.getObjectEntryFolderId());

		_assertInheritedDefaultValue(
			DepotEntry.class.getName(), defaultValues,
			_group.getExternalReferenceCode(), "count", "Integer", 1);

		defaultValues = CMSDefaultValueUtil.getInheritedDefaultValues(
			_group.getGroupId(), _filterFactory,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		Assert.assertEquals(defaultValues.toString(), 5, defaultValues.size());

		_assertInheritedDefaultValue(
			DepotEntry.class.getName(), defaultValues,
			_group.getExternalReferenceCode(), "title", "Text", "Space title");
	}

	@Test
	public void testGetObjectFieldKey() {
		Assert.assertEquals(
			"#title#Text",
			CMSDefaultValueUtil.getObjectFieldKey(null, "title", "Text"));
		Assert.assertEquals(
			"list#category#Picklist",
			CMSDefaultValueUtil.getObjectFieldKey(
				"list", "category", "Picklist"));
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			Group group, long parentObjectEntryFolderId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			RandomTestUtil.randomString(), group.getGroupId(),
			group.getCreatorUserId(), parentObjectEntryFolderId,
			StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertDefaultValues(
			String name, String value, Map<String, Serializable> values)
		throws Exception {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			MapUtil.getString(values, "defaultValues"));

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(name, jsonObject.getString("name"));
		Assert.assertEquals(value, jsonObject.getString("value"));
	}

	private void _assertInheritedDefaultValue(
		String className, Map<String, JSONObject> defaultValues,
		String externalReferenceCode, String name, String type, Object value) {

		JSONObject jsonObject = defaultValues.get(
			CMSDefaultValueUtil.getObjectFieldKey(null, name, type));

		Assert.assertEquals(value, jsonObject.get("value"));

		JSONObject sourceJSONObject = jsonObject.getJSONObject("source");

		Assert.assertEquals(className, sourceJSONObject.getString("className"));
		Assert.assertEquals(
			externalReferenceCode,
			sourceJSONObject.getString("externalReferenceCode"));
	}

	private JSONObject _createJSONObject(
		String name, String type, Object value) {

		return JSONUtil.put(
			"name", name
		).put(
			"type", type
		).put(
			"value", value
		);
	}

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	private Group _group;
	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	private ObjectEntryFolder _parentObjectEntryFolder;

}