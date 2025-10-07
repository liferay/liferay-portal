/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

import com.liferay.petra.string.StringPool;

/**
 * @author Jorge Ferrer
 */
public class AssetCategoryConstants {

	public static final long ALL_CLASS_NAME_ID = 0;

	public static final String ALL_CLASS_NAME_IDS_AND_CLASS_TYPE_PKS =
		ALL_CLASS_NAME_ID + StringPool.COLON +
			AssetCategoryConstants.ALL_CLASS_TYPE_PK;

	public static final long ALL_CLASS_TYPE_PK = -1;

	public static final long DEFAULT_PARENT_CATEGORY_ID = 0;

	public static final long EMPTY_PARENT_CATEGORY_ID = -1;

	public static final String PROPERTY_KEY_VALUE_SEPARATOR = "_KEY_VALUE_";

}