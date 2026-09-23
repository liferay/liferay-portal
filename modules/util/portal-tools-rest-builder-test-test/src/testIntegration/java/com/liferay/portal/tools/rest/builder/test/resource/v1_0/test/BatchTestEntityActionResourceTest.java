/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.BatchTestEntityAction;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.BatchTestEntityActionSerDes;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class BatchTestEntityActionResourceTest
	extends BaseBatchTestEntityActionResourceTestCase {

	@Override
	@Test
	public void testGraphQLPostBatchTestEntityAction() throws Exception {
		BatchTestEntityAction randomBatchTestEntityAction =
			randomBatchTestEntityAction();

		assertEquals(
			randomBatchTestEntityAction,
			BatchTestEntityActionSerDes.toDTO(
				String.valueOf(
					JSONUtil.getValueAsJSONObject(
						invokeGraphQLMutation(
							new GraphQLField(
								"createBatchTestEntityAction",
								HashMapBuilder.<String, Object>put(
									"batchTestEntityAction",
									StringBundler.concat(
										"{name: \"",
										randomBatchTestEntityAction.getName(),
										"\", sourceBatchTestEntityId: ",
										randomBatchTestEntityAction.
											getSourceBatchTestEntityId(),
										"}")
								).build(),
								getGraphQLFields())),
						"JSONObject/data",
						"JSONObject/createBatchTestEntityAction"))));
	}

	@Override
	@Test
	public void testPostBatchTestEntityAction() throws Exception {
		super.testPostBatchTestEntityAction();

		_testPostBatchTestEntityActionBatch();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name", "sourceBatchTestEntityId"};
	}

	@Override
	protected BatchTestEntityAction
			testPostBatchTestEntityAction_addBatchTestEntityAction(
				BatchTestEntityAction batchTestEntityAction)
		throws Exception {

		return batchTestEntityActionResource.postBatchTestEntityAction(
			batchTestEntityAction);
	}

	private void _assertLogEntry(
		BatchTestEntityAction batchTestEntityAction, LogEntry logEntry) {

		Assert.assertEquals(LoggerTestUtil.DEBUG, logEntry.getPriority());
		Assert.assertEquals(
			"Posting batch test entity action " +
				batchTestEntityAction.getName(),
			logEntry.getMessage());
	}

	private void _testPostBatchTestEntityActionBatch() throws Exception {
		BatchTestEntityAction batchTestEntityAction1 =
			randomBatchTestEntityAction();
		BatchTestEntityAction batchTestEntityAction2 =
			randomBatchTestEntityAction();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.tools.rest.builder.test.internal." +
					"resource.v1_0.BatchTestEntityActionResourceImpl",
				LoggerTestUtil.DEBUG)) {

			JSONObject importTaskJSONObject = _waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(
					batchTestEntityActionResource.
						postBatchTestEntityActionBatchHttpResponse(
							null,
							JSONUtil.putAll(
								JSONFactoryUtil.createJSONObject(
									batchTestEntityAction1.toString()),
								JSONFactoryUtil.createJSONObject(
									batchTestEntityAction2.toString()))
						).getContent()));

			Assert.assertEquals(
				2, importTaskJSONObject.getInt("processedItemsCount"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			_assertLogEntry(batchTestEntityAction1, logEntries.get(0));
			_assertLogEntry(batchTestEntityAction2, logEntries.get(1));
		}
	}

	private JSONObject _waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				"headless-batch-engine/v1.0/import-task" +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}
		}
	}

}