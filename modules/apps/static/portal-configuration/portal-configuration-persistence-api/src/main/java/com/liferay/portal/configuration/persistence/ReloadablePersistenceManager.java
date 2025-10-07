/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Objects;

import org.apache.felix.cm.PersistenceManager;

/**
 * @author Raymond Augé
 * @author Gregory Amerson
 */
public interface ReloadablePersistenceManager extends PersistenceManager {

	public default boolean isEphemeral(String pid) throws IOException {
		return isEphemeral(pid, load(pid));
	}

	public default boolean isEphemeral(
		String pid, Dictionary<Object, Object> dictionary) {

		if ((dictionary != null) &&
			Objects.equals(
				dictionary.get(STORAGE_POLICY_KEY),
				STORAGE_POLICY_VALUE_EPHEMERAL)) {

			return true;
		}

		return false;
	}

	public void reload(String pid) throws IOException;

	public String STORAGE_POLICY_KEY = ".persistenceManager.storagePolicy";

	public String STORAGE_POLICY_VALUE_EPHEMERAL = "ephemeral";

}