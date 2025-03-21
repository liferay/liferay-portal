/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.subscriptions.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.diff.DiffHtml;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalHelper;
import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.mail.kernel.service.MailServiceUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.subscription.test.util.BaseSubscriptionLocalizedContentTestCase;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetHeaders;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

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
 * @author Zsolt Berentey
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class JournalSubscriptionLocalizedContentTest
	extends BaseSubscriptionLocalizedContentTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		group.setName("Test Site in English", LocaleUtil.getDefault());
		group.setName("Sitio de Pruebas en español", LocaleUtil.SPAIN);

		group = _groupLocalService.updateGroup(group);

		_mailService = MailServiceUtil.getService();

		ReflectionTestUtil.setFieldValue(
			MailServiceUtil.class, "_mailService", _geMailService());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		ReflectionTestUtil.setFieldValue(
			MailServiceUtil.class, "_mailService", _mailService);

		super.tearDown();
	}

	@Test
	public void testSubscriptionLocalizedContentWithReplacementsWhenAddingBaseModel()
		throws Exception {

		user = _userLocalService.updateLanguageId(
			user.getUserId(), _language.getLanguageId(LocaleUtil.SPAIN));

		setBaseModelSubscriptionBodyPreferences(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, _EMAIL_ARTICLE_ADDED_BODY
			).build(),
			getSubscriptionAddedBodyPreferenceName());

		addSubscriptionContainerModel(getDefaultContainerModelId());

		long journalArticlePrimaryKey = addBaseModel(
			creatorUser.getUserId(), getDefaultContainerModelId());

		Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		Assert.assertEquals(
			_getExpectedMailBody(
				_EMAIL_ARTICLE_ADDED_BODY, journalArticlePrimaryKey),
			mailMessage.getBody());
	}

	@Test
	public void testSubscriptionLocalizedContentWithReplacementsWhenUpdatingBaseModel()
		throws Exception {

		user = _userLocalService.updateLanguageId(
			user.getUserId(), _language.getLanguageId(LocaleUtil.SPAIN));

		setBaseModelSubscriptionBodyPreferences(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, _EMAIL_ARTICLE_UPDATED_BODY
			).build(),
			getSubscriptionUpdatedBodyPreferenceName());

		long journalArticlePrimaryKey = addBaseModel(
			creatorUser.getUserId(), getDefaultContainerModelId());

		addSubscriptionContainerModel(getDefaultContainerModelId());

		updateBaseModel(creatorUser.getUserId(), journalArticlePrimaryKey);

		Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		Assert.assertEquals(
			_getExpectedMailBody(
				_EMAIL_ARTICLE_UPDATED_BODY, journalArticlePrimaryKey),
			mailMessage.getBody());
	}

	@Override
	protected long addBaseModel(long userId, long containerModelId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), userId);

		ThemeDisplay themeDisplay = _getThemeDisplay(creatorUser);

		serviceContext.setRequest(themeDisplay.getRequest());

		JournalArticle article = JournalTestUtil.addArticle(
			group.getGroupId(), containerModelId,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Title"
			).put(
				LocaleUtil.SPAIN, "Título"
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Content in English"
			).put(
				LocaleUtil.SPAIN, "Contenido en español"
			).build(),
			LocaleUtil.getDefault(), false, true, serviceContext);

		return article.getPrimaryKey();
	}

	@Override
	protected void addSubscriptionContainerModel(long containerModelId)
		throws Exception {

		JournalFolderLocalServiceUtil.subscribe(
			user.getUserId(), group.getGroupId(), containerModelId);
	}

	@Override
	protected User addUser() throws Exception {
		return UserTestUtil.addOmniadminUser();
	}

	@Override
	protected String getPortletId() {
		return JournalPortletKeys.JOURNAL;
	}

	@Override
	protected String getServiceName() {
		return JournalConstants.SERVICE_NAME;
	}

	@Override
	protected String getSubscriptionAddedBodyPreferenceName() {
		return "emailArticleAddedBody";
	}

	@Override
	protected String getSubscriptionUpdatedBodyPreferenceName() {
		return "emailArticleUpdatedBody";
	}

	@Override
	protected void setBaseModelSubscriptionBodyPreferences(
			Map<Locale, String> localizedContents, String bodyPreferenceName)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				layout, getServiceName());

		for (Map.Entry<Locale, String> localizedContent :
				localizedContents.entrySet()) {

			LocalizationUtil.setPreferencesValue(
				portletPreferences, bodyPreferenceName,
				LocaleUtil.toLanguageId(localizedContent.getKey()),
				localizedContent.getValue());
		}

		PortletPreferencesLocalServiceUtil.updatePreferences(
			group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_GROUP,
			PortletKeys.PREFS_PLID_SHARED, getServiceName(),
			portletPreferences);
	}

	@Override
	protected void updateBaseModel(long userId, long baseModelId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), userId);

		ThemeDisplay themeDisplay = _getThemeDisplay(creatorUser);

		serviceContext.setRequest(themeDisplay.getRequest());

		JournalTestUtil.updateArticle(
			userId, _journalArticleLocalService.getArticle(baseModelId),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "New Title"
			).put(
				LocaleUtil.SPAIN, "Título Nuevo"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "New content in English"
			).put(
				LocaleUtil.SPAIN, "Contenido nuevo en español"
			).build(),
			LocaleUtil.getDefault(), false, true, serviceContext);
	}

	private MailService _geMailService() {
		return new MailService() {

			@Override
			public void clearSession() {
				_mailService.clearSession();
			}

			@Override
			public void clearSession(long companyId) {
				_mailService.clearSession(companyId);
			}

			@Override
			public Session getSession() {
				return _mailService.getSession();
			}

			@Override
			public Session getSession(Account account) {
				return _mailService.getSession(account);
			}

			@Override
			public Session getSession(long companyId) {
				return _mailService.getSession(companyId);
			}

			@Override
			public void sendEmail(
				com.liferay.mail.kernel.model.MailMessage mailMessage) {

				InternetHeaders internetHeaders = new InternetHeaders();

				internetHeaders.setHeader("Content-Transfer-Encoding", "8bit");

				mailMessage.setInternetHeaders(internetHeaders);

				_mailService.sendEmail(mailMessage);
			}

		};
	}

	private String _getExpectedMailBody(
			String emailArticleBody, long journalArticlePrimaryKey)
		throws Exception {

		JournalArticle journalArticle = _journalArticleLocalService.getArticle(
			journalArticlePrimaryKey);

		String journalArticleDiffs = "";
		ThemeDisplay themeDisplay = _getThemeDisplay(user);

		if (_EMAIL_ARTICLE_UPDATED_BODY.equals(emailArticleBody)) {
			double previousJournalArticleVersion = journalArticle.getVersion();

			journalArticle = _journalArticleLocalService.getLatestArticle(
				journalArticle.getResourcePrimKey());

			journalArticleDiffs = _diffHtml.replaceStyles(
				_journalHelper.diffHtml(
					journalArticle.getGroupId(), journalArticle.getArticleId(),
					previousJournalArticleVersion, journalArticle.getVersion(),
					user.getLanguageId(), null, themeDisplay));
		}

		JournalArticleDisplay journalArticleDisplay =
			_journalArticleLocalService.getArticleDisplay(
				group.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, user.getLanguageId(), themeDisplay);

		return StringUtil.replace(
			emailArticleBody,
			new String[] {
				"[$ARTICLE_CONTENT$]", "[$ARTICLE_DIFFS$]",
				"[$ARTICLE_STATUS$]", "[$ARTICLE_TITLE$]", "[$FOLDER_NAME$]",
				"[$PORTLET_TITLE$]", "[$SITE_NAME$]"
			},
			new String[] {
				journalArticleDisplay.getContent(), journalArticleDiffs,
				_language.get(
					user.getLocale(),
					WorkflowConstants.getStatusLabel(
						journalArticle.getStatus())),
				journalArticle.getTitle(user.getLanguageId()),
				_language.get(user.getLocale(), "home"),
				_portal.getPortletTitle(
					JournalPortletKeys.JOURNAL, user.getLanguageId()),
				group.getDescriptiveName(user.getLocale())
			});
	}

	private HttpServletRequest _getHttpServletRequest(
		ThemeDisplay themeDisplay) {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		MockLiferayResourceRequest mockPortletRequest =
			new MockLiferayResourceRequest();

		mockPortletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, httpServletRequest);

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST, mockPortletRequest);

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(User user) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(group.getCompanyId()));
		themeDisplay.setLanguageId(group.getDefaultLanguageId());
		themeDisplay.setLayout(layout);

		LayoutSet layoutSet = layout.getLayoutSet();

		themeDisplay.setLayoutSet(layoutSet);

		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());
		themeDisplay.setRealUser(user);
		themeDisplay.setRequest(_getHttpServletRequest(themeDisplay));
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private static final String _EMAIL_ARTICLE_ADDED_BODY =
		"[$PORTLET_TITLE$]: [$SITE_NAME$]: [$FOLDER_NAME$]: " +
			"[$ARTICLE_TITLE$]: [$ARTICLE_STATUS$]: [$ARTICLE_CONTENT$]";

	private static final String _EMAIL_ARTICLE_UPDATED_BODY =
		"[$PORTLET_TITLE$]: [$SITE_NAME$]: [$FOLDER_NAME$]: " +
			"[$ARTICLE_TITLE$]: [$ARTICLE_STATUS$]: [$ARTICLE_DIFFS$]";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DiffHtml _diffHtml;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalHelper _journalHelper;

	@Inject
	private Language _language;

	private MailService _mailService;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}