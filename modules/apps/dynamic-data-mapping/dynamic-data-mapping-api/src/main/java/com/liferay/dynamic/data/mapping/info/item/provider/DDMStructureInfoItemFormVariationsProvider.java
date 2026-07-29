/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.info.item.provider;

import com.liferay.info.item.provider.InfoItemFormVariationsProvider;

/**
 * @author Víctor Galán
 */
public interface DDMStructureInfoItemFormVariationsProvider<T>
	extends InfoItemFormVariationsProvider<T> {

	public long getDDMStructureId(String formVariationKey);

}