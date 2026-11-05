/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.exportimport.data.handler.test;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseSiteBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.site.dto.v1_0.StyleBook;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.test.util.FrontendTokenDefinitionTestUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thiago Buarque
 */
public abstract class BaseStyleBookBatchEnginePortletDataHandlerTestCase
	extends BaseSiteBatchEnginePortletDataHandlerTestCase {

	@Test
	public void testExportImportGeneratesKeyWhenKeyIsTaken() throws Exception {
		String styleBookEntryKey = RandomTestUtil.randomString();
		Group targetGroup = getTargetGroup();

		StyleBookEntry targetStyleBookEntry1 = _addStyleBookEntry(
			RandomTestUtil.randomString(), targetGroup.getGroupId(),
			RandomTestUtil.randomString(), styleBookEntryKey,
			_THEME_ID_CLASSIC);

		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();
		String name = RandomTestUtil.randomString();

		_addStyleBookEntry(
			externalReferenceCode, group.getGroupId(), name, styleBookEntryKey,
			_THEME_ID_CLASSIC);

		_exportImport();

		Assert.assertEquals(
			Collections.emptyList(),
			_getExportImportReportEntries(
				targetGroup.getGroupId(),
				ExportImportReportEntryConstants.TYPE_ERROR));

		StyleBookEntry importedStyleBookEntry = _getStyleBookEntry(
			externalReferenceCode, targetGroup.getGroupId());

		Assert.assertEquals(name, importedStyleBookEntry.getName());
		Assert.assertNotEquals(
			targetStyleBookEntry1.getStyleBookEntryKey(),
			importedStyleBookEntry.getStyleBookEntryKey());

		StyleBookEntry targetStyleBookEntry2 = _getStyleBookEntry(
			targetStyleBookEntry1.getExternalReferenceCode(),
			targetGroup.getGroupId());

		Assert.assertEquals(
			targetStyleBookEntry1.getStyleBookEntryKey(),
			targetStyleBookEntry2.getStyleBookEntryKey());
	}

	@Test
	public void testExportImportKeepsDates() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();

		addEntry(externalReferenceCode, group.getGroupId());

		updateModifiedDate(
			externalReferenceCode, group.getGroupId(),
			new Date(System.currentTimeMillis() - Time.DAY));

		_exportImport();

		StyleBookEntry styleBookEntry1 = _getStyleBookEntry(
			externalReferenceCode, group.getGroupId());

		Group targetGroup = getTargetGroup();

		StyleBookEntry importedStyleBookEntry1 = _getStyleBookEntry(
			externalReferenceCode, targetGroup.getGroupId());

		DateTestUtil.assertEquals(
			styleBookEntry1.getCreateDate(),
			importedStyleBookEntry1.getCreateDate());
		DateTestUtil.assertEquals(
			styleBookEntry1.getModifiedDate(),
			importedStyleBookEntry1.getModifiedDate());

		updateModifiedDate(
			externalReferenceCode, group.getGroupId(),
			new Date(System.currentTimeMillis() - Time.HOUR));

		_exportImport();

		StyleBookEntry styleBookEntry2 = _getStyleBookEntry(
			externalReferenceCode, group.getGroupId());

		StyleBookEntry importedStyleBookEntry2 = _getStyleBookEntry(
			externalReferenceCode, targetGroup.getGroupId());

		DateTestUtil.assertEquals(
			styleBookEntry2.getModifiedDate(),
			importedStyleBookEntry2.getModifiedDate());
	}

	@Test
	public void testExportImportKeepsDuplicateStyleBookEntryKey()
		throws Exception {

		String name = RandomTestUtil.randomString();
		Group targetGroup = getTargetGroup();

		_addStyleBookEntry(
			RandomTestUtil.randomString(), targetGroup.getGroupId(), name,
			StringPool.BLANK, _THEME_ID_CLASSIC);

		Group group = getGroup();

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			RandomTestUtil.randomString(), group.getGroupId(), name,
			StringPool.BLANK, _THEME_ID_CLASSIC);

		_exportImport();

		StyleBookEntry importedStyleBookEntry1 = _getStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			targetGroup.getGroupId());

		String newName = RandomTestUtil.randomString();

		_updateStyleBookEntry(newName, styleBookEntry);

		_exportImport();

		Assert.assertEquals(
			Collections.emptyList(),
			_getExportImportReportEntries(
				targetGroup.getGroupId(),
				ExportImportReportEntryConstants.TYPE_ERROR));

		StyleBookEntry importedStyleBookEntry2 = _getStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			targetGroup.getGroupId());

		Assert.assertEquals(newName, importedStyleBookEntry2.getName());
		Assert.assertEquals(
			importedStyleBookEntry1.getStyleBookEntryKey(),
			importedStyleBookEntry2.getStyleBookEntryKey());
	}

	@Test
	public void testExportImportKeepsPreviewFileEntry() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();

		_addStyleBookEntry(
			externalReferenceCode, group.getGroupId(),
			RandomTestUtil.randomString(), StringPool.BLANK, _THEME_ID_CLASSIC);

		_exportImport();

		Group targetGroup = getTargetGroup();

		StyleBookEntry importedStyleBookEntry1 = _getStyleBookEntry(
			externalReferenceCode, targetGroup.getGroupId());

		FileEntry previewFileEntry = _addPreviewFileEntry(
			importedStyleBookEntry1);

		_styleBookEntryLocalService.updatePreviewFileEntryId(
			importedStyleBookEntry1.getStyleBookEntryId(),
			previewFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				targetGroup.getGroupId(), TestPropsValues.getUserId()));

		_exportImport();

		StyleBookEntry importedStyleBookEntry2 = _getStyleBookEntry(
			externalReferenceCode, targetGroup.getGroupId());

		Assert.assertEquals(
			previewFileEntry.getFileEntryId(),
			importedStyleBookEntry2.getPreviewFileEntryId());
	}

	@Test
	public void testExportImportReportsWarningsInOneEntry() throws Exception {
		String name = RandomTestUtil.randomString();
		Group targetGroup = getTargetGroup();

		_addStyleBookEntry(
			RandomTestUtil.randomString(), targetGroup.getGroupId(), name,
			StringPool.BLANK, _THEME_ID_CLASSIC);

		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();

		_addStyleBookEntry(
			externalReferenceCode, group.getGroupId(), name, StringPool.BLANK,
			RandomTestUtil.randomString());

		_exportImport();

		ExportImportReportEntry exportImportReportEntry =
			_getWarningExportImportReportEntry(
				externalReferenceCode, targetGroup.getGroupId());

		String uniqueName = StringUtil.appendParentheticalSuffix(name, 1);

		Assert.assertEquals(
			StringBundler.concat(
				_getRenamedWarningMessage(name, uniqueName), StringPool.SPACE,
				_getThemeWarningMessage(uniqueName)),
			exportImportReportEntry.getErrorMessage());
	}

	@Test
	public void testExportImportReportsWhenUniqueNameIsTooLong()
		throws Exception {

		String name = RandomTestUtil.randomString(
			ModelHintsUtil.getMaxLength(
				StyleBookEntry.class.getName(), "name"));
		Group targetGroup = getTargetGroup();

		_addStyleBookEntry(
			RandomTestUtil.randomString(), targetGroup.getGroupId(), name,
			StringPool.BLANK, _THEME_ID_CLASSIC);

		String externalReferenceCode1 = RandomTestUtil.randomString();
		Group group = getGroup();

		_addStyleBookEntry(
			externalReferenceCode1, group.getGroupId(), name, StringPool.BLANK,
			_THEME_ID_CLASSIC);

		String externalReferenceCode2 = RandomTestUtil.randomString();

		_addStyleBookEntry(
			externalReferenceCode2, group.getGroupId(),
			RandomTestUtil.randomString(), StringPool.BLANK, _THEME_ID_CLASSIC);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			_exportImport();
		}

		Assert.assertFalse(
			hasEntry(externalReferenceCode1, targetGroup.getGroupId()));
		Assert.assertTrue(
			hasEntry(externalReferenceCode2, targetGroup.getGroupId()));

		List<ExportImportReportEntry> exportImportReportEntries =
			_getExportImportReportEntries(
				targetGroup.getGroupId(),
				ExportImportReportEntryConstants.TYPE_ERROR);

		Assert.assertEquals(
			exportImportReportEntries.toString(), 1,
			exportImportReportEntries.size());

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntries.get(0);

		Assert.assertEquals(
			externalReferenceCode1,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			"Maximum length of name exceeded",
			exportImportReportEntry.getErrorMessage());
	}

	@Test
	public void testExportImportResolvesNameConflictOnAdd() throws Exception {
		String name = RandomTestUtil.randomString();
		Group targetGroup = getTargetGroup();

		StyleBookEntry targetStyleBookEntry = _addStyleBookEntry(
			RandomTestUtil.randomString(), targetGroup.getGroupId(), name,
			StringPool.BLANK, _THEME_ID_CLASSIC);

		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();

		_addStyleBookEntry(
			externalReferenceCode, group.getGroupId(), name, StringPool.BLANK,
			_THEME_ID_CLASSIC);

		_exportImport();

		StyleBookEntry importedStyleBookEntry1 = _getStyleBookEntry(
			externalReferenceCode, targetGroup.getGroupId());

		Assert.assertNotEquals(name, importedStyleBookEntry1.getName());
		Assert.assertNotEquals(
			targetStyleBookEntry.getStyleBookEntryKey(),
			importedStyleBookEntry1.getStyleBookEntryKey());

		ExportImportReportEntry exportImportReportEntry =
			_getWarningExportImportReportEntry(
				externalReferenceCode, targetGroup.getGroupId());

		Assert.assertEquals(
			_getRenamedWarningMessage(name, importedStyleBookEntry1.getName()),
			exportImportReportEntry.getErrorMessage());

		_exportImport();

		StyleBookEntry importedStyleBookEntry2 = _getStyleBookEntry(
			externalReferenceCode, targetGroup.getGroupId());

		Assert.assertEquals(
			importedStyleBookEntry1.getName(),
			importedStyleBookEntry2.getName());
		Assert.assertEquals(
			importedStyleBookEntry1.getStyleBookEntryKey(),
			importedStyleBookEntry2.getStyleBookEntryKey());

		Assert.assertEquals(
			2,
			_styleBookEntryLocalService.getStyleBookEntriesCount(
				targetGroup.getGroupId()));
	}

	@Test
	public void testExportImportResolvesNameConflictOnUpdate()
		throws Exception {

		Group group = getGroup();

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			RandomTestUtil.randomString(), StringPool.BLANK, _THEME_ID_CLASSIC);

		_exportImport();

		String name = RandomTestUtil.randomString();
		Group targetGroup = getTargetGroup();

		_addStyleBookEntry(
			RandomTestUtil.randomString(), targetGroup.getGroupId(), name,
			StringPool.BLANK, _THEME_ID_CLASSIC);

		_updateStyleBookEntry(name, styleBookEntry);

		_exportImport();

		StyleBookEntry importedStyleBookEntry = _getStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			targetGroup.getGroupId());

		String uniqueName = StringUtil.appendParentheticalSuffix(name, 1);

		Assert.assertEquals(uniqueName, importedStyleBookEntry.getName());

		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryKey(),
			importedStyleBookEntry.getStyleBookEntryKey());

		ExportImportReportEntry exportImportReportEntry =
			_getWarningExportImportReportEntry(
				styleBookEntry.getExternalReferenceCode(),
				targetGroup.getGroupId());

		Assert.assertEquals(
			_getRenamedWarningMessage(name, uniqueName),
			exportImportReportEntry.getErrorMessage());
	}

	@Test
	public void testExportImportStyleBookEntry() throws Exception {
		String frontendTokenName = RandomTestUtil.randomString();

		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				frontendTokenName);
		String frontendTokensValues = JSONUtil.put(
			StringBundler.concat(
				StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM,
				StringPool.COLON, frontendTokenName),
			JSONUtil.put("value", RandomTestUtil.randomString())
		).toString();

		Group group = getGroup();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				group.getGroupId(), true, frontendTokenDefinition,
				frontendTokensValues, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), _THEME_ID_CLASSIC,
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), TestPropsValues.getUserId()));

		_exportImport();

		Group targetGroup = getTargetGroup();

		StyleBookEntry importedStyleBookEntry = _getStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			targetGroup.getGroupId());

		Assert.assertTrue(importedStyleBookEntry.isDefaultStyleBookEntry());
		Assert.assertEquals(
			frontendTokenDefinition,
			importedStyleBookEntry.getFrontendTokenDefinition());
		Assert.assertEquals(
			frontendTokensValues,
			importedStyleBookEntry.getFrontendTokensValues());
		Assert.assertEquals(
			styleBookEntry.getName(), importedStyleBookEntry.getName());
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryKey(),
			importedStyleBookEntry.getStyleBookEntryKey());
		Assert.assertEquals(
			_THEME_ID_CLASSIC, importedStyleBookEntry.getThemeId());

		Assert.assertNull(
			_getWarningExportImportReportEntry(
				styleBookEntry.getExternalReferenceCode(),
				targetGroup.getGroupId()));
	}

	@Test
	public void testExportImportWarnsWhenThemeHasNoFrontendTokenDefinition()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();
		String name = RandomTestUtil.randomString();

		_addStyleBookEntry(
			externalReferenceCode, group.getGroupId(), name, StringPool.BLANK,
			"admin_WAR_admintheme");

		_exportImport();

		Group targetGroup = getTargetGroup();

		ExportImportReportEntry exportImportReportEntry =
			_getWarningExportImportReportEntry(
				externalReferenceCode, targetGroup.getGroupId());

		Assert.assertEquals(
			_getThemeWarningMessage(name),
			exportImportReportEntry.getErrorMessage());
	}

	@Test
	public void testExportImportWarnsWhenThemeIsNotAvailable()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();
		String name = RandomTestUtil.randomString();

		_addStyleBookEntry(
			externalReferenceCode, group.getGroupId(), name, StringPool.BLANK,
			RandomTestUtil.randomString());

		_exportImport();

		Group targetGroup = getTargetGroup();

		ExportImportReportEntry exportImportReportEntry =
			_getWarningExportImportReportEntry(
				externalReferenceCode, targetGroup.getGroupId());

		Assert.assertEquals(
			_getThemeWarningMessage(name),
			exportImportReportEntry.getErrorMessage());
	}

	@Test
	public void testExportImportWarnsWhenTokenIsMissing() throws Exception {
		Group group = getGroup();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				group.getGroupId(), false,
				FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
					RandomTestUtil.randomString()),
				JSONUtil.put(
					RandomTestUtil.randomString(),
					JSONUtil.put("value", RandomTestUtil.randomString())
				).toString(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_THEME_ID_CLASSIC,
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), TestPropsValues.getUserId()));

		_exportImport();

		Group targetGroup = getTargetGroup();

		ExportImportReportEntry exportImportReportEntry =
			_getWarningExportImportReportEntry(
				styleBookEntry.getExternalReferenceCode(),
				targetGroup.getGroupId());

		Assert.assertEquals(
			LanguageUtil.format(
				LocaleUtil.US,
				"the-style-book-x-references-tokens-that-do-not-exist-in-the-" +
					"current-theme-affected-styles-will-use-default-values",
				styleBookEntry.getName()),
			exportImportReportEntry.getErrorMessage());
	}

	@Test
	public void testExportImportWithThemeCSSClientExtensionEntry()
		throws Exception {

		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString());

		ClientExtensionEntry clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK, RandomTestUtil.randomLocaleStringMap(),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_THEME_CSS,
				"frontendTokenDefinitionJSON=" + frontendTokenDefinition);

		try {
			Group group = getGroup();

			StyleBookEntry styleBookEntry = _addStyleBookEntry(
				RandomTestUtil.randomString(), group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				clientExtensionEntry.getExternalReferenceCode());

			_exportImport();

			Group targetGroup = getTargetGroup();

			Assert.assertNull(
				_getWarningExportImportReportEntry(
					styleBookEntry.getExternalReferenceCode(),
					targetGroup.getGroupId()));
		}
		finally {
			_clientExtensionEntryLocalService.deleteClientExtensionEntry(
				clientExtensionEntry);
		}
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_addStyleBookEntry(
			externalReferenceCode, groupId, RandomTestUtil.randomString(),
			StringPool.BLANK, _THEME_ID_CLASSIC);
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_styleBookEntryLocalService.deleteStyleBookEntry(
			externalReferenceCode, groupId);
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		StyleBookEntry styleBookEntry = _getStyleBookEntry(
			externalReferenceCode, groupId);

		return styleBookEntry.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return StyleBook.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		StyleBookEntry styleBookEntry = _getStyleBookEntry(
			externalReferenceCode, groupId);

		return styleBookEntry.getStyleBookEntryId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.
				fetchStyleBookEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (styleBookEntry == null) {
			return false;
		}

		return true;
	}

	protected boolean hasStyleBookPortlet(long groupId) throws Exception {
		List<Portlet> portlets = ExportImportHelperUtil.getExportablePortlets(
			TestPropsValues.getCompanyId(), false, groupId);

		List<String> rootPortletIds = TransformUtil.transform(
			portlets, Portlet::getRootPortletId);

		return rootPortletIds.contains(StyleBookPortletKeys.STYLE_BOOK);
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return false;
	}

	@Override
	protected boolean supportsPermissions() {
		return false;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(
				_getStyleBookEntry(externalReferenceCode, groupId));

		draftStyleBookEntry.setUserId(userId);

		_styleBookEntryLocalService.publishDraft(draftStyleBookEntry);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_updateStyleBookEntry(
			RandomTestUtil.randomString(),
			_getStyleBookEntry(externalReferenceCode, groupId));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(
				_getStyleBookEntry(externalReferenceCode, groupId));

		draftStyleBookEntry.setModifiedDate(modifiedDate);

		_styleBookEntryLocalService.publishDraft(draftStyleBookEntry);
	}

	private FileEntry _addPreviewFileEntry(StyleBookEntry styleBookEntry)
		throws Exception {

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			styleBookEntry.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				styleBookEntry.getGroupId(), TestPropsValues.getUserId()));

		return PortletFileRepositoryUtil.addPortletFileEntry(
			null, styleBookEntry.getGroupId(), TestPropsValues.getUserId(),
			StyleBookEntry.class.getName(),
			styleBookEntry.getStyleBookEntryId(), RandomTestUtil.randomString(),
			repository.getDlFolderId(),
			new UnsyncByteArrayInputStream(RandomTestUtil.randomBytes()),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);
	}

	private StyleBookEntry _addStyleBookEntry(
			String externalReferenceCode, long groupId, String name,
			String styleBookEntryKey, String themeId)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			externalReferenceCode, TestPropsValues.getUserId(), groupId, false,
			StringPool.BLANK, StringPool.BLANK, name, styleBookEntryKey,
			themeId,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	private void _exportImport() throws Exception {
		exportImport(
			null,
			HashMapBuilder.put(
				ExportImportDateUtil.RANGE,
				new String[] {ExportImportDateUtil.RANGE_ALL}
			).put(
				PortletDataHandlerKeys.DATA_STRATEGY,
				new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR}
			).put(
				PortletDataHandlerKeys.PORTLET_DATA,
				new String[] {Boolean.TRUE.toString()}
			).put(
				PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
					portletId,
				new String[] {Boolean.TRUE.toString()}
			).build(),
			null);
	}

	private List<ExportImportReportEntry> _getExportImportReportEntries(
		long groupId, int type) {

		return ListUtil.filter(
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			exportImportReportEntry ->
				(exportImportReportEntry.getGroupId() == groupId) &&
				(exportImportReportEntry.getType() == type));
	}

	private String _getRenamedWarningMessage(String name, String uniqueName) {
		return LanguageUtil.format(
			LocaleUtil.US,
			"a-style-book-named-x-already-existed-the-imported-style-book-" +
				"was-renamed-to-x",
			new String[] {name, uniqueName});
	}

	private StyleBookEntry _getStyleBookEntry(
			String externalReferenceCode, long groupId)
		throws Exception {

		return _styleBookEntryLocalService.
			getStyleBookEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	private String _getThemeWarningMessage(String name) {
		return LanguageUtil.format(
			LocaleUtil.US,
			"the-theme-referenced-by-x-is-not-available-in-this-environment-" +
				"the-style-book-will-be-inactive-and-its-tokens-cannot-be-" +
					"verified-until-the-theme-is-available",
			name);
	}

	private ExportImportReportEntry _getWarningExportImportReportEntry(
		String externalReferenceCode, long groupId) {

		for (ExportImportReportEntry exportImportReportEntry :
				_getExportImportReportEntries(
					groupId, ExportImportReportEntryConstants.TYPE_WARNING)) {

			if (Objects.equals(
					externalReferenceCode,
					exportImportReportEntry.getClassExternalReferenceCode())) {

				return exportImportReportEntry;
			}
		}

		return null;
	}

	private void _updateStyleBookEntry(
			String name, StyleBookEntry styleBookEntry)
		throws Exception {

		_styleBookEntryLocalService.updateStyleBookEntry(
			TestPropsValues.getUserId(), styleBookEntry.getStyleBookEntryId(),
			styleBookEntry.isDefaultStyleBookEntry(),
			styleBookEntry.getFrontendTokenDefinition(),
			styleBookEntry.getFrontendTokensValues(), name,
			styleBookEntry.getStyleBookEntryKey(),
			styleBookEntry.getPreviewFileEntryId(), styleBookEntry.getThemeId(),
			ServiceContextTestUtil.getServiceContext(
				styleBookEntry.getGroupId(), TestPropsValues.getUserId()));
	}

	private static final String _THEME_ID_CLASSIC = "classic_WAR_classictheme";

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}