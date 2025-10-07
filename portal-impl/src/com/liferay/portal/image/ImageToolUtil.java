/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.image;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ImageResolutionException;
import com.liferay.portal.kernel.image.CMYKImageTool;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.ImageConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.ImageImpl;
import com.liferay.portal.module.framework.ModuleFrameworkUtil;
import com.liferay.portal.util.FileImpl;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

/**
 * The Image utility class.
 *
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class ImageToolUtil {

	/**
	 * Returns the CMYK image converted to RGB using ImageMagick. This must be
	 * run against the original <code>byte[]</code> and not one extracted from a
	 * {@link RenderedImage}. The latter may potentially have been already been
	 * read incorrectly.
	 *
	 * @param  bytes the image to convert
	 * @param  type the image type (e.g., "gif", "jpg", etc.)
	 * @return the asynchronous process converting the image or <code>null
	 *         </code> if ImageMagick was disabled or if the conversion could
	 *         not be completed. The conversion may not complete if (1) the
	 *         image was not in the CMYK colorspace to begin with or (2) there
	 *         was an error in the conversion process.
	 */
	public static Future<RenderedImage> convertCMYKtoRGB(
		byte[] bytes, String type) {

		CMYKImageTool cmykImageTool = _cmykImageToolSnapshot.get();

		return cmykImageTool.convertCMYKtoRGB(bytes, type);
	}

	/**
	 * Returns the image converted to the type.
	 *
	 * @param  sourceImage the image to convert
	 * @param  type the image type to convert to (e.g., "gif", "jpg", etc.)
	 * @return the converted image
	 */
	public static BufferedImage convertImageType(
		BufferedImage sourceImage, int type) {

		BufferedImage targetImage = new BufferedImage(
			sourceImage.getWidth(), sourceImage.getHeight(), type);

		Graphics2D graphics2D = targetImage.createGraphics();

		graphics2D.drawRenderedImage(sourceImage, null);

		graphics2D.dispose();

		return targetImage;
	}

	public static RenderedImage crop(
		RenderedImage renderedImage, int height, int width, int x, int y) {

		Rectangle rectangle = new Rectangle(x, y, width, height);

		Rectangle croppedRectangle = rectangle.intersection(
			new Rectangle(renderedImage.getWidth(), renderedImage.getHeight()));

		BufferedImage bufferedImage = getBufferedImage(renderedImage);

		return bufferedImage.getSubimage(
			croppedRectangle.x, croppedRectangle.y, croppedRectangle.width,
			croppedRectangle.height);
	}

	/**
	 * Encodes the image using the GIF format.
	 *
	 * @param  renderedImage the image to encode
	 * @param  outputStream the stream to write to
	 * @throws IOException if an IO exception occurred
	 */
	public static void encodeGIF(
			RenderedImage renderedImage, OutputStream outputStream)
		throws IOException {

		ImageIO.write(renderedImage, ImageConstants.TYPE_GIF, outputStream);
	}

	/**
	 * Encodes the image using the WBMP format.
	 *
	 * @param  renderedImage the image to encode
	 * @param  outputStream the stream to write to
	 * @throws IOException if an IO exception occurred
	 */
	public static void encodeWBMP(
			RenderedImage renderedImage, OutputStream outputStream)
		throws IOException {

		BufferedImage bufferedImage = getBufferedImage(renderedImage);

		SampleModel sampleModel = bufferedImage.getSampleModel();

		int type = sampleModel.getDataType();

		if ((bufferedImage.getType() != BufferedImage.TYPE_BYTE_BINARY) ||
			(type < DataBuffer.TYPE_BYTE) || (type > DataBuffer.TYPE_INT) ||
			(sampleModel.getNumBands() != 1) ||
			(sampleModel.getSampleSize(0) != 1)) {

			BufferedImage binaryImage = new BufferedImage(
				bufferedImage.getWidth(), bufferedImage.getHeight(),
				BufferedImage.TYPE_BYTE_BINARY);

			Graphics graphics = binaryImage.getGraphics();

			graphics.drawImage(bufferedImage, 0, 0, null);

			renderedImage = binaryImage;
		}

		if (!ImageIO.write(renderedImage, "wbmp", outputStream)) {

			// See http://www.jguru.com/faq/view.jsp?EID=127723

			outputStream.write(0);
			outputStream.write(0);
			outputStream.write(_toMultiByte(bufferedImage.getWidth()));
			outputStream.write(_toMultiByte(bufferedImage.getHeight()));

			Raster data = bufferedImage.getData();

			DataBuffer dataBuffer = data.getDataBuffer();

			int size = dataBuffer.getSize();

			for (int i = 0; i < size; i++) {
				outputStream.write((byte)dataBuffer.getElem(i));
			}
		}
	}

	public static RenderedImage flipHorizontal(RenderedImage renderedImage) {
		BufferedImage bufferedImage = getBufferedImage(renderedImage);

		AffineTransform affineTransform = AffineTransform.getScaleInstance(
			-1.0, 1.0);

		affineTransform.translate(-bufferedImage.getWidth(), 0);

		AffineTransformOp affineTransformOp = new AffineTransformOp(
			affineTransform, null);

		return affineTransformOp.filter(bufferedImage, null);
	}

	public static RenderedImage flipVertical(RenderedImage renderedImage) {
		BufferedImage bufferedImage = getBufferedImage(renderedImage);

		AffineTransform affineTransform = AffineTransform.getScaleInstance(
			1.0, -1.0);

		affineTransform.translate(0, -bufferedImage.getHeight());

		AffineTransformOp affineTransformOp = new AffineTransformOp(
			affineTransform, null);

		return affineTransformOp.filter(bufferedImage, null);
	}

	/**
	 * Returns the rendered image as a {@link BufferedImage}.
	 *
	 * @param  renderedImage the original image
	 * @return the converted image
	 */
	public static BufferedImage getBufferedImage(RenderedImage renderedImage) {
		if (renderedImage instanceof BufferedImage) {
			return (BufferedImage)renderedImage;
		}

		ColorModel colorModel = renderedImage.getColorModel();

		WritableRaster writableRaster =
			colorModel.createCompatibleWritableRaster(
				renderedImage.getWidth(), renderedImage.getHeight());

		Hashtable<String, Object> properties = new Hashtable<>();

		String[] keys = renderedImage.getPropertyNames();

		if (ArrayUtil.isNotEmpty(keys)) {
			for (String key : keys) {
				properties.put(key, renderedImage.getProperty(key));
			}
		}

		BufferedImage bufferedImage = new BufferedImage(
			colorModel, writableRaster, colorModel.isAlphaPremultiplied(),
			properties);

		renderedImage.copyData(writableRaster);

		return bufferedImage;
	}

	/**
	 * Returns the image as a <code>byte[]</code>.
	 *
	 * @param  renderedImage the image to read
	 * @param  contentType the content type (e.g., "image/jpeg") or image type
	 *         (e.g., "jpg") to use during encoding
	 * @return the encoded image
	 * @throws IOException if an IO exception occurred
	 */
	public static byte[] getBytes(
			RenderedImage renderedImage, String contentType)
		throws IOException {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		write(renderedImage, contentType, unsyncByteArrayOutputStream);

		return unsyncByteArrayOutputStream.toByteArray();
	}

	public static Image getDefaultCompanyLogo() {
		if (_defaultCompanyLogo != null) {
			return _defaultCompanyLogo;
		}

		ClassLoader classLoader = ImageToolUtil.class.getClassLoader();

		try {
			InputStream inputStream = null;

			String imageDefaultCompanyLogo = PropsUtil.get(
				PropsKeys.IMAGE_DEFAULT_COMPANY_LOGO);

			int index = imageDefaultCompanyLogo.indexOf(CharPool.SEMICOLON);

			if (index == -1) {
				inputStream = classLoader.getResourceAsStream(
					PropsUtil.get(PropsKeys.IMAGE_DEFAULT_COMPANY_LOGO));
			}
			else {
				String bundleIdString = imageDefaultCompanyLogo.substring(
					0, index);

				int bundleId = GetterUtil.getInteger(bundleIdString, -1);

				String name = imageDefaultCompanyLogo.substring(index + 1);

				if (bundleId < 0) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Fallback to portal class loader because of " +
								"invalid bundle ID " + bundleIdString);
					}

					inputStream = classLoader.getResourceAsStream(name);
				}
				else {
					Framework framework = ModuleFrameworkUtil.getFramework();

					BundleContext bundleContext = framework.getBundleContext();

					Bundle bundle = bundleContext.getBundle(bundleId);

					if (bundle != null) {
						URL url = bundle.getResource(name);

						inputStream = url.openStream();
					}
				}
			}

			if (inputStream == null) {
				_log.error("Default company logo is not available");
			}

			_defaultCompanyLogo = getImage(inputStream);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to configure the default company logo: " +
					exception.getMessage());
		}

		return _defaultCompanyLogo;
	}

	public static Image getDefaultOrganizationLogo() {
		if (_defaultOrganizationLogo != null) {
			return _defaultOrganizationLogo;
		}

		ClassLoader classLoader = ImageToolUtil.class.getClassLoader();

		try {
			InputStream inputStream = classLoader.getResourceAsStream(
				PropsUtil.get(PropsKeys.IMAGE_DEFAULT_ORGANIZATION_LOGO));

			if (inputStream == null) {
				_log.error("Default organization logo is not available");
			}

			_defaultOrganizationLogo = getImage(inputStream);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to configure the default organization logo: " +
					exception.getMessage());
		}

		return _defaultOrganizationLogo;
	}

	public static Image getDefaultSpacer() {
		if (_defaultSpacer != null) {
			return _defaultSpacer;
		}

		ClassLoader classLoader = ImageToolUtil.class.getClassLoader();

		try {
			InputStream inputStream = classLoader.getResourceAsStream(
				PropsUtil.get(PropsKeys.IMAGE_DEFAULT_SPACER));

			if (inputStream == null) {
				_log.error("Default spacer is not available");
			}

			_defaultSpacer = getImage(inputStream);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to configure the default spacer: " +
					exception.getMessage());
		}

		return _defaultSpacer;
	}

	public static Image getDefaultUserFemalePortrait() {
		if (_defaultUserFemalePortrait != null) {
			return _defaultUserFemalePortrait;
		}

		ClassLoader classLoader = ImageToolUtil.class.getClassLoader();

		try {
			InputStream inputStream = classLoader.getResourceAsStream(
				PropsUtil.get(PropsKeys.IMAGE_DEFAULT_USER_FEMALE_PORTRAIT));

			if (inputStream == null) {
				_log.error("Default user female portrait is not available");
			}

			_defaultUserFemalePortrait = getImage(inputStream);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to configure the default user female portrait: " +
					exception.getMessage());
		}

		return _defaultUserFemalePortrait;
	}

	public static Image getDefaultUserMalePortrait() {
		if (_defaultUserMalePortrait != null) {
			return _defaultUserMalePortrait;
		}

		ClassLoader classLoader = ImageToolUtil.class.getClassLoader();

		try {
			InputStream inputStream = classLoader.getResourceAsStream(
				PropsUtil.get(PropsKeys.IMAGE_DEFAULT_USER_MALE_PORTRAIT));

			if (inputStream == null) {
				_log.error("Default user male portrait is not available");
			}

			_defaultUserMalePortrait = getImage(inputStream);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to configure the default user male portrait: " +
					exception.getMessage());
		}

		return _defaultUserMalePortrait;
	}

	public static Image getDefaultUserPortrait() {
		if (_defaultUserPortrait != null) {
			return _defaultUserPortrait;
		}

		ClassLoader classLoader = ImageToolUtil.class.getClassLoader();

		try {
			InputStream inputStream = classLoader.getResourceAsStream(
				PropsUtil.get(PropsKeys.IMAGE_DEFAULT_USER_PORTRAIT));

			if (inputStream == null) {
				_log.error("Default user portrait is not available");
			}

			_defaultUserPortrait = getImage(inputStream);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to configure the default user portrait: " +
					exception.getMessage());
		}

		return _defaultUserPortrait;
	}

	public static Image getImage(byte[] bytes)
		throws ImageResolutionException, IOException {

		if (bytes == null) {
			return null;
		}

		ImageBag imageBag = read(bytes);

		RenderedImage renderedImage = imageBag.getRenderedImage();

		if (renderedImage == null) {
			throw new IOException("Unable to decode image");
		}

		int size = bytes.length;

		Image image = new ImageImpl();

		image.setCompanyId(CompanyThreadLocal.getCompanyId());
		image.setType(imageBag.getType());
		image.setHeight(renderedImage.getHeight());
		image.setWidth(renderedImage.getWidth());
		image.setSize(size);
		image.setTextObj(bytes);

		return image;
	}

	public static Image getImage(File file)
		throws ImageResolutionException, IOException {

		return getImage(_fileImpl.getBytes(file));
	}

	public static Image getImage(InputStream inputStream)
		throws ImageResolutionException, IOException {

		return getImage(_fileImpl.getBytes(inputStream, -1, true));
	}

	public static Image getImage(InputStream inputStream, boolean cleanUpStream)
		throws ImageResolutionException, IOException {

		return getImage(_fileImpl.getBytes(inputStream, -1, cleanUpStream));
	}

	public static boolean isNullOrDefaultSpacer(byte[] bytes) {
		if (ArrayUtil.isEmpty(bytes) ||
			Arrays.equals(bytes, getDefaultSpacer().getTextObj())) {

			return true;
		}

		return false;
	}

	/**
	 * Detects the image format and creates an {@link ImageBag} containing the
	 * {@link RenderedImage} and image type.
	 *
	 * @param  bytes the bytes to read
	 * @return the {@link ImageBag}
	 * @throws ImageResolutionException if the image's dimensions were larger
	 *         than those specified by portal properties
	 *         <code>image.tool.image.max.height</code> and
	 *         <code>image.tool.image.max.width</code>
	 * @throws IOException if an IO exception occurred
	 */
	public static ImageBag read(byte[] bytes)
		throws ImageResolutionException, IOException {

		String formatName = null;
		ImageInputStream imageInputStream = null;
		Queue<ImageReader> imageReaders = new LinkedList<>();
		RenderedImage renderedImage = null;

		try {
			imageInputStream = ImageIO.createImageInputStream(
				new ByteArrayInputStream(bytes));

			Iterator<ImageReader> iterator = ImageIO.getImageReaders(
				imageInputStream);

			while ((renderedImage == null) && iterator.hasNext()) {
				ImageReader imageReader = iterator.next();

				imageReaders.offer(imageReader);

				try {
					imageReader.setInput(imageInputStream);

					int height = imageReader.getHeight(0);
					int width = imageReader.getWidth(0);

					if (((PropsValues.IMAGE_TOOL_IMAGE_MAX_HEIGHT > 0) &&
						 (height > PropsValues.IMAGE_TOOL_IMAGE_MAX_HEIGHT)) ||
						((PropsValues.IMAGE_TOOL_IMAGE_MAX_WIDTH > 0) &&
						 (width > PropsValues.IMAGE_TOOL_IMAGE_MAX_WIDTH))) {

						throw new ImageResolutionException(
							StringBundler.concat(
								"Image's dimensions (", height, " px high and ",
								width, " px wide) exceed max dimensions (",
								PropsValues.IMAGE_TOOL_IMAGE_MAX_HEIGHT,
								" px high and ",
								PropsValues.IMAGE_TOOL_IMAGE_MAX_WIDTH,
								" px wide)"));
					}

					renderedImage = imageReader.read(0);
				}
				catch (ArrayIndexOutOfBoundsException | IOException exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					continue;
				}

				formatName = StringUtil.toLowerCase(
					imageReader.getFormatName());
			}

			if (renderedImage == null) {
				throw new IOException("Unsupported image type");
			}
		}
		finally {
			while (!imageReaders.isEmpty()) {
				ImageReader imageReader = imageReaders.poll();

				imageReader.dispose();
			}

			if (imageInputStream != null) {
				imageInputStream.close();
			}
		}

		String type = ImageConstants.TYPE_JPEG;

		if (formatName.contains(ImageConstants.TYPE_BMP)) {
			type = ImageConstants.TYPE_BMP;
		}
		else if (formatName.contains(ImageConstants.TYPE_GIF)) {
			type = ImageConstants.TYPE_GIF;
		}
		else if (formatName.contains("jpeg") ||
				 StringUtil.equalsIgnoreCase(type, "jpeg")) {

			type = ImageConstants.TYPE_JPEG;
		}
		else if (formatName.contains(ImageConstants.TYPE_PNG)) {
			type = ImageConstants.TYPE_PNG;
		}
		else if (formatName.contains(ImageConstants.TYPE_TIFF)) {
			type = ImageConstants.TYPE_TIFF;
		}
		else {
			throw new IllegalArgumentException(type + " is not supported");
		}

		return new ImageBag(renderedImage, type);
	}

	/**
	 * Detects the image format and creates an {@link ImageBag} containing the
	 * {@link RenderedImage} and image type.
	 *
	 * @param  file the file to read
	 * @return the {@link ImageBag}
	 * @throws ImageResolutionException if the image's dimensions were larger
	 *         than those specified by portal properties
	 *         <code>image.tool.image.max.height</code> and
	 *         <code>image.tool.image.max.width</code>
	 * @throws IOException if an IO exception occurred
	 */
	public static ImageBag read(File file)
		throws ImageResolutionException, IOException {

		return read(_fileImpl.getBytes(file));
	}

	public static ImageBag read(InputStream inputStream)
		throws ImageResolutionException, IOException {

		return read(_fileImpl.getBytes(inputStream));
	}

	public static RenderedImage rotate(
		RenderedImage renderedImage, int degrees) {

		BufferedImage bufferedImage = getBufferedImage(renderedImage);

		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();

		double radians = Math.toRadians(degrees);

		double absoluteSin = Math.abs(Math.sin(radians));
		double absoluteCos = Math.abs(Math.cos(radians));

		int rotatedImageWidth = (int)Math.floor(
			(imageWidth * absoluteCos) + (imageHeight * absoluteSin));
		int rotatedImageHeight = (int)Math.floor(
			(imageHeight * absoluteCos) + (imageWidth * absoluteSin));

		BufferedImage rotatedBufferedImage = new BufferedImage(
			rotatedImageWidth, rotatedImageHeight, bufferedImage.getType());

		AffineTransform affineTransform = new AffineTransform();

		affineTransform.translate(
			rotatedImageWidth / 2, rotatedImageHeight / 2);
		affineTransform.rotate(radians);
		affineTransform.translate(imageWidth / -2, imageHeight / -2);

		Graphics2D graphics2D = rotatedBufferedImage.createGraphics();

		graphics2D.drawImage(bufferedImage, affineTransform, null);

		graphics2D.dispose();

		return rotatedBufferedImage;
	}

	/**
	 * Returns the scaled image based on the given width with the height
	 * calculated to preserve aspect ratio.
	 *
	 * @param  renderedImage the image to scale
	 * @param  width the new width; also used to calculate the new height
	 * @return the scaled image
	 */
	public static RenderedImage scale(RenderedImage renderedImage, int width) {
		if (width <= 0) {
			return renderedImage;
		}

		int imageHeight = renderedImage.getHeight();

		int imageWidth = renderedImage.getWidth();

		double factor = (double)width / imageWidth;

		int scaledHeight = (int)Math.round(factor * imageHeight);

		int scaledWidth = width;

		return _scale(renderedImage, scaledHeight, scaledWidth);
	}

	/**
	 * Returns the scaled image based on the maximum height and width given
	 * while preserving the aspect ratio. If the image is already larger in both
	 * dimensions, the image will not be scaled.
	 *
	 * @param  renderedImage the image to scale
	 * @param  maxHeight the maximum height allowed for image
	 * @param  maxWidth the maximum width allowed for image
	 * @return the scaled image
	 */
	public static RenderedImage scale(
		RenderedImage renderedImage, int maxHeight, int maxWidth) {

		int imageHeight = renderedImage.getHeight();
		int imageWidth = renderedImage.getWidth();

		if (maxHeight == 0) {
			maxHeight = imageHeight;
		}

		if (maxWidth == 0) {
			maxWidth = imageWidth;
		}

		if ((imageHeight <= maxHeight) && (imageWidth <= maxWidth)) {
			return renderedImage;
		}

		double factor = Math.min(
			(double)maxHeight / imageHeight, (double)maxWidth / imageWidth);

		int scaledHeight = Math.max(1, (int)Math.round(factor * imageHeight));
		int scaledWidth = Math.max(1, (int)Math.round(factor * imageWidth));

		return _scale(renderedImage, scaledHeight, scaledWidth);
	}

	/**
	 * Encodes the image using the content or image type.
	 *
	 * @param  renderedImage the image to encode
	 * @param  contentType the content type (e.g., "image/jpeg") or image type
	 *         (e.g., "jpg") to use during encoding
	 * @param  outputStream the stream to write to
	 * @throws IOException if an IO exception occurred
	 */
	public static void write(
			RenderedImage renderedImage, String contentType,
			OutputStream outputStream)
		throws IOException {

		Iterator<ImageWriter> iterator = ImageIO.getImageWritersByMIMEType(
			contentType);

		if (!iterator.hasNext()) {
			ImageTypeSpecifier imageTypeSpecifier =
				ImageTypeSpecifier.createFromRenderedImage(renderedImage);

			iterator = ImageIO.getImageWriters(imageTypeSpecifier, contentType);
		}

		while (iterator.hasNext()) {
			ImageWriter imageWriter = iterator.next();

			ImageOutputStream imageOutputStream =
				ImageIO.createImageOutputStream(outputStream);

			try {
				imageWriter.setOutput(imageOutputStream);

				imageWriter.write(renderedImage);

				return;
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
			finally {
				imageWriter.dispose();

				imageOutputStream.flush();
			}
		}

		throw new IOException("No image writer is available for image type");
	}

	private static RenderedImage _scale(
		RenderedImage renderedImage, int scaledHeight, int scaledWidth) {

		// See http://www.oracle.com/technetwork/java/index-137037.html

		BufferedImage originalBufferedImage = getBufferedImage(renderedImage);

		int type = originalBufferedImage.getType();

		if ((type == BufferedImage.TYPE_BYTE_INDEXED) ||
			(type == BufferedImage.TYPE_CUSTOM)) {

			type = BufferedImage.TYPE_INT_ARGB;
		}

		BufferedImage scaledBufferedImage = new BufferedImage(
			scaledWidth, scaledHeight, type);

		int originalHeight = originalBufferedImage.getHeight();
		int originalWidth = originalBufferedImage.getWidth();

		if (((scaledHeight * 2) >= originalHeight) &&
			((scaledWidth * 2) >= originalWidth)) {

			Graphics2D scaledGraphics2D = scaledBufferedImage.createGraphics();

			scaledGraphics2D.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			scaledGraphics2D.setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			scaledGraphics2D.setRenderingHint(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

			scaledGraphics2D.drawImage(
				originalBufferedImage, 0, 0, scaledWidth, scaledHeight, null);

			scaledGraphics2D.dispose();

			return scaledBufferedImage;
		}

		BufferedImage tempBufferedImage = new BufferedImage(
			originalWidth, originalHeight, scaledBufferedImage.getType());

		Graphics2D tempGraphics2D = tempBufferedImage.createGraphics();

		RenderingHints renderingHints = new RenderingHints(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		tempGraphics2D.setRenderingHints(renderingHints);

		ColorModel originalColorModel = originalBufferedImage.getColorModel();

		if (originalColorModel.hasAlpha()) {
			tempGraphics2D.setComposite(AlphaComposite.Src);
		}

		int startHeight = scaledHeight;
		int startWidth = scaledWidth;

		while ((startHeight < originalHeight) && (startWidth < originalWidth)) {
			startHeight *= 2;
			startWidth *= 2;
		}

		originalHeight = startHeight / 2;
		originalWidth = startWidth / 2;

		tempGraphics2D.drawImage(
			originalBufferedImage, 0, 0, originalWidth, originalHeight, null);

		while ((originalHeight >= (scaledHeight * 2)) &&
			   (originalWidth >= (scaledWidth * 2))) {

			originalHeight /= 2;

			if (originalHeight < scaledHeight) {
				originalHeight = scaledHeight;
			}

			originalWidth /= 2;

			if (originalWidth < scaledWidth) {
				originalWidth = scaledWidth;
			}

			tempGraphics2D.drawImage(
				tempBufferedImage, 0, 0, originalWidth, originalHeight, 0, 0,
				originalWidth * 2, originalHeight * 2, null);
		}

		tempGraphics2D.dispose();

		Graphics2D scaledGraphics2D = scaledBufferedImage.createGraphics();

		scaledGraphics2D.drawImage(
			tempBufferedImage, 0, 0, scaledWidth, scaledHeight, 0, 0,
			originalWidth, originalHeight, null);

		scaledGraphics2D.dispose();

		return scaledBufferedImage;
	}

	private static byte[] _toMultiByte(int intValue) {
		int numBits = 32;
		int mask = 0x80000000;

		while ((mask != 0) && ((intValue & mask) == 0)) {
			numBits--;
			mask >>>= 1;
		}

		int numBitsLeft = numBits;

		byte[] multiBytes = new byte[(numBitsLeft + 6) / 7];

		int maxIndex = multiBytes.length - 1;

		for (int b = 0; b <= maxIndex; b++) {
			multiBytes[b] = (byte)((intValue >>> ((maxIndex - b) * 7)) & 0x7f);

			if (b != maxIndex) {
				multiBytes[b] |= (byte)0x80;
			}
		}

		return multiBytes;
	}

	private static final Log _log = LogFactoryUtil.getLog(ImageToolUtil.class);

	private static final Snapshot<CMYKImageTool> _cmykImageToolSnapshot =
		new Snapshot<>(ImageToolUtil.class, CMYKImageTool.class);
	private static Image _defaultCompanyLogo;
	private static Image _defaultOrganizationLogo;
	private static Image _defaultSpacer;
	private static Image _defaultUserFemalePortrait;
	private static Image _defaultUserMalePortrait;
	private static Image _defaultUserPortrait;
	private static final FileImpl _fileImpl = FileImpl.getInstance();

	static {
		ImageIO.setUseCache(PropsValues.IMAGE_IO_USE_DISK_CACHE);
	}

}