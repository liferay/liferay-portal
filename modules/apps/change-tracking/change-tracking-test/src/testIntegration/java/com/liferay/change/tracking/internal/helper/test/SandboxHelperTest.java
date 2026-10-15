/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.helper.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.helper.SandboxHelper;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionTemplateLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Noor Najjar
 */
@RunWith(Arquillian.class)
public class SandboxHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSandbox() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"sandboxEnabled", true
						).build())) {

			User user = UserTestUtil.addUser();

			CTCollection incompleteCTCollection =
				_ctCollectionLocalService.addCTCollection(
					null, user.getCompanyId(), user.getUserId(), 0,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString());

			incompleteCTCollection.setStatus(
				WorkflowConstants.STATUS_INCOMPLETE);

			incompleteCTCollection =
				_ctCollectionLocalService.updateCTCollection(
					incompleteCTCollection);

			CTPreferences ctPreferences =
				_ctPreferencesLocalService.getCTPreferences(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId());

			ctPreferences.setUserId(user.getUserId());
			ctPreferences.setCtCollectionId(
				CTCollectionThreadLocal.CT_COLLECTION_ID_PRODUCTION);

			ctPreferences = _ctPreferencesLocalService.updateCTPreferences(
				ctPreferences);

			PermissionChecker originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			try {
				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(user));

				_sandboxHelper.sandbox(ctPreferences);
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);
			}

			ctPreferences = _ctPreferencesLocalService.getCTPreferences(
				ctPreferences.getCtPreferencesId());

			Assert.assertEquals(
				incompleteCTCollection.getCtCollectionId(),
				ctPreferences.getCtCollectionId());
		}
	}

	@Test
	public void testSandboxWithDefaultSandboxCTCollectionTemplate()
		throws Exception {

		User publisherUser = UserTestUtil.addUser();

		CTCollectionTemplate ctCollectionTemplate =
			_ctCollectionTemplateLocalService.addCTCollectionTemplate(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				JSONUtil.put(
					"description", RandomTestUtil.randomString()
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"publicationsUserRoleUserIds",
					new long[] {publisherUser.getUserId()}
				).put(
					"roleValues",
					new int[] {PublicationRoleConstants.ROLE_PUBLISHER}
				).put(
					"userIds", new long[] {publisherUser.getUserId()}
				).toString());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"defaultSandboxCTCollectionTemplateId",
							ctCollectionTemplate.getCtCollectionTemplateId()
						).put(
							"sandboxEnabled", true
						).build())) {

			User user = UserTestUtil.addUser();

			CTPreferences ctPreferences =
				_ctPreferencesLocalService.getCTPreferences(
					user.getCompanyId(), user.getUserId());

			PermissionChecker originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			try {
				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(user));

				_sandboxHelper.sandbox(ctPreferences);
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);
			}

			ctPreferences = _ctPreferencesLocalService.getCTPreferences(
				ctPreferences.getCtPreferencesId());

			CTCollection ctCollection =
				_ctCollectionLocalService.getCTCollection(
					ctPreferences.getCtCollectionId());

			Assert.assertEquals(
				ctCollectionTemplate.getParsedPublicationName(),
				ctCollection.getName());

			Group group = _groupLocalService.fetchGroup(
				ctCollection.getCompanyId(),
				_portal.getClassNameId(CTCollection.class),
				ctCollection.getCtCollectionId());

			Assert.assertTrue(
				_userGroupRoleLocalService.hasUserGroupRole(
					publisherUser.getUserId(), group.getGroupId(),
					PublicationRoleConstants.getRoleName(
						PublicationRoleConstants.ROLE_PUBLISHER)));

			Assert.assertTrue(
				_userLocalService.hasRoleUser(
					publisherUser.getCompanyId(),
					RoleConstants.PUBLICATIONS_USER, publisherUser.getUserId(),
					false));
		}
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionTemplateLocalService _ctCollectionTemplateLocalService;

	@Inject
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SandboxHelper _sandboxHelper;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}