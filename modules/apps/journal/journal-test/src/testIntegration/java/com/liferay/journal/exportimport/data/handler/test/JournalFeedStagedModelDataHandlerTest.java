/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.service.JournalFeedLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 */
@RunWith(Arquillian.class)
public class JournalFeedStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layout = LayoutTestUtil.addTypePortletLayout(stagingGroup);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUuid(_layout.getUuid());

		LayoutLocalServiceUtil.addLayout(
			_layout.getExternalReferenceCode(), TestPropsValues.getUserId(),
			liveGroup.getGroupId(), _layout.isPrivateLayout(),
			_layout.getParentLayoutId(), _layout.getName(), _layout.getTitle(),
			_layout.getDescription(), _layout.getType(), _layout.isHidden(),
			_layout.getFriendlyURL(), serviceContext);

		serviceContext.setCompanyId(TestPropsValues.getCompanyId());

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				TestPropsValues.getUserId(), true);

		_originalPortalPreferencesXML = PortletPreferencesFactoryUtil.toXML(
			portalPreferences);

		portalPreferences.setValue("", "publishToLiveByDefaultEnabled", "true");
		portalPreferences.setValue(
			"", "versionHistoryByDefaultEnabled", "true");
		portalPreferences.setValue("", "articleCommentsEnabled", "true");
		portalPreferences.setValue(
			"", "expireAllArticleVersionsEnabled", "true");
		portalPreferences.setValue("", "folderIconCheckCountEnabled", "true");
		portalPreferences.setValue(
			"", "indexAllArticleVersionsEnabled", "true");
		portalPreferences.setValue(
			"", "journalArticlePageBreakToken", "@page_break@");

		PortalPreferencesLocalServiceUtil.updatePreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			PortletPreferencesFactoryUtil.toXML(portalPreferences));
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		PortalPreferencesLocalServiceUtil.updatePreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			_originalPortalPreferencesXML);
	}

	@Override
	@Test
	public void testCleanStagedModelDataHandler() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.journal.internal.exportimport.data.handler." +
					"JournalFeedStagedModelDataHandler",
				LoggerTestUtil.WARN)) {

			super.testCleanStagedModelDataHandler();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message, message.startsWith("A feed with the ID "));
			Assert.assertTrue(
				message,
				message.contains(" already exists. The new generated ID is "));
		}
	}

	@Test
	public void testDoubleExportImport() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.journal.internal.exportimport.data.handler." +
					"JournalFeedStagedModelDataHandler",
				LoggerTestUtil.WARN)) {

			Map<String, List<StagedModel>> dependentStagedModelsMap =
				addDependentStagedModelsMap(stagingGroup);

			StagedModel stagedModel = addStagedModel(
				stagingGroup, dependentStagedModelsMap);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				exportImportStagedModel(stagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			StagedModel importedStagedModel = getStagedModel(
				stagedModel.getUuid(), liveGroup);

			Assert.assertNotNull(importedStagedModel);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				exportImportStagedModel(stagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			importedStagedModel = getStagedModel(
				stagedModel.getUuid(), liveGroup);

			Assert.assertNotNull(importedStagedModel);
		}
	}

	@Test
	public void testExportImport() throws Exception {
		StagedModel stagedModel = addStagedModel(
			stagingGroup, addDependentStagedModelsMap(stagingGroup));

		exportStagedModel(stagedModel);

		LayoutLocalServiceUtil.deleteLayout(
			LayoutLocalServiceUtil.getLayoutByExternalReferenceCode(
				_layout.getExternalReferenceCode(), liveGroup.getGroupId()));

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			Element feedElement =
				portletDataContext.getImportDataStagedModelElement(stagedModel);

			Assert.assertEquals(
				_layout.getExternalReferenceCode(),
				feedElement.attributeValue("targetLayoutERC"));

			StagedModel exportedStagedModel = readExportedStagedModel(
				stagedModel);

			Assert.assertNotNull(exportedStagedModel);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.exportimport.internal.lifecycle." +
						"LoggerExportImportLifecycleListener",
					LoggerTestUtil.ERROR)) {

				AssertUtils.assertFailure(
					PortletDataException.class,
					StringBundler.concat(
						"No Layout exists with the key ",
						"{externalReferenceCode=",
						_layout.getExternalReferenceCode(), ", groupId=",
						liveGroup.getGroupId(), "}"),
					() -> StagedModelDataHandlerUtil.importStagedModel(
						portletDataContext, exportedStagedModel));

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				String message = logEntry.getMessage();

				Assert.assertTrue(
					message,
					message.startsWith(
						"Staged model {class: " + JournalFeed.class.getName()));
				Assert.assertTrue(message, message.endsWith("import failed"));
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUuid(_layout.getUuid());

		LayoutLocalServiceUtil.addLayout(
			_layout.getExternalReferenceCode(), TestPropsValues.getUserId(),
			liveGroup.getGroupId(), _layout.isPrivateLayout(),
			_layout.getParentLayoutId(), _layout.getName(), _layout.getTitle(),
			_layout.getDescription(), _layout.getType(), _layout.isHidden(),
			_layout.getFriendlyURL(), serviceContext);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				stagedModel);

			Assert.assertNotNull(exportedStagedModel);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			JournalFeed importedFeed =
				JournalFeedLocalServiceUtil.fetchJournalFeedByUuidAndGroupId(
					stagedModel.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(importedFeed);

			validateImportedStagedModel(stagedModel, importedFeed);
		}
	}

	@Test
	public void testExportImportWithTargetLayoutFromAnotherGroup()
		throws Exception {

		_otherGroup = GroupTestUtil.addGroup();

		Layout otherLayout = LayoutTestUtil.addTypePortletLayout(_otherGroup);

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		JournalFeed feed = (JournalFeed)addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		String targetLayoutFriendlyURL =
			PortalUtil.getPathFriendlyURLPublic() +
				_otherGroup.getFriendlyURL() + otherLayout.getFriendlyURL();

		feed.setTargetLayoutFriendlyUrl(targetLayoutFriendlyURL);

		feed = JournalFeedLocalServiceUtil.updateJournalFeed(feed);

		exportStagedModel(feed);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			Element feedElement =
				portletDataContext.getImportDataStagedModelElement(feed);

			Assert.assertEquals(
				otherLayout.getExternalReferenceCode(),
				feedElement.attributeValue("targetLayoutERC"));
			Assert.assertEquals(
				_otherGroup.getExternalReferenceCode(),
				feedElement.attributeValue("targetLayoutGroupERC"));

			StagedModel exportedStagedModel = readExportedStagedModel(feed);

			Assert.assertNotNull(exportedStagedModel);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			JournalFeed importedFeed =
				JournalFeedLocalServiceUtil.fetchJournalFeedByUuidAndGroupId(
					feed.getUuid(), liveGroup.getGroupId());

			Assert.assertEquals(
				targetLayoutFriendlyURL,
				importedFeed.getTargetLayoutFriendlyUrl());
		}
	}

	@Override
	@Test
	public void testStagedModelDataHandler() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.journal.internal.exportimport.data.handler." +
					"JournalFeedStagedModelDataHandler",
				LoggerTestUtil.WARN)) {

			super.testStagedModelDataHandler();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message, message.startsWith("A feed with the ID "));
			Assert.assertTrue(
				message,
				message.contains(" already exists. The new generated ID is "));
		}
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new LinkedHashMap<>();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), JournalArticle.class.getName());

		for (int i = 0; i < 2; i++) {
			DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
				group.getGroupId(), ddmStructure.getStructureId(),
				PortalUtil.getClassNameId(JournalArticle.class));

			addDependentStagedModel(
				dependentStagedModelsMap, DDMTemplate.class, ddmTemplate);
		}

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> ddmStructureDependentStagedModels =
			dependentStagedModelsMap.get(DDMStructure.class.getSimpleName());

		DDMStructure ddmStructure =
			(DDMStructure)ddmStructureDependentStagedModels.get(0);

		List<StagedModel> ddmTemplateDependentStagedModels =
			dependentStagedModelsMap.get(DDMTemplate.class.getSimpleName());

		DDMTemplate ddmTemplate =
			(DDMTemplate)ddmTemplateDependentStagedModels.get(0);

		DDMTemplate rendererDDMTemplate =
			(DDMTemplate)ddmTemplateDependentStagedModels.get(1);

		return JournalTestUtil.addFeed(
			group.getGroupId(), _layout.getPlid(),
			RandomTestUtil.randomString(), ddmStructure.getStructureId(),
			ddmTemplate.getTemplateKey(), rendererDDMTemplate.getTemplateKey());
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return JournalFeedLocalServiceUtil.getJournalFeedByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return JournalFeed.class;
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> ddmStructureDependentStagedModels =
			dependentStagedModelsMap.get(DDMStructure.class.getSimpleName());

		Assert.assertEquals(
			ddmStructureDependentStagedModels.toString(), 1,
			ddmStructureDependentStagedModels.size());

		DDMStructure ddmStructure =
			(DDMStructure)ddmStructureDependentStagedModels.get(0);

		DDMStructureLocalServiceUtil.getDDMStructureByUuidAndGroupId(
			ddmStructure.getUuid(), group.getGroupId());

		List<StagedModel> ddmTemplateDependentStagedModels =
			dependentStagedModelsMap.get(DDMTemplate.class.getSimpleName());

		Assert.assertEquals(
			ddmTemplateDependentStagedModels.toString(), 2,
			ddmTemplateDependentStagedModels.size());

		for (StagedModel ddmTemplateDependentStagedModel :
				ddmTemplateDependentStagedModels) {

			DDMTemplate ddmTemplate =
				(DDMTemplate)ddmTemplateDependentStagedModel;

			DDMTemplateLocalServiceUtil.getDDMTemplateByUuidAndGroupId(
				ddmTemplate.getUuid(), group.getGroupId());
		}
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		JournalFeed feed = (JournalFeed)stagedModel;
		JournalFeed importedFeed = (JournalFeed)importedStagedModel;

		Assert.assertEquals(feed.getName(), importedFeed.getName());
		Assert.assertEquals(feed.getDelta(), importedFeed.getDelta());
		Assert.assertEquals(feed.getOrderByCol(), importedFeed.getOrderByCol());
		Assert.assertEquals(
			feed.getOrderByType(), importedFeed.getOrderByType());
		Assert.assertEquals(
			feed.getContentField(), importedFeed.getContentField());
	}

	private Layout _layout;
	private String _originalPortalPreferencesXML;

	@DeleteAfterTestRun
	private Group _otherGroup;

}