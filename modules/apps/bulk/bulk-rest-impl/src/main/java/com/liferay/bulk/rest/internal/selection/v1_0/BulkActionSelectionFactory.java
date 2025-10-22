/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.selection.v1_0;

import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.dto.v1_0.SelectionScope;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.object.model.ObjectEntry;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringBundler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Andrea Sbarra
 */
@Component(service = BulkActionSelectionFactory.class)
public class BulkActionSelectionFactory {

	public BulkSelection<ObjectEntry> create(
		String search, Filter filter, BulkAction bulkAction) {

		BulkSelectionFactory<ObjectEntry> bulkSelectionFactory =
			_bulkSelectionFactoryRegistry.getBulkSelectionFactory(
				ObjectEntry.class.getName());

		return bulkSelectionFactory.create(
			_getParameterMap(
				search, filter,
				bulkAction.getBulkActionItems(),
				bulkAction.getSelectionScope()));
	}

	private Map<String, String[]> _getParameterMap(
		String search, Filter filter,
		BulkActionItem[] bulkActionItems, SelectionScope selectionScope) {

		if (selectionScope.getSelectAll()) {
			return HashMapBuilder.put(
				"filter", new String[] {filter.toString()}
			).put(
				"search", new String[] {search}
			).put(
				"selectAll",
				new String[] {Boolean.toString(selectionScope.getSelectAll())}
			).build();
		}

		return HashMapBuilder.put(
			"rowIds", () -> {
				if (bulkActionItems == null) {
					return null;
				}

				StringBundler sb = new StringBundler();
				for (BulkActionItem bulkActionItem : bulkActionItems) {
					sb.append(StringBundler.concat(
						bulkActionItem.getClassName(),
						StringPool.SPACE,
						String.valueOf(bulkActionItem.getClassPK()),
						StringPool.COMMA));
				}

				String rowIds = sb.toString();
				return rowIds.split(StringPool.COMMA);

			}).build();
	}

	@Reference
	private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;
}