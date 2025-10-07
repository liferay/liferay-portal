/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class CTSettingsConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER);
	}

	@Test
	public void testOnAfterSave() throws Exception {
		String pid = null;

		try {
			UserTestUtil.setUser(_user);

			CTCollection ctCollection =
				_ctCollectionLocalService.addCTCollection(
					null, TestPropsValues.getCompanyId(), _user.getUserId(), 0,
					RandomTestUtil.randomString(), null);

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			Assert.assertTrue(
				_ctCollectionModelResourcePermission.contains(
					permissionChecker, ctCollection, CTActionKeys.PUBLISH));

			pid = ConfigurationTestUtil.createFactoryConfiguration(
				CTSettingsConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", TestPropsValues.getCompanyId()
				).put(
					"defaultOwnerActionIds",
					new String[] {
						ActionKeys.UPDATE, ActionKeys.VIEW,
						CTActionKeys.INVITE_USERS
					}
				).build());

			Assert.assertFalse(
				_ctCollectionModelResourcePermission.contains(
					permissionChecker, ctCollection, CTActionKeys.PUBLISH));

			ConfigurationTestUtil.deleteConfiguration(pid);

			int initialCTEntriesCount =
				_ctEntryLocalService.getCTCollectionCTEntriesCount(
					ctCollection.getCtCollectionId());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollection.getCtCollectionId())) {

				pid = ConfigurationTestUtil.createFactoryConfiguration(
					CTSettingsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"companyId", TestPropsValues.getCompanyId()
					).put(
						"defaultOwnerActionIds",
						new String[] {
							ActionKeys.DELETE, ActionKeys.UPDATE,
							ActionKeys.VIEW, CTActionKeys.INVITE_USERS
						}
					).build());
			}

			int finalCTEntriesCount =
				_ctEntryLocalService.getCTCollectionCTEntriesCount(
					ctCollection.getCtCollectionId());

			Assert.assertEquals(0, finalCTEntriesCount - initialCTEntriesCount);
		}
		finally {
			if (pid != null) {
				ConfigurationTestUtil.deleteConfiguration(pid);
			}

			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.change.tracking.model.CTCollection"
	)
	private volatile ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	private Group _group;
	private User _user;

}