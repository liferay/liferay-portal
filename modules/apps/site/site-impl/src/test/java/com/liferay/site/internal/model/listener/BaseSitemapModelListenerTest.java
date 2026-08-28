/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionAttribute;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.transaction.TransactionStatus;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.manager.SitemapManager;

import java.util.function.BiConsumer;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Magdalena Jedraszak
 */
public class BaseSitemapModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testOnAfterCreateFiresOncePerGroup() {
		LayoutModelListener layoutModelListener = _createLayoutModelListener();

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::created);

		long companyId = RandomTestUtil.randomLong();
		long groupId1 = RandomTestUtil.randomLong();
		long groupId2 = RandomTestUtil.randomLong();

		layoutModelListener.onAfterCreate(_createLayout(companyId, groupId1));
		layoutModelListener.onAfterCreate(_createLayout(companyId, groupId2));

		Mockito.verifyNoInteractions(_sitemapManager);

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::committed);

		Mockito.verify(
			_sitemapManager, Mockito.times(1)
		).scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId, groupId1, null
		);

		Mockito.verify(
			_sitemapManager, Mockito.times(1)
		).scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId, groupId2, null
		);
	}

	@Test
	public void testOnAfterCreateFiresOncePerTransaction() {
		LayoutModelListener layoutModelListener = _createLayoutModelListener();

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::created);

		long companyId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();

		layoutModelListener.onAfterCreate(_createLayout(companyId, groupId));
		layoutModelListener.onAfterCreate(_createLayout(companyId, groupId));
		layoutModelListener.onAfterRemove(_createLayout(companyId, groupId));

		Mockito.verifyNoInteractions(_sitemapManager);

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::committed);

		Mockito.verify(
			_sitemapManager, Mockito.times(1)
		).scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId, groupId, null
		);
	}

	@Test
	public void testOnAfterCreateResetsOnCommit() {
		LayoutModelListener layoutModelListener = _createLayoutModelListener();

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::created);

		long companyId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();

		layoutModelListener.onAfterCreate(_createLayout(companyId, groupId));

		Mockito.verifyNoInteractions(_sitemapManager);

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::committed);

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::created);

		layoutModelListener.onAfterCreate(_createLayout(companyId, groupId));

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::committed);

		Mockito.verify(
			_sitemapManager, Mockito.times(2)
		).scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId, groupId, null
		);
	}

	@Test
	public void testOnAfterCreateResetsOnRollback() {
		LayoutModelListener layoutModelListener = _createLayoutModelListener();

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::created);

		long companyId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();

		layoutModelListener.onAfterCreate(_createLayout(companyId, groupId));

		Mockito.verifyNoInteractions(_sitemapManager);

		_fireTransactionLifecycleListenerEvent(
			(transactionAttribute, transactionStatus) ->
				TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER.
					rollbacked(transactionAttribute, transactionStatus, null));

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::created);

		layoutModelListener.onAfterCreate(_createLayout(companyId, groupId));

		_fireTransactionLifecycleListenerEvent(
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER::committed);

		Mockito.verify(
			_sitemapManager, Mockito.times(1)
		).scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId, groupId, null
		);
	}

	private Layout _createLayout(long companyId, long groupId) {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			groupId
		);

		return layout;
	}

	private LayoutModelListener _createLayoutModelListener() {
		LayoutModelListener layoutModelListener = new LayoutModelListener();

		_sitemapManager = Mockito.mock(SitemapManager.class);

		ReflectionTestUtil.setFieldValue(
			layoutModelListener, "sitemapManager", _sitemapManager);

		return layoutModelListener;
	}

	private TransactionAttribute _createTransactionAttribute() {
		TransactionAttribute.Builder builder =
			new TransactionAttribute.Builder();

		builder.setPropagation(Propagation.REQUIRED);

		return builder.build();
	}

	private TransactionStatus _createTransactionStatus() {
		return new TransactionStatus() {

			@Override
			public boolean isCompleted() {
				return false;
			}

			@Override
			public boolean isNewTransaction() {
				return true;
			}

			@Override
			public boolean isRollbackOnly() {
				return false;
			}

			@Override
			public void suppressLifecycleListenerThrowable(
				Throwable throwable) {
			}

		};
	}

	private void _fireTransactionLifecycleListenerEvent(
		BiConsumer<TransactionAttribute, TransactionStatus> biConsumer) {

		biConsumer.accept(
			_createTransactionAttribute(), _createTransactionStatus());
	}

	private SitemapManager _sitemapManager;

}