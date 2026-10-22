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

import java.lang.reflect.Method;

import java.net.HttpURLConnection;
import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hamcrest.CoreMatchers;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;

import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
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
		BodyURLReader.setJSONArrayInstance(
			BodyURLReader.newJSONArrayBodyURLReader());

		BodyURLReader.setJSONObjectInstance(
			BodyURLReader.newJSONObjectBodyURLReader());

		BodyURLReader.setTextInstance(BodyURLReader.newTextBodyURLReader());

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

		StreamURLReader.setInstance(new StreamURLReader());

		TestClassFactory.clear();
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

	protected List<BaseURLReader<?>> getBaseURLReaders() {
		return _baseURLReaders;
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

	protected StreamURLReader getStreamURLReader() {
		return _streamURLReader;
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

	protected HttpURLConnection mockURLConnection(
			String content, int responseCode)
		throws IOException {

		HttpURLConnection httpURLConnection = Mockito.mock(
			HttpURLConnection.class);

		Mockito.doReturn(
			new ByteArrayInputStream(content.getBytes())
		).when(
			httpURLConnection
		).getInputStream();

		Mockito.doReturn(
			responseCode
		).when(
			httpURLConnection
		).getResponseCode();

		return httpURLConnection;
	}

	protected void mockURLReaders() {
		BodyURLReader<JSONArray> jsonArrayBodyURLReader = Mockito.spy(
			BodyURLReader.newJSONArrayBodyURLReader());

		BodyURLReader.setJSONArrayInstance(jsonArrayBodyURLReader);

		BodyURLReader<JSONObject> jsonObjectBodyURLReader = Mockito.spy(
			BodyURLReader.newJSONObjectBodyURLReader());

		BodyURLReader.setJSONObjectInstance(jsonObjectBodyURLReader);

		_streamURLReader = Mockito.spy(new StreamURLReader());

		StreamURLReader.setInstance(_streamURLReader);

		BodyURLReader<String> textBodyURLReader = Mockito.spy(
			BodyURLReader.newTextBodyURLReader());

		BodyURLReader.setTextInstance(textBodyURLReader);

		_baseURLReaders = Arrays.asList(
			jsonArrayBodyURLReader, jsonObjectBodyURLReader, _streamURLReader,
			textBodyURLReader);

		for (BaseURLReader<?> baseURLReader : _baseURLReaders) {
			try {
				Mockito.doAnswer(
					invocation -> {
						String url = invocation.getArgument(6);

						throw new AssertionError(
							"No output set for URL: " + url);
					}
				).when(
					baseURLReader
				).openURLConnection(
					Mockito.any(), Mockito.anyBoolean(), Mockito.any(),
					Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(),
					Mockito.any()
				);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}

			Mockito.doNothing(
			).when(
				baseURLReader
			).sleep(
				Mockito.anyLong()
			);
		}
	}

	protected Map<String, String> newParameters(String... namesAndValues) {
		Map<String, String> parameters = new LinkedHashMap<>();

		for (int i = 0; i < namesAndValues.length; i = i + 2) {
			parameters.put(namesAndValues[i], namesAndValues[i + 1]);
		}

		return parameters;
	}

	protected String read(File file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file.toURI())));
	}

	protected String read(File dir, String fileName) throws IOException {
		return read(new File(dir, fileName));
	}

	protected void setShellCommandExitValue(
			String command, Shell shell, int exitValue)
		throws Exception {

		Mockito.doReturn(
			new Shell.ExecutionResult(exitValue, "", "")
		).when(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(executionRequest, command))
		);
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

	protected void setURLReaderException(IOException ioException, String url)
		throws Exception {

		for (BaseURLReader<?> baseURLReader : _baseURLReaders) {
			Mockito.doThrow(
				ioException
			).when(
				baseURLReader
			).openURLConnection(
				Mockito.any(), Mockito.anyBoolean(), Mockito.any(),
				Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.argThat(
					readURL -> (readURL != null) && readURL.contains(url))
			);
		}
	}

	protected void setURLReaderOutput(
			long delayMillis, String standardOut, String url)
		throws Exception {

		for (BaseURLReader<?> baseURLReader : _baseURLReaders) {
			Mockito.doAnswer(
				invocation -> {
					JenkinsResultsParserUtil.sleep(delayMillis);

					return mockURLConnection(standardOut, 200);
				}
			).when(
				baseURLReader
			).openURLConnection(
				Mockito.any(), Mockito.anyBoolean(), Mockito.any(),
				Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.argThat(
					readURL -> (readURL != null) && readURL.contains(url))
			);
		}
	}

	protected void setURLReaderOutput(String standardOut, String url)
		throws Exception {

		setURLReaderOutput(0, standardOut, url);
	}

	protected void setURLReaderOutputAfterException(
			IOException ioException, String standardOut, String url)
		throws Exception {

		for (BaseURLReader<?> baseURLReader : _baseURLReaders) {
			Mockito.doThrow(
				ioException
			).doAnswer(
				invocation -> mockURLConnection(standardOut, 200)
			).when(
				baseURLReader
			).openURLConnection(
				Mockito.any(), Mockito.anyBoolean(), Mockito.any(),
				Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.argThat(
					readURL -> (readURL != null) && readURL.contains(url))
			);
		}
	}

	protected void setURLReaderResponseCode(int responseCode, String url)
		throws Exception {

		for (BaseURLReader<?> baseURLReader : _baseURLReaders) {
			Mockito.doAnswer(
				invocation -> _mockURLConnection(responseCode)
			).when(
				baseURLReader
			).openURLConnection(
				Mockito.any(), Mockito.anyBoolean(), Mockito.any(),
				Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.argThat(
					readURL -> (readURL != null) && readURL.contains(url))
			);
		}
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

	protected void verifyURLReaderAttemptsCount(int expectedCount, String url) {
		int count = 0;

		for (BaseURLReader<?> baseURLReader : _baseURLReaders) {
			MockingDetails mockingDetails = Mockito.mockingDetails(
				baseURLReader);

			for (Invocation invocation : mockingDetails.getInvocations()) {
				Method method = invocation.getMethod();

				if (!method.equals(_openURLConnectionMethod)) {
					continue;
				}

				String readURL = invocation.getArgument(6);

				if ((readURL == null) || !readURL.contains(url)) {
					continue;
				}

				count++;
			}
		}

		testEquals(expectedCount, count);
	}

	protected void verifyURLReaderRead(
		boolean checkCache, int maxRetries, int timeoutMillis) {

		int count = 0;

		for (BaseURLReader<?> baseURLReader : _baseURLReaders) {
			MockingDetails mockingDetails = Mockito.mockingDetails(
				baseURLReader);

			for (Invocation invocation : mockingDetails.getInvocations()) {
				Method method = invocation.getMethod();

				if (!method.equals(_readMethod)) {
					continue;
				}

				boolean invocationCheckCache = invocation.getArgument(0);

				if (invocationCheckCache != checkCache) {
					continue;
				}

				int invocationMaxRetries = invocation.getArgument(4);

				if (invocationMaxRetries != maxRetries) {
					continue;
				}

				int invocationTimeout = invocation.getArgument(7);

				if (invocationTimeout != timeoutMillis) {
					continue;
				}

				count++;
			}
		}

		testEquals(1, count);
	}

	protected void verifyURLReaderSleepDurations(List<Long> expectedDurations) {
		List<Long> durations = new ArrayList<>();

		for (BaseURLReader<?> baseURLReader : _baseURLReaders) {
			MockingDetails mockingDetails = Mockito.mockingDetails(
				baseURLReader);

			for (Invocation invocation : mockingDetails.getInvocations()) {
				Method method = invocation.getMethod();

				if (!method.equals(_sleepMethod)) {
					continue;
				}

				durations.add(invocation.getArgument(0));
			}
		}

		testEquals(expectedDurations, durations);
	}

	protected List<File> dependenciesDirs = getDependenciesDirs(
		getSimpleClassNames());

	private static Method _getOpenURLConnectionMethod() {
		try {
			return BaseURLReader.class.getDeclaredMethod(
				"openURLConnection", String.class, boolean.class,
				JenkinsResultsParserUtil.HttpRequestMethod.class, String.class,
				boolean.class, int.class, String.class);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new ExceptionInInitializerError(noSuchMethodException);
		}
	}

	private static Method _getReadMethod() {
		try {
			return BaseURLReader.class.getDeclaredMethod(
				"read", boolean.class, boolean.class,
				JenkinsResultsParserUtil.HTTPAuthorization.class,
				JenkinsResultsParserUtil.HttpRequestMethod.class, int.class,
				String.class, int.class, int.class, String.class);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new ExceptionInInitializerError(noSuchMethodException);
		}
	}

	private static Method _getSleepMethod() {
		try {
			return BaseURLReader.class.getDeclaredMethod("sleep", long.class);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new ExceptionInInitializerError(noSuchMethodException);
		}
	}

	private HttpURLConnection _mockURLConnection(int responseCode)
		throws IOException {

		HttpURLConnection httpURLConnection = Mockito.mock(
			HttpURLConnection.class);

		Mockito.doThrow(
			new IOException(
				"Server returned HTTP response code: " + responseCode)
		).when(
			httpURLConnection
		).getInputStream();

		Mockito.doReturn(
			responseCode
		).when(
			httpURLConnection
		).getResponseCode();

		return httpURLConnection;
	}

	private static final Method _openURLConnectionMethod =
		_getOpenURLConnectionMethod();
	private static final Method _readMethod = _getReadMethod();
	private static final Method _sleepMethod = _getSleepMethod();

	private List<BaseURLReader<?>> _baseURLReaders;
	private List<String> _simpleClassNames;
	private StreamURLReader _streamURLReader;

}