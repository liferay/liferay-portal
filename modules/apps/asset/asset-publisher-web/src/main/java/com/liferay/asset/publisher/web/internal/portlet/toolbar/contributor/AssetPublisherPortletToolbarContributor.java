/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.toolbar.contributor;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.constants.AssetPublisherWebKeys;
import com.liferay.asset.publisher.web.internal.display.context.AssetPublisherDisplayContext;
import com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper;
import com.liferay.asset.util.AssetHelper;
import com.liferay.asset.util.AssetPublisherAddItemHolder;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.toolbar.contributor.BasePortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
		"mvc.path=-", "mvc.path=/view.jsp"
	},
	service = PortletToolbarContributor.class
)
public class AssetPublisherPortletToolbarContributor
	extends BasePortletToolbarContributor {

	@Override
	protected List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		List<MenuItem> menuItems = new ArrayList<>();

		try {
			_addPortletTitleAddAssetEntryMenuItems(
				menuItems, portletRequest, portletResponse);
		}
		catch (Exception exception) {
			_log.error("Unable to add folder menu item", exception);
		}

		return menuItems;
	}

	private void _addPortletTitleAddAssetEntryMenuItems(
			List<MenuItem> menuItems, PortletRequest portletRequest,
			PortletResponse portletResponse)
		throws Exception {

		AssetPublisherDisplayContext assetPublisherDisplayContext =
			(AssetPublisherDisplayContext)portletRequest.getAttribute(
				AssetPublisherWebKeys.ASSET_PUBLISHER_DISPLAY_CONTEXT);

		if ((assetPublisherDisplayContext == null) ||
			!_isVisible(assetPublisherDisplayContext, portletRequest)) {

			return;
		}

		Map<Long, List<AssetPublisherAddItemHolder>>
			scopeAssetPublisherAddItemHolders =
				assetPublisherDisplayContext.
					getScopeAssetPublisherAddItemHolders(1);

		if (MapUtil.isEmpty(scopeAssetPublisherAddItemHolders)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (scopeAssetPublisherAddItemHolders.size() == 1) {
			Set<Map.Entry<Long, List<AssetPublisherAddItemHolder>>> entries =
				scopeAssetPublisherAddItemHolders.entrySet();

			Iterator<Map.Entry<Long, List<AssetPublisherAddItemHolder>>>
				iterator = entries.iterator();

			Map.Entry<Long, List<AssetPublisherAddItemHolder>>
				scopeAddPortletURL = iterator.next();

			long groupId = scopeAddPortletURL.getKey();

			List<AssetPublisherAddItemHolder> assetPublisherAddItemHolders =
				scopeAddPortletURL.getValue();

			for (AssetPublisherAddItemHolder assetPublisherAddItemHolder :
					assetPublisherAddItemHolders) {

				URLMenuItem urlMenuItem = _getPortletTitleAddAssetEntryMenuItem(
					themeDisplay, assetPublisherDisplayContext, groupId,
					assetPublisherAddItemHolder);

				menuItems.add(urlMenuItem);
			}

			return;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		String title = _language.get(
			themeDisplay.getLocale(), "add-content-select-scope-and-type");

		urlMenuItem.setData(
			HashMapBuilder.<String, Object>put(
				"id",
				() -> {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					return HtmlUtil.escape(portletDisplay.getNamespace()) +
						"editAsset";
				}
			).put(
				"title", title
			).build());
		urlMenuItem.setLabel(title);

		urlMenuItem.setURL(
			PortletURLBuilder.createRenderURL(
				_portal.getLiferayPortletResponse(portletResponse)
			).setMVCPath(
				"/add_asset_selector.jsp"
			).setRedirect(
				themeDisplay.getURLCurrent()
			).buildString());

		menuItems.add(urlMenuItem);
	}

	private URLMenuItem _getPortletTitleAddAssetEntryMenuItem(
		ThemeDisplay themeDisplay,
		AssetPublisherDisplayContext assetPublisherDisplayContext, long groupId,
		AssetPublisherAddItemHolder assetPublisherAddItemHolder) {

		URLMenuItem urlMenuItem = new URLMenuItem();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String message = assetPublisherAddItemHolder.getModelResource();

		urlMenuItem.setData(
			HashMapBuilder.<String, Object>put(
				"id",
				HtmlUtil.escape(portletDisplay.getNamespace()) + "editAsset"
			).put(
				"title",
				_language.format(
					themeDisplay.getLocale(), "new-x", message, false)
			).build());
		urlMenuItem.setLabel(message);

		long curGroupId = groupId;

		Group group = _groupLocalService.fetchGroup(groupId);

		if (!group.isStagedPortlet(
				assetPublisherAddItemHolder.getPortletId()) &&
			!group.isStagedRemotely()) {

			curGroupId = group.getLiveGroupId();
		}

		PortletURL portletURL = PortletURLBuilder.create(
			assetPublisherAddItemHolder.getPortletURL()
		).setPortletResource(
			AssetPublisherPortletKeys.ASSET_PUBLISHER
		).buildPortletURL();

		boolean addDisplayPageParameter =
			_assetPublisherWebHelper.isDefaultAssetPublisher(
				themeDisplay.getLayout(), portletDisplay.getId(),
				assetPublisherDisplayContext.getPortletResource());

		urlMenuItem.setURL(
			_assetHelper.getAddURLPopUp(
				curGroupId, themeDisplay.getPlid(), portletURL,
				addDisplayPageParameter, themeDisplay.getLayout()));

		return urlMenuItem;
	}

	private boolean _isVisible(
			AssetPublisherDisplayContext assetPublisherDisplayContext,
			PortletRequest portletRequest)
		throws Exception {

		if (!assetPublisherDisplayContext.isShowAddContentButton()) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (scopeGroup.hasStagingGroup() && !scopeGroup.isStagingGroup() &&
			PropsValues.STAGING_LIVE_GROUP_LOCKING_ENABLED) {

			return false;
		}

		Layout layout = themeDisplay.getLayout();

		if (layout.isLayoutPrototypeLinkActive() &&
			assetPublisherDisplayContext.isSelectionStyleManual()) {

			return false;
		}

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		if (portletName.equals(
				AssetPublisherPortletKeys.HIGHEST_RATED_ASSETS) ||
			portletName.equals(AssetPublisherPortletKeys.MOST_VIEWED_ASSETS) ||
			portletName.equals(AssetPublisherPortletKeys.RELATED_ASSETS)) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetPublisherPortletToolbarContributor.class);

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private AssetPublisherWebHelper _assetPublisherWebHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}