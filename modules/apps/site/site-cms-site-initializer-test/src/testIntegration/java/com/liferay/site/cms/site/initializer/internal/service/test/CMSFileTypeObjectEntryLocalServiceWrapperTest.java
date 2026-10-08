/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
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
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class CMSFileTypeObjectEntryLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", TestPropsValues.getCompanyId());

		_objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_FILES", _depotEntry.getGroupId(),
					TestPropsValues.getCompanyId());
	}

	@Test
	@TestInfo("LPD-102524")
	public void testAddObjectEntry() throws Exception {
		String urlTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		long fileEntryId = _getFileEntryId(_addObjectEntry(urlTitle));

		Assert.assertEquals(fileEntryId, _resolveFileEntryId(urlTitle));

		ObjectEntry objectEntry = _addObjectEntry(null);

		Assert.assertEquals(
			_getFileEntryId(objectEntry),
			_resolveFileEntryId(_getObjectEntryUrlTitle(objectEntry)));

		_assertObjectEntryUrlTitle(_addObjectEntry(urlTitle), urlTitle + "-1");
		_assertObjectEntryUrlTitle(
			_addObjectEntry(null, urlTitle), urlTitle + "-2");

		Assert.assertEquals(fileEntryId, _resolveFileEntryId(urlTitle));

		String fileEntryUrlTitle = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _depotEntry.getGroupId(), 0,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), fileEntryUrlTitle, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));

		_assertObjectEntryUrlTitle(
			_addObjectEntry(fileEntryUrlTitle), fileEntryUrlTitle + "-1");
		_assertObjectEntryUrlTitle(
			_addObjectEntry(
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
				fileEntryUrlTitle, null),
			fileEntryUrlTitle + "-2");

		Assert.assertEquals(
			fileEntry.getFileEntryId(), _resolveFileEntryId(fileEntryUrlTitle));
	}

	@Test
	@TestInfo("LPD-102524")
	public void testAddObjectEntryWithCustomFileType() throws Exception {
		ObjectDefinition objectDefinition = _addFileTypeObjectDefinition();

		FileEntry fileFileEntry = _addTempFileEntry(objectDefinition);
		FileEntry uploadFileEntry = _addTempFileEntry(objectDefinition);

		String urlTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			_objectEntryFolder.getObjectEntryFolderId(),
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"file", fileFileEntry.getFileEntryId()
			).put(
				"upload", uploadFileEntry.getFileEntryId()
			).build(),
			_getServiceContext(urlTitle));

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			GetterUtil.getLong(values.get("file")),
			_resolveFileEntryId(urlTitle));

		_assertObjectEntryUrlTitle(_addObjectEntry(urlTitle), urlTitle + "-1");

		Assert.assertEquals(
			GetterUtil.getLong(values.get("file")),
			_resolveFileEntryId(urlTitle));
	}

	@Test
	@TestInfo("LPD-102524")
	public void testAddObjectEntryWithFileEntryUrlTitle() throws Exception {
		String urlTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			_objectDefinition.getObjectDefinitionId(), "file");

		FileEntry fileEntry = _attachmentManager.getOrAddFileEntry(
			TestPropsValues.getCompanyId(), null, RandomTestUtil.randomBytes(),
			urlTitle + ".txt", _depotEntry.getGroupId(),
			objectField.getObjectFieldId(),
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));

		Assert.assertEquals(
			fileEntry.getFileEntryId(), _resolveFileEntryId(urlTitle));

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			_objectEntryFolder.getObjectEntryFolderId(),
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"file", fileEntry.getFileEntryId()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
					RandomTestUtil.randomString()
				).build()
			).build(),
			_getServiceContext(urlTitle));

		_assertObjectEntryUrlTitle(objectEntry, urlTitle);

		Assert.assertEquals(
			fileEntry.getFileEntryId(), _getFileEntryId(objectEntry));
	}

	@Test
	@TestInfo("LPD-102524")
	public void testCopyObjectEntry() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		ObjectEntry copyObjectEntry = _objectEntryLocalService.copyObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			_objectEntryFolder.getObjectEntryFolderId(),
			objectEntry.getValues(), _getServiceContext(null));

		String urlTitle = _getObjectEntryUrlTitle(copyObjectEntry);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, copyObjectEntry.getStatus());

		Assert.assertEquals(0, _resolveFileEntryId(urlTitle));

		copyObjectEntry = _objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), copyObjectEntry.getObjectEntryId(),
			WorkflowConstants.STATUS_APPROVED, _getServiceContext(null));

		Assert.assertEquals(
			_getFileEntryId(copyObjectEntry), _resolveFileEntryId(urlTitle));
	}

	@Test
	@TestInfo("LPD-102524")
	public void testPartialUpdateObjectEntry() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		String urlTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		objectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			_objectEntryFolder.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"title_i18n", _getTitle(objectEntry)
			).build(),
			_getServiceContext(urlTitle));

		Assert.assertEquals(
			_getFileEntryId(objectEntry), _resolveFileEntryId(urlTitle));
	}

	@Test
	@TestInfo("LPD-102524")
	public void testUpdateObjectEntry() throws Exception {
		String urlTitle1 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		ObjectEntry objectEntry = _addObjectEntry(urlTitle1);

		String urlTitle2 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		objectEntry = _updateObjectEntry(
			_getFileEntryId(objectEntry), objectEntry, urlTitle2);

		Assert.assertEquals(
			_getFileEntryId(objectEntry), _resolveFileEntryId(urlTitle1));
		Assert.assertEquals(
			_getFileEntryId(objectEntry), _resolveFileEntryId(urlTitle2));

		List<String> fileEntryUrlTitles = _getFileEntryUrlTitles(
			_getFileEntryId(objectEntry));

		FileEntry fileEntry = _addTempFileEntry();

		objectEntry = _updateObjectEntry(
			fileEntry.getFileEntryId(), objectEntry, urlTitle2);

		Assert.assertEquals(
			fileEntryUrlTitles.toString(), 3, fileEntryUrlTitles.size());

		for (String fileEntryUrlTitle : fileEntryUrlTitles) {
			Assert.assertEquals(
				_getFileEntryId(objectEntry),
				_resolveFileEntryId(fileEntryUrlTitle));
		}

		String urlTitle3 = _getObjectEntryUrlTitle(_addObjectEntry(null));

		try {
			_updateObjectEntry(
				_getFileEntryId(objectEntry), objectEntry, urlTitle3);

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFriendlyURLIsInUse(modelListenerException);
		}

		Assert.assertEquals(urlTitle2, _getObjectEntryUrlTitle(objectEntry));
	}

	private ObjectDefinition _addFileTypeObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(),
				objectFolder.getObjectFolderId(), null, null, false, false,
				false, true, true, false, false, false, false,
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build()),
				Arrays.asList(
					_createAttachmentObjectField("file", true),
					_createAttachmentObjectField("upload", false)),
				Collections.emptyList(), new ServiceContext());

		_objectDefinitions.add(objectDefinition);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectEntry _addObjectEntry(String urlTitle) throws Exception {
		return _addObjectEntry(
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()), urlTitle);
	}

	private ObjectEntry _addObjectEntry(
			String defaultLanguageId, String urlTitle)
		throws Exception {

		return _addObjectEntry(
			defaultLanguageId, RandomTestUtil.randomString(), urlTitle);
	}

	private ObjectEntry _addObjectEntry(
			String defaultLanguageId, String title, String urlTitle)
		throws Exception {

		FileEntry fileEntry = _addTempFileEntry();

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			_objectEntryFolder.getObjectEntryFolderId(), defaultLanguageId,
			HashMapBuilder.<String, Serializable>put(
				"file", fileEntry.getFileEntryId()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()), title
				).build()
			).build(),
			_getServiceContext(urlTitle));
	}

	private FileEntry _addTempFileEntry() throws Exception {
		return _addTempFileEntry(_objectDefinition);
	}

	private FileEntry _addTempFileEntry(ObjectDefinition objectDefinition)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			FileUtil.createTempFile(RandomTestUtil.randomBytes()),
			ContentTypes.TEXT_PLAIN);
	}

	private void _assertFriendlyURLIsInUse(
		ModelListenerException modelListenerException) {

		ObjectValidationRuleEngineException
			objectValidationRuleEngineException =
				(ObjectValidationRuleEngineException)
					modelListenerException.getCause();

		List<ObjectValidationRuleResult> objectValidationRuleResults =
			objectValidationRuleEngineException.
				getObjectValidationRuleResults();

		Assert.assertEquals(
			objectValidationRuleResults.toString(), 1,
			objectValidationRuleResults.size());

		ObjectValidationRuleResult objectValidationRuleResult =
			objectValidationRuleResults.get(0);

		Assert.assertEquals(
			LanguageUtil.get(
				LocaleUtil.getDefault(),
				"the-friendly-url-is-already-in-use.-please-enter-a-unique-" +
					"friendly-url"),
			objectValidationRuleResult.getErrorMessage());
		Assert.assertEquals(
			"objectEntryFriendlyURL",
			objectValidationRuleResult.getObjectFieldName());
	}

	private void _assertObjectEntryUrlTitle(
			ObjectEntry objectEntry, String urlTitle)
		throws Exception {

		Assert.assertEquals(urlTitle, _getObjectEntryUrlTitle(objectEntry));
		Assert.assertEquals(
			_getFileEntryId(objectEntry), _resolveFileEntryId(urlTitle));
	}

	private ObjectField _createAttachmentObjectField(
		String name, boolean system) {

		return new AttachmentObjectFieldBuilder(
		).labelMap(
			RandomTestUtil.randomLocaleStringMap()
		).name(
			name
		).objectFieldSettings(
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
				).value(
					"txt"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_FILE_SOURCE
				).value(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
				).value(
					"100"
				).build())
		).system(
			system
		).build();
	}

	private long _getFileEntryId(ObjectEntry objectEntry) throws Exception {
		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		return GetterUtil.getLong(values.get("file"));
	}

	private List<String> _getFileEntryUrlTitles(long fileEntryId) {
		List<String> urlTitles = new ArrayList<>();

		for (FriendlyURLEntry friendlyURLEntry :
				_friendlyURLEntryLocalService.getFriendlyURLEntries(
					_depotEntry.getGroupId(),
					_classNameLocalService.getClassNameId(FileEntry.class),
					fileEntryId)) {

			Map<String, String> languageIdToUrlTitleMap =
				friendlyURLEntry.getLanguageIdToUrlTitleMap();

			urlTitles.addAll(languageIdToUrlTitleMap.values());
		}

		return urlTitles;
	}

	private String _getObjectEntryUrlTitle(ObjectEntry objectEntry)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_classNameLocalService.getClassNameId(
					_objectDefinition.getClassName()),
				objectEntry.getObjectEntryId());

		return friendlyURLEntry.getUrlTitle(
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()));
	}

	private ServiceContext _getServiceContext(String urlTitle)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId());

		if (urlTitle != null) {
			serviceContext.setAttribute(
				"friendlyUrlMap",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()), urlTitle
				).build());
		}

		return serviceContext;
	}

	private Serializable _getTitle(ObjectEntry objectEntry) throws Exception {
		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		return values.get("title_i18n");
	}

	private long _resolveFileEntryId(String urlTitle) throws Exception {
		FileEntry fileEntry = _fileEntryFriendlyURLResolver.resolveFriendlyURL(
			_depotEntry.getGroupId(), urlTitle);

		if (fileEntry == null) {
			return 0;
		}

		return fileEntry.getFileEntryId();
	}

	private ObjectEntry _updateObjectEntry(
			long fileEntryId, ObjectEntry objectEntry, String urlTitle)
		throws Exception {

		return _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			_objectEntryFolder.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"file", fileEntryId
			).put(
				"title_i18n", _getTitle(objectEntry)
			).build(),
			_getServiceContext(urlTitle));
	}

	@Inject
	private AttachmentManager _attachmentManager;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

}