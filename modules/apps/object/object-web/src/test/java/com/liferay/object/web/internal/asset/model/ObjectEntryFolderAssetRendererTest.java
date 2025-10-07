/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.info.item.InfoItemReference;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class ObjectEntryFolderAssetRendererTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetURLViewInContext() throws Exception {
		AssetRenderer<ObjectEntryFolder> assetRenderer =
			new ObjectEntryFolderAssetRenderer(
				_assetDisplayPageFriendlyURLProvider, _depotEntryLocalService,
				_objectEntryFolder, null, _portal);

		Assert.assertNull(assetRenderer.getURLViewInContext(null, null));
		Assert.assertEquals(
			_getCMSFriendlyURL(),
			assetRenderer.getURLViewInContext(_themeDisplay, null));
		Assert.assertEquals(
			_getFriendlyURL(),
			assetRenderer.getURLViewInContext(_themeDisplay, null));
	}

	private String _getCMSFriendlyURL() {
		String pathFriendlyURLPublic =
			StringPool.SLASH + RandomTestUtil.randomString();

		_themeDisplay.setPathFriendlyURLPublic(pathFriendlyURLPublic);

		String portalURL = RandomTestUtil.randomString();

		_themeDisplay.setPortalURL(portalURL);

		String urlCurrent = RandomTestUtil.randomString();

		_themeDisplay.setURLCurrent(urlCurrent);

		long objectEntryFolderId = RandomTestUtil.randomLong();

		Mockito.doReturn(
			objectEntryFolderId
		).when(
			_objectEntryFolder
		).getObjectEntryFolderId();

		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.doReturn(
			DepotConstants.TYPE_SPACE
		).when(
			depotEntry
		).getType();

		Mockito.when(
			_depotEntryLocalService.fetchGroupDepotEntry(Mockito.anyLong())
		).thenReturn(
			depotEntry
		);

		long classNameId = RandomTestUtil.randomLong();

		Mockito.when(
			_portal.getClassNameId(Mockito.anyString())
		).thenReturn(
			classNameId
		);

		return StringBundler.concat(
			portalURL, pathFriendlyURLPublic, GroupConstants.CMS_FRIENDLY_URL,
			"/e/view-folder/", classNameId, StringPool.SLASH,
			objectEntryFolderId, "?p_l_mode=read&redirect=",
			HtmlUtil.escapeURL(urlCurrent));
	}

	private String _getFriendlyURL() throws Exception {
		Mockito.when(
			_depotEntryLocalService.fetchGroupDepotEntry(Mockito.anyLong())
		).thenReturn(
			null
		);

		String friendlyURL = RandomTestUtil.randomString();

		Mockito.when(
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				Mockito.any(InfoItemReference.class),
				Mockito.any(ThemeDisplay.class))
		).thenReturn(
			friendlyURL
		);

		return friendlyURL;
	}

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider = Mockito.mock(
			AssetDisplayPageFriendlyURLProvider.class);
	private final DepotEntryLocalService _depotEntryLocalService = Mockito.mock(
		DepotEntryLocalService.class);
	private final ObjectEntryFolder _objectEntryFolder = Mockito.mock(
		ObjectEntryFolder.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

}