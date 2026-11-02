/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.notifications.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.notifications.test.util.BaseUserNotificationTestCase;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.util.Date;

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
public class BlogsUserNotificationTest extends BaseUserNotificationTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Test
	public void testUserNotificationWhenUpdateReachesScheduledTime()
		throws Exception {

		subscribeToContainer();

		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			null, TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new Date(System.currentTimeMillis() + Time.HOUR), true, true, null,
			RandomTestUtil.randomString(), null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		blogsEntry.setDisplayDate(
			new Date(System.currentTimeMillis() - Time.DAY));

		blogsEntry = _blogsEntryLocalService.updateBlogsEntry(blogsEntry);

		Assert.assertEquals(0, MailServiceTestUtil.getInboxSize());

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		String[] mailMessageTo = mailMessage.getHeaderValues("To");

		Assert.assertEquals(mailMessageTo.toString(), 1, mailMessageTo.length);

		Assert.assertEquals(
			StringBundler.concat(
				user.getFullName(), " <", user.getEmailAddress(), ">"),
			mailMessageTo[0]);

		String mailMessageBody = mailMessage.getBody();

		Assert.assertTrue(mailMessageBody.contains(blogsEntry.getContent()));

		String[] mailMessageSubject = mailMessage.getHeaderValues("Subject");

		User testUser = TestPropsValues.getUser();

		Assert.assertEquals(
			StringBundler.concat(
				testUser.getFullName(), " Published \"", blogsEntry.getTitle(),
				"\" on ", group.getGroupKey(), " Blogs"),
			mailMessageSubject[0]);
	}

	@Override
	protected BaseModel<?> addBaseModel() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		BlogsTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		return BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);
	}

	@Override
	protected String getPortletId() {
		return BlogsPortletKeys.BLOGS;
	}

	@Override
	protected void subscribeToContainer() throws Exception {
		_blogsEntryLocalService.subscribe(user.getUserId(), group.getGroupId());
	}

	@Override
	protected BaseModel<?> updateBaseModel(BaseModel<?> baseModel)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		BlogsTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.UPDATE);

		serviceContext.setAttribute("sendEmailEntryUpdated", Boolean.TRUE);

		BlogsEntry blogsEntry = (BlogsEntry)baseModel;

		return _blogsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), blogsEntry.getEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.scheduler.CheckEntrySchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

}