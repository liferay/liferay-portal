/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

/**
 * @author Drew Brokke
 */
public class CommerceGroupThreadLocal {

	public static Group get() {
		return _commerceGroup.get();
	}

	public static void set(Group group) {
		_commerceGroup.set(group);
	}

	public static SafeCloseable setCommerceGroupWithSafeCloseable(
		long groupId) {

		return _commerceGroup.setWithSafeCloseable(
			GroupLocalServiceUtil.fetchGroup(groupId));
	}

	private static final CentralizedThreadLocal<Group> _commerceGroup =
		new CentralizedThreadLocal<>(
			CommerceGroupThreadLocal.class + "._commerceGroup");

}