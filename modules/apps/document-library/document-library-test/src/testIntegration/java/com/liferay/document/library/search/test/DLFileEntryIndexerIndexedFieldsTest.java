/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextExtractor;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 * @author Eric Yan
 */
@RunWith(Arquillian.class)
@Sync
public class DLFileEntryIndexerIndexedFieldsTest extends BaseDLIndexerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_fileEntrySearchFixture = new FileEntrySearchFixture(dlAppLocalService);

		_fileEntrySearchFixture.setUp();

		setGroup(dlFixture.addGroup());
		setIndexerClass(DLFileEntry.class);
		setUser(dlFixture.addUser());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_fileEntrySearchFixture.tearDown();
	}

	@Test
	public void testIndexedFields() throws Exception {
		dlFixture.updateDisplaySettings(LocaleUtil.JAPAN);

		String fileName_jp = "content_search.txt";

		String searchTerm = "新規";

		long fileEntryId = _addFileEntry(fileName_jp);

		SearchResponse searchResponse =
			dlSearchFixture.searchOnlyOneSearchResponse(
				searchTerm, LocaleUtil.JAPAN);

		Map<String, String> map = new HashMap<>();

		_populateExpectedFieldValues(
			dlAppLocalService.getFileEntry(fileEntryId), map);

		FieldValuesAssert.assertFieldValues(
			map,
			name ->
				!name.contains(StringPool.PERIOD) && !name.equals("score") &&
				!name.equals("timestamp"),
			searchResponse);
	}

	private long _addFileEntry(String fileName) throws Exception {
		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(
				"dependencies/" + fileName)) {

			FileEntry fileEntry = _fileEntrySearchFixture.addFileEntry(
				new FileEntryBlueprint() {
					{
						setFileName(fileName);
						setGroupId(dlFixture.getGroupId());
						setInputStream(inputStream);
						setTitle(fileName);
						setUserId(dlFixture.getUserId());
					}
				});

			return fileEntry.getFileEntryId();
		}
	}

	private AssetEntry _getAssetEntry(FileEntry fileEntry) {
		return _assetEntryLocalService.fetchEntry(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());
	}

	private long _getAssetEntryId(AssetEntry assetEntry) {
		if (assetEntry == null) {
			return 0;
		}

		return assetEntry.getEntryId();
	}

	private String _getContents(FileEntry fileEntry) throws Exception {
		String contents = _textExtractor.extractText(
			_dlFileEntryLocalService.getFileAsStream(
				fileEntry.getFileEntryId(), fileEntry.getVersion(), false),
			-1);

		return contents.trim();
	}

	private long _getDDMStructureId(FileEntry fileEntry) throws Exception {
		long ddmStructureId = 0;

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		List<DLFileEntryMetadata> dlFileEntryMetadatas =
			dlFileEntryMetadataLocalService.getFileVersionFileEntryMetadatas(
				dlFileVersion.getFileVersionId());

		for (DLFileEntryMetadata dlFileEntryMetadata : dlFileEntryMetadatas) {
			if (dlFileEntryMetadata == null) {
				continue;
			}

			ddmStructureId = dlFileEntryMetadata.getDDMStructureId();
		}

		return ddmStructureId;
	}

	private void _legacyPopulateHttpHeader(
		String fieldName, String value, String ddmStructureId,
		Map<String, String> map) {

		String contentEncodingFieldName = StringBundler.concat(
			"ddm__text__", ddmStructureId, "__HttpHeaders_", fieldName);

		map.put(contentEncodingFieldName, value);
		map.put(
			contentEncodingFieldName + "_String_sortable",
			StringUtil.toLowerCase(value));
	}

	private void _legacyPopulateHttpHeaders(
			FileEntry fileEntry, Map<String, String> map)
		throws Exception {

		String ddmStructureId = String.valueOf(_getDDMStructureId(fileEntry));

		_legacyPopulateHttpHeader(
			"CONTENT_ENCODING", "UTF-8", ddmStructureId, map);
		_legacyPopulateHttpHeader(
			"CONTENT_TYPE", "text/plain; charset=UTF-8", ddmStructureId, map);
	}

	private void _populateDates(FileEntry fileEntry, Map<String, String> map) {
		Date createDate = fileEntry.getCreateDate();

		indexedFieldsFixture.populateDate(Field.CREATE_DATE, createDate, map);

		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, fileEntry.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(Field.PUBLISH_DATE, createDate, map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateExpectedFieldValues(
			FileEntry fileEntry, Map<String, String> map)
		throws Exception {

		long assetEntryId = _getAssetEntryId(_getAssetEntry(fileEntry));

		map.put(Field.ASSET_ENTRY_ID, String.valueOf(assetEntryId));
		map.put(
			Field.ASSET_ENTRY_ID + "_sortable", String.valueOf(assetEntryId));

		map.put(Field.CLASS_NAME_ID, "0");
		map.put(Field.CLASS_PK, "0");
		map.put(Field.COMPANY_ID, String.valueOf(fileEntry.getCompanyId()));
		map.put(
			Field.DEFAULT_LANGUAGE_ID,
			LocaleUtil.toLanguageId(LocaleUtil.JAPAN));
		map.put(Field.ENTRY_CLASS_NAME, DLFileEntry.class.getName());
		map.put(
			Field.ENTRY_CLASS_PK, String.valueOf(fileEntry.getFileEntryId()));
		map.put(Field.FOLDER_ID, String.valueOf(fileEntry.getFolderId()));

		Group group = _groupLocalService.getGroup(dlFixture.getGroupId());

		map.put("groupExternalReferenceCode", group.getExternalReferenceCode());
		map.put(
			"scopeGroupExternalReferenceCode",
			group.getExternalReferenceCode());

		map.put(Field.GROUP_ID, String.valueOf(fileEntry.getGroupId()));
		map.put(Field.SCOPE_GROUP_ID, String.valueOf(fileEntry.getGroupId()));
		map.put(Field.STAGING_GROUP, "false");
		map.put(Field.STATUS, "0");
		map.put(Field.TITLE, fileEntry.getTitle());
		map.put(Field.TITLE + "_sortable", fileEntry.getTitle());
		map.put(Field.TREE_PATH, "");
		map.put(Field.USER_ID, String.valueOf(fileEntry.getUserId()));
		map.put(
			Field.USER_NAME, StringUtil.toLowerCase(fileEntry.getUserName()));
		map.put("classTypeId", "0");
		map.put("content_ja_JP", _getContents(fileEntry));
		map.put("contentLength_ja_JP", "5");
		map.put("contentLength_ja_JP_sortable", "5");
		map.put(
			"dataRepositoryId", String.valueOf(fileEntry.getRepositoryId()));
		map.put("ddmContent", "text/plain; charset=UTF-8 UTF-8");
		map.put("extension", fileEntry.getExtension());
		map.put("externalReferenceCode", fileEntry.getExternalReferenceCode());
		map.put("fileEntryTypeId", "0");
		map.put("fileExtension", fileEntry.getExtension());
		map.put("fileExtension_String_sortable", fileEntry.getExtension());
		map.put("fileName", fileEntry.getFileName());
		map.put("groupExternalReferenceCode", group.getExternalReferenceCode());
		map.put("hidden", "false");
		map.put(
			"mimeType",
			StringUtil.replace(
				fileEntry.getMimeType(), CharPool.SLASH, CharPool.UNDERLINE));
		map.put(
			"mimeType_String_sortable",
			StringUtil.replace(
				fileEntry.getMimeType(), CharPool.SLASH, CharPool.UNDERLINE));
		map.put("readCount", String.valueOf(fileEntry.getReadCount()));
		map.put("size", String.valueOf(fileEntry.getSize()));
		map.put("size_sortable", String.valueOf(fileEntry.getSize()));
		map.put("title_ja_JP", fileEntry.getTitle());

		User user = _userLocalService.getUser(dlFixture.getUserId());

		map.put("userExternalReferenceCode", user.getExternalReferenceCode());

		map.put("versionCount", String.valueOf(fileEntry.getVersion()));
		map.put(
			"versionCount_sortable", String.valueOf(fileEntry.getVersion()));
		map.put("visible", "true");

		_populateDates(fileEntry, map);

		if (_ddmIndexer.isLegacyDDMIndexFieldsEnabled()) {
			_legacyPopulateHttpHeaders(fileEntry, map);
		}
		else {
			_populateHttpHeaders(fileEntry, map);
		}

		_populateLocalizedTitles(fileEntry, map);
		_populateViewCount(fileEntry, map);

		indexedFieldsFixture.populatePriority("0.0", map);
		indexedFieldsFixture.populateRoleIdFields(
			fileEntry.getCompanyId(), DLFileEntry.class.getName(),
			fileEntry.getPrimaryKey(), fileEntry.getGroupId(), null, map);
		indexedFieldsFixture.populateUID(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId(), map);
	}

	private String _populateHttpHeader(
			long ddmStructureId, String fieldName, String value)
		throws Exception {

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.getStructure(
			ddmStructureId);

		String httpHeaderFieldName = "HttpHeaders_" + fieldName;

		if (!GetterUtil.getBoolean(
				ddmStructure.getFieldProperty(
					httpHeaderFieldName, "localizable"))) {

			Map<String, String> ddmField = HashMapBuilder.put(
				"ddmFieldName",
				StringBundler.concat(
					"[ddm__text__", ddmStructureId, "__HttpHeaders_", fieldName,
					"]")
			).put(
				"ddmFieldValueText", "[" + value + "]"
			).put(
				"ddmFieldValueText_String_sortable",
				"[" + StringUtil.toLowerCase(value) + "]"
			).put(
				"ddmValueFieldName", "[ddmFieldValueText]"
			).build();

			return ddmField.toString();
		}

		DDMForm ddmForm = ddmStructure.getDDMForm();

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		StringBundler sb = new StringBundler(availableLocales.size());

		for (Locale locale : availableLocales) {
			String ddmFieldValueText =
				"ddmFieldValueText_" + LocaleUtil.toLanguageId(locale);

			Map<String, String> ddmField = HashMapBuilder.put(
				ddmFieldValueText, value
			).put(
				ddmFieldValueText + "_String_sortable",
				StringUtil.toLowerCase(value)
			).put(
				"ddmFieldName",
				StringBundler.concat(
					"ddm__text__", ddmStructure.getStructureId(),
					StringBundler.concat(
						StringPool.UNDERLINE, StringPool.UNDERLINE,
						httpHeaderFieldName, StringPool.UNDERLINE,
						LocaleUtil.toLanguageId(locale)))
			).put(
				"ddmValueFieldName", ddmFieldValueText
			).build();

			sb.append(ddmField.toString());
		}

		return sb.toString();
	}

	private void _populateHttpHeaders(
			FileEntry fileEntry, Map<String, String> map)
		throws Exception {

		String[] ddmFieldArray = new String[2];

		ddmFieldArray[0] = _populateHttpHeader(
			_getDDMStructureId(fileEntry), "CONTENT_TYPE",
			"text/plain; charset=UTF-8");
		ddmFieldArray[1] = _populateHttpHeader(
			_getDDMStructureId(fileEntry), "CONTENT_ENCODING", "UTF-8");

		map.put(
			"ddmFieldArray",
			StringBundler.concat(
				StringPool.OPEN_BRACKET, ddmFieldArray[0],
				StringPool.COMMA_AND_SPACE, ddmFieldArray[1],
				StringPool.CLOSE_BRACKET));
	}

	private void _populateLocalizedTitles(
		FileEntry fileEntry, Map<String, String> map) {

		String title = fileEntry.getTitle();

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat("_sortable"), title);
		}
	}

	private void _populateViewCount(
		FileEntry fileEntry, Map<String, String> map) {

		map.put("viewCount", String.valueOf(fileEntry.getReadCount()));
		map.put("viewCount_sortable", String.valueOf(fileEntry.getReadCount()));
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private DDMIndexer _ddmIndexer;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	private FileEntrySearchFixture _fileEntrySearchFixture;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private TextExtractor _textExtractor;

	@Inject
	private UserLocalService _userLocalService;

}