/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.util;

import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;

/**
 * @author Javier Gamarra
 */
public class CreatorUtil {

	public static Creator toCreator(Portal portal, User user) {
		if ((user == null) || user.isGuestUser()) {
			return null;
		}

		return new Creator() {
			{
				setAdditionalName(user::getMiddleName);
				setContentType(() -> "UserAccount");
				setExternalReferenceCode(user::getExternalReferenceCode);
				setFamilyName(user::getLastName);
				setGivenName(user::getFirstName);
				setId(user::getUserId);
				setImage(
					() -> {
						if (user.getPortraitId() == 0) {
							return null;
						}

						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setPathImage(portal.getPathImage());
							}
						};

						return user.getPortraitURL(themeDisplay);
					});
				setName(user::getFullName);
				setProfileURL(
					() -> {
						Group group = user.getGroup();

						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setPortalURL(StringPool.BLANK);
								setSiteGroupId(group.getGroupId());
							}
						};

						return group.getDisplayURL(themeDisplay);
					});
			}
		};
	}

}