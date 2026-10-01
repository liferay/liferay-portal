/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.JenkinsMaster;

import java.util.Collections;

import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class BaseBundlePersistentResourceTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testUpdateMissingQueueItem() {
		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, -1);

		Mockito.doReturn(
			Collections.emptyList()
		).when(
			jenkinsMaster
		).getQueueItems();

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).start();

		for (int i = 0; i < 5; i++) {
			baseBundlePersistentResource.update();
		}

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(2)
		).start();
	}

	private BaseBundlePersistentResource _getBaseBundlePersistentResource(
		JenkinsMaster jenkinsMaster, long queueId) {

		BaseBundlePersistentResource baseBundlePersistentResource =
			Mockito.mock(BaseBundlePersistentResource.class);

		Mockito.doReturn(
			new JSONObject()
		).when(
			baseBundlePersistentResource
		).getDataJSONObject();

		Mockito.doReturn(
			jenkinsMaster
		).when(
			baseBundlePersistentResource
		).getProducerJenkinsMaster();

		Mockito.doReturn(
			queueId
		).when(
			baseBundlePersistentResource
		).getProducerQueueId();

		Mockito.doReturn(
			PersistentResource.Status.IN_QUEUE
		).when(
			baseBundlePersistentResource
		).getStatus();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).getStatusMessage();

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isController();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).update();

		return baseBundlePersistentResource;
	}

}