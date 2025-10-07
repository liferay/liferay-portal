/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.util;

import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.exception.ImageResolutionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.awt.image.RenderedImage;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * @author Adolfo Pérez
 */
public class RenderedImageUtil {

	public static byte[] getRenderedImageContentStream(
		RenderedImage renderedImage, String mimeType) {

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			ImageToolUtil.write(
				renderedImage, mimeType, unsyncByteArrayOutputStream);

			return unsyncByteArrayOutputStream.toByteArray();
		}
		catch (IOException ioException) {
			throw new AMRuntimeException.IOException(ioException);
		}
	}

	public static RenderedImage readImage(InputStream inputStream)
		throws IOException, PortalException {

		ImageInputStream imageInputStream = ImageIO.createImageInputStream(
			inputStream);

		Iterator<ImageReader> iterator = ImageIO.getImageReaders(
			imageInputStream);

		while (iterator.hasNext()) {
			ImageReader imageReader = null;

			try {
				imageReader = iterator.next();

				imageReader.setInput(imageInputStream, false, true);

				int height = imageReader.getHeight(0);
				int width = imageReader.getWidth(0);

				if (((PropsValues.IMAGE_TOOL_IMAGE_MAX_HEIGHT > 0) &&
					 (height > PropsValues.IMAGE_TOOL_IMAGE_MAX_HEIGHT)) ||
					((PropsValues.IMAGE_TOOL_IMAGE_MAX_WIDTH > 0) &&
					 (width > PropsValues.IMAGE_TOOL_IMAGE_MAX_WIDTH))) {

					throw new ImageResolutionException(
						StringBundler.concat(
							"Image's dimensions of ", height, " px high and ",
							width, " px wide exceed max dimensions of ",
							PropsValues.IMAGE_TOOL_IMAGE_MAX_HEIGHT,
							" px high and ",
							PropsValues.IMAGE_TOOL_IMAGE_MAX_WIDTH,
							" px wide"));
				}

				return imageReader.read(0);
			}
			catch (ImageResolutionException imageResolutionException) {
				throw imageResolutionException;
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
			finally {
				if (imageReader != null) {
					imageReader.dispose();
				}

				StreamUtil.cleanUp(imageInputStream, inputStream);
			}
		}

		throw new IOException("Unsupported image type");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RenderedImageUtil.class);

}