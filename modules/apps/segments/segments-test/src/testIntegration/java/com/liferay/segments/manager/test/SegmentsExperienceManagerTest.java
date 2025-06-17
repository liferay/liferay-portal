/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class SegmentsExperienceManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_segmentsExperienceManager = new SegmentsExperienceManager(
			_segmentsExperienceLocalService);
	}

	@Test
	public void testGetSegmentsExperienceIdWithoutSegmentsExperienceIds() {
		Assert.assertEquals(
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()),
			_segmentsExperienceManager.getSegmentsExperienceId(
				_getMockHttpServletRequest(false)));
	}

	@Test
	public void testGetSegmentsExperienceIdWithSegmentsExperienceId1()
		throws PortalException {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(true);

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		mockHttpServletRequest.setAttribute(
			SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
			new long[] {segmentsExperience1.getSegmentsExperienceId()});

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		mockHttpServletRequest.setParameter(
			"segmentsExperienceId",
			String.valueOf(segmentsExperience2.getSegmentsExperienceId()));

		Assert.assertEquals(
			segmentsExperience2.getSegmentsExperienceId(),
			_segmentsExperienceManager.getSegmentsExperienceId(
				mockHttpServletRequest));
	}

	@Test
	public void testGetSegmentsExperienceIdWithSegmentsExperienceId2()
		throws PortalException {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(false);

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		mockHttpServletRequest.setAttribute(
			SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
			new long[] {segmentsExperience1.getSegmentsExperienceId()});

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		mockHttpServletRequest.setParameter(
			"segmentsExperienceId",
			String.valueOf(segmentsExperience2.getSegmentsExperienceId()));

		Assert.assertEquals(
			segmentsExperience1.getSegmentsExperienceId(),
			_segmentsExperienceManager.getSegmentsExperienceId(
				mockHttpServletRequest));
	}

	@Test
	public void testGetSegmentsExperienceIdWithSegmentsExperienceIds()
		throws PortalException {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(false);

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		mockHttpServletRequest.setAttribute(
			SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
			new long[] {segmentsExperience.getSegmentsExperienceId()});

		Assert.assertEquals(
			segmentsExperience.getSegmentsExperienceId(),
			_segmentsExperienceManager.getSegmentsExperienceId(
				mockHttpServletRequest));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		boolean groupAdmin) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(_layout);
		themeDisplay.setPermissionChecker(
			_getMockPermissionChecker(groupAdmin));
		themeDisplay.setPlid(_layout.getPlid());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private PermissionChecker _getMockPermissionChecker(boolean groupAdmin) {
		return new SimplePermissionChecker() {

			@Override
			public boolean isGroupAdmin(long groupId) {
				return groupAdmin;
			}

		};
	}

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private SegmentsExperienceManager _segmentsExperienceManager;

}