/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.media.internal;

import com.liferay.commerce.media.CommerceMediaProvider;
import com.liferay.commerce.media.internal.configuration.CommerceMediaDefaultImageConfiguration;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PropsKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = CommerceMediaProvider.class)
public class CommerceMediaProviderImpl implements CommerceMediaProvider {

	@Override
	public FileEntry getDefaultImageFileEntry(long companyId, long groupId)
		throws Exception {

		CommerceMediaDefaultImageConfiguration
			commerceMediaDefaultImageConfiguration =
				_configurationProvider.getGroupConfiguration(
					CommerceMediaDefaultImageConfiguration.class, companyId,
					groupId);

		FileEntry fileEntry = null;

		try {
			fileEntry = _dlAppLocalService.getFileEntry(
				commerceMediaDefaultImageConfiguration.defaultFileEntryId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			Company company = _companyLocalService.getCompany(companyId);

			fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					company.getGroupId(), PropsKeys.IMAGE_DEFAULT_COMPANY_LOGO);
		}

		return fileEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceMediaProviderImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLAppLocalService _dlAppLocalService;

}