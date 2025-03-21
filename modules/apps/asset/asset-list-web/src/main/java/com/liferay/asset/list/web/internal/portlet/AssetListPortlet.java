/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.exception.AssetListEntryTitleException;
import com.liferay.asset.list.exception.DuplicateAssetListEntryTitleException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.web.internal.constants.AssetListWebKeys;
import com.liferay.asset.list.web.internal.display.context.AssetListDisplayContext;
import com.liferay.asset.list.web.internal.display.context.AssetListItemsDisplayContext;
import com.liferay.asset.list.web.internal.display.context.EditAssetListDisplayContext;
import com.liferay.asset.list.web.internal.display.context.InfoCollectionProviderDisplayContext;
import com.liferay.asset.list.web.internal.display.context.InfoCollectionProviderItemsDisplayContext;
import com.liferay.asset.list.web.internal.display.context.SelectStructureFieldDisplayContext;
import com.liferay.asset.list.web.internal.servlet.taglib.util.ListItemsActionDropdownItems;
import com.liferay.asset.util.AssetRendererFactoryClassProvider;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.info.display.url.provider.InfoEditURLProviderRegistry;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-asset-list-web",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Asset List",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AssetListPortlet extends MVCPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			AssetListWebKeys.ASSET_LIST_ITEMS_DISPLAY_CONTEXT,
			new AssetListItemsDisplayContext(
				_assetListAssetEntryProvider, renderRequest, renderResponse));

		AssetListDisplayContext assetListDisplayContext =
			new AssetListDisplayContext(
				_assetRendererFactoryClassProvider, renderRequest,
				renderResponse);

		renderRequest.setAttribute(
			AssetListWebKeys.ASSET_LIST_DISPLAY_CONTEXT,
			assetListDisplayContext);

		renderRequest.setAttribute(AssetListWebKeys.DDM_INDEXER, _ddmIndexer);
		renderRequest.setAttribute(
			AssetListWebKeys.EDIT_ASSET_LIST_DISPLAY_CONTEXT,
			new EditAssetListDisplayContext(
				_assetRendererFactoryClassProvider,
				_infoSearchClassMapperRegistry, _itemSelector, renderRequest,
				renderResponse, _segmentsConfigurationProvider,
				_getUnicodeProperties(assetListDisplayContext)));
		renderRequest.setAttribute(
			AssetListWebKeys.INFO_COLLECTION_PROVIDER_DISPLAY_CONTEXT,
			new InfoCollectionProviderDisplayContext(
				_infoItemServiceRegistry, renderRequest, renderResponse));
		renderRequest.setAttribute(
			AssetListWebKeys.INFO_COLLECTION_PROVIDER_ITEMS_DISPLAY_CONTEXT,
			new InfoCollectionProviderItemsDisplayContext(
				_infoItemServiceRegistry, renderRequest, renderResponse));
		renderRequest.setAttribute(
			AssetListWebKeys.ITEM_SELECTOR, _itemSelector);
		renderRequest.setAttribute(
			AssetListWebKeys.LIST_ITEMS_ACTION_DROPDOWN_ITEMS,
			new ListItemsActionDropdownItems(
				_assetDisplayPageFriendlyURLProvider, _dlAppService,
				_infoEditURLProviderRegistry, _infoItemServiceRegistry,
				_infoSearchClassMapperRegistry,
				_portal.getHttpServletRequest(renderRequest)));
		renderRequest.setAttribute(
			AssetListWebKeys.SELECT_STRUCTURE_FIELD_DISPLAY_CONTEXT,
			new SelectStructureFieldDisplayContext(
				_assetRendererFactoryClassProvider, renderRequest,
				renderResponse));

		super.doDispatch(renderRequest, renderResponse);
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof AssetListEntryTitleException ||
			throwable instanceof DuplicateAssetListEntryTitleException) {

			return true;
		}

		return super.isSessionErrorException(throwable);
	}

	private UnicodeProperties _getUnicodeProperties(
			AssetListDisplayContext assetListDisplayContext)
		throws IOException {

		AssetListEntry assetListEntry =
			assetListDisplayContext.getAssetListEntry();

		if (assetListEntry == null) {
			return new UnicodeProperties();
		}

		return UnicodePropertiesBuilder.load(
			assetListEntry.getTypeSettings(
				assetListDisplayContext.getSegmentsEntryId())
		).build();
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Reference
	private AssetRendererFactoryClassProvider
		_assetRendererFactoryClassProvider;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private InfoEditURLProviderRegistry _infoEditURLProviderRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

}