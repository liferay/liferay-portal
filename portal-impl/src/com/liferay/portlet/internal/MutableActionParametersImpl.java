/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.MutableActionParameters;

import java.util.Map;

/**
 * @author Neil Griffin
 */
public class MutableActionParametersImpl
	extends BaseMutablePortletParameters<MutableActionParameters>
	implements LiferayMutablePortletParameters, MutableActionParameters {

	public MutableActionParametersImpl(Map<String, String[]> parameterMap) {
		super(parameterMap, MutableActionParametersImpl::new);
	}

}