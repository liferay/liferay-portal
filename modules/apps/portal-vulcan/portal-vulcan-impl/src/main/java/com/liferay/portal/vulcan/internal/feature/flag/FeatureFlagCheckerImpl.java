/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.feature.flag;

import com.liferay.portal.vulcan.feature.flag.FeatureFlagChecker;

import java.lang.reflect.Method;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Raposo
 * @author Carlos Correa
 */
@Component(service = FeatureFlagChecker.class)
public class FeatureFlagCheckerImpl implements FeatureFlagChecker {

	@Override
	public boolean isEnabled(long companyId, Method method) {
		return FeatureFlagUtil.isEnabled(companyId, method);
	}

}