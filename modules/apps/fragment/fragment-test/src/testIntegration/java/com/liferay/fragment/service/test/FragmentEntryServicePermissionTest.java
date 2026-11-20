/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FragmentEntryServicePermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.fragment.service"));

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group1.getGroupId());
		_user = UserTestUtil.addGroupUser(_group1, RoleConstants.POWER_USER);
	}

	@Test
	public void testAddFragmentEntryWithFragmentEntryKeyAndTypeWithPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentEntryWithFragmentEntryKeyAndTypeWithoutPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1, _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test
	public void testAddFragmentEntryWithFragmentEntryKeyWithPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentEntryWithFragmentEntryKeyWithoutPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1, _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test
	public void testAddFragmentEntryWithHTMLWithPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentEntryWithHTMLWithoutPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringPool.BLANK, RandomTestUtil.randomString(), StringPool.BLANK,
			false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test
	public void testAddFragmentEntryWithPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), RandomTestUtil.randomString(),
			StringUtil.randomString(), false, "{fieldSets: []}", null, 0, false,
			false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test
	public void testAddFragmentEntryWithTypeAndHTMLWithPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentEntryWithTypeAndHTMLWithoutPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test
	public void testAddFragmentEntryWithTypeWithPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentEntryWithTypeWithoutPermissions()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1, _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddFragmentEntryWithoutPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1, _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Test
	public void testCopyFragmentEntryWithPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId());

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		serviceContext = ServiceContextTestUtil.getServiceContext(
			_group1.getGroupId(), _user.getUserId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.copyFragmentEntry(
			_group1.getGroupId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testCopyFragmentEntryWithoutPermissions() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId());

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		serviceContext = ServiceContextTestUtil.getServiceContext(
			_group1.getGroupId(), _user.getUserId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.copyFragmentEntry(
			_group1.getGroupId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), serviceContext);
	}

	@Test
	public void testDeleteFragmentEntriesWithPermissions() throws Exception {
		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		long[] fragmentEntryIds = {
			fragmentEntry1.getFragmentEntryId(),
			fragmentEntry2.getFragmentEntryId()
		};

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.deleteFragmentEntries(fragmentEntryIds);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteFragmentEntriesWithoutPermissions() throws Exception {
		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		long[] fragmentEntryIds = {
			fragmentEntry1.getFragmentEntryId(),
			fragmentEntry2.getFragmentEntryId()
		};

		UserTestUtil.setUser(_user);

		_fragmentEntryService.deleteFragmentEntries(fragmentEntryIds);
	}

	@Test
	public void testDeleteFragmentEntryWithPermissions() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.deleteFragmentEntry(
			fragmentEntry.getFragmentEntryId());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDeleteFragmentEntryWithoutPermissions() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.deleteFragmentEntry(
			fragmentEntry.getFragmentEntryId());
	}

	@Test
	public void testFetchFragmentEntryWithPermissions() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.fetchFragmentEntry(
			fragmentEntry.getFragmentEntryId());
	}

	@Test
	@TestInfo("LPD-109774")
	public void testGetFragmentEntriesCountWithPermissions() throws Exception {
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		Assert.assertEquals(
			1,
			_fragmentEntryService.getFragmentEntriesCount(
				_group1.getGroupId(),
				_fragmentCollection.getFragmentCollectionId()));
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	@TestInfo("LPD-109774")
	public void testGetFragmentEntriesCountWithoutPermissions()
		throws Exception {

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.getFragmentEntriesCount(
			_group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId());
	}

	@Test
	@TestInfo("LPD-109774")
	public void testGetFragmentEntriesWithPermissions() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryService.getFragmentEntries(
				_fragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			fragmentEntries.toString(), 1, fragmentEntries.size());
		Assert.assertEquals(fragmentEntry, fragmentEntries.get(0));
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	@TestInfo("LPD-109774")
	public void testGetFragmentEntriesWithoutPermissions() throws Exception {
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.getFragmentEntries(
			_fragmentCollection.getFragmentCollectionId());
	}

	@Test
	@TestInfo("LPD-116159")
	public void testMoveFragmentEntry() throws Exception {
		_group2 = GroupTestUtil.addGroup();

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		_testMoveFragmentEntryWithFragmentCollectionFromAnotherGroup();
		_testMoveFragmentEntryWithPermissions();
		_testMoveFragmentEntryWithoutPermissions();
	}

	@Test
	@TestInfo("LPD-116159")
	public void testPublishDraft() throws Exception {
		_group2 = GroupTestUtil.addGroup();

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		_testPublishDraftWithFragmentEntryFromAnotherGroup();
		_testPublishDraftWithPermissions();
	}

	@Test
	@TestInfo("LPD-116159")
	public void testUpdateDraft() throws Exception {
		_group2 = GroupTestUtil.addGroup();

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		_testUpdateDraftWithFragmentCollectionFromAnotherGroup();
		_testUpdateDraftWithFragmentEntryFromAnotherGroup();
		_testUpdateDraftWithGroupIdFromAnotherGroup();
		_testUpdateDraftWithPermissions();
	}

	@Test
	@TestInfo("LPD-116159")
	public void testUpdateFragmentEntry() throws Exception {
		_group2 = GroupTestUtil.addGroup();

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		_testUpdateFragmentEntryNameWithPermissions();
		_testUpdateFragmentEntryNameWithoutPermissions();
		_testUpdateFragmentEntryValuesAndPreviewFileEntryIdWithPermissions();
		_testUpdateFragmentEntryValuesAndPreviewFileEntryIdWithoutPermissions();
		_testUpdateFragmentEntryValuesWithFragmentCollectionFromAnotherGroup();
		_testUpdateFragmentEntryValuesWithPermissions();
		_testUpdateFragmentEntryValuesWithoutPermissions();
		_testUpdateFragmentEntryWithFragmentEntryFromAnotherGroup();
	}

	@Test
	public void testUpdatePreviewFileEntryIdWithPermissions() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		long previewFileEntryId = fragmentEntry.getPreviewFileEntryId();

		_setRolePermissions(FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		UserTestUtil.setUser(_user);

		_fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), previewFileEntryId + 1);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUpdatePreviewFileEntryIdWithoutPermissions()
		throws Exception {

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		long previewFileEntryId = fragmentEntry.getPreviewFileEntryId();

		UserTestUtil.setUser(_user);

		_fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), previewFileEntryId + 1);
	}

	private FragmentEntry _addFragmentEntry(long groupId) throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(groupId);

		return FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
	}

	private void _assertMustHavePermission(
		PrincipalException.MustHavePermission principalException,
		long resourceId, String resourceName) {

		Assert.assertArrayEquals(
			new String[] {FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES},
			principalException.actionId);
		Assert.assertEquals(resourceId, principalException.resourceId);
		Assert.assertEquals(resourceName, principalException.resourceName);
	}

	private FragmentEntry _createFragmentEntry(long fragmentEntryId) {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.createFragmentEntry(fragmentEntryId);

		fragmentEntry.setGroupId(_group1.getGroupId());
		fragmentEntry.setFragmentCollectionId(
			_fragmentCollection.getFragmentCollectionId());

		return fragmentEntry;
	}

	private FragmentEntry _getDraftFragmentEntry(long fragmentEntryId)
		throws Exception {

		FragmentEntry draftFragmentEntry = _fragmentEntryLocalService.getDraft(
			fragmentEntryId);

		return (FragmentEntry)draftFragmentEntry.clone();
	}

	private void _setRolePermissions(String permissionType) throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		RoleTestUtil.addResourcePermission(
			_role, FragmentConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group1.getGroupId()),
			permissionType);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			_user.getUserId(), _group1.getGroupId(),
			new long[] {_role.getRoleId()});
	}

	private void _testMoveFragmentEntryWithFragmentCollectionFromAnotherGroup()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group2.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentEntryService.moveFragmentEntry(
				fragmentEntry.getFragmentEntryId(),
				fragmentCollection.getFragmentCollectionId());

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Fragment collection ",
					fragmentCollection.getFragmentCollectionId(),
					" does not belong to group ", _group1.getGroupId()),
				illegalArgumentException.getMessage());
		}
	}

	private void _testMoveFragmentEntryWithPermissions() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentCollection targetFragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group1.getGroupId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.moveFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			targetFragmentCollection.getFragmentCollectionId());
	}

	private void _testMoveFragmentEntryWithoutPermissions() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentCollection targetFragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group1.getGroupId());

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group1, RoleConstants.SITE_MEMBER));

		try {
			_fragmentEntryService.moveFragmentEntry(
				fragmentEntry.getFragmentEntryId(),
				targetFragmentCollection.getFragmentCollectionId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMustHavePermission(
				principalException, _group1.getGroupId(),
				FragmentConstants.RESOURCE_NAME);
		}
	}

	private void _testPublishDraftWithFragmentEntryFromAnotherGroup()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = _addFragmentEntry(_group2.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentEntryService.publishDraft(
				_createFragmentEntry(fragmentEntry.getFragmentEntryId()));

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMustHavePermission(
				principalException, _group2.getGroupId(),
				FragmentConstants.RESOURCE_NAME);
		}
	}

	private void _testPublishDraftWithPermissions() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntry draftFragmentEntry = _fragmentEntryLocalService.getDraft(
			fragmentEntry.getFragmentEntryId());

		String html = RandomTestUtil.randomString();

		draftFragmentEntry.setHtml(html);

		UserTestUtil.setUser(_user);

		FragmentEntry publishedFragmentEntry =
			_fragmentEntryService.publishDraft(draftFragmentEntry);

		Assert.assertEquals(html, publishedFragmentEntry.getHtml());
	}

	private void _testUpdateDraftWithFragmentCollectionFromAnotherGroup()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntry draftFragmentEntry = _getDraftFragmentEntry(
			fragmentEntry.getFragmentEntryId());

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group2.getGroupId());

		draftFragmentEntry.setFragmentCollectionId(
			fragmentCollection.getFragmentCollectionId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentEntryService.updateDraft(draftFragmentEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Fragment collection ",
					fragmentCollection.getFragmentCollectionId(),
					" does not belong to group ", _group1.getGroupId()),
				illegalArgumentException.getMessage());
		}
	}

	private void _testUpdateDraftWithFragmentEntryFromAnotherGroup()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = _addFragmentEntry(_group2.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentEntryService.updateDraft(
				_createFragmentEntry(fragmentEntry.getFragmentEntryId()));

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMustHavePermission(
				principalException, _group2.getGroupId(),
				FragmentConstants.RESOURCE_NAME);
		}
	}

	private void _testUpdateDraftWithGroupIdFromAnotherGroup()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntry draftFragmentEntry = _getDraftFragmentEntry(
			fragmentEntry.getFragmentEntryId());

		draftFragmentEntry.setGroupId(_group2.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentEntryService.updateDraft(draftFragmentEntry);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Fragment entry ", draftFragmentEntry.getFragmentEntryId(),
					" does not match the persisted fragment entry"),
				illegalArgumentException.getMessage());
		}
	}

	private void _testUpdateDraftWithPermissions() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntry draftFragmentEntry = _fragmentEntryLocalService.getDraft(
			fragmentEntry.getFragmentEntryId());

		String html = RandomTestUtil.randomString();

		draftFragmentEntry.setHtml(html);

		UserTestUtil.setUser(_user);

		FragmentEntry updatedFragmentEntry = _fragmentEntryService.updateDraft(
			draftFragmentEntry);

		Assert.assertEquals(html, updatedFragmentEntry.getHtml());
	}

	private void _testUpdateFragmentEntryNameWithPermissions()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), RandomTestUtil.randomString());
	}

	private void _testUpdateFragmentEntryNameWithoutPermissions()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group1, RoleConstants.SITE_MEMBER));

		try {
			_fragmentEntryService.updateFragmentEntry(
				fragmentEntry.getFragmentEntryId(),
				RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMustHavePermission(
				principalException, _group1.getGroupId(),
				FragmentConstants.RESOURCE_NAME);
		}
	}

	private void _testUpdateFragmentEntryValuesAndPreviewFileEntryIdWithPermissions()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(0, fragmentEntry.getPreviewFileEntryId());

		UserTestUtil.setUser(_user);

		_fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK,
			fragmentEntry.isCacheable(), "{\n\t\"fieldSets\": [\n\t]\n}",
			fragmentEntry.getIcon(), 1, false, fragmentEntry.getTypeOptions(),
			WorkflowConstants.STATUS_APPROVED);
	}

	private void _testUpdateFragmentEntryValuesAndPreviewFileEntryIdWithoutPermissions()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(0, fragmentEntry.getPreviewFileEntryId());

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group1, RoleConstants.SITE_MEMBER));

		try {
			_fragmentEntryService.updateFragmentEntry(
				fragmentEntry.getFragmentEntryId(),
				fragmentEntry.getFragmentCollectionId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK,
				fragmentEntry.isCacheable(), "{\n\t\"fieldSets\": [\n\t]\n}",
				fragmentEntry.getIcon(), 1, false,
				fragmentEntry.getTypeOptions(),
				WorkflowConstants.STATUS_APPROVED);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMustHavePermission(
				principalException, _group1.getGroupId(),
				FragmentConstants.RESOURCE_NAME);
		}
	}

	private void _testUpdateFragmentEntryValuesWithFragmentCollectionFromAnotherGroup()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group2.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentEntryService.updateFragmentEntry(
				fragmentEntry.getFragmentEntryId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK,
				fragmentEntry.isCacheable(), "{\n\t\"fieldSets\": [\n\t]\n}",
				fragmentEntry.getIcon(), fragmentEntry.getPreviewFileEntryId(),
				false, fragmentEntry.getTypeOptions(),
				WorkflowConstants.STATUS_APPROVED);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Fragment collection ",
					fragmentCollection.getFragmentCollectionId(),
					" does not belong to group ", _group1.getGroupId()),
				illegalArgumentException.getMessage());
		}
	}

	private void _testUpdateFragmentEntryValuesWithPermissions()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		UserTestUtil.setUser(_user);

		_fragmentEntryService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK,
			fragmentEntry.isCacheable(), "{\n\t\"fieldSets\": [\n\t]\n}",
			fragmentEntry.getIcon(), fragmentEntry.getPreviewFileEntryId(),
			false, fragmentEntry.getTypeOptions(),
			WorkflowConstants.STATUS_APPROVED);
	}

	private void _testUpdateFragmentEntryValuesWithoutPermissions()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = _fragmentEntryService.addFragmentEntry(
			null, _group1.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			StringUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group1, RoleConstants.SITE_MEMBER));

		try {
			_fragmentEntryService.updateFragmentEntry(
				fragmentEntry.getFragmentEntryId(),
				fragmentEntry.getFragmentCollectionId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK, false,
				"{\n\t\"fieldSets\": [\n\t]\n}", fragmentEntry.getIcon(),
				fragmentEntry.getPreviewFileEntryId(), false,
				fragmentEntry.getTypeOptions(),
				WorkflowConstants.STATUS_APPROVED);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMustHavePermission(
				principalException, _group1.getGroupId(),
				FragmentConstants.RESOURCE_NAME);
		}
	}

	private void _testUpdateFragmentEntryWithFragmentEntryFromAnotherGroup()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		FragmentEntry fragmentEntry = _addFragmentEntry(_group2.getGroupId());

		UserTestUtil.setUser(_user);

		try {
			_fragmentEntryService.updateFragmentEntry(
				_createFragmentEntry(fragmentEntry.getFragmentEntryId()));

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMustHavePermission(
				principalException, _group2.getGroupId(),
				FragmentConstants.RESOURCE_NAME);
		}
	}

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FragmentEntryService _fragmentEntryService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user;

}