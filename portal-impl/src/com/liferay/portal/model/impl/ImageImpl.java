/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.InputStream;

/**
 * @author Brian Wing Shun Chan
 */
public class ImageImpl extends ImageBaseImpl {

	@Override
	public byte[] getTextObj() {
		if (_textObj != null) {
			return _textObj;
		}

		long imageId = getImageId();

		try {
			DLFileEntry dlFileEntry = null;

			if (PropsValues.WEB_SERVER_SERVLET_CHECK_IMAGE_GALLERY) {
				dlFileEntry =
					DLFileEntryLocalServiceUtil.fetchFileEntryByAnyImageId(
						imageId);
			}

			InputStream inputStream = null;

			try {
				if ((dlFileEntry != null) &&
					(dlFileEntry.getLargeImageId() == imageId)) {

					inputStream = DLStoreUtil.getFileAsStream(
						dlFileEntry.getCompanyId(),
						dlFileEntry.getDataRepositoryId(),
						dlFileEntry.getName(), StringPool.BLANK);
				}
				else {
					inputStream = ImageLocalServiceUtil.getImageInputStream(
						getCompanyId(), imageId, getType());
				}

				byte[] bytes = FileUtil.getBytes(inputStream);

				_textObj = bytes;
			}
			finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		catch (Exception exception) {
			_log.error("Unable to read image " + imageId, exception);
		}

		return _textObj;
	}

	@Override
	public void setTextObj(byte[] textObj) {
		_textObj = textObj;
	}

	private static final Log _log = LogFactoryUtil.getLog(ImageImpl.class);

	private byte[] _textObj;

}