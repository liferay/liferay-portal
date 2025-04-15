/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.web.internal.display.context;

import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.service.MicroblogsEntryLocalServiceUtil;
import com.liferay.microblogs.service.MicroblogsEntryServiceUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mariano Álvaro Sáiz
 */
public class MicroblogsDisplayContext {

	public MicroblogsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public PortletURL getMicroblogsEntriesURL() {
		if (_microblogsEntriesURL != null) {
			return _microblogsEntriesURL;
		}

		_microblogsEntriesURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/microblogs/view.jsp"
		).setTabs1(
			_getTabs1()
		).setParameter(
			"cur",
			ParamUtil.getInteger(
				_httpServletRequest, SearchContainer.DEFAULT_CUR_PARAM)
		).setWindowState(
			LiferayWindowState.EXCLUSIVE
		).buildPortletURL();

		return _microblogsEntriesURL;
	}

	public long getParentMicroblogsEntryId() {
		if (_parentMicroblogsEntryId != null) {
			return _parentMicroblogsEntryId;
		}

		_parentMicroblogsEntryId = ParamUtil.getLong(
			_httpServletRequest, "parentMicroblogsEntryId");

		return _parentMicroblogsEntryId;
	}

	public PortletURL getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/microblogs/view.jsp"
		).setTabs1(
			_getTabs1()
		).setWindowState(
			WindowState.NORMAL
		).buildPortletURL();

		return _portletURL;
	}

	public SearchContainer<MicroblogsEntry> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer(
			_renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 10,
			getPortletURL(), null, null);

		_searchContainer.setDeltaConfigurable(false);

		_setSearchContainerResultsAndTotal();

		return _searchContainer;
	}

	public List<MicroblogsEntry> getSearchContainerResults()
		throws PortalException {

		SearchContainer<MicroblogsEntry> searchContainer = getSearchContainer();

		return searchContainer.getResults();
	}

	public String getTabs1Names() {
		if (_tabs1Names != null) {
			return _tabs1Names;
		}

		_tabs1Names = "timeline,mentions";

		if (!Objects.equals(_getTabs1(), "mentions") &&
			!Objects.equals(_getTabs1(), "timeline")) {

			_tabs1Names += "," + _getTabs1();
		}

		return _tabs1Names;
	}

	public boolean isUserPublicPage() {
		if (_userPublicPage != null) {
			return _userPublicPage;
		}

		_userPublicPage = false;

		Group group = _themeDisplay.getScopeGroup();
		Layout layout = _themeDisplay.getLayout();

		if (group.isUser() && layout.isPublicLayout()) {
			_userPublicPage = true;
		}

		return _userPublicPage;
	}

	private String _getAssetTagName() {
		if (_assetTagName != null) {
			return _assetTagName;
		}

		_assetTagName = ParamUtil.getString(
			_httpServletRequest, "assetTagName");

		if (!Objects.equals(_getTabs1(), "mentions") &&
			!Objects.equals(_getTabs1(), "timeline")) {

			_assetTagName = StringUtil.toLowerCase(_getTabs1());
		}

		return _assetTagName;
	}

	private long _getReceiverUserId() {
		if (_receiverUserId != null) {
			return _receiverUserId;
		}

		_receiverUserId = ParamUtil.getLong(
			_httpServletRequest, "receiverUserId");

		return _receiverUserId;
	}

	private String _getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_httpServletRequest, "tabs1", "timeline");

		return _tabs1;
	}

	private void _setSearchContainerResultsAndTotal() throws PortalException {
		Group group = _themeDisplay.getScopeGroup();

		if (Objects.equals(_getTabs1(), "mentions")) {
			long receiverUserId = _themeDisplay.getUserId();

			if (isUserPublicPage()) {
				receiverUserId = group.getClassPK();
			}

			String assetTagName = _getAssetTagName();

			try {
				User taggedUser = UserLocalServiceUtil.getUserById(
					receiverUserId);

				assetTagName = taggedUser.getScreenName();
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			String microblogsAssetTagName = assetTagName;

			_searchContainer.setResultsAndTotal(
				() -> MicroblogsEntryServiceUtil.getMicroblogsEntries(
					microblogsAssetTagName, _searchContainer.getStart(),
					_searchContainer.getEnd()),
				MicroblogsEntryServiceUtil.getMicroblogsEntriesCount(
					microblogsAssetTagName));
		}
		else if (getParentMicroblogsEntryId() > 0) {
			List<MicroblogsEntry> results = new ArrayList<>();

			MicroblogsEntry microblogsEntry =
				MicroblogsEntryLocalServiceUtil.fetchMicroblogsEntry(
					getParentMicroblogsEntryId());

			if (microblogsEntry != null) {
				results.add(microblogsEntry);
			}

			_searchContainer.setResultsAndTotal(() -> results, results.size());

			_portletURL.setParameter(
				"parentMicroblogsEntryId",
				String.valueOf(getParentMicroblogsEntryId()));
		}
		else if ((_getReceiverUserId() > 0) &&
				 (_getReceiverUserId() == _themeDisplay.getUserId())) {

			_searchContainer.setResultsAndTotal(
				() -> MicroblogsEntryLocalServiceUtil.getUserMicroblogsEntries(
					_getReceiverUserId(), _searchContainer.getStart(),
					_searchContainer.getEnd()),
				MicroblogsEntryLocalServiceUtil.getUserMicroblogsEntriesCount(
					_getReceiverUserId()));

			_portletURL.setParameter(
				"receiverUserId", String.valueOf(_getReceiverUserId()));
		}
		else if (_getReceiverUserId() > 0) {
			_searchContainer.setResultsAndTotal(
				() -> MicroblogsEntryServiceUtil.getUserMicroblogsEntries(
					_getReceiverUserId(), _searchContainer.getStart(),
					_searchContainer.getEnd()),
				MicroblogsEntryServiceUtil.getUserMicroblogsEntriesCount(
					_getReceiverUserId()));

			_portletURL.setParameter(
				"receiverUserId", String.valueOf(_getReceiverUserId()));
		}
		else if (Validator.isNotNull(_getAssetTagName())) {
			_searchContainer.setResultsAndTotal(
				() -> MicroblogsEntryServiceUtil.getMicroblogsEntries(
					_getAssetTagName(), _searchContainer.getStart(),
					_searchContainer.getEnd()),
				MicroblogsEntryServiceUtil.getMicroblogsEntriesCount(
					_getAssetTagName()));

			_portletURL.setParameter("assetTagName", _getAssetTagName());
		}
		else if (Objects.equals(_getTabs1(), "timeline")) {
			if (isUserPublicPage()) {
				_searchContainer.setResultsAndTotal(
					() -> MicroblogsEntryServiceUtil.getUserMicroblogsEntries(
						group.getClassPK(), _searchContainer.getStart(),
						_searchContainer.getEnd()),
					MicroblogsEntryServiceUtil.getUserMicroblogsEntriesCount(
						group.getClassPK()));
			}
			else {
				_searchContainer.setResultsAndTotal(
					() -> MicroblogsEntryServiceUtil.getMicroblogsEntries(
						_searchContainer.getStart(), _searchContainer.getEnd()),
					MicroblogsEntryServiceUtil.getMicroblogsEntriesCount());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MicroblogsDisplayContext.class);

	private String _assetTagName;
	private final HttpServletRequest _httpServletRequest;
	private PortletURL _microblogsEntriesURL;
	private Long _parentMicroblogsEntryId;
	private PortletURL _portletURL;
	private Long _receiverUserId;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<MicroblogsEntry> _searchContainer;
	private String _tabs1;
	private String _tabs1Names;
	private final ThemeDisplay _themeDisplay;
	private Boolean _userPublicPage;

}