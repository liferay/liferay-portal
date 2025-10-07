/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.SegmentTestClassGroup;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface Job {

	public Set<String> getAnalyticsCloudBatchNames();

	public Set<String> getAnalyticsCloudSegmentNames();

	public int getAxisCount();

	public AxisTestClassGroup getAxisTestClassGroup(String axisName);

	public List<AxisTestClassGroup> getAxisTestClassGroups();

	public Set<String> getBatchNames();

	public List<BatchTestClassGroup> getBatchTestClassGroups();

	public List<Build> getBuildHistory(JenkinsMaster jenkinsMaster);

	public BuildProfile getBuildProfile();

	public String getCompanyDefaultLocale();

	public List<AxisTestClassGroup> getDependentAxisTestClassGroups();

	public Set<String> getDependentBatchNames();

	public List<BatchTestClassGroup> getDependentBatchTestClassGroups();

	public Set<String> getDependentSegmentNames();

	public List<SegmentTestClassGroup> getDependentSegmentTestClassGroups();

	public List<String> getDistNodes();

	public List<String> getDistNodes(String networkName);

	public Set<String> getDistRequiredBatchNames();

	public Set<String> getDistRequiredSegmentNames();

	public DistType getDistType();

	public Set<String> getDistTypes();

	public Set<String> getDistTypesExcludingTomcat();

	public Set<JenkinsCohort> getJenkinsCohorts();

	public JobHistory getJobHistory();

	public String getJobName();

	public List<File> getJobPropertiesFiles();

	public List<String> getJobPropertyOptions();

	public String getJobURL(JenkinsMaster jenkinsMaster);

	public JSONObject getJSONObject();

	public Set<String> getNetworkNames();

	public Set<String> getSegmentNames();

	public List<SegmentTestClassGroup> getSegmentTestClassGroups();

	public Set<String> getStandaloneBatchNames();

	public Set<String> getStandaloneSegmentNames();

	public String getTestPropertiesContent();

	public int getTimeoutMinutes(JenkinsMaster jenkinsMaster);

	public boolean isBuildCachingEnabled();

	public boolean isDownstreamEnabled();

	public boolean isJUnitTestsModifiedOnly();

	public boolean isSegmentEnabled();

	public boolean isStandaloneBatchEnabled();

	public boolean isTestAnalyticsCloud();

	public boolean isTestHotfixChanges();

	public boolean isTestJaCoCoCodeCoverage();

	public boolean isTestReleaseBundle();

	public boolean isTestRelevantChanges();

	public boolean isTestRelevantChangesInStable();

	public boolean isValidationRequired();

	public static enum BuildProfile {

		DXP("DXP", "dxp"), PORTAL("Portal", "portal");

		public static BuildProfile getByString(String string) {
			return _buildProfiles.get(string);
		}

		public String toDisplayString() {
			return _displayString;
		}

		@Override
		public String toString() {
			return _string;
		}

		private BuildProfile(String displayString, String string) {
			_displayString = displayString;
			_string = string;
		}

		private static Map<String, BuildProfile> _buildProfiles =
			new HashMap<>();

		static {
			for (BuildProfile buildProfile : values()) {
				_buildProfiles.put(buildProfile.toString(), buildProfile);
			}
		}

		private final String _displayString;
		private final String _string;

	}

	public static enum DistType {

		CI("ci"), RELEASE("release");

		@Override
		public String toString() {
			return _string;
		}

		private DistType(String string) {
			_string = string;
		}

		private final String _string;

	}

}