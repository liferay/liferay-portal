/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v6_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateStructureRelUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-97443")
	public void testUpgradeDeletesRedundantOrphanedLayoutPageTemplateStructureRel()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_fetchLayoutPageTemplateStructure(draftLayout);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), draftLayout.getPlid());

		_layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperience.getSegmentsExperienceId(), StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		_orphanLayoutPageTemplateStructureRel(
			layoutPageTemplateStructure,
			segmentsExperience.getSegmentsExperienceId());

		Assert.assertNotNull(
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure,
				SegmentsExperienceConstants.ID_DEFAULT));

		_runUpgrade();

		Assert.assertNull(
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure,
				SegmentsExperienceConstants.ID_DEFAULT));

		Assert.assertNotNull(
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, defaultSegmentsExperienceId));
	}

	@Test
	@TestInfo("LPD-97443")
	public void testUpgradeReassignsOrphanedLayoutPageTemplateStructureRelCreatedInPublication()
		throws Exception {

		CTCollection ctCollection = _addCTCollection();

		LayoutPageTemplateStructure layoutPageTemplateStructure = null;

		long defaultSegmentsExperienceId =
			SegmentsExperienceConstants.ID_DEFAULT;
		String data = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			Layout draftLayout = layout.fetchDraftLayout();

			layoutPageTemplateStructure = _fetchLayoutPageTemplateStructure(
				draftLayout);

			defaultSegmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid());

			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
				_fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure, defaultSegmentsExperienceId);

			data = layoutPageTemplateStructureRel.getData();

			_orphanLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, defaultSegmentsExperienceId);

			Assert.assertNotNull(
				_fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure,
					SegmentsExperienceConstants.ID_DEFAULT));
		}

		_runUpgrade();

		Assert.assertNull(
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure,
				SegmentsExperienceConstants.ID_DEFAULT));
		Assert.assertNull(
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, defaultSegmentsExperienceId));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			Assert.assertNull(
				_fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure,
					SegmentsExperienceConstants.ID_DEFAULT));

			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
				_fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure, defaultSegmentsExperienceId);

			Assert.assertEquals(data, layoutPageTemplateStructureRel.getData());
		}

		_ctCollectionLocalService.deleteCTCollection(ctCollection);
	}

	@Test
	@TestInfo("LPD-97443")
	public void testUpgradeReassignsOrphanedLayoutPageTemplateStructureRelModifiedInPublication()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_fetchLayoutPageTemplateStructure(draftLayout);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		LayoutPageTemplateStructureRel defaultLayoutPageTemplateStructureRel =
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, defaultSegmentsExperienceId);

		String data = defaultLayoutPageTemplateStructureRel.getData();

		CTCollection ctCollection = _addCTCollection();

		String publicationData = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
				_fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure, defaultSegmentsExperienceId);

			layoutPageTemplateStructureRel.setSegmentsExperienceId(
				SegmentsExperienceConstants.ID_DEFAULT);
			layoutPageTemplateStructureRel.setData(publicationData);

			_layoutPageTemplateStructureRelLocalService.
				updateLayoutPageTemplateStructureRel(
					layoutPageTemplateStructureRel);

			Assert.assertNotNull(
				_fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure,
					SegmentsExperienceConstants.ID_DEFAULT));
		}

		_runUpgrade();

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, defaultSegmentsExperienceId);

		Assert.assertEquals(data, layoutPageTemplateStructureRel.getData());

		Assert.assertNull(
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure,
				SegmentsExperienceConstants.ID_DEFAULT));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			Assert.assertNull(
				_fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure,
					SegmentsExperienceConstants.ID_DEFAULT));

			layoutPageTemplateStructureRel =
				_fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure, defaultSegmentsExperienceId);

			Assert.assertEquals(
				publicationData, layoutPageTemplateStructureRel.getData());
		}

		_ctCollectionLocalService.deleteCTCollection(ctCollection);
	}

	@Test
	@TestInfo("LPD-97443")
	public void testUpgradeReassignsOrphanedLayoutPageTemplateStructureRelToDefaultExperience()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_fetchLayoutPageTemplateStructure(draftLayout);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		LayoutPageTemplateStructureRel defaultLayoutPageTemplateStructureRel =
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, defaultSegmentsExperienceId);

		String data = defaultLayoutPageTemplateStructureRel.getData();

		_orphanLayoutPageTemplateStructureRel(
			layoutPageTemplateStructure, defaultSegmentsExperienceId);

		Assert.assertNull(
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, defaultSegmentsExperienceId));

		_runUpgrade();

		Assert.assertNull(
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure,
				SegmentsExperienceConstants.ID_DEFAULT));

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, defaultSegmentsExperienceId);

		Assert.assertEquals(data, layoutPageTemplateStructureRel.getData());
	}

	private CTCollection _addCTCollection() throws Exception {
		return _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private LayoutPageTemplateStructure _fetchLayoutPageTemplateStructure(
		Layout layout) {

		return _layoutPageTemplateStructureLocalService.
			fetchLayoutPageTemplateStructure(
				layout.getGroupId(), layout.getPlid());
	}

	private LayoutPageTemplateStructureRel _fetchLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructure layoutPageTemplateStructure,
		long segmentsExperienceId) {

		return _layoutPageTemplateStructureRelLocalService.
			fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperienceId);
	}

	private void _orphanLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructure layoutPageTemplateStructure,
		long segmentsExperienceId) {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure, segmentsExperienceId);

		layoutPageTemplateStructureRel.setSegmentsExperienceId(
			SegmentsExperienceConstants.ID_DEFAULT);

		_layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRel);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_entityCache.clearCache();

		_multiVMPool.clear();
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.page.template.internal.upgrade.v6_2_0." +
			"LayoutPageTemplateStructureRelUpgradeProcess";

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}