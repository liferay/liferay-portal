/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.helper;

import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.MatchAllQuery;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRelTable;
import com.liferay.segments.provider.SegmentsEntryProviderRegistry;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Marcos Martins
 */
public class IndexerHelper {

	public IndexerHelper(
		Indexer<User> indexer, Portal portal,
		SegmentsEntryLocalService segmentsEntryLocalService,
		SegmentsEntryProviderRegistry segmentsEntryProviderRegistry,
		SegmentsEntryRelLocalService segmentsEntryRelLocalService) {

		_indexer = indexer;
		_portal = portal;
		_segmentsEntryLocalService = segmentsEntryLocalService;
		_segmentsEntryProviderRegistry = segmentsEntryProviderRegistry;
		_segmentsEntryRelLocalService = segmentsEntryRelLocalService;
	}

	public Set<Long> getIndexableClassPKs(
			long companyId, Set<Long> newClassPKs, long segmentEntryId)
		throws Exception {

		return SetUtil.symmetricDifference(
			_getOldIndexClassPKs(companyId, segmentEntryId), newClassPKs);
	}

	public Set<Long> getNewClassPKs(long segmentsEntryId)
		throws PortalException {

		return SetUtil.fromArray(
			_segmentsEntryProviderRegistry.getSegmentsEntryClassPKs(
				segmentsEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS));
	}

	public void updateDatabase(long segmentsEntryId, Set<Long> newClassPKs)
		throws PortalException {

		SegmentsEntry segmentsEntry =
			_segmentsEntryLocalService.fetchSegmentsEntry(segmentsEntryId);

		if ((segmentsEntry == null) ||
			(segmentsEntry.getCriteriaObj() == null) || (newClassPKs == null) ||
			newClassPKs.isEmpty()) {

			return;
		}

		Set<Long> oldClassPKs = _getOldDatabaseClassPKs(segmentsEntryId);

		Set<Long> addClassPKs = new HashSet<>(newClassPKs);
		Set<Long> deleteClassPKs = new HashSet<>();

		for (Long oldClassPK : oldClassPKs) {
			if (!addClassPKs.remove(oldClassPK)) {
				deleteClassPKs.add(oldClassPK);
			}
		}

		long classNameId = _portal.getClassNameId(User.class);

		_segmentsEntryRelLocalService.deleteSegmentsEntryRels(
			segmentsEntryId, classNameId,
			ArrayUtil.toLongArray(deleteClassPKs));

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(segmentsEntry.getGroupId());
		serviceContext.setUserId(segmentsEntry.getUserId());

		_segmentsEntryRelLocalService.addSegmentsEntryRels(
			segmentsEntryId, classNameId, ArrayUtil.toLongArray(addClassPKs),
			serviceContext);
	}

	private BooleanQuery _getLastDocumentBooleanQuery(Document lastDocument) {
		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(new MatchAllQuery(), BooleanClauseOccur.MUST);
		booleanQuery.add(
			new TermRangeQuery(
				Field.ENTRY_CLASS_PK, lastDocument.get(Field.ENTRY_CLASS_PK),
				null, false, true),
			BooleanClauseOccur.MUST);

		return booleanQuery;
	}

	private Set<Long> _getOldDatabaseClassPKs(long segmentsEntryId) {
		Iterable<Long> iterable = _segmentsEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				SegmentsEntryRelTable.INSTANCE.classPK
			).from(
				SegmentsEntryRelTable.INSTANCE
			).where(
				SegmentsEntryRelTable.INSTANCE.segmentsEntryId.eq(
					segmentsEntryId)
			));

		return SetUtil.fromIterator(iterable.iterator());
	}

	private Set<Long> _getOldIndexClassPKs(long companyId, long segmentsEntryId)
		throws Exception {

		Set<Long> classPKs = new HashSet<>();

		int indexSearchLimit = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.INDEX_SEARCH_LIMIT));

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			"segmentsEntryIds", new long[] {segmentsEntryId});
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(indexSearchLimit);
		searchContext.setSorts(
			new Sort(Field.ENTRY_CLASS_PK, Sort.LONG_TYPE, false));
		searchContext.setStart(0);

		Document lastDocument = null;

		while (true) {
			if (lastDocument != null) {
				searchContext.setBooleanClauses(
					new BooleanClause[] {
						new BooleanClause<>(
							_getLastDocumentBooleanQuery(lastDocument),
							BooleanClauseOccur.MUST)
					});
			}

			Hits hits = _indexer.search(searchContext);

			Document[] documents = hits.getDocs();

			if (documents.length == 0) {
				break;
			}

			int previousSize = classPKs.size();

			for (Document document : documents) {
				classPKs.add(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));
			}

			if (classPKs.size() == previousSize) {
				break;
			}

			lastDocument = documents[documents.length - 1];
		}

		return classPKs;
	}

	private final Indexer<User> _indexer;
	private final Portal _portal;
	private final SegmentsEntryLocalService _segmentsEntryLocalService;
	private final SegmentsEntryProviderRegistry _segmentsEntryProviderRegistry;
	private final SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

}