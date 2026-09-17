/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayCloudBucket;

import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class BaseTopLevelBuildReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_clearCache(BuildReportFactory.class, "_topLevelBuildReports");

		ReflectionTestUtil.setFieldValue(
			TestrayCloudBucket.class, "_hasGoogleApplicationCredentials", null);
	}

	@Test
	public void testAddDownstreamBuildReport() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReport(null);

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 0);

		DownstreamBuildReport cachedDownstreamBuildReport =
			_newDownstreamBuildReport(RandomTestUtil.randomString(), true);

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);
		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);

		DownstreamBuildReport downstreamBuildReport = _newDownstreamBuildReport(
			RandomTestUtil.randomString(), false);

		baseTopLevelBuildReport.addDownstreamBuildReport(downstreamBuildReport);
		baseTopLevelBuildReport.addDownstreamBuildReport(downstreamBuildReport);

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 2);
	}

	@Test
	public void testAddDownstreamBuildReports() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReports(null);

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 0);

		DownstreamBuildReport cachedDownstreamBuildReport =
			_newDownstreamBuildReport(RandomTestUtil.randomString(), true);
		DownstreamBuildReport downstreamBuildReport = _newDownstreamBuildReport(
			RandomTestUtil.randomString(), false);

		baseTopLevelBuildReport.addDownstreamBuildReports(
			Arrays.asList(cachedDownstreamBuildReport, downstreamBuildReport));

		List<DownstreamBuildReport> downstreamBuildReports =
			_assertDownstreamBuildReports(baseTopLevelBuildReport, 2);

		Assert.assertTrue(
			downstreamBuildReports.contains(cachedDownstreamBuildReport));
		Assert.assertTrue(
			downstreamBuildReports.contains(downstreamBuildReport));
	}

	@Test
	public void testAddTestrayAttachmentURL() throws Exception {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(null);

		baseTopLevelBuildReport.addTestrayAttachmentURL(
			_newTestrayAttachmentURL());

		_assertTestrayAttachmentURLs(baseTopLevelBuildReport, 0);

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addTestrayAttachmentURL(
			_newTestrayAttachmentURL());

		_assertTestrayAttachmentURLs(baseTopLevelBuildReport, 1);

		URL testrayAttachmentURL = _newTestrayAttachmentURL();

		baseTopLevelBuildReport.addTestrayAttachmentURL(testrayAttachmentURL);

		List<URL> testrayAttachmentURLs = _assertTestrayAttachmentURLs(
			baseTopLevelBuildReport, 2);

		Assert.assertEquals(
			String.valueOf(testrayAttachmentURL),
			String.valueOf(testrayAttachmentURLs.get(1)));
	}

	@Test
	public void testBaseTopLevelBuildReport() {
		try {
			_newBaseTopLevelBuildReport(new JSONObject(), "not-a-build-url");

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(message.startsWith("Invalid Build URL"));
		}
	}

	@Test
	public void testGetBuildProfile() {
		_testGetBuildProfile("portal", Job.BuildProfile.PORTAL);
		_testGetBuildProfile(
			RandomTestUtil.randomString(), Job.BuildProfile.DXP);
		_testGetBuildProfile(null, Job.BuildProfile.DXP);

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(null);

		Assert.assertEquals(
			Job.BuildProfile.DXP, baseTopLevelBuildReport.getBuildProfile());
	}

	@Test
	public void testGetBuildReportTestrayCloudObject() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		Assert.assertNull(
			baseTopLevelBuildReport.getBuildReportTestrayCloudObject());
	}

	@Test
	public void testGetControllerBuildReport() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(
				new JSONObject(
				).put(
					"controller", _newControllerJSONObject()
				));

		ControllerBuildReport controllerBuildReport =
			baseTopLevelBuildReport.getControllerBuildReport();

		Assert.assertNotNull(controllerBuildReport);
		Assert.assertSame(
			controllerBuildReport,
			baseTopLevelBuildReport.getControllerBuildReport());

		_testGetControllerBuildReportNull(new JSONObject());
		_testGetControllerBuildReportNull(
			new JSONObject(
			).put(
				"controller", new JSONObject()
			));
		_testGetControllerBuildReportNull(null);
	}

	@Test
	public void testGetDistinctFailureReports() {
		FailureReport failureReport1 = Mockito.mock(FailureReport.class);
		FailureReport failureReport2 = Mockito.mock(FailureReport.class);

		Mockito.doReturn(
			true
		).when(
			failureReport2
		).isSimilar(
			failureReport1
		);

		BaseTopLevelBuildReport baseTopLevelBuildReport = Mockito.mock(
			BaseTopLevelBuildReport.class);

		Mockito.doCallRealMethod(
		).when(
			baseTopLevelBuildReport
		).getDistinctFailureReports();

		FailureReport failureReport3 = Mockito.mock(FailureReport.class);

		Mockito.doReturn(
			Arrays.asList(failureReport1, failureReport2, failureReport3)
		).when(
			baseTopLevelBuildReport
		).getFailureReports();

		List<FailureReport> distinctFailureReports =
			baseTopLevelBuildReport.getDistinctFailureReports();

		Assert.assertTrue(distinctFailureReports.contains(failureReport1));
		Assert.assertTrue(distinctFailureReports.contains(failureReport3));
		Assert.assertSame(
			distinctFailureReports,
			baseTopLevelBuildReport.getDistinctFailureReports());
		Assert.assertEquals(
			distinctFailureReports.toString(), 2,
			distinctFailureReports.size());
	}

	@Test
	public void testGetDownstreamBuildReport() {
		String axisName = RandomTestUtil.randomString();

		DownstreamBuildReport cachedDownstreamBuildReport =
			_newDownstreamBuildReport(axisName, true);
		DownstreamBuildReport downstreamBuildReport = _newDownstreamBuildReport(
			axisName, false);

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);
		baseTopLevelBuildReport.addDownstreamBuildReport(downstreamBuildReport);

		Assert.assertSame(
			downstreamBuildReport,
			baseTopLevelBuildReport.getDownstreamBuildReport(axisName));

		Assert.assertNull(
			baseTopLevelBuildReport.getDownstreamBuildReport(
				RandomTestUtil.randomString()));

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 2);

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);

		Assert.assertSame(
			cachedDownstreamBuildReport,
			baseTopLevelBuildReport.getDownstreamBuildReport(axisName));
	}

	@Test
	public void testGetFailureReports() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(
				_newFailureReportsJSONObject("FAILURE"));

		FailureReport cachedFailureReport = Mockito.mock(FailureReport.class);

		DownstreamBuildReport cachedDownstreamBuildReport =
			_newDownstreamBuildReport(RandomTestUtil.randomString(), true);

		Mockito.doReturn(
			Collections.singletonList(cachedFailureReport)
		).when(
			cachedDownstreamBuildReport
		).getFailureReports();

		FailureReport downstreamFailureReport = Mockito.mock(
			FailureReport.class);

		DownstreamBuildReport downstreamBuildReport = _newDownstreamBuildReport(
			RandomTestUtil.randomString(), false);

		Mockito.doReturn(
			Collections.singletonList(downstreamFailureReport)
		).when(
			downstreamBuildReport
		).getFailureReports();

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);
		baseTopLevelBuildReport.addDownstreamBuildReport(downstreamBuildReport);

		List<FailureReport> failureReports =
			baseTopLevelBuildReport.getFailureReports();

		Assert.assertTrue(failureReports.contains(cachedFailureReport));
		Assert.assertTrue(failureReports.contains(downstreamFailureReport));
		Assert.assertEquals(
			failureReports.toString(), 3, failureReports.size());

		Assert.assertSame(
			failureReports, baseTopLevelBuildReport.getFailureReports());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(
			_newFailureReportsJSONObject("SUCCESS"));

		failureReports = baseTopLevelBuildReport.getFailureReports();

		Assert.assertEquals(
			failureReports.toString(), 0, failureReports.size());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(null);

		failureReports = baseTopLevelBuildReport.getFailureReports();

		Assert.assertEquals(
			failureReports.toString(), 0, failureReports.size());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(
			new JSONObject(
			).put(
				"result", "FAILURE"
			));

		failureReports = baseTopLevelBuildReport.getFailureReports();

		Assert.assertEquals(
			failureReports.toString(), 0, failureReports.size());
	}

	@Test
	public void testGetJobReport() throws Exception {
		BaseTopLevelBuildReport baseTopLevelBuildReport1 = Mockito.mock(
			BaseTopLevelBuildReport.class);

		Mockito.doCallRealMethod(
		).when(
			baseTopLevelBuildReport1
		).getJobReport();

		try {
			baseTopLevelBuildReport1.getJobReport();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(message.startsWith("Invalid Build URL"));
		}

		Mockito.doReturn(
			new URL("https://test-1-0.liferay.com/job/test-job/123")
		).when(
			baseTopLevelBuildReport1
		).getBuildURL();

		JobReport jobReport = baseTopLevelBuildReport1.getJobReport();

		Assert.assertEquals(
			"https://test-1-0.liferay.com/job/test-job",
			String.valueOf(jobReport.getJobURL()));

		BaseTopLevelBuildReport baseTopLevelBuildReport2 = Mockito.mock(
			BaseTopLevelBuildReport.class);

		Mockito.doReturn(
			new URL("https://test-1-0.liferay.com/job/test-job/456")
		).when(
			baseTopLevelBuildReport2
		).getBuildURL();

		Mockito.doCallRealMethod(
		).when(
			baseTopLevelBuildReport2
		).getJobReport();

		Assert.assertSame(jobReport, baseTopLevelBuildReport2.getJobReport());

		_clearCache(JobReport.class, "_jobReports");

		Assert.assertSame(jobReport, baseTopLevelBuildReport1.getJobReport());
	}

	@Test
	public void testGetPreviousTopLevelBuildReport() throws Exception {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		Assert.assertNull(
			baseTopLevelBuildReport.getPreviousTopLevelBuildReport());

		baseTopLevelBuildReport.setControllerBuildReport(
			_newControllerBuildReport(1));

		Assert.assertNull(
			baseTopLevelBuildReport.getPreviousTopLevelBuildReport());

		baseTopLevelBuildReport = _testGetPreviousTopLevelBuildReport(
			new JSONArray(
			).put(
				_newControllerBuildJSONObject(4, "SUCCESS")
			).put(
				_newControllerBuildJSONObject(3, "SUCCESS")
			).put(
				_newControllerBuildJSONObject(2, "ABORTED")
			).put(
				_newControllerBuildJSONObject(1, "SUCCESS")
			),
			3, "https://test-1-1.liferay.com/job/previous-job/1");

		TopLevelBuildReport previousTopLevelBuildReport =
			baseTopLevelBuildReport.getPreviousTopLevelBuildReport();

		_clearCache(BuildReportFactory.class, "_topLevelBuildReports");

		Assert.assertSame(
			previousTopLevelBuildReport,
			baseTopLevelBuildReport.getPreviousTopLevelBuildReport());

		_testGetPreviousTopLevelBuildReport(
			new JSONArray(
			).put(
				_newControllerBuildJSONObject(3, "SUCCESS")
			).put(
				_newControllerBuildJSONObject(2, "FAILURE")
			).put(
				_newControllerBuildJSONObject(1, "SUCCESS")
			),
			3, "https://test-1-1.liferay.com/job/previous-job/2");
		_testGetPreviousTopLevelBuildReport(
			new JSONArray(
			).put(
				_newControllerBuildJSONObject(3, "SUCCESS")
			).put(
				_newControllerBuildJSONObject(2, "SUCCESS")
			).put(
				_newControllerBuildJSONObject(1, "SUCCESS")
			),
			3, "https://test-1-1.liferay.com/job/previous-job/2");
		_testGetPreviousTopLevelBuildReport(
			new JSONArray(
			).put(
				_newControllerBuildJSONObject(3, "SUCCESS")
			).put(
				_newControllerBuildJSONObject(2, "UNSTABLE")
			).put(
				_newControllerBuildJSONObject(1, "SUCCESS")
			),
			3, "https://test-1-1.liferay.com/job/previous-job/2");
		_testGetPreviousTopLevelBuildReport(
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"number", 3
				)
			).put(
				new JSONObject(
				).put(
					"description", RandomTestUtil.randomString()
				).put(
					"number", 2
				)
			),
			3, null);
		_testGetPreviousTopLevelBuildReport(null, 3, null);
	}

	@Test
	public void testGetTestResultsJSONUserContentURL() {
		_testGetTestResultsJSONUserContentURL(
			"https://test-1-0.liferay.com/userContent/testResults/test-job" +
				"/builds/123/test.results.json",
			"");
		_testGetTestResultsJSONUserContentURL(
			"https://test-1-0.liferay.com/userContent/testResults/test-job" +
				"/builds/123/test.results.json",
			null);
		_testGetTestResultsJSONUserContentURL(
			"https://test-9-9.liferay.com/userContent/testResults/test-job" +
				"/builds/123/test.results.json",
			"https://test-9-9.liferay.com");
		_testGetTestResultsJSONUserContentURL(
			"https://test-9-9.liferay.com/userContent/testResults/test-job" +
				"/builds/123/test.results.json",
			"https://test-9-9.liferay.com/");
	}

	@Test
	public void testGetTestSuiteName() {
		String testSuiteName = RandomTestUtil.randomString();

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(
				new JSONObject(
				).put(
					"testSuiteName", testSuiteName
				));

		Assert.assertEquals(
			testSuiteName, baseTopLevelBuildReport.getTestSuiteName());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport();

		Assert.assertEquals("", baseTopLevelBuildReport.getTestSuiteName());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(null);

		Assert.assertNull(baseTopLevelBuildReport.getTestSuiteName());
	}

	@Test
	public void testGetTopLevelActiveDuration() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(
				new JSONObject(
				).put(
					"duration", 5000L
				));

		Assert.assertEquals(
			0L, baseTopLevelBuildReport.getTopLevelActiveDuration());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(
			_newStopWatchBuildReportJSONObject(
				5000L,
				_newStopWatchRecordJSONObject(2000L, "wait.for.invoked.jobs")));

		Assert.assertEquals(
			3000L, baseTopLevelBuildReport.getTopLevelActiveDuration());
	}

	@Test
	public void testGetTopLevelPassiveDuration() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		Assert.assertEquals(
			0L, baseTopLevelBuildReport.getTopLevelPassiveDuration());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(
			_newStopWatchBuildReportJSONObject(
				9000L,
				_newStopWatchRecordJSONObject(
					4000L, "invoke.downstream.builds")));

		Assert.assertEquals(
			4000L, baseTopLevelBuildReport.getTopLevelPassiveDuration());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(
			_newStopWatchBuildReportJSONObject(
				9000L,
				_newStopWatchRecordJSONObject(2000L, "wait.for.invoked.jobs"),
				_newStopWatchRecordJSONObject(
					3000L, "wait.for.invoked.smoke.jobs"),
				_newStopWatchRecordJSONObject(
					4000L, "invoke.downstream.builds")));

		Assert.assertEquals(
			5000L, baseTopLevelBuildReport.getTopLevelPassiveDuration());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(null);

		Assert.assertEquals(
			0L, baseTopLevelBuildReport.getTopLevelPassiveDuration());
	}

	@Test
	public void testGetTotalActualDuration() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(_newDurationsJSONObject());

		Assert.assertEquals(
			1000L, baseTopLevelBuildReport.getTotalActualDuration());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(null);

		Assert.assertEquals(
			0L, baseTopLevelBuildReport.getTotalActualDuration());
	}

	@Test
	public void testGetTotalCachedDuration() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(_newDurationsJSONObject());

		Assert.assertEquals(
			2000L, baseTopLevelBuildReport.getTotalCachedDuration());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(null);

		Assert.assertEquals(
			0L, baseTopLevelBuildReport.getTotalCachedDuration());
	}

	@Test
	public void testGetTotalDuration() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(_newDurationsJSONObject());

		Assert.assertEquals(3000L, baseTopLevelBuildReport.getTotalDuration());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport(null);

		Assert.assertEquals(0L, baseTopLevelBuildReport.getTotalDuration());
	}

	@Test
	public void testGetUniqueFailureReports() {
		FailureReport failureReport1 = Mockito.mock(FailureReport.class);
		FailureReport failureReport2 = Mockito.mock(FailureReport.class);

		Mockito.doReturn(
			true
		).when(
			failureReport1
		).isSimilar(
			failureReport2
		);

		_testGetUniqueFailureReports(
			Collections.singletonList(failureReport1), failureReport1, null);

		TopLevelBuildReport previousTopLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.doReturn(
			Collections.singletonList(failureReport2)
		).when(
			previousTopLevelBuildReport
		).getDistinctFailureReports();

		FailureReport failureReport3 = Mockito.mock(FailureReport.class);

		_testGetUniqueFailureReports(
			Arrays.asList(failureReport1, failureReport3), failureReport3,
			previousTopLevelBuildReport);
	}

	@Test
	public void testInitialize() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.initialize(
			new JSONObject(
			).put(
				"batches",
				new JSONArray(
				).put(
					new JSONObject(
					).put(
						"batchName", RandomTestUtil.randomString()
					).put(
						"builds",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"buildURL",
								"https://test-1-1/job/test-job" +
									"/AXIS_VARIABLE=0/1"
							)
						).put(
							new JSONObject()
						)
					)
				).put(
					new JSONObject(
					).put(
						"batchName", RandomTestUtil.randomString()
					)
				).put(
					new JSONObject(
					).put(
						"builds",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"buildURL",
								"https://test-1-1/job/test-job" +
									"/AXIS_VARIABLE=1/2"
							)
						)
					)
				)
			).put(
				"controller", _newControllerJSONObject()
			));

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 1);

		Assert.assertNotNull(
			baseTopLevelBuildReport.getControllerBuildReport());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.initialize(new JSONObject());

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 0);
	}

	private List<DownstreamBuildReport> _assertDownstreamBuildReports(
		BaseTopLevelBuildReport baseTopLevelBuildReport, int expectedCount) {

		List<DownstreamBuildReport> downstreamBuildReports =
			baseTopLevelBuildReport.getDownstreamBuildReports();

		Assert.assertEquals(
			downstreamBuildReports.toString(), expectedCount,
			downstreamBuildReports.size());

		return downstreamBuildReports;
	}

	private List<URL> _assertTestrayAttachmentURLs(
		BaseTopLevelBuildReport baseTopLevelBuildReport, int expectedCount) {

		List<URL> testrayAttachmentURLs =
			baseTopLevelBuildReport.getTestrayAttachmentURLs();

		Assert.assertEquals(
			testrayAttachmentURLs.toString(), expectedCount,
			testrayAttachmentURLs.size());

		return testrayAttachmentURLs;
	}

	private void _clearCache(Class<?> clazz, String fieldName) {
		Map<String, ?> cache = ReflectionTestUtil.getFieldValue(
			clazz, fieldName);

		cache.clear();
	}

	private BaseTopLevelBuildReport _newBaseTopLevelBuildReport() {
		return _newBaseTopLevelBuildReport(new JSONObject());
	}

	private BaseTopLevelBuildReport _newBaseTopLevelBuildReport(
		JSONObject buildReportJSONObject) {

		return _newBaseTopLevelBuildReport(
			buildReportJSONObject, "https://test-1-1/job/test-job/123");
	}

	private BaseTopLevelBuildReport _newBaseTopLevelBuildReport(
		JSONObject buildReportJSONObject, String buildURLString) {

		return new BaseTopLevelBuildReport(buildURLString) {

			@Override
			public JSONObject getBuildReportJSONObject() {
				return buildReportJSONObject;
			}

		};
	}

	private JSONObject _newControllerBuildJSONObject(
		int buildNumber, String status) {

		return new JSONObject(
		).put(
			"description",
			JenkinsResultsParserUtil.combine(
				"<strong>", status, "</strong> - <a href=\"https://test-1-1",
				"/job/previous-job/", String.valueOf(buildNumber),
				"/\">Build URL</a>")
		).put(
			"number", buildNumber
		);
	}

	private ControllerBuildReport _newControllerBuildReport(int buildNumber) {
		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);

		Mockito.doReturn(
			"https://test-1-1.liferay.com/"
		).when(
			jenkinsMaster
		).getRemoteURL();

		ControllerBuildReport controllerBuildReport = Mockito.mock(
			ControllerBuildReport.class);

		Mockito.doReturn(
			buildNumber
		).when(
			controllerBuildReport
		).getBuildNumber();

		Mockito.doReturn(
			jenkinsMaster
		).when(
			controllerBuildReport
		).getJenkinsMaster();

		Mockito.doReturn(
			"controller-job"
		).when(
			controllerBuildReport
		).getJobName();

		return controllerBuildReport;
	}

	private JSONObject _newControllerJSONObject() {
		return new JSONObject(
		).put(
			"buildURL", "https://test-1-1/job/controller-job/7"
		);
	}

	private DownstreamBuildReport _newDownstreamBuildReport(
		String axisName, boolean buildCached) {

		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		Mockito.doReturn(
			axisName
		).when(
			downstreamBuildReport
		).getAxisName();

		Mockito.doReturn(
			buildCached
		).when(
			downstreamBuildReport
		).isBuildCached();

		return downstreamBuildReport;
	}

	private JSONObject _newDurationsJSONObject() {
		return new JSONObject(
		).put(
			"totalActualDuration", 1000L
		).put(
			"totalCachedDuration", 2000L
		).put(
			"totalDuration", 3000L
		);
	}

	private JSONObject _newFailureReportsJSONObject(String result) {
		return new JSONObject(
		).put(
			"failureReports",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"message", RandomTestUtil.randomString()
				)
			)
		).put(
			"result", result
		);
	}

	private JSONObject _newStopWatchBuildReportJSONObject(
		long duration, JSONObject... stopWatchRecordJSONObjects) {

		return new JSONObject(
		).put(
			"duration", duration
		).put(
			"stopWatchRecords",
			new JSONArray(Arrays.asList(stopWatchRecordJSONObjects))
		);
	}

	private JSONObject _newStopWatchRecordJSONObject(
		long duration, String name) {

		return new JSONObject(
		).put(
			"duration", duration
		).put(
			"name", name
		);
	}

	private URL _newTestrayAttachmentURL() throws Exception {
		return new URL("https://test-1-1/" + RandomTestUtil.randomString());
	}

	private void _testGetBuildProfile(
		String buildProfileString, Job.BuildProfile expectedBuildProfile) {

		JSONObject buildReportJSONObject = new JSONObject();

		if (buildProfileString != null) {
			buildReportJSONObject.put(
				"buildParameters",
				new JSONObject(
				).put(
					"TEST_PORTAL_BUILD_PROFILE", buildProfileString
				));
		}

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(buildReportJSONObject);

		Assert.assertEquals(
			expectedBuildProfile, baseTopLevelBuildReport.getBuildProfile());
	}

	private void _testGetControllerBuildReportNull(
		JSONObject buildReportJSONObject) {

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(buildReportJSONObject);

		Assert.assertNull(baseTopLevelBuildReport.getControllerBuildReport());
	}

	private BaseTopLevelBuildReport _testGetPreviousTopLevelBuildReport(
			JSONArray buildsJSONArray, int currentBuildNumber,
			String expectedBuildURLString)
		throws Exception {

		UrlReader urlReader = mockUrlReader();

		JSONObject controllerJobJSONObject = new JSONObject();

		if (buildsJSONArray != null) {
			controllerJobJSONObject.put("builds", buildsJSONArray);
		}

		setUrlReaderOutput("{}", "previous-job/", urlReader);

		setUrlReaderOutput(
			String.valueOf(controllerJobJSONObject),
			"http://test-1-1/job/controller-job", urlReader);

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.setControllerBuildReport(
			_newControllerBuildReport(currentBuildNumber));

		TopLevelBuildReport previousTopLevelBuildReport =
			baseTopLevelBuildReport.getPreviousTopLevelBuildReport();

		if (expectedBuildURLString == null) {
			Assert.assertNull(previousTopLevelBuildReport);
		}
		else {
			Assert.assertEquals(
				expectedBuildURLString,
				String.valueOf(previousTopLevelBuildReport.getBuildURL()));
		}

		return baseTopLevelBuildReport;
	}

	private void _testGetTestResultsJSONUserContentURL(
		String expectedURLString, String jenkinsRemoteURL) {

		if (jenkinsRemoteURL == null) {
			JenkinsResultsParserUtil.setBuildProperties(new String[0]);
		}
		else {
			Properties properties = new Properties();

			properties.setProperty(
				"jenkins.remote.url[test-1-0]", jenkinsRemoteURL);

			JenkinsResultsParserUtil.setBuildProperties(properties);
		}

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		Assert.assertEquals(
			expectedURLString,
			String.valueOf(
				baseTopLevelBuildReport.getTestResultsJSONUserContentURL()));
	}

	private void _testGetUniqueFailureReports(
		List<FailureReport> distinctFailureReports,
		FailureReport expectedFailureReport,
		TopLevelBuildReport previousTopLevelBuildReport) {

		BaseTopLevelBuildReport baseTopLevelBuildReport = Mockito.mock(
			BaseTopLevelBuildReport.class);

		Mockito.doReturn(
			distinctFailureReports
		).when(
			baseTopLevelBuildReport
		).getDistinctFailureReports();

		Mockito.doReturn(
			previousTopLevelBuildReport
		).when(
			baseTopLevelBuildReport
		).getPreviousTopLevelBuildReport();

		Mockito.doCallRealMethod(
		).when(
			baseTopLevelBuildReport
		).getUniqueFailureReports();

		List<FailureReport> uniqueFailureReports =
			baseTopLevelBuildReport.getUniqueFailureReports();

		Assert.assertNotSame(distinctFailureReports, uniqueFailureReports);
		Assert.assertTrue(uniqueFailureReports.contains(expectedFailureReport));
		Assert.assertEquals(
			uniqueFailureReports.toString(), 1, uniqueFailureReports.size());

		Assert.assertSame(
			uniqueFailureReports,
			baseTopLevelBuildReport.getUniqueFailureReports());
	}

}