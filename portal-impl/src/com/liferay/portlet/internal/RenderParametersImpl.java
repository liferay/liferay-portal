/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.MutableRenderParameters;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Neil Griffin
 */
public class RenderParametersImpl
	extends BasePortletParametersImpl<MutableRenderParameters>
	implements LiferayRenderParameters {

	public RenderParametersImpl(
		Map<String, String[]> parameterMap,
		Set<String> publicRenderParameterNames, String namespace) {

		super(
			parameterMap, namespace,
			copiedMap -> new MutableRenderParametersImpl(
				copiedMap, _nullSafe(publicRenderParameterNames)));

		_publicRenderParameterNames = _nullSafe(publicRenderParameterNames);
	}

	@Override
	public Set<String> getPublicRenderParameterNames() {
		return _publicRenderParameterNames;
	}

	@Override
	public boolean isPublic(String name) {
		return _publicRenderParameterNames.contains(name);
	}

	private static Set<String> _nullSafe(Set<String> set) {
		if (set == null) {
			return Collections.emptySet();
		}

		return set;
	}

	private final Set<String> _publicRenderParameterNames;

}