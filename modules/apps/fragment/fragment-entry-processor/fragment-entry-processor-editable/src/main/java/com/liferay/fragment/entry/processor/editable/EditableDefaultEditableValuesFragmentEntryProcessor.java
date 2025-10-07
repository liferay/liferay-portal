/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.entry.processor.editable.parser.EditableElementParser;
import com.liferay.fragment.entry.processor.util.EditableFragmentEntryProcessorUtil;
import com.liferay.fragment.processor.DefaultEditableValuesFragmentEntryProcessor;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=2",
	service = DefaultEditableValuesFragmentEntryProcessor.class
)
public class EditableDefaultEditableValuesFragmentEntryProcessor
	implements DefaultEditableValuesFragmentEntryProcessor {

	@Override
	public JSONObject getDefaultEditableValuesJSONObject(
		JSONObject configurationJSONObject, Document document) {

		JSONObject defaultEditableValuesJSONObject =
			_jsonFactory.createJSONObject();

		for (Element element :
				document.getElementsByAttribute("data-lfr-editable-id")) {

			JSONObject defaultValueJSONObject = _getDefaultValueJSONObject(
				element, element.attr("data-lfr-editable-type"));

			if (defaultValueJSONObject == null) {
				continue;
			}

			defaultEditableValuesJSONObject.put(
				EditableFragmentEntryProcessorUtil.getElementId(element),
				defaultValueJSONObject);
		}

		for (Element element : document.getElementsByTag("lfr-editable")) {
			JSONObject defaultValueJSONObject = _getDefaultValueJSONObject(
				element, element.attr("type"));

			if (defaultValueJSONObject == null) {
				continue;
			}

			defaultEditableValuesJSONObject.put(
				EditableFragmentEntryProcessorUtil.getElementId(element),
				defaultValueJSONObject);
		}

		return defaultEditableValuesJSONObject;
	}

	@Override
	public String getKey() {
		return FragmentEntryProcessorConstants.
			KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, EditableElementParser.class, "type");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private JSONObject _getDefaultValueJSONObject(
		Element element, String type) {

		EditableElementParser editableElementParser =
			_serviceTrackerMap.getService(type);

		if (editableElementParser == null) {
			return null;
		}

		return JSONUtil.put(
			"config", editableElementParser.getAttributes(element)
		).put(
			"defaultValue", editableElementParser.getValue(element)
		);
	}

	@Reference
	private JSONFactory _jsonFactory;

	private ServiceTrackerMap<String, EditableElementParser> _serviceTrackerMap;

}