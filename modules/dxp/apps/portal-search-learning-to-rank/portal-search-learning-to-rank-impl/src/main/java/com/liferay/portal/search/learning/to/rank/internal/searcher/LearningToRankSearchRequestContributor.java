/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.learning.to.rank.internal.searcher;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.search.learning.to.rank.configuration.LearningToRankConfiguration;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.rescore.RescoreBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.spi.searcher.SearchRequestContributor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	configurationPid = "com.liferay.portal.search.learning.to.rank.configuration.LearningToRankConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, enabled = false,
	property = "search.request.contributor.id=com.liferay.portal.search.learning.to.rank",
	service = SearchRequestContributor.class
)
public class LearningToRankSearchRequestContributor
	implements SearchRequestContributor {

	@Override
	public SearchRequest contribute(SearchRequest searchRequest) {
		if (!_enabled) {
			return searchRequest;
		}

		SearchRequestBuilder searchRequestBuilder =
			searchRequestBuilderFactory.builder(searchRequest);

		searchRequestBuilder.rescores(_getRescores(searchRequest));

		return searchRequestBuilder.build();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		LearningToRankConfiguration learningToRankConfiguration =
			ConfigurableUtil.createConfigurable(
				LearningToRankConfiguration.class, properties);

		_enabled = learningToRankConfiguration.enabled();
		_model = learningToRankConfiguration.model();
	}

	@Reference
	protected RescoreBuilderFactory rescoreBuilderFactory;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	private Query _getRescoreQuery(String model, String keywords) {
		return QueriesUtil.wrapper(
			JSONUtil.put(
				"sltr",
				JSONUtil.put(
					"model", model
				).put(
					"params", JSONUtil.put("keywords", keywords)
				)
			).toString());
	}

	private List<Rescore> _getRescores(SearchRequest searchRequest) {
		return Arrays.asList(
			rescoreBuilderFactory.builder(
				_getRescoreQuery(_model, searchRequest.getQueryString())
			).windowSize(
				1000
			).build());
	}

	private volatile boolean _enabled;
	private volatile String _model;

}