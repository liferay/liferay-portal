/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.instance.tracker.url.provider;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Feliphe Marinho
 */
@ProviderType
public interface WorkflowInstanceTrackerURLProvider {

	public String getURL(
		Object bean, HttpServletRequest httpServletRequest, Class<?> modelClass,
		boolean useDialog);

}