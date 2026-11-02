/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryLocalServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.notifications.test.util.BaseUserNotificationTestCase;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class MBUserNotificationTest extends BaseUserNotificationTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Override
	protected BaseModel<?> addBaseModel() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		return MBTestUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), _category.getCategoryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Override
	protected void addContainerModel() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		_category = MBCategoryLocalServiceUtil.addCategory(
			null, TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);
	}

	@Override
	protected String getPortletId() {
		return PortletProviderUtil.getPortletId(
			MBMessage.class.getName(), PortletProvider.Action.VIEW);
	}

	@Override
	protected void subscribeToContainer() throws Exception {
		MBCategoryLocalServiceUtil.subscribeCategory(
			user.getUserId(), group.getGroupId(), _category.getCategoryId());
	}

	@Override
	protected BaseModel<?> updateBaseModel(BaseModel<?> baseModel)
		throws Exception {

		MBMessage message = (MBMessage)baseModel;

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				message.getGroupId(), TestPropsValues.getUserId());

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.UPDATE);

		return MBMessageLocalServiceUtil.updateMessage(
			TestPropsValues.getUserId(), message.getMessageId(),
			RandomTestUtil.randomString(), serviceContext);
	}

	private MBCategory _category;

}