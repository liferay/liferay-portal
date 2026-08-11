/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.exportimport.rest.dto.v1_0.ExportPreview;
import com.liferay.exportimport.rest.resource.v1_0.ExportPreviewResource;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate.Scope;
import com.liferay.exportimport.web.internal.util.ScopeUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.group.capability.GroupCapabilityUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author Daniel Raposo
 * @author Jorge González
 */
public class ExportImportPreviewDisplayContext {

	public ExportImportPreviewDisplayContext(
		String backMVCRenderCommandName,
		ExportPreviewResource.Factory exportPreviewResourceFactory, Group group,
		long groupId, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, long liveGroupId,
		boolean privateLayout) {

		_backMVCRenderCommandName = backMVCRenderCommandName;
		_exportPreviewResourceFactory = exportPreviewResourceFactory;
		_group = group;
		_groupId = groupId;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_liveGroupId = liveGroupId;
		_privateLayout = privateLayout;
	}

	public ExportImportPreviewDisplayContext(
		String backMVCRenderCommandName, Group group, long groupId,
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, long liveGroupId,
		boolean privateLayout) {

		this(
			backMVCRenderCommandName, null, group, groupId, httpServletRequest,
			liferayPortletResponse, liveGroupId, privateLayout);
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		String backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		if (Validator.isBlank(backURL)) {
			backURL = PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				_backMVCRenderCommandName
			).setParameter(
				"displayStyle",
				() -> ParamUtil.getString(_httpServletRequest, "displayStyle")
			).setParameter(
				"groupId", _groupId
			).setParameter(
				"liveGroupId", _liveGroupId
			).setParameter(
				"privateLayout", _privateLayout
			).buildString();
		}

		_backURL = backURL;

		return _backURL;
	}

	public String getExportPreviewAPIURL() {
		if (_exportPreviewAPIURL != null) {
			return _exportPreviewAPIURL;
		}

		_exportPreviewAPIURL = _getResourceAPIURL("/export-preview");

		return _exportPreviewAPIURL;
	}

	public JSONObject getExportPreviewJSONObject() {
		if (_exportPreviewJSONObject != null) {
			return _exportPreviewJSONObject;
		}

		ExportPreview exportPreview = _getExportPreview();

		if (exportPreview == null) {
			return null;
		}

		try {
			_exportPreviewJSONObject = JSONFactoryUtil.createJSONObject(
				exportPreview.toString());
		}
		catch (Exception exception) {
			_log.error("Unable to serialize export preview", exception);
		}

		return _exportPreviewJSONObject;
	}

	public String getExportProcessAPIURL() {
		if (_exportProcessAPIURL != null) {
			return _exportProcessAPIURL;
		}

		_exportProcessAPIURL = _getResourceAPIURL("/export-processes");

		return _exportProcessAPIURL;
	}

	public String getExportTitle() {
		return _getTitle("new-export-process");
	}

	public String getImportPreviewAPIURL() {
		if (_importPreviewAPIURL != null) {
			return _importPreviewAPIURL;
		}

		_importPreviewAPIURL = _getResourceAPIURL("/import-preview");

		return _importPreviewAPIURL;
	}

	public String getImportProcessAPIURL() {
		if (_importProcessAPIURL != null) {
			return _importProcessAPIURL;
		}

		_importProcessAPIURL = _getResourceAPIURL("/import-processes");

		return _importProcessAPIURL;
	}

	public String getImportTitle() {
		return _getTitle("new-import-process");
	}

	public Scope getScope() {
		if (!Validator.isBlank(_getPortletId())) {
			return Scope.PORTLET;
		}

		if (ScopeUtil.isInstanceScoped(_group)) {
			return Scope.COMPANY;
		}

		if (_group.isDepot()) {
			return Scope.DEPOT;
		}

		return Scope.SITE;
	}

	public boolean isCommentsAndRatingsEnabled() {
		if (!ScopeUtil.isInstanceScoped(_group) ||
			FeatureFlagManagerUtil.isEnabled(
				_group.getCompanyId(), "LPD-43996")) {

			return true;
		}

		return false;
	}

	public boolean isLookAndFeelEnabled() {
		if ((getScope() != Scope.PORTLET) &&
			GroupCapabilityUtil.isSupportsPages(_group) &&
			!_group.isCompany() && !_group.isLayoutPrototype()) {

			return true;
		}

		return false;
	}

	private String _encode(String value) {
		if (Validator.isBlank(value)) {
			return StringPool.BLANK;
		}

		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	private ExportPreview _getExportPreview() {
		if (_exportPreviewResourceFactory == null) {
			return null;
		}

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			ExportPreviewResource exportPreviewResource =
				_exportPreviewResourceFactory.create(
				).checkPermissions(
					true
				).httpServletRequest(
					_httpServletRequest
				).preferredLocale(
					themeDisplay.getLocale()
				).user(
					themeDisplay.getUser()
				).build();

			long plid = ParamUtil.getLong(_httpServletRequest, "plid");
			String portletId = _getPortletId();

			if (ScopeUtil.isInstanceScoped(_group)) {
				return exportPreviewResource.getExportPreview(
					null, null, plid, portletId, null);
			}

			if (_group.isDepot()) {
				return exportPreviewResource.getAssetLibraryExportPreview(
					_group.getExternalReferenceCode(), null, null, plid,
					portletId, null);
			}

			return exportPreviewResource.getSiteExportPreview(
				_group.getExternalReferenceCode(), null, null, plid, portletId,
				null);
		}
		catch (Exception exception) {
			_log.error("Unable to get export preview", exception);
		}

		return null;
	}

	private String _getPortletId() {
		return ParamUtil.getString(_httpServletRequest, "portletId");
	}

	private String _getResourceAPIURL(String endpoint) {
		String portletId = _getPortletId();

		if (Validator.isBlank(portletId)) {
			return ScopeUtil.getAPIURL(_group, endpoint);
		}

		return StringBundler.concat(
			ScopeUtil.getAPIURL(_group, endpoint), "?plid=",
			ParamUtil.getLong(_httpServletRequest, "plid"), "&portletId=",
			_encode(portletId));
	}

	private String _getTitle(String key) {
		String label = LanguageUtil.get(_httpServletRequest, key);

		String portletId = _getPortletId();

		if (Validator.isBlank(portletId)) {
			return label;
		}

		return StringBundler.concat(
			label, " - ",
			PortalUtil.getPortletTitle(
				portletId, PortalUtil.getLocale(_httpServletRequest)));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportPreviewDisplayContext.class);

	private final String _backMVCRenderCommandName;
	private String _backURL;
	private String _exportPreviewAPIURL;
	private JSONObject _exportPreviewJSONObject;
	private final ExportPreviewResource.Factory _exportPreviewResourceFactory;
	private String _exportProcessAPIURL;
	private final Group _group;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private String _importPreviewAPIURL;
	private String _importProcessAPIURL;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final long _liveGroupId;
	private final boolean _privateLayout;

}