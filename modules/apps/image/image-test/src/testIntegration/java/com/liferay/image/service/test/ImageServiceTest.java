/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.image.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ImageService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class ImageServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_image = _imageLocalService.updateImage(
			TestPropsValues.getCompanyId(), _counterLocalService.increment(),
			FileUtil.getBytes(getClass(), "dependencies/liferay.jpg"));
	}

	@After
	public void tearDown() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_imageLocalService.deleteImage(_image.getImageId());
	}

	@Test
	@TestInfo("LPD-106868")
	public void testGetImage() throws Exception {
		Assert.assertEquals(
			_image, _imageService.getImage(_image.getImageId()));

		UserTestUtil.setUser(UserTestUtil.addUser());

		_assertMustBeCompanyAdmin(_image.getImageId());
		_assertMustBeCompanyAdmin(_counterLocalService.increment());

		UserTestUtil.setUser(
			UserTestUtil.addCompanyAdminUser(CompanyTestUtil.addCompany()));

		_assertMustBeCompanyAdmin(_image.getImageId());
	}

	private void _assertMustBeCompanyAdmin(long imageId) throws Exception {
		try {
			_imageService.getImage(imageId);

			Assert.fail();
		}
		catch (PrincipalException.MustBeCompanyAdmin principalException) {
		}
	}

	@Inject
	private CounterLocalService _counterLocalService;

	private Image _image;

	@Inject
	private ImageLocalService _imageLocalService;

	@Inject
	private ImageService _imageService;

}