/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.fields;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Ivica Cardic
 */
public class NestedFieldsContextThreadLocal {

	public static NestedFieldsContext getNestedFieldsContext() {
		return _nestedFieldsContext.get();
	}

	public static void setNestedFieldsContext(
		NestedFieldsContext nestedFieldsContext) {

		_nestedFieldsContext.set(nestedFieldsContext);
	}

	public static SafeCloseable setNestedFieldsContextWithSafeCloseable(
		NestedFieldsContext nestedFieldsContext) {

		return _nestedFieldsContext.setWithSafeCloseable(nestedFieldsContext);
	}

	private static final CentralizedThreadLocal<NestedFieldsContext>
		_nestedFieldsContext = new CentralizedThreadLocal<>(
			NestedFieldsContextThreadLocal.class + "._nestedFieldsContext");

}