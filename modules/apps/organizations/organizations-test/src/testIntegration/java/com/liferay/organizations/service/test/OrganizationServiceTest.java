/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class OrganizationServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetGtOrganizations() throws Exception {
		for (int i = 0; i < 10; i++) {
			_organizations.add(OrganizationTestUtil.addOrganization());
		}

		long parentOrganizationId = 0;
		int size = 5;

		List<Organization> organizations =
			_organizationService.getGtOrganizations(
				0, TestPropsValues.getCompanyId(), parentOrganizationId, size);

		Assert.assertEquals(
			organizations.toString(), size, organizations.size());

		Organization lastOrganization = organizations.get(
			organizations.size() - 1);

		organizations = _organizationService.getGtOrganizations(
			lastOrganization.getOrganizationId(),
			TestPropsValues.getCompanyId(), parentOrganizationId, size);

		Assert.assertEquals(
			organizations.toString(), size, organizations.size());

		long previousOrganizationId = 0;

		for (Organization organization : organizations) {
			long organizationId = organization.getOrganizationId();

			Assert.assertTrue(
				organizationId > lastOrganization.getOrganizationId());
			Assert.assertTrue(organizationId > previousOrganizationId);

			previousOrganizationId = organizationId;
		}
	}

	@Test
	public void testGetOrAddEmptyOrganization() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			RoleTestUtil.addResourcePermission(
				role, PortletKeys.PORTAL, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				ActionKeys.ADD_ORGANIZATION);

			User user = UserTestUtil.addUser();

			UserLocalServiceUtil.addRoleUser(
				role.getRoleId(), user.getUserId());

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				Organization organization =
					_organizationService.getOrAddEmptyOrganization(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString());

				Assert.assertNotNull(organization);
			}

			// Without permissions

			user = UserTestUtil.addUser();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				_organizationService.getOrAddEmptyOrganization(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString());

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertNotNull(principalException);
			}
		}
	}

	@Test
	public void testGetOrganizationsLikeName() throws Exception {
		List<Organization> allChildOrganizations = new ArrayList<>();
		Organization parentOrganization =
			OrganizationTestUtil.addOrganization();

		List<Organization> allOrganizations = new ArrayList<>(
			_organizationLocalService.getOrganizations(
				TestPropsValues.getCompanyId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID));

		try {
			String name = RandomTestUtil.randomString(10);

			long parentOrganizationId = parentOrganization.getOrganizationId();

			List<Organization> likeNameChildOrganizations = new ArrayList<>();

			for (int i = 0; i < 10; i++) {
				Organization organization =
					OrganizationTestUtil.addOrganization(
						parentOrganizationId, name + i, false);

				likeNameChildOrganizations.add(organization);
			}

			allChildOrganizations.addAll(likeNameChildOrganizations);
			allChildOrganizations.add(
				OrganizationTestUtil.addOrganization(
					parentOrganizationId, RandomTestUtil.randomString(10),
					false));
			allChildOrganizations.add(
				OrganizationTestUtil.addOrganization(
					parentOrganizationId, RandomTestUtil.randomString(10),
					false));
			allChildOrganizations.add(
				OrganizationTestUtil.addOrganization(
					parentOrganizationId, RandomTestUtil.randomString(10),
					false));

			allOrganizations.addAll(allChildOrganizations);

			_assertExpectedOrganizations(
				likeNameChildOrganizations, parentOrganizationId, name + "%");
			_assertExpectedOrganizations(
				likeNameChildOrganizations, parentOrganizationId,
				StringUtil.toLowerCase(name) + "%");
			_assertExpectedOrganizations(
				likeNameChildOrganizations, parentOrganizationId,
				StringUtil.toUpperCase(name) + "%");
			_assertExpectedOrganizations(
				likeNameChildOrganizations,
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				StringUtil.toUpperCase(name) + "%");
			_assertExpectedOrganizations(
				allChildOrganizations, parentOrganizationId, null);
			_assertExpectedOrganizations(
				allChildOrganizations, parentOrganizationId, "");
			_assertExpectedOrganizations(
				allOrganizations,
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, "");
		}
		finally {
			for (Organization childOrganization : allChildOrganizations) {
				_organizationLocalService.deleteOrganization(childOrganization);
			}

			_organizationLocalService.deleteOrganization(parentOrganization);
		}
	}

	private void _assertExpectedOrganizations(
			List<Organization> expectedOrganizations, long parentOrganizationId,
			String nameSearch)
		throws Exception {

		List<Organization> actualOrganizations =
			_organizationService.getOrganizations(
				TestPropsValues.getCompanyId(), parentOrganizationId,
				nameSearch, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			actualOrganizations.toString(), expectedOrganizations.size(),
			actualOrganizations.size());
		Assert.assertTrue(
			actualOrganizations.toString(),
			actualOrganizations.containsAll(expectedOrganizations));

		Assert.assertEquals(
			expectedOrganizations.size(),
			_organizationService.getOrganizationsCount(
				TestPropsValues.getCompanyId(), parentOrganizationId,
				nameSearch));
	}

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>();

	@Inject
	private OrganizationService _organizationService;

}