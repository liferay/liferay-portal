/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.processor.SegmentsExperienceRequestProcessor;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class DefaultSegmentsExperienceRequestProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());
	}

	@Test
	public void testGetSegmentsExperienceIds() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					segmentsEntry.getGroupId(), _group.getGroupId()),
				layout.getPlid(), RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		long[] segmentsExperienceIds =
			_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
				new MockHttpServletRequest(), new MockHttpServletResponse(),
				_group.getGroupId(), layout.getPlid(), new long[0]);

		Assert.assertEquals(
			Arrays.toString(segmentsExperienceIds), 2,
			segmentsExperienceIds.length);
		Assert.assertTrue(
			ArrayUtil.contains(
				segmentsExperienceIds,
				segmentsExperience.getSegmentsExperienceId()));
	}

	@Test
	@TestInfo("LPD-73850")
	public void testGetSegmentsExperienceIdsWithSegmentEntryIds()
		throws Exception {

		User user = _userLocalService.getUser(TestPropsValues.getUserId());

		SegmentsEntry segmentsEntry1 = _getSegmentsEntry(
			_group.getGroupId(), user);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry1.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					segmentsEntry1.getGroupId(), _group.getGroupId()),
				layout.getPlid(), RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		SegmentsEntry segmentsEntry2 = _getSegmentsEntry(
			companyGroup.getGroupId(), user);

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry2.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					segmentsEntry2.getGroupId(), _group.getGroupId()),
				layout.getPlid(), RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperience segmentsExperience3 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				layout.getPlid(), RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertArrayEquals(
			new long[] {
				segmentsExperience3.getSegmentsExperienceId(),
				segmentsExperience2.getSegmentsExperienceId(),
				segmentsExperience1.getSegmentsExperienceId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid())
			},
			_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
				new MockHttpServletRequest(), new MockHttpServletResponse(),
				_group.getGroupId(), layout.getPlid(),
				new long[] {
					SegmentsEntryConstants.ID_DEFAULT,
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				},
				new long[0]));
	}

	@Test
	public void testGetSegmentsExperienceIdsWithoutSegmentsExperienceIds()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		long[] segmentsExperienceIds =
			_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
				new MockHttpServletRequest(), new MockHttpServletResponse(),
				_group.getGroupId(), layout.getPlid(), new long[0]);

		Assert.assertEquals(
			Arrays.toString(segmentsExperienceIds), 1,
			segmentsExperienceIds.length);
	}

	@Test
	public void testGetSegmentsExperienceIdsWithoutSegmentsExperienceIdsAndWithoutSegmentEntryIds()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		long[] segmentsExperienceIds =
			_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
				new MockHttpServletRequest(), new MockHttpServletResponse(),
				_group.getGroupId(), layout.getPlid(),
				new long[] {SegmentsEntryConstants.ID_DEFAULT}, new long[0]);

		Assert.assertEquals(
			Arrays.toString(segmentsExperienceIds), 1,
			segmentsExperienceIds.length);
	}

	private SegmentsEntry _getSegmentsEntry(long groupId, User user)
		throws Exception {

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria, String.format("(firstName eq '%s')", user.getFirstName()),
			Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			groupId, CriteriaSerializer.serialize(criteria));
	}

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "component.name=com.liferay.segments.internal.processor.DefaultSegmentsExperienceRequestProcessor"
	)
	private SegmentsExperienceRequestProcessor
		_segmentsExperienceRequestProcessor;

	@Inject
	private UserLocalService _userLocalService;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}