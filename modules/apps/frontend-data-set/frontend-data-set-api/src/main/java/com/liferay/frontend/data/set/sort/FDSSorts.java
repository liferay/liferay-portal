/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sort;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.model.FDSSortItem;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Daniel Sanz
 */
public interface FDSSorts {

	public default FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.DETACHED;
	}

	public List<FDSSortItem> getFDSSortItems(
		HttpServletRequest httpServletRequest);

}