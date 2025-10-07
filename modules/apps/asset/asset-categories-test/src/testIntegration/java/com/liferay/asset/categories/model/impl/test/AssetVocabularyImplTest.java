/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.model.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author José Manuel Navarro
 */
@RunWith(Arquillian.class)
public class AssetVocabularyImplTest {

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
				LocaleUtil.getDefault(), "name"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "description"
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testHasMoreThanOneCategorySelected() throws Exception {
		AssetVocabulary vocabulary1 = AssetTestUtil.addVocabulary(
			_group.getGroupId(), 1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
			true);

		AssetCategory category11 = AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary1.getVocabularyId());
		AssetCategory category12 = AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary1.getVocabularyId());

		Assert.assertFalse(
			vocabulary1.hasMoreThanOneCategorySelected(new long[0]));
		Assert.assertFalse(
			vocabulary1.hasMoreThanOneCategorySelected(
				new long[] {category11.getCategoryId()}));
		Assert.assertTrue(
			vocabulary1.hasMoreThanOneCategorySelected(
				new long[] {
					category11.getCategoryId(), category12.getCategoryId()
				}));

		AssetVocabulary vocabulary2 = AssetTestUtil.addVocabulary(
			_group.getGroupId(), 2, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
			true);

		AssetCategory category21 = AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary2.getVocabularyId());
		AssetCategory category22 = AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary2.getVocabularyId());

		Assert.assertFalse(
			vocabulary1.hasMoreThanOneCategorySelected(
				new long[] {
					category21.getCategoryId(), category22.getCategoryId()
				}));

		Assert.assertFalse(
			vocabulary2.hasMoreThanOneCategorySelected(new long[0]));
		Assert.assertFalse(
			vocabulary2.hasMoreThanOneCategorySelected(
				new long[] {category21.getCategoryId()}));
		Assert.assertTrue(
			vocabulary2.hasMoreThanOneCategorySelected(
				new long[] {
					category21.getCategoryId(), category22.getCategoryId()
				}));
	}

	@Test
	public void testIsAssociatedToClassNameId() throws Exception {
		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), AssetCategoryConstants.ALL_CLASS_NAME_ID,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true);

		Assert.assertTrue(vocabulary.isAssociatedToClassNameId(1));

		vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), 1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
			true);

		Assert.assertTrue(vocabulary.isAssociatedToClassNameId(1));
		Assert.assertFalse(vocabulary.isAssociatedToClassNameId(2));
	}

	@Test
	public void testIsDepotRequired() throws Exception {
		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_depotEntry.getGroupId(), AssetCategoryConstants.ALL_CLASS_NAME_ID,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, false, true);

		Assert.assertFalse(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
		Assert.assertTrue(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_depotEntry.getGroupId()));

		vocabulary = AssetTestUtil.addVocabulary(
			_depotEntry.getGroupId(), AssetCategoryConstants.ALL_CLASS_NAME_ID,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true, false);

		Assert.assertTrue(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
		Assert.assertTrue(
			vocabulary.isRequired(
				2, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));

		vocabulary = AssetTestUtil.addVocabulary(
			_depotEntry.getGroupId(), 1,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, false, true);

		Assert.assertFalse(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
		Assert.assertTrue(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_depotEntry.getGroupId()));

		vocabulary = AssetTestUtil.addVocabulary(
			_depotEntry.getGroupId(), 1,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true, false);

		Assert.assertTrue(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
		Assert.assertFalse(
			vocabulary.isRequired(
				2, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
	}

	@Test
	public void testIsMissingDepotRequiredCategory() throws Exception {
		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_depotEntry.getGroupId(), 1,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, false, true);

		AssetTestUtil.addCategory(
			_depotEntry.getGroupId(), vocabulary.getVocabularyId());

		Assert.assertFalse(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[] {1},
				_group.getGroupId()));

		Assert.assertTrue(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[] {1},
				_depotEntry.getGroupId()));

		vocabulary = AssetTestUtil.addVocabulary(
			_depotEntry.getGroupId(), 1,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true, false);

		Assert.assertTrue(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[] {1},
				_depotEntry.getGroupId()));
		Assert.assertFalse(
			vocabulary.isMissingRequiredCategory(
				2, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[0],
				_depotEntry.getGroupId()));

		AssetCategory category = AssetTestUtil.addCategory(
			_depotEntry.getGroupId(), vocabulary.getVocabularyId());

		Assert.assertTrue(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[] {1},
				_depotEntry.getGroupId()));
		Assert.assertFalse(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				new long[] {category.getCategoryId()},
				_depotEntry.getGroupId()));
	}

	@Test
	public void testIsMissingRequiredCategory() throws Exception {
		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), 1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
			false);

		AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary.getVocabularyId());

		Assert.assertFalse(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[] {1},
				_group.getGroupId()));

		vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), 1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
			true);

		Assert.assertTrue(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[] {1},
				_group.getGroupId()));
		Assert.assertFalse(
			vocabulary.isMissingRequiredCategory(
				2, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[0],
				_group.getGroupId()));

		AssetCategory category = AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary.getVocabularyId());

		Assert.assertTrue(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK, new long[] {1},
				_group.getGroupId()));
		Assert.assertFalse(
			vocabulary.isMissingRequiredCategory(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				new long[] {category.getCategoryId()}, _group.getGroupId()));
	}

	@Test
	public void testIsRequired() throws Exception {
		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), AssetCategoryConstants.ALL_CLASS_NAME_ID,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, false);

		Assert.assertFalse(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
		Assert.assertFalse(
			vocabulary.isRequired(
				2, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));

		vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), AssetCategoryConstants.ALL_CLASS_NAME_ID,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true);

		Assert.assertTrue(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
		Assert.assertTrue(
			vocabulary.isRequired(
				2, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));

		vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), 1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
			false);

		Assert.assertFalse(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
		Assert.assertFalse(
			vocabulary.isRequired(
				2, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));

		vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), 1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
			true);

		Assert.assertTrue(
			vocabulary.isRequired(
				1, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
		Assert.assertFalse(
			vocabulary.isRequired(
				2, AssetCategoryConstants.ALL_CLASS_TYPE_PK,
				_group.getGroupId()));
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}