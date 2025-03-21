/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet.action;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.configuration.SynonymsConfiguration;
import com.liferay.portal.search.tuning.synonyms.web.internal.constants.SynonymsPortletKeys;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;
import com.liferay.portal.search.tuning.synonyms.web.internal.storage.SynonymSetStorageAdapter;
import com.liferay.portal.search.tuning.synonyms.web.internal.synchronizer.IndexToFilterSynchronizer;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	configurationPid = "com.liferay.portal.search.tuning.synonyms.web.internal.configuration.SynonymsConfiguration",
	property = {
		"jakarta.portlet.name=" + SynonymsPortletKeys.SYNONYMS,
		"mvc.command.name=/synonyms/delete_synonym_sets"
	},
	service = MVCActionCommand.class
)
public class DeleteSynonymSetsMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_synonymSetIndexReader = new SynonymSetIndexReader(
			_searchEngineAdapter);

		modified(properties);
	}

	protected void deleteSynonymSets(ActionRequest actionRequest)
		throws PortalException {

		long companyId = _portal.getCompanyId(actionRequest);

		SynonymSetIndexName synonymSetIndexName =
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(companyId);

		List<SynonymSet> synonymSets = getDeletedSynonymSets(
			actionRequest, synonymSetIndexName);

		if (ListUtil.isEmpty(synonymSets)) {
			if (!ParamUtil.getBoolean(actionRequest, "deleteAllSynonymSets")) {
				return;
			}

			synonymSets = _synonymSetIndexReader.search(synonymSetIndexName);
		}

		deleteSynonymSets(synonymSetIndexName, synonymSets);

		_indexToFilterSynchronizer.copyToFilter(
			synonymSetIndexName, _indexNameBuilder.getIndexName(companyId),
			true);
	}

	protected void deleteSynonymSets(
			SynonymSetIndexName synonymSetIndexName,
			List<SynonymSet> synonymSets)
		throws PortalException {

		for (SynonymSet synonymSet : synonymSets) {
			_synonymSetStorageAdapter.delete(
				synonymSetIndexName, synonymSet.getSynonymSetDocumentId());
		}
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		deleteSynonymSets(actionRequest);

		sendRedirect(actionRequest, actionResponse);
	}

	protected List<SynonymSet> getDeletedSynonymSets(
		ActionRequest actionRequest, SynonymSetIndexName synonymSetIndexName) {

		return TransformUtil.transformToList(
			ParamUtil.getStringValues(actionRequest, "rowIds"),
			id -> _synonymSetIndexReader.fetch(synonymSetIndexName, id));
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		SynonymsConfiguration synonymsConfiguration =
			ConfigurableUtil.createConfigurable(
				SynonymsConfiguration.class, properties);

		_indexToFilterSynchronizer = new IndexToFilterSynchronizer(
			synonymsConfiguration.filterNames(), _searchEngineAdapter,
			_synonymSetIndexReader);
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private volatile IndexToFilterSynchronizer _indexToFilterSynchronizer;

	@Reference
	private Portal _portal;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	private SynonymSetIndexReader _synonymSetIndexReader;

	@Reference
	private SynonymSetStorageAdapter _synonymSetStorageAdapter;

}