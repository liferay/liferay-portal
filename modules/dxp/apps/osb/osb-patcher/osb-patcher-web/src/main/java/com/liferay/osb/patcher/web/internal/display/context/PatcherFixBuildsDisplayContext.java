/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixBuildsDisplayContext {

	public PatcherFixBuildsDisplayContext(
		HttpServletRequest httpServletRequest,
		PatcherConfiguration patcherConfiguration, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_patcherConfiguration = patcherConfiguration;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems(PatcherBuild patcherBuild) {
		PatcherFix patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			getPatcherFixId());

		return DropdownItemListBuilder.add(
			() ->
				Validator.isNotNull(patcherFix.getGitHash()) &&
				JenkinsUtil.isValidJenkinsSetup(_themeDisplay.getCompanyId()) &&
				JenkinsUtil.isValidSendDistJenkinsRequest(patcherBuild),
			dropdownItem -> {
				dropdownItem.putData("action", "submitForm");
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/patcher/build_builds"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherBuildId", patcherBuild.getPatcherBuildId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "build"));
			}
		).add(
			() ->
				patcherBuild.getStatus() ==
					WorkflowConstants.STATUS_BUILD_COMPLETE,
			dropdownItem -> {
				dropdownItem.putData("action", "submitForm");
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/patcher/test_builds"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherBuildId", patcherBuild.getPatcherBuildId()
					).setParameter(
						"status",
						WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "test"));
			}
		).add(
			() ->
				patcherBuild.getStatus() ==
					WorkflowConstants.STATUS_BUILD_COMPLETE,
			dropdownItem -> {
				dropdownItem.putData("action", "submitForm");
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/patcher/test_builds"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherBuildId", patcherBuild.getPatcherBuildId()
					).setParameter(
						"status",
						WorkflowConstants.
							STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "smoke-test"));
			}
		).add(
			() ->
				patcherBuild.getStatus() ==
					WorkflowConstants.STATUS_BUILD_COMPLETE,
			dropdownItem -> {
				String fileName = patcherBuild.getFileName();

				String hotfixURL =
					"https://storage.cloud.google.com/liferay-releases-hotfix" + fileName;

				if (!fileName.contains("/liferay-dxp-")) {
					hotfixURL =
						_patcherConfiguration.patcherBuildDownloadURL() + "/" +
							fileName;
				}

				dropdownItem.setHref(hotfixURL);

				dropdownItem.setIcon("download");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "download"));
			}
		).build();
	}

	public long getPatcherFixId() {
		if (_patcherFixId != null) {
			return _patcherFixId;
		}

		_patcherFixId = ParamUtil.getLong(_httpServletRequest, "patcherFixId");

		return _patcherFixId;
	}

	public SearchContainer<PatcherBuild> getSearchContainer() throws Exception {
		if (_patcherBuildSearchContainer != null) {
			return _patcherBuildSearchContainer;
		}

		SearchContainer<PatcherBuild> patcherBuildSearchContainer =
			new SearchContainer<>(
				_renderRequest, _getPortletURL(), null, "there-are-no-builds");

		Indexer<PatcherBuild> indexer = IndexerRegistryUtil.getIndexer(
			PatcherBuild.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setAttribute("accountEntryCode", _getAccountEntryCode());
		searchContext.setAttribute(
			"patcherProjectVersionId", _getPatcherProjectVersionId());
		searchContext.setAttribute("qaStatus", _getQAStatus());
		searchContext.setAttribute("status", _getStatus());
		searchContext.setAttribute("type", _getType());
		searchContext.setEnd(patcherBuildSearchContainer.getEnd());
		searchContext.setGroupIds(null);

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		String patcherBuildName = ParamUtil.getString(
			_httpServletRequest, "patcherBuildName");

		if ((!PatcherUtil.isPatcherTickets(keywords) ||
			 PatcherUtil.isPatcherProjectVersionName(keywords)) &&
			!PatcherUtil.isPatcherTickets(patcherBuildName)) {

			searchContext.setSorts(
				new Sort("statusDate", Sort.LONG_TYPE, true));
		}

		searchContext.setStart(patcherBuildSearchContainer.getStart());

		Hits hits = indexer.search(searchContext);

		patcherBuildSearchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				SearchResultUtil.getSearchResults(
					hits, LocaleUtil.getDefault()),
				searchResult -> PatcherBuildLocalServiceUtil.fetchPatcherBuild(
					searchResult.getClassPK())),
			hits.getLength());

		_patcherBuildSearchContainer = patcherBuildSearchContainer;

		return _patcherBuildSearchContainer;
	}

	private String _getAccountEntryCode() {
		if (_accountEntryCode != null) {
			return _accountEntryCode;
		}

		_accountEntryCode = ParamUtil.getString(
			_httpServletRequest, "accountEntryCode");

		return _accountEntryCode;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private long _getPatcherProjectVersionId() {
		if (_patcherProjectVersionId != null) {
			return _patcherProjectVersionId;
		}

		_patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		return _patcherProjectVersionId;
	}

	private PortletURL _getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/patcher/index_builds"
		).setKeywords(
			_getKeywords()
		).setTabs1(
			"builds"
		).setParameter(
			"patcherProjectVersionId", _getPatcherProjectVersionId()
		).setParameter(
			"qaStatus", _getQAStatus()
		).setParameter(
			"status", _getStatus()
		).setParameter(
			"type", _getType()
		).buildPortletURL();

		return _portletURL;
	}

	private int _getQAStatus() {
		if (_qaStatus != null) {
			return _qaStatus;
		}

		_qaStatus = ParamUtil.getInteger(
			_httpServletRequest, "qaStatus", WorkflowConstants.STATUS_ANY);

		return _qaStatus;
	}

	private int _getStatus() {
		if (_status != null) {
			return _status;
		}

		_status = ParamUtil.getInteger(
			_httpServletRequest, "status", WorkflowConstants.STATUS_ANY);

		return _status;
	}

	private int _getType() {
		if (_type != null) {
			return _type;
		}

		_type = ParamUtil.getInteger(
			_httpServletRequest, "type", PatcherBuildConstants.TYPE_ANY);

		return _type;
	}

	private String _accountEntryCode;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private SearchContainer<PatcherBuild> _patcherBuildSearchContainer;
	private final PatcherConfiguration _patcherConfiguration;
	private Long _patcherFixId;
	private Long _patcherProjectVersionId;
	private PortletURL _portletURL;
	private Integer _qaStatus;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Integer _status;
	private final ThemeDisplay _themeDisplay;
	private Integer _type;

}