/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.subscription.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryLocalServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.subscription.test.util.BaseSubscriptionAuthorTestCase;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author José Ángel Jiménez
 */
@RunWith(Arquillian.class)
public class MBSubscriptionAuthorTest extends BaseSubscriptionAuthorTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Override
	protected long addBaseModel(long userId, long containerModelId)
		throws Exception {

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

	@Override
	protected long addContainerModel(long userId, long containerModelId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), userId);

		MBCategory category = MBCategoryLocalServiceUtil.addCategory(
			null, userId, containerModelId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		return category.getCategoryId();
	}

	@Override
	protected void addSubscription(long userId, long containerModelId)
		throws Exception {

		MBCategoryLocalServiceUtil.subscribeCategory(
			userId, group.getGroupId(), containerModelId);
	}

	@Override
	protected boolean isSubscriptionForAuthorEnabled() {
		return true;
	}

	@Override
	protected void updateBaseModel(long userId, long baseModelId)
		throws Exception {

		MBMessage message = MBMessageLocalServiceUtil.getMessage(baseModelId);

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