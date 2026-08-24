/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.io.Serializable;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class IndexWriterHelperUtil {

	public static void addDocument(
			long companyId, Document document, boolean commitImmediately)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.addDocument(companyId, document, commitImmediately);
	}

	public static void addDocuments(
			long companyId, Collection<Document> documents,
			boolean commitImmediately)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.addDocuments(companyId, documents, commitImmediately);
	}

	public static void commit() throws SearchException {
		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.commit();
	}

	public static void commit(long companyId) throws SearchException {
		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.commit(companyId);
	}

	public static void deleteDocument(
			long companyId, String uid, boolean commitImmediately)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.deleteDocument(companyId, uid, commitImmediately);
	}

	public static void deleteDocuments(
			long companyId, Collection<String> uids, boolean commitImmediately)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.deleteDocuments(companyId, uids, commitImmediately);
	}

	public static void deleteEntityDocuments(
			long companyId, String className, boolean commitImmediately)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.deleteEntityDocuments(
			companyId, className, commitImmediately);
	}

	public static int getReindexTaskCount(long groupId, boolean completed)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		return indexWriterHelper.getReindexTaskCount(groupId, completed);
	}

	public static void indexKeyword(
			long companyId, String querySuggestion, float weight,
			String keywordType, Locale locale)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.indexKeyword(
			companyId, querySuggestion, weight, keywordType, locale);
	}

	public static void indexQuerySuggestionDictionaries(long companyId)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.indexQuerySuggestionDictionaries(companyId);
	}

	public static void indexQuerySuggestionDictionary(
			long companyId, Locale locale)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.indexQuerySuggestionDictionary(companyId, locale);
	}

	public static void indexSpellCheckerDictionaries(long companyId)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.indexSpellCheckerDictionaries(companyId);
	}

	public static void indexSpellCheckerDictionary(
			long companyId, Locale locale)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.indexSpellCheckerDictionary(companyId, locale);
	}

	public static boolean isIndexCommitImmediately() {
		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		return indexWriterHelper.isIndexCommitImmediately();
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             com.liferay.portal.search.index.IndexStatusManager#isIndexReadOnly}
	 */
	@Deprecated
	public static boolean isIndexReadOnly() {
		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		if (IndexStatusManagerThreadLocal.isIndexReadOnly() ||
			indexWriterHelper.isIndexReadOnly()) {

			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             com.liferay.portal.search.index.IndexStatusManager#isIndexReadOnly(
	 *             String)}
	 */
	@Deprecated
	public static boolean isIndexReadOnly(String className) {
		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		return indexWriterHelper.isIndexReadOnly(className);
	}

	public static void partiallyUpdateDocument(
			long companyId, Document document, boolean commitImmediately)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.partiallyUpdateDocument(
			companyId, document, commitImmediately);
	}

	public static void partiallyUpdateDocuments(
			long companyId, Collection<Document> documents,
			boolean commitImmediately)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.partiallyUpdateDocuments(
			companyId, documents, commitImmediately);
	}

	public static BackgroundTask reindex(
			long userId, String jobName, long[] companyIds,
			Map<String, Serializable> taskContextMap)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		return indexWriterHelper.reindex(
			userId, jobName, companyIds, taskContextMap);
	}

	public static BackgroundTask reindex(
			long userId, String jobName, long[] companyIds, String className,
			Map<String, Serializable> taskContextMap)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		return indexWriterHelper.reindex(
			userId, jobName, companyIds, className, taskContextMap);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             com.liferay.portal.search.index.IndexStatusManager#setIndexReadOnly(
	 *             boolean)}
	 */
	@Deprecated
	public static void setIndexReadOnly(boolean indexReadOnly) {
		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.setIndexReadOnly(indexReadOnly);
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             com.liferay.portal.search.index.IndexStatusManager#setIndexReadOnly(
	 *             String, boolean)}
	 */
	@Deprecated
	public static void setIndexReadOnly(
		String className, boolean indexReadOnly) {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.setIndexReadOnly(className, indexReadOnly);
	}

	public static void updateDocument(long companyId, Document document)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.updateDocument(companyId, document);
	}

	public static void updateDocuments(
			long companyId, Collection<Document> documents,
			boolean commitImmediately)
		throws SearchException {

		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.updateDocuments(
			companyId, documents, commitImmediately);
	}

	public static void updatePermissionFields(String name, String primKey) {
		IndexWriterHelper indexWriterHelper = _getIndexWriterHelper();

		indexWriterHelper.updatePermissionFields(name, primKey);
	}

	private static IndexWriterHelper _getIndexWriterHelper() {
		IndexWriterHelper indexWriterHelper = _indexWriterHelperSnapshot.get();

		if (indexWriterHelper == null) {
			return DummyIndexWriterHelperHolder._dummyIndexWriterHelper;
		}

		return indexWriterHelper;
	}

	private static final Snapshot<IndexWriterHelper>
		_indexWriterHelperSnapshot = new Snapshot<>(
			IndexWriterHelperUtil.class, IndexWriterHelper.class);

	private static class DummyIndexWriterHelperHolder {

		private static final IndexWriterHelper _dummyIndexWriterHelper =
			ProxyFactory.newDummyInstance(IndexWriterHelper.class);

	}

}