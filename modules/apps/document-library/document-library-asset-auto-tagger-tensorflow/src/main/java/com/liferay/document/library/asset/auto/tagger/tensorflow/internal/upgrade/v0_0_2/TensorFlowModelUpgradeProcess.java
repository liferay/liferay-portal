/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.upgrade.v0_0_2;

import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderDownloadConfiguration;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.URLUtil;

import java.io.File;
import java.io.FileInputStream;

import java.net.URL;

import java.nio.file.Paths;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Alejandro Tardín
 */
public class TensorFlowModelUpgradeProcess extends UpgradeProcess {

	public TensorFlowModelUpgradeProcess(
		CompanyLocalService companyLocalService,
		ConfigurationAdmin configurationAdmin,
		ConfigurationProvider configurationProvider, Store store) {

		_companyLocalService = companyLocalService;
		_configurationAdmin = configurationAdmin;
		_configurationProvider = configurationProvider;
		_store = store;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				TensorFlowImageAssetAutoTagProviderCompanyConfiguration
					tensorFlowImageAssetAutoTagProviderCompanyConfiguration =
						_configurationProvider.getCompanyConfiguration(
							TensorFlowImageAssetAutoTagProviderCompanyConfiguration.class,
							companyId);

				if (tensorFlowImageAssetAutoTagProviderCompanyConfiguration.
						enabled()) {

					Configuration configuration =
						_configurationAdmin.getConfiguration(
							TensorFlowImageAssetAutoTagProviderDownloadConfiguration.class.
								getName(),
							StringPool.QUESTION);

					Dictionary<String, Object> dictionary =
						configuration.getProperties();

					if (dictionary == null) {
						dictionary = new HashMapDictionary<>();
					}

					TensorFlowImageAssetAutoTagProviderDownloadConfiguration
						tensorFlowImageAssetAutoTagProviderDownloadConfiguration =
							ConfigurableUtil.createConfigurable(
								TensorFlowImageAssetAutoTagProviderDownloadConfiguration.class,
								dictionary);

					_downloadFile(
						"org.tensorflow.models.inception-5h.jar",
						tensorFlowImageAssetAutoTagProviderDownloadConfiguration.
							modelDownloadURL(),
						tensorFlowImageAssetAutoTagProviderDownloadConfiguration.
							modelDownloadSHA1());
					_downloadFile(
						"libtensorflow_jni-1.15.0.jar",
						tensorFlowImageAssetAutoTagProviderDownloadConfiguration.
							nativeLibraryDownloadURL(),
						tensorFlowImageAssetAutoTagProviderDownloadConfiguration.
							nativeLibraryDownloadSHA1());

					return;
				}
			});
	}

	private void _downloadFile(String fileName, String url, String sha1)
		throws Exception {

		File tempFile = FileUtil.createTempFile();

		URLUtil.download(new URL(url), tempFile.toPath(), sha1);

		_store.addFile(
			0, CompanyConstants.SYSTEM,
			String.valueOf(
				Paths.get(
					"com.liferay.document.library.asset.auto.tagger.tensorflow",
					fileName)),
			Store.VERSION_DEFAULT, new FileInputStream(tempFile));
	}

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;
	private final Store _store;

}