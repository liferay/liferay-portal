/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_4.test;

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
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

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
public class CMSFileTypeFriendlyURLUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_defaultLanguageId = UpgradeProcessUtil.getDefaultLanguageId(
			TestPropsValues.getCompanyId());

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.fromLanguageId(_defaultLanguageId),
				StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.fromLanguageId(_defaultLanguageId),
				StringUtil.randomString()
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
	public void testUpgrade() throws Exception {
		String urlTitle1 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		ObjectEntry objectEntry1 = _addObjectEntry(
			_objectDefinition, urlTitle1);

		String fileEntryUrlTitle1 = _resetFileEntryUrlTitle(objectEntry1);

		String urlTitle2 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		ObjectEntry objectEntry2 = _addObjectEntry(
			_objectDefinition, urlTitle2);

		_resetFileEntryUrlTitle(objectEntry2);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _depotEntry.getGroupId(), 0,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), urlTitle2, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));

		String urlTitle3 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		ObjectEntry objectEntry3 = _addObjectEntry(
			_addFileTypeObjectDefinition(), urlTitle3);

		_resetFileEntryUrlTitle(objectEntry3);

		Assert.assertEquals(0, _resolveFileEntryId(urlTitle1));
		Assert.assertEquals(0, _resolveFileEntryId(urlTitle3));

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		Assert.assertEquals(urlTitle1, _getObjectEntryUrlTitle(objectEntry1));
		Assert.assertEquals(
			_getFileEntryId(objectEntry1), _resolveFileEntryId(urlTitle1));
		Assert.assertEquals(
			_getFileEntryId(objectEntry1),
			_resolveFileEntryId(fileEntryUrlTitle1));

		Assert.assertEquals(
			urlTitle2 + "-1", _getObjectEntryUrlTitle(objectEntry2));
		Assert.assertEquals(
			_getFileEntryId(objectEntry2),
			_resolveFileEntryId(urlTitle2 + "-1"));
		Assert.assertEquals(
			fileEntry.getFileEntryId(), _resolveFileEntryId(urlTitle2));

		Assert.assertEquals(
			_getFileEntryId(objectEntry3), _resolveFileEntryId(urlTitle3));
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

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, String urlTitle)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId());

		serviceContext.setAttribute(
			"friendlyUrlMap",
			HashMapBuilder.put(
				_defaultLanguageId, urlTitle
			).build());

		FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			FileUtil.createTempFile(RandomTestUtil.randomBytes()),
			ContentTypes.TEXT_PLAIN);

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			_objectEntryFolder.getObjectEntryFolderId(), _defaultLanguageId,
			HashMapBuilder.<String, Serializable>put(
				"file", fileEntry.getFileEntryId()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					_defaultLanguageId, RandomTestUtil.randomString()
				).build()
			).build(),
			serviceContext);
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

	private String _getObjectEntryUrlTitle(ObjectEntry objectEntry)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_classNameLocalService.getClassNameId(
					_objectDefinition.getClassName()),
				objectEntry.getObjectEntryId());

		return friendlyURLEntry.getUrlTitle(_defaultLanguageId);
	}

	private String _resetFileEntryUrlTitle(ObjectEntry objectEntry)
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(
			FileEntry.class);
		long fileEntryId = _getFileEntryId(objectEntry);

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			_depotEntry.getGroupId(), classNameId, fileEntryId);

		String urlTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_depotEntry.getGroupId(), classNameId, fileEntryId, urlTitle,
			new ServiceContext());

		return urlTitle;
	}

	private long _resolveFileEntryId(String urlTitle) throws Exception {
		FileEntry fileEntry = _fileEntryFriendlyURLResolver.resolveFriendlyURL(
			_depotEntry.getGroupId(), urlTitle);

		if (fileEntry == null) {
			return 0;
		}

		return fileEntry.getFileEntryId();
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_4." +
			"CMSFileTypeFriendlyURLUpgradeProcess";

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private String _defaultLanguageId;

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
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}