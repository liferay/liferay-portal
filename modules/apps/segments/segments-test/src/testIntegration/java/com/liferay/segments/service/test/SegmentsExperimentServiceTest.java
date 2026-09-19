/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperimentService;
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
public class SegmentsExperimentServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_role = RoleLocalServiceUtil.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_SITE,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_user = UserTestUtil.addGroupUser(_group, _role.getName());
	}

	@Test
	public void testAddSegmentsExperimentWithUpdateLayoutPermission()
		throws Exception {

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_addSegmentsExperiment(
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSegmentsExperimentWithoutManageSegmentsEntriesPermission()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_addSegmentsExperiment(
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));
		}
	}

	@Test
	public void testDeleteSegmentsExperimentWithDeletePermission()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(),
			"com.liferay.segments.model.SegmentsExperiment",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.DELETE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperimentService.deleteSegmentsExperiment(
				segmentsExperiment.getSegmentsExperimentId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteSegmentsExperimentWithoutDeletePermission()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperimentService.deleteSegmentsExperiment(
				segmentsExperiment.getSegmentsExperimentId());
		}
	}

	@Test
	public void testDeleteSegmentsExperimentWithoutDeletePermissionAndWithUpdateLayoutPermission()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperimentService.deleteSegmentsExperiment(
				segmentsExperiment.getSegmentsExperimentId());
		}
	}

	@Test
	public void testFetchSegmentsExperimentsWithoutViewPermission()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment(layout);

		for (Role role : RoleLocalServiceUtil.getRoles(_group.getCompanyId())) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				_group.getCompanyId(),
				"com.liferay.segments.model.SegmentsExperiment",
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperiment.getSegmentsExperimentId()),
				role.getRoleId(), ActionKeys.VIEW);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertNull(
				_segmentsExperimentService.fetchSegmentsExperiment(
					layout.getGroupId(),
					segmentsExperiment.getSegmentsExperienceKey(),
					layout.getPlid()));
		}
	}

	@Test
	public void testGetSegmentsExperimentsWithViewPermission()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment(layout);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertEquals(
				segmentsExperiment,
				_segmentsExperimentService.fetchSegmentsExperiment(
					layout.getGroupId(),
					segmentsExperiment.getSegmentsExperienceKey(),
					layout.getPlid()));
		}
	}

	@Test
	public void testUpdateSegmentsExperimentWithUpdatePermission()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(),
			"com.liferay.segments.model.SegmentsExperiment",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperimentService.updateSegmentsExperimentStatus(
				segmentsExperiment.getSegmentsExperimentKey(),
				SegmentsExperimentConstants.STATUS_RUNNING);
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateSegmentsExperimentWithoutUpdatePermission()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperimentService.updateSegmentsExperimentStatus(
				segmentsExperiment.getSegmentsExperimentKey(),
				SegmentsExperimentConstants.STATUS_TERMINATED);
		}
	}

	private SegmentsExperiment _addSegmentsExperiment() throws Exception {
		return _addSegmentsExperiment(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private SegmentsExperiment _addSegmentsExperiment(Layout layout)
		throws Exception {

		return _addSegmentsExperiment(
			layout,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private SegmentsExperiment _addSegmentsExperiment(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), layout.getPlid());

		return _segmentsExperimentService.addSegmentsExperiment(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK, serviceContext);
	}

	private SegmentsExperiment _addSegmentsExperiment(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		return _addSegmentsExperiment(layout, serviceContext);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private SegmentsExperimentService _segmentsExperimentService;

	@DeleteAfterTestRun
	private User _user;

}