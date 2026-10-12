/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class LiferayCommercePIMConnectorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_friendlyURLNormalizer.normalize(Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> StringUtil.toLowerCase(
				invocationOnMock.getArgument(0, String.class))
		);

		Mockito.when(
			_language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);

		ReflectionTestUtil.setFieldValue(
			_liferayCommercePIMConnector, "_friendlyURLNormalizer",
			_friendlyURLNormalizer);
		ReflectionTestUtil.setFieldValue(
			_liferayCommercePIMConnector, "language", _language);
		ReflectionTestUtil.setFieldValue(
			_liferayCommercePIMConnector, "objectDefinitionLocalService",
			_objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_liferayCommercePIMConnector, "objectEntryLocalService",
			_objectEntryLocalService);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			_CLASS_NAME
		);

		Mockito.when(
			_objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinition(
				_OBJECT_DEFINITION_ID)
		).thenReturn(
			_objectDefinition
		);
	}

	@After
	public void tearDown() {
		_listTypeEntryLocalServiceUtilMockedStatic.close();
		_objectFieldLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testExecute() throws Exception {
		_testExecute();
		_testExecuteWithBooleanProductType();
		_testExecuteWithForeignStructureFixedValue();
		_testExecuteWithInvalidStructureFieldMapping();
		_testExecuteWithJoinedValues();
		_testExecuteWithPicklistProductSpecification();
		_testExecuteWithProductOptions();
		_testExecuteWithProductSpecifications();
		_testExecuteWithVariantPIMLinks();
		_testExecuteWithoutUnitOfMeasure();
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(
			"liferay-commerce", _liferayCommercePIMConnector.getKey());
	}

	@Test
	public void testGetName() {
		Assert.assertEquals(
			"liferay-commerce",
			_liferayCommercePIMConnector.getName(LocaleUtil.US));
	}

	@Test
	public void testGetPIMConnectorChannelFields() {
		List<PIMConnectorChannelField> pimConnectorChannelFields =
			_liferayCommercePIMConnector.getPIMConnectorChannelFields();

		Assert.assertEquals(
			pimConnectorChannelFields.toString(), 15,
			pimConnectorChannelFields.size());
		Assert.assertEquals(
			Arrays.asList(
				"catalog-id", "depth", "description", "height", "name",
				"precision", "product-options", "product-specifications",
				"product-type", "sku", "tags", "unit-of-measure-key",
				"unit-of-measure-name", "weight", "width"),
			TransformUtil.transform(
				pimConnectorChannelFields,
				pimConnectorChannelField -> pimConnectorChannelField.getLabel(
					LocaleUtil.US)));
		Assert.assertEquals(
			Arrays.asList(
				"catalogId", "skus[].depth", "description", "skus[].height",
				"name", "skus[].skuUnitOfMeasures[].precision",
				"productOptions", "productSpecifications", "productType",
				"skus[].sku", "tags", "skus[].skuUnitOfMeasures[].key",
				"skus[].skuUnitOfMeasures[].name", "skus[].weight",
				"skus[].width"),
			TransformUtil.transform(
				pimConnectorChannelFields, PIMConnectorChannelField::getName));
		Assert.assertEquals(
			Arrays.asList("catalogId", "name", "productType", "skus[].sku"),
			TransformUtil.transform(
				pimConnectorChannelFields,
				pimConnectorChannelField -> {
					if (!pimConnectorChannelField.isRequired()) {
						return null;
					}

					return pimConnectorChannelField.getName();
				}));
		Assert.assertEquals(
			Arrays.asList(
				"LongInteger", "Decimal", "Text", "Decimal", "Text", "Integer",
				"Text", "Text", "Text", "Text", "Text", "Text", "Text",
				"Decimal", "Decimal"),
			TransformUtil.transform(
				pimConnectorChannelFields, PIMConnectorChannelField::getType));
		Assert.assertEquals(
			Arrays.asList("productOptions", "productSpecifications", "tags"),
			TransformUtil.transform(
				pimConnectorChannelFields,
				pimConnectorChannelField -> {
					if (!pimConnectorChannelField.isMultiple()) {
						return null;
					}

					return pimConnectorChannelField.getName();
				}));
	}

	private JSONObject _execute(
			String externalReferenceCode,
			List<ObjectEntry> pimFieldMappingObjectEntries,
			ObjectEntry... pimProductObjectEntries)
		throws Exception {

		Map<String, List<ObjectEntry>> objectEntriesMap = new HashMap<>();

		for (ObjectEntry objectEntry : pimFieldMappingObjectEntries) {
			List<ObjectEntry> objectEntries = objectEntriesMap.computeIfAbsent(
				MapUtil.getString(objectEntry.getValues(), "channelFieldName"),
				key -> new ArrayList<>());

			objectEntries.add(objectEntry);
		}

		Mockito.doReturn(
			objectEntriesMap
		).when(
			_liferayCommercePIMConnector
		).getPIMFieldMappingObjectEntriesMap(
			_pimConnectorObjectEntry
		);

		Mockito.doReturn(
			HashMapBuilder.put(
				externalReferenceCode, Arrays.asList(pimProductObjectEntries)
			).build()
		).when(
			_liferayCommercePIMConnector
		).getPIMProductObjectEntriesMap(
			Mockito.anyLong()
		);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			_liferayCommercePIMConnector.execute(_pimConnectorObjectEntry));

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		return jsonArray.getJSONObject(0);
	}

	private List<ObjectEntry> _getRequiredObjectEntries(
		ObjectEntry... objectEntries) {

		List<ObjectEntry> requiredObjectEntries = ListUtil.fromArray(
			_mockFieldMapping(
				"catalogId", StringPool.BLANK, StringPool.BLANK,
				PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "1"),
			_mockFieldMapping(
				"name", StringPool.BLANK, "title", "dynamicValue",
				StringPool.BLANK),
			_mockFieldMapping(
				"productType", StringPool.BLANK, StringPool.BLANK,
				PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "simple"),
			_mockFieldMapping(
				"skus[].sku", StringPool.BLANK, "sku", "dynamicValue",
				StringPool.BLANK));

		Collections.addAll(requiredObjectEntries, objectEntries);

		return requiredObjectEntries;
	}

	private JSONObject _getSkuJSONObject(int index, JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.getJSONArray("skus");

		Assert.assertTrue(jsonArray.toString(), jsonArray.length() > index);

		return jsonArray.getJSONObject(index);
	}

	private ObjectEntry _mockFieldMapping(
		String channelFieldName, String sourceClassName, String sourceFieldName,
		String type, String value) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"channelFieldName", channelFieldName
			).put(
				"sourceClassName", sourceClassName
			).put(
				"sourceFieldName", sourceFieldName
			).put(
				"type", type
			).put(
				"value", value
			).build()
		);

		return objectEntry;
	}

	private void _mockObjectField(String label, String name) {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getLabel(LocaleUtil.US)
		).thenReturn(
			label
		);

		_objectFieldLocalServiceUtilMockedStatic.when(
			() -> ObjectFieldLocalServiceUtil.fetchObjectField(
				_OBJECT_DEFINITION_ID, name)
		).thenReturn(
			objectField
		);
	}

	private ObjectEntry _mockProductObjectEntry(
			String sku, Map<String, Serializable> values)
		throws Exception {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getExternalReferenceCode()
		).thenReturn(
			sku
		);

		Mockito.when(
			objectEntry.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			_objectEntryLocalService.getValues(objectEntry)
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"sku", sku
			).put(
				"title", sku + " title"
			).put(
				"unitOfMeasureKey", "box"
			).putAll(
				values
			).build()
		);

		return objectEntry;
	}

	private void _testExecute() throws Exception {
		JSONObject jsonObject = _execute(
			"SKU-1", _getRequiredObjectEntries(),
			_mockProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		Assert.assertTrue(jsonObject.getBoolean("active"));
		Assert.assertEquals(1, jsonObject.getLong("catalogId"));
		Assert.assertEquals(
			"SKU-1", jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals("simple", jsonObject.getString("productType"));
		Assert.assertFalse(
			jsonObject.toString(), jsonObject.has("description"));
		Assert.assertFalse(
			jsonObject.toString(), jsonObject.has("productOptions"));
		Assert.assertFalse(
			jsonObject.toString(), jsonObject.has("productSpecifications"));
		Assert.assertFalse(jsonObject.toString(), jsonObject.has("tags"));

		JSONObject nameJSONObject = jsonObject.getJSONObject("name");

		Assert.assertEquals("SKU-1 title", nameJSONObject.getString("en_US"));

		JSONObject skuJSONObject = _getSkuJSONObject(0, jsonObject);

		Assert.assertEquals(
			"SKU-1", skuJSONObject.getString("externalReferenceCode"));
		Assert.assertTrue(skuJSONObject.getBoolean("published"));
		Assert.assertTrue(skuJSONObject.getBoolean("purchasable"));
		Assert.assertEquals("SKU-1", skuJSONObject.getString("sku"));
		Assert.assertFalse(
			skuJSONObject.toString(), skuJSONObject.has("skuOptions"));
	}

	private void _testExecuteWithBooleanProductType() throws Exception {
		List<ObjectEntry> objectEntries = ListUtil.fromArray(
			_mockFieldMapping(
				"catalogId", StringPool.BLANK, StringPool.BLANK,
				PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "1"),
			_mockFieldMapping(
				"name", StringPool.BLANK, "title", "dynamicValue",
				StringPool.BLANK),
			_mockFieldMapping(
				"productType", StringPool.BLANK, "virtual", "dynamicValue",
				StringPool.BLANK),
			_mockFieldMapping(
				"skus[].sku", StringPool.BLANK, "sku", "dynamicValue",
				StringPool.BLANK));

		JSONObject jsonObject = _execute(
			"SKU-1", objectEntries,
			_mockProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"virtual", false
				).build()));

		Assert.assertEquals("simple", jsonObject.getString("productType"));

		jsonObject = _execute(
			"SKU-2", objectEntries,
			_mockProductObjectEntry(
				"SKU-2",
				HashMapBuilder.<String, Serializable>put(
					"virtual", true
				).build()));

		Assert.assertEquals("virtual", jsonObject.getString("productType"));
	}

	private void _testExecuteWithForeignStructureFixedValue() throws Exception {
		JSONObject jsonObject = _execute(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockFieldMapping(
					"description", RandomTestUtil.randomString(),
					StringPool.BLANK,
					PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "A shirt")),
			_mockProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		Assert.assertFalse(
			jsonObject.toString(), jsonObject.has("description"));
	}

	private void _testExecuteWithInvalidStructureFieldMapping()
		throws Exception {

		JSONObject jsonObject = _execute(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockFieldMapping(
					"description", RandomTestUtil.randomString(), "description",
					"dynamicValue", StringPool.BLANK)),
			_mockProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"description", "A red shirt"
				).build()));

		Assert.assertFalse(
			jsonObject.toString(), jsonObject.has("description"));
	}

	private void _testExecuteWithJoinedValues() throws Exception {
		JSONObject jsonObject = _execute(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockFieldMapping(
					"name", _CLASS_NAME, "sku", "dynamicValue",
					StringPool.BLANK)),
			_mockProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		JSONObject nameJSONObject = jsonObject.getJSONObject("name");

		Assert.assertEquals("SKU-1", nameJSONObject.getString("en_US"));
	}

	private void _testExecuteWithPicklistProductSpecification()
		throws Exception {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)
		).thenReturn(
			true
		);

		Mockito.when(
			objectField.getLabel(LocaleUtil.US)
		).thenReturn(
			"Size"
		);

		Mockito.when(
			objectField.getListTypeDefinitionId()
		).thenReturn(
			_LIST_TYPE_DEFINITION_ID
		);

		_objectFieldLocalServiceUtilMockedStatic.when(
			() -> ObjectFieldLocalServiceUtil.fetchObjectField(
				_OBJECT_DEFINITION_ID, "size")
		).thenReturn(
			objectField
		);

		ListTypeEntry listTypeEntry = Mockito.mock(ListTypeEntry.class);

		Mockito.when(
			listTypeEntry.getName(LocaleUtil.US)
		).thenReturn(
			"Small"
		);

		_listTypeEntryLocalServiceUtilMockedStatic.when(
			() -> ListTypeEntryLocalServiceUtil.fetchListTypeEntry(
				_LIST_TYPE_DEFINITION_ID, "small")
		).thenReturn(
			listTypeEntry
		);

		JSONObject jsonObject = _execute(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockFieldMapping(
					"productSpecifications", StringPool.BLANK, "size",
					"dynamicValue", StringPool.BLANK)),
			_mockProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"size", "small"
				).build()));

		JSONArray jsonArray = jsonObject.getJSONArray("productSpecifications");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		JSONObject valueJSONObject = jsonObject.getJSONObject("value");

		Assert.assertEquals("Small", valueJSONObject.getString("en_US"));
	}

	private void _testExecuteWithProductOptions() throws Exception {
		_mockObjectField("Color", "color");

		JSONObject jsonObject = _execute(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockFieldMapping(
					"productOptions", StringPool.BLANK, "color", "dynamicValue",
					StringPool.BLANK)),
			_mockProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"color", "Red"
				).build()),
			_mockProductObjectEntry(
				"SKU-2",
				HashMapBuilder.<String, Serializable>put(
					"color", "Blue"
				).build()));

		JSONArray jsonArray = jsonObject.getJSONArray("productOptions");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject productOptionJSONObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			"select", productOptionJSONObject.getString("fieldType"));
		Assert.assertEquals("color", productOptionJSONObject.getString("key"));
		Assert.assertEquals(
			"color",
			productOptionJSONObject.getString("optionExternalReferenceCode"));
		Assert.assertEquals(0, productOptionJSONObject.getLong("optionId"));
		Assert.assertTrue(productOptionJSONObject.getBoolean("skuContributor"));

		JSONObject nameJSONObject = productOptionJSONObject.getJSONObject(
			"name");

		Assert.assertEquals("Color", nameJSONObject.getString("en_US"));

		jsonArray = productOptionJSONObject.getJSONArray("productOptionValues");

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		JSONObject productOptionValueJSONObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			"red", productOptionValueJSONObject.getString("key"));

		productOptionValueJSONObject = jsonArray.getJSONObject(1);

		Assert.assertEquals(
			"blue", productOptionValueJSONObject.getString("key"));

		jsonArray = _getSkuJSONObject(
			0, jsonObject
		).getJSONArray(
			"skuOptions"
		);

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject skuOptionJSONObject = jsonArray.getJSONObject(0);

		Assert.assertEquals("color", skuOptionJSONObject.getString("key"));
		Assert.assertEquals("red", skuOptionJSONObject.getString("value"));
	}

	private void _testExecuteWithProductSpecifications() throws Exception {
		_mockObjectField("Color", "color");

		JSONObject jsonObject = _execute(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockFieldMapping(
					"productSpecifications", StringPool.BLANK, "color",
					"dynamicValue", StringPool.BLANK),
				_mockFieldMapping(
					"productSpecifications", StringPool.BLANK, "color",
					"dynamicValue", StringPool.BLANK),
				_mockFieldMapping(
					"productSpecifications", StringPool.BLANK, StringPool.BLANK,
					PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "Red")),
			_mockProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"color", "Red"
				).build()));

		JSONArray jsonArray = jsonObject.getJSONArray("productSpecifications");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject productSpecificationJSONObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			0, productSpecificationJSONObject.getInt("priority"));
		Assert.assertEquals(
			"color",
			productSpecificationJSONObject.getString("specificationKey"));

		JSONObject labelJSONObject =
			productSpecificationJSONObject.getJSONObject("label");

		Assert.assertEquals("Color", labelJSONObject.getString("en_US"));
	}

	private void _testExecuteWithVariantPIMLinks() throws Exception {
		JSONObject jsonObject = _execute(
			"CLUSTER-1", _getRequiredObjectEntries(),
			_mockProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()),
			_mockProductObjectEntry(
				"SKU-2", Collections.<String, Serializable>emptyMap()));

		Assert.assertEquals(
			"CLUSTER-1", jsonObject.getString("externalReferenceCode"));

		JSONObject skuJSONObject = _getSkuJSONObject(0, jsonObject);

		Assert.assertEquals(
			"SKU-1", skuJSONObject.getString("externalReferenceCode"));
		Assert.assertEquals("SKU-1", skuJSONObject.getString("sku"));

		skuJSONObject = _getSkuJSONObject(1, jsonObject);

		Assert.assertEquals(
			"SKU-2", skuJSONObject.getString("externalReferenceCode"));
		Assert.assertEquals("SKU-2", skuJSONObject.getString("sku"));
	}

	private void _testExecuteWithoutUnitOfMeasure() throws Exception {
		JSONObject jsonObject = _execute(
			"SKU-1", _getRequiredObjectEntries(),
			_mockProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		JSONObject skuJSONObject = _getSkuJSONObject(0, jsonObject);

		Assert.assertFalse(
			skuJSONObject.toString(), skuJSONObject.has("skuUnitOfMeasures"));
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.model.ObjectDefinition#PIMBasicProduct";

	private static final long _LIST_TYPE_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private final FriendlyURLNormalizer _friendlyURLNormalizer = Mockito.mock(
		FriendlyURLNormalizer.class);
	private final Language _language = Mockito.mock(Language.class);
	private final LiferayCommercePIMConnector _liferayCommercePIMConnector =
		Mockito.spy(new LiferayCommercePIMConnector());
	private final MockedStatic<ListTypeEntryLocalServiceUtil>
		_listTypeEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ListTypeEntryLocalServiceUtil.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final MockedStatic<ObjectFieldLocalServiceUtil>
		_objectFieldLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectFieldLocalServiceUtil.class);
	private final ObjectEntry _pimConnectorObjectEntry = Mockito.mock(
		ObjectEntry.class);

}