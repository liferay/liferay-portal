/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionDescriptor {

	public static final String FILE_NAME = "client-extension-descriptor.json";

	public static final String LABEL_VERSION =
		"com.liferay.client-extension.descriptor.version";

	public static final String TYPE_BATCH = "batch";

	public static final String TYPE_CONFIGURATION = "configuration";

	public static final String TYPE_SERVICE = "service";

	public static final int VERSION = 1;

	public static Map<String, Object> derive(
		Map<String, Object> configsMap, String imageDigest,
		String imageRepository, String imageTag,
		String oAuthApplicationHeadlessServerERC, String type) {

		Map<String, Object> clientExtensionMap = new LinkedHashMap<>();

		clientExtensionMap.put("configs", new TreeMap<>(configsMap));
		clientExtensionMap.put("descriptorVersion", VERSION);

		if (!type.equals(TYPE_CONFIGURATION)) {
			Map<String, Object> imageMap = new LinkedHashMap<>();

			imageMap.put("digest", imageDigest);
			imageMap.put("repository", imageRepository);
			imageMap.put("tag", imageTag);

			clientExtensionMap.put("image", imageMap);
		}

		if (type.equals(TYPE_BATCH) &&
			(oAuthApplicationHeadlessServerERC != null)) {

			clientExtensionMap.put(
				"oauthApplicationHeadlessServerERC",
				oAuthApplicationHeadlessServerERC);
		}

		clientExtensionMap.put("type", type);

		return Collections.singletonMap("descriptor", clientExtensionMap);
	}

	public static String getRepository(String image) {
		int index = image.indexOf('@');

		if (index >= 0) {
			image = image.substring(0, index);
		}

		index = image.lastIndexOf(':');

		if (index > image.lastIndexOf('/')) {
			image = image.substring(0, index);
		}

		return image;
	}

	/**
	 * Returns the descriptor type for a client extension project's
	 * classification grouping. The descriptor calls a microservice a service.
	 * A project that declares only configuration but has its own Dockerfile,
	 * such as a cron job, is a service.
	 */
	public static String getType(
		String classificationGrouping, boolean dockerfile) {

		if (classificationGrouping.equals("microservice") ||
			(classificationGrouping.equals(TYPE_CONFIGURATION) && dockerfile)) {

			return TYPE_SERVICE;
		}

		return classificationGrouping;
	}

}