/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.template;

import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.resource.locator.ResourceLocatorFactory;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_GLOBAL,
	service = TemplateContextContributor.class
)
public class ResourceLocatorTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		contextObjects.put(
			"resourceLocator",
			new UnsafeSupplierValue<>(
				() -> {
					try {
						User user = _portal.getUser(httpServletRequest);

						if (user == null) {
							user = _userLocalService.getGuestUser(
								_portal.getCompanyId(httpServletRequest));
						}

						return _resourceLocatorFactory.create(
							httpServletRequest, user);
					}
					catch (PortalException portalException) {
						_log.error(portalException);
					}

					return null;
				}));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceLocatorTemplateContextContributor.class);

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocatorFactory _resourceLocatorFactory;

	@Reference
	private UserLocalService _userLocalService;

}