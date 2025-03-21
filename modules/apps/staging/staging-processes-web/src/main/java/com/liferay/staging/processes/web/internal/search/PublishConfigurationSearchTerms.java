/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Máté Thurzó
 */
public class PublishConfigurationSearchTerms
	extends PublishConfigurationDisplayTerms {

	public PublishConfigurationSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		description = DAOParamUtil.getString(portletRequest, DESCRIPTION);
		name = DAOParamUtil.getString(portletRequest, NAME);
	}

}