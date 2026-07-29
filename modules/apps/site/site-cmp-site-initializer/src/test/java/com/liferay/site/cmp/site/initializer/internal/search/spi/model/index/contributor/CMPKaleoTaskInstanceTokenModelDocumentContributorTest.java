/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;

/**
 * @author Guilherme Camacho
 */
public class CMPKaleoTaskInstanceTokenModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_cmpKaleoTaskInstanceTokenModelDocumentContributor,
			"_classNameLocalService", _classNameLocalService);
		ReflectionTestUtil.setFieldValue(
			_cmpKaleoTaskInstanceTokenModelDocumentContributor,
			"_filterFactory", _filterFactory);
		ReflectionTestUtil.setFieldValue(
			_cmpKaleoTaskInstanceTokenModelDocumentContributor,
			"_groupLocalService", _groupLocalService);
		ReflectionTestUtil.setFieldValue(
			_cmpKaleoTaskInstanceTokenModelDocumentContributor,
			"_objectDefinitionLocalService", _objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_cmpKaleoTaskInstanceTokenModelDocumentContributor,
			"_objectEntryLocalService", _objectEntryLocalService);

		_setUpGroupLocalService();
		_setUpKaleoTaskInstanceToken();
		_setUpObjectDefinitionLocalService();
		_setUpObjectEntryLocalService();
	}

	@Test
	public void testContributeWhenObjectEntryIsLinkedToCMPTask()
		throws Exception {

		long linkedObjectEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			_objectEntryLocalService.getPrimaryKeys(
				Mockito.any(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.any())
		).thenReturn(
			List.of(linkedObjectEntryId)
		);

		long cmpTaskObjectEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			_objectEntryLocalService.getValues(linkedObjectEntryId)
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"r_cmpTaskToCMPTaskLinks_c_cmpTaskId", cmpTaskObjectEntryId
			).build()
		);

		Document document = Mockito.mock(Document.class);

		_cmpKaleoTaskInstanceTokenModelDocumentContributor.contribute(
			document, _kaleoTaskInstanceToken);

		Mockito.verify(
			document
		).addKeyword(
			Mockito.eq("cmpTaskObjectEntryIds"),
			AdditionalMatchers.aryEq(new long[] {cmpTaskObjectEntryId})
		);
	}

	@Test
	public void testContributeWhenObjectEntryIsNotLinkedToCMPTask()
		throws Exception {

		Mockito.when(
			_objectEntryLocalService.getPrimaryKeys(
				Mockito.any(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.any())
		).thenReturn(
			Collections.emptyList()
		);

		Document document = Mockito.mock(Document.class);

		_cmpKaleoTaskInstanceTokenModelDocumentContributor.contribute(
			document, _kaleoTaskInstanceToken);

		Mockito.verifyNoInteractions(document);
	}

	@Test
	public void testContributeWhenObjectEntryIsNull() {
		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(Mockito.anyLong())
		).thenReturn(
			null
		);

		Document document = Mockito.mock(Document.class);

		_cmpKaleoTaskInstanceTokenModelDocumentContributor.contribute(
			document, _kaleoTaskInstanceToken);

		Mockito.verifyNoInteractions(document);
	}

	private void _setUpGroupLocalService() {
		Mockito.when(
			_groupLocalService.fetchGroup(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(Group.class)
		);
	}

	private void _setUpKaleoTaskInstanceToken() {
		Mockito.when(
			_kaleoTaskInstanceToken.getClassPK()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_kaleoTaskInstanceToken.getKaleoTaskAssignmentInstances()
		).thenReturn(
			Collections.emptyList()
		);
	}

	private void _setUpObjectDefinitionLocalService() {
		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			Mockito.mock(ObjectDefinition.class)
		);
	}

	private void _setUpObjectEntryLocalService() {
		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(ObjectEntry.class)
		);
	}

	private final ClassNameLocalService _classNameLocalService = Mockito.mock(
		ClassNameLocalService.class);
	private final CMPKaleoTaskInstanceTokenModelDocumentContributor
		_cmpKaleoTaskInstanceTokenModelDocumentContributor =
			new CMPKaleoTaskInstanceTokenModelDocumentContributor();
	private final FilterFactory<Predicate> _filterFactory = Mockito.mock(
		FilterFactory.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final KaleoTaskInstanceToken _kaleoTaskInstanceToken = Mockito.mock(
		KaleoTaskInstanceToken.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);

}