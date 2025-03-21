/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.index.RankingPinBuilderFactory;
import com.liferay.portal.search.tuning.rankings.storage.RankingStorageAdapter;
import com.liferay.portal.search.tuning.rankings.web.internal.BaseRankingsWebTestCase;
import com.liferay.portal.search.tuning.rankings.web.internal.index.Criteria;
import com.liferay.portal.search.tuning.rankings.web.internal.index.DuplicateQueryStringsDetector;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingImpl;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingPinBuilderFactoryImpl;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public abstract class BaseRankingsPortletActionTestCase
	extends BaseRankingsWebTestCase {

	@SuppressWarnings("unchecked")
	protected void setUpDuplicateQueryStringsDetector() {
		Criteria.Builder builder = Mockito.mock(Criteria.Builder.class);

		Mockito.doReturn(
			builder
		).when(
			builder
		).groupExternalReferenceCode(
			Mockito.anyString()
		);

		Mockito.doReturn(
			builder
		).when(
			builder
		).index(
			Mockito.anyString()
		);

		Mockito.doReturn(
			builder
		).when(
			builder
		).queryStrings(
			Mockito.anyCollection()
		);

		Mockito.doReturn(
			builder
		).when(
			builder
		).rankingIndexName(
			Mockito.any()
		);

		Mockito.doReturn(
			builder
		).when(
			builder
		).sxpBlueprintExternalReferenceCode(
			Mockito.anyString()
		);

		Mockito.doReturn(
			builder
		).when(
			builder
		).unlessRankingDocumentId(
			Mockito.anyString()
		);

		Mockito.doReturn(
			Mockito.mock(Criteria.class)
		).when(
			builder
		).build();

		Mockito.doReturn(
			Collections.emptyList()
		).when(
			duplicateQueryStringsDetector
		).detect(
			Mockito.any()
		);
	}

	protected void setUpIndexNameBuilder() {
		Mockito.doReturn(
			"indexName"
		).when(
			indexNameBuilder
		).getIndexName(
			Mockito.anyLong()
		);
	}

	@Override
	protected void setUpPortletRequestParamValue(
		PortletRequest portletRequest, String returnValue, String paramName) {

		Mockito.doReturn(
			returnValue
		).when(
			portletRequest
		).getParameter(
			Mockito.eq(paramName)
		);
	}

	protected void setUpRankingIndexReader() {
		RankingImpl rankingImpl = Mockito.mock(RankingImpl.class);

		ReflectionTestUtil.setFieldValue(
			rankingImpl, "_aliases", Arrays.asList("aliases"));
		ReflectionTestUtil.setFieldValue(
			rankingImpl, "_groupExternalReferenceCode",
			"groupExternalReferenceCode");
		ReflectionTestUtil.setFieldValue(
			rankingImpl, "_hiddenDocumentIds",
			new LinkedHashSet<String>(Arrays.asList("hiddenDocumentIds")));
		ReflectionTestUtil.setFieldValue(
			rankingImpl, "_pins",
			new ArrayList<Ranking.Pin>(
				Arrays.asList(
					_rankingPinBuilderFactory.builder(
					).documentId(
						"id"
					).position(
						0
					).build())));
		ReflectionTestUtil.setFieldValue(
			rankingImpl, "_sxpBlueprintExternalReferenceCode",
			"sxpBlueprintExternalReferenceCode");

		Mockito.doReturn(
			rankingImpl
		).when(
			rankingIndexReader
		).fetch(
			Mockito.anyString(), Mockito.any()
		);
	}

	protected void setUpResourceResponse() {
		Mockito.doReturn(
			true
		).when(
			resourceResponse
		).isCommitted();

		Mockito.doReturn(
			Mockito.mock(PortletURL.class)
		).when(
			resourceResponse
		).createRenderURL();
	}

	protected DuplicateQueryStringsDetector duplicateQueryStringsDetector =
		Mockito.mock(DuplicateQueryStringsDetector.class);
	protected IndexNameBuilder indexNameBuilder = Mockito.mock(
		IndexNameBuilder.class);
	protected RankingIndexReader rankingIndexReader = Mockito.mock(
		RankingIndexReader.class);
	protected RankingStorageAdapter rankingStorageAdapter = Mockito.mock(
		RankingStorageAdapter.class);

	private final RankingPinBuilderFactory _rankingPinBuilderFactory =
		new RankingPinBuilderFactoryImpl();

}