/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.sharing.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.permission.contributor.PermissionSQLContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.security.permission.SharingPermissionChecker;
import com.liferay.sharing.test.util.BaseSharingTestCase;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ObjectEntrySharingTest extends BaseSharingTestCase<ObjectEntry> {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_objectDefinition = _addObjectDefinition();

		_modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				_objectDefinition.getClassName());

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_permissionSQLContributorServiceTracker = ServiceTrackerFactory.open(
			bundleContext,
			StringBundler.concat(
				"(&(model.class.name=", _objectDefinition.getClassName(),
				")(objectClass=", PermissionSQLContributor.class.getName(),
				"))"));
		_sharingPermissionCheckerServiceTracker = ServiceTrackerFactory.open(
			bundleContext,
			StringBundler.concat(
				"(&(model.class.name=", _objectDefinition.getClassName(),
				")(objectClass=", SharingPermissionChecker.class.getName(),
				"))"));
	}

	@After
	public void tearDown() throws Exception {
		_permissionSQLContributorServiceTracker.close();
		_sharingPermissionCheckerServiceTracker.close();
	}

	@Ignore
	@Override
	@Test
	public void testMovingToRecycleBinSharedModelDoesNotDeleteSharingEntries() {
	}

	@Override
	protected void deleteModel(ObjectEntry objectEntry) throws PortalException {
		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	@Override
	protected String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	protected ObjectEntry getModel(User user, Group group)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setScopeGroupId(group.getGroupId());

		return _objectEntryLocalService.addObjectEntry(
			group.getGroupId(), user.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);
	}

	@Override
	protected int getModelCount(Group group) {
		return _objectEntryLocalService.getObjectEntriesCount(
			group.getGroupId(), _objectDefinition.getObjectDefinitionId());
	}

	@Override
	protected ModelResourcePermission<ObjectEntry>
		getModelResourcePermission() {

		return _modelResourcePermission;
	}

	@Override
	protected ObjectEntry getPendingModel(User user, Group group)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return _objectEntryLocalService.addObjectEntry(
			group.getGroupId(), user.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);
	}

	@Override
	protected PermissionSQLContributor getPermissionSQLContributor() {
		return _permissionSQLContributorServiceTracker.getService();
	}

	@Override
	protected SharingPermissionChecker getSharingPermissionChecker() {
		return _sharingPermissionCheckerServiceTracker.getService();
	}

	@Override
	protected void moveModelToTrash(ObjectEntry model) {
		throw new UnsupportedOperationException();
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, true, true, false,
				false, false, true, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(StringUtil.randomString()),
				"A" + StringUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "fieldName")),
				Collections.emptyList());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ModelResourcePermission<ObjectEntry> _modelResourcePermission;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ServiceTracker<PermissionSQLContributor, PermissionSQLContributor>
		_permissionSQLContributorServiceTracker;
	private ServiceTracker<SharingPermissionChecker, SharingPermissionChecker>
		_sharingPermissionCheckerServiceTracker;

}