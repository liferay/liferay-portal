/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.status.internal.struts;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.PortalMessages;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class StatusStrutsActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpHttpServletRequest();
		_setUpLayoutSetLocalService();
		_setUpRequestDispatcher();
		_setUpStatusStrutsAction();
		_setUpThemeUtil();
	}

	@AfterClass
	public static void tearDownClass() {
		_portalMessagesMockedStatic.close();
		_servletResponseUtilMockedStatic.close();
		_sessionErrorsMockedStatic.close();
		_sessionMessagesMockedStatic.close();
		_themeUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-85590")
	public void testExecuteWithPortalStatusException() throws Exception {
		String htmlStart = StringBundler.concat(
			_HTML_START, "\n  ", RandomTestUtil.randomString());
		String htmlEnd = StringBundler.concat(
			RandomTestUtil.randomString(), "\n ", _HTML_END);

		String expected = StringBundler.concat(
			htmlStart, "<div id=\"content\">", _STATUS_PAGE_CONTENT, "</div>",
			htmlEnd);
		String html = StringBundler.concat(
			htmlStart, "<div id=\"content\">", _CONTENT_INCLUDE, "</div>",
			htmlEnd);

		_sessionErrorsMockedStatic.clearInvocations();

		_testExecute(expected, html);

		_sessionErrorsMockedStatic.verifyNoInteractions();

		_httpServletRequest.setAttribute(
			WebKeys.PORTAL_STATUS_EXCEPTION, new Exception());

		_sessionErrorsMockedStatic.clearInvocations();

		try {
			_testExecute(expected, html);

			_sessionErrorsMockedStatic.verify(
				() -> SessionErrors.remove(
					_httpServletRequest, Exception.class));
		}
		finally {
			_httpServletRequest.removeAttribute(
				WebKeys.PORTAL_STATUS_EXCEPTION);
		}
	}

	@Test
	public void testExecuteWithThemeIncludingContent() throws Exception {
		String htmlStart = StringBundler.concat(
			_HTML_START, "\n  ", RandomTestUtil.randomString());
		String htmlEnd = StringBundler.concat(
			RandomTestUtil.randomString(), "\n ", _HTML_END);

		_testExecute(
			StringBundler.concat(
				htmlStart, "<div id=\"content\">", _STATUS_PAGE_CONTENT,
				"</div>", htmlEnd),
			StringBundler.concat(
				htmlStart, "<div id=\"content\">", _CONTENT_INCLUDE, "</div>",
				htmlEnd));
	}

	@Test
	@TestInfo("LPD-1226")
	public void testExecuteWithThemeWithoutContentInclude() throws Exception {
		Mockito.reset(_log);
		Mockito.when(
			_log.isWarnEnabled()
		).thenReturn(
			true
		);

		String expected = StringBundler.concat(
			_HTML_START, _STATUS_PAGE_CONTENT, _HTML_END);
		String html = StringBundler.concat(
			_HTML_START + RandomTestUtil.randomString(), "<div id=\"",
			RandomTestUtil.randomString(), "\">", RandomTestUtil.randomString(),
			"</div>", RandomTestUtil.randomString() + _HTML_END);

		_testExecute(expected, html);

		Mockito.verify(
			_log
		).isWarnEnabled();

		Mockito.verify(
			_log
		).warn(
			"Replacing all body content because theme " +
				_layoutSet.getThemeId() + " lacks a content include"
		);

		Mockito.reset(_log);

		_testExecute(expected, html);

		Mockito.verify(
			_log
		).isWarnEnabled();

		Mockito.verifyNoMoreInteractions(_log);
	}

	private static void _setUpHttpServletRequest() {
		_httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);
	}

	private static void _setUpLayoutSetLocalService() throws Exception {
		Mockito.when(
			_layoutSet.getTheme()
		).thenReturn(
			_theme
		);

		Mockito.when(
			_layoutSet.getThemeId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_layoutSetLocalService.getLayoutSet(0, false)
		).thenReturn(
			_layoutSet
		);
	}

	private static void _setUpRequestDispatcher() throws Exception {
		Mockito.doAnswer(
			(Answer<Void>)invocation -> {
				PipingServletResponse pipingServletResponse =
					invocation.getArgument(1, PipingServletResponse.class);

				PrintWriter printWriter = pipingServletResponse.getWriter();

				printWriter.print(_STATUS_PAGE_CONTENT);

				return null;
			}
		).when(
			_requestDispatcher
		).include(
			Mockito.eq(_httpServletRequest),
			Mockito.any(PipingServletResponse.class)
		);

		Mockito.when(
			_servletContext.getRequestDispatcher("/status.jsp")
		).thenReturn(
			_requestDispatcher
		);
	}

	private static void _setUpStatusStrutsAction() {
		ReflectionTestUtil.setFieldValue(
			_statusStrutsAction, "_layoutSetLocalService",
			_layoutSetLocalService);
		ReflectionTestUtil.setFieldValue(_statusStrutsAction, "_log", _log);
		ReflectionTestUtil.setFieldValue(
			_statusStrutsAction, "_servletContext", _servletContext);
	}

	private static void _setUpThemeUtil() {
		_themeUtilMockedStatic.when(
			() -> ThemeUtil.replaceBodyContent(
				Mockito.anyString(), Mockito.anyString())
		).thenCallRealMethod();
	}

	private void _testExecute(String expected, String html) throws Exception {
		_servletResponseUtilMockedStatic.clearInvocations();

		_themeUtilMockedStatic.when(
			() -> ThemeUtil.include(
				_httpServletRequest.getServletContext(), _httpServletRequest,
				_httpServletResponse, "portal_normal.ftl", _theme, false)
		).thenAnswer(
			invocation -> {
				if (!html.contains(_CONTENT_INCLUDE)) {
					return html;
				}

				StringBundler sb =
					(StringBundler)_httpServletRequest.getAttribute(
						WebKeys.LAYOUT_CONTENT);

				_httpServletRequest.removeAttribute(WebKeys.LAYOUT_CONTENT);

				return StringUtil.replace(
					html, _CONTENT_INCLUDE, sb.toString());
			}
		);

		_statusStrutsAction.execute(_httpServletRequest, _httpServletResponse);

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		_servletResponseUtilMockedStatic.verify(
			() -> ServletResponseUtil.write(
				Mockito.eq(_httpServletResponse), argumentCaptor.capture()));

		Assert.assertEquals(expected, argumentCaptor.getValue());
	}

	private static final String _CONTENT_INCLUDE = "[$CONTENT_INCLUDE$]";

	private static final String _HTML_END = "</body>\n</html>";

	private static final String _HTML_START = StringBundler.concat(
		"<html>\n <head>\n  <script>var ", RandomTestUtil.randomString(), " = ",
		RandomTestUtil.randomString(), ";</script>\n </head>\n <body>");

	private static final String _STATUS_PAGE_CONTENT =
		RandomTestUtil.randomString();

	private static final Log _log = Mockito.mock(Log.class);

	private static final HttpServletRequest _httpServletRequest =
		new MockHttpServletRequest();
	private static final HttpServletResponse _httpServletResponse =
		Mockito.mock(HttpServletResponse.class);
	private static final LayoutSet _layoutSet = Mockito.mock(LayoutSet.class);
	private static final LayoutSetLocalService _layoutSetLocalService =
		Mockito.mock(LayoutSetLocalService.class);
	private static final MockedStatic<PortalMessages>
		_portalMessagesMockedStatic = Mockito.mockStatic(PortalMessages.class);
	private static final RequestDispatcher _requestDispatcher = Mockito.mock(
		RequestDispatcher.class);
	private static final ServletContext _servletContext = Mockito.mock(
		ServletContext.class);
	private static final MockedStatic<ServletResponseUtil>
		_servletResponseUtilMockedStatic = Mockito.mockStatic(
			ServletResponseUtil.class);
	private static final MockedStatic<SessionErrors>
		_sessionErrorsMockedStatic = Mockito.mockStatic(SessionErrors.class);
	private static final MockedStatic<SessionMessages>
		_sessionMessagesMockedStatic = Mockito.mockStatic(
			SessionMessages.class);
	private static final StatusStrutsAction _statusStrutsAction =
		new StatusStrutsAction();
	private static final Theme _theme = Mockito.mock(Theme.class);
	private static final ThemeDisplay _themeDisplay = Mockito.mock(
		ThemeDisplay.class);
	private static final MockedStatic<ThemeUtil> _themeUtilMockedStatic =
		Mockito.mockStatic(ThemeUtil.class);

}