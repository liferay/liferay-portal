/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.VerifyProcess;
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
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class SegmentsServiceVerifyProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testVerify() throws Exception {
		SegmentsEntry defaultSegmentsEntry = _addActiveSegmentsEntry(
			SegmentsEntryConstants.SOURCE_DEFAULT);

		_verifyProcess.verify();

		defaultSegmentsEntry = _segmentsEntryLocalService.getSegmentsEntry(
			defaultSegmentsEntry.getSegmentsEntryId());

		Assert.assertTrue(defaultSegmentsEntry.isActive());
	}

	@FeatureFlag(enable = false, value = "LPD-78863")
	@Test
	public void testVerifyWithFeatureFlagDisabled() throws Exception {
		SegmentsEntry asahFaroSegmentsEntry = _addActiveSegmentsEntry(
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND);

		SegmentsEntry defaultSegmentsEntry = _addActiveSegmentsEntry(
			SegmentsEntryConstants.SOURCE_DEFAULT);

		SegmentsExperience segmentsExperience = _addActiveSegmentsExperience(
			defaultSegmentsEntry);

		_verifyProcess.verify();

		asahFaroSegmentsEntry = _segmentsEntryLocalService.getSegmentsEntry(
			asahFaroSegmentsEntry.getSegmentsEntryId());

		Assert.assertTrue(asahFaroSegmentsEntry.isActive());

		defaultSegmentsEntry = _segmentsEntryLocalService.getSegmentsEntry(
			defaultSegmentsEntry.getSegmentsEntryId());

		Assert.assertFalse(defaultSegmentsEntry.isActive());

		segmentsExperience =
			_segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId());

		Assert.assertFalse(segmentsExperience.isActive());
	}

	private SegmentsEntry _addActiveSegmentsEntry(String source)
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			CriteriaSerializer.serialize(new Criteria()), source,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		if (!segmentsEntry.isActive()) {
			segmentsEntry.setActive(true);

			segmentsEntry = _segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry);
		}

		return segmentsEntry;
	}

	private SegmentsExperience _addActiveSegmentsExperience(
			SegmentsEntry segmentsEntry)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				layout.getPlid(), RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		if (!segmentsExperience.isActive()) {
			segmentsExperience.setActive(true);

			segmentsExperience =
				_segmentsExperienceLocalService.updateSegmentsExperience(
					segmentsExperience);
		}

		return segmentsExperience;
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "component.name=com.liferay.segments.internal.verify.SegmentsServiceVerifyProcess"
	)
	private VerifyProcess _verifyProcess;

}