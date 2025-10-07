/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.filter;

import aQute.bnd.annotation.ProviderType;

import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Pablo Molina
 */
@ProviderType
public interface FragmentCollectionFilter {

	public default JSONObject getConfigurationJSONObject() {
		return null;
	}

	public String getFilterKey();

	public default String getFilterValueLabel(
		String filterValue, Locale locale) {

		return filterValue;
	}

	public String getLabel(Locale locale);

	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

}