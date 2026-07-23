/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.comment.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.comment.configuration.CommentGroupServiceConfiguration;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class MBDiscussionPermissionImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();
		_siteUser1 = UserTestUtil.addUser(_group.getGroupId());
		_siteUser2 = UserTestUtil.addUser(_group.getGroupId());

		_fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, _user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			null, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Test
	public void testAddDiscussionPermissionWhenUserIsDiscussionOwnerButDoesNotHaveNotAddDiscussionPermission()
		throws Exception {

		_addComment(_siteUser1);

		List<Role> roles = RoleLocalServiceUtil.getRoles(
			TestPropsValues.getCompanyId());

		for (Role role : roles) {
			if (RoleConstants.OWNER.equals(role.getName())) {
				continue;
			}

			_resourcePermissionLocalService.removeResourcePermission(
				TestPropsValues.getCompanyId(),
				DLFileEntryConstants.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_fileEntry.getFileEntryId()), role.getRoleId(),
				ActionKeys.ADD_DISCUSSION);
		}

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_siteUser1);

		Assert.assertFalse(
			_discussionPermission.hasAddPermission(
				permissionChecker, TestPropsValues.getCompanyId(),
				_group.getGroupId(), DLFileEntry.class.getName(),
				_fileEntry.getFileEntryId()));
	}

	@Test
	public void testBannedSiteMemberCannotAddComment() throws Exception {
		_mbBanLocalService.addBan(
			_user.getUserId(), _siteUser1.getUserId(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_siteUser1);

		Assert.assertFalse(
			_discussionPermission.hasAddPermission(
				permissionChecker, TestPropsValues.getCompanyId(),
				_group.getGroupId(), DLFileEntry.class.getName(),
				_fileEntry.getFileEntryId()));
	}

	@Test
	public void testSiteMemberCanAddComment() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_siteUser1);

		Assert.assertTrue(
			_discussionPermission.hasAddPermission(
				permissionChecker, TestPropsValues.getCompanyId(),
				_group.getGroupId(), DLFileEntry.class.getName(),
				_fileEntry.getFileEntryId()));
	}

	@Test
	public void testUserCannotUpdateHisComment() throws Exception {
		long commentId = _addComment(_siteUser1);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_siteUser1);

		Assert.assertFalse(
			_discussionPermission.hasUpdatePermission(
				permissionChecker, commentId));
	}

	@Test
	public void testUserCannotUpdateSomeoneElseCommentIfPropsEnabled()
		throws Exception {

		_withAlwaysEditableByOwnerEnabled(
			() -> {
				long commentId = _addComment(_siteUser1);

				PermissionChecker permissionChecker =
					PermissionCheckerFactoryUtil.create(_siteUser2);

				Assert.assertFalse(
					_discussionPermission.hasUpdatePermission(
						permissionChecker, commentId));
			});
	}

	@Test
	@TestInfo("LPD-93071")
	public void testUserCanUpdateAndDeleteHisCommentInSpace() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			PrincipalThreadLocal.setName(_user.getUserId());
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));

			Group cmsGroup = CMSTestUtil.getOrAddGroup(
				MBDiscussionPermissionImplTest.class);

			_depotEntry = _depotEntryLocalService.addDepotEntry(
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				null, DepotConstants.TYPE_SPACE,
				ServiceContextTestUtil.getServiceContext(
					cmsGroup.getGroupId()));

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_CMS_BASIC_WEB_CONTENT", cmsGroup.getCompanyId());

			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.
					getObjectEntryFolderByExternalReferenceCode(
						"L_CONTENTS", _depotEntry.getGroupId(),
						_depotEntry.getCompanyId());

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				_depotEntry.getGroupId(), _user.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				objectEntryFolder.getObjectEntryFolderId(), "en_US",
				HashMapBuilder.<String, Serializable>put(
					"title_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).build()
				).build(),
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));

			long commentId = _commentManager.addComment(
				_siteUser1.getUserId(), _depotEntry.getGroupId(),
				objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
				StringUtil.randomString(),
				new IdentityServiceContextFunction(
					ServiceContextTestUtil.getServiceContext(
						_depotEntry.getGroupId(), _siteUser1.getUserId())));

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(_siteUser1);

			Assert.assertTrue(
				_discussionPermission.hasUpdatePermission(
					permissionChecker, commentId));
			Assert.assertTrue(
				_discussionPermission.hasDeletePermission(
					permissionChecker, commentId));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	@Test
	public void testUserCanUpdateHisCommentIfPropsEnabled() throws Exception {
		_withAlwaysEditableByOwnerEnabled(
			() -> {
				long commentId = _addComment(_siteUser1);

				PermissionChecker permissionChecker =
					PermissionCheckerFactoryUtil.create(_siteUser1);

				Assert.assertTrue(
					_discussionPermission.hasUpdatePermission(
						permissionChecker, commentId));
			});
	}

	private long _addComment(User user) throws Exception {
		IdentityServiceContextFunction serviceContextFunction =
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(
					_group, user.getUserId()));

		return _commentManager.addComment(
			user.getUserId(), _group.getGroupId(),
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId(),
			StringUtil.randomString(), serviceContextFunction);
	}

	private void _withAlwaysEditableByOwnerEnabled(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Configuration configuration = _configurationAdmin.getConfiguration(
			CommentGroupServiceConfiguration.class.getName(),
			StringPool.QUESTION);

		Dictionary<String, Object> originalConfigurationProperties =
			configuration.getProperties();

		ConfigurationTestUtil.saveConfiguration(
			configuration,
			HashMapDictionaryBuilder.putAll(
				originalConfigurationProperties
			).put(
				"alwaysEditableByOwner", true
			).build());

		try {
			unsafeRunnable.run();
		}
		finally {
			ConfigurationTestUtil.saveConfiguration(
				configuration, originalConfigurationProperties);
		}
	}

	@Inject(
		filter = "component.name=com.liferay.message.boards.comment.internal.MBCommentManagerImpl"
	)
	private CommentManager _commentManager;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DiscussionPermission _discussionPermission;

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MBBanLocalService _mbBanLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private User _siteUser1;

	@DeleteAfterTestRun
	private User _siteUser2;

	private User _user;

}