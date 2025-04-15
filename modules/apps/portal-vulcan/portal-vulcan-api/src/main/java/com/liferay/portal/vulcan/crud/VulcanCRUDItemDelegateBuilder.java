/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.crud;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Carlos Correa
 */
@ProviderType
public interface VulcanCRUDItemDelegateBuilder {

	public GroupLocalServiceStepVulcanCRUDItemDelegateBuilder acceptLanguage(
		AcceptLanguage acceptLanguage);

	@ProviderType
	public interface BuildStepVulcanCRUDItemDelegateBuilder {

		public VulcanCRUDItemDelegate build() throws Exception;

	}

	@ProviderType
	public interface GroupLocalServiceStepVulcanCRUDItemDelegateBuilder {

		public HttpServletRequestStepVulcanCRUDItemDelegateBuilder
			groupLocalService(GroupLocalService groupLocalService);

	}

	@ProviderType
	public interface HttpServletRequestStepVulcanCRUDItemDelegateBuilder {

		public HttpServletResponseStepVulcanCRUDItemDelegateBuilder
			httpServletRequest(HttpServletRequest httpServletRequest);

	}

	@ProviderType
	public interface HttpServletResponseStepVulcanCRUDItemDelegateBuilder {

		public ResourceActionLocalServiceStepVulcanCRUDItemDelegateBuilder
			httpServletResponse(HttpServletResponse httpServletResponse);

	}

	@ProviderType
	public interface
		ResourceActionLocalServiceStepVulcanCRUDItemDelegateBuilder {

		public ResourcePermissionLocalServiceStepVulcanCRUDItemDelegateBuilder
			resourceActionLocalService(
				ResourceActionLocalService resourceActionLocalService);

	}

	@ProviderType
	public interface
		ResourcePermissionLocalServiceStepVulcanCRUDItemDelegateBuilder {

		public RoleLocalServiceStepVulcanCRUDItemDelegateBuilder
			resourcePermissionLocalService(
				ResourcePermissionLocalService resourcePermissionLocalService);

	}

	@ProviderType
	public interface RoleLocalServiceStepVulcanCRUDItemDelegateBuilder {

		public ScopeCheckerStepVulcanCRUDItemDelegateBuilder roleLocalService(
			RoleLocalService roleLocalService);

	}

	@ProviderType
	public interface ScopeCheckerStepVulcanCRUDItemDelegateBuilder {

		public UriInfoStepVulcanCRUDItemDelegateBuilder scopeChecker(
			Object scopeChecker);

	}

	@ProviderType
	public interface UriInfoStepVulcanCRUDItemDelegateBuilder {

		public UserStepVulcanCRUDItemDelegateBuilder uriInfo(UriInfo uriInfo);

	}

	@ProviderType
	public interface UserStepVulcanCRUDItemDelegateBuilder {

		public BuildStepVulcanCRUDItemDelegateBuilder user(User user);

	}

}