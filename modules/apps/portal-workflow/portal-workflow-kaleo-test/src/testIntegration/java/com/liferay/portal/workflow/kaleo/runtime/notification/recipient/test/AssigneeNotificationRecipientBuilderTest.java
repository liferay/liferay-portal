/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.notification.recipient.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserNotificationEventTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Victor Kammerer
 */
@RunWith(Arquillian.class)
public class AssigneeNotificationRecipientBuilderTest
	extends BaseNotificationRecipientBuilderTestCase {

	@Test
	public void testProcessKaleoNotificationRecipient() throws Exception {
		List<UserNotificationEvent> userNotificationEvents =
			UserNotificationEventTestUtil.getUserNotificationEvents(
				TestPropsValues.getUserId());

		Assert.assertTrue(userNotificationEvents.isEmpty());

		BlogsEntry blogsEntry = addBlogsEntry();

		Assert.assertTrue(blogsEntry.isPending());

		userNotificationEvents =
			UserNotificationEventTestUtil.getUserNotificationEvents(
				TestPropsValues.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());
	}

	@Override
	protected String getFileName() {
		return "dependencies/assignee-workflow-definition.json";
	}

}