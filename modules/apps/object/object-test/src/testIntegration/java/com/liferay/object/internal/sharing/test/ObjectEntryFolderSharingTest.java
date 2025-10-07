/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.sharing.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.permission.contributor.PermissionSQLContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.sharing.security.permission.SharingPermissionChecker;
import com.liferay.sharing.test.util.BaseSharingTestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderSharingTest
	extends BaseSharingTestCase<ObjectEntryFolder> {

	@Ignore
	@Override
	@Test
	public void testAdminCanShareWithAddDiscussion() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testMovingToRecycleBinSharedModelDoesNotDeleteSharingEntries() {
	}

	@Ignore
	@Override
	@Test
	public void testUserWithAddDiscussionPermissionCanShareWithAddDiscussion()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testUserWithViewSharingEntryActionCannotViewPendingModel()
		throws Exception {
	}

	@Override
	protected void deleteModel(ObjectEntryFolder objectEntryFolder)
		throws PortalException {

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder);
	}

	@Override
	protected String getClassName() {
		return ObjectEntryFolder.class.getName();
	}

	@Override
	protected ObjectEntryFolder getModel(User user, Group group)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setScopeGroupId(group.getGroupId());

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, group.getGroupId(), user.getUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			StringUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			StringUtil.randomString(), serviceContext);
	}

	@Override
	protected int getModelCount(Group group) {
		return _objectEntryFolderLocalService.getObjectEntryFoldersCount(
			group.getGroupId(), group.getCompanyId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
	}

	@Override
	protected ModelResourcePermission<ObjectEntryFolder>
		getModelResourcePermission() {

		return _modelResourcePermission;
	}

	@Override
	protected ObjectEntryFolder getPendingModel(User user, Group group)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, group.getGroupId(), user.getUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			StringUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			StringUtil.randomString(), serviceContext);
	}

	@Override
	protected PermissionSQLContributor getPermissionSQLContributor() {
		return _permissionSQLContributorSnapshot.get();
	}

	@Override
	protected SharingPermissionChecker getSharingPermissionChecker() {
		return _sharingPermissionChecker;
	}

	@Override
	protected void moveModelToTrash(ObjectEntryFolder model) {
		throw new UnsupportedOperationException();
	}

	@Inject(
		filter = "model.class.name=com.liferay.object.model.ObjectEntryFolder"
	)
	private ModelResourcePermission<ObjectEntryFolder> _modelResourcePermission;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	private final Snapshot<PermissionSQLContributor>
		_permissionSQLContributorSnapshot = new Snapshot<>(
			ObjectEntryFolderSharingTest.class, PermissionSQLContributor.class,
			"(model.class.name=com.liferay.object.model.ObjectEntryFolder)");

	@Inject(
		filter = "model.class.name=com.liferay.object.model.ObjectEntryFolder"
	)
	private SharingPermissionChecker _sharingPermissionChecker;

}