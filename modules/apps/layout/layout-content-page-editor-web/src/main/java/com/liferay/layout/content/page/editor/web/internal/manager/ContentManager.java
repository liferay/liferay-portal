/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.manager;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryUsage;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntryUsageLocalService;
import com.liferay.asset.util.AssetHelper;
import com.liferay.asset.util.AssetPublisherAddItemHolder;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.display.url.provider.InfoEditURLProvider;
import com.liferay.info.display.url.provider.InfoEditURLProviderRegistry;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.list.permission.provider.LayoutListPermissionProvider;
import com.liferay.layout.list.permission.provider.LayoutListPermissionProviderRegistry;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.list.retriever.ListObjectReferenceFactory;
import com.liferay.layout.list.retriever.ListObjectReferenceFactoryRegistry;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.RenderRequestFactory;
import com.liferay.portlet.RenderResponseFactory;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuPortletKeys;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ContentManager.class)
public class ContentManager {

	public Set<LayoutDisplayPageObjectProvider<?>>
		getFragmentEntryLinkMappedLayoutDisplayPageObjectProviders(
			FragmentEntryLink fragmentEntryLink) {

		return _getFragmentEntryLinkMappedLayoutDisplayPageObjectProviders(
			fragmentEntryLink, new HashSet<>());
	}

	public Set<LayoutDisplayPageObjectProvider<?>>
		getLayoutMappedLayoutDisplayPageObjectProviders(String layoutData) {

		Set<LayoutDisplayPageObjectProvider<?>>
			layoutDisplayPageObjectProviders = new HashSet<>();

		_getLayoutMappedLayoutDisplayPageObjectProviders(
			LayoutStructure.of(layoutData), layoutDisplayPageObjectProviders,
			new HashSet<>());

		return layoutDisplayPageObjectProviders;
	}

	public Set<LayoutDisplayPageObjectProvider<?>>
			getMappedLayoutDisplayPageObjectProviders(long groupId, long plid)
		throws PortalException {

		Set<LayoutDisplayPageObjectProvider<?>>
			layoutDisplayPageObjectProviders = new HashSet<>();

		Set<String> uniqueLayoutClassedModelUsageKeys = new HashSet<>();

		_getFragmentEntryLinksMappedLayoutDisplayPageObjectProviders(
			groupId, plid, layoutDisplayPageObjectProviders,
			uniqueLayoutClassedModelUsageKeys);
		_getLayoutMappedLayoutDisplayPageObjectProviders(
			LayoutStructureUtil.getLayoutStructure(
				groupId, plid, SegmentsExperienceConstants.KEY_DEFAULT),
			layoutDisplayPageObjectProviders,
			uniqueLayoutClassedModelUsageKeys);

		return layoutDisplayPageObjectProviders;
	}

	public JSONArray getPageContentsJSONArray(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long plid,
			List<String> restrictedItemIds, long segmentsExperienceId)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), plid, segmentsExperienceId);

		return _getPageContentsJSONArray(
			httpServletRequest, httpServletResponse, plid, segmentsExperienceId,
			layoutStructure, restrictedItemIds);
	}

	public JSONArray getPageContentsJSONArray(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long plid,
			long segmentsExperienceId)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), plid, segmentsExperienceId);

		List<String> restrictedItemIds = getRestrictedItemIds(
			httpServletRequest, layoutStructure, themeDisplay);

		return _getPageContentsJSONArray(
			httpServletRequest, httpServletResponse, plid, segmentsExperienceId,
			layoutStructure, restrictedItemIds);
	}

	public List<String> getRestrictedItemIds(
		HttpServletRequest httpServletRequest, LayoutStructure layoutStructure,
		ThemeDisplay themeDisplay) {

		List<String> restrictedItemIds = new ArrayList<>();

		for (FormStyledLayoutStructureItem formStyledLayoutStructureItem :
				layoutStructure.getFormStyledLayoutStructureItems()) {

			if (layoutStructure.isItemMarkedForDeletion(
					formStyledLayoutStructureItem.getItemId()) ||
				(formStyledLayoutStructureItem.getClassNameId() <= 0)) {

				continue;
			}

			String className = formStyledLayoutStructureItem.getClassName();

			if (Validator.isNull(className)) {
				continue;
			}

			InfoPermissionProvider<?> infoPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoPermissionProvider.class, className);

			if ((infoPermissionProvider == null) ||
				(infoPermissionProvider.hasViewPermission(
					themeDisplay.getPermissionChecker()) &&
				 infoPermissionProvider.hasViewPermission(
					 String.valueOf(
						 formStyledLayoutStructureItem.getClassTypeId()),
					 themeDisplay.getScopeGroupId(),
					 PermissionThreadLocal.getPermissionChecker()))) {

				continue;
			}

			restrictedItemIds.add(formStyledLayoutStructureItem.getItemId());
		}

		for (CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem :
					layoutStructure.getCollectionStyledLayoutStructureItems()) {

			JSONObject collectionJSONObject =
				collectionStyledLayoutStructureItem.getCollectionJSONObject();

			if ((collectionJSONObject == null) ||
				(collectionJSONObject.length() <= 0)) {

				continue;
			}

			String type = collectionJSONObject.getString("type");

			LayoutListRetriever<?, ?> layoutListRetriever =
				_layoutListRetrieverRegistry.getLayoutListRetriever(type);

			if (layoutListRetriever == null) {
				continue;
			}

			ListObjectReferenceFactory<?> listObjectReferenceFactory =
				_listObjectReferenceFactoryRegistry.getListObjectReference(
					type);

			if (listObjectReferenceFactory == null) {
				continue;
			}

			ListObjectReference listObjectReference =
				listObjectReferenceFactory.getListObjectReference(
					collectionJSONObject);

			Class<? extends ListObjectReference> listObjectReferenceClass =
				listObjectReference.getClass();

			LayoutListPermissionProvider<ListObjectReference>
				layoutListPermissionProvider =
					(LayoutListPermissionProvider<ListObjectReference>)
						_layoutListPermissionProviderRegistry.
							getLayoutListPermissionProvider(
								listObjectReferenceClass.getName());

			if ((layoutListPermissionProvider == null) ||
				layoutListPermissionProvider.hasPermission(
					themeDisplay.getPermissionChecker(), listObjectReference,
					ActionKeys.VIEW)) {

				continue;
			}

			restrictedItemIds.add(
				collectionStyledLayoutStructureItem.getItemId());
		}

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		for (LayoutStructureItem layoutStructureItem :
				fragmentLayoutStructureItems.values()) {

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			if (layoutStructure.isItemMarkedForDeletion(
					fragmentStyledLayoutStructureItem.getItemId())) {

				continue;
			}

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if (fragmentEntryLink == null) {
				continue;
			}

			String rendererKey = fragmentEntryLink.getRendererKey();

			if (Validator.isNull(rendererKey)) {
				continue;
			}

			FragmentRenderer fragmentRenderer =
				_fragmentRendererRegistry.getFragmentRenderer(rendererKey);

			if ((fragmentRenderer == null) ||
				fragmentRenderer.hasViewPermission(
					new DefaultFragmentRendererContext(fragmentEntryLink),
					httpServletRequest)) {

				continue;
			}

			restrictedItemIds.add(
				fragmentStyledLayoutStructureItem.getItemId());
		}

		return restrictedItemIds;
	}

	private LiferayRenderRequest _createRenderRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Portlet portlet = _portletLocalService.getPortletById(
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET);
		ServletContext servletContext =
			(ServletContext)httpServletRequest.getAttribute(WebKeys.CTX);

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LiferayRenderRequest liferayRenderRequest = RenderRequestFactory.create(
			httpServletRequest, portlet,
			PortletInstanceFactoryUtil.create(portlet, servletContext),
			portletConfig.getPortletContext(), WindowState.NORMAL,
			PortletMode.VIEW,
			_portletPreferencesLocalService.getStrictPreferences(
				PortletPreferencesFactoryUtil.getPortletPreferencesIds(
					httpServletRequest, portlet.getPortletId())),
			themeDisplay.getPlid());

		liferayRenderRequest.setPortletRequestDispatcherRequest(
			httpServletRequest);

		liferayRenderRequest.defineObjects(
			portletConfig,
			RenderResponseFactory.create(
				httpServletResponse, liferayRenderRequest));

		return liferayRenderRequest;
	}

	private boolean _exists(AssetListEntryUsage assetListEntryUsage) {
		if (Objects.equals(
				assetListEntryUsage.getClassName(),
				AssetListEntry.class.getName())) {

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.fetchAssetListEntry(
					GetterUtil.getLong(assetListEntryUsage.getKey()));

			if (assetListEntry != null) {
				return true;
			}
		}

		if (Objects.equals(
				assetListEntryUsage.getClassName(),
				InfoCollectionProvider.class.getName())) {

			InfoCollectionProvider<?> infoCollectionProvider =
				_infoItemServiceRegistry.getInfoItemService(
					InfoCollectionProvider.class, assetListEntryUsage.getKey());

			if (infoCollectionProvider == null) {
				infoCollectionProvider =
					_infoItemServiceRegistry.getInfoItemService(
						RelatedInfoItemCollectionProvider.class,
						assetListEntryUsage.getKey());
			}

			if (infoCollectionProvider != null) {
				return true;
			}
		}

		return false;
	}

	private String _generateUniqueLayoutClassedModelUsageKey(
		AssetListEntryUsage assetListEntryUsage) {

		return assetListEntryUsage.getClassNameId() + StringPool.DASH +
			assetListEntryUsage.getKey();
	}

	private String _generateUniqueLayoutClassedModelUsageKey(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		return _generateUniqueLayoutClassedModelUsageKey(
			layoutClassedModelUsage.getClassNameId(),
			layoutClassedModelUsage.getClassPK(),
			layoutClassedModelUsage.getClassedModelExternalReferenceCode());
	}

	private String _generateUniqueLayoutClassedModelUsageKey(
		long classNameId, long classPK, String externalReferenceCode) {

		return StringBundler.concat(
			classNameId, StringPool.DASH, classPK, StringPool.DASH,
			externalReferenceCode);
	}

	private JSONObject _getActionsJSONObject(
		LayoutClassedModelUsage layoutClassedModelUsage,
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider,
		ThemeDisplay themeDisplay, HttpServletRequest httpServletRequest) {

		String className = layoutClassedModelUsage.getClassName();

		InfoItemPermissionProvider<?> infoItemPermissionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemPermissionProvider.class, className);

		InfoItemReference infoItemReference = _getInfoItemIdentifier(
			layoutClassedModelUsage.getClassName(),
			layoutClassedModelUsage.getClassPK(),
			layoutClassedModelUsage.getClassedModelExternalReferenceCode());

		boolean hasUpdatePermission = false;

		try {
			hasUpdatePermission = infoItemPermissionProvider.hasPermission(
				themeDisplay.getPermissionChecker(), infoItemReference,
				ActionKeys.UPDATE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"An error occurred while getting mapped content with ",
						"class name ", className, " and class PK ",
						layoutClassedModelUsage.getClassPK()),
					exception);
			}
		}

		boolean finalHasUpdatePermission = hasUpdatePermission;

		return JSONUtil.put(
			"editImage",
			() -> {
				if (!finalHasUpdatePermission ||
					!Objects.equals(className, FileEntry.class.getName())) {

					return null;
				}

				FileEntry fileEntry =
					(FileEntry)
						layoutDisplayPageObjectProvider.getDisplayObject();

				PortletResponse portletResponse =
					(PortletResponse)httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_RESPONSE);

				return JSONUtil.put(
					"editImageURL",
					PortletURLBuilder.createActionURL(
						_portal.getLiferayPortletResponse(portletResponse),
						DLPortletKeys.DOCUMENT_LIBRARY_ADMIN
					).setActionName(
						"/document_library/edit_file_entry_image_editor"
					).buildString()
				).put(
					"fileEntryId", fileEntry.getFileEntryId()
				).put(
					"previewURL",
					_dlURLHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), themeDisplay,
						StringPool.BLANK)
				);
			}
		).put(
			"editURL",
			() -> {
				if (!finalHasUpdatePermission) {
					return null;
				}

				InfoEditURLProvider<Object> infoEditURLProvider =
					_infoEditURLProviderRegistry.getInfoEditURLProvider(
						className);

				if (infoEditURLProvider == null) {
					return null;
				}

				PortletResponse portletResponse =
					(PortletResponse)httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_RESPONSE);

				return _layoutLockManager.getUnlockDraftLayoutURL(
					_portal.getLiferayPortletResponse(portletResponse),
					() -> infoEditURLProvider.getURL(
						layoutDisplayPageObjectProvider.getDisplayObject(),
						httpServletRequest));
			}
		).put(
			"permissionsURL",
			() -> {
				if (!infoItemPermissionProvider.hasPermission(
						themeDisplay.getPermissionChecker(), infoItemReference,
						ActionKeys.PERMISSIONS)) {

					return null;
				}

				return PermissionsURLTag.doTag(
					StringPool.BLANK, className,
					HtmlUtil.escape(
						layoutDisplayPageObjectProvider.getTitle(
							themeDisplay.getLocale())),
					null, String.valueOf(layoutClassedModelUsage.getClassPK()),
					LiferayWindowState.POP_UP.toString(), null,
					httpServletRequest);
			}
		).put(
			"viewUsagesURL",
			() -> {
				if (!infoItemPermissionProvider.hasPermission(
						themeDisplay.getPermissionChecker(), infoItemReference,
						ActionKeys.VIEW)) {

					return null;
				}

				return PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						httpServletRequest,
						ContentPageEditorPortletKeys.
							CONTENT_PAGE_EDITOR_PORTLET,
						PortletRequest.RENDER_PHASE)
				).setMVCPath(
					"/view_layout_classed_model_usages.jsp"
				).setParameter(
					"className", className
				).setParameter(
					"classPK", layoutClassedModelUsage.getClassPK()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString();
			}
		);
	}

	private String _getAssetEntryListSubtypeLabel(
		AssetListEntry assetListEntry, Locale locale) {

		String typeLabel = _resourceActions.getModelResource(
			locale, assetListEntry.getAssetEntryType());

		String subtypeLabel = _getSubtypeLabel(assetListEntry, locale);

		if (Validator.isNull(subtypeLabel)) {
			return typeLabel;
		}

		return typeLabel + " - " + subtypeLabel;
	}

	private JSONObject _getAssetListEntryActionsJSONObject(
		AssetListEntry assetListEntry, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String redirect) {

		return JSONUtil.put(
			"addItems",
			() -> {
				try {
					JSONArray addItemsJSONArray =
						_getAssetListEntryAddItemsJSONArray(
							assetListEntry, httpServletRequest,
							httpServletResponse);

					if ((addItemsJSONArray != null) &&
						(addItemsJSONArray.length() > 0)) {

						return addItemsJSONArray;
					}
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				return null;
			}
		).put(
			"editURL",
			() -> _getAssetListEntryEditURL(
				assetListEntry, httpServletRequest, redirect)
		).put(
			"permissionsURL",
			() -> _getAssetListEntryPermissionsURL(
				assetListEntry, httpServletRequest)
		).put(
			"viewItemsURL",
			() -> _getViewItemsURL(
				String.valueOf(assetListEntry.getAssetListEntryId()),
				InfoListItemSelectorReturnType.class.getName(),
				httpServletRequest, redirect)
		);
	}

	private JSONArray _getAssetListEntryAddItemsJSONArray(
			AssetListEntry assetListEntry,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		JSONArray addItemsJSONArray = _jsonFactory.createJSONArray();

		for (AssetPublisherAddItemHolder assetPublisherAddItemHolder :
				_getAssetPublisherAddItemHolders(
					assetListEntry, httpServletRequest, httpServletResponse)) {

			addItemsJSONArray.put(
				JSONUtil.put(
					"href", assetPublisherAddItemHolder.getPortletURL()
				).put(
					"label", assetPublisherAddItemHolder.getModelResource()
				));
		}

		return addItemsJSONArray;
	}

	private String _getAssetListEntryEditURL(
		AssetListEntry assetListEntry, HttpServletRequest httpServletRequest,
		String redirect) {

		try {
			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, AssetListEntry.class.getName(),
					PortletProvider.Action.EDIT)
			).setRedirect(
				redirect
			).setBackURL(
				redirect
			).setParameter(
				"assetListEntryId", assetListEntry.getAssetListEntryId()
			).setParameter(
				"backURLTitle",
				() -> {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)httpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					Layout layout = themeDisplay.getLayout();

					return layout.getName(themeDisplay.getLocale());
				}
			).buildString();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	private String _getAssetListEntryPermissionsURL(
		AssetListEntry assetListEntry, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (_assetListEntryModelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), assetListEntry,
					ActionKeys.PERMISSIONS)) {

				return PermissionsURLTag.doTag(
					StringPool.BLANK, AssetListEntry.class.getName(),
					HtmlUtil.escape(assetListEntry.getTitle()), null,
					String.valueOf(assetListEntry.getAssetListEntryId()),
					LiferayWindowState.POP_UP.toString(), null,
					httpServletRequest);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private List<AssetPublisherAddItemHolder> _getAssetPublisherAddItemHolders(
			AssetListEntry assetListEntry,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		AssetEntryQuery assetEntryQuery =
			_assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry,
				_segmentsEntryRetriever.getSegmentsEntryIds(
					_portal.getScopeGroupId(httpServletRequest),
					_portal.getUserId(httpServletRequest),
					_requestContextMapper.map(httpServletRequest), new long[0]),
				StringPool.BLANK);

		long[] allTagIds = assetEntryQuery.getAllTagIds();

		String[] allTagNames = new String[allTagIds.length];

		int index = 0;

		for (long tagId : allTagIds) {
			AssetTag assetTag = _assetTagLocalService.getAssetTag(tagId);

			allTagNames[index++] = assetTag.getName();
		}

		LiferayPortletRequest liferayPortletRequest = _createRenderRequest(
			httpServletRequest, httpServletResponse);

		PortletResponse portletResponse =
			(PortletResponse)liferayPortletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(portletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _assetHelper.getAssetPublisherAddItemHolders(
			liferayPortletRequest, liferayPortletResponse,
			assetListEntry.getGroupId(), assetEntryQuery.getClassNameIds(),
			assetEntryQuery.getClassTypeIds(),
			assetEntryQuery.getAllCategoryIds(), allTagNames,
			_getRedirect(
				assetListEntry.getAssetListEntryId(), httpServletRequest,
				themeDisplay));
	}

	private Set<LayoutDisplayPageObjectProvider<?>>
		_getFragmentEntryLinkMappedLayoutDisplayPageObjectProviders(
			FragmentEntryLink fragmentEntryLink,
			Set<String> uniqueLayoutClassedModelUsageKeys) {

		JSONObject editableValuesJSONObject = null;

		try {
			editableValuesJSONObject = _jsonFactory.createJSONObject(
				fragmentEntryLink.getEditableValues());
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to create JSON object from " +
						fragmentEntryLink.getEditableValues(),
					jsonException);
			}

			return Collections.emptySet();
		}

		Set<LayoutDisplayPageObjectProvider<?>>
			layoutDisplayPageObjectProviders = new HashSet<>();

		for (String key : editableValuesJSONObject.keySet()) {
			JSONObject editableProcessorJSONObject =
				editableValuesJSONObject.getJSONObject(key);

			if (editableProcessorJSONObject == null) {
				continue;
			}

			for (String editableKey : editableProcessorJSONObject.keySet()) {
				JSONObject editableJSONObject =
					editableProcessorJSONObject.getJSONObject(editableKey);

				if (editableJSONObject == null) {
					continue;
				}

				_getLocalizedLayoutDisplayPageObjectProviders(
					editableJSONObject, layoutDisplayPageObjectProviders,
					uniqueLayoutClassedModelUsageKeys);

				JSONObject configJSONObject = editableJSONObject.getJSONObject(
					"config");

				if ((configJSONObject != null) &&
					(configJSONObject.length() > 0)) {

					_getLayoutDisplayPageObjectProvider(
						configJSONObject, layoutDisplayPageObjectProviders,
						uniqueLayoutClassedModelUsageKeys);

					_getLocalizedLayoutDisplayPageObjectProviders(
						configJSONObject, layoutDisplayPageObjectProviders,
						uniqueLayoutClassedModelUsageKeys);

					JSONObject mappedActionJSONObject =
						configJSONObject.getJSONObject("mappedAction");

					if ((mappedActionJSONObject != null) &&
						(mappedActionJSONObject.length() > 0)) {

						_getLayoutDisplayPageObjectProvider(
							mappedActionJSONObject,
							layoutDisplayPageObjectProviders,
							uniqueLayoutClassedModelUsageKeys);
					}
				}

				JSONObject itemSelectorJSONObject =
					editableJSONObject.getJSONObject("itemSelector");

				if ((itemSelectorJSONObject != null) &&
					(itemSelectorJSONObject.length() > 0)) {

					_getLayoutDisplayPageObjectProvider(
						itemSelectorJSONObject,
						layoutDisplayPageObjectProviders,
						uniqueLayoutClassedModelUsageKeys);
				}

				_getLayoutDisplayPageObjectProvider(
					editableJSONObject, layoutDisplayPageObjectProviders,
					uniqueLayoutClassedModelUsageKeys);
			}
		}

		return layoutDisplayPageObjectProviders;
	}

	private void _getFragmentEntryLinksMappedLayoutDisplayPageObjectProviders(
		long groupId, long plid,
		Set<LayoutDisplayPageObjectProvider<?>>
			layoutDisplayPageObjectProviders,
		Set<String> uniqueLayoutClassedModelUsageKeys) {

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				groupId, plid);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			layoutDisplayPageObjectProviders.addAll(
				_getFragmentEntryLinkMappedLayoutDisplayPageObjectProviders(
					fragmentEntryLink, uniqueLayoutClassedModelUsageKeys));
		}
	}

	private String _getIcon(String className, long classPK) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_infoSearchClassMapperRegistry.getSearchClassName(className));

		if (assetRendererFactory == null) {
			return "web-content";
		}

		try {
			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(classPK);

			if (assetRenderer == null) {
				return "web-content";
			}

			return assetRenderer.getIconCssClass();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"An error occurred while getting mapped content with ",
						"class PK ", classPK, " and class name ", className),
					exception);
			}
		}

		return "web-content";
	}

	private String _getInfoCollectionProviderSubtypeLabel(
		long groupId, InfoCollectionProvider<?> infoCollectionProvider,
		Locale locale) {

		String className = infoCollectionProvider.getCollectionItemClassName();

		if (Validator.isNull(className)) {
			return StringPool.BLANK;
		}

		if (!(infoCollectionProvider instanceof
				SingleFormVariationInfoCollectionProvider)) {

			return _resourceActions.getModelResource(locale, className);
		}

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, className);

		if (infoItemFormVariationsProvider == null) {
			return _resourceActions.getModelResource(locale, className);
		}

		SingleFormVariationInfoCollectionProvider<?>
			singleFormVariationInfoCollectionProvider =
				(SingleFormVariationInfoCollectionProvider<?>)
					infoCollectionProvider;

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				groupId,
				singleFormVariationInfoCollectionProvider.
					getFormVariationKey());

		if (infoItemFormVariation == null) {
			return _resourceActions.getModelResource(locale, className);
		}

		return _resourceActions.getModelResource(locale, className) + " - " +
			infoItemFormVariation.getLabel(locale);
	}

	private InfoItemReference _getInfoItemIdentifier(
		String className, long classPK, String externalReferenceCode) {

		InfoItemIdentifier infoItemIdentifier = null;

		if (classPK > 0) {
			infoItemIdentifier = new ClassPKInfoItemIdentifier(classPK);
		}
		else {
			infoItemIdentifier = new ERCInfoItemIdentifier(
				externalReferenceCode);
		}

		return new InfoItemReference(className, infoItemIdentifier);
	}

	private JSONArray _getLayoutClassedModelPageContentsJSONArray(
		HttpServletRequest httpServletRequest, LayoutStructure layoutStructure,
		long plid, List<String> hiddenItemIds, List<String> restrictedItemIds,
		long segmentsExperienceId) {

		JSONArray mappedContentsJSONArray = _jsonFactory.createJSONArray();

		Set<String> uniqueLayoutClassedModelUsageKeys = new HashSet<>();

		List<String> restrictedPortletIds = _getRestrictedPortletIds(
			layoutStructure, hiddenItemIds);

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(plid);

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			if (uniqueLayoutClassedModelUsageKeys.contains(
					_generateUniqueLayoutClassedModelUsageKey(
						layoutClassedModelUsage))) {

				continue;
			}

			boolean restricted = false;

			if (layoutClassedModelUsage.getContainerType() ==
					_portal.getClassNameId(FragmentEntryLink.class.getName())) {

				FragmentEntryLink fragmentEntryLink =
					_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
						GetterUtil.getLong(
							layoutClassedModelUsage.getContainerKey()));

				if (fragmentEntryLink == null) {
					_layoutClassedModelUsageLocalService.
						deleteLayoutClassedModelUsage(layoutClassedModelUsage);

					continue;
				}

				if (!Objects.equals(
						fragmentEntryLink.getSegmentsExperienceId(),
						segmentsExperienceId)) {

					continue;
				}

				LayoutStructureItem layoutStructureItem =
					layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
						fragmentEntryLink.getFragmentEntryLinkId());

				if ((layoutStructureItem == null) ||
					fragmentEntryLink.isDeleted() ||
					hiddenItemIds.contains(layoutStructureItem.getItemId())) {

					continue;
				}

				restricted = restrictedItemIds.contains(
					layoutStructureItem.getItemId());
			}

			if ((layoutClassedModelUsage.getContainerType() ==
					_portal.getClassNameId(Portlet.class.getName())) &&
				(layoutStructure.isPortletMarkedForDeletion(
					layoutClassedModelUsage.getContainerKey()) ||
				 restrictedPortletIds.contains(
					 layoutClassedModelUsage.getContainerKey()))) {

				continue;
			}

			LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
				_layoutDisplayPageProviderRegistry.
					getLayoutDisplayPageProviderByClassName(
						layoutClassedModelUsage.getClassName());

			if (layoutDisplayPageProvider == null) {
				_layoutClassedModelUsageLocalService.
					deleteLayoutClassedModelUsage(layoutClassedModelUsage);

				continue;
			}

			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_getInfoItemIdentifier(
						layoutClassedModelUsage.getClassName(),
						layoutClassedModelUsage.getClassPK(),
						layoutClassedModelUsage.
							getClassedModelExternalReferenceCode()));

			if (layoutDisplayPageObjectProvider == null) {
				_layoutClassedModelUsageLocalService.
					deleteLayoutClassedModelUsage(layoutClassedModelUsage);

				continue;
			}

			mappedContentsJSONArray.put(
				_getPageContentJSONObject(
					layoutClassedModelUsage, layoutDisplayPageObjectProvider,
					httpServletRequest, restricted));

			uniqueLayoutClassedModelUsageKeys.add(
				_generateUniqueLayoutClassedModelUsageKey(
					layoutClassedModelUsage));
		}

		return mappedContentsJSONArray;
	}

	private void _getLayoutDisplayPageObjectProvider(
		JSONObject jsonObject,
		Set<LayoutDisplayPageObjectProvider<?>>
			layoutDisplayPageObjectProviders,
		Set<String> uniqueLayoutClassedModelUsageKeys) {

		if (!jsonObject.has("classNameId") ||
			(!jsonObject.has("classPK") &&
			 !jsonObject.has("externalReferenceCode"))) {

			return;
		}

		long classNameId = jsonObject.getLong("classNameId");

		if (classNameId <= 0) {
			return;
		}

		String className = _portal.fetchClassName(classNameId);

		if (Validator.isNull(className)) {
			return;
		}

		long classPK = jsonObject.getLong("classPK");
		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");

		String uniqueLayoutClassedModelUsageKey =
			_generateUniqueLayoutClassedModelUsageKey(
				classNameId, classPK, externalReferenceCode);

		if (((classPK <= 0) && Validator.isNull(externalReferenceCode)) ||
			uniqueLayoutClassedModelUsageKeys.contains(
				uniqueLayoutClassedModelUsageKey)) {

			return;
		}

		className = _infoSearchClassMapperRegistry.getClassName(className);

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(className);

		if (layoutDisplayPageProvider == null) {
			return;
		}

		uniqueLayoutClassedModelUsageKeys.add(uniqueLayoutClassedModelUsageKey);

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_getInfoItemIdentifier(
					className, classPK, externalReferenceCode));

		if (layoutDisplayPageObjectProvider == null) {
			return;
		}

		layoutDisplayPageObjectProviders.add(layoutDisplayPageObjectProvider);
	}

	private void _getLayoutMappedLayoutDisplayPageObjectProviders(
		LayoutStructure layoutStructure,
		Set<LayoutDisplayPageObjectProvider<?>>
			layoutDisplayPageObjectProviders,
		Set<String> uniqueLayoutClassedModelUsageKeys) {

		for (LayoutStructureItem layoutStructureItem :
				layoutStructure.getLayoutStructureItems()) {

			if (!(layoutStructureItem instanceof
					ContainerStyledLayoutStructureItem) ||
				layoutStructure.isItemMarkedForDeletion(
					layoutStructureItem.getItemId())) {

				continue;
			}

			ContainerStyledLayoutStructureItem
				containerStyledLayoutStructureItem =
					(ContainerStyledLayoutStructureItem)layoutStructureItem;

			JSONObject backgroundImageJSONObject =
				containerStyledLayoutStructureItem.
					getBackgroundImageJSONObject();

			if (backgroundImageJSONObject != null) {
				_getLayoutDisplayPageObjectProvider(
					backgroundImageJSONObject, layoutDisplayPageObjectProviders,
					uniqueLayoutClassedModelUsageKeys);
			}

			JSONObject linkJSONObject =
				containerStyledLayoutStructureItem.getLinkJSONObject();

			if (linkJSONObject != null) {
				_getLayoutDisplayPageObjectProvider(
					linkJSONObject, layoutDisplayPageObjectProviders,
					uniqueLayoutClassedModelUsageKeys);
				_getLocalizedLayoutDisplayPageObjectProviders(
					linkJSONObject, layoutDisplayPageObjectProviders,
					uniqueLayoutClassedModelUsageKeys);
			}
		}
	}

	private void _getLocalizedLayoutDisplayPageObjectProviders(
		JSONObject jsonObject,
		Set<LayoutDisplayPageObjectProvider<?>>
			layoutDisplayPageObjectProviders,
		Set<String> uniqueLayoutClassedModelUsageKeys) {

		Set<Locale> locales = _language.getAvailableLocales();

		for (Locale locale : locales) {
			JSONObject localizableJSONObject = jsonObject.getJSONObject(
				LocaleUtil.toLanguageId(locale));

			if ((localizableJSONObject == null) ||
				(localizableJSONObject.length() == 0)) {

				continue;
			}

			_getLayoutDisplayPageObjectProvider(
				localizableJSONObject, layoutDisplayPageObjectProviders,
				uniqueLayoutClassedModelUsageKeys);
		}
	}

	private JSONObject _getPageContentJSONObject(
		AssetListEntryUsage assetListEntryUsage,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String redirect,
		List<String> restrictedItemIds) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONObject mappedContentJSONObject = JSONUtil.put(
			"className", assetListEntryUsage.getClassName()
		).put(
			"classNameId", assetListEntryUsage.getClassNameId()
		).put(
			"classPK", assetListEntryUsage.getKey()
		).put(
			"icon", "list-ul"
		).put(
			"isRestricted",
			() -> {
				if ((assetListEntryUsage.getContainerType() ==
						_portal.getClassNameId(
							CollectionStyledLayoutStructureItem.class.
								getName())) &&
					restrictedItemIds.contains(
						assetListEntryUsage.getContainerKey())) {

					return true;
				}

				return false;
			}
		).put(
			"type", _language.get(httpServletRequest, "collection")
		);

		if (Objects.equals(
				assetListEntryUsage.getClassName(),
				AssetListEntry.class.getName())) {

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.fetchAssetListEntry(
					GetterUtil.getLong(assetListEntryUsage.getKey()));

			if (assetListEntry != null) {
				mappedContentJSONObject.put(
					"actions",
					_getAssetListEntryActionsJSONObject(
						assetListEntry, httpServletRequest, httpServletResponse,
						redirect)
				).put(
					"subtype",
					_getAssetEntryListSubtypeLabel(
						assetListEntry, themeDisplay.getLocale())
				).put(
					"title", assetListEntry.getTitle()
				);
			}
		}

		if (!Objects.equals(
				assetListEntryUsage.getClassName(),
				InfoCollectionProvider.class.getName())) {

			return mappedContentJSONObject;
		}

		InfoCollectionProvider<?> infoCollectionProvider =
			_infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class, assetListEntryUsage.getKey());

		if (infoCollectionProvider == null) {
			infoCollectionProvider =
				_infoItemServiceRegistry.getInfoItemService(
					RelatedInfoItemCollectionProvider.class,
					assetListEntryUsage.getKey());
		}

		if (infoCollectionProvider == null) {
			return mappedContentJSONObject;
		}

		InfoCollectionProvider<?> finalInfoCollectionProvider =
			infoCollectionProvider;

		return mappedContentJSONObject.put(
			"actions",
			() -> {
				if (finalInfoCollectionProvider instanceof
						RelatedInfoItemCollectionProvider) {

					return null;
				}

				return JSONUtil.put(
					"viewItemsURL",
					() -> _getViewItemsURL(
						finalInfoCollectionProvider.getKey(),
						InfoListProviderItemSelectorReturnType.class.getName(),
						httpServletRequest, redirect));
			}
		).put(
			"subtype",
			_getInfoCollectionProviderSubtypeLabel(
				themeDisplay.getScopeGroupId(), infoCollectionProvider,
				themeDisplay.getLocale())
		).put(
			"title", infoCollectionProvider.getLabel(themeDisplay.getLocale())
		);
	}

	private JSONObject _getPageContentJSONObject(
		LayoutClassedModelUsage layoutClassedModelUsage,
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider,
		HttpServletRequest httpServletRequest, boolean restricted) {

		if (restricted) {
			return JSONUtil.put(
				"actions", _jsonFactory.createJSONObject()
			).put(
				"className", layoutClassedModelUsage.getClassName()
			).put(
				"classNameId", layoutClassedModelUsage.getClassNameId()
			).put(
				"classPK", layoutClassedModelUsage.getClassPK()
			).put(
				"classTypeId", layoutDisplayPageObjectProvider.getClassTypeId()
			).put(
				"externalReferenceCode",
				layoutDisplayPageObjectProvider.getExternalReferenceCode()
			).put(
				"icon", StringPool.BLANK
			).put(
				"isRestricted", true
			).put(
				"status", _jsonFactory.createJSONObject()
			).put(
				"subtype", StringPool.BLANK
			).put(
				"title", StringPool.BLANK
			).put(
				"type", _language.get(httpServletRequest, "restricted-content")
			);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"actions",
			_getActionsJSONObject(
				layoutClassedModelUsage, layoutDisplayPageObjectProvider,
				themeDisplay, httpServletRequest)
		).put(
			"className", layoutClassedModelUsage.getClassName()
		).put(
			"classNameId", layoutClassedModelUsage.getClassNameId()
		).put(
			"classPK", layoutClassedModelUsage.getClassPK()
		).put(
			"classTypeId", layoutDisplayPageObjectProvider.getClassTypeId()
		).put(
			"externalReferenceCode",
			layoutDisplayPageObjectProvider.getExternalReferenceCode()
		).put(
			"icon",
			_getIcon(
				layoutClassedModelUsage.getClassName(),
				layoutClassedModelUsage.getClassPK())
		).put(
			"isRestricted", false
		).put(
			"status", _getStatusJSONObject(layoutClassedModelUsage)
		).put(
			"subtype",
			_getSubtype(
				layoutClassedModelUsage.getClassName(),
				layoutDisplayPageObjectProvider.getClassTypeId(),
				themeDisplay.getLocale())
		).put(
			"title",
			layoutDisplayPageObjectProvider.getTitle(themeDisplay.getLocale())
		).put(
			"type",
			_resourceActions.getModelResource(
				themeDisplay.getLocale(),
				layoutClassedModelUsage.getClassName())
		);
	}

	private JSONArray _getPageContentsJSONArray(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long plid,
			long segmentsExperienceId, LayoutStructure layoutStructure,
			List<String> restrictedItemIds)
		throws PortalException {

		List<String> hiddenItemIds = new ArrayList<>();

		for (String restrictedItemId : restrictedItemIds) {
			hiddenItemIds.addAll(
				LayoutStructureItemUtil.getChildrenItemIds(
					restrictedItemId, layoutStructure));
		}

		return JSONUtil.concat(
			_getLayoutClassedModelPageContentsJSONArray(
				httpServletRequest, layoutStructure, plid, hiddenItemIds,
				restrictedItemIds, segmentsExperienceId),
			_getPageContentsJSONArray(
				hiddenItemIds, httpServletRequest, httpServletResponse,
				layoutStructure, plid, restrictedItemIds));
	}

	private JSONArray _getPageContentsJSONArray(
			List<String> hiddenItemIds, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			LayoutStructure layoutStructure, long plid,
			List<String> restrictedItemIds)
		throws PortalException {

		JSONArray mappedContentsJSONArray = _jsonFactory.createJSONArray();

		Set<String> uniqueAssetListEntryUsagesKeys = new HashSet<>();

		for (AssetListEntryUsage assetListEntryUsage :
				_assetListEntryUsageLocalService.getAssetEntryListUsagesByPlid(
					plid)) {

			if (!_exists(assetListEntryUsage)) {
				continue;
			}

			String uniqueKey = _generateUniqueLayoutClassedModelUsageKey(
				assetListEntryUsage);

			if (uniqueAssetListEntryUsagesKeys.contains(uniqueKey) ||
				_isCollectionStyledLayoutStructureItemDeletedOrHidden(
					assetListEntryUsage, hiddenItemIds, layoutStructure) ||
				_isFragmentEntryLinkDeletedOrHidden(
					assetListEntryUsage, hiddenItemIds, layoutStructure)) {

				continue;
			}

			mappedContentsJSONArray.put(
				_getPageContentJSONObject(
					assetListEntryUsage, httpServletRequest,
					httpServletResponse, _getRedirect(httpServletRequest),
					restrictedItemIds));

			uniqueAssetListEntryUsagesKeys.add(uniqueKey);
		}

		return mappedContentsJSONArray;
	}

	private String _getRedirect(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		try {
			return HttpComponentsUtil.setParameter(
				_portal.getLayoutRelativeURL(layout, themeDisplay), "p_l_mode",
				Constants.EDIT);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return themeDisplay.getURLCurrent();
	}

	private String _getRedirect(
			long assetListEntryId, HttpServletRequest httpServletRequest,
			ThemeDisplay themeDisplay)
		throws Exception {

		String currentURL = HttpComponentsUtil.addParameter(
			_portal.getLayoutRelativeURL(
				themeDisplay.getLayout(), themeDisplay),
			"p_l_mode", Constants.EDIT);

		return HttpComponentsUtil.addParameter(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest,
					ProductNavigationControlMenuPortletKeys.
						PRODUCT_NAVIGATION_CONTROL_MENU,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"/layout_content_page_editor/add_collection_item"
			).setRedirect(
				currentURL
			).setParameter(
				"assetListEntryId", assetListEntryId
			).buildString(),
			"portletResource",
			ProductNavigationControlMenuPortletKeys.
				PRODUCT_NAVIGATION_CONTROL_MENU);
	}

	private List<String> _getRestrictedPortletIds(
		LayoutStructure layoutStructure, List<String> hiddenItemIds) {

		if (hiddenItemIds.isEmpty()) {
			return Collections.emptyList();
		}

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Map<String, List<String>> portletIds = new HashMap<>();

		for (Map.Entry<Long, LayoutStructureItem> entry :
				fragmentLayoutStructureItems.entrySet()) {

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)entry.getValue();

			if (layoutStructure.isItemMarkedForDeletion(
					fragmentStyledLayoutStructureItem.getItemId())) {

				continue;
			}

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					GetterUtil.getLong(entry.getKey()));

			if ((fragmentEntryLink == null) || fragmentEntryLink.isDeleted()) {
				continue;
			}

			for (String portletId :
					_portletRegistry.getFragmentEntryLinkPortletIds(
						fragmentEntryLink)) {

				List<String> itemIds = portletIds.computeIfAbsent(
					portletId, key -> new ArrayList<>());

				itemIds.add(fragmentStyledLayoutStructureItem.getItemId());
			}
		}

		List<String> restrictedPortletIds = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry : portletIds.entrySet()) {
			boolean restrictedPortletId = true;

			for (String itemId : entry.getValue()) {
				if (!hiddenItemIds.contains(itemId)) {
					restrictedPortletId = false;

					break;
				}
			}

			if (restrictedPortletId) {
				restrictedPortletIds.add(entry.getKey());
			}
		}

		return restrictedPortletIds;
	}

	private JSONObject _getStatusJSONObject(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				layoutClassedModelUsage.getClassName());

		if (assetRendererFactory == null) {
			return JSONUtil.put(
				"hasApprovedVersion", false
			).put(
				"label",
				WorkflowConstants.getStatusLabel(
					WorkflowConstants.STATUS_APPROVED)
			).put(
				"style",
				WorkflowConstants.getStatusStyle(
					WorkflowConstants.STATUS_APPROVED)
			);
		}

		boolean hasApprovedVersion = false;

		try {
			AssetRenderer<?> latestAssetRenderer =
				assetRendererFactory.getAssetRenderer(
					layoutClassedModelUsage.getClassPK(),
					AssetRendererFactory.TYPE_LATEST);

			if (latestAssetRenderer.getStatus() !=
					WorkflowConstants.STATUS_APPROVED) {

				AssetRenderer<?> assetRenderer =
					assetRendererFactory.getAssetRenderer(
						layoutClassedModelUsage.getClassPK(),
						AssetRendererFactory.TYPE_LATEST_APPROVED);

				if (assetRenderer.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) {

					hasApprovedVersion = true;
				}
			}

			return JSONUtil.put(
				"hasApprovedVersion", hasApprovedVersion
			).put(
				"label",
				WorkflowConstants.getStatusLabel(
					latestAssetRenderer.getStatus())
			).put(
				"style",
				WorkflowConstants.getStatusStyle(
					latestAssetRenderer.getStatus())
			);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"An error occurred while getting mapped content with ",
						"class name ", layoutClassedModelUsage.getClassName(),
						" and class PK ", layoutClassedModelUsage.getClassPK()),
					exception);
			}
		}

		return JSONUtil.put(
			"hasApprovedVersion", false
		).put(
			"label",
			WorkflowConstants.getStatusLabel(WorkflowConstants.STATUS_APPROVED)
		).put(
			"style",
			WorkflowConstants.getStatusStyle(WorkflowConstants.STATUS_APPROVED)
		);
	}

	private String _getSubtype(
		String className, long classTypeId, Locale locale) {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_infoSearchClassMapperRegistry.getSearchClassName(className));

		if (assetRendererFactory == null) {
			return StringPool.BLANK;
		}

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		try {
			ClassType classType = classTypeReader.getClassType(
				classTypeId, locale);

			return classType.getName();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	private String _getSubtypeLabel(
		AssetListEntry assetListEntry, Locale locale) {

		long classTypeId = GetterUtil.getLong(
			assetListEntry.getAssetEntrySubtype(), -1);

		if (classTypeId < 0) {
			return StringPool.BLANK;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_infoSearchClassMapperRegistry.getSearchClassName(
					assetListEntry.getAssetEntryType()));

		if ((assetRendererFactory == null) ||
			!assetRendererFactory.isSupportsClassTypes()) {

			return StringPool.BLANK;
		}

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		try {
			ClassType classType = classTypeReader.getClassType(
				classTypeId, locale);

			return classType.getName();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	private String _getViewItemsURL(
		String collectionPK, String collectionType,
		HttpServletRequest httpServletRequest, String redirect) {

		try {
			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, AssetListEntry.class.getName(),
					PortletProvider.Action.VIEW)
			).setRedirect(
				redirect
			).setParameter(
				"collectionPK", collectionPK
			).setParameter(
				"collectionType", collectionType
			).setParameter(
				"showActions", true
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private boolean _isCollectionStyledLayoutStructureItemDeletedOrHidden(
		AssetListEntryUsage assetListEntryUsage, List<String> hiddenItemIds,
		LayoutStructure layoutStructure) {

		if (assetListEntryUsage.getContainerType() != _portal.getClassNameId(
				CollectionStyledLayoutStructureItem.class.getName())) {

			return false;
		}

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				assetListEntryUsage.getContainerKey());

		if (layoutStructureItem == null) {
			_assetListEntryUsageLocalService.deleteAssetListEntryUsage(
				assetListEntryUsage);

			return true;
		}

		if (layoutStructure.isItemMarkedForDeletion(
				layoutStructureItem.getItemId()) ||
			hiddenItemIds.contains(layoutStructureItem.getItemId())) {

			return true;
		}

		return false;
	}

	private boolean _isFragmentEntryLinkDeletedOrHidden(
		AssetListEntryUsage assetListEntryUsage, List<String> hiddenItemIds,
		LayoutStructure layoutStructure) {

		if (assetListEntryUsage.getContainerType() != _portal.getClassNameId(
				FragmentEntryLink.class.getName())) {

			return false;
		}

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				GetterUtil.getLong(assetListEntryUsage.getContainerKey()));

		if (fragmentEntryLink == null) {
			_assetListEntryUsageLocalService.deleteAssetListEntryUsage(
				assetListEntryUsage);

			return true;
		}

		if (fragmentEntryLink.isDeleted()) {
			return true;
		}

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLink.getFragmentEntryLinkId());

		if ((layoutStructureItem == null) ||
			layoutStructure.isItemMarkedForDeletion(
				layoutStructureItem.getItemId()) ||
			hiddenItemIds.contains(layoutStructureItem.getItemId())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(ContentManager.class);

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.asset.list.model.AssetListEntry)"
	)
	private ModelResourcePermission<AssetListEntry>
		_assetListEntryModelResourcePermission;

	@Reference
	private AssetListEntryUsageLocalService _assetListEntryUsageLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private InfoEditURLProviderRegistry _infoEditURLProviderRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private LayoutListPermissionProviderRegistry
		_layoutListPermissionProviderRegistry;

	@Reference
	private LayoutListRetrieverRegistry _layoutListRetrieverRegistry;

	@Reference
	private LayoutLockManager _layoutLockManager;

	@Reference
	private ListObjectReferenceFactoryRegistry
		_listObjectReferenceFactoryRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private SegmentsEntryRetriever _segmentsEntryRetriever;

}