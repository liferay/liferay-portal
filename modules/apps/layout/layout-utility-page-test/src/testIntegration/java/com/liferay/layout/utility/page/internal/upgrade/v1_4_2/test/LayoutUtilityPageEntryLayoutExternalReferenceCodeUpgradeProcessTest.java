/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.internal.upgrade.v1_4_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class
	LayoutUtilityPageEntryLayoutExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo({"LPD-74327", "LPD-117047"})
	public void testUpgradeProcess() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		String externalReferenceCode1 = "LFR-" + RandomTestUtil.randomString();

		LayoutUtilityPageEntry layoutUtilityPageEntry1 =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				externalReferenceCode1, TestPropsValues.getUserId(),
				_group.getGroupId(), 0, 0, false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_STATUS, null,
				_serviceContext);

		String externalReferenceCode2 = RandomTestUtil.randomString();

		LayoutUtilityPageEntry layoutUtilityPageEntry2 =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				externalReferenceCode2, TestPropsValues.getUserId(),
				_group.getGroupId(), 0, 0, false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_STATUS, null,
				_serviceContext);

		_updateExternalReferenceCodes(
			layout,
			_layoutLocalService.getLayout(layoutUtilityPageEntry1.getPlid()),
			_layoutLocalService.getLayout(layoutUtilityPageEntry2.getPlid()));

		_runUpgrade();

		_assertExternalReferenceCodes(
			_layoutLocalService.getLayout(layout.getPlid()),
			(externalReferenceCode, suffix) -> {
				if (!Objects.equals(suffix, "-layout") &&
					StringUtil.startsWith(
						externalReferenceCode,
						layout.getExternalReferenceCode())) {

					return false;
				}

				return true;
			});

		_assertExternalReferenceCodes(
			_layoutLocalService.getLayout(layoutUtilityPageEntry1.getPlid()),
			(externalReferenceCode, suffix) -> Objects.equals(
				externalReferenceCode, externalReferenceCode1 + suffix));

		_assertExternalReferenceCodes(
			_layoutLocalService.getLayout(layoutUtilityPageEntry2.getPlid()),
			(externalReferenceCode, suffix) -> !StringUtil.startsWith(
				externalReferenceCode, externalReferenceCode2));
	}

	private void _assertExternalReferenceCodes(
			Layout layout,
			UnsafeBiFunction<String, String, Boolean, Exception>
				unsafeBiFunction)
		throws Exception {

		Assert.assertTrue(
			layout.getExternalReferenceCode(),
			unsafeBiFunction.apply(
				layout.getExternalReferenceCode(), "-layout"));

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				layout.getPlid());

		Assert.assertTrue(
			segmentsExperience.getExternalReferenceCode(),
			unsafeBiFunction.apply(
				segmentsExperience.getExternalReferenceCode(),
				"-layout-default"));

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertTrue(
			draftLayout.getExternalReferenceCode(),
			unsafeBiFunction.apply(
				draftLayout.getExternalReferenceCode(), "-layout-draft"));

		segmentsExperience =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				draftLayout.getPlid());

		Assert.assertTrue(
			segmentsExperience.getExternalReferenceCode(),
			unsafeBiFunction.apply(
				segmentsExperience.getExternalReferenceCode(),
				"-layout-draft-default"));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_entityCache.clearCache();
	}

	private void _updateDefaultSegmentsExperienceExternalReferenceCode(
		long plid) {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				plid);

		String externalReferenceCode = RandomTestUtil.randomString();

		segmentsExperience.setExternalReferenceCode(externalReferenceCode);

		segmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperience(
				segmentsExperience);

		Assert.assertEquals(
			externalReferenceCode,
			segmentsExperience.getExternalReferenceCode());
	}

	private void _updateExternalReferenceCodes(Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_updateLayoutExternalReferenceCode(layout);
			_updateDefaultSegmentsExperienceExternalReferenceCode(
				layout.getPlid());

			Layout draftLayout = layout.fetchDraftLayout();

			_updateLayoutExternalReferenceCode(draftLayout);
			_updateDefaultSegmentsExperienceExternalReferenceCode(
				draftLayout.getPlid());
		}
	}

	private void _updateLayoutExternalReferenceCode(Layout layout) {
		String externalReferenceCode = RandomTestUtil.randomString();

		layout.setExternalReferenceCode(externalReferenceCode);

		layout = _layoutLocalService.updateLayout(layout);

		Assert.assertEquals(
			externalReferenceCode, layout.getExternalReferenceCode());
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.utility.page.internal.upgrade.v1_4_2." +
			"LayoutUtilityPageEntryLayoutExternalReferenceCodeUpgradeProcess";

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.utility.page.internal.upgrade.registry.LayoutUtilityPageEntryUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}