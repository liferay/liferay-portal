/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
public class CMSAssetVocabularyServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_cmsGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo({"LPD-90753", "LPD-95312"})
	public void testGetGroupVocabularies() throws Exception {
		_testGetGroupVocabulariesWithAssetLibraryDepotEntry();
		_testGetGroupVocabulariesWithSpaceDepotEntry();
		_testGetGroupVocabulariesWithSpaceDepotEntryAndDifferentVisibilityType();
		_testGetGroupVocabulariesWithSpaceDepotEntryAndInternalVisibilityType();
	}

	private AssetVocabulary _addAssetVocabulary(
			long groupId, int visibilityType)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _cmsGroup.getGroupId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, null, visibilityType,
				ServiceContextTestUtil.getServiceContext(
					_cmsGroup.getGroupId()));

		_cmsAssetVocabularies.add(assetVocabulary);

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			assetVocabulary.getVocabularyId(), new long[] {groupId},
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

	private void _testGetGroupVocabulariesWithAssetLibraryDepotEntry()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			depotEntry.getGroupId(),
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				new long[] {_group.getGroupId(), depotEntry.getGroupId()},
				new int[] {AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC});

		long assetVocabularyId = assetVocabulary.getVocabularyId();

		Assert.assertFalse(
			ListUtil.exists(
				assetVocabularies,
				curAssetVocabulary ->
					curAssetVocabulary.getVocabularyId() == assetVocabularyId));
	}

	private void _testGetGroupVocabulariesWithSpaceDepotEntry()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			depotEntry.getGroupId(),
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				new long[] {_group.getGroupId(), depotEntry.getGroupId()},
				new int[] {AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC});

		long assetVocabularyId = assetVocabulary.getVocabularyId();

		Assert.assertTrue(
			ListUtil.exists(
				assetVocabularies,
				curAssetVocabulary ->
					curAssetVocabulary.getVocabularyId() == assetVocabularyId));
	}

	private void _testGetGroupVocabulariesWithSpaceDepotEntryAndDifferentVisibilityType()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			depotEntry.getGroupId(),
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				new long[] {_group.getGroupId(), depotEntry.getGroupId()},
				new int[] {AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC});

		long assetVocabularyId = assetVocabulary.getVocabularyId();

		Assert.assertFalse(
			ListUtil.exists(
				assetVocabularies,
				curAssetVocabulary ->
					curAssetVocabulary.getVocabularyId() == assetVocabularyId));
	}

	private void _testGetGroupVocabulariesWithSpaceDepotEntryAndInternalVisibilityType()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		AssetVocabulary internalAssetVocabulary = _addAssetVocabulary(
			depotEntry.getGroupId(),
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);
		AssetVocabulary publicAssetVocabulary = _addAssetVocabulary(
			depotEntry.getGroupId(),
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				new long[] {_group.getGroupId(), depotEntry.getGroupId()},
				AssetVocabularyConstants.VISIBILITY_TYPES);

		long internalAssetVocabularyId =
			internalAssetVocabulary.getVocabularyId();

		Assert.assertFalse(
			ListUtil.exists(
				assetVocabularies,
				curAssetVocabulary ->
					curAssetVocabulary.getVocabularyId() ==
						internalAssetVocabularyId));

		long publicAssetVocabularyId = publicAssetVocabulary.getVocabularyId();

		Assert.assertTrue(
			ListUtil.exists(
				assetVocabularies,
				curAssetVocabulary ->
					curAssetVocabulary.getVocabularyId() ==
						publicAssetVocabularyId));
	}

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private AssetVocabularyService _assetVocabularyService;

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

	@Inject
	private GroupLocalService _groupLocalService;

}