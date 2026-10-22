/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal.test.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.server.mock.KubernetesMixedDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;

import java.net.InetAddress;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * @author Shuyang Zhou
 */
public class KubernetesMockServerTestUtil {

	public static void createOrReplaceConfigMap(String json) {
		MixedOperation<ConfigMap, ConfigMapList, Resource<ConfigMap>>
			mixedOperation = _kubernetesMockClient.configMaps();

		mixedOperation.createOrReplace(
			Serialization.unmarshal(json, ConfigMap.class));
	}

	public static String getHostName() {
		return _kubernetesMockServer.getHostName();
	}

	public static int getPort() {
		return _kubernetesMockServer.getPort();
	}

	public static void start() {
		Map<ServerRequest, Queue<ServerResponse>> serverResponses =
			new HashMap<>();

		_kubernetesMockServer = new KubernetesMockServer(
			new Context(), new MockWebServer(), serverResponses,
			new KubernetesMixedDispatcher(
				serverResponses, Collections.emptyList()) {

				@Override
				public MockResponse dispatch(RecordedRequest request)
					throws InterruptedException {

					try (SafeCloseable safeCloseable =
							ThreadContextClassLoaderUtil.swap(
								DefaultKubernetesClient.class.
									getClassLoader())) {

						return super.dispatch(request);
					}
					catch (Exception exception) {
						_log.error(exception);
					}

					return null;
				}

			},
			false);

		_kubernetesMockServer.init(InetAddress.getLoopbackAddress(), 0);

		_kubernetesMockClient = _kubernetesMockServer.createClient();
	}

	public static void stop() {
		_kubernetesMockClient.close();

		_kubernetesMockServer.destroy();
	}

	public static String waitForConfigMap(
		String name, Predicate<Map<String, String>> dataPredicate, long amount,
		TimeUnit timeUnit) {

		MixedOperation<ConfigMap, ConfigMapList, Resource<ConfigMap>>
			mixedOperation = _kubernetesMockClient.configMaps();

		Resource<ConfigMap> resource = mixedOperation.withName(name);

		ConfigMap configMap = resource.waitUntilCondition(
			curConfigMap -> {
				if (curConfigMap == null) {
					return dataPredicate.test(null);
				}

				return dataPredicate.test(curConfigMap.getData());
			},
			amount, timeUnit);

		if (configMap == null) {
			return null;
		}

		return Serialization.asJson(configMap);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KubernetesMockServerTestUtil.class);

	private static NamespacedKubernetesClient _kubernetesMockClient;
	private static KubernetesMockServer _kubernetesMockServer;

}