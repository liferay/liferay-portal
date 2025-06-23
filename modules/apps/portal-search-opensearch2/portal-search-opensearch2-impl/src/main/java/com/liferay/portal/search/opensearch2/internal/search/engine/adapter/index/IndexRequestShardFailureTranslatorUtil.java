/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.search.engine.adapter.index.IndexRequestShardFailure;

import org.opensearch.client.opensearch._types.ErrorCause;
import org.opensearch.client.opensearch._types.ShardFailure;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class IndexRequestShardFailureTranslatorUtil {

	public static IndexRequestShardFailure translate(
		ShardFailure shardFailure) {

		IndexRequestShardFailure indexRequestShardFailure =
			new IndexRequestShardFailure();

		indexRequestShardFailure.setIndex(shardFailure.index());

		ErrorCause errorCause = shardFailure.reason();

		indexRequestShardFailure.setReason(errorCause.reason());

		indexRequestShardFailure.setShardId(shardFailure.shard());

		return indexRequestShardFailure;
	}

}