/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.CompileModulesBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.FunctionalBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitModulesBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JUnitBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.NPMTestBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PlaywrightBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PluginsBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PluginsGulpBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.RESTBuilderModulesBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.SemVerModulesBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.ServiceBuilderModulesBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.TCKJunitBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.WorkspacesCompileBatchTestClassGroup;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestClassFactory {

	public static List<JUnitTestClass> getJUnitTestClasses() {
		List<JUnitTestClass> jUnitTestClasses = new ArrayList<>(
			_jUnitTestClasses.values());

		Collections.sort(jUnitTestClasses);

		return jUnitTestClasses;
	}

	public static List<NPMTestClass> getNPMTestClasses() {
		List<NPMTestClass> npmTestClasses = new ArrayList<>(
			_npmTestClasses.values());

		Collections.sort(npmTestClasses);

		return npmTestClasses;
	}

	public static List<PlaywrightJUnitTestClass> getPlaywrightTestClasses() {
		List<PlaywrightJUnitTestClass> playwrightJUnitTestClasses =
			new ArrayList<>(_playwrightJUnitTestClasses.values());

		Collections.sort(playwrightJUnitTestClasses);

		return playwrightJUnitTestClasses;
	}

	public static TestClass newTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile) {

		return _newTestClass(batchTestClassGroup, null, testClassFile, null);
	}

	public static TestClass newTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile,
		List<String> testClassMethodNames) {

		if (batchTestClassGroup instanceof JUnitBatchTestClassGroup) {
			return new JUnitTestClass(
				batchTestClassGroup, testClassFile, testClassMethodNames);
		}

		return _newTestClass(batchTestClassGroup, null, testClassFile, null);
	}

	public static TestClass newTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile,
		String testClassMethodName) {

		return _newTestClass(
			batchTestClassGroup, null, testClassFile, testClassMethodName);
	}

	public static TestClass newTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		return _newTestClass(batchTestClassGroup, jsonObject, null, null);
	}

	public static TestClass newTestClass(
		BatchTestClassGroup batchTestClassGroup, String testClassMethodName) {

		return _newTestClass(
			batchTestClassGroup, null, null, testClassMethodName);
	}

	public static TestClassMethod newTestClassMethod(
		boolean ignored, String name, String issues, TestClass testClass) {

		if (testClass instanceof PlaywrightJUnitTestClass) {
			return new PlaywrightTestClassMethod(
				ignored, name, issues, testClass);
		}

		return new TestClassMethod(ignored, name, issues, testClass);
	}

	public static TestClassMethod newTestClassMethod(
		boolean ignored, String name, TestClass testClass) {

		if (testClass instanceof PlaywrightJUnitTestClass) {
			return new PlaywrightTestClassMethod(ignored, name, testClass);
		}

		return new TestClassMethod(ignored, name, testClass);
	}

	public static TestClassMethod newTestClassMethod(
		JSONObject jsonObject, TestClass testClass) {

		if (testClass instanceof PlaywrightJUnitTestClass) {
			return new PlaywrightTestClassMethod(jsonObject, testClass);
		}

		return new TestClassMethod(jsonObject, testClass);
	}

	private static File _getCanonicalFile(
		File testClassFile, JSONObject jsonObject) {

		File canonicalFile = null;

		if (testClassFile != null) {
			canonicalFile = JenkinsResultsParserUtil.getCanonicalFile(
				testClassFile);
		}
		else if ((jsonObject != null) && jsonObject.has("file")) {
			canonicalFile = JenkinsResultsParserUtil.getCanonicalFile(
				new File(jsonObject.getString("file")));
		}
		else {
			throw new RuntimeException("Please set a test class file");
		}

		return canonicalFile;
	}

	private static TestClass _newTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject,
		File testClassFile, String testClassMethodName) {

		long start = System.currentTimeMillis();

		try {
			if (batchTestClassGroup instanceof
					CompileModulesBatchTestClassGroup) {

				if (jsonObject != null) {
					return new CompileModulesTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new CompileModulesTestClass(
					batchTestClassGroup, testClassFile);
			}
			else if (batchTestClassGroup instanceof
						FunctionalBatchTestClassGroup) {

				if (jsonObject != null) {
					return new FunctionalTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new FunctionalTestClass(
					batchTestClassGroup, testClassMethodName);
			}
			else if (batchTestClassGroup instanceof
						JSUnitModulesBatchTestClassGroup) {

				if (jsonObject != null) {
					return new JSUnitModulesTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new JSUnitModulesTestClass(
					batchTestClassGroup, testClassFile);
			}
			else if (batchTestClassGroup instanceof JUnitBatchTestClassGroup) {
				File canonicalFile = _getCanonicalFile(
					testClassFile, jsonObject);

				if (_jUnitTestClasses.containsKey(canonicalFile)) {
					return _jUnitTestClasses.get(canonicalFile);
				}

				JUnitTestClass jUnitTestClass = null;

				if (jsonObject != null) {
					jUnitTestClass = new JUnitTestClass(
						batchTestClassGroup, jsonObject);
				}
				else {
					jUnitTestClass = new JUnitTestClass(
						batchTestClassGroup, testClassFile);
				}

				_jUnitTestClasses.put(canonicalFile, jUnitTestClass);

				return _jUnitTestClasses.get(canonicalFile);
			}
			else if (batchTestClassGroup instanceof
						NPMTestBatchTestClassGroup) {

				File canonicalFile = _getCanonicalFile(
					testClassFile, jsonObject);

				if (_npmTestClasses.containsKey(canonicalFile)) {
					return _npmTestClasses.get(canonicalFile);
				}

				NPMTestClass npmTestClass = null;

				if (jsonObject != null) {
					npmTestClass = new NPMTestClass(
						batchTestClassGroup, jsonObject);
				}
				else {
					npmTestClass = new NPMTestClass(
						batchTestClassGroup, testClassFile);
				}

				_npmTestClasses.put(canonicalFile, npmTestClass);

				return _npmTestClasses.get(canonicalFile);
			}
			else if (batchTestClassGroup instanceof
						PlaywrightBatchTestClassGroup) {

				File canonicalFile = _getCanonicalFile(
					testClassFile, jsonObject);

				if (_playwrightJUnitTestClasses.containsKey(canonicalFile)) {
					return _playwrightJUnitTestClasses.get(canonicalFile);
				}

				PlaywrightJUnitTestClass playwrightJUnitTestClass = null;

				if (jsonObject != null) {
					playwrightJUnitTestClass = new PlaywrightJUnitTestClass(
						batchTestClassGroup, jsonObject);
				}
				else {
					playwrightJUnitTestClass = new PlaywrightJUnitTestClass(
						batchTestClassGroup, testClassFile);
				}

				_playwrightJUnitTestClasses.put(
					canonicalFile, playwrightJUnitTestClass);

				return _playwrightJUnitTestClasses.get(canonicalFile);
			}
			else if (batchTestClassGroup instanceof
						PluginsBatchTestClassGroup) {

				if (jsonObject != null) {
					return new PluginsTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new PluginsTestClass(batchTestClassGroup, testClassFile);
			}
			else if (batchTestClassGroup instanceof
						PluginsGulpBatchTestClassGroup) {

				if (jsonObject != null) {
					return new PluginsGulpTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new PluginsGulpTestClass(
					batchTestClassGroup, testClassFile);
			}
			else if (batchTestClassGroup instanceof
						RESTBuilderModulesBatchTestClassGroup) {

				if (jsonObject != null) {
					return new RESTBuilderModulesTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new RESTBuilderModulesTestClass(
					batchTestClassGroup, testClassFile);
			}
			else if (batchTestClassGroup instanceof
						SemVerModulesBatchTestClassGroup) {

				if (jsonObject != null) {
					return new SemVerModulesTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new SemVerModulesTestClass(
					batchTestClassGroup, testClassFile);
			}
			else if (batchTestClassGroup instanceof
						ServiceBuilderModulesBatchTestClassGroup) {

				if ((testClassFile == null) && jsonObject.has("file")) {
					testClassFile = new File(jsonObject.getString("file"));
				}

				String testClassFileName = testClassFile.getName();

				if (testClassFileName.endsWith(".xml")) {
					if (jsonObject != null) {
						return new ServiceBuilderAntTargetTestClass(
							batchTestClassGroup, jsonObject);
					}

					return new ServiceBuilderAntTargetTestClass(
						batchTestClassGroup, testClassFile);
				}

				if (jsonObject != null) {
					return new ServiceBuilderModulesTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new ServiceBuilderModulesTestClass(
					batchTestClassGroup, testClassFile);
			}
			else if (batchTestClassGroup instanceof
						TCKJunitBatchTestClassGroup) {

				if (jsonObject != null) {
					return new TCKTestClass(batchTestClassGroup, jsonObject);
				}

				return new TCKTestClass(batchTestClassGroup, testClassFile);
			}
			else if (batchTestClassGroup instanceof
						WorkspacesCompileBatchTestClassGroup) {

				if (jsonObject != null) {
					return new WorkspacesCompileTestClass(
						batchTestClassGroup, jsonObject);
				}

				return new WorkspacesCompileTestClass(
					batchTestClassGroup, testClassFile);
			}

			if (jsonObject != null) {
				return new BatchTestClass(batchTestClassGroup, jsonObject);
			}

			return new BatchTestClass(batchTestClassGroup, testClassFile);
		}
		finally {
			long duration = System.currentTimeMillis() - start;

			if (duration > 500) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"[", batchTestClassGroup.getBatchName(),
						"] Created test class for ", testClassFile.toString(),
						" in ",
						JenkinsResultsParserUtil.toDurationString(duration)));
			}
		}
	}

	private static final Map<File, JUnitTestClass> _jUnitTestClasses =
		new HashMap<>();
	private static final Map<File, NPMTestClass> _npmTestClasses =
		new HashMap<>();
	private static final Map<File, PlaywrightJUnitTestClass>
		_playwrightJUnitTestClasses = new HashMap<>();

}