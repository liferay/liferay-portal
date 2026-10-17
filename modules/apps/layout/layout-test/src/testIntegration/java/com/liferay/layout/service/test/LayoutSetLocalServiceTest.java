/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.LayoutSetJavaScriptException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutSetLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test(expected = LayoutSetJavaScriptException.class)
	public void testUpdateLayoutSetWithJavaScriptIvalidValue1()
		throws Exception {

		_layoutSetLocalService.updateSettings(
			_group.getGroupId(), false,
			UnicodePropertiesBuilder.put(
				"javascript", "<script>"
			).buildString());
	}

	@Test(expected = LayoutSetJavaScriptException.class)
	public void testUpdateLayoutSetWithJavaScriptIvalidValue2()
		throws Exception {

		_layoutSetLocalService.updateSettings(
			_group.getGroupId(), false,
			UnicodePropertiesBuilder.put(
				"javascript", "</script>"
			).buildString());
	}

	@Test
	public void testUpdateLogoWithChangedImageBytes() throws Exception {
		LayoutSet layoutSet = _layoutSetLocalService.updateLogo(
			_group.getGroupId(), false, true,
			FileUtil.getBytes(getClass(), "dependencies/classic_logo.png"));

		long logoId = layoutSet.getLogoId();

		byte[] bytes = FileUtil.getBytes(
			getClass(), "dependencies/dxp_logo.png");

		layoutSet = _layoutSetLocalService.updateLogo(
			_group.getGroupId(), false, true, bytes);

		Assert.assertNotEquals(logoId, layoutSet.getLogoId());

		Assert.assertNull(_imageLocalService.fetchImage(logoId));

		_assertImageBytes(bytes, layoutSet.getLogoId());
	}

	@Test
	public void testUpdateLogoWithUnchangedImageBytes() throws Exception {
		byte[] bytes = FileUtil.getBytes(
			getClass(), "dependencies/classic_logo.png");

		LayoutSet layoutSet = _layoutSetLocalService.updateLogo(
			_group.getGroupId(), false, true, bytes);

		long logoId = layoutSet.getLogoId();

		layoutSet = _layoutSetLocalService.updateLogo(
			_group.getGroupId(), false, true, bytes);

		Assert.assertEquals(logoId, layoutSet.getLogoId());

		_assertImageBytes(bytes, logoId);
	}

	@Test
	public void testUpdateLogoWithUnchangedImageBytesInPublication()
		throws Exception {

		byte[] bytes = FileUtil.getBytes(
			getClass(), "dependencies/classic_logo.png");

		LayoutSet layoutSet = _layoutSetLocalService.updateLogo(
			_group.getGroupId(), false, true, bytes);

		long logoId = layoutSet.getLogoId();

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, LayoutSetLocalServiceTest.class.getName(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			layoutSet = _layoutSetLocalService.updateLogo(
				_group.getGroupId(), false, true, bytes);

			Assert.assertNotEquals(logoId, layoutSet.getLogoId());
		}
		finally {
			_ctCollectionLocalService.deleteCTCollection(ctCollection);
		}
	}

	@Test
	public void testUpdateLogoWithUnchangedImageBytesRestoresMissingFile()
		throws Exception {

		byte[] bytes = FileUtil.getBytes(
			getClass(), "dependencies/classic_logo.png");

		LayoutSet layoutSet = _layoutSetLocalService.updateLogo(
			_group.getGroupId(), false, true, bytes);

		Image image = _imageLocalService.getImage(layoutSet.getLogoId());

		_store.deleteFile(
			image.getCompanyId(), 0,
			image.getImageId() + StringPool.PERIOD + image.getType(),
			Store.VERSION_DEFAULT);

		layoutSet = _layoutSetLocalService.updateLogo(
			_group.getGroupId(), false, true, bytes);

		Assert.assertNotEquals(image.getImageId(), layoutSet.getLogoId());

		_assertImageBytes(bytes, layoutSet.getLogoId());
	}

	private void _assertImageBytes(byte[] bytes, long imageId)
		throws Exception {

		Image image = _imageLocalService.getImage(imageId);

		Assert.assertArrayEquals(
			bytes,
			FileUtil.getBytes(
				_imageLocalService.getImageInputStream(
					image.getCompanyId(), imageId, image.getType())));
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ImageLocalService _imageLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject(filter = "default=true", type = Store.class)
	private Store _store;

}