/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.theme;

import com.liferay.layout.utility.page.kernel.provider.util.LayoutUtilityPageEntryLayoutProviderUtil;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.taglib.util.OutputData;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;

import java.io.IOException;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockBodyContent;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.mock.web.MockPageContext;

/**
 * @author Lourdes Fernández Besada
 */
public class MetaTagsTagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_layoutUtilityPageEntryLayoutProviderUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_setUpLanguageUtil();

		_layout = Mockito.mock(Layout.class);

		_themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_themeDisplay.getLayout()
		).thenReturn(
			_layout
		);

		Mockito.when(
			_themeDisplay.getLanguageId()
		).thenReturn(
			LocaleUtil.toLanguageId(LocaleUtil.SPAIN)
		);
	}

	@Test
	@TestInfo("LPD-45944")
	public void testMetaTagsTagDescription() throws Exception {
		_testDescriptionMetaTagsTag(
			false, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
		_testDescriptionMetaTagsTag(false, RandomTestUtil.randomString(), null);
		_testDescriptionMetaTagsTag(false, null, RandomTestUtil.randomString());
		_testDescriptionMetaTagsTag(
			true, RandomTestUtil.randomString(), RandomTestUtil.randomString());
		_testDescriptionMetaTagsTag(true, RandomTestUtil.randomString(), null);
		_testDescriptionMetaTagsTag(true, null, RandomTestUtil.randomString());
	}

	@Test
	public void testMetaTagsTagKeywords() throws Exception {
		_testKeywordsMetaTagsTag(
			false, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
		_testKeywordsMetaTagsTag(false, RandomTestUtil.randomString(), null);
		_testKeywordsMetaTagsTag(false, null, RandomTestUtil.randomString());
		_testKeywordsMetaTagsTag(
			true, RandomTestUtil.randomString(), RandomTestUtil.randomString());
		_testKeywordsMetaTagsTag(true, RandomTestUtil.randomString(), null);
		_testKeywordsMetaTagsTag(true, null, RandomTestUtil.randomString());
	}

	@Test
	public void testMetaTagsTagResponseStatus() throws Exception {
		_testMetaTagsTagResponseStatus(
			RandomTestUtil.randomString(),
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		_testMetaTagsTagResponseStatus(
			RandomTestUtil.randomString(), HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void testMetaTagsTagRobots() throws Exception {
		_testRobotsMetaTagsTag(null, null, null, null);

		String pageRobotsRequestAttribute = "noindex, nofollow";

		_testRobotsMetaTagsTag(
			pageRobotsRequestAttribute, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), pageRobotsRequestAttribute);

		String robots = RandomTestUtil.randomString();

		_testRobotsMetaTagsTag(
			robots, RandomTestUtil.randomString(), robots, null);
		_testRobotsMetaTagsTag(robots, robots, null, null);
	}

	private ListMergeable<String> _getListMergeable(String value) {
		ListMergeable<String> listMergeable = null;

		if (Validator.isNotNull(value)) {
			listMergeable = new ListMergeable<>();

			listMergeable.add(value);
		}

		return listMergeable;
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.getLanguageId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			LocaleUtil.toLanguageId(LocaleUtil.SPAIN)
		);

		Mockito.when(
			language.getLanguageId(Mockito.any(Locale.class))
		).thenReturn(
			LocaleUtil.toLanguageId(LocaleUtil.US)
		);

		Mockito.when(
			language.getLanguageId((Locale)null)
		).thenReturn(
			LocaleUtil.toLanguageId(LocaleUtil.US)
		);

		languageUtil.setLanguage(language);
	}

	private void _setUpPageContext(
		String robots, String pageDescription, String pageKeywords,
		int status) {

		_unsyncStringWriter = new UnsyncStringWriter();

		final JspWriter jspWriter = new MockJspWriter(_unsyncStringWriter);

		_pageContext = new MockPageContext() {

			@Override
			public JspWriter getOut() {
				return jspWriter;
			}

			@Override
			public ServletRequest getRequest() {
				return new MockHttpServletRequest() {

					@Override
					public Object getAttribute(String name) {
						if (WebKeys.PAGE_DESCRIPTION.equals(name)) {
							return _getListMergeable(pageDescription);
						}

						if (WebKeys.PAGE_KEYWORDS.equals(name)) {
							return _getListMergeable(pageKeywords);
						}

						if (WebKeys.PAGE_ROBOTS.equals(name)) {
							return robots;
						}

						if (WebKeys.THEME_DISPLAY.equals(name)) {
							return _themeDisplay;
						}

						if (!WebKeys.OUTPUT_DATA.equals(name)) {
							return null;
						}

						return new OutputData() {

							@Override
							public void addDataSB(
								String outputKey, String webKey,
								StringBundler sb) {

								try {
									jspWriter.write(sb.toString());
								}
								catch (IOException ioException) {
									ReflectionUtil.throwException(ioException);
								}
							}

						};
					}

				};
			}

			@Override
			public ServletResponse getResponse() {
				HttpServletResponse httpServletResponse =
					new MockHttpServletResponse();

				httpServletResponse.setStatus(status);

				return httpServletResponse;
			}

			@Override
			public BodyContent pushBody() {
				final UnsyncStringWriter unsyncStringWriter =
					new UnsyncStringWriter();

				return new MockBodyContent(
					StringPool.BLANK, unsyncStringWriter) {

					@Override
					public String getString() {
						return unsyncStringWriter.toString();
					}

				};
			}

		};
	}

	private void _testDescriptionMetaTagsTag(
			boolean defaultLanguage, String layoutDescription,
			String pageDescription)
		throws Exception {

		String defaultDescription = null;
		String localizedDescription = null;
		String metaLang = LocaleUtil.toW3cLanguageId(LocaleUtil.SPAIN);

		if (defaultLanguage && Validator.isNotNull(layoutDescription)) {
			defaultDescription = layoutDescription;
			metaLang = LocaleUtil.toW3cLanguageId(LocaleUtil.US);
		}
		else {
			localizedDescription = layoutDescription;
		}

		Mockito.when(
			_layout.getDescription(Mockito.anyString())
		).thenReturn(
			defaultDescription
		);

		Mockito.when(
			_layout.getDescription(Mockito.anyString(), Mockito.anyBoolean())
		).thenReturn(
			localizedDescription
		);

		_setUpPageContext(
			null, pageDescription, null, HttpServletResponse.SC_OK);

		String metaDescription = StringPool.BLANK;

		if (Validator.isNotNull(layoutDescription) &&
			Validator.isNotNull(pageDescription)) {

			metaDescription = pageDescription + ". " + layoutDescription;
		}
		else if (Validator.isNotNull(layoutDescription)) {
			metaDescription = layoutDescription;
		}
		else if (Validator.isNotNull(pageDescription)) {
			metaDescription = pageDescription;
		}

		_testMetaTagsTag(metaDescription, metaLang, "description");
	}

	private void _testKeywordsMetaTagsTag(
			boolean defaultLanguage, String layoutKeywords, String pageKeywords)
		throws Exception {

		String defaultKeywords = null;
		String localizedKeywords = null;
		String metaLang = LocaleUtil.toW3cLanguageId(LocaleUtil.SPAIN);

		if (defaultLanguage && Validator.isNotNull(layoutKeywords)) {
			defaultKeywords = layoutKeywords;
			metaLang = LocaleUtil.toW3cLanguageId(LocaleUtil.US);
		}
		else {
			localizedKeywords = layoutKeywords;
		}

		Mockito.when(
			_layout.getKeywords(Mockito.anyString())
		).thenReturn(
			defaultKeywords
		);

		Mockito.when(
			_layout.getKeywords(Mockito.anyString(), Mockito.anyBoolean())
		).thenReturn(
			localizedKeywords
		);

		_setUpPageContext(null, null, pageKeywords, HttpServletResponse.SC_OK);

		String metaKeywords = StringPool.BLANK;

		if (Validator.isNotNull(layoutKeywords) &&
			Validator.isNotNull(pageKeywords)) {

			metaKeywords = pageKeywords + ", " + layoutKeywords;
		}
		else if (Validator.isNotNull(layoutKeywords)) {
			metaKeywords = layoutKeywords;
		}
		else if (Validator.isNotNull(pageKeywords)) {
			metaKeywords = pageKeywords;
		}

		_testMetaTagsTag(metaKeywords, metaLang, "keywords");
	}

	private void _testMetaTagsTag(
			String metaContent, String metaLang, String metaName)
		throws Exception {

		MetaTagsTag metaTagsTag = new MetaTagsTag();

		metaTagsTag.setPageContext(_pageContext);

		metaTagsTag.doStartTag();

		metaTagsTag.setBodyContent(_pageContext.pushBody());

		metaTagsTag.doEndTag();

		String content = _unsyncStringWriter.toString();

		if (Validator.isNull(metaContent)) {
			Assert.assertFalse(
				content,
				StringUtil.contains(
					content, " name=\"" + metaName + "\" />",
					StringPool.BLANK));
		}
		else if (Validator.isNotNull(metaLang)) {
			Assert.assertTrue(
				content,
				StringUtil.contains(
					content,
					StringBundler.concat(
						"<meta content=\"", metaContent, "\" lang=\"", metaLang,
						"\" name=\"", metaName, "\" />"),
					StringPool.BLANK));
		}
		else {
			Assert.assertTrue(
				content,
				StringUtil.contains(
					content,
					StringBundler.concat(
						"<meta content=\"", metaContent, "\" name=\"", metaName,
						"\" />"),
					StringPool.BLANK));
		}
	}

	private void _testMetaTagsTagResponseStatus(
			String htmlDescription, int status)
		throws Exception {

		Mockito.when(
			_layout.getDescription(Mockito.anyString(), Mockito.anyBoolean())
		).thenReturn(
			htmlDescription
		);

		_setUpPageContext(RandomTestUtil.randomString(), null, null, status);

		_layoutUtilityPageEntryLayoutProviderUtilMockedStatic.when(
			() ->
				LayoutUtilityPageEntryLayoutProviderUtil.
					getDefaultLayoutUtilityPageEntryLayout(
						Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_layout
		);

		_testMetaTagsTag(
			htmlDescription, LocaleUtil.toW3cLanguageId(LocaleUtil.SPAIN),
			"description");

		_testMetaTagsTag(null, null, "robots");
	}

	private void _testRobotsMetaTagsTag(
			String expectedRobotsMetaTagContent, String layoutRobots,
			String localizedLayoutRobots, String pageRobotsRequestAttribute)
		throws Exception {

		Mockito.when(
			_layout.getRobots(Mockito.anyString(), Mockito.anyBoolean())
		).thenReturn(
			localizedLayoutRobots
		);

		Mockito.when(
			_layout.getRobots(Mockito.anyString())
		).thenReturn(
			layoutRobots
		);

		_setUpPageContext(
			pageRobotsRequestAttribute, null, null, HttpServletResponse.SC_OK);

		_testMetaTagsTag(expectedRobotsMetaTagContent, null, "robots");
	}

	private static final MockedStatic<LayoutUtilityPageEntryLayoutProviderUtil>
		_layoutUtilityPageEntryLayoutProviderUtilMockedStatic =
			Mockito.mockStatic(LayoutUtilityPageEntryLayoutProviderUtil.class);

	private Layout _layout;
	private PageContext _pageContext;
	private ThemeDisplay _themeDisplay;
	private UnsyncStringWriter _unsyncStringWriter;

}