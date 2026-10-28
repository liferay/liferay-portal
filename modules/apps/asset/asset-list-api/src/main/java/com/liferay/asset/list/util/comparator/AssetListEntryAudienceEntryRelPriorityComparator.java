/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.util.comparator;

import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Víctor Galán
 */
public class AssetListEntryAudienceEntryRelPriorityComparator
	extends OrderByComparator<AssetListEntryAudienceEntryRel> {

	public static final String ORDER_BY_ASC =
		"AssetListEntryAudienceEntryRel.priority ASC";

	public static final String ORDER_BY_DESC =
		"AssetListEntryAudienceEntryRel.priority DESC";

	public static final String[] ORDER_BY_FIELDS = {"priority"};

	public static AssetListEntryAudienceEntryRelPriorityComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel1,
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel2) {

		int value = Double.compare(
			assetListEntryAudienceEntryRel1.getPriority(),
			assetListEntryAudienceEntryRel2.getPriority());

		if (_ascending) {
			return value;
		}

		return Math.negateExact(value);
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return ORDER_BY_ASC;
		}

		return ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private AssetListEntryAudienceEntryRelPriorityComparator(
		boolean ascending) {

		_ascending = ascending;
	}

	private static final AssetListEntryAudienceEntryRelPriorityComparator
		_INSTANCE_ASCENDING =
			new AssetListEntryAudienceEntryRelPriorityComparator(true);

	private static final AssetListEntryAudienceEntryRelPriorityComparator
		_INSTANCE_DESCENDING =
			new AssetListEntryAudienceEntryRelPriorityComparator(false);

	private final boolean _ascending;

}