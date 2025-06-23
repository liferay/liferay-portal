/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.engine.adapter.index.IndexRequestShardFailure;

import org.elasticsearch.action.ShardOperationFailedException;
import org.elasticsearch.rest.RestStatus;

/**
 * @author Michael C. Han
 */
public class IndexRequestShardFailureTranslatorUtil {

	public static IndexRequestShardFailure translate(
		ShardOperationFailedException shardOperationFailedException) {

		IndexRequestShardFailure indexRequestShardFailure =
			new IndexRequestShardFailure();

		indexRequestShardFailure.setIndex(
			shardOperationFailedException.index());
		indexRequestShardFailure.setReason(
			shardOperationFailedException.reason());
		indexRequestShardFailure.setShardId(
			shardOperationFailedException.shardId());

		RestStatus restStatus = shardOperationFailedException.status();

		indexRequestShardFailure.setRestStatus(restStatus.getStatus());

		return indexRequestShardFailure;
	}

}