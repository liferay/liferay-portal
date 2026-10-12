/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.internal.recommendation.info.collection.provider;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.machine.learning.internal.recommendation.configuration.CommerceMLRecommendationsCollectionProviderConfiguration;
import com.liferay.commerce.util.CommerceChannelConfigurationUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Riccardo Ferrari
 */
public class BaseCommerceMLRecommendationCollectionProvider {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		commerceMLRecommendationsCollectionProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				CommerceMLRecommendationsCollectionProviderConfiguration.class,
				properties);
	}

	protected boolean isGuestBrowsingEnabled() throws PortalException {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return true;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return true;
		}

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return true;
		}

		return CommerceChannelConfigurationUtil.isGuestBrowsingEnabled(
			PermissionThreadLocal.getPermissionChecker(),
			commerceContext.getCommerceChannelGroupId());
	}

	protected volatile CommerceMLRecommendationsCollectionProviderConfiguration
		commerceMLRecommendationsCollectionProviderConfiguration;

}