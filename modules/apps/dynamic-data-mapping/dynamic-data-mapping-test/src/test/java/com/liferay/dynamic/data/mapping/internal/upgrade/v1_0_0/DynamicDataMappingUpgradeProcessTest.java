/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_0;

import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Marcellus Tavares
 */
public class DynamicDataMappingUpgradeProcessTest extends BaseDDMTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		setUpDDMFormValuesJSONDeserializer();
		setUpDDMFormValuesJSONSerializer();
		setUpJSONFactoryUtil();
		setUpLanguageUtil();
		setUpLocalizationUtil();
		setUpSAXReaderUtil();
		_setUpSecureXMLFactoryProviderUtil();

		_dynamicDataMappingUpgradeProcess =
			new DynamicDataMappingUpgradeProcess(
				null, null, null, null, null, null, null,
				ddmFormValuesJSONDeserializer, ddmFormValuesJSONSerializer,
				null, null, null, null, null, null,
				(ResourceActions)ProxyUtil.newProxyInstance(
					DynamicDataMappingUpgradeProcessTest.class.getClassLoader(),
					new Class<?>[] {ResourceActions.class},
					(proxy, method, args) -> {
						String methodName = method.getName();

						if (!methodName.equals("getCompositeModelName")) {
							return null;
						}

						if (ArrayUtil.isEmpty(args)) {
							return StringPool.BLANK;
						}

						Arrays.sort(args);

						StringBundler sb = new StringBundler(args.length * 2);

						for (Object className : args) {
							sb.append(className);
						}

						sb.setIndex(sb.index() - 1);

						return sb.toString();
					}),
				null, null, null, null);
	}

	@Test
	public void testCreateNewFieldNameWithConflictingNewFieldName()
		throws Exception {

		Set<String> existingFieldNames = new HashSet<>();

		existingFieldNames.add("myna");
		existingFieldNames.add("myna1");

		Assert.assertEquals(
			"myna2",
			_dynamicDataMappingUpgradeProcess.createNewDDMFormFieldName(
				"?my/--na", existingFieldNames));
	}

	@Test
	public void testCreateNewFieldNameWithSupportedOldFieldName()
		throws Exception {

		Set<String> existingFieldNames = Collections.<String>emptySet();

		Assert.assertEquals(
			"name",
			_dynamicDataMappingUpgradeProcess.createNewDDMFormFieldName(
				"name/?--", existingFieldNames));
		Assert.assertEquals(
			"firstName",
			_dynamicDataMappingUpgradeProcess.createNewDDMFormFieldName(
				"first Name", existingFieldNames));
		Assert.assertEquals(
			"this_is_a_field_name",
			_dynamicDataMappingUpgradeProcess.createNewDDMFormFieldName(
				"this?*&_is///_{{a[[  [_]  ~'field'////>_<name",
				existingFieldNames));
	}

	@Test(expected = UpgradeException.class)
	public void testCreateNewFieldNameWithUnsupportedOldFieldName()
		throws Exception {

		Set<String> existingFieldNames = Collections.<String>emptySet();

		_dynamicDataMappingUpgradeProcess.createNewDDMFormFieldName(
			"??????", existingFieldNames);
	}

	@Test
	public void testIsInvalidFieldName() {
		Assert.assertTrue(
			_dynamicDataMappingUpgradeProcess.isInvalidFieldName("/name?"));
		Assert.assertTrue(
			_dynamicDataMappingUpgradeProcess.isInvalidFieldName("_name--"));
		Assert.assertTrue(
			_dynamicDataMappingUpgradeProcess.isInvalidFieldName("name^*"));
		Assert.assertTrue(
			_dynamicDataMappingUpgradeProcess.isInvalidFieldName("name^*"));
		Assert.assertTrue(
			_dynamicDataMappingUpgradeProcess.isInvalidFieldName("my name"));
	}

	@Test
	public void testIsValidFieldName() {
		Assert.assertFalse(
			_dynamicDataMappingUpgradeProcess.isInvalidFieldName("name"));
		Assert.assertFalse(
			_dynamicDataMappingUpgradeProcess.isInvalidFieldName("name_"));
		Assert.assertFalse(
			_dynamicDataMappingUpgradeProcess.isInvalidFieldName("转注字"));
	}

	@Test
	public void testRenameInvalidDDMFormFieldNamesInJSON() throws Exception {
		long structureId = RandomTestUtil.randomLong();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", false, false, false);

		ddmFormField.setProperty("oldName", "name<!**>");

		ddmForm.addDDMFormField(ddmFormField);

		_dynamicDataMappingUpgradeProcess.
			populateStructureInvalidDDMFormFieldNamesMap(structureId, ddmForm);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"name<!**>", "Joe Bloggs"));

		String serializedDDMFormValues = serialize(ddmFormValues);

		String updatedSerializedDDMFormValues =
			_dynamicDataMappingUpgradeProcess.renameInvalidDDMFormFieldNames(
				structureId, serializedDDMFormValues);

		DDMFormValues updatedDDMFormValues = deserialize(
			updatedSerializedDDMFormValues, ddmForm);

		List<DDMFormFieldValue> updatedDDMFormFieldValues =
			updatedDDMFormValues.getDDMFormFieldValues();

		Assert.assertEquals(
			updatedDDMFormFieldValues.toString(), 1,
			updatedDDMFormFieldValues.size());

		DDMFormFieldValue updatedDDMFormFieldValue =
			updatedDDMFormFieldValues.get(0);

		Value value = updatedDDMFormFieldValue.getValue();

		Assert.assertEquals("name", updatedDDMFormFieldValue.getName());

		Assert.assertEquals("Joe Bloggs", value.getString(LocaleUtil.US));
	}

	@Test
	public void testRenameInvalidDDMFormFieldNamesInVMTemplate() {
		long structureId = RandomTestUtil.randomLong();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", false, false, false);

		ddmFormField.setProperty("oldName", "name*");

		ddmForm.addDDMFormField(ddmFormField);

		_dynamicDataMappingUpgradeProcess.
			populateStructureInvalidDDMFormFieldNamesMap(structureId, ddmForm);

		String updatedScript =
			_dynamicDataMappingUpgradeProcess.renameInvalidDDMFormFieldNames(
				structureId, "Hello $name*!");

		Assert.assertEquals("Hello $name!", updatedScript);
	}

	@Test
	public void testToJSONWithLocalizedAndNestedData() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(createAvailableLocales(LocaleUtil.US));
		ddmForm.setDefaultLocale(LocaleUtil.US);

		DDMFormField textDDMFormField = new DDMFormField("Text", "text");

		textDDMFormField.setDataType("string");
		textDDMFormField.setLocalizable(true);
		textDDMFormField.setRepeatable(true);

		DDMFormField textAreaDDMFormField = new DDMFormField(
			"TextArea", "textarea");

		textAreaDDMFormField.setDataType("string");
		textAreaDDMFormField.setLocalizable(true);
		textAreaDDMFormField.setRepeatable(true);

		textDDMFormField.addNestedDDMFormField(textAreaDDMFormField);

		ddmForm.addDDMFormField(textDDMFormField);

		// DDM form values

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setAvailableLocales(
			createAvailableLocales(LocaleUtil.BRAZIL, LocaleUtil.US));
		ddmFormValues.setDefaultLocale(LocaleUtil.US);

		DDMFormFieldValue text1DDMFormFieldValue = createDDMFormFieldValue(
			"srfa", "Text",
			createLocalizedValue(
				"En Text Value 1", "Pt Text Value 1", LocaleUtil.US));

		text1DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"elcy", "TextArea",
				createLocalizedValue(
					"En Text Area Value 1", "Pt Text Area Value 1",
					LocaleUtil.US)));
		text1DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"uxyj", "TextArea",
				createLocalizedValue(
					"En Text Area Value 2", "Pt Text Area Value 2",
					LocaleUtil.US)));

		ddmFormValues.addDDMFormFieldValue(text1DDMFormFieldValue);

		DDMFormFieldValue text2DDMFormFieldValue = createDDMFormFieldValue(
			"ealq", "Text",
			createLocalizedValue(
				"En Text Value 2", "Pt Text Value 2", LocaleUtil.US));

		text2DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"eepy", "TextArea",
				createLocalizedValue(
					"En Text Area Value 3", "Pt Text Area Value 3",
					LocaleUtil.US)));

		ddmFormValues.addDDMFormFieldValue(text2DDMFormFieldValue);

		// XML

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute("default-locale", "en_US");
		rootElement.addAttribute("available-locales", "en_US,pt_BR");

		_addDynamicElementElement(
			rootElement, "Text",
			new String[] {"En Text Value 1", "En Text Value 2"},
			new String[] {"Pt Text Value 1", "Pt Text Value 2"});
		_addDynamicElementElement(
			rootElement, "TextArea",
			new String[] {
				"En Text Area Value 1", "En Text Area Value 2",
				"En Text Area Value 3"
			},
			new String[] {
				"Pt Text Area Value 1", "Pt Text Area Value 2",
				"Pt Text Area Value 3"
			});
		_addDynamicElementElement(
			rootElement, "_fieldsDisplay",
			new String[] {
				"Text_INSTANCE_srfa,TextArea_INSTANCE_elcy," +
					"TextArea_INSTANCE_uxyj,Text_INSTANCE_ealq," +
						"TextArea_INSTANCE_eepy"
			});

		String expectedJSON = serialize(ddmFormValues);

		DDMFormValues actualDDMFormValues =
			_dynamicDataMappingUpgradeProcess.getDDMFormValues(
				1L, ddmForm, document.asXML());

		String actualJSON = _dynamicDataMappingUpgradeProcess.toJSON(
			actualDDMFormValues);

		JSONAssert.assertEquals(expectedJSON, actualJSON, false);
	}

	@Test
	public void testToJSONWithLocalizedData() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(createAvailableLocales(LocaleUtil.US));
		ddmForm.setDefaultLocale(LocaleUtil.US);

		DDMFormField textDDMFormField = new DDMFormField("Text", "text");

		textDDMFormField.setDataType("string");
		textDDMFormField.setLocalizable(true);
		textDDMFormField.setRepeatable(true);

		ddmForm.addDDMFormField(textDDMFormField);

		DDMFormField textAreaDDMFormField = new DDMFormField(
			"TextArea", "textarea");

		textAreaDDMFormField.setDataType("string");
		textAreaDDMFormField.setLocalizable(true);
		textAreaDDMFormField.setRepeatable(true);

		ddmForm.addDDMFormField(textAreaDDMFormField);

		DDMFormField integerDDMFormField = new DDMFormField(
			"Integer", "ddm-integer");

		integerDDMFormField.setDataType("integer");
		integerDDMFormField.setLocalizable(false);
		integerDDMFormField.setRepeatable(false);

		ddmForm.addDDMFormField(integerDDMFormField);

		// DDM form values

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setAvailableLocales(
			createAvailableLocales(LocaleUtil.BRAZIL, LocaleUtil.US));
		ddmFormValues.setDefaultLocale(LocaleUtil.US);

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"srfa", "Text",
				createLocalizedValue(
					"En Text Value 1", "Pt Text Value 1", LocaleUtil.US)));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"ealq", "Text",
				createLocalizedValue(
					"En Text Value 2", "Pt Text Value 2", LocaleUtil.US)));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"elcy", "TextArea",
				createLocalizedValue(
					"En Text Area Value 1", "Pt Text Area Value 1",
					LocaleUtil.US)));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"uxyj", "TextArea",
				createLocalizedValue(
					"En Text Area Value 2", "Pt Text Area Value 2",
					LocaleUtil.US)));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"eepy", "TextArea",
				createLocalizedValue(
					"En Text Area Value 3", "Pt Text Area Value 3",
					LocaleUtil.US)));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"ckkp", "Integer", new UnlocalizedValue("1")));

		// XML

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute("default-locale", "en_US");
		rootElement.addAttribute("available-locales", "en_US,pt_BR");

		_addDynamicElementElement(
			rootElement, "Text",
			new String[] {"En Text Value 1", "En Text Value 2"},
			new String[] {"Pt Text Value 1", "Pt Text Value 2"});
		_addDynamicElementElement(
			rootElement, "TextArea",
			new String[] {
				"En Text Area Value 1", "En Text Area Value 2",
				"En Text Area Value 3"
			},
			new String[] {
				"Pt Text Area Value 1", "Pt Text Area Value 2",
				"Pt Text Area Value 3"
			});
		_addDynamicElementElement(rootElement, "Integer", new String[] {"1"});
		_addDynamicElementElement(
			rootElement, "_fieldsDisplay",
			new String[] {
				"Text_INSTANCE_srfa,Text_INSTANCE_ealq," +
					"TextArea_INSTANCE_elcy,TextArea_INSTANCE_uxyj," +
						"TextArea_INSTANCE_eepy,Integer_INSTANCE_ckkp"
			});

		String expectedJSON = serialize(ddmFormValues);

		DDMFormValues actualDDMFormValues =
			_dynamicDataMappingUpgradeProcess.getDDMFormValues(
				1L, ddmForm, document.asXML());

		String actualJSON = _dynamicDataMappingUpgradeProcess.toJSON(
			actualDDMFormValues);

		JSONAssert.assertEquals(expectedJSON, actualJSON, false);
	}

	@Test
	public void testToJSONWithoutLocalizedData() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(createAvailableLocales(LocaleUtil.US));
		ddmForm.setDefaultLocale(LocaleUtil.US);

		DDMFormField textDDMFormField = new DDMFormField("Text", "text");

		textDDMFormField.setDataType("string");
		textDDMFormField.setLocalizable(false);
		textDDMFormField.setRepeatable(false);

		ddmForm.addDDMFormField(textDDMFormField);

		DDMFormField textAreaDDMFormField = new DDMFormField(
			"TextArea", "textarea");

		textAreaDDMFormField.setDataType("string");
		textAreaDDMFormField.setLocalizable(false);
		textAreaDDMFormField.setRepeatable(true);

		ddmForm.addDDMFormField(textAreaDDMFormField);

		// DDM form values

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setAvailableLocales(
			createAvailableLocales(LocaleUtil.US));
		ddmFormValues.setDefaultLocale(LocaleUtil.US);

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"hcxo", "Text", new UnlocalizedValue("Text Value")));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"vfqd", "TextArea", new UnlocalizedValue("Text Area Value 1")));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"ycey", "TextArea", new UnlocalizedValue("Text Area Value 2")));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"habt", "TextArea", new UnlocalizedValue("Text Area Value 3")));

		// XML

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute("default-locale", "en_US");
		rootElement.addAttribute("available-locales", "en_US");

		_addDynamicElementElement(
			rootElement, "Text", new String[] {"Text Value"});
		_addDynamicElementElement(
			rootElement, "TextArea",
			new String[] {
				"Text Area Value 1", "Text Area Value 2", "Text Area Value 3"
			});
		_addDynamicElementElement(
			rootElement, "_fieldsDisplay",
			new String[] {
				"Text_INSTANCE_hcxo,TextArea_INSTANCE_vfqd," +
					"TextArea_INSTANCE_ycey,TextArea_INSTANCE_habt"
			});

		String expectedJSON = serialize(ddmFormValues);

		DDMFormValues actualDDMFormValues =
			_dynamicDataMappingUpgradeProcess.getDDMFormValues(
				1L, ddmForm, document.asXML());

		String actualJSON = _dynamicDataMappingUpgradeProcess.toJSON(
			actualDDMFormValues);

		JSONAssert.assertEquals(expectedJSON, actualJSON, false);
	}

	@Test
	public void testToXMLWithoutLocalizedData() throws Exception {
		String fieldsDisplay = "Text_INSTANCE_hcxo";

		String xml = _dynamicDataMappingUpgradeProcess.toXML(
			HashMapBuilder.put(
				"_fieldsDisplay",
				_createLocalizationXML(new String[] {fieldsDisplay})
			).put(
				"Text", _createLocalizationXML(new String[] {"Joe Bloggs"})
			).build());

		Document document = SAXReaderUtil.read(xml);

		Map<String, Map<String, List<String>>> dataMap = _toDataMap(document);

		Map<String, List<String>> actualTextData = dataMap.get("Text");

		_assertEquals(
			ListUtil.fromArray("Joe Bloggs"), actualTextData.get("en_US"));

		Map<String, List<String>> actualFieldsDisplayData = dataMap.get(
			"_fieldsDisplay");

		_assertEquals(
			ListUtil.fromArray(fieldsDisplay),
			actualFieldsDisplayData.get("en_US"));
	}

	@Test
	public void testToXMLWithRepeatableAndLocalizedData() throws Exception {
		String fieldsDisplay =
			"Text_INSTANCE_hcxo,Text_INSTANCE_vfqd,Text_INSTANCE_ycey";

		String xml = _dynamicDataMappingUpgradeProcess.toXML(
			HashMapBuilder.put(
				"_fieldsDisplay",
				_createLocalizationXML(new String[] {fieldsDisplay})
			).put(
				"Text",
				_createLocalizationXML(
					new String[] {"A", "B", "C"}, new String[] {"D", "E", "F"})
			).build());

		Document document = SAXReaderUtil.read(xml);

		Map<String, Map<String, List<String>>> dataMap = _toDataMap(document);

		Map<String, List<String>> actualTextData = dataMap.get("Text");

		_assertEquals(
			ListUtil.fromArray("A", "B", "C"), actualTextData.get("en_US"));

		_assertEquals(
			ListUtil.fromArray("D", "E", "F"), actualTextData.get("pt_BR"));

		Map<String, List<String>> actualFieldsDisplayData = dataMap.get(
			"_fieldsDisplay");

		_assertEquals(
			ListUtil.fromArray(fieldsDisplay),
			actualFieldsDisplayData.get("en_US"));
	}

	private void _addDynamicContentElements(
		Element dynamicElementElement, String[] dynamicContentDataArray,
		Locale locale) {

		for (String dynamicContentData : dynamicContentDataArray) {
			Element dynamicContentElement = dynamicElementElement.addElement(
				"dynamic-content");

			dynamicContentElement.addAttribute(
				"language-id", LocaleUtil.toLanguageId(locale));
			dynamicContentElement.addCDATA(dynamicContentData);
		}
	}

	private void _addDynamicElementElement(
		Element rootElement, String fieldName,
		String[] enDynamicContentDataArray) {

		Element dynamicElementElement = _createDynamicElementElement(
			rootElement, fieldName);

		_addDynamicContentElements(
			dynamicElementElement, enDynamicContentDataArray, LocaleUtil.US);
	}

	private void _addDynamicElementElement(
		Element rootElement, String fieldName,
		String[] enDynamicContentDataArray,
		String[] ptDynamicContentDataArray) {

		Element dynamicElementElement = _createDynamicElementElement(
			rootElement, fieldName);

		_addDynamicContentElements(
			dynamicElementElement, enDynamicContentDataArray, LocaleUtil.US);
		_addDynamicContentElements(
			dynamicElementElement, ptDynamicContentDataArray,
			LocaleUtil.BRAZIL);
	}

	private void _append(
		Map<String, List<String>> localizedDataMap, String languageId,
		String localizedData) {

		List<String> data = localizedDataMap.get(languageId);

		if (data == null) {
			data = new ArrayList<>();

			localizedDataMap.put(languageId, data);
		}

		data.add(localizedData);
	}

	private void _assertEquals(
		List<String> expectedDataValues, List<String> actualDataValues) {

		int expectedDataValuesSize = expectedDataValues.size();

		Assert.assertEquals(
			actualDataValues.toString(), expectedDataValuesSize,
			actualDataValues.size());

		for (int i = 0; i < expectedDataValuesSize; i++) {
			Assert.assertEquals(
				expectedDataValues.get(i), actualDataValues.get(i));
		}
	}

	private Element _createDynamicElementElement(
		Element rootElement, String fieldName) {

		Element dynamicElementElement = rootElement.addElement(
			"dynamic-element");

		dynamicElementElement.addAttribute("default-language-id", "en_US");
		dynamicElementElement.addAttribute("name", fieldName);

		return dynamicElementElement;
	}

	private String _createLocalizationXML(String[] enData) {
		StringBundler sb = new StringBundler(6);

		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<root available-locales='en_US' default-locale='en_US'>");
		sb.append("<Data language-id='en_US'>");
		sb.append(StringUtil.merge(enData));
		sb.append("</Data>");
		sb.append("</root>");

		return sb.toString();
	}

	private String _createLocalizationXML(String[] enData, String[] ptData) {
		StringBundler sb = new StringBundler(10);

		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<root available-locales='en_US,pt_BR,' ");
		sb.append("default-locale='en_US'>");
		sb.append("<Data language-id='en_US'>");
		sb.append(StringUtil.merge(enData));
		sb.append("</Data>");
		sb.append("<Data language-id='pt_BR'>");
		sb.append(StringUtil.merge(ptData));
		sb.append("</Data>");
		sb.append("</root>");

		return sb.toString();
	}

	private Map<String, List<String>> _getLocalizedDataMap(
		Element dynamicElementElement) {

		Map<String, List<String>> localizedDataMap = new HashMap<>();

		for (Element dynamicContentElement : dynamicElementElement.elements()) {
			String languageId = dynamicContentElement.attributeValue(
				"language-id");

			_append(
				localizedDataMap, languageId, dynamicContentElement.getText());
		}

		return localizedDataMap;
	}

	private void _setUpSecureXMLFactoryProviderUtil() {
		SecureXMLFactoryProviderUtil secureXMLFactoryProviderUtil =
			new SecureXMLFactoryProviderUtil();

		secureXMLFactoryProviderUtil.setSecureXMLFactoryProvider(
			new SecureXMLFactoryProviderImpl());
	}

	private Map<String, Map<String, List<String>>> _toDataMap(
		Document document) {

		Element rootElement = document.getRootElement();

		Map<String, Map<String, List<String>>> dataMap = new HashMap<>();

		for (Element dynamicElementElement :
				rootElement.elements("dynamic-element")) {

			String name = dynamicElementElement.attributeValue("name");

			dataMap.put(name, _getLocalizedDataMap(dynamicElementElement));
		}

		return dataMap;
	}

	private DynamicDataMappingUpgradeProcess _dynamicDataMappingUpgradeProcess;

}