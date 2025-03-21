/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.util;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lily Chi
 */
public class CPDefinitionLocalServiceCircularDependencyUtil {

	public static CPDefinition copyCPDefinition(long cpDefinitionId)
		throws PortalException {

		CPDefinitionLocalService cpDefinitionLocalService =
			_cpDefinitionLocalServiceSnapshot.get();

		return cpDefinitionLocalService.copyCPDefinition(cpDefinitionId);
	}

	public static boolean isVersionable(long cpDefinitionId) {
		CPDefinitionLocalService cpDefinitionLocalService =
			_cpDefinitionLocalServiceSnapshot.get();

		return cpDefinitionLocalService.isVersionable(cpDefinitionId);
	}

	public static boolean isVersionable(
		long cpDefinitionId, HttpServletRequest httpServletRequest) {

		CPDefinitionLocalService cpDefinitionLocalService =
			_cpDefinitionLocalServiceSnapshot.get();

		return cpDefinitionLocalService.isVersionable(
			cpDefinitionId, httpServletRequest);
	}

	public static CPDefinition updateCPDefinitionIgnoreSKUCombinations(
			long cpDefinitionId, boolean ignoreSKUCombinations,
			ServiceContext serviceContext)
		throws PortalException {

		CPDefinitionLocalService cpDefinitionLocalService =
			_cpDefinitionLocalServiceSnapshot.get();

		return cpDefinitionLocalService.updateCPDefinitionIgnoreSKUCombinations(
			cpDefinitionId, ignoreSKUCombinations, serviceContext);
	}

	private static final Snapshot<CPDefinitionLocalService>
		_cpDefinitionLocalServiceSnapshot = new Snapshot<>(
			CPDefinitionLocalServiceCircularDependencyUtil.class,
			CPDefinitionLocalService.class);

}