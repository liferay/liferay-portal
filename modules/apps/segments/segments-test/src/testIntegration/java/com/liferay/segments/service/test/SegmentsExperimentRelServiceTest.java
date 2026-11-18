/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
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
import com.liferay.portal.kernel.service.LayoutLocalService;
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
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.segments.service.SegmentsExperimentRelService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class SegmentsExperimentRelServiceTest {

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

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_classNameId = _classNameLocalService.getClassNameId(Layout.class);

		_classPK = layout.getPlid();
	}

	@Test
	public void testAddSegmentsExperienceWithUpdateLayoutPermission()
		throws Exception {

		_addLayoutUpdateResourcePermission();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_addSegmentsExperimentRel(
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSegmentsExperienceWithoutUpdateLayoutsPermission()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_addSegmentsExperimentRel(
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSegmentsExperimentRelWithSegmentsExperienceFromDifferentGroup()
		throws Exception {

		_addLayoutUpdateResourcePermission();

		SegmentsExperience segmentsExperience = _addSegmentsExperience(
			GroupTestUtil.addGroup());
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperimentRelService.addSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperience.getSegmentsExperienceId(),
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteSegmentsExperimentRelWithSegmentsExperienceFromDifferentGroup()
		throws Exception {

		_addLayoutUpdateResourcePermission();

		SegmentsExperience segmentsExperience = _addSegmentsExperience(
			GroupTestUtil.addGroup());
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperience.getSegmentsExperienceId(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperimentRelService.deleteSegmentsExperimentRel(
				segmentsExperimentRel.getSegmentsExperimentRelId());
		}
	}

	private void _addLayoutUpdateResourcePermission() throws Exception {
		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.UPDATE);
	}

	private SegmentsExperience _addSegmentsExperience() throws Exception {
		return _addSegmentsExperience(_group);
	}

	private SegmentsExperience _addSegmentsExperience(Group group)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		return SegmentsTestUtil.addSegmentsExperience(
			group.getGroupId(), layout.getPlid());
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

	private SegmentsExperimentRel _addSegmentsExperimentRel(
			ServiceContext serviceContext)
		throws Exception {

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		return _segmentsExperimentRelService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience.getSegmentsExperienceId(), serviceContext);
	}

	private long _classNameId;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private long _classPK;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

	@Inject
	private SegmentsExperimentRelService _segmentsExperimentRelService;

	@DeleteAfterTestRun
	private User _user;

}