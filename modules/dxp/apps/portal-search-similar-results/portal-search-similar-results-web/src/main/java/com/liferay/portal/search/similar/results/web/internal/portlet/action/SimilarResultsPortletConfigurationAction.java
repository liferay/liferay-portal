/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.search.similar.results.web.internal.constants.SimilarResultsPortletKeys;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Kevin Tan
 */
@Component(
	property = "jakarta.portlet.name=" + SimilarResultsPortletKeys.SIMILAR_RESULTS,
	service = ConfigurationAction.class
)
public class SimilarResultsPortletConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/similar/results/configuration.jsp";
	}

}