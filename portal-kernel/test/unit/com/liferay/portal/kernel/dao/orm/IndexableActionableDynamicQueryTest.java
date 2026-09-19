/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author André de Oliveira
 */
public class IndexableActionableDynamicQueryTest {

	@Before
	public void setUp() throws Exception {
		Mockito.doAnswer(
			invocation -> {
				Collection<Document> documents = invocation.getArgument(1);

				_updatedDocuments.addAll(documents);

				return null;
			}
		).when(
			indexWriterHelper
		).updateDocuments(
			Mockito.anyLong(), Mockito.anyCollection(), Mockito.anyBoolean()
		);

		_indexWriterHelperServiceRegistration = _bundleContext.registerService(
			IndexWriterHelper.class, indexWriterHelper, null);
		_indexerRegistryServiceRegistration = _bundleContext.registerService(
			IndexerRegistry.class, Mockito.mock(IndexerRegistry.class), null);
		_portalExecutorManagerServiceRegistration =
			_bundleContext.registerService(
				PortalExecutorManager.class,
				Mockito.mock(PortalExecutorManager.class), null);

		IndexerRegistry indexerRegistry = Mockito.mock(IndexerRegistry.class);

		Mockito.when(
			indexerRegistry.getIndexer((String)null)
		).thenReturn(
			Mockito.mock(Indexer.class)
		);
	}

	@After
	public void tearDown() {
		_indexWriterHelperServiceRegistration.unregister();
		_indexerRegistryServiceRegistration.unregister();
		_portalExecutorManagerServiceRegistration.unregister();
	}

	@Test
	public void testAddDocument() throws Exception {
		indexableActionableDynamicQuery.setInterval(1);

		_addDocument(document1);

		verifyDocumentsUpdated(document1);
	}

	@Test
	public void testAddDocumentWithinInterval() throws Exception {
		indexableActionableDynamicQuery.setInterval(3);

		_addDocument(document1);
		_addDocument(document2);

		verifyNoDocumentsUpdated();

		_addDocument(document3);

		verifyDocumentsUpdated(document1, document2, document3);
	}

	protected void verifyDocumentsUpdated(Document... documents) {
		Assert.assertEquals(Arrays.asList(documents), _updatedDocuments);
	}

	protected void verifyNoDocumentsUpdated() {
		Assert.assertTrue(_updatedDocuments.isEmpty());
	}

	protected Document document1 = Mockito.mock(Document.class);
	protected Document document2 = Mockito.mock(Document.class);
	protected Document document3 = Mockito.mock(Document.class);
	protected IndexWriterHelper indexWriterHelper = Mockito.mock(
		IndexWriterHelper.class);
	protected IndexableActionableDynamicQuery indexableActionableDynamicQuery =
		new IndexableActionableDynamicQuery();

	private void _addDocument(Document document) throws Exception {
		Method method = IndexableActionableDynamicQuery.class.getDeclaredMethod(
			"_addDocument", Document.class, List.class);

		method.setAccessible(true);

		method.invoke(indexableActionableDynamicQuery, document, _documents);
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private final List<Document> _documents = new ArrayList<>();
	private ServiceRegistration<IndexWriterHelper>
		_indexWriterHelperServiceRegistration;
	private ServiceRegistration<IndexerRegistry>
		_indexerRegistryServiceRegistration;
	private ServiceRegistration<PortalExecutorManager>
		_portalExecutorManagerServiceRegistration;
	private final List<Document> _updatedDocuments = new ArrayList<>();

}