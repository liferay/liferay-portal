/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.article.dynamic.data.mapping.form.field.type.internal.journal.article;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever;
import com.liferay.journal.article.dynamic.data.mapping.form.field.type.constants.JournalArticleDDMFormFieldTypeConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rodrigo Paulino
 */
@Component(
	property = "ddm.form.field.type.name=" + JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE,
	service = DDMFormFieldValueRequestParameterRetriever.class
)
public class JournalArticleDDMFormFieldValueRequestParameterRetriever
	implements DDMFormFieldValueRequestParameterRetriever {

	@Override
	public String get(
		HttpServletRequest httpServletRequest, String ddmFormFieldParameterName,
		String defaultDDMFormFieldParameterValue) {

		String parameterValue = httpServletRequest.getParameter(
			ddmFormFieldParameterName);

		if (!Validator.isBlank(parameterValue)) {
			parameterValue = String.valueOf(
				getJSONObject(_log, parameterValue));
		}

		if (parameterValue != null) {
			return parameterValue;
		}

		return defaultDDMFormFieldParameterValue;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleDDMFormFieldValueRequestParameterRetriever.class);

}