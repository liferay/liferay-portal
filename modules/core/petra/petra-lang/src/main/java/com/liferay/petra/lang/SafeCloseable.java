/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.lang;

/**
 * @author Preston Crary
 */
@FunctionalInterface
public interface SafeCloseable extends AutoCloseable {

	public static final SafeCloseable EMPTY = () -> {
	};

	@Override
	public void close();

}