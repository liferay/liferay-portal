/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cache.transactional;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.SkipReplicationThreadLocal;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.internal.cache.InvalidationSequence;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionAttribute;
import com.liferay.portal.kernel.transaction.TransactionDefinition;
import com.liferay.portal.kernel.transaction.TransactionLifecycleListener;
import com.liferay.portal.kernel.transaction.TransactionStatus;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Shuyang Zhou
 */
public class TransactionalPortalCacheUtil {

	public static final TransactionLifecycleListener
		TRANSACTION_LIFECYCLE_LISTENER = new TransactionLifecycleListener() {

			@Override
			public void committed(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				if (!_isTransactionalCacheEnabled()) {
					return;
				}

				Propagation propagation = transactionAttribute.getPropagation();

				if (propagation == Propagation.NESTED) {
					if (transactionStatus.isNewTransaction()) {
						commit(transactionAttribute.isReadOnly());
					}
					else {
						commitSavepoint();
					}
				}
				else if (propagation.value() >=
							TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {

					List<List<PortalCacheMap>> backupPortalCacheMaps =
						_backupPortalCacheMaps.get();

					_portalCacheMaps.set(
						backupPortalCacheMaps.remove(
							backupPortalCacheMaps.size() - 1));
				}
				else if (transactionStatus.isNewTransaction()) {
					commit(transactionAttribute.isReadOnly());
				}
			}

			@Override
			public void created(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				if (!_isTransactionalCacheEnabled()) {
					return;
				}

				Propagation propagation = transactionAttribute.getPropagation();

				if (propagation == Propagation.NESTED) {
					_begin(!transactionStatus.isNewTransaction());
				}
				else if (propagation.value() >=
							TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {

					List<List<PortalCacheMap>> backupPortalCacheMaps =
						_backupPortalCacheMaps.get();

					backupPortalCacheMaps.add(_portalCacheMaps.get());

					_portalCacheMaps.remove();
				}
				else if (transactionStatus.isNewTransaction()) {
					begin();
				}
			}

			@Override
			public void rollbacked(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus, Throwable throwable) {

				if (!_isTransactionalCacheEnabled()) {
					return;
				}

				Propagation propagation = transactionAttribute.getPropagation();

				if (propagation == Propagation.NESTED) {
					rollback();

					EntityCacheUtil.clearLocalCache();
					FinderCacheUtil.clearLocalCache();
				}
				else if (propagation.value() >=
							TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {

					List<List<PortalCacheMap>> backupPortalCacheMaps =
						_backupPortalCacheMaps.get();

					_portalCacheMaps.set(
						backupPortalCacheMaps.remove(
							backupPortalCacheMaps.size() - 1));
				}
				else if (transactionStatus.isNewTransaction()) {
					rollback();

					EntityCacheUtil.clearLocalCache();
					FinderCacheUtil.clearLocalCache();
				}
			}

		};

	public static void begin() {
		_begin(false);
	}

	public static void commit(boolean readOnly) {
		PortalCacheMap portalCacheMap = _popPortalCacheMap();

		for (UncommittedBuffer uncommittedBuffer : portalCacheMap.values()) {
			uncommittedBuffer.commit(readOnly, portalCacheMap._startSequence);
		}

		portalCacheMap.clear();
	}

	public static void commitSavepoint() {
		PortalCacheMap portalCacheMap = _popPortalCacheMap();

		PortalCacheMap parentPortalCacheMap = _peekPortalCacheMap();

		for (PortalCache<? extends Serializable, ?> portalCache :
				portalCacheMap.keySet()) {

			UncommittedBuffer uncommittedBuffer = portalCacheMap.get(
				portalCache);
			UncommittedBuffer parentUncommittedBuffer =
				parentPortalCacheMap.get(portalCache);

			if (parentUncommittedBuffer == null) {
				parentPortalCacheMap.put(portalCache, uncommittedBuffer);
			}
			else {
				uncommittedBuffer.replay(parentUncommittedBuffer);
			}
		}

		portalCacheMap.clear();
	}

	public static <K extends Serializable, V> boolean completePut(
		PortalCache<K, V> portalCache, K key, V value) {

		PendingPut pendingPut = _pendingPut.get();

		if ((pendingPut == null) || (pendingPut._portalCache != portalCache) ||
			!key.equals(pendingPut._key) || isEnabled()) {

			PortalCacheHelperUtil.putWithoutReplicator(portalCache, key, value);

			return true;
		}

		_pendingPut.remove();

		return _invalidationSequence.publish(
			_getRegionName(portalCache), pendingPut._sequence,
			() -> PortalCacheHelperUtil.putWithoutReplicator(
				portalCache, key, value),
			() -> PortalCacheHelperUtil.removeWithoutReplicator(
				portalCache, key));
	}

	public static <K extends Serializable, V> V get(
		PortalCache<K, V> portalCache, K key) {

		// A nested savepoint shares its enclosing transaction's connection, so
		// its reads must see the entries buffered by that transaction; search
		// outward through savepoint scopes, but stop at the first independent
		// transaction (such as REQUIRES_NEW), which must stay isolated

		List<PortalCacheMap> portalCacheMaps = _portalCacheMaps.get();

		int index = portalCacheMaps.size() - 1;

		V value = null;

		while (true) {
			PortalCacheMap portalCacheMap = portalCacheMaps.get(index);

			UncommittedBuffer uncommittedBuffer = portalCacheMap.get(
				portalCache);

			if (uncommittedBuffer != null) {
				ValueEntry valueEntry = uncommittedBuffer.get(key);

				if (valueEntry != null) {
					value = (V)valueEntry._value;

					break;
				}
			}

			if (!portalCacheMap._savepoint) {
				break;
			}

			index--;
		}

		if ((value == null) || (index != (portalCacheMaps.size() - 1))) {
			boolean[] uncommittedBufferMissMarker =
				_uncommittedBufferMissMarker.get();

			if (uncommittedBufferMissMarker != null) {
				uncommittedBufferMissMarker[0] = true;
			}
		}

		return value;
	}

	public static <K extends Serializable, V> V get(
		PortalCache<K, V> portalCache, K key,
		boolean[] uncommittedBufferMissMarker) {

		try (SafeCloseable safeCloseable =
				_uncommittedBufferMissMarker.setWithSafeCloseable(
					uncommittedBufferMissMarker)) {

			return portalCache.get(key);
		}
	}

	public static Serializable getNullHolder() {
		return _NULL_HOLDER;
	}

	public static boolean isEnabled() {
		if (!_isTransactionalCacheEnabled()) {
			return false;
		}

		List<PortalCacheMap> portalCacheMaps = _portalCacheMaps.get();

		return !portalCacheMaps.isEmpty();
	}

	public static <K extends Serializable> void preparePut(
		PortalCache<K, ?> portalCache, K key) {

		if (!_isTransactionalCacheEnabled()) {
			return;
		}

		List<PortalCacheMap> portalCacheMaps = _portalCacheMaps.get();

		if (!portalCacheMaps.isEmpty()) {
			return;
		}

		_pendingPut.set(
			new PendingPut(
				portalCache, key, _invalidationSequence.getSequence()));
	}

	public static <K extends Serializable, V> void put(
		PortalCache<K, V> portalCache, K key, V value, int ttl, boolean mvcc) {

		UncommittedBuffer uncommittedBuffer = _getUncommittedBuffer(
			portalCache, mvcc);

		uncommittedBuffer.put(
			key,
			new ValueEntry(value, ttl, SkipReplicationThreadLocal.isEnabled()));
	}

	public static <K extends Serializable, V> void removeAll(
		PortalCache<K, V> portalCache, boolean mvcc) {

		UncommittedBuffer uncommittedBuffer = _getUncommittedBuffer(
			portalCache, mvcc);

		uncommittedBuffer.removeAll(SkipReplicationThreadLocal.isEnabled());
	}

	public static void rollback() {
		PortalCacheMap portalCacheMap = _popPortalCacheMap();

		portalCacheMap.clear();
	}

	protected static class PortalCacheMap
		extends HashMap
			<PortalCache<? extends Serializable, ?>, UncommittedBuffer> {

		protected PortalCacheMap(boolean savepoint) {
			_savepoint = savepoint;
		}

		private final boolean _savepoint;
		private final long _startSequence = _invalidationSequence.getSequence();

	}

	private static void _begin(boolean savepoint) {
		List<PortalCacheMap> portalCacheMaps = _portalCacheMaps.get();

		portalCacheMaps.add(new PortalCacheMap(savepoint));
	}

	private static String _getRegionName(PortalCache<?, ?> portalCache) {
		if (portalCache.isSharded()) {
			return _getShardedRegionName(
				CompanyThreadLocal.getNonsystemCompanyId(), portalCache);
		}

		return portalCache.getPortalCacheName();
	}

	private static String _getShardedRegionName(
		long companyId, PortalCache<?, ?> portalCache) {

		return portalCache.getPortalCacheName() + StringPool.UNDERLINE +
			companyId;
	}

	@SuppressWarnings("unchecked")
	private static <K extends Serializable, V> UncommittedBuffer
		_getUncommittedBuffer(PortalCache<K, V> portalCache, boolean mvcc) {

		PortalCacheMap portalCacheMap = _peekPortalCacheMap();

		UncommittedBuffer uncommittedBuffer = portalCacheMap.get(portalCache);

		if (uncommittedBuffer == null) {
			if (mvcc) {
				if (portalCache.isSharded()) {
					uncommittedBuffer = new ShardedUncommittedBuffer(
						(PortalCache<Serializable, Object>)portalCache,
						(companyId, targetPortalCache) ->
							new MVCCUncommittedBuffer(targetPortalCache));
				}
				else {
					uncommittedBuffer = new MVCCUncommittedBuffer(
						(PortalCache<Serializable, Object>)portalCache);
				}
			}
			else {
				if (portalCache.isSharded()) {
					uncommittedBuffer = new ShardedUncommittedBuffer(
						(PortalCache<Serializable, Object>)portalCache,
						(companyId, targetPortalCache) ->
							new InvalidatingUncommittedBuffer(
								companyId, targetPortalCache));
				}
				else {
					uncommittedBuffer = new InvalidatingUncommittedBuffer(
						(PortalCache<Serializable, Object>)portalCache);
				}
			}

			portalCacheMap.put(portalCache, uncommittedBuffer);
		}

		return uncommittedBuffer;
	}

	private static boolean _isTransactionalCacheEnabled() {
		if (_transactionalCacheEnabled == null) {
			_transactionalCacheEnabled = GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.TRANSACTIONAL_CACHE_ENABLED));
		}

		return _transactionalCacheEnabled;
	}

	private static PortalCacheMap _peekPortalCacheMap() {
		List<PortalCacheMap> portalCacheMaps = _portalCacheMaps.get();

		return portalCacheMaps.get(portalCacheMaps.size() - 1);
	}

	private static PortalCacheMap _popPortalCacheMap() {
		List<PortalCacheMap> portalCacheMaps = _portalCacheMaps.get();

		return portalCacheMaps.remove(portalCacheMaps.size() - 1);
	}

	private static final Serializable _NULL_HOLDER = "NULL_HOLDER";

	private static final ValueEntry _NULL_HOLDER_VALUE_ENTRY = new ValueEntry(
		_NULL_HOLDER, PortalCache.DEFAULT_TIME_TO_LIVE, false);

	private static final ThreadLocal<List<List<PortalCacheMap>>>
		_backupPortalCacheMaps = new CentralizedThreadLocal<>(
			TransactionalPortalCacheUtil.class.getName() +
				"._backupPortalCacheMaps",
			ArrayList::new, false);
	private static final InvalidationSequence _invalidationSequence =
		new InvalidationSequence();
	private static final ThreadLocal<PendingPut> _pendingPut =
		new CentralizedThreadLocal<>(
			TransactionalPortalCacheUtil.class.getName() + "._pendingPut");
	private static final ThreadLocal<List<PortalCacheMap>> _portalCacheMaps =
		new CentralizedThreadLocal<>(
			TransactionalPortalCacheUtil.class.getName() + "._portalCacheMaps",
			ArrayList::new, false);
	private static volatile Boolean _transactionalCacheEnabled;
	private static final CentralizedThreadLocal<boolean[]>
		_uncommittedBufferMissMarker = new CentralizedThreadLocal<>(
			TransactionalPortalCacheUtil.class.getName() +
				"._uncommittedBufferMissMarker");

	private static class InvalidatingUncommittedBuffer
		extends MVCCUncommittedBuffer {

		@Override
		public void commit(boolean readOnly, long startSequence) {
			if (skipCommit(readOnly)) {
				return;
			}

			if (readOnly) {
				_invalidationSequence.publish(
					_regionName, startSequence, () -> doCommit(false),
					() -> doCommit(true));
			}
			else {
				doCommit(
					_invalidationSequence.invalidate(
						_regionName, startSequence));
			}
		}

		@Override
		public void put(Serializable key, ValueEntry valueEntry) {
			ValueEntry oldValueEntry = super._uncommittedMap.put(
				key, valueEntry);

			if (oldValueEntry != null) {
				oldValueEntry.merge(valueEntry);
			}
		}

		private InvalidatingUncommittedBuffer(
			long companyId, PortalCache<Serializable, Object> portalCache) {

			super(portalCache);

			_regionName = _getShardedRegionName(companyId, portalCache);
		}

		private InvalidatingUncommittedBuffer(
			PortalCache<Serializable, Object> portalCache) {

			super(portalCache);

			_regionName = portalCache.getPortalCacheName();
		}

		private final String _regionName;

	}

	private static class MVCCUncommittedBuffer implements UncommittedBuffer {

		public void commit(boolean readOnly, long startSequence) {
			if (skipCommit(readOnly)) {
				return;
			}

			doCommit(false);
		}

		public ValueEntry get(Serializable key) {
			ValueEntry valueEntry = _uncommittedMap.get(key);

			if ((valueEntry == null) && _removeAll) {
				valueEntry = _NULL_HOLDER_VALUE_ENTRY;
			}

			return valueEntry;
		}

		public void put(Serializable key, ValueEntry valueEntry) {
			ValueEntry oldValueEntry = _uncommittedMap.put(key, valueEntry);

			if (oldValueEntry != null) {
				oldValueEntry.merge(valueEntry);

				if (oldValueEntry.isRemove()) {
					valueEntry._removed = true;
				}
			}
		}

		public void removeAll(boolean skipReplicator) {
			_uncommittedMap.clear();

			_removeAll = true;

			if (_skipReplicator) {
				_skipReplicator = skipReplicator;
			}
		}

		public void replay(UncommittedBuffer uncommittedBuffer) {
			if (_removeAll) {
				uncommittedBuffer.removeAll(_skipReplicator);
			}

			for (Map.Entry<Serializable, ValueEntry> entry :
					_uncommittedMap.entrySet()) {

				uncommittedBuffer.put(entry.getKey(), entry.getValue());
			}
		}

		protected void doCommit(boolean byRemove) {
			if (_removeAll) {
				if (_skipReplicator) {
					PortalCacheHelperUtil.removeAllWithoutReplicator(
						_portalCache);
				}
				else {
					_portalCache.removeAll();
				}
			}

			for (Map.Entry<? extends Serializable, ValueEntry> entry :
					_uncommittedMap.entrySet()) {

				ValueEntry valueEntry = entry.getValue();

				if (byRemove) {
					valueEntry.commitToByRemove(_portalCache, entry.getKey());
				}
				else {
					valueEntry.commitTo(_portalCache, entry.getKey());
				}
			}
		}

		protected boolean skipCommit(boolean readOnly) {
			if (readOnly) {
				_removeAll = false;

				Collection<ValueEntry> valueEntries = _uncommittedMap.values();

				Iterator<ValueEntry> iterator = valueEntries.iterator();

				while (iterator.hasNext()) {
					ValueEntry valueEntry = iterator.next();

					if (valueEntry.isRemove()) {
						iterator.remove();
					}
				}
			}

			if (!_removeAll && _uncommittedMap.isEmpty()) {
				return true;
			}

			return false;
		}

		private MVCCUncommittedBuffer(
			PortalCache<Serializable, Object> portalCache) {

			_portalCache = portalCache;
		}

		private final PortalCache<Serializable, Object> _portalCache;
		private boolean _removeAll;
		private boolean _skipReplicator = true;
		private final Map<Serializable, ValueEntry> _uncommittedMap =
			new HashMap<>();

	}

	private static class PendingPut {

		private PendingPut(
			PortalCache<?, ?> portalCache, Serializable key, long sequence) {

			_portalCache = portalCache;
			_key = key;
			_sequence = sequence;
		}

		private final Serializable _key;
		private final PortalCache<?, ?> _portalCache;
		private final long _sequence;

	}

	private static class ShardedUncommittedBuffer implements UncommittedBuffer {

		@Override
		public void commit(boolean readOnly, long startSequence) {
			for (Map.Entry<Long, UncommittedBuffer> entry :
					_shardedUncommittedBuffers.entrySet()) {

				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							entry.getKey())) {

					UncommittedBuffer uncommittedBuffer = entry.getValue();

					uncommittedBuffer.commit(readOnly, startSequence);
				}
			}

			_shardedUncommittedBuffers.clear();
		}

		@Override
		public ValueEntry get(Serializable key) {
			UncommittedBuffer uncommittedBuffer =
				_shardedUncommittedBuffers.get(
					CompanyThreadLocal.getNonsystemCompanyId());

			if (uncommittedBuffer == null) {
				return null;
			}

			return uncommittedBuffer.get(key);
		}

		@Override
		public void put(Serializable key, ValueEntry valueEntry) {
			UncommittedBuffer uncommittedBuffer =
				_shardedUncommittedBuffers.computeIfAbsent(
					CompanyThreadLocal.getNonsystemCompanyId(),
					companyId -> _uncommittedBufferFunction.apply(
						companyId, _portalCache));

			uncommittedBuffer.put(key, valueEntry);
		}

		@Override
		public void removeAll(boolean skipReplicator) {
			UncommittedBuffer uncommittedBuffer =
				_shardedUncommittedBuffers.computeIfAbsent(
					CompanyThreadLocal.getNonsystemCompanyId(),
					companyId -> _uncommittedBufferFunction.apply(
						companyId, _portalCache));

			uncommittedBuffer.removeAll(skipReplicator);
		}

		@Override
		public void replay(UncommittedBuffer uncommittedBuffer) {
			ShardedUncommittedBuffer shardedUncommittedBuffer =
				(ShardedUncommittedBuffer)uncommittedBuffer;

			Map<Long, UncommittedBuffer> shardedUncommittedBuffers =
				shardedUncommittedBuffer._shardedUncommittedBuffers;

			for (Map.Entry<Long, UncommittedBuffer> entry :
					_shardedUncommittedBuffers.entrySet()) {

				UncommittedBuffer parentUncommittedBuffer =
					shardedUncommittedBuffers.get(entry.getKey());

				if (parentUncommittedBuffer == null) {
					parentUncommittedBuffer =
						shardedUncommittedBuffer._uncommittedBufferFunction.
							apply(
								entry.getKey(),
								shardedUncommittedBuffer._portalCache);

					shardedUncommittedBuffers.put(
						entry.getKey(), parentUncommittedBuffer);
				}

				UncommittedBuffer uncommittedBuffer2 = entry.getValue();

				uncommittedBuffer2.replay(parentUncommittedBuffer);
			}
		}

		private ShardedUncommittedBuffer(
			PortalCache<Serializable, Object> portalCache,
			BiFunction
				<Long, PortalCache<Serializable, Object>, UncommittedBuffer>
					uncommittedBufferFunction) {

			_portalCache = portalCache;
			_uncommittedBufferFunction = uncommittedBufferFunction;
		}

		private final PortalCache<Serializable, Object> _portalCache;
		private final Map<Long, UncommittedBuffer> _shardedUncommittedBuffers =
			new HashMap<>();
		private final BiFunction
			<Long, PortalCache<Serializable, Object>, UncommittedBuffer>
				_uncommittedBufferFunction;

	}

	private static class ValueEntry {

		public void commitTo(
			PortalCache<Serializable, Object> portalCache, Serializable key) {

			boolean remove = isRemove();

			if (remove || _removed) {
				if (_skipReplicator) {
					PortalCacheHelperUtil.removeWithoutReplicator(
						portalCache, key);
				}
				else {
					portalCache.remove(key);
				}
			}

			if (!remove) {
				if (_skipReplicator) {
					PortalCacheHelperUtil.putWithoutReplicator(
						portalCache, key, _value, _ttl);
				}
				else {
					portalCache.put(key, _value, _ttl);
				}
			}
		}

		public void commitToByRemove(
			PortalCache<Serializable, Object> portalCache, Serializable key) {

			if (_skipReplicator) {
				PortalCacheHelperUtil.removeWithoutReplicator(portalCache, key);
			}
			else {
				portalCache.remove(key);
			}
		}

		public boolean isRemove() {
			if (_value == _NULL_HOLDER) {
				return true;
			}

			return false;
		}

		public void merge(ValueEntry valueEntry) {
			if (!_skipReplicator) {
				valueEntry._skipReplicator = false;
			}
		}

		private ValueEntry(Object value, int ttl, boolean skipReplicator) {
			_value = value;
			_ttl = ttl;
			_skipReplicator = skipReplicator;
		}

		private boolean _removed;
		private boolean _skipReplicator;
		private final int _ttl;
		private final Object _value;

	}

	private interface UncommittedBuffer {

		public void commit(boolean readOnly, long startSequence);

		public ValueEntry get(Serializable key);

		public void put(Serializable key, ValueEntry valueEntry);

		public void removeAll(boolean skipReplicator);

		public void replay(UncommittedBuffer uncommittedBuffer);

	}

}