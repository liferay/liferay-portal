/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.properties.swapper.internal;

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(enabled = false, service = {})
public class DefaultCompanyLogoSwapper {

	@Activate
	protected void activate(BundleContext bundleContext) {
		if (PropsHelperUtil.isCustomized(
				PropsKeys.IMAGE_DEFAULT_COMPANY_LOGO)) {

			return;
		}

		Bundle bundle = bundleContext.getBundle();

		PropsUtil.set(
			PropsKeys.IMAGE_DEFAULT_COMPANY_LOGO,
			bundle.getBundleId() +
				";com/liferay/portal/properties/swapper/internal" +
					"/default_company_logo.png");
	}

}