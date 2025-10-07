/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.feature.flag;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
public interface FeatureFlagManager {

	public List<FeatureFlag> getFeatureFlags(
		long companyId, Predicate<FeatureFlag> predicate);

	public String getJSON(long companyId);

	public boolean isEnabled(long companyId, String key);

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *		#isEnabled(long, String)}
	 */
	@Deprecated
	public boolean isEnabled(String key);

}