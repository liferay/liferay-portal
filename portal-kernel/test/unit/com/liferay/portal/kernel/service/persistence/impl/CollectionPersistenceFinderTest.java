/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class CollectionPersistenceFinderTest {

	@Test
	public void testMultiElementArrayableFindUsesPaginatedFinderPath() {
		FinderPath[] recordedFinderPath = new FinderPath[1];

		CollectionPersistenceFinder<TestModel, NoSuchModelException>
			collectionPersistenceFinder = _createCollectionPersistenceFinder();

		collectionPersistenceFinder.find(
			_createRecordingFinderCache(recordedFinderPath),
			new Object[] {new long[] {100L, 200L}}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null, true);

		Assert.assertSame(_PAGINATED_FIND_PATH, recordedFinderPath[0]);
	}

	@Test
	public void testSingleElementArrayableFindUsesUnpaginatedFinderPath() {
		FinderPath[] recordedFinderPath = new FinderPath[1];

		CollectionPersistenceFinder<TestModel, NoSuchModelException>
			collectionPersistenceFinder = _createCollectionPersistenceFinder();

		collectionPersistenceFinder.find(
			_createRecordingFinderCache(recordedFinderPath),
			new Object[] {new long[] {100L}}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null, true);

		Assert.assertSame(_UNPAGINATED_FIND_PATH, recordedFinderPath[0]);
	}

	private CollectionPersistenceFinder<TestModel, NoSuchModelException>
		_createCollectionPersistenceFinder() {

		return new CollectionPersistenceFinder<>(
			new BasePersistenceImpl<TestModel, NoSuchModelException>() {
			},
			_PAGINATED_FIND_PATH, _UNPAGINATED_FIND_PATH, _COUNT_FIND_PATH, "",
			"", "", "", "", "", null,
			new ArrayableFinderColumn<>(
				"t.", "col", FinderColumn.Type.LONG, "=", false, true, true,
				testModel -> 0L));
	}

	private FinderCache _createRecordingFinderCache(
		FinderPath[] recordedFinderPath) {

		return new FinderCache() {

			@Override
			public void clearCache() {
			}

			@Override
			public void clearCache(Class<?> clazz) {
			}

			@Override
			public void clearDSLQueryCache(String tableName) {
			}

			@Override
			public void clearLocalCache() {
			}

			@Override
			public Object getResult(
				FinderPath finderPath, Object[] args,
				BasePersistence<?> basePersistence) {

				recordedFinderPath[0] = finderPath;

				return Collections.emptyList();
			}

			@Override
			public void invalidate() {
			}

			@Override
			public void putResult(
				FinderPath finderPath, Object[] args, Object result) {
			}

			@Override
			public void removeCache(String className) {
			}

			@Override
			public void removeResult(FinderPath finderPath, Object[] args) {
			}

		};
	}

	private static final FinderPath _COUNT_FIND_PATH = new FinderPath(
		"TestModel", "countByCol", new String[] {Long.class.getName()},
		new String[] {"col"}, false);

	private static final FinderPath _PAGINATED_FIND_PATH = new FinderPath(
		"TestModel.List1", "findByCol",
		new String[] {
			Long.class.getName(), Integer.class.getName(),
			Integer.class.getName(), OrderByComparator.class.getName()
		},
		new String[] {"col"}, true);

	private static final FinderPath _UNPAGINATED_FIND_PATH = new FinderPath(
		"TestModel.List2", "findByCol", new String[] {Long.class.getName()},
		new String[] {"col"}, true);

	private interface TestModel extends BaseModel<TestModel> {
	}

}