/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.processor;

import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.DLProcessorHelper;
import com.liferay.document.library.kernel.processor.DLProcessorThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.xml.Element;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(service = DLProcessorHelper.class)
public class DLProcessorHelperImpl implements DLProcessorHelper {

	@Override
	public void cleanUp(FileEntry fileEntry) {
		if (!DLProcessorThreadLocal.isEnabled()) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileEntry.getCtCollectionId())) {

			for (DLProcessor dlProcessor : _serviceTrackerMap.values()) {
				if (!dlProcessor.isSupported(fileEntry.getMimeType())) {
					continue;
				}

				dlProcessor.cleanUp(fileEntry);
			}
		}
	}

	@Override
	public void cleanUp(FileVersion fileVersion) {
		if (!DLProcessorThreadLocal.isEnabled()) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileVersion.getCtCollectionId())) {

			for (DLProcessor dlProcessor : _serviceTrackerMap.values()) {
				if (!dlProcessor.isSupported(fileVersion)) {
					continue;
				}

				dlProcessor.cleanUp(fileVersion);
			}
		}
	}

	@Override
	public void exportGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			Element fileEntryElement)
		throws Exception {

		if ((fileEntry == null) || (fileEntry.getSize() == 0)) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileEntry.getCtCollectionId())) {

			FileVersion latestFileVersion = _getLatestFileVersion(
				fileEntry, true);

			if (latestFileVersion == null) {
				return;
			}

			for (DLProcessor dlProcessor : _serviceTrackerMap.values()) {
				if (!dlProcessor.isSupported(latestFileVersion)) {
					continue;
				}

				dlProcessor.exportGeneratedFiles(
					portletDataContext, fileEntry, fileEntryElement);
			}
		}
	}

	@Override
	public DLProcessor getDLProcessor(String dlProcessorType) {
		return _serviceTrackerMap.getService(dlProcessorType);
	}

	@Override
	public long getPreviewableProcessorMaxSize(long groupId) {
		return _dlFileEntryConfigurationProvider.
			getGroupPreviewableProcessorMaxSize(groupId);
	}

	@Override
	public void importGeneratedFiles(
			PortletDataContext portletDataContext, FileEntry fileEntry,
			FileEntry importedFileEntry, Element fileEntryElement)
		throws Exception {

		if ((importedFileEntry == null) || (importedFileEntry.getSize() == 0)) {
			return;
		}

		FileVersion fileVersion = importedFileEntry.getFileVersion();

		if (fileVersion == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileVersion.getCtCollectionId())) {

			for (DLProcessor dlProcessor : _serviceTrackerMap.values()) {
				if (!dlProcessor.isSupported(fileVersion)) {
					continue;
				}

				dlProcessor.importGeneratedFiles(
					portletDataContext, fileEntry, importedFileEntry,
					fileEntryElement);
			}
		}
	}

	@Override
	public boolean isPreviewableSize(FileVersion fileVersion) {
		long previewableProcessorMaxSize =
			_dlFileEntryConfigurationProvider.
				getGroupPreviewableProcessorMaxSize(fileVersion.getGroupId());

		if ((previewableProcessorMaxSize == 0) ||
			((previewableProcessorMaxSize > 0) &&
			 (fileVersion.getSize() > previewableProcessorMaxSize))) {

			return false;
		}

		return true;
	}

	@Override
	public void trigger(FileEntry fileEntry, FileVersion fileVersion) {
		trigger(fileEntry, fileVersion, false);
	}

	@Override
	public void trigger(
		FileEntry fileEntry, FileVersion fileVersion, boolean trusted) {

		if (!DLProcessorThreadLocal.isEnabled() || (fileEntry == null) ||
			(fileEntry.getSize() == 0)) {

			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileEntry.getCtCollectionId())) {

			FileVersion latestFileVersion = _getLatestFileVersion(
				fileEntry, trusted);

			if (latestFileVersion == null) {
				return;
			}

			for (DLProcessor dlProcessor : _serviceTrackerMap.values()) {
				if (!dlProcessor.isSupported(latestFileVersion)) {
					continue;
				}

				dlProcessor.trigger(fileVersion, latestFileVersion);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DLProcessor.class, "type");
	}

	@Deactivate
	protected void deactivate() throws Exception {
		_serviceTrackerMap.close();
	}

	private FileVersion _getLatestFileVersion(
		FileEntry fileEntry, boolean trusted) {

		try {
			return fileEntry.getLatestFileVersion(trusted);
		}
		catch (NoSuchFileEntryException | NoSuchFileVersionException
					exception) {

			if (_log.isInfoEnabled()) {
				_log.info(exception);
			}

			return null;
		}
		catch (Exception exception) {
			_log.error(exception);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLProcessorHelperImpl.class);

	private BundleContext _bundleContext;

	@Reference
	private DLFileEntryConfigurationProvider _dlFileEntryConfigurationProvider;

	private ServiceTrackerMap<String, DLProcessor> _serviceTrackerMap;

}