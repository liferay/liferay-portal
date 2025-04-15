/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.helper.RankingHelper;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.tuning.rankings.web.internal.index.Criteria;
import com.liferay.portal.search.tuning.rankings.web.internal.index.DuplicateQueryStringsDetector;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ResultRankingsPortletKeys.RESULT_RANKINGS,
		"mvc.command.name=/result_rankings/validate_ranking"
	},
	service = MVCResourceCommand.class
)
public class ValidateRankingMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		try {
			writeJSONPortletResponse(
				resourceRequest, resourceResponse,
				getJSONObject(resourceRequest));

			return false;
		}
		catch (RuntimeException runtimeException) {
			_log.error(runtimeException);

			throw runtimeException;
		}
	}

	@Activate
	protected void activate() {
		_duplicateQueryStringsDetector = new DuplicateQueryStringsDetector(
			_queries, _searchEngineAdapter);
	}

	protected JSONObject getJSONObject(ResourceRequest resourceRequest) {
		ValidateRankingMVCResourceRequest validateRankingMVCResourceRequest =
			new ValidateRankingMVCResourceRequest(resourceRequest);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<String> duplicateQueryStrings = _getDuplicateQueryStrings(
			resourceRequest, validateRankingMVCResourceRequest);

		if (ListUtil.isNotEmpty(duplicateQueryStrings) &&
			Objects.equals(
				validateRankingMVCResourceRequest.getStatus(),
				ResultRankingsConstants.STATUS_ACTIVE)) {

			jsonArray.put(
				_language.format(
					portal.getHttpServletRequest(resourceRequest),
					"active-rankings-must-be-unique-x",
					StringUtil.merge(
						duplicateQueryStrings, StringPool.COMMA_AND_SPACE),
					false));
		}

		return JSONUtil.put("errors", jsonArray);
	}

	protected void writeJSONPortletResponse(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		JSONObject jsonObject) {

		if (jsonObject == null) {
			return;
		}

		try {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected Portal portal;

	@Reference
	protected RankingIndexNameBuilder rankingIndexNameBuilder;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	private List<String> _getAliases(
		ValidateRankingMVCResourceRequest validateRankingMVCResourceRequest) {

		return ListUtil.filter(
			validateRankingMVCResourceRequest.getAliases(),
			alias -> !_isUpdateSpecial(alias));
	}

	private long _getCompanyId(ResourceRequest resourceRequest) {
		return portal.getCompanyId(resourceRequest);
	}

	private List<String> _getDuplicateQueryStrings(
		ResourceRequest resourceRequest,
		ValidateRankingMVCResourceRequest validateRankingMVCResourceRequest) {

		return _duplicateQueryStringsDetector.detect(
			new Criteria.Builder(
			).index(
				_getIndexName(resourceRequest)
			).groupExternalReferenceCode(
				validateRankingMVCResourceRequest.
					getGroupExternalReferenceCode()
			).queryStrings(
				_rankingHelper.getQueryStrings(
					validateRankingMVCResourceRequest.getQueryString(),
					_getAliases(validateRankingMVCResourceRequest))
			).rankingIndexName(
				_getRankingIndexName(resourceRequest)
			).sxpBlueprintExternalReferenceCode(
				validateRankingMVCResourceRequest.
					getSXPBlueprintExternalReferenceCode()
			).unlessRankingDocumentId(
				validateRankingMVCResourceRequest.getResultsRankingUid()
			).build());
	}

	private String _getIndexName(ResourceRequest resourceRequest) {
		return indexNameBuilder.getIndexName(_getCompanyId(resourceRequest));
	}

	private RankingIndexName _getRankingIndexName(
		ResourceRequest resourceRequest) {

		return rankingIndexNameBuilder.getRankingIndexName(
			_getCompanyId(resourceRequest));
	}

	private boolean _isUpdateSpecial(String string) {
		return string.startsWith(_UPDATE_SPECIAL);
	}

	private static final String _UPDATE_SPECIAL = StringPool.GREATER_THAN;

	private static final Log _log = LogFactoryUtil.getLog(
		ValidateRankingMVCResourceCommand.class);

	private DuplicateQueryStringsDetector _duplicateQueryStringsDetector;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Queries _queries;

	@Reference
	private RankingHelper _rankingHelper;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	private class ValidateRankingMVCResourceRequest {

		public ValidateRankingMVCResourceRequest(
			ResourceRequest resourceRequest) {

			_aliases = Arrays.asList(
				ParamUtil.getStringValues(resourceRequest, "aliases"));
			_groupExternalReferenceCode = ParamUtil.getString(
				resourceRequest, "groupExternalReferenceCode");
			_queryString = ParamUtil.getString(resourceRequest, "keywords");
			_resultsRankingUid = ParamUtil.getString(
				resourceRequest, "resultsRankingUid");
			_status = ParamUtil.getString(resourceRequest, "status");
			_sxpBlueprintExternalReferenceCode = ParamUtil.getString(
				resourceRequest, "sxpBlueprintExternalReferenceCode");
		}

		public List<String> getAliases() {
			return Collections.unmodifiableList(_aliases);
		}

		public String getGroupExternalReferenceCode() {
			return _groupExternalReferenceCode;
		}

		public String getQueryString() {
			return _queryString;
		}

		public String getResultsRankingUid() {
			return _resultsRankingUid;
		}

		public String getStatus() {
			return _status;
		}

		public String getSXPBlueprintExternalReferenceCode() {
			return _sxpBlueprintExternalReferenceCode;
		}

		private final List<String> _aliases;
		private final String _groupExternalReferenceCode;
		private final String _queryString;
		private final String _resultsRankingUid;
		private final String _status;
		private final String _sxpBlueprintExternalReferenceCode;

	}

}