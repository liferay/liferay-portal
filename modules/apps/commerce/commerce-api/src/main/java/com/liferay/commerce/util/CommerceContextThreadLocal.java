/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceContextThreadLocal {

	public static CommerceContext get() {
		return _commerceContext.get();
	}

	public static void set(CommerceContext commerceContext) {
		_commerceContext.set(commerceContext);
	}

	public static SafeCloseable setCommerceContextWithSafeCloseable(
		CommerceContext commerceContext) {

		return _commerceContext.setWithSafeCloseable(commerceContext);
	}

	private static final CentralizedThreadLocal<CommerceContext>
		_commerceContext = new CentralizedThreadLocal<>(
			CommerceContextThreadLocal.class + "._commerceContext");

}