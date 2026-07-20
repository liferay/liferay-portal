/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Adolfo Pérez
 */
@ProviderType
public abstract class BaseTranslator implements Translator {

	@Override
	public boolean isAIAssisted() {
		return false;
	}

	protected String getLanguageCode(String languageId) {
		List<String> parts = StringUtil.split(languageId, CharPool.UNDERLINE);

		String languageCode = parts.get(0);

		// AWS, Azure, DeepL, and GoogleCloud all expect ISO-639 language codes.
		// ISO-639:1989 renamed "in" to "id." See LPD-23561.

		if (languageCode.equals("in")) {
			return "id";
		}

		return languageCode;
	}

}