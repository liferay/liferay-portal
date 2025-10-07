/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.SearchPermissionChecker;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.configuration.IndexWriterHelperConfiguration;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portal.search.internal.background.task.ReindexPortalBackgroundTaskExecutor;
import com.liferay.portal.search.internal.background.task.ReindexSingleIndexerBackgroundTaskExecutor;
import com.liferay.portal.search.model.uid.UIDFactory;

import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.IndexWriterHelperConfiguration",
	service = IndexWriterHelper.class
)
public class IndexWriterHelperImpl implements IndexWriterHelper {

	@Override
	public void addDocument(
			long companyId, Document document, boolean commitImmediately)
		throws SearchException {

		_enforceStandardUID(document);

		if (_indexStatusManager.isIndexReadOnly() || (document == null)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Add document " + document.toString());
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		_searchPermissionChecker.addPermissionFields(companyId, document);

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, commitImmediately);

		indexWriter.addDocument(searchContext, document);
	}

	@Override
	public void addDocuments(
			long companyId, Collection<Document> documents,
			boolean commitImmediately)
		throws SearchException {

		_enforceStandardUID(documents);

		if (_indexStatusManager.isIndexReadOnly() || (documents == null) ||
			documents.isEmpty()) {

			return;
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		for (Document document : documents) {
			if (_log.isDebugEnabled()) {
				_log.debug("Add document " + document.toString());
			}

			_searchPermissionChecker.addPermissionFields(companyId, document);
		}

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, commitImmediately);

		indexWriter.addDocuments(searchContext, documents);
	}

	@Override
	public void commit() throws SearchException {
		if (!_commitImmediately) {
			return;
		}

		for (Company company : _companyLocalService.getCompanies()) {
			commit(company.getCompanyId());
		}
	}

	@Override
	public void commit(long companyId) throws SearchException {
		if (!_commitImmediately) {
			return;
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		indexWriter.commit(searchContext);
	}

	@Override
	public void deleteDocument(
			long companyId, String uid, boolean commitImmediately)
		throws SearchException {

		if (_indexStatusManager.isIndexReadOnly()) {
			return;
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, commitImmediately);

		indexWriter.deleteDocument(searchContext, uid);
	}

	@Override
	public void deleteDocuments(
			long companyId, Collection<String> uids, boolean commitImmediately)
		throws SearchException {

		if (_indexStatusManager.isIndexReadOnly() || (uids == null) ||
			uids.isEmpty()) {

			return;
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, commitImmediately);

		indexWriter.deleteDocuments(searchContext, uids);
	}

	@Override
	public void deleteEntityDocuments(
			long companyId, String className, boolean commitImmediately)
		throws SearchException {

		if (_indexStatusManager.isIndexReadOnly()) {
			return;
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		if (searchEngine == null) {
			return;
		}

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, commitImmediately);

		indexWriter.deleteEntityDocuments(searchContext, className);
	}

	@Override
	public int getReindexTaskCount(long groupId, boolean completed)
		throws SearchException {

		return _backgroundTaskManager.getBackgroundTasksCount(
			groupId,
			new String[] {
				ReindexPortalBackgroundTaskExecutor.class.getName(),
				ReindexSingleIndexerBackgroundTaskExecutor.class.getName()
			},
			completed);
	}

	@Override
	public void indexKeyword(
			long companyId, String querySuggestion, float weight,
			String keywordType, Locale locale)
		throws SearchException {

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setKeywords(querySuggestion);
		searchContext.setLocale(locale);

		indexWriter.indexKeyword(searchContext, weight, keywordType);
	}

	@Override
	public void indexQuerySuggestionDictionaries(long companyId)
		throws SearchException {

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		indexWriter.indexQuerySuggestionDictionaries(searchContext);
	}

	@Override
	public void indexQuerySuggestionDictionary(long companyId, Locale locale)
		throws SearchException {

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setLocale(locale);

		indexWriter.indexQuerySuggestionDictionary(searchContext);
	}

	@Override
	public void indexSpellCheckerDictionaries(long companyId)
		throws SearchException {

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		indexWriter.indexSpellCheckerDictionaries(searchContext);
	}

	@Override
	public void indexSpellCheckerDictionary(long companyId, Locale locale)
		throws SearchException {

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setLocale(locale);

		indexWriter.indexSpellCheckerDictionary(searchContext);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             IndexStatusManager#isIndexReadWrite()}
	 */
	@Deprecated
	@Override
	public boolean isIndexReadOnly() {
		return _indexStatusManager.isIndexReadOnly();
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             IndexStatusManager#isIndexReadWrite(String)}
	 */
	@Deprecated
	@Override
	public boolean isIndexReadOnly(String className) {
		return _indexStatusManager.isIndexReadOnly(className);
	}

	@Override
	public void partiallyUpdateDocument(
			long companyId, Document document, boolean commitImmediately)
		throws SearchException {

		_enforceStandardUID(document);

		if (_indexStatusManager.isIndexReadOnly() || (document == null)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Document " + document.toString());
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		_searchPermissionChecker.addPermissionFields(companyId, document);

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, commitImmediately);

		indexWriter.partiallyUpdateDocument(searchContext, document);
	}

	@Override
	public void partiallyUpdateDocuments(
			long companyId, Collection<Document> documents,
			boolean commitImmediately)
		throws SearchException {

		_enforceStandardUID(documents);

		if (_indexStatusManager.isIndexReadOnly() || (documents == null) ||
			documents.isEmpty()) {

			return;
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		for (Document document : documents) {
			if (_log.isDebugEnabled()) {
				_log.debug("Document " + document.toString());
			}

			_searchPermissionChecker.addPermissionFields(companyId, document);
		}

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, commitImmediately);

		indexWriter.partiallyUpdateDocuments(searchContext, documents);
	}

	@Override
	public BackgroundTask reindex(
			long userId, String jobName, long[] companyIds,
			Map<String, Serializable> taskContextMap)
		throws SearchException {

		if (taskContextMap == null) {
			taskContextMap = new HashMap<>();
		}

		taskContextMap.put(
			BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true);
		taskContextMap.put(
			ReindexBackgroundTaskConstants.COMPANY_IDS, companyIds);

		try {
			return _backgroundTaskManager.addBackgroundTask(
				userId, BackgroundTaskConstants.GROUP_ID_DEFAULT, jobName,
				ReindexPortalBackgroundTaskExecutor.class.getName(),
				taskContextMap, new ServiceContext());
		}
		catch (PortalException portalException) {
			throw new SearchException(
				"Unable to schedule portal reindex", portalException);
		}
	}

	@Override
	public BackgroundTask reindex(
			long userId, String jobName, long[] companyIds, String className,
			Map<String, Serializable> taskContextMap)
		throws SearchException {

		if (Validator.isNull(className)) {
			return reindex(userId, jobName, companyIds, taskContextMap);
		}

		if (taskContextMap == null) {
			taskContextMap = new HashMap<>();
		}

		taskContextMap.put(
			BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true);
		taskContextMap.put(
			ReindexBackgroundTaskConstants.CLASS_NAME, className);
		taskContextMap.put(
			ReindexBackgroundTaskConstants.COMPANY_IDS, companyIds);

		try {
			return _backgroundTaskManager.addBackgroundTask(
				userId, BackgroundTaskConstants.GROUP_ID_DEFAULT, jobName,
				ReindexSingleIndexerBackgroundTaskExecutor.class.getName(),
				taskContextMap, new ServiceContext());
		}
		catch (PortalException portalException) {
			throw new SearchException(
				"Unable to schedule portal reindex", portalException);
		}
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             IndexStatusManager#setIndexReadOnly(boolean)}
	 */
	@Deprecated
	@Override
	public void setIndexReadOnly(boolean indexReadOnly) {
		_indexStatusManager.setIndexReadOnly(indexReadOnly);
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             IndexStatusManager#setIndexReadOnly(String, boolean)}
	 */
	@Deprecated
	@Override
	public void setIndexReadOnly(String className, boolean indexReadOnly) {
		_indexStatusManager.setIndexReadOnly(className, indexReadOnly);
	}

	@Override
	public void updateDocument(long companyId, Document document)
		throws SearchException {

		_enforceStandardUID(document);

		if (_indexStatusManager.isIndexReadOnly() || (document == null)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Document " + document.toString());
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		_searchPermissionChecker.addPermissionFields(companyId, document);

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, _commitImmediately);

		indexWriter.updateDocument(searchContext, document);
	}

	@Override
	public void updateDocuments(
			long companyId, Collection<Document> documents,
			boolean commitImmediately)
		throws SearchException {

		_enforceStandardUID(documents);

		if (_indexStatusManager.isIndexReadOnly() || (documents == null) ||
			documents.isEmpty()) {

			return;
		}

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		IndexWriter indexWriter = searchEngine.getIndexWriter();

		for (Document document : documents) {
			if (_log.isDebugEnabled()) {
				_log.debug("Document " + document.toString());
			}

			_searchPermissionChecker.addPermissionFields(companyId, document);
		}

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);

		_setCommitImmediately(searchContext, commitImmediately);

		indexWriter.updateDocuments(searchContext, documents);
	}

	@Override
	public void updatePermissionFields(String name, String primKey) {
		if (_indexStatusManager.isIndexReadOnly()) {
			return;
		}

		if (PermissionThreadLocal.isFlushResourcePermissionEnabled(
				name, primKey)) {

			_searchPermissionChecker.updatePermissionFields(
				_getIndexerModelName(name), primKey);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		IndexWriterHelperConfiguration indexWriterHelperConfiguration =
			ConfigurableUtil.createConfigurable(
				IndexWriterHelperConfiguration.class, properties);

		_commitImmediately =
			indexWriterHelperConfiguration.indexCommitImmediately();
	}

	@Reference
	protected UIDFactory uidFactory;

	private void _enforceStandardUID(Collection<Document> documents) {
		documents.forEach(this::_enforceStandardUID);
	}

	private void _enforceStandardUID(Document document) {
		uidFactory.getUID(document);
	}

	private String _getIndexerModelName(String name) {
		String[] names = StringUtil.split(
			name, ResourceActionsUtil.getCompositeModelNameSeparator());

		return names[0];
	}

	private void _setCommitImmediately(
		SearchContext searchContext, boolean commitImmediately) {

		if (!commitImmediately) {
			searchContext.setCommitImmediately(_commitImmediately);
		}
		else {
			searchContext.setCommitImmediately(true);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexWriterHelperImpl.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	private volatile boolean _commitImmediately;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private IndexStatusManager _indexStatusManager;

	@Reference
	private SearchEngineHelper _searchEngineHelper;

	@Reference
	private SearchPermissionChecker _searchPermissionChecker;

}