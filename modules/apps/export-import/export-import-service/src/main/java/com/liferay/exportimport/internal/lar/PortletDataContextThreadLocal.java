/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Alejandro Tardín
 */
public class PortletDataContextThreadLocal {

	public static PortletDataContext getPortletDataContext() {
		return _portletDataContext.get();
	}

	public static SafeCloseable setPortletDataContextWithSafeCloseable(
		PortletDataContext portletDataContext) {

		return _portletDataContext.setWithSafeCloseable(portletDataContext);
	}

	private static final CentralizedThreadLocal<PortletDataContext>
		_portletDataContext = new CentralizedThreadLocal<>(
			PortletDataContextThreadLocal.class + "._portletDataContext",
			() -> null);

}