/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeSettings;
import com.liferay.dynamic.data.mapping.form.field.type.internal.fieldset.FieldSetDDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.internal.radio.RadioDDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.internal.select.SelectDDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.internal.text.TextDDMFormFieldType;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormJSONDeserializer;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormJSONSerializer;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormLayoutJSONDeserializer;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormLayoutJSONSerializer;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormValuesJSONSerializer;
import com.liferay.dynamic.data.mapping.internal.util.DDMImpl;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateImpl;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.test.util.DDMFormFieldTypeSettingsTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactory;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.util.LocalizationImpl;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Pablo Carvalho
 * @author Miguel Angelo Caldas Gallindo
 */
public abstract class BaseDDMTestCase {

	@BeforeClass
	public static void setUpClass() {
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

		_siteConnectedGroupGroupProviderServiceRegistration =
			bundleContext.registerService(
				SiteConnectedGroupGroupProvider.class,
				Mockito.mock(SiteConnectedGroupGroupProvider.class), null);
	}

	@AfterClass
	public static void tearDownClass() {
		_ddmFormDeserializerServiceRegistration.unregister();
		_siteConnectedGroupGroupProviderServiceRegistration.unregister();

		_frameworkUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		setUpPortalClassLoaderUtil();
		setUpPortalUtil();
		setUpResourceBundleUtil();
	}

	protected void addDDMFormFields(
		DDMForm ddmForm, DDMFormField... ddmFormFieldsArray) {

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		for (DDMFormField ddmFormField : ddmFormFieldsArray) {
			ddmFormFields.add(ddmFormField);
		}
	}

	protected void addNestedTextDDMFormFields(
		DDMFormField ddmFormField, String... fieldNames) {

		List<DDMFormField> nestedDDMFormFields =
			ddmFormField.getNestedDDMFormFields();

		for (String fieldName : fieldNames) {
			nestedDDMFormFields.add(createTextDDMFormField(fieldName));
		}
	}

	protected void addTextDDMFormFields(DDMForm ddmForm, String... fieldNames) {
		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		for (String fieldName : fieldNames) {
			ddmFormFields.add(createTextDDMFormField(fieldName));
		}
	}

	protected Set<Locale> createAvailableLocales(Locale... locales) {
		Set<Locale> availableLocales = new LinkedHashSet<>();

		for (Locale locale : locales) {
			availableLocales.add(locale);
		}

		return availableLocales;
	}

	protected Field createBRField(
		long ddmStructureId, String fieldName, List<Serializable> ptValues) {

		return new MockField(
			ddmStructureId, fieldName, ptValues, LocaleUtil.BRAZIL);
	}

	protected DDMForm createDDMForm(
		Set<Locale> availableLocales, Locale defaultLocale) {

		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(availableLocales);
		ddmForm.setDefaultLocale(defaultLocale);

		return ddmForm;
	}

	protected DDMForm createDDMForm(String... fieldNames) {
		DDMForm ddmForm = createDDMForm(
			createAvailableLocales(LocaleUtil.US), LocaleUtil.US);

		addTextDDMFormFields(ddmForm, fieldNames);

		return ddmForm;
	}

	protected DDMFormFieldValue createDDMFormFieldValue(
		DDMFormValues ddmFormValues, String instanceId, String name,
		Value value) {

		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			instanceId, name, value);

		ddmFormFieldValue.setDDMFormValues(ddmFormValues);

		return ddmFormFieldValue;
	}

	protected DDMFormFieldValue createDDMFormFieldValue(String name) {
		return createDDMFormFieldValue(
			StringUtil.randomString(), name,
			new UnlocalizedValue(StringUtil.randomString()));
	}

	protected DDMFormFieldValue createDDMFormFieldValue(
		String instanceId, String name, Value value) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setFieldReference(name);
		ddmFormFieldValue.setInstanceId(instanceId);
		ddmFormFieldValue.setName(name);
		ddmFormFieldValue.setValue(value);

		return ddmFormFieldValue;
	}

	protected DDMFormFieldValue createDDMFormFieldValue(
		String name, Value value) {

		return createDDMFormFieldValue(StringUtil.randomString(), name, value);
	}

	protected DDMFormValues createDDMFormValues(DDMForm ddmForm) {
		return createDDMFormValues(
			ddmForm, createAvailableLocales(LocaleUtil.US), LocaleUtil.US);
	}

	protected DDMFormValues createDDMFormValues(
		DDMForm ddmForm, Set<Locale> availableLocales, Locale defaultLocale) {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setAvailableLocales(availableLocales);
		ddmFormValues.setDefaultLocale(defaultLocale);

		return ddmFormValues;
	}

	protected Field createField(
		long ddmStructureId, String fieldName, List<Serializable> enValues,
		List<Serializable> ptValues) {

		Map<Locale, List<Serializable>> valuesMap = createValuesMap(
			enValues, ptValues);

		return new MockField(
			ddmStructureId, fieldName, valuesMap, LocaleUtil.US);
	}

	protected Fields createFields(Field... fieldsArray) {
		Fields fields = new Fields();

		for (Field field : fieldsArray) {
			fields.put(field);
		}

		return fields;
	}

	protected Field createFieldsDisplayField(
		long ddmStructureId, String value) {

		Field fieldsDisplayField = new MockField(
			ddmStructureId, DDMImpl.FIELDS_DISPLAY_NAME,
			createValuesList(value), LocaleUtil.US);

		fieldsDisplayField.setDefaultLocale(LocaleUtil.US);

		return fieldsDisplayField;
	}

	protected Value createLocalizedValue(
		String enValue, String ptValue, Locale defaultLocale) {

		Value value = new LocalizedValue(defaultLocale);

		value.addString(LocaleUtil.BRAZIL, ptValue);
		value.addString(LocaleUtil.US, enValue);

		return value;
	}

	protected DDMFormField createParagraphDDMFormField(String name) {
		DDMFormField ddmFormField = new DDMFormField(name, "paragraph");

		ddmFormField.setLocalizable(false);
		ddmFormField.setRepeatable(false);
		ddmFormField.setRequired(false);

		LocalizedValue localizedValue = ddmFormField.getLabel();

		localizedValue.addString(LocaleUtil.US, name);

		return ddmFormField;
	}

	protected DDMFormField createSeparatorDDMFormField(
		String name, boolean repeatable) {

		DDMFormField ddmFormField = new DDMFormField(name, "separator");

		ddmFormField.setRepeatable(repeatable);

		LocalizedValue localizedValue = ddmFormField.getLabel();

		localizedValue.addString(LocaleUtil.US, name);

		return ddmFormField;
	}

	protected DDMStructure createStructure(String name, DDMForm ddmForm) {
		DDMFormSerializerSerializeRequest.Builder builder =
			DDMFormSerializerSerializeRequest.Builder.newBuilder(ddmForm);

		DDMFormSerializerSerializeResponse ddmFormSerializerSerializeResponse =
			ddmFormJSONSerializer.serialize(builder.build());

		return createStructure(
			name, ddmFormSerializerSerializeResponse.getContent());
	}

	protected DDMStructure createStructure(String name, String definition) {
		DDMStructure structure = new DDMStructureImpl();

		ReflectionTestUtil.setFieldValue(
			_ddmFormDeserializer, "_ddmFormFieldTypeServicesRegistry",
			getMockedDDMFormFieldTypeServicesRegistry());
		ReflectionTestUtil.setFieldValue(
			_ddmFormDeserializer, "_jsonFactory", jsonFactory);

		structure.setStructureId(RandomTestUtil.randomLong());
		structure.setName(name);
		structure.setDefinition(definition);

		structures.put(structure.getStructureId(), structure);

		return structure;
	}

	protected DDMStructure createStructure(String name, String... fieldNames) {
		DDMForm ddmForm = createDDMForm(fieldNames);

		return createStructure(name, ddmForm);
	}

	protected DDMTemplate createTemplate(
		long templateId, String name, String mode, String script) {

		DDMTemplate template = new DDMTemplateImpl();

		template.setTemplateId(templateId);
		template.setName(name);
		template.setMode(mode);
		template.setScript(script);

		templates.put(template.getTemplateId(), template);

		return template;
	}

	protected DDMFormField createTextDDMFormField(String name) {
		return createTextDDMFormField(name, name, true, false, false);
	}

	protected DDMFormField createTextDDMFormField(
		String name, String label, boolean localizable, boolean repeatable,
		boolean required) {

		DDMFormField ddmFormField = new DDMFormField(name, "text");

		ddmFormField.setDataType("string");
		ddmFormField.setLocalizable(localizable);
		ddmFormField.setRepeatable(repeatable);
		ddmFormField.setRequired(required);

		LocalizedValue localizedValue = ddmFormField.getLabel();

		localizedValue.addString(LocaleUtil.US, label);

		return ddmFormField;
	}

	protected List<Serializable> createValuesList(String... valuesString) {
		List<Serializable> values = new ArrayList<>();

		for (String valueString : valuesString) {
			values.add(valueString);
		}

		return values;
	}

	protected Map<Locale, List<Serializable>> createValuesMap(
		List<Serializable> enValues, List<Serializable> ptValues) {

		Map<Locale, List<Serializable>> valuesMap = new HashMap<>();

		if (enValues != null) {
			valuesMap.put(LocaleUtil.US, enValues);
		}

		if (ptValues != null) {
			valuesMap.put(LocaleUtil.BRAZIL, ptValues);
		}

		return valuesMap;
	}

	protected DDMFormValues deserialize(String content, DDMForm ddmForm) {
		DDMFormValuesDeserializerDeserializeRequest.Builder builder =
			DDMFormValuesDeserializerDeserializeRequest.Builder.newBuilder(
				content, ddmForm);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				ddmFormValuesJSONDeserializer.deserialize(builder.build());

		return ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();
	}

	protected DDMFormFieldTypeServicesRegistry
		getMockedDDMFormFieldTypeServicesRegistry() {

		setUpDefaultDDMFormFieldType();

		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
			Mockito.mock(DDMFormFieldTypeServicesRegistry.class);

		DDMFormFieldRenderer ddmFormFieldRenderer = Mockito.mock(
			DDMFormFieldRenderer.class);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldRenderer(
				Mockito.anyString())
		).thenReturn(
			ddmFormFieldRenderer
		);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
				Mockito.anyString())
		).thenReturn(
			_defaultDDMFormFieldType
		);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
				Mockito.eq("fieldset"))
		).thenReturn(
			new FieldSetDDMFormFieldType()
		);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
				Mockito.eq("radio"))
		).thenReturn(
			new RadioDDMFormFieldType()
		);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
				Mockito.eq("select"))
		).thenReturn(
			new SelectDDMFormFieldType()
		);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
				Mockito.eq("text"))
		).thenReturn(
			new TextDDMFormFieldType()
		);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldTypeProperties(
				Mockito.anyString())
		).thenReturn(
			HashMapBuilder.<String, Object>put(
				"ddm.form.field.type.icon", "my-icon"
			).put(
				"ddm.form.field.type.js.class.name", "myJavaScriptClass"
			).put(
				"ddm.form.field.type.js.module", "myJavaScriptModule"
			).build()
		);

		return ddmFormFieldTypeServicesRegistry;
	}

	protected String read(String fileName) throws IOException {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	protected String serialize(DDMFormValues ddmFormValues) {
		DDMFormValuesSerializerSerializeRequest.Builder builder =
			DDMFormValuesSerializerSerializeRequest.Builder.newBuilder(
				ddmFormValues);

		DDMFormValuesSerializerSerializeResponse
			ddmFormValuesSerializerSerializeResponse =
				ddmFormValuesJSONSerializer.serialize(builder.build());

		return ddmFormValuesSerializerSerializeResponse.getContent();
	}

	protected void setUpConfigurationFactoryUtil() {
		Mockito.when(
			_configurationFactory.getConfiguration(
				Mockito.any(ClassLoader.class), Mockito.anyString())
		).thenReturn(
			_configuration
		);

		ReflectionTestUtil.setFieldValue(
			ConfigurationFactoryUtil.class, "_configurationFactory",
			_configurationFactory);
	}

	protected void setUpDDMFormJSONDeserializer() {
		ReflectionTestUtil.setFieldValue(
			ddmFormJSONDeserializer, "_ddmFormFieldTypeServicesRegistry",
			getMockedDDMFormFieldTypeServicesRegistry());
		ReflectionTestUtil.setFieldValue(
			ddmFormJSONDeserializer, "_jsonFactory", jsonFactory);
	}

	protected void setUpDDMFormJSONSerializer() {
		ReflectionTestUtil.setFieldValue(
			ddmFormJSONSerializer, "_ddmFormFieldTypeServicesRegistry",
			getMockedDDMFormFieldTypeServicesRegistry());
		ReflectionTestUtil.setFieldValue(
			ddmFormJSONSerializer, "_jsonFactory", jsonFactory);
	}

	protected void setUpDDMFormLayoutJSONDeserializer() {
		ReflectionTestUtil.setFieldValue(
			ddmFormLayoutJSONDeserializer, "_jsonFactory", jsonFactory);
	}

	protected void setUpDDMFormLayoutJSONSerializer() {
		ReflectionTestUtil.setFieldValue(
			ddmFormLayoutJSONSerializer, "_jsonFactory", jsonFactory);
	}

	protected void setUpDDMFormValuesJSONDeserializer() {
		ReflectionTestUtil.setFieldValue(
			ddmFormValuesJSONDeserializer, "_jsonFactory", jsonFactory);
		ReflectionTestUtil.setFieldValue(
			ddmFormValuesJSONDeserializer, "_serviceTrackerMap",
			ProxyFactory.newDummyInstance(ServiceTrackerMap.class));
	}

	protected void setUpDDMFormValuesJSONSerializer() {
		ReflectionTestUtil.setFieldValue(
			ddmFormValuesJSONSerializer, "_jsonFactory", jsonFactory);
		ReflectionTestUtil.setFieldValue(
			ddmFormValuesJSONSerializer, "_serviceTrackerMap",
			ProxyFactory.newDummyInstance(ServiceTrackerMap.class));
	}

	protected void setUpDDMStructureLocalServiceUtil() throws PortalException {
		DDMStructureLocalService ddmStructureLocalService = Mockito.mock(
			DDMStructureLocalService.class);

		ReflectionTestUtil.setFieldValue(
			DDMStructureLocalServiceUtil.class, "_serviceSnapshot",
			new Snapshot<DDMStructureLocalService>(
				DDMStructureLocalServiceUtil.class,
				DDMStructureLocalService.class) {

				@Override
				public DDMStructureLocalService get() {
					return ddmStructureLocalService;
				}

			});

		Mockito.when(
			ddmStructureLocalService.getStructure(Mockito.anyLong())
		).thenAnswer(
			invocationOnMock -> {
				Object[] args = invocationOnMock.getArguments();

				Long structureId = (Long)args[0];

				return structures.get(structureId);
			}
		);

		Mockito.when(
			ddmStructureLocalService.getStructureDDMForm(
				Mockito.any(DDMStructure.class))
		).thenAnswer(
			invocationOnMock -> {
				Object[] args = invocationOnMock.getArguments();

				DDMStructure structure = (DDMStructure)args[0];

				DDMFormDeserializerDeserializeRequest.Builder builder =
					DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
						structure.getDefinition());

				DDMFormDeserializerDeserializeResponse
					ddmFormDeserializerDeserializeResponse =
						ddmFormJSONDeserializer.deserialize(builder.build());

				return ddmFormDeserializerDeserializeResponse.getDDMForm();
			}
		);
	}

	protected void setUpDDMTemplateLocalServiceUtil() throws PortalException {
		DDMTemplateLocalService ddmTemplateLocalService = Mockito.mock(
			DDMTemplateLocalService.class);

		ReflectionTestUtil.setFieldValue(
			DDMTemplateLocalServiceUtil.class, "_serviceSnapshot",
			new Snapshot<DDMTemplateLocalService>(
				DDMTemplateLocalServiceUtil.class,
				DDMTemplateLocalService.class) {

				@Override
				public DDMTemplateLocalService get() {
					return ddmTemplateLocalService;
				}

			});

		Mockito.when(
			ddmTemplateLocalService.getTemplate(Mockito.anyLong())
		).thenAnswer(
			invocationOnMock -> {
				Object[] args = invocationOnMock.getArguments();

				Long templateId = (Long)args[0];

				return templates.get(templateId);
			}
		);
	}

	protected void setUpDefaultDDMFormFieldType() {
		Mockito.when(
			_defaultDDMFormFieldType.getDDMFormFieldTypeSettings()
		).then(
			(Answer<Class<? extends DDMFormFieldTypeSettings>>)
				invocationOnMock ->
					DDMFormFieldTypeSettingsTestUtil.getSettings()
		);
	}

	protected void setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(jsonFactory);
	}

	protected void setUpLanguageUtil() {
		Set<Locale> availableLocales = SetUtil.fromArray(
			LocaleUtil.BRAZIL, LocaleUtil.SPAIN, LocaleUtil.US);

		whenLanguageGetAvailableLocalesThen(availableLocales);

		whenLanguageGet(LocaleUtil.BRAZIL, "false", "Falso");
		whenLanguageGet(LocaleUtil.BRAZIL, "true", "Verdadeiro");
		whenLanguageGet(LocaleUtil.SPAIN, "latitude", "Latitud");
		whenLanguageGet(LocaleUtil.SPAIN, "longitude", "Longitud");
		whenLanguageGet(LocaleUtil.US, "latitude", "Latitude");
		whenLanguageGet(LocaleUtil.US, "longitude", "Longitude");
		whenLanguageGet(LocaleUtil.US, "false", "False");
		whenLanguageGet(LocaleUtil.US, "true", "True");

		whenLanguageGetLanguageId(LocaleUtil.BRAZIL, "pt_BR");
		whenLanguageGetLanguageId(LocaleUtil.SPAIN, "es_ES");
		whenLanguageGetLanguageId(LocaleUtil.US, "en_US");

		whenLanguageIsAvailableLocale(LocaleUtil.BRAZIL);
		whenLanguageIsAvailableLocale(LocaleUtil.SPAIN);
		whenLanguageIsAvailableLocale(LocaleUtil.US);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(language);

		ReflectionTestUtil.setFieldValue(
			ddmFormValuesJSONDeserializer, "_language", language);
	}

	protected void setUpLanguageUtil(Map<String, String> languageKeys) {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			language.get(Mockito.any(ResourceBundle.class), Mockito.anyString())
		).then(
			(Answer<String>)invocationOnMock -> {
				Object[] arguments = invocationOnMock.getArguments();

				return languageKeys.get((String)arguments[1]);
			}
		);

		languageUtil.setLanguage(language);

		ReflectionTestUtil.setFieldValue(
			ddmFormValuesJSONDeserializer, "_language", language);
	}

	protected void setUpLocalizationUtil() {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(new LocalizationImpl());
	}

	protected void setUpPortalClassLoaderUtil() {
		PortalClassLoaderUtil.setClassLoader(_classLoader);
	}

	protected void setUpPortalUtil() {
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

	protected void setUpResourceBundleUtil() {
		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(LocaleUtil.BRAZIL)
		).thenReturn(
			_resourceBundle
		);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(LocaleUtil.US)
		).thenReturn(
			_resourceBundle
		);
	}

	protected void setUpSAXReaderUtil() {
		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		SAXReaderImpl secureSAXReaderImpl = new SAXReaderImpl();

		secureSAXReaderImpl.setSecure(true);

		saxReaderUtil.setSAXReader(secureSAXReaderImpl);

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());
	}

	protected void whenLanguageGet(
		Locale locale, String key, String returnValue) {

		Mockito.when(
			language.get(Mockito.eq(locale), Mockito.eq(key))
		).thenReturn(
			returnValue
		);
	}

	protected void whenLanguageGetAvailableLocalesThen(
		Set<Locale> availableLocales) {

		Mockito.when(
			language.getAvailableLocales()
		).thenReturn(
			availableLocales
		);
	}

	protected void whenLanguageGetLanguageId(Locale locale, String languageId) {
		Mockito.when(
			language.getLanguageId(Mockito.eq(locale))
		).thenReturn(
			languageId
		);
	}

	protected void whenLanguageIsAvailableLocale(Locale locale) {
		Mockito.when(
			language.isAvailableLocale(Mockito.eq(locale))
		).thenReturn(
			true
		);

		Mockito.when(
			language.isAvailableLocale(
				Mockito.eq(LocaleUtil.toLanguageId(locale)))
		).thenReturn(
			true
		);
	}

	protected static final DDMFormJSONDeserializer ddmFormJSONDeserializer =
		new DDMFormJSONDeserializer();
	protected static final DDMFormJSONSerializer ddmFormJSONSerializer =
		new DDMFormJSONSerializer();
	protected static final DDMFormLayoutJSONDeserializer
		ddmFormLayoutJSONDeserializer = new DDMFormLayoutJSONDeserializer();
	protected static final DDMFormLayoutJSONSerializer
		ddmFormLayoutJSONSerializer = new DDMFormLayoutJSONSerializer();
	protected static final DDMFormValuesDeserializer
		ddmFormValuesJSONDeserializer = new DDMFormValuesJSONDeserializer();
	protected static final DDMFormValuesJSONSerializer
		ddmFormValuesJSONSerializer = new DDMFormValuesJSONSerializer();
	protected static final JSONFactory jsonFactory = new JSONFactoryImpl();

	protected Language language = Mockito.mock(Language.class);
	protected Map<Long, DDMStructure> structures = new HashMap<>();
	protected Map<Long, DDMTemplate> templates = new HashMap<>();

	protected class MockField extends Field {

		public MockField(
			long ddmStructureId, String name, List<Serializable> values,
			Locale locale) {

			super(ddmStructureId, name, values, locale);
		}

		public MockField(
			long ddmStructureId, String name,
			Map<Locale, List<Serializable>> valuesMap, Locale defaultLocale) {

			super(ddmStructureId, name, valuesMap, defaultLocale);
		}

		@Override
		public DDMStructure getDDMStructure() {
			return structures.get(getDDMStructureId());
		}

		private static final long serialVersionUID = 1L;

	}

	private static final DDMFormDeserializer _ddmFormDeserializer =
		new DDMFormJSONDeserializer();
	private static ServiceRegistration<DDMFormDeserializer>
		_ddmFormDeserializerServiceRegistration;
	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);
	private static ServiceRegistration<SiteConnectedGroupGroupProvider>
		_siteConnectedGroupGroupProviderServiceRegistration;

	private final ClassLoader _classLoader = Mockito.mock(ClassLoader.class);
	private final Configuration _configuration = Mockito.mock(
		Configuration.class);
	private final ConfigurationFactory _configurationFactory = Mockito.mock(
		ConfigurationFactory.class);
	private final DDMFormFieldType _defaultDDMFormFieldType = Mockito.mock(
		DDMFormFieldType.class);
	private final ResourceBundle _resourceBundle = Mockito.mock(
		ResourceBundle.class);

}