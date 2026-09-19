/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class FriendlyURLMapperThreadLocal {

	public static Map<String, String> getPRPIdentifiers() {
		return _prpIdentifiers.get();
	}

	public static Map<String, String[]> getParentParameters() {
		return _parentParameters.get();
	}

	public static SafeCloseable setPRPIdentifiersWithSafeCloseable(
		Map<String, String> prpIdentifiers) {

		return _prpIdentifiers.setWithSafeCloseable(prpIdentifiers);
	}

	public static SafeCloseable setParentParametersWithSafeCloseable(
		Map<String, String[]> parentParameters) {

		return _parentParameters.setWithSafeCloseable(parentParameters);
	}

	private static final CentralizedThreadLocal<Map<String, String[]>>
		_parentParameters = new CentralizedThreadLocal<>(
			FriendlyURLMapperThreadLocal.class + "._parentParameters");
	private static final CentralizedThreadLocal<Map<String, String>>
		_prpIdentifiers = new CentralizedThreadLocal<>(
			FriendlyURLMapperThreadLocal.class + "._prpIdentifiers");

}