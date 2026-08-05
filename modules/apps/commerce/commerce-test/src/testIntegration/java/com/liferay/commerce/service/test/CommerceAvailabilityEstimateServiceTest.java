/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.CommerceAvailabilityEstimateService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
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
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommerceAvailabilityEstimateServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		_commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateService.
				addCommerceAvailabilityEstimate(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.nextDouble(), _serviceContext);
		_role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null, _serviceContext);

		_user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(_user.getUserId(), _role);
	}

	@Test
	public void testAddCommerceAvailabilityEstimate() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				addCommerceAvailabilityEstimate(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.nextDouble(), _serviceContext);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceActionKeys.ADD_COMMERCE_AVAILABILITY_ESTIMATE,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceConstants.RESOURCE_NAME_COMMERCE_AVAILABILITY,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceActionKeys.ADD_COMMERCE_AVAILABILITY_ESTIMATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				addCommerceAvailabilityEstimate(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.nextDouble(), _serviceContext);
		}
	}

	@Test
	public void testDeleteCommerceAvailabilityEstimate() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				deleteCommerceAvailabilityEstimate(
					_commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.DELETE, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceAvailabilityEstimate.getCompanyId(),
			CommerceAvailabilityEstimate.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				_commerceAvailabilityEstimate.
					getCommerceAvailabilityEstimateId()),
			_role.getRoleId(), new String[] {ActionKeys.DELETE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				deleteCommerceAvailabilityEstimate(
					_commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId());
		}
	}

	@Test
	public void testGetCommerceAvailabilityEstimate() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimate(
					_commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.VIEW, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceAvailabilityEstimate.getCompanyId(),
			CommerceAvailabilityEstimate.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				_commerceAvailabilityEstimate.
					getCommerceAvailabilityEstimateId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimate(
					_commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId());
		}
	}

	@Test
	public void testGetCommerceAvailabilityEstimates() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimates(
					_commerceAvailabilityEstimate.getCompanyId(), 0, 0, null);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceActionKeys.VIEW_COMMERCE_AVAILABILITY_ESTIMATES,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceConstants.RESOURCE_NAME_COMMERCE_AVAILABILITY,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceActionKeys.VIEW_COMMERCE_AVAILABILITY_ESTIMATES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimates(
					_commerceAvailabilityEstimate.getCompanyId(), 0, 0, null);
		}
	}

	@Test
	public void testGetCommerceAvailabilityEstimatesCount() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimatesCount(
					_commerceAvailabilityEstimate.getCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceActionKeys.VIEW_COMMERCE_AVAILABILITY_ESTIMATES,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceConstants.RESOURCE_NAME_COMMERCE_AVAILABILITY,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceActionKeys.VIEW_COMMERCE_AVAILABILITY_ESTIMATES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				getCommerceAvailabilityEstimatesCount(
					_commerceAvailabilityEstimate.getCompanyId());
		}
	}

	@Test
	public void testUpdateCommerceAvailabilityEstimate() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				updateCommerceAvailabilityEstimate(
					_commerceAvailabilityEstimate.getExternalReferenceCode(),
					_commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.nextDouble(), _serviceContext);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				ActionKeys.UPDATE, exception.getMessage(), _user.getUserId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceAvailabilityEstimate.getCompanyId(),
			CommerceAvailabilityEstimate.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				_commerceAvailabilityEstimate.
					getCommerceAvailabilityEstimateId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceAvailabilityEstimateService.
				updateCommerceAvailabilityEstimate(
					_commerceAvailabilityEstimate.getExternalReferenceCode(),
					_commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.nextDouble(), _serviceContext);
		}
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	private CommerceAvailabilityEstimate _commerceAvailabilityEstimate;

	@Inject
	private CommerceAvailabilityEstimateService
		_commerceAvailabilityEstimateService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}