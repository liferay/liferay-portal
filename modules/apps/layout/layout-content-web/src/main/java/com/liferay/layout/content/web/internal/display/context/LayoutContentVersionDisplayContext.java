/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.web.internal.display.context;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.util.SegmentsExperienceUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutContentVersionDisplayContext {

	public LayoutContentVersionDisplayContext(
		HttpServletRequest httpServletRequest, Language language,
		LayoutLocalService layoutLocalService,
		SegmentsExperienceLocalService segmentsExperienceLocalService) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_layoutLocalService = layoutLocalService;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getContext() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"config",
			HashMapBuilder.<String, Object>put(
				"availableLanguages", _getAvailableLanguages()
			).put(
				"availableSegmentsExperiences",
				_getAvailableSegmentsExperiences()
			).put(
				"defaultLanguageId",
				LocaleUtil.toLanguageId(_themeDisplay.getSiteDefaultLocale())
			).put(
				"defaultUserImageSrc",
				_themeDisplay.getPathImage() + "/user_portrait?img_id=0"
			).put(
				"getPagePreviewURL",
				_themeDisplay.getPathMain() + "/portal/get_page_preview"
			).put(
				"getPageVersionPreviewURL",
				_themeDisplay.getPathMain() + "/portal/get_page_version_preview"
			).put(
				"layout",
				() -> {
					Layout layout = _themeDisplay.getLayout();

					return HashMapBuilder.<String, Object>put(
						"name", layout.getName(_themeDisplay.getLocale())
					).put(
						"status", layout.isApproved() ? "approved" : "draft"
					).build();
				}
			).put(
				"pageSpecificationVersionPageExperiencesURL",
				_getPageSpecificationVersionPageExperiencesURL()
			).put(
				"pageSpecificationVersionsURL",
				_getPageSpecificationVersionsURL()
			).build()
		).build();
	}

	private Map<String, Object> _getAvailableLanguages() {
		Map<String, Object> availableLanguages = new LinkedHashMap<>();

		for (Locale locale :
				_language.getAvailableLocales(_themeDisplay.getSiteGroupId())) {

			availableLanguages.put(
				LocaleUtil.toLanguageId(locale),
				HashMapBuilder.<String, Object>put(
					"languageIcon",
					StringUtil.toLowerCase(LocaleUtil.toW3cLanguageId(locale))
				).put(
					"w3cLanguageId", LocaleUtil.toW3cLanguageId(locale)
				).build());
		}

		return availableLanguages;
	}

	private List<Map<String, Object>> _getAvailableSegmentsExperiences()
		throws PortalException {

		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				_themeDisplay.getScopeGroupId(), _themeDisplay.getPlid(), true);

		return TransformUtil.transform(
			segmentsExperiences,
			segmentsExperience -> {
				boolean active = SegmentsExperienceUtil.isActive(
					segmentsExperience, segmentsExperiences);

				return HashMapBuilder.<String, Object>put(
					"active", active
				).put(
					"priority", segmentsExperience.getPriority()
				).put(
					"segmentsExperienceERC",
					segmentsExperience.getExternalReferenceCode()
				).put(
					"segmentsExperienceId",
					String.valueOf(segmentsExperience.getSegmentsExperienceId())
				).put(
					"segmentsExperienceName",
					segmentsExperience.getName(_themeDisplay.getLocale())
				).put(
					"statusLabel",
					() -> _language.get(
						_httpServletRequest, active ? "active" : "inactive")
				).build();
			});
	}

	private String _getPageSpecificationVersionPageExperiencesURL()
		throws PortalException {

		Group group = _themeDisplay.getScopeGroup();

		Layout draftLayout = _themeDisplay.getLayout();

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		return StringBundler.concat(
			"/o/headless-admin-site/v1.0/sites/",
			group.getExternalReferenceCode(), "/site-pages/",
			layout.getExternalReferenceCode(), "/page-specification-versions",
			"/{pageSpecificationVersionExternalReferenceCode}",
			"/page-specification-version-page-experiences");
	}

	private String _getPageSpecificationVersionsURL() throws PortalException {
		Group group = _themeDisplay.getScopeGroup();

		Layout draftLayout = _themeDisplay.getLayout();

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		return StringBundler.concat(
			"/o/headless-admin-site/v1.0/sites/",
			group.getExternalReferenceCode(), "/site-pages/",
			layout.getExternalReferenceCode(), "/page-specification-versions");
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final LayoutLocalService _layoutLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;
	private final ThemeDisplay _themeDisplay;

}