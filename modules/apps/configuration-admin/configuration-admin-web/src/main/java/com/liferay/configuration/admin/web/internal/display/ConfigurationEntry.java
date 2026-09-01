/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Locale;
import java.util.Map;

/**
 * @author Jorge Ferrer
 */
public interface ConfigurationEntry {

	public String getCategory();

	public String getEditURL(
		RenderRequest renderRequest, RenderResponse renderResponse);

	public Map<String, String> getEditURLParameters();

	public String getKey();

	public String getName(Locale locale);

	public String getScope();

	public boolean isDeprecated();

}