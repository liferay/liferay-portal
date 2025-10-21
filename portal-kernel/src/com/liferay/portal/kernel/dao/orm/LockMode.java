/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

/**
 * @author Brian Wing Shun Chan
 */
public enum LockMode {

	NONE, OPTIMISTIC, OPTIMISTIC_FORCE_INCREMENT, PESSIMISTIC_FORCE_INCREMENT,
	PESSIMISTIC_READ, PESSIMISTIC_WRITE, READ, UPGRADE_NOWAIT,
	UPGRADE_SKIPLOCKED, WRITE

}