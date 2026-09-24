/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webserver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.awt.image.BufferedImage;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class WebServerServletTokenTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_image = ImageLocalServiceUtil.updateImage(
			TestPropsValues.getCompanyId(), CounterLocalServiceUtil.increment(),
			ImageToolUtil.getBytes(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "png"));
	}

	@Test
	public void testGetTokenIsNotDerivableFromTheImageAlone() {
		Date modifiedDate = _image.getModifiedDate();

		Assert.assertNotEquals(
			StringPool.BLANK,
			WebServerServletTokenUtil.getToken(_image.getImageId()));
		Assert.assertNotEquals(
			DigesterUtil.digestHex(
				DigesterUtil.SHA_256, String.valueOf(_image.getImageId()),
				String.valueOf(modifiedDate.getTime())),
			WebServerServletTokenUtil.getToken(_image.getImageId()));
	}

	@Test
	public void testGetTokenWhenImageDoesNotExist() {
		Assert.assertEquals(
			StringPool.BLANK,
			WebServerServletTokenUtil.getToken(
				CounterLocalServiceUtil.increment()));
	}

	@Test
	public void testGetTokenWhenImageIsModified() {
		String token = WebServerServletTokenUtil.getToken(_image.getImageId());

		Date modifiedDate = _image.getModifiedDate();

		_image.setModifiedDate(new Date(modifiedDate.getTime() + Time.SECOND));

		_image = ImageLocalServiceUtil.updateImage(_image);

		Assert.assertNotEquals(
			token, WebServerServletTokenUtil.getToken(_image.getImageId()));
	}

	@DeleteAfterTestRun
	private Image _image;

}