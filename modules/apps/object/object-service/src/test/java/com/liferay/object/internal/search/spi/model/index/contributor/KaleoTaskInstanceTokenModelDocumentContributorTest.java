/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jan Brychta
 */
public class KaleoTaskInstanceTokenModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_kaleoTaskInstanceTokenModelDocumentContributor,
			"_objectDefinitionLocalService", _objectDefinitionLocalService);

		long companyId = RandomTestUtil.randomLong();
		String className = RandomTestUtil.randomString();

		_setUpKaleoTaskInstanceToken(companyId, className);
		_setUpObjectDefinitionLocalService(companyId, className);
	}

	@Test
	public void testContributeWhenObjectDefinitionIsCMP() {
		Mockito.when(
			_objectDefinition.isCMP()
		).thenReturn(
			true
		);

		Document document = Mockito.mock(Document.class);

		_kaleoTaskInstanceTokenModelDocumentContributor.contribute(
			document, _kaleoTaskInstanceToken);

		Mockito.verify(
			document
		).addKeyword(
			"cmpProjectTask", true
		);

		Mockito.verifyNoMoreInteractions(document);
	}

	@Test
	public void testContributeWhenObjectDefinitionIsCMPAndCMS() {
		Mockito.when(
			_objectDefinition.isCMP()
		).thenReturn(
			true
		);

		Mockito.when(
			_objectDefinition.isCMS()
		).thenReturn(
			true
		);

		Document document = Mockito.mock(Document.class);

		_kaleoTaskInstanceTokenModelDocumentContributor.contribute(
			document, _kaleoTaskInstanceToken);

		Mockito.verify(
			document
		).addKeyword(
			"cmpProjectTask", true
		);

		Mockito.verify(
			document
		).addKeyword(
			"cmsTask", true
		);
	}

	@Test
	public void testContributeWhenObjectDefinitionIsCMS() {
		Mockito.when(
			_objectDefinition.isCMS()
		).thenReturn(
			true
		);

		Document document = Mockito.mock(Document.class);

		_kaleoTaskInstanceTokenModelDocumentContributor.contribute(
			document, _kaleoTaskInstanceToken);

		Mockito.verify(
			document
		).addKeyword(
			"cmsTask", true
		);

		Mockito.verifyNoMoreInteractions(document);
	}

	@Test
	public void testContributeWhenObjectDefinitionIsNotCMPOrCMS() {
		Document document = Mockito.mock(Document.class);

		_kaleoTaskInstanceTokenModelDocumentContributor.contribute(
			document, _kaleoTaskInstanceToken);

		Mockito.verifyNoInteractions(document);
	}

	@Test
	public void testContributeWhenObjectDefinitionIsNull() {
		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		Document document = Mockito.mock(Document.class);

		_kaleoTaskInstanceTokenModelDocumentContributor.contribute(
			document, _kaleoTaskInstanceToken);

		Mockito.verifyNoInteractions(document);
	}

	private void _setUpKaleoTaskInstanceToken(
		long companyId, String className) {

		Mockito.when(
			_kaleoTaskInstanceToken.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			_kaleoTaskInstanceToken.getClassName()
		).thenReturn(
			className
		);
	}

	private void _setUpObjectDefinitionLocalService(
		long companyId, String className) {

		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				companyId, className)
		).thenReturn(
			_objectDefinition
		);
	}

	private final KaleoTaskInstanceToken _kaleoTaskInstanceToken = Mockito.mock(
		KaleoTaskInstanceToken.class);
	private final KaleoTaskInstanceTokenModelDocumentContributor
		_kaleoTaskInstanceTokenModelDocumentContributor =
			new KaleoTaskInstanceTokenModelDocumentContributor();
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);

}