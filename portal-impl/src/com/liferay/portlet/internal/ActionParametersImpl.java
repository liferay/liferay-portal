/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.MutableActionParameters;

import java.util.Map;

/**
 * @author Neil Griffin
 */
public class ActionParametersImpl
	extends BasePortletParametersImpl<MutableActionParameters>
	implements ActionParameters {

	public ActionParametersImpl(
		Map<String, String[]> parameterMap, String namespace) {

		super(parameterMap, namespace, MutableActionParametersImpl::new);
	}

}