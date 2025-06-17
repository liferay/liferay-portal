/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.diff.DiffVersion;
import com.liferay.diff.DiffVersionsInfo;
import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Chema Balsas
 */
public class DiffVersionComparatorTag extends IncludeTag {

	public JSONObject createDiffVersionJSONObject(
			DiffVersion diffVersion, PortletURL sourceURL, PortletURL targetURL)
		throws PortalException {

		HttpServletRequest httpServletRequest = getRequest();

		String diffVersionString = String.valueOf(diffVersion.getVersion());

		sourceURL.setParameter("sourceVersion", diffVersionString);

		if (Validator.isNotNull(_languageId)) {
			targetURL.setParameter("languageId", _languageId);
		}

		targetURL.setParameter("targetVersion", diffVersionString);

		User user = UserLocalServiceUtil.fetchUser(diffVersion.getUserId());

		return JSONUtil.put(
			"displayDate",
			() -> {
				Date modifiedDate = diffVersion.getModifiedDate();

				return LanguageUtil.format(
					httpServletRequest, "x-ago",
					LanguageUtil.getTimeDescription(
						httpServletRequest,
						System.currentTimeMillis() - modifiedDate.getTime(),
						true),
					false);
			}
		).put(
			"inRange",
			(diffVersion.getVersion() > _sourceVersion) &&
			(diffVersion.getVersion() <= _targetVersion)
		).put(
			"label",
			LanguageUtil.format(
				httpServletRequest, "version-x", diffVersionString)
		).put(
			"sourceURL", sourceURL.toString()
		).put(
			"targetURL", targetURL.toString()
		).put(
			"userInitials",
			(user != null) ? user.getInitials() : StringPool.BLANK
		).put(
			"userName", (user != null) ? user.getFullName() : StringPool.BLANK
		).put(
			"version", diffVersionString
		);
	}

	public Set<Locale> getAvailableLocales() {
		return _availableLocales;
	}

	public String getDiffHtmlResults() {
		return _diffHtmlResults;
	}

	public DiffVersionsInfo getDiffVersionsInfo() {
		return _diffVersionsInfo;
	}

	public String getLanguageId() {
		return _languageId;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public PortletURL getResourceURL() {
		return _resourceURL;
	}

	public double getSourceVersion() {
		return _sourceVersion;
	}

	public double getTargetVersion() {
		return _targetVersion;
	}

	public void setAvailableLocales(Set<Locale> availableLocales) {
		_availableLocales = availableLocales;
	}

	public void setDiffHtmlResults(String diffHtmlResults) {
		_diffHtmlResults = diffHtmlResults;
	}

	public void setDiffVersionsInfo(DiffVersionsInfo diffVersionsInfo) {
		_diffVersionsInfo = diffVersionsInfo;
	}

	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setResourceURL(PortletURL resourceURL) {
		_resourceURL = resourceURL;
	}

	public void setSourceVersion(double sourceVersion) {
		_sourceVersion = sourceVersion;
	}

	public void setTargetVersion(double targetVersion) {
		_targetVersion = targetVersion;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_availableLocales = null;
		_diffHtmlResults = null;
		_diffVersionsInfo = null;
		_languageId = null;
		_portletURL = null;
		_resourceURL = null;
		_sourceVersion = 0;
		_targetVersion = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		if (Validator.isNotNull(_languageId)) {
			_resourceURL.setParameter("languageId", _languageId);
		}

		Map<String, Object> data = new HashMap<>();

		HttpServletRequest parentHttpServletRequest = getRequest();

		try {
			if (_availableLocales != null) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)parentHttpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				JSONArray availableLocalesJSONArray =
					JSONFactoryUtil.createJSONArray();

				for (Locale availableLocale : _availableLocales) {
					availableLocalesJSONArray.put(
						JSONUtil.put(
							"displayName",
							availableLocale.getDisplayName(
								themeDisplay.getLocale())
						).put(
							"languageId",
							LocaleUtil.toLanguageId(availableLocale)
						));
				}

				data.put("availableLocales", availableLocalesJSONArray);
			}

			data.put("diffHtmlResults", _diffHtmlResults);

			RenderResponse renderResponse =
				(RenderResponse)parentHttpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE);

			PortletURL sourceURL = PortletURLBuilder.create(
				PortletURLUtil.clone(_portletURL, renderResponse)
			).setParameter(
				"targetVersion", _targetVersion
			).buildPortletURL();

			PortletURL targetURL = PortletURLBuilder.create(
				PortletURLUtil.clone(_portletURL, renderResponse)
			).setParameter(
				"sourceVersion", _sourceVersion
			).buildPortletURL();

			JSONArray diffVersionsJSONArray = JSONFactoryUtil.createJSONArray();

			int diffVersionsCount = 0;

			for (DiffVersion diffVersion :
					_diffVersionsInfo.getDiffVersions()) {

				JSONObject diffVersionJSONObject = createDiffVersionJSONObject(
					diffVersion, sourceURL, targetURL);

				if (diffVersionJSONObject.getBoolean("inRange")) {
					diffVersionsCount++;
				}

				diffVersionsJSONArray.put(diffVersionJSONObject);
			}

			data.put("diffVersions", diffVersionsJSONArray);

			data.put("diffVersionsCount", diffVersionsCount);
			data.put("languageId", _languageId);
			data.put(
				"nextVersion",
				String.valueOf(_diffVersionsInfo.getNextVersion()));
			data.put("portletURL", _portletURL.toString());
			data.put(
				"previousVersion",
				String.valueOf(_diffVersionsInfo.getPreviousVersion()));
			data.put("resourceURL", _resourceURL.toString());
			data.put("sourceVersion", String.valueOf(_sourceVersion));
			data.put("targetVersion", String.valueOf(_targetVersion));
		}
		catch (PortalException | PortletException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		httpServletRequest.setAttribute(
			"liferay-frontend:diff-version-comparator:data", data);
	}

	private static final String _PAGE = "/diff_version_comparator/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		DiffVersionComparatorTag.class);

	private Set<Locale> _availableLocales;
	private String _diffHtmlResults;
	private DiffVersionsInfo _diffVersionsInfo;
	private String _languageId;
	private PortletURL _portletURL;
	private PortletURL _resourceURL;
	private double _sourceVersion;
	private double _targetVersion;

}