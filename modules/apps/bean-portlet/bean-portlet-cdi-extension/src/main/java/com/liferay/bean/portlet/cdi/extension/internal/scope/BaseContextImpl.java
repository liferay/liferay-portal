/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.scope;

import jakarta.enterprise.context.spi.Context;
import jakarta.enterprise.context.spi.Contextual;

/**
 * @author Neil Griffin
 */
public abstract class BaseContextImpl implements Context {

	@Override
	public <T> T get(Contextual<T> contextual) {
		return get(contextual, null);
	}

	@Override
	public boolean isActive() {
		ScopedBeanManager scopedBeanManager =
			ScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		if ((scopedBeanManager != null) &&
			(scopedBeanManager.getPortletRequest() != null)) {

			return true;
		}

		return false;
	}

}