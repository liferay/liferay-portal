/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.net.URL;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 * @author Peter Yoo
 */
public class TestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testCacheTestrayCaseResultURL() throws Exception {
		_testCacheTestrayCaseResultURL(false);
		_testCacheTestrayCaseResultURL(true);
	}

	@Test
	public void testGetTestrayCaseResultURL() throws Exception {
		_testGetTestrayCaseResultURL(RandomTestUtil.randomLong(), null);
		_testGetTestrayCaseResultURL(null, RandomTestUtil.randomLong());
	}

	@Test
	public void testGetTestrayCaseResultURLCached() throws Exception {
		TestrayServer testrayServer = _mockTestrayServer();

		TestrayCaseResult testrayCaseResult = _mockTestrayCaseResult(
			testrayServer);

		URL testrayCaseResultURL = new URL(
			_TESTRAY_URL + "/case-result/" + RandomTestUtil.randomLong());

		ReflectionTestUtil.setFieldValue(
			testrayCaseResult, "_testrayCaseResultURL", testrayCaseResultURL);

		testSame(
			testrayCaseResultURL, testrayCaseResult.getTestrayCaseResultURL());

		Mockito.verifyNoInteractions(testrayServer);
	}

	@Test
	public void testGetTestrayCaseResultURLNull() throws Exception {
		_testGetTestrayCaseResultURLNull(
			Mockito.mock(TestrayBuild.class), null);
		_testGetTestrayCaseResultURLNull(null, Mockito.mock(TestrayCase.class));
	}

	@Test
	public void testGetTestrayCaseResultURLRequest() throws Exception {
		TestrayServer testrayServer = _mockTestrayServer();

		_setRequestGraphQLResult(testrayServer, Collections.emptySet());
		_setRequestPostResult(testrayServer, RandomTestUtil.randomLong());

		TestrayCaseResult testrayCaseResult = _mockTestrayCaseResult(
			testrayServer);

		Mockito.doReturn(
			_DURATION
		).when(
			testrayCaseResult
		).getDuration();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			testrayCaseResult
		).getErrors();

		Mockito.doReturn(
			TestrayCaseResult.Status.FAILED
		).when(
			testrayCaseResult
		).getStatus();

		Mockito.doReturn(
			Collections.singletonList(_mockTestrayAttachment())
		).when(
			testrayCaseResult
		).getTestrayAttachments();

		Assert.assertNotNull(testrayCaseResult.getTestrayCaseResultURL());

		ArgumentCaptor<String> requestDataArgumentCaptor =
			ArgumentCaptor.forClass(String.class);

		Mockito.verify(
			testrayServer
		).requestPost(
			Mockito.eq("/o/c/caseresults"), requestDataArgumentCaptor.capture()
		);

		JSONObject requestJSONObject = new JSONObject(
			requestDataArgumentCaptor.getValue());

		Assert.assertTrue(
			requestJSONObject.toString(), requestJSONObject.has("attachments"));
		Assert.assertTrue(
			requestJSONObject.toString(), requestJSONObject.has("dueStatus"));
		Assert.assertFalse(
			requestJSONObject.toString(), requestJSONObject.has("errors"));
		Assert.assertTrue(
			requestJSONObject.toString(),
			requestJSONObject.has("r_buildToCaseResult_c_buildId"));
		Assert.assertTrue(
			requestJSONObject.toString(),
			requestJSONObject.has("r_caseToCaseResult_c_caseId"));

		testEquals(_DURATION, requestJSONObject.getLong("duration"));
	}

	private TestrayAttachment _mockTestrayAttachment() {
		TestrayAttachment testrayAttachment = Mockito.mock(
			TestrayAttachment.class);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("name", RandomTestUtil.randomString());

		Mockito.doReturn(
			jsonObject
		).when(
			testrayAttachment
		).getJSONObject();

		return testrayAttachment;
	}

	private TestrayBuild _mockTestrayBuild() {
		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		try {
			Mockito.doReturn(
				new URL(_TESTRAY_URL)
			).when(
				testrayBuild
			).getURL();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return testrayBuild;
	}

	private TestrayCaseResult _mockTestrayCaseResult(
		TestrayServer testrayServer) {

		TestrayCaseResult testrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);

		Mockito.doCallRealMethod(
		).when(
			testrayCaseResult
		).cacheTestrayCaseResultURL();

		Mockito.doCallRealMethod(
		).when(
			testrayCaseResult
		).getTestrayCaseResultURL();

		Mockito.doReturn(
			_mockTestrayBuild()
		).when(
			testrayCaseResult
		).getTestrayBuild();

		Mockito.doReturn(
			Mockito.mock(TestrayCase.class)
		).when(
			testrayCaseResult
		).getTestrayCase();

		ReflectionTestUtil.setFieldValue(
			testrayCaseResult, "_testrayServer", testrayServer);

		return testrayCaseResult;
	}

	private TestrayServer _mockTestrayServer() {
		return Mockito.mock(TestrayServer.class);
	}

	private void _setRequestGraphQLResult(
			TestrayServer testrayServer, Set<JSONObject> entityJSONObjects)
		throws Exception {

		Mockito.doReturn(
			entityJSONObjects
		).when(
			testrayServer
		).requestGraphQL(
			Mockito.anyString(), Mockito.any(), Mockito.anyString(),
			Mockito.any(), Mockito.anyLong(), Mockito.anyInt()
		);
	}

	private void _setRequestPostResult(
			TestrayServer testrayServer, long testrayCaseResultId)
		throws Exception {

		JSONObject responseJSONObject = new JSONObject();

		responseJSONObject.put("id", testrayCaseResultId);

		Mockito.doReturn(
			responseJSONObject.toString()
		).when(
			testrayServer
		).requestPost(
			Mockito.anyString(), Mockito.anyString()
		);
	}

	private void _testCacheTestrayCaseResultURL(boolean requestGraphQLFails)
		throws Exception {

		TestrayServer testrayServer = _mockTestrayServer();

		AtomicInteger requestsCount = new AtomicInteger();

		if (requestGraphQLFails) {
			Mockito.doAnswer(
				invocation -> {
					requestsCount.incrementAndGet();

					throw new IOException();
				}
			).when(
				testrayServer
			).requestGraphQL(
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.any(), Mockito.anyLong(), Mockito.anyInt()
			);
		}
		else {
			_setRequestGraphQLResult(testrayServer, Collections.emptySet());

			Mockito.doAnswer(
				invocation -> {
					requestsCount.incrementAndGet();

					throw new IOException();
				}
			).when(
				testrayServer
			).requestPost(
				Mockito.anyString(), Mockito.anyString()
			);
		}

		TestrayCaseResult testrayCaseResult = _mockTestrayCaseResult(
			testrayServer);

		String name = RandomTestUtil.randomString();

		Mockito.doReturn(
			name
		).when(
			testrayCaseResult
		).getName();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();
		PrintStream printStream = System.out;

		System.setOut(new PrintStream(byteArrayOutputStream, true));

		try (MockedStatic<JenkinsResultsParserUtil> mockedStatic =
				Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			mockedStatic.when(
				() -> JenkinsResultsParserUtil.sleep(Mockito.anyLong())
			).thenAnswer(
				invocation -> null
			);

			testrayCaseResult.cacheTestrayCaseResultURL();
		}
		finally {
			System.setOut(printStream);
		}

		Assert.assertTrue(
			byteArrayOutputStream.toString(),
			byteArrayOutputStream.toString(
			).contains(
				"Unable to create Testray case result '" + name + "'"
			));

		int attemptsCount = requestsCount.get();

		Assert.assertTrue(attemptsCount > 0);

		Assert.assertNull(testrayCaseResult.getTestrayCaseResultURL());
		Assert.assertNull(testrayCaseResult.getTestrayCaseResultURL());

		testEquals(attemptsCount, requestsCount.get());
	}

	private void _testGetTestrayCaseResultURL(
			Long createdTestrayCaseResultId, Long fetchedTestrayCaseResultId)
		throws Exception {

		TestrayServer testrayServer = _mockTestrayServer();

		if (fetchedTestrayCaseResultId == null) {
			_setRequestGraphQLResult(testrayServer, Collections.emptySet());
			_setRequestPostResult(testrayServer, createdTestrayCaseResultId);
		}
		else {
			JSONObject entityJSONObject = new JSONObject();

			entityJSONObject.put("id", fetchedTestrayCaseResultId);

			_setRequestGraphQLResult(
				testrayServer, Collections.singleton(entityJSONObject));
		}

		TestrayCaseResult testrayCaseResult = _mockTestrayCaseResult(
			testrayServer);

		Long testrayCaseResultId = createdTestrayCaseResultId;

		if (testrayCaseResultId == null) {
			testrayCaseResultId = fetchedTestrayCaseResultId;
		}

		testEquals(
			new URL(_TESTRAY_URL + "/case-result/" + testrayCaseResultId),
			testrayCaseResult.getTestrayCaseResultURL());

		if (fetchedTestrayCaseResultId != null) {
			Mockito.verify(
				testrayServer, Mockito.never()
			).requestPost(
				Mockito.anyString(), Mockito.anyString()
			);
		}
	}

	private void _testGetTestrayCaseResultURLNull(
			TestrayBuild testrayBuild, TestrayCase testrayCase)
		throws Exception {

		TestrayServer testrayServer = _mockTestrayServer();

		TestrayCaseResult testrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);

		Mockito.doCallRealMethod(
		).when(
			testrayCaseResult
		).getTestrayCaseResultURL();

		Mockito.doReturn(
			testrayBuild
		).when(
			testrayCaseResult
		).getTestrayBuild();

		Mockito.doReturn(
			testrayCase
		).when(
			testrayCaseResult
		).getTestrayCase();

		ReflectionTestUtil.setFieldValue(
			testrayCaseResult, "_testrayServer", testrayServer);

		Assert.assertNull(testrayCaseResult.getTestrayCaseResultURL());

		Mockito.verifyNoInteractions(testrayServer);
	}

	private static final long _DURATION = 1000;

	private static final String _TESTRAY_URL =
		"https://testray.liferay.com/home";

}