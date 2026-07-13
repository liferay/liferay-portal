/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.dto.v1_0.converter;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rubén Pulido
 */
@Component(
	property = "dto.class.name=com.liferay.fragment.model.FragmentCollection",
	service = DTOConverter.class
)
public class FragmentSetDTOConverter
	implements DTOConverter<FragmentCollection, FragmentSet> {

	@Override
	public String getContentType() {
		return FragmentSet.class.getSimpleName();
	}

	@Override
	public FragmentSet toDTO(
			DTOConverterContext dtoConverterContext,
			FragmentCollection fragmentCollection)
		throws Exception {

		return new FragmentSet() {
			{
				setActions(dtoConverterContext::getActions);
				setCreator(
					() -> CreatorUtil.toCreator(
						fragmentCollection.getUserId()));
				setDateCreated(fragmentCollection::getCreateDate);
				setDateModified(fragmentCollection::getModifiedDate);
				setDescription(fragmentCollection::getDescription);
				setExternalReferenceCode(
					fragmentCollection::getExternalReferenceCode);
				setKey(fragmentCollection::getFragmentCollectionKey);
				setMarketplace(fragmentCollection::isMarketplace);
				setName(fragmentCollection::getName);
			}
		};
	}

}