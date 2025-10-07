/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.image.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class ImageLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		String className =
			"com.liferay.portal.store.file.system.AdvancedFileSystemStore";

		Assume.assumeTrue(
			StringBundler.concat(
				"Property \"", PropsKeys.DL_STORE_IMPL, "\" is not set to \"",
				className, "\""),
			Objects.equals(PropsUtil.get(PropsKeys.DL_STORE_IMPL), className));
	}

	@Test
	public void testDeleteImage() throws Exception {
		long imageId = _counterLocalService.increment();

		Image image = _imageLocalService.updateImage(
			TestPropsValues.getCompanyId(), imageId,
			FileUtil.getBytes(getClass(), "dependencies/liferay.jpg"));

		Store store = _storeSnapshot.get();
		String fileName = imageId + ".jpg";

		Assert.assertTrue(
			store.hasFile(
				image.getCompanyId(), 0, fileName, Store.VERSION_DEFAULT));

		_imageLocalService.deleteImage(imageId);

		Assert.assertFalse(
			store.hasFile(
				image.getCompanyId(), 0, fileName, Store.VERSION_DEFAULT));
	}

	private static final Snapshot<Store> _storeSnapshot = new Snapshot<>(
		ImageLocalServiceTest.class, Store.class, "(default=true)");

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private ImageLocalService _imageLocalService;

}