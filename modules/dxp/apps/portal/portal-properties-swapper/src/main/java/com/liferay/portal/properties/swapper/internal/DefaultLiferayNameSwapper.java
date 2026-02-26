/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.properties.swapper.internal;

import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(enabled = false, service = {})
public class DefaultLiferayNameSwapper {

	@Activate
	protected void activate() {
		if (PropsHelperUtil.isCustomized(
				PropsKeys.APPLICATIONS_MENU_DEFAULT_LIFERAY_NAME)) {

			return;
		}

		PropsValues.APPLICATIONS_MENU_DEFAULT_LIFERAY_NAME = "Liferay DXP";
	}

	@Reference(target = ModuleServiceLifecycle.PORTLETS_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}