/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.portlet.action;

import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.product.asset.categories.web.internal.constants.CommerceProductAssetCategoriesPortletKeys;
import com.liferay.commerce.product.configuration.AttachmentsConfiguration;
import com.liferay.commerce.product.exception.CPAttachmentFileEntryNameException;
import com.liferay.commerce.product.exception.CPAttachmentFileEntrySizeException;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UniqueFileNameProvider;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	configurationPid = "com.liferay.commerce.product.configuration.AttachmentsConfiguration",
	property = {
		"jakarta.portlet.name=" + CommerceProductAssetCategoriesPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/commerce_product_asset_categories/upload_temp_asset_category_attachment"
	},
	service = MVCActionCommand.class
)
public class UploadTempAssetCategoryAttachmentMVCActionCommand
	extends BaseMVCActionCommand {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_attachmentsConfiguration = ConfigurableUtil.createConfigurable(
			AttachmentsConfiguration.class, properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_tempAssetCategoryAttachmentsUploadFileEntryHandler,
			_assetCategoryAttachmentsUploadResponseHandler, actionRequest,
			actionResponse);
	}

	@Reference
	protected AssetCategoryService assetCategoryService;

	private static final Log _log = LogFactoryUtil.getLog(
		UploadTempAssetCategoryAttachmentMVCActionCommand.class);

	private final AssetCategoryAttachmentsUploadResponseHandler
		_assetCategoryAttachmentsUploadResponseHandler =
			new AssetCategoryAttachmentsUploadResponseHandler();
	private volatile AttachmentsConfiguration _attachmentsConfiguration;

	@Reference
	private File _file;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	private final TempAssetCategoryAttachmentsUploadFileEntryHandler
		_tempAssetCategoryAttachmentsUploadFileEntryHandler =
			new TempAssetCategoryAttachmentsUploadFileEntryHandler();

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class AssetCategoryAttachmentsUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			JSONObject jsonObject =
				_itemSelectorUploadResponseHandler.onFailure(
					portletRequest, portalException);

			if (portalException instanceof CPAttachmentFileEntryNameException ||
				portalException instanceof CPAttachmentFileEntrySizeException) {

				String errorMessage = StringPool.BLANK;
				int errorType = 0;

				if (portalException instanceof
						CPAttachmentFileEntryNameException) {

					errorType =
						ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION;

					errorMessage = StringUtil.merge(
						_attachmentsConfiguration.imageExtensions());
				}
				else if (portalException instanceof
							CPAttachmentFileEntrySizeException) {

					errorType = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;
				}

				jsonObject.put(
					"error",
					JSONUtil.put(
						"errorType", errorType
					).put(
						"message", errorMessage
					));
			}
			else {
				throw portalException;
			}

			return jsonObject;
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			return _itemSelectorUploadResponseHandler.onSuccess(
				uploadPortletRequest, fileEntry);
		}

	}

	private class TempAssetCategoryAttachmentsUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			String fileName = uploadPortletRequest.getFileName(_parameterName);

			_validateFile(
				fileName, uploadPortletRequest.getSize(_parameterName));

			String contentType = uploadPortletRequest.getContentType(
				_parameterName);

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					_parameterName)) {

				return _addFileEntry(
					fileName, contentType, inputStream, themeDisplay);
			}
		}

		private FileEntry _addFileEntry(
				String fileName, String contentType, InputStream inputStream,
				ThemeDisplay themeDisplay)
			throws PortalException {

			String uniqueFileName = _uniqueFileNameProvider.provide(
				fileName, curFileName -> _exists(themeDisplay, curFileName));

			return TempFileEntryUtil.addTempFileEntry(
				themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
				_tempFolderName, uniqueFileName, inputStream, contentType);
		}

		private boolean _exists(ThemeDisplay themeDisplay, String curFileName) {
			try {
				FileEntry fileEntry = TempFileEntryUtil.getTempFileEntry(
					themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
					_tempFolderName, curFileName);

				if (fileEntry != null) {
					return true;
				}

				return false;
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				return false;
			}
		}

		private void _validateFile(String fileName, long size)
			throws PortalException {

			if ((_attachmentsConfiguration.imageMaxSize() > 0) &&
				(size > _attachmentsConfiguration.imageMaxSize())) {

				throw new CPAttachmentFileEntrySizeException();
			}

			String extension = _file.getExtension(fileName);

			String[] imageExtensions =
				_attachmentsConfiguration.imageExtensions();

			for (String imageExtension : imageExtensions) {
				if (StringPool.STAR.equals(imageExtension) ||
					imageExtension.equals(StringPool.PERIOD + extension)) {

					return;
				}
			}

			throw new CPAttachmentFileEntryNameException(
				"Invalid image for file name " + fileName);
		}

		private final String _parameterName = "imageSelectorFileName";
		private final String _tempFolderName =
			UploadTempAssetCategoryAttachmentMVCActionCommand.
				TempAssetCategoryAttachmentsUploadFileEntryHandler.class.
					getName();

	}

}