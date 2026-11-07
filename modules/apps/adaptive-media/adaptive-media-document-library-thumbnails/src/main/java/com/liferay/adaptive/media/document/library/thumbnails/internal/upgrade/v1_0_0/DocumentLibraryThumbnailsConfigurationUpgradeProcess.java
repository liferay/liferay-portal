/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.upgrade.v1_0_0;

import com.liferay.adaptive.media.document.library.thumbnails.internal.util.AMCompanyThumbnailConfigurationUtil;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PrefsProps;

/**
 * @author Roberto Díaz
 */
public class DocumentLibraryThumbnailsConfigurationUpgradeProcess
	extends UpgradeProcess {

	public DocumentLibraryThumbnailsConfigurationUpgradeProcess(
		AMImageConfigurationHelper amImageConfigurationHelper,
		CompanyLocalService companyLocalService, PrefsProps prefsProps) {

		_amImageConfigurationHelper = amImageConfigurationHelper;
		_companyLocalService = companyLocalService;
		_prefsProps = prefsProps;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_companyLocalService.forEachCompany(
				company -> {
					try {
						AMCompanyThumbnailConfigurationUtil.initializeCompany(
							_amImageConfigurationHelper, company, _prefsProps);
					}
					catch (Exception exception) {
						_log.error(exception);
					}
				});
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentLibraryThumbnailsConfigurationUpgradeProcess.class);

	private final AMImageConfigurationHelper _amImageConfigurationHelper;
	private final CompanyLocalService _companyLocalService;
	private final PrefsProps _prefsProps;

}