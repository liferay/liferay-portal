/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.image.uploader.web.internal.portlet.action;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.image.uploader.web.internal.constants.ImageUploaderPortletKeys;
import com.liferay.image.uploader.web.internal.util.UploadImageUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.InputStream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ImageUploaderPortletKeys.IMAGE_UPLOADER,
		"mvc.command.name=/image_uploader/upload_image"
	},
	service = MVCResourceCommand.class
)
public class UploadImageMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long ctCollectionId = ParamUtil.getLong(
			resourceRequest, "ctCollectionId",
			CTCollectionThreadLocal.getCTCollectionId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

			if (cmd.equals(Constants.GET_TEMP)) {
				FileEntry tempFileEntry = UploadImageUtil.getTempImageFileEntry(
					resourceRequest);

				_serveTempImageFile(
					resourceResponse, tempFileEntry.getContentStream());
			}
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileEntryException);
			}
		}
		catch (Exception exception) {
			_log.error("Unable to serve resource", exception);
		}
	}

	private void _serveTempImageFile(
			MimeResponse mimeResponse, InputStream tempImageInputStream)
		throws Exception {

		ImageBag imageBag = ImageToolUtil.read(tempImageInputStream);

		byte[] bytes = ImageToolUtil.getBytes(
			imageBag.getRenderedImage(), imageBag.getType());

		mimeResponse.setContentType(
			MimeTypesUtil.getExtensionContentType(imageBag.getType()));

		PortletResponseUtil.write(mimeResponse, bytes);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadImageMVCResourceCommand.class);

}