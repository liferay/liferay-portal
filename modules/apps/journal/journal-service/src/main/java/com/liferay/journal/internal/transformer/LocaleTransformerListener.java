/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.transformer;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.templateparser.BaseTransformerListener;
import com.liferay.portal.kernel.templateparser.TransformerListener;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Raymond Augé
 */
@Component(
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = TransformerListener.class
)
public class LocaleTransformerListener extends BaseTransformerListener {

	@Override
	public String onScript(
		String script, Document document, String languageId,
		Map<String, String> tokens) {

		if (_log.isDebugEnabled()) {
			_log.debug("onScript");
		}

		return StringUtil.replace(script, "@language_id@", languageId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LocaleTransformerListener.class);

}