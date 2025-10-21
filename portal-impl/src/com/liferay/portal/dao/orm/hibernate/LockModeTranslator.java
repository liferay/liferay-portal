/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.orm.LockMode;

/**
 * @author Brian Wing Shun Chan
 */
public class LockModeTranslator {

	public static org.hibernate.LockMode translate(LockMode lockMode) {
		if (lockMode == LockMode.NONE) {
			return org.hibernate.LockMode.NONE;
		}
		else if (lockMode == LockMode.OPTIMISTIC) {
			return org.hibernate.LockMode.OPTIMISTIC;
		}
		else if (lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT) {
			return org.hibernate.LockMode.OPTIMISTIC_FORCE_INCREMENT;
		}
		else if (lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT) {
			return org.hibernate.LockMode.PESSIMISTIC_FORCE_INCREMENT;
		}
		else if (lockMode == LockMode.PESSIMISTIC_READ) {
			return org.hibernate.LockMode.PESSIMISTIC_READ;
		}
		else if (lockMode == LockMode.PESSIMISTIC_WRITE) {
			return org.hibernate.LockMode.PESSIMISTIC_WRITE;
		}
		else if (lockMode == LockMode.READ) {
			return org.hibernate.LockMode.READ;
		}
		else if (lockMode == LockMode.UPGRADE_NOWAIT) {
			return org.hibernate.LockMode.UPGRADE_NOWAIT;
		}
		else if (lockMode == LockMode.UPGRADE_SKIPLOCKED) {
			return org.hibernate.LockMode.UPGRADE_SKIPLOCKED;
		}
		else if (lockMode == LockMode.WRITE) {
			return org.hibernate.LockMode.WRITE;
		}

		return org.hibernate.LockMode.fromExternalForm(lockMode.toString());
	}

}