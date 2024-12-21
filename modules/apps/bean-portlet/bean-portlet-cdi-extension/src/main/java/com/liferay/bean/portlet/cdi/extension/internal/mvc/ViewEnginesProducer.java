/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;

import jakarta.mvc.engine.ViewEngine;

import jakarta.portlet.PortletContext;

import jakarta.ws.rs.core.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author Neil Griffin
 */
@ApplicationScoped
public class ViewEnginesProducer {

	@ApplicationScoped
	@Produces
	@ViewEngines
	public List<ViewEngine> getViewEngines(
		BeanManager beanManager, Configuration configuration,
		PortletContext portletContext) {

		List<ViewEngine> viewEngines = BeanUtil.getBeanInstances(
			beanManager, ViewEngine.class);

		viewEngines.add(new ViewEngineJspImpl(configuration, portletContext));

		Collections.sort(viewEngines, new ViewEnginePriorityComparator());

		return viewEngines;
	}

	private static class ViewEnginePriorityComparator
		extends BaseDescendingPriorityComparator<ViewEngine> {

		private ViewEnginePriorityComparator() {

			// The Javadoc for jakarta.mvc.engine.ViewEngine states "View
			// engines can be decorated with jakarta.annotation.Priority to
			// indicate their priority; otherwise the priority is assumed to be
			// ViewEngine.PRIORITY_APPLICATION."

			super(ViewEngine.PRIORITY_APPLICATION);
		}

	}

}