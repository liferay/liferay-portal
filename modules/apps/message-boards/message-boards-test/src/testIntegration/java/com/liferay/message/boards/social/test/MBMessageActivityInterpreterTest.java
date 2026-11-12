/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.social.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.service.MBThreadLocalServiceUtil;
import com.liferay.message.boards.social.MBActivityKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.util.Collections;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class MBMessageActivityInterpreterTest
	extends BaseMBSocialActivityInterpreterTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addActivities() throws Exception {
		message = addMessage(null);

		message = addMessage(message);
	}

	protected MBMessage addMessage(MBMessage parentMessage) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		long categoryId = MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;
		long parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
		long threadId = 0;

		if (parentMessage != null) {
			categoryId = parentMessage.getCategoryId();
			parentMessageId = parentMessage.getMessageId();
			threadId = parentMessage.getThreadId();
		}

		return MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), categoryId, threadId, parentMessageId,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			MBMessageConstants.DEFAULT_FORMAT,
			Collections.<ObjectValuePair<String, InputStream>>emptyList(),
			false, 0.0, false, serviceContext);
	}

	@Override
	protected int[] getActivityTypes() {
		return new int[] {
			MBActivityKeys.ADD_MESSAGE, MBActivityKeys.REPLY_MESSAGE
		};
	}

	@Override
	protected String getClassName() {
		return MBMessage.class.getName();
	}

	@Override
	protected boolean isSupportsRename() {
		return false;
	}

	@Override
	protected void moveModelsToTrash() throws Exception {
		MBThreadLocalServiceUtil.moveThreadToTrash(
			TestPropsValues.getUserId(), message.getThreadId());
	}

	@Override
	protected void renameModels() throws Exception {
	}

	@Override
	protected void restoreModelsFromTrash() throws Exception {
		MBThreadLocalServiceUtil.restoreThreadFromTrash(
			TestPropsValues.getUserId(), message.getThreadId());
	}

	protected MBMessage message;

}