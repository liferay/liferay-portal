/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.image;

/**
 * @author Brian Wing Shun Chan
 */
public class ImageToolUtil_IW {
	public static ImageToolUtil_IW getInstance() {
		return _instance;
	}

	public java.util.concurrent.Future<java.awt.image.RenderedImage> convertCMYKtoRGB(
		byte[] bytes, java.lang.String type) {
		return ImageToolUtil.convertCMYKtoRGB(bytes, type);
	}

	public java.awt.image.BufferedImage convertImageType(
		java.awt.image.BufferedImage sourceImage, int type) {
		return ImageToolUtil.convertImageType(sourceImage, type);
	}

	public java.awt.image.RenderedImage crop(
		java.awt.image.RenderedImage renderedImage, int height, int width,
		int x, int y) {
		return ImageToolUtil.crop(renderedImage, height, width, x, y);
	}

	public void encodeGIF(java.awt.image.RenderedImage renderedImage,
		java.io.OutputStream outputStream) throws java.io.IOException {
		ImageToolUtil.encodeGIF(renderedImage, outputStream);
	}

	public void encodeWBMP(java.awt.image.RenderedImage renderedImage,
		java.io.OutputStream outputStream) throws java.io.IOException {
		ImageToolUtil.encodeWBMP(renderedImage, outputStream);
	}

	public java.awt.image.RenderedImage flipHorizontal(
		java.awt.image.RenderedImage renderedImage) {
		return ImageToolUtil.flipHorizontal(renderedImage);
	}

	public java.awt.image.RenderedImage flipVertical(
		java.awt.image.RenderedImage renderedImage) {
		return ImageToolUtil.flipVertical(renderedImage);
	}

	public java.awt.image.BufferedImage getBufferedImage(
		java.awt.image.RenderedImage renderedImage) {
		return ImageToolUtil.getBufferedImage(renderedImage);
	}

	public byte[] getBytes(java.awt.image.RenderedImage renderedImage,
		java.lang.String contentType) throws java.io.IOException {
		return ImageToolUtil.getBytes(renderedImage, contentType);
	}

	public com.liferay.portal.kernel.model.Image getDefaultCompanyGroupLogo() {
		return ImageToolUtil.getDefaultCompanyGroupLogo();
	}

	public com.liferay.portal.kernel.model.Image getDefaultCompanyLogo() {
		return ImageToolUtil.getDefaultCompanyLogo();
	}

	public com.liferay.portal.kernel.model.Image getDefaultLiferayLogo() {
		return ImageToolUtil.getDefaultLiferayLogo();
	}

	public com.liferay.portal.kernel.model.Image getDefaultOrganizationLogo() {
		return ImageToolUtil.getDefaultOrganizationLogo();
	}

	public com.liferay.portal.kernel.model.Image getDefaultSpacer() {
		return ImageToolUtil.getDefaultSpacer();
	}

	public com.liferay.portal.kernel.model.Image getDefaultUserFemalePortrait() {
		return ImageToolUtil.getDefaultUserFemalePortrait();
	}

	public com.liferay.portal.kernel.model.Image getDefaultUserMalePortrait() {
		return ImageToolUtil.getDefaultUserMalePortrait();
	}

	public com.liferay.portal.kernel.model.Image getDefaultUserPortrait() {
		return ImageToolUtil.getDefaultUserPortrait();
	}

	public com.liferay.portal.kernel.model.Image getImage(byte[] bytes)
		throws com.liferay.portal.kernel.exception.ImageResolutionException,
			java.io.IOException {
		return ImageToolUtil.getImage(bytes);
	}

	public com.liferay.portal.kernel.model.Image getImage(java.io.File file)
		throws com.liferay.portal.kernel.exception.ImageResolutionException,
			java.io.IOException {
		return ImageToolUtil.getImage(file);
	}

	public com.liferay.portal.kernel.model.Image getImage(
		java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.ImageResolutionException,
			java.io.IOException {
		return ImageToolUtil.getImage(inputStream);
	}

	public com.liferay.portal.kernel.model.Image getImage(
		java.io.InputStream inputStream, boolean cleanUpStream)
		throws com.liferay.portal.kernel.exception.ImageResolutionException,
			java.io.IOException {
		return ImageToolUtil.getImage(inputStream, cleanUpStream);
	}

	public boolean isNullOrDefaultSpacer(byte[] bytes) {
		return ImageToolUtil.isNullOrDefaultSpacer(bytes);
	}

	public com.liferay.portal.kernel.image.ImageBag read(byte[] bytes)
		throws com.liferay.portal.kernel.exception.ImageResolutionException,
			java.io.IOException {
		return ImageToolUtil.read(bytes);
	}

	public com.liferay.portal.kernel.image.ImageBag read(java.io.File file)
		throws com.liferay.portal.kernel.exception.ImageResolutionException,
			java.io.IOException {
		return ImageToolUtil.read(file);
	}

	public com.liferay.portal.kernel.image.ImageBag read(
		java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.ImageResolutionException,
			java.io.IOException {
		return ImageToolUtil.read(inputStream);
	}

	public java.awt.image.RenderedImage rotate(
		java.awt.image.RenderedImage renderedImage, int degrees) {
		return ImageToolUtil.rotate(renderedImage, degrees);
	}

	public java.awt.image.RenderedImage scale(
		java.awt.image.RenderedImage renderedImage, int width) {
		return ImageToolUtil.scale(renderedImage, width);
	}

	public java.awt.image.RenderedImage scale(
		java.awt.image.RenderedImage renderedImage, int maxHeight, int maxWidth) {
		return ImageToolUtil.scale(renderedImage, maxHeight, maxWidth);
	}

	public void write(java.awt.image.RenderedImage renderedImage,
		java.lang.String contentType, java.io.OutputStream outputStream)
		throws java.io.IOException {
		ImageToolUtil.write(renderedImage, contentType, outputStream);
	}

	private ImageToolUtil_IW() {
	}

	private static ImageToolUtil_IW _instance = new ImageToolUtil_IW();
}