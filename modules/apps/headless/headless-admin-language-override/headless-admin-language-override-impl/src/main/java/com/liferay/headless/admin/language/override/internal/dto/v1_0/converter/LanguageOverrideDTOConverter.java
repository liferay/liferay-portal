/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.dto.v1_0.converter;

import com.liferay.headless.admin.language.override.dto.v1_0.LanguageOverride;
import com.liferay.headless.admin.language.override.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(
	property = "dto.class.name=com.liferay.portal.language.override.model.PLOEntry",
	service = DTOConverter.class
)
public class LanguageOverrideDTOConverter
	implements DTOConverter<PLOEntry, LanguageOverride> {

	@Override
	public String getContentType() {
		return LanguageOverride.class.getSimpleName();
	}

	@Override
	public PLOEntry getObject(String externalReferenceCode) throws Exception {
		return _ploEntryLocalService.getPLOEntryByExternalReferenceCode(
			externalReferenceCode, CompanyThreadLocal.getCompanyId());
	}

	@Override
	public LanguageOverride toDTO(
			DTOConverterContext dtoConverterContext, PLOEntry ploEntry)
		throws Exception {

		return new LanguageOverride() {
			{
				setCreator(
					() -> NestedFieldsSupplier.supply(
						"creator",
						fieldName -> CreatorUtil.toCreator(
							_portal,
							_userLocalService.fetchUser(
								ploEntry.getUserId()))));
				setDateCreated(ploEntry::getCreateDate);
				setDateModified(ploEntry::getModifiedDate);
				setExternalReferenceCode(ploEntry::getExternalReferenceCode);
				setId(ploEntry::getPloEntryId);
				setKey(ploEntry::getKey);
				setLanguageId(ploEntry::getLanguageId);
				setValue(ploEntry::getValue);
			}
		};
	}

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}