/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.FragmentDropZonePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem"
	},
	service = DTOConverter.class
)
public class FragmentDropZonePageElementDefinitionDTOConverter
	implements DTOConverter
		<FragmentDropZoneLayoutStructureItem,
		 FragmentDropZonePageElementDefinition> {

	@Override
	public String getContentType() {
		return FragmentDropZonePageElementDefinition.class.getSimpleName();
	}

	@Override
	public FragmentDropZonePageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			FragmentDropZoneLayoutStructureItem
				fragmentDropZoneLayoutStructureItem)
		throws Exception {

		return new FragmentDropZonePageElementDefinition() {
			{
				setFragmentDropZoneId(
					fragmentDropZoneLayoutStructureItem::getFragmentDropZoneId);
				setType(() -> PageElementDefinition.Type.FRAGMENT_DROP_ZONE);
			}
		};
	}

}