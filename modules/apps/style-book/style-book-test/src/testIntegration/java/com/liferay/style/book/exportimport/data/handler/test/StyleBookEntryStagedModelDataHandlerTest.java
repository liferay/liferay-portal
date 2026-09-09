/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.test.util.FrontendTokenDefinitionTestUtil;

import java.util.List;
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
public class StyleBookEntryStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testExportImport() throws Exception {
		String frontendTokenName = RandomTestUtil.randomString();

		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				frontendTokenName);
		String frontendTokensValues = JSONUtil.put(
			frontendTokenName,
			JSONUtil.put("value", RandomTestUtil.randomString())
		).toString();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				false, frontendTokenDefinition, frontendTokensValues,
				RandomTestUtil.randomString(), StringPool.BLANK,
				_THEME_ID_ADMIN,
				ServiceContextTestUtil.getServiceContext(
					stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		FileEntry previewFileEntry = _addPreviewFileEntry(styleBookEntry);

		styleBookEntry = _styleBookEntryLocalService.updatePreviewFileEntryId(
			styleBookEntry.getStyleBookEntryId(),
			previewFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		Thread.sleep(1000);

		exportImportStagedModel(styleBookEntry);

		StyleBookEntry importedStyleBookEntry = (StyleBookEntry)getStagedModel(
			styleBookEntry.getUuid(), liveGroup);

		Assert.assertEquals(
			styleBookEntry.getName(), importedStyleBookEntry.getName());
		Assert.assertEquals(
			frontendTokenDefinition,
			importedStyleBookEntry.getFrontendTokenDefinition());
		Assert.assertEquals(
			frontendTokensValues,
			importedStyleBookEntry.getFrontendTokensValues());
		Assert.assertTrue(importedStyleBookEntry.getPreviewFileEntryId() > 0);
		DateTestUtil.assertEquals(
			styleBookEntry.getModifiedDate(),
			importedStyleBookEntry.getModifiedDate());

		Assert.assertNull(
			_getWarningExportImportReportEntry(liveGroup.getGroupId()));
	}

	@Test
	public void testExportImportResolvesExternalReferenceCodeConflict()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_styleBookEntryLocalService.addStyleBookEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			liveGroup.getGroupId(), false, StringPool.BLANK, StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				liveGroup.getGroupId(), TestPropsValues.getUserId()));

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				externalReferenceCode, TestPropsValues.getUserId(),
				stagingGroup.getGroupId(), false, StringPool.BLANK,
				StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		exportImportStagedModel(styleBookEntry);

		StyleBookEntry importedStyleBookEntry = (StyleBookEntry)getStagedModel(
			styleBookEntry.getUuid(), liveGroup);

		Assert.assertNotEquals(
			externalReferenceCode,
			importedStyleBookEntry.getExternalReferenceCode());
	}

	@Test
	public void testExportImportResolvesNameConflictWithUniqueName()
		throws Exception {

		String name = RandomTestUtil.randomString();

		StyleBookEntry liveStyleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), liveGroup.getGroupId(),
				false, StringPool.BLANK, StringPool.BLANK, name,
				StringPool.BLANK, RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					liveGroup.getGroupId(), TestPropsValues.getUserId()));

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				false, StringPool.BLANK, StringPool.BLANK, name,
				StringPool.BLANK, RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		exportImportStagedModel(styleBookEntry);

		StyleBookEntry importedStyleBookEntry = (StyleBookEntry)getStagedModel(
			styleBookEntry.getUuid(), liveGroup);

		Assert.assertEquals(
			styleBookEntry.getUuid(), importedStyleBookEntry.getUuid());
		Assert.assertNotEquals(name, importedStyleBookEntry.getName());
		Assert.assertNotEquals(
			liveStyleBookEntry.getStyleBookEntryKey(),
			importedStyleBookEntry.getStyleBookEntryKey());
	}

	@Test
	public void testExportImportWarnsWhenThemeIsNotDeployed() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				false, StringPool.BLANK, StringPool.BLANK,
				RandomTestUtil.randomString(), StringPool.BLANK,
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		exportImportStagedModel(styleBookEntry);

		StyleBookEntry importedStyleBookEntry = (StyleBookEntry)getStagedModel(
			styleBookEntry.getUuid(), liveGroup);

		Assert.assertNotNull(importedStyleBookEntry);

		ExportImportReportEntry exportImportReportEntry =
			_getWarningExportImportReportEntry(liveGroup.getGroupId());

		String errorMessage = exportImportReportEntry.getErrorMessage();

		Assert.assertTrue(errorMessage.contains("is not deployed"));
	}

	@Test
	public void testExportImportWarnsWhenTokenIsMissing() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				false,
				FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
					RandomTestUtil.randomString()),
				JSONUtil.put(
					RandomTestUtil.randomString(),
					JSONUtil.put("value", RandomTestUtil.randomString())
				).toString(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_THEME_ID_ADMIN,
				ServiceContextTestUtil.getServiceContext(
					stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		exportImportStagedModel(styleBookEntry);

		StyleBookEntry importedStyleBookEntry = (StyleBookEntry)getStagedModel(
			styleBookEntry.getUuid(), liveGroup);

		Assert.assertNotNull(importedStyleBookEntry);

		ExportImportReportEntry exportImportReportEntry =
			_getWarningExportImportReportEntry(liveGroup.getGroupId());

		String errorMessage = exportImportReportEntry.getErrorMessage();

		Assert.assertTrue(errorMessage, errorMessage.contains("do not exist"));
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			StringPool.BLANK, StringPool.BLANK, RandomTestUtil.randomString(),
			StringPool.BLANK, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Override
	protected StagedModel addVersion(StagedModel stagedModel) throws Exception {
		StyleBookEntry styleBookEntry = (StyleBookEntry)stagedModel;

		Thread.sleep(1000);

		return _styleBookEntryLocalService.updateStyleBookEntry(
			styleBookEntry.getStyleBookEntryId(),
			styleBookEntry.getFrontendTokenDefinition(), "{}",
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				styleBookEntry.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return _styleBookEntryLocalService.fetchStyleBookEntryByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return StyleBookEntry.class;
	}

	@Override
	protected boolean isVersionableStagedModel() {
		return true;
	}

	private FileEntry _addPreviewFileEntry(StyleBookEntry styleBookEntry)
		throws Exception {

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			stagingGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));
		Class<?> clazz = getClass();

		return PortletFileRepositoryUtil.addPortletFileEntry(
			null, stagingGroup.getGroupId(), TestPropsValues.getUserId(),
			StyleBookEntry.class.getName(),
			styleBookEntry.getStyleBookEntryId(), RandomTestUtil.randomString(),
			repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);
	}

	private ExportImportReportEntry _getWarningExportImportReportEntry(
			long groupId)
		throws Exception {

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(), 0);

		for (ExportImportReportEntry exportImportReportEntry :
				exportImportReportEntries) {

			if ((exportImportReportEntry.getGroupId() == groupId) &&
				(exportImportReportEntry.getType() ==
					ExportImportReportEntryConstants.TYPE_WARNING)) {

				return exportImportReportEntry;
			}
		}

		return null;
	}

	private static final String _THEME_ID_ADMIN = "admin_WAR_admintheme";

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}