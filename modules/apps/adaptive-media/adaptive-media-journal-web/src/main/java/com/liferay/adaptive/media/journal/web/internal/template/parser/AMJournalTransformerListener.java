/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.journal.web.internal.template.parser;

import com.liferay.adaptive.media.content.transformer.ContentTransformerHandler;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.util.JournalContent;
import com.liferay.portal.kernel.templateparser.BaseTransformerListener;
import com.liferay.portal.kernel.templateparser.TransformerListener;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = TransformerListener.class
)
public class AMJournalTransformerListener extends BaseTransformerListener {

	@Override
	public String onOutput(
		String output, String languageId, Map<String, String> tokens) {

		return _contentTransformerHandler.transform(output);
	}

	@Activate
	protected void activate() {
		_journalContent.clearCache();
	}

	@Deactivate
	protected void deactivate() {
		_journalContent.clearCache();
	}

	@Reference
	private ContentTransformerHandler _contentTransformerHandler;

	@Reference
	private JournalContent _journalContent;

}