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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.model.ImageConstants;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;

import java.awt.image.RenderedImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

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

			scaledImageFile = _scaleAndConvertToPNG(
				amImageConfigurationEntry, imageFile);

			ImageBag imageBag = ImageToolUtil.read(scaledImageFile);

			RenderedImage renderedImage = imageBag.getRenderedImage();

			return new AMImageScaledImageImpl(
				ImageToolUtil.getBytes(renderedImage, imageBag.getType()),
				renderedImage.getHeight(), ContentTypes.IMAGE_PNG,
				renderedImage.getWidth());
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

	private File _getFile(FileVersion fileVersion)
		throws IOException, PortalException {

		try (InputStream inputStream = fileVersion.getContentStream(false)) {
			return _file.createTempFile(inputStream);
		}
	}

	private File _scaleAndConvertToPNG(
			AMImageConfigurationEntry amImageConfigurationEntry, File imageFile)
		throws Exception {

		Map<String, String> properties =
			amImageConfigurationEntry.getProperties();

		byte[] bytes = _imageMagick.scale(
			_file.getBytes(imageFile), ImageConstants.TYPE_PNG,
			GetterUtil.getInteger(properties.get("max-width")),
			GetterUtil.getInteger(properties.get("max-height")));

		if (bytes == null) {
			throw new AMRuntimeException.IOException(
				"Unable to scale image using ImageMagick");
		}

		return _file.createTempFile(bytes);
	}

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private ImageMagick _imageMagick;

}