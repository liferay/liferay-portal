/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.storage.helper;

import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = SitemapStorageHelper.class)
public class SitemapStorageHelperImpl implements SitemapStorageHelper {

	@Override
	public void deleteSitemap(
			long companyId, long groupId, String assetTypeKey, int page)
		throws PortalException {

		_dlStore.deleteFile(
			companyId, CompanyConstants.SYSTEM,
			_getSitemapFileName(groupId, assetTypeKey, page));
	}

	@Override
	public void deleteSitemaps(long companyId) throws PortalException {
		_dlStore.deleteDirectory(
			companyId, CompanyConstants.SYSTEM, _getDirName());

		String lastRegenerateSitemapDateFileName =
			_getLastRegenerateSitemapDateFileName();

		if (_dlStore.hasFile(
				companyId, CompanyConstants.SYSTEM,
				lastRegenerateSitemapDateFileName, Store.VERSION_DEFAULT)) {

			_dlStore.deleteFile(
				companyId, CompanyConstants.SYSTEM,
				lastRegenerateSitemapDateFileName);
		}
	}

	@Override
	public void deleteSitemaps(long companyId, long groupId)
		throws PortalException {

		_dlStore.deleteDirectory(
			companyId, CompanyConstants.SYSTEM, _getDirName(groupId));
	}

	@Override
	public Date getLastRegenerateSitemapDate(long companyId)
		throws PortalException {

		String lastRegenerateSitemapDateFileName =
			_getLastRegenerateSitemapDateFileName();

		if (!_dlStore.hasFile(
				companyId, CompanyConstants.SYSTEM,
				lastRegenerateSitemapDateFileName, Store.VERSION_DEFAULT)) {

			return null;
		}

		try (InputStream inputStream = _dlStore.getFileAsStream(
				companyId, CompanyConstants.SYSTEM,
				lastRegenerateSitemapDateFileName, Store.VERSION_DEFAULT)) {

			return new Date(GetterUtil.getLong(StringUtil.read(inputStream)));
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	@Override
	public InputStream getSitemapInputStream(long companyId, long groupId)
		throws PortalException {

		return _dlStore.getFileAsStream(
			companyId, CompanyConstants.SYSTEM, _getSitemapFileName(groupId),
			Store.VERSION_DEFAULT);
	}

	@Override
	public InputStream getSitemapInputStream(
			long companyId, long groupId, String assetTypeKey, int page)
		throws PortalException {

		return _dlStore.getFileAsStream(
			companyId, CompanyConstants.SYSTEM,
			_getSitemapFileName(groupId, assetTypeKey, page),
			Store.VERSION_DEFAULT);
	}

	@Override
	public boolean hasSitemapFile(long companyId, long groupId)
		throws PortalException {

		return _dlStore.hasFile(
			companyId, CompanyConstants.SYSTEM, _getSitemapFileName(groupId),
			Store.VERSION_DEFAULT);
	}

	@Override
	public boolean hasSitemapFile(
			long companyId, long groupId, String assetTypeKey, int page)
		throws PortalException {

		return _dlStore.hasFile(
			companyId, CompanyConstants.SYSTEM,
			_getSitemapFileName(groupId, assetTypeKey, page),
			Store.VERSION_DEFAULT);
	}

	@Override
	public boolean hasSitemapFiles(long companyId) throws PortalException {
		return ArrayUtil.isNotEmpty(
			_dlStore.getFileNames(
				companyId, CompanyConstants.SYSTEM, _getDirName()));
	}

	@Override
	public void storeLastRegenerateSitemapDateFile(long companyId)
		throws PortalException {

		String lastRegenerateSitemapDateFileName =
			_getLastRegenerateSitemapDateFileName();

		if (_dlStore.hasFile(
				companyId, CompanyConstants.SYSTEM,
				lastRegenerateSitemapDateFileName, Store.VERSION_DEFAULT)) {

			_dlStore.deleteFile(
				companyId, CompanyConstants.SYSTEM,
				lastRegenerateSitemapDateFileName);
		}

		_storeSitemapFile(
			companyId, lastRegenerateSitemapDateFileName,
			String.valueOf(System.currentTimeMillis()));
	}

	@Override
	public void storeSitemapFile(long companyId, long groupId, String xml)
		throws PortalException {

		_storeSitemapFile(companyId, _getSitemapFileName(groupId), xml);
	}

	@Override
	public void storeSitemapFile(
			long companyId, long groupId, String assetTypeKey, int page,
			String xml)
		throws PortalException {

		_storeSitemapFile(
			companyId, _getSitemapFileName(groupId, assetTypeKey, page), xml);
	}

	private String _getDirName() {
		return "sitemaps";
	}

	private String _getDirName(long groupId) {
		return "sitemaps/" + groupId;
	}

	private String _getLastRegenerateSitemapDateFileName() {
		return "sitemap-metadata/last-regenerate-sitemap-date";
	}

	private String _getSitemapFileName(long groupId) {
		return StringBundler.concat("sitemaps/", groupId, "/sitemap-index.xml");
	}

	private String _getSitemapFileName(
		long groupId, String assetTypeKey, int page) {

		return StringBundler.concat(
			"sitemaps/", groupId, StringPool.SLASH, assetTypeKey,
			StringPool.SLASH, page, ".xml");
	}

	private void _storeSitemapFile(long companyId, String fileName, String xml)
		throws PortalException {

		byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);

		DLStoreRequest dlStoreRequest = DLStoreRequest.builder(
			companyId, CompanyConstants.SYSTEM, fileName
		).size(
			bytes.length
		).versionLabel(
			Store.VERSION_DEFAULT
		).build();

		_dlStore.addFile(dlStoreRequest, bytes);
	}

	@Reference
	private DLStore _dlStore;

}