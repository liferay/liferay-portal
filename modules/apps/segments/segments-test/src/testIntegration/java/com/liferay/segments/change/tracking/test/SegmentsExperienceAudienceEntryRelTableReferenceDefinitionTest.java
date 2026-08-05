/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.change.tracking.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.change.tracking.test.util.BaseTableReferenceDefinitionTestCase;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Cheryl Tang
 */
@RunWith(Arquillian.class)
public class SegmentsExperienceAudienceEntryRelTableReferenceDefinitionTest
	extends BaseTableReferenceDefinitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_audiencesEntry = _audiencesEntryLocalService.addAudiencesEntry(
			null, TestPropsValues.getUserId(), StringPool.BLANK,
			RandomTestUtil.randomString());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		_segmentsExperience = SegmentsTestUtil.addSegmentsExperience(
			group.getGroupId(), layout.getPlid());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		List<SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels =
				_segmentsExperienceAudienceEntryRelLocalService.
					updateSegmentsExperienceAudienceEntryRels(
						TestPropsValues.getUserId(), group.getGroupId(),
						new String[] {
							_audiencesEntry.getExternalReferenceCode()
						},
						_segmentsExperience.getExternalReferenceCode());

		Assert.assertEquals(
			segmentsExperienceAudienceEntryRels.toString(), 1,
			segmentsExperienceAudienceEntryRels.size());

		return segmentsExperienceAudienceEntryRels.get(0);
	}

	@DeleteAfterTestRun
	private AudiencesEntry _audiencesEntry;

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	private SegmentsExperience _segmentsExperience;

	@Inject
	private SegmentsExperienceAudienceEntryRelLocalService
		_segmentsExperienceAudienceEntryRelLocalService;

}