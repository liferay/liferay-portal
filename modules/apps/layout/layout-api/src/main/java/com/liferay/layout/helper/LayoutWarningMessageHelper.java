/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.helper;

import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Pablo Molina
 */
@ProviderType
public interface LayoutWarningMessageHelper {

	public JSONObject getCollectionWarningMessageJSONObject(
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem,
			HttpServletRequest httpServletRequest)
		throws Exception;

	public JSONObject getFragmentWarningMessageJsonObject(
		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

}