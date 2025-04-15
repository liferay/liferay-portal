/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.staging.configuration.StagingConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.test.util.WikiTestUtil;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletPreferences;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Tamas Molnar
 */
@RunWith(Arquillian.class)
public class StagingDataPortletPreferencesTest
	extends BaseLocalStagingTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			StagingConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishDisplayedContent", false
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			StagingConfiguration.class.getName());
	}

	@Test
	public void testDDLDisplayPortletPreferences() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		ServiceTracker<Portlet, Portlet> serviceTracker =
			ServiceTrackerFactory.open(
				bundle.getBundleContext(),
				StringBundler.concat(
					"(jakarta.portlet.name=", DDLPortletKeys.DYNAMIC_DATA_LISTS,
					")"),
				null);

		try {
			Assert.assertNotNull(serviceTracker.waitForService(10000));
		}
		finally {
			serviceTracker.close();
		}

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			stagingGroup.getGroupId(), DDLRecordSet.class.getName());

		DDMTemplate displayDDMTemplate = DDMTemplateTestUtil.addTemplate(
			stagingGroup.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(DDLRecordSet.class));
		DDMTemplate formDDMTemplate = DDMTemplateTestUtil.addTemplate(
			stagingGroup.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(DDLRecordSet.class));

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.addRecordSet(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			ddmStructure.getStructureId(), null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT,
			DDLRecordSetConstants.SCOPE_DYNAMIC_DATA_LISTS,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"displayDDMTemplateId",
			new String[] {String.valueOf(displayDDMTemplate.getTemplateId())}
		).put(
			"formDDMTemplateId",
			new String[] {String.valueOf(formDDMTemplate.getTemplateId())}
		).put(
			"groupId", new String[] {String.valueOf(ddlRecordSet.getGroupId())}
		).put(
			"recordSetId",
			new String[] {String.valueOf(ddlRecordSet.getRecordSetId())}
		).put(
			"recordSetKey",
			new String[] {String.valueOf(ddlRecordSet.getRecordSetKey())}
		).build();

		String portletId = publishLayoutWithDisplayPortlet(
			DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY, preferenceMap, true);

		Assert.assertEquals(
			String.valueOf(displayDDMTemplate.getTemplateId()),
			livePortletPreferences.getValue(
				"displayDDMTemplateId", StringPool.BLANK));
		Assert.assertEquals(
			String.valueOf(formDDMTemplate.getTemplateId()),
			livePortletPreferences.getValue(
				"formDDMTemplateId", StringPool.BLANK));
		Assert.assertEquals(
			String.valueOf(ddlRecordSet.getRecordSetKey()),
			livePortletPreferences.getValue("recordSetKey", StringPool.BLANK));

		publishPortlet(DDLPortletKeys.DYNAMIC_DATA_LISTS);

		publishLayoutWithDisplayPortlet(portletId, preferenceMap, false);

		DDMTemplate liveDisplayDDMTemplate =
			_ddmTemplateLocalService.getDDMTemplateByUuidAndGroupId(
				displayDDMTemplate.getUuid(), liveGroup.getGroupId());
		DDMTemplate liveFormDDMTemplate =
			_ddmTemplateLocalService.getDDMTemplateByUuidAndGroupId(
				formDDMTemplate.getUuid(), liveGroup.getGroupId());

		DDLRecordSet liveDDLRecordSet =
			_ddlRecordSetLocalService.getDDLRecordSetByUuidAndGroupId(
				ddlRecordSet.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			String.valueOf(liveDisplayDDMTemplate.getTemplateId()),
			livePortletPreferences.getValue(
				"displayDDMTemplateId", StringPool.BLANK));
		Assert.assertEquals(
			String.valueOf(liveFormDDMTemplate.getTemplateId()),
			livePortletPreferences.getValue(
				"formDDMTemplateId", StringPool.BLANK));
		Assert.assertEquals(
			String.valueOf(liveDDLRecordSet.getRecordSetKey()),
			livePortletPreferences.getValue("recordSetKey", StringPool.BLANK));
	}

	@Test
	public void testDDMFormPortletPreferences() throws Exception {
		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				stagingGroup, TestPropsValues.getUserId());

		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"formInstanceId",
			new String[] {String.valueOf(ddmFormInstance.getFormInstanceId())}
		).put(
			"groupId", new String[] {String.valueOf(stagingGroup.getGroupId())}
		).build();

		String portletId = publishLayoutWithDisplayPortlet(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM, preferenceMap, true);

		Assert.assertEquals(
			String.valueOf(ddmFormInstance.getFormInstanceId()),
			livePortletPreferences.getValue(
				"formInstanceId", StringPool.BLANK));

		publishPortlet(DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN);

		publishLayoutWithDisplayPortlet(portletId, preferenceMap, false);

		DDMFormInstance liveDDMFormInstance =
			_ddmFormInstanceLocalService.getDDMFormInstanceByUuidAndGroupId(
				ddmFormInstance.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			String.valueOf(liveDDMFormInstance.getFormInstanceId()),
			livePortletPreferences.getValue(
				"formInstanceId", StringPool.BLANK));
	}

	@Test
	public void testJournalContentDataPortletPreferences() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"articleExternalReferenceCode",
			new String[] {journalArticle.getExternalReferenceCode()}
		).put(
			"groupExternalReferenceCode",
			new String[] {stagingGroup.getExternalReferenceCode()}
		).build();

		String portletId = publishLayoutWithDisplayPortlet(
			JournalContentPortletKeys.JOURNAL_CONTENT, preferenceMap, true);

		Assert.assertEquals(
			journalArticle.getExternalReferenceCode(),
			livePortletPreferences.getValue(
				"articleExternalReferenceCode", StringPool.BLANK));

		publishPortlet(JournalPortletKeys.JOURNAL);

		publishLayoutWithDisplayPortlet(portletId, preferenceMap, false);

		JournalArticle liveJournalArticle =
			_journalArticleLocalService.getJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			liveJournalArticle.getExternalReferenceCode(),
			livePortletPreferences.getValue(
				"articleExternalReferenceCode", StringPool.BLANK));
	}

	@Test
	public void testWikiDisplayDataPortletPreferences() throws Exception {
		WikiNode wikiNode = WikiTestUtil.addNode(stagingGroup.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			wikiNode.getGroupId(), wikiNode.getNodeId(), true);

		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"nodeId", new String[] {String.valueOf(wikiPage.getNodeId())}
		).put(
			"title", new String[] {String.valueOf(wikiPage.getTitle())}
		).build();

		String portletId = publishLayoutWithDisplayPortlet(
			WikiPortletKeys.WIKI_DISPLAY, preferenceMap, true);

		Assert.assertEquals(
			String.valueOf(wikiPage.getNodeId()),
			livePortletPreferences.getValue("nodeId", StringPool.BLANK));
		Assert.assertEquals(
			wikiPage.getTitle(),
			livePortletPreferences.getValue("title", StringPool.BLANK));

		publishPortlet(WikiPortletKeys.WIKI);

		publishLayoutWithDisplayPortlet(portletId, preferenceMap, false);

		WikiNode liveWikiNode =
			_wikiNodeLocalService.getWikiNodeByUuidAndGroupId(
				wikiNode.getUuid(), liveGroup.getGroupId());

		WikiPage liveWikiPage =
			_wikiPageLocalService.getWikiPageByUuidAndGroupId(
				wikiPage.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			String.valueOf(liveWikiNode.getNodeId()),
			livePortletPreferences.getValue("nodeId", StringPool.BLANK));
		Assert.assertEquals(
			liveWikiPage.getTitle(),
			livePortletPreferences.getValue("title", StringPool.BLANK));
	}

	protected String publishLayoutWithDisplayPortlet(
			String portletId, Map<String, String[]> preferenceMap,
			boolean addPortlet)
		throws Exception {

		if (addPortlet) {
			portletId = LayoutTestUtil.addPortletToLayout(
				stagingLayout, portletId, preferenceMap);
		}

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()});

		publishLayouts(parameterMap);

		livePortletPreferences = LayoutTestUtil.getPortletPreferences(
			liveLayout, portletId);

		return portletId;
	}

	protected PortletPreferences livePortletPreferences;

	@Inject
	private DDLRecordSetLocalService _ddlRecordSetLocalService;

	@Inject
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private WikiNodeLocalService _wikiNodeLocalService;

	@Inject
	private WikiPageLocalService _wikiPageLocalService;

}