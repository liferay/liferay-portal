/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.buffer.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.search.internal.buffer.IndexerRequest;
import com.liferay.portal.search.internal.buffer.IndexerRequestBuffer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Joshua Cords
 */
public class IndexerRequestBufferExecutorUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_indexerRequestBuffer.getIndexerRequests()
		).thenReturn(
			new ArrayList<IndexerRequest>() {
				{
					add(_indexerRequest);
				}
			}
		);

		Mockito.when(
			_indexerRequestBuffer.size()
		).thenReturn(
			1
		);
	}

	@After
	public void tearDown() {
		_searchContextMockedStatic.close();
	}

	@Test
	public void testExecuteWithBatchModeFalse() throws Exception {
		_searchContextMockedStatic.when(
			SearchContext::isBatchMode
		).thenReturn(
			false
		);

		_assertThreadLocalIds(false);
	}

	@Ignore
	@Test
	public void testExecuteWithBatchModeTrue() throws Exception {
		Mockito.when(
			_indexerRequestBuffer.transferCopy()
		).thenReturn(
			_indexerRequestBuffer
		);

		_searchContextMockedStatic.when(
			SearchContext::isBatchMode
		).thenReturn(
			true
		);

		_assertThreadLocalIds(true);
	}

	private void _assertThreadLocalIds(boolean pause) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_EXPECTED_COMPANY_ID, _EXPECTED_CT_COLLECTION_ID)) {

			long[] actualCompanyId = {0};
			long[] actualCTCollectionId = {0};

			Mockito.doAnswer(
				invocation -> {
					actualCompanyId[0] = CompanyThreadLocal.getCompanyId();
					actualCTCollectionId[0] =
						CTCollectionThreadLocal.getCTCollectionId();

					return null;
				}
			).when(
				_indexerRequest
			).execute();

			IndexerRequestBufferExecutorUtil.execute(_indexerRequestBuffer);

			if (pause) {
				TimeUnit.MILLISECONDS.sleep(100);
			}

			Assert.assertEquals(_EXPECTED_COMPANY_ID, actualCompanyId[0]);
			Assert.assertEquals(
				_EXPECTED_CT_COLLECTION_ID, actualCTCollectionId[0]);
		}
	}

	private static final long _EXPECTED_COMPANY_ID = 12345L;

	private static final long _EXPECTED_CT_COLLECTION_ID = 1L;

	private final IndexerRequest _indexerRequest = Mockito.mock(
		IndexerRequest.class);
	private final IndexerRequestBuffer _indexerRequestBuffer = Mockito.mock(
		IndexerRequestBuffer.class);
	private final MockedStatic<SearchContext> _searchContextMockedStatic =
		Mockito.mockStatic(SearchContext.class);

}