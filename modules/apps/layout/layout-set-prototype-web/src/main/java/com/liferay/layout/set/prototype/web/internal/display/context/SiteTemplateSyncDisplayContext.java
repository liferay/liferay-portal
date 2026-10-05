/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.display.context;

import com.liferay.exportimport.rest.dto.v1_0.SyncPreview;
import com.liferay.exportimport.rest.resource.v1_0.SyncPreviewResource;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Jaime Leon
 */
public class SiteTemplateSyncDisplayContext {

	public SiteTemplateSyncDisplayContext(
			HttpServletRequest httpServletRequest,
			LayoutSetPrototype layoutSetPrototype,
			LiferayPortletResponse liferayPortletResponse,
			SyncPreviewResource.Factory syncPreviewResourceFactory)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_layoutSetPrototype = layoutSetPrototype;
		_liferayPortletResponse = liferayPortletResponse;
		_syncPreviewResourceFactory = syncPreviewResourceFactory;

		_group = layoutSetPrototype.getGroup();
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBackURL() {
		String redirect = PortalUtil.escapeRedirect(
			ParamUtil.getString(_httpServletRequest, "redirect"));

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).buildString();
	}

	public Map<String, Object> getPageTreeModalConfiguration() {
		return HashMapBuilder.<String, Object>put(
			"groupId", _group.getGroupId()
		).put(
			"pageSize", PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN
		).put(
			"privateLayoutsAvailable", false
		).put(
			"siteExternalReferenceCode", _group.getExternalReferenceCode()
		).build();
	}

	public String getSiteTemplateName() {
		return _layoutSetPrototype.getName(_themeDisplay.getLocale());
	}

	public String getSyncPreviewAPIURL() {
		return _getAPIURL("/sync-preview");
	}

	public JSONObject getSyncPreviewJSONObject() {
		try {
			SyncPreviewResource syncPreviewResource =
				_syncPreviewResourceFactory.create(
				).checkPermissions(
					true
				).httpServletRequest(
					_httpServletRequest
				).preferredLocale(
					_themeDisplay.getLocale()
				).user(
					_themeDisplay.getUser()
				).build();

			SyncPreview syncPreview =
				syncPreviewResource.getSiteTemplateSyncPreview(
					_layoutSetPrototype.getLayoutSetPrototypeId());

			return JSONFactoryUtil.createJSONObject(syncPreview.toString());
		}
		catch (Exception exception) {
			_log.error("Unable to get site template sync preview", exception);
		}

		return null;
	}

	public String getSyncProcessAPIURL() {
		return _getAPIURL("/sync-processes");
	}

	public String getTitle() {
		return StringBundler.concat(
			LanguageUtil.get(_httpServletRequest, "site-template-sync"), " - ",
			getSiteTemplateName());
	}

	public boolean isCommentsAndRatingsEnabled() {
		return ScopeUtil.isCommentsAndRatingsEnabled(_group);
	}

	private String _getAPIURL(String endpoint) {
		return ScopeUtil.getAPIURL(
			StringBundler.concat(
				"/site-templates/",
				_layoutSetPrototype.getLayoutSetPrototypeId(), endpoint));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteTemplateSyncDisplayContext.class);

	private final Group _group;
	private final HttpServletRequest _httpServletRequest;
	private final LayoutSetPrototype _layoutSetPrototype;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final SyncPreviewResource.Factory _syncPreviewResourceFactory;
	private final ThemeDisplay _themeDisplay;

}