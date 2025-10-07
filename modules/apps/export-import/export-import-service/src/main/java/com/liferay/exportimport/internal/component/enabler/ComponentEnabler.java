/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.component.enabler;

import com.liferay.exportimport.internal.scheduler.CheckSystemEventSchedulerJobConfiguration;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(service = {})
public class ComponentEnabler {

	@Activate
	protected void activate(ComponentContext componentContext) {
		if (PropsValues.STAGING_SYSTEM_EVENT_CHECK_INTERVAL > 0) {
			componentContext.enableComponent(
				CheckSystemEventSchedulerJobConfiguration.class.getName());
		}
	}

}