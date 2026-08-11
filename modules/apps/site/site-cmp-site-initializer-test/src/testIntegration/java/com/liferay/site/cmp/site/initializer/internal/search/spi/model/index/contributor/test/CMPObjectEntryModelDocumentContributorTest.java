/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class CMPObjectEntryModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(
			CMPObjectEntryModelDocumentContributorTest.class);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_objectDefinition = _publishObjectDefinition();
	}

	@Test
	public void testContribute() throws Exception {
		Assume.assumeTrue(LicenseManagerUtil.isAppEnabled(App.CMP));

		ObjectEntry linkedObjectEntry = _addLinkedObjectEntry(
			_objectDefinition);

		_assertFieldValues("cmpProjectObjectEntryIds", linkedObjectEntry);
		_assertFieldValues("cmpTaskObjectEntryIds", linkedObjectEntry);

		ObjectEntry cmpProjectObjectEntry1 =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry cmpProjectLinkObjectEntry =
			CMPTestUtil.addCMPProjectLinkObjectEntry(
				cmpProjectObjectEntry1, linkedObjectEntry);

		_assertFieldValues(
			"cmpProjectObjectEntryIds", linkedObjectEntry,
			cmpProjectObjectEntry1);

		_assertFieldValues("cmpTaskObjectEntryIds", linkedObjectEntry);

		ObjectEntry cmpProjectObjectEntry2 =
			CMPTestUtil.addCMPProjectObjectEntry();

		CMPTestUtil.addCMPProjectLinkObjectEntry(
			cmpProjectObjectEntry2, linkedObjectEntry);

		_assertFieldValues(
			"cmpProjectObjectEntryIds", linkedObjectEntry,
			cmpProjectObjectEntry1, cmpProjectObjectEntry2);

		_objectEntryLocalService.deleteObjectEntry(
			cmpProjectLinkObjectEntry.getObjectEntryId());

		_assertFieldValues(
			"cmpProjectObjectEntryIds", linkedObjectEntry,
			cmpProjectObjectEntry2);

		ObjectEntry cmpProjectObjectEntry3 =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry cmpTaskObjectEntry1 = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry3);

		ObjectEntry cmpTaskLinkObjectEntry1 =
			CMPTestUtil.addCMPTaskLinkObjectEntry(
				cmpTaskObjectEntry1, linkedObjectEntry);

		_assertFieldValues(
			"cmpProjectObjectEntryIds", linkedObjectEntry,
			cmpProjectObjectEntry2, cmpProjectObjectEntry3);
		_assertFieldValues(
			"cmpTaskObjectEntryIds", linkedObjectEntry, cmpTaskObjectEntry1);

		ObjectEntry cmpTaskObjectEntry2 = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry3);

		ObjectEntry cmpTaskLinkObjectEntry2 =
			CMPTestUtil.addCMPTaskLinkObjectEntry(
				cmpTaskObjectEntry2, linkedObjectEntry);

		_assertFieldValues(
			"cmpProjectObjectEntryIds", linkedObjectEntry,
			cmpProjectObjectEntry2, cmpProjectObjectEntry3);
		_assertFieldValues(
			"cmpTaskObjectEntryIds", linkedObjectEntry, cmpTaskObjectEntry1,
			cmpTaskObjectEntry2);

		_objectEntryLocalService.deleteObjectEntry(
			cmpTaskLinkObjectEntry1.getObjectEntryId());

		_assertFieldValues(
			"cmpProjectObjectEntryIds", linkedObjectEntry,
			cmpProjectObjectEntry2, cmpProjectObjectEntry3);
		_assertFieldValues(
			"cmpTaskObjectEntryIds", linkedObjectEntry, cmpTaskObjectEntry2);

		_objectEntryLocalService.deleteObjectEntry(
			cmpTaskLinkObjectEntry2.getObjectEntryId());

		_assertFieldValues(
			"cmpProjectObjectEntryIds", linkedObjectEntry,
			cmpProjectObjectEntry2);
		_assertFieldValues("cmpTaskObjectEntryIds", linkedObjectEntry);
	}

	private ObjectEntry _addLinkedObjectEntry(ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_depotEntry.getGroupId(), TestPropsValues.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), null,
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));
	}

	private void _assertFieldValues(
			String fieldName, ObjectEntry linkedObjectEntry,
			ObjectEntry... objectEntries)
		throws Exception {

		Indexer<ObjectEntry> indexer = IndexerRegistryUtil.getIndexer(
			linkedObjectEntry.getModelClassName());

		Document document = indexer.getDocument(linkedObjectEntry);

		Field field = document.getField(fieldName);

		if (objectEntries.length == 0) {
			Assert.assertNull(field);

			return;
		}

		Assert.assertEquals(
			ListUtil.sort(
				TransformUtil.transformToList(
					objectEntries,
					objectEntry -> String.valueOf(
						objectEntry.getObjectEntryId()))),
			ListUtil.sort(Arrays.asList(field.getValues())));
	}

	private ObjectDefinition _publishObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						StringUtil.randomId()
					).build()),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		return objectDefinition;
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

}