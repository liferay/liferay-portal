/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.RenderResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Raymond Augé
 * @author Neil Griffin
 */
@ProviderType
public interface LiferayRenderResponse
	extends LiferayPortletResponse, MimeResponse, RenderResponse {

	public String getTitle();

	public boolean getUseDefaultTemplate();

	public void setResourceName(String resourceName);

	public void setUseDefaultTemplate(Boolean useDefaultTemplate);

}