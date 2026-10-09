/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.dto.v1_0.converter.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.rest.dto.v1_0.ElementDefinition;
import com.liferay.search.experiences.rest.dto.v1_0.Field;
import com.liferay.search.experiences.rest.dto.v1_0.FieldSet;
import com.liferay.search.experiences.rest.dto.v1_0.UiConfiguration;

import java.util.Locale;
import java.util.Map;

/**
 * @author Gustavo Lima
 */
public class SXPDTOConverterUtil {

	public static ElementDefinition translate(
		ElementDefinition elementDefinition, Language language, Locale locale) {

		try {
			UiConfiguration uiConfiguration =
				elementDefinition.getUiConfiguration();

			if (uiConfiguration == null) {
				return elementDefinition;
			}

			FieldSet[] fieldSets = uiConfiguration.getFieldSets();

			if (fieldSets == null) {
				return elementDefinition;
			}

			for (FieldSet fieldSet : fieldSets) {
				Field[] fields = fieldSet.getFields();

				for (Field field : fields) {
					if (!Validator.isBlank(field.getHelpText())) {
						field.setHelpTextLocalized(
							() -> language.get(locale, field.getHelpText()));
					}

					if (Validator.isBlank(field.getLabel())) {
						continue;
					}

					field.setLabelLocalized(
						() -> language.get(locale, field.getLabel()));
				}
			}

			return elementDefinition;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	public static String translate(
		String fallback, Language language, Locale locale,
		Map<Locale, String> localizedMap) {

		return language.get(
			locale,
			localizedMap.getOrDefault(
				locale,
				localizedMap.getOrDefault(
					LocaleUtil.getSiteDefault(),
					localizedMap.getOrDefault(
						LocaleUtil.getDefault(), fallback))));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SXPDTOConverterUtil.class);

}