/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.display.context;

import com.liferay.audiences.service.AudiencesEntryService;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationService;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelLocalService;
import com.liferay.segments.service.SegmentsExperienceService;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Víctor Galán
 */
public class EditElementVariationsDisplayContext {

	public EditElementVariationsDisplayContext(
		AudiencesEntryService audiencesEntryService,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		HttpServletRequest httpServletRequest,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateStructureRelElementVariationService
			layoutPageTemplateStructureRelElementVariationService,
		Portal portal,
		SegmentsExperienceAudienceEntryRelLocalService
			segmentsExperienceAudienceEntryRelLocalService,
		SegmentsExperienceService segmentsExperienceService) {

		_audiencesEntryService = audiencesEntryService;
		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_httpServletRequest = httpServletRequest;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateStructureRelElementVariationService =
			layoutPageTemplateStructureRelElementVariationService;
		_portal = portal;
		_segmentsExperienceAudienceEntryRelLocalService =
			segmentsExperienceAudienceEntryRelLocalService;
		_segmentsExperienceService = segmentsExperienceService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getData() {
		return HashMapBuilder.<String, Object>put(
			"addElementVariationURL",
			_getActionURL(
				"/layout_content_page_editor" +
					"/save_layout_page_template_structure_rel_element" +
						"_variation")
		).put(
			"audiences", _getAudiencesEntries()
		).put(
			"defaultLanguageId",
			LocaleUtil.toLanguageId(_themeDisplay.getSiteDefaultLocale())
		).put(
			"deleteElementVariationURL",
			_getActionURL(
				"/layout_content_page_editor" +
					"/delete_layout_page_template_structure_rel_element" +
						"_variation")
		).put(
			"elementVariations",
			_getLayoutPageTemplateStructureRelElementVariations()
		).put(
			"experiences", _getSegmentsExperiences()
		).put(
			"itemNames", _getLayoutStructureItemNamesMap()
		).put(
			"locales", _getAvailableLocalesJSONArray()
		).put(
			"plid", _getPlid()
		).put(
			"portletNamespace",
			_portal.getPortletNamespace(
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET)
		).put(
			"previewURL", _getPreviewURL()
		).put(
			"redirect", getRedirect()
		).put(
			"selectedSegmentsExperienceId", _getSegmentsExperienceId()
		).put(
			"updateAudiencesPriorityURL",
			_getActionURL(
				"/layout_content_page_editor" +
					"/update_segments_experience_audience_entry_rels")
		).build();
	}

	public String getRedirect() {
		if (Validator.isNotNull(_redirect)) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	private String _getActionURL(String actionName) {
		return PortletURLBuilder.createActionURL(
			_portal.getLiferayPortletResponse(
				(PortletResponse)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE)),
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET
		).setActionName(
			actionName
		).buildString();
	}

	private List<Map<String, Object>> _getAudiencesEntries() {
		try {
			return TransformUtil.transform(
				_audiencesEntryService.getAudiencesEntries(
					_themeDisplay.getCompanyId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null),
				audiencesEntry -> HashMapBuilder.<String, Object>put(
					"label", audiencesEntry.getName()
				).put(
					"value", audiencesEntry.getExternalReferenceCode()
				).build());
		}
		catch (Exception exception) {
			_log.error(exception);

			return Collections.emptyList();
		}
	}

	private JSONArray _getAvailableLocalesJSONArray() {
		JSONArray availableLocalesJSONArray = JSONFactoryUtil.createJSONArray();

		for (Locale locale :
				LanguageUtil.getAvailableLocales(
					_themeDisplay.getSiteGroupId())) {

			String w3cLanguageId = LocaleUtil.toW3cLanguageId(locale);

			availableLocalesJSONArray.put(
				JSONUtil.put(
					"displayName",
					locale.getDisplayName(_themeDisplay.getLocale())
				).put(
					"id", LocaleUtil.toLanguageId(locale)
				).put(
					"label", w3cLanguageId
				).put(
					"symbol", StringUtil.toLowerCase(w3cLanguageId)
				));
		}

		return availableLocalesJSONArray;
	}

	private List<Map<String, Object>>
		_getLayoutPageTemplateStructureRelElementVariations() {

		try {
			return TransformUtil.transform(
				_layoutPageTemplateStructureRelElementVariationService.
					getLayoutPageTemplateStructureRelElementVariations(
						_getPlid()),
				layoutPageTemplateStructureRelElementVariation ->
					HashMapBuilder.<String, Object>put(
						"active",
						layoutPageTemplateStructureRelElementVariation.
							isActive()
					).put(
						"audienceEntryERCs",
						layoutPageTemplateStructureRelElementVariation.
							getAudienceEntryERCs()
					).put(
						"externalReferenceCode",
						layoutPageTemplateStructureRelElementVariation.
							getExternalReferenceCode()
					).put(
						"hide",
						layoutPageTemplateStructureRelElementVariation.getHide()
					).put(
						"html",
						LocalizedMapUtil.getLanguageIdMap(
							layoutPageTemplateStructureRelElementVariation.
								getHtmlMap())
					).put(
						"js",
						LocalizedMapUtil.getLanguageIdMap(
							layoutPageTemplateStructureRelElementVariation.
								getJsMap())
					).put(
						"name",
						layoutPageTemplateStructureRelElementVariation.getName()
					).put(
						"segmentsExperienceERC",
						layoutPageTemplateStructureRelElementVariation.
							getSegmentsExperienceERC()
					).put(
						"targetElement",
						layoutPageTemplateStructureRelElementVariation.
							getTargetElement()
					).build());
		}
		catch (Exception exception) {
			_log.error(exception);

			return Collections.emptyList();
		}
	}

	private Map<String, String> _getLayoutStructureItemNamesMap() {
		try {
			Map<String, String> layoutStructureItemNamesMap = new HashMap<>();

			LayoutStructure layoutStructure =
				LayoutStructureUtil.getLayoutStructure(
					_themeDisplay.getScopeGroupId(), _getPlid(),
					_getSegmentsExperienceId());

			Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
				layoutStructure.getFragmentLayoutStructureItems();

			for (LayoutStructureItem layoutStructureItem :
					fragmentLayoutStructureItems.values()) {

				FragmentStyledLayoutStructureItem
					fragmentStyledLayoutStructureItem =
						(FragmentStyledLayoutStructureItem)layoutStructureItem;

				String name = fragmentStyledLayoutStructureItem.getName();

				if (Validator.isNull(name)) {
					FragmentEntryLink fragmentEntryLink =
						_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
							fragmentStyledLayoutStructureItem.
								getFragmentEntryLinkId());

					if (fragmentEntryLink != null) {
						FragmentEntry fragmentEntry =
							LayoutStructureUtil.getFragmentEntry(
								fragmentEntryLink);

						if (fragmentEntry != null) {
							name = fragmentEntry.getName();
						}
					}
				}

				if (Validator.isNotNull(name)) {
					layoutStructureItemNamesMap.put(
						fragmentStyledLayoutStructureItem.getItemId(), name);
				}
			}

			return layoutStructureItemNamesMap;
		}
		catch (Exception exception) {
			_log.error(exception);

			return Collections.emptyMap();
		}
	}

	private long _getPlid() {
		if (_plid != null) {
			return _plid;
		}

		_plid = ParamUtil.getLong(_httpServletRequest, "plid");

		return _plid;
	}

	private String _getPreviewURL() {
		Layout layout = _layoutLocalService.fetchLayout(_getPlid());

		if (layout == null) {
			return StringPool.BLANK;
		}

		Layout draftLayout = layout;

		if (!layout.isDraftLayout()) {
			draftLayout = layout.fetchDraftLayout();
		}

		String previewURL = HttpComponentsUtil.addParameters(
			_themeDisplay.getPortalURL() + _themeDisplay.getPathMain() +
				"/portal/get_page_preview",
			"p_l_id", draftLayout.getPlid(), "p_l_mode", Constants.PREVIEW,
			"p_p_state", WindowState.UNDEFINED.toString(),
			"segmentsExperienceId", _getSegmentsExperienceId(), "selPlid",
			draftLayout.getPlid());

		if (Validator.isNotNull(_themeDisplay.getDoAsUserId())) {
			previewURL = _portal.addPreservedParameters(
				_themeDisplay, previewURL, false, true);
		}

		return previewURL;
	}

	private long _getSegmentsExperienceId() {
		if (_segmentsExperienceId != null) {
			return _segmentsExperienceId;
		}

		_segmentsExperienceId = ParamUtil.getLong(
			_httpServletRequest, "segmentsExperienceId");

		return _segmentsExperienceId;
	}

	private List<Map<String, Object>> _getSegmentsExperiences() {
		try {
			return TransformUtil.transform(
				_segmentsExperienceService.getSegmentsExperiences(
					_themeDisplay.getScopeGroupId(), _getPlid(), true),
				segmentsExperience -> HashMapBuilder.<String, Object>put(
					"audienceEntryERCs",
					TransformUtil.transform(
						_segmentsExperienceAudienceEntryRelLocalService.
							getSegmentsExperienceAudienceEntryRels(
								_themeDisplay.getScopeGroupId(),
								segmentsExperience.getExternalReferenceCode()),
						SegmentsExperienceAudienceEntryRel::getAudienceEntryERC)
				).put(
					"label",
					segmentsExperience.getName(_themeDisplay.getLocale())
				).put(
					"segmentsExperienceERC",
					segmentsExperience.getExternalReferenceCode()
				).put(
					"segmentsExperienceId",
					segmentsExperience.getSegmentsExperienceId()
				).build());
		}
		catch (Exception exception) {
			_log.error(exception);

			return Collections.emptyList();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditElementVariationsDisplayContext.class);

	private final AudiencesEntryService _audiencesEntryService;
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateStructureRelElementVariationService
		_layoutPageTemplateStructureRelElementVariationService;
	private Long _plid;
	private final Portal _portal;
	private String _redirect;
	private final SegmentsExperienceAudienceEntryRelLocalService
		_segmentsExperienceAudienceEntryRelLocalService;
	private Long _segmentsExperienceId;
	private final SegmentsExperienceService _segmentsExperienceService;
	private final ThemeDisplay _themeDisplay;

}