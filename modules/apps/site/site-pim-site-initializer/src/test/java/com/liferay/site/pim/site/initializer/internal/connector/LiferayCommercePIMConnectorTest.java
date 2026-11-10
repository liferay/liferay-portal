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
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public void setUp() throws Exception {
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
			_liferayCommercePIMConnector, "_http", _http);
		ReflectionTestUtil.setFieldValue(
			_liferayCommercePIMConnector, "_jsonFactory",
			JSONFactoryUtil.getJSONFactory());
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

		_mockHttp(Collections.emptyMap());
	}

	@After
	public void tearDown() {
		_htmlParserUtilMockedStatic.close();
		_listTypeEntryLocalServiceUtilMockedStatic.close();
		_objectFieldLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testExecute() throws Exception {
		_testExecute();
		_testExecuteWithBooleanProductType();
		_testExecuteWithCatalogs();
		_testExecuteWithErrors();
		_testExecuteWithFieldMappingRules();
		_testExecuteWithFirstNonemptySourceFieldMappingRule();
		_testExecuteWithForeignStructureFixedValue();
		_testExecuteWithInvalidStructureFieldMapping();
		_testExecuteWithJoinedValues();
		_testExecuteWithPicklistProductOptions();
		_testExecuteWithPicklistProductSpecification();
		_testExecuteWithProductOptions();
		_testExecuteWithProductSpecifications();
		_testExecuteWithTrailingSlashURL();
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
				"catalog-external-reference-code", "depth", "description",
				"height", "name", "precision", "product-options",
				"product-specifications", "product-type", "sku", "tags",
				"unit-of-measure-key", "unit-of-measure-name", "weight",
				"width"),
			TransformUtil.transform(
				pimConnectorChannelFields,
				pimConnectorChannelField -> pimConnectorChannelField.getLabel(
					LocaleUtil.US)));
		Assert.assertEquals(
			Arrays.asList(
				"catalogExternalReferenceCode", "skus[].depth", "description",
				"skus[].height", "name", "skus[].skuUnitOfMeasures[].precision",
				"productOptions", "productSpecifications", "productType",
				"skus[].sku", "tags", "skus[].skuUnitOfMeasures[].key",
				"skus[].skuUnitOfMeasures[].name", "skus[].weight",
				"skus[].width"),
			TransformUtil.transform(
				pimConnectorChannelFields, PIMConnectorChannelField::getName));
		Assert.assertEquals(
			Arrays.asList(
				"catalogExternalReferenceCode", "name", "productType",
				"skus[].sku"),
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
				"Text", "Decimal", "Text", "Decimal", "Text", "Integer", "Text",
				"Text", "Text", "Text", "Text", "Text", "Text", "Decimal",
				"Decimal"),
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

	@Test
	public void testGetPIMConnectorConfigurationObjectDefinitionExternalReferenceCode() {
		Assert.assertEquals(
			"L_PIM_CONNECTOR_CONFIGURATION_LIFERAY_COMMERCE",
			_liferayCommercePIMConnector.
				getPIMConnectorConfigurationObjectDefinitionExternalReferenceCode());
	}

	private PIMConnectorException _assertPIMConnectorException(
			String expectedMessage, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				expectedMessage, pimConnectorException.getMessage());

			return pimConnectorException;
		}

		throw new AssertionError(expectedMessage);
	}

	private String _execute(
			Map<String, Serializable> pimConnectorConfigurationValues,
			List<ObjectEntry> pimFieldMappingObjectEntries,
			Map<String, ObjectEntry> pimFieldMappingRuleObjectEntriesMap,
			Map<String, List<ObjectEntry>> pimProductObjectEntriesMap)
		throws Exception {

		Map<String, List<ObjectEntry>> pimFieldMappingObjectEntriesMap =
			new HashMap<>();

		for (ObjectEntry objectEntry : pimFieldMappingObjectEntries) {
			List<ObjectEntry> objectEntries =
				pimFieldMappingObjectEntriesMap.computeIfAbsent(
					MapUtil.getString(
						objectEntry.getValues(), "channelFieldName"),
					key -> new ArrayList<>());

			objectEntries.add(objectEntry);
		}

		_options.clear();

		return _liferayCommercePIMConnector.doExecute(
			pimConnectorConfigurationValues, _pimConnectorObjectEntry,
			pimFieldMappingObjectEntriesMap,
			pimFieldMappingRuleObjectEntriesMap, pimProductObjectEntriesMap);
	}

	private Http.Options _getOptions(String location) {
		List<Http.Options> matchingOptions = ListUtil.filter(
			_options,
			options -> {
				String curLocation = options.getLocation();

				return curLocation.contains(location);
			});

		Assert.assertEquals(
			matchingOptions.toString(), 1, matchingOptions.size());

		return matchingOptions.get(0);
	}

	private JSONObject _getProductJSONObject(
			String externalReferenceCode,
			List<ObjectEntry> pimFieldMappingObjectEntries,
			Map<String, ObjectEntry> pimFieldMappingRuleObjectEntriesMap,
			ObjectEntry... pimProductObjectEntries)
		throws Exception {

		_execute(
			_pimConnectorConfigurationValues, pimFieldMappingObjectEntries,
			pimFieldMappingRuleObjectEntriesMap,
			HashMapBuilder.put(
				externalReferenceCode, Arrays.asList(pimProductObjectEntries)
			).build());

		Http.Options options = _getOptions("/import-task");

		Http.Body body = options.getBody();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			body.getContent());

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		return jsonArray.getJSONObject(0);
	}

	private List<ObjectEntry> _getRequiredObjectEntries(
		ObjectEntry... objectEntries) {

		List<ObjectEntry> requiredObjectEntries = ListUtil.fromArray(
			_mockPIMFieldMappingObjectEntry(
				"catalogExternalReferenceCode", StringPool.BLANK,
				StringPool.BLANK,
				PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE,
				_CATALOG_EXTERNAL_REFERENCE_CODE_1),
			_mockPIMFieldMappingObjectEntry(
				"name", StringPool.BLANK, "title", "dynamicValue",
				StringPool.BLANK),
			_mockPIMFieldMappingObjectEntry(
				"productType", StringPool.BLANK, StringPool.BLANK,
				PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "simple"),
			_mockPIMFieldMappingObjectEntry(
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

	private void _mockHttp(Map<String, Integer> responseCodes)
		throws Exception {

		Mockito.when(
			_http.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(
					0, Http.Options.class);

				_options.add(options);

				String location = options.getLocation();
				Http.Response response = options.getResponse();

				for (Map.Entry<String, Integer> entry :
						responseCodes.entrySet()) {

					if (location.contains(entry.getKey())) {
						response.setResponseCode(entry.getValue());

						return JSONUtil.put(
							"title", RandomTestUtil.randomString()
						).toString();
					}
				}

				response.setResponseCode(200);

				if (location.endsWith("/o/oauth2/token")) {
					return JSONUtil.put(
						"access_token", "TOKEN"
					).toString();
				}

				if (location.contains("/import-task")) {
					return JSONUtil.put(
						"executeStatus", "INITIAL"
					).put(
						"externalReferenceCode", "BATCH-1"
					).toString();
				}

				return "{}";
			}
		);
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

	private ObjectEntry _mockPIMFieldMappingObjectEntry(
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

	private ObjectEntry _mockPIMFieldMappingRuleObjectEntry(
		Map<String, Serializable> values) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			values
		);

		return objectEntry;
	}

	private ObjectEntry _mockPIMProductObjectEntry(
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
		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1", _getRequiredObjectEntries(),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		Assert.assertTrue(jsonObject.getBoolean("active"));
		Assert.assertEquals(
			_CATALOG_EXTERNAL_REFERENCE_CODE_1,
			jsonObject.getString("catalogExternalReferenceCode"));
		Assert.assertFalse(jsonObject.has("catalogId"));
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
			_mockPIMFieldMappingObjectEntry(
				"catalogExternalReferenceCode", StringPool.BLANK,
				StringPool.BLANK,
				PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE,
				_CATALOG_EXTERNAL_REFERENCE_CODE_1),
			_mockPIMFieldMappingObjectEntry(
				"name", StringPool.BLANK, "title", "dynamicValue",
				StringPool.BLANK),
			_mockPIMFieldMappingObjectEntry(
				"productType", StringPool.BLANK, "virtual", "dynamicValue",
				StringPool.BLANK),
			_mockPIMFieldMappingObjectEntry(
				"skus[].sku", StringPool.BLANK, "sku", "dynamicValue",
				StringPool.BLANK));

		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1", objectEntries, Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"virtual", false
				).build()));

		Assert.assertEquals("simple", jsonObject.getString("productType"));

		jsonObject = _getProductJSONObject(
			"SKU-2", objectEntries, Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-2",
				HashMapBuilder.<String, Serializable>put(
					"virtual", true
				).build()));

		Assert.assertEquals("virtual", jsonObject.getString("productType"));
	}

	private void _testExecuteWithCatalogs() throws Exception {
		_mockObjectField("Color", "color");

		List<ObjectEntry> objectEntries = ListUtil.fromArray(
			_mockPIMFieldMappingObjectEntry(
				"catalogExternalReferenceCode", StringPool.BLANK, "catalog",
				"dynamicValue", StringPool.BLANK),
			_mockPIMFieldMappingObjectEntry(
				"name", StringPool.BLANK, "title", "dynamicValue",
				StringPool.BLANK),
			_mockPIMFieldMappingObjectEntry(
				"productOptions", StringPool.BLANK, "color", "dynamicValue",
				StringPool.BLANK),
			_mockPIMFieldMappingObjectEntry(
				"productType", StringPool.BLANK, StringPool.BLANK,
				PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "simple"),
			_mockPIMFieldMappingObjectEntry(
				"skus[].sku", StringPool.BLANK, "sku", "dynamicValue",
				StringPool.BLANK));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_execute(
				_pimConnectorConfigurationValues, objectEntries,
				Collections.<String, ObjectEntry>emptyMap(),
				HashMapBuilder.put(
					"SKU-1",
					Arrays.asList(
						_mockPIMProductObjectEntry(
							"SKU-1",
							HashMapBuilder.<String, Serializable>put(
								"catalog", _CATALOG_EXTERNAL_REFERENCE_CODE_1
							).put(
								"color", RandomTestUtil.randomString()
							).build()))
				).put(
					"SKU-2",
					Arrays.asList(
						_mockPIMProductObjectEntry(
							"SKU-2",
							HashMapBuilder.<String, Serializable>put(
								"catalog", _CATALOG_EXTERNAL_REFERENCE_CODE_2
							).put(
								"color", RandomTestUtil.randomString()
							).build()))
				).build()));

		Assert.assertEquals(
			"BATCH-1", jsonObject.getString("batchExternalReferenceCode"));
		Assert.assertEquals("INITIAL", jsonObject.getString("status"));

		Http.Options options = _getOptions("/o/oauth2/token");

		Assert.assertEquals(
			"https://example.com/o/oauth2/token", options.getLocation());

		options = _getOptions("/options/by-externalReferenceCode/color");

		Assert.assertEquals(Http.Method.PUT, options.getMethod());

		Http.Body body = options.getBody();

		JSONObject optionJSONObject = JSONFactoryUtil.createJSONObject(
			body.getContent());

		Assert.assertEquals("select", optionJSONObject.getString("fieldType"));
		Assert.assertEquals("color", optionJSONObject.getString("key"));
		Assert.assertTrue(optionJSONObject.getBoolean("skuContributor"));

		JSONObject nameJSONObject = optionJSONObject.getJSONObject("name");

		Assert.assertEquals("Color", nameJSONObject.getString("en_US"));

		options = _getOptions("/import-task");

		Assert.assertEquals(Http.Method.POST, options.getMethod());
		Assert.assertEquals(
			StringBundler.concat(
				"https://example.com/o/headless-batch-engine/v1.0/import-task",
				"/com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product",
				"?createStrategy=UPSERT&importStrategy=ON_ERROR_CONTINUE"),
			options.getLocation());

		body = options.getBody();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			body.getContent());

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		Set<String> catalogExternalReferenceCodes = new HashSet<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject productJSONObject = jsonArray.getJSONObject(i);

			catalogExternalReferenceCodes.add(
				productJSONObject.getString("catalogExternalReferenceCode"));
		}

		Assert.assertEquals(
			SetUtil.fromArray(
				_CATALOG_EXTERNAL_REFERENCE_CODE_1,
				_CATALOG_EXTERNAL_REFERENCE_CODE_2),
			catalogExternalReferenceCodes);

		jsonArray = jsonObject.getJSONArray("log");

		Assert.assertEquals(jsonArray.toString(), 3, jsonArray.length());
	}

	private void _testExecuteWithErrors() throws Exception {
		Map<String, List<ObjectEntry>> pimProductObjectEntriesMap =
			HashMapBuilder.<String, List<ObjectEntry>>put(
				"SKU-1",
				Arrays.asList(
					_mockPIMProductObjectEntry(
						"SKU-1", Collections.<String, Serializable>emptyMap()))
			).build();
		List<ObjectEntry> requiredObjectEntries = _getRequiredObjectEntries();

		_assertPIMConnectorException(
			"enter-a-valid-url",
			() -> _execute(
				Collections.<String, Serializable>emptyMap(),
				requiredObjectEntries,
				Collections.<String, ObjectEntry>emptyMap(),
				pimProductObjectEntriesMap));
		_assertPIMConnectorException(
			"enter-a-valid-url",
			() -> _execute(
				HashMapBuilder.<String, Serializable>put(
					"url", "ftp://localhost"
				).build(),
				requiredObjectEntries,
				Collections.<String, ObjectEntry>emptyMap(),
				pimProductObjectEntriesMap));
		_assertPIMConnectorException(
			"enter-a-valid-url",
			() -> _execute(
				HashMapBuilder.<String, Serializable>put(
					"url", "http://"
				).build(),
				requiredObjectEntries,
				Collections.<String, ObjectEntry>emptyMap(),
				pimProductObjectEntriesMap));
		_assertPIMConnectorException(
			"enter-a-valid-url",
			() -> _execute(
				HashMapBuilder.<String, Serializable>put(
					"url", "localhost"
				).build(),
				requiredObjectEntries,
				Collections.<String, ObjectEntry>emptyMap(),
				pimProductObjectEntriesMap));

		Assert.assertTrue(_options.isEmpty());

		Mockito.when(
			_http.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(
					0, Http.Options.class);

				_options.add(options);

				Http.Response response = options.getResponse();

				response.setResponseCode(200);

				String location = options.getLocation();

				if (location.endsWith("/o/oauth2/token")) {
					return JSONUtil.put(
						"access_token", "TOKEN"
					).toString();
				}

				if (location.contains("/import-task")) {
					return "<html>Login</html>";
				}

				return "{}";
			}
		);

		PIMConnectorException pimConnectorException =
			_assertPIMConnectorException(
				"an-unexpected-error-occurred",
				() -> _execute(
					_pimConnectorConfigurationValues,
					_getRequiredObjectEntries(),
					Collections.<String, ObjectEntry>emptyMap(),
					HashMapBuilder.put(
						"SKU-1",
						Arrays.asList(
							_mockPIMProductObjectEntry(
								"SKU-1",
								HashMapBuilder.<String, Serializable>put(
									"sku", "SKU-1"
								).put(
									"title", RandomTestUtil.randomString()
								).build()))
					).build()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			pimConnectorException.getOutput());

		JSONArray jsonArray = jsonObject.getJSONArray("log");

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		JSONObject logJSONObject = jsonArray.getJSONObject(1);

		JSONObject responseJSONObject = logJSONObject.getJSONObject("response");

		Assert.assertEquals(
			"<html>Login</html>", responseJSONObject.getString("body"));

		_mockHttp(Collections.emptyMap());

		_mockHttp(
			HashMapBuilder.put(
				"/import-task", 500
			).build());

		pimConnectorException = _assertPIMConnectorException(
			"the-remote-instance-returned-an-error",
			() -> _getProductJSONObject(
				"SKU-1", _getRequiredObjectEntries(),
				Collections.<String, ObjectEntry>emptyMap(),
				_mockPIMProductObjectEntry(
					"SKU-1", Collections.<String, Serializable>emptyMap())));

		jsonObject = JSONFactoryUtil.createJSONObject(
			pimConnectorException.getOutput());

		jsonArray = jsonObject.getJSONArray("log");

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		_mockHttp(Collections.emptyMap());
	}

	private void _testExecuteWithFieldMappingRules() throws Exception {
		_htmlParserUtilMockedStatic.when(
			() -> HtmlParserUtil.extractText("<br>")
		).thenReturn(
			StringPool.BLANK
		);

		JSONObject jsonObject1 = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"description", StringPool.BLANK, "description",
					"dynamicValue", StringPool.BLANK),
				_mockPIMFieldMappingObjectEntry(
					"name", StringPool.BLANK, "sku", "dynamicValue",
					StringPool.BLANK),
				_mockPIMFieldMappingObjectEntry(
					"productOptions", StringPool.BLANK, "color", "dynamicValue",
					StringPool.BLANK),
				_mockPIMFieldMappingObjectEntry(
					"productSpecifications", StringPool.BLANK, "material",
					"dynamicValue", StringPool.BLANK),
				_mockPIMFieldMappingObjectEntry(
					"tags", StringPool.BLANK, StringPool.BLANK,
					PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE,
					"Home Office"),
				_mockPIMFieldMappingObjectEntry(
					"tags", StringPool.BLANK, StringPool.BLANK,
					PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE,
					"Summer Sale")),
			HashMapBuilder.put(
				"description",
				_mockPIMFieldMappingRuleObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"stripHTMLTags", true
					).build())
			).put(
				"name",
				_mockPIMFieldMappingRuleObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"textCase", "upper"
					).build())
			).put(
				"productOptions",
				_mockPIMFieldMappingRuleObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"trimWhitespace", true
					).build())
			).put(
				"productSpecifications",
				_mockPIMFieldMappingRuleObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"textCase", "upper"
					).build())
			).put(
				"tags",
				_mockPIMFieldMappingRuleObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"textCase", "lower"
					).put(
						"urlEncode", true
					).build())
			).build(),
			_mockPIMProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"color", " Red "
				).put(
					"description", "<br>"
				).put(
					"material", "Cotton"
				).build()));

		Assert.assertFalse(
			jsonObject1.toString(), jsonObject1.has("description"));

		JSONObject jsonObject2 = jsonObject1.getJSONObject("name");

		Assert.assertEquals(
			"SKU-1 TITLE SKU-1", jsonObject2.getString("en_US"));

		JSONArray jsonArray = jsonObject1.getJSONArray("productOptions");

		jsonObject2 = jsonArray.getJSONObject(0);

		jsonArray = jsonObject2.getJSONArray("productOptionValues");

		jsonObject2 = jsonArray.getJSONObject(0);

		Assert.assertEquals("red", jsonObject2.getString("key"));

		jsonObject2 = jsonObject2.getJSONObject("name");

		Assert.assertEquals("Red", jsonObject2.getString("en_US"));

		jsonArray = jsonObject1.getJSONArray("productSpecifications");

		jsonObject2 = jsonArray.getJSONObject(0);

		jsonObject2 = jsonObject2.getJSONObject("value");

		Assert.assertEquals("COTTON", jsonObject2.getString("en_US"));

		jsonObject2 = _getSkuJSONObject(0, jsonObject1);

		jsonArray = jsonObject2.getJSONArray("skuOptions");

		jsonObject2 = jsonArray.getJSONObject(0);

		Assert.assertEquals("red", jsonObject2.getString("value"));

		Assert.assertEquals(
			Arrays.asList("home%20office", "summer%20sale"),
			JSONUtil.toStringList(jsonObject1.getJSONArray("tags")));
	}

	private void _testExecuteWithFirstNonemptySourceFieldMappingRule()
		throws Exception {

		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"name", StringPool.BLANK, "sku", "dynamicValue",
					StringPool.BLANK)),
			HashMapBuilder.put(
				"name",
				_mockPIMFieldMappingRuleObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"useFirstNonemptySource", true
					).build())
			).build(),
			_mockPIMProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		jsonObject = jsonObject.getJSONObject("name");

		Assert.assertEquals("SKU-1 title", jsonObject.getString("en_US"));
	}

	private void _testExecuteWithForeignStructureFixedValue() throws Exception {
		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"description", RandomTestUtil.randomString(),
					StringPool.BLANK,
					PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "A shirt")),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		Assert.assertFalse(
			jsonObject.toString(), jsonObject.has("description"));
	}

	private void _testExecuteWithInvalidStructureFieldMapping()
		throws Exception {

		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"description", RandomTestUtil.randomString(), "description",
					"dynamicValue", StringPool.BLANK)),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"description", "A red shirt"
				).build()));

		Assert.assertFalse(
			jsonObject.toString(), jsonObject.has("description"));
	}

	private void _testExecuteWithJoinedValues() throws Exception {
		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"name", _CLASS_NAME, "sku", "dynamicValue",
					StringPool.BLANK)),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		JSONObject nameJSONObject = jsonObject.getJSONObject("name");

		Assert.assertEquals("SKU-1", nameJSONObject.getString("en_US"));
	}

	private void _testExecuteWithPicklistProductOptions() throws Exception {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)
		).thenReturn(
			true
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
			listTypeEntry.getKey()
		).thenReturn(
			"extraLarge"
		);

		Mockito.when(
			listTypeEntry.getName(LocaleUtil.US)
		).thenReturn(
			"Extra Large"
		);

		_listTypeEntryLocalServiceUtilMockedStatic.when(
			() -> ListTypeEntryLocalServiceUtil.fetchListTypeEntry(
				_LIST_TYPE_DEFINITION_ID, "extraLarge")
		).thenReturn(
			listTypeEntry
		);

		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"productOptions", StringPool.BLANK, "size", "dynamicValue",
					StringPool.BLANK)),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"size", "extraLarge"
				).build()));

		JSONArray jsonArray = jsonObject.getJSONArray("productOptions");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject productOptionJSONObject = jsonArray.getJSONObject(0);

		jsonArray = productOptionJSONObject.getJSONArray("productOptionValues");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject productOptionValueJSONObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			"extraLarge", productOptionValueJSONObject.getString("key"));

		JSONObject nameJSONObject = productOptionValueJSONObject.getJSONObject(
			"name");

		Assert.assertEquals("Extra Large", nameJSONObject.getString("en_US"));

		JSONObject skuJSONObject = _getSkuJSONObject(0, jsonObject);

		jsonArray = skuJSONObject.getJSONArray("skuOptions");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject skuOptionJSONObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			"extraLarge", skuOptionJSONObject.getString("value"));
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

		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"productSpecifications", StringPool.BLANK, "size",
					"dynamicValue", StringPool.BLANK)),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
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

		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"productOptions", StringPool.BLANK, "color", "dynamicValue",
					StringPool.BLANK)),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"color", "Red"
				).build()),
			_mockPIMProductObjectEntry(
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
		Assert.assertEquals(0, productOptionValueJSONObject.getInt("quantity"));

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

		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1",
			_getRequiredObjectEntries(
				_mockPIMFieldMappingObjectEntry(
					"productSpecifications", StringPool.BLANK, "color",
					"dynamicValue", StringPool.BLANK),
				_mockPIMFieldMappingObjectEntry(
					"productSpecifications", StringPool.BLANK, "color",
					"dynamicValue", StringPool.BLANK),
				_mockPIMFieldMappingObjectEntry(
					"productSpecifications", StringPool.BLANK, StringPool.BLANK,
					PIMConnectorFieldMappingsUtil.TYPE_FIXED_VALUE, "Red")),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
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

	private void _testExecuteWithTrailingSlashURL() throws Exception {
		_execute(
			HashMapBuilder.<String, Serializable>put(
				"clientId", RandomTestUtil.randomString()
			).put(
				"clientSecret", RandomTestUtil.randomString()
			).put(
				"url", "https://example.com//"
			).build(),
			_getRequiredObjectEntries(),
			Collections.<String, ObjectEntry>emptyMap(),
			HashMapBuilder.put(
				"SKU-1",
				Arrays.asList(
					_mockPIMProductObjectEntry(
						"SKU-1",
						HashMapBuilder.<String, Serializable>put(
							"sku", "SKU-1"
						).put(
							"title", RandomTestUtil.randomString()
						).build()))
			).build());

		Http.Options options = _getOptions("/o/oauth2/token");

		Assert.assertEquals(
			"https://example.com/o/oauth2/token", options.getLocation());
	}

	private void _testExecuteWithVariantPIMLinks() throws Exception {
		JSONObject jsonObject = _getProductJSONObject(
			"CLUSTER-1", _getRequiredObjectEntries(),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()),
			_mockPIMProductObjectEntry(
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
		JSONObject jsonObject = _getProductJSONObject(
			"SKU-1", _getRequiredObjectEntries(),
			Collections.<String, ObjectEntry>emptyMap(),
			_mockPIMProductObjectEntry(
				"SKU-1", Collections.<String, Serializable>emptyMap()));

		JSONObject skuJSONObject = _getSkuJSONObject(0, jsonObject);

		Assert.assertFalse(
			skuJSONObject.toString(), skuJSONObject.has("skuUnitOfMeasures"));
	}

	private static final String _CATALOG_EXTERNAL_REFERENCE_CODE_1 =
		RandomTestUtil.randomString();

	private static final String _CATALOG_EXTERNAL_REFERENCE_CODE_2 =
		RandomTestUtil.randomString();

	private static final String _CLASS_NAME =
		"com.liferay.object.model.ObjectDefinition#PIMBasicProduct";

	private static final long _LIST_TYPE_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final Map<String, Serializable>
		_pimConnectorConfigurationValues =
			HashMapBuilder.<String, Serializable>put(
				"clientId", RandomTestUtil.randomString()
			).put(
				"clientSecret", RandomTestUtil.randomString()
			).put(
				"url", "https://example.com"
			).build();

	private final FriendlyURLNormalizer _friendlyURLNormalizer = Mockito.mock(
		FriendlyURLNormalizer.class);
	private final MockedStatic<HtmlParserUtil> _htmlParserUtilMockedStatic =
		Mockito.mockStatic(HtmlParserUtil.class);
	private final Http _http = Mockito.mock(Http.class);
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
	private final List<Http.Options> _options = new ArrayList<>();
	private final ObjectEntry _pimConnectorObjectEntry = Mockito.mock(
		ObjectEntry.class);

}