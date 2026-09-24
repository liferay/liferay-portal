/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

/**
 * @author Marco Leo
 */
public class ObjectActionImpl extends ObjectActionBaseImpl {

	@Override
	public String getDefaultLanguageId() {
		String xml = getErrorMessage();

		if (xml == null) {
			return "";
		}

		return LocalizationUtil.getDefaultLanguageId(
			xml, LocaleUtil.getDefault());
	}

	@Override
	public UnicodeProperties getParametersUnicodeProperties() {
		if (_parametersUnicodeProperties == null) {
			_parametersUnicodeProperties = UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				getParameters()
			).build();
		}

		return _parametersUnicodeProperties;
	}

	private transient UnicodeProperties _parametersUnicodeProperties;

}