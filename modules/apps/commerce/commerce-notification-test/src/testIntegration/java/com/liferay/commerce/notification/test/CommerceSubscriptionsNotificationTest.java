/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceSubscriptionNotificationConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryLocalService;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateLocalService;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateLocalServiceUtil;
import com.liferay.commerce.notification.test.util.CommerceNotificationTestUtil;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.engine.CommerceSubscriptionEngine;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.commerce.subscription.CommerceSubscriptionEntryHelper;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CommerceSubscriptionsNotificationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(),
			_group.getName(_serviceContext.getLanguageId()) + " Portal",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_toUser = UserTestUtil.addUser(
			_user.getCompanyId(), _user.getUserId(), "businessUser",
			_serviceContext.getLocale(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			new long[] {_serviceContext.getScopeGroupId()}, _serviceContext);

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {_toUser.getUserId()},
			null, _serviceContext);

		CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			_group.getCompanyId(), RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			_accountEntry.getAccountEntryId(), _serviceContext);

		_addCommerceNotificationTemplates();
	}

	@After
	public void tearDown() throws PortalException {
		for (CommerceNotificationTemplate commerceNotificationTemplate :
				_commerceNotificationTemplateList) {

			_commerceNotificationTemplateLocalService.
				deleteCommerceNotificationTemplate(
					commerceNotificationTemplate);
		}
	}

	@Test
	public void testSubscriptionStatusNotification() throws Exception {
		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceNotificationTemplateLocalService.
			addCommerceNotificationTemplate(
				_user.getUserId(), _commerceOrder.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				String.valueOf(_toUser.getUserId()),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				CommerceOrderConstants.ORDER_NOTIFICATION_PLACED, true,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), _serviceContext);

		_commerceOrder = CommerceTestUtil.addCheckoutDetailsToCommerceOrder(
			_commerceOrder, _user.getUserId(), true);

		_commerceSubscriptionEntryHelper.checkCommerceSubscriptions(
			_commerceOrder);

		_commerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
			_commerceOrder, _user.getUserId());

		List<CommerceSubscriptionEntry> commerceSubscriptionEntries =
			_commerceSubscriptionEntryLocalService.
				getCommerceSubscriptionEntries(
					_group.getCompanyId(), _commerceOrder.getUserId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commerceSubscriptionEntries.toString(), 1,
			commerceSubscriptionEntries.size());

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			commerceSubscriptionEntries.get(0);

		_commerceSubscriptionEngine.suspendRecurringPayment(
			commerceSubscriptionEntry.getCommerceSubscriptionEntryId());

		_checkCommerceNotificationTemplate(
			CommerceSubscriptionNotificationConstants.SUBSCRIPTION_SUSPENDED);

		_commerceSubscriptionEngine.activateRecurringPayment(
			commerceSubscriptionEntry.getCommerceSubscriptionEntryId());

		_checkCommerceNotificationTemplate(
			CommerceSubscriptionNotificationConstants.SUBSCRIPTION_ACTIVATED);

		_commerceSubscriptionEngine.cancelRecurringPayment(
			commerceSubscriptionEntry.getCommerceSubscriptionEntryId());

		_checkCommerceNotificationTemplate(
			CommerceSubscriptionNotificationConstants.SUBSCRIPTION_CANCELLED);
	}

	private void _addCommerceNotificationTemplates() throws Exception {
		_commerceNotificationTemplateList = new ArrayList<>(4);

		for (String commerceSubscriptionNotification :
				_COMMERCE_SUBSCRIPTION_NOTIFICATIONS) {

			CommerceNotificationTemplate commerceNotificationTemplate =
				CommerceNotificationTestUtil.addNotificationTemplate(
					"[%ORDER_CREATOR%]", commerceSubscriptionNotification,
					_serviceContext);

			_commerceNotificationTemplateList.add(commerceNotificationTemplate);
		}
	}

	private void _checkCommerceNotificationTemplate(
			String commerceNotificationTemplateType)
		throws Exception {

		List<String> commerceNotificationTemplateTypes =
			TransformUtil.transform(
				CommerceNotificationTestUtil.
					getCommerceNotificationQueueEntries(
						_group.getGroupId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null),
				commerceNotificationQueueEntry -> {
					CommerceNotificationTemplate commerceNotificationTemplate =
						CommerceNotificationTemplateLocalServiceUtil.
							getCommerceNotificationTemplate(
								commerceNotificationQueueEntry.
									getCommerceNotificationTemplateId());

					return commerceNotificationTemplate.getType();
				});

		Assert.assertTrue(
			commerceNotificationTemplateTypes.contains(
				commerceNotificationTemplateType));
	}

	private static final String[] _COMMERCE_SUBSCRIPTION_NOTIFICATIONS = {
		CommerceSubscriptionNotificationConstants.SUBSCRIPTION_ACTIVATED,
		CommerceSubscriptionNotificationConstants.SUBSCRIPTION_CANCELLED,
		CommerceSubscriptionNotificationConstants.SUBSCRIPTION_RENEWED,
		CommerceSubscriptionNotificationConstants.SUBSCRIPTION_SUSPENDED
	};

	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceNotificationQueueEntryLocalService
		_commerceNotificationQueueEntryLocalService;

	private List<CommerceNotificationTemplate>
		_commerceNotificationTemplateList;

	@Inject
	private CommerceNotificationTemplateLocalService
		_commerceNotificationTemplateLocalService;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceSubscriptionEngine _commerceSubscriptionEngine;

	@Inject
	private CommerceSubscriptionEntryHelper _commerceSubscriptionEntryHelper;

	@Inject
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _toUser;
	private User _user;

}