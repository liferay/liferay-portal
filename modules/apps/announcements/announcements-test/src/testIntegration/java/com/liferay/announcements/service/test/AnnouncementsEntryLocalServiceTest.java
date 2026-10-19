/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.service.test;

import com.liferay.announcements.kernel.exception.EntryDisplayDateException;
import com.liferay.announcements.kernel.exception.EntryExpirationDateException;
import com.liferay.announcements.kernel.exception.EntryTitleException;
import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.model.AnnouncementsFlagConstants;
import com.liferay.announcements.kernel.service.AnnouncementsEntryLocalService;
import com.liferay.announcements.kernel.service.AnnouncementsFlagLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christopher Kian
 * @author Hugo Huijser
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class AnnouncementsEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();
	}

	@Test(expected = EntryTitleException.class)
	public void testAddEntryWithInvalidTitle() throws Exception {
		_announcementsEntryLocalService.addEntry(
			_user.getUserId(), 0, 0,
			"InvalidTitleOver75CharactersInvalidTitleOver75Characters" +
				"InvalidTitleOver75Characters",
			StringUtil.randomString(), "http://localhost", "general",
			_portal.getDate(
				1, 1, 1990, 1, 1, _user.getTimeZone(),
				EntryDisplayDateException.class),
			_portal.getDate(
				1, 1, 3000, 1, 1, _user.getTimeZone(),
				EntryExpirationDateException.class),
			1, false);
	}

	@Test
	public void testDeleteGroupAnnouncements() throws Exception {
		Group group = GroupTestUtil.addGroup(
			_user.getCompanyId(), _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		AnnouncementsEntry entry = _addEntry(
			group.getClassNameId(), group.getGroupId());

		_announcementsEntries.add(entry);

		Assert.assertNotNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));

		_groupLocalService.deleteGroup(group);

		Assert.assertNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));
	}

	@Test
	public void testDeleteOrganizationAnnouncements() throws Exception {
		Organization organization = _organizationLocalService.addOrganization(
			_user.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		AnnouncementsEntry entry = _addEntry(
			_classNameLocalService.getClassNameId(Organization.class),
			organization.getOrganizationId());

		_announcementsEntries.add(entry);

		Assert.assertNotNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));

		_organizationLocalService.deleteOrganization(organization);

		Assert.assertNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));
	}

	@Test
	public void testDeleteOrganizationGroupAnnouncements() throws Exception {
		Organization organization = _organizationLocalService.addOrganization(
			_user.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Group group = organization.getGroup();

		AnnouncementsEntry entry = _addEntry(
			_classNameLocalService.getClassNameId(Group.class),
			group.getGroupId());

		_announcementsEntries.add(entry);

		Assert.assertNotNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));

		_organizationLocalService.deleteOrganization(organization);

		Assert.assertNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));
	}

	@Test
	public void testDeleteRoleAnnouncements() throws Exception {
		_deleteRoleAnnouncements(RoleConstants.TYPE_ORGANIZATION);
		_deleteRoleAnnouncements(RoleConstants.TYPE_REGULAR);
		_deleteRoleAnnouncements(RoleConstants.TYPE_SITE);
	}

	@Test
	public void testDeleteUserGroupAnnouncements() throws Exception {
		Group group = GroupTestUtil.addGroup(
			_user.getCompanyId(), _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		UserGroup userGroup = _userGroupLocalService.addUserGroup(
			StringPool.BLANK, _user.getUserId(), _user.getCompanyId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			RandomTestUtil.randomString(50),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		AnnouncementsEntry entry = _addEntry(
			_classNameLocalService.getClassNameId(UserGroup.class),
			userGroup.getUserGroupId());

		_announcementsEntries.add(entry);

		Assert.assertNotNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));

		_userGroupLocalService.deleteUserGroup(userGroup);

		Assert.assertNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));
	}

	@Test
	public void testGetEntries() throws Exception {
		Group group = GroupTestUtil.addGroup(
			_user.getCompanyId(), _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		AnnouncementsEntry entry1 = _addEntry(
			group.getClassNameId(), group.getGroupId());
		AnnouncementsEntry entry2 = _addEntry(
			group.getClassNameId(), group.getGroupId());
		AnnouncementsEntry entry3 = _addEntry(
			group.getClassNameId(), group.getGroupId());

		_announcementsEntries.add(entry1);
		_announcementsEntries.add(entry2);
		_announcementsEntries.add(entry3);

		_announcementsFlagLocalService.addFlag(
			_user.getUserId(), entry1.getEntryId(),
			AnnouncementsFlagConstants.HIDDEN);
		_announcementsFlagLocalService.addFlag(
			_user.getUserId(), entry2.getEntryId(),
			AnnouncementsFlagConstants.HIDDEN);

		LinkedHashMap<Long, long[]> scopes =
			LinkedHashMapBuilder.<Long, long[]>put(
				_portal.getClassNameId(Group.class.getName()),
				new long[] {group.getGroupId()}
			).build();

		List<AnnouncementsEntry> hiddenAnnouncementsEntries =
			_announcementsEntryLocalService.getEntries(
				_user.getUserId(), scopes, false,
				AnnouncementsFlagConstants.HIDDEN, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			hiddenAnnouncementsEntries.toString(), 2,
			hiddenAnnouncementsEntries.size());

		List<AnnouncementsEntry> notHiddenAnnouncementsEntries =
			_announcementsEntryLocalService.getEntries(
				_user.getUserId(), scopes, false,
				AnnouncementsFlagConstants.NOT_HIDDEN, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			notHiddenAnnouncementsEntries.toString(), 1,
			notHiddenAnnouncementsEntries.size());

		AnnouncementsEntry entry4 = notHiddenAnnouncementsEntries.get(0);

		Assert.assertEquals(entry4.getEntryId(), entry3.getEntryId());
	}

	@Test
	public void testGetEntriesCountInDifferentCompany() throws Exception {
		_announcementsEntries.add(_addEntry(0, 0));

		Assert.assertEquals(
			0,
			_announcementsEntryLocalService.getEntriesCount(
				_company.getCompanyId(), 0, 0, false));
	}

	@Test
	public void testGetEntriesCountInSameCompany() throws Exception {
		_announcementsEntries.add(_addEntry(0, 0));

		Assert.assertEquals(
			1,
			_announcementsEntryLocalService.getEntriesCount(
				_user.getCompanyId(), 0, 0, false));
	}

	@Test
	public void testGetEntriesInDifferentCompany() throws Exception {
		_announcementsEntries.add(_addEntry(0, 0));

		List<AnnouncementsEntry> announcementsEntries =
			_announcementsEntryLocalService.getEntries(
				_company.getCompanyId(), 0, 0, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			announcementsEntries.toString(), 0, announcementsEntries.size());
	}

	@Test
	public void testGetEntriesInSameCompany() throws Exception {
		AnnouncementsEntry entry = _addEntry(0, 0);

		_announcementsEntries.add(entry);

		List<AnnouncementsEntry> announcementsEntries =
			_announcementsEntryLocalService.getEntries(
				_user.getCompanyId(), 0, 0, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			announcementsEntries.toString(), 1, announcementsEntries.size());

		Assert.assertEquals(entry, announcementsEntries.get(0));
	}

	private AnnouncementsEntry _addEntry(long classNameId, long classPK)
		throws Exception {

		return _announcementsEntryLocalService.addEntry(
			_user.getUserId(), classNameId, classPK, StringUtil.randomString(),
			StringUtil.randomString(), "http://localhost", "general",
			_portal.getDate(
				1, 1, 1990, 1, 1, _user.getTimeZone(),
				EntryDisplayDateException.class),
			_portal.getDate(
				1, 1, 3000, 1, 1, _user.getTimeZone(),
				EntryExpirationDateException.class),
			1, false);
	}

	private void _deleteRoleAnnouncements(int roleType) throws Exception {
		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), _user.getUserId(), null, 0,
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			null, null, roleType, null, null);

		AnnouncementsEntry entry = _addEntry(
			role.getClassNameId(), role.getRoleId());

		Assert.assertNotNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));

		_roleLocalService.deleteRole(role);

		Assert.assertNull(
			_announcementsEntryLocalService.fetchAnnouncementsEntry(
				entry.getEntryId()));
	}

	private static Company _company;

	@DeleteAfterTestRun
	private final List<AnnouncementsEntry> _announcementsEntries =
		new ArrayList<>();

	@Inject
	private AnnouncementsEntryLocalService _announcementsEntryLocalService;

	@Inject
	private AnnouncementsFlagLocalService _announcementsFlagLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

}