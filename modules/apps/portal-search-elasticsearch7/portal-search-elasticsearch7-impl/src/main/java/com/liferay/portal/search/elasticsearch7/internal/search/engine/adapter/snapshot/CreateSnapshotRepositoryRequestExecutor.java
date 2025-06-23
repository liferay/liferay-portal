/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.snapshot;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryResponse;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.SnapshotClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.repositories.fs.FsRepository;

/**
 * @author Michael C. Han
 */
public class CreateSnapshotRepositoryRequestExecutor {

	public CreateSnapshotRepositoryRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public CreateSnapshotRepositoryResponse execute(
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest) {

		PutRepositoryRequest putRepositoryRequest = createPutRepositoryRequest(
			createSnapshotRepositoryRequest);

		AcknowledgedResponse acknowledgedResponse = getAcknowledgedResponse(
			putRepositoryRequest, createSnapshotRepositoryRequest);

		return new CreateSnapshotRepositoryResponse(
			acknowledgedResponse.isAcknowledged());
	}

	protected PutRepositoryRequest createPutRepositoryRequest(
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest) {

		PutRepositoryRequest putRepositoryRequest = new PutRepositoryRequest(
			createSnapshotRepositoryRequest.getName());

		Settings.Builder builder = Settings.builder();

		builder.put(
			FsRepository.COMPRESS_SETTING.getKey(),
			createSnapshotRepositoryRequest.isCompress());

		builder.put(
			FsRepository.LOCATION_SETTING.getKey(),
			createSnapshotRepositoryRequest.getLocation());

		putRepositoryRequest.settings(builder);

		putRepositoryRequest.type(createSnapshotRepositoryRequest.getType());
		putRepositoryRequest.verify(createSnapshotRepositoryRequest.isVerify());

		return putRepositoryRequest;
	}

	protected AcknowledgedResponse getAcknowledgedResponse(
		PutRepositoryRequest putRepositoryRequest,
		CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				createSnapshotRepositoryRequest.getConnectionId(),
				createSnapshotRepositoryRequest.isPreferLocalCluster());

		SnapshotClient snapshotClient = restHighLevelClient.snapshot();

		try {
			return snapshotClient.createRepository(
				putRepositoryRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}