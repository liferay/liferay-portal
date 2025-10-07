/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class DepotAssetRendererFactoryWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAssetRendererReturnsFallbackGroup() throws Exception {
		AssetRenderer<Object> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.when(
			_assetRendererFactory.getAssetRenderer(Mockito.anyLong())
		).thenReturn(
			assetRenderer
		);

		long depotGroupId = _setUpDepotGroup();

		Mockito.when(
			assetRenderer.getGroupId()
		).thenReturn(
			depotGroupId
		);

		long groupId = _setUpGroup();

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(Mockito.anyLong())
		).thenReturn(
			new long[] {depotGroupId, groupId}
		);

		DepotAssetRendererFactoryWrapper depotAssetRendererFactoryWrapper =
			new DepotAssetRendererFactoryWrapper(
				_assetRendererFactory, null, null, _groupLocalService, null,
				null, _siteConnectedGroupGroupProvider);

		try (SafeCloseable safeCloseable =
				GroupThreadLocal.setGroupIdWithSafeCloseable(-1)) {

			Assert.assertSame(
				assetRenderer,
				depotAssetRendererFactoryWrapper.getAssetRenderer(
					RandomTestUtil.randomLong()));
		}

		try (SafeCloseable safeCloseable =
				GroupThreadLocal.setGroupIdWithSafeCloseable(-1)) {

			ServiceContextThreadLocal.pushServiceContext(new ServiceContext());

			try {
				Assert.assertSame(
					assetRenderer,
					depotAssetRendererFactoryWrapper.getAssetRenderer(
						RandomTestUtil.randomLong()));
			}
			finally {
				ServiceContextThreadLocal.popServiceContext();
			}
		}
	}

	@Test
	public void testAssetRenderIsNotReturnedForUnconnectedGroup()
		throws Exception {

		AssetRenderer<Object> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.when(
			_assetRendererFactory.getAssetRenderer(Mockito.anyLong())
		).thenReturn(
			assetRenderer
		);

		long depotGroupId = _setUpDepotGroup();

		Mockito.when(
			assetRenderer.getGroupId()
		).thenReturn(
			depotGroupId
		);

		DepotAssetRendererFactoryWrapper depotAssetRendererFactoryWrapper =
			new DepotAssetRendererFactoryWrapper(
				_assetRendererFactory, null, _depotEntryLocalService,
				_groupLocalService, null, null,
				_siteConnectedGroupGroupProvider);

		try (SafeCloseable safeCloseable =
				GroupThreadLocal.setGroupIdWithSafeCloseable(_setUpGroup())) {

			Assert.assertNull(
				depotAssetRendererFactoryWrapper.getAssetRenderer(
					RandomTestUtil.randomLong()));
		}
	}

	@Test
	public void testAssetRenderIsReturnedForConnectedGroup() throws Exception {
		AssetRenderer<Object> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.when(
			_assetRendererFactory.getAssetRenderer(Mockito.anyLong())
		).thenReturn(
			assetRenderer
		);

		long depotGroupId = _setUpDepotGroup();

		Mockito.when(
			assetRenderer.getGroupId()
		).thenReturn(
			depotGroupId
		);

		long groupId = _setUpGroup();

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(groupId)
		).thenReturn(
			new long[] {depotGroupId, groupId}
		);

		DepotAssetRendererFactoryWrapper depotAssetRendererFactoryWrapper =
			new DepotAssetRendererFactoryWrapper(
				_assetRendererFactory, null, null, _groupLocalService, null,
				null, _siteConnectedGroupGroupProvider);

		try (SafeCloseable safeCloseable =
				GroupThreadLocal.setGroupIdWithSafeCloseable(groupId)) {

			Assert.assertSame(
				assetRenderer,
				depotAssetRendererFactoryWrapper.getAssetRenderer(
					RandomTestUtil.randomLong()));
		}
	}

	@Test
	public void testControlPanelIsImplicitlyConnectedToDepotEntry()
		throws Exception {

		AssetRenderer<Object> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.when(
			_assetRendererFactory.getAssetRenderer(Mockito.anyLong())
		).thenReturn(
			assetRenderer
		);

		long depotGroupId = _setUpDepotGroup();

		Mockito.when(
			assetRenderer.getGroupId()
		).thenReturn(
			depotGroupId
		);

		long controlPanelGroupId = _setUpControlPanelGroup();

		DepotAssetRendererFactoryWrapper depotAssetRendererFactoryWrapper =
			new DepotAssetRendererFactoryWrapper(
				_assetRendererFactory, null, null, _groupLocalService, null,
				null, null);

		try (SafeCloseable safeCloseable =
				GroupThreadLocal.setGroupIdWithSafeCloseable(
					controlPanelGroupId)) {

			Assert.assertSame(
				assetRenderer,
				depotAssetRendererFactoryWrapper.getAssetRenderer(
					RandomTestUtil.randomLong()));
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testGetAssetRendererByDepotEntryType() throws Exception {
		AssetRenderer<Object> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.when(
			_assetRendererFactory.getAssetRenderer(Mockito.anyLong())
		).thenReturn(
			assetRenderer
		);

		long depotGroupId = _setUpDepotGroup();

		Mockito.when(
			assetRenderer.getGroupId()
		).thenReturn(
			depotGroupId
		);

		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			_depotEntryLocalService.getGroupDepotEntry(Mockito.anyLong())
		).thenReturn(
			depotEntry
		);

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			DepotConstants.TYPE_SPACE
		);

		DepotAssetRendererFactoryWrapper depotAssetRendererFactoryWrapper =
			new DepotAssetRendererFactoryWrapper(
				_assetRendererFactory, null, _depotEntryLocalService,
				_groupLocalService, null, null,
				_siteConnectedGroupGroupProvider);

		long groupId = _setUpGroup();

		try (SafeCloseable safeCloseable =
				GroupThreadLocal.setGroupIdWithSafeCloseable(groupId)) {

			Assert.assertSame(
				assetRenderer,
				depotAssetRendererFactoryWrapper.getAssetRenderer(
					RandomTestUtil.randomLong()));
		}

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			DepotConstants.TYPE_ASSET_LIBRARY
		);

		depotAssetRendererFactoryWrapper = new DepotAssetRendererFactoryWrapper(
			_assetRendererFactory, null, _depotEntryLocalService,
			_groupLocalService, null, null, _siteConnectedGroupGroupProvider);

		try (SafeCloseable safeCloseable =
				GroupThreadLocal.setGroupIdWithSafeCloseable(groupId)) {

			Assert.assertNull(
				depotAssetRendererFactoryWrapper.getAssetRenderer(
					RandomTestUtil.randomLong()));
		}
	}

	private long _setUpControlPanelGroup() throws Exception {
		return _setUpGroup(
			group -> Mockito.when(
				group.isControlPanel()
			).thenReturn(
				true
			));
	}

	private long _setUpDepotGroup() throws Exception {
		return _setUpGroup(
			group -> Mockito.when(
				group.isDepot()
			).thenReturn(
				true
			));
	}

	private long _setUpGroup() throws Exception {
		return _setUpGroup(
			group -> {
			});
	}

	private long _setUpGroup(Consumer<Group> consumer) throws Exception {
		Group group = Mockito.mock(Group.class);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.isLayoutPrototype()
		).thenReturn(
			false
		);

		Mockito.when(
			_groupLocalService.fetchGroup(groupId)
		).thenReturn(
			group
		);

		Mockito.when(
			_groupLocalService.getGroup(groupId)
		).thenReturn(
			group
		);

		consumer.accept(group);

		return groupId;
	}

	private final AssetRendererFactory<Object> _assetRendererFactory =
		Mockito.mock(AssetRendererFactory.class);
	private final DepotEntryLocalService _depotEntryLocalService = Mockito.mock(
		DepotEntryLocalService.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider = Mockito.mock(
			SiteConnectedGroupGroupProvider.class);

}