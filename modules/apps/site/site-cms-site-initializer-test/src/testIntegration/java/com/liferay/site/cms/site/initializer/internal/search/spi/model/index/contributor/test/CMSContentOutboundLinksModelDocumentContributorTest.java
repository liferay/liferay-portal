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
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.ArrayList;
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
	public void testContributeNoOutboundLinks() throws Exception {
		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_RICH_TEXT_OBJECT_FIELD_NAME, "<p>No references at all</p>"
			).build());

		Document document = _getDocument(objectDefinition, objectEntry);

		Assert.assertNull(document.getField("outboundLinks"));
	}

	@Test
	public void testContributeRelationshipReference() throws Exception {
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

	@Test
	public void testContributeRichTextReferences() throws Exception {
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

	@Test
	public void testContributeWhenObjectDefinitionIsNotCMS() throws Exception {
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

	private ObjectDefinition _addCMSObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, false, false, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(_buildRichTextObjectField()),
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
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private final List<ObjectRelationship> _objectRelationships =
		new ArrayList<>();

}