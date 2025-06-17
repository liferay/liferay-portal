/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class DLFileEntryTypeServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddFileEntryTypeWithEmptyDDMForm() throws Exception {
		int fileEntryTypesCount =
			_dlFileEntryTypeService.getFileEntryTypesCount(
				new long[] {_group.getGroupId()});

		_addFileEntryType(null);

		Assert.assertEquals(
			fileEntryTypesCount + 1,
			_dlFileEntryTypeService.getFileEntryTypesCount(
				new long[] {_group.getGroupId()}));
	}

	@Test
	public void testAddFileEntryTypeWithEmptyOrNullExternalReferenceCode()
		throws Exception {

		DLFileEntryType dlFileEntryType = _addFileEntryType(null);

		Assert.assertNotNull(dlFileEntryType);
		Assert.assertNotNull(dlFileEntryType.getExternalReferenceCode());

		dlFileEntryType = _addFileEntryType(StringPool.BLANK);

		Assert.assertNotNull(dlFileEntryType);
		Assert.assertNotNull(dlFileEntryType.getExternalReferenceCode());
	}

	@Test
	public void testAddFileEntryTypeWithNonemptyDDMForm() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		byte[] testFileBytes = _file.getBytes(getClass(), _TEST_DDM_STRUCTURE);

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				new String(testFileBytes));

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_ddmFormDeserializer.deserialize(builder.build());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			DLFileEntryMetadata.class.getName(),
			ddmFormDeserializerDeserializeResponse.getDDMForm());

		User user = TestPropsValues.getUser();

		serviceContext.setLanguageId(LocaleUtil.toLanguageId(user.getLocale()));

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				ddmStructure.getStructureId(), null,
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		Assert.assertEquals(ddmStructures.toString(), 1, ddmStructures.size());

		ddmStructure = ddmStructures.get(0);

		Locale[] availableLocales = LocaleUtil.fromLanguageIds(
			ddmStructure.getAvailableLanguageIds());

		boolean hasDefaultLocale = ArrayUtil.contains(
			availableLocales, LocaleUtil.getSiteDefault());

		Assert.assertTrue(hasDefaultLocale);

		boolean hasHungarianLocale = ArrayUtil.contains(
			availableLocales, LocaleUtil.HUNGARY);

		Assert.assertTrue(hasHungarianLocale);

		boolean hasUserLocale = ArrayUtil.contains(
			availableLocales, user.getLocale());

		Assert.assertTrue(hasUserLocale);

		_dlFileEntryTypeLocalService.deleteFileEntryType(dlFileEntryType);
	}

	@Test
	public void testDeleteFileEntryTypeByExternalReferenceCode()
		throws Exception {

		_addFileEntryType("12345678");

		Assert.assertNotNull(
			_dlFileEntryTypeService.fetchFileEntryTypeByExternalReferenceCode(
				"12345678", _group.getGroupId()));

		SearchContext searchContext = SearchContextTestUtil.getSearchContext();

		searchContext.setEntryClassNames(
			new String[] {DLFileEntryType.class.getName()});
		searchContext.setGroupIds(new long[] {_group.getGroupId()});

		Assert.assertEquals(1, _indexer.searchCount(searchContext));

		_dlFileEntryTypeService.deleteFileEntryTypeByExternalReferenceCode(
			"12345678", _group.getGroupId());

		Assert.assertNull(
			_dlFileEntryTypeService.fetchFileEntryTypeByExternalReferenceCode(
				"12345678", _group.getGroupId()));

		Assert.assertEquals(0, _indexer.searchCount(searchContext));
	}

	@Test
	public void testFetchFileEntryTypeByExternalReferenceCode()
		throws Exception {

		Assert.assertNull(
			_dlFileEntryTypeService.fetchFileEntryTypeByExternalReferenceCode(
				"12345678", _group.getGroupId()));

		_addFileEntryType("12345678");

		Assert.assertNotNull(
			_dlFileEntryTypeService.fetchFileEntryTypeByExternalReferenceCode(
				"12345678", _group.getGroupId()));
	}

	@Test
	public void testFileEntryTypeRestrictions() throws Exception {
		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Folder A",
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Folder subfolder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			folder.getFolderId(), "SubFolder AA", StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_basicDocumentDLFileEntryType =
			_dlFileEntryTypeLocalService.getFileEntryType(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		DLFileEntryType dlFileEntryType1 = _addFileEntryType(null);
		DLFileEntryType dlFileEntryType2 = _addFileEntryType(null);

		_dlAppLocalService.updateFolder(
			folder.getFolderId(), folder.getParentFolderId(), folder.getName(),
			folder.getDescription(),
			_getFolderServiceContext(dlFileEntryType1, dlFileEntryType2));

		String name = "Test.txt";
		byte[] bytes = _CONTENT.getBytes();

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, _group.getGroupId(), folder.getFolderId(), name,
			ContentTypes.TEXT_PLAIN, name, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, bytes, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		assertFileEntryType(fileEntry, dlFileEntryType1);

		fileEntry = _dlAppService.addFileEntry(
			null, _group.getGroupId(), subfolder.getFolderId(), name,
			ContentTypes.TEXT_PLAIN, name, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, bytes, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		assertFileEntryType(fileEntry, dlFileEntryType1);

		_dlAppLocalService.updateFolder(
			subfolder.getFolderId(), subfolder.getParentFolderId(),
			subfolder.getName(), subfolder.getDescription(),
			_getFolderServiceContext(_basicDocumentDLFileEntryType));

		assertFileEntryType(
			_dlAppService.getFileEntry(fileEntry.getFileEntryId()),
			_basicDocumentDLFileEntryType);
	}

	@Test(expected = PortalException.class)
	public void testGetFileEntryTypeByExternalReferenceCode() throws Exception {
		_addFileEntryType("12345678");

		Assert.assertNotNull(
			_dlFileEntryTypeService.getFileEntryTypeByExternalReferenceCode(
				"12345678", _group.getGroupId()));

		_dlFileEntryTypeService.deleteFileEntryTypeByExternalReferenceCode(
			"12345678", _group.getGroupId());

		_dlFileEntryTypeService.getFileEntryTypeByExternalReferenceCode(
			"12345678", _group.getGroupId());
	}

	@Test
	public void testLocalizedSiteAddFileEntryType() throws Exception {
		Locale locale = LocaleThreadLocal.getSiteDefaultLocale();

		try {
			LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.SPAIN);

			String name = RandomTestUtil.randomString();
			String description = RandomTestUtil.randomString();
			DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
				DLFileEntryMetadata.class.getName(),
				new Locale[] {LocaleUtil.SPAIN}, LocaleUtil.SPAIN);

			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.addFileEntryType(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					ddmStructure.getStructureId(), null,
					Collections.singletonMap(LocaleUtil.US, name),
					Collections.singletonMap(LocaleUtil.US, description),
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			Assert.assertEquals(
				name, dlFileEntryType.getName(LocaleUtil.US, true));
			Assert.assertEquals(
				description,
				dlFileEntryType.getDescription(LocaleUtil.US, true));
		}
		finally {
			LocaleThreadLocal.setSiteDefaultLocale(locale);
		}
	}

	@Test
	public void testLocalizedSiteUpdateFileEntryType() throws Exception {
		Locale locale = LocaleThreadLocal.getSiteDefaultLocale();

		try {
			LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.SPAIN);

			String name = RandomTestUtil.randomString();
			String description = RandomTestUtil.randomString();
			DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
				DLFileEntryMetadata.class.getName(),
				new Locale[] {LocaleUtil.SPAIN}, LocaleUtil.SPAIN);

			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.addFileEntryType(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					ddmStructure.getStructureId(), null,
					Collections.singletonMap(LocaleUtil.US, name),
					Collections.singletonMap(LocaleUtil.US, description),
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			name = RandomTestUtil.randomString();
			description = RandomTestUtil.randomString();

			_dlFileEntryTypeLocalService.updateFileEntryType(
				dlFileEntryType.getFileEntryTypeId(),
				Collections.singletonMap(LocaleUtil.US, name),
				Collections.singletonMap(LocaleUtil.US, description));

			dlFileEntryType = _dlFileEntryTypeLocalService.getFileEntryType(
				dlFileEntryType.getFileEntryTypeId());

			Assert.assertEquals(
				name, dlFileEntryType.getName(LocaleUtil.US, true));
			Assert.assertEquals(
				description,
				dlFileEntryType.getDescription(LocaleUtil.US, true));
		}
		finally {
			LocaleThreadLocal.setSiteDefaultLocale(locale);
		}
	}

	@Test
	public void testUpdateFileEntryTypeWithEmptyDDMForm() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(new DDMFormField("text", "text"));
		ddmForm.setAvailableLocales(
			Collections.singleton(LocaleUtil.getDefault()));
		ddmForm.setDefaultLocale(LocaleUtil.getDefault());

		DLFileEntryType dlFileEntryType = _addFileEntryType(null);

		_dlFileEntryTypeService.updateFileEntryType(
			dlFileEntryType.getFileEntryTypeId(),
			Collections.singletonMap(LocaleUtil.US, StringUtil.randomString()),
			Collections.singletonMap(LocaleUtil.US, StringUtil.randomString()));

		dlFileEntryType = _dlFileEntryTypeService.getFileEntryType(
			dlFileEntryType.getFileEntryTypeId());

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		Assert.assertEquals(ddmStructures.toString(), 1, ddmStructures.size());
	}

	protected void assertFileEntryType(
		FileEntry fileEntry, DLFileEntryType dlFileEntryType) {

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		Assert.assertEquals(
			"File should be of file entry type " +
				dlFileEntryType.getFileEntryTypeId(),
			dlFileEntryType.getPrimaryKey(), dlFileEntry.getFileEntryTypeId());
	}

	private DLFileEntryType _addFileEntryType(String externalReferenceCode)
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DLFileEntryMetadata.class.getName());

		return _dlFileEntryTypeService.addFileEntryType(
			externalReferenceCode, _group.getGroupId(),
			ddmStructure.getStructureId(), null,
			Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
			Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private ServiceContext _getFolderServiceContext(
			DLFileEntryType... dlFileEntryTypes)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute(
			"defaultFileEntryTypeId", dlFileEntryTypes[0].getPrimaryKey());
		serviceContext.setAttribute(
			"dlFileEntryTypesSearchContainerPrimaryKeys",
			ArrayUtil.toString(dlFileEntryTypes, "primaryKey"));
		serviceContext.setAttribute(
			"restrictionType",
			DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW);

		return serviceContext;
	}

	private static final String _CONTENT =
		"Content: Enterprise. Open Source. For Life.";

	private static final String _TEST_DDM_STRUCTURE =
		"dependencies/ddmstructure.xml";

	@Inject(
		filter = "indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntryType"
	)
	private static Indexer<DLFileEntryType> _indexer;

	private DLFileEntryType _basicDocumentDLFileEntryType;

	@Inject(filter = "ddm.form.deserializer.type=xsd")
	private DDMFormDeserializer _ddmFormDeserializer;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Inject
	private File _file;

	@DeleteAfterTestRun
	private Group _group;

}