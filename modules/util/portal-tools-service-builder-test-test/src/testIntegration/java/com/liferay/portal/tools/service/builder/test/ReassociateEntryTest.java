/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.ReassociateEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ReassociateEntryPersistence;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class ReassociateEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Test
	public void test() {
		ReassociateEntry reassociateEntry = _reassociateEntryPersistence.create(
			RandomTestUtil.nextLong());

		reassociateEntry.setName(RandomTestUtil.randomString());

		reassociateEntry = _reassociateEntryPersistence.update(
			reassociateEntry);

		Session session = _reassociateEntryPersistence.getCurrentSession();

		session.flush();

		session.clear();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.SQL", LoggerTestUtil.DEBUG)) {

			String name = RandomTestUtil.randomString();

			reassociateEntry.setName(name);

			reassociateEntry = _reassociateEntryPersistence.update(
				reassociateEntry);

			Assert.assertEquals(name, reassociateEntry.getName());

			session.flush();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(message, message.startsWith("select"));

			logEntry = logEntries.get(1);

			message = logEntry.getMessage();

			Assert.assertTrue(message, message.startsWith("update"));
		}

		session.clear();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.SQL", LoggerTestUtil.DEBUG)) {

			reassociateEntry = _reassociateEntryPersistence.reassociateIfAbsent(
				reassociateEntry);

			String name = RandomTestUtil.randomString();

			reassociateEntry.setName(name);

			reassociateEntry = _reassociateEntryPersistence.update(
				reassociateEntry);

			Assert.assertEquals(name, reassociateEntry.getName());

			session.flush();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(message, message.startsWith("update"));
		}
	}

	@Inject
	private ReassociateEntryPersistence _reassociateEntryPersistence;

}