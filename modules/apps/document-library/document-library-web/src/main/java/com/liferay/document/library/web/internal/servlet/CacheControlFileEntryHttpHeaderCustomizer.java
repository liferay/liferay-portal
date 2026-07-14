/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.servlet;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.web.internal.configuration.CacheControlConfiguration;
import com.liferay.document.library.web.internal.configuration.helper.CacheControlConfigurationHelper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.repository.http.header.customizer.FileEntryHttpHeaderCustomizer;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ArrayUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.document.library.web.internal.configuration.CacheControlConfiguration",
	property = "http.header.name=" + HttpHeaders.CACHE_CONTROL,
	service = FileEntryHttpHeaderCustomizer.class
)
public class CacheControlFileEntryHttpHeaderCustomizer
	implements FileEntryHttpHeaderCustomizer {

	@Override
	public String getHttpHeaderValue(FileEntry fileEntry, String currentValue) {
		try {
			return _getHttpHeaderValue(fileEntry, currentValue);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return currentValue;
		}
	}

	private String _getHttpHeaderValue(FileEntry fileEntry, String currentValue)
		throws PortalException {

		if (!CTCollectionThreadLocal.isProductionMode()) {
			return HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE;
		}

		CacheControlConfiguration cacheControlConfiguration =
			_cacheControlConfigurationHelper.
				getCompanyCacheControlConfiguration(fileEntry.getCompanyId());

		if (ArrayUtil.contains(
				cacheControlConfiguration.notCacheableMimeTypes(),
				fileEntry.getMimeType())) {

			return HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE;
		}

		Company company = _companyLocalService.getCompany(
			fileEntry.getCompanyId());

		if (!_dlFileEntryModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(
					company.getGuestUser(), false),
				fileEntry.getPrimaryKey(), ActionKeys.VIEW)) {

			return currentValue;
		}

		if (cacheControlConfiguration.maxAge() <= 0) {
			return cacheControlConfiguration.cacheControl();
		}

		return String.format(
			"%s, max-age=%d", cacheControlConfiguration.cacheControl(),
			cacheControlConfiguration.maxAge());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CacheControlFileEntryHttpHeaderCustomizer.class);

	@Reference
	private CacheControlConfigurationHelper _cacheControlConfigurationHelper;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;

}