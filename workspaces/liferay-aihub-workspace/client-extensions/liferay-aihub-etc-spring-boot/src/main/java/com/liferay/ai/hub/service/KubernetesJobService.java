/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.service;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.PrettyLoggable;
import io.fabric8.kubernetes.client.dsl.ScalableResource;
import io.fabric8.kubernetes.client.utils.Serialization;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author José Abelenda
 */
@Service
public class KubernetesJobService {

	public Job createJob(long accountEntryId, String indexName, String url) {
		URI uri = null;

		try {
			uri = URI.create(url);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new IllegalArgumentException(
				"Invalid crawler URL \"" + url + "\"",
				illegalArgumentException);
		}

		Job job = _kubernetesClient.batch(
		).v1(
		).jobs(
		).inNamespace(
			_namespace
		).resource(
			new JobBuilder(
				_job
			).editMetadata(
			).addToLabels(
				"account-entry-id", String.valueOf(accountEntryId)
			).endMetadata(
			).editSpec(
			).editTemplate(
			).editMetadata(
			).addToLabels(
				"account-entry-id", String.valueOf(accountEntryId)
			).endMetadata(
			).editSpec(
			).editFirstContainer(
			).withImage(
				_imageName
			).withEnv(
				_createEnvVar(
					"ACCOUNT_ENTRY_ID", String.valueOf(accountEntryId)),
				_createEnvVar(
					"CRAWLER_DOMAIN_URL",
					uri.getScheme() + "://" + uri.getAuthority()),
				_createEnvVar("CRAWLER_LOG_LEVEL", _crawlerLogLevel),
				_createEnvVar("CRAWLER_OUTPUT_INDEX", indexName),
				_createEnvVar("CRAWLER_SEED_URL", url),
				_createEnvVar("ELASTICSEARCH_HOST", _elasticsearchHost),
				_createEnvVar(
					"ELASTICSEARCH_PORT", String.valueOf(_elasticsearchPort))
			).endContainer(
			).endSpec(
			).endTemplate(
			).endSpec(
			).build()
		).create();

		if (_log.isInfoEnabled()) {
			ObjectMeta objectMeta = job.getMetadata();

			_log.info("Kubernetes job dispatched: " + objectMeta.getName());
		}

		return job;
	}

	public Job getJob(String name) {
		ScalableResource<Job> scalableResource = _getJobScalableResource(name);

		return scalableResource.get();
	}

	public String getJobLog(String name, int tailLines) {
		ScalableResource<Job> scalableResource = _getJobScalableResource(name);

		PrettyLoggable prettyLoggable = scalableResource.tailingLines(
			tailLines);

		return prettyLoggable.getLog();
	}

	@PostConstruct
	public void postConstruct() {
		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(
				"dependencies/crawler-job.yaml")) {

			_job = Serialization.unmarshal(inputStream, Job.class);
		}
		catch (IOException ioException) {
			throw new IllegalStateException(
				"Unable to load \"crawler-job.yaml\"", ioException);
		}
	}

	@PreDestroy
	public void preDestroy() {
		_kubernetesClient.close();
	}

	private EnvVar _createEnvVar(String name, String value) {
		return new EnvVarBuilder(
		).withName(
			name
		).withValue(
			value
		).build();
	}

	private ScalableResource<Job> _getJobScalableResource(String name) {
		return _kubernetesClient.batch(
		).v1(
		).jobs(
		).inNamespace(
			_namespace
		).withName(
			name
		);
	}

	private static final Log _log = LogFactory.getLog(
		KubernetesJobService.class);

	@Value("${liferay.ai.hub.crawler.log.level}")
	private String _crawlerLogLevel;

	@Value("${liferay.ai.hub.crawler.elasticsearch.host}")
	private String _elasticsearchHost;

	@Value("${liferay.ai.hub.crawler.elasticsearch.port}")
	private int _elasticsearchPort;

	@Value("${liferay.ai.hub.crawler.k8s.image.name}")
	private String _imageName;

	private Job _job;
	private final KubernetesClient _kubernetesClient =
		new KubernetesClientBuilder(
		).build();

	@Value("${liferay.ai.hub.crawler.k8s.namespace}")
	private String _namespace;

}