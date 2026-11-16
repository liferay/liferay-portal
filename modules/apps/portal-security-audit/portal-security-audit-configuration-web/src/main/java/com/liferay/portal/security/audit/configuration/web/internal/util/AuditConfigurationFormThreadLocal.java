/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.util;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Álvaro Saugar
 */
public class AuditConfigurationFormThreadLocal {

	public static Boolean isPreviousPseudonymizationEnabled() {
		return _previousPseudonymizationEnabled.get();
	}

	public static void removePreviousPseudonymizationEnabled() {
		_previousPseudonymizationEnabled.remove();
	}

	public static void setPreviousPseudonymizationEnabled(
		boolean previousPseudonymizationEnabled) {

		_previousPseudonymizationEnabled.set(previousPseudonymizationEnabled);
	}

	private static final ThreadLocal<Boolean> _previousPseudonymizationEnabled =
		new CentralizedThreadLocal<>(
			AuditConfigurationFormThreadLocal.class +
				"._previousPseudonymizationEnabled");

}