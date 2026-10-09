/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.dto.v1_0.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Process;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Rafael Praxedes
 */
public class ProcessUtil {

	public static Process toProcess(Document document, Locale locale) {
		return new Process() {
			{
				setActive(() -> document.getBoolean("active"));
				setDateCreated(
					() -> _parseDate(document.getDate("createDate")));
				setDateModified(
					() -> _parseDate(document.getDate("modifiedDate")));
				setDescription(() -> document.getString("description"));
				setId(() -> document.getLong("processId"));
				setTitle(
					() -> {
						Map<String, String> titleMap = getTitle_i18n();

						String title = titleMap.get(locale.toLanguageTag());

						if (Validator.isNull(title)) {
							Locale defaultLocale =
								LocaleThreadLocal.getDefaultLocale();

							title = titleMap.get(defaultLocale.toLanguageTag());
						}

						return title;
					});
				setTitle_i18n(() -> _createTitleMap(document));
				setVersion(() -> document.getString("version"));
			}
		};
	}

	private static Map<String, String> _createTitleMap(Document document) {
		Map<String, String> titleMap = new HashMap<>();

		Map<String, Field> fields = document.getFields();

		for (Map.Entry<String, Field> entry : fields.entrySet()) {
			String key = entry.getKey();

			if (!StringUtil.startsWith(key, "title_") ||
				StringUtil.endsWith(key, "_sortable")) {

				continue;
			}

			String languageTag = _toLanguageTag(
				StringUtil.removeSubstring(key, "title_"));

			if (languageTag == null) {
				continue;
			}

			Field field = entry.getValue();

			titleMap.put(languageTag, String.valueOf(field.getValue()));
		}

		return titleMap;
	}

	private static Date _parseDate(String dateString) {
		try {
			return DateUtil.parseDate(
				"yyyyMMddHHmmss", dateString, LocaleUtil.getDefault());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private static String _toLanguageTag(String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId, true, false);

		if (locale == null) {
			return null;
		}

		return locale.toLanguageTag();
	}

	private static final Log _log = LogFactoryUtil.getLog(ProcessUtil.class);

}