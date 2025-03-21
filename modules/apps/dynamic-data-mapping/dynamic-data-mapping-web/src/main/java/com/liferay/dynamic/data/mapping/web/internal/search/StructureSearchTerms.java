/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

/**
 * @author Eduardo Lundgren
 */
public class StructureSearchTerms extends StructureDisplayTerms {

	public StructureSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		description = DAOParamUtil.getString(portletRequest, DESCRIPTION);
		name = DAOParamUtil.getString(portletRequest, NAME);
		searchRestriction = DAOParamUtil.getBoolean(
			portletRequest, SEARCH_RESTRICTION);
		storageType = DAOParamUtil.getString(portletRequest, STORAGE_TYPE);
	}

	public int getStatus() {
		return _status;
	}

	public void setStatus(int status) {
		_status = status;
	}

	private int _status = WorkflowConstants.STATUS_ANY;

}