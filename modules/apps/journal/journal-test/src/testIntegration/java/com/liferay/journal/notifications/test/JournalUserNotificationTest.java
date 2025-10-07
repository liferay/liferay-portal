/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.notifications.test.util.BaseUserNotificationTestCase;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class JournalUserNotificationTest extends BaseUserNotificationTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Test
	public void testNoUserNotificationWhenJournalArticleIsPending()
		throws Exception {

		_activateSingleApproverWorkflow();

		User subscribedUser = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, JournalFolder.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(group.getCompanyId()), ActionKeys.SUBSCRIBE);

		_roleLocalService.addUserRole(subscribedUser.getUserId(), role);

		JournalFolderLocalServiceUtil.subscribe(
			subscribedUser.getUserId(), group.getGroupId(),
			_folder.getFolderId());

		JournalArticle article = (JournalArticle)addBaseModel();

		_assertJournalArticleNotifications(
			article, 1, UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
			subscribedUser, 0);

		_journalArticleLocalService.moveArticleToTrash(
			user.getUserId(), article);

		_assertJournalArticleNotifications(
			article, 1, UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
			subscribedUser, 0);

		_deactivateSingleApproverWorkflow();
	}

	@Test
	public void testUserNotificationWhenJournalArticleExpiredAutomatically()
		throws Exception {

		JournalArticle expiredArticle = (JournalArticle)addBaseModel();

		expiredArticle.setExpirationDate(
			new Date(System.currentTimeMillis() - (Time.HOUR * 2)));

		expiredArticle = _journalArticleLocalService.updateJournalArticle(
			expiredArticle);

		subscribeToContainer();

		_journalArticleLocalService.checkArticles(group.getCompanyId());

		_assertJournalArticleNotifications(
			expiredArticle, 2,
			UserNotificationDefinition.NOTIFICATION_TYPE_EXPIRED_ENTRY, user,
			1);
	}

	@Test
	public void testUserNotificationWhenJournalArticleExpiredManually()
		throws Exception {

		JournalArticle expiredArticle = (JournalArticle)addBaseModel();

		subscribeToContainer();

		expiredArticle = JournalArticleLocalServiceUtil.expireArticle(
			TestPropsValues.getUserId(), group.getGroupId(),
			expiredArticle.getArticleId(), expiredArticle.getVersion(),
			expiredArticle.getUrlTitle(),
			ServiceContextTestUtil.getServiceContext());

		_assertJournalArticleNotifications(
			expiredArticle, 1,
			UserNotificationDefinition.NOTIFICATION_TYPE_EXPIRED_ENTRY, user,
			1);
	}

	@Test
	public void testUserNotificationWhenJournalArticleReviewNotificationIsSent()
		throws Exception {

		JournalArticle journalArticle = (JournalArticle)addBaseModel();

		journalArticle.setUserId(user.getUserId());
		journalArticle.setReviewDate(
			new Date(System.currentTimeMillis() - (Time.SECOND * 1)));

		journalArticle = _journalArticleLocalService.updateJournalArticle(
			journalArticle);

		subscribeToContainer();

		_journalArticleLocalService.checkArticles(group.getCompanyId());

		_assertJournalArticleNotifications(
			journalArticle, 2,
			UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY, user, 2);
	}

	@Test
	public void testUserNotificationWithArticlePreviewURL() throws Exception {
		String name = StringUtil.randomString();

		_workflowDefinitionManager.deployWorkflowDefinition(
			null, TestPropsValues.getCompanyId(), user.getUserId(),
			StringUtil.randomString(), name,
			_getContentBytes("workflow-definition.xml"));

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				user.getUserId(), group.getCompanyId(), group.getGroupId(),
				JournalFolder.class.getName(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.DDM_STRUCTURE_ID_ALL, name, 1);

		JournalArticle article = JournalTestUtil.addArticle(
			group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		_assertJournalArticleNotificationsCount(1, user, 1);

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		String body = mailMessage.getBody();

		Assert.assertTrue(body.contains(_getArticlePreviewURL(article)));

		_journalArticleLocalService.deleteArticle(article);

		_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
			workflowDefinitionLink);

		_workflowDefinitionManager.updateActive(
			user.getCompanyId(), user.getUserId(), name, 1, false);

		_workflowDefinitionManager.undeployWorkflowDefinition(
			user.getCompanyId(), user.getUserId(), name, 1);
	}

	@Override
	protected BaseModel<?> addBaseModel() throws Exception {
		return JournalTestUtil.addArticleWithWorkflow(
			group.getGroupId(), _folder.getFolderId(), true);
	}

	@Override
	protected void addContainerModel() throws Exception {
		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		_folder = journalFolderFixture.addFolder(
			group.getGroupId(), RandomTestUtil.randomString());
	}

	@Override
	protected String getPortletId() {
		return JournalPortletKeys.JOURNAL;
	}

	@Override
	protected void subscribeToContainer() throws Exception {
		JournalFolderLocalServiceUtil.subscribe(
			user.getUserId(), group.getGroupId(), _folder.getFolderId());
	}

	@Override
	protected BaseModel<?> updateBaseModel(BaseModel<?> baseModel)
		throws Exception {

		return JournalTestUtil.updateArticleWithWorkflow(
			(JournalArticle)baseModel, true);
	}

	private void _activateSingleApproverWorkflow() throws Exception {
		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			user.getUserId(), group.getCompanyId(), group.getGroupId(),
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL, "Single Approver", 1);
	}

	private void _assertJournalArticleNotifications(
			JournalArticle article, int emailNotificationsCount,
			int notificationType, User subscribedUser,
			int userNotificationsCount)
		throws Exception {

		_assertJournalArticleNotificationsCount(
			emailNotificationsCount, subscribedUser, userNotificationsCount);

		List<JSONObject> userNotificationEventsJSONObjects =
			getUserNotificationEventsJSONObjects(subscribedUser.getUserId());

		for (int i = 0; i < userNotificationsCount; i++) {
			JSONObject jsonObject = userNotificationEventsJSONObjects.get(i);

			Assert.assertEquals(article.getId(), jsonObject.getLong("classPK"));
			Assert.assertEquals(
				notificationType, jsonObject.getInt("notificationType"));
		}
	}

	private void _assertJournalArticleNotificationsCount(
			int emailNotificationsCount, User subscribedUser,
			int userNotificationsCount)
		throws Exception {

		Assert.assertEquals(
			emailNotificationsCount, MailServiceTestUtil.getInboxSize());

		List<JSONObject> userNotificationEventsJSONObjects =
			getUserNotificationEventsJSONObjects(subscribedUser.getUserId());

		Assert.assertEquals(
			userNotificationEventsJSONObjects.toString(),
			userNotificationsCount, userNotificationEventsJSONObjects.size());
	}

	private void _deactivateSingleApproverWorkflow() throws Exception {
		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			user.getUserId(), group.getCompanyId(), group.getGroupId(),
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL, null);
	}

	private String _getArticlePreviewURL(JournalArticle article)
		throws Exception {

		String portletId = PortletProviderUtil.getPortletId(
			JournalArticle.class.getName(), PortletProvider.Action.EDIT);

		String previewURL = _portal.getControlPanelFullURL(
			article.getGroupId(), portletId, null);

		String namespace = _portal.getPortletNamespace(portletId);

		previewURL = HttpComponentsUtil.addParameter(
			previewURL, namespace + "mvcPath", "/preview_article_content.jsp");
		previewURL = HttpComponentsUtil.addParameter(
			previewURL, namespace + "groupId", article.getGroupId());
		previewURL = HttpComponentsUtil.addParameter(
			previewURL, namespace + "articleId", article.getArticleId());
		previewURL = HttpComponentsUtil.addParameter(
			previewURL, namespace + "version", article.getVersion());

		return previewURL;
	}

	private byte[] _getContentBytes(String fileName) throws Exception {
		Class<?> clazz = getClass();

		String content = StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/journal/dependencies/" + fileName);

		return content.getBytes();
	}

	private JournalFolder _folder;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}