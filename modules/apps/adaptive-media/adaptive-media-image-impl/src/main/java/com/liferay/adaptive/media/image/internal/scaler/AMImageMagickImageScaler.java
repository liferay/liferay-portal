/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.scaler;

import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.scaler.AMImageScaledImage;
import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.image.ImageMagick;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ImageConstants;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Future;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eric Yan
 */
@Component(
	configurationPid = "com.liferay.adaptive.media.image.internal.configuration.AMImageMagickConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = AMImageScaler.class
)
public class AMImageMagickImageScaler implements AMImageScaler {

	@Override
	public boolean isEnabled() {
		return _imageMagick.isEnabled();
	}

	@Override
	public AMImageScaledImage scaleImage(
		FileVersion fileVersion,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		File imageFile = null;
		File scaledImageFile = null;

		try {
			imageFile = _getFile(fileVersion);

			String mimeType = fileVersion.getMimeType();

			String extension = _extensions.get(mimeType);

			if (extension == null) {
				extension = ImageConstants.TYPE_PNG;
				mimeType = ContentTypes.IMAGE_PNG;
			}

			byte[] bytes = _scale(
				amImageConfigurationEntry, extension, imageFile);

			scaledImageFile = _file.createTempFile(bytes);

			String[] dimensions = _getDimensions(scaledImageFile);

			return new AMImageScaledImageImpl(
				bytes, GetterUtil.getInteger(dimensions[1]), mimeType,
				GetterUtil.getInteger(dimensions[0]));
		}
		catch (Exception exception) {
			throw new AMRuntimeException.IOException(
				StringBundler.concat(
					"Unable to scale file entry ", fileVersion.getFileEntryId(),
					" to match adaptive media configuration ",
					amImageConfigurationEntry.getUUID()),
				exception);
		}
		finally {
			if (imageFile != null) {
				imageFile.delete();
			}

			if (scaledImageFile != null) {
				scaledImageFile.delete();
			}
		}
	}

	private String[] _getDimensions(File file) throws Exception {
		File dimensionsFile = _file.createTempFile("txt");

		try {
			Future<?> future = _imageMagick.convert(
				Arrays.asList(
					file.getAbsolutePath() + "[0]", "-format", "%w %h",
					"info:" + dimensionsFile.getAbsolutePath()));

			future.get();

			String[] dimensions = StringUtil.split(
				_file.read(dimensionsFile), CharPool.SPACE);

			if (dimensions.length != 2) {
				throw new AMRuntimeException.IOException(
					"Unable to get image dimensions using ImageMagick");
			}

			return dimensions;
		}
		finally {
			dimensionsFile.delete();
		}
	}

	private File _getFile(FileVersion fileVersion)
		throws IOException, PortalException {

		try (InputStream inputStream = fileVersion.getContentStream(false)) {
			return _file.createTempFile(inputStream);
		}
	}

	private byte[] _scale(
			AMImageConfigurationEntry amImageConfigurationEntry,
			String extension, File imageFile)
		throws Exception {

		Map<String, String> properties =
			amImageConfigurationEntry.getProperties();

		byte[] bytes = _imageMagick.scale(
			_file.getBytes(imageFile), extension,
			GetterUtil.getInteger(properties.get("max-width")),
			GetterUtil.getInteger(properties.get("max-height")));

		if (bytes == null) {
			throw new AMRuntimeException.IOException(
				"Unable to scale image using ImageMagick");
		}

		return bytes;
	}

	private static final Map<String, String> _extensions = HashMapBuilder.put(
		"image/avif", "avif"
	).put(
		"image/webp", "webp"
	).build();

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private ImageMagick _imageMagick;

}