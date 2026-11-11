/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.example.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalContent;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
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
import com.liferay.portal.kernel.template.TemplateConstants;
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
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
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

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

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
		_testClearCacheWithCustomDDMTemplate();
		_testClearCacheWithLowercaseArticleId();
	}

	@Test
	public void testGetDisplay() throws Exception {
		_testGetDisplay();
		_testGetDisplayWithCSPNonceTemplate();
		_testGetDisplayWithCSPNonceTemplateWithBlankNonce();
		_testGetDisplayWithoutCSPNonceTemplate();
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
		return _companyLocalService.getCompany(_group.getCompanyId());
	}

	protected RenderRequest getRenderRequest(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		MockRenderRequest renderRequest = new MockRenderRequest();

		renderRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, renderRequest);

		return renderRequest;
	}

	protected ServiceContext getServiceContext(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	protected Theme getTheme(LayoutSet layoutSet) throws PortalException {
		return _themeLocalService.getTheme(
			_group.getCompanyId(), layoutSet.getThemeId());
	}

	protected ThemeDisplay getThemeDisplay(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(getCompany());
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLookAndFeel(getTheme(layoutSet), null);

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
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

	private JournalArticle _addJournalArticle(String script) throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, script,
			LocaleUtil.getSiteDefault());

		ddmTemplate.setCacheable(true);

		ddmTemplate = _ddmTemplateLocalService.updateDDMTemplate(ddmTemplate);

		String xml = DDMStructureTestUtil.getSampleStructuredContent(
			"content",
			Collections.singletonList(
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build()),
			LocaleUtil.toLanguageId(LocaleUtil.US));

		return JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
			LocaleUtil.getSiteDefault());
	}

	private CompanyConfigurationTemporarySwapper
			_getCompanyConfigurationTemporarySwapper()
		throws Exception {

		return new CompanyConfigurationTemporarySwapper(
			_group.getCompanyId(),
			"com.liferay.portal.security.content.security.policy.internal." +
				"configuration.ContentSecurityPolicyConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).put(
				"policy",
				"default-src 'self'; script-src 'self' '[$NONCE$]'; " +
					"style-src 'self' '[$NONCE$]'"
			).put(
				"reportOnly", false
			).build());
	}

	private JournalArticleDisplay _getJournalArticleDisplay(
			JournalArticle journalArticle)
		throws Exception {

		return _getJournalArticleDisplay(
			journalArticle, new MockHttpServletRequest());
	}

	private JournalArticleDisplay _getJournalArticleDisplay(
			JournalArticle journalArticle,
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = getThemeDisplay(httpServletRequest);

		MockRenderRequest mockRenderRequest = new MockRenderRequest();

		mockRenderRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, mockRenderRequest);

		PortletRequestModel portletRequestModel = new PortletRequestModel(
			mockRenderRequest, new MockPortletResponse());

		return _journalContent.getDisplay(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getDDMTemplateKey(), Constants.VIEW,
			journalArticle.getDefaultLanguageId(), 1, portletRequestModel,
			themeDisplay);
	}

	private JournalArticleDisplay _getJournalArticleDisplay(
			JournalArticle journalArticle, String nonce)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			"com.liferay.portal.security.content.security.policy.internal." +
				"ContentSecurityPolicyNonceManager#NONCE",
			nonce);

		return _getJournalArticleDisplay(
			journalArticle, mockHttpServletRequest);
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

		JournalArticle journalArticle = JournalTestUtil.addArticle(
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
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, englishLanguageId, _portletRequestModel);

		Assert.assertEquals(englishContent, englishArticleDisplay.getContent());

		String spanishLanguageId = LocaleUtil.toLanguageId(LocaleUtil.SPAIN);

		JournalArticleDisplay spanishArticleDisplay =
			_journalContent.getDisplay(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, spanishLanguageId, _portletRequestModel);

		Assert.assertEquals(spanishContent, spanishArticleDisplay.getContent());

		_journalArticleLocalService.removeArticleLocale(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getVersion(), spanishLanguageId);

		englishArticleDisplay = _journalContent.getDisplay(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			Constants.VIEW, englishLanguageId, _portletRequestModel);

		Assert.assertEquals(englishContent, englishArticleDisplay.getContent());

		spanishArticleDisplay = _journalContent.getDisplay(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			Constants.VIEW, spanishLanguageId, _portletRequestModel);

		Assert.assertNotEquals(
			spanishContent, spanishArticleDisplay.getContent());
	}

	private void _testClearCacheWithCustomDDMTemplate() throws Exception {
		String spanishContent = RandomTestUtil.randomString();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			_portal.getClassNameId(JournalArticle.class),
			_getLocalizedMap(new Locale[] {LocaleUtil.SPAIN, LocaleUtil.US}),
			_getLocalizedMap(new Locale[] {LocaleUtil.SPAIN, LocaleUtil.US}),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, spanishContent
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), journalArticle.getDDMStructureId(),
			_portal.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL,
			JournalTestUtil.getSampleTemplateFTL(),
			LocaleUtil.getSiteDefault());

		ddmTemplate.setCacheable(true);

		ddmTemplate = _ddmTemplateLocalService.updateDDMTemplate(ddmTemplate);

		String spanishLanguageId = LocaleUtil.toLanguageId(LocaleUtil.SPAIN);

		JournalArticleDisplay spanishArticleDisplay =
			_journalContent.getDisplay(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				ddmTemplate.getTemplateKey(), Constants.VIEW, spanishLanguageId,
				_portletRequestModel);

		Assert.assertEquals(spanishContent, spanishArticleDisplay.getContent());

		_journalArticleLocalService.removeArticleLocale(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getVersion(), spanishLanguageId);

		spanishArticleDisplay = _journalContent.getDisplay(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			ddmTemplate.getTemplateKey(), Constants.VIEW, spanishLanguageId,
			_portletRequestModel);

		Assert.assertNotEquals(
			spanishContent, spanishArticleDisplay.getContent());
	}

	private void _testClearCacheWithLowercaseArticleId() throws Exception {
		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			null, _group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			StringUtil.toLowerCase(RandomTestUtil.randomString()), false,
			titleMap, titleMap, titleMap, null, LocaleUtil.US, null, null,
			false, true, _serviceContext);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), journalArticle.getDDMStructureId(),
			_portal.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, "${randomNamespace}",
			LocaleUtil.US);

		ddmTemplate.setCacheable(true);

		ddmTemplate = _ddmTemplateLocalService.updateDDMTemplate(ddmTemplate);

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.US);

		JournalArticleDisplay journalArticleDisplay1 =
			_journalContent.getDisplay(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				ddmTemplate.getTemplateKey(), Constants.VIEW, languageId,
				_portletRequestModel);

		_journalContent.clearCache(
			journalArticle.getGroupId(),
			StringUtil.toLowerCase(journalArticle.getArticleId()));

		JournalArticleDisplay journalArticleDisplay2 =
			_journalContent.getDisplay(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				ddmTemplate.getTemplateKey(), Constants.VIEW, languageId,
				_portletRequestModel);

		Assert.assertNotEquals(
			journalArticleDisplay1.getContent(),
			journalArticleDisplay2.getContent());
	}

	private void _testGetDisplay() throws Exception {
		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				getXML(), "BASIC-WEB-CONTENT", "BASIC-WEB-CONTENT");

		String defaultLanguageId = journalArticle.getDefaultLanguageId();

		JournalArticleDisplay journalArticleDisplay =
			_journalContent.getDisplay(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, defaultLanguageId, _portletRequestModel);

		Assert.assertEquals(
			journalArticle.getDescription(defaultLanguageId),
			journalArticleDisplay.getDescription());
		Assert.assertEquals(
			journalArticle.getTitle(defaultLanguageId),
			journalArticleDisplay.getTitle());
	}

	private void _testGetDisplayWithCSPNonceTemplate() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper()) {

			JournalArticle journalArticle = _addJournalArticle(_SCRIPT);

			String oldNonce = RandomTestUtil.randomString();

			_getJournalArticleDisplay(journalArticle, oldNonce);

			String newNonce = RandomTestUtil.randomString();

			JournalArticleDisplay journalArticleDisplay =
				_getJournalArticleDisplay(journalArticle, newNonce);

			String content = journalArticleDisplay.getContent();

			Assert.assertTrue(content.contains(" nonce=\"" + newNonce + "\""));
			Assert.assertFalse(content.contains(" nonce=\"" + oldNonce + "\""));
		}
	}

	private void _testGetDisplayWithCSPNonceTemplateWithBlankNonce()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper()) {

			JournalArticle journalArticle = _addJournalArticle(_SCRIPT);

			_getJournalArticleDisplay(
				journalArticle, RandomTestUtil.randomString());

			JournalArticleDisplay journalArticleDisplay =
				_getJournalArticleDisplay(journalArticle);

			String content = journalArticleDisplay.getContent();

			Assert.assertTrue(content.contains("data-lfr-nonce-journal"));
		}
	}

	private void _testGetDisplayWithoutCSPNonceTemplate() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getCompanyConfigurationTemporarySwapper()) {

			JournalArticle journalArticle = _addJournalArticle(
				"<script>console.log(\"test\");</script>");

			_getJournalArticleDisplay(
				journalArticle, RandomTestUtil.randomString());

			JournalArticleDisplay journalArticleDisplay =
				_getJournalArticleDisplay(
					journalArticle, RandomTestUtil.randomString());

			String content = journalArticleDisplay.getContent();

			Assert.assertFalse(content.contains("data-lfr-nonce-journal"));
			Assert.assertFalse(content.contains(" nonce=\""));
		}
	}

	private static final String _SCRIPT =
		"<script ${nonceAttribute}>console.log(\"test\");</script>";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalContent _journalContent;

	private Layout _layout;

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