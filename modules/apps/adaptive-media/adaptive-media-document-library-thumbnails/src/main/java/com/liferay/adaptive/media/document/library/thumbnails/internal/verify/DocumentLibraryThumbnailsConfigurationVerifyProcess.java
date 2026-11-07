/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.verify;

import com.liferay.adaptive.media.document.library.thumbnails.internal.util.AMCompanyThumbnailConfigurationUtil;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.verify.VerifyProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(property = "initial.deployment=true", service = VerifyProcess.class)
public class DocumentLibraryThumbnailsConfigurationVerifyProcess
	extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
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

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentLibraryThumbnailsConfigurationVerifyProcess.class);

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private PrefsProps _prefsProps;

}