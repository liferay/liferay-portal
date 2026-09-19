/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class BlogsEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groupUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.POWER_USER);

		_permissionChecker = PermissionCheckerFactoryUtil.create(_groupUser);
	}

	@Test
	public void testAddAttachmentFileEntryWithAddEntryPermission()
		throws Exception {

		_addResourcePermission(
			ActionKeys.ADD_ENTRY, BlogsConstants.RESOURCE_NAME);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.addAttachmentFileEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				ContentTypes.APPLICATION_OCTET_STREAM,
				new UnsyncByteArrayInputStream(new byte[0]));
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddAttachmentFileEntryWithoutAddEntryPermission()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.addAttachmentFileEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				ContentTypes.APPLICATION_OCTET_STREAM,
				new UnsyncByteArrayInputStream(new byte[0]));
		}
	}

	@Test
	public void testAddEntryWithAddEntryPermission1() throws Exception {
		_addResourcePermission(
			ActionKeys.ADD_ENTRY, BlogsConstants.RESOURCE_NAME);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_addEntry1();
		}
	}

	@Test
	public void testAddEntryWithAddEntryPermission2() throws Exception {
		_addResourcePermission(
			ActionKeys.ADD_ENTRY, BlogsConstants.RESOURCE_NAME);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_addEntry2();
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddEntryWithoutAddEntryPermission1() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_addEntry1();
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddEntryWithoutAddEntryPermission2() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_addEntry2();
		}
	}

	@Test
	public void testDeleteAttachmentFileEntryWithDeletePermission()
		throws Exception {

		FileEntry fileEntry = _addAttachmentFileEntry();

		_addResourcePermission(
			ActionKeys.ADD_ENTRY, BlogsConstants.RESOURCE_NAME);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.deleteAttachmentFileEntry(
				fileEntry.getFileEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteAttachmentFileEntryWithoutDeletePermission()
		throws Exception {

		FileEntry fileEntry = _addAttachmentFileEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.deleteAttachmentFileEntry(
				fileEntry.getFileEntryId());
		}
	}

	@Test
	public void testDeleteEntryWithDeletePermission() throws Exception {
		BlogsEntry entry = _addEntry();

		_addResourcePermission(ActionKeys.DELETE, _CLASS_NAME_BLOGS_ENTRY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.deleteEntry(entry.getEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteEntryWithoutDeletePermission() throws Exception {
		BlogsEntry entry = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.deleteEntry(entry.getEntryId());
		}
	}

	@Test
	public void testGetAttachmentFileEntryByExternalReferenceCodeWithViewPermission()
		throws Exception {

		FileEntry fileEntry = _addAttachmentFileEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getAttachmentFileEntryByExternalReferenceCode(
				fileEntry.getExternalReferenceCode(), fileEntry.getGroupId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetAttachmentFileEntryByExternalReferenceCodeWithoutViewPermission()
		throws Exception {

		FileEntry fileEntry = _addAttachmentFileEntry();

		_removeViewResourcePermission(fileEntry);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getAttachmentFileEntryByExternalReferenceCode(
				fileEntry.getExternalReferenceCode(), fileEntry.getGroupId());
		}
	}

	@Test
	public void testGetAttachmentFileEntryWithViewPermission()
		throws Exception {

		FileEntry fileEntry = _addAttachmentFileEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getAttachmentFileEntry(
				fileEntry.getFileEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetAttachmentFileEntryWithoutViewPermission()
		throws Exception {

		FileEntry fileEntry = _addAttachmentFileEntry();

		_removeViewResourcePermission(fileEntry);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getAttachmentFileEntry(
				fileEntry.getFileEntryId());
		}
	}

	@Test
	public void testGetCompanyEntriesWithViewPermission() throws Exception {
		BlogsEntry entry1 = _addEntry();
		BlogsEntry entry2 = _addEntry();
		BlogsEntry entry3 = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			List<BlogsEntry> blogsEntries =
				_blogsEntryService.getCompanyEntries(
					TestPropsValues.getCompanyId(), new Date(),
					WorkflowConstants.STATUS_APPROVED, 100);

			Assert.assertEquals(
				blogsEntries.toString(), 3, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry3));
			Assert.assertTrue(blogsEntries.contains(entry2));
			Assert.assertTrue(blogsEntries.contains(entry1));
		}
	}

	@Test
	public void testGetCompanyEntriesWithViewPermissionAndDisplayDate()
		throws Exception {

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		BlogsEntry entry1 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 2);

		BlogsEntry entry2 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 2);

		_addEntry(calendar.getTime());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			calendar = Calendar.getInstance();

			calendar.add(Calendar.HOUR, 3);
			calendar.set(Calendar.YEAR, 2000);

			List<BlogsEntry> blogsEntries =
				_blogsEntryService.getCompanyEntries(
					TestPropsValues.getCompanyId(), calendar.getTime(),
					WorkflowConstants.STATUS_APPROVED, 2);

			Assert.assertEquals(
				blogsEntries.toString(), 2, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry1));
			Assert.assertTrue(blogsEntries.contains(entry2));
		}
	}

	@Test
	public void testGetCompanyEntriesWithViewPermissionAndMax()
		throws Exception {

		_addEntry();

		BlogsEntry entry2 = _addEntry();
		BlogsEntry entry3 = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			List<BlogsEntry> blogsEntries =
				_blogsEntryService.getCompanyEntries(
					TestPropsValues.getCompanyId(), new Date(),
					WorkflowConstants.STATUS_APPROVED, 2);

			Assert.assertEquals(
				blogsEntries.toString(), 2, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry3));
			Assert.assertTrue(blogsEntries.contains(entry2));
		}
	}

	@Test
	public void testGetCompanyEntriesWithoutViewPermission() throws Exception {
		BlogsEntry entry1 = _addEntry();
		BlogsEntry entry2 = _addEntry();
		BlogsEntry entry3 = _addEntry();

		_removeViewResourcePermission(entry2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			List<BlogsEntry> blogsEntries =
				_blogsEntryService.getCompanyEntries(
					TestPropsValues.getCompanyId(), new Date(),
					WorkflowConstants.STATUS_APPROVED, 100);

			Assert.assertEquals(
				blogsEntries.toString(), 2, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry3));
			Assert.assertTrue(blogsEntries.contains(entry1));
		}
	}

	@Test
	public void testGetCompanyEntriesWithoutViewPermissionAndDisplayDate()
		throws Exception {

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		BlogsEntry entry1 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 2);

		BlogsEntry entry2 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 2);

		_addEntry(calendar.getTime());

		_removeViewResourcePermission(entry2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			calendar = Calendar.getInstance();

			calendar.add(Calendar.HOUR, 3);
			calendar.set(Calendar.YEAR, 2000);

			List<BlogsEntry> blogsEntries =
				_blogsEntryService.getCompanyEntries(
					TestPropsValues.getCompanyId(), calendar.getTime(),
					WorkflowConstants.STATUS_APPROVED, 2);

			Assert.assertEquals(
				blogsEntries.toString(), 1, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry1));
		}
	}

	@Test
	public void testGetCompanyEntriesWithoutViewPermissionAndMax()
		throws Exception {

		BlogsEntry entry1 = _addEntry();
		BlogsEntry entry2 = _addEntry();
		BlogsEntry entry3 = _addEntry();

		_removeViewResourcePermission(entry2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			List<BlogsEntry> blogsEntries =
				_blogsEntryService.getCompanyEntries(
					TestPropsValues.getCompanyId(), new Date(),
					WorkflowConstants.STATUS_APPROVED, 2);

			Assert.assertEquals(
				blogsEntries.toString(), 2, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry3));
			Assert.assertTrue(blogsEntries.contains(entry1));
		}
	}

	@Test
	public void testGetEntriesPrevAndNext() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		BlogsEntry entry1 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 1);

		BlogsEntry entry2 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 1);

		BlogsEntry entry3 = _addEntry();

		BlogsEntry[] prevAndNext = _blogsEntryService.getEntriesPrevAndNext(
			entry2.getEntryId());

		Assert.assertEquals(
			StringBundler.concat(
				"The previous entry relative to entry ", entry2.getEntryId(),
				" should be ", entry1.getEntryId()),
			entry1, prevAndNext[0]);
		Assert.assertEquals(
			StringBundler.concat(
				"The current entry relative to entry ", entry2.getEntryId(),
				" should be ", entry2.getEntryId()),
			entry2, prevAndNext[1]);
		Assert.assertEquals(
			StringBundler.concat(
				"The next entry relative to entry ", entry2.getEntryId(),
				" should be ", entry3.getEntryId()),
			entry3, prevAndNext[2]);
	}

	@Test
	public void testGetEntriesPrevAndNextWithOnlyNext() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		BlogsEntry entry1 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 1);

		BlogsEntry entry2 = _addEntry(calendar.getTime());

		BlogsEntry[] prevAndNext = _blogsEntryService.getEntriesPrevAndNext(
			entry1.getEntryId());

		Assert.assertNull(
			"The previous entry relative to entry " + entry1.getEntryId() +
				" should be null",
			prevAndNext[0]);
		Assert.assertEquals(
			StringBundler.concat(
				"The current entry relative to entry ", entry1.getEntryId(),
				" should be ", entry1.getEntryId()),
			entry1, prevAndNext[1]);
		Assert.assertEquals(
			StringBundler.concat(
				"The next entry relative to entry ", entry1.getEntryId(),
				" should be ", entry2.getEntryId()),
			entry2, prevAndNext[2]);
	}

	@Test
	public void testGetEntriesPrevAndNextWithOnlyPrev() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		BlogsEntry entry1 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 1);

		BlogsEntry entry2 = _addEntry(calendar.getTime());

		BlogsEntry[] prevAndNext = _blogsEntryService.getEntriesPrevAndNext(
			entry2.getEntryId());

		Assert.assertEquals(
			StringBundler.concat(
				"The previous entry relative to entry ", entry2.getEntryId(),
				" should be ", entry1.getEntryId()),
			entry1, prevAndNext[0]);
		Assert.assertEquals(
			StringBundler.concat(
				"The current entry relative to entry ", entry2.getEntryId(),
				" should be ", entry2.getEntryId()),
			entry2, prevAndNext[1]);
		Assert.assertNull(
			"The next entry relative to entry " + entry1.getEntryId() +
				" should be null",
			prevAndNext[2]);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetEntriesPrevAndNextWithoutEntryViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		_addEntry(calendar.getTime(), serviceContext);

		calendar.add(Calendar.HOUR, 1);

		BlogsEntry entry2 = _addEntry(calendar.getTime(), serviceContext);

		calendar.add(Calendar.HOUR, 1);

		_addEntry(calendar.getTime(), serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getEntriesPrevAndNext(entry2.getEntryId());
		}
	}

	@Test
	public void testGetEntriesPrevAndNextWithoutNextViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		BlogsEntry entry1 = _addEntry(calendar.getTime(), serviceContext);

		calendar.add(Calendar.HOUR, 1);

		BlogsEntry entry2 = _addEntry(calendar.getTime(), serviceContext);

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		calendar.add(Calendar.HOUR, 1);

		_addEntry(calendar.getTime(), serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			BlogsEntry[] prevAndNext = _blogsEntryService.getEntriesPrevAndNext(
				entry2.getEntryId());

			Assert.assertEquals(
				StringBundler.concat(
					"The previous entry relative to entry ",
					entry2.getEntryId(), " should be ", entry1.getEntryId()),
				entry1, prevAndNext[0]);
			Assert.assertEquals(
				StringBundler.concat(
					"The current entry relative to entry ", entry2.getEntryId(),
					" should be ", entry2.getEntryId()),
				entry2, prevAndNext[1]);
			Assert.assertNull(
				"The next entry relative to entry " + entry2.getEntryId() +
					" should be null",
				prevAndNext[2]);
		}
	}

	@Test
	public void testGetEntriesPrevAndNextWithoutPrevViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		_addEntry(calendar.getTime(), serviceContext);

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		calendar.add(Calendar.HOUR, 1);

		BlogsEntry entry2 = _addEntry(calendar.getTime(), serviceContext);

		calendar.add(Calendar.HOUR, 1);

		BlogsEntry entry3 = _addEntry(calendar.getTime(), serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			BlogsEntry[] prevAndNext = _blogsEntryService.getEntriesPrevAndNext(
				entry2.getEntryId());

			Assert.assertNull(
				"The previous entry relative to entry " + entry2.getEntryId() +
					" should be null",
				prevAndNext[0]);
			Assert.assertEquals(
				StringBundler.concat(
					"The current entry relative to entry ", entry2.getEntryId(),
					" should be ", entry2.getEntryId()),
				entry2, prevAndNext[1]);
			Assert.assertEquals(
				StringBundler.concat(
					"The next entry relative to entry ", entry2.getEntryId(),
					" should be ", entry3.getEntryId()),
				entry3, prevAndNext[2]);
		}
	}

	@Test
	public void testGetEntryWithViewPermission() throws Exception {
		BlogsEntry entry = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getEntry(entry.getEntryId());
		}
	}

	@Test
	public void testGetEntryWithViewPermission2() throws Exception {
		BlogsEntry entry = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getEntry(
				entry.getGroupId(), entry.getUrlTitle());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetEntryWithoutViewPermission() throws Exception {
		BlogsEntry entry = _addEntry();

		_removeViewResourcePermission(entry);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getEntry(entry.getEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGetEntryWithoutViewPermission2() throws Exception {
		BlogsEntry entry = _addEntry();

		_removeViewResourcePermission(entry);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.getEntry(
				entry.getGroupId(), entry.getUrlTitle());
		}
	}

	@Test
	public void testGetGroupEntriesWithViewPermission() throws Exception {
		BlogsEntry entry1 = _addEntry();
		BlogsEntry entry2 = _addEntry();
		BlogsEntry entry3 = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			List<BlogsEntry> blogsEntries = _blogsEntryService.getGroupEntries(
				_group.getGroupId(), new Date(),
				WorkflowConstants.STATUS_APPROVED, 100);

			Assert.assertEquals(
				blogsEntries.toString(), 3, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry3));
			Assert.assertTrue(blogsEntries.contains(entry2));
			Assert.assertTrue(blogsEntries.contains(entry1));
		}
	}

	@Test
	public void testGetGroupEntriesWithViewPermissionAndDisplayDate()
		throws Exception {

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		BlogsEntry entry1 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 2);

		BlogsEntry entry2 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 2);

		_addEntry(calendar.getTime());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			calendar = Calendar.getInstance();

			calendar.add(Calendar.HOUR, 3);
			calendar.set(Calendar.YEAR, 2000);

			List<BlogsEntry> blogsEntries = _blogsEntryService.getGroupEntries(
				_group.getGroupId(), calendar.getTime(),
				WorkflowConstants.STATUS_APPROVED, 2);

			Assert.assertEquals(
				blogsEntries.toString(), 2, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry1));
			Assert.assertTrue(blogsEntries.contains(entry2));
		}
	}

	@Test
	public void testGetGroupEntriesWithViewPermissionAndMax() throws Exception {
		_addEntry();

		BlogsEntry entry2 = _addEntry();
		BlogsEntry entry3 = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			List<BlogsEntry> blogsEntries = _blogsEntryService.getGroupEntries(
				_group.getGroupId(), new Date(),
				WorkflowConstants.STATUS_APPROVED, 2);

			Assert.assertEquals(
				blogsEntries.toString(), 2, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry3));
			Assert.assertTrue(blogsEntries.contains(entry2));
		}
	}

	@Test
	public void testGetGroupEntriesWithoutViewPermission() throws Exception {
		BlogsEntry entry1 = _addEntry();
		BlogsEntry entry2 = _addEntry();
		BlogsEntry entry3 = _addEntry();

		_removeViewResourcePermission(entry2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			List<BlogsEntry> blogsEntries = _blogsEntryService.getGroupEntries(
				_group.getGroupId(), new Date(),
				WorkflowConstants.STATUS_APPROVED, 100);

			Assert.assertEquals(
				blogsEntries.toString(), 2, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry3));
			Assert.assertTrue(blogsEntries.contains(entry1));
		}
	}

	@Test
	public void testGetGroupEntriesWithoutViewPermissionAndDisplayDate()
		throws Exception {

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.YEAR, 2000);

		BlogsEntry entry1 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 2);

		BlogsEntry entry2 = _addEntry(calendar.getTime());

		calendar.add(Calendar.HOUR, 2);

		_addEntry(calendar.getTime());

		_removeViewResourcePermission(entry2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			calendar = Calendar.getInstance();

			calendar.add(Calendar.HOUR, 3);
			calendar.set(Calendar.YEAR, 2000);

			List<BlogsEntry> blogsEntries = _blogsEntryService.getGroupEntries(
				_group.getGroupId(), calendar.getTime(),
				WorkflowConstants.STATUS_APPROVED, 2);

			Assert.assertEquals(
				blogsEntries.toString(), 1, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry1));
		}
	}

	@Test
	public void testGetGroupEntriesWithoutViewPermissionAndMax()
		throws Exception {

		BlogsEntry entry1 = _addEntry();
		BlogsEntry entry2 = _addEntry();
		BlogsEntry entry3 = _addEntry();

		_removeViewResourcePermission(entry2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			List<BlogsEntry> blogsEntries = _blogsEntryService.getGroupEntries(
				_group.getGroupId(), new Date(),
				WorkflowConstants.STATUS_APPROVED, 2);

			Assert.assertEquals(
				blogsEntries.toString(), 2, blogsEntries.size());

			Assert.assertTrue(blogsEntries.contains(entry3));
			Assert.assertTrue(blogsEntries.contains(entry1));
		}
	}

	@Test
	public void testMoveEntryToTrashWithDeletePermission() throws Exception {
		BlogsEntry entry = _addEntry();

		_addResourcePermission(ActionKeys.DELETE, _CLASS_NAME_BLOGS_ENTRY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.moveEntryToTrash(entry.getEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testMoveEntryToTrashWithoutDeletePermission() throws Exception {
		BlogsEntry entry = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.moveEntryToTrash(entry.getEntryId());
		}
	}

	@Test
	public void testOwnerCanDeleteEntry() throws Exception {
		BlogsEntry entry = _addEntry(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.deleteEntry(entry.getEntryId());
		}
	}

	@Test
	public void testOwnerCanMoveEntryToTrash() throws Exception {
		BlogsEntry entry = _addEntry(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.moveEntryToTrash(entry.getEntryId());
		}
	}

	@Test
	public void testOwnerCanRestoreEntryFromTrash() throws Exception {
		BlogsEntry entry = _addEntry(_groupUser);

		_blogsEntryLocalService.moveEntryToTrash(_groupUser.getUserId(), entry);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.restoreEntryFromTrash(entry.getEntryId());
		}
	}

	@Test
	public void testOwnerCanUpdateEntry() throws Exception {
		BlogsEntry entry = _addEntry(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_updateEntry(entry.getEntryId());
		}
	}

	@Test
	public void testRestoreEntryFromTrashWithDeletePermission()
		throws Exception {

		BlogsEntry entry = _addEntry();

		_blogsEntryLocalService.moveEntryToTrash(
			TestPropsValues.getUserId(), entry);

		_addResourcePermission(ActionKeys.DELETE, _CLASS_NAME_BLOGS_ENTRY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.restoreEntryFromTrash(entry.getEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testRestoreEntryFromTrashWithoutDeletePermission()
		throws Exception {

		BlogsEntry entry = _addEntry();

		_blogsEntryLocalService.moveEntryToTrash(
			TestPropsValues.getUserId(), entry);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.restoreEntryFromTrash(entry.getEntryId());
		}
	}

	@Test
	public void testSubscribeEntryWithSubscribePermission() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.subscribe(_group.getGroupId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testSubscribeEntryWithoutSubscribePermission()
		throws Exception {

		User user = UserTestUtil.addUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_blogsEntryService.subscribe(_group.getGroupId());
		}
	}

	@Test
	public void testUnsubscribeEntryWithSubscribePermission() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_blogsEntryService.unsubscribe(_group.getGroupId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUnsubscribeEntryWithoutSubscribePermission()
		throws Exception {

		User user = UserTestUtil.addUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_blogsEntryService.unsubscribe(_group.getGroupId());
		}
	}

	@Test
	public void testUpdateEntryWithUpdatePermission1() throws Exception {
		BlogsEntry entry = _addEntry();

		_addResourcePermission(ActionKeys.UPDATE, _CLASS_NAME_BLOGS_ENTRY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_updateEntry(entry.getEntryId());
		}
	}

	@Test
	public void testUpdateEntryWithUpdatePermission2() throws Exception {
		BlogsEntry entry = _addEntry();

		_addResourcePermission(ActionKeys.UPDATE, _CLASS_NAME_BLOGS_ENTRY);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_updateEntry(entry.getEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateEntryWithoutUpdatePermission1() throws Exception {
		BlogsEntry entry = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_updateEntry(entry.getEntryId());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdateEntryWithoutUpdatePermission2() throws Exception {
		BlogsEntry entry = _addEntry();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, _permissionChecker)) {

			_updateEntry(entry.getEntryId());
		}
	}

	private FileEntry _addAttachmentFileEntry() throws Exception {
		return _blogsEntryLocalService.addAttachmentFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM,
			new UnsyncByteArrayInputStream(new byte[0]));
	}

	private BlogsEntry _addEntry() throws Exception {
		return _addEntry(new Date());
	}

	private BlogsEntry _addEntry(Date displayDate) throws Exception {
		return _addEntry(
			displayDate,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private BlogsEntry _addEntry(
			Date displayDate, ServiceContext serviceContext)
		throws Exception {

		return _blogsEntryLocalService.addEntry(
			serviceContext.getUserId(), StringUtil.randomString(),
			RandomTestUtil.randomString(), displayDate, serviceContext);
	}

	private BlogsEntry _addEntry(User user) throws Exception {
		return _addEntry(
			new Date(),
			ServiceContextTestUtil.getServiceContext(_group, user.getUserId()));
	}

	private void _addEntry1() throws PortalException {
		_blogsEntryService.addEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), 1, 1,
			1990, 1, 1, true, false, new String[0],
			RandomTestUtil.randomString(), null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId()));
	}

	private void _addEntry2() throws PortalException {
		_blogsEntryService.addEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), 1, 1,
			1990, 1, 1, true, false, new String[0],
			RandomTestUtil.randomString(), null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId()));
	}

	private void _addResourcePermission(String actionId, String name)
		throws Exception {

		Role siteMemberRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), name, ResourceConstants.SCOPE_GROUP,
			String.valueOf(_group.getGroupId()), siteMemberRole.getRoleId(),
			actionId);
	}

	private void _removeViewResourcePermission(BlogsEntry entry)
		throws Exception {

		_removeViewResourcePermission(
			BlogsEntry.class.getName(), entry.getEntryId());
	}

	private void _removeViewResourcePermission(FileEntry fileEntry)
		throws Exception {

		_removeViewResourcePermission(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());
	}

	private void _removeViewResourcePermission(String name, long primKey)
		throws Exception {

		for (Role role :
				_roleLocalService.getRoles(TestPropsValues.getCompanyId())) {

			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			_resourcePermissionLocalService.removeResourcePermission(
				TestPropsValues.getCompanyId(), name,
				ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(primKey),
				role.getRoleId(), ActionKeys.VIEW);
		}
	}

	private void _updateEntry(long entryId) throws PortalException {
		_blogsEntryService.updateEntry(
			entryId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), 1, 1, 1990, 1, 1, true, false,
			new String[0], RandomTestUtil.randomString(), null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _groupUser.getUserId()));
	}

	private static final String _CLASS_NAME_BLOGS_ENTRY =
		"com.liferay.blogs.model.BlogsEntry";

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private BlogsEntryService _blogsEntryService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private User _groupUser;
	private PermissionChecker _permissionChecker;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}