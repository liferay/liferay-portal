/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.engine;

import java.util.List;

/**
 * @author Jose Luis Navarro
 */
public interface DataMaskingEngine {

	public void evictPattern(String regex);

	public String redact(
		long companyId, List<String> maskExternalReferenceCodes, String text);

}