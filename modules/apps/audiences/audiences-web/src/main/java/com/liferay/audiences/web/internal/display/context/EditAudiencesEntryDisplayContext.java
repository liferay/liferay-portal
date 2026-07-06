/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.display.context;

import com.liferay.audiences.criteria.AudiencesCriteria;
import com.liferay.audiences.criteria.AudiencesCriteriaProvider;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class EditAudiencesEntryDisplayContext {

	public EditAudiencesEntryDisplayContext(
		AudiencesCriteriaProvider audiencesCriteriaProvider,
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_audiencesCriteriaProvider = audiencesCriteriaProvider;
		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public long getAudiencesEntryId() {
		if (_audiencesEntryId != null) {
			return _audiencesEntryId;
		}

		_audiencesEntryId = ParamUtil.getLong(
			_httpServletRequest, "audiencesEntryId");

		return _audiencesEntryId;
	}

	public JSONObject getAudiencesEntryJSONObject() {
		try {
			AudiencesEntry audiencesEntry = _getAudiencesEntry();

			if (audiencesEntry != null) {
				return JSONFactoryUtil.createJSONObject(
					audiencesEntry.getJSON());
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return JSONFactoryUtil.createJSONObject();
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = ParamUtil.getString(
			_httpServletRequest, "backURL", getRedirect());

		return _backURL;
	}

	public String getBackURLTitle() {
		String backURLTitle = ParamUtil.getString(
			_httpServletRequest, "backURLTitle");

		if (Validator.isNotNull(backURLTitle)) {
			return backURLTitle;
		}

		return LanguageUtil.get(_httpServletRequest, "audiences");
	}

	public Map<String, Object> getData() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"audiencesCriteriaTypes",
			TransformUtil.transform(
				_audiencesCriteriaProvider.getAudiencesCriteriaTypes(
					themeDisplay.getCompanyId(), themeDisplay.getLocale()),
				audiencesCriteriaType -> HashMapBuilder.<String, Object>put(
					"audiencesCriterias",
					TransformUtil.transform(
						audiencesCriteriaType.getAudiencesCriterias(),
						audiencesCriteria -> HashMapBuilder.<String, Object>put(
							"icon", audiencesCriteria.getIcon()
						).put(
							"inputType",
							() -> {
								AudiencesCriteria.InputType inputType =
									audiencesCriteria.getInputType();

								return inputType.getValue();
							}
						).put(
							"key", audiencesCriteria.getKey()
						).put(
							"label", audiencesCriteria.getLabel()
						).put(
							"options",
							TransformUtil.transform(
								audiencesCriteria.getOptions(),
								option -> HashMapBuilder.<String, Object>put(
									"label", option.getLabel()
								).put(
									"value", option.getValue()
								).build())
						).put(
							"type",
							() -> {
								AudiencesCriteria.Type type =
									audiencesCriteria.getType();

								return type.getValue();
							}
						).build())
				).put(
					"key", audiencesCriteriaType.getKey()
				).put(
					"label", audiencesCriteriaType.getLabel()
				).build())
		).put(
			"backURL", getBackURL()
		).put(
			"backURLTitle", getBackURLTitle()
		).put(
			"externalReferenceCode", _getExternalReferenceCode()
		).put(
			"name", _getName()
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"rulesGroup", getAudiencesEntryJSONObject()
		).build();
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNull(_redirect)) {
			PortletURL portletURL = _renderResponse.createRenderURL();

			_redirect = portletURL.toString();
		}

		return _redirect;
	}

	public String getTitle() throws PortalException {
		if (_title != null) {
			return _title;
		}

		AudiencesEntry audiencesEntry = _getAudiencesEntry();

		if (audiencesEntry != null) {
			_title = audiencesEntry.getName();
		}
		else {
			_title = LanguageUtil.get(_httpServletRequest, "new-audience");
		}

		return _title;
	}

	private AudiencesEntry _getAudiencesEntry() throws PortalException {
		if (_audiencesEntry != null) {
			return _audiencesEntry;
		}

		long audiencesEntryId = getAudiencesEntryId();

		if (audiencesEntryId > 0) {
			_audiencesEntry = AudiencesEntryServiceUtil.getAudiencesEntry(
				audiencesEntryId);

			return _audiencesEntry;
		}

		return null;
	}

	private String _getExternalReferenceCode() {
		try {
			AudiencesEntry audiencesEntry = _getAudiencesEntry();

			if (audiencesEntry != null) {
				return audiencesEntry.getExternalReferenceCode();
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	private String _getName() {
		try {
			AudiencesEntry audiencesEntry = _getAudiencesEntry();

			if (audiencesEntry != null) {
				return audiencesEntry.getName();
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditAudiencesEntryDisplayContext.class);

	private final AudiencesCriteriaProvider _audiencesCriteriaProvider;
	private AudiencesEntry _audiencesEntry;
	private Long _audiencesEntryId;
	private String _backURL;
	private final HttpServletRequest _httpServletRequest;
	private String _redirect;
	private final RenderResponse _renderResponse;
	private String _title;

}