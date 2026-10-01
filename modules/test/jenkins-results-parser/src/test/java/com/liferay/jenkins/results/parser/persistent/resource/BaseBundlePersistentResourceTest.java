/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.JenkinsStopBuildUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;

import java.util.Collections;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class BaseBundlePersistentResourceTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testSetStatus() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(
				Mockito.mock(JenkinsMaster.class), RandomTestUtil.randomLong());

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).setStatus(
			Mockito.any()
		);

		String why = RandomTestUtil.randomString();

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_queueItemWhy", why);

		baseBundlePersistentResource.setStatus(
			PersistentResource.Status.IN_QUEUE);

		String statusMessage = baseBundlePersistentResource.getStatusMessage();

		Assert.assertTrue(statusMessage, statusMessage.endsWith(": " + why));

		baseBundlePersistentResource.setStatus(
			PersistentResource.Status.IN_PROGRESS);
		baseBundlePersistentResource.setStatus(
			PersistentResource.Status.IN_QUEUE);

		statusMessage = baseBundlePersistentResource.getStatusMessage();

		Assert.assertFalse(statusMessage, statusMessage.contains(why));
	}

	@Test
	public void testUpdateCancelledQueueItem() {
		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);
		long queueId = RandomTestUtil.randomLong();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, queueId);

		JenkinsMaster.QueueItem queueItem = Mockito.mock(
			JenkinsMaster.QueueItem.class);

		Mockito.doReturn(
			true
		).when(
			queueItem
		).isCancelled();

		Mockito.doReturn(
			queueItem
		).when(
			jenkinsMaster
		).getQueueItem(
			queueId
		);

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource
		).start();

		for (int i = 0; i < 10; i++) {
			baseBundlePersistentResource.update();
		}

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(10)
		).start();
	}

	@Test
	public void testUpdateInQueue() throws Exception {
		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);
		long queueId = RandomTestUtil.randomLong();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, queueId);

		JenkinsMaster.QueueItem queueItem = Mockito.mock(
			JenkinsMaster.QueueItem.class);

		Mockito.doReturn(
			queueId
		).when(
			queueItem
		).getId();

		long currentTimeMillis = System.currentTimeMillis();

		Mockito.doReturn(
			currentTimeMillis - (1000 * 60 * 30)
		).when(
			queueItem
		).getInQueueSince();

		Mockito.doReturn(
			jenkinsMaster
		).when(
			queueItem
		).getJenkinsMaster();

		String why = RandomTestUtil.randomString();

		Mockito.doReturn(
			why
		).when(
			queueItem
		).getWhy();

		Mockito.doReturn(
			Collections.singletonList(queueItem)
		).when(
			jenkinsMaster
		).getQueueItems();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class, Mockito.CALLS_REAL_METHODS);
			MockedStatic<JenkinsStopBuildUtil>
				jenkinsStopBuildUtilMockedStatic = Mockito.mockStatic(
					JenkinsStopBuildUtil.class)) {

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.combine(Mockito.<String[]>any())
			).thenAnswer(
				invocation -> {
					StringBuilder sb = new StringBuilder();

					for (Object argument : invocation.getArguments()) {
						sb.append(argument);
					}

					return sb.toString();
				}
			);

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				currentTimeMillis
			);

			baseBundlePersistentResource.update();

			String statusMessage =
				baseBundlePersistentResource.getStatusMessage();

			Assert.assertTrue(
				statusMessage, statusMessage.endsWith(": " + why));

			Mockito.verify(
				baseBundlePersistentResource, Mockito.never()
			).start();

			Mockito.doReturn(
				currentTimeMillis - (1000 * 60 * 30) - 1
			).when(
				queueItem
			).getInQueueSince();

			for (int i = 0; i < 3; i++) {
				baseBundlePersistentResource.update();
			}

			jenkinsStopBuildUtilMockedStatic.verify(
				() -> JenkinsStopBuildUtil.cancelQueueItem(
					jenkinsMaster, queueId),
				Mockito.times(2));

			Mockito.verify(
				baseBundlePersistentResource, Mockito.times(2)
			).start();
		}
	}

	@Test
	public void testUpdateMissingQueueItem() {
		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, -1);

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

	@Test
	public void testUpdateStartedQueueItem() {
		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);
		long queueId = RandomTestUtil.randomLong();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, queueId);

		JenkinsMaster.QueueItem queueItem = Mockito.mock(
			JenkinsMaster.QueueItem.class);

		String executableURL =
			"https://" + RandomTestUtil.randomString() +
				"/job/app-server-bundle-builder/1/";

		Mockito.doReturn(
			executableURL
		).when(
			queueItem
		).getExecutableURL();

		Mockito.doReturn(
			queueItem
		).when(
			jenkinsMaster
		).getQueueItem(
			queueId
		);

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource
		).setProducerBuildURL(
			executableURL
		);

		Mockito.verify(
			baseBundlePersistentResource
		).setStatus(
			PersistentResource.Status.IN_PROGRESS
		);

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).start();
	}

	private BaseBundlePersistentResource _getBaseBundlePersistentResource(
		JenkinsMaster jenkinsMaster, long queueId) {

		BaseBundlePersistentResource baseBundlePersistentResource =
			Mockito.mock(BaseBundlePersistentResource.class);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).getStatusMessage();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).update();

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

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isController();

		return baseBundlePersistentResource;
	}

}