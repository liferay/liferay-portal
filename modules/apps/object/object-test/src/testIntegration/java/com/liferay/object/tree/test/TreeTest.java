/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.tree.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.Tree;
import com.liferay.object.tree.constants.TreeConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class TreeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetHeight() throws Exception {
		Tree tree = TreeTestUtil.createObjectDefinitionTree(
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
			).build());

		Assert.assertEquals(2, tree.getHeight(tree.getRootNode()));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AB", "C_AA", "C_AAB", "C_AAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testIterator() throws Exception {

		// Breadth first iterator

		Tree tree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
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
			).build());

		_testIterator(
			Arrays.asList("A", "AA", "AB", "AAA", "AAB"),
			tree.iterator(TreeConstants.ITERATOR_TYPE_BREADTH_FIRST));

		// Default iterator

		_testIterator(
			Arrays.asList("A", "AA", "AB", "AAA", "AAB"), tree.iterator());

		// Default iterator, subtree

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		_testIterator(
			Arrays.asList("AA", "AAA", "AAB"),
			tree.iterator(objectDefinition.getObjectDefinitionId()));

		// Post order iterator

		_testIterator(
			Arrays.asList("AAA", "AAB", "AA", "AB", "A"),
			tree.iterator(TreeConstants.ITERATOR_TYPE_POST_ORDER));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AB", "C_AA", "C_AAB", "C_AAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	private void _testIterator(
			List<String> expectedObjectDefinitionShortNames,
			Iterator<Node> iterator)
		throws Exception {

		int position = 0;

		while (iterator.hasNext()) {
			Node node = iterator.next();

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					node.getPrimaryKey());

			Assert.assertEquals(
				expectedObjectDefinitionShortNames.get(position),
				objectDefinition.getShortName());

			position++;
		}

		Assert.assertEquals(
			expectedObjectDefinitionShortNames.size(), position);
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}