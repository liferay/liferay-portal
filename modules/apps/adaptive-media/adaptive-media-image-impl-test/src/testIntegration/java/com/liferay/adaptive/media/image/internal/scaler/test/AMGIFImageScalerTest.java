/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.scaler.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.scaler.AMImageScaledImage;
import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class AMGIFImageScalerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_configurationTemporarySwapper = new ConfigurationTemporarySwapper(
			"com.liferay.adaptive.media.image.internal.configuration." +
				"AMImageConfiguration",
			new HashMapDictionary<>());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_configurationTemporarySwapper.close();
	}

	@Before
	public void setUp() throws Exception {
		Assume.assumeTrue(_amImageScaler.isEnabled());

		_group = GroupTestUtil.addGroup();

		UserTestUtil.setUser(TestPropsValues.getUser());

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry -> true);

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry.getUUID());
		}

		_amImageConfigurationEntry = _addTestVariant();
	}

	@Test
	public void testAMGIFImageScaler() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		AMImageScaledImage amImageScaledImage = _amImageScaler.scaleImage(
			fileEntry.getFileVersion(), _amImageConfigurationEntry);

		Assert.assertEquals(25, amImageScaledImage.getHeight());
		Assert.assertEquals(100, amImageScaledImage.getWidth());

		Assert.assertArrayEquals(
			FileUtil.getBytes(
				AMGIFImageScalerTest.class, "dependencies/scaled.gif"),
			FileUtil.getBytes(amImageScaledImage.getInputStream()));
	}

	private FileEntry _addFileEntry() throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".gif", ContentTypes.IMAGE_GIF,
			FileUtil.getBytes(
				AMGIFImageScalerTest.class, "dependencies/image.gif"),
			null, null, null, new ServiceContext());
	}

	private AMImageConfigurationEntry _addTestVariant() throws Exception {
		return _amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), "small", StringPool.BLANK, "0",
			HashMapBuilder.put(
				"max-height", "100"
			).put(
				"max-width", "100"
			).build());
	}

	private static ConfigurationTemporarySwapper _configurationTemporarySwapper;

	private AMImageConfigurationEntry _amImageConfigurationEntry;

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Inject(filter = "mimeTypes=image/gif", type = AMImageScaler.class)
	private AMImageScaler _amImageScaler;

	@DeleteAfterTestRun
	private Group _group;

}