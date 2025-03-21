/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.company.log.web.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.log4j.Log4JUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileNotFoundException;

import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Hai Yu
 */
@RunWith(Arquillian.class)
public class CompanyLogServletTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_companyAdminUser = UserTestUtil.addCompanyAdminUser(_company);

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_companyAdminUser.getUserId());

		File companyLogDirectory = Log4JUtil.getCompanyLogDirectory(
			_company.getCompanyId());

		File[] files = companyLogDirectory.listFiles();

		Arrays.sort(files, Collections.reverseOrder());

		for (File file : files) {
			_file = file;

			break;
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		try {
			_companyLocalService.deleteCompany(_company);
		}
		finally {
			File parentDirectory = _file.getParentFile();

			_file.delete();

			parentDirectory.delete();
		}

		PrincipalThreadLocal.setName(_originalName);
	}

	@Ignore
	@Test
	public void testDownloadWithFileNotFound() throws Exception {
		String fileName = StringUtil.randomString() + ".log";

		String message = StringBundler.concat(
			"Unable to get file ", fileName, " for company ",
			_company.getCompanyId());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalImpl.class.getName(), LoggerTestUtil.WARN)) {

			_assertHttpServletResponseStatusAndLogEntry(
				_createMockHttpServletRequest(
					StringBundler.concat(
						StringPool.SLASH, _company.getCompanyId(),
						StringPool.SLASH, fileName),
					_companyAdminUser),
				FileNotFoundException.class, message,
				HttpServletResponse.SC_NOT_FOUND);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				FileNotFoundException.class, throwable.getClass());
			Assert.assertEquals(message, throwable.getMessage());
		}
	}

	@Ignore
	@Test
	public void testDownloadWithInvalidPath() throws Exception {
		File companyLogDirectory = Log4JUtil.getCompanyLogDirectory(
			_company.getCompanyId());

		File parentFile = companyLogDirectory.getParentFile();

		_assertHttpServletResponseStatusAndLogEntry(
			_createMockHttpServletRequest(
				StringBundler.concat(
					StringPool.SLASH, _company.getCompanyId(), "/../"),
				_companyAdminUser),
			PrincipalException.class, "Invalid path " + parentFile.getPath(),
			HttpServletResponse.SC_FORBIDDEN);
	}

	@Ignore
	@Test
	public void testDownloadWithNoSuchCompany() throws Exception {
		long companyId = 1;

		_assertHttpServletResponseStatusAndLogEntry(
			_createMockHttpServletRequest(
				StringBundler.concat(
					StringPool.SLASH, companyId, StringPool.SLASH,
					_file.getName()),
				_companyAdminUser),
			NoSuchCompanyException.class,
			"No Company exists with the primary key " + companyId,
			HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void testDownloadWithoutStartAndEnd() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(
				StringBundler.concat(
					StringPool.SLASH, _company.getCompanyId(), StringPool.SLASH,
					_file.getName()),
				_companyAdminUser);

		_servlet.service(mockHttpServletRequest, _mockHttpServletResponse);

		_assertHttpServletResponse(0, _file.length());
	}

	@Ignore
	@Test
	public void testDownloadWithStartGreaterThanOrEqualsToEnd()
		throws Exception {

		_assertDownloadWithInvalidData("0", "0");
		_assertDownloadWithInvalidData("10", "0");
		_assertDownloadWithInvalidData("10", "10");
		_assertDownloadWithInvalidData("2", "1");
	}

	@Test
	public void testDownloadWithStartLessThanEnd() throws Exception {
		_assertDownloadWithValidData("0", "10");
		_assertDownloadWithValidData("1", String.valueOf(Integer.MAX_VALUE));
		_assertDownloadWithValidData("2", "5");
		_assertDownloadWithValidData("2", String.valueOf(_file.length() + 1));
	}

	@Ignore
	@Test
	public void testDownloadWithStartOrEndAsNull() throws Exception {
		_assertDownloadWithInvalidData("", "0");
		_assertDownloadWithInvalidData(String.valueOf(_file.length()), "");
		_assertDownloadWithValidData("", "");
		_assertDownloadWithValidData("3", "");
		_assertDownloadWithValidData(null, null);
	}

	@Ignore
	@Test
	public void testDownloadWithStartOrEndLessThanZero() throws Exception {
		_assertDownloadWithInvalidData("-3", "-2");
		_assertDownloadWithInvalidData("-3", "3");
		_assertDownloadWithInvalidData("3", "-3");
	}

	@Ignore
	@Test
	public void testListWithCompanyAdminUser() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest("/", _companyAdminUser);

		_servlet.service(mockHttpServletRequest, _mockHttpServletResponse);

		Map<Long, JSONObject> jsonObjects = _toCompanyJSONObjects(
			_mockHttpServletResponse.getContentAsString());

		_assertCompanyLogFiles(
			_company, jsonObjects.get(_company.getCompanyId()),
			mockHttpServletRequest);
	}

	@Ignore
	@Test
	public void testListWithCompanyUser() throws Exception {
		User user = UserTestUtil.addUser(_company);

		_assertHttpServletResponseStatusAndLogEntry(
			_createMockHttpServletRequest("/", user),
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must be the company ",
				"administrator to perform the action"),
			HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void testListWithOmniadminUser() throws Exception {
		User omniadminUser = null;

		try {
			omniadminUser = UserTestUtil.addOmniadminUser();

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequest("/", omniadminUser);

			_servlet.service(mockHttpServletRequest, _mockHttpServletResponse);

			Map<Long, JSONObject> jsonObjects = _toCompanyJSONObjects(
				_mockHttpServletResponse.getContentAsString());

			_assertCompanyLogFiles(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				jsonObjects.get(TestPropsValues.getCompanyId()),
				mockHttpServletRequest);
			_assertCompanyLogFiles(
				_company, jsonObjects.get(_company.getCompanyId()),
				mockHttpServletRequest);
		}
		finally {
			if (omniadminUser != null) {
				_userLocalService.deleteUser(omniadminUser);
			}
		}
	}

	@Ignore
	@Test
	public void testUserUnauthenticated() throws Exception {
		_assertHttpServletResponseStatusAndLogEntry(
			_createMockHttpServletRequest("/", (User)null),
			PrincipalException.MustBeAuthenticated.class,
			"User 0 must be authenticated", HttpServletResponse.SC_FORBIDDEN);
	}

	private void _assertCompanyLogFiles(
		Company company, JSONObject jsonObject,
		MockHttpServletRequest mockHttpServletRequest) {

		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON,
			_mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, _mockHttpServletResponse.getStatus());
		Assert.assertEquals(
			company.getCompanyId(), jsonObject.getLong("companyId"));
		Assert.assertEquals(company.getWebId(), jsonObject.getString("webId"));

		File companyLogDirectory = Log4JUtil.getCompanyLogDirectory(
			company.getCompanyId());

		File[] files = companyLogDirectory.listFiles();

		Assert.assertTrue(
			StringBundler.concat(
				"The directory ", companyLogDirectory.getPath(),
				" must have log files"),
			files.length > 0);

		JSONArray companyLogsJSONArray = jsonObject.getJSONArray("companyLogs");

		Arrays.sort(files, Collections.reverseOrder());

		for (int i = 0; i < files.length; i++) {
			JSONObject companyLogJSONObject =
				companyLogsJSONArray.getJSONObject(i);

			Assert.assertEquals(
				files[i].getName(), companyLogJSONObject.getString("fileName"));
			Assert.assertEquals(
				_language.formatStorageSize(
					files[i].length(), mockHttpServletRequest.getLocale()),
				companyLogJSONObject.getString("fileSize"));
		}
	}

	private void _assertDownloadWithInvalidData(
			String startString, String endString)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalImpl.class.getName(), LoggerTestUtil.WARN)) {

			String message =
				"Start and end cannot be less than 0. Start cannot be " +
					"greater than or equal to end.";

			_assertHttpServletResponseStatusAndLogEntry(
				_createMockHttpServletRequest(startString, endString),
				IllegalArgumentException.class, message,
				HttpServletResponse.SC_BAD_REQUEST);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				IllegalArgumentException.class, throwable.getClass());
			Assert.assertEquals(message, throwable.getMessage());
		}
		finally {
			_mockHttpServletResponse.setCommitted(false);

			_mockHttpServletResponse.reset();
		}
	}

	private void _assertDownloadWithValidData(
			String startString, String endString)
		throws Exception {

		try {
			_servlet.service(
				_createMockHttpServletRequest(startString, endString),
				_mockHttpServletResponse);

			long start = 0;

			if (Validator.isNotNull(startString)) {
				start = GetterUtil.getLongStrict(startString);
			}

			long end = _file.length();

			if (Validator.isNotNull(endString) &&
				(GetterUtil.getLongStrict(endString) < end)) {

				end = GetterUtil.getLongStrict(endString);
			}

			if (start != 0) {
				--start;
			}

			_assertHttpServletResponse(start, end);
		}
		finally {
			_mockHttpServletResponse.setCommitted(false);
			_mockHttpServletResponse.reset();
		}
	}

	private void _assertHttpServletResponse(long start, long end)
		throws Exception {

		StringBundler sb = new StringBundler(4);

		sb.append(HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		sb.append("; filename=\"");
		sb.append(_file.getName());
		sb.append("\"");

		Assert.assertEquals(
			sb.toString(),
			_mockHttpServletResponse.getHeader(
				HttpHeaders.CONTENT_DISPOSITION));

		long contentLength = end - start;

		Assert.assertEquals(
			String.valueOf(contentLength),
			_mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_LENGTH));

		Assert.assertEquals(
			_mimeTypes.getContentType(_file),
			_mockHttpServletResponse.getContentType());

		try (FileChannel fileChannel = FileChannel.open(_file.toPath());
			UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			fileChannel.position(start);

			StreamUtil.transfer(
				Channels.newInputStream(fileChannel),
				unsyncByteArrayOutputStream, contentLength);

			Assert.assertEquals(
				unsyncByteArrayOutputStream.toString(),
				_mockHttpServletResponse.getContentAsString());
		}
	}

	private void _assertHttpServletResponseStatusAndLogEntry(
			MockHttpServletRequest mockHttpServletRequest, Class<?> clazz,
			String message, int status)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.company.log.internal.servlet." +
					"CompanyLogServlet",
				LoggerTestUtil.WARN)) {

			_servlet.service(mockHttpServletRequest, _mockHttpServletResponse);

			Assert.assertEquals(status, _mockHttpServletResponse.getStatus());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(clazz, throwable.getClass());
			Assert.assertEquals(message, throwable.getMessage());
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
		String startString, String endString) {

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(
				StringBundler.concat(
					StringPool.SLASH, _company.getCompanyId(), StringPool.SLASH,
					_file.getName()),
				_companyAdminUser);

		mockHttpServletRequest.setParameter("end", endString);
		mockHttpServletRequest.setParameter("start", startString);

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
		String path, User user) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(Method.GET, "/company-log" + path);

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		mockHttpServletRequest.setContextPath("/company-log");
		mockHttpServletRequest.setPathInfo(path);

		return mockHttpServletRequest;
	}

	private Map<Long, JSONObject> _toCompanyJSONObjects(String json)
		throws JSONException {

		Map<Long, JSONObject> jsonObjects = new HashMap<>();

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			jsonObjects.put(jsonObject.getLong("companyId"), jsonObject);
		}

		return jsonObjects;
	}

	private static Company _company;
	private static User _companyAdminUser;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static File _file;
	private static String _originalName;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private MimeTypes _mimeTypes;

	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.portal.company.log.web.internal.servlet.CompanyLogServlet"
	)
	private Servlet _servlet;

	@Inject
	private UserLocalService _userLocalService;

}