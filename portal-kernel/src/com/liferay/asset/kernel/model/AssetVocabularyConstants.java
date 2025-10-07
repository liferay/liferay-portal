/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

/**
 * @author Rubén Pulido
 */
public class AssetVocabularyConstants {

	public static final long EMPTY_VOCABULARY_ID = -1;

	public static final int VISIBILITY_TYPE_EMPTY = -1;

	public static final int VISIBILITY_TYPE_INTERNAL = 1;

	public static final int VISIBILITY_TYPE_PUBLIC = 0;

	public static final int[] VISIBILITY_TYPES = {
		VISIBILITY_TYPE_INTERNAL, VISIBILITY_TYPE_PUBLIC
	};

}