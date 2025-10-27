/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.internal;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.change.tracking.store.service.CTSContentLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class CTStore implements Store {

	public CTStore(
		CTEntryLocalService ctEntryLocalService,
		CTSContentLocalService ctsContentLocalService, Store store,
		String storeType) {

		_ctEntryLocalService = ctEntryLocalService;
		_ctsContentLocalService = ctsContentLocalService;
		_store = store;
		_storeType = storeType;
	}

	@Override
	public void addFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel, InputStream inputStream)
		throws PortalException {

		if (CTCollectionThreadLocal.isProductionMode()) {
			_store.addFile(
				companyId, repositoryId, fileName, versionLabel, inputStream);
		}
		else {
			_ensureCTSContentIsLoaded(
				companyId, repositoryId, fileName, versionLabel);

			_ctsContentLocalService.addCTSContent(
				companyId, repositoryId, fileName, versionLabel, _storeType,
				inputStream);
		}
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		if (CTCollectionThreadLocal.isProductionMode()) {
			_store.deleteDirectory(companyId, repositoryId, dirName);
		}
		else {
			for (String fileName :
					_store.getFileNames(companyId, repositoryId, dirName)) {

				for (String versionLabel :
						_store.getFileVersions(
							companyId, repositoryId, fileName)) {

					if (!_isCTSContentLoaded(
							companyId, repositoryId, fileName, versionLabel)) {

						_loadCTSContent(
							companyId, repositoryId, fileName, versionLabel);
					}
				}
			}
		}

		_ctsContentLocalService.deleteCTSContentsByDirectory(
			companyId, repositoryId, dirName, _storeType);
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		if (CTCollectionThreadLocal.isProductionMode()) {
			_store.deleteFile(companyId, repositoryId, fileName, versionLabel);
		}
		else {
			_ensureCTSContentIsLoaded(
				companyId, repositoryId, fileName, versionLabel);

			_ctsContentLocalService.deleteCTSContent(
				companyId, repositoryId, fileName, versionLabel, _storeType);
		}
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		if (!CTCollectionThreadLocal.isProductionMode() &&
			_isCTSContentLoaded(
				companyId, repositoryId, fileName, versionLabel)) {

			CTSContent ctsContent = _ctsContentLocalService.getCTSContent(
				companyId, repositoryId, fileName, versionLabel, _storeType);

			try {
				return ctsContent.getData(
				).getBinaryStream();
			}
			catch (Exception exception) {
				throw new PortalException(exception);
			}
		}

		return _store.getFileAsStream(
			companyId, repositoryId, fileName, versionLabel);
	}

	@Override
	public String[] getFileNames(
		long companyId, long repositoryId, String dirName) {

		String[] fileNames = _store.getFileNames(
			companyId, repositoryId, dirName);

		if (CTCollectionThreadLocal.isProductionMode()) {
			return fileNames;
		}

		Set<Long> deletedCTSContentIds = _getDeletedCTSContentIds(
			CTCollectionThreadLocal.getCTCollectionId());

		if (deletedCTSContentIds == null) {
			Set<String> mergedFileNames = new HashSet<>(
				Arrays.asList(fileNames));

			for (CTSContent ctsContent :
					_ctsContentLocalService.getCTSContentsByDirectory(
						companyId, repositoryId, dirName, _storeType)) {

				mergedFileNames.add(ctsContent.getPath());
			}

			return _toSortedArray(mergedFileNames, null);
		}

		Map<String, Set<String>> fileVersionsMap = new HashMap<>();

		for (String fileName : fileNames) {
			fileVersionsMap.put(
				fileName,
				new HashSet<>(
					Arrays.asList(
						_store.getFileVersions(
							companyId, repositoryId, fileName))));
		}

		for (CTSContent ctsContent :
				_ctsContentLocalService.getCTSContentsByDirectory(
					companyId, repositoryId, dirName, _storeType)) {

			fileVersionsMap.compute(
				ctsContent.getPath(),
				(fileName, fileVersions) -> {
					if (fileVersions == null) {
						fileVersions = new HashSet<>();
					}

					fileVersions.add(ctsContent.getVersion());

					return fileVersions;
				});
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			for (CTSContent ctsContent :
					_ctsContentLocalService.getCTSContentsByDirectory(
						companyId, repositoryId, dirName, _storeType)) {

				if (deletedCTSContentIds.contains(
						ctsContent.getCtsContentId())) {

					fileVersionsMap.compute(
						ctsContent.getPath(),
						(fileName, fileVersions) -> {
							fileVersions.remove(ctsContent.getVersion());

							if (fileVersions.isEmpty()) {
								return null;
							}

							return fileVersions;
						});
				}
			}
		}

		return _toSortedArray(fileVersionsMap.keySet(), null);
	}

	@Override
	public long getFileSize(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		if (!CTCollectionThreadLocal.isProductionMode() &&
			_isCTSContentLoaded(
				companyId, repositoryId, fileName, versionLabel)) {

			CTSContent ctsContent = _ctsContentLocalService.getCTSContent(
				companyId, repositoryId, fileName, versionLabel, _storeType);

			return ctsContent.getSize();
		}

		return _store.getFileSize(
			companyId, repositoryId, fileName, versionLabel);
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		String[] versions = _store.getFileVersions(
			companyId, repositoryId, fileName);

		if (CTCollectionThreadLocal.isProductionMode()) {
			return versions;
		}

		Set<String> mergedVersions = new HashSet<>(Arrays.asList(versions));

		for (CTSContent ctsContent :
				_ctsContentLocalService.getCTSContents(
					companyId, repositoryId, fileName, _storeType)) {

			mergedVersions.add(ctsContent.getVersion());
		}

		Set<Long> deletedCTSContentIds = _getDeletedCTSContentIds(
			CTCollectionThreadLocal.getCTCollectionId());

		if (deletedCTSContentIds != null) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				for (CTSContent ctsContent :
						_ctsContentLocalService.getCTSContents(
							companyId, repositoryId, fileName, _storeType)) {

					if (deletedCTSContentIds.contains(
							ctsContent.getCtsContentId())) {

						mergedVersions.remove(ctsContent.getVersion());
					}
				}
			}
		}

		return _toSortedArray(mergedVersions, DLUtil::compareVersions);
	}

	@Override
	public boolean hasFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		if (!CTCollectionThreadLocal.isProductionMode()) {
			if (_ctsContentLocalService.hasCTSContent(
					companyId, repositoryId, fileName, versionLabel,
					_storeType)) {

				return true;
			}

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				if (_ctsContentLocalService.hasCTSContent(
						companyId, repositoryId, fileName, versionLabel,
						_storeType)) {

					return false;
				}
			}
		}

		return _store.hasFile(companyId, repositoryId, fileName, versionLabel);
	}

	private void _ensureCTSContentIsLoaded(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		if (_isCTSContentLoaded(
				companyId, repositoryId, fileName, versionLabel) ||
			!_store.hasFile(companyId, repositoryId, fileName, versionLabel)) {

			return;
		}

		_loadCTSContent(companyId, repositoryId, fileName, versionLabel);
	}

	private Set<Long> _getDeletedCTSContentIds(long ctCollectionId) {
		Set<Long> deletedCTSContentIds = null;

		for (CTEntry ctEntry :
				_ctEntryLocalService.getCTEntries(
					ctCollectionId,
					PortalUtil.getClassNameId(CTSContent.class.getName()))) {

			if (ctEntry.getChangeType() ==
					CTConstants.CT_CHANGE_TYPE_DELETION) {

				if (deletedCTSContentIds == null) {
					deletedCTSContentIds = new HashSet<>();
				}

				deletedCTSContentIds.add(ctEntry.getModelClassPK());
			}
		}

		return deletedCTSContentIds;
	}

	private boolean _isCTSContentLoaded(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		if (_ctsContentLocalService.hasCTSContent(
				companyId, repositoryId, fileName, versionLabel, _storeType)) {

			return true;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			return _ctsContentLocalService.hasCTSContent(
				companyId, repositoryId, fileName, versionLabel, _storeType);
		}
	}

	private void _loadCTSContent(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		try (InputStream inputStream = _store.getFileAsStream(
				companyId, repositoryId, fileName, versionLabel);
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			_ctsContentLocalService.addCTSContent(
				companyId, repositoryId, fileName, versionLabel, _storeType,
				inputStream);
		}
		catch (Exception exception) {
			throw new SystemException(
				"Unable to reload change tracking store content for deletion",
				exception);
		}
	}

	private String[] _toSortedArray(
		Collection<String> collection, Comparator<String> comparator) {

		String[] stringArray = collection.toArray(new String[0]);

		Arrays.sort(stringArray, comparator);

		return stringArray;
	}

	private final CTEntryLocalService _ctEntryLocalService;
	private final CTSContentLocalService _ctsContentLocalService;
	private final Store _store;
	private final String _storeType;

}