/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence;

import java.io.IOException;

import org.apache.felix.cm.PersistenceManager;

/**
 * @author Raymond Augé
 * @author Gregory Amerson
 */
public interface ReloadablePersistenceManager extends PersistenceManager {

	public void reload(String pid) throws IOException;

	public String STORAGE_POLICY_KEY = ".persistenceManager.storagePolicy";

}