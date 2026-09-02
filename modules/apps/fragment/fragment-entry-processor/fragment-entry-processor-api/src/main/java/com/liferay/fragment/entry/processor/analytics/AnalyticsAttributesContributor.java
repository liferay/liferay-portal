/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.analytics;

import com.liferay.fragment.entry.processor.helper.InfoItemFieldMapped;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;
import java.util.Map;

/**
 * @author Marcos Martins
 */
public interface AnalyticsAttributesContributor {

	public Map<String, Object> getAnalyticsAttributes(
			InfoItemFieldMapped infoItemFieldMapped, Locale locale)
		throws PortalException;

}