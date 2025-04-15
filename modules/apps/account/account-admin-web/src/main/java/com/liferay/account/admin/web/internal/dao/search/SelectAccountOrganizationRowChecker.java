/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.dao.search;

import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.Organization;

import jakarta.portlet.PortletResponse;

/**
 * @author Pei-Jung Lan
 */
public class SelectAccountOrganizationRowChecker
	extends EmptyOnClickRowChecker {

	public SelectAccountOrganizationRowChecker(
		PortletResponse portletResponse, long accountEntryId) {

		super(portletResponse);

		_accountEntryId = accountEntryId;
	}

	@Override
	public boolean isChecked(Object object) {
		Organization organization = (Organization)object;

		return AccountEntryOrganizationRelLocalServiceUtil.
			hasAccountEntryOrganizationRel(
				_accountEntryId, organization.getOrganizationId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final long _accountEntryId;

}