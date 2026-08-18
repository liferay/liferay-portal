/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class TemplateEntryStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_classNameId = _portal.getClassNameId(TemplateEntry.class);
	}

	@Test
	@TestInfo("LPD-86646")
	public void testExportImportTemplateEntryPropagatesModifiedDateOnUpdate()
		throws Exception {

		TemplateEntry templateEntry = _addTemplateEntry(
			stagingGroup, JournalArticle.class.getName(), StringPool.BLANK);

		_exportImportStagedModel(templateEntry);

		Date modifiedDate = templateEntry.getModifiedDate();

		templateEntry.setModifiedDate(
			new Date(modifiedDate.getTime() + Time.SECOND));

		templateEntry = _templateEntryLocalService.updateTemplateEntry(
			templateEntry);

		_exportImportStagedModel(templateEntry);

		TemplateEntry importedTemplateEntry = (TemplateEntry)getStagedModel(
			templateEntry.getUuid(), liveGroup);

		Assert.assertEquals(
			templateEntry.getCreateDate(),
			importedTemplateEntry.getCreateDate());
		Assert.assertTrue(
			DateUtil.equals(
				templateEntry.getModifiedDate(),
				importedTemplateEntry.getModifiedDate()));
	}

	@Test
	@TestInfo("LPD-32929")
	public void testExportImportTemplateEntryWithoutVariation()
		throws Exception {

		_asserExportImportTemplateEntry(StringPool.BLANK, StringPool.BLANK);
	}

	@Test
	@TestInfo("LPD-32929")
	public void testExportImportTemplateEntryWithSiteTiedVariation()
		throws Exception {

		DDMStructure stagingGroupDDMStructure =
			DDMStructureTestUtil.addStructure(
				stagingGroup.getGroupId(), JournalArticle.class.getName());

		_exportImportStagedModel(stagingGroupDDMStructure);

		DDMStructure liveGroupDDMStructure =
			_ddmStructureLocalService.fetchStructure(
				liveGroup.getGroupId(),
				_portal.getClassNameId(JournalArticle.class),
				stagingGroupDDMStructure.getStructureKey());

		_asserExportImportTemplateEntry(
			String.valueOf(liveGroupDDMStructure.getStructureId()),
			String.valueOf(stagingGroupDDMStructure.getStructureId()));
	}

	@Test
	@TestInfo("LPD-32929")
	public void testExportImportTemplateEntryWithUnpublishedSiteTiedVariation()
		throws Exception {

		DDMStructure stagingGroupDDMStructure =
			DDMStructureTestUtil.addStructure(
				stagingGroup.getGroupId(), JournalArticle.class.getName());

		_asserExportImportTemplateEntry(
			StringPool.BLANK,
			String.valueOf(stagingGroupDDMStructure.getStructureId()));
	}

	@Test
	@TestInfo("LPD-32929")
	public void testExportImportTemplateEntryWithVariation() throws Exception {
		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			stagingGroup.getGroupId(),
			_portal.getClassNameId(JournalArticle.class), "BASIC-WEB-CONTENT",
			true);

		_asserExportImportTemplateEntry(
			String.valueOf(ddmStructure.getStructureId()),
			String.valueOf(ddmStructure.getStructureId()));
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return _addTemplateEntry(group, StringPool.BLANK, StringPool.BLANK);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return _templateEntryLocalService.getTemplateEntryByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return TemplateEntry.class;
	}

	private TemplateEntry _addTemplateEntry(
			Group group, String infoItemClassName,
			String infoItemFormVariationKey)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.addTemplate(
			null, TestPropsValues.getUserId(), group.getGroupId(), _classNameId,
			0, _classNameId, Collections.singletonMap(LocaleUtil.US, "name"),
			Collections.emptyMap(), DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			StringPool.BLANK, TemplateConstants.LANG_TYPE_FTL,
			"<#-- Empty script -->", serviceContext);

		return _templateEntryLocalService.addTemplateEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			ddmTemplate.getTemplateId(), infoItemClassName,
			infoItemFormVariationKey, serviceContext);
	}

	private void _asserExportImportTemplateEntry(
			String expectedInfoItemFormVariationKey,
			String infoItemFormVariationKey)
		throws Exception {

		TemplateEntry templateEntry = _addTemplateEntry(
			stagingGroup, JournalArticle.class.getName(),
			infoItemFormVariationKey);

		_exportImportStagedModel(templateEntry);

		TemplateEntry importedTemplateEntry = (TemplateEntry)getStagedModel(
			templateEntry.getUuid(), liveGroup);

		Assert.assertEquals(
			templateEntry.getCreateDate(),
			importedTemplateEntry.getCreateDate());
		Assert.assertEquals(
			expectedInfoItemFormVariationKey,
			importedTemplateEntry.getInfoItemFormVariationKey());
		Assert.assertEquals(
			templateEntry.getModifiedDate(),
			importedTemplateEntry.getModifiedDate());
	}

	private void _exportImportStagedModel(StagedModel stagedModel)
		throws Exception {

		ExportImportThreadLocal.setPortletImportInProcess(true);

		try {
			exportImportStagedModel(stagedModel);
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(false);
		}
	}

	private long _classNameId;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private TemplateEntryLocalService _templateEntryLocalService;

}