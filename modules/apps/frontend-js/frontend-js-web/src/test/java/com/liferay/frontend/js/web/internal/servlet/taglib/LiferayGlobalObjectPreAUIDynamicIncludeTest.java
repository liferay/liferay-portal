/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.taglib;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.frontend.js.web.internal.configuration.LiferayGlobalObjectConfiguration;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
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
import com.liferay.portal.kernel.util.ListUtil;
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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Predicate;

import org.junit.After;
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

	@After
	public void tearDown() {
		_browserSnifferUtilMockedStatic.close();
		_contentSecurityPolicyNonceProviderUtilMockedStatic.close();
		_portalWebResourcesUtilMockedStatic.close();
		_shutdownUtilMockedStatic.close();
		_timeMockedStatic.close();
	}

	@Test
	public void test() throws Exception {
		LiferayGlobalObjectPreAUIDynamicInclude
			liferayGlobalObjectPreAUIDynamicInclude =
				new LiferayGlobalObjectPreAUIDynamicInclude();

		_setUpMocks(liferayGlobalObjectPreAUIDynamicInclude);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		liferayGlobalObjectPreAUIDynamicInclude.include(
			_mockHttpServletRequest(), mockHttpServletResponse,
			StringPool.BLANK);

		Assert.assertEquals(
			_read("liferay_test.js.tpl"),
			StringUtil.trim(mockHttpServletResponse.getContentAsString()));
	}

	@Test
	public void testDisableGetRemoteMethodsConfiguration() throws Exception {
		LiferayGlobalObjectPreAUIDynamicInclude
			liferayGlobalObjectPreAUIDynamicInclude =
				new LiferayGlobalObjectPreAUIDynamicInclude();

		_setUpMocks(liferayGlobalObjectPreAUIDynamicInclude);

		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_configurationProvider",
			_mockConfigurationProvider(true));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		liferayGlobalObjectPreAUIDynamicInclude.include(
			_mockHttpServletRequest(), mockHttpServletResponse,
			StringPool.BLANK);

		String contentAsString = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			contentAsString.contains(
				"getRemoteAddr: () => {throw new Error('Method getRemoteAddr " +
					"has been disabled in Instance Settings');},\n"));
		Assert.assertTrue(
			contentAsString.contains(
				"getRemoteHost: () => {throw new Error('Method getRemoteHost " +
					"has been disabled in Instance Settings');},\n"));
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

	private ConfigurationProvider _mockConfigurationProvider(
			boolean disableGetRemoteMethods)
		throws Exception {

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		LiferayGlobalObjectConfiguration liferayGlobalObjectConfiguration =
			Mockito.mock(LiferayGlobalObjectConfiguration.class);

		Mockito.when(
			liferayGlobalObjectConfiguration.disableGetRemoteMethods()
		).thenReturn(
			disableGetRemoteMethods
		);

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				Mockito.any(), Mockito.anyLong())
		).thenReturn(
			liferayGlobalObjectConfiguration
		);

		return configurationProvider;
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

	private FeatureFlag _mockFeatureFlag(boolean enabled, String key) {
		FeatureFlag featureFlag = Mockito.mock(FeatureFlag.class);

		Mockito.when(
			featureFlag.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			featureFlag.isEnabled()
		).thenReturn(
			enabled
		);

		return featureFlag;
	}

	private FeatureFlagManager _mockFeatureFlagManager() {
		FeatureFlagManager featureFlagManager = Mockito.mock(
			FeatureFlagManager.class);

		List<FeatureFlag> featureFlags = Arrays.asList(
			_mockFeatureFlag(true, "LPD-00042"),
			_mockFeatureFlag(false, "LPD-00043"));

		Mockito.when(
			featureFlagManager.getFeatureFlags(
				Mockito.anyLong(), Mockito.any(Predicate.class))
		).thenAnswer(
			invocation -> ListUtil.filter(
				featureFlags, invocation.getArgument(1))
		);

		return featureFlagManager;
	}

	private HttpServletRequest _mockHttpServletRequest() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _mockThemeDisplay());

		return httpServletRequest;
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
			themeDisplay.getPathThemeEmptyStatesSpritemap()
		).thenReturn(
			"http://localhost:8080/o/classic-theme/images/clay/empty_states.svg"
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

	private String _read(String name) throws Exception {
		try (InputStream inputStream =
				LiferayGlobalObjectPreAUIDynamicIncludeTest.class.
					getResourceAsStream("dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
	}

	private void _setUpMocks(
			LiferayGlobalObjectPreAUIDynamicInclude
				liferayGlobalObjectPreAUIDynamicInclude)
		throws Exception {

		// BrowserSnifferUtil

		_browserSnifferUtilMockedStatic.when(
			() -> BrowserSnifferUtil.getBrowserMetadata(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			Mockito.mock(BrowserMetadata.class)
		);

		_browserSnifferUtilMockedStatic.when(
			() -> BrowserSnifferUtil.getRevision(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"42.0"
		);

		_browserSnifferUtilMockedStatic.when(
			() -> BrowserSnifferUtil.getVersion(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"42.0"
		);

		// ContentSecurityPolicyNonceProviderUtil

		_contentSecurityPolicyNonceProviderUtilMockedStatic.when(
			() -> ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				Mockito.any())
		).thenReturn(
			StringPool.BLANK
		);

		// PortalWebResourcesUtil

		_portalWebResourcesUtilMockedStatic.when(
			() -> PortalWebResourcesUtil.getContextPath(Mockito.anyString())
		).thenAnswer(
			(Answer<String>)
				invocationOnMock -> "/o/" + invocationOnMock.getArgument(0)
		);

		// ShutdownUtil

		_shutdownUtilMockedStatic.when(
			ShutdownUtil::isInProcess
		).thenReturn(
			true
		);

		// Time

		_timeMockedStatic.when(
			() -> Time.getDate(Mockito.any(Calendar.class))
		).thenReturn(
			new Date(109, 3, 23)
		);

		//  Populate liferayGlobalObjectPreAUIDynamicInclude

		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_authToken",
			_mockAuthToken());
		ReflectionTestUtil.setFieldValue(
			liferayGlobalObjectPreAUIDynamicInclude, "_configurationProvider",
			_mockConfigurationProvider(false));
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
	}

	private final MockedStatic<BrowserSnifferUtil>
		_browserSnifferUtilMockedStatic = Mockito.mockStatic(
			BrowserSnifferUtil.class);
	private final MockedStatic<ContentSecurityPolicyNonceProviderUtil>
		_contentSecurityPolicyNonceProviderUtilMockedStatic =
			Mockito.mockStatic(ContentSecurityPolicyNonceProviderUtil.class);
	private final MockedStatic<PortalWebResourcesUtil>
		_portalWebResourcesUtilMockedStatic = Mockito.mockStatic(
			PortalWebResourcesUtil.class);
	private final MockedStatic<ShutdownUtil> _shutdownUtilMockedStatic =
		Mockito.mockStatic(ShutdownUtil.class);
	private final MockedStatic<Time> _timeMockedStatic = Mockito.mockStatic(
		Time.class);

}