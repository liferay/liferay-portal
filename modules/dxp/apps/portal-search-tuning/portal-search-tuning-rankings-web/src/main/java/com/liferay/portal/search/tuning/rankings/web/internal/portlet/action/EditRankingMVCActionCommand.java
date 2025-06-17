/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.helper.RankingHelper;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.index.RankingPinBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.storage.RankingStorageAdapter;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.tuning.rankings.web.internal.exception.DuplicateQueryStringException;
import com.liferay.portal.search.tuning.rankings.web.internal.exception.NotApplicableStatusException;
import com.liferay.portal.search.tuning.rankings.web.internal.index.Criteria;
import com.liferay.portal.search.tuning.rankings.web.internal.index.DuplicateQueryStringsDetector;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		"mvc.command.name=/result_rankings/edit_ranking"
	},
	service = MVCActionCommand.class
)
public class EditRankingMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_companyId = portal.getCompanyId(actionRequest);

		EditRankingMVCActionRequest editRankingMVCActionRequest =
			new EditRankingMVCActionRequest(actionRequest);

		if (editRankingMVCActionRequest.isCmd(Constants.ADD)) {
			_add(actionRequest, actionResponse, editRankingMVCActionRequest);
		}
		else if (editRankingMVCActionRequest.isCmd(Constants.UPDATE)) {
			_update(actionRequest, actionResponse, editRankingMVCActionRequest);
		}
		else if (editRankingMVCActionRequest.isCmd(Constants.DELETE)) {
			_delete(actionRequest, actionResponse, editRankingMVCActionRequest);
		}
		else if (editRankingMVCActionRequest.isCmd(
					ResultRankingsConstants.ACTION_ACTIVATE) ||
				 editRankingMVCActionRequest.isCmd(
					 ResultRankingsConstants.ACTION_DEACTIVATE)) {

			_updateStatus(
				actionRequest, actionResponse, editRankingMVCActionRequest);
		}
	}

	@Activate
	protected void activate() {
		_duplicateQueryStringsDetector = new DuplicateQueryStringsDetector(
			_queries, _searchEngineAdapter);
	}

	protected String getIndexName(ActionRequest actionRequest) {
		return indexNameBuilder.getIndexName(
			portal.getCompanyId(actionRequest));
	}

	protected RankingIndexName getRankingIndexName() {
		return rankingIndexNameBuilder.getRankingIndexName(_companyId);
	}

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected Portal portal;

	@Reference
	protected RankingBuilderFactory rankingBuilderFactory;

	@Reference
	protected RankingHelper rankingHelper;

	@Reference
	protected RankingIndexNameBuilder rankingIndexNameBuilder;

	@Reference
	protected RankingIndexReader rankingIndexReader;

	@Reference
	protected RankingPinBuilderFactory rankingPinBuilderFactory;

	@Reference
	protected RankingStorageAdapter rankingStorageAdapter;

	private void _add(
			ActionRequest actionRequest, ActionResponse actionResponse,
			EditRankingMVCActionRequest editRankingMVCActionRequest)
		throws Exception {

		try {
			Ranking ranking = _add(actionRequest, editRankingMVCActionRequest);

			String redirect = _getSaveAndContinueRedirect(
				actionRequest, ranking,
				editRankingMVCActionRequest.getRedirect());

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			actionRequest.setAttribute(
				WebKeys.REDIRECT,
				PortletURLBuilder.createRenderURL(
					portal.getLiferayPortletResponse(actionResponse)
				).setMVCRenderCommandName(
					"/result_rankings/add_results_rankings"
				).setRedirect(
					editRankingMVCActionRequest.getRedirect()
				).buildString());

			SessionErrors.add(actionRequest, exception.getClass());

			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
	}

	private Ranking _add(
		ActionRequest actionRequest,
		EditRankingMVCActionRequest editRankingMVCActionRequest) {

		Ranking.Builder rankingBuilder = rankingBuilderFactory.builder();

		String resultActionCmd = ParamUtil.getString(
			actionRequest, "resultActionCmd");
		String resultActionUid = ParamUtil.getString(
			actionRequest, "resultActionUid");

		if (!resultActionCmd.isEmpty() && !resultActionUid.isEmpty()) {
			if (resultActionCmd.equals("pin")) {
				rankingBuilder.pins(
					Arrays.asList(
						rankingPinBuilderFactory.builder(
						).documentId(
							resultActionUid
						).position(
							0
						).build()));
			}
			else {
				rankingBuilder.hiddenDocumentIds(
					ListUtil.fromString(resultActionUid));
			}
		}

		rankingBuilder.groupExternalReferenceCode(
			editRankingMVCActionRequest.getGroupExternalReferenceCode()
		).indexName(
			getIndexName(actionRequest)
		).name(
			editRankingMVCActionRequest.getQueryString()
		).queryString(
			editRankingMVCActionRequest.getQueryString()
		).status(
			editRankingMVCActionRequest.getStatus()
		).sxpBlueprintExternalReferenceCode(
			editRankingMVCActionRequest.getSXPBlueprintExternalReferenceCode()
		);

		Ranking ranking = rankingBuilder.build();

		_guardDuplicateQueryStrings(editRankingMVCActionRequest, ranking);

		RankingIndexName rankingIndexName = getRankingIndexName();

		String id = rankingStorageAdapter.create(ranking, rankingIndexName);

		return rankingIndexReader.fetch(id, rankingIndexName);
	}

	private void _addExcludedName(
		String key, String name, Map<String, List<String>> excludedNamesMap) {

		List<String> excludedNames = excludedNamesMap.get(key);

		if (excludedNames == null) {
			excludedNames = new ArrayList<>();
		}

		excludedNames.add(name);

		excludedNamesMap.put(key, excludedNames);
	}

	private void _delete(
			ActionRequest actionRequest, ActionResponse actionResponse,
			EditRankingMVCActionRequest editRankingMVCActionRequest)
		throws Exception {

		_delete(actionRequest, editRankingMVCActionRequest);

		sendRedirect(
			actionRequest, actionResponse,
			editRankingMVCActionRequest.getRedirect());
	}

	private void _delete(
			ActionRequest actionRequest,
			EditRankingMVCActionRequest editRankingMVCActionRequest)
		throws Exception {

		String[] rankingDocumentIds = _getRankingDocumentIds(
			actionRequest, editRankingMVCActionRequest);

		for (String rankingDocumentId : rankingDocumentIds) {
			rankingStorageAdapter.delete(
				rankingDocumentId, getRankingIndexName());
		}
	}

	private boolean _detectedDuplicateQueryStrings(
		Ranking ranking, Collection<String> queryStrings) {

		List<String> duplicateQueryStrings =
			_duplicateQueryStringsDetector.detect(
				new Criteria.Builder(
				).groupExternalReferenceCode(
					ranking.getGroupExternalReferenceCode()
				).index(
					_getCompanyIndexName()
				).queryStrings(
					queryStrings
				).rankingIndexName(
					getRankingIndexName()
				).sxpBlueprintExternalReferenceCode(
					ranking.getSXPBlueprintExternalReferenceCode()
				).unlessRankingDocumentId(
					ranking.getRankingDocumentId()
				).build());

		return ListUtil.isNotEmpty(duplicateQueryStrings);
	}

	private List<String> _getAliases(
		EditRankingMVCActionRequest editRankingMVCActionRequest) {

		return ListUtil.filter(
			editRankingMVCActionRequest.getAliases(),
			string -> !_isUpdateSpecial(string));
	}

	private String _getCompanyIndexName() {
		return indexNameBuilder.getIndexName(_companyId);
	}

	private String _getNameForUpdate(
		String oldName,
		EditRankingMVCActionRequest editRankingMVCActionRequest) {

		List<String> strings = TransformUtil.transform(
			editRankingMVCActionRequest.getAliases(),
			alias -> {
				if (_isUpdateSpecial(alias)) {
					return _stripUpdateSpecial(alias);
				}

				return null;
			});

		if (strings.isEmpty()) {
			return oldName;
		}

		return strings.get(0);
	}

	private String[] _getRankingDocumentIds(
		ActionRequest actionRequest,
		EditRankingMVCActionRequest editRankingMVCActionRequest) {

		String[] rankingDocumentIds = null;

		String resultsRankingUid =
			editRankingMVCActionRequest.getResultsRankingUid();

		if (Validator.isNotNull(resultsRankingUid)) {
			rankingDocumentIds = new String[] {resultsRankingUid};
		}
		else {
			rankingDocumentIds = ParamUtil.getStringValues(
				actionRequest, "rowIds");
		}

		return rankingDocumentIds;
	}

	private List<Ranking> _getRankings(
		ActionRequest actionRequest,
		EditRankingMVCActionRequest editRankingMVCActionRequest) {

		RankingIndexName rankingIndexName = getRankingIndexName();

		return TransformUtil.transformToList(
			_getRankingDocumentIds(actionRequest, editRankingMVCActionRequest),
			id -> rankingIndexReader.fetch(id, rankingIndexName));
	}

	private String _getSaveAndContinueRedirect(
			ActionRequest actionRequest, Ranking ranking, String redirect)
		throws Exception {

		PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG);

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, portletConfig.getPortletName(),
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"mvcRenderCommandName", "/result_rankings/edit_results_rankings");
		portletURL.setParameter(Constants.CMD, Constants.UPDATE, false);
		portletURL.setParameter("redirect", redirect, false);
		portletURL.setParameter(
			"resultsRankingUid", ranking.getRankingDocumentId(), false);
		portletURL.setParameter(
			EditRankingMVCActionRequest.PARAM_ALIASES,
			StringUtil.merge(ranking.getAliases(), StringPool.COMMA), false);
		portletURL.setParameter(
			EditRankingMVCActionRequest.PARAM_KEYWORDS,
			ranking.getQueryString(), false);
		portletURL.setWindowState(actionRequest.getWindowState());

		return portletURL.toString();
	}

	private void _guardDuplicateQueryStrings(
		EditRankingMVCActionRequest editRankingMVCActionRequest,
		Ranking ranking) {

		_hasDuplicateQueryString(editRankingMVCActionRequest, ranking, true);
	}

	private boolean _hasDuplicateQueryString(
		EditRankingMVCActionRequest editRankingMVCActionRequest,
		Ranking ranking, boolean throwException) {

		if (editRankingMVCActionRequest.isCmd(
				ResultRankingsConstants.ACTION_DEACTIVATE)) {

			return false;
		}

		Collection<String> queryStrings = ranking.getQueryStrings();

		if (editRankingMVCActionRequest.isCmd(Constants.UPDATE)) {
			if (_isInactive(editRankingMVCActionRequest)) {
				return false;
			}

			queryStrings = rankingHelper.getQueryStrings(
				ranking.getQueryString(),
				_getAliases(editRankingMVCActionRequest));
		}

		if (_detectedDuplicateQueryStrings(ranking, queryStrings)) {
			if (throwException) {
				throw new DuplicateQueryStringException();
			}

			return true;
		}

		return false;
	}

	private boolean _isInactive(
		EditRankingMVCActionRequest editRankingMVCActionRequest) {

		return !Objects.equals(
			editRankingMVCActionRequest.getStatus(),
			ResultRankingsConstants.STATUS_ACTIVE);
	}

	private boolean _isUpdateSpecial(String string) {
		return string.startsWith(_UPDATE_SPECIAL);
	}

	private String _stripUpdateSpecial(String string) {
		return string.substring(_UPDATE_SPECIAL.length());
	}

	private void _update(
			ActionRequest actionRequest, ActionResponse actionResponse,
			EditRankingMVCActionRequest editRankingMVCActionRequest)
		throws IOException {

		try {
			_update(actionRequest, editRankingMVCActionRequest);

			sendRedirect(
				actionRequest, actionResponse,
				editRankingMVCActionRequest.getRedirect());
		}
		catch (Exception exception) {
			if (exception instanceof DuplicateQueryStringException) {
				SessionErrors.add(actionRequest, Exception.class);

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/result_rankings/edit_results_rankings");
			}
			else {
				SessionErrors.add(actionRequest, Exception.class);

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
		}
	}

	private void _update(
			ActionRequest actionRequest,
			EditRankingMVCActionRequest editRankingMVCActionRequest)
		throws PortalException {

		String id = editRankingMVCActionRequest.getResultsRankingUid();

		Ranking ranking = rankingIndexReader.fetch(
			id,
			rankingIndexNameBuilder.getRankingIndexName(
				portal.getCompanyId(actionRequest)));

		if (ranking == null) {
			return;
		}

		if (Objects.equals(
				ranking.getStatus(),
				ResultRankingsConstants.STATUS_NOT_APPLICABLE)) {

			throw new NotApplicableStatusException();
		}

		_guardDuplicateQueryStrings(editRankingMVCActionRequest, ranking);

		Ranking.Builder rankingBuilder = rankingBuilderFactory.builder(ranking);

		String[] addedHiddenIds = ParamUtil.getStringValues(
			actionRequest, "addedHiddenIds");
		String[] removedHiddenIds = ParamUtil.getStringValues(
			actionRequest, "removedHiddenIds");

		rankingBuilder.aliases(
			_getAliases(editRankingMVCActionRequest)
		).groupExternalReferenceCode(
			editRankingMVCActionRequest.getGroupExternalReferenceCode()
		).hiddenDocumentIds(
			_updateHiddenIds(
				addedHiddenIds, ranking.getHiddenDocumentIds(),
				removedHiddenIds)
		).indexName(
			getIndexName(actionRequest)
		).name(
			_getNameForUpdate(ranking.getName(), editRankingMVCActionRequest)
		).status(
			editRankingMVCActionRequest.getStatus()
		).sxpBlueprintExternalReferenceCode(
			editRankingMVCActionRequest.getSXPBlueprintExternalReferenceCode()
		);

		List<Ranking.Pin> pins = new ArrayList<>();

		String[] pinnedIds = ParamUtil.getStringValues(
			actionRequest, "pinnedIds");

		for (int i = 0; i < pinnedIds.length; i++) {
			pins.add(
				rankingPinBuilderFactory.builder(
				).documentId(
					pinnedIds[i]
				).position(
					i
				).build());
		}

		if (ListUtil.isNotEmpty(pins)) {
			rankingBuilder.pins(pins);
		}
		else {
			rankingBuilder.pins(null);
		}

		rankingStorageAdapter.update(
			rankingBuilder.build(), getRankingIndexName());
	}

	private List<String> _updateHiddenIds(
		String[] addedHiddenIds, List<String> currentHiddenIds,
		String[] removedHiddenIds) {

		List<String> hiddenIdsUpdated = null;

		if (ListUtil.isEmpty(currentHiddenIds)) {
			hiddenIdsUpdated = Arrays.asList(addedHiddenIds);
		}
		else {
			hiddenIdsUpdated = rankingHelper.translateDocumentIds(
				currentHiddenIds);

			Collections.addAll(hiddenIdsUpdated, addedHiddenIds);
		}

		hiddenIdsUpdated.removeAll(Arrays.asList(removedHiddenIds));

		return hiddenIdsUpdated;
	}

	private void _updateStatus(
			ActionRequest actionRequest, ActionResponse actionResponse,
			EditRankingMVCActionRequest editRankingMVCActionRequest)
		throws Exception {

		try {
			Map<String, List<String>> excludedNames = _updateStatus(
				actionRequest, editRankingMVCActionRequest);

			if (!excludedNames.isEmpty()) {
				for (Map.Entry<String, List<String>> entry :
						excludedNames.entrySet()) {

					String key = entry.getKey();

					if (key.equals(_KEY_DUPLICATE)) {
						SessionErrors.add(
							actionRequest, DuplicateQueryStringException.class,
							ListUtil.unique(entry.getValue()));
					}
					else if (key.equals(_KEY_NOT_APPLICABLE)) {
						SessionErrors.add(
							actionRequest, NotApplicableStatusException.class,
							ListUtil.unique(entry.getValue()));
					}
				}

				hideDefaultErrorMessage(actionRequest);

				sendRedirect(actionRequest, actionResponse);

				return;
			}

			sendRedirect(
				actionRequest, actionResponse,
				editRankingMVCActionRequest.getRedirect());
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
	}

	private Map<String, List<String>> _updateStatus(
			ActionRequest actionRequest,
			EditRankingMVCActionRequest editRankingMVCActionRequest)
		throws PortalException {

		Map<String, List<String>> excludedNames = new HashMap<>();

		List<Ranking> rankings = _getRankings(
			actionRequest, editRankingMVCActionRequest);

		for (Ranking ranking : rankings) {
			if (Objects.equals(
					ranking.getStatus(),
					ResultRankingsConstants.STATUS_NOT_APPLICABLE)) {

				_addExcludedName(
					_KEY_NOT_APPLICABLE, ranking.getName(), excludedNames);

				continue;
			}

			if (editRankingMVCActionRequest.isCmd(
					ResultRankingsConstants.ACTION_ACTIVATE) &&
				_hasDuplicateQueryString(
					editRankingMVCActionRequest, ranking, false)) {

				_addExcludedName(
					_KEY_DUPLICATE, ranking.getName(), excludedNames);

				continue;
			}

			Ranking.Builder rankingBuilder = rankingBuilderFactory.builder(
				ranking);

			if (editRankingMVCActionRequest.isCmd(
					ResultRankingsConstants.ACTION_ACTIVATE)) {

				rankingBuilder.status(ResultRankingsConstants.STATUS_ACTIVE);
			}
			else {
				rankingBuilder.status(ResultRankingsConstants.STATUS_INACTIVE);
			}

			rankingStorageAdapter.update(
				rankingBuilder.build(), getRankingIndexName());
		}

		return excludedNames;
	}

	private static final String _KEY_DUPLICATE = "duplicate";

	private static final String _KEY_NOT_APPLICABLE = "not-applicable";

	private static final String _UPDATE_SPECIAL = StringPool.GREATER_THAN;

	private long _companyId;
	private DuplicateQueryStringsDetector _duplicateQueryStringsDetector;

	@Reference
	private Queries _queries;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	private class EditRankingMVCActionRequest {

		public static final String PARAM_ALIASES = "aliases";

		public static final String PARAM_KEYWORDS = "keywords";

		public EditRankingMVCActionRequest(ActionRequest actionRequest) {
			_aliases = Arrays.asList(
				ParamUtil.getStringValues(actionRequest, PARAM_ALIASES));
			_cmd = ParamUtil.getString(actionRequest, Constants.CMD);
			_groupExternalReferenceCode = ParamUtil.getString(
				actionRequest, "groupExternalReferenceCode");
			_queryString = ParamUtil.getString(actionRequest, PARAM_KEYWORDS);
			_redirect = ParamUtil.getString(actionRequest, "redirect");
			_resultsRankingUid = ParamUtil.getString(
				actionRequest, "resultsRankingUid");
			_status = ParamUtil.getString(actionRequest, "status");
			_sxpBlueprintExternalReferenceCode = ParamUtil.getString(
				actionRequest, "sxpBlueprintExternalReferenceCode");
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

		public String getRedirect() {
			return _redirect;
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

		public boolean isCmd(String cmd) {
			return Objects.equals(cmd, _cmd);
		}

		private final List<String> _aliases;
		private final String _cmd;
		private final String _groupExternalReferenceCode;
		private final String _queryString;
		private final String _redirect;
		private final String _resultsRankingUid;
		private final String _status;
		private final String _sxpBlueprintExternalReferenceCode;

	}

}