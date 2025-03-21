/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import com.liferay.learn.LearnMessageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;

import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Kevin Tan
 * @author Petteri Karttunen
 */
public class EditRankingDisplayBuilder {

	public EditRankingDisplayBuilder(
		HttpServletRequest httpServletRequest,
		RankingIndexNameBuilder rankingIndexNameBuilder,
		RankingIndexReader rankingIndexReader, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_rankingIndexNameBuilder = rankingIndexNameBuilder;
		_rankingIndexReader = rankingIndexReader;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_ranking = _fetchRanking();
	}

	public EditRankingDisplayContext build() {
		EditRankingDisplayContext editRankingDisplayContext =
			new EditRankingDisplayContext();

		_setBackURL(editRankingDisplayContext);
		_setCompanyId(editRankingDisplayContext);
		_setData(editRankingDisplayContext);
		_setFormName(editRankingDisplayContext);
		_setKeywords(editRankingDisplayContext);
		_setRedirect(editRankingDisplayContext);
		_setResultsRankingUid(editRankingDisplayContext);
		_setStatus(editRankingDisplayContext);

		return editRankingDisplayContext;
	}

	private Ranking _fetchRanking() {
		String resultsRankingUid = _getResultsRankingUid();

		if (!Validator.isBlank(resultsRankingUid)) {
			return _rankingIndexReader.fetch(
				resultsRankingUid, _getRankingIndexName());
		}

		return null;
	}

	private String[] _getAliases() {
		if (_ranking != null) {
			return ArrayUtil.toStringArray(_ranking.getAliases());
		}

		return new String[0];
	}

	private Map<String, Object> _getConstants() {
		return HashMapBuilder.<String, Object>put(
			"WORKFLOW_ACTION_PUBLISH", WorkflowConstants.ACTION_PUBLISH
		).put(
			"WORKFLOW_ACTION_SAVE_DRAFT", WorkflowConstants.ACTION_SAVE_DRAFT
		).build();
	}

	private Map<String, Object> _getContext() {
		return HashMapBuilder.<String, Object>put(
			"companyId", String.valueOf(_themeDisplay.getCompanyId())
		).put(
			"constants", _getConstants()
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).build();
	}

	private String _getFormName() {
		return "editResultRankingsFm";
	}

	private String _getGroupExternalReferenceCode() {
		if (_ranking != null) {
			return _ranking.getGroupExternalReferenceCode();
		}

		return null;
	}

	private String _getHiddenResultRankingsResourceURL() {
		ResourceURL resourceURL = _renderResponse.createResourceURL();

		resourceURL.setParameter(
			"companyId", String.valueOf(_themeDisplay.getCompanyId()));
		resourceURL.setParameter(Constants.CMD, "getHiddenResultsJSONObject");
		resourceURL.setParameter("resultsRankingUid", _getResultsRankingUid());
		resourceURL.setResourceID("/result_rankings/get_results");

		return resourceURL.toString();
	}

	private String _getKeywords() {
		return ParamUtil.getString(_httpServletRequest, "keywords");
	}

	private Map<String, Object> _getProps() {
		return HashMapBuilder.<String, Object>put(
			"cancelURL", HtmlUtil.escape(_getRedirect())
		).put(
			"fetchDocumentsHiddenURL", _getHiddenResultRankingsResourceURL()
		).put(
			"fetchDocumentsSearchURL", _getSearchResultRankingsResourceURL()
		).put(
			"fetchDocumentsVisibleURL", _getVisibleResultRankingsResourceURL()
		).put(
			"formName", _renderResponse.getNamespace() + _getFormName()
		).put(
			"initialAliases", _getAliases()
		).put(
			"initialGroupExternalReferenceCode",
			_getGroupExternalReferenceCode()
		).put(
			"initialStatus", _getStatus()
		).put(
			"initialSXPBlueprintExternalReferenceCode",
			_getSXPBlueprintExternalReferenceCode()
		).put(
			"learnResources",
			LearnMessageUtil.getReactDataJSONObject(
				"portal-search-tuning-rankings-web")
		).put(
			"resultsRankingUid", _getResultsRankingUid()
		).put(
			"searchQuery", _getKeywords()
		).put(
			"siteDisplayName",
			() -> {
				Group group =
					GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
						_getGroupExternalReferenceCode(),
						_themeDisplay.getCompanyId());

				if (group == null) {
					return "descriptiveName";
				}

				return group.getDescriptiveName(_themeDisplay.getLocale());
			}
		).put(
			"validateFormURL", _getValidateResultRankingsResourceURL()
		).build();
	}

	private RankingIndexName _getRankingIndexName() {
		return _rankingIndexNameBuilder.getRankingIndexName(
			_themeDisplay.getCompanyId());
	}

	private String _getRedirect() {
		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNull(redirect)) {
			redirect = String.valueOf(_renderResponse.createRenderURL());
		}

		return redirect;
	}

	private String _getResultsRankingUid() {
		return ParamUtil.getString(_httpServletRequest, "resultsRankingUid");
	}

	private String _getSearchResultRankingsResourceURL() {
		ResourceURL resourceURL = _renderResponse.createResourceURL();

		resourceURL.setParameter(
			"companyId", String.valueOf(_themeDisplay.getCompanyId()));
		resourceURL.setParameter(Constants.CMD, "getSearchResultsJSONObject");
		resourceURL.setResourceID("/result_rankings/get_results");

		return resourceURL.toString();
	}

	private String _getStatus() {
		if (_ranking != null) {
			return _ranking.getStatus();
		}

		return ResultRankingsConstants.STATUS_ACTIVE;
	}

	private String _getSXPBlueprintExternalReferenceCode() {
		if (_ranking != null) {
			return _ranking.getSXPBlueprintExternalReferenceCode();
		}

		return null;
	}

	private String _getValidateResultRankingsResourceURL() {
		ResourceURL resourceURL = _renderResponse.createResourceURL();

		resourceURL.setResourceID("/result_rankings/validate_ranking");

		return resourceURL.toString();
	}

	private String _getVisibleResultRankingsResourceURL() {
		ResourceURL resourceURL = _renderResponse.createResourceURL();

		resourceURL.setParameter(
			"companyId", String.valueOf(_themeDisplay.getCompanyId()));
		resourceURL.setParameter(Constants.CMD, "getVisibleResultsJSONObject");
		resourceURL.setParameter("resultsRankingUid", _getResultsRankingUid());
		resourceURL.setResourceID("/result_rankings/get_results");

		return resourceURL.toString();
	}

	private void _setBackURL(
		EditRankingDisplayContext editRankingDisplayContext) {

		editRankingDisplayContext.setBackURL(
			ParamUtil.getString(
				_httpServletRequest, "backURL", _getRedirect()));
	}

	private void _setCompanyId(
		EditRankingDisplayContext editRankingDisplayContext) {

		editRankingDisplayContext.setCompanyId(_themeDisplay.getCompanyId());
	}

	private void _setData(EditRankingDisplayContext editRankingDisplayContext) {
		editRankingDisplayContext.setData(
			HashMapBuilder.<String, Object>put(
				"context", _getContext()
			).put(
				"props", _getProps()
			).build());
	}

	private void _setFormName(
		EditRankingDisplayContext editRankingDisplayContext) {

		editRankingDisplayContext.setFormName(_getFormName());
	}

	private void _setKeywords(
		EditRankingDisplayContext editRankingDisplayContext) {

		editRankingDisplayContext.setKeywords(_getKeywords());
	}

	private void _setRedirect(
		EditRankingDisplayContext editRankingDisplayContext) {

		editRankingDisplayContext.setRedirect(_getRedirect());
	}

	private void _setResultsRankingUid(
		EditRankingDisplayContext editRankingDisplayContext) {

		editRankingDisplayContext.setResultsRankingUid(_getResultsRankingUid());
	}

	private void _setStatus(
		EditRankingDisplayContext editRankingDisplayContext) {

		editRankingDisplayContext.setStatus(_getStatus());
	}

	private final HttpServletRequest _httpServletRequest;
	private final Ranking _ranking;
	private final RankingIndexNameBuilder _rankingIndexNameBuilder;
	private final RankingIndexReader _rankingIndexReader;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}