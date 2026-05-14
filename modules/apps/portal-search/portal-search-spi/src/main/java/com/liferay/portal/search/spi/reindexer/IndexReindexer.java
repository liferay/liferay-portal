/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.spi.reindexer;

/**
 * @author Bryan Engler
 */
public interface IndexReindexer {

	public String getIndexNameSuffix();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), portal-search-spi, replaced by {@link
	 *             #reindex(long, ExecutionMode)}
	 */
	@Deprecated
	public default void reindex(long companyId) throws Exception {
		reindex(companyId, ExecutionMode.FULL);
	}

	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception;

	public default void reindex(long companyId, String executionMode)
		throws Exception {

		reindex(companyId, ExecutionMode.valueOf(executionMode.toUpperCase()));
	}

	/*
	create enum instead of String so that developers know what the
	different modes are. this could maybe be in portal-search-api instead, and
	also used for company index reindexing, but thats all internal, so probably
	not really necessary
	 */
	public enum ExecutionMode {

		CONCURRENT, FULL, SYNC

	}

}