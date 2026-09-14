/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.extension;

import java.io.Serializable;

import java.util.Collection;
import java.util.Map;

/**
 * @author Javier de Arcos
 */
public interface ExtensionProvider {

	public Map<String, Serializable> getExtendedProperties(
			long companyId, long userId, String className, Object entity)
		throws Exception;

	public Map<String, PropertyDefinition> getExtendedPropertyDefinitions(
			long companyId, String className)
		throws Exception;

	public Collection<String> getFilteredPropertyNames(
		long companyId, Object entity);

	public boolean isApplicableExtension(long companyId, String className);

	public void setExtendedProperties(
			long companyId, long userId, String className, Object entity,
			Map<String, Serializable> extendedProperties)
		throws Exception;

	public default void setExtendedProperties(
			long companyId, long userId, String className, Object entity,
			Map<String, Serializable> extendedProperties, boolean partialUpdate)
		throws Exception {

		setExtendedProperties(
			companyId, userId, className, entity, extendedProperties);
	}

}