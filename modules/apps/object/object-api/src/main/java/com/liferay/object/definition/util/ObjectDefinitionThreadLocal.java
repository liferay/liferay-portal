/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Shuyang Zhou
 */
public class ObjectDefinitionThreadLocal {

	public static boolean isDeleteObjectDefinitionId(long objectDefinitionId) {
		Long deleteObjectDefinitionId = _deleteObjectDefinitionId.get();

		if ((deleteObjectDefinitionId != null) &&
			(deleteObjectDefinitionId == objectDefinitionId)) {

			return true;
		}

		return false;
	}

	public static boolean isSkipBundleAllowedCheck() {
		return _skipBundleAllowedCheck.get();
	}

	public static SafeCloseable setDeleteObjectDefinitionIdWithSafeCloseable(
		long id) {

		return _deleteObjectDefinitionId.setWithSafeCloseable(id);
	}

	public static SafeCloseable setSkipBundleAllowedCheckWithSafeCloseable(
		boolean skipBundleAllowedCheck) {

		return _skipBundleAllowedCheck.setWithSafeCloseable(
			skipBundleAllowedCheck);
	}

	private static final CentralizedThreadLocal<Long>
		_deleteObjectDefinitionId = new CentralizedThreadLocal<>(
			ObjectEntryThreadLocal.class + "._deleteObjectDefinitionId");
	private static final CentralizedThreadLocal<Boolean>
		_skipBundleAllowedCheck = new CentralizedThreadLocal<>(
			ObjectDefinitionThreadLocal.class + "._skipBundleAllowedCheck",
			() -> false);

}