/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class JournalContentExportImportTest
	extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	public String getPortletId() throws Exception {
		return PortletIdCodec.encode(
			JournalContentPortletKeys.JOURNAL_CONTENT,
			RandomTestUtil.randomString());
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

	@Test
	@TestInfo("LPD-98716")
	public void testExportImportGroupExternalReferenceCodeFromStagedGroup()
		throws Exception {

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		Group stagingGroup = group.getStagingGroup();

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"articleExternalReferenceCode",
				new String[] {RandomTestUtil.randomString()}
			).put(
				"groupExternalReferenceCode",
				new String[] {stagingGroup.getExternalReferenceCode()}
			).build());

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			portletPreferences.getValue("groupExternalReferenceCode", null));
	}

	@Test
	@TestInfo("LPD-98716")
	public void testExportImportGroupExternalReferenceCodeWithoutStaging()
		throws Exception {

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		Group stagingGroup = group.getStagingGroup();

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"articleExternalReferenceCode",
				new String[] {RandomTestUtil.randomString()}
			).put(
				"groupExternalReferenceCode",
				new String[] {stagingGroup.getExternalReferenceCode()}
			).build(),
			false);

		Assert.assertEquals(
			stagingGroup.getExternalReferenceCode(),
			portletPreferences.getValue("groupExternalReferenceCode", null));
	}

	@Test
	@TestInfo("LPD-103207")
	public void testExportImportSelectedArticleWithLayoutStaging()
		throws Exception {

		JournalArticle article = JournalTestUtil.addArticle(
			group.getGroupId(), 0);

		ExportImportThreadLocal.setLayoutStagingInProcess(true);

		try {
			_exportImportSelectedArticle(article, false);
		}
		finally {
			ExportImportThreadLocal.setLayoutStagingInProcess(false);
		}

		Assert.assertNull(
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					importedGroup.getGroupId(),
					article.getExternalReferenceCode()));
	}

	@Test
	@TestInfo("LPD-103207")
	public void testExportImportSelectedArticleWithoutStaging()
		throws Exception {

		JournalArticle article = JournalTestUtil.addArticle(
			group.getGroupId(), 0);

		_exportImportSelectedArticle(article, false);

		Assert.assertNotNull(
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					importedGroup.getGroupId(),
					article.getExternalReferenceCode()));
	}

	@Test
	@TestInfo("LPD-103207")
	public void testExportImportSelectedArticleWithPortletStaging()
		throws Exception {

		JournalArticle article = JournalTestUtil.addArticle(
			group.getGroupId(), 0);

		_exportImportSelectedArticle(article, true);

		JournalArticle importedArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					importedGroup.getGroupId(),
					article.getExternalReferenceCode());

		Assert.assertEquals(
			article.getTitle(LocaleUtil.getSiteDefault()),
			importedArticle.getTitle(LocaleUtil.getSiteDefault()));
		Assert.assertEquals(article.getContent(), importedArticle.getContent());
	}

	@Test
	@TestInfo("LPD-98716")
	public void testGetPortletConfigurationWithLocalStaging() throws Exception {
		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		Group stagingGroup = group.getStagingGroup();

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			_getExportedGroupExternalReferenceCode(
				stagingGroup.getExternalReferenceCode()));

		ExportImportThreadLocal.setPortletStagingInProcess(false);
	}

	@Test
	@TestInfo("LPD-98716")
	public void testGetPortletConfigurationWithLocalStagingFromDifferentGroup()
		throws Exception {

		Group contentGroup = GroupTestUtil.addGroup();

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), contentGroup, false, false,
			new ServiceContext());

		Group stagingGroup = contentGroup.getStagingGroup();

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		Assert.assertEquals(
			contentGroup.getExternalReferenceCode(),
			_getExportedGroupExternalReferenceCode(
				stagingGroup.getExternalReferenceCode()));

		ExportImportThreadLocal.setPortletStagingInProcess(false);

		GroupTestUtil.deleteGroup(contentGroup);
	}

	@Test
	@TestInfo("LPD-98716")
	public void testGetPortletConfigurationWithoutStaging() throws Exception {
		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		Group stagingGroup = group.getStagingGroup();

		Assert.assertEquals(
			stagingGroup.getExternalReferenceCode(),
			_getExportedGroupExternalReferenceCode(
				stagingGroup.getExternalReferenceCode()));
	}

	@Test
	@TestInfo("LPD-98716")
	public void testGetPortletConfigurationWithRemoteStaging()
		throws Exception {

		String remoteGroupExternalReferenceCode = RandomTestUtil.randomString();

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"remoteGroupExternalReferenceCode",
			remoteGroupExternalReferenceCode);
		typeSettingsUnicodeProperties.setProperty("staged", "true");
		typeSettingsUnicodeProperties.setProperty("stagedRemotely", "true");

		_groupLocalService.updateGroup(
			group.getGroupId(), typeSettingsUnicodeProperties.toString());

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		Assert.assertEquals(
			remoteGroupExternalReferenceCode,
			_getExportedGroupExternalReferenceCode(
				group.getExternalReferenceCode()));

		ExportImportThreadLocal.setPortletStagingInProcess(false);
	}

	private void _exportImportSelectedArticle(
			JournalArticle article, boolean portletStagingInProcess)
		throws Exception {

		exportImportPortlet(
			LayoutTestUtil.addPortletToLayout(
				TestPropsValues.getUserId(), layout, getPortletId(), "column-1",
				HashMapBuilder.put(
					"articleExternalReferenceCode",
					new String[] {article.getExternalReferenceCode()}
				).build()),
			portletStagingInProcess);
	}

	private Object _getExportedGroupExternalReferenceCode(
			String groupExternalReferenceCode)
		throws Exception {

		String portletId = LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout, getPortletId(), "column-1",
			HashMapBuilder.put(
				"groupExternalReferenceCode",
				new String[] {groupExternalReferenceCode}
			).build());

		Map<String, Object> portletConfiguration =
			_portletPreferencesPortletConfigurationExporter.
				getPortletConfiguration(layout.getPlid(), portletId);

		return portletConfiguration.get("groupExternalReferenceCode");
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private PortletPreferencesPortletConfigurationExporter
		_portletPreferencesPortletConfigurationExporter;

}