/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.reports.web.internal.struts;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.helper.FragmentEntryLinkHelper;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.helper.LayoutWarningMessageHelper;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.taglib.servlet.taglib.renderer.LayoutStructureRenderer;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.DummyWriter;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "path=/layout_reports/get_layout_item_data",
	service = StrutsAction.class
)
public class GetLayoutReportsLayoutItemDataStrutsAction
	implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(
			ParamUtil.getLong(httpServletRequest, "p_l_id"));

		if ((layout == null) || !layout.isTypeContent()) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			themeDisplay.getPermissionChecker(), layout);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(),
				_getSegmentsExperienceId(httpServletRequest, layout));

		if (layoutStructure == null) {
			return null;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		LayoutStructureRenderer layoutStructureRenderer =
			new LayoutStructureRenderer(
				httpServletRequest, layoutStructure,
				layoutStructure.getMainItemId(),
				FragmentEntryLinkConstants.VIEW,
				PageContextFactoryUtil.create(
					httpServletRequest,
					new PipingServletResponse(
						httpServletResponse, new DummyWriter())),
				false, false);

		try {
			layoutStructureRenderer.render();
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get layout structure item render times", exception);

			return null;
		}

		for (LayoutStructureRenderer.LayoutStructureItemRenderTime
				layoutStructureItemRenderTime :
					layoutStructureRenderer.
						getLayoutStructureItemRenderTimes()) {

			LayoutStructureItem layoutStructureItem =
				layoutStructureItemRenderTime.getLayoutStructureItem();

			if (!(layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem) &&
				!(layoutStructureItem instanceof
					CollectionStyledLayoutStructureItem)) {

				continue;
			}

			if (layoutStructureItem instanceof
					CollectionStyledLayoutStructureItem) {

				jsonArray.put(
					JSONUtil.put(
						"cached", false
					).put(
						"fragmentCollectionURL", StringPool.BLANK
					).put(
						"fromMaster",
						_isFromMaster(null, layout, layoutStructureItem)
					).put(
						"hierarchy",
						_getLayoutStructureHierarchy(
							layoutStructure, layoutStructureItem,
							themeDisplay.getLocale())
					).put(
						"isPortlet", false
					).put(
						"itemId", layoutStructureItem.getItemId()
					).put(
						"itemType", layoutStructureItem.getItemType()
					).put(
						"name",
						_language.get(
							themeDisplay.getLocale(), "collection-display")
					).put(
						"renderTime",
						layoutStructureItemRenderTime.getRenderTime()
					).put(
						"warnings",
						() -> {
							JSONArray collectionWarningMessagesJSONArray =
								_jsonFactory.createJSONArray();

							JSONObject collectionWarningMessageJSONObject =
								_layoutWarningMessageHelper.
									getCollectionWarningMessageJSONObject(
										(CollectionStyledLayoutStructureItem)
											layoutStructureItem,
										httpServletRequest);

							if (collectionWarningMessageJSONObject.has(
									"description")) {

								collectionWarningMessagesJSONArray.put(
									collectionWarningMessageJSONObject);
							}

							return collectionWarningMessagesJSONArray;
						}
					));

				continue;
			}

			FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
				layoutStructureItem);

			FragmentEntry fragmentEntry = _getFragmentEntry(fragmentEntryLink);

			jsonArray.put(
				JSONUtil.put(
					"cached", fragmentEntryLink.isCacheable()
				).put(
					"fragmentCollectionURL",
					_getFragmentCollectionURL(
						fragmentEntry, httpServletRequest, themeDisplay)
				).put(
					"fromMaster",
					_isFromMaster(
						fragmentEntryLink, layout, layoutStructureItem)
				).put(
					"hierarchy",
					_getLayoutStructureHierarchy(
						layoutStructure, layoutStructureItem,
						themeDisplay.getLocale())
				).put(
					"isPortlet", fragmentEntryLink.isTypePortlet()
				).put(
					"itemId", layoutStructureItem.getItemId()
				).put(
					"itemType", layoutStructureItem.getItemType()
				).put(
					"name",
					_getFragmentEntryName(
						fragmentEntryLink, themeDisplay.getLocale())
				).put(
					"renderTime", layoutStructureItemRenderTime.getRenderTime()
				).put(
					"warnings",
					() -> {
						JSONArray fragmentWarningMessagesJSONArray =
							_jsonFactory.createJSONArray();

						JSONObject fragmentWarningMessageJSONObject =
							_layoutWarningMessageHelper.
								getFragmentWarningMessageJsonObject(
									(FragmentStyledLayoutStructureItem)
										layoutStructureItem,
									httpServletRequest, httpServletResponse);

						if (fragmentWarningMessageJSONObject.has(
								"description")) {

							fragmentWarningMessagesJSONArray.put(
								fragmentWarningMessageJSONObject);
						}

						return fragmentWarningMessagesJSONArray;
					}
				));
		}

		ServletResponseUtil.write(httpServletResponse, jsonArray.toString());

		return null;
	}

	private String _getFragmentCollectionURL(
		FragmentEntry fragmentEntry, HttpServletRequest httpServletRequest,
		ThemeDisplay themeDisplay) {

		if (fragmentEntry == null) {
			return StringPool.BLANK;
		}

		long fragmentCollectionId = fragmentEntry.getFragmentCollectionId();

		if (fragmentCollectionId > 0) {
			return PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest, themeDisplay.getScopeGroup(),
					FragmentPortletKeys.FRAGMENT, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"fragmentCollectionId", fragmentCollectionId
			).buildString();
		}

		String fragmentEntryKey = fragmentEntry.getFragmentEntryKey();

		int index = fragmentEntryKey.indexOf(CharPool.DASH);

		if (index == -1) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, themeDisplay.getScopeGroup(),
				FragmentPortletKeys.FRAGMENT, 0, 0, PortletRequest.RENDER_PHASE)
		).setParameter(
			"fragmentCollectionKey", fragmentEntryKey.substring(0, index)
		).buildString();
	}

	private FragmentEntry _getFragmentEntry(
		FragmentEntryLink fragmentEntryLink) {

		if (fragmentEntryLink == null) {
			return null;
		}

		long fragmentEntryId = fragmentEntryLink.getFragmentEntryId();

		if (fragmentEntryId > 0) {
			return _fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntryId);
		}

		String rendererKey = fragmentEntryLink.getRendererKey();

		if (Validator.isNull(rendererKey)) {
			return null;
		}

		return _fragmentCollectionContributorRegistry.getFragmentEntry(
			rendererKey);
	}

	private FragmentEntryLink _getFragmentEntryLink(
		LayoutStructureItem layoutStructureItem) {

		if (!(layoutStructureItem instanceof
				FragmentStyledLayoutStructureItem)) {

			return null;
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)layoutStructureItem;

		long fragmentEntryLinkId =
			fragmentStyledLayoutStructureItem.getFragmentEntryLinkId();

		if (fragmentEntryLinkId == 0) {
			return null;
		}

		return _fragmentEntryLinkLocalService.fetchFragmentEntryLink(
			fragmentEntryLinkId);
	}

	private String _getFragmentEntryName(
			FragmentEntryLink fragmentEntryLink, Locale locale)
		throws Exception {

		if (fragmentEntryLink == null) {
			return StringPool.BLANK;
		}

		if (Validator.isNotNull(fragmentEntryLink.getRendererKey()) ||
			(fragmentEntryLink.getFragmentEntryId() > 0)) {

			return _fragmentEntryLinkHelper.getFragmentEntryName(
				fragmentEntryLink, locale);
		}

		String portletId = _getPortletId(fragmentEntryLink);

		if (Validator.isNotNull(portletId)) {
			return _portal.getPortletTitle(portletId, locale);
		}

		return StringPool.BLANK;
	}

	private String _getLayoutStructureHierarchy(
			LayoutStructure layoutStructure,
			LayoutStructureItem layoutStructureItem, Locale locale)
		throws Exception {

		List<String> layoutStructureHierarchy = _getLayoutStructureHierarchy(
			layoutStructure, new ArrayList<>(), layoutStructureItem, locale);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < layoutStructureHierarchy.size(); i++) {
			sb.append(layoutStructureHierarchy.get(i));

			if (i < (layoutStructureHierarchy.size() - 1)) {
				sb.append(StringPool.SPACE);
				sb.append(StringPool.GREATER_THAN);
				sb.append(StringPool.SPACE);
			}
		}

		return sb.toString();
	}

	private List<String> _getLayoutStructureHierarchy(
			LayoutStructure layoutStructure,
			List<String> layoutStructureHierarchy,
			LayoutStructureItem layoutStructureItem, Locale locale)
		throws Exception {

		if (!Objects.equals(
				layoutStructureItem.getItemId(),
				layoutStructure.getMainItemId()) &&
			Validator.isNotNull(layoutStructureItem.getParentItemId())) {

			_getLayoutStructureHierarchy(
				layoutStructure, layoutStructureHierarchy,
				layoutStructure.getLayoutStructureItem(
					layoutStructureItem.getParentItemId()),
				locale);
		}

		String name = _getLayoutStructureItemName(layoutStructureItem, locale);

		if (Validator.isNotNull(name)) {
			layoutStructureHierarchy.add(name);
		}

		return layoutStructureHierarchy;
	}

	private String _getLayoutStructureItemName(
			LayoutStructureItem layoutStructureItem, Locale locale)
		throws Exception {

		if (layoutStructureItem instanceof
				CollectionStyledLayoutStructureItem) {

			return _language.get(locale, "collection-display");
		}
		else if (layoutStructureItem instanceof
					ContainerStyledLayoutStructureItem) {

			return _language.get(locale, "container");
		}
		else if (layoutStructureItem instanceof FormStyledLayoutStructureItem) {
			return _language.get(locale, "form-container");
		}
		else if (layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem) {

			return _getFragmentEntryName(
				_getFragmentEntryLink(layoutStructureItem), locale);
		}
		else if (layoutStructureItem instanceof RowStyledLayoutStructureItem) {
			return _language.get(locale, "grid");
		}

		return StringPool.BLANK;
	}

	private String _getPortletId(FragmentEntryLink fragmentEntryLink)
		throws Exception {

		if ((fragmentEntryLink == null) || !fragmentEntryLink.isTypePortlet()) {
			return StringPool.BLANK;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		return jsonObject.getString("portletId");
	}

	private long _getSegmentsExperienceId(
		HttpServletRequest httpServletRequest, Layout layout) {

		long segmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId");

		if (segmentsExperienceId != 0) {
			return segmentsExperienceId;
		}

		return _segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
			layout.getPlid());
	}

	private boolean _isFromMaster(
		FragmentEntryLink fragmentEntryLink, Layout layout,
		LayoutStructureItem layoutStructureItem) {

		if (layout.getMasterLayoutPlid() == 0) {
			return false;
		}

		if (fragmentEntryLink != null) {
			if (fragmentEntryLink.getPlid() == layout.getMasterLayoutPlid()) {
				return true;
			}

			return false;
		}

		LayoutStructure masterLayoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getMasterLayoutPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						layout.getMasterLayoutPlid()));

		if (masterLayoutStructure == null) {
			return false;
		}

		LayoutStructureItem masterLayoutStructureItem =
			masterLayoutStructure.getLayoutStructureItem(
				layoutStructureItem.getItemId());

		if (masterLayoutStructureItem != null) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetLayoutReportsLayoutItemDataStrutsAction.class);

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryLinkHelper _fragmentEntryLinkHelper;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutStructureProvider _layoutStructureProvider;

	@Reference
	private LayoutWarningMessageHelper _layoutWarningMessageHelper;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}