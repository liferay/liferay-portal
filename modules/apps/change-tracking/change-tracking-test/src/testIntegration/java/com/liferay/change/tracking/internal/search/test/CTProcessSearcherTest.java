/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTProcessSearcherTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSearchByKeywords() throws Exception {
		CTCollection ctCollection1 = _addCTCollection();
		CTCollection ctCollection2 = _addCTCollection();

		CTProcess ctProcess1 = _addCTProcess(ctCollection1.getCtCollectionId());

		CTProcess ctProcess2 = _addCTProcess(ctCollection2.getCtCollectionId());

		_assertHits(
			_getUIDs(ctProcess1), _byKeywords(ctCollection1.getDescription()));
		_assertHits(_getUIDs(ctProcess1), _byKeywords(ctCollection1.getName()));
		_assertHits(
			_getUIDs(ctProcess2), _byKeywords(ctCollection2.getDescription()));
		_assertHits(_getUIDs(ctProcess2), _byKeywords(ctCollection2.getName()));
	}

	@Test
	public void testSearchByStatus() throws Exception {
		BackgroundTaskExecutor backgroundTaskExecutor =
			(BackgroundTaskExecutor)ProxyUtil.newProxyInstance(
				BackgroundTaskExecutor.class.getClassLoader(),
				new Class<?>[] {BackgroundTaskExecutor.class},
				(proxy, method, argus) -> {
					if (Objects.equals(method.getName(), "clone")) {
						return proxy;
					}
					else if (Objects.equals(method.getName(), "execute")) {
						return new BackgroundTaskResult(
							BackgroundTaskConstants.STATUS_FAILED);
					}
					else if (Objects.equals(
								method.getName(), "getIsolationLevel")) {

						return BackgroundTaskConstants.
							ISOLATION_LEVEL_NOT_ISOLATED;
					}
					else if (Objects.equals(method.getName(), "isSerial")) {
						return false;
					}

					return null;
				});

		Class<?> backgroundTaskExecutorClass =
			backgroundTaskExecutor.getClass();

		Bundle bundle = FrameworkUtil.getBundle(CTProcessSearcherTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<BackgroundTaskExecutor> serviceRegistration =
			bundleContext.registerService(
				BackgroundTaskExecutor.class, backgroundTaskExecutor,
				HashMapDictionaryBuilder.<String, Object>put(
					"background.task.executor.class.name",
					backgroundTaskExecutorClass.getName()
				).build());

		try {
			CTCollection ctCollection = _addCTCollection();

			CTProcess ctProcess = _addCTProcess(
				backgroundTaskExecutor, ctCollection.getCtCollectionId());

			_assertHits(
				_getUIDs(ctProcess),
				_byAttribute(
					"statuses",
					new int[] {BackgroundTaskConstants.STATUS_FAILED}),
				_byKeywords(ctCollection.getName()));

			_assertHits(
				Collections.emptyList(),
				_byAttribute(
					"statuses",
					new int[] {BackgroundTaskConstants.STATUS_SUCCESSFUL}),
				_byKeywords(ctCollection.getName()));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testSearchByUserId() throws Exception {
		CTCollection ctCollection = _addCTCollection();

		User user = UserTestUtil.addUser();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			DDMStructureTestUtil.addStructure(
				TestPropsValues.getGroupId(), JournalArticle.class.getName());
		}

		CTProcess ctProcess = _ctProcessLocalService.addCTProcess(
			user.getUserId(), ctCollection.getCtCollectionId());

		_assertHits(
			_getUIDs(ctProcess), _byAttribute("userId", user.getUserId()));
	}

	private CTCollection _addCTCollection() throws Exception {
		return _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private CTProcess _addCTProcess(
			BackgroundTaskExecutor backgroundTaskExecutor, long ctCollectionId)
		throws Exception {

		long ctProcessId = _counterLocalService.increment(
			CTProcess.class.getName());

		CTProcess ctProcess = _ctProcessLocalService.createCTProcess(
			ctProcessId);

		ctProcess.setCompanyId(TestPropsValues.getCompanyId());
		ctProcess.setUserId(TestPropsValues.getUserId());
		ctProcess.setCreateDate(new Date());
		ctProcess.setCtCollectionId(ctCollectionId);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.addBackgroundTask(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				RandomTestUtil.randomString(), null,
				backgroundTaskExecutor.getClass(), null, null);

		ctProcess.setBackgroundTaskId(backgroundTask.getBackgroundTaskId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			DDMStructureTestUtil.addStructure(
				TestPropsValues.getGroupId(), JournalArticle.class.getName());
		}

		return _ctProcessLocalService.addCTProcess(ctProcess);
	}

	private CTProcess _addCTProcess(long ctCollectionId) throws Exception {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			DDMStructureTestUtil.addStructure(
				TestPropsValues.getGroupId(), JournalArticle.class.getName());
		}

		return _ctProcessLocalService.addCTProcess(
			TestPropsValues.getUserId(), ctCollectionId);
	}

	private void _assertHits(
			List<String> expectedValues,
			Consumer<SearchRequestBuilder>... consumers)
		throws Exception {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				CTProcess.class
			).withSearchRequestBuilder(
				consumers
			).build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getResponseString(), searchResponse.getDocuments(),
			Field.UID, expectedValues);
	}

	private Consumer<SearchRequestBuilder> _byAttribute(
		String field, Serializable value) {

		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(field, value));
	}

	private Consumer<SearchRequestBuilder> _byKeywords(String keywords) {
		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setKeywords(keywords));
	}

	private List<String> _getUIDs(CTProcess... ctProcesses) {
		String[] uids = new String[ctProcesses.length];

		for (int i = 0; i < ctProcesses.length; i++) {
			uids[i] = _uidFactory.getUID(ctProcesses[i]);
		}

		return Arrays.asList(uids);
	}

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	@Inject
	private UIDFactory _uidFactory;

}