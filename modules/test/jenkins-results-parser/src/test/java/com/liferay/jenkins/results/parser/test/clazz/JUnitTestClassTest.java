/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import java.io.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class JUnitTestClassTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetCachedDownstreamBuildReport() {
		TestClassReport nestedTestClassReport = _getTestClassReport();
		TestClassReport testClassReport = _getTestClassReport();

		_testGetCachedDownstreamBuildReport(
			nestedTestClassReport.getDownstreamBuildReport(),
			Collections.singletonList(nestedTestClassReport), null);
		_testGetCachedDownstreamBuildReport(
			testClassReport.getDownstreamBuildReport(),
			Collections.singletonList(nestedTestClassReport), testClassReport);

		_testGetCachedDownstreamBuildReport(
			null, Collections.emptyList(), null);
	}

	@Test
	public void testGetCachedTestClassReports() {
		TestClassReport testClassReport = _getTestClassReport();
		TestClassReport nestedTestClassReport1 = _getTestClassReport();
		TestClassReport nestedTestClassReport2 = _getTestClassReport();

		_testGetCachedTestClassReports(
			Arrays.asList(nestedTestClassReport1, nestedTestClassReport2),
			Arrays.asList(nestedTestClassReport1, nestedTestClassReport2),
			null);
		_testGetCachedTestClassReports(
			Arrays.asList(
				testClassReport, nestedTestClassReport1,
				nestedTestClassReport2),
			Arrays.asList(nestedTestClassReport1, nestedTestClassReport2),
			testClassReport);

		_testGetCachedTestClassReports(
			Collections.singletonList(testClassReport), Collections.emptyList(),
			testClassReport);

		_testGetCachedTestClassReports(null, Collections.emptyList(), null);
	}

	@Test
	public void testGetCachedTestClassReportsBuildCachingDisabled() {
		String testClassName = RandomTestUtil.randomString();

		BatchTestClassGroup batchTestClassGroup = _getBatchTestClassGroup(
			Collections.emptyList(), testClassName, _getTestClassReport());

		JUnitTestClass jUnitTestClass = _getJUnitTestClass(
			batchTestClassGroup, testClassName);

		Mockito.doReturn(
			false
		).when(
			jUnitTestClass
		).isBuildCachingEnabled();

		Assert.assertNull(
			jUnitTestClass.getCachedDownstreamBuildReport(batchTestClassGroup));
		Assert.assertNull(
			jUnitTestClass.getCachedTestClassReports(batchTestClassGroup));

		Mockito.verifyNoInteractions(batchTestClassGroup);
	}

	@Test
	public void testGetCachedTestClassReportsDefaultBatch() {
		String testClassName = RandomTestUtil.randomString();
		TestClassReport testClassReport = _getTestClassReport();

		JUnitTestClass jUnitTestClass = _getJUnitTestClass(
			_getBatchTestClassGroup(
				Collections.emptyList(), testClassName, testClassReport),
			testClassName);

		testSame(
			testClassReport.getDownstreamBuildReport(),
			jUnitTestClass.getCachedDownstreamBuildReport());
		testEquals(
			Collections.singletonList(testClassReport),
			jUnitTestClass.getCachedTestClassReports());
	}

	@Test
	public void testGetCachedTestClassReportsHit() {
		String testClassName = RandomTestUtil.randomString();

		BatchTestClassGroup batchTestClassGroup = _getBatchTestClassGroup(
			Collections.emptyList(), testClassName, _getTestClassReport());

		JUnitTestClass jUnitTestClass = _getJUnitTestClass(
			batchTestClassGroup, testClassName);

		testSame(
			jUnitTestClass.getCachedTestClassReports(batchTestClassGroup),
			jUnitTestClass.getCachedTestClassReports(batchTestClassGroup));

		Mockito.verify(
			batchTestClassGroup
		).getCachedTestClassReport(
			testClassName
		);
	}

	@Test
	public void testGetCachedTestClassReportsMiss() {
		String testClassName = RandomTestUtil.randomString();

		BatchTestClassGroup batchTestClassGroup = _getBatchTestClassGroup(
			Collections.emptyList(), testClassName, null);

		JUnitTestClass jUnitTestClass = _getJUnitTestClass(
			batchTestClassGroup, testClassName);

		Assert.assertNull(
			jUnitTestClass.getCachedTestClassReports(batchTestClassGroup));

		TestClassReport testClassReport = _getTestClassReport();

		Mockito.doReturn(
			testClassReport
		).when(
			batchTestClassGroup
		).getCachedTestClassReport(
			testClassName
		);

		testEquals(
			Collections.singletonList(testClassReport),
			jUnitTestClass.getCachedTestClassReports(batchTestClassGroup));
	}

	@Test
	public void testGetCachedTestClassReportsSharedBatches() {
		TestClassReport dbPartitionTestClassReport = _getTestClassReport();
		String testClassName = RandomTestUtil.randomString();

		BatchTestClassGroup dbPartitionBatchTestClassGroup =
			_getBatchTestClassGroup(
				Collections.emptyList(), testClassName,
				dbPartitionTestClassReport);

		TestClassReport testClassReport = _getTestClassReport();

		BatchTestClassGroup batchTestClassGroup = _getBatchTestClassGroup(
			Collections.emptyList(), testClassName, testClassReport);

		JUnitTestClass jUnitTestClass = _getJUnitTestClass(
			dbPartitionBatchTestClassGroup, testClassName);

		testEquals(
			Collections.singletonList(dbPartitionTestClassReport),
			jUnitTestClass.getCachedTestClassReports(
				dbPartitionBatchTestClassGroup));
		testEquals(
			Collections.singletonList(testClassReport),
			jUnitTestClass.getCachedTestClassReports(batchTestClassGroup));
		testEquals(
			Collections.singletonList(testClassReport),
			jUnitTestClass.getCachedTestClassReports(batchTestClassGroup));
		testSame(
			dbPartitionTestClassReport.getDownstreamBuildReport(),
			jUnitTestClass.getCachedDownstreamBuildReport(
				dbPartitionBatchTestClassGroup));
		testSame(
			testClassReport.getDownstreamBuildReport(),
			jUnitTestClass.getCachedDownstreamBuildReport(batchTestClassGroup));
	}

	@Test
	public void testGetJSONObject() throws Exception {
		JUnitTestClass jUnitTestClass = Mockito.mock(JUnitTestClass.class);

		Mockito.doCallRealMethod(
		).when(
			jUnitTestClass
		).getJSONObject();

		Mockito.doReturn(
			new File(RandomTestUtil.randomString())
		).when(
			jUnitTestClass
		).getTestClassFile();

		File testPropertiesFile = temporaryFolder.newFile();

		ReflectionTestUtil.setFieldValue(
			jUnitTestClass, "_testPropertiesFile", testPropertiesFile);

		JSONObject jUnitTestClassJSONObject = jUnitTestClass.getJSONObject();

		testEquals(
			JenkinsResultsParserUtil.getCanonicalPath(testPropertiesFile),
			jUnitTestClassJSONObject.get("test_properties_file"));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private BatchTestClassGroup _getBatchTestClassGroup(
		List<TestClassReport> nestedTestClassReports, String testClassName,
		TestClassReport testClassReport) {

		BatchTestClassGroup batchTestClassGroup = Mockito.mock(
			BatchTestClassGroup.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			batchTestClassGroup
		).getBatchName();

		if (testClassReport != null) {
			Mockito.doReturn(
				testClassReport
			).when(
				batchTestClassGroup
			).getCachedTestClassReport(
				testClassName
			);
		}

		Mockito.doReturn(
			nestedTestClassReports
		).when(
			batchTestClassGroup
		).getCachedTestClassReportByPrefix(
			testClassName + "$"
		);

		return batchTestClassGroup;
	}

	private JUnitTestClass _getJUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, String testClassName) {

		JUnitTestClass jUnitTestClass = Mockito.mock(JUnitTestClass.class);

		Mockito.doReturn(
			batchTestClassGroup
		).when(
			jUnitTestClass
		).getBatchTestClassGroup();

		Mockito.doCallRealMethod(
		).when(
			jUnitTestClass
		).getCachedDownstreamBuildReport();

		Mockito.doCallRealMethod(
		).when(
			jUnitTestClass
		).getCachedDownstreamBuildReport(
			Mockito.any(BatchTestClassGroup.class)
		);

		Mockito.doCallRealMethod(
		).when(
			jUnitTestClass
		).getCachedTestClassReports();

		Mockito.doCallRealMethod(
		).when(
			jUnitTestClass
		).getCachedTestClassReports(
			Mockito.any(BatchTestClassGroup.class)
		);

		Mockito.doReturn(
			testClassName
		).when(
			jUnitTestClass
		).getTestClassName();

		Mockito.doReturn(
			true
		).when(
			jUnitTestClass
		).isBuildCachingEnabled();

		ReflectionTestUtil.setFieldValue(
			jUnitTestClass, "_cachedTestClassReportsMap",
			new ConcurrentHashMap<>());

		return jUnitTestClass;
	}

	private TestClassReport _getTestClassReport() {
		TestClassReport testClassReport = Mockito.mock(TestClassReport.class);

		Mockito.doReturn(
			Mockito.mock(DownstreamBuildReport.class)
		).when(
			testClassReport
		).getDownstreamBuildReport();

		return testClassReport;
	}

	private void _testGetCachedDownstreamBuildReport(
		DownstreamBuildReport expectedDownstreamBuildReport,
		List<TestClassReport> nestedTestClassReports,
		TestClassReport testClassReport) {

		String testClassName = RandomTestUtil.randomString();

		BatchTestClassGroup batchTestClassGroup = _getBatchTestClassGroup(
			nestedTestClassReports, testClassName, testClassReport);

		JUnitTestClass jUnitTestClass = _getJUnitTestClass(
			batchTestClassGroup, testClassName);

		testSame(
			expectedDownstreamBuildReport,
			jUnitTestClass.getCachedDownstreamBuildReport(batchTestClassGroup));
	}

	private void _testGetCachedTestClassReports(
		List<TestClassReport> expectedTestClassReports,
		List<TestClassReport> nestedTestClassReports,
		TestClassReport testClassReport) {

		String testClassName = RandomTestUtil.randomString();

		BatchTestClassGroup batchTestClassGroup = _getBatchTestClassGroup(
			nestedTestClassReports, testClassName, testClassReport);

		JUnitTestClass jUnitTestClass = _getJUnitTestClass(
			batchTestClassGroup, testClassName);

		testEquals(
			expectedTestClassReports,
			jUnitTestClass.getCachedTestClassReports(batchTestClassGroup));
	}

}