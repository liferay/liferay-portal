/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.job.property.GlobJobProperty;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;
import com.liferay.jenkins.results.parser.test.batch.TestBatch;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.FunctionalBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.FunctionalSegmentTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.SegmentTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.TestClassGroupFactory;

import java.io.File;
import java.io.IOException;

import java.nio.file.PathMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseJob implements Job {

	public Set<String> getAnalyticsCloudBatchNames() {
		Set<String> batchNames = new TreeSet<>();

		for (BatchTestClassGroup batchTestClassGroup :
				getBatchTestClassGroups()) {

			if (batchTestClassGroup.isTestAnalyticsCloud()) {
				batchNames.add(batchTestClassGroup.getBatchName());
			}
		}

		return batchNames;
	}

	public Set<String> getAnalyticsCloudSegmentNames() {
		Set<String> segmentNames = new TreeSet<>();

		for (SegmentTestClassGroup segmentTestClassGroup :
				getSegmentTestClassGroups()) {

			if (segmentTestClassGroup.isTestAnalyticsCloud()) {
				segmentNames.add(segmentTestClassGroup.getSegmentName());
			}
		}

		return segmentNames;
	}

	@Override
	public int getAxisCount() {
		List<AxisTestClassGroup> axisTestClassGroups = getAxisTestClassGroups();

		if (axisTestClassGroups == null) {
			return 0;
		}

		return axisTestClassGroups.size();
	}

	@Override
	public AxisTestClassGroup getAxisTestClassGroup(String axisName) {
		List<AxisTestClassGroup> axisTestClassGroups = new ArrayList<>();

		axisTestClassGroups.addAll(getAxisTestClassGroups());
		axisTestClassGroups.addAll(getDependentAxisTestClassGroups());

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			if (!Objects.equals(axisName, axisTestClassGroup.getAxisName())) {
				continue;
			}

			return axisTestClassGroup;
		}

		return null;
	}

	@Override
	public List<AxisTestClassGroup> getAxisTestClassGroups() {
		List<AxisTestClassGroup> axisTestClassGroups = new ArrayList<>();

		for (BatchTestClassGroup batchTestClassGroup :
				getBatchTestClassGroups()) {

			axisTestClassGroups.addAll(
				batchTestClassGroup.getAxisTestClassGroups());
		}

		return axisTestClassGroups;
	}

	@Override
	public Set<String> getBatchNames() {
		if (_batchNames != null) {
			return _batchNames;
		}

		_batchNames = Collections.synchronizedSet(new TreeSet<String>());

		for (BatchTestClassGroup batchTestClassGroup :
				getBatchTestClassGroups()) {

			_batchNames.add(batchTestClassGroup.getBatchName());
		}

		return _batchNames;
	}

	@Override
	public List<BatchTestClassGroup> getBatchTestClassGroups() {
		synchronized (jobProperties) {
			if (batchTestClassGroups != null) {
				return batchTestClassGroups;
			}

			batchTestClassGroups = Collections.synchronizedList(
				new ArrayList<BatchTestClassGroup>());

			if ((jsonObject != null) && jsonObject.has("batches")) {
				JSONArray batchesJSONArray = jsonObject.getJSONArray("batches");

				for (int i = 0; i < batchesJSONArray.length(); i++) {
					JSONObject batchJSONObject = batchesJSONArray.getJSONObject(
						i);

					if (batchJSONObject == null) {
						continue;
					}

					batchTestClassGroups.add(
						TestClassGroupFactory.newBatchTestClassGroup(
							this, batchJSONObject));
				}

				return batchTestClassGroups;
			}

			batchTestClassGroups.addAll(
				getBatchTestClassGroups(getRawBatchNames()));

			return batchTestClassGroups;
		}
	}

	@Override
	public List<Build> getBuildHistory(JenkinsMaster jenkinsMaster) {
		JSONObject jobJSONObject = getJobJSONObject(
			jenkinsMaster, "builds[number]");

		JSONArray buildsJSONArray = jobJSONObject.getJSONArray("builds");

		List<Build> builds = new ArrayList<>(buildsJSONArray.length());

		for (int i = 0; i < buildsJSONArray.length(); i++) {
			JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

			builds.add(
				BuildFactory.newBuild(
					JenkinsResultsParserUtil.combine(
						jenkinsMaster.getURL(), "/job/", getJobName(), "/",
						String.valueOf(buildJSONObject.getInt("number"))),
					null));
		}

		return builds;
	}

	@Override
	public BuildProfile getBuildProfile() {
		return _buildProfile;
	}

	@Override
	public String getCompanyDefaultLocale() {
		if (_companyDefaultLocale != null) {
			return _companyDefaultLocale;
		}

		JobProperty jobProperty = getJobProperty(
			"test.batch.company.default.locale");

		String jobPropertyValue = jobProperty.getValue();

		if (jobPropertyValue != null) {
			recordJobProperty(jobProperty);

			_companyDefaultLocale = jobPropertyValue;

			return _companyDefaultLocale;
		}

		return null;
	}

	@Override
	public List<AxisTestClassGroup> getDependentAxisTestClassGroups() {
		List<AxisTestClassGroup> axisTestClassGroups = new ArrayList<>();

		for (BatchTestClassGroup batchTestClassGroup :
				getDependentBatchTestClassGroups()) {

			axisTestClassGroups.addAll(
				batchTestClassGroup.getAxisTestClassGroups());
		}

		return axisTestClassGroups;
	}

	@Override
	public Set<String> getDependentBatchNames() {
		Set<String> batchNames = new TreeSet<>();

		for (BatchTestClassGroup batchTestClassGroup :
				getDependentBatchTestClassGroups()) {

			batchNames.add(batchTestClassGroup.getBatchName());
		}

		return batchNames;
	}

	@Override
	public List<BatchTestClassGroup> getDependentBatchTestClassGroups() {
		synchronized (jobProperties) {
			if (_dependentBatchTestClassGroups != null) {
				return _dependentBatchTestClassGroups;
			}

			_dependentBatchTestClassGroups = Collections.synchronizedList(
				new ArrayList<BatchTestClassGroup>());

			if ((jsonObject != null) && jsonObject.has("smoke_batches")) {
				JSONArray smokeBatchesJSONArray = jsonObject.getJSONArray(
					"smoke_batches");

				for (int i = 0; i < smokeBatchesJSONArray.length(); i++) {
					JSONObject smokeBatchJSONObject =
						smokeBatchesJSONArray.getJSONObject(i);

					if (smokeBatchJSONObject == null) {
						continue;
					}

					_dependentBatchTestClassGroups.add(
						TestClassGroupFactory.newBatchTestClassGroup(
							this, smokeBatchJSONObject));
				}

				return _dependentBatchTestClassGroups;
			}

			_dependentBatchTestClassGroups.addAll(
				getBatchTestClassGroups(getRawDependentBatchNames()));

			return _dependentBatchTestClassGroups;
		}
	}

	@Override
	public Set<String> getDependentSegmentNames() {
		Set<String> segmentNames = new TreeSet<>();

		for (SegmentTestClassGroup segmentTestClassGroup :
				getDependentSegmentTestClassGroups()) {

			segmentNames.add(segmentTestClassGroup.getSegmentName());
		}

		return segmentNames;
	}

	@Override
	public List<SegmentTestClassGroup> getDependentSegmentTestClassGroups() {
		List<SegmentTestClassGroup> segmentTestClassGroups = new ArrayList<>();

		for (BatchTestClassGroup batchTestClassGroup :
				getDependentBatchTestClassGroups()) {

			segmentTestClassGroups.addAll(
				batchTestClassGroup.getSegmentTestClassGroups());
		}

		return segmentTestClassGroups;
	}

	@Override
	public List<String> getDistNodes() {
		List<String> distNodes = new ArrayList<>();

		for (String networkName : getNetworkNames()) {
			if (JenkinsResultsParserUtil.isNullOrEmpty(networkName)) {
				continue;
			}

			distNodes.addAll(getDistNodes(networkName));
		}

		return distNodes;
	}

	@Override
	public List<String> getDistNodes(String networkName) {
		synchronized (_distNodesMap) {
			List<String> distNodes = _distNodesMap.get(networkName);

			if (distNodes != null) {
				return distNodes;
			}

			distNodes = new ArrayList<>();

			try {
				List<JenkinsMaster> jenkinsMasters =
					JenkinsResultsParserUtil.getJenkinsMasters(
						JenkinsResultsParserUtil.getBuildProperties(),
						_getSlaveRAMMinimumDefault(),
						_getSlavesPerHostDefault(),
						JenkinsResultsParserUtil.getCohortName(), networkName);

				int axisCount = getAxisCount();
				int distNodeAxisCount = _getDistNodeAxisCount();

				int distNodeCount = axisCount / distNodeAxisCount;

				Set<String> networkNames = getNetworkNames();

				distNodeCount = distNodeCount / networkNames.size();

				if ((axisCount % distNodeAxisCount) > 0) {
					distNodeCount++;
				}

				distNodeCount = Math.min(distNodeCount, jenkinsMasters.size());

				distNodeCount = Math.max(
					distNodeCount, _getDistNodeCountMinimum());

				List<JenkinsSlave> jenkinsSlaves =
					JenkinsResultsParserUtil.getReachableJenkinsSlaves(
						jenkinsMasters, distNodeCount);

				for (JenkinsSlave jenkinsSlave : jenkinsSlaves) {
					distNodes.add(jenkinsSlave.getName());
				}

				return distNodes;
			}
			catch (IOException ioException) {
				return new ArrayList<>();
			}
		}
	}

	@Override
	public Set<String> getDistRequiredBatchNames() {
		if (!isStandaloneBatchEnabled()) {
			return getBatchNames();
		}

		Set<String> batchNames = new TreeSet<>();

		JobProperty jobProperty = getJobProperty("test.batch.names.standalone");

		Set<String> standaloneTestBatchNames = getSetFromString(
			jobProperty.getValue());

		for (BatchTestClassGroup batchTestClassGroup :
				getBatchTestClassGroups()) {

			String batchName = batchTestClassGroup.getBatchName();

			if (!standaloneTestBatchNames.contains(batchName)) {
				batchNames.add(batchName);
			}
		}

		return batchNames;
	}

	@Override
	public Set<String> getDistRequiredSegmentNames() {
		if (!isStandaloneBatchEnabled()) {
			return getSegmentNames();
		}

		Set<String> segmentNames = new TreeSet<>();

		JobProperty jobProperty = getJobProperty("test.batch.names.standalone");

		Set<String> standaloneTestBatchNames = getSetFromString(
			jobProperty.getValue());

		for (SegmentTestClassGroup segmentTestClassGroup :
				getSegmentTestClassGroups()) {

			if (standaloneTestBatchNames.contains(
					segmentTestClassGroup.getBatchName()) ||
				(segmentTestClassGroup.isTestAnalyticsCloud() &&
				 JenkinsResultsParserUtil.isCloudCINode())) {

				continue;
			}

			segmentNames.add(segmentTestClassGroup.getSegmentName());
		}

		return segmentNames;
	}

	@Override
	public DistType getDistType() {
		return DistType.CI;
	}

	@Override
	public Set<String> getDistTypes() {
		JobProperty jobProperty = getJobProperty("test.batch.dist.app.servers");

		return getSetFromString(jobProperty.getValue());
	}

	@Override
	public Set<String> getDistTypesExcludingTomcat() {
		Set<String> distTypesExcludingTomcat = new TreeSet<>(getDistTypes());

		distTypesExcludingTomcat.remove("tomcat");

		return distTypesExcludingTomcat;
	}

	@Override
	public Set<JenkinsCohort> getJenkinsCohorts() {
		return Collections.singleton(
			JenkinsResultsParserUtil.getJenkinsCohort());
	}

	@Override
	public JobHistory getJobHistory() {
		if (_jobHistory != null) {
			return _jobHistory;
		}

		_jobHistory = HistoryUtil.getJobHistory(this);

		return _jobHistory;
	}

	@Override
	public String getJobName() {
		return _jobName;
	}

	@Override
	public List<File> getJobPropertiesFiles() {
		return jobPropertiesFiles;
	}

	@Override
	public List<String> getJobPropertyOptions() {
		List<String> jobPropertyOptions = new ArrayList<>();

		jobPropertyOptions.add(String.valueOf(getBuildProfile()));

		String jobName = getJobName();

		jobPropertyOptions.add(jobName);

		if (jobName.contains("(")) {
			jobPropertyOptions.add(jobName.substring(0, jobName.indexOf("(")));
		}

		jobPropertyOptions.removeAll(Collections.singleton(null));

		return jobPropertyOptions;
	}

	@Override
	public String getJobURL(JenkinsMaster jenkinsMaster) {
		return JenkinsResultsParserUtil.combine(
			jenkinsMaster.getURL(), "/job/", _jobName);
	}

	@Override
	public JSONObject getJSONObject() {
		synchronized (jobProperties) {
			if (jsonObject != null) {
				return jsonObject;
			}

			jsonObject = new JSONObject();

			List<BatchTestClassGroup> batchTestClassGroups =
				getBatchTestClassGroups();

			if ((batchTestClassGroups != null) &&
				!batchTestClassGroups.isEmpty()) {

				JSONArray batchesJSONArray = new JSONArray();

				for (BatchTestClassGroup batchTestClassGroup :
						batchTestClassGroups) {

					batchesJSONArray.put(batchTestClassGroup.getJSONObject());
				}

				jsonObject.put("batches", batchesJSONArray);
			}

			jsonObject.put(
				"build_profile", String.valueOf(getBuildProfile())
			).put(
				"company_default_locale", getCompanyDefaultLocale()
			).put(
				"job_name", getJobName()
			).put(
				"job_properties", _getJobPropertiesMap()
			).put(
				"job_property_options", getJobPropertyOptions()
			);

			List<BatchTestClassGroup> dependentBatchTestClassGroups =
				getDependentBatchTestClassGroups();

			if ((dependentBatchTestClassGroups != null) &&
				!dependentBatchTestClassGroups.isEmpty()) {

				JSONArray smokeBatchesJSONArray = new JSONArray();

				for (BatchTestClassGroup batchTestClassGroup :
						dependentBatchTestClassGroups) {

					smokeBatchesJSONArray.put(
						batchTestClassGroup.getJSONObject());
				}

				jsonObject.put("smoke_batches", smokeBatchesJSONArray);
			}

			String testSuiteName = getTestSuiteName();

			if (testSuiteName != null) {
				jsonObject.put("test_suite_name", testSuiteName);
			}

			return jsonObject;
		}
	}

	@Override
	public Set<String> getNetworkNames() {
		Set<String> networkNames = new HashSet<>();

		for (JenkinsCohort jenkinsCohort : getJenkinsCohorts()) {
			networkNames.addAll(jenkinsCohort.getNetworkNames());
		}

		networkNames.removeAll(Collections.singleton(null));

		return networkNames;
	}

	@Override
	public Set<String> getSegmentNames() {
		Set<String> segmentNames = new TreeSet<>();

		for (SegmentTestClassGroup segmentTestClassGroup :
				getSegmentTestClassGroups()) {

			segmentNames.add(segmentTestClassGroup.getSegmentName());
		}

		return segmentNames;
	}

	@Override
	public List<SegmentTestClassGroup> getSegmentTestClassGroups() {
		List<SegmentTestClassGroup> segmentTestClassGroups = new ArrayList<>();

		for (BatchTestClassGroup batchTestClassGroup :
				getBatchTestClassGroups()) {

			segmentTestClassGroups.addAll(
				batchTestClassGroup.getSegmentTestClassGroups());
		}

		return segmentTestClassGroups;
	}

	@Override
	public Set<String> getStandaloneBatchNames() {
		if (!isStandaloneBatchEnabled()) {
			return Collections.emptySet();
		}

		Set<String> batchNames = new TreeSet<>();

		JobProperty jobProperty = getJobProperty("test.batch.names.standalone");

		Set<String> standaloneTestBatchNames = getSetFromString(
			jobProperty.getValue());

		for (BatchTestClassGroup batchTestClassGroup :
				getBatchTestClassGroups()) {

			String batchName = batchTestClassGroup.getBatchName();

			if (standaloneTestBatchNames.contains(batchName)) {
				batchNames.add(batchName);
			}
		}

		return batchNames;
	}

	@Override
	public Set<String> getStandaloneSegmentNames() {
		if (!isStandaloneBatchEnabled()) {
			return Collections.emptySet();
		}

		Set<String> segmentNames = new TreeSet<>();

		JobProperty jobProperty = getJobProperty("test.batch.names.standalone");

		Set<String> standaloneTestBatchNames = getSetFromString(
			jobProperty.getValue());

		for (SegmentTestClassGroup segmentTestClassGroup :
				getSegmentTestClassGroups()) {

			if (standaloneTestBatchNames.contains(
					segmentTestClassGroup.getBatchName())) {

				segmentNames.add(segmentTestClassGroup.getSegmentName());
			}
		}

		return segmentNames;
	}

	@Override
	public String getTestPropertiesContent() {
		Map<String, Properties> propertiesMap = new HashMap<>();

		List<BatchTestClassGroup> batchTestClassGroups = new ArrayList<>(
			getBatchTestClassGroups());

		batchTestClassGroups.addAll(getDependentBatchTestClassGroups());

		for (BatchTestClassGroup batchTestClassGroup : batchTestClassGroups) {
			Properties batchProperties = new Properties();

			batchProperties.setProperty(
				"test.batch.cohort.name", batchTestClassGroup.getCohortName());
			batchProperties.setProperty(
				"test.batch.job.name", batchTestClassGroup.getBatchJobName());
			batchProperties.setProperty(
				"test.batch.maximum.slaves.per.host",
				String.valueOf(batchTestClassGroup.getMaximumSlavesPerHost()));
			batchProperties.setProperty(
				"test.batch.minimum.slave.ram",
				String.valueOf(batchTestClassGroup.getMinimumSlaveRAM()));
			batchProperties.setProperty(
				"test.batch.slave.label", batchTestClassGroup.getSlaveLabel());

			if (batchTestClassGroup instanceof FunctionalBatchTestClassGroup) {
				FunctionalBatchTestClassGroup functionalBatchTestClassGroup =
					(FunctionalBatchTestClassGroup)batchTestClassGroup;

				String testBatchRunPropertyQuery =
					functionalBatchTestClassGroup.
						getTestBatchRunPropertyQuery();

				if (testBatchRunPropertyQuery != null) {
					batchProperties.setProperty(
						"test.batch.run.property.query",
						testBatchRunPropertyQuery);
				}
			}
			else {
				batchProperties.setProperty(
					"test.batch.size",
					String.valueOf(batchTestClassGroup.getAxisCount()));
			}

			if (isDownstreamEnabled()) {
				batchProperties.setProperty(
					"test.downstream.job.name",
					batchTestClassGroup.getDownstreamJobName());
			}

			propertiesMap.put(
				batchTestClassGroup.getBatchName(), batchProperties);

			for (int i = 0; i < batchTestClassGroup.getSegmentCount(); i++) {
				Properties segmentProperties = new Properties();

				SegmentTestClassGroup segmentTestClassGroup =
					batchTestClassGroup.getSegmentTestClassGroup(i);

				segmentProperties.setProperty(
					"test.batch.cohort.name",
					segmentTestClassGroup.getCohortName());
				segmentProperties.setProperty(
					"test.batch.job.name",
					segmentTestClassGroup.getBatchJobName());
				segmentProperties.setProperty(
					"test.batch.maximum.slaves.per.host",
					String.valueOf(
						segmentTestClassGroup.getMaximumSlavesPerHost()));
				segmentProperties.setProperty(
					"test.batch.minimum.slave.ram",
					String.valueOf(segmentTestClassGroup.getMinimumSlaveRAM()));
				segmentProperties.setProperty(
					"test.batch.name", segmentTestClassGroup.getBatchName());
				segmentProperties.setProperty(
					"test.batch.size",
					String.valueOf(segmentTestClassGroup.getAxisCount()));
				segmentProperties.setProperty(
					"test.batch.slave.label",
					segmentTestClassGroup.getSlaveLabel());

				String testCasePropertiesContent =
					segmentTestClassGroup.getTestCasePropertiesContent();

				if (testCasePropertiesContent != null) {
					testCasePropertiesContent =
						testCasePropertiesContent.replaceAll(
							"\n", "\\${line.separator}");

					segmentProperties.setProperty(
						"test.case.properties", testCasePropertiesContent);
				}

				if (isDownstreamEnabled()) {
					segmentProperties.setProperty(
						"test.downstream.job.name",
						segmentTestClassGroup.getDownstreamJobName());
				}

				if (segmentTestClassGroup instanceof
						FunctionalSegmentTestClassGroup) {

					segmentProperties.setProperty(
						"run.test.case.method.group", String.valueOf(i));
				}

				propertiesMap.put(
					segmentTestClassGroup.getSegmentName(), segmentProperties);
			}
		}

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Properties> propertiesEntry :
				propertiesMap.entrySet()) {

			Properties properties = propertiesEntry.getValue();

			for (String propertyName : properties.stringPropertyNames()) {
				sb.append(propertyName);
				sb.append("[");
				sb.append(propertiesEntry.getKey());
				sb.append("]=");
				sb.append(properties.getProperty(propertyName));
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	public String getTestSuiteName() {
		if (this instanceof TestSuiteJob) {
			TestSuiteJob testSuiteJob = (TestSuiteJob)this;

			return testSuiteJob.getTestSuiteName();
		}

		return null;
	}

	@Override
	public int getTimeoutMinutes(JenkinsMaster jenkinsMaster) {
		return JenkinsResultsParserUtil.getJobTimeoutMinutes(
			jenkinsMaster, getJobName());
	}

	@Override
	public boolean isBuildCachingEnabled() {
		String buildCachingEnabled = System.getenv("BUILD_CACHING_ENABLED");

		if (Objects.equals(buildCachingEnabled, "true")) {
			return true;
		}

		try {
			buildCachingEnabled = JenkinsResultsParserUtil.getBuildProperty(
				"build.caching.enabled", getJobName(), getTestSuiteName());

			if (Objects.equals(buildCachingEnabled, "true")) {
				return true;
			}
		}
		catch (IOException ioException) {
			return false;
		}

		return false;
	}

	@Override
	public boolean isDownstreamEnabled() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.downstream.enabled");

		String downstreamEnabled = jobProperty.getValue();

		if ((downstreamEnabled != null) && downstreamEnabled.equals("true")) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isJUnitTestsModifiedOnly() {
		if (_jUnitTestFileModifiedOnly != null) {
			return _jUnitTestFileModifiedOnly;
		}

		if (!(this instanceof PortalTestClassJob)) {
			_jUnitTestFileModifiedOnly = false;

			return _jUnitTestFileModifiedOnly;
		}

		List<PathMatcher> jUnitIncludePathMatchers =
			_getJUnitIncludePathMatchers();

		if (jUnitIncludePathMatchers.isEmpty()) {
			_jUnitTestFileModifiedOnly = false;

			return _jUnitTestFileModifiedOnly;
		}

		PortalTestClassJob portalTestClassJob = (PortalTestClassJob)this;

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		List<File> modifiedFilesList =
			portalGitWorkingDirectory.getModifiedFilesList();

		if (modifiedFilesList.isEmpty()) {
			_jUnitTestFileModifiedOnly = false;

			return _jUnitTestFileModifiedOnly;
		}

		for (File modifiedFile : modifiedFilesList) {
			if (!JenkinsResultsParserUtil.isFileIncluded(
					null, jUnitIncludePathMatchers, modifiedFile)) {

				_jUnitTestFileModifiedOnly = false;

				return _jUnitTestFileModifiedOnly;
			}
		}

		_jUnitTestFileModifiedOnly = true;

		return _jUnitTestFileModifiedOnly;
	}

	@Override
	public boolean isSegmentEnabled() {
		JobProperty jobProperty = getJobProperty("test.batch.segment.enabled");

		String segmentEnabled = jobProperty.getValue();

		if ((segmentEnabled != null) && segmentEnabled.equals("true")) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isStandaloneBatchEnabled() {
		return false;
	}

	@Override
	public boolean isTestAnalyticsCloud() {
		for (BatchTestClassGroup batchTestClassGroup :
				getBatchTestClassGroups()) {

			if (batchTestClassGroup.isTestAnalyticsCloud()) {
				_testAnalyticsCloud = true;

				return _testAnalyticsCloud;
			}
		}

		_testAnalyticsCloud = false;

		return _testAnalyticsCloud;
	}

	@Override
	public boolean isTestHotfixChanges() {
		JobProperty jobProperty = getJobProperty("test.hotfix.changes");

		if (jobProperty != null) {
			recordJobProperty(jobProperty);

			return Boolean.parseBoolean(jobProperty.getValue());
		}

		return false;
	}

	@Override
	public boolean isTestJaCoCoCodeCoverage() {
		JobProperty jobProperty = getJobProperty("test.jacoco.code.coverage");

		if (jobProperty != null) {
			recordJobProperty(jobProperty);

			return Boolean.parseBoolean(jobProperty.getValue());
		}

		return false;
	}

	@Override
	public boolean isTestReleaseBundle() {
		JobProperty jobProperty = getJobProperty("test.release.bundle");

		if (jobProperty != null) {
			recordJobProperty(jobProperty);

			return Boolean.parseBoolean(jobProperty.getValue());
		}

		return false;
	}

	@Override
	public boolean isTestRelevantChanges() {
		JobProperty jobProperty = getJobProperty("test.relevant.changes");

		if (jobProperty != null) {
			recordJobProperty(jobProperty);

			return Boolean.parseBoolean(jobProperty.getValue());
		}

		return false;
	}

	@Override
	public boolean isTestRelevantChangesInStable() {
		JobProperty jobProperty = getJobProperty(
			"test.relevant.changes.in.stable");

		if (jobProperty != null) {
			recordJobProperty(jobProperty);

			return Boolean.parseBoolean(jobProperty.getValue());
		}

		return false;
	}

	@Override
	public boolean isValidationRequired() {
		return false;
	}

	protected BaseJob(BuildProfile buildProfile, String jobName) {
		_buildProfile = buildProfile;
		_jobName = jobName;
	}

	protected BaseJob(JSONObject jsonObject) {
		this.jsonObject = jsonObject;

		_buildProfile = BuildProfile.getByString(
			jsonObject.getString("build_profile"));
		_companyDefaultLocale = jsonObject.optString("company_default_locale");
		_jobName = jsonObject.getString("job_name");
	}

	protected List<BatchTestClassGroup> getBatchTestClassGroups(
		List<TestBatch> testBatches) {

		if ((testBatches == null) || testBatches.isEmpty()) {
			return new ArrayList<>();
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Started creating ", String.valueOf(testBatches.size()),
				" batch test class groups at ",
				JenkinsResultsParserUtil.toDateString(new Date(start))));

		List<Callable<BatchTestClassGroup>> callables = new ArrayList<>();

		String testSuiteName = getTestSuiteName();

		final Job job = this;

		Map<File, List<Callable<BatchTestClassGroup>>> testBaseDirCallablesMap =
			new HashMap<>();

		for (TestBatch testBatch : testBatches) {
			File testBaseDir = null;

			String batchName = testBatch.getName();

			JobProperty jobProperty = getJobProperty(
				"test.base.dir", testSuiteName, batchName);

			if ((jobProperty != null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(
					jobProperty.getValue())) {

				testBaseDir = new File(jobProperty.getValue());
			}

			Callable<BatchTestClassGroup> callable =
				new Callable<BatchTestClassGroup>() {

					@Override
					public BatchTestClassGroup call() throws Exception {
						for (int i = 0; i < _pauseRetryCount; i++) {
							try {
								return _call();
							}
							catch (Exception exception) {
								String message = exception.getMessage();

								if ((message != null) &&
									message.contains(
										"Errors found in Playwright tests")) {

									throw exception;
								}

								System.out.println(
									JenkinsResultsParserUtil.combine(
										"[", batchName, "] Retry creating a ",
										"test class group in ",
										String.valueOf(
											_pauseRetryDuration / 1000),
										" seconds"));

								JenkinsResultsParserUtil.sleep(
									_pauseRetryDuration);
							}
						}

						return _call();
					}

					private BatchTestClassGroup _call() throws Exception {
						long start =
							JenkinsResultsParserUtil.getCurrentTimeMillis();

						System.out.println(
							JenkinsResultsParserUtil.combine(
								"[", batchName, "] Started batch test class ",
								"group at ",
								JenkinsResultsParserUtil.toDateString(
									new Date(start))));

						BatchTestClassGroup batchTestClassGroup =
							TestClassGroupFactory.newBatchTestClassGroup(
								job, testBatch);

						long duration =
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start;

						System.out.println(
							JenkinsResultsParserUtil.combine(
								"[", batchName, "] Completed batch test class ",
								"group in ",
								JenkinsResultsParserUtil.toDurationString(
									duration),
								" at ",
								JenkinsResultsParserUtil.toDateString(
									new Date())));

						if (batchTestClassGroup.getAxisCount() <= 0) {
							return null;
						}

						return batchTestClassGroup;
					}

					private final Integer _pauseRetryCount = 2;
					private final Integer _pauseRetryDuration = 5000;

				};

			if (testBaseDir == null) {
				callables.add(callable);

				continue;
			}

			List<Callable<BatchTestClassGroup>> testBaseDirCallables =
				testBaseDirCallablesMap.get(testBaseDir);

			if (testBaseDirCallables == null) {
				testBaseDirCallables = new ArrayList<>();

				testBaseDirCallablesMap.put(testBaseDir, testBaseDirCallables);
			}

			testBaseDirCallables.add(callable);

			testBaseDirCallablesMap.put(testBaseDir, testBaseDirCallables);
		}

		ParallelExecutor<BatchTestClassGroup> parallelExecutor =
			new ParallelExecutor<>(
				callables, _executorService, "getBatchTestClassGroups");

		List<BatchTestClassGroup> batchTestClassGroups = null;

		try {
			batchTestClassGroups = parallelExecutor.execute();

			for (List<Callable<BatchTestClassGroup>> testBaseDirCallables :
					testBaseDirCallablesMap.values()) {

				parallelExecutor = new ParallelExecutor<>(
					testBaseDirCallables, _executorService,
					"getBatchTestClassGroups2");

				batchTestClassGroups.addAll(parallelExecutor.execute());
			}
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		batchTestClassGroups.removeAll(Collections.singleton(null));

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Completed creating ",
				String.valueOf(batchTestClassGroups.size()),
				" batch test class groups in ",
				JenkinsResultsParserUtil.toDurationString(
					JenkinsResultsParserUtil.getCurrentTimeMillis() - start),
				" at ", JenkinsResultsParserUtil.toDateString(new Date())));

		return batchTestClassGroups;
	}

	protected List<BatchTestClassGroup> getBatchTestClassGroups(
		Set<String> rawBatchNames) {

		if ((rawBatchNames == null) || rawBatchNames.isEmpty()) {
			return new ArrayList<>();
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Started creating ", String.valueOf(rawBatchNames.size()),
				" batch test class groups at ",
				JenkinsResultsParserUtil.toDateString(new Date(start))));

		List<Callable<BatchTestClassGroup>> callables = new ArrayList<>();

		String testSuiteName = getTestSuiteName();

		final Job job = this;

		Map<File, List<Callable<BatchTestClassGroup>>> testBaseDirCallablesMap =
			new HashMap<>();

		for (final String batchName : rawBatchNames) {
			File testBaseDir = null;

			JobProperty jobProperty = getJobProperty(
				"test.base.dir", testSuiteName, batchName);

			if ((jobProperty != null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(
					jobProperty.getValue())) {

				testBaseDir = new File(jobProperty.getValue());
			}

			Callable<BatchTestClassGroup> callable =
				new Callable<BatchTestClassGroup>() {

					@Override
					public BatchTestClassGroup call() throws Exception {
						for (int i = 0; i < _pauseRetryCount; i++) {
							try {
								return _call();
							}
							catch (Exception exception) {
								String message = exception.getMessage();

								if ((message != null) &&
									message.contains(
										"Errors found in Playwright tests")) {

									throw exception;
								}

								System.out.println(
									JenkinsResultsParserUtil.combine(
										"[", batchName, "] Retry creating a ",
										"test class group in ",
										String.valueOf(
											_pauseRetryDuration / 1000),
										" seconds."));

								JenkinsResultsParserUtil.sleep(
									_pauseRetryDuration);
							}
						}

						return _call();
					}

					private BatchTestClassGroup _call() throws Exception {
						long start =
							JenkinsResultsParserUtil.getCurrentTimeMillis();

						System.out.println(
							JenkinsResultsParserUtil.combine(
								"[", batchName, "] Started batch test class ",
								"group at ",
								JenkinsResultsParserUtil.toDateString(
									new Date(start))));

						BatchTestClassGroup batchTestClassGroup =
							TestClassGroupFactory.newBatchTestClassGroup(
								batchName, job);

						long duration =
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start;

						System.out.println(
							JenkinsResultsParserUtil.combine(
								"[", batchName, "] Completed batch test class ",
								"group in ",
								JenkinsResultsParserUtil.toDurationString(
									duration),
								" at ",
								JenkinsResultsParserUtil.toDateString(
									new Date())));

						if (batchTestClassGroup.getAxisCount() <= 0) {
							return null;
						}

						return batchTestClassGroup;
					}

					private final Integer _pauseRetryCount = 2;
					private final Integer _pauseRetryDuration = 5000;

				};

			if (testBaseDir == null) {
				callables.add(callable);

				continue;
			}

			List<Callable<BatchTestClassGroup>> testBaseDirCallables =
				testBaseDirCallablesMap.get(testBaseDir);

			if (testBaseDirCallables == null) {
				testBaseDirCallables = new ArrayList<>();

				testBaseDirCallablesMap.put(testBaseDir, testBaseDirCallables);
			}

			testBaseDirCallables.add(callable);

			testBaseDirCallablesMap.put(testBaseDir, testBaseDirCallables);
		}

		ParallelExecutor<BatchTestClassGroup> parallelExecutor =
			new ParallelExecutor<>(
				callables, _executorService, "getBatchTestClassGroups");

		List<BatchTestClassGroup> batchTestClassGroups;

		try {
			batchTestClassGroups = parallelExecutor.execute();

			if (parallelExecutor.hasFailedTask()) {
				throw new RuntimeException(
					"Unable to create batch test class groups");
			}

			for (List<Callable<BatchTestClassGroup>> testBaseDirCallables :
					testBaseDirCallablesMap.values()) {

				parallelExecutor = new ParallelExecutor<>(
					testBaseDirCallables, _executorService,
					"getBatchTestClassGroups2");

				batchTestClassGroups.addAll(parallelExecutor.execute());

				if (parallelExecutor.hasFailedTask()) {
					throw new RuntimeException(
						"Unable to create batch test class groups");
				}
			}
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		batchTestClassGroups.removeAll(Collections.singleton(null));

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Completed creating ",
				String.valueOf(batchTestClassGroups.size()),
				" batch test class groups in ",
				JenkinsResultsParserUtil.toDurationString(
					JenkinsResultsParserUtil.getCurrentTimeMillis() - start),
				" at ", JenkinsResultsParserUtil.toDateString(new Date())));

		return batchTestClassGroups;
	}

	protected Document getConfigDocument(JenkinsMaster jenkinsMaster)
		throws DocumentException, IOException {

		if (_configDocument == null) {
			_configDocument = JenkinsResultsParserUtil.getJobConfigDocument(
				jenkinsMaster, getJobName());
		}

		return _configDocument;
	}

	protected JSONObject getJobJSONObject(
		JenkinsMaster jenkinsMaster, String tree) {

		if (getJobURL(jenkinsMaster) == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		sb.append(
			JenkinsResultsParserUtil.getLocalURL(getJobURL(jenkinsMaster)));
		sb.append("/api/json?pretty");

		if (tree != null) {
			sb.append("&tree=");
			sb.append(tree);
		}

		try {
			return JenkinsResultsParserUtil.toJSONObject(sb.toString(), false);
		}
		catch (IOException ioException) {
			throw new RuntimeException("Unable to get job JSON", ioException);
		}
	}

	protected JobProperty getJobProperty(String basePropertyName) {
		return JobPropertyFactory.newJobProperty(
			basePropertyName, null, null, this, null, null, true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, boolean useBasePropertyName) {

		return JobPropertyFactory.newJobProperty(
			basePropertyName, null, null, this, null, null,
			useBasePropertyName);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, String testSuiteName, String batchName) {

		return JobPropertyFactory.newJobProperty(
			basePropertyName, testSuiteName, batchName, this, null, null, true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, String testSuiteName, String batchName,
		JobProperty.Type type) {

		return JobPropertyFactory.newJobProperty(
			basePropertyName, testSuiteName, batchName, this, null, type, true);
	}

	protected Set<String> getRawBatchNames() {
		JobProperty jobProperty = getJobProperty("test.batch.names");

		recordJobProperty(jobProperty);

		return getSetFromString(jobProperty.getValue());
	}

	protected Set<String> getRawDependentBatchNames() {
		JobProperty jobProperty = getJobProperty("test.batch.names.smoke");

		recordJobProperty(jobProperty);

		return getSetFromString(jobProperty.getValue());
	}

	protected Set<String> getSetFromString(String string) {
		Set<String> set = new TreeSet<>();

		if (JenkinsResultsParserUtil.isNullOrEmpty(string)) {
			return set;
		}

		for (String item : StringUtils.split(string, ",")) {
			if (item.startsWith("#")) {
				continue;
			}

			set.add(item.trim());
		}

		return set;
	}

	protected List<TestBatch> getTestBatches() {
		return new ArrayList<>();
	}

	protected void recordJobProperties(Set<JobProperty> jobProperties) {
		for (JobProperty jobProperty : jobProperties) {
			recordJobProperty(jobProperty);
		}
	}

	protected void recordJobProperty(JobProperty jobProperty) {
		if ((jobProperty == null) || jobProperties.contains(jobProperty)) {
			return;
		}

		jobProperties.add(jobProperty);
	}

	protected List<BatchTestClassGroup> batchTestClassGroups;
	protected final List<JobProperty> jobProperties = new ArrayList<>();
	protected final List<File> jobPropertiesFiles = new ArrayList<>();
	protected JSONObject jsonObject;

	private int _getDistNodeAxisCount() {
		try {
			String distNodeAxisCount =
				JenkinsResultsParserUtil.getBuildProperty(
					"dist.node.axis.count");

			if (JenkinsResultsParserUtil.isInteger(distNodeAxisCount)) {
				return Integer.parseInt(distNodeAxisCount);
			}
		}
		catch (IOException ioException) {
		}

		return 25;
	}

	private int _getDistNodeCountMinimum() {
		try {
			String distNodeCountMinimum =
				JenkinsResultsParserUtil.getBuildProperty(
					"dist.node.count.minimum");

			if (JenkinsResultsParserUtil.isInteger(distNodeCountMinimum)) {
				return Integer.parseInt(distNodeCountMinimum);
			}
		}
		catch (IOException ioException) {
		}

		return 3;
	}

	private Map<String, Properties> _getJobPropertiesMap() {
		synchronized (jobProperties) {
			if (!_initializeJobProperties) {
				getBatchTestClassGroups();

				getDependentBatchTestClassGroups();

				_initializeJobProperties = true;
			}
		}

		Map<String, Properties> jobPropertiesMap = new TreeMap<>();

		for (JobProperty jobProperty : jobProperties) {
			if (jobProperty == null) {
				continue;
			}

			String jobPropertyValue = jobProperty.getValue();

			if (jobPropertyValue == null) {
				continue;
			}

			String propertiesFilePath = jobProperty.getPropertiesFilePath();

			Properties properties = jobPropertiesMap.get(propertiesFilePath);

			if (properties == null) {
				properties = new Properties();
			}

			properties.setProperty(jobProperty.getName(), jobPropertyValue);

			jobPropertiesMap.put(propertiesFilePath, properties);
		}

		return jobPropertiesMap;
	}

	private List<PathMatcher> _getJUnitIncludePathMatchers() {
		List<PathMatcher> jUnitIncludePathMatchers = new ArrayList<>();

		String testSuiteName = getTestSuiteName();

		if (testSuiteName == null) {
			testSuiteName = "default";
		}

		for (String jUnitBatchName : _JUNIT_BATCH_NAMES) {
			JobProperty jobProperty = getJobProperty(
				"test.batch.class.names.filter", testSuiteName, jUnitBatchName,
				JobProperty.Type.INCLUDE_GLOB);

			if (!(jobProperty instanceof GlobJobProperty)) {
				continue;
			}

			String jobPropertyValue = jobProperty.getValue();

			if (jobPropertyValue == null) {
				continue;
			}

			GlobJobProperty globJobProperty = (GlobJobProperty)jobProperty;

			jUnitIncludePathMatchers.addAll(globJobProperty.getPathMatchers());
		}

		return jUnitIncludePathMatchers;
	}

	private int _getSlaveRAMMinimumDefault() {
		try {
			String slaveRAMMinimumDefault =
				JenkinsResultsParserUtil.getBuildProperty(
					"slave.ram.minimum.default");

			if (JenkinsResultsParserUtil.isInteger(slaveRAMMinimumDefault)) {
				return Integer.parseInt(slaveRAMMinimumDefault);
			}
		}
		catch (IOException ioException) {
		}

		return 16;
	}

	private int _getSlavesPerHostDefault() {
		try {
			String slavesPerHostDefault =
				JenkinsResultsParserUtil.getBuildProperty(
					"slaves.per.host.default");

			if (JenkinsResultsParserUtil.isInteger(slavesPerHostDefault)) {
				return Integer.parseInt(slavesPerHostDefault);
			}
		}
		catch (IOException ioException) {
		}

		return 2;
	}

	private static final String[] _JUNIT_BATCH_NAMES = {
		"integration-jdk8", "modules-integration", "modules-unit", "unit"
	};

	private static final Integer _THREAD_COUNT = 10;

	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(_THREAD_COUNT, true);

	private Set<String> _batchNames;
	private final BuildProfile _buildProfile;
	private String _companyDefaultLocale;
	private Document _configDocument;
	private List<BatchTestClassGroup> _dependentBatchTestClassGroups;
	private final Map<String, List<String>> _distNodesMap = new HashMap<>();
	private boolean _initializeJobProperties;
	private JobHistory _jobHistory;
	private final String _jobName;
	private Boolean _jUnitTestFileModifiedOnly;
	private Boolean _testAnalyticsCloud;

}