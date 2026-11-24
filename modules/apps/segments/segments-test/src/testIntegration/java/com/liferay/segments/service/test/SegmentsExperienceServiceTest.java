/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsActionKeys;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.exception.DuplicateSegmentsExperienceExternalReferenceCodeException;
import com.liferay.segments.exception.NoSuchExperienceException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperienceService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.List;
import java.util.Objects;

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
public class SegmentsExperienceServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		_role = RoleLocalServiceUtil.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_SITE,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_user = UserTestUtil.addGroupUser(_group, _role.getName());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_classPK = layout.getPlid();
	}

	@Test
	@TestInfo("LPD-116164")
	public void testAddSegmentsExperience() throws Exception {
		_testAddSegmentsExperience(_companyGroup);
		_testAddSegmentsExperience(_group);
		_testAddSegmentsExperienceByExternalReferenceCodeWithoutPermissions();
		_testAddSegmentsExperienceWithExistingExternalReferenceCode();
		_testAddSegmentsExperienceWithManageSegmentsEntriesPermission();
		_testAddSegmentsExperienceWithManageSegmentsEntriesPermissionInAnotherGroup();
		_testAddSegmentsExperienceWithoutManageSegmentsEntriesPermission();
		_testAddSegmentsExperienceWithoutManageSegmentsEntriesPermissionAndWithUpdateLayoutPermission();
		_testAddSegmentsExperienceWithoutTypeSettings();
	}

	@Test
	@TestInfo("LPD-116164")
	public void testAppendSegmentsExperience() throws Exception {
		_testAppendSegmentsExperience();
		_testAppendSegmentsExperienceWithManageSegmentsEntriesPermissionInAnotherGroup();
		_testAppendSegmentsExperienceWithoutTypeSettings();
	}

	@Test
	public void testDeleteSegmentsExperience() throws Exception {
		_testDeleteSegmentsExperienceByExternalReferenceCode();
		_testDeleteSegmentsExperienceByExternalReferenceCodeWithoutPermissions();
		_testDeleteSegmentsExperienceWithDeletePermission();
		_testDeleteSegmentsExperienceWithoutDeletePermission();
		_testDeleteSegmentsExperienceWithoutDeletePermissionAndWithUpdateLayoutPermission();
	}

	@Test
	public void testGetSegmentsExperience() throws Exception {
		_testGetSegmentsExperienceWithViewPermission();
		_testGetSegmentsExperienceWithoutViewPermission();
		_testGetSegmentsExperienceWithoutViewPermissionAndWithUpdateLayoutPermission();
	}

	@Test
	public void testGetSegmentsExperiences() throws Exception {
		_testGetSegmentsExperiencesWithViewPermission();
		_testGetSegmentsExperiencesWithoutViewPermission();
		_testGetSegmentsExperiencesWithoutViewPermissionAndWithUpdateLayoutPermission();
	}

	@Test
	public void testGetSegmentsExperiencesCount() throws Exception {
		_testGetSegmentsExperiencesCountWithViewPermission();
		_testGetSegmentsExperiencesCountWithoutViewPermission();
		_testGetSegmentsExperiencesCountWithoutViewPermissionAndWithUpdateLayoutPermission();
	}

	@Test
	public void testUpdateSegmentsExperience() throws Exception {
		_testUpdateSegmentsExperience(_companyGroup);
		_testUpdateSegmentsExperience(_group);
		_testUpdateSegmentsExperienceWithUpdatePermission();
		_testUpdateSegmentsExperienceWithoutTypeSettings();
		_testUpdateSegmentsExperienceWithoutUpdatePermission();
		_testUpdateSegmentsExperienceWithoutUpdatePermissionAndWithUpdateLayoutPermission();
	}

	private SegmentsExperience _addSegmentsExperience(
			ServiceContext serviceContext)
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		return _segmentsExperienceService.addSegmentsExperience(
			null, _group.getGroupId(), segmentsEntry.getExternalReferenceCode(),
			null, _classPK, RandomTestUtil.randomLocaleStringMap(), true,
			new UnicodeProperties(true), serviceContext);
	}

	private void _assertSegmentsExperience(
		SegmentsExperience segmentsExperience, String segmentsEntryERC,
		String segmentsEntryScopeERC) {

		UnicodeProperties actualTypeSettingsUnicodeProperties =
			segmentsExperience.getTypeSettingsUnicodeProperties();

		if (segmentsExperience.hasDefaultSegmentsEntry() &&
			Objects.equals(
				segmentsExperience.getSegmentsExperienceKey(),
				SegmentsExperienceConstants.KEY_DEFAULT)) {

			Assert.assertTrue(segmentsExperience.isDefault());
			Assert.assertEquals(
				SegmentsEntryConstants.ID_DEFAULT,
				segmentsExperience.getSegmentsEntryId());
		}
		else {
			Assert.assertFalse(segmentsExperience.isDefault());
			Assert.assertNotEquals(
				SegmentsEntryConstants.ID_DEFAULT,
				segmentsExperience.getSegmentsEntryId());
		}

		if (segmentsExperience.hasDefaultSegmentsEntry()) {
			Assert.assertTrue(Validator.isNull(segmentsEntryERC));
		}
		else {
			Assert.assertEquals(
				segmentsEntryERC, segmentsExperience.getSegmentsEntryERC());
		}

		if (Validator.isNull(segmentsEntryScopeERC)) {
			Assert.assertTrue(
				Validator.isNull(
					segmentsExperience.getSegmentsEntryScopeERC()));
		}
		else {
			Assert.assertEquals(
				segmentsEntryScopeERC,
				segmentsExperience.getSegmentsEntryScopeERC());
		}

		Assert.assertEquals(
			"value",
			actualTypeSettingsUnicodeProperties.getProperty("property"));
	}

	private SegmentsExperience _getDefaultSegmentsExperience(long plid)
		throws Exception {

		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceService.getSegmentsExperiences(
				_group.getGroupId(), plid, true);

		Assert.assertEquals(
			segmentsExperiences.toString(), 1, segmentsExperiences.size());

		return segmentsExperiences.get(0);
	}

	private void _testAddSegmentsExperience(Group group) throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			group.getGroupId());

		String segmentsEntryScopeERC =
			ScopeUtil.getItemScopeExternalReferenceCode(
				segmentsEntry.getGroupId(), _group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.addSegmentsExperience(
				null, _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), segmentsEntryScopeERC,
				_classPK, RandomTestUtil.randomLocaleStringMap(), true,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"property", "value"
				).build(),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		_assertSegmentsExperience(
			segmentsExperience, segmentsEntry.getExternalReferenceCode(),
			segmentsEntryScopeERC);
	}

	private void _testAddSegmentsExperienceByExternalReferenceCodeWithoutPermissions()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			String externalReferenceCode = StringUtil.randomString();

			SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
				_group.getGroupId());

			Assert.assertThrows(
				PrincipalException.class,
				() -> _segmentsExperienceService.addSegmentsExperience(
					externalReferenceCode, _group.getGroupId(),
					segmentsEntry.getExternalReferenceCode(), null, _classPK,
					RandomTestUtil.randomLocaleStringMap(), true,
					new UnicodeProperties(true),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId())));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private void _testAddSegmentsExperienceWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsExperienceService.addSegmentsExperience(
			externalReferenceCode, _group.getGroupId(),
			segmentsEntry.getExternalReferenceCode(), null, _classPK,
			RandomTestUtil.randomLocaleStringMap(), true,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertThrows(
			DuplicateSegmentsExperienceExternalReferenceCodeException.class,
			() -> _segmentsExperienceService.addSegmentsExperience(
				externalReferenceCode, _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _classPK,
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId())));
	}

	private void _testAddSegmentsExperienceWithManageSegmentsEntriesPermission()
		throws Exception {

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(), "com.liferay.segments",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_addSegmentsExperience(
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));
		}

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			_group.getCompanyId(), "com.liferay.segments",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);
	}

	private void _testAddSegmentsExperienceWithManageSegmentsEntriesPermissionInAnotherGroup()
		throws Exception {

		_otherGroup = GroupTestUtil.addGroup();

		_otherGroupUser = UserTestUtil.addGroupUser(
			_otherGroup, _role.getName());

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_otherGroup.getCompanyId(), "com.liferay.segments",
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_otherGroup.getGroupId()), _role.getRoleId(),
			SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		SegmentsEntry otherGroupSegmentsEntry =
			SegmentsTestUtil.addSegmentsEntry(_otherGroup.getGroupId());

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		int segmentsExperiencesCount =
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _classPK);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_otherGroupUser,
				PermissionCheckerFactoryUtil.create(_otherGroupUser))) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_otherGroup, _otherGroupUser.getUserId());

			Assert.assertThrows(
				NoSuchLayoutException.class,
				() -> _segmentsExperienceService.addSegmentsExperience(
					null, _otherGroup.getGroupId(),
					otherGroupSegmentsEntry.getExternalReferenceCode(), null,
					_classPK, RandomTestUtil.randomLocaleStringMap(), true,
					new UnicodeProperties(true), serviceContext));
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _segmentsExperienceService.addSegmentsExperience(
					null, _group.getGroupId(),
					segmentsEntry.getExternalReferenceCode(), null, _classPK,
					RandomTestUtil.randomLocaleStringMap(), true,
					new UnicodeProperties(true), serviceContext));
		}

		Assert.assertEquals(
			segmentsExperiencesCount,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _classPK));
	}

	private void _testAddSegmentsExperienceWithoutManageSegmentsEntriesPermission()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _addSegmentsExperience(
					ServiceContextTestUtil.getServiceContext(
						_group, _user.getUserId())));
		}
	}

	private void _testAddSegmentsExperienceWithoutManageSegmentsEntriesPermissionAndWithUpdateLayoutPermission()
		throws Exception {

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_addSegmentsExperience(
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId()));
		}
	}

	private void _testAddSegmentsExperienceWithoutTypeSettings()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.addSegmentsExperience(
				null, _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _classPK,
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		Assert.assertEquals(
			StringPool.BLANK, segmentsExperience.getTypeSettings());
	}

	private void _testAppendSegmentsExperience() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.appendSegmentsExperience(
				_group.getGroupId(), segmentsEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					segmentsEntry.getGroupId(), _group.getGroupId()),
				_classPK, RandomTestUtil.randomLocaleStringMap(), true,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"property", "value"
				).build(),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		UnicodeProperties actualTypeSettingsUnicodeProperties =
			segmentsExperience.getTypeSettingsUnicodeProperties();

		Assert.assertEquals(
			"value",
			actualTypeSettingsUnicodeProperties.getProperty("property"));
	}

	private void _testAppendSegmentsExperienceWithManageSegmentsEntriesPermissionInAnotherGroup()
		throws Exception {

		_otherGroup = GroupTestUtil.addGroup();

		_otherGroupUser = UserTestUtil.addGroupUser(
			_otherGroup, _role.getName());

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_otherGroup.getCompanyId(), "com.liferay.segments",
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_otherGroup.getGroupId()), _role.getRoleId(),
			SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		SegmentsEntry otherGroupSegmentsEntry =
			SegmentsTestUtil.addSegmentsEntry(_otherGroup.getGroupId());

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		int segmentsExperiencesCount =
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _classPK);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_otherGroupUser,
				PermissionCheckerFactoryUtil.create(_otherGroupUser))) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_otherGroup, _otherGroupUser.getUserId());

			Assert.assertThrows(
				NoSuchLayoutException.class,
				() -> _segmentsExperienceService.appendSegmentsExperience(
					_otherGroup.getGroupId(),
					otherGroupSegmentsEntry.getExternalReferenceCode(), null,
					_classPK, RandomTestUtil.randomLocaleStringMap(), true,
					serviceContext));
			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _segmentsExperienceService.appendSegmentsExperience(
					_group.getGroupId(),
					segmentsEntry.getExternalReferenceCode(), null, _classPK,
					RandomTestUtil.randomLocaleStringMap(), true,
					serviceContext));
		}

		Assert.assertEquals(
			segmentsExperiencesCount,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _classPK));
	}

	private void _testAppendSegmentsExperienceWithoutTypeSettings()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.appendSegmentsExperience(
				_group.getGroupId(), segmentsEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					segmentsEntry.getGroupId(), _group.getGroupId()),
				_classPK, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		Assert.assertEquals(
			StringPool.BLANK, segmentsExperience.getTypeSettings());
	}

	private void _testDeleteSegmentsExperienceByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsExperienceService.addSegmentsExperience(
			null, _group.getGroupId(), segmentsEntry.getExternalReferenceCode(),
			null, _classPK, RandomTestUtil.randomLocaleStringMap(), true,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertThrows(
			NoSuchExperienceException.class,
			() -> _segmentsExperienceService.deleteSegmentsExperience(
				externalReferenceCode, _group.getGroupId()));
	}

	private void _testDeleteSegmentsExperienceByExternalReferenceCodeWithoutPermissions()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsExperienceService.addSegmentsExperience(
			externalReferenceCode, _group.getGroupId(),
			segmentsEntry.getExternalReferenceCode(), null, _classPK,
			RandomTestUtil.randomLocaleStringMap(), true,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			Assert.assertThrows(
				PrincipalException.class,
				() -> _segmentsExperienceService.deleteSegmentsExperience(
					externalReferenceCode, _group.getGroupId()));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private void _testDeleteSegmentsExperienceWithDeletePermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(),
			"com.liferay.segments.model.SegmentsExperience",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.DELETE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperienceService.deleteSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId());
		}

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			_group.getCompanyId(),
			"com.liferay.segments.model.SegmentsExperience",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.DELETE);
	}

	private void _testDeleteSegmentsExperienceWithoutDeletePermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _segmentsExperienceService.deleteSegmentsExperience(
					segmentsExperience.getSegmentsExperienceId()));
		}
	}

	private void _testDeleteSegmentsExperienceWithoutDeletePermissionAndWithUpdateLayoutPermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperienceService.deleteSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId());
		}
	}

	private void _testGetSegmentsExperienceWithViewPermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperienceService.getSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId());

			_segmentsExperienceService.getSegmentsExperience(
				segmentsExperience.getGroupId(),
				segmentsExperience.getSegmentsExperienceKey(),
				segmentsExperience.getPlid());
		}
	}

	private void _testGetSegmentsExperienceWithoutViewPermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		List<Role> roles = RoleLocalServiceUtil.getRoles(_group.getCompanyId());

		for (Role role : roles) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				_group.getCompanyId(),
				"com.liferay.segments.model.SegmentsExperience",
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience.getSegmentsExperienceId()),
				role.getRoleId(), ActionKeys.VIEW);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _segmentsExperienceService.getSegmentsExperience(
					segmentsExperience.getSegmentsExperienceId()));
		}
	}

	private void _testGetSegmentsExperienceWithoutViewPermissionAndWithUpdateLayoutPermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		List<Role> roles = RoleLocalServiceUtil.getRoles(_group.getCompanyId());

		for (Role role : roles) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				_group.getCompanyId(),
				"com.liferay.segments.model.SegmentsExperience",
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience.getSegmentsExperienceId()),
				role.getRoleId(), ActionKeys.VIEW);

			ResourcePermissionLocalServiceUtil.setResourcePermissions(
				_group.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience.getPlid()), _role.getRoleId(),
				new String[] {ActionKeys.UPDATE});
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperienceService.getSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId());
		}
	}

	private void _testGetSegmentsExperiencesCountWithViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_getDefaultSegmentsExperience(layout.getPlid());

		SegmentsTestUtil.addSegmentsExperience(
			layout.getPlid(), serviceContext);
		SegmentsTestUtil.addSegmentsExperience(
			layout.getPlid(), serviceContext);
		SegmentsTestUtil.addSegmentsExperience(
			layout.getPlid(), serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertEquals(
				4,
				_segmentsExperienceService.getSegmentsExperiencesCount(
					_group.getGroupId(), layout.getPlid(), true));
		}
	}

	private void _testGetSegmentsExperiencesCountWithoutViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_getDefaultSegmentsExperience(layout.getPlid());

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);

		SegmentsTestUtil.addSegmentsExperience(
			layout.getPlid(), serviceContext);
		SegmentsTestUtil.addSegmentsExperience(
			layout.getPlid(), serviceContext);

		List<Role> roles = RoleLocalServiceUtil.getRoles(_group.getCompanyId());

		for (Role role : roles) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				_group.getCompanyId(),
				"com.liferay.segments.model.SegmentsExperience",
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience.getSegmentsExperienceId()),
				role.getRoleId(), ActionKeys.VIEW);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertEquals(
				3,
				_segmentsExperienceService.getSegmentsExperiencesCount(
					_group.getGroupId(), layout.getPlid(), true));
		}
	}

	private void _testGetSegmentsExperiencesCountWithoutViewPermissionAndWithUpdateLayoutPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_getDefaultSegmentsExperience(layout.getPlid());

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);

		SegmentsTestUtil.addSegmentsExperience(
			layout.getPlid(), serviceContext);
		SegmentsTestUtil.addSegmentsExperience(
			layout.getPlid(), serviceContext);

		List<Role> roles = RoleLocalServiceUtil.getRoles(_group.getCompanyId());

		for (Role role : roles) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				_group.getCompanyId(),
				"com.liferay.segments.model.SegmentsExperience",
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience.getSegmentsExperienceId()),
				role.getRoleId(), ActionKeys.VIEW);

			ResourcePermissionLocalServiceUtil.setResourcePermissions(
				_group.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience.getPlid()), _role.getRoleId(),
				new String[] {ActionKeys.UPDATE});
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertEquals(
				4,
				_segmentsExperienceService.getSegmentsExperiencesCount(
					_group.getGroupId(), layout.getPlid(), true));
		}
	}

	private void _testGetSegmentsExperiencesWithViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperience defaultSegmentsExperience =
			_getDefaultSegmentsExperience(layout.getPlid());
		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);
		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);
		SegmentsExperience segmentsExperience3 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			List<SegmentsExperience> segmentsExperiences =
				_segmentsExperienceService.getSegmentsExperiences(
					_group.getGroupId(), layout.getPlid(), true,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertEquals(
				segmentsExperiences.toString(), 4, segmentsExperiences.size());

			Assert.assertTrue(
				segmentsExperiences.contains(defaultSegmentsExperience));
			Assert.assertTrue(
				segmentsExperiences.contains(segmentsExperience1));
			Assert.assertTrue(
				segmentsExperiences.contains(segmentsExperience2));
			Assert.assertTrue(
				segmentsExperiences.contains(segmentsExperience3));
		}
	}

	private void _testGetSegmentsExperiencesWithoutViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperience defaultSegmentsExperience =
			_getDefaultSegmentsExperience(layout.getPlid());

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);
		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);
		SegmentsExperience segmentsExperience3 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);

		for (Role role : RoleLocalServiceUtil.getRoles(_group.getCompanyId())) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				_group.getCompanyId(),
				"com.liferay.segments.model.SegmentsExperience",
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience2.getSegmentsExperienceId()),
				role.getRoleId(), ActionKeys.VIEW);
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			List<SegmentsExperience> segmentsEntries =
				_segmentsExperienceService.getSegmentsExperiences(
					_group.getGroupId(), layout.getPlid(), true,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertEquals(
				segmentsEntries.toString(), 3, segmentsEntries.size());

			Assert.assertTrue(
				segmentsEntries.contains(defaultSegmentsExperience));
			Assert.assertTrue(segmentsEntries.contains(segmentsExperience1));
			Assert.assertTrue(segmentsEntries.contains(segmentsExperience3));
		}
	}

	private void _testGetSegmentsExperiencesWithoutViewPermissionAndWithUpdateLayoutPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperience defaultSegmentsExperience =
			_getDefaultSegmentsExperience(layout.getPlid());

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);
		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);
		SegmentsExperience segmentsExperience3 =
			SegmentsTestUtil.addSegmentsExperience(
				layout.getPlid(), serviceContext);

		for (Role role : RoleLocalServiceUtil.getRoles(_group.getCompanyId())) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				_group.getCompanyId(),
				"com.liferay.segments.model.SegmentsExperience",
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience2.getSegmentsExperienceId()),
				role.getRoleId(), ActionKeys.VIEW);

			ResourcePermissionLocalServiceUtil.setResourcePermissions(
				_group.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(segmentsExperience2.getPlid()),
				_role.getRoleId(), new String[] {ActionKeys.UPDATE});
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			List<SegmentsExperience> segmentsEntries =
				_segmentsExperienceService.getSegmentsExperiences(
					_group.getGroupId(), layout.getPlid(), true,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertEquals(
				segmentsEntries.toString(), 4, segmentsEntries.size());

			Assert.assertTrue(
				segmentsEntries.contains(defaultSegmentsExperience));
			Assert.assertTrue(segmentsEntries.contains(segmentsExperience1));
			Assert.assertTrue(segmentsEntries.contains(segmentsExperience2));
			Assert.assertTrue(segmentsEntries.contains(segmentsExperience3));
		}
	}

	private void _testUpdateSegmentsExperience(Group group) throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			group.getGroupId());

		String segmentsEntryERC = segmentsEntry.getExternalReferenceCode();
		String segmentsEntryScopeERC =
			ScopeUtil.getItemScopeExternalReferenceCode(
				segmentsEntry.getGroupId(), _group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.addSegmentsExperience(
				null, _group.getGroupId(), segmentsEntryERC,
				segmentsEntryScopeERC, _classPK,
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		segmentsEntryERC = RandomTestUtil.randomString();
		segmentsEntryScopeERC = RandomTestUtil.randomString();

		SegmentsExperience updatedSegmentsExperience =
			_segmentsExperienceService.updateSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId(), segmentsEntryERC,
				segmentsEntryScopeERC, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"property", "value"
				).build());

		_assertSegmentsExperience(
			updatedSegmentsExperience, segmentsEntryERC, segmentsEntryScopeERC);
	}

	private void _testUpdateSegmentsExperienceWithUpdatePermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			_group.getCompanyId(),
			"com.liferay.segments.model.SegmentsExperience",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperienceService.updateSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean());
		}

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			_group.getCompanyId(),
			"com.liferay.segments.model.SegmentsExperience",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			_role.getRoleId(), ActionKeys.UPDATE);
	}

	private void _testUpdateSegmentsExperienceWithoutTypeSettings()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.addSegmentsExperience(
				null, _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _classPK,
				RandomTestUtil.randomLocaleStringMap(), true,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"property", "value"
				).build(),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		SegmentsExperience updatedSegmentsExperience =
			_segmentsExperienceService.updateSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean());

		UnicodeProperties actualTypeSettingsUnicodeProperties =
			updatedSegmentsExperience.getTypeSettingsUnicodeProperties();

		Assert.assertEquals(
			"value",
			actualTypeSettingsUnicodeProperties.getProperty("property"));
	}

	private void _testUpdateSegmentsExperienceWithoutUpdatePermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _segmentsExperienceService.updateSegmentsExperience(
					segmentsExperience.getSegmentsExperienceId(),
					RandomTestUtil.randomString(), null,
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomBoolean()));
		}
	}

	private void _testUpdateSegmentsExperienceWithoutUpdatePermissionAndWithUpdateLayoutPermission()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_classPK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			_group.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(segmentsExperience.getPlid()), _role.getRoleId(),
			new String[] {ActionKeys.UPDATE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_segmentsExperienceService.updateSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean());
		}
	}

	private long _classPK;
	private Group _companyGroup;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private Group _otherGroup;

	@DeleteAfterTestRun
	private User _otherGroupUser;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperienceService _segmentsExperienceService;

	@DeleteAfterTestRun
	private User _user;

}