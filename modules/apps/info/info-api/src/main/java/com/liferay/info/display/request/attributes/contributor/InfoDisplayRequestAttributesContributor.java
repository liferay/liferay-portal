/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.display.request.attributes.contributor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Pavel Savinov
 */
public interface InfoDisplayRequestAttributesContributor {

	public void addAttributes(HttpServletRequest httpServletRequest);

}