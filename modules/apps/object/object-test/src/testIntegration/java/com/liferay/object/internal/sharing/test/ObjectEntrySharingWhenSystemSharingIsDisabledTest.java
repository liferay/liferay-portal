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
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.security.permission.SharingPermissionChecker;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Marco Galluzzi
 */
@RunWith(Arquillian.class)
public class ObjectEntrySharingWhenSystemSharingIsDisabledTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUserWithViewSharingEntryActionCannotViewObjectEntry()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.sharing.internal.configuration." +
						"SharingSystemConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", false
					).build())) {

			ObjectDefinition objectDefinition = _addObjectDefinition();

			ModelResourcePermission<ObjectEntry> modelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					objectDefinition.getClassName());

			ServiceTracker<SharingPermissionChecker, SharingPermissionChecker>
				serviceTracker = _getServiceTracker(
					objectDefinition.getClassName());

			try {
				ObjectEntry objectEntry = _addObjectEntry(
					objectDefinition.getObjectDefinitionId());

				User user = UserTestUtil.addGroupUser(
					_group, RoleConstants.POWER_USER);

				_addSharingEntry(objectEntry, user.getUserId());

				PermissionChecker permissionChecker =
					PermissionCheckerFactoryUtil.create(user);

				try (ContextUserReplace contextUserReplace =
						new ContextUserReplace(user, permissionChecker)) {

					Assert.assertFalse(
						modelResourcePermission.contains(
							permissionChecker, objectEntry, ActionKeys.VIEW));
				}
			}
			finally {
				serviceTracker.close();
			}
		}
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		return ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), "fieldName")),
			ObjectDefinitionConstants.SCOPE_SITE);
	}

	private ObjectEntry _addObjectEntry(long objectDefinitionId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);
	}

	private void _addSharingEntry(ObjectEntry objectEntry, long toUserId)
		throws Exception {

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, toUserId,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private ServiceTracker<SharingPermissionChecker, SharingPermissionChecker>
		_getServiceTracker(String objectDefinitionClassName) {

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		return ServiceTrackerFactory.open(
			bundle.getBundleContext(),
			StringBundler.concat(
				"(&(model.class.name=", objectDefinitionClassName,
				")(objectClass=", SharingPermissionChecker.class.getName(),
				"))"));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

}