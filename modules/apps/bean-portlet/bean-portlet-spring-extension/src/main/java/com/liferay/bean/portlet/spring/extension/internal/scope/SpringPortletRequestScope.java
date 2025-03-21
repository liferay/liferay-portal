/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.scope;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.annotations.PortletRequestScoped;

/**
 * @author Neil Griffin
 */
public class SpringPortletRequestScope extends BaseScope {

	@Override
	public String getScopeName() {
		return PortletRequestScoped.class.getSimpleName();
	}

	@Override
	protected SpringScopedBean getSpringScopedBean(String name) {
		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		if (springScopedBeanManager == null) {
			_log.error(
				"Attempted to get a @PortletRequestScoped bean named " + name +
					" outside the scope of a portlet request");

			return null;
		}

		return springScopedBeanManager.getPortletRequestScopedBean(name);
	}

	@Override
	protected void setSpringScopedBean(
		String name, SpringScopedBean springScopedBean) {

		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		springScopedBeanManager.setPortletRequestScopedBean(
			name, springScopedBean);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpringPortletRequestScope.class);

}