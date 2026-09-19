/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.exception.LockedSegmentsExperimentException;
import com.liferay.segments.exception.SegmentsExperimentRelNameException;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.model.SegmentsExperimentRelModel;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class SegmentsExperimentRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddSegmentsExperimentRel() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperience.getSegmentsExperienceId(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertNotNull(segmentsExperimentRel);
		Assert.assertEquals(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperimentRel.getSegmentsExperimentId());
		Assert.assertEquals(
			segmentsExperiment.getSegmentsExperimentKey(),
			segmentsExperimentRel.getSegmentsExperimentKey());
		Assert.assertEquals(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperimentRel.getSegmentsExperienceId());
		Assert.assertEquals(
			segmentsExperience.getSegmentsExperienceKey(),
			segmentsExperimentRel.getSegmentsExperienceKey());
		Assert.assertEquals(
			segmentsExperience.getName(LocaleUtil.getDefault()),
			segmentsExperimentRel.getName(LocaleUtil.getDefault()));
	}

	@Test(expected = LockedSegmentsExperimentException.class)
	public void testAddSegmentsExperimentRelWithLockedExperiment()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testDeleteSegmentsExperimentRel() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		segmentsExperience.setActive(false);

		segmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperience(
				segmentsExperience);

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperience.getSegmentsExperienceId(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentRelLocalService.deleteSegmentsExperimentRel(
			segmentsExperimentRel);

		Assert.assertNull(
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId()));
	}

	@Test(expected = LockedSegmentsExperimentException.class)
	public void testDeleteSegmentsExperimentRelWithLockedExperiment()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperience.getSegmentsExperienceId(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentRelLocalService.deleteSegmentsExperimentRel(
			segmentsExperimentRel);

		Assert.assertNull(
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId()));
	}

	@Test
	public void testDeleteSegmentsExperimentRels() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience = _addSegmentsExperience();

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentRelLocalService.deleteSegmentsExperimentRels(
			segmentsExperiment.getSegmentsExperimentId());

		List<SegmentsExperimentRel> segmentsExperimentRels =
			_segmentsExperimentRelLocalService.getSegmentsExperimentRels(
				segmentsExperiment.getSegmentsExperimentId());

		Assert.assertTrue(segmentsExperimentRels.isEmpty());
	}

	@Test
	public void testGetSegmentsExperimentRels() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience segmentsExperience1 = _addSegmentsExperience();
		SegmentsExperience segmentsExperience2 = _addSegmentsExperience();

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience1.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience2.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		List<SegmentsExperimentRel> segmentsExperimentRels =
			_segmentsExperimentRelLocalService.getSegmentsExperimentRels(
				segmentsExperiment.getSegmentsExperimentId());

		Assert.assertEquals(
			segmentsExperimentRels.toString(), 3,
			segmentsExperimentRels.size());

		long[] segmentsExperienceIds = TransformUtil.transformToLongArray(
			segmentsExperimentRels,
			SegmentsExperimentRelModel::getSegmentsExperienceId);

		Assert.assertTrue(
			ArrayUtil.containsAll(
				segmentsExperienceIds,
				new long[] {
					segmentsExperiment.getSegmentsExperienceId(),
					segmentsExperience1.getSegmentsExperienceId(),
					segmentsExperience2.getSegmentsExperienceId()
				}));
	}

	@Test(expected = SegmentsExperimentRelNameException.class)
	public void testUpdateSegmentsExperimentRelControl() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.getSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperiment.getSegmentsExperienceKey());

		_segmentsExperimentRelLocalService.updateSegmentsExperimentRel(
			segmentsExperimentRel.getSegmentsExperimentRelId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testUpdateSegmentsExperimentRelNoControl() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperience.getSegmentsExperienceId(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		String name = RandomTestUtil.randomString();

		SegmentsExperimentRel updatedSegmentsExperimentRel =
			_segmentsExperimentRelLocalService.updateSegmentsExperimentRel(
				segmentsExperimentRel.getSegmentsExperimentRelId(), name,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			name,
			updatedSegmentsExperimentRel.getName(LocaleUtil.getDefault()));
	}

	@Test(expected = LockedSegmentsExperimentException.class)
	public void testUpdateSegmentsExperimentRelWithLockedExperiment()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.getSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperiment.getSegmentsExperienceKey());

		_segmentsExperimentRelLocalService.updateSegmentsExperimentRel(
			segmentsExperimentRel.getSegmentsExperimentRelId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private SegmentsExperience _addSegmentsExperience() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), draftLayout.getPlid());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), draftLayout.getPlid());

		_layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperience.getSegmentsExperienceId(),
				layoutPageTemplateStructure.getDefaultSegmentsExperienceData(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		return _segmentsExperienceLocalService.fetchSegmentsExperience(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceKey(),
			layout.getPlid());
	}

	private SegmentsExperiment _addSegmentsExperiment() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		return _addSegmentsExperiment(segmentsExperience);
	}

	private SegmentsExperiment _addSegmentsExperiment(
			SegmentsExperience segmentsExperience)
		throws Exception {

		return SegmentsTestUtil.addSegmentsExperiment(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Inject
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

}