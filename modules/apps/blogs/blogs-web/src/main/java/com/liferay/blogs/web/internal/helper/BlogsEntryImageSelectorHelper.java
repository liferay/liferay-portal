/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.helper;

import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.util.FileUtil;

/**
 * @author Roberto Díaz
 */
public class BlogsEntryImageSelectorHelper {

	public BlogsEntryImageSelectorHelper(
		long oldSmallImageId, long imageFileEntryId, long oldImageFileEntryId,
		String imageCropRegion, String imageURL, String oldImageURL) {

		_oldSmallImageId = oldSmallImageId;
		_imageFileEntryId = imageFileEntryId;
		_oldImageFileEntryId = oldImageFileEntryId;
		_imageCropRegion = imageCropRegion;
		_imageURL = imageURL;
		_oldImageURL = oldImageURL;
	}

	public ImageSelector getImageSelector() throws Exception {
		if ((_oldSmallImageId != 0) ||
			(_imageFileEntryId != _oldImageFileEntryId)) {

			if (_imageFileEntryId != 0) {
				FileEntry fileEntry = DLAppServiceUtil.getFileEntry(
					_imageFileEntryId);

				_fileEntryTempFile = fileEntry.isRepositoryCapabilityProvided(
					TemporaryFileEntriesCapability.class);

				return new ImageSelector(
					FileUtil.getBytes(fileEntry.getContentStream()),
					fileEntry.getFileName(), fileEntry.getMimeType(),
					_imageCropRegion);
			}

			return new ImageSelector();
		}
		else if (!_imageURL.equals(_oldImageURL)) {
			return new ImageSelector(_imageURL);
		}

		return null;
	}

	public boolean isFileEntryTempFile() {
		if (_fileEntryTempFile == null) {
			if ((_imageFileEntryId == 0) ||
				(_imageFileEntryId == _oldImageFileEntryId)) {

				_fileEntryTempFile = false;
			}
			else {
				try {
					FileEntry fileEntry =
						PortletFileRepositoryUtil.getPortletFileEntry(
							_imageFileEntryId);

					_fileEntryTempFile =
						fileEntry.isRepositoryCapabilityProvided(
							TemporaryFileEntriesCapability.class);
				}
				catch (PortalException portalException) {

					// LPS-52675

					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}

					return false;
				}
			}
		}

		return _fileEntryTempFile;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryImageSelectorHelper.class);

	private Boolean _fileEntryTempFile;
	private final String _imageCropRegion;
	private final long _imageFileEntryId;
	private final String _imageURL;
	private final long _oldImageFileEntryId;
	private final String _oldImageURL;
	private final long _oldSmallImageId;

}