/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.notification.recipient.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ScriptNotificationRecipientBuilderTest
	extends BaseNotificationRecipientBuilderTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		MailServiceTestUtil.clearMessages();
	}

	@Test
	public void testProcessKaleoNotificationRecipient() throws Exception {
		BlogsEntry blogsEntry = addBlogsEntry();

		Assert.assertTrue(blogsEntry.isPending());

		AssertUtils.assertFailure(
			IndexOutOfBoundsException.class,
			"There are no messages in the inbox",
			MailServiceTestUtil::getLastMailMessage);

		User user = UserTestUtil.addUser();

		updateWorkflowDefinitionLink(
			StringUtil.replace(
				getWorkflowDefinitionContent(), "user = null;",
				StringBundler.concat(
					"user = com.liferay.portal.kernel.service.",
					"UserLocalServiceUtil.getUserById(", user.getUserId(),
					");")));

		blogsEntry = addBlogsEntry();

		Assert.assertTrue(blogsEntry.isPending());

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		Assert.assertTrue(
			StringUtil.contains(mailMessage.getBody(), "Content submitted."));
		Assert.assertTrue(
			StringUtil.startsWith(
				mailMessage.getFirstHeaderValue("To"), user.getFullName()));
	}

	@Override
	protected String getFileName() {
		return "dependencies/script-workflow-definition.json";
	}

}