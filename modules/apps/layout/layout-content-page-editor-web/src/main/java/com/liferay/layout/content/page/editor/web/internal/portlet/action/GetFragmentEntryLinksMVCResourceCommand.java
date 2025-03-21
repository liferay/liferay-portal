/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.util.LinkedAssetEntryIdsUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_fragment_entry_links"
	},
	service = MVCResourceCommand.class
)
public class GetFragmentEntryLinksMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray fragmentEntryLinksJSONArray = _jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			resourceRequest, "segmentsExperienceId");

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			ParamUtil.getString(resourceRequest, "data"));

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			fragmentEntryLinksJSONArray.put(
				_getFragmentEntryLinkJSONObject(
					jsonObject.getLong("fragmentEntryLinkId"),
					jsonObject.getString("itemClassName"),
					jsonObject.getLong("itemClassPK"),
					jsonObject.getString("itemExternalReferenceCode"),
					ParamUtil.getString(
						resourceRequest, "languageId",
						themeDisplay.getLanguageId()),
					layoutStructure, resourceRequest, resourceResponse));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, fragmentEntryLinksJSONArray);
	}

	private void _addLinkedAssetEntryId(
		String className, long classPK, HttpServletRequest httpServletRequest) {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory == null) {
			return;
		}

		try {
			AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
				className, classPK);

			if (assetEntry != null) {
				LinkedAssetEntryIdsUtil.addLinkedAssetEntryId(
					httpServletRequest, assetEntry.getEntryId());
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	private JSONObject _getFragmentEntryLinkJSONObject(
			long fragmentEntryLinkId, String itemClassName, long itemClassPK,
			String itemExternalReferenceCode, String languageId,
			LayoutStructure layoutStructure, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLinkId);

		if (fragmentEntryLink == null) {
			return jsonObject;
		}

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setLocale(
			LocaleUtil.fromLanguageId(languageId));

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		LayoutDisplayPageProvider<?> currentLayoutDisplayPageProvider =
			(LayoutDisplayPageProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER);

		if (Validator.isNotNull(itemClassName) &&
			((itemClassPK > 0) ||
			 Validator.isNotNull(itemExternalReferenceCode))) {

			InfoItemIdentifier infoItemIdentifier = null;

			if (itemClassPK > 0) {
				infoItemIdentifier = new ClassPKInfoItemIdentifier(itemClassPK);
			}
			else {
				infoItemIdentifier = new ERCInfoItemIdentifier(
					itemExternalReferenceCode);
			}

			InfoItemObjectProvider<Object> infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class, itemClassName,
					infoItemIdentifier.getInfoItemServiceFilter());

			if (infoItemObjectProvider != null) {
				Object infoItemObject = infoItemObjectProvider.getInfoItem(
					infoItemIdentifier);

				defaultFragmentRendererContext.setContextInfoItemReference(
					new InfoItemReference(itemClassName, infoItemIdentifier));

				httpServletRequest.setAttribute(
					InfoDisplayWebKeys.INFO_ITEM, infoItemObject);

				InfoItemDetailsProvider infoItemDetailsProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemDetailsProvider.class, itemClassName);

				if (infoItemDetailsProvider != null) {
					httpServletRequest.setAttribute(
						InfoDisplayWebKeys.INFO_ITEM_DETAILS,
						infoItemDetailsProvider.getInfoItemDetails(
							infoItemObject));
				}

				httpServletRequest.setAttribute(
					InfoDisplayWebKeys.INFO_ITEM_REFERENCE,
					new InfoItemReference(itemClassName, infoItemIdentifier));

				_addLinkedAssetEntryId(
					itemClassName, itemClassPK, httpServletRequest);
			}

			LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
				_layoutDisplayPageProviderRegistry.
					getLayoutDisplayPageProviderByClassName(itemClassName);

			if (layoutDisplayPageProvider != null) {
				httpServletRequest.setAttribute(
					LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER,
					layoutDisplayPageProvider);
			}
		}

		try {
			jsonObject =
				_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					defaultFragmentRendererContext, fragmentEntryLink,
					_portal.getHttpServletRequest(resourceRequest),
					_portal.getHttpServletResponse(resourceResponse),
					layoutStructure);
		}
		finally {
			httpServletRequest.removeAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

			httpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER,
				currentLayoutDisplayPageProvider);
		}

		if (SessionErrors.contains(
				httpServletRequest, "fragmentEntryContentInvalid")) {

			jsonObject.put("error", true);

			SessionErrors.clear(httpServletRequest);
		}

		return jsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetFragmentEntryLinksMVCResourceCommand.class);

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private Portal _portal;

}