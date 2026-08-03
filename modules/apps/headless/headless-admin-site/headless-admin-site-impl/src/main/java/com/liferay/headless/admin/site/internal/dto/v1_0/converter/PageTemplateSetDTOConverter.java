/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(
	property = "dto.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateCollection",
	service = DTOConverter.class
)
public class PageTemplateSetDTOConverter
	implements DTOConverter<LayoutPageTemplateCollection, PageTemplateSet> {

	@Override
	public String getContentType() {
		return PageTemplateSet.class.getSimpleName();
	}

	@Override
	public PageTemplateSet toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		return new PageTemplateSet() {
			{
				setCreator(
					() -> CreatorUtil.toCreator(
						layoutPageTemplateCollection.getUserId(),
						layoutPageTemplateCollection.getUserName()));
				setDateCreated(layoutPageTemplateCollection::getCreateDate);
				setDateModified(layoutPageTemplateCollection::getModifiedDate);
				setDescription(layoutPageTemplateCollection::getDescription);
				setExternalReferenceCode(
					layoutPageTemplateCollection::getExternalReferenceCode);
				setKey(
					layoutPageTemplateCollection::
						getLayoutPageTemplateCollectionKey);
				setName(layoutPageTemplateCollection::getName);
			}
		};
	}

}