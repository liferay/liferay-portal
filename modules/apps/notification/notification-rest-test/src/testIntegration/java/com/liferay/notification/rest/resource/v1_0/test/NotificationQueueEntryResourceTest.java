/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.rest.client.dto.v1_0.NotificationQueueEntry;
import com.liferay.notification.rest.client.serdes.v1_0.NotificationQueueEntrySerDes;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class NotificationQueueEntryResourceTest
	extends BaseNotificationQueueEntryResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testClientSerDesToDTO() throws Exception {
		super.testClientSerDesToDTO();

		NotificationQueueEntry notificationQueueEntry =
			new NotificationQueueEntry();

		notificationQueueEntry.setActions(
			new TreeMap<>(
				HashMapBuilder.put(
					"action1",
					new TreeMap<>(
						HashMapBuilder.put(
							"href", "href1"
						).put(
							"method", "method1"
						).build())
				).put(
					"action2",
					new TreeMap<>(
						HashMapBuilder.put(
							"href", "href2"
						).put(
							"method", "method2"
						).build())
				).build()));
		notificationQueueEntry.setBody("This is a body.");
		notificationQueueEntry.setSubject("This Is a Subject");
		notificationQueueEntry.setRecipients(
			new Object[] {
				new TreeMap<>(
					HashMapBuilder.put(
						"from", "first@liferay.com"
					).put(
						"fromName", "first"
					).put(
						"to", "first@liferay.com"
					).build()),
				new TreeMap<>(
					HashMapBuilder.put(
						"from", "second@liferay.com"
					).put(
						"fromName", "second"
					).put(
						"to", "second@liferay.com"
					).build()),
				new Object[] {
					new TreeMap<>(
						HashMapBuilder.put(
							"from", "third@liferay.com"
						).put(
							"fromName", "third"
						).put(
							"to", "third@liferay.com"
						).build())
				}
			});
		notificationQueueEntry.setType("email");

		JSONAssert.assertEquals(
			JSONUtil.put(
				"actions",
				JSONUtil.put(
					"action1",
					JSONUtil.put(
						"href", "href1"
					).put(
						"method", "method1"
					)
				).put(
					"action2",
					JSONUtil.put(
						"href", "href2"
					).put(
						"method", "method2"
					)
				)
			).put(
				"body", "This is a body."
			).put(
				"recipients",
				JSONUtil.putAll(
					JSONUtil.put(
						"from", "first@liferay.com"
					).put(
						"fromName", "first"
					).put(
						"to", "first@liferay.com"
					),
					JSONUtil.put(
						"from", "second@liferay.com"
					).put(
						"fromName", "second"
					).put(
						"to", "second@liferay.com"
					),
					JSONUtil.putAll(
						JSONUtil.put(
							"from", "third@liferay.com"
						).put(
							"fromName", "third"
						).put(
							"to", "third@liferay.com"
						)))
			).put(
				"subject", "This Is a Subject"
			).put(
				"type", "email"
			).toString(),
			notificationQueueEntry.toString(), JSONCompareMode.LENIENT);

		NotificationQueueEntrySerDes.NotificationQueueEntryJSONParser
			notificationQueueEntryJSONParser =
				new NotificationQueueEntrySerDes.
					NotificationQueueEntryJSONParser();

		Assert.assertEquals(
			notificationQueueEntry,
			notificationQueueEntryJSONParser.parseToDTO(
				notificationQueueEntry.toString()));
	}

	@Ignore
	@Override
	@Test
	public void testDeleteNotificationQueueEntry() throws Exception {
		super.testDeleteNotificationQueueEntry();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteNotificationQueueEntryBatch() throws Exception {
		super.testDeleteNotificationQueueEntryBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGetNotificationQueueEntriesPage() throws Exception {
		super.testGetNotificationQueueEntriesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetNotificationQueueEntriesPageWithPagination()
		throws Exception {

		super.testGetNotificationQueueEntriesPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetNotificationQueueEntry() throws Exception {
		super.testGetNotificationQueueEntry();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteNotificationQueueEntry() throws Exception {
		super.testGraphQLDeleteNotificationQueueEntry();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationQueueEntriesPage() throws Exception {
		super.testGraphQLGetNotificationQueueEntriesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationQueueEntry() throws Exception {
		super.testGraphQLGetNotificationQueueEntry();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationQueueEntryNotFound()
		throws Exception {

		super.testGraphQLGetNotificationQueueEntryNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testPostNotificationQueueEntry() throws Exception {
		super.testPostNotificationQueueEntry();
	}

	@Ignore
	@Override
	@Test
	public void testPutNotificationQueueEntryResend() throws Exception {
		super.testPutNotificationQueueEntryResend();
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		super.testVulcanCRUDItemDelegateGetItem();
	}

}