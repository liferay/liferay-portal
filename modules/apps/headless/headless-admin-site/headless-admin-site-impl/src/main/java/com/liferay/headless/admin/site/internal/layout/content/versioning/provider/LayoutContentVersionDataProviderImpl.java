/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.layout.content.versioning.provider;

import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.layout.content.provider.LayoutContentVersionDataProvider;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = LayoutContentVersionDataProvider.class)
public class LayoutContentVersionDataProviderImpl
	implements LayoutContentVersionDataProvider {

	@Override
	public String getLayoutContentVersionData(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		PageSpecification pageSpecification =
			_pageSpecificationDTOConverter.toDTO(
				DTOConverterContextUtil.getDTOConverterContext(
					false,
					HashMapBuilder.<String, Object>put(
						"companyId", layout.getCompanyId()
					).put(
						"layoutPlid", layout.getPlid()
					).put(
						"scopeGroupId", layout.getGroupId()
					).build(),
					_dtoConverterRegistry, serviceContext.getRequest(),
					layout.getPlid(),
					LocaleUtil.fromLanguageId(layout.getDefaultLanguageId()),
					null,
					_userLocalService.getUser(serviceContext.getUserId())),
				layout);

		return pageSpecification.toString();
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

	@Reference
	private UserLocalService _userLocalService;

}