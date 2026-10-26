/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author João Victor Alves
 */
@RunWith(Arquillian.class)
public class FindEntryStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_PID);
	}

	@Test
	public void testExecute() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"allowedDomains", new String[] {"*.google.com"}
			).put(
				"securityMode", "domain"
			).build());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.util.PortalImpl", LoggerTestUtil.WARN)) {

			Assert.assertNotEquals(
				"https://google.co", _execute("https://google.co"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Redirect URL https://google.co is not allowed",
				logEntry.getMessage());
		}

		Assert.assertEquals(
			"https://mail.google.com", _execute("https://mail.google.com"));
	}

	private String _execute(String noSuchEntryRedirect) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"noSuchEntryRedirect", noSuchEntryRedirect);
		mockHttpServletRequest.setRequestURI(
			"/" + RandomTestUtil.randomString());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_editInfoItemStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse.getRedirectedUrl();
	}

	private static final String _PID =
		"com.liferay.redirect.internal.configuration.RedirectURLConfiguration";

	@DeleteAfterTestRun
	private static Group _group;

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.struts.FindEntryStrutsAction"
	)
	private StrutsAction _editInfoItemStrutsAction;

}