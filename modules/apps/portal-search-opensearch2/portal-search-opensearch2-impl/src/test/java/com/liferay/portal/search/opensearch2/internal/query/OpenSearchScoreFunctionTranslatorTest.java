/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.query;

import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.query.function.score.OpenSearchScoreFunctionTranslator;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;
import com.liferay.portal.search.query.function.score.ScoreFunction;
import com.liferay.portal.search.test.util.query.BaseScoreFunctionTranslatorTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;

import org.opensearch.client.opensearch._types.query_dsl.FunctionScore;

/**
 * @author André de Oliveira
 */
public class OpenSearchScoreFunctionTranslatorTest
	extends BaseScoreFunctionTranslatorTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static final OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Override
	protected String translate(ScoreFunction scoreFunction) {
		OpenSearchScoreFunctionTranslator openSearchScoreFunctionTranslator =
			new OpenSearchScoreFunctionTranslator();

		FunctionScore.Builder.ContainerBuilder containerBuilder =
			openSearchScoreFunctionTranslator.translate(scoreFunction);

		return JsonpUtil.toString(containerBuilder.build());
	}

}