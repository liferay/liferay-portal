/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.processor;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

/**
 * @author Lance Ji
 */
public interface FragmentEntryProcessorRegistry {

	public default JSONArray getAvailableTagsJSONArray() {
		return null;
	}

	public default JSONArray getDataAttributesJSONArray() {
		return null;
	}

	public JSONObject getDefaultEditableValuesJSONObject(
		String html, JSONObject configurationJSONObject);

	public default String processFragmentEntryLinkCSS(
			FragmentEntryLink fragmentEntryLink,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		return fragmentEntryLink.getCss();
	}

	public default String processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		return fragmentEntryLink.getHtml();
	}

	public void validateFragmentEntryHTML(
			String html, JSONObject configurationJSONObject)
		throws PortalException;

}