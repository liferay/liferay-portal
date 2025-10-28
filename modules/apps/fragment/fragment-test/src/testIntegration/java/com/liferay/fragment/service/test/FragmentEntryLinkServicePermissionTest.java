/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkServicePermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.POWER_USER);

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		_fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());
	}

	@Test
	public void testAddFragmentEntryLink() throws Exception {
		_addSiteMemberUpdatePermission();

		UserTestUtil.setUser(_user);

		_fragmentEntryLinkService.addFragmentEntryLink(
			null, _group.getGroupId(), null,
			_fragmentEntry.getExternalReferenceCode(), null,
			_defaultSegmentsExperienceId, _layout.getPlid(), StringPool.BLANK,
			"<div>test</div>", StringPool.BLANK, "{fieldSets: []}",
			StringPool.BLANK, StringPool.BLANK, 0, null,
			_fragmentEntry.getType(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentEntryLinkWithoutPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentEntryLinkService.addFragmentEntryLink(
			null, _group.getGroupId(), null,
			_fragmentEntry.getExternalReferenceCode(), null,
			_defaultSegmentsExperienceId, _layout.getPlid(),
			_fragmentEntry.getCss(), _fragmentEntry.getHtml(),
			_fragmentEntry.getJs(), _fragmentEntry.getConfiguration(),
			StringPool.BLANK, StringPool.BLANK, 0, null,
			_fragmentEntry.getType(), serviceContext);
	}

	@Test
	public void testDeleteFragmentEntryLink() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			FragmentTestUtil.addFragmentEntryLink(
				_fragmentEntry, _layout.getPlid());

		_addSiteMemberUpdatePermission();

		UserTestUtil.setUser(_user);

		_fragmentEntryLinkService.deleteFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteFragmentEntryLinkWithoutPermissions()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentTestUtil.addFragmentEntryLink(
				_fragmentEntry, _layout.getPlid());

		UserTestUtil.setUser(_user);

		_fragmentEntryLinkService.deleteFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId());
	}

	@Test
	public void testUpdateFragmentEntryLink() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			FragmentTestUtil.addFragmentEntryLink(
				_fragmentEntry, _layout.getPlid());

		_addSiteMemberUpdatePermission();

		UserTestUtil.setUser(_user);

		_fragmentEntryLinkService.updateFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId(),
			_createEditableValues());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateFragmentEntryLinkWithoutPermissions()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentTestUtil.addFragmentEntryLink(
				_fragmentEntry, _layout.getPlid());

		UserTestUtil.setUser(_user);

		_fragmentEntryLinkService.updateFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId(),
			RandomTestUtil.randomString());
	}

	private void _addSiteMemberUpdatePermission() throws Exception {
		Role siteMemberRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			TestPropsValues.getCompanyId(),
			"com.liferay.portal.kernel.model.Layout",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			siteMemberRole.getRoleId(), ActionKeys.UPDATE);
	}

	private String _createEditableValues() {
		JSONObject jsonObject = JSONUtil.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		return jsonObject.toString();
	}

	private long _defaultSegmentsExperienceId;
	private FragmentEntry _fragmentEntry;

	@Inject
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@DeleteAfterTestRun
	private User _user;

}