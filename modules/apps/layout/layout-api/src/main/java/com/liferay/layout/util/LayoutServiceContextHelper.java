/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Lourdes Fernández Besada
 */
@ProviderType
public interface LayoutServiceContextHelper {

	public AutoCloseable getServiceContextAutoCloseable(Company company)
		throws PortalException;

	public AutoCloseable getServiceContextAutoCloseable(
			Company company, User user)
		throws PortalException;

	public AutoCloseable getServiceContextAutoCloseable(Group group)
		throws PortalException;

	public AutoCloseable getServiceContextAutoCloseable(Layout layout)
		throws PortalException;

	public AutoCloseable getServiceContextAutoCloseable(
			Layout layout, User user)
		throws PortalException;

}