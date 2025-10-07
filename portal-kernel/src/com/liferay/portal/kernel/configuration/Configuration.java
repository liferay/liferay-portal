/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.configuration;

import java.util.List;
import java.util.Properties;

/**
 * @author Brian Wing Shun Chan
 */
public interface Configuration {

	public void clearCache();

	public boolean contains(String key);

	public String get(String key);

	public String get(String key, Filter filter);

	public String[] getArray(String key);

	public String[] getArray(String key, Filter filter);

	public List<String> getLoadedSources();

	public Properties getProperties();

	public Properties getProperties(String prefix, boolean removePrefix);

	public void set(String key, String value);

}