/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.dto.v1_0.util;

import com.liferay.headless.admin.workflow.dto.v1_0.Assignee;
import com.liferay.portal.kernel.model.User;

/**
 * @author Rafael Praxedes
 */
public class AssigneeUtil {

	public static Assignee toAssignee(User user) {
		if ((user == null) || user.isGuestUser()) {
			return null;
		}

		return new Assignee() {
			{
				setId(user::getUserId);
				setName(user::getFullName);
			}
		};
	}

}