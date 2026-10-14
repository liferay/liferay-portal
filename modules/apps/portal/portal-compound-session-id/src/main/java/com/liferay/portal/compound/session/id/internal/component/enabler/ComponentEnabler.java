/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.compound.session.id.internal.component.enabler;

import com.liferay.portal.compound.session.id.internal.CompoundSessionIdFilter;
import com.liferay.portal.kernel.servlet.filters.compoundsessionid.CompoundSessionIdSplitterUtil;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(service = {})
public class ComponentEnabler {

	@Activate
	protected void activate(
		BundleContext bundleContext, ComponentContext componentContext) {

		if (CompoundSessionIdSplitterUtil.hasSessionDelimiter()) {
			componentContext.enableComponent(
				CompoundSessionIdFilter.class.getName());
		}
	}

}