/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.render;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.internal.render.CheckboxDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.DateDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.DecimalDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.DocumentLibraryDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.GeolocationDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.IntegerDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.LinkToPageDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.SelectDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.TextDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcellus Tavares
 */
public class DDMFormFieldValueRendererTest extends BaseDDMTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setUpDLAppLocalServiceUtil();
		setUpFastDateFormatFactoryUtil();
		setUpJSONFactoryUtil();
		setUpLanguageUtil();
		setUpLayoutServiceUtil();
	}

	@Test
	public void testCheckboxFieldValueRendererWithRepeatableValues() {
		DDMFormValues ddmFormValues = new DDMFormValues(null);

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"Checkbox",
				createLocalizedValue("false", "true", LocaleUtil.US)));

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"Checkbox",
				createLocalizedValue("true", "true", LocaleUtil.US)));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new CheckboxDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormValues.getDDMFormFieldValues(), LocaleUtil.US);

		Assert.assertEquals("False, True", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormValues.getDDMFormFieldValues(), LocaleUtil.BRAZIL);

		Assert.assertEquals("Verdadeiro, Verdadeiro", renderedValue);
	}

	@Test
	public void testCheckboxFieldValueRendererWithoutRepeatableValues() {
		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Checkbox", createLocalizedValue("false", "true", LocaleUtil.US));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new CheckboxDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("False", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.BRAZIL);

		Assert.assertEquals("Verdadeiro", renderedValue);
	}

	@Test
	public void testDateFieldValueRenderer() {
		String valueString = "2014-10-22";

		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Date", new UnlocalizedValue(valueString));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new DateDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("10/22/2014", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.BRAZIL);

		Assert.assertEquals("22/10/2014", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, Locale.forLanguageTag("zh-Hant"));

		Assert.assertEquals("2014/10/22", renderedValue);
	}

	@Test
	public void testDateFieldValueRendererWithEmptyValue() {
		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Date", new UnlocalizedValue(StringPool.BLANK));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new DateDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals(StringPool.BLANK, renderedValue);
	}

	@Test
	public void testDecimalFieldValueRenderer() {
		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Decimal", createLocalizedValue("1.2", "1.2", LocaleUtil.US));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new DecimalDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("1.2", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.BRAZIL);

		Assert.assertEquals("1,2", renderedValue);
	}

	@Test
	public void testDecimalFieldValueRendererWithoutDefaultValue() {
		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Decimal",
			createLocalizedValue(StringPool.BLANK, "1,2", LocaleUtil.US));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new DecimalDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("0", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.BRAZIL);

		Assert.assertEquals("1,2", renderedValue);
	}

	@Test
	public void testDocumentLibraryFieldValueRenderer() {
		JSONObject jsonObject = JSONUtil.put(
			"groupId", RandomTestUtil.randomLong()
		).put(
			"uuid", RandomTestUtil.randomString()
		);

		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"DocumentLibrary", new UnlocalizedValue(jsonObject.toString()));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new DocumentLibraryDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("File Entry Title", renderedValue);
	}

	@Test
	public void testGeolocationFieldValueRenderer() {
		JSONObject jsonObject = JSONUtil.put(
			"latitude", 9.8765
		).put(
			"longitude", 1.2345
		);

		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Geolocation", new UnlocalizedValue(jsonObject.toString()));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new GeolocationDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.SPAIN);

		Assert.assertEquals("Latitud: 9,877, Longitud: 1,234", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("Latitude: 9.877, Longitude: 1.234", renderedValue);
	}

	@Test
	public void testIntegerFieldValueRenderer() {
		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Integer", createLocalizedValue("1", "2", LocaleUtil.US));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new IntegerDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("1", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.BRAZIL);

		Assert.assertEquals("2", renderedValue);
	}

	@Test
	public void testLinkToPageFieldValueRenderer() {
		JSONObject jsonObject = JSONUtil.put(
			"groupId", RandomTestUtil.randomLong()
		).put(
			"layoutId", RandomTestUtil.randomLong()
		).put(
			"privateLayout", RandomTestUtil.randomBoolean()
		);

		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"LinkToPage", new UnlocalizedValue(jsonObject.toString()));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new LinkToPageDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("Layout Name", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.BRAZIL);

		Assert.assertEquals("Nome da Pagina", renderedValue);
	}

	@Test
	public void testNumberFieldValueRenderer() {
		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Number", createLocalizedValue("1", "2.1", LocaleUtil.US));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new IntegerDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("1", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.BRAZIL);

		Assert.assertEquals("2,1", renderedValue);
	}

	@Test(expected = ValueAccessorException.class)
	public void testSelectFieldValueRendererWithInvalidValue()
		throws Exception {

		DDMForm ddmForm = createDDMFormWithSelectField();

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"Select", new UnlocalizedValue("Invalid JSON")));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new SelectDDMFormFieldValueRenderer();

		ddmFormFieldValueRenderer.render(
			ddmFormValues.getDDMFormFieldValues(), LocaleUtil.US);
	}

	@Test
	public void testSelectFieldValueRendererWithRepeatableValues() {
		DDMForm ddmForm = createDDMFormWithSelectField();

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		JSONArray jsonArray = toJSONArray("Option 1", "Option 2");

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"Select", new UnlocalizedValue(jsonArray.toString())));

		jsonArray = toJSONArray("Option 1");

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"Select", new UnlocalizedValue(jsonArray.toString())));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new SelectDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormValues.getDDMFormFieldValues(), LocaleUtil.US);

		Assert.assertEquals(
			"English Label 1, English Label 2, English Label 1", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormValues.getDDMFormFieldValues(), LocaleUtil.BRAZIL);

		Assert.assertEquals(
			"Portuguese Label 1, Portuguese Label 2, Portuguese Label 1",
			renderedValue);
	}

	@Test
	public void testSelectFieldValueRendererWithoutRepeatableValues() {
		DDMForm ddmForm = createDDMFormWithSelectField();

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		JSONArray jsonArray = toJSONArray("Option 1", "Option 2");

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"Select", new UnlocalizedValue(jsonArray.toString())));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new SelectDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormValues.getDDMFormFieldValues(), LocaleUtil.US);

		Assert.assertEquals("English Label 1, English Label 2", renderedValue);

		renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormValues.getDDMFormFieldValues(), LocaleUtil.BRAZIL);

		Assert.assertEquals(
			"Portuguese Label 1, Portuguese Label 2", renderedValue);
	}

	@Test
	public void testTextFieldValueRendererWithRepeatableValues() {
		DDMFormValues ddmFormValues = new DDMFormValues(null);

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"Text", new UnlocalizedValue("Charlie Parker")));

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"Text", new UnlocalizedValue("Dave Brubeck")));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new TextDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormValues.getDDMFormFieldValues(), LocaleUtil.US);

		Assert.assertEquals("Charlie Parker, Dave Brubeck", renderedValue);
	}

	@Test
	public void testTextFieldValueRendererWithoutRepeatableValues() {
		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
			"Text", new UnlocalizedValue("Scott Joplin"));

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			new TextDDMFormFieldValueRenderer();

		String renderedValue = ddmFormFieldValueRenderer.render(
			ddmFormFieldValue, LocaleUtil.US);

		Assert.assertEquals("Scott Joplin", renderedValue);
	}

	protected DDMForm createDDMFormWithSelectField() {
		DDMForm ddmForm = createDDMForm(
			createAvailableLocales(LocaleUtil.BRAZIL, LocaleUtil.US),
			LocaleUtil.US);

		DDMFormField ddmFormField = new DDMFormField(
			"Select", DDMFormFieldType.SELECT);

		ddmFormField.setDataType("string");
		ddmFormField.setRepeatable(true);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel(
			"Option 1", LocaleUtil.US, "English Label 1");
		ddmFormFieldOptions.addOptionLabel(
			"Option 2", LocaleUtil.US, "English Label 2");

		ddmFormFieldOptions.addOptionLabel(
			"Option 1", LocaleUtil.BRAZIL, "Portuguese Label 1");
		ddmFormFieldOptions.addOptionLabel(
			"Option 2", LocaleUtil.BRAZIL, "Portuguese Label 2");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmForm.addDDMFormField(ddmFormField);

		return ddmForm;
	}

	protected void setUpDLAppLocalServiceUtil() throws PortalException {
		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry.getTitle()
		).thenReturn(
			"File Entry Title"
		);

		DLAppLocalService dlAppLocalService = Mockito.mock(
			DLAppLocalService.class);

		ReflectionTestUtil.setFieldValue(
			DLAppLocalServiceUtil.class, "_service", dlAppLocalService);

		Mockito.when(
			dlAppLocalService.getFileEntryByUuidAndGroupId(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			fileEntry
		);
	}

	protected void setUpFastDateFormatFactoryUtil() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());
	}

	protected void setUpLayoutServiceUtil() throws Exception {
		LayoutService layoutService = Mockito.mock(LayoutService.class);

		ReflectionTestUtil.setFieldValue(
			LayoutServiceUtil.class, "_service", layoutService);
		Mockito.when(
			layoutService.getLayoutName(
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong(),
				Mockito.eq("en_US"))
		).thenReturn(
			"Layout Name"
		);

		Mockito.when(
			layoutService.getLayoutName(
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong(),
				Mockito.eq("pt_BR"))
		).thenReturn(
			"Nome da Pagina"
		);
	}

	protected JSONArray toJSONArray(String... values) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (String value : values) {
			jsonArray.put(value);
		}

		return jsonArray;
	}

}