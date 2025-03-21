/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.scope;

import jakarta.portlet.annotations.PortletSessionScoped;

/**
 * @author Neil Griffin
 */
public class SpringPortletSessionScope extends BaseScope {

	public SpringPortletSessionScope(int subscope) {
		_subscope = subscope;
	}

	@Override
	public String getScopeName() {
		return PortletSessionScoped.class.getSimpleName();
	}

	@Override
	protected SpringScopedBean getSpringScopedBean(String name) {
		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		return springScopedBeanManager.getPortletSessionScopedBean(
			_subscope, name);
	}

	@Override
	protected void setSpringScopedBean(
		String name, SpringScopedBean springScopedBean) {

		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		springScopedBeanManager.setPortletSessionScopedBean(
			_subscope, name, springScopedBean);
	}

	private final int _subscope;

}