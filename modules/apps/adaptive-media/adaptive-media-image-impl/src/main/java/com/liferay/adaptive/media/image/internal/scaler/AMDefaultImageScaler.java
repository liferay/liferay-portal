/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.scaler;

import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.internal.processor.util.TiffOrientationTransformer;
import com.liferay.adaptive.media.image.internal.util.RenderedImageUtil;
import com.liferay.adaptive.media.image.scaler.AMImageScaledImage;
import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.GetterUtil;

import java.awt.image.RenderedImage;

import java.io.InputStream;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class AMDefaultImageScaler implements AMImageScaler {

	@Override
	public AMImageScaledImage scaleImage(
		FileVersion fileVersion,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		try {
			RenderedImage renderedImage = TiffOrientationTransformer.transform(
				() -> _getInputStream(fileVersion));

			Map<String, String> properties =
				amImageConfigurationEntry.getProperties();

			int maxHeight = GetterUtil.getInteger(properties.get("max-height"));
			int maxWidth = GetterUtil.getInteger(properties.get("max-width"));

			RenderedImage scaledRenderedImage = ImageToolUtil.scale(
				renderedImage, maxHeight, maxWidth);

			return new AMImageScaledImageImpl(
				RenderedImageUtil.getRenderedImageContentStream(
					scaledRenderedImage, fileVersion.getMimeType()),
				scaledRenderedImage.getHeight(), fileVersion.getMimeType(),
				scaledRenderedImage.getWidth());
		}
		catch (AMRuntimeException.IOException | PortalException exception) {
			throw new AMRuntimeException.IOException(
				StringBundler.concat(
					"Unable to scale file entry ", fileVersion.getFileEntryId(),
					" to match adaptive media configuration ",
					amImageConfigurationEntry.getUUID()),
				exception);
		}
	}

	private InputStream _getInputStream(FileVersion fileVersion) {
		try {
			return fileVersion.getContentStream(false);
		}
		catch (PortalException portalException) {
			throw new AMRuntimeException.IOException(portalException);
		}
	}

}