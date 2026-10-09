/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderProcessConfiguration;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.petra.process.GetLabelProbabilitiesProcessCallable;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.util.TensorFlowDownloadHelper;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.util.TensorFlowProcessHolder;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderProcessConfiguration",
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = AssetAutoTagProvider.class
)
public class TensorFlowImageAssetAutoTagProvider
	implements AssetAutoTagProvider<FileEntry> {

	@Override
	public Collection<String> getTagNames(FileEntry fileEntry) {
		try {
			TensorFlowImageAssetAutoTagProviderCompanyConfiguration
				tensorFlowImageAssetAutoTagProviderCompanyConfiguration =
					_configurationProvider.getCompanyConfiguration(
						TensorFlowImageAssetAutoTagProviderCompanyConfiguration.
							class,
						fileEntry.getCompanyId());

			if (tensorFlowImageAssetAutoTagProviderCompanyConfiguration.
					enabled() &&
				!_isTemporary(fileEntry) &&
				_tensorFlowDownloadHelper.isDownloaded()) {

				if (_labels == null) {
					_labels = _tensorFlowDownloadHelper.getLabels();
				}

				FileVersion fileVersion = fileEntry.getFileVersion();

				if (_isSupportedMimeType(fileVersion.getMimeType())) {
					return _label(
						FileUtil.getBytes(fileVersion.getContentStream(false)),
						fileVersion.getMimeType(),
						tensorFlowImageAssetAutoTagProviderCompanyConfiguration.
							confidenceThreshold());
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return Collections.emptyList();
	}

	@Activate
	protected void activate(
			BundleContext bundleContext, Map<String, Object> properties)
		throws Exception {

		modified(properties);

		_tensorFlowProcessHolder = new TensorFlowProcessHolder(
			bundleContext.getBundle(), _processExecutor,
			_tensorFlowDownloadHelper);
	}

	@Deactivate
	protected void deactivate() {
		_tensorFlowProcessHolder.destroy();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_tensorFlowImageAssetAutoTagProviderProcessConfiguration =
			ConfigurableUtil.createConfigurable(
				TensorFlowImageAssetAutoTagProviderProcessConfiguration.class,
				properties);
	}

	private boolean _isSupportedMimeType(String mimeType) {
		return _supportedMimeTypes.contains(mimeType);
	}

	private boolean _isTemporary(FileEntry fileEntry) {
		return fileEntry.isRepositoryCapabilityProvided(
			TemporaryFileEntriesCapability.class);
	}

	private List<String> _label(
		byte[] imageBytes, String mimeType, float confidenceThreshold) {

		int maximumNumberOfRelaunches =
			_tensorFlowImageAssetAutoTagProviderProcessConfiguration.
				maximumNumberOfRelaunches();
		long maximumNumberOfRelaunchesTimeout =
			_tensorFlowImageAssetAutoTagProviderProcessConfiguration.
				maximumNumberOfRelaunchesTimeout();

		float[] labelProbabilities = _tensorFlowProcessHolder.execute(
			new GetLabelProbabilitiesProcessCallable(imageBytes, mimeType),
			maximumNumberOfRelaunches, maximumNumberOfRelaunchesTimeout * 1000);

		List<String> labels = new ArrayList<>();

		for (int i = 0; i < labelProbabilities.length; i++) {
			if (!(labelProbabilities[i] >= confidenceThreshold) ||
				(i >= _labels.length)) {

				continue;
			}

			labels.add(_labels[i]);
		}

		return labels;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TensorFlowImageAssetAutoTagProvider.class);

	private static final Set<String> _supportedMimeTypes = new HashSet<>(
		Arrays.asList(
			ContentTypes.IMAGE_BMP, ContentTypes.IMAGE_JPEG,
			ContentTypes.IMAGE_PNG));

	@Reference
	private ConfigurationProvider _configurationProvider;

	private String[] _labels;

	@Reference
	private ProcessExecutor _processExecutor;

	@Reference
	private TensorFlowDownloadHelper _tensorFlowDownloadHelper;

	private volatile TensorFlowImageAssetAutoTagProviderProcessConfiguration
		_tensorFlowImageAssetAutoTagProviderProcessConfiguration;
	private TensorFlowProcessHolder _tensorFlowProcessHolder;

}