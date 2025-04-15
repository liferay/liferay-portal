/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.scope;

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;

import jakarta.portlet.annotations.RenderStateScoped;

import java.lang.annotation.Annotation;

/**
 * @author Neil Griffin
 */
public class RenderStateBeanContext extends BaseContextImpl {

	@Override
	public <T> T get(
		Contextual<T> contextual, CreationalContext<T> creationalContext) {

		ScopedBeanManager scopedBeanManager =
			ScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		return scopedBeanManager.getRenderStateScopedBean(
			(Bean<T>)contextual, creationalContext);
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return RenderStateScoped.class;
	}

}