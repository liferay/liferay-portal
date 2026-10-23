/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
@Sync
public class CheckKBArticleSchedulerJobConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testDoNotExpireKBArticleIfKBArticleIsPending()
		throws Exception {

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			_user.getUserId(), _group.getCompanyId(), _group.getGroupId(),
			KBArticle.class.getName(), 0, 0, "Single Approver", 1);

		KBArticle kbArticle = _addKBArticle(
			new Date(System.currentTimeMillis() + (Time.MINUTE * 5)), null,
			_getServiceContext(_user));

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, kbArticle.getStatus());

		kbArticle.setExpirationDate(
			new Date(System.currentTimeMillis() - (Time.MINUTE * 10)));

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, kbArticle.getStatus());

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				kbArticle.getCompanyId(), kbArticle.getGroupId(),
				KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		for (WorkflowTask workflowTask :
				_workflowTaskManager.getWorkflowTasksByWorkflowInstance(
					kbArticle.getCompanyId(), null,
					workflowInstanceLink.getWorkflowInstanceId(), false,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			_workflowTaskManager.assignWorkflowTaskToUser(
				kbArticle.getCompanyId(), _user.getUserId(),
				workflowTask.getWorkflowTaskId(), _user.getUserId(),
				StringPool.BLANK, null, null);

			_workflowTaskManager.completeWorkflowTask(
				kbArticle.getCompanyId(), _user.getUserId(),
				workflowTask.getWorkflowTaskId(), Constants.APPROVE,
				StringPool.BLANK, null);
		}

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, kbArticle.getStatus());
	}

	@Test
	public void testDoNotExpireKBArticleIfKBArticleIsScheduled()
		throws Exception {

		Date displayDate = new Date(
			System.currentTimeMillis() + (1 * Time.DAY));

		Date expirationDate = new Date(displayDate.getTime() + (1 * Time.DAY));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, UserLocalServiceUtil.getGuestUserId(_group.getCompanyId()),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, displayDate, expirationDate, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		kbArticle.setExpirationDate(
			new Date(System.currentTimeMillis() - (Time.MINUTE * 10)));

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EXPIRED, kbArticle.getStatus());
	}

	@Test
	public void testDoNotPublishKBArticleIfKBArticleIsDraft() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		Date displayDate = new Date(
			System.currentTimeMillis() + (Time.MINUTE * 10));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, UserLocalServiceUtil.getGuestUserId(_group.getCompanyId()),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, displayDate, null, null, null, serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, kbArticle.getStatus());

		kbArticle.setDisplayDate(
			new Date(System.currentTimeMillis() - (Time.MINUTE * 10)));

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, kbArticle.getStatus());
	}

	@Test
	public void testExpireKBArticle() throws Exception {
		Date expirationDate = new Date(
			System.currentTimeMillis() + (Time.MINUTE * 5));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, UserLocalServiceUtil.getGuestUserId(_group.getCompanyId()),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, RandomTestUtil.nextDate(), expirationDate, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		kbArticle.setExpirationDate(
			new Date(System.currentTimeMillis() - (Time.MINUTE * 10)));

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, kbArticle.getStatus());
	}

	@Test
	public void testExpireKBArticleSendsNotificationToOwner() throws Exception {
		User user = _addUser();

		KBArticle kbArticle = _addKBArticle(
			new Date(System.currentTimeMillis() + (Time.MINUTE * 5)), null,
			_getServiceContext(user));

		kbArticle.setExpirationDate(
			new Date(System.currentTimeMillis() - (Time.MINUTE * 10)));

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, kbArticle.getStatus());

		_assertUserNotificationEvent(
			kbArticle, "A knowledge base article has expired.", user);
	}

	@Test
	public void testExpireRepublishedKBArticle() throws Exception {
		User user = _addUser();

		ServiceContext serviceContext = _getServiceContext(user);

		KBArticle kbArticle = _addKBArticle(null, null, serviceContext);

		_kbArticleLocalService.expireKBArticle(
			user.getUserId(), kbArticle.getResourcePrimKey(), serviceContext);

		Date expirationDate = new Date(
			System.currentTimeMillis() + (Time.MINUTE * 5));

		_kbArticleLocalService.updateKBArticle(
			user.getUserId(), kbArticle.getResourcePrimKey(),
			kbArticle.getTitle(), kbArticle.getContent(),
			kbArticle.getDescription(), null, null, new Date(), expirationDate,
			null, null, null, serviceContext);

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(expirationDate, kbArticle.getExpirationDate());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());

		kbArticle.setExpirationDate(
			new Date(System.currentTimeMillis() - (Time.MINUTE * 10)));

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, kbArticle.getStatus());

		_assertUserNotificationEvent(
			kbArticle, "A knowledge base article has expired.", user);
	}

	@Test
	public void testPublishKBArticleIfKBArticleIsScheduled() throws Exception {
		Date displayDate = new Date(
			System.currentTimeMillis() + (Time.MINUTE * 10));

		KBArticle kbArticle = _kbArticleLocalService.addKBArticle(
			null, UserLocalServiceUtil.getGuestUserId(_group.getCompanyId()),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, displayDate, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, kbArticle.getStatus());

		kbArticle.setDisplayDate(
			new Date(System.currentTimeMillis() - (Time.MINUTE * 10)));

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kbArticle.getStatus());
	}

	@Test
	public void testReviewKBArticle() throws Exception {
		User user = _addUser();

		KBArticle kbArticle = _addKBArticle(
			null, new Date(System.currentTimeMillis() + (Time.MINUTE * 10)),
			_getServiceContext(user));

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		kbArticle.setReviewDate(new Date());

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		_assertUserNotificationEvent(
			kbArticle, "A knowledge base article needs review.", user);
	}

	@Test
	public void testReviewKBArticleAfterUpdatingReviewDate() throws Exception {
		User user = _addUser();

		ServiceContext serviceContext = _getServiceContext(user);

		KBArticle kbArticle = _addKBArticle(
			null, new Date(System.currentTimeMillis() + (Time.MINUTE * 10)),
			serviceContext);

		Date reviewDate = new Date(
			System.currentTimeMillis() + (Time.MINUTE * 2));

		_kbArticleLocalService.updateKBArticle(
			user.getUserId(), kbArticle.getResourcePrimKey(),
			RandomTestUtil.randomString(), kbArticle.getContent(),
			kbArticle.getDescription(), null, null, kbArticle.getDisplayDate(),
			null, reviewDate, null, null, serviceContext);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		kbArticle = _kbArticleLocalService.getLatestKBArticle(
			kbArticle.getResourcePrimKey(), WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(reviewDate, kbArticle.getReviewDate());

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		kbArticle.setReviewDate(new Date());

		kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);

		_kbArticleLocalService.checkKBArticles(_group.getCompanyId());

		_assertUserNotificationEvent(
			kbArticle, "A knowledge base article needs review.", user);
	}

	private KBArticle _addKBArticle(
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws Exception {

		return _kbArticleLocalService.addKBArticle(
			null, serviceContext.getUserId(),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, new Date(), expirationDate, reviewDate, null, serviceContext);
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser(_group.getGroupId());

		_users.add(user);

		return user;
	}

	private void _assertUserNotificationEvent(
			KBArticle kbArticle, String title, User user)
		throws Exception {

		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationEvent userNotificationEvent =
			userNotificationEvents.get(0);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			userNotificationEvent.getPayload());

		Assert.assertEquals(
			kbArticle.getTitle(), jsonObject.getString("entryTitle"));

		UserNotificationFeedEntry userNotificationFeedEntry =
			_userNotificationHandler.interpret(
				userNotificationEvent,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(title, userNotificationFeedEntry.getTitle());
	}

	private ServiceContext _getServiceContext(User user) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, user.getUserId());

		serviceContext.setPortletId(KBPortletKeys.KNOWLEDGE_BASE_ADMIN);

		return serviceContext;
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	private User _user;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Inject(
		filter = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN
	)
	private UserNotificationHandler _userNotificationHandler;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}