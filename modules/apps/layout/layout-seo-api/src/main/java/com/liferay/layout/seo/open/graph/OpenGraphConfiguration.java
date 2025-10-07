/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.open.graph;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Adolfo Pérez
 */
@ProviderType
public interface OpenGraphConfiguration {

	public boolean isLayoutTranslatedLanguagesEnabled(Company company)
		throws PortalException;

	public boolean isLayoutTranslatedLanguagesEnabled(Group group)
		throws PortalException;

	public boolean isOpenGraphEnabled(Group group) throws PortalException;

	public boolean isOpenGraphEnabled(long companyId) throws PortalException;

}