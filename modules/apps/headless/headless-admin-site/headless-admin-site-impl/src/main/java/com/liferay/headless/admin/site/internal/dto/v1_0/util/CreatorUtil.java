/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Lourdes Fernández Besada
 */
public class CreatorUtil {

	public static Creator toCreator(long userId, String userName) {
		User user = UserLocalServiceUtil.fetchUser(userId);

		if (user == null) {
			if (Validator.isNull(userName)) {
				return null;
			}

			return new Creator() {
				{
					setName(() -> userName);
				}
			};
		}

		return new Creator() {
			{
				setExternalReferenceCode(user::getExternalReferenceCode);
				setImage(
					() -> {
						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setPathImage(PortalUtil.getPathImage());
							}
						};

						return user.getPortraitURL(themeDisplay);
					});
				setName(user::getFullName);
			}
		};
	}

}