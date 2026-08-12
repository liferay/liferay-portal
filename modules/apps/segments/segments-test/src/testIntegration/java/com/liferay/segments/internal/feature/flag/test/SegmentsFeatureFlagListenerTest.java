/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.feature.flag.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class SegmentsFeatureFlagListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-87246")
	public void testOnValueActivateSegmentsEntries() throws Exception {
		SegmentsEntry defaultSegmentsEntry = _addSegmentsEntry(
			false, SegmentsEntryConstants.SOURCE_DEFAULT);
		SegmentsEntry referredSegmentsEntry = _addSegmentsEntry(
			false, SegmentsEntryConstants.SOURCE_REFERRED);

		_invokeFeatureFlagListeners(true);

		defaultSegmentsEntry = _segmentsEntryLocalService.getSegmentsEntry(
			defaultSegmentsEntry.getSegmentsEntryId());

		Assert.assertTrue(defaultSegmentsEntry.isActive());

		referredSegmentsEntry = _segmentsEntryLocalService.getSegmentsEntry(
			referredSegmentsEntry.getSegmentsEntryId());

		Assert.assertTrue(referredSegmentsEntry.isActive());
	}

	@Test
	@TestInfo("LPD-87246")
	public void testOnValueAsahFaroBackendSegmentEntriesAreSkipped()
		throws Exception {

		SegmentsEntry asahFaroSegmentsEntry = _addSegmentsEntry(
			false, SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND);

		_invokeFeatureFlagListeners(true);

		asahFaroSegmentsEntry = _segmentsEntryLocalService.getSegmentsEntry(
			asahFaroSegmentsEntry.getSegmentsEntryId());

		Assert.assertFalse(asahFaroSegmentsEntry.isActive());
	}

	@Test
	@TestInfo("LPD-87246")
	public void testOnValueDeactivateSegmentsEntries() throws Exception {
		SegmentsEntry defaultSegmentsEntry = _addSegmentsEntry(
			true, SegmentsEntryConstants.SOURCE_DEFAULT);
		SegmentsEntry referredSegmentsEntry = _addSegmentsEntry(
			true, SegmentsEntryConstants.SOURCE_REFERRED);

		_invokeFeatureFlagListeners(false);

		defaultSegmentsEntry = _segmentsEntryLocalService.getSegmentsEntry(
			defaultSegmentsEntry.getSegmentsEntryId());

		Assert.assertFalse(defaultSegmentsEntry.isActive());

		referredSegmentsEntry = _segmentsEntryLocalService.getSegmentsEntry(
			referredSegmentsEntry.getSegmentsEntryId());

		Assert.assertFalse(referredSegmentsEntry.isActive());
	}

	@Test
	@TestInfo("LPD-87246")
	public void testOnValueDeactivateSegmentsExperiences() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsEntry defaultSegmentsEntry = _addSegmentsEntry(
			true, SegmentsEntryConstants.SOURCE_DEFAULT);
		SegmentsEntry asahFaroSegmentsEntry = _addSegmentsEntry(
			true, SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND);

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(),
				defaultSegmentsEntry.getExternalReferenceCode(),
				_group.getExternalReferenceCode(), layout.getPlid());
		SegmentsExperience asahFaroSegmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(),
				asahFaroSegmentsEntry.getExternalReferenceCode(),
				_group.getExternalReferenceCode(), layout.getPlid());

		_invokeFeatureFlagListeners(false);

		SegmentsExperience defaultSegmentsExperience =
			_segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId());

		Assert.assertFalse(defaultSegmentsExperience.isActive());

		SegmentsExperience asahSegmentsExperience =
			_segmentsExperienceLocalService.getSegmentsExperience(
				asahFaroSegmentsExperience.getSegmentsExperienceId());

		Assert.assertTrue(asahSegmentsExperience.isActive());
	}

	private SegmentsEntry _addSegmentsEntry(boolean active, String source)
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			CriteriaSerializer.serialize(new Criteria()), source,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		if (!active) {
			segmentsEntry.setActive(false);

			segmentsEntry = _segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry);
		}

		return segmentsEntry;
	}

	private void _invokeFeatureFlagListeners(boolean enabled) {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			CompanyConstants.SYSTEM, !enabled, "LPD-78863");
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			CompanyConstants.SYSTEM, enabled, "LPD-78863");
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}