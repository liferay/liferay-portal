/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.upgrade.v3_2_8;

import com.liferay.document.library.internal.upgrade.helper.DLConfigurationUpgradeHelper;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Marco Galluzzi
 */
public class DLFileEntryConfigurationUpgradeProcess extends UpgradeProcess {

	public DLFileEntryConfigurationUpgradeProcess(
		DLConfigurationUpgradeHelper dlConfigurationUpgradeHelper) {

		_dlConfigurationUpgradeHelper = dlConfigurationUpgradeHelper;
	}

	@Override
	protected void doUpgrade() throws Exception {
		long systemPreviewableProcessorMaxSize =
			_dlConfigurationUpgradeHelper.
				getDLFileEntryConfigurationPreviewableProcessorMaxSize();

		_dlConfigurationUpgradeHelper.
			updateDLFileEntryConfigurationSystemConfiguration(
				systemPreviewableProcessorMaxSize);

		_dlConfigurationUpgradeHelper.updateScopedConfigurations(
			systemPreviewableProcessorMaxSize);

		_dlConfigurationUpgradeHelper.deleteConfigurations(
			DLConfigurationUpgradeHelper.CLASS_NAME_PDF_PREVIEW_CONFIGURATION);
	}

	private final DLConfigurationUpgradeHelper _dlConfigurationUpgradeHelper;

}