/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class CMSAssetVocabularyLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_cmsGroup = CMSTestUtil.getOrAddGroup(
			CMSAssetVocabularyLocalServiceWrapperTest.class);
		_group = GroupTestUtil.addGroup();
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();
	}

	@Test
	@TestInfo("LPD-90753")
	public void testGetGroupsVocabularies() throws Exception {
		_testGetGroupsVocabulariesWithAssetLibraryDepotEntry();
		_testGetGroupsVocabulariesWithSameVocabularyInMultipleSpaceDepotEntries();
		_testGetGroupsVocabulariesWithSameVocabularyInSpaceDepotEntryAndAnyParentGroup();
		_testGetGroupsVocabulariesWithSpaceDepotEntry();
		_testGetGroupsVocabulariesWithSpaceDepotEntryAndDifferentClassName();
	}

	private AssetVocabulary _addAssetVocabulary(
			long classTypePK, long... groupIds)
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_cmsGroup.getGroupId(),
			_portal.getClassNameId(_objectDefinition.getClassName()),
			classTypePK, false);

		_cmsAssetVocabularies.add(assetVocabulary);

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			assetVocabulary.getVocabularyId(), groupIds,
			DepotConstants.TYPE_SPACE);

		return assetVocabulary;
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			type, ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _testGetGroupsVocabulariesWithAssetLibraryDepotEntry()
		throws Exception {

		long classTypePK = RandomTestUtil.randomLong();

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			classTypePK, depotEntry.getGroupId());

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyLocalService.getGroupsVocabularies(
				new long[] {_group.getGroupId(), depotEntry.getGroupId()},
				_objectDefinition.getClassName(), classTypePK);

		long assetVocabularyId = assetVocabulary.getVocabularyId();

		Assert.assertFalse(
			ListUtil.exists(
				assetVocabularies,
				curAssetVocabulary ->
					curAssetVocabulary.getVocabularyId() == assetVocabularyId));
	}

	private void _testGetGroupsVocabulariesWithSameVocabularyInMultipleSpaceDepotEntries()
		throws Exception {

		long classTypePK = RandomTestUtil.randomLong();

		DepotEntry depotEntry1 = _addDepotEntry(DepotConstants.TYPE_SPACE);
		DepotEntry depotEntry2 = _addDepotEntry(DepotConstants.TYPE_SPACE);

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			classTypePK, depotEntry1.getGroupId(), depotEntry2.getGroupId());

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyLocalService.getGroupsVocabularies(
				new long[] {depotEntry1.getGroupId(), depotEntry2.getGroupId()},
				_objectDefinition.getClassName(), classTypePK);

		long assetVocabularyId = assetVocabulary.getVocabularyId();

		List<AssetVocabulary> matchingAssetVocabularies = ListUtil.filter(
			assetVocabularies,
			curAssetVocabulary ->
				curAssetVocabulary.getVocabularyId() == assetVocabularyId);

		Assert.assertEquals(
			matchingAssetVocabularies.toString(), 1,
			matchingAssetVocabularies.size());
	}

	private void _testGetGroupsVocabulariesWithSameVocabularyInSpaceDepotEntryAndAnyParentGroup()
		throws Exception {

		long classTypePK = RandomTestUtil.randomLong();

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			classTypePK, depotEntry.getGroupId(),
			GroupConstants.ANY_PARENT_GROUP_ID);

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyLocalService.getGroupsVocabularies(
				new long[] {_group.getGroupId(), depotEntry.getGroupId()},
				_objectDefinition.getClassName(), classTypePK);

		long assetVocabularyId = assetVocabulary.getVocabularyId();

		List<AssetVocabulary> matchingAssetVocabularies = ListUtil.filter(
			assetVocabularies,
			curAssetVocabulary ->
				curAssetVocabulary.getVocabularyId() == assetVocabularyId);

		Assert.assertEquals(
			matchingAssetVocabularies.toString(), 1,
			matchingAssetVocabularies.size());
	}

	private void _testGetGroupsVocabulariesWithSpaceDepotEntry()
		throws Exception {

		long classTypePK = RandomTestUtil.randomLong();

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			classTypePK, depotEntry.getGroupId());

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyLocalService.getGroupsVocabularies(
				new long[] {_group.getGroupId(), depotEntry.getGroupId()},
				_objectDefinition.getClassName(), classTypePK);

		long assetVocabularyId = assetVocabulary.getVocabularyId();

		Assert.assertTrue(
			ListUtil.exists(
				assetVocabularies,
				curAssetVocabulary ->
					curAssetVocabulary.getVocabularyId() == assetVocabularyId));
	}

	private void _testGetGroupsVocabulariesWithSpaceDepotEntryAndDifferentClassName()
		throws Exception {

		long classTypePK = RandomTestUtil.randomLong();

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			classTypePK, depotEntry.getGroupId());

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyLocalService.getGroupsVocabularies(
				new long[] {_group.getGroupId(), depotEntry.getGroupId()},
				Group.class.getName(), classTypePK);

		long assetVocabularyId = assetVocabulary.getVocabularyId();

		Assert.assertFalse(
			ListUtil.exists(
				assetVocabularies,
				curAssetVocabulary ->
					curAssetVocabulary.getVocabularyId() == assetVocabularyId));
	}

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private final List<AssetVocabulary> _cmsAssetVocabularies =
		new ArrayList<>();

	private Group _cmsGroup;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private Portal _portal;

}