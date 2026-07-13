/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.kernel.model.FileAttachment;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.service.DefinitionLocalServiceUtil;
import com.liferay.portal.reports.engine.console.service.EntryLocalServiceUtil;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alberto Sousa
 */
@RunWith(Arquillian.class)
public class EntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Test
	public void testAddEntry() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(EntryLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		TestMessageListener testMessageListener = new TestMessageListener();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				MessageListener.class, testMessageListener,
				MapUtil.singletonDictionary(
					"destination.name", DestinationNames.MAIL));

		Definition definition = _addDefinition();

		MailServiceTestUtil.clearMessages();

		try {
			String reportName = RandomTestUtil.randomString();

			EntryLocalServiceUtil.addEntry(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				definition.getDefinitionId(), "txt", false, null, null, false,
				StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString() + "@liferay.com",
				StringPool.BLANK, StringPool.BLANK, reportName,
				StringPool.BLANK, ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());

			com.liferay.portal.test.mail.MailMessage lastMailMessage =
				MailServiceTestUtil.getLastMailMessage();

			String mailMessageBody = lastMailMessage.getBody();

			Assert.assertTrue(mailMessageBody.contains(reportName));

			for (FileAttachment fileAttachment :
					testMessageListener.getFileAttachment()) {

				Assert.assertFalse(
					FileUtil.exists(fileAttachment.getFileName()));

				AssertUtils.assertFailure(
					IOException.class, null,
					() -> {
						InputStream inputStream =
							fileAttachment.getInputStream();

						inputStream.read();
					});
			}
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private Definition _addDefinition() throws Exception {
		String fileName = "reports_admin_template_source_sample_list_type";

		try (InputStream inputStream =
				EntryServiceTest.class.getResourceAsStream(
					"dependencies/" + fileName + ".jrxml")) {

			String content = StringUtil.read(inputStream);

			return DefinitionLocalServiceUtil.addDefinition(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(), null, 0, null, fileName,
				new UnsyncByteArrayInputStream(
					content.getBytes(StringPool.DEFAULT_CHARSET_NAME)),
				ServiceContextTestUtil.getServiceContext());
		}
	}

	private class TestMessageListener extends BaseMessageListener {

		public List<FileAttachment> getFileAttachment() {
			return _fileAttachments;
		}

		@Override
		protected void doReceive(Message message) {
			MailMessage mailMessage = (MailMessage)message.getPayload();

			_fileAttachments = mailMessage.getFileAttachments();
		}

		private List<FileAttachment> _fileAttachments = new ArrayList<>();

	}

}