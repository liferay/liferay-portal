/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.ParallelExecutor;
import com.liferay.jenkins.results.parser.TopLevelBuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

/**
 * @author Michael Hashimoto
 */
public class PersistentResourceFactory {

	public static synchronized PersistentResource newPersistentResource(
		BuildDatabase buildDatabase, TopLevelBuild topLevelBuild,
		PersistentResource.Type type) {

		String key = buildDatabase.getBuildDatabaseFile() + "/" + type;

		if (_persistentResources.containsKey(key)) {
			return _persistentResources.get(key);
		}

		PersistentResource persistentResource = null;

		if (type == PersistentResource.Type.ASAH_BUNDLE) {
			persistentResource = new AsahBundlePersistentResource(
				buildDatabase, topLevelBuild);
		}
		else if (type == PersistentResource.Type.FARO_BUNDLE) {
			persistentResource = new FaroBundlePersistentResource(
				buildDatabase, topLevelBuild);
		}
		else if (type == PersistentResource.Type.PORTAL_BUNDLE) {
			persistentResource = new PortalBundlePersistentResource(
				buildDatabase, topLevelBuild);
		}

		if (persistentResource != null) {
			_persistentResources.put(key, persistentResource);
		}

		return persistentResource;
	}

	public static void touchUsedPersistentResources(
		ExecutorService executorService) {

		PersistentResource[] persistentResources;

		synchronized (PersistentResourceFactory.class) {
			persistentResources = _persistentResources.values(
			).toArray(
				new PersistentResource[_persistentResources.size()]
			);
		}

		List<Callable<Object>> touchCallables = new ArrayList<>();

		for (PersistentResource persistentResource : persistentResources) {
			if (persistentResource.getStatus() ==
					PersistentResource.Status.SUCCESS) {

				touchCallables.add(
					new Callable<Object>() {

						@Override
						public Object call() {
							persistentResource.touch();

							return null;
						}

					});
			}
		}

		if (touchCallables.isEmpty()) {
			return;
		}

		ParallelExecutor<Object> parallelExecutor = new ParallelExecutor<>(
			touchCallables, executorService, "touch persistent resources");

		try {
			parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			System.out.println(
				"WARNING: Failed to touch persistent resources: " +
					timeoutException.getMessage());
		}
	}

	private static final Map<String, PersistentResource> _persistentResources =
		new HashMap<>();

}