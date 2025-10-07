/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.JobFactory;
import com.liferay.jenkins.results.parser.PortalAWSJob;
import com.liferay.jenkins.results.parser.PortalEnvironmentJob;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.QAWebsitesGitRepositoryJob;
import com.liferay.jenkins.results.parser.test.batch.JUnitTestBatch;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestBatch;
import com.liferay.jenkins.results.parser.test.batch.PoshiTestBatch;
import com.liferay.jenkins.results.parser.test.batch.TestBatch;

import java.io.File;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestClassGroupFactory {

	public static AxisTestClassGroup newAxisTestClassGroup(
		BatchTestClassGroup batchTestClassGroup) {

		return newAxisTestClassGroup(batchTestClassGroup, null);
	}

	public static AxisTestClassGroup newAxisTestClassGroup(
		BatchTestClassGroup batchTestClassGroup, File testBaseDir) {

		if (batchTestClassGroup instanceof FunctionalBatchTestClassGroup) {
			return new FunctionalAxisTestClassGroup(
				(FunctionalBatchTestClassGroup)batchTestClassGroup,
				testBaseDir);
		}

		if (batchTestClassGroup instanceof JSUnitModulesBatchTestClassGroup) {
			return new JSUnitAxisTestClassGroup(
				(JSUnitModulesBatchTestClassGroup)batchTestClassGroup);
		}

		if (batchTestClassGroup instanceof JUnitBatchTestClassGroup) {
			return new JUnitAxisTestClassGroup(
				(JUnitBatchTestClassGroup)batchTestClassGroup);
		}

		if (batchTestClassGroup instanceof ModulesBatchTestClassGroup) {
			return new ModulesAxisTestClassGroup(
				(ModulesBatchTestClassGroup)batchTestClassGroup);
		}

		if (batchTestClassGroup instanceof PlaywrightBatchTestClassGroup) {
			return new PlaywrightAxisTestClassGroup(batchTestClassGroup);
		}

		if (batchTestClassGroup instanceof PluginsGulpBatchTestClassGroup) {
			return new PluginsGulpAxisTestClassGroup(
				(PluginsGulpBatchTestClassGroup)batchTestClassGroup);
		}

		return new AxisTestClassGroup(batchTestClassGroup);
	}

	public static AxisTestClassGroup newAxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		BatchTestClassGroup batchTestClassGroup =
			segmentTestClassGroup.getBatchTestClassGroup();

		if (batchTestClassGroup instanceof FunctionalBatchTestClassGroup) {
			return new FunctionalAxisTestClassGroup(
				jsonObject, segmentTestClassGroup);
		}

		if (batchTestClassGroup instanceof JSUnitModulesBatchTestClassGroup) {
			return new JSUnitAxisTestClassGroup(
				jsonObject, segmentTestClassGroup);
		}

		if (batchTestClassGroup instanceof JUnitBatchTestClassGroup) {
			return new JUnitAxisTestClassGroup(
				jsonObject, segmentTestClassGroup);
		}

		if (batchTestClassGroup instanceof ModulesBatchTestClassGroup) {
			return new ModulesAxisTestClassGroup(
				jsonObject, segmentTestClassGroup);
		}

		if (batchTestClassGroup instanceof PlaywrightBatchTestClassGroup) {
			return new PlaywrightAxisTestClassGroup(
				jsonObject, segmentTestClassGroup);
		}

		if (batchTestClassGroup instanceof PluginsGulpBatchTestClassGroup) {
			return new PluginsGulpAxisTestClassGroup(
				jsonObject, segmentTestClassGroup);
		}

		return new AxisTestClassGroup(jsonObject, segmentTestClassGroup);
	}

	public static BatchTestClassGroup newBatchTestClassGroup(
		Job job, JSONObject jsonObject) {

		return _newBatchTestClassGroup(null, job, jsonObject, null);
	}

	public static BatchTestClassGroup newBatchTestClassGroup(
		Job job, TestBatch testBatch) {

		return _newBatchTestClassGroup(null, job, null, testBatch);
	}

	public static BatchTestClassGroup newBatchTestClassGroup(
		String batchName, Job job) {

		return _newBatchTestClassGroup(batchName, job, null, null);
	}

	public static SegmentTestClassGroup newSegmentTestClassGroup(
		BatchTestClassGroup batchTestClassGroup) {

		return newSegmentTestClassGroup(batchTestClassGroup, null);
	}

	public static SegmentTestClassGroup newSegmentTestClassGroup(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		if (batchTestClassGroup instanceof
				EnvironmentFunctionalBatchTestClassGroup) {

			if (jsonObject != null) {
				return new EnvironmentFunctionalSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new EnvironmentFunctionalSegmentTestClassGroup(
				batchTestClassGroup);
		}

		if (batchTestClassGroup instanceof
				QAWebsitesFunctionalBatchTestClassGroup) {

			if (jsonObject != null) {
				return new QAWebsitesFunctionalSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new QAWebsitesFunctionalSegmentTestClassGroup(
				batchTestClassGroup);
		}

		if (batchTestClassGroup instanceof FunctionalBatchTestClassGroup) {
			Job job = batchTestClassGroup.getJob();

			if (job instanceof PortalAWSJob) {
				if (jsonObject != null) {
					return new AWSFunctionalSegmentTestClassGroup(
						batchTestClassGroup, jsonObject);
				}

				return new AWSFunctionalSegmentTestClassGroup(
					batchTestClassGroup);
			}

			if (jsonObject != null) {
				return new FunctionalSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new FunctionalSegmentTestClassGroup(batchTestClassGroup);
		}
		else if (batchTestClassGroup instanceof JUnitBatchTestClassGroup) {
			if (jsonObject != null) {
				return new JUnitSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new JUnitSegmentTestClassGroup(batchTestClassGroup);
		}
		else if (batchTestClassGroup instanceof ModulesBatchTestClassGroup) {
			if (batchTestClassGroup instanceof
					JSUnitModulesBatchTestClassGroup) {

				if (jsonObject != null) {
					return new JSUnitModulesSegmentTestClassGroup(
						batchTestClassGroup, jsonObject);
				}

				return new JSUnitModulesSegmentTestClassGroup(
					batchTestClassGroup);
			}

			if (batchTestClassGroup instanceof
					SemVerModulesBatchTestClassGroup) {

				if (jsonObject != null) {
					return new SemVerModulesSegmentTestClassGroup(
						batchTestClassGroup, jsonObject);
				}

				return new SemVerModulesSegmentTestClassGroup(
					batchTestClassGroup);
			}

			if (batchTestClassGroup instanceof
					ServiceBuilderModulesBatchTestClassGroup) {

				if (jsonObject != null) {
					return new ServiceBuilderModulesSegmentTestClassGroup(
						batchTestClassGroup, jsonObject);
				}

				return new ServiceBuilderModulesSegmentTestClassGroup(
					batchTestClassGroup);
			}

			if (jsonObject != null) {
				return new ModulesSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new ModulesSegmentTestClassGroup(batchTestClassGroup);
		}
		else if (batchTestClassGroup instanceof
					QAWebsitesPlaywrightBatchTestClassGroup) {

			if (jsonObject != null) {
				return new QAWebsitesPlaywrightSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new QAWebsitesPlaywrightSegmentTestClassGroup(
				batchTestClassGroup);
		}
		else if (batchTestClassGroup instanceof PlaywrightBatchTestClassGroup) {
			if (jsonObject != null) {
				return new PlaywrightSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new PlaywrightSegmentTestClassGroup(batchTestClassGroup);
		}
		else if (batchTestClassGroup instanceof PluginsBatchTestClassGroup) {
			if (jsonObject != null) {
				return new PluginsSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new PluginsSegmentTestClassGroup(batchTestClassGroup);
		}
		else if (batchTestClassGroup instanceof
					PluginsGulpBatchTestClassGroup) {

			if (jsonObject != null) {
				return new PluginsGulpSegmentTestClassGroup(
					batchTestClassGroup, jsonObject);
			}

			return new PluginsGulpSegmentTestClassGroup(batchTestClassGroup);
		}

		if (jsonObject != null) {
			return new SegmentTestClassGroup(batchTestClassGroup, jsonObject);
		}

		return new SegmentTestClassGroup(batchTestClassGroup);
	}

	private static BatchTestClassGroup _newBatchTestClassGroup(
		String batchName, Job job, JSONObject jsonObject, TestBatch testBatch) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(batchName)) {
			if (jsonObject != null) {
				batchName = jsonObject.getString("batch_name");
			}
			else {
				batchName = testBatch.getName();
			}
		}

		String key = JobFactory.getKey(job) + "_" + batchName;

		BatchTestClassGroup batchTestClassGroup = _batchTestClassGroups.get(
			key);

		if ((batchTestClassGroup == null) &&
			(job instanceof PortalEnvironmentJob)) {

			if (jsonObject != null) {
				batchTestClassGroup =
					new EnvironmentFunctionalBatchTestClassGroup(
						jsonObject, (PortalEnvironmentJob)job);
			}
			else {
				batchTestClassGroup =
					new EnvironmentFunctionalBatchTestClassGroup(
						batchName, (PortalEnvironmentJob)job);
			}
		}

		if ((batchTestClassGroup == null) &&
			(job instanceof PortalTestClassJob)) {

			PortalTestClassJob portalTestClassJob = (PortalTestClassJob)job;

			if (batchName.startsWith("functional") ||
				batchName.startsWith("modules-functional") ||
				batchName.startsWith("subrepository-functional")) {

				if (jsonObject != null) {
					batchTestClassGroup = new FunctionalBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else if (testBatch != null) {
					batchTestClassGroup = new FunctionalBatchTestClassGroup(
						batchName, portalTestClassJob,
						(PoshiTestBatch)testBatch);
				}
				else {
					batchTestClassGroup = new FunctionalBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("integration") ||
					 batchName.startsWith("junit-test") ||
					 batchName.startsWith("unit")) {

				if (jsonObject != null) {
					batchTestClassGroup = new JUnitBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new JUnitBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("js-test") ||
					 batchName.startsWith("portal-frontend-js")) {

				if (jsonObject != null) {
					batchTestClassGroup = new NPMTestBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new NPMTestBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("js-unit")) {
				if (jsonObject != null) {
					batchTestClassGroup = new JSUnitModulesBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new JSUnitModulesBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("modules-compile")) {
				if (jsonObject != null) {
					batchTestClassGroup = new CompileModulesBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new CompileModulesBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith(
						"modules-integration-project-templates") ||
					 batchName.startsWith("modules-unit-project-templates")) {

				if (jsonObject != null) {
					batchTestClassGroup =
						new ProjectTemplatesJUnitBatchTestClassGroup(
							jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup =
						new ProjectTemplatesJUnitBatchTestClassGroup(
							batchName, portalTestClassJob);
				}
			}
			else if ((batchName.startsWith("modules-integration") &&
					  !batchName.startsWith(
						  "modules-integration-project-templates")) ||
					 (batchName.startsWith("modules-unit") &&
					  !batchName.startsWith(
						  "modules-unit-project-templates")) ||
					 batchName.startsWith("subrepository-integration") ||
					 batchName.startsWith("subrepository-unit")) {

				if (jsonObject != null) {
					batchTestClassGroup = new ModulesJUnitBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else if (testBatch != null) {
					batchTestClassGroup = new ModulesJUnitBatchTestClassGroup(
						batchName, portalTestClassJob,
						(JUnitTestBatch)testBatch);
				}
				else {
					batchTestClassGroup = new ModulesJUnitBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("modules-semantic-versioning")) {
				if (jsonObject != null) {
					batchTestClassGroup = new SemVerModulesBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new SemVerModulesBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("playwright-compile")) {
				if (jsonObject != null) {
					batchTestClassGroup =
						new PlaywrightCompileBatchTestClassGroup(
							jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup =
						new PlaywrightCompileBatchTestClassGroup(
							batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("playwright-js")) {
				if (jsonObject != null) {
					batchTestClassGroup = new PlaywrightBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else if (testBatch != null) {
					batchTestClassGroup = new PlaywrightBatchTestClassGroup(
						batchName, (PlaywrightTestBatch)testBatch,
						portalTestClassJob);
				}
				else {
					batchTestClassGroup = new PlaywrightBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("plugins-compile")) {
				if (jsonObject != null) {
					batchTestClassGroup = new PluginsBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new PluginsBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("plugins-functional")) {
				if (jsonObject != null) {
					batchTestClassGroup =
						new PluginsFunctionalBatchTestClassGroup(
							jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup =
						new PluginsFunctionalBatchTestClassGroup(
							batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("plugins-gulp")) {
				if (jsonObject != null) {
					batchTestClassGroup = new PluginsGulpBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new PluginsGulpBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("qa-websites-functional") &&
					 (job instanceof QAWebsitesGitRepositoryJob)) {

				if (jsonObject != null) {
					batchTestClassGroup =
						new QAWebsitesFunctionalBatchTestClassGroup(
							jsonObject, (QAWebsitesGitRepositoryJob)job);
				}
				else {
					batchTestClassGroup =
						new QAWebsitesFunctionalBatchTestClassGroup(
							batchName, (QAWebsitesGitRepositoryJob)job);
				}
			}
			else if (batchName.startsWith("qa-websites-playwright")) {
				if (jsonObject != null) {
					batchTestClassGroup =
						new QAWebsitesPlaywrightBatchTestClassGroup(
							jsonObject, (QAWebsitesGitRepositoryJob)job);
				}
				else {
					batchTestClassGroup =
						new QAWebsitesPlaywrightBatchTestClassGroup(
							batchName, (QAWebsitesGitRepositoryJob)job);
				}
			}
			else if (batchName.startsWith("rest-builder")) {
				if (jsonObject != null) {
					batchTestClassGroup =
						new RESTBuilderModulesBatchTestClassGroup(
							jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup =
						new RESTBuilderModulesBatchTestClassGroup(
							batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("semantic-versioning")) {
				if (jsonObject != null) {
					batchTestClassGroup = new SemanticVersioningTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new SemanticVersioningTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("service-builder")) {
				if (jsonObject != null) {
					batchTestClassGroup =
						new ServiceBuilderModulesBatchTestClassGroup(
							jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup =
						new ServiceBuilderModulesBatchTestClassGroup(
							batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("tck")) {
				if (jsonObject != null) {
					batchTestClassGroup = new TCKJunitBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new TCKJunitBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
			else if (batchName.startsWith("workspaces-compile")) {
				if (jsonObject != null) {
					batchTestClassGroup =
						new WorkspacesCompileBatchTestClassGroup(
							jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup =
						new WorkspacesCompileBatchTestClassGroup(
							batchName, portalTestClassJob);
				}
			}
			else {
				if (jsonObject != null) {
					batchTestClassGroup = new DefaultBatchTestClassGroup(
						jsonObject, portalTestClassJob);
				}
				else {
					batchTestClassGroup = new DefaultBatchTestClassGroup(
						batchName, portalTestClassJob);
				}
			}
		}

		if (batchTestClassGroup == null) {
			throw new IllegalArgumentException("Unknown test class group");
		}

		_batchTestClassGroups.put(key, batchTestClassGroup);

		return batchTestClassGroup;
	}

	private static final Map<String, BatchTestClassGroup>
		_batchTestClassGroups = new ConcurrentHashMap<>();

}