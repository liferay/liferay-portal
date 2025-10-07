/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardMessage;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class MessageBoardMessageResourceTest
	extends BaseMessageBoardMessageResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());
	}

	@Override
	@Test
	public void testDeleteMessageBoardMessageMyRating() throws Exception {
		super.testDeleteMessageBoardMessageMyRating();

		MessageBoardMessage messageBoardMessage =
			testDeleteMessageBoardMessageMyRating_addMessageBoardMessage();

		assertHttpResponseStatusCode(
			204,
			messageBoardMessageResource.
				deleteMessageBoardMessageMyRatingHttpResponse(
					messageBoardMessage.getId()));
		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				deleteMessageBoardMessageMyRatingHttpResponse(
					messageBoardMessage.getId()));

		MessageBoardMessage irrelevantMessageBoardMessage =
			randomIrrelevantMessageBoardMessage();

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				deleteMessageBoardMessageMyRatingHttpResponse(
					irrelevantMessageBoardMessage.getId()));
	}

	@Override
	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPage()
		throws Exception {

		super.testGetMessageBoardMessageMessageBoardMessagesPage();

		// Message board messages in a tree hierarchy

		Long parentMessageBoardMessageId =
			testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId();

		MessageBoardMessage messageBoardMessage1 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardMessage1.getId(), randomMessageBoardMessage());

		Boolean flatten = false;

		Page<MessageBoardMessage> page =
			messageBoardMessageResource.
				getMessageBoardMessageMessageBoardMessagesPage(
					parentMessageBoardMessageId, flatten, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(messageBoardMessage1),
			(List<MessageBoardMessage>)page.getItems());
		assertValid(page);

		flatten = true;

		page =
			messageBoardMessageResource.
				getMessageBoardMessageMessageBoardMessagesPage(
					parentMessageBoardMessageId, flatten, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(messageBoardMessage1, messageBoardMessage2),
			(List<MessageBoardMessage>)page.getItems());
		assertValid(page);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteMessageBoardMessageMyRating()
		throws Exception {

		super.testGraphQLDeleteMessageBoardMessageMyRating();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostMessageBoardThreadMessageBoardMessage()
		throws Exception {

		super.testGraphQLPostMessageBoardThreadMessageBoardMessage();
	}

	@Test
	public void testPutSiteMessageBoardMessageWithoutParentMessageId()
		throws Exception {

		MessageBoardMessage randomMessageBoardMessage =
			randomMessageBoardMessage();

		randomMessageBoardMessage.setParentMessageBoardMessageId((Long)null);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				400,
				messageBoardMessageResource.
					putSiteMessageBoardMessageByExternalReferenceCodeHttpResponse(
						testGroup.getGroupId(),
						randomMessageBoardMessage.getExternalReferenceCode(),
						randomMessageBoardMessage));
		}
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"articleBody", "headline"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"childMessagesCount", "creatorId", "dateCreated", "dateModified",
			"lastPostDate", "messageBoardSectionId", "messageBoardThreadId",
			"modified", "parentMessageBoardMessageId", "ratingsStatTotalScore",
			"ratingValue", "viewCount"
		};
	}

	@Override
	protected MessageBoardMessage randomMessageBoardMessage() throws Exception {
		MessageBoardMessage messageBoardMessage =
			super.randomMessageBoardMessage();

		messageBoardMessage.setMessageBoardSectionId((Long)null);

		return messageBoardMessage;
	}

	@Override
	protected MessageBoardMessage
			testDeleteMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testDeleteMessageBoardMessageMyRating_addMessageBoardMessage()
		throws Exception {

		MessageBoardMessage messageBoardMessage = _addMessageBoardMessage();

		messageBoardMessageResource.putMessageBoardMessageMyRating(
			messageBoardMessage.getId(), randomRating());

		return messageBoardMessage;
	}

	@Override
	protected MessageBoardMessage
			testDeleteSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testGetMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected Long
			testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId()
		throws Exception {

		MBMessage mbMessage = _addMbMessage(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		return mbMessage.getRootMessageId();
	}

	@Override
	protected Long
			testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId()
		throws Exception {

		MBMessage mbMessage = _addMbMessage(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		return mbMessage.getThreadId();
	}

	@Override
	protected MessageBoardMessage
			testGetSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testGetSiteMessageBoardMessageByFriendlyUrlPath_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Override
	protected MessageBoardMessage
			testGetSiteMessageBoardMessagePermissionsPage_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				Long siteId, MessageBoardMessage messageBoardMessage)
		throws Exception {

		return _addMessageBoardMessage(messageBoardMessage, siteId);
	}

	@Override
	protected MessageBoardMessage
			testGetSiteUserMessageBoardMessagesActivityPage_addMessageBoardMessage(
				Long siteId, Long userId,
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		return _addMessageBoardMessage(messageBoardMessage, siteId);
	}

	@Override
	protected Long testGetSiteUserMessageBoardMessagesActivityPage_getSiteId()
		throws Exception {

		return TestPropsValues.getGroupId();
	}

	@Override
	protected Long testGetSiteUserMessageBoardMessagesActivityPage_getUserId()
		throws Exception {

		return UserLocalServiceUtil.getDefaultUserId(testGroup.getCompanyId());
	}

	@Override
	protected MessageBoardMessage
			testGraphQLMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		MBMessage mbMessage = MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), "test", testGroup.getGroupId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new ServiceContext());

		return messageBoardMessageResource.getMessageBoardMessage(
			mbMessage.getMessageId());
	}

	@Override
	protected MessageBoardMessage
			testGraphQLSiteMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPatchMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPutMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPutMessageBoardMessageMarkAsAnswer_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPutMessageBoardMessagePermissionsPage_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPutMessageBoardMessageSubscribe_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPutMessageBoardMessageUnmarkAsAnswer_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPutMessageBoardMessageUnsubscribe_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPutSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	@Override
	protected MessageBoardMessage
			testPutSiteMessageBoardMessageByExternalReferenceCode_createMessageBoardMessage()
		throws Exception {

		MessageBoardMessage messageBoardMessage =
			super.
				testPutSiteMessageBoardMessageByExternalReferenceCode_createMessageBoardMessage();

		messageBoardMessage.setParentMessageBoardMessageId(
			_mbThread.getRootMessageId());

		return messageBoardMessage;
	}

	@Override
	protected MessageBoardMessage
			testPutSiteMessageBoardMessagePermissionsPage_addMessageBoardMessage()
		throws Exception {

		return _addMessageBoardMessage();
	}

	private MBMessage _addMbMessage(Long siteId, String subject, String body)
		throws Exception {

		MBMessage mbMessage = MBTestUtil.addMessage(
			siteId,
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			subject, body);

		_mbThread = mbMessage.getThread();

		return mbMessage;
	}

	private MessageBoardMessage _addMessageBoardMessage() throws Exception {
		return _addMessageBoardMessage(null, testGroup.getGroupId());
	}

	private MessageBoardMessage _addMessageBoardMessage(
			MessageBoardMessage messageBoardMessage, Long siteId)
		throws Exception {

		if (messageBoardMessage != null) {
			MBMessage mbMessage = _addMbMessage(
				siteId, messageBoardMessage.getHeadline(),
				messageBoardMessage.getArticleBody());

			return messageBoardMessageResource.getMessageBoardMessage(
				mbMessage.getMessageId());
		}

		_addMbMessage(
			siteId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		return messageBoardMessageResource.
			postMessageBoardThreadMessageBoardMessage(
				_mbThread.getThreadId(), randomMessageBoardMessage());
	}

	@DeleteAfterTestRun
	private MBThread _mbThread;

}