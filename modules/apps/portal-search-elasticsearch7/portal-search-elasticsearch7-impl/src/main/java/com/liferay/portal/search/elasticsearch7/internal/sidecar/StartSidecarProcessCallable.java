/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;

import java.io.IOException;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Tina Tian
 */
public class StartSidecarProcessCallable
	implements ProcessCallable<Serializable> {

	@Override
	public Serializable call() throws ProcessException {
		System.setProperty("es.distribution.type", "tar");
		System.setProperty("es.networkaddress.cache.negative.ttl", "10");
		System.setProperty("es.networkaddress.cache.ttl", "60");
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("io.netty.noKeySetOptimization", "true");
		System.setProperty("io.netty.noUnsafe", "true");
		System.setProperty("io.netty.recycler.maxCapacityPerThread", "0");
		System.setProperty("java.awt.headless", "true");
		System.setProperty("jna.nosys", "true");
		System.setProperty("log4j.shutdownHookEnabled", "false");
		System.setProperty("log4j2.disable.jmx", "true");
		System.setProperty("log4j2.formatMsgNoLookups", "true");
		System.setProperty(
			"org.apache.lucene.vectorization.upperJavaFeatureVersion", "21");
		System.setProperty("jdk.module.main", "org.elasticsearch.server");

		try {
			Path tempPath = Path.of(System.getProperty("java.io.tmpdir"));

			Files.createDirectories(tempPath);

			Path tempSidecarPath = Files.createTempDirectory(
				tempPath, "sidecar");

			Path configPath = tempSidecarPath.resolve("config");

			Files.createDirectories(configPath);

			System.setProperty(
				"es.path.conf", String.valueOf(configPath.toAbsolutePath()));

			Files.writeString(
				configPath.resolve("log4j2.properties"),
				System.getProperty("sidecar.log4j2.properties"));
		}
		catch (IOException ioException) {
			throw new ProcessException(
				"Unable to create log4j2.properties", ioException);
		}

		ElasticsearchServerUtil.start();

		return null;
	}

	private static final long serialVersionUID = 1L;

}