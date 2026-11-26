/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.engine.adapter.index;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class FlushIndexResponse implements IndexResponse {

	public void addIndexRequestShardFailure(
		IndexRequestShardFailure indexRequestShardFailure) {

		if (_indexRequestShardFailures == null) {
			_indexRequestShardFailures = new ArrayList<>();
		}

		_indexRequestShardFailures.add(indexRequestShardFailure);
	}

	public int getFailedShards() {
		return _failedShards;
	}

	public List<IndexRequestShardFailure> getIndexRequestShardFailures() {
		return _indexRequestShardFailures;
	}

	public int getSuccessfulShards() {
		return _successfulShards;
	}

	public int getTotalShards() {
		return _totalShards;
	}

	public void setFailedShards(int failedShards) {
		_failedShards = failedShards;
	}

	public void setSuccessfulShards(int successfulShards) {
		_successfulShards = successfulShards;
	}

	public void setTotalShards(int totalShards) {
		_totalShards = totalShards;
	}

	private int _failedShards;
	private List<IndexRequestShardFailure> _indexRequestShardFailures;
	private int _successfulShards;
	private int _totalShards;

}