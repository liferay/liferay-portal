/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.rest.client.dto.v1_0.Strategy;
import com.liferay.batch.planner.rest.client.pagination.Page;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.URLCodec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matija Petanjek
 */
@RunWith(Arquillian.class)
public class StrategyResourceTest extends BaseStrategyResourceTestCase {

	@Override
	@Test
	public void testGetPlanInternalClassNameKeyStrategiesPage()
		throws Exception {

		// Dispatch trigger

		_testGetPlanInternalClassNameKeyStrategiesPage(
			Collections.singletonList("INSERT"),
			"com.liferay.dispatch.rest.dto.v1_0.DispatchTrigger",
			Collections.emptyList());

		// Object entry

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		_testGetPlanInternalClassNameKeyStrategiesPage(
			Arrays.asList("INSERT", "UPSERT"),
			URLCodec.encodeURL(
				TaskItemUtil.getInternalClassNameKey(
					"com.liferay.object.rest.dto.v1_0.ObjectEntry",
					_objectDefinition.getName())),
			Arrays.asList("PARTIAL_UPDATE", "UPDATE"));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name", "type"};
	}

	private void _testGetPlanInternalClassNameKeyStrategiesPage(
			List<String> createStrategyNames, String internalClassNameKey,
			List<String> updateStrategyNames)
		throws Exception {

		Page<Strategy> strategiesPage =
			strategyResource.getPlanInternalClassNameKeyStrategiesPage(
				internalClassNameKey);

		assertEqualsIgnoringOrder(
			ListUtil.concat(
				_toStrategies(createStrategyNames, "create"),
				_toStrategies(updateStrategyNames, "update")),
			(List<Strategy>)strategiesPage.getItems());
	}

	private List<Strategy> _toStrategies(List<String> names, String type) {
		return TransformUtil.transform(
			names,
			name -> {
				Strategy strategy = new Strategy();

				strategy.setName(name);
				strategy.setType(type);

				return strategy;
			});
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

}