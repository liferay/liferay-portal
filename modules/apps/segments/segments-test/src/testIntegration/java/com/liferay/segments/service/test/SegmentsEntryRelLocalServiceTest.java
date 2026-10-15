/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class SegmentsEntryRelLocalServiceTest {

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
	public void testAddSegmentsEntryRel() throws PortalException {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		int classNameId = RandomTestUtil.randomInt();
		int classPK = RandomTestUtil.randomInt();

		SegmentsEntryRel segmentsEntryRel =
			_segmentsEntryRelLocalService.addSegmentsEntryRel(
				segmentsEntry.getSegmentsEntryId(), classNameId, classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertNotNull(segmentsEntryRel);
		Assert.assertEquals(
			segmentsEntry.getSegmentsEntryId(),
			segmentsEntryRel.getSegmentsEntryId());
		Assert.assertEquals(classNameId, segmentsEntryRel.getClassNameId());
		Assert.assertEquals(classPK, segmentsEntryRel.getClassPK());
	}

	@Test
	public void testDeleteSegmentsEntryRel() throws PortalException {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		int classNameId = RandomTestUtil.randomInt();
		int classPK = RandomTestUtil.randomInt();

		_segmentsEntryRelLocalService.addSegmentsEntryRel(
			segmentsEntry.getSegmentsEntryId(), classNameId, classPK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsEntryRelLocalService.deleteSegmentsEntryRel(
			segmentsEntry.getSegmentsEntryId(), classNameId, classPK);

		Assert.assertEquals(
			0,
			_segmentsEntryRelLocalService.getSegmentsEntryRelsCount(
				segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testDeleteSegmentsEntryRelsByClassNameIdAndClassPK()
		throws PortalException {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		int classNameId = RandomTestUtil.randomInt();
		int classPK = RandomTestUtil.randomInt();

		_segmentsEntryRelLocalService.addSegmentsEntryRel(
			segmentsEntry.getSegmentsEntryId(), classNameId, classPK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsEntryRelLocalService.deleteSegmentsEntryRels(
			classNameId, classPK);

		Assert.assertEquals(
			0,
			_segmentsEntryRelLocalService.getSegmentsEntryRelsCount(
				segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testDeleteSegmentsEntryRelsBySegmentsEntryId()
		throws PortalException {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsEntryRelLocalService.addSegmentsEntryRel(
			segmentsEntry.getSegmentsEntryId(), RandomTestUtil.randomInt(),
			RandomTestUtil.randomInt(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsEntryRelLocalService.deleteSegmentsEntryRels(
			segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			0,
			_segmentsEntryRelLocalService.getSegmentsEntryRelsCount(
				segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testHasSegmentsEntryRel() throws PortalException {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		int classNameId = RandomTestUtil.randomInt();
		int classPK = RandomTestUtil.randomInt();

		_segmentsEntryRelLocalService.addSegmentsEntryRel(
			segmentsEntry.getSegmentsEntryId(), classNameId, classPK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertTrue(
			_segmentsEntryRelLocalService.hasSegmentsEntryRel(
				segmentsEntry.getSegmentsEntryId(), classNameId, classPK));
	}

	@Test
	public void testHasSegmentsEntryRelWithAnotherSegmentsEntry()
		throws PortalException {

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());
		int classNameId = RandomTestUtil.randomInt();
		int classPK = RandomTestUtil.randomInt();

		_segmentsEntryRelLocalService.addSegmentsEntryRel(
			segmentsEntry1.getSegmentsEntryId(), classNameId, classPK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertTrue(
			_segmentsEntryRelLocalService.hasSegmentsEntryRel(
				segmentsEntry1.getSegmentsEntryId(), classNameId, classPK));

		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		Assert.assertFalse(
			_segmentsEntryRelLocalService.hasSegmentsEntryRel(
				segmentsEntry2.getSegmentsEntryId(), classNameId, classPK));
	}

	@Test
	public void testHasSegmentsEntryRelWithNoSegmentsEntryRels()
		throws PortalException {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		Assert.assertFalse(
			_segmentsEntryRelLocalService.hasSegmentsEntryRel(
				segmentsEntry.getSegmentsEntryId(), RandomTestUtil.randomInt(),
				RandomTestUtil.randomInt()));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

}