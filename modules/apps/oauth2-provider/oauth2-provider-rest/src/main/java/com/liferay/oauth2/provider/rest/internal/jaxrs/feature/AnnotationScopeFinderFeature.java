/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import java.util.Set;

import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;

import org.osgi.framework.BundleContext;

/**
 * @author Shuyang Zhou
 */
public class AnnotationScopeFinderFeature extends BaseScopeFinderFeature {

	public AnnotationScopeFinderFeature(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	protected void collectScopes(
		OperationResourceInfo operationResourceInfo, Set<String> scopes) {

		ClassResourceInfo classResourceInfo =
			operationResourceInfo.getClassResourceInfo();

		scopes.addAll(
			RequiresScopeAnnotationFinder.find(
				classResourceInfo.getServiceClass()));
	}

}