/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.display.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionVirtualSettingRequestHelper extends BaseRequestHelper {

	public CPDefinitionVirtualSettingRequestHelper(
		RenderRequest renderRequest) {

		super(PortalUtil.getHttpServletRequest(renderRequest));
	}

}