/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.index.configuration.contributor;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.index.configuration.contributor.CompanyIndexMappingsContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Murilo Stodolni
 */
@Component(service = CompanyIndexMappingsContributor.class)
public class ObjectCompanyIndexMappingsContributor
	implements CompanyIndexMappingsContributor {

	@Override
	public String contributeMappings() {
		return StringUtil.read(
			getClass(), "dependencies/additional-type-mappings.json");
	}

}