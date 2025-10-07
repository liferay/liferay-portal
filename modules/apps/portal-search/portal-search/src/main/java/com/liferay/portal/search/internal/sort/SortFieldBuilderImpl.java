/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.sort;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.contributor.sort.SortFieldNameTranslator;
import com.liferay.portal.search.sort.SortFieldBuilder;

import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = SortFieldBuilder.class)
public class SortFieldBuilderImpl implements SortFieldBuilder {

	@Override
	public String getSortField(Class<?> entityClass, String orderByCol) {
		String sortField = _getSortField(entityClass, orderByCol);

		if (_defaultSortableTextFields.contains(sortField)) {
			return Field.getSortableFieldName(sortField);
		}

		return sortField;
	}

	@Override
	public String getSortField(
		Class<?> entityClass, String orderByCol, int sortType) {

		if ((sortType == Sort.DOUBLE_TYPE) || (sortType == Sort.FLOAT_TYPE) ||
			(sortType == Sort.INT_TYPE) || (sortType == Sort.LONG_TYPE)) {

			return Field.getSortableFieldName(orderByCol);
		}

		return getSortField(entityClass, orderByCol);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_defaultSortableTextFields = SetUtil.fromArray(
			PropsUtil.getArray(PropsKeys.INDEX_SORTABLE_TEXT_FIELDS));
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SortFieldNameTranslator.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(sortFieldNameTranslator, emitter) -> emitter.emit(
					sortFieldNameTranslator.getEntityClass())));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _getSortField(Class<?> entityClass, String orderByCol) {
		SortFieldNameTranslator sortFieldNameTranslator =
			_serviceTrackerMap.getService(entityClass);

		if (sortFieldNameTranslator == null) {
			Indexer<?> indexer = _indexerRegistry.getIndexer(entityClass);

			return indexer.getSortField(orderByCol);
		}

		return sortFieldNameTranslator.getSortFieldName(orderByCol);
	}

	private Set<String> _defaultSortableTextFields;

	@Reference
	private IndexerRegistry _indexerRegistry;

	private ServiceTrackerMap<Class<?>, SortFieldNameTranslator>
		_serviceTrackerMap;

}