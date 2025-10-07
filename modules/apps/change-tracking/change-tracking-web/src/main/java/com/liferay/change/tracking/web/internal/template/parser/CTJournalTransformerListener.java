/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.template.parser;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.templateparser.BaseTransformerListener;
import com.liferay.portal.kernel.templateparser.TransformerListener;

import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = TransformerListener.class
)
public class CTJournalTransformerListener extends BaseTransformerListener {

	@Override
	public String onOutput(
		String output, String languageId, Map<String, String> tokens) {

		if (!output.contains("previewCTCollectionId")) {
			return output;
		}

		Source source = new Source(output);

		List<StartTag> imgTags = source.getAllStartTags("img");

		OutputDocument outputDocument = new OutputDocument(source);

		Long ctCollectionId = Long.valueOf(tokens.get("ct_collection_id"));

		for (StartTag imgTag : imgTags) {
			Attributes attributes = imgTag.getAttributes();

			Map<String, String> map = outputDocument.replace(attributes, false);

			String src = attributes.getValue("src");

			int previewCTCollectionId = src.indexOf("previewCTCollectionId=");

			map.put(
				"src",
				StringBundler.concat(
					src.substring(0, previewCTCollectionId),
					"previewCTCollectionId=", ctCollectionId));
		}

		return outputDocument.toString();
	}

}