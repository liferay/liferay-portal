/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.query;

import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore;

import com.liferay.portal.search.elasticsearch8.internal.query.function.score.ElasticsearchScoreFunctionTranslator;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.query.function.score.ScoreFunction;
import com.liferay.portal.search.test.util.query.BaseScoreFunctionTranslatorTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author André de Oliveira
 */
public class ElasticsearchScoreFunctionTranslatorTest
	extends BaseScoreFunctionTranslatorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Override
	protected String translate(ScoreFunction scoreFunction) {
		ElasticsearchScoreFunctionTranslator
			elasticsearchScoreFunctionTranslator =
				new ElasticsearchScoreFunctionTranslator();

		FunctionScore.Builder.ContainerBuilder containerBuilder =
			elasticsearchScoreFunctionTranslator.translate(scoreFunction);

		return JsonpUtil.toString(containerBuilder.build());
	}

}