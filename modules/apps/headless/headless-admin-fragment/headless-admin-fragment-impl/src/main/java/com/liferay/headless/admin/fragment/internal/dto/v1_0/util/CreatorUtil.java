/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.dto.v1_0.util;

import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

/**
 * @author Rubén Pulido
 */
public class CreatorUtil {

	public static Creator toCreator(long userId) {
		User user = UserLocalServiceUtil.fetchUser(userId);

		if (user == null) {
			return null;
		}

		return new Creator() {
			{
				setExternalReferenceCode(user::getExternalReferenceCode);
			}
		};
	}

}