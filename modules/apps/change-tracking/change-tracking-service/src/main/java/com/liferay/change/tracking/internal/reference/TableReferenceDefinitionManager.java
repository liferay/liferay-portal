/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Preston Crary
 */
@Component(service = TableReferenceDefinitionManager.class)
public class TableReferenceDefinitionManager {

	public long getClassNameId(String tableName) {
		TableReferenceInfo<?> tableReferenceInfo =
			_serviceTrackerMap.getService(tableName);

		if (tableReferenceInfo == null) {
			throw new IllegalStateException(
				"No table reference definition for " + tableName);
		}

		return tableReferenceInfo.getClassNameId();
	}

	public long getClassNameId(Table<?> table) {
		return getClassNameId(table.getTableName());
	}

	public Map<Long, TableReferenceInfo<?>> getCombinedTableReferenceInfos() {
		Map<Long, TableReferenceInfo<?>> combinedTableReferenceInfos =
			_combinedTableReferenceInfos;

		if (combinedTableReferenceInfos != null) {
			return combinedTableReferenceInfos;
		}

		synchronized (this) {
			combinedTableReferenceInfos = new HashMap<>();

			List<TableReferenceInfo<?>> tableReferenceInfos = new ArrayList<>();

			for (TableReferenceInfo<?> tableReferenceInfo :
					_serviceTrackerMap.values()) {

				if (tableReferenceInfo != null) {
					tableReferenceInfos.add(tableReferenceInfo);
				}
			}

			for (TableReferenceInfo<?> tableReferenceInfo :
					tableReferenceInfos) {

				combinedTableReferenceInfos.put(
					tableReferenceInfo.getClassNameId(),
					_getCombinedTableReferenceInfo(
						tableReferenceInfo, tableReferenceInfos));
			}

			combinedTableReferenceInfos = Collections.unmodifiableMap(
				combinedTableReferenceInfos);

			_combinedTableReferenceInfos = combinedTableReferenceInfos;
		}

		return combinedTableReferenceInfos;
	}

	public Map<Long, TableReferenceInfo<?>> getCombinedTableReferenceInfos(
		long classNameId) {

		Map<Long, TableReferenceInfo<?>> combinedTableReferenceInfos =
			new HashMap<>();

		Map<Long, TableReferenceInfo<?>> allCombinedTableReferenceInfos =
			getCombinedTableReferenceInfos();

		Queue<Long> queue = new LinkedList<>();

		queue.add(classNameId);

		while (queue.size() > 0) {
			classNameId = queue.poll();

			TableReferenceInfo<?> tableReferenceInfo =
				allCombinedTableReferenceInfos.get(classNameId);

			combinedTableReferenceInfos.put(classNameId, tableReferenceInfo);

			Map<Table<?>, List<TableJoinHolder>> parentTableJoinHoldersMap =
				tableReferenceInfo.getParentTableJoinHoldersMap();

			Map<Table<?>, List<TableJoinHolder>> childTableJoinHoldersMap =
				tableReferenceInfo.getChildTableJoinHoldersMap();

			for (Table<?> table : childTableJoinHoldersMap.keySet()) {
				long childClassNameId = getClassNameId(table);

				if (parentTableJoinHoldersMap.containsKey(table)) {
					combinedTableReferenceInfos.put(
						childClassNameId,
						allCombinedTableReferenceInfos.get(childClassNameId));
				}
				else if (!combinedTableReferenceInfos.containsKey(
							childClassNameId)) {

					queue.add(childClassNameId);
				}
			}
		}

		return combinedTableReferenceInfos;
	}

	public boolean isChildModelOptional(
		long childModelClassNameId, long parentModelClassNameId) {

		Map<Long, TableReferenceInfo<?>> combinedTableReferenceInfos =
			getCombinedTableReferenceInfos();

		TableReferenceInfo<?> parentTableReferenceInfo =
			combinedTableReferenceInfos.get(parentModelClassNameId);

		if (parentTableReferenceInfo == null) {
			throw new IllegalArgumentException(
				"{parentModelClassNameId=" + parentModelClassNameId + "}");
		}

		TableReferenceInfo<?> childTableReferenceInfo =
			combinedTableReferenceInfos.get(childModelClassNameId);

		if (childTableReferenceInfo == null) {
			throw new IllegalArgumentException(
				"{childModelClassNameId=" + childModelClassNameId + "}");
		}

		Map<Table<?>, List<TableJoinHolder>> childTableJoinHoldersMap =
			parentTableReferenceInfo.getChildTableJoinHoldersMap();

		TableReferenceDefinition<?> childTableReferenceDefinition =
			childTableReferenceInfo.getTableReferenceDefinition();

		List<TableJoinHolder> tableJoinHolders = childTableJoinHoldersMap.get(
			childTableReferenceDefinition.getTable());

		if (tableJoinHolders == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"{childModelClassNameId=", childModelClassNameId,
					", parentModelClassNameId=", parentModelClassNameId, "}"));
		}

		for (TableJoinHolder tableJoinHolder : tableJoinHolders) {
			if (!tableJoinHolder.isReversed()) {
				return false;
			}
		}

		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<TableReferenceDefinition<?>>)
				(Class<?>)TableReferenceDefinition.class,
			null,
			(serviceReference, emitter) -> {
				TableReferenceDefinition<?> tableReferenceDefinition =
					bundleContext.getService(serviceReference);

				try {
					Table<?> table = tableReferenceDefinition.getTable();

					emitter.emit(table.getTableName());

					synchronized (this) {
						_combinedTableReferenceInfos = null;
					}
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			},
			new TableReferenceDefinitionServiceTrackerCustomizer(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private Map<Table<?>, List<TableJoinHolder>> _copyTableJoinHoldersMap(
		Map<Table<?>, List<TableJoinHolder>> tableJoinHoldersMap) {

		Map<Table<?>, List<TableJoinHolder>> copy = new HashMap<>();

		for (Map.Entry<Table<?>, List<TableJoinHolder>> entry :
				tableJoinHoldersMap.entrySet()) {

			copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}

		return copy;
	}

	private <T extends Table<T>> TableReferenceInfo<T>
		_getCombinedTableReferenceInfo(
			TableReferenceInfo<T> tableReferenceInfo,
			List<TableReferenceInfo<?>> tableReferenceInfos) {

		Map<Table<?>, List<TableJoinHolder>> combinedParentTableJoinHoldersMap =
			_copyTableJoinHoldersMap(
				tableReferenceInfo.getParentTableJoinHoldersMap());

		Map<Table<?>, List<TableJoinHolder>> combinedChildTableJoinHoldersMap =
			_copyTableJoinHoldersMap(
				tableReferenceInfo.getChildTableJoinHoldersMap());

		TableReferenceDefinition<T> tableReferenceDefinition =
			tableReferenceInfo.getTableReferenceDefinition();

		T table = tableReferenceDefinition.getTable();

		for (TableReferenceInfo<?> currentTableReferenceInfo :
				tableReferenceInfos) {

			TableReferenceDefinition<?> currentTableReferenceDefinition =
				currentTableReferenceInfo.getTableReferenceDefinition();

			Map<Table<?>, List<TableJoinHolder>>
				currentParentTableJoinHoldersMap =
					currentTableReferenceInfo.getParentTableJoinHoldersMap();

			List<TableJoinHolder> currentParentTableJoinHolders =
				currentParentTableJoinHoldersMap.get(table);

			if (currentParentTableJoinHolders != null) {
				List<TableJoinHolder> combinedChildTableJoinHolders =
					combinedChildTableJoinHoldersMap.computeIfAbsent(
						currentTableReferenceDefinition.getTable(),
						key -> new ArrayList<>());

				combinedChildTableJoinHolders.addAll(
					TransformUtil.transform(
						currentParentTableJoinHolders,
						currentParentTableJoinHolder -> TableJoinHolder.reverse(
							currentParentTableJoinHolder)));
			}

			Map<Table<?>, List<TableJoinHolder>>
				currentChildTableJoinHoldersMap =
					currentTableReferenceInfo.getChildTableJoinHoldersMap();

			List<TableJoinHolder> currentChildTableJoinHolders =
				currentChildTableJoinHoldersMap.get(table);

			if (currentChildTableJoinHolders != null) {
				List<TableJoinHolder> combinedParentTableJoinHolders =
					combinedParentTableJoinHoldersMap.computeIfAbsent(
						currentTableReferenceDefinition.getTable(),
						key -> new ArrayList<>());

				combinedParentTableJoinHolders.addAll(
					TransformUtil.transform(
						currentChildTableJoinHolders,
						currentChildTableJoinHolder -> TableJoinHolder.reverse(
							currentChildTableJoinHolder)));
			}
		}

		return new TableReferenceInfo<>(
			combinedChildTableJoinHoldersMap,
			tableReferenceInfo.getClassNameId(),
			combinedParentTableJoinHoldersMap, tableReferenceDefinition);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TableReferenceDefinitionManager.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private volatile Map<Long, TableReferenceInfo<?>>
		_combinedTableReferenceInfos;
	private ServiceTrackerMap<String, TableReferenceInfo<?>> _serviceTrackerMap;

	private class TableReferenceDefinitionServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<TableReferenceDefinition<?>, TableReferenceInfo<?>> {

		@Override
		public TableReferenceInfo<?> addingService(
			ServiceReference<TableReferenceDefinition<?>> serviceReference) {

			TableReferenceDefinition<?> tableReferenceDefinition =
				_bundleContext.getService(serviceReference);

			TableReferenceInfo<?> tableReferenceInfo =
				_createTableReferenceContext(tableReferenceDefinition);

			if (tableReferenceInfo == null) {
				_bundleContext.ungetService(serviceReference);

				return null;
			}

			return tableReferenceInfo;
		}

		@Override
		public void modifiedService(
			ServiceReference<TableReferenceDefinition<?>> serviceReference,
			TableReferenceInfo<?> tableReferenceInfo) {
		}

		@Override
		public void removedService(
			ServiceReference<TableReferenceDefinition<?>> serviceReference,
			TableReferenceInfo<?> tableReferenceInfo) {

			synchronized (TableReferenceDefinitionManager.this) {
				_combinedTableReferenceInfos = null;
			}

			_bundleContext.ungetService(serviceReference);
		}

		private TableReferenceDefinitionServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private <T extends Table<T>> TableReferenceInfo<?>
			_createTableReferenceContext(
				TableReferenceDefinition<T> tableReferenceDefinition) {

			Column<T, Long> primaryKeyColumn = TableUtil.getPrimaryKeyColumn(
				tableReferenceDefinition.getTable());

			if (primaryKeyColumn == null) {
				_log.error(
					"No long type primary key column found for " +
						tableReferenceDefinition);

				return null;
			}

			BasePersistence<?> basePersistence =
				tableReferenceDefinition.getBasePersistence();

			return TableReferenceInfoFactory.create(
				_classNameLocalService.getClassNameId(
					basePersistence.getModelClass()),
				primaryKeyColumn, tableReferenceDefinition);
		}

		private final BundleContext _bundleContext;

	}

}