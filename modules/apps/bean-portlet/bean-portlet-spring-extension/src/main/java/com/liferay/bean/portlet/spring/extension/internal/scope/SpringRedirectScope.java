/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.scope;

import jakarta.mvc.RedirectScoped;

/**
 * @author Neil Griffin
 */
public class SpringRedirectScope extends BaseScope {

	@Override
	public String getScopeName() {
		return RedirectScoped.class.getSimpleName();
	}

	@Override
	protected SpringScopedBean getSpringScopedBean(String name) {
		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		return springScopedBeanManager.getRedirectScopedBean(name);
	}

	@Override
	protected void setSpringScopedBean(
		String name, SpringScopedBean springScopedBean) {

		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		springScopedBeanManager.setRedirectScopedBean(name, springScopedBean);
	}

}