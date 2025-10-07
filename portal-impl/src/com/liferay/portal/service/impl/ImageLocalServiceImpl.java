/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.exception.ImageTypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.service.base.ImageLocalServiceBaseImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 * @author Shuyang Zhou
 */
public class ImageLocalServiceImpl extends ImageLocalServiceBaseImpl {

	@Override
	public Image deleteImage(long imageId) throws PortalException {
		if (imageId <= 0) {
			return null;
		}

		Image image = getImage(imageId);

		if (image == null) {
			return null;
		}

		imagePersistence.remove(image);

		Store store = _storeSnapshot.get();

		store.deleteFile(
			image.getCompanyId(), _REPOSITORY_ID,
			_getFileName(image.getImageId(), image.getType()),
			Store.VERSION_DEFAULT);

		return image;
	}

	@Override
	public Image getCompanyLogo(long imageId) {
		Image image = getImage(imageId);

		if (image != null) {
			return image;
		}

		return ImageToolUtil.getDefaultCompanyLogo();
	}

	@Override
	public Image getImage(long imageId) {
		try {
			if (imageId <= 0) {
				return null;
			}

			return imagePersistence.fetchByPrimaryKey(imageId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to get image ", imageId, ": ",
						exception.getMessage()));
			}

			return null;
		}
	}

	@Override
	public InputStream getImageInputStream(
			long companyId, long imageId, String type)
		throws PortalException {

		Store store = _storeSnapshot.get();

		String fileName = _getFileName(imageId, type);

		if (store.hasFile(
				companyId, _REPOSITORY_ID, fileName, Store.VERSION_DEFAULT)) {

			return store.getFileAsStream(
				companyId, _REPOSITORY_ID, fileName, StringPool.BLANK);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Get image " + imageId + " from the default company");
		}

		return store.getFileAsStream(
			0, _REPOSITORY_ID, fileName, StringPool.BLANK);
	}

	@Override
	public Image getImageOrDefault(long imageId) {
		Image image = getImage(imageId);

		if (image != null) {
			return image;
		}

		return ImageToolUtil.getDefaultSpacer();
	}

	@Override
	public List<Image> getImages() {
		return imagePersistence.findAll();
	}

	@Override
	public List<Image> getImagesBySize(int size) {
		return imagePersistence.findByLtSize(size);
	}

	@Override
	public Image moveImage(long imageId, byte[] bytes) throws PortalException {
		Image image = updateImage(
			_getImageCompanyId(imageId), counterLocalService.increment(),
			bytes);

		deleteImage(imageId);

		return image;
	}

	@Override
	public Image updateImage(long companyId, long imageId, byte[] bytes)
		throws PortalException {

		try {
			Image image = ImageToolUtil.getImage(bytes);

			return updateImage(
				companyId, imageId, image.getTextObj(), image.getType(),
				image.getHeight(), image.getWidth(), image.getSize());
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public Image updateImage(
			long companyId, long imageId, byte[] bytes, String type, int height,
			int width, int size)
		throws PortalException {

		if ((companyId == CompanyConstants.SYSTEM) && _log.isWarnEnabled()) {
			_log.warn("Associating image " + imageId + " to a system company");
		}

		validate(type);

		Image image = imagePersistence.fetchByPrimaryKey(imageId);

		if (image == null) {
			image = imagePersistence.create(imageId);

			image.setCompanyId(companyId);
		}

		image.setModifiedDate(new Date());
		image.setType(type);
		image.setHeight(height);
		image.setWidth(width);
		image.setSize(size);

		String fileName = _getFileName(image.getImageId(), image.getType());

		DLValidatorUtil.validateFileSize(
			GroupThreadLocal.getGroupId(), fileName,
			MimeTypesUtil.getContentType(fileName), bytes);

		Store store = _storeSnapshot.get();

		if (store.hasFile(
				image.getCompanyId(), _REPOSITORY_ID, fileName,
				Store.VERSION_DEFAULT)) {

			store.deleteFile(
				image.getCompanyId(), _REPOSITORY_ID, fileName,
				Store.VERSION_DEFAULT);
		}

		try (InputStream inputStream = new UnsyncByteArrayInputStream(bytes)) {
			store.addFile(
				image.getCompanyId(), _REPOSITORY_ID, fileName,
				Store.VERSION_DEFAULT, inputStream);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}

		image = imagePersistence.update(image);

		WebServerServletTokenUtil.resetToken(imageId);

		return image;
	}

	@Override
	public Image updateImage(long companyId, long imageId, File file)
		throws PortalException {

		try {
			Image image = ImageToolUtil.getImage(file);

			return updateImage(
				companyId, imageId, image.getTextObj(), image.getType(),
				image.getHeight(), image.getWidth(), image.getSize());
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public Image updateImage(
			long companyId, long imageId, InputStream inputStream)
		throws PortalException {

		try {
			Image image = ImageToolUtil.getImage(inputStream);

			return updateImage(
				companyId, imageId, image.getTextObj(), image.getType(),
				image.getHeight(), image.getWidth(), image.getSize());
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public Image updateImage(
			long companyId, long imageId, InputStream inputStream,
			boolean cleanUpStream)
		throws PortalException {

		try {
			Image image = ImageToolUtil.getImage(inputStream, cleanUpStream);

			return updateImage(
				companyId, imageId, image.getTextObj(), image.getType(),
				image.getHeight(), image.getWidth(), image.getSize());
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	protected void validate(String type) throws PortalException {
		if ((type == null) || type.contains(StringPool.BACK_SLASH) ||
			type.contains(StringPool.COLON) ||
			type.contains(StringPool.GREATER_THAN) ||
			type.contains(StringPool.LESS_THAN) ||
			type.contains(StringPool.PERCENT) ||
			type.contains(StringPool.PERIOD) ||
			type.contains(StringPool.PIPE) ||
			type.contains(StringPool.QUESTION) ||
			type.contains(StringPool.QUOTE) ||
			type.contains(StringPool.SLASH) ||
			type.contains(StringPool.SPACE) || type.contains(StringPool.STAR)) {

			throw new ImageTypeException();
		}
	}

	private String _getFileName(long imageId, String type) {
		return imageId + StringPool.PERIOD + type;
	}

	private long _getImageCompanyId(long imageId) {
		Image image = getImage(imageId);

		if (image == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Image " + imageId + " is associated to a system company");
			}

			return CompanyConstants.SYSTEM;
		}

		return image.getCompanyId();
	}

	private static final long _REPOSITORY_ID = 0;

	private static final Log _log = LogFactoryUtil.getLog(
		ImageLocalServiceImpl.class);

	private static final Snapshot<Store> _storeSnapshot = new Snapshot<>(
		ImageLocalServiceImpl.class, Store.class, "(default=true)");

}