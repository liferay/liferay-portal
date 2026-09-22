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
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
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
	public void testGetErrorsList() {
		List<String> errorsList = TestrayCaseResult.getErrorsList(
			"first\nsecond\nthird\nlast");

		Assert.assertEquals("first\nsecond\nthird\nlast", errorsList.get(0));
		Assert.assertEquals("first\n...\nlast", errorsList.get(1));
		Assert.assertEquals(errorsList.toString(), 3, errorsList.size());
	}

	@Test
	public void testGetErrorsListEmptyErrors() {
		for (String errors : new String[] {null, ""}) {
			List<String> errorsList = TestrayCaseResult.getErrorsList(errors);

			Assert.assertEquals(errors, errorsList.get(0));
			Assert.assertEquals(errorsList.toString(), 1, errorsList.size());
		}
	}

	@Test
	public void testGetErrorsListEndsWithRejectedMessage() {
		for (String errors :
				new String[] {
					"only line", "first\nlast", "first\nsecond\nlast",
					_SEM_VER_ERRORS
				}) {

			List<String> errorsList = TestrayCaseResult.getErrorsList(errors);

			String lastErrors = errorsList.get(errorsList.size() - 1);

			Assert.assertNotEquals(errors, lastErrors);

			Assert.assertTrue(
				lastErrors, lastErrors.contains("web application firewall"));
		}
	}

	@Test
	public void testGetErrorsListSkipsSummaryForShortErrors() {
		List<String> errorsList = TestrayCaseResult.getErrorsList(
			"first\nlast");

		Assert.assertEquals("first\nlast", errorsList.get(0));
		Assert.assertEquals(errorsList.toString(), 2, errorsList.size());
	}

	@Test
	public void testGetErrorsListSummarizesSemanticVersioning() {
		List<String> errorsList = TestrayCaseResult.getErrorsList(
			_SEM_VER_ERRORS);

		String summarizedErrors = errorsList.get(1);

		Assert.assertTrue(
			summarizedErrors,
			summarizedErrors.endsWith("Semantic versioning is incorrect"));
		Assert.assertTrue(
			summarizedErrors,
			summarizedErrors.startsWith("     [exec]   PACKAGE_NAME"));
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

		String errors = RandomTestUtil.randomString();

		Mockito.doReturn(
			errors
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
		Assert.assertTrue(
			requestJSONObject.toString(),
			requestJSONObject.has("r_buildToCaseResult_c_buildId"));
		Assert.assertTrue(
			requestJSONObject.toString(),
			requestJSONObject.has("r_caseToCaseResult_c_caseId"));

		testEquals(_DURATION, requestJSONObject.getLong("duration"));
		testEquals(errors, requestJSONObject.getString("errors"));
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

		try {
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

	private static final String _SEM_VER_ERRORS =
		JenkinsResultsParserUtil.combine(
			"     [exec]   PACKAGE_NAME   DELTA   CUR_VER\n",
			"     [exec] * com.liferay.portal.kernel.util   MINOR   102.0.0\n",
			"     [exec] \t\t\t+   return     java.lang.String\n",
			"     [exec] Semantic versioning is incorrect");

	private static final String _TESTRAY_URL =
		"https://testray.liferay.com/home";

}