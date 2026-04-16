/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.TopLevelBuild;

import java.util.HashMap;
import java.util.Map;

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

	public static synchronized void touchUsedPersistentResources() {
		for (PersistentResource persistentResource :
				_persistentResources.values()) {

			if (persistentResource.getStatus() ==
					PersistentResource.Status.SUCCESS) {

				persistentResource.touch();
			}
		}
	}

	private static final Map<String, PersistentResource> _persistentResources =
		new HashMap<>();

}