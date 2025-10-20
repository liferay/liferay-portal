/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.Serializable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class StagingLocalizationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_availableLocales = LanguageUtil.getAvailableLocales(
			TestPropsValues.getCompanyId());
		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		LanguageUtil.init();

		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), _availableLocales, _defaultLocale);
	}

	@Before
	public void setUp() throws Exception {
		LanguageUtil.init();

		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), _locales, LocaleUtil.US);

		UserTestUtil.setUser(TestPropsValues.getUser());

		_sourceGroup = GroupTestUtil.addGroup();
		_targetGroup = GroupTestUtil.addGroup();
	}

	@Test
	public void testEnableStagingLocalizedNameBlank() throws Exception {
		Map<Locale, String> nameMap = _prepareNameMap();

		_enableDisableLocalizedStaging(
			nameMap, LocaleUtil.US, LocaleUtil.GERMANY);
	}

	@Test
	public void testEnableStagingLocalizedNameExists() throws Exception {
		Map<Locale, String> nameMap = _prepareNameMap();

		_enableDisableLocalizedStaging(nameMap, LocaleUtil.US, LocaleUtil.US);
	}

	@Test
	public void testEnableStagingLocalizedNameNull() throws Exception {
		Map<Locale, String> nameMap = _prepareNameMap();

		_enableDisableLocalizedStaging(
			nameMap, LocaleUtil.US, LocaleUtil.SPAIN);
	}

	@Test(expected = LocaleException.class)
	public void testRemoveSupportedLocale() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.exportimport.internal.lifecycle." +
					"LoggerExportImportLifecycleListener",
				LoggerTestUtil.ERROR)) {

			testUpdateLocales("es_ES", "de_DE,es_ES", "en_US");
		}
	}

	@Test
	public void testUpdateDefaultLocale() throws Exception {
		testUpdateLocales("es_ES", "de_DE,en_US,es_ES", "en_US");
	}

	@Test
	public void testUpdateDefaultLocaleAndDefaultContentLocale()
		throws Exception {

		testUpdateLocales("es_ES", "de_DE,en_US,es_ES", "de_DE");
	}

	@Test
	public void testUpdateToTheSameLocale() throws Exception {
		testUpdateLocales("en_US", "de_DE,en_US,es_ES", "en_US");
	}

	protected void testUpdateLocales(
			String defaultLanguageId, String languageIds,
			String defaultContentLanguageId)
		throws Exception {

		GroupTestUtil.enableLocalStaging(
			_sourceGroup, TestPropsValues.getUserId());

		JournalArticle article = JournalTestUtil.addArticle(
			_sourceGroup.getGroupId(), "Title", "content",
			LocaleUtil.fromLanguageId(defaultContentLanguageId));

		User user = TestPropsValues.getUser();

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap();

		Map<String, Serializable> publishLayoutLocalSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutLocalSettingsMap(
					user, _sourceGroup.getGroupId(), _targetGroup.getGroupId(),
					false,
					ExportImportHelperUtil.getAllLayoutIds(
						_sourceGroup.getGroupId(), false),
					parameterMap);

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_LAYOUT_LOCAL,
					publishLayoutLocalSettingsMap);

		File file = ExportImportLocalServiceUtil.exportLayoutsAsFile(
			exportImportConfiguration);

		LanguageUtil.init();

		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), languageIds, defaultLanguageId);

		ExportImportLocalServiceUtil.importLayouts(
			exportImportConfiguration, file);

		JournalArticleResource articleResource =
			JournalArticleResourceLocalServiceUtil.
				fetchJournalArticleResourceByUuidAndGroupId(
					article.getArticleResourceUuid(),
					_targetGroup.getGroupId());

		Assert.assertNotNull(articleResource);

		JournalArticle stagingArticle =
			JournalArticleLocalServiceUtil.getLatestArticle(
				articleResource.getResourcePrimKey(),
				WorkflowConstants.STATUS_ANY, false);

		if (languageIds.contains(defaultContentLanguageId)) {
			Assert.assertEquals(
				article.getDefaultLanguageId(),
				stagingArticle.getDefaultLanguageId());
		}
		else {
			Assert.assertEquals(
				defaultLanguageId, stagingArticle.getDefaultLanguageId());
		}

		for (Locale locale : _locales) {
			if (languageIds.contains(LocaleUtil.toLanguageId(locale)) ||
				languageIds.contains(defaultContentLanguageId)) {

				Assert.assertEquals(
					article.getTitle(locale), stagingArticle.getTitle(locale));
			}
			else {
				Assert.assertEquals(
					article.getTitle(defaultLanguageId),
					stagingArticle.getTitle(locale));
			}
		}
	}

	private Group _addLocalizedGroup(
			Map<Locale, String> nameMap, ServiceContext serviceContext)
		throws Exception {

		return GroupLocalServiceUtil.addGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, Group.class.getName(), 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, null, 0, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, true, false,
			true, serviceContext);
	}

	private void _enableDisableLocalizedStaging(
			Map<Locale, String> nameMap, Locale defaultLocale,
			Locale updatedDefaultLocale)
		throws Exception {

		String usName = nameMap.get(LocaleUtil.US);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		Group group = null;

		try {
			LocaleThreadLocal.setDefaultLocale(defaultLocale);

			group = _addLocalizedGroup(nameMap, serviceContext);

			LocaleThreadLocal.setDefaultLocale(updatedDefaultLocale);

			Group stagingGroup = _enableLocalizedStaging(group, serviceContext);

			Assert.assertNotNull(stagingGroup);

			Assert.assertTrue(
				stagingGroup.getLiveGroupId() == group.getGroupId());

			Assert.assertEquals(
				usName + "-staging", stagingGroup.getGroupKey());
		}
		finally {
			try {
				StagingLocalServiceUtil.disableStaging(group, serviceContext);
			}
			catch (Exception exception) {
			}

			try {
				GroupLocalServiceUtil.deleteGroup(group);
			}
			catch (Exception exception) {
			}
		}
	}

	private Group _enableLocalizedStaging(
			Group group, ServiceContext serviceContext)
		throws Exception {

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false, serviceContext);

		return group.getStagingGroup();
	}

	private Map<Locale, String> _prepareNameMap() {
		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.GERMANY, StringPool.BLANK);
		nameMap.put(LocaleUtil.SPAIN, null);
		nameMap.put(LocaleUtil.US, RandomTestUtil.randomString());

		return nameMap;
	}

	private static Set<Locale> _availableLocales;
	private static Locale _defaultLocale;

	private final List<Locale> _locales = Arrays.asList(
		LocaleUtil.US, LocaleUtil.GERMANY, LocaleUtil.SPAIN);

	@DeleteAfterTestRun
	private Group _sourceGroup;

	@DeleteAfterTestRun
	private Group _targetGroup;

}