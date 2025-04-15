/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.taglib;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.servlet.BrowserMetadata;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.ShutdownUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.text.Format;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Iván Zaera Avellón
 */
public class LiferayGlobalObjectPreAUIDynamicIncludeTest {

	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void test() throws Exception {
		LiferayGlobalObjectPreAUIDynamicInclude
			liferayGlobalObjectPreAUIDynamicInclude =
				new LiferayGlobalObjectPreAUIDynamicInclude();

		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_authToken",
			_mockAuthToken());
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_fastDateFormatFactory",
			_mockFastDateFormatFactory());
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_featureFlagManager",
			_mockFeatureFlagManager());
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_language",
			_mockLanguage());
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_layoutSEOLinkManager",
			_mockLayoutSEOLinkManager());
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_portal", _mockPortal());
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_prefsProps",
			Mockito.mock(PrefsProps.class));
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_staging",
			_mockStaging());
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude,
			"_uploadServletRequestConfigurationProvider",
			Mockito.mock(UploadServletRequestConfigurationProvider.class));

		_mockBrowserSnifferUtil();
		_mockPortalWebResourcesUtil();
		_mockShutdownUtil();
		_mockTime();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		liferayGlobalObjectPreAUIDynamicInclude.include(
			_mockHttpServletRequest(), mockHttpServletResponse,
			StringPool.BLANK);

		Assert.assertEquals(
			_read("liferay_test.js.tpl"),
			StringUtil.trim(mockHttpServletResponse.getContentAsString()));
	}

	private AuthToken _mockAuthToken() {
		AuthToken authToken = Mockito.mock(AuthToken.class);

		Mockito.when(
			authToken.getToken(Mockito.any())
		).thenReturn(
			"LrPaVz44"
		);

		return authToken;
	}

	private void _mockBrowserSnifferUtil() {
		MockedStatic<BrowserSnifferUtil> browserSnifferUtilMockedStatic =
			Mockito.mockStatic(BrowserSnifferUtil.class);

		BrowserMetadata browserMetadata = Mockito.mock(BrowserMetadata.class);

		browserSnifferUtilMockedStatic.when(
			() -> BrowserSnifferUtil.getBrowserMetadata(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			browserMetadata
		);

		browserSnifferUtilMockedStatic.when(
			() -> BrowserSnifferUtil.getRevision(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"42.0"
		);

		browserSnifferUtilMockedStatic.when(
			() -> BrowserSnifferUtil.getVersion(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"42.0"
		);
	}

	private FastDateFormatFactory _mockFastDateFormatFactory() {
		FastDateFormatFactory fastDateFormatFactory = Mockito.mock(
			FastDateFormatFactory.class);

		Format format = Mockito.mock(Format.class);

		Mockito.when(
			format.format(Mockito.any(Date.class))
		).thenReturn(
			"2009/04/23, 00:00:00"
		);

		Mockito.when(
			fastDateFormatFactory.getTime(Mockito.any(Locale.class))
		).thenReturn(
			format
		);

		return fastDateFormatFactory;
	}

	private FeatureFlagManager _mockFeatureFlagManager() {
		FeatureFlagManager featureFlagManager = Mockito.mock(
			FeatureFlagManager.class);

		FeatureFlag featureFlag = Mockito.mock(FeatureFlag.class);

		Mockito.when(
			featureFlag.getKey()
		).thenReturn(
			"LPD-00042"
		);

		Mockito.when(
			featureFlag.isEnabled()
		).thenReturn(
			true
		);

		Mockito.when(
			featureFlagManager.getFeatureFlags(
				Mockito.anyLong(), Mockito.any(Predicate.class))
		).thenReturn(
			Arrays.asList(featureFlag)
		);

		return featureFlagManager;
	}

	private MockHttpServletRequest _mockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _mockThemeDisplay());

		return mockHttpServletRequest;
	}

	private Language _mockLanguage() {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.format(
				Mockito.any(Locale.class), Mockito.anyString(), Mockito.any(),
				Mockito.anyBoolean())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> {
				String key = invocationOnMock.getArgument(1);

				if (key.equals("lang.dir")) {
					return "ltr";
				}

				return key;
			}
		);

		Mockito.when(
			language.getAvailableLocales()
		).thenReturn(
			new HashSet<>(Arrays.asList(LocaleUtil.CANADA, LocaleUtil.FRANCE))
		);

		Mockito.when(
			language.getBCP47LanguageId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"en-US"
		);

		Mockito.when(
			language.getLanguageId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"en_US"
		);

		return language;
	}

	private LayoutSEOLinkManager _mockLayoutSEOLinkManager() throws Exception {
		LayoutSEOLinkManager layoutSEOLinkManager = Mockito.mock(
			LayoutSEOLinkManager.class);

		LayoutSEOLink layoutSEOLink = Mockito.mock(LayoutSEOLink.class);

		Mockito.when(
			layoutSEOLinkManager.getCanonicalLayoutSEOLink(
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())
		).thenReturn(
			layoutSEOLink
		);

		Mockito.when(
			layoutSEOLink.getHref()
		).thenReturn(
			"http://localhost:8080"
		);

		return layoutSEOLinkManager;
	}

	private Portal _mockPortal() throws Exception {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getCurrentURL(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			StringPool.SLASH
		);

		Mockito.when(
			portal.getPathProxy()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			portal.getSiteAdminURL(
				Mockito.any(ThemeDisplay.class), Mockito.anyString(),
				Mockito.isNull())
		).thenReturn(
			"http://localhost:8080/group/guest/~/control_panel/manage?" +
				"p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view"
		);

		Mockito.when(
			portal.getStaticResourceURL(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyLong())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> StringBundler.concat(
				invocationOnMock.getArgument(1), StringPool.QUESTION,
				invocationOnMock.getArgument(2), "&t=",
				(Long)invocationOnMock.getArgument(3))
		);

		return portal;
	}

	private void _mockPortalWebResourcesUtil() {
		MockedStatic<PortalWebResourcesUtil>
			portalWebResourcesUtilMockedStatic = Mockito.mockStatic(
				PortalWebResourcesUtil.class);

		portalWebResourcesUtilMockedStatic.when(
			() -> PortalWebResourcesUtil.getContextPath(Mockito.anyString())
		).thenAnswer(
			(Answer<String>)
				invocationOnMock -> "/o/" + invocationOnMock.getArgument(0)
		);
	}

	private void _mockShutdownUtil() {
		MockedStatic<ShutdownUtil> shutdownUtilMockedStatic =
			Mockito.mockStatic(ShutdownUtil.class);

		shutdownUtilMockedStatic.when(
			ShutdownUtil::isInProcess
		).thenReturn(
			true
		);
	}

	private Staging _mockStaging() {
		Staging staging = Mockito.mock(Staging.class);

		Mockito.when(
			staging.getLiveGroup(Mockito.any(Group.class))
		).thenReturn(
			Mockito.mock(Group.class)
		);

		return staging;
	}

	private ThemeDisplay _mockThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCDNBaseURL()
		).thenReturn(
			"http://localhost:8080"
		);

		Mockito.when(
			themeDisplay.getCDNDynamicResourcesHost()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			themeDisplay.getCDNHost()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			themeDisplay.getDoAsUserId()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			themeDisplay.getLayoutTypePortlet()
		).thenReturn(
			Mockito.mock(LayoutTypePortlet.class)
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.ENGLISH
		);

		Mockito.when(
			themeDisplay.getPathContext()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			themeDisplay.getPathImage()
		).thenReturn(
			"/image"
		);

		Mockito.when(
			themeDisplay.getPathJavaScript()
		).thenReturn(
			"/o/frontend-js-web"
		);

		Mockito.when(
			themeDisplay.getPathMain()
		).thenReturn(
			"c"
		);

		Mockito.when(
			themeDisplay.getPathThemeImages()
		).thenReturn(
			"http://localhost:8080/o/classic-theme/images"
		);

		Mockito.when(
			themeDisplay.getPathThemeRoot()
		).thenReturn(
			"/o/classic-theme"
		);

		Mockito.when(
			themeDisplay.getPortalURL()
		).thenReturn(
			"http://localhost:8080"
		);

		Mockito.when(
			themeDisplay.getRemoteAddr()
		).thenReturn(
			"127.0.0.1"
		);

		Mockito.when(
			themeDisplay.getRemoteHost()
		).thenReturn(
			"127.0.0.1"
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			Mockito.mock(Group.class)
		);

		Mockito.when(
			themeDisplay.getSiteDefaultLocale()
		).thenReturn(
			LocaleUtil.ENGLISH
		);

		Mockito.when(
			themeDisplay.getTimeZone()
		).thenReturn(
			TimeZone.getTimeZone("UTC")
		);

		Mockito.when(
			themeDisplay.getURLControlPanel()
		).thenReturn(
			"/group/control_panel?refererPlid=8"
		);

		Mockito.when(
			themeDisplay.getURLHome()
		).thenReturn(
			"http://localhost:8080/web/guest"
		);

		return themeDisplay;
	}

	private void _mockTime() {
		MockedStatic<Time> timeMockedStatic = Mockito.mockStatic(Time.class);

		timeMockedStatic.when(
			() -> Time.getDate(Mockito.any(Calendar.class))
		).thenReturn(
			new Date(109, 3, 23)
		);
	}

	private String _read(String name) throws Exception {
		try (InputStream inputStream =
				LiferayGlobalObjectPreAUIDynamicIncludeTest.class.
					getResourceAsStream("dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
	}

}