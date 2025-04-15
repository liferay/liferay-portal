/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.Organization;

import jakarta.portlet.RenderResponse;

import java.util.Set;

/**
 * @author Geyson Silva
 */
public class OrganizationChecker extends EmptyOnClickRowChecker {

	public OrganizationChecker(RenderResponse renderResponse, Set<String> ids) {
		super(renderResponse);

		setRowIds("syncedOrganizationIds");

		_ids = ids;
	}

	@Override
	public boolean isChecked(Object object) {
		Organization organization = (Organization)object;

		return _ids.contains(String.valueOf(organization.getOrganizationId()));
	}

	private final Set<String> _ids;

}