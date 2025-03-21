/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.upgrade.v1_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
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

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testUpgradePortletPreferences() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_assertUpgrade(
			HashMapBuilder.put(
				"articleId", journalArticle.getArticleId()
			).put(
				"groupId", String.valueOf(journalArticle.getGroupId())
			).build(),
			HashMapBuilder.put(
				"articleExternalReferenceCode",
				journalArticle.getExternalReferenceCode()
			).put(
				"groupExternalReferenceCode", _group.getExternalReferenceCode()
			).build());
	}

	@Test
	public void testUpgradePortletPreferencesWithCompanyContentAndCompanyTemplate()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			companyGroup.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			companyGroup.getGroupId(),
			_portal.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				companyGroup.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());

		_assertUpgrade(
			HashMapBuilder.put(
				"articleId", journalArticle.getArticleId()
			).put(
				"ddmTemplateKey", ddmTemplate.getTemplateKey()
			).put(
				"groupId", String.valueOf(journalArticle.getGroupId())
			).build(),
			HashMapBuilder.put(
				"articleExternalReferenceCode",
				journalArticle.getExternalReferenceCode()
			).put(
				"ddmTemplateExternalReferenceCode",
				ddmTemplate.getExternalReferenceCode()
			).put(
				"groupExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			).build());
	}

	@Test
	public void testUpgradePortletPreferencesWithCompanyContentAndGroupTemplate()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			companyGroup.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			companyGroup.getGroupId(),
			_portal.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		DDMTemplate ddmTemplate2 = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), _portal.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				companyGroup.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate1.getTemplateKey());

		_assertUpgrade(
			HashMapBuilder.put(
				"articleId", journalArticle.getArticleId()
			).put(
				"ddmTemplateKey", ddmTemplate2.getTemplateKey()
			).put(
				"groupId", String.valueOf(journalArticle.getGroupId())
			).build(),
			HashMapBuilder.put(
				"articleExternalReferenceCode",
				journalArticle.getExternalReferenceCode()
			).put(
				"ddmTemplateExternalReferenceCode",
				ddmTemplate2.getExternalReferenceCode()
			).put(
				"groupExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			).build());
	}

	@Test
	public void testUpgradePortletPreferencesWithGroupContentAndGroupTemplate()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), _portal.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());

		_assertUpgrade(
			HashMapBuilder.put(
				"articleId", journalArticle.getArticleId()
			).put(
				"ddmTemplateKey", ddmTemplate.getTemplateKey()
			).put(
				"groupId", String.valueOf(journalArticle.getGroupId())
			).build(),
			HashMapBuilder.put(
				"articleExternalReferenceCode",
				journalArticle.getExternalReferenceCode()
			).put(
				"ddmTemplateExternalReferenceCode",
				ddmTemplate.getExternalReferenceCode()
			).put(
				"groupExternalReferenceCode", _group.getExternalReferenceCode()
			).build());
	}

	private void _assertPortletPreferences(
			String portletId, Map<String, String> portletPreferencesMap)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, portletId);

		for (Map.Entry<String, String> entry :
				portletPreferencesMap.entrySet()) {

			Assert.assertEquals(
				entry.getValue(),
				portletPreferences.getValue(entry.getKey(), null));
		}
	}

	private void _assertUpgrade(
			Map<String, String> actualPortletPreferencesMap,
			Map<String, String> expectedPortletPreferencesMapMap)
		throws Exception {

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, JournalContentPortletKeys.JOURNAL_CONTENT,
			_getPreferenceMap(actualPortletPreferencesMap));

		_assertPortletPreferences(portletId, actualPortletPreferencesMap);

		_runUpgrade();

		_assertPortletPreferences(portletId, expectedPortletPreferencesMapMap);
	}

	private Map<String, String[]> _getPreferenceMap(Map<String, String> map) {
		Map<String, String[]> preferenceMap = new HashMap<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			preferenceMap.put(entry.getKey(), new String[] {entry.getValue()});
		}

		return preferenceMap;
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_entityCache.clearCache();
			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.journal.content.web.internal.upgrade.v1_1_1." +
			"UpgradePortletPreferences";

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "(&(component.name=com.liferay.journal.content.web.internal.upgrade.registry.JournalContentWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}