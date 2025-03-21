/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
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
		"mvc.command.name=/synonyms/edit_synonym_sets"
	},
	service = MVCActionCommand.class
)
public class EditSynonymSetsMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_synonymSetIndexReader = new SynonymSetIndexReader(
			_searchEngineAdapter);

		modified(properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		updateSynonymSets(
			actionRequest, ParamUtil.getString(actionRequest, "synonymSet"));

		sendRedirect(actionRequest, actionResponse);
	}

	protected SynonymSet getSynonymSet(
		SynonymSetIndexName synonymSetIndexName, ActionRequest actionRequest) {

		String synonymSetId = ParamUtil.getString(
			actionRequest, "synonymSetId", null);

		if (synonymSetId == null) {
			return null;
		}

		return _synonymSetIndexReader.fetch(synonymSetIndexName, synonymSetId);
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

	protected void updateSynonymSetIndex(
			SynonymSetIndexName synonymSetIndexName, String synonyms,
			SynonymSet synonymSet)
		throws PortalException {

		SynonymSet.SynonymSetBuilder synonymSetBuilder =
			new SynonymSet.SynonymSetBuilder();

		synonymSetBuilder.synonyms(synonyms);

		if (synonymSet == null) {
			_synonymSetStorageAdapter.create(
				synonymSetIndexName, synonymSetBuilder.build());
		}
		else {
			synonymSetBuilder.synonymSetDocumentId(
				synonymSet.getSynonymSetDocumentId());

			_synonymSetStorageAdapter.update(
				synonymSetIndexName, synonymSetBuilder.build());
		}
	}

	protected void updateSynonymSets(
			ActionRequest actionRequest, String... synonymSets)
		throws PortalException {

		long companyId = portal.getCompanyId(actionRequest);

		SynonymSetIndexName synonymSetIndexName =
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(companyId);

		SynonymSet synonymSet = getSynonymSet(
			synonymSetIndexName, actionRequest);

		for (String synonyms : synonymSets) {
			updateSynonymSetIndex(synonymSetIndexName, synonyms, synonymSet);
		}

		_indexToFilterSynchronizer.copyToFilter(
			synonymSetIndexName, _indexNameBuilder.getIndexName(companyId),
			false);
	}

	@Reference
	protected Portal portal;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private volatile IndexToFilterSynchronizer _indexToFilterSynchronizer;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	private SynonymSetIndexReader _synonymSetIndexReader;

	@Reference
	private SynonymSetStorageAdapter _synonymSetStorageAdapter;

}