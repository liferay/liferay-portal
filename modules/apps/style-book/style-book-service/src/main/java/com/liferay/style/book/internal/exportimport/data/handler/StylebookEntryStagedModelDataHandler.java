/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = StagedModelDataHandler.class)
public class StylebookEntryStagedModelDataHandler
	extends BaseStagedModelDataHandler<StyleBookEntry> {

	public static final String[] CLASS_NAMES = {StyleBookEntry.class.getName()};

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(
			uuid, groupId, className, extraData);
	}

	@Override
	public void deleteStagedModel(StyleBookEntry styleBookEntry)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(styleBookEntry);
	}

	@Override
	public List<StyleBookEntry> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _stagedModelRepository.fetchStagedModelsByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			StyleBookEntry styleBookEntry)
		throws Exception {

		if (styleBookEntry.getPreviewFileEntryId() > 0) {
			FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
				styleBookEntry.getPreviewFileEntryId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, styleBookEntry, fileEntry,
				PortletDataContext.REFERENCE_TYPE_WEAK);
		}

		Element entryElement = portletDataContext.getExportDataElement(
			styleBookEntry);

		portletDataContext.addClassedModel(
			entryElement, ExportImportPathUtil.getModelPath(styleBookEntry),
			styleBookEntry);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long styleBookEntryId)
		throws Exception {

		StyleBookEntry existingStyleBookEntry = fetchMissingReference(
			uuid, groupId);

		if (existingStyleBookEntry == null) {
			return;
		}

		Map<Long, Long> styleBookEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				StyleBookEntry.class);

		styleBookEntryIds.put(
			styleBookEntryId, existingStyleBookEntry.getStyleBookEntryId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			StyleBookEntry styleBookEntry)
		throws Exception {

		StyleBookEntry importedStyleBookEntry =
			(StyleBookEntry)styleBookEntry.clone();

		importedStyleBookEntry.setGroupId(portletDataContext.getScopeGroupId());

		String originalName = importedStyleBookEntry.getName();

		StyleBookEntry existingStyleBookEntry =
			_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
				styleBookEntry.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingStyleBookEntry == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			String uniqueName =
				_styleBookEntryLocalService.generateStyleBookEntryName(
					portletDataContext.getScopeGroupId(), originalName);

			if (!Objects.equals(originalName, uniqueName)) {
				importedStyleBookEntry.setName(uniqueName);
				importedStyleBookEntry.setStyleBookEntryKey(StringPool.BLANK);
			}

			String externalReferenceCode =
				importedStyleBookEntry.getExternalReferenceCode();

			if (Validator.isNotNull(externalReferenceCode)) {
				StyleBookEntry ercStyleBookEntry =
					_styleBookEntryLocalService.
						fetchStyleBookEntryByExternalReferenceCode(
							externalReferenceCode,
							portletDataContext.getScopeGroupId());

				if (ercStyleBookEntry != null) {
					importedStyleBookEntry.setExternalReferenceCode(
						StringPool.BLANK);
				}
			}

			importedStyleBookEntry = _stagedModelRepository.addStagedModel(
				portletDataContext, importedStyleBookEntry);
		}
		else {
			importedStyleBookEntry.setMvccVersion(
				existingStyleBookEntry.getMvccVersion());
			importedStyleBookEntry.setStyleBookEntryId(
				existingStyleBookEntry.getStyleBookEntryId());

			importedStyleBookEntry = _stagedModelRepository.updateStagedModel(
				portletDataContext, importedStyleBookEntry);
		}

		if (styleBookEntry.getPreviewFileEntryId() > 0) {
			Map<Long, Long> fileEntryIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					FileEntry.class);

			long previewFileEntryId = MapUtil.getLong(
				fileEntryIds, styleBookEntry.getPreviewFileEntryId(), 0);

			importedStyleBookEntry.setPreviewFileEntryId(previewFileEntryId);

			importedStyleBookEntry =
				_styleBookEntryLocalService.updatePreviewFileEntryId(
					importedStyleBookEntry.getStyleBookEntryId(),
					previewFileEntryId,
					portletDataContext.createServiceContext(styleBookEntry));
		}

		_reportImportWarnings(
			importedStyleBookEntry, originalName, portletDataContext);

		portletDataContext.importClassedModel(
			styleBookEntry, importedStyleBookEntry);
	}

	@Override
	protected StagedModelRepository<StyleBookEntry> getStagedModelRepository() {
		return _stagedModelRepository;
	}

	private List<String> _getFrontendTokenNames(
		PortletDataContext portletDataContext, StyleBookEntry styleBookEntry) {

		FrontendTokenDefinition themeFrontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				portletDataContext.getCompanyId(), styleBookEntry.getThemeId());

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.parseFrontendTokenDefinitionJSONObject(
				styleBookEntry.getFrontendTokenDefinition());

		if (themeFrontendTokenDefinition == null) {
			return FrontendTokenDefinitionUtil.getFrontendTokenNames(
				overrideFrontendTokenDefinitionJSONObject);
		}

		return FrontendTokenDefinitionUtil.getFrontendTokenNames(
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				themeFrontendTokenDefinition.getJSONObject(LocaleUtil.US),
				overrideFrontendTokenDefinitionJSONObject));
	}

	private boolean _hasMissingTokens(
			List<String> frontendTokenNames, String frontendTokensValues)
		throws Exception {

		if (frontendTokenNames.isEmpty() ||
			Validator.isBlank(frontendTokensValues)) {

			return false;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			frontendTokensValues);

		for (String name : jsonObject.keySet()) {
			if (!frontendTokenNames.contains(name)) {
				return true;
			}
		}

		return false;
	}

	private void _reportImportWarnings(
			StyleBookEntry importedStyleBookEntry, String originalName,
			PortletDataContext portletDataContext)
		throws Exception {

		List<String> warningMessages = new ArrayList<>();

		String name = importedStyleBookEntry.getName();

		if (!Objects.equals(name, originalName)) {
			warningMessages.add(
				_language.format(
					LocaleUtil.US,
					"a-style-book-named-x-already-existed-the-imported-style-" +
						"book-was-renamed-to-x",
					new String[] {originalName, name}));
		}

		Theme theme = _themeLocalService.fetchTheme(
			portletDataContext.getCompanyId(),
			importedStyleBookEntry.getThemeId());

		if (theme == null) {
			String themeWarningMessage = _language.format(
				LocaleUtil.US,
				"the-theme-referenced-by-x-is-not-deployed-in-this-" +
					"environment-and-its-tokens-could-not-be-verified-the-" +
						"default-theme-will-be-used-until-it-is-available",
				name);

			warningMessages.add(themeWarningMessage);
		}
		else if (_hasMissingTokens(
					_getFrontendTokenNames(
						portletDataContext, importedStyleBookEntry),
					importedStyleBookEntry.getFrontendTokensValues())) {

			warningMessages.add(
				_language.format(
					LocaleUtil.US,
					"the-style-book-x-references-tokens-that-do-not-exist-in-" +
						"the-current-theme-affected-styles-will-use-default-" +
							"values",
					name));
		}

		if (!warningMessages.isEmpty()) {
			_exportImportReportEntryLocalService.
				getOrAddExportImportReportEntry(
					importedStyleBookEntry.getGroupId(),
					portletDataContext.getCompanyId(),
					importedStyleBookEntry.getExternalReferenceCode(),
					PortalUtil.getClassNameId(StyleBookEntry.class),
					importedStyleBookEntry.getStyleBookEntryId(),
					GetterUtil.getLong(
						ExportImportThreadLocal.
							getExportImportConfigurationId()),
					ExportImportReportEntryConstants.TYPE_WARNING,
					StringUtil.merge(warningMessages, StringPool.SPACE), null,
					StyleBookEntry.class.getName());
		}
	}

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.style.book.model.StyleBookEntry)",
		unbind = "-"
	)
	private StagedModelRepository<StyleBookEntry> _stagedModelRepository;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private ThemeLocalService _themeLocalService;

}