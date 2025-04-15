/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.resource;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.odata.entity.EntityModel;

import jakarta.ws.rs.core.MultivaluedMap;

/**
 * @author Brian Wing Shun Chan
 */
public interface EntityModelResource {

	public EntityModel getEntityModel(MultivaluedMap<?, ?> multivaluedMap)
		throws Exception;

	public void setContextCompany(Company contextCompany);

}