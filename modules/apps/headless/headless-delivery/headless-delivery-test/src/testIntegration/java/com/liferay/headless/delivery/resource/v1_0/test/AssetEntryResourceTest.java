/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.headless.delivery.client.dto.v1_0.AssetEntry;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.AssetEntryResource;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class AssetEntryResourceTest extends BaseAssetEntryResourceTestCase {

	@Override
	@Test
	public void testGetAssetEntriesPage() throws Exception {
		super.testGetAssetEntriesPage();

		_testGetAssetEntriesPageAssetEntryFields();
		_testGetAssetEntriesPagePermissions();
		_testGetAssetEntriesPageWithClassNameIdFilter();
		_testGetAssetEntriesPageWithClassPKFilter();
		_testGetAssetEntriesPageWithClassTypeIdFilter();
		_testGetAssetEntriesPageWithMultipleClassNameIdsFilter();
		_testGetAssetEntriesPageWithMultipleGroupIds();
		_testGetAssetEntriesPageWithStatusFilter();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"assetEntryId", "className", "classPK", "title"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"classNameId", "classTypeId", "status"};
	}

	@Override
	protected AssetEntry testGetAssetEntriesPage_addAssetEntry(
			AssetEntry assetEntry)
		throws Exception {

		BlogsEntry blogsEntry = _addBlogsEntry(testGroup.getGroupId());

		return _toAssetEntry(
			BlogsEntry.class.getName(), blogsEntry.getEntryId());
	}

	private BlogsEntry _addBlogsEntry(long groupId) throws Exception {
		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	private AssetEntry _getAssetEntry(
		List<AssetEntry> assetEntries, long classPK) {

		for (AssetEntry assetEntry : assetEntries) {
			Long assetEntryClassPK = assetEntry.getClassPK();

			if ((assetEntryClassPK != null) && (assetEntryClassPK == classPK)) {
				return assetEntry;
			}
		}

		return null;
	}

	private boolean _hasClassPK(List<AssetEntry> assetEntries, long classPK) {
		for (AssetEntry assetEntry : assetEntries) {
			Long assetEntryClassPK = assetEntry.getClassPK();

			if ((assetEntryClassPK != null) && (assetEntryClassPK == classPK)) {
				return true;
			}
		}

		return false;
	}

	private void _testGetAssetEntriesPageAssetEntryFields() throws Exception {
		Long groupId = testGroup.getGroupId();

		BlogsEntry blogsEntry = _addBlogsEntry(groupId);

		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			new Long[] {groupId}, null, null,
			"classNameId eq " +
				PortalUtil.getClassNameId(BlogsEntry.class.getName()),
			Pagination.of(1, 20), null);

		AssetEntry assetEntry = null;

		for (AssetEntry curAssetEntry : (List<AssetEntry>)page.getItems()) {
			Long classPK = curAssetEntry.getClassPK();

			if ((classPK != null) && (classPK == blogsEntry.getEntryId())) {
				assetEntry = curAssetEntry;

				break;
			}
		}

		Assert.assertNotNull(assetEntry.getCreator());
		Assert.assertNotNull(assetEntry.getDateModified());
		Assert.assertEquals(
			Integer.valueOf(WorkflowConstants.STATUS_APPROVED),
			assetEntry.getStatus());
	}

	private void _testGetAssetEntriesPagePermissions() throws Exception {
		Role guestRole = _roleLocalService.getRole(
			testCompany.getCompanyId(), RoleConstants.GUEST);
		BlogsEntry notGuestViewableBlogsEntry = _addBlogsEntry(
			testGroup.getGroupId());

		_resourcePermissionLocalService.removeResourcePermission(
			testCompany.getCompanyId(), BlogsEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(notGuestViewableBlogsEntry.getEntryId()),
			guestRole.getRoleId(), ActionKeys.VIEW);

		BlogsEntry guestViewableBlogsEntry = _addBlogsEntry(
			testGroup.getGroupId());

		AssetEntryResource nestedFieldsAssetEntryResource =
			AssetEntryResource.builder(
			).authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).parameters(
				"nestedFields", "permissions"
			).build();

		Page<AssetEntry> page =
			nestedFieldsAssetEntryResource.getAssetEntriesPage(
				new Long[] {testGroup.getGroupId()}, null, null, null,
				Pagination.of(1, 50), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		AssetEntry guestViewableAssetEntry = _getAssetEntry(
			assetEntries, guestViewableBlogsEntry.getEntryId());

		Assert.assertTrue(
			ArrayUtil.exists(
				guestViewableAssetEntry.getPermissions(),
				permission ->
					Objects.equals(
						permission.getRoleName(), RoleConstants.GUEST) &&
					ArrayUtil.contains(
						permission.getActionIds(), ActionKeys.VIEW)));

		AssetEntry notGuestViewableAssetEntry = _getAssetEntry(
			assetEntries, notGuestViewableBlogsEntry.getEntryId());

		Assert.assertFalse(
			ArrayUtil.exists(
				notGuestViewableAssetEntry.getPermissions(),
				permission ->
					Objects.equals(
						permission.getRoleName(), RoleConstants.GUEST) &&
					ArrayUtil.contains(
						permission.getActionIds(), ActionKeys.VIEW)));

		page = assetEntryResource.getAssetEntriesPage(
			new Long[] {testGroup.getGroupId()}, null, null, null,
			Pagination.of(1, 50), null);

		assetEntries = (List<AssetEntry>)page.getItems();

		AssetEntry assetEntry = _getAssetEntry(
			assetEntries, guestViewableBlogsEntry.getEntryId());

		Assert.assertNull(assetEntry.getPermissions());
	}

	private void _testGetAssetEntriesPageWithClassNameIdFilter()
		throws Exception {

		Long groupId = testGroup.getGroupId();

		BlogsEntry blogsEntry = _addBlogsEntry(groupId);
		JournalArticle journalArticle = JournalTestUtil.addArticle(groupId, 0);

		String filterString =
			"classNameId eq " +
				PortalUtil.getClassNameId(BlogsEntry.class.getName());

		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			new Long[] {groupId}, null, null, filterString,
			Pagination.of(1, 20), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(_hasClassPK(assetEntries, blogsEntry.getEntryId()));
		Assert.assertFalse(
			_hasClassPK(assetEntries, journalArticle.getResourcePrimKey()));
	}

	private void _testGetAssetEntriesPageWithClassPKFilter() throws Exception {
		Long groupId = testGroup.getGroupId();

		BlogsEntry blogsEntry1 = _addBlogsEntry(groupId);
		BlogsEntry blogsEntry2 = _addBlogsEntry(groupId);

		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			new Long[] {groupId}, null, null,
			"classPK eq " + blogsEntry1.getEntryId(), Pagination.of(1, 20),
			null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(_hasClassPK(assetEntries, blogsEntry1.getEntryId()));
		Assert.assertFalse(_hasClassPK(assetEntries, blogsEntry2.getEntryId()));
	}

	private void _testGetAssetEntriesPageWithClassTypeIdFilter()
		throws Exception {

		Long groupId = testGroup.getGroupId();

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(groupId, 0);
		JournalArticle journalArticle2 = JournalTestUtil.addArticle(groupId, 0);

		DDMStructure ddmStructure1 = journalArticle1.getDDMStructure();
		DDMStructure ddmStructure2 = journalArticle2.getDDMStructure();

		Assert.assertNotEquals(ddmStructure1, ddmStructure2);

		String filterString =
			"classTypeId eq " + ddmStructure1.getStructureId();

		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			new Long[] {groupId}, null, null, filterString,
			Pagination.of(1, 20), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(
			_hasClassPK(assetEntries, journalArticle1.getResourcePrimKey()));
		Assert.assertFalse(
			_hasClassPK(assetEntries, journalArticle2.getResourcePrimKey()));
	}

	private void _testGetAssetEntriesPageWithMultipleClassNameIdsFilter()
		throws Exception {

		Long groupId = testGroup.getGroupId();

		BlogsEntry blogsEntry = _addBlogsEntry(groupId);
		JournalArticle journalArticle = JournalTestUtil.addArticle(groupId, 0);

		String filterString = StringBundler.concat(
			"classNameId in (",
			PortalUtil.getClassNameId(BlogsEntry.class.getName()), ", ",
			PortalUtil.getClassNameId(JournalArticle.class.getName()), ")");

		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			new Long[] {groupId}, null, null, filterString,
			Pagination.of(1, 20), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(_hasClassPK(assetEntries, blogsEntry.getEntryId()));
		Assert.assertTrue(
			_hasClassPK(assetEntries, journalArticle.getResourcePrimKey()));
	}

	private void _testGetAssetEntriesPageWithMultipleGroupIds()
		throws Exception {

		Long groupId1 = testGroup.getGroupId();

		BlogsEntry blogsEntry1 = _addBlogsEntry(groupId1);

		Long groupId2 = irrelevantGroup.getGroupId();

		BlogsEntry blogsEntry2 = _addBlogsEntry(groupId2);

		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			new Long[] {groupId1, groupId2}, null, null, null,
			Pagination.of(1, 50), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(_hasClassPK(assetEntries, blogsEntry1.getEntryId()));
		Assert.assertTrue(_hasClassPK(assetEntries, blogsEntry2.getEntryId()));
	}

	private void _testGetAssetEntriesPageWithStatusFilter() throws Exception {
		Long groupId = testGroup.getGroupId();

		BlogsEntry approvedBlogsEntry = _addBlogsEntry(groupId);

		BlogsEntry draftBlogsEntry = _addBlogsEntry(groupId);

		BlogsEntryLocalServiceUtil.updateStatus(
			TestPropsValues.getUserId(), draftBlogsEntry.getEntryId(),
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()),
			null);

		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			new Long[] {groupId}, null, null, "status eq 'approved'",
			Pagination.of(1, 50), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(
			_hasClassPK(assetEntries, approvedBlogsEntry.getEntryId()));
		Assert.assertFalse(
			_hasClassPK(assetEntries, draftBlogsEntry.getEntryId()));
	}

	private AssetEntry _toAssetEntry(String className, long classPK) {
		AssetEntry assetEntry = new AssetEntry();

		com.liferay.asset.kernel.model.AssetEntry serviceBuilderAssetEntry =
			_assetEntryLocalService.fetchEntry(className, classPK);

		assetEntry.setAssetEntryId(serviceBuilderAssetEntry.getEntryId());

		assetEntry.setClassName(className);
		assetEntry.setClassPK(classPK);
		assetEntry.setTitle(
			serviceBuilderAssetEntry.getTitle(LocaleUtil.getDefault()));

		return assetEntry;
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}