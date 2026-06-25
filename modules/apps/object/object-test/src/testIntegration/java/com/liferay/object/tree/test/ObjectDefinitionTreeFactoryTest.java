/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.tree.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionTreeFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCreate() throws Exception {
		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA", "AB"}
			).put(
				"AA", new String[] {"AAA", "AAB"}
			).put(
				"AB", new String[0]
			).put(
				"AAA", new String[0]
			).put(
				"AAB", new String[0]
			).build(),
			TreeTestUtil.createObjectDefinitionTree(
				_objectDefinitionLocalService, _objectRelationshipLocalService,
				false,
				LinkedHashMapBuilder.put(
					"A", new String[] {"AA", "AB"}
				).put(
					"AA", new String[] {"AAA", "AAB"}
				).put(
					"AB", new String[0]
				).put(
					"AAA", new String[0]
				).put(
					"AAB", new String[0]
				).build()),
			_objectDefinitionLocalService);
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}