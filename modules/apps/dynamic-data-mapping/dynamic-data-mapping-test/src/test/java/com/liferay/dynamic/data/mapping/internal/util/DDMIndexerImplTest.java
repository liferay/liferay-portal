/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.util;

import com.liferay.dynamic.data.mapping.configuration.DDMIndexerConfiguration;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormJSONDeserializer;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormJSONSerializer;
import com.liferay.dynamic.data.mapping.internal.test.util.DDMFixture;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureImpl;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.engine.ConnectionInformation;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Lino Alves
 * @author André de Oliveira
 */
public class DDMIndexerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("ddm.form.deserializer.type", "json");

		_ddmFormDeserializerServiceRegistration = bundleContext.registerService(
			DDMFormDeserializer.class, _ddmFormDeserializer, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_ddmFormDeserializerServiceRegistration.unregister();

		_frameworkUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_ddmIndexer = _createDDMIndexer(false);
		_ddmFixture.setUp();
		_documentFixture.setUp();

		_setUpJSONFactoryUtil();
		_setUpPortalUtil();
	}

	@After
	public void tearDown() {
		_ddmFixture.tearDown();
		_documentFixture.tearDown();
	}

	@Test
	public void testExtractIndexableAttributes() {
		_testExtractIndexableAttributes(
			_createDDMFormField(), StringPool.BLANK);
		_testExtractIndexableAttributes(_createDDMFormField(), "Create New");
		_testExtractIndexableAttributes(_createDDMFormField(), "null");

		DDMFormField ddmFormField = _createDDMFormField();

		ddmFormField.setRepeatable(true);

		_testExtractIndexableAttributes(ddmFormField, StringPool.BLANK);
	}

	@Test
	public void testExtractIndexableAttributesWithJournalArticleField() {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.BRAZIL, LocaleUtil.US),
			LocaleUtil.BRAZIL);

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			_FIELD_NAME, RandomTestUtil.randomString(),
			DDMFormFieldTypeConstants.JOURNAL_ARTICLE,
			DDMFormFieldTypeConstants.JOURNAL_ARTICLE, false, false, false);

		ddmFormField.setIndexType("keyword");

		ddmForm.addDDMFormField(ddmFormField);

		Assert.assertEquals(
			"Title",
			_ddmIndexer.extractIndexableAttributes(
				_createDDMStructure(ddmForm),
				_createDDMFormValues(
					ddmForm,
					DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
						_FIELD_NAME,
						JSONUtil.put(
							"title", "Title"
						).toString())),
				null));
		Assert.assertEquals(
			"Title Título",
			_ddmIndexer.extractIndexableAttributes(
				_createDDMStructure(ddmForm),
				_createDDMFormValues(
					ddmForm,
					DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
						_FIELD_NAME,
						JSONUtil.put(
							"title", "Title"
						).put(
							"titleMap",
							JSONUtil.put(
								"en_US", "Title"
							).put(
								"pt_BR", "Título"
							)
						).toString())),
				null));
	}

	@Test
	public void testFormWithLegacyDDMIndexFieldsEnabled() {
		DDMIndexer ddmIndexer = _createDDMIndexer(true);

		Document document = _createDocument();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.US), LocaleUtil.US);

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			"date", "date", DDMFormFieldType.DATE, "string", false, false,
			false);

		ddmFormField.setIndexType("keyword");

		ddmForm.addDDMFormField(ddmFormField);

		DDMStructure ddmStructure = _createDDMStructure(ddmForm);

		String randomDate = _randomDate();

		ddmIndexer.addAttributes(
			document, ddmStructure,
			_createDDMFormValues(
				ddmForm,
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"date", new UnlocalizedValue(randomDate))));

		String name = StringBundler.concat(
			"ddm__keyword__", ddmStructure.getStructureId(), "__date_");

		FieldValuesAssert.assertFieldValues(
			_getSortableValues(Collections.singletonMap(name, randomDate)),
			name, document, randomDate);
	}

	@Test
	public void testFormWithOneAvailableLocaleSameAsDefaultLocale() {
		Document document = _createDocument();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.JAPAN), LocaleUtil.JAPAN);

		ddmForm.addDDMFormField(_createDDMFormField());

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.JAPAN);

		localizedValue.addString(LocaleUtil.JAPAN, "新規作成");

		_ddmIndexer.addAttributes(
			document, _createDDMStructure(ddmForm),
			_createDDMFormValues(
				ddmForm,
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME, localizedValue)));

		FieldValuesAssert.assertFieldValues(
			_getSortableValues(
				Collections.singletonMap(
					"ddmFieldArray.ddmFieldValueText_ja_JP", "新規作成")),
			"ddmFieldArray.ddmFieldValueText", document, "新規作成");
	}

	@Test
	public void testFormWithRepeatableField() {
		_testFormWithRepeatableField("keyword");
		_testFormWithRepeatableField("text");
		_testFormWithRepeatableRichTextField();
	}

	@Test
	public void testFormWithSelectField() throws JSONException {
		Document document = _createDocument();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.US), LocaleUtil.US);

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			_FIELD_NAME, RandomTestUtil.randomString(), DDMFormFieldType.SELECT,
			"string", true, false, false);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel("apple", LocaleUtil.US, "Apple");
		ddmFormFieldOptions.addOptionLabel(
			"pineapple", LocaleUtil.US, "Pineapple");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmFormField.setIndexType("keyword");

		ddmForm.addDDMFormField(ddmFormField);

		_ddmIndexer.addAttributes(
			document, _createDDMStructure(ddmForm),
			_createDDMFormValues(
				ddmForm,
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME,
					DDMFormValuesTestUtil.createLocalizedValue(
						"[\"pineapple\"]", LocaleUtil.US))));

		FieldValuesAssert.assertFieldValues(
			HashMapBuilder.put(
				"ddmFieldArray.ddmFieldValueKeyword_en_US", "pineapple"
			).put(
				"ddmFieldArray.ddmFieldValueKeyword_en_US_String", "Pineapple"
			).put(
				"ddmFieldArray.ddmFieldValueKeyword_en_US_String_sortable",
				"pineapple"
			).build(),
			"ddmFieldArray.ddmFieldValueKeyword_en_US", document,
			StringPool.BLANK);
	}

	@Test
	public void testFormWithTwoAvailableLocalesAndFieldWithNondefaultLocale() {
		Document document = _createDocument();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.US, LocaleUtil.JAPAN), LocaleUtil.US);

		ddmForm.addDDMFormField(_createDDMFormField());

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.JAPAN, "新規作成");

		_ddmIndexer.addAttributes(
			document, _createDDMStructure(ddmForm),
			_createDDMFormValues(
				ddmForm,
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME, localizedValue)));

		FieldValuesAssert.assertFieldValues(
			_getSortableValues(
				Collections.singletonMap(
					"ddmFieldArray.ddmFieldValueText_ja_JP", "新規作成")),
			"ddmFieldArray.ddmFieldValueText", document, "新規作成");
	}

	@Test
	public void testFormWithTwoAvailableLocalesAndFieldWithTwoLocales() {
		Document document = _createDocument();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.JAPAN, LocaleUtil.US),
			LocaleUtil.JAPAN);

		ddmForm.addDDMFormField(_createDDMFormField());

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.JAPAN);

		localizedValue.addString(LocaleUtil.JAPAN, "新規作成");
		localizedValue.addString(LocaleUtil.US, "Create New");

		_ddmIndexer.addAttributes(
			document, _createDDMStructure(ddmForm),
			_createDDMFormValues(
				ddmForm,
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME, localizedValue)));

		FieldValuesAssert.assertFieldValues(
			_getSortableValues(
				HashMapBuilder.put(
					"ddmFieldArray.ddmFieldValueText_en_US", "Create New"
				).put(
					"ddmFieldArray.ddmFieldValueText_ja_JP", "新規作成"
				).build()),
			"ddmFieldArray.ddmFieldValueText", document, "新規作成");
	}

	private DDMFormField _createDDMFormField() {
		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			_FIELD_NAME, true, false, true);

		ddmFormField.setIndexType("text");

		return ddmFormField;
	}

	private DDMFormJSONSerializer _createDDMFormJSONSerializer() {
		return new DDMFormJSONSerializer() {
			{
				ReflectionTestUtil.setFieldValue(
					this, "_ddmFormFieldTypeServicesRegistry",
					Mockito.mock(DDMFormFieldTypeServicesRegistry.class));
				ReflectionTestUtil.setFieldValue(
					this, "_jsonFactory", new JSONFactoryImpl());
			}
		};
	}

	private DDMFormValues _createDDMFormValues(
		DDMForm ddmForm, DDMFormFieldValue... ddmFormFieldValues) {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return ddmFormValues;
	}

	private DDMIndexer _createDDMIndexer(boolean enableLegacyDDMIndexFields) {
		return new DDMIndexerImpl() {
			{
				DDMIndexerConfiguration ddmIndexerConfiguration =
					() -> enableLegacyDDMIndexFields;

				ReflectionTestUtil.setFieldValue(
					this, "_ddmFormValuesToFieldsConverter",
					new DDMFormValuesToFieldsConverterImpl());
				ReflectionTestUtil.setFieldValue(
					this, "_ddmIndexerConfiguration", ddmIndexerConfiguration);

				searchEngineInformation = new SearchEngineInformation() {

					public String getClientVersionString() {
						return null;
					}

					public List<ConnectionInformation>
						getConnectionInformationList() {

						return null;
					}

					@Override
					public int[] getEmbeddingVectorDimensions() {
						return new int[0];
					}

					public String getNodesString() {
						return null;
					}

					public String getVendorString() {
						return null;
					}

				};
			}
		};
	}

	private DDMStructure _createDDMStructure(DDMForm ddmForm) {
		DDMStructure ddmStructure = new DDMStructureImpl();

		DDMFormSerializerSerializeRequest.Builder builder =
			DDMFormSerializerSerializeRequest.Builder.newBuilder(ddmForm);

		DDMFormSerializerSerializeResponse ddmFormSerializerSerializeResponse =
			_ddmFormJSONSerializer.serialize(builder.build());

		ddmStructure.setDefinition(
			ddmFormSerializerSerializeResponse.getContent());

		ddmStructure.setStructureId(RandomTestUtil.randomLong());
		ddmStructure.setName(RandomTestUtil.randomString());
		ddmStructure.setDDMForm(ddmForm);

		_ddmFixture.whenDDMStructureLocalServiceFetchStructure(ddmStructure);

		return ddmStructure;
	}

	private Document _createDocument() {
		return DocumentFixture.newDocument(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			DDMForm.class.getName());
	}

	private Map<String, String> _getSortableValues(Map<String, String> map) {
		Map<String, String> sortableValues = new HashMap<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			sortableValues.put(
				entry.getKey() + "_String_sortable",
				StringUtil.toLowerCase(entry.getValue()));
		}

		sortableValues.putAll(map);

		return sortableValues;
	}

	private String _randomDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		return simpleDateFormat.format(new Date());
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		JSONFactory jsonFactory = new JSONFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			_ddmIndexer, "_jsonFactory", jsonFactory);

		jsonFactoryUtil.setJSONFactory(jsonFactory);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		ResourceBundle resourceBundle = Mockito.mock(ResourceBundle.class);

		Mockito.when(
			portal.getResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			resourceBundle
		);

		portalUtil.setPortal(portal);
	}

	private void _testExtractIndexableAttributes(
		DDMFormField ddmFormField, String fieldValue) {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.US), LocaleUtil.US);

		ddmForm.addDDMFormField(ddmFormField);

		Assert.assertEquals(
			fieldValue,
			_ddmIndexer.extractIndexableAttributes(
				_createDDMStructure(ddmForm),
				_createDDMFormValues(
					ddmForm,
					DDMFormValuesTestUtil.createDDMFormFieldValue(
						_FIELD_NAME,
						DDMFormValuesTestUtil.createLocalizedValue(
							fieldValue, LocaleUtil.US))),
				LocaleUtil.US));
	}

	private void _testFormWithRepeatableField(String indexType) {
		Document document = _createDocument();

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			_FIELD_NAME, "string", indexType, true,
			DDMFormFieldTypeConstants.TEXT, new Locale[] {LocaleUtil.US},
			LocaleUtil.US);

		_ddmIndexer.addAttributes(
			document, _createDDMStructure(ddmForm),
			_createDDMFormValues(
				ddmForm,
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME,
					DDMFormValuesTestUtil.createLocalizedValue(
						"able", LocaleUtil.US)),
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME,
					DDMFormValuesTestUtil.createLocalizedValue(
						"baker", LocaleUtil.US))));

		indexType = StringUtil.upperCaseFirstLetter(indexType);

		FieldValuesAssert.assertFieldValues(
			_getSortableValues(
				Collections.singletonMap(
					"ddmFieldArray.ddmFieldValue" + indexType + "_en_US",
					"[able, baker]")),
			"ddmFieldArray.ddmFieldValue" + indexType, document,
			StringPool.BLANK);
	}

	private void _testFormWithRepeatableRichTextField() {
		DDMIndexer ddmIndexer = _createDDMIndexer(true);

		HtmlParser htmlParser = Mockito.mock(HtmlParser.class);

		Mockito.when(
			htmlParser.extractText("<h1>able</h1>")
		).thenReturn(
			"able"
		);

		Mockito.when(
			htmlParser.extractText("<h1>baker</h1>")
		).thenReturn(
			"baker"
		);

		ReflectionTestUtil.setFieldValue(ddmIndexer, "_htmlParser", htmlParser);

		Document document = _createDocument();

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			_FIELD_NAME, "string", "text", true,
			DDMFormFieldTypeConstants.RICH_TEXT, new Locale[] {LocaleUtil.US},
			LocaleUtil.US);

		DDMStructure ddmStructure = _createDDMStructure(ddmForm);

		ddmIndexer.addAttributes(
			document, ddmStructure,
			_createDDMFormValues(
				ddmForm,
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME,
					DDMFormValuesTestUtil.createLocalizedValue(
						"<h1>able</h1>", LocaleUtil.US)),
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME,
					DDMFormValuesTestUtil.createLocalizedValue(
						"<h1>baker</h1>", LocaleUtil.US))));

		Assert.assertArrayEquals(
			new String[] {"able", "baker"},
			document.getValues(
				StringBundler.concat(
					"ddm__text__", ddmStructure.getStructureId(), "__",
					_FIELD_NAME, "_en_US")));
		Assert.assertArrayEquals(
			new String[] {"able", "baker"},
			document.getValues(
				StringBundler.concat(
					"ddm__text__", ddmStructure.getStructureId(), "__",
					_FIELD_NAME, "_en_US_String_sortable")));

		String value = RandomTestUtil.randomString(10000);

		String valueHTML = "<h1>" + value + "</h1>";

		Mockito.when(
			htmlParser.extractText(valueHTML)
		).thenReturn(
			value
		);

		ddmIndexer.addAttributes(
			document, ddmStructure,
			_createDDMFormValues(
				ddmForm,
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME,
					DDMFormValuesTestUtil.createLocalizedValue(
						valueHTML, LocaleUtil.US)),
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					_FIELD_NAME,
					DDMFormValuesTestUtil.createLocalizedValue(
						valueHTML, LocaleUtil.US))));

		Assert.assertArrayEquals(
			new String[] {value, value},
			document.getValues(
				StringBundler.concat(
					"ddm__text__", ddmStructure.getStructureId(), "__",
					_FIELD_NAME, "_en_US")));

		String truncatedValue = value.substring(
			0, _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH);

		Assert.assertArrayEquals(
			new String[] {truncatedValue, truncatedValue},
			document.getValues(
				StringBundler.concat(
					"ddm__text__", ddmStructure.getStructureId(), "__",
					_FIELD_NAME, "_en_US_String_sortable")));
	}

	private static final String _FIELD_NAME = RandomTestUtil.randomString();

	private static final int _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH = 255;

	private static final DDMFormDeserializer _ddmFormDeserializer =
		new DDMFormJSONDeserializer();
	private static ServiceRegistration<DDMFormDeserializer>
		_ddmFormDeserializerServiceRegistration;
	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private final DDMFixture _ddmFixture = new DDMFixture();
	private final DDMFormJSONSerializer _ddmFormJSONSerializer =
		_createDDMFormJSONSerializer();
	private DDMIndexer _ddmIndexer;
	private final DocumentFixture _documentFixture = new DocumentFixture();

}