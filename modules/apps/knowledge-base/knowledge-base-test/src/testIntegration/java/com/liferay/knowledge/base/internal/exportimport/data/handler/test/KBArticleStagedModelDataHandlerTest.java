/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class KBArticleStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testExportImportKBArticleWithAttachments() throws Exception {
		initExport();

		KBArticle kbArticle = _addKBArticle(
			new Date(System.currentTimeMillis() + Time.DAY),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			ClassNameLocalServiceUtil.getClassNameId(
				KBFolderConstants.getClassName()),
			_createServiceContext(stagingGroup));

		InputStream inputStream = new ByteArrayInputStream(
			TestDataConstants.TEST_BYTE_ARRAY);

		FileEntry fileEntry1 = _kbArticleLocalService.addAttachment(
			TestPropsValues.getUserId(), kbArticle.getResourcePrimKey(),
			RandomTestUtil.randomString(), inputStream,
			ContentTypes.TEXT_PLAIN);
		FileEntry fileEntry2 = _kbArticleLocalService.addAttachment(
			TestPropsValues.getUserId(), kbArticle.getResourcePrimKey(),
			RandomTestUtil.randomString(), inputStream,
			ContentTypes.TEXT_PLAIN);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, kbArticle);

		initImport();

		KBArticle exportedKBArticle = (KBArticle)readExportedStagedModel(
			kbArticle);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedKBArticle);

		KBArticle importedKBArticle = (KBArticle)getStagedModel(
			kbArticle.getUuid(), liveGroup);

		_assertKBArticleAttachments(
			new String[] {fileEntry1.getFileName(), fileEntry2.getFileName()},
			importedKBArticle);

		initExport();

		kbArticle = _kbArticleLocalService.updateKBArticle(
			kbArticle.getUserId(), kbArticle.getResourcePrimKey(),
			kbArticle.getTitle(), kbArticle.getContent(),
			kbArticle.getDescription(), null, kbArticle.getSourceURL(),
			kbArticle.getDisplayDate(), kbArticle.getExpirationDate(),
			kbArticle.getReviewDate(), null,
			new long[] {fileEntry2.getFileEntryId()},
			_createServiceContext(stagingGroup));

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, kbArticle);

		initImport();

		exportedKBArticle = (KBArticle)readExportedStagedModel(kbArticle);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedKBArticle);

		importedKBArticle = (KBArticle)getStagedModel(
			kbArticle.getUuid(), liveGroup);

		_assertKBArticleAttachments(
			new String[] {fileEntry1.getFileName()}, importedKBArticle);
	}

	@Test
	public void testExportImportScheduledKBArticleCanBePublished()
		throws Exception {

		initExport();

		KBArticle kbArticle = _addKBArticle(
			new Date(System.currentTimeMillis() + Time.DAY),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			ClassNameLocalServiceUtil.getClassNameId(
				KBFolderConstants.getClassName()),
			_createServiceContext(stagingGroup));

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, kbArticle);

		initImport();

		KBArticle exportedKBArticle = (KBArticle)readExportedStagedModel(
			kbArticle);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedKBArticle);

		KBArticle importedKBArticle = (KBArticle)getStagedModel(
			kbArticle.getUuid(), liveGroup);

		Assert.assertEquals(
			kbArticle.getExternalReferenceCode(),
			importedKBArticle.getExternalReferenceCode());
		Assert.assertEquals(
			kbArticle.isScheduled(), importedKBArticle.isScheduled());

		importedKBArticle.setDisplayDate(
			new Date(System.currentTimeMillis() - (Time.MINUTE * 10)));

		importedKBArticle = _kbArticleLocalService.updateKBArticle(
			importedKBArticle);

		_kbArticleLocalService.checkKBArticles(liveGroup.getCompanyId());

		importedKBArticle = _kbArticleLocalService.fetchKBArticle(
			importedKBArticle.getKbArticleId());

		Assert.assertFalse(importedKBArticle.isScheduled());
	}

	@Test
	public void testMovingKBArticleUpdatesParentResourcePrimKey()
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		exportImportStagedModel(stagedModel);

		KBArticle kbArticle = (KBArticle)stagedModel;

		_kbArticleLocalService.moveKBArticle(
			TestPropsValues.getUserId(), kbArticle.getResourcePrimKey(),
			ClassNameLocalServiceUtil.getClassNameId(
				KBFolderConstants.getClassName()),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			KBArticleConstants.DEFAULT_PRIORITY);

		exportImportStagedModel(
			getStagedModel(kbArticle.getUuid(), stagingGroup));

		KBArticle importedKBArticle = (KBArticle)getStagedModel(
			kbArticle.getUuid(), liveGroup);

		Assert.assertEquals(
			kbArticle.getExternalReferenceCode(),
			importedKBArticle.getExternalReferenceCode());
		Assert.assertEquals(
			ClassNameLocalServiceUtil.getClassNameId(
				KBFolderConstants.getClassName()),
			importedKBArticle.getParentResourceClassNameId());
		Assert.assertEquals(
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			importedKBArticle.getParentResourcePrimKey());
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		addDependentStagedModel(
			dependentStagedModelsMap, KBArticle.class,
			_addKBArticle(
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				ClassNameLocalServiceUtil.getClassNameId(
					KBFolderConstants.getClassName()),
				_createServiceContext(group)));

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> stagedModels = dependentStagedModelsMap.get(
			KBArticle.class.getSimpleName());

		KBArticle kbArticle = (KBArticle)stagedModels.get(0);

		return _addKBArticle(
			kbArticle.getResourcePrimKey(),
			ClassNameLocalServiceUtil.getClassNameId(
				KBArticleConstants.getClassName()),
			_createServiceContext(group));
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return _kbArticleLocalService.getKBArticleByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return KBArticle.class;
	}

	private KBArticle _addKBArticle(
			Date displayDate, long parentResourcePrimKey,
			long parentResourceClassNameId, ServiceContext serviceContext)
		throws Exception {

		return _kbArticleLocalService.addKBArticle(
			null, serviceContext.getUserId(), parentResourceClassNameId,
			parentResourcePrimKey, StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), null, null, displayDate, null, null,
			null, serviceContext);
	}

	private KBArticle _addKBArticle(
			long parentResourcePrimKey, long parentResourceClassNameId,
			ServiceContext serviceContext)
		throws Exception {

		return _addKBArticle(
			RandomTestUtil.nextDate(), parentResourcePrimKey,
			parentResourceClassNameId, serviceContext);
	}

	private void _assertKBArticleAttachments(
			String[] expectedFileNames, KBArticle importedKBArticle)
		throws Exception {

		List<FileEntry> attachmentsFileEntries =
			importedKBArticle.getAttachmentsFileEntries();

		Assert.assertEquals(
			attachmentsFileEntries.toString(), attachmentsFileEntries.size(),
			expectedFileNames.length);

		for (FileEntry attachmentFileEntry : attachmentsFileEntries) {
			Assert.assertTrue(
				ArrayUtil.contains(
					expectedFileNames, attachmentFileEntry.getFileName()));
		}
	}

	private ServiceContext _createServiceContext(Group group) throws Exception {
		return ServiceContextTestUtil.getServiceContext(group.getGroupId());
	}

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

}