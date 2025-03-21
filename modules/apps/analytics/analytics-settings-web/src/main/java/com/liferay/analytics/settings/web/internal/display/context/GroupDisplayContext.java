/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.display.context;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.web.internal.constants.AnalyticsSettingsWebKeys;
import com.liferay.analytics.settings.web.internal.search.GroupChecker;
import com.liferay.analytics.settings.web.internal.search.GroupSearch;
import com.liferay.analytics.settings.web.internal.util.AnalyticsSettingsUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

/**
 * @author Marcellus Tavares
 * @author André Miranda
 */
public class GroupDisplayContext {

	public GroupDisplayContext(
		String mvcRenderCommandName, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_mvcRenderCommandName = mvcRenderCommandName;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_analyticsConfiguration =
			(AnalyticsConfiguration)renderRequest.getAttribute(
				AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION);
	}

	public String getChannelName(Long groupId) {
		if (_channelNames == null) {
			return StringPool.BLANK;
		}

		return _channelNames.getOrDefault(
			String.valueOf(groupId), StringPool.BLANK);
	}

	public GroupSearch getGroupSearch() {
		GroupSearch groupSearch = new GroupSearch(
			_renderRequest, getPortletURL());

		groupSearch.setOrderByCol(_getOrderByCol());
		groupSearch.setOrderByType(getOrderByType());
		groupSearch.setResultsAndTotal(
			() -> {
				List<Group> groups = Collections.emptyList();

				try {
					groups = GroupServiceUtil.search(
						_getCompanyId(), _getClassNameIds(), _getKeywords(),
						_getGroupParams(), groupSearch.getStart(),
						groupSearch.getEnd(),
						new GroupNameComparator(_isOrderByAscending()));
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}

				_fetchChannelNames(groups);

				return groups;
			},
			GroupServiceUtil.searchCount(
				_getCompanyId(), _getClassNameIds(), _getKeywords(),
				_getGroupParams()));
		groupSearch.setRowChecker(
			new GroupChecker(
				_renderResponse,
				ParamUtil.getString(_renderRequest, "channelId"),
				_getDisabledGroupIds(), _mvcRenderCommandName));

		return groupSearch;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION,
			"group-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			_mvcRenderCommandName
		).buildPortletURL();

		if (StringUtil.equalsIgnoreCase(
				_mvcRenderCommandName, "/analytics_settings/edit_channel")) {

			portletURL.setParameter(
				"channelId", ParamUtil.getString(_renderRequest, "channelId"));
			portletURL.setParameter(
				"channelName",
				ParamUtil.getString(_renderRequest, "channelName"));
		}

		return portletURL;
	}

	private void _fetchChannelNames(List<Group> groups) {
		_channelNames = new HashMap<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (groups.isEmpty() ||
			!AnalyticsSettingsUtil.isAnalyticsEnabled(
				themeDisplay.getCompanyId())) {

			return;
		}

		try {
			HttpResponse httpResponse = AnalyticsSettingsUtil.doPost(
				JSONUtil.put(
					"dataSourceId",
					AnalyticsSettingsUtil.getDataSourceId(
						themeDisplay.getCompanyId())
				).put(
					"groupIds",
					TransformUtil.transform(
						groups, group -> String.valueOf(group.getGroupId()))
				),
				themeDisplay.getCompanyId(),
				"api/1.0/channels/query_channel_names");

			StatusLine statusLine = httpResponse.getStatusLine();

			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				_log.error("Failed to fetch channels");

				return;
			}

			JSONObject channelsJSONObject = JSONFactoryUtil.createJSONObject(
				EntityUtils.toString(httpResponse.getEntity()));

			for (String key : channelsJSONObject.keySet()) {
				_channelNames.put(key, channelsJSONObject.getString(key));
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private long[] _getClassNameIds() {
		if (_classNameIds != null) {
			return _classNameIds;
		}

		_classNameIds = new long[] {
			PortalUtil.getClassNameId(Group.class),
			PortalUtil.getClassNameId(Organization.class)
		};

		return _classNameIds;
	}

	private long _getCompanyId() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.getCompanyId();
	}

	private Set<String> _getDisabledGroupIds() {
		if (MapUtil.isEmpty(_channelNames)) {
			return Collections.emptySet();
		}

		return _channelNames.keySet();
	}

	private LinkedHashMap<String, Object> _getGroupParams() {
		return LinkedHashMapBuilder.<String, Object>put(
			"active", Boolean.TRUE
		).put(
			"site", Boolean.TRUE
		).build();
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION,
			"group-order-by-col", "site-name");

		return _orderByCol;
	}

	private boolean _isOrderByAscending() {
		return Objects.equals(getOrderByType(), "asc");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupDisplayContext.class);

	private final AnalyticsConfiguration _analyticsConfiguration;
	private Map<String, String> _channelNames;
	private long[] _classNameIds;
	private String _keywords;
	private final String _mvcRenderCommandName;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}