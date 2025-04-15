/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.helper;

import com.liferay.saml.runtime.SamlException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Michael C. Han
 */
public interface SamlHttpRequestHelper {

	public String getEntityDescriptorString(
			HttpServletRequest httpServletRequest)
		throws SamlException;

	public String getRequestPath(HttpServletRequest httpServletRequest);

}