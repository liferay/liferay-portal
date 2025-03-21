/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.lang.reflect.Constructor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UpgradePortletPreferencesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_setUpUpgradePortletPreferences();
	}

	@Test
	public void testUpgradePortletPreferencesGlobalScopedJournalArticle()
		throws Exception {

		Locale locale = _portal.getSiteDefaultLocale(_group);

		Map<String, String> expectedScopedPreferenceMap = HashMapBuilder.put(
			"portletSetupTitle",
			StringUtil.appendParentheticalSuffix(
				"Web Content Display", _language.get(locale, "global"))
		).put(
			"scopeType", "company"
		).build();

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			companyGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			expectedScopedPreferenceMap);
		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			HashMapBuilder.put(
				"portletSetupTitle", "Web Content Display"
			).build());
		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			HashMapBuilder.put(
				"portletSetupTitle",
				StringUtil.appendParentheticalSuffix(
					"Web Content Display", layout.getName(locale))
			).put(
				"scopeLayoutUuid", layout.getUuid()
			).put(
				"scopeType", "layout"
			).build());
	}

	@Test
	public void testUpgradePortletPreferencesLayoutScopedJournalArticle()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		Group layoutGroup = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), layout.getGroupId(), layout);

		Locale locale = _portal.getSiteDefaultLocale(_group);

		Map<String, String> expectedScopedPreferenceMap = HashMapBuilder.put(
			"portletSetupTitle",
			StringUtil.appendParentheticalSuffix(
				"Web Content Display", layout.getName(locale))
		).put(
			"scopeLayoutUuid", layout.getUuid()
		).put(
			"scopeType", "layout"
		).build();

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			_portal.getSiteGroupId(_group.getGroupId()),
			_portal.getClassNameId(JournalArticle.class.getName()),
			"BASIC-WEB-CONTENT", true);

		JournalArticle journalArticle = _journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), layoutGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			DDMStructureTestUtil.getSampleStructuredContent(
				"content", Collections.emptyList(),
				LocaleUtil.toLanguageId(locale)),
			ddmStructure.getStructureId(), "BASIC-WEB-CONTENT",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			expectedScopedPreferenceMap);
		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			HashMapBuilder.put(
				"portletSetupTitle", "Web Content Display"
			).build());
		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			HashMapBuilder.put(
				"portletSetupTitle",
				StringUtil.appendParentheticalSuffix(
					"Web Content Display", _language.get(locale, "global"))
			).put(
				"scopeType", "company"
			).build());
	}

	@Test
	public void testUpgradePortletPreferencesNoJournalArticleSelected()
		throws Exception {

		Locale locale = _portal.getSiteDefaultLocale(_group);

		Map<String, String> globalScopedPreferenceMap = HashMapBuilder.put(
			"portletSetupTitle",
			StringUtil.appendParentheticalSuffix(
				"Web Content Display", _language.get(locale, "global"))
		).put(
			"scopeType", "company"
		).build();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_assertUpgradePortletPreferences(
			globalScopedPreferenceMap, null, layout, locale,
			globalScopedPreferenceMap);

		Map<String, String> layoutScopedPreferenceMap = HashMapBuilder.put(
			"portletSetupTitle",
			StringUtil.appendParentheticalSuffix(
				"Web Content Display", layout.getName(locale))
		).put(
			"scopeLayoutUuid", layout.getUuid()
		).put(
			"scopeType", "layout"
		).build();

		_assertUpgradePortletPreferences(
			layoutScopedPreferenceMap, null, layout, locale,
			layoutScopedPreferenceMap);

		Map<String, String> siteScopedPreferenceMap = HashMapBuilder.put(
			"portletSetupTitle", "Web Content Display"
		).build();

		_assertUpgradePortletPreferences(
			siteScopedPreferenceMap, null, layout, locale,
			siteScopedPreferenceMap);
	}

	@Test
	public void testUpgradePortletPreferencesSiteScopedJournalArticle()
		throws Exception {

		Map<String, String> expectedScopedPreferenceMap = HashMapBuilder.put(
			"portletSetupTitle", "Web Content Display"
		).build();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			layout.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Locale locale = _portal.getSiteDefaultLocale(_group);

		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			expectedScopedPreferenceMap);
		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			HashMapBuilder.put(
				"portletSetupTitle",
				StringUtil.appendParentheticalSuffix(
					"Web Content Display", _language.get(locale, "global"))
			).put(
				"scopeType", "company"
			).build());
		_assertUpgradePortletPreferences(
			expectedScopedPreferenceMap, journalArticle, layout, locale,
			HashMapBuilder.put(
				"portletSetupTitle",
				StringUtil.appendParentheticalSuffix(
					"Web Content Display", layout.getName(locale))
			).put(
				"scopeLayoutUuid", layout.getUuid()
			).put(
				"scopeType", "layout"
			).build());
	}

	private String _addJournalContentPortletToLayout(
			Layout layout, Map<String, String[]> preferencesMap)
		throws Exception {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		List<String> columns = layoutTemplate.getColumns();

		String columnId = columns.get(0);

		return LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout,
			JournalContentPortletKeys.JOURNAL_CONTENT, columnId,
			preferencesMap);
	}

	private void _assertUpgradePortletPreferences(
			Map<String, String> expectedScopedPreferencesMap,
			JournalArticle journalArticle, Layout layout, Locale locale,
			Map<String, String> originalScopedPreferencesMap)
		throws Exception {

		String portletId = _addJournalContentPortletToLayout(
			layout,
			HashMapBuilder.put(
				"articleId",
				new String[] {
					(journalArticle == null) ? StringPool.BLANK :
						journalArticle.getArticleId()
				}
			).put(
				"groupId",
				new String[] {
					(journalArticle == null) ? StringPool.BLANK :
						String.valueOf(journalArticle.getGroupId())
				}
			).put(
				"lfrScopeLayoutUuid",
				new String[] {
					originalScopedPreferencesMap.getOrDefault(
						"scopeLayoutUuid", StringPool.BLANK)
				}
			).put(
				"lfrScopeType",
				new String[] {
					originalScopedPreferencesMap.getOrDefault(
						"scopeType", StringPool.BLANK)
				}
			).put(
				"portletSetupTitle_" + LocaleUtil.toLanguageId(locale),
				new String[] {
					originalScopedPreferencesMap.getOrDefault(
						"portletSetupTitle", StringPool.BLANK)
				}
			).build());

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				layout.getCompanyId(), 0, PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				layout.getPlid(), portletId,
				ReflectionTestUtil.invoke(
					_upgradePortletPreferences, "upgradePreferences",
					new Class<?>[] {
						long.class, long.class, int.class, long.class,
						String.class, String.class
					},
					layout.getCompanyId(), 0,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
					portletId,
					PortletPreferencesFactoryUtil.toXML(
						LayoutTestUtil.getPortletPreferences(
							layout, portletId))));

		Assert.assertEquals(
			(journalArticle == null) ? StringPool.BLANK :
				journalArticle.getArticleId(),
			portletPreferences.getValue("articleId", null));
		Assert.assertEquals(
			(journalArticle == null) ? StringPool.BLANK :
				String.valueOf(journalArticle.getGroupId()),
			portletPreferences.getValue("groupId", null));
		Assert.assertEquals(
			expectedScopedPreferencesMap.getOrDefault(
				"portletSetupTitle", StringPool.BLANK),
			portletPreferences.getValue(
				"portletSetupTitle_" + LocaleUtil.toLanguageId(locale), null));
		Assert.assertEquals(
			expectedScopedPreferencesMap.getOrDefault(
				"scopeLayoutUuid", StringPool.BLANK),
			portletPreferences.getValue("lfrScopeLayoutUuid", null));
		Assert.assertEquals(
			expectedScopedPreferencesMap.getOrDefault(
				"scopeType", StringPool.BLANK),
			portletPreferences.getValue("lfrScopeType", null));
	}

	private void _setUpUpgradePortletPreferences() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			UpgradePortletPreferencesTest.class);

		Bundle journalContentWebBundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.journal.content.web");

		Assert.assertNotNull(
			"Unable to find journal-content-web bundle",
			journalContentWebBundle);

		Class<?> clazz = journalContentWebBundle.loadClass(
			"com.liferay.journal.content.web.internal.upgrade.v1_1_0." +
				"UpgradePortletPreferences");

		Constructor<?> constructor = clazz.getConstructor(
			GroupLocalService.class, JournalArticleLocalService.class,
			Language.class, LayoutLocalService.class, Portal.class);

		_upgradePortletPreferences = constructor.newInstance(
			_groupLocalService, _journalArticleLocalService, _language,
			_layoutLocalService, _portal);
	}

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	private Object _upgradePortletPreferences;

}