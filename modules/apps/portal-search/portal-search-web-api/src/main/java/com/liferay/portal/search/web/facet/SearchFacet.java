/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.facet;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Eudaldo Alonso
 */
public interface SearchFacet {

	public String getClassName();

	public JSONObject getData();

	public FacetConfiguration getDefaultConfiguration(long companyId);

	public Facet getFacet();

	public String getFacetClassName();

	public FacetConfiguration getFacetConfiguration();

	public String getFieldName();

	public String getId();

	public JSONObject getJSONData(ActionRequest actionRequest);

	public String getLabel();

	public String getOrder();

	public String getTitle();

	public double getWeight();

	public void includeConfiguration(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

	public void includeView(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

	public void init(long companyId, String searchConfiguration)
		throws Exception;

	public void init(
			long companyId, String searchConfiguration,
			SearchContext searchContext)
		throws Exception;

	public boolean isStatic();

}