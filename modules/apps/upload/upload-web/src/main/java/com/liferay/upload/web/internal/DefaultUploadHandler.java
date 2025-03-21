/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Adolfo Pérez
 * @author Roberto Díaz
 * @author Alejandro Tardín
 */
@Component(service = UploadHandler.class)
public class DefaultUploadHandler implements UploadHandler {

	@Override
	public void upload(
			UploadFileEntryHandler uploadFileEntryHandler,
			UploadResponseHandler uploadResponseHandler,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

		try {
			JSONPortletResponseUtil.writeJSON(
				portletRequest, portletResponse,
				_getResponseJSONObject(
					uploadFileEntryHandler, uploadResponseHandler,
					portletRequest));
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private JSONObject _getResponseJSONObject(
			UploadFileEntryHandler uploadFileEntryHandler,
			UploadResponseHandler uploadResponseHandler,
			PortletRequest portletRequest)
		throws IOException, PortalException {

		try {
			UploadPortletRequest uploadPortletRequest =
				_getUploadPortletRequest(portletRequest);

			FileEntry fileEntry = uploadFileEntryHandler.upload(
				uploadPortletRequest);

			return uploadResponseHandler.onSuccess(
				uploadPortletRequest, fileEntry);
		}
		catch (PortalException portalException) {
			return uploadResponseHandler.onFailure(
				portletRequest, portalException);
		}
	}

	private UploadPortletRequest _getUploadPortletRequest(
			PortletRequest portletRequest)
		throws PortalException {

		UploadException uploadException =
			(UploadException)portletRequest.getAttribute(
				WebKeys.UPLOAD_EXCEPTION);

		if (uploadException != null) {
			Throwable throwable = uploadException.getCause();

			if (uploadException.isExceededFileSizeLimit()) {
				throw new FileSizeException(throwable);
			}

			if (uploadException.isExceededLiferayFileItemSizeLimit()) {
				throw new LiferayFileItemException(throwable);
			}

			if (uploadException.isExceededUploadRequestSizeLimit()) {
				throw new UploadRequestSizeException(throwable);
			}

			throw new PortalException(throwable);
		}

		return _portal.getUploadPortletRequest(portletRequest);
	}

	@Reference
	private Portal _portal;

}