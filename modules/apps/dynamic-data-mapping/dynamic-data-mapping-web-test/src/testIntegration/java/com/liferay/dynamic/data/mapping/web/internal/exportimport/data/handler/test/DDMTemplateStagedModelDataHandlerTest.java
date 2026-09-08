/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 */
@RunWith(Arquillian.class)
public class DDMTemplateStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	@Override
	public void tearDown() throws Exception {
		if (_journalArticle != null) {
			JournalArticleLocalServiceUtil.deleteArticle(_journalArticle);
		}

		if (_importedTemplate != null) {
			DDMTemplateLocalServiceUtil.deleteTemplate(_importedTemplate);
		}

		if (_importedStructure != null) {
			DDMStructureLocalServiceUtil.deleteStructure(_importedStructure);
		}

		super.tearDown();
	}

	@Test
	public void testImportTemplateToCompanyGroup() throws Exception {
		DDMStructure structure = DDMStructureTestUtil.addStructure(
			stagingGroup.getGroupId(), _CLASS_NAME_JOURNAL_ARTICLE);

		DDMTemplate template = DDMTemplateTestUtil.addTemplate(
			stagingGroup.getGroupId(), structure.getStructureId(),
			PortalUtil.getClassNameId(_CLASS_NAME_JOURNAL_ARTICLE));

		_exportTemplateAndStructure(stagingGroup, template, structure);

		Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
			stagingGroup.getCompanyId());

		_importTemplateAndStructure(
			stagingGroup, companyGroup, template, structure);

		_importedStructure =
			DDMStructureLocalServiceUtil.getDDMStructureByUuidAndGroupId(
				structure.getUuid(), companyGroup.getGroupId());

		_importedTemplate =
			DDMTemplateLocalServiceUtil.getDDMTemplateByUuidAndGroupId(
				template.getUuid(), companyGroup.getGroupId());

		validateImportedStagedModel(template, _importedTemplate);

		Assert.assertEquals(
			_importedStructure.getStructureId(),
			_importedTemplate.getClassPK());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(liveGroup.getGroupId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		_journalArticle = JournalArticleLocalServiceUtil.addArticle(
			null, TestPropsValues.getUserId(), liveGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DDMStructureTestUtil.getSampleStructuredContent(),
			_importedStructure.getStructureId(),
			_importedTemplate.getTemplateKey(), serviceContext);

		Assert.assertEquals(
			_importedStructure.getStructureId(),
			_journalArticle.getDDMStructureId());
		Assert.assertEquals(
			_importedTemplate.getTemplateKey(),
			_journalArticle.getDDMTemplateKey());
	}

	@Test
	public void testPublishTemplateToLiveBeforeStructure() throws Exception {
		DDMTemplate template = DDMTemplateTestUtil.addTemplate(
			stagingGroup.getGroupId(), 0,
			PortalUtil.getClassNameId(_CLASS_NAME));

		DDMStructure structure = DDMStructureTestUtil.addStructure(
			stagingGroup.getGroupId(), _CLASS_NAME);

		_exportImportTemplate(template);

		template.setClassPK(structure.getStructureId());

		template = DDMTemplateLocalServiceUtil.updateDDMTemplate(template);

		_exportImportTemplateAndStructure(template, structure);

		DDMStructure importedStructure =
			DDMStructureLocalServiceUtil.fetchDDMStructureByUuidAndGroupId(
				structure.getUuid(), liveGroup.getGroupId());

		DDMTemplate importedTemplate = (DDMTemplate)getStagedModel(
			template.getUuid(), liveGroup);

		Assert.assertNotNull(importedTemplate);

		Assert.assertNotNull(importedStructure);
		Assert.assertEquals(
			importedStructure.getStructureId(), importedTemplate.getClassPK());
	}

	@Test
	public void testPublishTemplateWithParentGroups() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		DDMTemplate template = DDMTemplateTestUtil.addTemplate(
			parentGroup.getGroupId(), 0,
			PortalUtil.getClassNameId(_CLASS_NAME));

		DDMStructure structure = DDMStructureTestUtil.addStructure(
			parentGroup.getGroupId(), _CLASS_NAME_JOURNAL_ARTICLE);

		template.setClassPK(structure.getStructureId());

		template = DDMTemplateLocalServiceUtil.updateDDMTemplate(template);

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		Map<Locale, String> descriptionMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(childGroup.getGroupId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		JournalArticle journalArticle =
			JournalArticleLocalServiceUtil.addArticle(
				null, TestPropsValues.getUserId(), childGroup.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, titleMap,
				descriptionMap, content, structure.getStructureId(),
				template.getTemplateKey(), serviceContext);

		_exportTemplateAndStructure(parentGroup, template, structure);

		Group newParentGroup = GroupTestUtil.addGroup();

		_importTemplateAndStructure(
			parentGroup, newParentGroup, template, structure);

		_exportJournalArticle(childGroup, journalArticle);

		childGroup = GroupTestUtil.deleteGroup(childGroup);

		GroupTestUtil.deleteGroup(parentGroup);

		Group newChildGroup = GroupTestUtil.addGroup(
			newParentGroup.getGroupId());

		_importJournalArticle(childGroup, newChildGroup, journalArticle);

		DDMStructure importedStructure =
			DDMStructureLocalServiceUtil.fetchDDMStructureByUuidAndGroupId(
				structure.getUuid(), newParentGroup.getGroupId());

		DDMTemplate importedTemplate =
			DDMTemplateLocalServiceUtil.fetchDDMTemplateByUuidAndGroupId(
				template.getUuid(), newParentGroup.getGroupId());

		JournalArticle importedJournalArticle =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), newChildGroup.getGroupId());

		Assert.assertNotNull(importedStructure);
		Assert.assertNotNull(importedTemplate);
		Assert.assertNotNull(importedJournalArticle);
		Assert.assertEquals(
			importedJournalArticle.getDDMStructureId(),
			importedStructure.getStructureId());
		Assert.assertEquals(
			importedJournalArticle.getDDMTemplateKey(),
			importedTemplate.getTemplateKey());
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		DDMStructure structure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), _CLASS_NAME);

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, structure);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			DDMStructure.class.getSimpleName());

		DDMStructure structure = (DDMStructure)dependentStagedModels.get(0);

		return DDMTemplateTestUtil.addTemplate(
			group.getGroupId(), structure.getStructureId(),
			PortalUtil.getClassNameId(_CLASS_NAME));
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return DDMTemplateLocalServiceUtil.getDDMTemplateByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return DDMTemplate.class;
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			DDMStructure.class.getSimpleName());

		Assert.assertEquals(
			dependentStagedModels.toString(), 1, dependentStagedModels.size());

		DDMStructure structure = (DDMStructure)dependentStagedModels.get(0);

		DDMStructureLocalServiceUtil.getDDMStructureByUuidAndGroupId(
			structure.getUuid(), group.getGroupId());
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		DDMTemplate template = (DDMTemplate)stagedModel;
		DDMTemplate importedTemplate = (DDMTemplate)importedStagedModel;

		Assert.assertEquals(
			template.getTemplateKey(), importedTemplate.getTemplateKey());
		Assert.assertEquals(template.getName(), importedTemplate.getName());
		Assert.assertEquals(
			template.getDescription(), importedTemplate.getDescription());
		Assert.assertEquals(template.getMode(), importedTemplate.getMode());
		Assert.assertEquals(
			template.getLanguage(), importedTemplate.getLanguage());
		Assert.assertEquals(template.getScript(), importedTemplate.getScript());
		Assert.assertEquals(
			template.isCacheable(), importedTemplate.isCacheable());
		Assert.assertEquals(
			template.isSmallImage(), importedTemplate.isSmallImage());
	}

	private void _exportImportTemplate(DDMTemplate template) throws Exception {
		_exportTemplateAndStructure(template, null);
		_importTemplateAndStructure(template, null);
	}

	private void _exportImportTemplateAndStructure(
			DDMTemplate template, DDMStructure structure)
		throws Exception {

		_exportTemplateAndStructure(template, structure);
		_importTemplateAndStructure(template, structure);
	}

	private void _exportJournalArticle(
			Group exportGroup, JournalArticle journalArticle)
		throws Exception {

		initExport(exportGroup);

		if (Objects.nonNull(journalArticle)) {
			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, journalArticle);
		}
	}

	private void _exportTemplateAndStructure(
			DDMTemplate template, DDMStructure structure)
		throws Exception {

		initExport();

		if (Objects.nonNull(structure)) {
			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, structure);
		}

		if (Objects.nonNull(template)) {
			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, template);
		}
	}

	private void _exportTemplateAndStructure(
			Group exportGroup, DDMTemplate template, DDMStructure structure)
		throws Exception {

		initExport(exportGroup);

		if (Objects.nonNull(structure)) {
			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, structure);
		}

		if (Objects.nonNull(template)) {
			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, template);
		}
	}

	private void _importJournalArticle(
			Group exportGroup, Group importGroup, JournalArticle journalArticle)
		throws Exception {

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable(
				exportGroup, importGroup)) {

			if (Objects.nonNull(journalArticle)) {
				JournalArticle exportedJournalArticle =
					(JournalArticle)readExportedStagedModel(journalArticle);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedJournalArticle);
			}
		}
	}

	private void _importTemplateAndStructure(
			DDMTemplate template, DDMStructure structure)
		throws Exception {

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			if (Objects.nonNull(structure)) {
				DDMStructure exportedStructure =
					(DDMStructure)readExportedStagedModel(structure);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStructure);
			}

			if (Objects.nonNull(template)) {
				DDMTemplate exportedTemplate =
					(DDMTemplate)readExportedStagedModel(template);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedTemplate);
			}
		}
	}

	private void _importTemplateAndStructure(
			Group exportGroup, Group importGroup, DDMTemplate template,
			DDMStructure structure)
		throws Exception {

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable(
				exportGroup, importGroup)) {

			if (Objects.nonNull(structure)) {
				DDMStructure exportedStructure =
					(DDMStructure)readExportedStagedModel(structure);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedStructure);
			}

			if (Objects.nonNull(template)) {
				DDMTemplate exportedTemplate =
					(DDMTemplate)readExportedStagedModel(template);

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, exportedTemplate);
			}
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.lists.model.DDLRecordSet";

	private static final String _CLASS_NAME_JOURNAL_ARTICLE =
		"com.liferay.journal.model.JournalArticle";

	private DDMStructure _importedStructure;
	private DDMTemplate _importedTemplate;
	private JournalArticle _journalArticle;

}