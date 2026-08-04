/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.util;

import com.amazonaws.regions.DefaultAwsRegionProviderChain;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Christopher Kian
 */
public class AWSRegionUtil {

	public static String getRegion() {
		try {
			DefaultAwsRegionProviderChain defaultAwsRegionProviderChain =
				new DefaultAwsRegionProviderChain();

			return defaultAwsRegionProviderChain.getRegion();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get AWS region from environment", exception);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(AWSRegionUtil.class);

}