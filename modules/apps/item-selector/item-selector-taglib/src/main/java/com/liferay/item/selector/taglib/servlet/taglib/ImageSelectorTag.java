/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.servlet.taglib;

import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.item.selector.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Sergio González
 * @author Roberto Díaz
 * @author Carlos Lancha
 */
public class ImageSelectorTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest = getRequest();

		if (_fileEntryId != 0) {
			try {
				FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
					_fileEntryId);

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				_imageURL = DLURLHelperUtil.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), themeDisplay,
					StringPool.BLANK);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to get HTML preview entry image URL", exception);
			}
		}

		if (Validator.isNotNull(_paramName)) {
			_imageCropRegion = ParamUtil.getString(
				httpServletRequest, _paramName + "CropRegion");
		}

		return super.doStartTag();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getImageCropDirection()}
	 */
	@Deprecated
	public String getDraggableImage() {
		return getImageCropDirection();
	}

	public long getFileEntryId() {
		return _fileEntryId;
	}

	public String getImageCropDirection() {
		return _imageCropDirection;
	}

	public String getItemSelectorEventName() {
		return _itemSelectorEventName;
	}

	public String getItemSelectorURL() {
		return _itemSelectorURL;
	}

	public long getMaxFileSize() {
		return _maxFileSize;
	}

	public String getParamName() {
		return _paramName;
	}

	public String getUploadURL() {
		return _uploadURL;
	}

	public String getValidExtensions() {
		return _validExtensions;
	}

	public boolean isDraggable() {
		return !_imageCropDirection.equals("none");
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #setImageCropDirection()}
	 */
	@Deprecated
	public void setDraggableImage(String draggableImage) {
		setImageCropDirection(draggableImage);
	}

	public void setFileEntryId(long fileEntryId) {
		_fileEntryId = fileEntryId;
	}

	public void setImageCropDirection(String imageCropDirection) {
		_imageCropDirection = imageCropDirection;
	}

	public void setItemSelectorEventName(String itemSelectorEventName) {
		_itemSelectorEventName = itemSelectorEventName;
	}

	public void setItemSelectorURL(String itemSelectorURL) {
		_itemSelectorURL = itemSelectorURL;
	}

	public void setMaxFileSize(long maxFileSize) {
		_maxFileSize = maxFileSize;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setParamName(String paramName) {
		_paramName = paramName;
	}

	public void setUploadURL(String uploadURL) {
		_uploadURL = uploadURL;
	}

	public void setValidExtensions(String validExtensions) {
		_validExtensions = validExtensions;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_fileEntryId = 0;
		_imageCropDirection = "none";
		_imageCropRegion = null;
		_imageURL = null;
		_itemSelectorEventName = null;
		_itemSelectorURL = null;
		_maxFileSize = 0;
		_paramName = "imageSelectorFileEntryId";
		_uploadURL = null;
		_validExtensions = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:draggable", isDraggable());
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:fileEntryId", _fileEntryId);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:imageCropDirection",
			_imageCropDirection);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:imageCropRegion", _imageCropRegion);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:imageURL", _imageURL);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:itemSelectorEventName",
			_itemSelectorEventName);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:itemSelectorURL", _itemSelectorURL);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:maxFileSize", _maxFileSize);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:paramName", _paramName);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:uploadURL", _uploadURL);
		httpServletRequest.setAttribute(
			"liferay-ui:image-selector:validExtensions", _validExtensions);
	}

	private static final String _PAGE = "/image_selector/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		ImageSelectorTag.class);

	private long _fileEntryId;
	private String _imageCropDirection = "none";
	private String _imageCropRegion;
	private String _imageURL;
	private String _itemSelectorEventName;
	private String _itemSelectorURL;
	private long _maxFileSize;
	private String _paramName = "imageSelectorFileEntryId";
	private String _uploadURL;
	private String _validExtensions;

}