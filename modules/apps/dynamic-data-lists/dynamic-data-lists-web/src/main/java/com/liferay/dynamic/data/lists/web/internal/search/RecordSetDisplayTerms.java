/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

/**
 * @author Marcellus Tavares
 */
public class RecordSetDisplayTerms extends DisplayTerms {

	public static final String DESCRIPTION = "description";

	public static final String NAME = "name";

	public RecordSetDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		description = ParamUtil.getString(portletRequest, DESCRIPTION);
		name = ParamUtil.getString(portletRequest, NAME);
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isAndOperator() {
		if (Validator.isNull(description) && Validator.isNull(name)) {
			return true;
		}

		return super.isAndOperator();
	}

	protected String description;
	protected String name;

}