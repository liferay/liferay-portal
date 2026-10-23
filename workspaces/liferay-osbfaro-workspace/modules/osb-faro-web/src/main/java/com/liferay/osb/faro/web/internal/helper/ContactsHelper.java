/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shinn Lok
 */
@Component(service = ContactsHelper.class)
public class ContactsHelper {

	public String getOwnerType(int type) {
		if (type == FaroConstants.TYPE_ACCOUNT) {
			return FieldMappingConstants.OWNER_TYPE_ACCOUNT;
		}
		else if (type == FaroConstants.TYPE_INDIVIDUAL) {
			return FieldMappingConstants.OWNER_TYPE_INDIVIDUAL;
		}

		return FieldMappingConstants.OWNER_TYPE_INDIVIDUAL_SEGMENT;
	}

}