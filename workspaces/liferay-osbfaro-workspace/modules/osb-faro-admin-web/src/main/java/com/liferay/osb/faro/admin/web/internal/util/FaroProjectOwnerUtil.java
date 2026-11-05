/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.util;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

/**
 * @author Marcos Martins
 */
public class FaroProjectOwnerUtil {

	public static String fetchOwner(FaroProject faroProject) {
		FaroUser faroUser = FaroUserLocalServiceUtil.fetchOwnerFaroUser(
			faroProject.getGroupId());

		if (faroUser == null) {
			return null;
		}

		if (faroUser.getLiveUserId() <= 0) {
			return faroUser.getEmailAddress();
		}

		User user = UserLocalServiceUtil.fetchUser(faroUser.getLiveUserId());

		if (user != null) {
			return user.getFullName() + " " + user.getEmailAddress();
		}

		return null;
	}

}