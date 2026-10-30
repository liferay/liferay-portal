/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.scaler;

import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.scaler.AMImageScaledImage;
import com.liferay.image.ImageMagick;
import com.liferay.portal.kernel.model.ImageConstants;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Arrays;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class AMImageMagickImageScalerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_amImageMagickImageScaler, "_file", _file);
		ReflectionTestUtil.setFieldValue(
			_amImageMagickImageScaler, "_imageMagick", _imageMagick);
	}

	@Test
	public void testScaleImage() {
		try {
			_amImageMagickImageScaler.scaleImage(
				Mockito.mock(FileVersion.class),
				Mockito.mock(AMImageConfigurationEntry.class));

			Assert.fail();
		}
		catch (AMRuntimeException.IOException ioException) {
			Throwable throwable = ioException.getCause();

			Assert.assertEquals(
				"Unable to scale image using ImageMagick",
				throwable.getMessage());
		}
	}

	@Test
	public void testScaleImageWithMimeType() throws Exception {
		_testScaleImage("avif", "image/avif", "image/avif");
		_testScaleImage("webp", "image/webp", "image/webp");
		_testScaleImage(
			ImageConstants.TYPE_PNG, ContentTypes.IMAGE_PNG, "image/heic");
	}

	private AMImageConfigurationEntry _mockAMImageConfigurationEntry(
		int maxHeight, int maxWidth) {

		AMImageConfigurationEntry amImageConfigurationEntry = Mockito.mock(
			AMImageConfigurationEntry.class);

		Mockito.when(
			amImageConfigurationEntry.getProperties()
		).thenReturn(
			HashMapBuilder.put(
				"max-height", String.valueOf(maxHeight)
			).put(
				"max-width", String.valueOf(maxWidth)
			).build()
		);

		return amImageConfigurationEntry;
	}

	private FileVersion _mockFileVersion(
			InputStream inputStream, String mimeType)
		throws Exception {

		FileVersion fileVersion = Mockito.mock(FileVersion.class);

		Mockito.when(
			fileVersion.getContentStream(false)
		).thenReturn(
			inputStream
		);

		Mockito.when(
			fileVersion.getMimeType()
		).thenReturn(
			mimeType
		);

		return fileVersion;
	}

	private void _testScaleImage(
			String expectedExtension, String expectedMimeType, String mimeType)
		throws Exception {

		byte[] bytes = RandomTestUtil.randomBytes();

		InputStream inputStream = new ByteArrayInputStream(bytes);

		java.io.File imageFile = new java.io.File(
			RandomTestUtil.randomString());

		Mockito.when(
			_file.createTempFile(inputStream)
		).thenReturn(
			imageFile
		);

		Mockito.when(
			_file.getBytes(imageFile)
		).thenReturn(
			bytes
		);

		int maxHeight = RandomTestUtil.randomInt();
		int maxWidth = RandomTestUtil.randomInt();
		byte[] scaledBytes = RandomTestUtil.randomBytes();

		Mockito.when(
			_imageMagick.scale(bytes, expectedExtension, maxWidth, maxHeight)
		).thenReturn(
			scaledBytes
		);

		java.io.File scaledImageFile = new java.io.File(
			RandomTestUtil.randomString());

		Mockito.when(
			_file.createTempFile(scaledBytes)
		).thenReturn(
			scaledImageFile
		);

		java.io.File dimensionsFile = new java.io.File(
			RandomTestUtil.randomString());

		Mockito.when(
			_file.createTempFile("txt")
		).thenReturn(
			dimensionsFile
		);

		Mockito.doReturn(
			Mockito.mock(Future.class)
		).when(
			_imageMagick
		).convert(
			Arrays.asList(
				scaledImageFile.getAbsolutePath() + "[0]", "-format", "%w %h",
				"info:" + dimensionsFile.getAbsolutePath())
		);

		Mockito.when(
			_file.read(dimensionsFile)
		).thenReturn(
			"1000 667"
		);

		AMImageScaledImage amImageScaledImage =
			_amImageMagickImageScaler.scaleImage(
				_mockFileVersion(inputStream, mimeType),
				_mockAMImageConfigurationEntry(maxHeight, maxWidth));

		try (InputStream scaledInputStream =
				amImageScaledImage.getInputStream()) {

			Assert.assertArrayEquals(
				scaledBytes, scaledInputStream.readAllBytes());
		}

		Assert.assertEquals(667, amImageScaledImage.getHeight());
		Assert.assertEquals(expectedMimeType, amImageScaledImage.getMimeType());
		Assert.assertEquals(1000, amImageScaledImage.getWidth());
	}

	private final AMImageMagickImageScaler _amImageMagickImageScaler =
		new AMImageMagickImageScaler();
	private final File _file = Mockito.mock(File.class);
	private final ImageMagick _imageMagick = Mockito.mock(ImageMagick.class);

}