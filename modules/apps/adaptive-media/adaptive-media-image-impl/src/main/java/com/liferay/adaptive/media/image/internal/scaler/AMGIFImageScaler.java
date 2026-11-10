/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.scaler;

import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.internal.configuration.AMImageConfiguration;
import com.liferay.adaptive.media.image.internal.util.RenderedImageUtil;
import com.liferay.adaptive.media.image.internal.util.Tuple;
import com.liferay.adaptive.media.image.scaler.AMImageScaledImage;
import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.process.CollectorOutputProcessor;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.GetterUtil;

import java.awt.image.RenderedImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	configurationPid = "com.liferay.adaptive.media.image.internal.configuration.AMImageConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "mimeTypes=image/gif", service = AMImageScaler.class
)
public class AMGIFImageScaler implements AMImageScaler {

	@Override
	public boolean isEnabled() {
		return _amImageConfiguration.gifsicleEnabled();
	}

	@Override
	public AMImageScaledImage scaleImage(
		FileVersion fileVersion,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		try {
			File file = _getFile(fileVersion);

			Future<Map.Entry<byte[], byte[]>> collectorFuture =
				ProcessUtil.execute(
					CollectorOutputProcessor.INSTANCE, "gifsicle", "--careful",
					"--resize-fit",
					_getResizeFitValues(amImageConfigurationEntry), "--output",
					"-", file.getAbsolutePath());

			Map.Entry<byte[], byte[]> objectValuePair = collectorFuture.get();

			file.delete();

			byte[] bytes = objectValuePair.getKey();

			Tuple<Integer, Integer> dimension = _getDimension(bytes);

			return new AMImageScaledImageImpl(
				bytes, dimension.second, fileVersion.getMimeType(),
				dimension.first);
		}
		catch (ExecutionException | InterruptedException | IOException |
			   PortalException | ProcessException exception) {

			throw new AMRuntimeException.IOException(exception);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_amImageConfiguration = ConfigurableUtil.createConfigurable(
			AMImageConfiguration.class, properties);
	}

	private Tuple<Integer, Integer> _getDimension(byte[] bytes)
		throws IOException, PortalException {

		try (InputStream inputStream = new UnsyncByteArrayInputStream(bytes)) {
			RenderedImage renderedImage = RenderedImageUtil.readImage(
				inputStream);

			return Tuple.of(
				renderedImage.getWidth(), renderedImage.getHeight());
		}
	}

	private File _getFile(FileVersion fileVersion)
		throws IOException, PortalException {

		try (InputStream inputStream = fileVersion.getContentStream(false)) {
			return _file.createTempFile(inputStream);
		}
	}

	private String _getResizeFitValues(
		AMImageConfigurationEntry amImageConfigurationEntry) {

		Map<String, String> properties =
			amImageConfigurationEntry.getProperties();

		int maxHeight = GetterUtil.getInteger(properties.get("max-height"));

		String maxHeightString = StringPool.UNDERLINE;

		if (maxHeight != 0) {
			maxHeightString = String.valueOf(maxHeight);
		}

		int maxWidth = GetterUtil.getInteger(properties.get("max-width"));

		String maxWidthString = StringPool.UNDERLINE;

		if (maxWidth != 0) {
			maxWidthString = String.valueOf(maxWidth);
		}

		return StringBundler.concat(maxWidthString, "x", maxHeightString);
	}

	private volatile AMImageConfiguration _amImageConfiguration;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

}