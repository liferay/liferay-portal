/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.DDMFormValuesReader;
import com.liferay.document.library.asset.DLFileEntryDDMFormValuesReader;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMForm;
import com.liferay.dynamic.data.mapping.kernel.DDMFormField;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.LocalizedValue;
import com.liferay.dynamic.data.mapping.kernel.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcellus Tavares
 */
@RunWith(Arquillian.class)
public class DLFileEntryDDMFormValuesReaderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		setUpGroup();
		setUpFileEntry();
	}

	@Test
	public void testGetDDMFormValues() throws Exception {
		DDMFormValuesReader ddmFormValuesReader =
			new DLFileEntryDDMFormValuesReader(
				_fileEntry, _fileEntry.getFileVersion());

		Assert.assertEquals(
			getExpectedDDMFormValues(), ddmFormValuesReader.getDDMFormValues());
	}

	protected DLFileEntryType addDLFileEntryType(ServiceContext serviceContext)
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DLFileEntryMetadata.class.getName());

		return DLFileEntryTypeLocalServiceUtil.addFileEntryType(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			ddmStructure.getStructureId(), null,
			Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
			Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			serviceContext);
	}

	protected DLFileEntry addFileEntry() throws Exception {
		ServiceContext serviceContext = getServiceContext();

		DLFileEntryType dlFileEntryType = addDLFileEntryType(serviceContext);

		serviceContext.setAttribute(
			"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		Map<String, DDMFormValues> ddmFormValuesMap = createDDMFormValuesMap(
			ddmStructures.get(0));

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			TestDataConstants.TEST_BYTE_ARRAY);

		return DLFileEntryLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null,
			dlFileEntryType.getFileEntryTypeId(), ddmFormValuesMap, null,
			byteArrayInputStream, byteArrayInputStream.available(), null, null,
			null, serviceContext);
	}

	protected DDMForm createDDMForm() {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addAvailableLocale(LocaleUtil.US);
		ddmForm.setDefaultLocale(LocaleUtil.US);

		ddmForm.addDDMFormField(createTextDDMFormField("Text1"));
		ddmForm.addDDMFormField(createTextDDMFormField("Text2"));

		return ddmForm;
	}

	protected DDMFormFieldValue createDDMFormFieldValue(
		String instanceId, String name, String value) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue(instanceId);

		ddmFormFieldValue.setName(name);
		ddmFormFieldValue.setValue(new UnlocalizedValue(value));

		return ddmFormFieldValue;
	}

	protected DDMFormValues createDDMFormValues(DDMForm ddmForm)
		throws Exception {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addAvailableLocale(LocaleUtil.US);
		ddmFormValues.setDefaultLocale(LocaleUtil.US);

		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue("baga", "Text1", "Text 1 Value"));
		ddmFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue("hagt", "Text2", "Text 2 Value"));

		return ddmFormValues;
	}

	protected Map<String, DDMFormValues> createDDMFormValuesMap(
			DDMStructure ddmStructure)
		throws Exception {

		return HashMapBuilder.<String, DDMFormValues>put(
			ddmStructure.getStructureKey(), createDDMFormValues(createDDMForm())
		).build();
	}

	protected DDMFormField createTextDDMFormField(String name) {
		DDMFormField ddmFormField = new DDMFormField(name, "text");

		ddmFormField.setDataType("string");

		LocalizedValue label = new LocalizedValue(LocaleUtil.US);

		label.addString(LocaleUtil.US, name);

		ddmFormField.setLabel(label);

		ddmFormField.setLocalizable(false);

		return ddmFormField;
	}

	protected DDMFormValues getExpectedDDMFormValues() throws Exception {
		return createDDMFormValues(null);
	}

	protected ServiceContext getServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		User user = TestPropsValues.getUser();

		serviceContext.setLanguageId(LocaleUtil.toLanguageId(user.getLocale()));

		return serviceContext;
	}

	protected void setUpFileEntry() throws Exception, PortalException {
		DLFileEntry dlFileEntry = addFileEntry();

		_fileEntry = DLAppLocalServiceUtil.getFileEntry(
			dlFileEntry.getFileEntryId());
	}

	protected void setUpGroup() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

}