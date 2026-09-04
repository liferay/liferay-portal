/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.group.JUnitBatchTestClassGroup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;

import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

/**
 * @author Peter Yoo
 */
public class Test {

	@Before
	public void setUp() throws Exception {
		JenkinsResultsParserUtil.clearCache();

		mockEnvironment(Collections.<String, String>emptyMap());
	}

	@After
	public void tearDown() {
		BuildDatabaseUtil.clearBuildDatabases();

		Environment.setInstance(new Environment());

		JUnitBatchTestClassGroup.clear();

		JenkinsMasterTestUtil.resetCaches();

		JenkinsResultsParserUtil.setBuildProperties(new Properties());

		JenkinsResultsParserUtil.setTopLevelJobNames(null);

		Map<String, Job> jobs = ReflectionTestUtil.getFieldValue(
			JobFactory.class, "_jobs");

		jobs.clear();

		JobPropertyFactory.clear();

		Shell.setInstance(new Shell());

		TestClassFactory.clear();

		UrlReader.setInstance(new UrlReader());
	}

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	protected static List<File> getDependenciesDirs(
		List<String> simpleClassNames) {

		List<File> dirs = new ArrayList<>(simpleClassNames.size());

		for (String simpleClassName : simpleClassNames) {
			dirs.add(
				new File("src/test/resources/dependencies/" + simpleClassName));
		}

		return dirs;
	}

	protected String getMismatchMessage(
		String expectedValue, String actualValue, String valueName) {

		return JenkinsResultsParserUtil.combine(
			"The expected ", valueName, " value ", expectedValue,
			", Did not match the actual ", valueName, " value ", actualValue,
			".");
	}

	protected List<String> getSimpleClassNames() {
		if (_simpleClassNames == null) {
			_simpleClassNames = new ArrayList<>();

			Class<?> clazz = getClass();

			String simpleName = clazz.getSimpleName();

			while (!simpleName.equals("Object")) {
				_simpleClassNames.add(simpleName);

				clazz = clazz.getSuperclass();

				simpleName = clazz.getSimpleName();
			}
		}

		return _simpleClassNames;
	}

	protected VerificationMode getVerificationMode(boolean invoked) {
		if (invoked) {
			return Mockito.times(1);
		}

		return Mockito.never();
	}

	protected boolean hasCommand(
		Shell.ExecutionRequest executionRequest, String... substrings) {

		if (executionRequest == null) {
			return false;
		}

		String command = executionRequest.getCommands()[0];

		for (String substring : substrings) {
			if (!command.contains(substring)) {
				return false;
			}
		}

		return true;
	}

	protected Environment mockEnvironment(Map<String, String> environmentMap) {
		Environment environment = Mockito.mock(Environment.class);

		Mockito.doAnswer(
			invocation -> environmentMap.get(invocation.getArgument(0))
		).when(
			environment
		).doGet(
			Mockito.anyString()
		);

		Mockito.doReturn(
			environmentMap
		).when(
			environment
		).doGetAll();

		Environment.setInstance(environment);

		return environment;
	}

	protected Shell mockShell() {
		Shell shell = Mockito.mock(
			Shell.class,
			invocation -> {
				Shell.ExecutionRequest executionRequest =
					invocation.getArgument(0);

				throw new AssertionError(
					"No output set for shell command: " +
						Arrays.toString(executionRequest.getCommands()));
			});

		Shell.setInstance(shell);

		return shell;
	}

	protected UrlReader mockUrlReader() {
		UrlReader urlReader = Mockito.mock(
			UrlReader.class,
			invocation -> {
				String url = invocation.getArgument(7);

				throw new AssertionError("No output set for URL: " + url);
			});

		UrlReader.setInstance(urlReader);

		return urlReader;
	}

	protected String read(File file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file.toURI())));
	}

	protected String read(File dir, String fileName) throws IOException {
		return read(new File(dir, fileName));
	}

	protected void setShellCommandOutput(
			String command, Shell shell, String standardOut)
		throws Exception {

		Mockito.doReturn(
			new Shell.ExecutionResult(0, "", standardOut)
		).when(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(executionRequest, command))
		);
	}

	protected void setUrlReaderException(
			IOException ioException, String url, UrlReader urlReader)
		throws Exception {

		Mockito.doThrow(
			ioException
		).when(
			urlReader
		).doRead(
			Mockito.anyBoolean(), Mockito.any(), Mockito.any(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.argThat(
				readURL -> (readURL != null) && readURL.contains(url))
		);
	}

	protected void setUrlReaderOutput(
			long delayMillis, String standardOut, String url,
			UrlReader urlReader)
		throws Exception {

		Mockito.doAnswer(
			invocation -> {
				JenkinsResultsParserUtil.sleep(delayMillis);

				return new ByteArrayInputStream(standardOut.getBytes());
			}
		).when(
			urlReader
		).doRead(
			Mockito.anyBoolean(), Mockito.any(), Mockito.any(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.argThat(
				readURL -> (readURL != null) && readURL.contains(url))
		);
	}

	protected void setUrlReaderOutput(
			String standardOut, String url, UrlReader urlReader)
		throws Exception {

		setUrlReaderOutput(0, standardOut, url, urlReader);
	}

	protected void testEquals(Object expected, Object actual) {
		errorCollector.checkThat(actual, CoreMatchers.equalTo(expected));
	}

	protected void testSame(Object expected, Object actual) {
		errorCollector.checkThat(actual, CoreMatchers.sameInstance(expected));
	}

	protected String toURLString(File file) throws Exception {
		URI uri = file.toURI();

		String urlString = String.valueOf(uri.toURL());

		File dependenciesDir = dependenciesDirs.get(0);

		String path = dependenciesDir.getPath();

		int x =
			path.indexOf("src/test/resources/dependencies/") +
				"src/test/resources/dependencies/".length();

		path = path.substring(x);

		return urlString.replace(
			"file:" +
				JenkinsResultsParserUtil.getCanonicalPath(dependenciesDir),
			"${dependencies.url}/" + path);
	}

	protected void verifyUrlReaderRead(
			boolean checkCache, int maxRetries, int timeoutMillis,
			UrlReader urlReader)
		throws Exception {

		Mockito.verify(
			urlReader
		).doRead(
			Mockito.eq(checkCache), Mockito.any(), Mockito.any(),
			Mockito.eq(maxRetries), Mockito.any(), Mockito.anyInt(),
			Mockito.eq(timeoutMillis), Mockito.anyString()
		);
	}

	protected List<File> dependenciesDirs = getDependenciesDirs(
		getSimpleClassNames());

	private List<String> _simpleClassNames;

}