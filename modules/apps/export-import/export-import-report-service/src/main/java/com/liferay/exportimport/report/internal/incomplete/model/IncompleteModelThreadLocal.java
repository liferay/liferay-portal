/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.incomplete.model;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Carlos Correa
 */
public class IncompleteModelThreadLocal {

	public static boolean isIncompleteModel() {
		return _incompleteModel.get();
	}

	public static SafeCloseable setIncompleteModelWithSafeCloseable(
		boolean incompleteModel) {

		return _incompleteModel.setWithSafeCloseable(incompleteModel);
	}

	private static final CentralizedThreadLocal<Boolean> _incompleteModel =
		new CentralizedThreadLocal<>(
			IncompleteModelThreadLocal.class + "._incompleteModel",
			() -> Boolean.FALSE);

}