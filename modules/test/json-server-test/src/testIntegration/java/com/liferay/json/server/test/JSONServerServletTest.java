/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class JSONServerServletTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Map<?, ?> applicationMaps = ReflectionTestUtil.getFieldValue(
			_servlet, "_applicationMaps");

		applicationMaps.clear();

		ReflectionTestUtil.invoke(
			_servlet, "_load", new Class<?>[] {String.class, URL.class},
			"fruit",
			JSONServerServletTest.class.getResource("dependencies/fruit.json"));
		ReflectionTestUtil.invoke(
			_servlet, "_load", new Class<?>[] {String.class, URL.class}, "meat",
			JSONServerServletTest.class.getResource("dependencies/meat.json"));
	}

	@Test
	public void testDelete() throws Exception {

		// /meat/beef with cut/Chunk

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContent("{\"cut\": \"Chunk\"}".getBytes());
		mockHttpServletRequest.setMethod(HttpMethods.DELETE);
		mockHttpServletRequest.setPathInfo("/meat/beef");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Map<String, Object> message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals("Done", message.get("message"));

		// /meat/beef with cut/Rib

		mockHttpServletRequest.setContent("{\"cut\": \"Rib\"}".getBytes());

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals("Are you sure?", message.get("message"));

		// /meat/beef with cut/Round

		mockHttpServletRequest.setContent("{\"cut\": \"Round\"}".getBytes());

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals("Item does not exist.", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals("Item does not exist.", message.get("message"));
		}

		// /meat/beef without cut

		mockHttpServletRequest.setContent(null);

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals(
				"The input is invalid.", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"The input is invalid.", message.get("message"));
		}

		// /meat/pork

		mockHttpServletRequest.setPathInfo("/meat/pork");

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				IllegalArgumentException.class, throwable.getClass());

			Assert.assertEquals(
				"Missing ID in path /meat/pork", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"Missing ID in path /meat/pork", message.get("message"));
		}

		// /meat/pork/2

		mockHttpServletRequest.setMethod(HttpMethods.GET);
		mockHttpServletRequest.setPathInfo("/meat/pork/2");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals(2, message.get("id"));
		Assert.assertEquals("Pork Ribs", message.get("name"));

		mockHttpServletRequest.setMethod(HttpMethods.DELETE);

		message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		mockHttpServletRequest.setMethod(HttpMethods.GET);

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals(
				"Unknown ID in path /meat/pork/2", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"Unknown ID in path /meat/pork/2", message.get("message"));
		}
	}

	@Test
	public void testGet() throws Exception {

		// /

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setMethod(HttpMethods.GET);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				IllegalArgumentException.class, throwable.getClass());

			Assert.assertEquals(
				"Missing application name in path null",
				throwable.getMessage());

			Map<String, Object> message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"Missing application name in path null",
				message.get("message"));
		}

		// /fruit

		mockHttpServletRequest.setPathInfo("/fruit");

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				IllegalArgumentException.class, throwable.getClass());

			Assert.assertEquals(
				"Missing model name in path /fruit", throwable.getMessage());

			Map<String, Object> message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"Missing model name in path /fruit", message.get("message"));
		}

		// /fruit/apple with color/green

		mockHttpServletRequest.setContent("{\"color\": \"green\"}".getBytes());
		mockHttpServletRequest.setPathInfo("/fruit/apple");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Map<String, Object> message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals(5, message.get("price"));

		// /fruit/apple with color/red

		mockHttpServletRequest.setContent("{\"color\": \"red\"}".getBytes());

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals(6, message.get("price"));

		// /fruit/apple with color/yellow

		mockHttpServletRequest.setContent("{\"color\": \"yellow\"}".getBytes());

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals(
				"The item is out of stock.", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"The item is out of stock.", message.get("message"));
		}

		// /fruit/apple without color

		mockHttpServletRequest.setContent(null);

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals(
				"The input is invalid.", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"The input is invalid.", message.get("message"));
		}

		// /fruit/orange

		mockHttpServletRequest.setPathInfo("/fruit/orange");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		List<Map<String, Object>> oranges = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), List.class);

		Assert.assertEquals(oranges.toString(), 2, oranges.size());

		Map<String, Object> orange1 = oranges.get(0);

		Assert.assertEquals(1, orange1.get("id"));
		Assert.assertEquals("Navel Orange", orange1.get("name"));

		Map<String, Object> orange2 = oranges.get(1);

		Assert.assertEquals(2, orange2.get("id"));
		Assert.assertEquals("Blood Orange", orange2.get("name"));

		// /fruit/orange/2

		mockHttpServletRequest.setPathInfo("/fruit/orange/2");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Map<String, Object> orange3 = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals(2, orange3.get("id"));
		Assert.assertEquals("Blood Orange", orange3.get("name"));

		// /fruit/orange/3

		mockHttpServletRequest.setPathInfo("/fruit/orange/3");

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals(
				"Unknown ID in path /fruit/orange/3", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"Unknown ID in path /fruit/orange/3", message.get("message"));
		}

		// /fruit/supercalifragilisticexpialidocious/something/to/say/when/you
		// /have/nothing/to/say?a=b&c=d&e=f

		mockHttpServletRequest.setPathInfo(
			"/fruit/supercalifragilisticexpialidocious/something/to/say/when" +
				"/you/have/nothing/to/say");
		mockHttpServletRequest.setQueryString("a=b&c=d&e=f");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		List<Map<String, Object>> maps = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), List.class);

		Assert.assertEquals(maps.toString(), 1, maps.size());

		Map<String, Object> map = maps.get(0);

		Assert.assertEquals(1, map.get("id"));
		Assert.assertEquals("Merlot", map.get("name"));
	}

	@Test
	public void testPost() throws Exception {

		// /fruit/banana with color/green

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContent("{\"color\": \"green\"}".getBytes());
		mockHttpServletRequest.setMethod(HttpMethods.POST);
		mockHttpServletRequest.setPathInfo("/fruit/banana");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals("Unripen banana :(", throwable.getMessage());

			Map<String, Object> message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals("Unripen banana :(", message.get("message"));
		}

		// /fruit/banana with color/yellow

		mockHttpServletRequest.setContent("{\"color\": \"yellow\"}".getBytes());

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Map<String, Object> message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals("Yummy!", message.get("message"));

		// /fruit/banana without color

		mockHttpServletRequest.setContent(null);

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals(
				"The input is invalid.", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"The input is invalid.", message.get("message"));
		}

		// /fruit/raspberry with ID

		mockHttpServletRequest.setContent(
			"{\"id\": 7, \"name\": \"Jewel\"}".getBytes());
		mockHttpServletRequest.setPathInfo("/fruit/raspberry");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		mockHttpServletRequest.setContent(null);
		mockHttpServletRequest.setMethod(HttpMethods.GET);
		mockHttpServletRequest.setPathInfo("/fruit/raspberry/7");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals(7, message.get("id"));
		Assert.assertEquals("Jewel", message.get("name"));

		// /fruit/raspberry without ID

		mockHttpServletRequest.setContent(
			"{\"name\": \"Allen Black\"}".getBytes());
		mockHttpServletRequest.setMethod(HttpMethods.POST);
		mockHttpServletRequest.setPathInfo("/fruit/raspberry");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		mockHttpServletRequest.setContent(null);
		mockHttpServletRequest.setMethod(HttpMethods.GET);
		mockHttpServletRequest.setPathInfo("/fruit/raspberry/8");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals(8, message.get("id"));
		Assert.assertEquals("Allen Black", message.get("name"));
	}

	@Test
	public void testPut() throws Exception {

		// /meat/chicken with cut/Breast

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContent("{\"cut\": \"Breast\"}".getBytes());
		mockHttpServletRequest.setMethod(HttpMethods.PUT);
		mockHttpServletRequest.setPathInfo("/meat/chicken");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Map<String, Object> message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals("Done", message.get("message"));

		// /meat/chicken with cut/Thighs

		mockHttpServletRequest.setContent("{\"cut\": \"Thighs\"}".getBytes());

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals("Item does not exist.", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals("Item does not exist.", message.get("message"));
		}

		// /meat/chicken without cut

		mockHttpServletRequest.setContent(null);

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());

			Assert.assertEquals(
				"The input is invalid.", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"The input is invalid.", message.get("message"));
		}

		// /meat/fish with unknown ID

		mockHttpServletRequest.setContent(
			"{\"id\": 2, \"name\": \"Rainbow Trout\"}".getBytes());
		mockHttpServletRequest.setPathInfo("/meat/fish");

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());
			Assert.assertEquals("Unknown ID 2", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals("Unknown ID 2", message.get("message"));
		}

		// /meat/fish without ID

		mockHttpServletRequest.setContent(
			"{\"name\": \"Rainbow Trout\"}".getBytes());

		mockHttpServletResponse = new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.ERROR)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(ServletException.class, throwable.getClass());
			Assert.assertEquals(
				"Missing ID {name=Rainbow Trout}", throwable.getMessage());

			message = _objectMapper.readValue(
				mockHttpServletResponse.getContentAsString(), HashMap.class);

			Assert.assertEquals(500, message.get("code"));
			Assert.assertEquals(
				"Missing ID {name=Rainbow Trout}", message.get("message"));
		}

		// /meat/fish/1

		mockHttpServletRequest.setContent(
			"{\"id\": 1, \"name\": \"Rainbow Trout\"}".getBytes());
		mockHttpServletRequest.setPathInfo("/meat/fish");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		mockHttpServletRequest.setMethod(HttpMethods.GET);
		mockHttpServletRequest.setPathInfo("/meat/fish/1");

		mockHttpServletResponse = new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		message = _objectMapper.readValue(
			mockHttpServletResponse.getContentAsString(), HashMap.class);

		Assert.assertEquals(1, message.get("id"));
		Assert.assertEquals("Rainbow Trout", message.get("name"));
	}

	private static final String _CLASS_NAME =
		"com.liferay.json.server.internal.servlet.JSONServerServlet";

	@Inject(filter = "osgi.http.whiteboard.servlet.name=" + _CLASS_NAME)
	private static Servlet _servlet;

	private final ObjectMapper _objectMapper = new ObjectMapper();

}