/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.display.context;

import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.util.TensorFlowDownloadHelper;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Adolfo Pérez
 */
public class EditConfigurationDisplayContext {

	public EditConfigurationDisplayContext(
		TensorFlowDownloadHelper tensorFlowDownloadHelper,
		TensorFlowImageAssetAutoTagProviderCompanyConfiguration
			tensorFlowImageAssetAutoTagProviderCompanyConfiguration) {

		_tensorFlowDownloadHelper = tensorFlowDownloadHelper;
		_tensorFlowImageAssetAutoTagProviderCompanyConfiguration =
			tensorFlowImageAssetAutoTagProviderCompanyConfiguration;
	}

	public boolean isDownloadFailed() {
		if (isTensorFlowImageAssetAutoTagProviderEnabled() &&
			_tensorFlowDownloadHelper.isDownloadFailed()) {

			return true;
		}

		return false;
	}

	public boolean isDownloaded() throws PortalException {
		return _tensorFlowDownloadHelper.isDownloaded();
	}

	public boolean isTensorFlowImageAssetAutoTagProviderEnabled() {
		if ((_tensorFlowImageAssetAutoTagProviderCompanyConfiguration !=
				null) &&
			_tensorFlowImageAssetAutoTagProviderCompanyConfiguration.
				enabled()) {

			return true;
		}

		return false;
	}

	private final TensorFlowDownloadHelper _tensorFlowDownloadHelper;
	private final TensorFlowImageAssetAutoTagProviderCompanyConfiguration
		_tensorFlowImageAssetAutoTagProviderCompanyConfiguration;

}