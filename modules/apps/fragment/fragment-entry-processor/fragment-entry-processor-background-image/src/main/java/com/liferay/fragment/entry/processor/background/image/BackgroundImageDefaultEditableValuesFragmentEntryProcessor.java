/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.background.image;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.processor.DefaultEditableValuesFragmentEntryProcessor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=5",
	service = DefaultEditableValuesFragmentEntryProcessor.class
)
public class BackgroundImageDefaultEditableValuesFragmentEntryProcessor
	implements DefaultEditableValuesFragmentEntryProcessor {

	@Override
	public JSONObject getDefaultEditableValuesJSONObject(
		JSONObject configurationJSONObject, Document document) {

		JSONObject defaultEditableValuesJSONObject =
			_jsonFactory.createJSONObject();

		for (Element element :
				document.getElementsByAttribute(
					"data-lfr-background-image-id")) {

			String id = element.attr("data-lfr-background-image-id");

			defaultEditableValuesJSONObject.put(
				id, _jsonFactory.createJSONObject());
		}

		return defaultEditableValuesJSONObject;
	}

	@Override
	public String getKey() {
		return FragmentEntryProcessorConstants.
			KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR;
	}

	@Reference
	private JSONFactory _jsonFactory;

}