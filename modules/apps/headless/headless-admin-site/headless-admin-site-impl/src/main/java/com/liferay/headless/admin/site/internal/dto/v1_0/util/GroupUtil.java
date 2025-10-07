/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Mikel Lorza
 */
public class GroupUtil {

	public static String getExternalReferenceCode(
			String externalReferenceCode, long scopeGroupId)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			return externalReferenceCode;
		}

		Group group = GroupLocalServiceUtil.getGroup(scopeGroupId);

		return group.getExternalReferenceCode();
	}

}