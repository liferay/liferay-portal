/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.empty.model;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Carlos Correa
 */
public class EmptyModelThreadLocal {

	public static boolean isEmptyModel() {
		return _emptyModel.get();
	}

	public static SafeCloseable setEmptyModelWithSafeCloseable(
		boolean emptyModel) {

		return _emptyModel.setWithSafeCloseable(emptyModel);
	}

	private static final CentralizedThreadLocal<Boolean> _emptyModel =
		new CentralizedThreadLocal<>(
			EmptyModelThreadLocal.class + "._emptyModel", () -> Boolean.FALSE);

}