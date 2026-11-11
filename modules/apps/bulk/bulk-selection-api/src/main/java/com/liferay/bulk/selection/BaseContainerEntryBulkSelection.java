/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.selection;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseContainerEntryBulkSelection<T>
	implements BulkSelection<T> {

	public BaseContainerEntryBulkSelection(
		long containerId, Map<String, String[]> parameterMap) {

		_containerId = containerId;
		_parameterMap = parameterMap;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	@Override
	public Serializable serialize() {
		return "all:" + _containerId;
	}

	private final long _containerId;
	private final Map<String, String[]> _parameterMap;

}