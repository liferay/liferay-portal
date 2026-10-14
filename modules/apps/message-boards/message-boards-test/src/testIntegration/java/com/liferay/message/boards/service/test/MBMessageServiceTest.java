/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBBanLocalServiceUtil;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.service.MBMessageServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.constants.ServiceTestConstants;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.security.permission.DoAsUserThread;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class MBMessageServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		String name = "Test Category";
		String description = "This is a test category.";
		String displayStyle = MBCategoryConstants.DEFAULT_DISPLAY_STYLE;
		String emailAddress = null;
		String inProtocol = null;
		String inServerName = null;
		int inServerPort = 0;
		boolean inUseSSL = false;
		String inUserName = null;
		String inPassword = null;
		int inReadInterval = 0;
		String outEmailAddress = null;
		boolean outCustom = false;
		String outServerName = null;
		int outServerPort = 0;
		boolean outUseSSL = false;
		String outUserName = null;
		String outPassword = null;
		boolean allowAnonymous = false;
		boolean mailingListActive = false;

		_group = GroupTestUtil.addGroup();

		_users = new User[ServiceTestConstants.THREAD_COUNT];

		for (int i = 0; i < ServiceTestConstants.THREAD_COUNT; i++) {
			User user = UserTestUtil.addUser(_group.getGroupId());

			_users[i] = user;
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				new String[] {ActionKeys.ADD_MESSAGE, ActionKeys.VIEW},
				new String[] {ActionKeys.ADD_MESSAGE, ActionKeys.VIEW},
				MBCategory.class.getName()));

		_category = MBCategoryServiceUtil.addCategory(
			null, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, name,
			description, displayStyle, emailAddress, inProtocol, inServerName,
			inServerPort, inUseSSL, inUserName, inPassword, inReadInterval,
			outEmailAddress, outCustom, outServerName, outServerPort, outUseSSL,
			outUserName, outPassword, allowAnonymous, mailingListActive,
			serviceContext);
	}

	@Test
	public void testAddMessagesConcurrently() throws Exception {
		DoAsUserThread[] doAsUserThreads = new DoAsUserThread[_users.length];

		for (int i = 0; i < doAsUserThreads.length; i++) {
			String subject = "Test Message " + i;

			doAsUserThreads[i] = new AddMessageThread(
				_users[i].getUserId(), subject);
		}

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				BasePersistenceImpl.class.getName(), LoggerTestUtil.ERROR);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.spring.transaction." +
					"DefaultTransactionExecutor",
				LoggerTestUtil.ERROR);
			LogCapture logCapture3 = LoggerTestUtil.configureLog4JLogger(
				DoAsUserThread.class.getName(), LoggerTestUtil.ERROR);
			LogCapture logCapture4 = LoggerTestUtil.configureLog4JLogger(
				SqlExceptionHelper.class.getName(), LoggerTestUtil.ERROR);
			LogCapture logCapture5 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.messaging.internal.SynchronousDestination",
				LoggerTestUtil.ERROR)) {

			for (DoAsUserThread doAsUserThread : doAsUserThreads) {
				doAsUserThread.start();
			}

			for (DoAsUserThread doAsUserThread : doAsUserThreads) {
				doAsUserThread.join();
			}

			if (DBManagerUtil.getDBType() == DBType.HYPERSONIC) {
				for (LogEntry logEntry : logCapture2.getLogEntries()) {
					String message = logEntry.getMessage();

					Assert.assertTrue(
						message.startsWith(
							"Application exception overridden by commit " +
								"exception"));
				}

				for (LogEntry logEntry : logCapture5.getLogEntries()) {
					String message = logEntry.getMessage();

					Assert.assertTrue(
						message.startsWith("Unable to process message"));
				}
			}
		}

		int successCount = 0;

		for (DoAsUserThread doAsUserThread : doAsUserThreads) {
			if (doAsUserThread.isSuccess()) {
				successCount++;
			}
		}

		Assert.assertTrue(
			StringBundler.concat(
				"Only ", successCount, " out of ", _users.length,
				" threads added messages successfully"),
			successCount == _users.length);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testBannedUserCannotAddMessageInCategory() throws Exception {
		User user = _users[0];

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		MBBanLocalServiceUtil.addBan(
			TestPropsValues.getUserId(), user.getUserId(), serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			MBMessageServiceUtil.addMessage(
				_group.getGroupId(), _category.getCategoryId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				MBMessageConstants.DEFAULT_FORMAT, new ArrayList<>(), false,
				0.0, false, serviceContext);
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testBannedUserCannotAddRootMessage() throws Exception {
		User user = _users[0];

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		MBBanLocalServiceUtil.addBan(
			TestPropsValues.getUserId(), user.getUserId(), serviceContext);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			MBMessageServiceUtil.addMessage(
				_group.getGroupId(),
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				MBMessageConstants.DEFAULT_FORMAT, new ArrayList<>(), false,
				0.0, false, serviceContext);
		}
	}

	@Test
	public void testGetCategoryMessagesCount() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);

		MBMessageServiceUtil.addMessage(
			_group.getGroupId(), _category.getCategoryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			MBMessageConstants.DEFAULT_FORMAT, new ArrayList<>(), false, 0.0,
			false, serviceContext);

		Assert.assertEquals(
			1,
			MBMessageServiceUtil.getCategoryMessagesCount(
				_group.getGroupId(), _category.getCategoryId(),
				WorkflowConstants.STATUS_ANY));
		Assert.assertEquals(
			1,
			MBMessageServiceUtil.getCategoryMessagesCount(
				_group.getGroupId(), _category.getCategoryId(),
				WorkflowConstants.STATUS_APPROVED));

		_user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertEquals(
				0,
				MBMessageServiceUtil.getCategoryMessagesCount(
					_group.getGroupId(), _category.getCategoryId(),
					WorkflowConstants.STATUS_ANY));
			Assert.assertEquals(
				0,
				MBMessageServiceUtil.getCategoryMessagesCount(
					_group.getGroupId(), _category.getCategoryId(),
					WorkflowConstants.STATUS_APPROVED));
		}
	}

	private MBCategory _category;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

	@DeleteAfterTestRun
	private User[] _users;

	private class AddMessageThread extends DoAsUserThread {

		public AddMessageThread(long userId, String subject) {
			super(userId, ServiceTestConstants.RETRY_COUNT);

			_subject = subject;
		}

		@Override
		protected void doRun() throws Exception {
			String body = "This is a test message.";
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
				new ArrayList<>();
			boolean anonymous = false;
			double priority = 0.0;
			boolean allowPingbacks = false;

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			MBMessage mbMessage = MBMessageServiceUtil.addMessage(
				_category.getGroupId(), _category.getCategoryId(), _subject,
				body, MBMessageConstants.DEFAULT_FORMAT, inputStreamOVPs,
				anonymous, priority, allowPingbacks, serviceContext);

			MBMessageLocalServiceUtil.deleteMessage(mbMessage);
		}

		private final String _subject;

	}

}