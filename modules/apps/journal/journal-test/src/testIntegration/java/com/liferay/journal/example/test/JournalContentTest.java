/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.example.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalContent;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.portlet.MockPortletResponse;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class JournalContentTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		setUpPortletRequestModel(mockHttpServletRequest);
		setUpServiceContext(mockHttpServletRequest);
	}

	@After
	public void tearDown() {
		tearDownServiceContext();
	}

	@Test
	public void testClearCache() throws Exception {
		_testClearCache(new Locale[] {LocaleUtil.SPAIN, LocaleUtil.US});
		_testClearCache(new Locale[] {LocaleUtil.US});
	}

	@Test
	public void testGetDisplay() throws Exception {
		_journalArticle = JournalTestUtil.addArticleWithXMLContent(
			getXML(), "BASIC-WEB-CONTENT", "BASIC-WEB-CONTENT");

		String defaultLanguageId = _journalArticle.getDefaultLanguageId();

		JournalArticleDisplay articleDisplay = _journalContent.getDisplay(
			_journalArticle.getGroupId(), _journalArticle.getArticleId(),
			Constants.VIEW, defaultLanguageId, _portletRequestModel);

		Assert.assertEquals(
			_journalArticle.getDescription(defaultLanguageId),
			articleDisplay.getDescription());
		Assert.assertEquals(
			_journalArticle.getTitle(defaultLanguageId),
			articleDisplay.getTitle());
	}

	protected static String getXML() {
		return DDMStructureTestUtil.getSampleStructuredContent(
			"content",
			Collections.singletonList(
				HashMapBuilder.put(
					LocaleUtil.US, "example"
				).build()),
			LocaleUtil.toLanguageId(LocaleUtil.US));
	}

	protected Company getCompany() throws PortalException {
		return _companyLocalService.getCompany(TestPropsValues.getCompanyId());
	}

	protected Layout getLayout() throws PortalException {
		List<Layout> layouts = _layoutLocalService.getLayouts(
			TestPropsValues.getGroupId(), false, 0, 1, null);

		return layouts.get(0);
	}

	protected RenderRequest getRenderRequest(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		MockRenderRequest renderRequest = new MockRenderRequest();

		renderRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST, renderRequest);

		return renderRequest;
	}

	protected ServiceContext getServiceContext(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	protected Theme getTheme(LayoutSet layoutSet) throws PortalException {
		return _themeLocalService.getTheme(
			TestPropsValues.getCompanyId(), layoutSet.getThemeId());
	}

	protected ThemeDisplay getThemeDisplay(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(getCompany());
		themeDisplay.setLayout(getLayout());

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			TestPropsValues.getGroupId(), false);

		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLookAndFeel(getTheme(layoutSet), null);

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(TestPropsValues.getUser());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return themeDisplay;
	}

	protected void setUpPortletRequestModel(
			MockHttpServletRequest mockHttpServletRequest)
		throws PortalException {

		RenderRequest renderRequest = getRenderRequest(
			mockHttpServletRequest, getThemeDisplay(mockHttpServletRequest));

		_portletRequestModel = new PortletRequestModel(
			renderRequest, new MockPortletResponse());
	}

	protected void setUpServiceContext(
			MockHttpServletRequest mockHttpServletRequest)
		throws PortalException {

		_serviceContext = getServiceContext(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	protected void tearDownServiceContext() {
		ServiceContextThreadLocal.popServiceContext();
	}

	private Map<Locale, String> _getLocalizedMap(Locale[] locales) {
		Map<Locale, String> map = new HashMap<>();

		for (Locale locale : locales) {
			map.put(locale, RandomTestUtil.randomString());
		}

		return map;
	}

	private void _testClearCache(Locale[] locales) throws Exception {
		String englishContent = RandomTestUtil.randomString();
		String spanishContent = RandomTestUtil.randomString();

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			_portal.getClassNameId(JournalArticle.class),
			_getLocalizedMap(locales), _getLocalizedMap(locales),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, spanishContent
			).put(
				LocaleUtil.US, englishContent
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		String englishLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		JournalArticleDisplay englishArticleDisplay =
			_journalContent.getDisplay(
				_journalArticle.getGroupId(), _journalArticle.getArticleId(),
				Constants.VIEW, englishLanguageId, _portletRequestModel);

		Assert.assertEquals(englishContent, englishArticleDisplay.getContent());

		String spanishLanguageId = LocaleUtil.toLanguageId(LocaleUtil.SPAIN);

		JournalArticleDisplay spanishArticleDisplay =
			_journalContent.getDisplay(
				_journalArticle.getGroupId(), _journalArticle.getArticleId(),
				Constants.VIEW, spanishLanguageId, _portletRequestModel);

		Assert.assertEquals(spanishContent, spanishArticleDisplay.getContent());

		_journalArticleLocalService.removeArticleLocale(
			_journalArticle.getGroupId(), _journalArticle.getArticleId(),
			_journalArticle.getVersion(), spanishLanguageId);

		_journalContent.clearCache(
			_journalArticle.getGroupId(), _journalArticle.getArticleId(),
			_journalArticle.getDDMTemplateKey());

		englishArticleDisplay = _journalContent.getDisplay(
			_journalArticle.getGroupId(), _journalArticle.getArticleId(),
			Constants.VIEW, englishLanguageId, _portletRequestModel);

		Assert.assertEquals(englishContent, englishArticleDisplay.getContent());

		spanishArticleDisplay = _journalContent.getDisplay(
			_journalArticle.getGroupId(), _journalArticle.getArticleId(),
			Constants.VIEW, spanishLanguageId, _portletRequestModel);

		Assert.assertNotEquals(
			spanishContent, spanishArticleDisplay.getContent());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalContent _journalContent;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	private PortletRequestModel _portletRequestModel;
	private ServiceContext _serviceContext;

	@Inject
	private ThemeLocalService _themeLocalService;

}