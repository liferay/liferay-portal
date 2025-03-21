/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet.action;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.synchronizer.IndexToFilterSynchronizer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class DeleteSynonymSetsMVCActionCommandTest
	extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_deleteSynonymSetsMVCActionCommand =
			new DeleteSynonymSetsMVCActionCommand();

		ReflectionTestUtil.setFieldValue(
			_deleteSynonymSetsMVCActionCommand, "_indexNameBuilder",
			_indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_deleteSynonymSetsMVCActionCommand, "_indexToFilterSynchronizer",
			_indexToFilterSynchronizer);
		ReflectionTestUtil.setFieldValue(
			_deleteSynonymSetsMVCActionCommand, "_portal", portal);
		ReflectionTestUtil.setFieldValue(
			_deleteSynonymSetsMVCActionCommand, "_synonymSetIndexNameBuilder",
			synonymSetIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_deleteSynonymSetsMVCActionCommand, "_synonymSetIndexReader",
			synonymSetIndexReader);
		ReflectionTestUtil.setFieldValue(
			_deleteSynonymSetsMVCActionCommand, "_synonymSetStorageAdapter",
			synonymSetStorageAdapter);
	}

	@Test
	public void testDeleteSynonymSets() throws Exception {
		SynonymSet.SynonymSetBuilder synonymSetBuilder =
			new SynonymSet.SynonymSetBuilder();

		_deleteSynonymSetsMVCActionCommand.deleteSynonymSets(
			Mockito.mock(SynonymSetIndexName.class),
			Arrays.asList(
				synonymSetBuilder.synonyms(
					"car,atumobile"
				).synonymSetDocumentId(
					"id-1"
				).build(),
				synonymSetBuilder.synonyms(
					"clever,smart"
				).synonymSetDocumentId(
					"id-2"
				).build()));

		Mockito.verify(
			synonymSetStorageAdapter, Mockito.times(2)
		).delete(
			Mockito.any(), Mockito.anyString()
		);
	}

	@Test
	public void testDoProcessAction() throws Exception {
		setUpHttpServletRequestParameterValues(
			_httpServletRequest, "rowIds", new String[] {"id"});
		setUpPortal(_httpServletRequest);
		setUpPortalUtil();
		setUpPortletRequest(_actionRequest);
		setUpSynonymSetIndexNameBuilder();
		setUpSynonymSetIndexReader("id", "car,automobile");

		_deleteSynonymSetsMVCActionCommand.doProcessAction(
			_actionRequest, _actionResponse);

		Mockito.verify(
			_actionRequest, Mockito.times(3)
		).getAttribute(
			Mockito.anyString()
		);
	}

	@Test
	public void testGetDeletedSynonymSets() throws Exception {
		setUpHttpServletRequestParameterValues(
			_httpServletRequest, "rowIds", new String[] {"id"});
		setUpPortal(_httpServletRequest);
		setUpPortalUtil();
		setUpSynonymSetIndexReader("id", "car,automobile");

		List<SynonymSet> synonymSets =
			_deleteSynonymSetsMVCActionCommand.getDeletedSynonymSets(
				_actionRequest, Mockito.mock(SynonymSetIndexName.class));

		Assert.assertEquals(1, synonymSets.size(), 0.0);

		synonymSets.forEach(
			synonymSet -> {
				Assert.assertEquals("car,automobile", synonymSet.getSynonyms());
				Assert.assertEquals("id", synonymSet.getSynonymSetDocumentId());
			});
	}

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final ActionResponse _actionResponse = Mockito.mock(
		ActionResponse.class);
	private DeleteSynonymSetsMVCActionCommand
		_deleteSynonymSetsMVCActionCommand;
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final IndexNameBuilder _indexNameBuilder = Mockito.mock(
		IndexNameBuilder.class);
	private final IndexToFilterSynchronizer _indexToFilterSynchronizer =
		Mockito.mock(IndexToFilterSynchronizer.class);

}