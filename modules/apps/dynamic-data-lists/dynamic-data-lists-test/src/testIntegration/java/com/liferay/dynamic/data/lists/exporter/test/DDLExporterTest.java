/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.dynamic.data.lists.exporter.DDLExporter;
import com.liferay.dynamic.data.lists.exporter.DDLExporterFactory;
import com.liferay.dynamic.data.lists.helper.DDLRecordSetTestHelper;
import com.liferay.dynamic.data.lists.helper.DDLRecordTestHelper;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Renato Rego
 */
@RunWith(Arquillian.class)
public class DDLExporterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_availableLocales = DDMFormTestUtil.createAvailableLocales(
			LocaleUtil.US);
		_group = GroupTestUtil.addGroup();

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		setUpDDMFormFieldDataTypes();
		setUpDDMFormFieldValues();
		setUpPermissionChecker();
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testCSVExport() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			_availableLocales, _defaultLocale);

		createDDMFormFields(ddmForm);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, _availableLocales, _defaultLocale);

		createDDMFormFieldValues(ddmFormValues);

		DDLRecordSetTestHelper recordSetTestHelper = new DDLRecordSetTestHelper(
			_group);

		DDLRecordSet recordSet = recordSetTestHelper.addRecordSet(ddmForm);

		DDLRecordTestHelper recordTestHelper = new DDLRecordTestHelper(
			_group, recordSet);

		DDLRecord record = recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		DDLRecordVersion recordVersion = record.getRecordVersion();

		DDLExporter ddlExporter = _ddlExporterFactory.getDDLExporter("csv");

		byte[] bytes = ddlExporter.export(recordSet.getRecordSetId());

		try (ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(bytes);

			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(byteArrayInputStream))) {

			String header = bufferedReader.readLine();

			Assert.assertEquals(
				"Field0,Field1,Field2,Field3,Field4,Field5,Field6,Field7," +
					"Field8,Field9,Field10,Field11,Field12,Status," +
						"Modified Date,Author",
				header);

			String data = bufferedReader.readLine();

			Assert.assertEquals(
				StringBundler.concat(
					"False,1/1/1970,1,file.txt,\"Latitude: -8.035, Longitude: ",
					"-34.918\",2,Link to Page content,3,Option 1,Option 1,",
					"Text content,Text Area content,Text HTML content,",
					"Approved,", formatDate(recordVersion.getStatusDate()),
					CharPool.COMMA, recordVersion.getUserName()),
				data);
		}
	}

	@Test
	public void testExportRecordsWithDistinctFields() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			_availableLocales, _defaultLocale);

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"field0", false, false, false));
		ddmForm.addDDMFormField(
			createDDMFormField("field1", "radio", "string"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, _availableLocales, _defaultLocale);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0", new UnlocalizedValue("text0")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1", createDDMFormFieldValue("Value 1")));

		DDLRecordSetTestHelper recordSetTestHelper = new DDLRecordSetTestHelper(
			_group);

		DDLRecordSet recordSet = recordSetTestHelper.addRecordSet(ddmForm);

		DDLRecordTestHelper recordTestHelper = new DDLRecordTestHelper(
			_group, recordSet);

		DDLRecord record0 = recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		DDLRecordVersion recordVersion0 = record0.getRecordVersion();

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		ddmFormFields.clear();

		ddmForm.setDDMFormFields(ddmFormFields);

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"field2", false, false, false));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"field3", false, false, false));

		ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, _availableLocales, _defaultLocale);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2", new UnlocalizedValue("text1")));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field3", new UnlocalizedValue("text2")));

		DDMStructure ddmStructure = recordSet.getDDMStructure();

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(DDLRecordSet.class), _group);

		ddmStructure = ddmStructureTestHelper.updateStructure(
			ddmStructure.getStructureId(), ddmForm);

		recordSet = recordSetTestHelper.updateRecordSet(
			recordSet.getRecordSetId(), ddmStructure);

		recordTestHelper = new DDLRecordTestHelper(_group, recordSet);

		DDLRecord record1 = recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		DDLRecordVersion recordVersion1 = record1.getRecordVersion();

		DDLExporter ddlExporter = _ddlExporterFactory.getDDLExporter("csv");

		byte[] bytes = ddlExporter.export(recordSet.getRecordSetId());

		try (ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(bytes);

			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(byteArrayInputStream))) {

			String header = bufferedReader.readLine();

			Assert.assertEquals(
				"field0,field1,field2,field3,Status,Modified Date,Author",
				header);

			String row2 = bufferedReader.readLine();

			Assert.assertEquals(
				StringBundler.concat(
					",,text1,text2,Approved,",
					formatDate(recordVersion1.getStatusDate()), CharPool.COMMA,
					recordVersion1.getUserName()),
				row2);

			String row1 = bufferedReader.readLine();

			Assert.assertEquals(
				StringBundler.concat(
					"text0,Option 1,,,Approved,",
					formatDate(recordVersion0.getStatusDate()), CharPool.COMMA,
					recordVersion0.getUserName()),
				row1);
		}
	}

	@Test
	public void testExportWithSpecialCharacters() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			_availableLocales, _defaultLocale);

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"field0", false, false, false));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, _availableLocales, _defaultLocale);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0", new UnlocalizedValue("I'm \"good\"")));

		DDLRecordSetTestHelper recordSetTestHelper = new DDLRecordSetTestHelper(
			_group);

		DDLRecordSet recordSet = recordSetTestHelper.addRecordSet(ddmForm);

		DDLRecordTestHelper recordTestHelper = new DDLRecordTestHelper(
			_group, recordSet);

		DDLRecord record0 = recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		DDLRecordVersion recordVersion0 = record0.getRecordVersion();

		DDLExporter ddlExporter = _ddlExporterFactory.getDDLExporter("csv");

		byte[] bytes = ddlExporter.export(recordSet.getRecordSetId());

		try (ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(bytes);

			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(byteArrayInputStream))) {

			String header = bufferedReader.readLine();

			Assert.assertEquals("field0,Status,Modified Date,Author", header);

			String row0 = bufferedReader.readLine();

			Assert.assertEquals(
				StringBundler.concat(
					"\"I'm \"\"good\"\"\",Approved,",
					formatDate(recordVersion0.getStatusDate()), CharPool.COMMA,
					recordVersion0.getUserName()),
				row0);
		}
	}

	@Test
	public void testXLSExport() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			_availableLocales, _defaultLocale);

		createDDMFormFields(ddmForm);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, _availableLocales, _defaultLocale);

		createDDMFormFieldValues(ddmFormValues);

		DDLRecordSetTestHelper recordSetTestHelper = new DDLRecordSetTestHelper(
			_group);

		DDLRecordSet recordSet = recordSetTestHelper.addRecordSet(ddmForm);

		DDLRecordTestHelper recordTestHelper = new DDLRecordTestHelper(
			_group, recordSet);

		DDLRecord record = recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		DDLRecordVersion recordVersion = record.getRecordVersion();

		DDLExporter ddlExporter = _ddlExporterFactory.getDDLExporter("xls");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.apache.poi.POIDocument", LoggerTestUtil.WARN)) {

			ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(
					ddlExporter.export(recordSet.getRecordSetId()));

			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(byteArrayInputStream);

			Sheet sheet = hssfWorkbook.getSheetAt(0);

			Row row = sheet.getRow(0);

			Cell cell = null;

			for (int i = 0; i < 13; i++) {
				cell = row.getCell(i);

				Assert.assertEquals("Field" + i, cell.getStringCellValue());
			}

			row = sheet.getRow(1);

			cell = row.getCell(0);

			Assert.assertEquals("False", cell.getStringCellValue());

			cell = row.getCell(1);

			Assert.assertEquals("1/1/1970", cell.getStringCellValue());

			cell = row.getCell(2);

			Assert.assertEquals("1", cell.getStringCellValue());

			cell = row.getCell(3);

			Assert.assertEquals("file.txt", cell.getStringCellValue());

			cell = row.getCell(4);

			Assert.assertEquals(
				"Latitude: -8.035, Longitude: -34.918",
				cell.getStringCellValue());

			cell = row.getCell(5);

			Assert.assertEquals("2", cell.getStringCellValue());

			cell = row.getCell(6);

			Assert.assertEquals(
				"Link to Page content", cell.getStringCellValue());

			cell = row.getCell(7);

			Assert.assertEquals("3", cell.getStringCellValue());

			cell = row.getCell(8);

			Assert.assertEquals("Option 1", cell.getStringCellValue());

			cell = row.getCell(9);

			Assert.assertEquals("Option 1", cell.getStringCellValue());

			cell = row.getCell(10);

			Assert.assertEquals("Text content", cell.getStringCellValue());

			cell = row.getCell(11);

			Assert.assertEquals("Text Area content", cell.getStringCellValue());

			cell = row.getCell(12);

			Assert.assertEquals("Text HTML content", cell.getStringCellValue());

			cell = row.getCell(13);

			Assert.assertEquals("Approved", cell.getStringCellValue());

			cell = row.getCell(14);

			Assert.assertEquals(
				formatDate(recordVersion.getStatusDate()),
				cell.getStringCellValue());

			cell = row.getCell(15);

			Assert.assertEquals(
				recordVersion.getUserName(), cell.getStringCellValue());
		}
	}

	@Test
	public void testXMLExport() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			_availableLocales, _defaultLocale);

		createDDMFormFields(ddmForm);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, _availableLocales, _defaultLocale);

		createDDMFormFieldValues(ddmFormValues);

		DDLRecordSetTestHelper recordSetTestHelper = new DDLRecordSetTestHelper(
			_group);

		DDLRecordSet recordSet = recordSetTestHelper.addRecordSet(ddmForm);

		DDLRecordTestHelper recordTestHelper = new DDLRecordTestHelper(
			_group, recordSet);

		DDLRecord record = recordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);

		DDLRecordVersion recordVersion = record.getRecordVersion();

		DDLExporter ddlExporter = _ddlExporterFactory.getDDLExporter("xml");

		byte[] bytes = ddlExporter.export(recordSet.getRecordSetId());

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		Element fieldsElement = rootElement.addElement("fields");

		addFieldElement(fieldsElement, "Field0", "False");
		addFieldElement(fieldsElement, "Field1", "1/1/1970");
		addFieldElement(fieldsElement, "Field2", "1");
		addFieldElement(fieldsElement, "Field3", "file.txt");
		addFieldElement(
			fieldsElement, "Field4", "Latitude: -8.035, Longitude: -34.918");
		addFieldElement(fieldsElement, "Field5", "2");
		addFieldElement(fieldsElement, "Field6", "Link to Page content");
		addFieldElement(fieldsElement, "Field7", "3");
		addFieldElement(fieldsElement, "Field8", "Option 1");
		addFieldElement(fieldsElement, "Field9", "Option 1");
		addFieldElement(fieldsElement, "Field10", "Text content");
		addFieldElement(fieldsElement, "Field11", "Text Area content");
		addFieldElement(fieldsElement, "Field12", "Text HTML content");
		addFieldElement(fieldsElement, "Status", "Approved");
		addFieldElement(
			fieldsElement, "Modified Date",
			formatDate(recordVersion.getStatusDate()));
		addFieldElement(fieldsElement, "Author", recordVersion.getUserName());

		String xml = document.asXML();

		Assert.assertEquals(new String(bytes), xml);
	}

	protected void addFieldElement(
		Element fieldsElement, String label, Serializable value) {

		Element fieldElement = fieldsElement.addElement("field");

		Element labelElement = fieldElement.addElement("label");

		labelElement.addText(label);

		Element valueElement = fieldElement.addElement("value");

		valueElement.addText(String.valueOf(value));
	}

	protected DDMFormField createDDMFormField(
		String name, String type, String dataType) {

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			name, name, type, dataType, true, false, false);

		if (type.equals("radio") || type.equals("select")) {
			setDDMFormFieldOptions(ddmFormField, 3);
		}

		return ddmFormField;
	}

	protected Value createDDMFormFieldValue(String valueString) {
		Value value = new LocalizedValue(_defaultLocale);

		value.addString(_defaultLocale, valueString);

		return value;
	}

	protected void createDDMFormFieldValues(DDMFormValues ddmFormValues) {
		for (DDMFormFieldType type : DDMFormFieldType.values()) {
			String fieldName = "Field" + type.ordinal();

			Value fieldValue = createDDMFormFieldValue(_fieldValues.get(type));

			ddmFormValues.addDDMFormFieldValue(
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					fieldName, fieldValue));
		}
	}

	protected void createDDMFormFields(DDMForm ddmForm) {
		for (DDMFormFieldType ddmFormFieldType : DDMFormFieldType.values()) {
			String fieldName = "Field" + ddmFormFieldType.ordinal();

			ddmForm.addDDMFormField(
				createDDMFormField(
					fieldName, ddmFormFieldType.getValue(),
					_ddmFormFieldDataTypes.get(ddmFormFieldType)));
		}
	}

	protected String createDocumentLibraryDDMFormFieldValue() throws Exception {
		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "file.txt",
			ContentTypes.TEXT_PLAIN, TestDataConstants.TEST_BYTE_ARRAY, null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		return JSONUtil.put(
			"groupId", fileEntry.getGroupId()
		).put(
			"name", fileEntry.getTitle()
		).put(
			"tempFile", "false"
		).put(
			"title", fileEntry.getTitle()
		).put(
			"uuid", fileEntry.getUuid()
		).toString();
	}

	protected String createGeolocationDDMFormFieldValue() throws Exception {
		return JSONUtil.put(
			"latitude", "-8.035"
		).put(
			"longitude", "-34.918"
		).toString();
	}

	protected String createLinkToPageDDMFormFieldValue() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), "Link to Page content", false);

		return JSONUtil.put(
			"groupId", layout.getGroupId()
		).put(
			"layoutId", layout.getLayoutId()
		).put(
			"privateLayout", layout.isPrivateLayout()
		).toString();
	}

	protected String createListDDMFormFieldValue() throws Exception {
		JSONArray jsonArray = JSONUtil.put("Value 1");

		return jsonArray.toString();
	}

	protected String formatDate(Date date) {
		DateTimeFormatter dateTimeFormatter =
			DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

		dateTimeFormatter = dateTimeFormatter.withLocale(_defaultLocale);

		LocalDateTime localDateTime = LocalDateTime.ofInstant(
			date.toInstant(), ZoneId.systemDefault());

		return dateTimeFormatter.format(localDateTime);
	}

	protected void setDDMFormFieldOptions(
		DDMFormField ddmFormField, int availableOptions) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		for (int i = 1; i <= availableOptions; i++) {
			ddmFormFieldOptions.addOptionLabel(
				"Value " + i, LocaleUtil.US, "Option " + i);
		}

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);
	}

	protected Map<DDMFormFieldType, String> setUpDDMFormFieldDataTypes() {
		_ddmFormFieldDataTypes = HashMapBuilder.<DDMFormFieldType, String>put(
			DDMFormFieldType.CHECKBOX, "boolean"
		).put(
			DDMFormFieldType.DATE, "date"
		).put(
			DDMFormFieldType.DECIMAL, "double"
		).put(
			DDMFormFieldType.DOCUMENT_LIBRARY, "document-library"
		).put(
			DDMFormFieldType.GEOLOCATION, "geolocation"
		).put(
			DDMFormFieldType.INTEGER, "integer"
		).put(
			DDMFormFieldType.LINK_TO_PAGE, "link-to-page"
		).put(
			DDMFormFieldType.NUMBER, "number"
		).put(
			DDMFormFieldType.RADIO, "string"
		).put(
			DDMFormFieldType.SELECT, "string"
		).put(
			DDMFormFieldType.TEXT, "string"
		).put(
			DDMFormFieldType.TEXT_AREA, "string"
		).put(
			DDMFormFieldType.TEXT_HTML, "html"
		).build();

		return _ddmFormFieldDataTypes;
	}

	protected Map<DDMFormFieldType, String> setUpDDMFormFieldValues()
		throws Exception {

		_fieldValues = HashMapBuilder.<DDMFormFieldType, String>put(
			DDMFormFieldType.CHECKBOX, "false"
		).put(
			DDMFormFieldType.DATE, "1970-01-01"
		).put(
			DDMFormFieldType.DECIMAL, "1.0"
		).put(
			DDMFormFieldType.DOCUMENT_LIBRARY,
			createDocumentLibraryDDMFormFieldValue()
		).put(
			DDMFormFieldType.GEOLOCATION, createGeolocationDDMFormFieldValue()
		).put(
			DDMFormFieldType.INTEGER, "2"
		).put(
			DDMFormFieldType.LINK_TO_PAGE, createLinkToPageDDMFormFieldValue()
		).put(
			DDMFormFieldType.NUMBER, "3"
		).put(
			DDMFormFieldType.RADIO, "Value 1"
		).put(
			DDMFormFieldType.SELECT, createListDDMFormFieldValue()
		).put(
			DDMFormFieldType.TEXT, "Text content"
		).put(
			DDMFormFieldType.TEXT_AREA, "Text Area content"
		).put(
			DDMFormFieldType.TEXT_HTML, "Text HTML content"
		).build();

		return _fieldValues;
	}

	protected void setUpPermissionChecker() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean hasOwnerPermission(
					long companyId, String name, String primKey, long ownerId,
					String actionId) {

					return true;
				}

			});
	}

	private Set<Locale> _availableLocales;

	@Inject
	private DDLExporterFactory _ddlExporterFactory;

	private Map<DDMFormFieldType, String> _ddmFormFieldDataTypes;
	private final Locale _defaultLocale = LocaleUtil.US;
	private Map<DDMFormFieldType, String> _fieldValues;

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _originalPermissionChecker;

	private enum DDMFormFieldType {

		CHECKBOX("checkbox"), DATE("ddm-date"), DECIMAL("ddm-decimal"),
		DOCUMENT_LIBRARY("ddm-documentlibrary"), GEOLOCATION("ddm-geolocation"),
		INTEGER("ddm-integer"), LINK_TO_PAGE("ddm-link-to-page"),
		NUMBER("ddm-number"), RADIO("radio"), SELECT("select"), TEXT("text"),
		TEXT_AREA("textarea"), TEXT_HTML("ddm-text-html");

		public String getValue() {
			return _value;
		}

		private DDMFormFieldType(String value) {
			_value = value;
		}

		private final String _value;

	}

}