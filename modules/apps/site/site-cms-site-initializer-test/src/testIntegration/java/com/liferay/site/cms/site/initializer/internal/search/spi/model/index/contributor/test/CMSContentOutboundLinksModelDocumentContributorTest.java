/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-82226"))
@RunWith(Arquillian.class)
public class CMSContentOutboundLinksModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_contentStructuresObjectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_group = _depotEntry.getGroup();

		_objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_group.getGroupId(), TestPropsValues.getCompanyId());
	}

	@After
	public void tearDown() throws Exception {
		for (ObjectRelationship objectRelationship : _objectRelationships) {
			_objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship.getObjectRelationshipId());
		}

		_objectRelationships.clear();

		for (ObjectDefinition objectDefinition : _objectDefinitions) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}

		_objectDefinitions.clear();

		_depotEntryLocalService.deleteDepotEntry(_depotEntry.getDepotEntryId());
	}

	@Test
	public void testContribute() throws Exception {
		_testContributeWhenObjectDefinitionIsNotCMS();
		_testContributeWithAttachmentWhenFileEntryIsForeignOwned();
		_testContributeWithAttachmentWhenFileEntryIsSelfOwned();
		_testContributeWithAttachmentWhenObjectEntryIsCopied();
		_testContributeWithAttachmentWhenObjectFieldIsLocalized();
		_testContributeWithRelationshipReference();
		_testContributeWithRichTextReferences();
		_testContributeWithoutReferences();
	}

	private ObjectEntry _addAttachmentObjectEntry(
			ObjectDefinition objectDefinition)
		throws Exception {

		FileEntry fileEntry = _addTempFileEntry(objectDefinition);

		return _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_ATTACHMENT_OBJECT_FIELD_NAME, fileEntry.getFileEntryId()
			).build());
	}

	private ObjectDefinition _addCMSObjectDefinition() throws Exception {
		return _addCMSObjectDefinition(
			Collections.singletonList(_buildRichTextObjectField()));
	}

	private ObjectDefinition _addCMSObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, false, false, ObjectDefinitionTestUtil.getRandomName(),
				objectFields,
				_contentStructuresObjectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS, "true");

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			_group.getGroupId(), objectDefinition,
			_objectEntryFolder.getObjectEntryFolderId(), values);
	}

	private FileEntry _addTempFileEntry(ObjectDefinition objectDefinition)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			FileUtil.createTempFile(
				RandomTestUtil.randomString(
				).getBytes()),
			ContentTypes.TEXT_PLAIN);
	}

	private ObjectField _buildAttachmentObjectField(String fileSource)
		throws Exception {

		return new AttachmentObjectFieldBuilder(
		).indexed(
			true
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			_ATTACHMENT_OBJECT_FIELD_NAME
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
					fileSource
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
				).value(
					"100"
				).build())
		).userId(
			TestPropsValues.getUserId()
		).build();
	}

	private ObjectField _buildLocalizedAttachmentObjectField(String fileSource)
		throws Exception {

		ObjectField objectField = _buildAttachmentObjectField(fileSource);

		objectField.setLocalized(true);

		return objectField;
	}

	private ObjectField _buildRichTextObjectField() throws Exception {
		return new RichTextObjectFieldBuilder(
		).indexed(
			true
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			_RICH_TEXT_OBJECT_FIELD_NAME
		).userId(
			TestPropsValues.getUserId()
		).build();
	}

	private Document _getDocument(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		Indexer<ObjectEntry> indexer = IndexerRegistryUtil.getIndexer(
			objectDefinition.getClassName());

		return indexer.getDocument(objectEntry);
	}

	private long _getFileEntryId(ObjectEntry objectEntry) {
		return MapUtil.getLong(
			objectEntry.getValues(), _ATTACHMENT_OBJECT_FIELD_NAME);
	}

	private String _getImageHTML(String externalReferenceCode) {
		return StringBundler.concat(
			"<img src=\"/documents/20125/0/image.jpg/", StringUtil.randomId(),
			"?download=true&amp;objectDefinitionExternalReferenceCode=",
			"L_CMS_BASIC_DOCUMENT&amp;objectEntryExternalReferenceCode=",
			externalReferenceCode,
			"&amp;objectFieldExternalReferenceCode=FILE\">");
	}

	private String _getVideoHTML(String externalReferenceCode) {
		String url = StringBundler.concat(
			"/documents/20125/0/video.mp4/", StringUtil.randomId(),
			"?download=true&amp;objectDefinitionExternalReferenceCode=",
			"L_CMS_BASIC_DOCUMENT&amp;objectEntryExternalReferenceCode=",
			externalReferenceCode,
			"&amp;objectFieldExternalReferenceCode=FILE");

		return StringBundler.concat(
			"<div data-oembed-url=\"", url, "\"><video src=\"", url,
			"\"></video></div>");
	}

	private void _testContributeWhenObjectDefinitionIsNotCMS()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, false, false, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(_buildRichTextObjectField()), 0,
				ObjectDefinitionConstants.SCOPE_SITE,
				TestPropsValues.getUserId());

		_objectDefinitions.add(objectDefinition);

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			TestPropsValues.getGroupId(), objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_RICH_TEXT_OBJECT_FIELD_NAME,
				_getImageHTML(RandomTestUtil.randomString())
			).build());

		Document document = _getDocument(objectDefinition, objectEntry);

		Assert.assertNull(document.getField("outboundLinks"));
	}

	private void _testContributeWithAttachmentWhenFileEntryIsForeignOwned()
		throws Exception {

		ObjectDefinition attachmentObjectDefinition = _addCMSObjectDefinition(
			Collections.singletonList(
				_buildAttachmentObjectField(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT)));

		ObjectEntry attachmentObjectEntry = _addAttachmentObjectEntry(
			attachmentObjectDefinition);

		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			Collections.singletonList(
				_buildAttachmentObjectField(
					ObjectFieldSettingConstants.VALUE_CMS_BASIC_DOCUMENT)));

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_ATTACHMENT_OBJECT_FIELD_NAME,
				_getFileEntryId(attachmentObjectEntry)
			).build());

		Document document = _getDocument(objectDefinition, objectEntry);

		Assert.assertArrayEquals(
			new String[] {
				"objectEntryId_" + attachmentObjectEntry.getObjectEntryId()
			},
			document.getValues("outboundLinks"));
	}

	private void _testContributeWithAttachmentWhenFileEntryIsSelfOwned()
		throws Exception {

		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			Arrays.asList(
				_buildAttachmentObjectField(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT),
				_buildRichTextObjectField()));

		FileEntry fileEntry = _addTempFileEntry(objectDefinition);

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_ATTACHMENT_OBJECT_FIELD_NAME, fileEntry.getFileEntryId()
			).build());

		Document document = _getDocument(objectDefinition, objectEntry);

		Assert.assertNull(document.getField("outboundLinks"));
	}

	private void _testContributeWithAttachmentWhenObjectEntryIsCopied()
		throws Exception {

		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			Arrays.asList(
				_buildAttachmentObjectField(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT),
				_buildRichTextObjectField()));

		FileEntry fileEntry = _addTempFileEntry(objectDefinition);

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_ATTACHMENT_OBJECT_FIELD_NAME, fileEntry.getFileEntryId()
			).build());

		ObjectEntry copiedObjectEntry =
			_objectEntryLocalService.copyObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(), objectEntry.getValues(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Document document = _getDocument(objectDefinition, copiedObjectEntry);

		Assert.assertNull(document.getField("outboundLinks"));
	}

	private void _testContributeWithAttachmentWhenObjectFieldIsLocalized()
		throws Exception {

		ObjectDefinition attachmentObjectDefinition = _addCMSObjectDefinition(
			Collections.singletonList(
				_buildAttachmentObjectField(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT)));

		ObjectEntry attachmentObjectEntry1 = _addAttachmentObjectEntry(
			attachmentObjectDefinition);
		ObjectEntry attachmentObjectEntry2 = _addAttachmentObjectEntry(
			attachmentObjectDefinition);

		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			Collections.singletonList(
				_buildLocalizedAttachmentObjectField(
					ObjectFieldSettingConstants.VALUE_CMS_BASIC_DOCUMENT)));

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		ObjectField objectField = objectFieldBag.getObjectField(
			_ATTACHMENT_OBJECT_FIELD_NAME);

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				objectField.getI18nObjectFieldName(),
				(Serializable)HashMapBuilder.<String, Object>put(
					LocaleUtil.toLanguageId(LocaleUtil.BRAZIL),
					_getFileEntryId(attachmentObjectEntry2)
				).put(
					LocaleUtil.toLanguageId(LocaleUtil.US),
					_getFileEntryId(attachmentObjectEntry1)
				).build()
			).build());

		Document document = _getDocument(objectDefinition, objectEntry);

		Assert.assertEquals(
			SetUtil.fromArray(
				"objectEntryId_" + attachmentObjectEntry1.getObjectEntryId(),
				"objectEntryId_" + attachmentObjectEntry2.getObjectEntryId()),
			SetUtil.fromArray(document.getValues("outboundLinks")));
	}

	private void _testContributeWithoutReferences() throws Exception {
		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_RICH_TEXT_OBJECT_FIELD_NAME, "<p>No references at all</p>"
			).build());

		Document document = _getDocument(objectDefinition, objectEntry);

		Assert.assertNull(document.getField("outboundLinks"));
	}

	private void _testContributeWithRelationshipReference() throws Exception {
		ObjectDefinition parentObjectDefinition = _addCMSObjectDefinition();
		ObjectDefinition childObjectDefinition = _addCMSObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, parentObjectDefinition,
				childObjectDefinition);

		_objectRelationships.add(objectRelationship);

		ObjectEntry parentObjectEntry = _addObjectEntry(
			parentObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_RICH_TEXT_OBJECT_FIELD_NAME, "<p>Target</p>"
			).build());

		ObjectEntry childObjectEntry = _addObjectEntry(
			childObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				ObjectRelationshipUtil.getObjectRelationshipFieldName(
					parentObjectDefinition, objectRelationship.getName()),
				parentObjectEntry.getObjectEntryId()
			).put(
				_RICH_TEXT_OBJECT_FIELD_NAME, "<p>Source</p>"
			).build());

		Document document = _getDocument(
			childObjectDefinition, childObjectEntry);

		Assert.assertArrayEquals(
			new String[] {
				"objectEntryId_" + parentObjectEntry.getObjectEntryId()
			},
			document.getValues("outboundLinks"));
	}

	private void _testContributeWithRichTextReferences() throws Exception {
		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		String externalReferenceCode = RandomTestUtil.randomString();
		String otherExternalReferenceCode = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_RICH_TEXT_OBJECT_FIELD_NAME,
				StringBundler.concat(
					_getImageHTML(externalReferenceCode),
					_getVideoHTML(otherExternalReferenceCode),
					"<a href=\"https://www.liferay.com\">External</a>")
			).build());

		Document document = _getDocument(objectDefinition, objectEntry);

		Assert.assertArrayEquals(
			new String[] {
				"objectEntryERC_" + externalReferenceCode,
				"objectEntryERC_" + otherExternalReferenceCode
			},
			document.getValues("outboundLinks"));
	}

	private static final String _ATTACHMENT_OBJECT_FIELD_NAME = "upload";

	private static final String _RICH_TEXT_OBJECT_FIELD_NAME = "content";

	private ObjectFolder _contentStructuresObjectFolder;
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private final List<ObjectRelationship> _objectRelationships =
		new ArrayList<>();

}