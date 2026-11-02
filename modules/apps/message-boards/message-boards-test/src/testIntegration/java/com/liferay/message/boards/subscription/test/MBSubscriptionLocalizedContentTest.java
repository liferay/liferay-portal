/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.subscription.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryLocalServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.subscription.test.util.BaseSubscriptionLocalizedContentTestCase;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class MBSubscriptionLocalizedContentTest
	extends BaseSubscriptionLocalizedContentTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Override
	protected long addBaseModel(long userId, long containerModelId)
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"MESSAGE_BOARDS_EMAIL_BULK", false)) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), userId);

			MBTestUtil.populateNotificationsServiceContext(
				serviceContext, Constants.ADD);

			MBMessage message = MBTestUtil.addMessage(
				userId, RandomTestUtil.randomString(), group.getGroupId(),
				containerModelId, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), serviceContext);

			return message.getMessageId();
		}
	}

	@Override
	protected void addSubscriptionContainerModel(long containerModelId)
		throws Exception {

		MBCategoryLocalServiceUtil.subscribeCategory(
			user.getUserId(), group.getGroupId(), containerModelId);
	}

	@Override
	protected String getPortletId() {
		return PortletProviderUtil.getPortletId(
			MBMessage.class.getName(), PortletProvider.Action.VIEW);
	}

	@Override
	protected String getServiceName() {
		return MBConstants.SERVICE_NAME;
	}

	@Override
	protected String getSubscriptionAddedBodyPreferenceName() {
		return "emailMessageAddedBody";
	}

	@Override
	protected String getSubscriptionUpdatedBodyPreferenceName() {
		return "emailMessageUpdatedBody";
	}

	@Override
	protected void updateBaseModel(long userId, long baseModelId)
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"MESSAGE_BOARDS_EMAIL_BULK", false)) {

			MBMessage message = MBMessageLocalServiceUtil.getMessage(
				baseModelId);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					message.getGroupId(), userId);

			MBTestUtil.populateNotificationsServiceContext(
				serviceContext, Constants.UPDATE);

			MBMessageLocalServiceUtil.updateMessage(
				userId, message.getMessageId(), RandomTestUtil.randomString(),
				serviceContext);
		}
	}

}