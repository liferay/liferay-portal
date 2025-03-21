/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.portlet.LiferayHeaderRequest;

import jakarta.portlet.PortletRequest;

/**
 * @author Neil Griffin
 */
public class HeaderRequestImpl
	extends RenderRequestImpl implements LiferayHeaderRequest {

	@Override
	public String getLifecycle() {
		return PortletRequest.HEADER_PHASE;
	}

}