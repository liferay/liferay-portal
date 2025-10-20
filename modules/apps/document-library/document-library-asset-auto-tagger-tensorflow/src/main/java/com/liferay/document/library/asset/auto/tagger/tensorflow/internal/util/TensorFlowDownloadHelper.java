/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.util;

import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderDownloadConfiguration;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.zip.ZipFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = TensorFlowDownloadHelper.class)
public class TensorFlowDownloadHelper {

	public static final String NATIVE_LIBRARY_FILE_NAME =
		"libtensorflow_jni-1.15.0.jar";

	public void download(
			TensorFlowImageAssetAutoTagProviderDownloadConfiguration
				tensorFlowImageAssetAutoTagProviderDownloadConfiguration)
		throws Exception {

		if (isDownloaded()) {
			return;
		}

		try {
			_downloadFailed = false;

			_downloadFile(
				_getModelFileName(),
				tensorFlowImageAssetAutoTagProviderDownloadConfiguration.
					modelDownloadURL(),
				tensorFlowImageAssetAutoTagProviderDownloadConfiguration.
					modelDownloadSHA1());

			_downloadFile(
				_getNativeLibraryFileName(),
				tensorFlowImageAssetAutoTagProviderDownloadConfiguration.
					nativeLibraryDownloadURL(),
				tensorFlowImageAssetAutoTagProviderDownloadConfiguration.
					nativeLibraryDownloadSHA1());
		}
		catch (Exception exception) {
			_downloadFailed = true;

			throw exception;
		}
	}

	public byte[] getGraphBytes() throws IOException, PortalException {
		return StreamUtil.toByteArray(
			_getModelFileInputStream("tensorflow_inception_graph.pb"));
	}

	public String[] getLabels() throws IOException, PortalException {
		return StringUtil.splitLines(
			StringUtil.read(
				_getModelFileInputStream(
					"imagenet_comp_graph_label_strings.txt")));
	}

	public InputStream getNativeLibraryInputStream() throws PortalException {
		return _store.getFileAsStream(
			_COMPANY_ID, CompanyConstants.SYSTEM, _getNativeLibraryFileName(),
			StringPool.BLANK);
	}

	public boolean isDownloaded() throws PortalException {
		if (_store.hasFile(
				_COMPANY_ID, CompanyConstants.SYSTEM, _getModelFileName(),
				Store.VERSION_DEFAULT) &&
			_store.hasFile(
				_COMPANY_ID, CompanyConstants.SYSTEM,
				_getNativeLibraryFileName(), Store.VERSION_DEFAULT)) {

			return true;
		}

		return false;
	}

	public boolean isDownloadFailed() {
		return _downloadFailed;
	}

	private void _downloadFile(String fileName, String url, String sha1)
		throws Exception {

		File tempFile = FileUtil.createTempFile();

		URLUtil.download(new URL(url), tempFile.toPath(), sha1);

		try (InputStream inputStream = new FileInputStream(tempFile)) {
			_store.addFile(
				_COMPANY_ID, CompanyConstants.SYSTEM, fileName,
				Store.VERSION_DEFAULT, inputStream);
		}
	}

	private String _getFileName(String fileName) {
		return "com.liferay.document.library.asset.auto.tagger.tensorflow/" +
			fileName;
	}

	private InputStream _getModelFileInputStream(String fileName)
		throws IOException, PortalException {

		return ZipFileUtil.openInputStream(
			FileUtil.createTempFile(
				_store.getFileAsStream(
					_COMPANY_ID, CompanyConstants.SYSTEM, _getModelFileName(),
					StringPool.BLANK)),
			fileName);
	}

	private String _getModelFileName() {
		return _getFileName("org.tensorflow.models.inception-5h.jar");
	}

	private String _getNativeLibraryFileName() {
		return _getFileName(NATIVE_LIBRARY_FILE_NAME);
	}

	private static final long _COMPANY_ID = 0;

	private static boolean _downloadFailed;

	@Reference(target = "(default=true)")
	private Store _store;

}