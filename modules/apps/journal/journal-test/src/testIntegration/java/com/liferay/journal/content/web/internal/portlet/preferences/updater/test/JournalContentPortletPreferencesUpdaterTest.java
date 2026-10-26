/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.portlet.preferences.updater.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.portlet.preferences.updater.PortletPreferencesUpdater;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Chaitanya Sammetla
 */
@RunWith(Arquillian.class)
public class JournalContentPortletPreferencesUpdaterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testUpdatePortletPreferencesWhenScopeIsCompany()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		_companyJournalArticle = JournalTestUtil.addArticle(
			companyGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Map<String, String[]> portletPreferencesMap = HashMapBuilder.put(
			"lfrScopeType", new String[] {"company"}
		).build();

		PortletPreferences portletPreferences = _updatePortletPreferences(
			_companyJournalArticle, portletPreferencesMap);

		Assert.assertEquals(
			_companyJournalArticle.getExternalReferenceCode(),
			portletPreferences.getValue("articleExternalReferenceCode", null));
		Assert.assertEquals(
			companyGroup.getExternalReferenceCode(),
			portletPreferences.getValue("groupExternalReferenceCode", null));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		portletPreferences = _updatePortletPreferences(
			journalArticle, portletPreferencesMap);

		Assert.assertEquals(
			journalArticle.getExternalReferenceCode(),
			portletPreferences.getValue("articleExternalReferenceCode", null));
		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			portletPreferences.getValue("groupExternalReferenceCode", null));
	}

	@Test
	public void testUpdatePortletPreferencesWhenScopeIsLayout()
		throws Exception {

		Group scopeGroup = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), _layout);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		JournalArticle journalArticle1 =
			JournalTestUtil.addArticleWithXMLContent(
				scopeGroup.getGroupId(),
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), null);

		Map<String, String[]> portletPreferencesMap = HashMapBuilder.put(
			"lfrScopeLayoutUuid", new String[] {_layout.getUuid()}
		).put(
			"lfrScopeType", new String[] {"layout"}
		).build();

		PortletPreferences portletPreferences = _updatePortletPreferences(
			journalArticle1, portletPreferencesMap);

		Assert.assertEquals(
			journalArticle1.getExternalReferenceCode(),
			portletPreferences.getValue("articleExternalReferenceCode", null));
		Assert.assertEquals(
			scopeGroup.getExternalReferenceCode(),
			portletPreferences.getValue("groupExternalReferenceCode", null));

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		portletPreferences = _updatePortletPreferences(
			journalArticle2, portletPreferencesMap);

		Assert.assertEquals(
			journalArticle2.getExternalReferenceCode(),
			portletPreferences.getValue("articleExternalReferenceCode", null));
		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			portletPreferences.getValue("groupExternalReferenceCode", null));
	}

	private PortletPreferences _updatePortletPreferences(
			JournalArticle journalArticle,
			Map<String, String[]> portletPreferencesMap)
		throws Exception {

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, JournalContentPortletKeys.JOURNAL_CONTENT,
			portletPreferencesMap);

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout, portletId);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(_layout);

		_portletPreferencesUpdater.updatePortletPreferences(
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			portletId, portletPreferences, themeDisplay);

		return portletPreferences;
	}

	@DeleteAfterTestRun
	private JournalArticle _companyJournalArticle;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject(
		filter = "model.class.name=com.liferay.journal.model.JournalArticle"
	)
	private PortletPreferencesUpdater _portletPreferencesUpdater;

}