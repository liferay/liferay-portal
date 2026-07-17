/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.lang.reflect.Field;

import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;

import org.mockito.Mockito;

/**
 * @author Peter Yoo
 */
public class Test {

	@Before
	public void setUp() throws Exception {
		JenkinsResultsParserUtil.clearCache();
	}

	@After
	public void tearDown() {
		BuildDatabaseUtil.clearBuildDatabases();

		Environment.setInstance(new Environment());

		Shell.setInstance(new Shell());

		UrlReader.setInstance(new UrlReader());

		setDeclaredFieldValue(
			JenkinsResultsParserUtil.class, null, "_ciNode", null);
	}

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	protected static Object getDeclaredFieldValue(
		Class<?> clazz, Object object, String fieldName) {

		try {
			Field field = clazz.getDeclaredField(fieldName);

			field.setAccessible(true);

			return field.get(object);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	protected static List<File> getDependenciesDirs(
		List<String> simpleClassNames) {

		List<File> dirs = new ArrayList<>(simpleClassNames.size());

		for (String simpleClassName : simpleClassNames) {
			dirs.add(
				new File("src/test/resources/dependencies/" + simpleClassName));
		}

		return dirs;
	}

	protected static void setDeclaredFieldValue(
		Class<?> clazz, Object object, String fieldName, Object value) {

		try {
			Field field = clazz.getDeclaredField(fieldName);

			field.setAccessible(true);

			field.set(object, value);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
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

	protected Environment mockEnvironment(
		Map<String, String> environmentValues) {

		Environment environment = Mockito.mock(Environment.class);

		Mockito.doAnswer(
			invocation -> environmentValues.get(invocation.getArgument(0))
		).when(
			environment
		).doGet(
			Mockito.anyString()
		);

		Mockito.doReturn(
			environmentValues
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

	protected void setUrlReaderOutput(
			String standardOut, String url, UrlReader urlReader)
		throws Exception {

		Mockito.doAnswer(
			invocation -> new ByteArrayInputStream(standardOut.getBytes())
		).when(
			urlReader
		).doRead(
			Mockito.anyBoolean(), Mockito.any(), Mockito.any(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.argThat(
				readURL -> (readURL != null) && readURL.contains(url))
		);
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

	protected List<File> dependenciesDirs = getDependenciesDirs(
		getSimpleClassNames());

	private List<String> _simpleClassNames;

}