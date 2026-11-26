/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.url.provider;

import com.liferay.launch.url.provider.LaunchSetEditURLProvider;
import com.liferay.portal.kernel.launch.LaunchSetMode;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Correa
 */
@Component(service = LaunchSetEditURLProvider.class)
public class LaunchSetEditURLProviderImpl implements LaunchSetEditURLProvider {

	@Override
	public String getEditURL(long launchSetId, String url) {
		if ((launchSetId <= 0) || Validator.isNull(url)) {
			return url;
		}

		return HttpComponentsUtil.setParameter(
			HttpComponentsUtil.setParameter(
				url, "p_launch_set_id", launchSetId),
			"p_launch_set_mode", LaunchSetMode.EDIT.getValue());
	}

}