/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.search.instance.lifecycle;

import com.liferay.analytics.machine.learning.internal.recommendation.constants.RecommendationIndexNames;
import com.liferay.analytics.machine.learning.internal.recommendation.search.RecommendationIndexer;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class RecommendationIndexerPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		try {
			for (RecommendationIndexer recommendationIndexer :
					_recommendationIndexers) {

				recommendationIndexer.createIndex(company.getCompanyId());
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to add analytics recommendation index for company " +
					company,
				exception);
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
		try {
			for (RecommendationIndexer recommendationIndexer :
					_recommendationIndexers) {

				recommendationIndexer.dropIndex(company.getCompanyId());
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to remove analytics recommendation index for company " +
					company,
				exception);
		}
	}

	@Activate
	protected void activate() {
		_recommendationIndexers.add(
			new RecommendationIndexer(
				RecommendationIndexNames.MOST_VIEWED_CONTENT_RECOMMENDATION,
				_indexNameBuilder, _searchCapabilities, _searchEngineAdapter));
		_recommendationIndexers.add(
			new RecommendationIndexer(
				RecommendationIndexNames.USER_CONTENT_RECOMMENDATION,
				_indexNameBuilder, _searchCapabilities, _searchEngineAdapter));
	}

	@Deactivate
	protected void deactivate() {
		_recommendationIndexers.clear();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RecommendationIndexerPortalInstanceLifecycleListener.class);

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private final List<RecommendationIndexer> _recommendationIndexers =
		new ArrayList<>();

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}