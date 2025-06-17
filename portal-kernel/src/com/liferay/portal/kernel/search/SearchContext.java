/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.transaction.TransactionLifecycleListener;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 */
public class SearchContext implements Serializable {

	public static boolean isBatchMode() {
		Map.Entry<Set<Future<?>>, List<Callable<Void>>> entry =
			_batchModeSyncFuturesAndCallables.get();

		if (entry == null) {
			return false;
		}

		return true;
	}

	public static SafeCloseable openBatchMode() {
		return openBatchMode(true);
	}

	public static SafeCloseable openBatchMode(boolean commit) {
		TransactionLifecycleListener transactionLifecycleListener;

		if (commit) {
			transactionLifecycleListener =
				_transactionLifecycleListenerSnapshot.get();
		}
		else {
			transactionLifecycleListener = null;
		}

		if (transactionLifecycleListener != null) {
			transactionLifecycleListener.created(null, null);
		}

		SafeCloseable safeCloseable =
			_batchModeSyncFuturesAndCallables.setWithSafeCloseable(
				new AbstractMap.SimpleImmutableEntry<>(
					Collections.newSetFromMap(new ConcurrentHashMap<>()),
					new ArrayList<>()));

		return () -> {
			Exception exception1 = null;

			try {
				Map.Entry<Set<Future<?>>, List<Callable<Void>>> entry =
					_batchModeSyncFuturesAndCallables.get();

				for (Future<?> future : entry.getKey()) {
					try {
						future.get();
					}
					catch (Exception exception2) {
						if (exception1 != null) {
							exception2.addSuppressed(exception1);
						}

						exception1 = exception2;
					}
				}

				for (Callable<?> callable : entry.getValue()) {
					try {
						callable.call();
					}
					catch (Exception exception2) {
						if (exception1 != null) {
							exception2.addSuppressed(exception1);
						}

						exception1 = exception2;
					}
				}
			}
			finally {
				safeCloseable.close();

				if (transactionLifecycleListener != null) {
					transactionLifecycleListener.committed(null, null);
				}

				try {
					if (commit) {
						IndexWriterHelperUtil.commit();
					}
				}
				catch (SearchException searchException) {
					if (exception1 != null) {
						searchException.addSuppressed(exception1);
					}

					ReflectionUtil.throwException(searchException);
				}
			}
		};
	}

	public static void registerBatchModeSyncCallable(Callable<Void> callable) {
		Map.Entry<Set<Future<?>>, List<Callable<Void>>> entry =
			_batchModeSyncFuturesAndCallables.get();

		if (entry == null) {
			throw new IllegalStateException("Not in batch mode");
		}

		List<Callable<Void>> batchModeSyncCallables = entry.getValue();

		batchModeSyncCallables.add(callable);
	}

	public static void registerBatchModeSyncFuture(Future<?> future) {
		Map.Entry<Set<Future<?>>, List<Callable<Void>>> entry =
			_batchModeSyncFuturesAndCallables.get();

		if (entry == null) {
			throw new IllegalStateException("Not in batch mode");
		}

		Set<Future<?>> batchModeSyncFutures = entry.getKey();

		batchModeSyncFutures.add(future);
	}

	public static void unregisterBatchModeSyncFuture(Future<?> future) {
		Map.Entry<Set<Future<?>>, List<Callable<Void>>> entry =
			_batchModeSyncFuturesAndCallables.get();

		if (entry != null) {
			Set<Future<?>> batchModeSyncFutures = entry.getKey();

			batchModeSyncFutures.remove(future);
		}
	}

	public void addFacet(Facet facet) {
		if (facet == null) {
			return;
		}

		_facets.put(facet.getFieldName(), facet);
	}

	public void addFullQueryEntryClassName(String entryClassName) {
		if (_fullQueryEntryClassNames == null) {
			_fullQueryEntryClassNames = new HashSet<>();
		}

		_fullQueryEntryClassNames.add(entryClassName);
	}

	public void addStats(Stats stats) {
		_stats.put(stats.getField(), stats);
	}

	public void clearFullQueryEntryClassNames() {
		_fullQueryEntryClassNames = null;
	}

	public long[] getAssetCategoryIds() {
		return _assetCategoryIds;
	}

	public String[] getAssetTagNames() {
		return _assetTagNames;
	}

	public long[] getAssetVocabularyIds() {
		return _assetVocabularyIds;
	}

	public Serializable getAttribute(String name) {
		if (_attributes == null) {
			return null;
		}

		return _attributes.get(name);
	}

	public Map<String, Serializable> getAttributes() {
		if (_attributes == null) {
			_attributes = new HashMap<>();
		}

		return _attributes;
	}

	public BooleanClause<Query>[] getBooleanClauses() {
		return _booleanClauses;
	}

	public long[] getCategoryIds() {
		return _categoryIds;
	}

	public long[] getClassTypeIds() {
		return _classTypeIds;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public int getEnd() {
		return _end;
	}

	public String[] getEntryClassNames() {
		if (_entryClassNames == null) {
			_entryClassNames = new String[0];
		}

		return _entryClassNames;
	}

	public Facet getFacet(String fieldName) {
		return _facets.get(fieldName);
	}

	public Map<String, Facet> getFacets() {
		return _facets;
	}

	public long[] getFolderIds() {
		return _folderIds;
	}

	public String[] getFullQueryEntryClassNames() {
		if (_fullQueryEntryClassNames == null) {
			return new String[0];
		}

		return _fullQueryEntryClassNames.toArray(new String[0]);
	}

	public GroupBy getGroupBy() {
		return _groupBy;
	}

	public long[] getGroupIds() {
		return _groupIds;
	}

	public String getKeywords() {
		return _keywords;
	}

	public String getLanguageId() {
		return LocaleUtil.toLanguageId(_locale);
	}

	public Layout getLayout() {
		return _layout;
	}

	public Locale getLocale() {
		return _locale;
	}

	public long[] getNodeIds() {
		return _nodeIds;
	}

	public long getOwnerUserId() {
		return _ownerUserId;
	}

	public String[] getPortletIds() {
		return _portletIds;
	}

	public QueryConfig getQueryConfig() {
		if (_queryConfig == null) {
			_queryConfig = new QueryConfig();
		}

		return _queryConfig;
	}

	public float getScoresThreshold() {
		return _scoresThreshold;
	}

	public Sort[] getSorts() {
		return _sorts;
	}

	public int getStart() {
		return _start;
	}

	public Map<String, Stats> getStats() {
		return Collections.unmodifiableMap(_stats);
	}

	public Stats getStats(String fieldName) {
		return _stats.get(fieldName);
	}

	public TimeZone getTimeZone() {
		return _timeZone;
	}

	public long getUserId() {
		return _userId;
	}

	public boolean hasOverridenKeywords() {
		return Validator.isNull(_originalKeywords);
	}

	public boolean isAndSearch() {
		return _andSearch;
	}

	public boolean isCommitImmediately() {
		if (isBatchMode()) {
			return false;
		}

		return _commitImmediately;
	}

	public boolean isIncludeAttachments() {
		return _includeAttachments;
	}

	public boolean isIncludeDiscussions() {
		return _includeDiscussions;
	}

	public boolean isIncludeFolders() {
		return _includeFolders;
	}

	public boolean isIncludeInternalAssetCategories() {
		return _includeInternalAssetCategories;
	}

	public boolean isIncludeLiveGroups() {
		return _includeLiveGroups;
	}

	public boolean isIncludeStagingGroups() {
		return _includeStagingGroups;
	}

	public boolean isLike() {
		return _like;
	}

	public boolean isScopeStrict() {
		return _scopeStrict;
	}

	public void overrideKeywords(String keywords) {
		_originalKeywords = _keywords;

		_keywords = keywords;
	}

	public void setAndSearch(boolean andSearch) {
		_andSearch = andSearch;
	}

	public void setAssetCategoryIds(long[] assetCategoryIds) {
		_assetCategoryIds = assetCategoryIds;
	}

	public void setAssetTagNames(String[] assetTagNames) {
		_assetTagNames = assetTagNames;
	}

	public void setAssetVocabularyIds(long[] assetVocabularyIds) {
		_assetVocabularyIds = assetVocabularyIds;
	}

	public void setAttribute(String name, Serializable value) {
		if (_attributes == null) {
			_attributes = new HashMap<>();
		}

		_attributes.put(name, value);
	}

	public void setAttributes(Map<String, Serializable> attributes) {
		_attributes = attributes;
	}

	public void setBooleanClauses(BooleanClause<Query>[] booleanClauses) {
		_booleanClauses = booleanClauses;
	}

	public void setCategoryIds(long[] categoryIds) {
		_categoryIds = categoryIds;
	}

	public void setClassTypeIds(long[] classTypeIds) {
		_classTypeIds = classTypeIds;
	}

	public void setCommitImmediately(boolean commitImmediately) {
		_commitImmediately = commitImmediately;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setEnd(int end) {
		_end = end;
	}

	public void setEntryClassNames(String[] entryClassNames) {
		_entryClassNames = entryClassNames;
	}

	public void setFacets(List<Facet> facets) {
		for (Facet facet : facets) {
			_facets.put(facet.getFieldName(), facet);
		}
	}

	public void setFolderIds(List<Long> folderIds) {
		_folderIds = ArrayUtil.toArray(folderIds.toArray(new Long[0]));
	}

	public void setFolderIds(long[] folderIds) {
		_folderIds = folderIds;
	}

	public void setGroupBy(GroupBy groupBy) {
		_groupBy = groupBy;
	}

	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;
	}

	public void setIncludeAttachments(boolean includeAttachments) {
		_includeAttachments = includeAttachments;
	}

	public void setIncludeDiscussions(boolean includeDiscussions) {
		_includeDiscussions = includeDiscussions;
	}

	public void setIncludeFolders(boolean includeFolders) {
		_includeFolders = includeFolders;
	}

	public void setIncludeInternalAssetCategories(
		boolean includeInternalAssetCategories) {

		_includeInternalAssetCategories = includeInternalAssetCategories;
	}

	public void setIncludeLiveGroups(boolean includeLiveGroups) {
		_includeLiveGroups = includeLiveGroups;
	}

	public void setIncludeStagingGroups(boolean includeStagingGroups) {
		_includeStagingGroups = includeStagingGroups;
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setLayout(Layout layout) {
		_layout = layout;
	}

	public void setLike(boolean like) {
		_like = like;
	}

	public void setLocale(Locale locale) {
		if (locale != null) {
			_locale = locale;
		}
	}

	public void setNodeIds(long[] nodeIds) {
		_nodeIds = nodeIds;
	}

	public void setOwnerUserId(long ownerUserId) {
		_ownerUserId = ownerUserId;
	}

	public void setPortletIds(String[] portletIds) {
		_portletIds = portletIds;
	}

	public void setScopeStrict(boolean scopeStrict) {
		_scopeStrict = scopeStrict;
	}

	public void setScoresThreshold(float scoresThreshold) {
		_scoresThreshold = scoresThreshold;
	}

	public void setSorts(Sort... sorts) {
		_sorts = sorts;
	}

	public void setStart(int start) {
		_start = start;
	}

	public void setTimeZone(TimeZone timeZone) {
		_timeZone = timeZone;
	}

	public void setUserId(long userId) {
		_userId = userId;

		if (_attributes != null) {
			_attributes.remove("searchPermissionContext");
		}
	}

	private static final CentralizedThreadLocal
		<Map.Entry<Set<Future<?>>, List<Callable<Void>>>>
			_batchModeSyncFuturesAndCallables = new CentralizedThreadLocal<>(
				SearchContext.class.getName() +
					"._batchModeSyncFuturesAndCallables");
	private static final Snapshot<TransactionLifecycleListener>
		_transactionLifecycleListenerSnapshot = new Snapshot<>(
			SearchContext.class, TransactionLifecycleListener.class,
			"(component.name=com.liferay.portal.search.internal.buffer." +
				"IndexerRequestBufferTransactionLifecycleListener)");

	private boolean _andSearch;
	private long[] _assetCategoryIds;
	private String[] _assetTagNames;
	private long[] _assetVocabularyIds;
	private Map<String, Serializable> _attributes;
	private BooleanClause<Query>[] _booleanClauses;
	private long[] _categoryIds;
	private long[] _classTypeIds;
	private boolean _commitImmediately;
	private long _companyId;
	private int _end = QueryUtil.ALL_POS;
	private String[] _entryClassNames;
	private final Map<String, Facet> _facets = new ConcurrentHashMap<>();
	private long[] _folderIds;
	private Set<String> _fullQueryEntryClassNames;
	private GroupBy _groupBy;
	private long[] _groupIds;
	private boolean _includeAttachments;
	private boolean _includeDiscussions;
	private boolean _includeFolders = true;
	private boolean _includeInternalAssetCategories;
	private boolean _includeLiveGroups = true;
	private boolean _includeStagingGroups = true;
	private String _keywords;
	private Layout _layout;
	private boolean _like;
	private Locale _locale = LocaleUtil.getMostRelevantLocale();
	private long[] _nodeIds;
	private String _originalKeywords;
	private long _ownerUserId;
	private String[] _portletIds;
	private QueryConfig _queryConfig;
	private boolean _scopeStrict = true;
	private float _scoresThreshold;
	private Sort[] _sorts;
	private int _start = QueryUtil.ALL_POS;
	private final Map<String, Stats> _stats = new ConcurrentHashMap<>();
	private TimeZone _timeZone;
	private long _userId;

}