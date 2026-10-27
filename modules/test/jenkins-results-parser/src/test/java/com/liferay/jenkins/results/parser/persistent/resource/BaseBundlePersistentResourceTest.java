/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.BuildFactory;
import com.liferay.jenkins.results.parser.JenkinsAPIUtil;
import com.liferay.jenkins.results.parser.JenkinsCohort;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.JenkinsStopBuildUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.TopLevelBuild;

import java.io.IOException;

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Michael Hashimoto
 */
public class BaseBundlePersistentResourceTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testSetStatus() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(
				_getJenkinsMaster(), RandomTestUtil.randomLong());

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
	public void testStart() {
		_testStart(0, PersistentResource.Status.NOT_STARTED, invocation -> 0L);
		_testStart(
			0, PersistentResource.Status.NOT_STARTED,
			invocation -> {
				throw new RuntimeException(RandomTestUtil.randomString());
			});

		long queueId = _getQueueId();

		_testStart(
			queueId, PersistentResource.Status.IN_QUEUE, invocation -> queueId);
	}

	@Test
	public void testUpdate() throws Exception {
		AtomicReference<JSONObject> dataJSONObjectAtomicReference =
			new AtomicReference<>();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(_getJenkinsMaster(), 0);

		Mockito.doAnswer(
			invocation -> dataJSONObjectAtomicReference.get()
		).when(
			baseBundlePersistentResource
		).getDataJSONObject();

		AtomicInteger startCount = new AtomicInteger();

		Mockito.doAnswer(
			invocation -> {
				startCount.incrementAndGet();

				Thread.sleep(500);

				dataJSONObjectAtomicReference.set(new JSONObject());

				return null;
			}
		).when(
			baseBundlePersistentResource
		).start();

		CountDownLatch countDownLatch = new CountDownLatch(1);
		AtomicReference<Throwable> throwableAtomicReference =
			new AtomicReference<>();

		Thread[] threads = new Thread[2];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(
				() -> {
					try {
						countDownLatch.await();

						baseBundlePersistentResource.update();
					}
					catch (Throwable throwable) {
						throwableAtomicReference.set(throwable);
					}
				});

			threads[i].start();
		}

		countDownLatch.countDown();

		for (Thread thread : threads) {
			thread.join();
		}

		Assert.assertNull(throwableAtomicReference.get());

		Assert.assertEquals(1, startCount.get());
	}

	@Test
	public void testUpdateCancelledQueueItem() throws Exception {
		JenkinsMaster jenkinsMaster = _getJenkinsMaster();
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
		).fetchQueueItem(
			queueId
		);

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource
		).start();

		for (int i = 0; i < 9; i++) {
			baseBundlePersistentResource.update();
		}

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(10)
		).start();

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).setStatus(
			PersistentResource.Status.FAILED
		);

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(10)
		).start();

		Mockito.verify(
			baseBundlePersistentResource
		).setStatus(
			PersistentResource.Status.FAILED
		);

		Mockito.verify(
			baseBundlePersistentResource
		).save();
	}

	@Test
	public void testUpdateControllerDiscardedProducerBuild() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(_getJenkinsMaster(), 0);

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_producerBuildURL", _getBuildURL());
		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_status",
			PersistentResource.Status.IN_PROGRESS);

		RuntimeException runtimeException1 = new RuntimeException(
			RandomTestUtil.randomString());

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				Mockito.mockStatic(
					JenkinsAPIUtil.class,
					invocation -> {
						throw runtimeException1;
					})) {

			baseBundlePersistentResource.update();

			Assert.fail();
		}
		catch (RuntimeException runtimeException2) {
			Assert.assertSame(runtimeException1, runtimeException2);
		}

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).isBuildDiscarded(
			Mockito.any()
		);
	}

	@Test
	public void testUpdateControllerHandover() {
		String currentTopLevelBuildURL =
			"https://" + RandomTestUtil.randomString() + "/job/" +
				RandomTestUtil.randomString() + "/1/";

		_testUpdateControllerHandover(
			RandomTestUtil.randomString(), currentTopLevelBuildURL, false,
			PersistentResource.Status.IN_PROGRESS);
		_testUpdateControllerHandover(
			RandomTestUtil.randomString(), currentTopLevelBuildURL, false,
			PersistentResource.Status.NOT_STARTED);
		_testUpdateControllerHandover(
			RandomTestUtil.randomString(), currentTopLevelBuildURL, true,
			PersistentResource.Status.IN_QUEUE);
		_testUpdateControllerHandover(
			currentTopLevelBuildURL, currentTopLevelBuildURL, false,
			PersistentResource.Status.IN_QUEUE);
		_testUpdateControllerHandover(
			null, currentTopLevelBuildURL, false,
			PersistentResource.Status.IN_QUEUE);
	}

	@Test
	public void testUpdateFailedInvocation() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(_getJenkinsMaster(), 0);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).start();

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_status",
			PersistentResource.Status.NOT_STARTED);

		AtomicInteger invocationCount = new AtomicInteger();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.singletonMap(
							"invokeJenkinsBuild",
							invocation -> {
								invocationCount.incrementAndGet();

								return 0L;
							}))) {

			_update(baseBundlePersistentResource, 5);

			Assert.assertEquals(5, invocationCount.get());
			Assert.assertEquals(
				PersistentResource.Status.NOT_STARTED,
				baseBundlePersistentResource.getStatus());

			baseBundlePersistentResource.update();

			Assert.assertEquals(5, invocationCount.get());
			Assert.assertEquals(
				PersistentResource.Status.FAILED,
				baseBundlePersistentResource.getStatus());

			Mockito.verify(
				baseBundlePersistentResource
			).print(
				"No invocation attempts remaining"
			);
		}
	}

	@Test
	public void testUpdateFailedInvocationReset() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(_getJenkinsMaster(), 0);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).start();

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_status",
			PersistentResource.Status.NOT_STARTED);

		AtomicLong queueIdAtomicLong = new AtomicLong();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.singletonMap(
							"invokeJenkinsBuild",
							invocation -> queueIdAtomicLong.get()))) {

			_update(baseBundlePersistentResource, 4);

			queueIdAtomicLong.set(_getQueueId());

			baseBundlePersistentResource.update();

			Assert.assertEquals(
				PersistentResource.Status.IN_QUEUE,
				baseBundlePersistentResource.getStatus());

			queueIdAtomicLong.set(0);

			baseBundlePersistentResource.start();

			_update(baseBundlePersistentResource, 5);

			Assert.assertEquals(
				PersistentResource.Status.NOT_STARTED,
				baseBundlePersistentResource.getStatus());
		}
	}

	@Test
	public void testUpdateFollowerControllerBuildLookupFailure() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(
				_getDataJSONObject(0, PersistentResource.Status.IN_PROGRESS));

		RuntimeException runtimeException1 = new RuntimeException(
			RandomTestUtil.randomString());

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				Mockito.mockStatic(
					JenkinsAPIUtil.class,
					invocation -> {
						throw runtimeException1;
					})) {

			baseBundlePersistentResource.update();

			Assert.fail();
		}
		catch (RuntimeException runtimeException2) {
			Assert.assertSame(runtimeException1, runtimeException2);
		}

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).save();
	}

	@Test
	public void testUpdateFollowerControllerFinished() {
		for (PersistentResource.Status status :
				new PersistentResource.Status[] {
					PersistentResource.Status.IN_PROGRESS,
					PersistentResource.Status.IN_QUEUE,
					PersistentResource.Status.NOT_STARTED
				}) {

			_testUpdateFollowerControllerFinished(false, 2, status);
			_testUpdateFollowerControllerFinished(true, 0, status);
			_testUpdateFollowerControllerFinished(true, 1, status);
		}
	}

	@Test
	public void testUpdateFollowerControllerRunning() {
		_testUpdateFollowerControllerRunning(
			PersistentResource.Status.IN_PROGRESS);
		_testUpdateFollowerControllerRunning(
			PersistentResource.Status.IN_QUEUE);
		_testUpdateFollowerControllerRunning(
			PersistentResource.Status.NOT_STARTED);
	}

	@Test
	public void testUpdateFollowerDiscardedControllerBuild() {
		_testUpdateFollowerDiscardedControllerBuild(
			PersistentResource.Status.IN_PROGRESS);
		_testUpdateFollowerDiscardedControllerBuild(
			PersistentResource.Status.IN_QUEUE);
		_testUpdateFollowerDiscardedControllerBuild(
			PersistentResource.Status.NOT_STARTED);
	}

	@Test
	public void testUpdateFollowerDiscardedProducerBuild() {
		JSONObject dataJSONObject = _getDataJSONObject(
			0, PersistentResource.Status.SUCCESS);

		String producerBuildURL = _getBuildURL();

		dataJSONObject.put("producer_build_url", producerBuildURL);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(dataJSONObject);

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isArtifactsAvailable();

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isBuildDiscarded(
			producerBuildURL
		);

		TopLevelBuild topLevelBuild = Mockito.mock(TopLevelBuild.class);

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_topLevelBuild", topLevelBuild);

		RuntimeException runtimeException1 = new RuntimeException(
			RandomTestUtil.randomString());

		try (MockedStatic<BuildFactory> buildFactoryMockedStatic =
				Mockito.mockStatic(
					BuildFactory.class,
					invocation -> {
						throw runtimeException1;
					})) {

			_update(baseBundlePersistentResource, 2);

			Assert.assertEquals(
				PersistentResource.Status.SUCCESS,
				baseBundlePersistentResource.getStatus());

			Mockito.verify(
				topLevelBuild, Mockito.never()
			).addDownstreamBuild(
				Mockito.any()
			);

			Mockito.verify(
				baseBundlePersistentResource
			).isBuildDiscarded(
				producerBuildURL
			);

			Mockito.verify(
				baseBundlePersistentResource
			).print(
				"WARNING: Producer build " + producerBuildURL + " was discarded"
			);

			Mockito.verify(
				baseBundlePersistentResource, Mockito.never()
			).save();

			Mockito.verify(
				baseBundlePersistentResource, Mockito.times(2)
			).touch();

			String redispatchedProducerBuildURL = _getBuildURL();

			dataJSONObject.put(
				"producer_build_url", redispatchedProducerBuildURL);

			try {
				baseBundlePersistentResource.update();

				Assert.fail();
			}
			catch (RuntimeException runtimeException2) {
				Assert.assertSame(runtimeException1, runtimeException2);
			}

			buildFactoryMockedStatic.verify(
				() -> BuildFactory.newBuild(
					redispatchedProducerBuildURL, topLevelBuild));
		}
	}

	@Test
	public void testUpdateFollowerDiscardedProducerBuildControllerRunning() {
		JSONObject dataJSONObject = _getDataJSONObject(
			0, PersistentResource.Status.IN_PROGRESS);

		String producerBuildURL = _getBuildURL();

		dataJSONObject.put("producer_build_url", producerBuildURL);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(dataJSONObject);

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isBuildDiscarded(
			producerBuildURL
		);

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_topLevelBuild",
			Mockito.mock(TopLevelBuild.class));

		try (MockedStatic<BuildFactory> buildFactoryMockedStatic =
				Mockito.mockStatic(BuildFactory.class);
			MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				_mockJenkinsAPIUtil("")) {

			baseBundlePersistentResource.update();

			buildFactoryMockedStatic.verifyNoInteractions();
		}

		Assert.assertEquals(
			PersistentResource.Status.IN_PROGRESS,
			baseBundlePersistentResource.getStatus());

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).save();
	}

	@Test
	public void testUpdateFollowerFailed() {
		_testUpdateFollowerFailed(false, 2);
		_testUpdateFollowerFailed(true, 0);
		_testUpdateFollowerFailed(true, 1);
	}

	@Test
	public void testUpdateFollowerMissingArtifacts() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(
				_getDataJSONObject(0, PersistentResource.Status.SUCCESS));

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				_mockJenkinsAPIUtil("FAILURE");
			MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.<String, Answer<Object>>emptyMap())) {

			baseBundlePersistentResource.update();

			Assert.assertEquals(
				PersistentResource.Status.IN_PROGRESS,
				baseBundlePersistentResource.getStatus());

			Mockito.doReturn(
				true
			).when(
				baseBundlePersistentResource
			).isArtifactsAvailable();

			baseBundlePersistentResource.update();

			Mockito.doReturn(
				false
			).when(
				baseBundlePersistentResource
			).isArtifactsAvailable();

			baseBundlePersistentResource.update();

			Mockito.verify(
				baseBundlePersistentResource, Mockito.never()
			).save();

			baseBundlePersistentResource.update();

			Mockito.verify(
				baseBundlePersistentResource
			).save();
		}
	}

	@Test
	public void testUpdateFollowerMissingArtifactsControllerRunning() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(
				_getDataJSONObject(0, PersistentResource.Status.SUCCESS));

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				_mockJenkinsAPIUtil("")) {

			_update(baseBundlePersistentResource, 3);
		}

		Assert.assertEquals(
			PersistentResource.Status.SUCCESS,
			baseBundlePersistentResource.getStatus());

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).save();

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(3)
		).touch();
	}

	@Test
	public void testUpdateFollowerMissingArtifactsDiscardedControllerBuild() {
		JSONObject dataJSONObject = _getDataJSONObject(
			0, PersistentResource.Status.SUCCESS);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(dataJSONObject);

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isBuildDiscarded(
			dataJSONObject.getString("controller_build_url")
		);

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				Mockito.mockStatic(
					JenkinsAPIUtil.class,
					invocation -> {
						throw new RuntimeException(
							RandomTestUtil.randomString());
					});
			MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.<String, Answer<Object>>emptyMap())) {

			baseBundlePersistentResource.update();

			Assert.assertEquals(
				PersistentResource.Status.IN_PROGRESS,
				baseBundlePersistentResource.getStatus());

			Mockito.verify(
				baseBundlePersistentResource, Mockito.never()
			).save();

			baseBundlePersistentResource.update();

			Mockito.verify(
				baseBundlePersistentResource
			).save();
		}
	}

	@Test
	public void testUpdateFollowerMissingArtifactsFailed() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(
				_getDataJSONObject(2, PersistentResource.Status.SUCCESS));

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				_mockJenkinsAPIUtil("FAILURE")) {

			_update(baseBundlePersistentResource, 2);
		}

		Assert.assertEquals(
			PersistentResource.Status.FAILED,
			baseBundlePersistentResource.getStatus());

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).save();
	}

	@Test
	public void testUpdateFollowerProducerBuild() {
		JSONObject dataJSONObject = _getDataJSONObject(
			0, PersistentResource.Status.SUCCESS);

		String producerBuildURL = _getBuildURL();

		dataJSONObject.put("producer_build_url", producerBuildURL);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(dataJSONObject);

		TopLevelBuild topLevelBuild = Mockito.mock(TopLevelBuild.class);

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_topLevelBuild", topLevelBuild);

		RuntimeException runtimeException1 = new RuntimeException(
			RandomTestUtil.randomString());

		try (MockedStatic<BuildFactory> buildFactoryMockedStatic =
				Mockito.mockStatic(
					BuildFactory.class,
					invocation -> {
						throw runtimeException1;
					})) {

			try {
				baseBundlePersistentResource.update();

				Assert.fail();
			}
			catch (RuntimeException runtimeException2) {
				Assert.assertSame(runtimeException1, runtimeException2);
			}

			buildFactoryMockedStatic.verify(
				() -> BuildFactory.newBuild(producerBuildURL, topLevelBuild));
		}

		Mockito.verify(
			baseBundlePersistentResource
		).isBuildDiscarded(
			producerBuildURL
		);
	}

	@Test
	public void testUpdateFollowerSuccess() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(
				_getDataJSONObject(0, PersistentResource.Status.SUCCESS));

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isArtifactsAvailable();

		baseBundlePersistentResource.update();

		Assert.assertEquals(
			PersistentResource.Status.SUCCESS,
			baseBundlePersistentResource.getStatus());

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).save();

		Mockito.verify(
			baseBundlePersistentResource
		).touch();
	}

	@Test
	public void testUpdateFoundBuild() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(
				_getJenkinsMaster(), _getQueueId());

		String buildURL =
			"https://" + RandomTestUtil.randomString() + "/job/" +
				RandomTestUtil.randomString() + "/1/";

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.singletonMap(
							"fetchBuildURL", invocation -> buildURL))) {

			baseBundlePersistentResource.update();
		}

		Assert.assertEquals(
			PersistentResource.Status.IN_PROGRESS,
			baseBundlePersistentResource.getStatus());
		Assert.assertEquals(
			buildURL, baseBundlePersistentResource.getProducerBuildURL());

		Mockito.verify(
			baseBundlePersistentResource
		).save();
	}

	@Test
	public void testUpdateInQueue() throws Exception {
		JenkinsMaster jenkinsMaster = _getJenkinsMaster();
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
	public void testUpdateLookupFailure() throws Exception {
		JenkinsMaster jenkinsMaster = _getJenkinsMaster();
		long queueId = _getQueueId();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, queueId);

		AtomicReference<IOException> ioExceptionAtomicReference =
			new AtomicReference<>();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.singletonMap(
							"fetchBuildURL",
							invocation -> {
								IOException ioException =
									ioExceptionAtomicReference.get();

								if (ioException != null) {
									throw ioException;
								}

								return null;
							}))) {

			Mockito.doThrow(
				new RuntimeException(RandomTestUtil.randomString())
			).when(
				jenkinsMaster
			).getQueueItems();

			_update(baseBundlePersistentResource, 3);

			Mockito.doReturn(
				Collections.emptyList()
			).when(
				jenkinsMaster
			).getQueueItems();

			Mockito.doThrow(
				new IOException(RandomTestUtil.randomString())
			).when(
				jenkinsMaster
			).fetchQueueItem(
				queueId
			);

			_update(baseBundlePersistentResource, 3);

			Mockito.doReturn(
				null
			).when(
				jenkinsMaster
			).fetchQueueItem(
				queueId
			);

			ioExceptionAtomicReference.set(
				new IOException(RandomTestUtil.randomString()));

			_update(baseBundlePersistentResource, 3);

			Assert.assertEquals(
				PersistentResource.Status.IN_QUEUE,
				baseBundlePersistentResource.getStatus());

			Mockito.verify(
				baseBundlePersistentResource, Mockito.never()
			).print(
				"WARNING: Unable to find queue item"
			);

			Mockito.verify(
				baseBundlePersistentResource
			).print(
				Mockito.contains("(9 of 10)")
			);

			baseBundlePersistentResource.update();

			Assert.assertEquals(
				PersistentResource.Status.FAILED,
				baseBundlePersistentResource.getStatus());

			Mockito.verify(
				baseBundlePersistentResource
			).print(
				"No lookup attempts remaining"
			);

			Mockito.verify(
				baseBundlePersistentResource
			).save();
		}
	}

	@Test
	public void testUpdateLookupFailureReset() throws Exception {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(
				_getJenkinsMaster(), _getQueueId());

		AtomicReference<IOException> ioExceptionAtomicReference =
			new AtomicReference<>();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.singletonMap(
							"fetchBuildURL",
							invocation -> {
								IOException ioException =
									ioExceptionAtomicReference.get();

								if (ioException != null) {
									throw ioException;
								}

								return null;
							}))) {

			ioExceptionAtomicReference.set(
				new IOException(RandomTestUtil.randomString()));

			_update(baseBundlePersistentResource, 9);

			ioExceptionAtomicReference.set(null);

			baseBundlePersistentResource.update();

			ioExceptionAtomicReference.set(
				new IOException(RandomTestUtil.randomString()));

			_update(baseBundlePersistentResource, 9);

			Assert.assertEquals(
				PersistentResource.Status.IN_QUEUE,
				baseBundlePersistentResource.getStatus());

			Mockito.verify(
				baseBundlePersistentResource, Mockito.never()
			).save();
		}
	}

	@Test
	public void testUpdateMissingQueueItem() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(_getJenkinsMaster(), -1);

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).start();

		_update(baseBundlePersistentResource, 4);

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(2)
		).start();

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).setStatus(
			PersistentResource.Status.FAILED
		);

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(2)
		).start();

		Mockito.verify(
			baseBundlePersistentResource
		).setStatus(
			PersistentResource.Status.FAILED
		);

		Mockito.verify(
			baseBundlePersistentResource
		).save();
	}

	@Test
	public void testUpdateMissingQueueItemReset() throws Exception {
		JenkinsMaster jenkinsMaster = _getJenkinsMaster();
		long queueId = _getQueueId();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, queueId);

		JenkinsMaster.QueueItem queueItem = Mockito.mock(
			JenkinsMaster.QueueItem.class);

		Mockito.doReturn(
			queueId
		).when(
			queueItem
		).getId();

		Mockito.doReturn(
			System.currentTimeMillis()
		).when(
			queueItem
		).getInQueueSince();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.<String, Answer<Object>>emptyMap())) {

			baseBundlePersistentResource.update();

			Mockito.doReturn(
				Collections.singletonList(queueItem)
			).when(
				jenkinsMaster
			).getQueueItems();

			baseBundlePersistentResource.update();

			Mockito.doReturn(
				Collections.emptyList()
			).when(
				jenkinsMaster
			).getQueueItems();

			baseBundlePersistentResource.update();

			Mockito.doReturn(
				queueItem
			).when(
				jenkinsMaster
			).fetchQueueItem(
				queueId
			);

			baseBundlePersistentResource.update();

			Mockito.doReturn(
				null
			).when(
				jenkinsMaster
			).fetchQueueItem(
				queueId
			);

			baseBundlePersistentResource.update();

			Mockito.verify(
				baseBundlePersistentResource, Mockito.never()
			).start();

			baseBundlePersistentResource.update();

			Mockito.verify(
				baseBundlePersistentResource
			).start();
		}
	}

	@Test
	public void testUpdateRedispatch() {
		_testUpdateRedispatch(
			"Redispatching bundles (2 of 2)",
			PersistentResource.Status.IN_QUEUE, _getQueueId());
		_testUpdateRedispatch(
			"WARNING: Unable to redispatch bundles (2 of 2)",
			PersistentResource.Status.NOT_STARTED, 0);
	}

	@Test
	public void testUpdateStartedQueueItem() throws Exception {
		JenkinsMaster jenkinsMaster = _getJenkinsMaster();
		long queueId = RandomTestUtil.randomLong();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, queueId);

		JenkinsMaster.QueueItem queueItem = Mockito.mock(
			JenkinsMaster.QueueItem.class);

		String executableURL = "https://" + RandomTestUtil.randomString();

		Mockito.doReturn(
			executableURL
		).when(
			queueItem
		).getExecutableURL();

		Mockito.doReturn(
			queueItem
		).when(
			jenkinsMaster
		).fetchQueueItem(
			queueId
		);

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource
		).save();

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

	@Test
	public void testUpdateSubclasses() {
		_testUpdateSubclass(AsahBundlePersistentResource.class);
		_testUpdateSubclass(FaroBundlePersistentResource.class);
		_testUpdateSubclass(PortalBundlePersistentResource.class);
		_testUpdateSubclass(WorkspaceBundlePersistentResource.class);
	}

	@Test
	public void testUpdateTransientFailure() {
		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(
				_getJenkinsMaster(), _getQueueId());

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_status",
			PersistentResource.Status.NOT_STARTED);

		_update(baseBundlePersistentResource, 2);

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(2)
		).start();

		Assert.assertEquals(
			PersistentResource.Status.NOT_STARTED,
			baseBundlePersistentResource.getStatus());

		baseBundlePersistentResource.update();

		Mockito.verify(
			baseBundlePersistentResource, Mockito.times(2)
		).start();

		Assert.assertEquals(
			PersistentResource.Status.FAILED,
			baseBundlePersistentResource.getStatus());

		Mockito.verify(
			baseBundlePersistentResource
		).save();
	}

	@Test
	public void testUpdateWaitingQueueItem() throws Exception {
		JenkinsMaster jenkinsMaster = _getJenkinsMaster();
		long queueId = _getQueueId();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, queueId);

		JenkinsMaster.QueueItem queueItem = Mockito.mock(
			JenkinsMaster.QueueItem.class);

		String why = RandomTestUtil.randomString();

		Mockito.doReturn(
			why
		).when(
			queueItem
		).getWhy();

		Mockito.doReturn(
			queueItem
		).when(
			jenkinsMaster
		).fetchQueueItem(
			queueId
		);

		_update(baseBundlePersistentResource, 3);

		Assert.assertEquals(
			PersistentResource.Status.IN_QUEUE,
			baseBundlePersistentResource.getStatus());

		String statusMessage = baseBundlePersistentResource.getStatusMessage();

		Assert.assertTrue(statusMessage, statusMessage.endsWith(": " + why));

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).print(
			"WARNING: Unable to find queue item"
		);

		Mockito.verify(
			baseBundlePersistentResource, Mockito.never()
		).start();
	}

	private BaseBundlePersistentResource _getBaseBundlePersistentResource(
		Class<? extends BaseBundlePersistentResource> clazz,
		JenkinsMaster jenkinsMaster, long queueId) {

		BaseBundlePersistentResource baseBundlePersistentResource =
			Mockito.mock(clazz);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).getControllerBuildURL();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).getProducerBuildURL();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).getProducerJenkinsMaster();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).getProducerQueueId();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).getStatus();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).getStatusMessage();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).isController();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).setControllerBuildURL(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).setProducerBuildURL(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).setProducerJenkinsMaster(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).setProducerQueueId(
			Mockito.anyLong()
		);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).setStatus(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).update();

		Mockito.doReturn(
			Mockito.mock(BuildDatabase.class)
		).when(
			baseBundlePersistentResource
		).getBuildDatabase();

		String currentTopLevelBuildURL =
			"https://" + RandomTestUtil.randomString() + "/job/" +
				RandomTestUtil.randomString() + "/1/";

		Mockito.doReturn(
			currentTopLevelBuildURL
		).when(
			baseBundlePersistentResource
		).getCurrentTopLevelBuildURL();

		Mockito.doReturn(
			new JSONObject()
		).when(
			baseBundlePersistentResource
		).getDataJSONObject();

		Mockito.doReturn(
			new Properties()
		).when(
			baseBundlePersistentResource
		).getStartProperties();

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_controllerBuildURL",
			currentTopLevelBuildURL);
		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_producerJenkinsMaster",
			jenkinsMaster);
		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_producerQueueId", queueId);
		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_status",
			PersistentResource.Status.IN_QUEUE);

		return baseBundlePersistentResource;
	}

	private BaseBundlePersistentResource _getBaseBundlePersistentResource(
		JenkinsMaster jenkinsMaster, long queueId) {

		return _getBaseBundlePersistentResource(
			BaseBundlePersistentResource.class, jenkinsMaster, queueId);
	}

	private String _getBuildURL() {
		return JenkinsResultsParserUtil.combine(
			"https://", RandomTestUtil.randomString(), "/job/",
			RandomTestUtil.randomString(), "/1");
	}

	private JSONObject _getDataJSONObject(
		int redispatchAttempts, PersistentResource.Status status) {

		return new JSONObject(
		).put(
			"controller_build_url",
			"https://" + RandomTestUtil.randomString() + "/job/" +
				RandomTestUtil.randomString() + "/1/"
		).put(
			"redispatch_attempts", redispatchAttempts
		).put(
			"status", String.valueOf(status)
		);
	}

	private BaseBundlePersistentResource
		_getFollowerBaseBundlePersistentResource(
			Class<? extends BaseBundlePersistentResource> clazz,
			JSONObject dataJSONObject) {

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(clazz, _getJenkinsMaster(), 0);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).isMissing();

		Mockito.doReturn(
			dataJSONObject
		).when(
			baseBundlePersistentResource
		).getDataJSONObject();

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_controllerBuildURL",
			dataJSONObject.getString("controller_build_url"));

		return baseBundlePersistentResource;
	}

	private BaseBundlePersistentResource
		_getFollowerBaseBundlePersistentResource(JSONObject dataJSONObject) {

		return _getFollowerBaseBundlePersistentResource(
			BaseBundlePersistentResource.class, dataJSONObject);
	}

	private JenkinsMaster _getJenkinsMaster() {
		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);

		JenkinsCohort jenkinsCohort = Mockito.mock(JenkinsCohort.class);

		Mockito.doReturn(
			jenkinsMaster
		).when(
			jenkinsCohort
		).getMostAvailableJenkinsMaster(
			Mockito.any(), Mockito.anyInt(), Mockito.anyString()
		);

		Mockito.doReturn(
			jenkinsCohort
		).when(
			jenkinsMaster
		).getJenkinsCohort();

		return jenkinsMaster;
	}

	private long _getQueueId() {
		return Math.abs(RandomTestUtil.randomLong() % 1000000) + 1;
	}

	private MockedStatic<JenkinsAPIUtil> _mockJenkinsAPIUtil(String result) {
		return Mockito.mockStatic(
			JenkinsAPIUtil.class,
			invocation -> new JSONObject(
			).put(
				"result", result
			));
	}

	private MockedStatic<JenkinsResultsParserUtil>
		_mockJenkinsResultsParserUtil(Map<String, Answer<Object>> answers) {

		return Mockito.mockStatic(
			JenkinsResultsParserUtil.class,
			invocation -> {
				Method method = invocation.getMethod();

				String methodName = method.getName();

				Answer<Object> answer = answers.get(methodName);

				if (answer != null) {
					return answer.answer(invocation);
				}

				if (methodName.equals("fetchBuildURL") ||
					methodName.equals("sleep")) {

					return null;
				}

				if (methodName.equals("getBuildParameters")) {
					return new HashMap<>();
				}

				return invocation.callRealMethod();
			});
	}

	private void _testStart(
		long expectedProducerQueueId, PersistentResource.Status expectedStatus,
		Answer<Object> invokeJenkinsBuildAnswer) {

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(_getJenkinsMaster(), 0);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).start();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.singletonMap(
							"invokeJenkinsBuild", invokeJenkinsBuildAnswer))) {

			baseBundlePersistentResource.start();
		}

		Assert.assertEquals(
			expectedProducerQueueId,
			baseBundlePersistentResource.getProducerQueueId());
		Assert.assertEquals(
			expectedStatus, baseBundlePersistentResource.getStatus());

		Mockito.verify(
			baseBundlePersistentResource,
			getVerificationMode(
				expectedStatus == PersistentResource.Status.IN_QUEUE)
		).print(
			Mockito.startsWith("Start building bundles at ")
		);

		Mockito.verify(
			baseBundlePersistentResource
		).save();
	}

	private void _testUpdateControllerHandover(
		String controllerBuildURL, String currentTopLevelBuildURL,
		boolean expectedCancelled, PersistentResource.Status status) {

		JenkinsMaster jenkinsMaster = _getJenkinsMaster();
		long queueId = _getQueueId();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getBaseBundlePersistentResource(jenkinsMaster, queueId);

		Mockito.doReturn(
			currentTopLevelBuildURL
		).when(
			baseBundlePersistentResource
		).getCurrentTopLevelBuildURL();

		Mockito.doReturn(
			new JSONObject(
			).put(
				"controller_build_url", controllerBuildURL
			)
		).when(
			baseBundlePersistentResource
		).getDataJSONObject();

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_controllerBuildURL",
			currentTopLevelBuildURL);
		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_status", status);

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.<String, Answer<Object>>emptyMap());
			MockedStatic<JenkinsStopBuildUtil>
				jenkinsStopBuildUtilMockedStatic = Mockito.mockStatic(
					JenkinsStopBuildUtil.class)) {

			baseBundlePersistentResource.update();

			jenkinsStopBuildUtilMockedStatic.verify(
				() -> JenkinsStopBuildUtil.cancelQueueItem(
					jenkinsMaster, queueId),
				getVerificationMode(expectedCancelled));
		}

		String expectedControllerBuildURL = controllerBuildURL;

		if (JenkinsResultsParserUtil.isNullOrEmpty(controllerBuildURL)) {
			expectedControllerBuildURL = currentTopLevelBuildURL;
		}

		Assert.assertEquals(
			expectedControllerBuildURL,
			baseBundlePersistentResource.getControllerBuildURL());
		Assert.assertEquals(
			expectedControllerBuildURL.equals(currentTopLevelBuildURL),
			baseBundlePersistentResource.isController());
	}

	private void _testUpdateFollowerControllerFinished(
		boolean expectedRedispatched, int redispatchAttempts,
		PersistentResource.Status status) {

		AtomicReference<JSONObject> savedDataJSONObjectAtomicReference =
			new AtomicReference<>();

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(
				_getDataJSONObject(redispatchAttempts, status));

		Mockito.doAnswer(
			invocation -> {
				JSONObject savedDataJSONObject = new JSONObject();

				baseBundlePersistentResource.populateDataJSONObject(
					savedDataJSONObject);

				savedDataJSONObjectAtomicReference.set(savedDataJSONObject);

				return null;
			}
		).when(
			baseBundlePersistentResource
		).save();

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).populateDataJSONObject(
			Mockito.any()
		);

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				_mockJenkinsAPIUtil("FAILURE");
			MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.<String, Answer<Object>>emptyMap())) {

			baseBundlePersistentResource.update();
		}

		Mockito.verify(
			baseBundlePersistentResource,
			getVerificationMode(expectedRedispatched)
		).save();

		if (expectedRedispatched) {
			JSONObject savedDataJSONObject =
				savedDataJSONObjectAtomicReference.get();

			Assert.assertEquals(
				redispatchAttempts + 1,
				savedDataJSONObject.getInt("redispatch_attempts"));

			return;
		}

		Assert.assertEquals(
			PersistentResource.Status.FAILED,
			baseBundlePersistentResource.getStatus());
	}

	private void _testUpdateFollowerControllerRunning(
		PersistentResource.Status status) {

		BaseBundlePersistentResource followerBaseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(
				_getDataJSONObject(0, status));

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				_mockJenkinsAPIUtil("")) {

			followerBaseBundlePersistentResource.update();
		}

		Assert.assertEquals(
			status, followerBaseBundlePersistentResource.getStatus());

		Mockito.verify(
			followerBaseBundlePersistentResource, Mockito.never()
		).isBuildDiscarded(
			Mockito.any()
		);

		Mockito.verify(
			followerBaseBundlePersistentResource, Mockito.never()
		).save();
	}

	private void _testUpdateFollowerDiscardedControllerBuild(
		PersistentResource.Status status) {

		JSONObject dataJSONObject = _getDataJSONObject(0, status);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(dataJSONObject);

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isBuildDiscarded(
			dataJSONObject.getString("controller_build_url")
		);

		try (MockedStatic<JenkinsAPIUtil> jenkinsAPIUtilMockedStatic =
				Mockito.mockStatic(
					JenkinsAPIUtil.class,
					invocation -> {
						throw new RuntimeException(
							RandomTestUtil.randomString());
					});
			MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.<String, Answer<Object>>emptyMap())) {

			baseBundlePersistentResource.update();
		}

		Mockito.verify(
			baseBundlePersistentResource
		).save();
	}

	private void _testUpdateFollowerFailed(
		boolean expectedRedispatched, int redispatchAttempts) {

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(
				_getDataJSONObject(
					redispatchAttempts, PersistentResource.Status.FAILED));

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.<String, Answer<Object>>emptyMap())) {

			baseBundlePersistentResource.update();
		}

		Mockito.verify(
			baseBundlePersistentResource,
			getVerificationMode(!expectedRedispatched)
		).print(
			"No redispatch attempts remaining"
		);

		Mockito.verify(
			baseBundlePersistentResource,
			getVerificationMode(expectedRedispatched)
		).save();
	}

	private void _testUpdateRedispatch(
		String expectedMessage, PersistentResource.Status expectedStatus,
		long queueId) {

		JSONArray redispatchHistoryJSONArray = new JSONArray();

		for (int i = 0; i < 2; i++) {
			redispatchHistoryJSONArray.put(
				new JSONObject(
				).put(
					"controller_build_url", RandomTestUtil.randomString()
				));
		}

		JSONObject dataJSONObject = _getDataJSONObject(
			1, PersistentResource.Status.FAILED);

		dataJSONObject.put("redispatch_history", redispatchHistoryJSONArray);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(dataJSONObject);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).populateDataJSONObject(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			baseBundlePersistentResource
		).start();

		Mockito.doReturn(
			dataJSONObject,
			new JSONObject(
			).put(
				"controller_build_url",
				baseBundlePersistentResource.getCurrentTopLevelBuildURL()
			)
		).when(
			baseBundlePersistentResource
		).getDataJSONObject();

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"github.webhook.base.invocation.url", "http://test-1.liferay.com");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		JenkinsMaster jenkinsMaster = _getJenkinsMaster();

		JenkinsCohort jenkinsCohort = jenkinsMaster.getJenkinsCohort();

		try (MockedStatic<JenkinsCohort> jenkinsCohortMockedStatic =
				Mockito.mockStatic(
					JenkinsCohort.class, invocation -> jenkinsCohort);
			MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic =
					_mockJenkinsResultsParserUtil(
						Collections.singletonMap(
							"invokeJenkinsBuild", invocation -> queueId))) {

			baseBundlePersistentResource.update();
		}

		Assert.assertEquals(
			expectedStatus, baseBundlePersistentResource.getStatus());

		Mockito.verify(
			baseBundlePersistentResource
		).print(
			Mockito.startsWith(expectedMessage)
		);

		JSONObject populatedDataJSONObject = new JSONObject();

		baseBundlePersistentResource.populateDataJSONObject(
			populatedDataJSONObject);

		Assert.assertEquals(
			2, populatedDataJSONObject.getInt("redispatch_attempts"));

		JSONArray populatedRedispatchHistoryJSONArray =
			populatedDataJSONObject.getJSONArray("redispatch_history");

		Assert.assertEquals(2, populatedRedispatchHistoryJSONArray.length());

		JSONObject redispatchHistoryJSONObject =
			populatedRedispatchHistoryJSONArray.getJSONObject(1);

		Assert.assertEquals(
			dataJSONObject.getString("controller_build_url"),
			redispatchHistoryJSONObject.getString("controller_build_url"));
	}

	private void _testUpdateSubclass(
		Class<? extends BaseBundlePersistentResource> clazz) {

		JSONObject dataJSONObject = _getDataJSONObject(
			0, PersistentResource.Status.SUCCESS);

		String producerBuildURL = _getBuildURL();

		dataJSONObject.put("producer_build_url", producerBuildURL);

		BaseBundlePersistentResource baseBundlePersistentResource =
			_getFollowerBaseBundlePersistentResource(clazz, dataJSONObject);

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isArtifactsAvailable();

		Mockito.doReturn(
			true
		).when(
			baseBundlePersistentResource
		).isBuildDiscarded(
			producerBuildURL
		);

		ReflectionTestUtil.setFieldValue(
			baseBundlePersistentResource, "_topLevelBuild",
			Mockito.mock(TopLevelBuild.class));

		try (MockedStatic<BuildFactory> buildFactoryMockedStatic =
				Mockito.mockStatic(BuildFactory.class)) {

			baseBundlePersistentResource.update();

			buildFactoryMockedStatic.verifyNoInteractions();
		}

		Assert.assertEquals(
			PersistentResource.Status.SUCCESS,
			baseBundlePersistentResource.getStatus());
	}

	private void _update(
		BaseBundlePersistentResource baseBundlePersistentResource, int count) {

		for (int i = 0; i < count; i++) {
			baseBundlePersistentResource.update();
		}
	}

}