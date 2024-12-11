/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.moderation.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class MBModerationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();
	}

	@Test
	public void testAddMessageWithModerationDisabled() throws Exception {
		MBTestUtil.addMessage(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(50), RandomTestUtil.randomString(50));

		Assert.assertEquals(
			1,
			_mbMessageLocalService.getGroupMessagesCount(
				_group.getGroupId(), _user.getUserId(),
				WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testAddMessageWithModerationEnabledAndEnoughPublishedMessages()
		throws Exception {

		MBTestUtil.addMessage(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(50), RandomTestUtil.randomString(50));

		_withModerationEnabled(
			() -> {
				MBTestUtil.addMessage(
					_group.getGroupId(), _user.getUserId(),
					RandomTestUtil.randomString(50),
					RandomTestUtil.randomString(50));

				Assert.assertEquals(
					2,
					_mbMessageLocalService.getGroupMessagesCount(
						_group.getGroupId(), _user.getUserId(),
						WorkflowConstants.STATUS_APPROVED));
			});
	}

	@Test
	public void testAddMessageWithModerationEnabledAndNotEnoughPublishedMessages()
		throws Exception {

		_withModerationEnabled(
			() -> {
				MBTestUtil.addMessage(
					_group.getGroupId(), _user.getUserId(),
					RandomTestUtil.randomString(50),
					RandomTestUtil.randomString(50));

				Assert.assertEquals(
					0,
					_mbMessageLocalService.getGroupMessagesCount(
						_group.getGroupId(), _user.getUserId(),
						WorkflowConstants.STATUS_APPROVED));
			});
	}

	private void _withModerationEnabled(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _group.getCompanyId()
			).put(
				"enableMessageBoardsModeration", true
			).put(
				"minimumContributedMessages", 1
			).build();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.message.boards.moderation.configuration." +
						"MBModerationGroupConfiguration",
					dictionary)) {

			unsafeRunnable.run();
		}
		finally {
			dictionary.put("enableMessageBoardsModeration", false);

			ConfigurationTestUtil.saveConfiguration(
				"com.liferay.message.boards.moderation.configuration." +
					"MBModerationGroupConfiguration",
				dictionary);
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MBMessageLocalService _mbMessageLocalService;

	private User _user;

}