/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.comment.internal;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.DuplicateCommentException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class MBCommentManagerImplTest extends Mockito {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_mbCommentManagerImpl, "_mbMessageLocalService",
			_mbMessageLocalService);

		ReflectionTestUtil.setFieldValue(
			_mbCommentManagerImpl, "_portal", _portal);

		_setUpMBCommentManagerImpl();
		_setUpPortalUtil();
		_setUpServiceContext();
	}

	@Test
	public void testAddComment() throws Exception {
		_mbCommentManagerImpl.addComment(
			_USER_ID, _GROUP_ID, _CLASS_NAME, _ENTRY_ID, _BODY,
			_serviceContextFunction);

		Mockito.verify(
			_mbMessageLocalService
		).addDiscussionMessage(
			null, _USER_ID, StringPool.BLANK, _GROUP_ID, _CLASS_NAME, _ENTRY_ID,
			_THREAD_ID, _ROOT_MESSAGE_ID, StringPool.BLANK, _BODY,
			_serviceContext
		);

		Mockito.verify(
			_mbMessageLocalService
		).getThreadMessages(
			_THREAD_ID, WorkflowConstants.STATUS_APPROVED
		);
	}

	@Test
	public void testAddCommentWithExternalReferenceCodeAndParentMessage()
		throws Exception {

		when(
			_mbMessage.getMessageId()
		).thenReturn(
			_MBMESSAGE_ID
		);

		when(
			_mbMessage.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		when(
			_mbMessage.getThreadId()
		).thenReturn(
			_THREAD_ID
		);

		when(
			_mbMessage.getParentMessageId()
		).thenReturn(
			_ROOT_MESSAGE_ID
		);

		when(
			_mbMessage.isDiscussion()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			_MBMESSAGE_ID,
			_mbCommentManagerImpl.addComment(
				_EXTERNAL_REFERENCE_CODE, _USER_ID, _CLASS_NAME, _ENTRY_ID,
				_USER_NAME, _ROOT_MESSAGE_ID, _SUBJECT, _BODY,
				_serviceContextFunction));

		Mockito.verify(
			_mbMessageLocalService
		).getMessage(
			_ROOT_MESSAGE_ID
		);

		Mockito.verify(
			_mbMessage
		).getGroupId();

		Mockito.verify(
			_mbMessage
		).getThreadId();

		Mockito.verify(
			_mbMessageLocalService
		).addDiscussionMessage(
			_EXTERNAL_REFERENCE_CODE, _USER_ID, _USER_NAME, _GROUP_ID,
			_CLASS_NAME, _ENTRY_ID, _THREAD_ID, _ROOT_MESSAGE_ID, _SUBJECT,
			_BODY, _serviceContext
		);
	}

	@Test
	public void testAddCommentWithExternalReferenceCodeUserNameAndSubject()
		throws Exception {

		when(
			_mbMessage.getMessageId()
		).thenReturn(
			_MBMESSAGE_ID
		);

		Assert.assertEquals(
			_MBMESSAGE_ID,
			_mbCommentManagerImpl.addComment(
				_EXTERNAL_REFERENCE_CODE, _USER_ID, _GROUP_ID, _CLASS_NAME,
				_ENTRY_ID, _USER_NAME, _SUBJECT, _BODY,
				_serviceContextFunction));

		Mockito.verify(
			_mbMessageLocalService
		).addDiscussionMessage(
			_EXTERNAL_REFERENCE_CODE, _USER_ID, _USER_NAME, _GROUP_ID,
			_CLASS_NAME, _ENTRY_ID, _THREAD_ID, _ROOT_MESSAGE_ID, _SUBJECT,
			_BODY, _serviceContext
		);

		Mockito.verify(
			_mbMessageLocalService
		).getDiscussionMessageDisplay(
			_USER_ID, _GROUP_ID, _CLASS_NAME, _ENTRY_ID,
			WorkflowConstants.STATUS_APPROVED
		);
	}

	@Test
	public void testAddCommentWithUserNameAndSubject() throws Exception {
		when(
			_mbMessage.getMessageId()
		).thenReturn(
			_MBMESSAGE_ID
		);

		Assert.assertEquals(
			_MBMESSAGE_ID,
			_mbCommentManagerImpl.addComment(
				null, _USER_ID, _GROUP_ID, _CLASS_NAME, _ENTRY_ID, _USER_NAME,
				_SUBJECT, _BODY, _serviceContextFunction));

		Mockito.verify(
			_mbMessageLocalService
		).addDiscussionMessage(
			null, _USER_ID, _USER_NAME, _GROUP_ID, _CLASS_NAME, _ENTRY_ID,
			_THREAD_ID, _ROOT_MESSAGE_ID, _SUBJECT, _BODY, _serviceContext
		);

		Mockito.verify(
			_mbMessageLocalService
		).getDiscussionMessageDisplay(
			_USER_ID, _GROUP_ID, _CLASS_NAME, _ENTRY_ID,
			WorkflowConstants.STATUS_APPROVED
		);
	}

	@Test
	public void testAddDiscussion() throws Exception {
		_mbCommentManagerImpl.addDiscussion(
			_USER_ID, _GROUP_ID, _CLASS_NAME, _ENTRY_ID, _USER_NAME);

		Mockito.verify(
			_mbMessageLocalService
		).addDiscussionMessage(
			_USER_ID, _USER_NAME, _GROUP_ID, _CLASS_NAME, _ENTRY_ID,
			WorkflowConstants.ACTION_PUBLISH
		);
	}

	@Test(expected = DuplicateCommentException.class)
	public void testAddDuplicateComment() throws Exception {
		_setUpExistingComment(_BODY);

		_mbCommentManagerImpl.addComment(
			_USER_ID, _GROUP_ID, _CLASS_NAME, _ENTRY_ID, _BODY,
			_serviceContextFunction);
	}

	@Test
	public void testAddUniqueComment() throws Exception {
		_setUpExistingComment(_BODY + RandomTestUtil.randomString());

		_mbCommentManagerImpl.addComment(
			_USER_ID, _GROUP_ID, _CLASS_NAME, _ENTRY_ID, _BODY,
			_serviceContextFunction);

		Mockito.verify(
			_mbMessageLocalService
		).addDiscussionMessage(
			null, _USER_ID, StringPool.BLANK, _GROUP_ID, _CLASS_NAME, _ENTRY_ID,
			_THREAD_ID, _ROOT_MESSAGE_ID, StringPool.BLANK, _BODY,
			_serviceContext
		);
	}

	@Test
	public void testDeleteComment() throws Exception {
		when(
			_mbMessage.isDiscussion()
		).thenReturn(
			true
		);

		_mbCommentManagerImpl.deleteComment(_MBMESSAGE_ID);

		Mockito.verify(
			_mbMessageLocalService
		).deleteDiscussionMessage(
			_MBMESSAGE_ID
		);
	}

	@Test
	public void testDeleteDiscussion() throws Exception {
		long classPK = RandomTestUtil.randomLong();

		_mbCommentManagerImpl.deleteDiscussion(_CLASS_NAME, classPK);

		Mockito.verify(
			_mbMessageLocalService
		).deleteDiscussionMessages(
			_CLASS_NAME, classPK
		);
	}

	@Test
	public void testFetchComment() {
		long commentId = RandomTestUtil.randomLong();

		_mbCommentManagerImpl.fetchComment(commentId);

		Mockito.verify(
			_mbMessageLocalService
		).fetchMBMessage(
			commentId
		);
	}

	@Test
	public void testFetchCommentByGroupIdAndExternalReferenceCode() {
		when(
			_mbMessage.isDiscussion()
		).thenReturn(
			true
		);

		when(
			_mbMessageLocalService.fetchMBMessageByExternalReferenceCode(
				anyString(), anyLong())
		).thenReturn(
			_mbMessage
		);

		long groupId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();

		Comment comment = _mbCommentManagerImpl.fetchComment(
			groupId, externalReferenceCode);

		Assert.assertTrue(comment instanceof MBCommentImpl);
		Assert.assertSame(
			_mbMessage,
			ReflectionTestUtil.getFieldValue(
				(MBCommentImpl)comment, "_message"));

		Mockito.verify(
			_mbMessageLocalService
		).fetchMBMessageByExternalReferenceCode(
			externalReferenceCode, groupId
		);
	}

	@Test
	public void testFetchCommentByGroupIdAndExternalReferenceCodeNull() {
		when(
			_mbMessageLocalService.fetchMBMessageByExternalReferenceCode(
				anyString(), anyLong())
		).thenReturn(
			null
		);

		long groupId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();

		Comment comment = _mbCommentManagerImpl.fetchComment(
			groupId, externalReferenceCode);

		Assert.assertNull(comment);

		Mockito.verify(
			_mbMessageLocalService
		).fetchMBMessageByExternalReferenceCode(
			externalReferenceCode, groupId
		);
	}

	@Test
	public void testGetCommentByGroupIdAndExternalReferenceCode()
		throws PortalException {

		when(
			_mbMessage.isDiscussion()
		).thenReturn(
			true
		);

		when(
			_mbMessageLocalService.getMBMessageByExternalReferenceCode(
				anyString(), anyLong())
		).thenReturn(
			_mbMessage
		);

		long groupId = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();

		Comment comment = _mbCommentManagerImpl.getComment(
			groupId, externalReferenceCode);

		Assert.assertTrue(comment instanceof MBCommentImpl);
		Assert.assertSame(
			_mbMessage,
			ReflectionTestUtil.getFieldValue(
				(MBCommentImpl)comment, "_message"));

		Mockito.verify(
			_mbMessageLocalService
		).getMBMessageByExternalReferenceCode(
			externalReferenceCode, groupId
		);
	}

	@Test
	public void testGetCommentsCount() {
		long classPK = RandomTestUtil.randomLong();
		long classNameId = RandomTestUtil.randomLong();
		int commentsCount = RandomTestUtil.randomInt();

		when(
			_mbMessageLocalService.getDiscussionMessagesCount(
				classNameId, classPK, WorkflowConstants.STATUS_APPROVED)
		).thenReturn(
			commentsCount
		);

		when(
			_portal.getClassNameId(_CLASS_NAME)
		).thenReturn(
			classNameId
		);

		Assert.assertEquals(
			commentsCount,
			_mbCommentManagerImpl.getCommentsCount(_CLASS_NAME, classPK));
	}

	private void _setUpExistingComment(String body) {
		when(
			_mbMessage.getBody()
		).thenReturn(
			body
		);

		List<MBMessage> messages = Collections.singletonList(_mbMessage);

		when(
			_mbMessageLocalService.getThreadMessages(
				_THREAD_ID, WorkflowConstants.STATUS_APPROVED)
		).thenReturn(
			messages
		);
	}

	private void _setUpMBCommentManagerImpl() throws Exception {
		when(
			_mbMessageDisplay.getThread()
		).thenReturn(
			_mbThread
		);

		when(
			_mbMessageLocalService.getMessage(Mockito.anyLong())
		).thenReturn(
			_mbMessage
		);

		when(
			_mbMessageLocalService.addDiscussionMessage(
				Mockito.nullable(String.class), Mockito.anyLong(),
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.anyString(), Mockito.any())
		).thenReturn(
			_mbMessage
		);

		when(
			_mbMessageLocalService.addDiscussionMessage(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any())
		).thenReturn(
			_mbMessage
		);

		when(
			_mbMessageLocalService.getDiscussionMessageDisplay(
				_USER_ID, _GROUP_ID, _CLASS_NAME, _ENTRY_ID,
				WorkflowConstants.STATUS_APPROVED)
		).thenReturn(
			_mbMessageDisplay
		);

		when(
			_mbThread.getRootMessageId()
		).thenReturn(
			_ROOT_MESSAGE_ID
		);

		when(
			_mbThread.getThreadId()
		).thenReturn(
			_THREAD_ID
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	private void _setUpServiceContext() {
		when(
			_serviceContextFunction.apply(MBMessage.class.getName())
		).thenReturn(
			_serviceContext
		);
	}

	private static final String _BODY = RandomTestUtil.randomString();

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final long _ENTRY_ID = RandomTestUtil.randomLong();

	private static final String _EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _MBMESSAGE_ID = RandomTestUtil.randomLong();

	private static final long _ROOT_MESSAGE_ID = RandomTestUtil.randomLong();

	private static final String _SUBJECT = RandomTestUtil.randomString();

	private static final long _THREAD_ID = RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private static final String _USER_NAME = RandomTestUtil.randomString();

	private final MBCommentManagerImpl _mbCommentManagerImpl =
		new MBCommentManagerImpl();
	private final MBMessage _mbMessage = Mockito.mock(MBMessage.class);
	private final MBMessageDisplay _mbMessageDisplay = Mockito.mock(
		MBMessageDisplay.class);
	private final MBMessageLocalService _mbMessageLocalService = Mockito.mock(
		MBMessageLocalService.class);
	private final MBThread _mbThread = Mockito.mock(MBThread.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ServiceContext _serviceContext = new ServiceContext();
	private final Function<String, ServiceContext> _serviceContextFunction =
		Mockito.mock(Function.class);

}