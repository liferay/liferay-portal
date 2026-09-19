/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition;

import com.liferay.portal.kernel.json.JSONObject;

import java.util.Collection;
import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Iván Zaera
 */
@ProviderType
public interface FrontendTokenDefinition {

	public Collection<FrontendTokenCategory> getFrontendTokenCategories();

	public Collection<FrontendTokenMapping> getFrontendTokenMappings();

	public Collection<FrontendTokenSet> getFrontendTokenSets();

	public Collection<FrontendToken> getFrontendTokens();

	public JSONObject getJSONObject(Locale locale);

	public int getPriority();

	public String getThemeId();

	public String getThemeName(Locale locale);

	public String getThemeType();

}