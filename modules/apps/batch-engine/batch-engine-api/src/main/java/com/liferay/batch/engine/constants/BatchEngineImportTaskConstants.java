/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.constants;

/**
 * @author Matija Petanjek
 */
public class BatchEngineImportTaskConstants {

	public static final String IMPORT_CREATOR_STRATEGY_KEEP_CREATOR =
		"KEEP_CREATOR";

	public static final int IMPORT_STRATEGY_ON_ERROR_CONTINUE = 1;

	public static final int IMPORT_STRATEGY_ON_ERROR_FAIL = 2;

	public static final String IMPORT_STRATEGY_STRING_ON_ERROR_CONTINUE =
		"ON_ERROR_CONTINUE";

	public static final String IMPORT_STRATEGY_STRING_ON_ERROR_FAIL =
		"ON_ERROR_FAIL";

	public static int getImportStrategy(String importStrategy) {
		if (importStrategy.equals(IMPORT_STRATEGY_STRING_ON_ERROR_CONTINUE)) {
			return IMPORT_STRATEGY_ON_ERROR_CONTINUE;
		}
		else if (importStrategy.equals(IMPORT_STRATEGY_STRING_ON_ERROR_FAIL)) {
			return IMPORT_STRATEGY_ON_ERROR_FAIL;
		}

		throw new IllegalArgumentException(
			"Invalid import strategy " + importStrategy);
	}

	public static String getImportStrategyString(int importStrategy) {
		if (importStrategy == IMPORT_STRATEGY_ON_ERROR_CONTINUE) {
			return IMPORT_STRATEGY_STRING_ON_ERROR_CONTINUE;
		}
		else if (importStrategy == IMPORT_STRATEGY_ON_ERROR_FAIL) {
			return IMPORT_STRATEGY_STRING_ON_ERROR_FAIL;
		}

		throw new IllegalArgumentException(
			"Invalid import strategy " + importStrategy);
	}

}