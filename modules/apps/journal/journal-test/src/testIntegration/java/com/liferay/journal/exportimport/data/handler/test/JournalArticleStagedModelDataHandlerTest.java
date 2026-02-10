/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseWorkflowedStagedModelDataHandlerTestCase;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.service.persistence.JournalArticleResourceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.PortletPreferences;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Daniel Kocsis
 */
@RunWith(Arquillian.class)
public class JournalArticleStagedModelDataHandlerTest
	extends BaseWorkflowedStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public boolean isAssetPrioritySupported() {
		return true;
	}

	@Test
	public void testArticleCreatedBeforeImportingLayoutDependencies()
		throws Exception {

		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout layout = LayoutTestUtil.addTypePortletLayout(stagingGroup);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, journalArticle, layout,
			PortletDataContext.REFERENCE_TYPE_DEPENDENCY);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			StagedModelDataHandler<Layout>
				originalLayoutStagedModelDataHandler =
					(StagedModelDataHandler<Layout>)
						StagedModelDataHandlerRegistryUtil.
							getStagedModelDataHandler(Layout.class.getName());

			TestLayoutStagedModelDataHandler testLayoutStagedModelDataHandler =
				new TestLayoutStagedModelDataHandler(
					originalLayoutStagedModelDataHandler);

			Bundle bundle = FrameworkUtil.getBundle(getClass());

			BundleContext bundleContext = bundle.getBundleContext();

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					StagedModelDataHandler.class,
					testLayoutStagedModelDataHandler,
					MapUtil.singletonDictionary("service.ranking", 100));

			try {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);

				serviceRegistration.unregister();
			}

			JournalArticle importJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(importJournalArticle);

			Map<Long, Long> primaryKeys =
				testLayoutStagedModelDataHandler.getPrimaryKeys();

			Assert.assertNotNull(primaryKeys);

			long importedResourcePrimKey = MapUtil.getLong(
				primaryKeys, journalArticle.getResourcePrimKey());

			Assert.assertEquals(
				importJournalArticle.getResourcePrimKey(),
				importedResourcePrimKey);
		}
	}

	@Test
	public void testArticleKeepsExternalReferenceCode() throws Exception {
		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			boolean portletImportInProcess =
				ExportImportThreadLocal.isPortletImportInProcess();

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(
					portletImportInProcess);
			}

			JournalArticle importedJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertEquals(
				journalArticle.getExternalReferenceCode(),
				importedJournalArticle.getExternalReferenceCode());
		}

		initExport();

		journalArticle = JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString());

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			boolean portletImportInProcess =
				ExportImportThreadLocal.isPortletImportInProcess();

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(
					portletImportInProcess);
			}

			JournalArticle importedJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertEquals(
				journalArticle.getExternalReferenceCode(),
				importedJournalArticle.getExternalReferenceCode());
		}
	}

	@Test
	public void testArticleRecoversExternalReferenceCode() throws Exception {
		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		User user = UserTestUtil.addUser(
			RandomTestUtil.randomString(4), liveGroup.getGroupId());

		journalArticle.setStatusByUserId(user.getUserId());
		journalArticle.setStatusByUserName(user.getFullName());

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			JournalArticle importJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			importJournalArticle.setExternalReferenceCode(
				RandomTestUtil.randomString());

			importJournalArticle =
				_journalArticleLocalService.updateJournalArticle(
					importJournalArticle);

			Assert.assertNotEquals(
				journalArticle,
				importJournalArticle.getExternalReferenceCode());
		}

		initExport();

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			JournalArticle importJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertEquals(
				journalArticle.getExternalReferenceCode(),
				importJournalArticle.getExternalReferenceCode());
		}
	}

	@Test
	public void testArticlesWithSameResourceUUID() throws Exception {
		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(liveGroup.getGroupId());

		serviceContext.setAttribute(
			"articleResourceUuid", journalArticle.getArticleResourceUuid());
		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		JournalArticle importJournalArticle = JournalTestUtil.addArticle(
			liveGroup.getGroupId(), journalArticle.getFolderId(),
			serviceContext);

		Assert.assertEquals(
			journalArticle.getArticleResourceUuid(),
			importJournalArticle.getArticleResourceUuid());
		Assert.assertEquals(
			liveGroup.getGroupId(), importJournalArticle.getGroupId());
		Assert.assertNotEquals(
			journalArticle.getUuid(), importJournalArticle.getUuid());

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			importJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(importJournalArticle);
			Assert.assertEquals(
				journalArticle.getVersion(), importJournalArticle.getVersion(),
				0D);
		}
	}

	@Test
	public void testArticleWithAssetDisplayPageEntry() throws Exception {
		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				stagingGroup.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(stagingGroup.getGroupId());

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			journalArticle.getUserId(), stagingGroup.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC, serviceContext);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					stagingGroup.getGroupId(),
					layoutPageTemplateEntry.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateStructure.getPlid());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, layoutPageTemplateStructure.getUserId(),
				stagingGroup.getGroupId(), null, null, null,
				defaultSegmentsExperienceId,
				layoutPageTemplateStructure.getPlid(), StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				JSONUtil.put(
					"instanceId", StringUtil.randomId()
				).put(
					"portletId", JournalContentPortletKeys.JOURNAL_CONTENT
				).toString(),
				StringPool.BLANK, 0, StringPool.BLANK,
				FragmentConstants.TYPE_PORTLET, serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			layoutStructure.getMainItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(),
				layoutPageTemplateStructure.getGroupId(),
				layoutPageTemplateStructure.getPlid(),
				defaultSegmentsExperienceId, layoutStructure.toString());

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				stagingGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, 0,
				JournalContentPortletKeys.JOURNAL_CONTENT);

		portletPreferences.setValue(
			"articleExternalReferenceCode",
			journalArticle.getExternalReferenceCode());
		portletPreferences.setValue(
			"groupExternalReferenceCode",
			stagingGroup.getExternalReferenceCode());

		_portletPreferencesLocalService.addPortletPreferences(
			stagingGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			layoutPageTemplateStructure.getPlid(),
			JournalContentPortletKeys.JOURNAL_CONTENT,
			_portletLocalService.fetchPortletById(
				stagingGroup.getCompanyId(),
				JournalContentPortletKeys.JOURNAL_CONTENT),
			PortletPreferencesFactoryUtil.toXML(portletPreferences));

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			boolean portletImportInProcess =
				ExportImportThreadLocal.isPortletImportInProcess();

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(
					portletImportInProcess);
			}

			JournalArticle importedJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(
				_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
					liveGroup.getGroupId(),
					_portal.getClassNameId(JournalArticle.class.getName()),
					importedJournalArticle.getResourcePrimKey()));
		}
	}

	@Test
	public void testArticleWithSmallImageURL() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		journalArticle.setSmallImage(true);
		journalArticle.setSmallImageURL(RandomTestUtil.randomString());

		journalArticle = JournalTestUtil.updateArticle(journalArticle);

		exportImportStagedModel(journalArticle);
	}

	@Test
	public void testCircularDependencyBetweenAssetDisplayPageEntryAndConfiguredWebContentDisplayPortlet()
		throws Exception {

		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				stagingGroup.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(stagingGroup.getGroupId());

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			journalArticle.getUserId(), stagingGroup.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC, serviceContext);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					stagingGroup.getGroupId(),
					layoutPageTemplateEntry.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateStructure.getPlid());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, layoutPageTemplateStructure.getUserId(),
				stagingGroup.getGroupId(), null, null, null,
				defaultSegmentsExperienceId,
				layoutPageTemplateStructure.getPlid(), StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				JSONUtil.put(
					"instanceId", StringUtil.randomId()
				).put(
					"portletId", JournalContentPortletKeys.JOURNAL_CONTENT
				).toString(),
				StringPool.BLANK, 0, StringPool.BLANK,
				FragmentConstants.TYPE_PORTLET, serviceContext);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			layoutStructure.getMainItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(),
				layoutPageTemplateStructure.getGroupId(),
				layoutPageTemplateStructure.getPlid(),
				defaultSegmentsExperienceId, layoutStructure.toString());

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				stagingGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, 0,
				JournalContentPortletKeys.JOURNAL_CONTENT);

		portletPreferences.setValue(
			"articleExternalReferenceCode",
			journalArticle.getExternalReferenceCode());
		portletPreferences.setValue(
			"groupExternalReferenceCode",
			stagingGroup.getExternalReferenceCode());

		_portletPreferencesLocalService.addPortletPreferences(
			stagingGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			layoutPageTemplateStructure.getPlid(),
			JournalContentPortletKeys.JOURNAL_CONTENT,
			_portletLocalService.fetchPortletById(
				stagingGroup.getCompanyId(),
				JournalContentPortletKeys.JOURNAL_CONTENT),
			PortletPreferencesFactoryUtil.toXML(portletPreferences));

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			boolean portletImportInProcess =
				ExportImportThreadLocal.isPortletImportInProcess();

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(
					portletImportInProcess);
			}

			JournalArticle importedJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(importedJournalArticle);

			LayoutPageTemplateEntry importedLayoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						liveGroup.getGroupId(),
						layoutPageTemplateEntry.
							getLayoutPageTemplateEntryKey());

			Assert.assertNotNull(importedLayoutPageTemplateEntry);

			LayoutPageTemplateStructure importedLayoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(
						liveGroup.getGroupId(),
						importedLayoutPageTemplateEntry.getPlid());

			Assert.assertNotNull(importedLayoutPageTemplateStructure);

			PortletPreferences importedPortletPreferences =
				_portletPreferencesLocalService.getPreferences(
					stagingGroup.getCompanyId(),
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
					importedLayoutPageTemplateEntry.getPlid(),
					JournalContentPortletKeys.JOURNAL_CONTENT);

			Assert.assertNotNull(importedPortletPreferences);
			Assert.assertEquals(
				importedJournalArticle.getExternalReferenceCode(),
				importedPortletPreferences.getValue(
					"articleExternalReferenceCode", null));
		}
	}

	@Override
	@Test
	public void testCleanAssetCategoriesAndTags() throws Exception {
		ExportImportThreadLocal.setLayoutImportInProcess(true);

		try {
			super.testCleanAssetCategoriesAndTags();
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}
	}

	@Test
	public void testCompanyScopeDependencies() throws Exception {
		initExport();

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addCompanyDependencies();

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				stagedModel);

			Assert.assertNotNull(exportedStagedModel);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStagedModel);

			validateCompanyDependenciesImport(
				dependentStagedModelsMap, liveGroup);
		}
	}

	@Test
	public void testCopyAsNewCreatesNewJournalArticle() throws Exception {
		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			portletDataContext.setDataStrategy(
				PortletDataHandlerKeys.DATA_STRATEGY_COPY_AS_NEW);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStagedModel);
		}

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			portletDataContext.setDataStrategy(
				PortletDataHandlerKeys.DATA_STRATEGY_COPY_AS_NEW);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStagedModel);

			List<JournalArticle> articles =
				_journalArticleLocalService.getArticles(liveGroup.getGroupId());

			Assert.assertEquals(articles.toString(), 2, articles.size());
		}
	}

	@Test
	public void testExpiredArticleWithLastVersionDraftStatus()
		throws Exception {

		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticleWithWorkflow(
			stagingGroup.getGroupId(), true);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				stagingGroup.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(stagingGroup.getGroupId());

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			journalArticle.getUserId(), stagingGroup.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_DEFAULT, serviceContext);

		journalArticle = updateArticleStatus(
			journalArticle, WorkflowConstants.STATUS_APPROVED,
			journalArticle.getUserId());

		journalArticle = _journalArticleLocalService.expireArticle(
			journalArticle.getUserId(), stagingGroup.getGroupId(),
			journalArticle.getArticleId(), journalArticle.getVersion(),
			journalArticle.getUrlTitle(), serviceContext);

		// Do not assign to the "journalArticle" variable because the draft
		// status is not exportable

		updateArticleStatus(
			journalArticle, WorkflowConstants.STATUS_DRAFT,
			journalArticle.getUserId());

		portletDataContext.setPortletId(JournalPortletKeys.JOURNAL);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			boolean portletImportInProcess =
				ExportImportThreadLocal.isPortletImportInProcess();

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(
					portletImportInProcess);
			}

			JournalArticle importJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(importJournalArticle);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EXPIRED,
				importJournalArticle.getStatus());
		}
	}

	@Test
	public void testFileEntryFriendlyURLRetained() throws Exception {
		initExport();

		DLFolder dlFolder = DLTestUtil.addDLFolder(stagingGroup.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		_dlFileEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), dlFileEntry,
			dlFileEntry.getLatestFileVersion(true),
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(dlFolder.getGroupId()),
			new HashMap<>());

		FriendlyURLEntry mainFriendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				dlFileEntry.getFileEntryId());

		String stagingGroupDLFileEntryFriendlyURL = StringBundler.concat(
			"http://localhost:8080/documents/d", stagingGroup.getFriendlyURL(),
			StringPool.SLASH, mainFriendlyURLEntry.getUrlTitle());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			"<a href=\"" + stagingGroupDLFileEntryFriendlyURL + "\">Link</a>");

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			JournalArticle importedJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(importedJournalArticle);

			String content = journalArticle.getContent();

			String liveGroupDLFileEntryFriendlyURL = StringBundler.concat(
				"http://localhost:8080/documents/d", liveGroup.getFriendlyURL(),
				StringPool.SLASH, mainFriendlyURLEntry.getUrlTitle());

			Assert.assertEquals(
				content.replaceAll(
					stagingGroupDLFileEntryFriendlyURL,
					liveGroupDLFileEntryFriendlyURL),
				importedJournalArticle.getContent());
		}
	}

	@Test
	public void testPreloadedArticlesWithDifferentResourceUUID()
		throws Exception {

		initExport();

		User guestUser = UserLocalServiceUtil.getGuestUser(
			TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), guestUser.getUserId());

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), false, serviceContext);

		JournalTestUtil.addArticle(
			liveGroup.getGroupId(), journalArticle.getFolderId(),
			journalArticle.getArticleId(), false);

		User user = UserTestUtil.addUser();

		journalArticle.setUserId(user.getUserId());

		journalArticle = JournalTestUtil.updateArticle(journalArticle);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			JournalArticle importJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(importJournalArticle);
			Assert.assertEquals(
				journalArticle.getArticleId(),
				importJournalArticle.getArticleId());
			Assert.assertNotEquals(
				journalArticle.getArticleResourceUuid(),
				importJournalArticle.getArticleResourceUuid());
		}
	}

	@Test
	public void testRenameDLFolder() throws Exception {
		initExport();

		JournalArticle article = null;

		StringBundler sb1 = new StringBundler(15);

		sb1.append("{\"availableLanguageIds\":[\"en_US\"],");
		sb1.append("\"defaultLanguageId\":\"en_US\",");
		sb1.append("\"definitionSchemaVersion\":\"2.0\",");
		sb1.append("\"fields\":[{\"dataType\":\"image\",");
		sb1.append("\"fieldNamespace\":\"ddm\",");
		sb1.append("\"fieldReference\":\"image\",");
		sb1.append("\"indexType\":\"text\",");
		sb1.append("\"label\":{\"en_US\":\"Image\"},");
		sb1.append("\"localizable\":true,\"name\":\"image\",");
		sb1.append("\"predefinedValue\":{\"en_US\":\"\"},");
		sb1.append("\"readOnly\":false,\"repeatable\":false,");
		sb1.append("\"required\":false,\"showLabel\":true,");
		sb1.append("\"tip\":{\"en_US\":\"\"},\"type\":\"image\"}],");
		sb1.append("\"successPage\":{\"body\":{},");
		sb1.append("\"enabled\":false,\"title\":{}}}");

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				sb1.toString());

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_ddmFormDeserializer.deserialize(builder.build());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			stagingGroup.getGroupId(), JournalArticle.class.getName(),
			ddmFormDeserializerDeserializeResponse.getDDMForm());

		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(
				"/com/liferay/journal/dependencies/liferay.png")) {

			FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
				String.valueOf(UUID.randomUUID()), stagingGroup.getGroupId(),
				TestPropsValues.getUserId(), JournalArticle.class.getName(),
				"image.png", inputStream, ContentTypes.IMAGE_PNG);

			StringBundler sb2 = new StringBundler(15);

			sb2.append("<?xml version=\"1.0\"?>");
			sb2.append("<root available-locales=\"en_US,es_ES\" ");
			sb2.append("default-locale=\"en_US\">");
			sb2.append("<dynamic-element index-type=\"text\" ");
			sb2.append("instance-id=\"dqqn\" name=\"image\" ");
			sb2.append("type=\"image\">");
			sb2.append("<dynamic-content language-id=\"en_US\">");
			sb2.append("<![CDATA[{\"alt\": \"alt text\",\"groupId\": \"");
			sb2.append(String.valueOf(stagingGroup.getGroupId()));
			sb2.append("\",\"uuid\": \"");
			sb2.append(tempFileEntry.getUuid());
			sb2.append("\"}]]>");
			sb2.append("</dynamic-content>");
			sb2.append("</dynamic-element>");
			sb2.append("</root>");

			article = JournalTestUtil.addArticleWithXMLContent(
				stagingGroup.getGroupId(), sb2.toString(),
				ddmStructure.getStructureKey(), null);
		}

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, article);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedArticleStagedModel = readExportedStagedModel(
				article);

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedArticleStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			JournalArticle importedArticle =
				_journalArticleLocalService.fetchJournalArticleByUuidAndGroupId(
					article.getUuid(), liveGroup.getGroupId());

			Assert.assertNotNull(importedArticle);
			Assert.assertNotEquals(
				article.getResourcePrimKey(),
				importedArticle.getResourcePrimKey());

			DLFolder defaultParentDLFolder = _dlFolderLocalService.getFolder(
				importedArticle.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalConstants.RESOURCE_NAME);

			DLFolder renamedDLFolder = _dlFolderLocalService.fetchFolder(
				liveGroup.getGroupId(), defaultParentDLFolder.getFolderId(),
				String.valueOf(importedArticle.getResourcePrimKey()));

			Assert.assertNotNull(renamedDLFolder);

			DLFolder oldDLFolder = _dlFolderLocalService.fetchFolder(
				liveGroup.getGroupId(), defaultParentDLFolder.getFolderId(),
				String.valueOf(article.getResourcePrimKey()));

			Assert.assertNull(oldDLFolder);
		}
	}

	@Test
	public void testStatusByUserIdAndStatusByUserName() throws Exception {
		initExport();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		User user = UserTestUtil.addUser(
			RandomTestUtil.randomString(4), liveGroup.getGroupId());

		journalArticle.setStatusByUserId(user.getUserId());
		journalArticle.setStatusByUserName(user.getFullName());

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, journalArticle);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			StagedModel exportedStagedModel = readExportedStagedModel(
				journalArticle);

			Assert.assertNotNull(exportedStagedModel);

			try {
				ExportImportThreadLocal.setPortletImportInProcess(true);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStagedModel);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}

			JournalArticle importJournalArticle =
				JournalArticleLocalServiceUtil.
					fetchJournalArticleByUuidAndGroupId(
						journalArticle.getUuid(), liveGroup.getGroupId());

			Assert.assertEquals(
				journalArticle.getStatusByUserId(),
				importJournalArticle.getStatusByUserId());
			Assert.assertEquals(
				journalArticle.getStatusByUserName(),
				importJournalArticle.getStatusByUserName());
		}
	}

	public class TestLayoutStagedModelDataHandler
		implements StagedModelDataHandler<Layout> {

		public TestLayoutStagedModelDataHandler(
			StagedModelDataHandler<Layout>
				wrappedLayoutStagedModelDataHandler) {

			_wrappedLayoutStagedModelDataHandler =
				wrappedLayoutStagedModelDataHandler;
		}

		@Override
		public void deleteStagedModel(Layout stagedModel)
			throws PortalException {

			_wrappedLayoutStagedModelDataHandler.deleteStagedModel(stagedModel);
		}

		@Override
		public void deleteStagedModel(
				String uuid, long groupId, String className, String extraData)
			throws PortalException {

			_wrappedLayoutStagedModelDataHandler.deleteStagedModel(
				uuid, groupId, className, extraData);
		}

		@Override
		public void exportStagedModel(
				PortletDataContext portletDataContext, Layout stagedModel)
			throws PortletDataException {

			_wrappedLayoutStagedModelDataHandler.exportStagedModel(
				portletDataContext, stagedModel);
		}

		@Override
		public Layout fetchMissingReference(String uuid, long groupId) {
			return _wrappedLayoutStagedModelDataHandler.fetchMissingReference(
				uuid, groupId);
		}

		@Override
		public Layout fetchStagedModelByUuidAndGroupId(
			String uuid, long groupId) {

			return _wrappedLayoutStagedModelDataHandler.
				fetchStagedModelByUuidAndGroupId(uuid, groupId);
		}

		@Override
		public List<Layout> fetchStagedModelsByUuidAndCompanyId(
			String uuid, long companyId) {

			return _wrappedLayoutStagedModelDataHandler.
				fetchStagedModelsByUuidAndCompanyId(uuid, companyId);
		}

		@Override
		public String[] getClassNames() {
			return _wrappedLayoutStagedModelDataHandler.getClassNames();
		}

		@Override
		public String getDisplayName(Layout stagedModel) {
			return _wrappedLayoutStagedModelDataHandler.getDisplayName(
				stagedModel);
		}

		@Override
		public int[] getExportableStatuses() {
			return _wrappedLayoutStagedModelDataHandler.getExportableStatuses();
		}

		public Map<Long, Long> getPrimaryKeys() {
			return _primaryKeys;
		}

		@Override
		public Map<String, String> getReferenceAttributes(
			PortletDataContext portletDataContext, Layout stagedModel) {

			return _wrappedLayoutStagedModelDataHandler.getReferenceAttributes(
				portletDataContext, stagedModel);
		}

		@Override
		public void importMissingReference(
				PortletDataContext portletDataContext, Element referenceElement)
			throws PortletDataException {

			_wrappedLayoutStagedModelDataHandler.importMissingReference(
				portletDataContext, referenceElement);
		}

		@Override
		public void importMissingReference(
				PortletDataContext portletDataContext, String uuid,
				long groupId, long classPK)
			throws PortletDataException {

			_wrappedLayoutStagedModelDataHandler.importMissingReference(
				portletDataContext, uuid, groupId, classPK);
		}

		@Override
		public void importStagedModel(
				PortletDataContext portletDataContext, Layout stagedModel)
			throws PortletDataException {

			if (_primaryKeys == null) {
				_primaryKeys = new HashMap<>();

				Map<Long, Long> primaryKeys =
					(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
						JournalArticle.class);

				MapUtil.copy(primaryKeys, _primaryKeys);
			}

			_wrappedLayoutStagedModelDataHandler.importStagedModel(
				portletDataContext, stagedModel);
		}

		@Override
		public void restoreStagedModel(
				PortletDataContext portletDataContext, Layout stagedModel)
			throws PortletDataException {

			_wrappedLayoutStagedModelDataHandler.restoreStagedModel(
				portletDataContext, stagedModel);
		}

		@Override
		public boolean validateReference(
			PortletDataContext portletDataContext, Element referenceElement) {

			return _wrappedLayoutStagedModelDataHandler.validateReference(
				portletDataContext, referenceElement);
		}

		private Map<Long, Long> _primaryKeys;
		private final StagedModelDataHandler<Layout>
			_wrappedLayoutStagedModelDataHandler;

	}

	protected Map<String, List<StagedModel>> addCompanyDependencies()
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		Company company = CompanyLocalServiceUtil.fetchCompany(
			stagingGroup.getCompanyId());

		Group companyGroup = company.getGroup();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			companyGroup.getGroupId(), JournalArticle.class.getName());

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			companyGroup.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		addDependentStagedModel(
			dependentStagedModelsMap, DDMTemplate.class, ddmTemplate);

		JournalFolder folder = JournalTestUtil.addFolder(
			stagingGroup.getGroupId(), RandomTestUtil.randomString());

		addDependentStagedModel(
			dependentStagedModelsMap, JournalFolder.class, folder);

		return dependentStagedModelsMap;
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new LinkedHashMap<>();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		addDependentStagedModel(
			dependentStagedModelsMap, DDMTemplate.class, ddmTemplate);

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		JournalFolder folder = JournalTestUtil.addFolder(
			group.getGroupId(), RandomTestUtil.randomString());

		addDependentStagedModel(
			dependentStagedModelsMap, JournalFolder.class, folder);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> folderDependentStagedModels =
			dependentStagedModelsMap.get(JournalFolder.class.getSimpleName());

		JournalFolder folder = (JournalFolder)folderDependentStagedModels.get(
			0);

		List<StagedModel> ddmStructureDependentStagedModels =
			dependentStagedModelsMap.get(DDMStructure.class.getSimpleName());

		DDMStructure ddmStructure =
			(DDMStructure)ddmStructureDependentStagedModels.get(0);

		List<StagedModel> ddmTemplateDependentStagedModels =
			dependentStagedModelsMap.get(DDMTemplate.class.getSimpleName());

		DDMTemplate ddmTemplate =
			(DDMTemplate)ddmTemplateDependentStagedModels.get(0);

		return JournalTestUtil.addArticleWithXMLContent(
			group.getGroupId(), folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			DDMStructureTestUtil.getSampleStructuredContent(),
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());
	}

	@Override
	protected List<StagedModel> addWorkflowedStagedModels(Group group)
		throws Exception {

		List<StagedModel> stagedModels = new ArrayList<>();

		stagedModels.add(
			JournalTestUtil.addArticleWithWorkflow(group.getGroupId(), true));

		stagedModels.add(
			JournalTestUtil.addArticleWithWorkflow(group.getGroupId(), false));

		JournalArticle expiredArticle = JournalTestUtil.addArticleWithWorkflow(
			group.getGroupId(), true);

		expiredArticle = JournalArticleLocalServiceUtil.expireArticle(
			TestPropsValues.getUserId(), group.getGroupId(),
			expiredArticle.getArticleId(), expiredArticle.getVersion(),
			expiredArticle.getUrlTitle(),
			ServiceContextTestUtil.getServiceContext());

		stagedModels.add(expiredArticle);

		return stagedModels;
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return JournalArticleLocalServiceUtil.getJournalArticleByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return JournalArticle.class;
	}

	@Override
	protected boolean isCommentableStagedModel() {
		return true;
	}

	protected JournalArticle updateArticleStatus(
			JournalArticle article, int status, long userId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		if (status == WorkflowConstants.STATUS_DRAFT) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}
		else {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}

		return JournalTestUtil.updateArticle(
			userId, article, article.getTitleMap(), article.getContent(), false,
			true, serviceContext);
	}

	protected void validateCompanyDependenciesImport(
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

		Assert.assertNull(
			"Company DDM structure dependency should not be imported",
			_ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
				ddmStructure.getUuid(), group.getGroupId()));

		List<StagedModel> ddmTemplateDependentStagedModels =
			dependentStagedModelsMap.get(DDMTemplate.class.getSimpleName());

		Assert.assertEquals(
			ddmTemplateDependentStagedModels.toString(), 1,
			ddmTemplateDependentStagedModels.size());

		DDMTemplate ddmTemplate =
			(DDMTemplate)ddmTemplateDependentStagedModels.get(0);

		Assert.assertNull(
			"Company DDM template dependency should not be imported",
			DDMTemplateLocalServiceUtil.fetchDDMTemplateByUuidAndGroupId(
				ddmTemplate.getUuid(), group.getGroupId()));

		List<StagedModel> folderDependentStagedModels =
			dependentStagedModelsMap.get(JournalFolder.class.getSimpleName());

		Assert.assertEquals(
			folderDependentStagedModels.toString(), 1,
			folderDependentStagedModels.size());

		JournalFolder folder = (JournalFolder)folderDependentStagedModels.get(
			0);

		JournalFolderLocalServiceUtil.getJournalFolderByUuidAndGroupId(
			folder.getUuid(), group.getGroupId());
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

		_ddmStructureLocalService.getDDMStructureByUuidAndGroupId(
			ddmStructure.getUuid(), group.getGroupId());

		List<StagedModel> ddmTemplateDependentStagedModels =
			dependentStagedModelsMap.get(DDMTemplate.class.getSimpleName());

		Assert.assertEquals(
			ddmTemplateDependentStagedModels.toString(), 1,
			ddmTemplateDependentStagedModels.size());

		DDMTemplate ddmTemplate =
			(DDMTemplate)ddmTemplateDependentStagedModels.get(0);

		DDMTemplateLocalServiceUtil.getDDMTemplateByUuidAndGroupId(
			ddmTemplate.getUuid(), group.getGroupId());

		List<StagedModel> folderDependentStagedModels =
			dependentStagedModelsMap.get(JournalFolder.class.getSimpleName());

		Assert.assertEquals(
			folderDependentStagedModels.toString(), 1,
			folderDependentStagedModels.size());

		JournalFolder folder = (JournalFolder)folderDependentStagedModels.get(
			0);

		JournalFolderLocalServiceUtil.getJournalFolderByUuidAndGroupId(
			folder.getUuid(), group.getGroupId());
	}

	@Override
	protected void validateImport(
			StagedModel stagedModel, StagedModelAssets stagedModelAssets,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		JournalArticle article = (JournalArticle)stagedModel;

		JournalArticleResource articleResource =
			JournalArticleResourceUtil.fetchByUUID_G(
				article.getArticleResourceUuid(), group.getGroupId());

		Assert.assertNotNull(articleResource);

		JournalArticle importedArticle =
			JournalArticleLocalServiceUtil.getLatestArticle(
				articleResource.getResourcePrimKey(), article.getStatus(),
				false);

		_validateDDMStructureId(importedArticle, article);

		validateAssets(importedArticle, stagedModelAssets, group);

		validateComments(article, importedArticle, group);

		validateImport(dependentStagedModelsMap, group);
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		JournalArticle article = (JournalArticle)stagedModel;
		JournalArticle importedArticle = (JournalArticle)importedStagedModel;

		Assert.assertEquals(
			article.getExternalReferenceCode(),
			importedArticle.getExternalReferenceCode());
		Assert.assertEquals(
			article.getUrlTitle(), importedArticle.getUrlTitle());
		Assert.assertEquals(
			article.getDescription(), importedArticle.getDescription());
		Assert.assertEquals(
			article.getDisplayDate(), importedArticle.getDisplayDate());
		Assert.assertEquals(
			article.getExpirationDate(), importedArticle.getExpirationDate());
		Assert.assertEquals(
			article.getReviewDate(), importedArticle.getReviewDate());
		Assert.assertEquals(
			article.isIndexable(), importedArticle.isIndexable());
		Assert.assertEquals(
			article.isSmallImage(), importedArticle.isSmallImage());
		Assert.assertEquals(
			article.getSmallImageURL(), importedArticle.getSmallImageURL());
	}

	private void _validateDDMStructureId(
			JournalArticle importedJournalArticle,
			JournalArticle stagedJournalArticle)
		throws Exception {

		DDMStructure stagedDDMStructure =
			_ddmStructureLocalService.getDDMStructure(
				stagedJournalArticle.getDDMStructureId());

		Assert.assertEquals(
			_portal.getSiteGroupId(stagedJournalArticle.getGroupId()),
			stagedDDMStructure.getGroupId());

		DDMStructure importedDDMStructure =
			_ddmStructureLocalService.getDDMStructure(
				importedJournalArticle.getDDMStructureId());

		Assert.assertEquals(
			_portal.getSiteGroupId(importedJournalArticle.getGroupId()),
			importedDDMStructure.getGroupId());
	}

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject(filter = "ddm.form.deserializer.type=json")
	private DDMFormDeserializer _ddmFormDeserializer;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}