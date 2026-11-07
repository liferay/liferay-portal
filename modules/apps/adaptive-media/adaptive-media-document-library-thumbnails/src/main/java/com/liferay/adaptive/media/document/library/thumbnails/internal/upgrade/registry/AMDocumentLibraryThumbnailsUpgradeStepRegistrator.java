/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.upgrade.registry;

import com.liferay.adaptive.media.document.library.thumbnails.internal.upgrade.v1_0_0.DocumentLibraryThumbnailsConfigurationUpgradeProcess;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = UpgradeStepRegistrator.class)
public class AMDocumentLibraryThumbnailsUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		// See LPS-86356

		registry.register(
			"1.0.0", "1.0.1",
			new DocumentLibraryThumbnailsConfigurationUpgradeProcess(
				_amImageConfigurationHelper, _companyLocalService,
				_prefsProps));
	}

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private PrefsProps _prefsProps;

}