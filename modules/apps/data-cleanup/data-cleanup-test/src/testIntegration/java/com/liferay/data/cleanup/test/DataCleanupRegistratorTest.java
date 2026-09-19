/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.cleanup.DataCleanup;
import com.liferay.data.cleanup.util.DataCleanupUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class DataCleanupRegistratorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDataCleanupUpgradeAmazonRankings() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.amazon.rankings.web", null,
			"com_liferay_amazon_rankings_web_portlet_AmazonRankingsPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeChat() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.chat.service", "dependencies/chat-tables.sql", null,
			null);
	}

	@Test
	public void testDataCleanupUpgradeCurrencyConverter() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.currency.converter.web", null,
			"com_liferay_currency_converter_web_portlet_" +
				"CurrencyConverterPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeDictionary() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.dictionary.web", null,
			"com_liferay_dictionary_web_portlet_DictionaryPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeDirectory() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.directory.web", null,
			"com_liferay_directory_web_portlet_DirectoryPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeDocumentLibraryFileRank()
		throws Exception {

		_testModuleDataCleanup(
			"com.liferay.document.library.file.rank.service",
			"dependencies/document-library-file-rank-tables.sql", null, null);
	}

	@Test
	public void testDataCleanupUpgradeFrontendImageEditor() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.frontend.image.editor.web", null,
			"com_liferay_image_editor_web_portlet_ImageEditorPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeGoogleMaps() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.google.maps.web", null,
			"com_liferay_google_maps_web_portlet_GoogleMapsPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeHTMLPreview() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.html.preview.service",
			"dependencies/html-preview-tables.sql", null, null);
	}

	@Test
	public void testDataCleanupUpgradeHelloVelocity() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.hello.velocity.web", null,
			"com_liferay_hello_velocity_web_portlet_HelloVelocityPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeHelloWorld() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.hello.world.web", null,
			"com_liferay_hello_world_web_portlet_HelloWorldPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeInvitation() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.invitation.web", null,
			"com_liferay_invitation_web_portlet_InvitationPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeLoanCalculator() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.loan.calculator.web", null,
			"com_liferay_loan_calculator_portlet_LoanCalculatorPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeMailReader() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.mail.reader.service",
			"dependencies/mail-reader-tables.sql",
			"com_liferay_mail_reader_web_portlet_MailPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeNetworkUtilities() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.network.utilities.web", null,
			"com_liferay_network_utilities_web_portlet_NetworkUtilitiesPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeOAuth() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.oauth.service", "dependencies/oauth-tables.sql",
			"com_liferay_oauth_web_internal_portlet_AdminPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeOpenSocial() throws Exception {
		_testModuleDataCleanup(
			"opensocial-portlet", "dependencies/opensocial-tables.sql",
			"3_WAR_opensocialportlet", "OPEN_SOCIAL_DATA_");
	}

	@Test
	public void testDataCleanupUpgradePasswordGenerator() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.password.generator.web", null,
			"com_liferay_password_generator_web_portlet_" +
				"PasswordGeneratorPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradePortalSecurityWedeployAuth()
		throws Exception {

		_testModuleDataCleanup(
			"com.liferay.portal.security.wedeploy.auth.service",
			"dependencies/portal-security-wedeploy-auth-tables.sql",
			"com_liferay_portal_security_wedeploy_auth_web_internal_" +
				"portlet_WeDeployAuthAdminPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeQuickNote() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.quick.note.web", null,
			"com_liferay_quick_note_web_portlet_QuickNotePortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeRecentDocuments() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.recent.documents.web", null,
			"com_liferay_recent_documents_web_portlet_RecentDocumentsPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeShopping() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.shopping.service", "dependencies/shopping-tables.sql",
			"com_liferay_shopping_web_portlet_ShoppingPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeSocialActivity() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.social.activity.web", null,
			"com_liferay_social_activity_web_portlet_SocialActivityPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeSocialGroupStatistics() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.social.group.statistics.web", null,
			"com_liferay_social_group_statistics_web_portlet_" +
				"SocialGroupStatisticsPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeSocialPrivateMessaging()
		throws Exception {

		_testModuleDataCleanup(
			"com.liferay.social.privatemessaging.service",
			"dependencies/social-private-messaging-tables.sql",
			"com_liferay_social_privatemessaging_web_portlet_" +
				"PrivateMessagingPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeSocialRequests() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.social.requests.web", null,
			"com_liferay_social_requests_web_portlet_SocialRequestsPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeSocialUserStatistics() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.social.user.statistics.web", null,
			"com_liferay_social_user_statistics_web_portlet_" +
				"SocialUserStatisticsPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeSoftwareCatalog() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.softwarecatalog.service",
			"dependencies/software-catalog-tables.sql",
			"com.liferay.portlet.softwarecatalog", null);
	}

	@Test
	public void testDataCleanupUpgradeTranslator() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.translator.web", null,
			"com_liferay_translator_web_portlet_TranslatorPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeTwitter() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.twitter.service", "dependencies/twitter-tables.sql",
			"com_liferay_twitter_web_portlet_TwitterPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeUnitConverter() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.unit.converter.web", null,
			"com_liferay_unit_converter_web_portlet_UnitConverterPortlet",
			null);
	}

	@Test
	public void testDataCleanupUpgradeWeather() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.weather.web", null,
			"com_liferay_weather_web_portlet_WeatherPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeWebForm() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.web.form.web", null,
			"com_liferay_web_form_web_portlet_WebFormPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeWebProxy() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.web.proxy.web", null,
			"com_liferay_web_proxy_web_portlet_WebProxyPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeWysiwyg() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.wysiwyg.web", null,
			"com_liferay_wysiwyg_web_portlet_WYSIWYGPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeXSLContent() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.xsl.content.web", null,
			"com_liferay_xsl_content_web_portlet_XSLContentPortlet", null);
	}

	@Test
	public void testDataCleanupUpgradeYoutube() throws Exception {
		_testModuleDataCleanup(
			"com.liferay.youtube.web", null,
			"com_liferay_youtube_web_portlet_YouTubePortlet", null);
	}

	private void _testModuleDataCleanup(
			String servletContextName, String sqlFilePath,
			String portletPreferencePortletId, String expandoTableName)
		throws Exception {

		if (Validator.isNotNull(sqlFilePath)) {
			try (InputStream inputStream =
					DataCleanupRegistratorTest.class.getResourceAsStream(
						sqlFilePath)) {

				DB db = DBManagerUtil.getDB();

				db.runSQLTemplate(StringUtil.read(inputStream), true);
			}
		}

		if (portletPreferencePortletId != null) {
			_layout = LayoutTestUtil.addTypePortletLayout(
				TestPropsValues.getGroupId());

			UnicodeProperties unicodeProperties =
				_layout.getTypeSettingsProperties();

			unicodeProperties.put(
				"test-property-1", portletPreferencePortletId);
			unicodeProperties.put(
				"test-property-2",
				portletPreferencePortletId + "," + portletPreferencePortletId);
			unicodeProperties.put(
				"test-property-3", "abc," + portletPreferencePortletId);
			unicodeProperties.put(
				"test-property-4", portletPreferencePortletId + ",def");
			unicodeProperties.put(
				"test-property-5",
				"abc," + portletPreferencePortletId + ",def");

			_layout.setTypeSettings(unicodeProperties.toString());

			_layout = _layoutLocalService.updateLayout(_layout);
		}

		String expandoColumnName = "testColumn";
		long expandoTableId = 0;
		long expandoValueId = 0;

		if (Validator.isNotNull(expandoTableName)) {
			ClassName className = _classNameLocalService.addClassName(
				expandoTableName + "test");

			ExpandoTable expandoTable = ExpandoTestUtil.addTable(
				className.getClassNameId(), expandoTableName);

			expandoTableId = expandoTable.getTableId();

			ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
				expandoTable, expandoColumnName, ExpandoColumnConstants.STRING);

			ExpandoValue expandoValue = ExpandoTestUtil.addValue(
				expandoTable, expandoColumn, className.getClassNameId(),
				"testValue");

			expandoValueId = expandoValue.getValueId();
		}

		for (DataCleanup dataCleanup :
				DataCleanupUtil.getModuleDataCleanups()) {

			if (servletContextName.equals(
					dataCleanup.getServletContextName())) {

				dataCleanup.cleanup();
			}
		}

		if (portletPreferencePortletId != null) {
			EntityCacheUtil.clearLocalCache();

			_layout = _layoutLocalService.getLayout(_layout.getPlid());

			UnicodeProperties unicodeProperties =
				_layout.getTypeSettingsProperties();

			Assert.assertNull(unicodeProperties.getProperty("test-property-1"));
			Assert.assertNull(unicodeProperties.getProperty("test-property-2"));
			Assert.assertEquals(
				"abc", unicodeProperties.getProperty("test-property-3"));
			Assert.assertEquals(
				"def", unicodeProperties.getProperty("test-property-4"));
			Assert.assertEquals(
				"abc,def", unicodeProperties.getProperty("test-property-5"));
		}

		if (Validator.isNotNull(expandoTableName)) {
			Assert.assertEquals(
				null,
				_expandoColumnLocalService.fetchColumn(
					expandoTableId, expandoColumnName));
			Assert.assertEquals(
				null,
				_expandoTableLocalService.fetchExpandoTable(expandoTableId));
			Assert.assertEquals(
				null,
				_expandoValueLocalService.fetchExpandoValue(expandoValueId));
		}
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private ExpandoValueLocalService _expandoValueLocalService;

	@DeleteAfterTestRun
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ReleaseLocalService _releaseLocalService;

}