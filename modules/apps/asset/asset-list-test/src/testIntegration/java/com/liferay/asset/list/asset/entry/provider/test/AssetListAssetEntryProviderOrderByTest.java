/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.asset.entry.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.info.pagination.InfoPage;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;

import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Olivia Yu
 */
@RunWith(Arquillian.class)
public class AssetListAssetEntryProviderOrderByTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE,
					ObjectFieldConstants.DB_TYPE_DATE, true, false, null,
					RandomTestUtil.randomString(), "dueDate", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), "learnDocumentation", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
					ObjectFieldConstants.DB_TYPE_INTEGER, true, false, null,
					RandomTestUtil.randomString(), "priority", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					RandomTestUtil.randomString(), "title", false)),
			ObjectDefinitionConstants.SCOPE_SITE);

		ObjectField titleObjectField =
			_objectFieldLocalService.fetchObjectField(
				_objectDefinition.getObjectDefinitionId(), "title");

		_objectDefinition =
			_objectDefinitionLocalService.updateTitleObjectFieldId(
				_objectDefinition.getObjectDefinitionId(),
				titleObjectField.getObjectFieldId());
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageOrderedByObjectFieldDate()
		throws Exception {

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"dueDate", "2026-03-01"
			).build());
		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"dueDate", "2026-01-01"
			).build());
		ObjectEntry objectEntry3 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"dueDate", "2026-02-01"
			).build());

		_assertOrderedObjectEntries(
			_getOrderByColumn("dueDate"), "ASC", objectEntry2, objectEntry3,
			objectEntry1);
		_assertOrderedObjectEntries(
			_getOrderByColumn("dueDate"), "DESC", objectEntry1, objectEntry3,
			objectEntry2);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageOrderedByObjectFieldInteger()
		throws Exception {

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"priority", 30
			).build());
		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"priority", 10
			).build());
		ObjectEntry objectEntry3 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"priority", 20
			).build());

		_assertOrderedObjectEntries(
			_getOrderByColumn("priority"), "ASC", objectEntry2, objectEntry3,
			objectEntry1);
		_assertOrderedObjectEntries(
			_getOrderByColumn("priority"), "DESC", objectEntry1, objectEntry3,
			objectEntry2);
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-74731"))
	@Test
	public void testGetAssetEntriesInfoPageOrderedByObjectFieldText()
		throws Exception {

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"learnDocumentation", "charlie"
			).build());
		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"learnDocumentation", "alpha"
			).build());
		ObjectEntry objectEntry3 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"learnDocumentation", "bravo"
			).build());

		_assertOrderedObjectEntries(
			_getOrderByColumn("learnDocumentation"), "ASC", objectEntry2,
			objectEntry3, objectEntry1);
		_assertOrderedObjectEntries(
			_getOrderByColumn("learnDocumentation"), "DESC", objectEntry1,
			objectEntry3, objectEntry2);
	}

	private AssetListEntry _addDynamicAssetListEntryWithOrderBy(
			String orderByColumn, String orderByType)
		throws Exception {

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId(), 0);

		_assetListEntryLocalService.updateAssetListEntryTypeSettings(
			assetListEntry.getAssetListEntryId(),
			SegmentsEntryConstants.ID_DEFAULT,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"anyAssetType",
				String.valueOf(
					_portal.getClassNameId(_objectDefinition.getClassName()))
			).put(
				"orderByColumn1", orderByColumn
			).put(
				"orderByType1", orderByType
			).build(
			).toString());

		return _assetListEntryLocalService.getAssetListEntry(
			assetListEntry.getAssetListEntryId());
	}

	private ObjectEntry _addObjectEntry(Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertOrderedObjectEntries(
			String orderByColumn, String orderByType,
			ObjectEntry... expectedObjectEntries)
		throws Exception {

		AssetListEntry assetListEntry = _addDynamicAssetListEntryWithOrderBy(
			orderByColumn, orderByType);

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK, StringPool.BLANK,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<Long> actualClassPKs = TransformUtil.transform(
			infoPage.getPageItems(), AssetEntry::getClassPK);

		List<Long> expectedClassPKs = TransformUtil.transformToList(
			expectedObjectEntries, ObjectEntry::getObjectEntryId);

		Assert.assertEquals(
			actualClassPKs.toString(), expectedClassPKs, actualClassPKs);
	}

	private String _getOrderByColumn(String propertyName) {
		return JSONUtil.put(
			"classNameId",
			_portal.getClassNameId(_objectDefinition.getClassName())
		).put(
			"classTypeId", _objectDefinition.getObjectDefinitionId()
		).put(
			"propertyName", propertyName
		).toString();
	}

	@Inject
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private Portal _portal;

}