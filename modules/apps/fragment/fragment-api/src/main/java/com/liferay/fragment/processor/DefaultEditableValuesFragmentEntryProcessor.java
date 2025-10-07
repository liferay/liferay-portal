/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.processor;

import com.liferay.portal.kernel.json.JSONObject;

import org.jsoup.nodes.Document;

/**
 * @author Eudaldo Alonso
 */
public interface DefaultEditableValuesFragmentEntryProcessor {

	public JSONObject getDefaultEditableValuesJSONObject(
		JSONObject configurationJSONObject, Document document);

	public String getKey();

}