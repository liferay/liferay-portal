/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.comment.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.message.boards.exception.NoSuchMessageException;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.Discussion;
import com.liferay.portal.kernel.comment.DiscussionComment;
import com.liferay.portal.kernel.comment.DiscussionCommentIterator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class MBCommentManagerImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();

		_fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, _user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			null, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		_createDiscussion();
	}

	@Test
	@TestInfo("LPD-115704")
	public void testAddComment() throws Exception {
		MBMessage mbMessage = _addMessage();

		Assert.assertThrows(
			NoSuchMessageException.class,
			() -> _addComment(mbMessage.getMessageId()));
	}

	@Test
	@TestInfo("LPD-115812")
	public void testAddCommentWithParentCommentOfAnotherClassName()
		throws Exception {

		Assert.assertThrows(
			NoSuchMessageException.class,
			() -> _commentManager.addComment(
				null, _user.getUserId(), User.class.getName(),
				_fileEntry.getFileEntryId(), _user.getFullName(),
				_parentCommentId, StringUtil.randomString(),
				StringUtil.randomString(), _createServiceContextFunction()));

		Assert.assertEquals(
			3,
			_commentManager.getChildCommentsCount(
				_parentCommentId, WorkflowConstants.STATUS_ANY));
	}

	@Test
	@TestInfo("LPD-115812")
	public void testAddCommentWithParentCommentOfAnotherClassPK()
		throws Exception {

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, _user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			null, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		Assert.assertThrows(
			NoSuchMessageException.class,
			() -> _commentManager.addComment(
				null, _user.getUserId(), DLFileEntryConstants.getClassName(),
				fileEntry.getFileEntryId(), _user.getFullName(),
				_parentCommentId, StringUtil.randomString(),
				StringUtil.randomString(), _createServiceContextFunction()));

		Assert.assertEquals(
			3,
			_commentManager.getChildCommentsCount(
				_parentCommentId, WorkflowConstants.STATUS_ANY));
	}

	@Test
	@TestInfo("LPD-115704")
	public void testDeleteComment() throws Exception {
		MBMessage mbMessage = _addMessage();

		Assert.assertThrows(
			NoSuchMessageException.class,
			() -> _commentManager.deleteComment(mbMessage.getMessageId()));
	}

	@Test
	@TestInfo("LPD-115704")
	public void testFetchComment() throws Exception {
		Assert.assertNotNull(_commentManager.fetchComment(_parentCommentId));

		MBMessage mbMessage = _addMessage();

		Assert.assertNull(
			_commentManager.fetchComment(
				_group.getGroupId(), mbMessage.getExternalReferenceCode()));
		Assert.assertNull(
			_commentManager.fetchComment(mbMessage.getMessageId()));
	}

	@Test
	public void testFetchDiscussionCommentCommentsCount() throws Exception {
		DiscussionComment discussionComment =
			_commentManager.fetchDiscussionComment(
				_user.getUserId(), _parentCommentId);

		Assert.assertEquals(2, discussionComment.getDescendantCommentsCount());
	}

	@Test
	public void testGetChildComments() {
		List<Comment> childComments = _commentManager.getChildComments(
			_parentCommentId, WorkflowConstants.STATUS_APPROVED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(childComments.toString(), 3, childComments.size());
	}

	@Test
	public void testGetChildCommentsCount() {
		Assert.assertEquals(
			3,
			_commentManager.getChildCommentsCount(
				_parentCommentId, WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	@TestInfo("LPD-115704")
	public void testGetComment() throws Exception {
		MBMessage mbMessage = _addMessage();

		Assert.assertThrows(
			NoSuchMessageException.class,
			() -> _commentManager.getComment(
				_group.getGroupId(), mbMessage.getExternalReferenceCode()));
	}

	@Test
	public void testGetRootComments() throws Exception {
		List<Comment> rootComments = _commentManager.getRootComments(
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId(),
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(rootComments.toString(), 2, rootComments.size());
	}

	@Test
	public void testGetRootCommentsCount() {
		Assert.assertEquals(
			2,
			_commentManager.getRootCommentsCount(
				DLFileEntryConstants.getClassName(),
				_fileEntry.getFileEntryId(),
				WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testSecondLevelThreadCommentsCount() throws Exception {
		Discussion discussion = _commentManager.getDiscussion(
			_user.getUserId(), _group.getGroupId(),
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId(),
			_createServiceContextFunction());

		DiscussionComment rootDiscussionComment =
			discussion.getRootDiscussionComment();

		DiscussionCommentIterator threadDiscussionCommentIterator =
			rootDiscussionComment.getThreadDiscussionCommentIterator();

		DiscussionComment discussionComment =
			threadDiscussionCommentIterator.next();

		Assert.assertEquals(3, discussionComment.getDescendantCommentsCount());
	}

	@Test
	public void testSubscribeDiscussion() throws Exception {
		_commentManager.subscribeDiscussion(
			TestPropsValues.getUserId(), _group.getGroupId(),
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId());

		Assert.assertNotNull(
			_subscriptionLocalService.fetchSubscription(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				MBDiscussion.class.getName() + StringPool.UNDERLINE +
					DLFileEntryConstants.getClassName(),
				_fileEntry.getFileEntryId()));
	}

	@Test
	public void testTopLevelThreadCommentsCount() throws Exception {
		Discussion discussion = _commentManager.getDiscussion(
			_user.getUserId(), _group.getGroupId(),
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId(),
			_createServiceContextFunction());

		DiscussionComment rootDiscussionComment =
			discussion.getRootDiscussionComment();

		Assert.assertEquals(
			2, rootDiscussionComment.getDescendantCommentsCount());
	}

	@Test
	public void testUnsubscribeDiscussion() throws Exception {
		_commentManager.subscribeDiscussion(
			TestPropsValues.getUserId(), _group.getGroupId(),
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId());

		_commentManager.unsubscribeDiscussion(
			TestPropsValues.getUserId(), DLFileEntryConstants.getClassName(),
			_fileEntry.getFileEntryId());

		Assert.assertNull(
			_subscriptionLocalService.fetchSubscription(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				DLFileEntryConstants.getClassName(),
				_fileEntry.getFileEntryId()));
	}

	@Test
	@TestInfo("LPD-115704")
	public void testUpdateComment() throws Exception {
		MBMessage mbMessage = _addMessage();

		Assert.assertThrows(
			NoSuchMessageException.class,
			() -> _commentManager.updateComment(
				_user.getUserId(), DLFileEntryConstants.getClassName(),
				_fileEntry.getFileEntryId(), mbMessage.getMessageId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_createServiceContextFunction()));
	}

	private long _addComment() throws Exception {
		User user = TestPropsValues.getUser();

		IdentityServiceContextFunction serviceContextFunction =
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(
					_group, user.getUserId()));

		return _commentManager.addComment(
			user.getUserId(), _group.getGroupId(),
			DLFileEntryConstants.getClassName(), _fileEntry.getFileEntryId(),
			StringUtil.randomString(), serviceContextFunction);
	}

	private long _addComment(long parentCommentId) throws Exception {
		User user = TestPropsValues.getUser();

		IdentityServiceContextFunction serviceContextFunction =
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(
					_group, user.getUserId()));

		return _commentManager.addComment(
			null, user.getUserId(), DLFileEntryConstants.getClassName(),
			_fileEntry.getFileEntryId(), user.getFullName(), parentCommentId,
			StringUtil.randomString(), StringUtil.randomString(),
			serviceContextFunction);
	}

	private MBMessage _addMessage() throws Exception {
		return MBTestUtil.addMessage(
			_group.getGroupId(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private void _createDiscussion() throws Exception {
		_parentCommentId = _addComment();

		_addComment();
		_addComment(_parentCommentId);
		_addComment(_parentCommentId);
		_addComment(_parentCommentId);
	}

	private Function<String, ServiceContext> _createServiceContextFunction()
		throws Exception {

		return new IdentityServiceContextFunction(
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Inject(
		filter = "component.name=com.liferay.message.boards.comment.internal.MBCommentManagerImpl"
	)
	private CommentManager _commentManager;

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	private long _parentCommentId;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	private User _user;

}