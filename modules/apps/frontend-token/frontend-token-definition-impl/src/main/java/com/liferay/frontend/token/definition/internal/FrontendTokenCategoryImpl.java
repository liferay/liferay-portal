/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenCategory;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.frontend.token.definition.FrontendTokenSet;
import com.liferay.frontend.token.definition.internal.json.JSONLocalizer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * @author Iván Zaera
 */
public class FrontendTokenCategoryImpl implements FrontendTokenCategory {

	public FrontendTokenCategoryImpl(
		FrontendTokenDefinitionImpl frontendTokenDefinitionImpl,
		JSONObject jsonObject) {

		_frontendTokenDefinitionImpl = frontendTokenDefinitionImpl;

		_jsonLocalizer = frontendTokenDefinitionImpl.createJSONLocalizer(
			jsonObject);

		JSONArray frontendTokenSetsJSONArray = jsonObject.getJSONArray(
			"frontendTokenSets");

		if (frontendTokenSetsJSONArray == null) {
			return;
		}

		for (int i = 0; i < frontendTokenSetsJSONArray.length(); i++) {
			FrontendTokenSet frontendTokenSet = new FrontendTokenSetImpl(
				this, frontendTokenSetsJSONArray.getJSONObject(i));

			_frontendTokenMappings.addAll(
				frontendTokenSet.getFrontendTokenMappings());

			_frontendTokens.addAll(frontendTokenSet.getFrontendTokens());

			_frontendTokenSets.add(frontendTokenSet);
		}
	}

	@Override
	public FrontendTokenDefinitionImpl getFrontendTokenDefinition() {
		return _frontendTokenDefinitionImpl;
	}

	@Override
	public Collection<FrontendTokenMapping> getFrontendTokenMappings() {
		return _frontendTokenMappings;
	}

	@Override
	public Collection<FrontendTokenSet> getFrontendTokenSets() {
		return _frontendTokenSets;
	}

	@Override
	public Collection<FrontendToken> getFrontendTokens() {
		return _frontendTokens;
	}

	@Override
	public JSONObject getJSONObject(Locale locale) {
		return _jsonLocalizer.getJSONObject(locale);
	}

	private final FrontendTokenDefinitionImpl _frontendTokenDefinitionImpl;
	private final Collection<FrontendTokenMapping> _frontendTokenMappings =
		new ArrayList<>();
	private final Collection<FrontendTokenSet> _frontendTokenSets =
		new ArrayList<>();
	private final Collection<FrontendToken> _frontendTokens = new ArrayList<>();
	private final JSONLocalizer _jsonLocalizer;

}