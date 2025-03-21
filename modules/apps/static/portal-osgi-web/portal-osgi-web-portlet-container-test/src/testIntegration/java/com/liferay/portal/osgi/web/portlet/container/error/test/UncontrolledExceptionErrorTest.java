/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.error.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.osgi.web.portlet.container.test.BasePortletContainerTestCase;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UncontrolledExceptionErrorTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = _userLocalService.getGuestUser(group.getCompanyId());
	}

	@Test
	public void testUncontrolledExceptionCustomizedError() throws Exception {
		UncontrolledExceptionErrorPortlet uncontrolledExceptionErrorPortlet =
			_setUpUncontrolledExceptionErrorPortlet();

		String expectedErrorMessage = RandomTestUtil.randomString();

		registerService(
			ResourceBundle.class,
			new ResourceBundle() {

				@Override
				public Enumeration<String> getKeys() {
					return Collections.enumeration(
						Arrays.asList(
							"is-temporarily-unavailable[" + _PORTLET_NAME +
								"]"));
				}

				@Override
				protected Object handleGetObject(String key) {
					if (Objects.equals(
							key,
							"is-temporarily-unavailable[" + _PORTLET_NAME +
								"]")) {

						return expectedErrorMessage;
					}

					return null;
				}

			},
			HashMapDictionaryBuilder.<String, Object>put(
				"language.id", LocaleUtil.toLanguageId(_user.getLocale())
			).build());

		_testUncontrolledExceptionError(
			uncontrolledExceptionErrorPortlet, expectedErrorMessage);
	}

	@Test
	public void testUncontrolledExceptionError() throws Exception {
		UncontrolledExceptionErrorPortlet uncontrolledExceptionErrorPortlet =
			_setUpUncontrolledExceptionErrorPortlet();

		_testUncontrolledExceptionError(
			uncontrolledExceptionErrorPortlet,
			_language.format(
				_user.getLocale(), "is-temporarily-unavailable",
				uncontrolledExceptionErrorPortlet.getTitle()));
	}

	private UncontrolledExceptionErrorPortlet
			_setUpUncontrolledExceptionErrorPortlet()
		throws Exception {

		UncontrolledExceptionErrorPortlet uncontrolledExceptionErrorPortlet =
			new UncontrolledExceptionErrorPortlet();

		setUpPortlet(
			uncontrolledExceptionErrorPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.use-default-template",
				Boolean.TRUE.toString()
			).put(
				"jakarta.portlet.display-name",
				"Uncontrolled Exception Error Portlet"
			).put(
				"jakarta.portlet.init-param.view-template",
				"/error_test/view.jsp"
			).put(
				"jakarta.portlet.name", _PORTLET_NAME
			).put(
				"jakarta.portlet.resource-bundle", "content.Language"
			).build(),
			_PORTLET_NAME);

		return uncontrolledExceptionErrorPortlet;
	}

	private void _testUncontrolledExceptionError(
			UncontrolledExceptionErrorPortlet uncontrolledExceptionErrorPortlet,
			String expectedError)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.html.portal.render_portlet_jsp",
				LoggerTestUtil.ERROR)) {

			PortletContainerTestUtil.Response response =
				PortletContainerTestUtil.request(
					PortletURLBuilder.create(
						_portletURLFactory.create(
							PortletContainerTestUtil.getHttpServletRequest(
								group, layout),
							_PORTLET_NAME, layout.getPlid(),
							PortletRequest.RENDER_PHASE)
					).buildString());

			Assert.assertTrue(
				uncontrolledExceptionErrorPortlet.isCalledRender());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals("null", logEntry.getMessage());

			String responseBodyHTML = response.getBody();

			Assert.assertTrue(
				"Page body should contain error message : " + expectedError,
				responseBodyHTML.contains(expectedError));
		}
	}

	private static final String _PORTLET_NAME =
		"com_liferay_portal_portlet_container_error_test_" +
			"UncontrolledExceptionErrorPortlet";

	@Inject
	private Language _language;

	@Inject
	private PortletURLFactory _portletURLFactory;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	private class UncontrolledExceptionErrorPortlet extends MVCPortlet {

		public boolean isCalledRender() {
			return _calledRender;
		}

		@Override
		public void render(
				RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

			super.render(renderRequest, renderResponse);

			_calledRender = true;

			throw new RuntimeException();
		}

		protected String getTitle() {
			return _title;
		}

		@Override
		protected String getTitle(RenderRequest renderRequest) {
			return getTitle();
		}

		private UncontrolledExceptionErrorPortlet() {
			_title = RandomTestUtil.randomString();
		}

		private boolean _calledRender;
		private final String _title;

	}

}