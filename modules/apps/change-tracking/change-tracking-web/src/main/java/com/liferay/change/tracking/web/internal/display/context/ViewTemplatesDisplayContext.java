/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.service.CTCollectionTemplateService;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionTemplatePermission;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

/**
 * @author David Truong
 */
public class ViewTemplatesDisplayContext
	extends BasePublicationsDisplayContext {

	public ViewTemplatesDisplayContext(
		CTCollectionTemplateService ctCollectionTemplateService,
		CTSettingsConfigurationHelper ctSettingsConfigurationHelper,
		HttpServletRequest httpServletRequest, Language language,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		super(httpServletRequest);

		_ctCollectionTemplateService = ctCollectionTemplateService;
		_ctSettingsConfigurationHelper = ctSettingsConfigurationHelper;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getDropdownReactData(
			CTCollectionTemplate ctCollectionTemplate)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"deleteURL",
			_getDeleteTemplateURL(
				ctCollectionTemplate.getCtCollectionTemplateId())
		).put(
			"editURL",
			getEditTemplateURL(ctCollectionTemplate.getCtCollectionTemplateId())
		).put(
			"isPublicationTemplate", true
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).build();
	}

	public String getEditTemplateURL(long ctCollectionTemplateId)
		throws PortalException {

		if (!CTCollectionTemplatePermission.contains(
				_themeDisplay.getPermissionChecker(), ctCollectionTemplateId,
				ActionKeys.UPDATE)) {

			return null;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/change_tracking/edit_ct_collection_template"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"ctCollectionTemplateId", ctCollectionTemplateId
		).buildString();
	}

	public SearchContainer<CTCollectionTemplate> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<CTCollectionTemplate> searchContainer =
			new SearchContainer<>(
				_renderRequest, new DisplayTerms(_renderRequest), null,
				SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA,
				PortletURLUtil.getCurrent(_renderRequest, _renderResponse),
				null,
				_language.get(_httpServletRequest, "no-templates-were-found"));

		searchContainer.setId("templates");
		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());

		DisplayTerms displayTerms = searchContainer.getDisplayTerms();

		String keywords = displayTerms.getKeywords();

		searchContainer.setResultsAndTotal(
			() -> {
				String column = searchContainer.getOrderByCol();

				if (column.equals("modified-date")) {
					column = "modifiedDate";
				}

				return _ctCollectionTemplateService.getCTCollectionTemplates(
					keywords, searchContainer.getStart(),
					searchContainer.getEnd(),
					OrderByComparatorFactoryUtil.create(
						"CTCollectionTemplate", column,
						Objects.equals(
							searchContainer.getOrderByType(), "asc")));
			},
			_ctCollectionTemplateService.getCTCollectionTemplatesCount(
				keywords));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public boolean isDefaultCTCollectionTemplate(
			CTCollectionTemplate ctCollectionTemplate)
		throws PortalException {

		return _ctSettingsConfigurationHelper.isDefaultCTCollectionTemplate(
			ctCollectionTemplate.getCompanyId(),
			ctCollectionTemplate.getCtCollectionTemplateId());
	}

	public boolean isDefaultSandboxCTCollectionTemplate(
			CTCollectionTemplate ctCollectionTemplate)
		throws PortalException {

		return _ctSettingsConfigurationHelper.
			isDefaultSandboxCTCollectionTemplate(
				ctCollectionTemplate.getCompanyId(),
				ctCollectionTemplate.getCtCollectionTemplateId());
	}

	@Override
	protected String getDefaultOrderByCol() {
		return "name";
	}

	@Override
	protected String getPortalPreferencesPrefix() {
		return "templates";
	}

	private String _getDeleteTemplateURL(long ctCollectionTemplateId)
		throws PortalException {

		if (!CTCollectionTemplatePermission.contains(
				_themeDisplay.getPermissionChecker(), ctCollectionTemplateId,
				ActionKeys.UPDATE)) {

			return null;
		}

		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/change_tracking/delete_ct_collection_template"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"ctCollectionTemplateId", ctCollectionTemplateId
		).buildString();
	}

	private final CTCollectionTemplateService _ctCollectionTemplateService;
	private final CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<CTCollectionTemplate> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}