/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.client.dto.v1_0.Subscription;
import com.liferay.headless.admin.user.client.resource.v1_0.SubscriptionResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class SubscriptionResourceTest extends BaseSubscriptionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_userSubscriptionResource = SubscriptionResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Override
	@Test
	public void testDeleteMyUserAccountSubscription() throws Exception {
		super.testDeleteMyUserAccountSubscription();

		_testDeleteMyUserAccountSubscriptionWithAdminUser();
		_testDeleteMyUserAccountSubscriptionWithOwnerUser();
		_testDeleteMyUserAccountSubscriptionWithoutOwnerUser();
	}

	@Override
	@Test
	public void testGetMyUserAccountSubscription() throws Exception {
		super.testGetMyUserAccountSubscription();

		_testGetMyUserAccountSubscriptionWithAdminUser();
		_testGetMyUserAccountSubscriptionWithOwnerUser();
		_testGetMyUserAccountSubscriptionWithoutOwnerUser();
	}

	@Override
	protected Subscription testDeleteMyUserAccountSubscription_addSubscription()
		throws Exception {

		return _addSubscription(_adminUser.getUserId());
	}

	@Override
	protected Subscription testGetMyUserAccountSubscription_addSubscription()
		throws Exception {

		return _addSubscription(_adminUser.getUserId());
	}

	@Override
	protected Subscription
			testGetMyUserAccountSubscriptionsPage_addSubscription(
				Subscription subscription)
		throws Exception {

		return _addSubscription(_adminUser.getUserId());
	}

	@Override
	protected Subscription testGraphQLSubscription_addSubscription()
		throws Exception {

		return _addSubscription(_adminUser.getUserId());
	}

	private Subscription _addSubscription(long userId) throws Exception {
		com.liferay.subscription.model.Subscription serviceBuilderSubscription =
			_subscriptionLocalService.addSubscription(
				userId, testGroup.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLong());

		return new Subscription() {
			{
				setContentId(serviceBuilderSubscription.getClassPK());
				setContentType(serviceBuilderSubscription.getClassName());
				setDateCreated(serviceBuilderSubscription.getCreateDate());
				setDateModified(serviceBuilderSubscription.getModifiedDate());
				setFrequency(serviceBuilderSubscription.getFrequency());
				setId(serviceBuilderSubscription.getSubscriptionId());
				setSiteId(serviceBuilderSubscription.getGroupId());
			}
		};
	}

	private void _testDeleteMyUserAccountSubscriptionWithAdminUser()
		throws Exception {

		Subscription subscription = _addSubscription(_user.getUserId());

		assertHttpResponseStatusCode(
			204,
			subscriptionResource.deleteMyUserAccountSubscriptionHttpResponse(
				subscription.getId()));
	}

	private void _testDeleteMyUserAccountSubscriptionWithoutOwnerUser()
		throws Exception {

		Subscription subscription = _addSubscription(_adminUser.getUserId());

		assertHttpResponseStatusCode(
			403,
			_userSubscriptionResource.
				deleteMyUserAccountSubscriptionHttpResponse(
					subscription.getId()));
	}

	private void _testDeleteMyUserAccountSubscriptionWithOwnerUser()
		throws Exception {

		Subscription subscription = _addSubscription(_user.getUserId());

		assertHttpResponseStatusCode(
			204,
			_userSubscriptionResource.
				deleteMyUserAccountSubscriptionHttpResponse(
					subscription.getId()));
	}

	private void _testGetMyUserAccountSubscriptionWithAdminUser()
		throws Exception {

		Subscription subscription = _addSubscription(_user.getUserId());

		assertEquals(
			subscription,
			subscriptionResource.getMyUserAccountSubscription(
				subscription.getId()));
	}

	private void _testGetMyUserAccountSubscriptionWithoutOwnerUser()
		throws Exception {

		Subscription subscription = _addSubscription(_adminUser.getUserId());

		assertHttpResponseStatusCode(
			404,
			_userSubscriptionResource.getMyUserAccountSubscriptionHttpResponse(
				subscription.getId()));
	}

	private void _testGetMyUserAccountSubscriptionWithOwnerUser()
		throws Exception {

		Subscription subscription = _addSubscription(_user.getUserId());

		assertEquals(
			subscription,
			_userSubscriptionResource.getMyUserAccountSubscription(
				subscription.getId()));
	}

	private User _adminUser;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	@DeleteAfterTestRun
	private User _user;

	private SubscriptionResource _userSubscriptionResource;

}