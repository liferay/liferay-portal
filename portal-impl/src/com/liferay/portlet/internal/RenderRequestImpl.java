/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.portlet.LiferayRenderRequest;

import jakarta.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class RenderRequestImpl
	extends PortletRequestImpl implements LiferayRenderRequest {

	@Override
	public String getETag() {
		return null;
	}

	@Override
	public String getLifecycle() {
		return PortletRequest.RENDER_PHASE;
	}

}