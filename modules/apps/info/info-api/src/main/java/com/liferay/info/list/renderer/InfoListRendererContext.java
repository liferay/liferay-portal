/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.list.renderer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Pavel Savinov
 */
@ProviderType
public interface InfoListRendererContext {

	public HttpServletRequest getHttpServletRequest();

	public HttpServletResponse getHttpServletResponse();

	public String getListItemRendererKey();

	public String getTemplateKey();

}