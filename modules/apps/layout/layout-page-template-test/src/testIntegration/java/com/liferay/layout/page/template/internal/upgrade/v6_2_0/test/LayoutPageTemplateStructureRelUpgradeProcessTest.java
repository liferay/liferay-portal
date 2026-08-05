/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v6_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
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
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
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
public class LayoutPageTemplateStructureRelUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-97443")
	public void testUpgrade() throws Exception {

		// An orphaned LayoutPageTemplateStructureRel is deleted

		LayoutPageTemplateStructure layoutPageTemplateStructure1 =
			_addLayoutPageTemplateStructure();

		long defaultSegmentsExperienceId1 =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateStructure1.getPlid());

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel1 =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure1.
						getLayoutPageTemplateStructureId(),
					defaultSegmentsExperienceId1);

		String data1 = layoutPageTemplateStructureRel1.getData();

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), layoutPageTemplateStructure1.getPlid());

		_layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layoutPageTemplateStructure1.getLayoutPageTemplateStructureId(),
				segmentsExperience.getSegmentsExperienceId(), StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		_createOrphanLayoutPageTemplateStructureRel(
			layoutPageTemplateStructure1,
			segmentsExperience.getSegmentsExperienceId());

		// An orphaned LayoutPageTemplateStructureRel is reassigned to the
		// existing default experience

		LayoutPageTemplateStructure layoutPageTemplateStructure2 =
			_addLayoutPageTemplateStructure();

		long defaultSegmentsExperienceId2 =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateStructure2.getPlid());

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel2 =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure2.
						getLayoutPageTemplateStructureId(),
					defaultSegmentsExperienceId2);

		String data2 = layoutPageTemplateStructureRel2.getData();

		_createOrphanLayoutPageTemplateStructureRel(
			layoutPageTemplateStructure2, defaultSegmentsExperienceId2);

		// An orphaned LayoutPageTemplateStructureRel is reassigned to a newly
		// created default experience

		LayoutPageTemplateStructure layoutPageTemplateStructure3 =
			_addLayoutPageTemplateStructure();

		long defaultSegmentsExperienceId3 =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateStructure3.getPlid());

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel3 =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure3.
						getLayoutPageTemplateStructureId(),
					defaultSegmentsExperienceId3);

		String data3 = layoutPageTemplateStructureRel3.getData();

		_createOrphanLayoutPageTemplateStructureRel(
			layoutPageTemplateStructure3, defaultSegmentsExperienceId3);

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			_segmentsExperienceLocalService.getSegmentsExperience(
				defaultSegmentsExperienceId3));

		// An orphaned LayoutPageTemplateStructureRel is reassigned to the
		// default experience while production keeps its own data

		LayoutPageTemplateStructure layoutPageTemplateStructure4 =
			_addLayoutPageTemplateStructure();

		long defaultSegmentsExperienceId4 =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateStructure4.getPlid());

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel4 =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure4.
						getLayoutPageTemplateStructureId(),
					defaultSegmentsExperienceId4);

		String data4 = layoutPageTemplateStructureRel4.getData();

		CTCollection ctCollection = _addCTCollection();

		String publicationData = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
				_layoutPageTemplateStructureRelLocalService.
					fetchLayoutPageTemplateStructureRel(
						layoutPageTemplateStructure4.
							getLayoutPageTemplateStructureId(),
						defaultSegmentsExperienceId4);

			layoutPageTemplateStructureRel.setSegmentsExperienceId(
				SegmentsExperienceConstants.ID_DEFAULT);
			layoutPageTemplateStructureRel.setData(publicationData);

			_layoutPageTemplateStructureRelLocalService.
				updateLayoutPageTemplateStructureRel(
					layoutPageTemplateStructureRel);
		}

		runUpgrade();

		_assertUpgrade(layoutPageTemplateStructure1, data1);
		_assertUpgrade(layoutPageTemplateStructure2, data2);
		_assertUpgrade(layoutPageTemplateStructure3, data3);
		_assertUpgrade(layoutPageTemplateStructure4, data4);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_assertUpgrade(layoutPageTemplateStructure4, publicationData);
		}
		finally {
			_ctCollectionLocalService.deleteCTCollection(ctCollection);
		}
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_addLayoutPageTemplateStructure();

		return _layoutPageTemplateStructureRelLocalService.
			fetchLayoutPageTemplateStructureRel(
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						layoutPageTemplateStructure.getPlid()));
	}

	@Override
	protected CTService<?> getCTService() {
		return _layoutPageTemplateStructureRelLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			(LayoutPageTemplateStructureRel)ctModel;

		layoutPageTemplateStructureRel.setData(RandomTestUtil.randomString());

		return _layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRel);
	}

	private CTCollection _addCTCollection() throws Exception {
		return _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private LayoutPageTemplateStructure _addLayoutPageTemplateStructure()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		return _layoutPageTemplateStructureLocalService.
			fetchLayoutPageTemplateStructure(
				draftLayout.getGroupId(), draftLayout.getPlid());
	}

	private void _assertUpgrade(
		LayoutPageTemplateStructure layoutPageTemplateStructure, String data) {

		Assert.assertNull(
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					SegmentsExperienceConstants.ID_DEFAULT));

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateStructure.getPlid());

		Assert.assertNotEquals(
			SegmentsExperienceConstants.ID_DEFAULT,
			defaultSegmentsExperienceId);

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					defaultSegmentsExperienceId);

		Assert.assertEquals(data, layoutPageTemplateStructureRel.getData());
	}

	private void _createOrphanLayoutPageTemplateStructureRel(
		LayoutPageTemplateStructure layoutPageTemplateStructure,
		long segmentsExperienceId) {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					segmentsExperienceId);

		layoutPageTemplateStructureRel.setSegmentsExperienceId(
			SegmentsExperienceConstants.ID_DEFAULT);

		_layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRel);
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