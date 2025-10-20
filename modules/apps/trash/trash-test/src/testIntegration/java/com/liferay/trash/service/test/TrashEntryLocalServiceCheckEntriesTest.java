/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashLocalServiceUtil;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalServiceUtil;
import com.liferay.trash.test.util.TrashTestUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sampsa Sohlman
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class TrashEntryLocalServiceCheckEntriesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		deleteTrashEntries();
	}

	@After
	public void tearDown() throws Exception {
		deleteTrashEntries();
	}

	@Test
	public void testCompanies() throws Exception {
		long[] companyIds = new long[_COMPANIES_COUNT];

		for (int i = 0; i < _COMPANIES_COUNT; i++) {
			companyIds[i] = createCompany();
		}

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				Group group = updateTrashEntriesMaxAge(
					createGroup(companyId), _MAX_AGE);

				createTrashEntries(group);
			},
			companyIds);

		TrashEntryLocalServiceUtil.checkEntries();

		Assert.assertEquals(
			_COMPANIES_COUNT * _NOT_EXPIRED_TRASH_ENTRIES_COUNT,
			TrashEntryLocalServiceUtil.getTrashEntriesCount());
	}

	@Test
	public void testCustomMaxAge() throws Exception {
		Group group = updateTrashEntriesMaxAge(
			createGroup(TestPropsValues.getCompanyId()), 2);

		deleteTrashEntries(group);
	}

	@Test
	public void testGroups() throws Exception {
		for (int i = 0; i < _GROUPS_COUNT; i++) {
			Group group = updateTrashEntriesMaxAge(
				createGroup(TestPropsValues.getCompanyId()), _MAX_AGE);

			createTrashEntries(group);
		}

		TrashEntryLocalServiceUtil.checkEntries();

		Assert.assertEquals(
			_GROUPS_COUNT * _NOT_EXPIRED_TRASH_ENTRIES_COUNT,
			TrashEntryLocalServiceUtil.getTrashEntriesCount());
	}

	@Test
	public void testGroupTrashDisabled() throws Exception {
		Group group = createGroup(TestPropsValues.getCompanyId());

		createFileEntryTrashEntry(group, false);

		TrashTestUtil.disableTrash(group);

		TrashEntryLocalServiceUtil.checkEntries();

		Assert.assertEquals(
			0, TrashEntryLocalServiceUtil.getTrashEntriesCount());
	}

	@Test
	public void testLayoutGroup() throws Exception {
		Group group = updateTrashEntriesMaxAge(
			createGroup(TestPropsValues.getCompanyId()), 2);

		deleteTrashEntries(createLayoutGroup(group));
	}

	@Test
	public void testRegularGroup() throws Exception {
		deleteTrashEntries(createGroup(TestPropsValues.getCompanyId()));
	}

	@Test
	public void testStagingGroup() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Group group = updateTrashEntriesMaxAge(createGroup(companyId), 2);
		User user = UserTestUtil.getAdminUser(companyId);

		StagingLocalServiceUtil.enableLocalStaging(
			user.getUserId(), group, false, false,
			ServiceContextTestUtil.getServiceContext(group, user.getUserId()));

		deleteTrashEntries(group.getStagingGroup());
	}

	@Test
	public void testStagingLayoutScope() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Group group = updateTrashEntriesMaxAge(createGroup(companyId), 2);
		User user = UserTestUtil.getAdminUser(companyId);

		StagingLocalServiceUtil.enableLocalStaging(
			user.getUserId(), group, false, false,
			ServiceContextTestUtil.getServiceContext(group, user.getUserId()));

		group = createLayoutGroup(group.getStagingGroup());

		deleteTrashEntries(group);
	}

	@Test
	public void testStagingTrashDisabled() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Group group = TrashTestUtil.disableTrash(createGroup(companyId));
		User user = UserTestUtil.getAdminUser(companyId);

		StagingLocalServiceUtil.enableLocalStaging(
			user.getUserId(), group, false, false,
			ServiceContextTestUtil.getServiceContext(group, user.getUserId()));

		createFileEntryTrashEntry(group.getStagingGroup(), false);

		TrashEntryLocalServiceUtil.checkEntries();

		Assert.assertEquals(
			0, TrashEntryLocalServiceUtil.getTrashEntriesCount());
	}

	protected long createCompany() throws Exception {
		Company company = CompanyTestUtil.addCompany(
			RandomTestUtil.randomString());

		_companies.add(company);

		return company.getCompanyId();
	}

	protected void createFileEntryTrashEntry(Group group, boolean expired)
		throws Exception {

		User user = UserTestUtil.getAdminUser(group.getCompanyId());

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, user.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId()));

		DLTrashLocalServiceUtil.moveFileEntryToTrash(
			user.getUserId(), fileEntry.getRepositoryId(),
			fileEntry.getFileEntryId());

		if (expired) {
			int maxAge = _trashHelper.getMaxAge(group);

			TrashEntry trashEntry = TrashEntryLocalServiceUtil.getEntry(
				DLFileEntry.class.getName(), fileEntry.getFileEntryId());

			Date createDate = trashEntry.getCreateDate();

			trashEntry.setCreateDate(
				new Date(
					createDate.getTime() - (maxAge * Time.MINUTE) - Time.DAY));

			TrashEntryLocalServiceUtil.updateTrashEntry(trashEntry);
		}
	}

	protected Group createGroup(long companyId) throws Exception {
		User user = UserTestUtil.getAdminUser(companyId);

		Group group = GroupTestUtil.addGroup(
			companyId, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_groups.add(group);

		return group;
	}

	protected Group createLayoutGroup(Group group) throws Exception {
		User user = UserTestUtil.getAdminUser(group.getCompanyId());

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		return GroupLocalServiceUtil.addGroup(
			StringPool.BLANK, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, Layout.class.getName(),
			layout.getPlid(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), String.valueOf(layout.getPlid())
			).build(),
			(Map<Locale, String>)null, 0, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, false,
			true, null);
	}

	protected void createTrashEntries(Group group) throws Exception {
		for (int i = 0; i < _EXPIRED_TRASH_ENTRIES_COUNT; i++) {
			createFileEntryTrashEntry(group, true);
		}

		for (int i = 0; i < _NOT_EXPIRED_TRASH_ENTRIES_COUNT; i++) {
			createFileEntryTrashEntry(group, false);
		}
	}

	protected void deleteTrashEntries() {
		List<TrashEntry> trashEntries =
			TrashEntryLocalServiceUtil.getTrashEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (TrashEntry trashEntry : trashEntries) {
			TrashEntryLocalServiceUtil.deleteEntry(trashEntry);
		}
	}

	protected void deleteTrashEntries(Group group) throws Exception {
		createTrashEntries(group);

		TrashEntryLocalServiceUtil.checkEntries();

		Assert.assertEquals(
			_NOT_EXPIRED_TRASH_ENTRIES_COUNT,
			TrashEntryLocalServiceUtil.getTrashEntriesCount());
	}

	protected Group updateTrashEntriesMaxAge(Group group, int days)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getParentLiveGroupTypeSettingsProperties();

		int companyTrashEntriesMaxAge = PrefsPropsUtil.getInteger(
			group.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE);

		if (days > 0) {
			days *= 1440;
		}
		else {
			days = GetterUtil.getInteger(
				typeSettingsUnicodeProperties.getProperty("trashEntriesMaxAge"),
				companyTrashEntriesMaxAge);
		}

		if (days != companyTrashEntriesMaxAge) {
			typeSettingsUnicodeProperties.setProperty(
				"trashEntriesMaxAge", String.valueOf(days));
		}
		else {
			typeSettingsUnicodeProperties.remove("trashEntriesMaxAge");
		}

		group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		return GroupLocalServiceUtil.updateGroup(group);
	}

	private static final int _COMPANIES_COUNT = 2;

	private static final int _EXPIRED_TRASH_ENTRIES_COUNT = 3;

	private static final int _GROUPS_COUNT = 2;

	private static final int _MAX_AGE = 5;

	private static final int _NOT_EXPIRED_TRASH_ENTRIES_COUNT = 4;

	@DeleteAfterTestRun
	private final List<Company> _companies = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private TrashHelper _trashHelper;

}