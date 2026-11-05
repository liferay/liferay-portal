/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.batch.engine.action;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.headless.admin.site.dto.v1_0.StyleBook;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.StyleBookUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(service = ImportTaskPostAction.class)
public class StyleBookImportTaskPostAction implements ImportTaskPostAction {

	@Override
	public void run(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			ImportTaskContext importTaskContext, Object item,
			Object persistedItem)
		throws Exception {

		if (!ExportImportThreadLocal.isImportInProcess() ||
			!StringUtil.equals(
				batchEngineImportTask.getOperation(),
				BatchEngineTaskOperation.CREATE.name()) ||
			!(persistedItem instanceof StyleBook)) {

			return;
		}

		StyleBook persistedStyleBook = (StyleBook)persistedItem;

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.getStyleBookEntry(
				persistedStyleBook.getId());

		List<String> warningMessages = new ArrayList<>();

		StyleBook styleBook = (StyleBook)item;

		if (!StringUtil.equals(styleBook.getName(), styleBookEntry.getName())) {
			warningMessages.add(
				_language.format(
					LocaleUtil.US,
					"a-style-book-named-x-already-existed-the-imported-style-" +
						"book-was-renamed-to-x",
					new String[] {
						styleBook.getName(), styleBookEntry.getName()
					}));
		}

		if (StyleBookUtil.isThemeInactive(
				styleBookEntry.getCompanyId(), styleBookEntry.getThemeId())) {

			warningMessages.add(
				_language.format(
					LocaleUtil.US,
					StringBundler.concat(
						"the-theme-referenced-by-x-is-not-available-in-this-",
						"environment-the-style-book-will-be-inactive-and-its-",
						"tokens-cannot-be-verified-until-the-theme-is-",
						"available"),
					styleBookEntry.getName()));
		}
		else if (_hasMissingTokens(styleBookEntry)) {
			warningMessages.add(
				_language.format(
					LocaleUtil.US,
					"the-style-book-x-references-tokens-that-do-not-exist-in-" +
						"the-current-theme-affected-styles-will-use-default-" +
							"values",
					styleBookEntry.getName()));
		}

		if (warningMessages.isEmpty()) {
			return;
		}

		_exportImportReportEntryLocalService.getOrAddExportImportReportEntry(
			styleBookEntry.getGroupId(), styleBookEntry.getCompanyId(),
			styleBookEntry.getExternalReferenceCode(),
			_portal.getClassNameId(StyleBookEntry.class),
			styleBookEntry.getStyleBookEntryId(),
			GetterUtil.getLong(
				ExportImportThreadLocal.getExportImportConfigurationId()),
			ExportImportReportEntryConstants.TYPE_WARNING,
			StringUtil.merge(warningMessages, StringPool.SPACE), null,
			"style-books");
	}

	private boolean _hasMissingTokens(StyleBookEntry styleBookEntry)
		throws Exception {

		String frontendTokensValues = styleBookEntry.getFrontendTokensValues();

		if (Validator.isBlank(frontendTokensValues)) {
			return false;
		}

		List<String> frontendTokensValuesKeys =
			StyleBookUtil.getFrontendTokensValuesKeys(
				LocaleUtil.US, styleBookEntry);

		if (frontendTokensValuesKeys.isEmpty()) {
			return false;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			frontendTokensValues);

		for (String frontendTokensValuesKey : jsonObject.keySet()) {
			if (!frontendTokensValuesKeys.contains(frontendTokensValuesKey)) {
				return true;
			}
		}

		return false;
	}

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}