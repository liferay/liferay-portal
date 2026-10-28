/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.scaler;

import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.image.ImageMagick;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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

	private final AMImageMagickImageScaler _amImageMagickImageScaler =
		new AMImageMagickImageScaler();
	private final File _file = Mockito.mock(File.class);
	private final ImageMagick _imageMagick = Mockito.mock(ImageMagick.class);

}