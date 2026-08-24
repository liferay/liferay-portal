/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.internal.configuration.HeadlessAPICacheCompanyConfiguration;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

import java.net.URI;

import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Alejandro Tardín
 */
@Provider
public class CacheContainerResponseFilter implements ContainerResponseFilter {

	public CacheContainerResponseFilter(ConfigurationAdmin configurationAdmin) {
		_configurationAdmin = configurationAdmin;
	}

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		MultivaluedMap<String, Object> headers =
			containerResponseContext.getHeaders();

		String cacheControl = null;

		try {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					String.format(
						"(&(service.factoryPid=%s)(%s=%d))",
						HeadlessAPICacheCompanyConfiguration.class.getName(),
						ExtendedObjectClassDefinition.Scope.COMPANY.
							getPropertyKey(),
						CompanyThreadLocal.getCompanyId()));

			if (configurations != null) {
				UriInfo uriInfo = containerRequestContext.getUriInfo();

				URI baseURI = uriInfo.getBaseUri();

				String basePath = StringUtil.removeFirst(
				baseURI.getPath(), Portal.PATH_MODULE);

				if (!basePath.endsWith("/")) {
					basePath += "/";
				}

				String[] pathParts = StringUtil.split(
					basePath + uriInfo.getPath(), CharPool.SLASH);

				for (Configuration configuration : configurations) {
					HeadlessAPICacheCompanyConfiguration
						headlessAPICacheCompanyConfiguration =
							ConfigurableUtil.createConfigurable(
								HeadlessAPICacheCompanyConfiguration.class,
								configuration.getProperties());

					String[] patternParts = StringUtil.split(
						headlessAPICacheCompanyConfiguration.path(), CharPool.SLASH);

					if (patternParts.length != pathParts.length) {
						continue;
					}

					boolean matches = true;

					for (int i = 0; i < pathParts.length; i++) {
						if (!Objects.equals(patternParts[i], "*") &&
							!Objects.equals(pathParts[i], patternParts[i])) {

							matches = false;

							break;
						}
					}

					if (!matches) {
						continue;
					}

					if (headlessAPICacheCompanyConfiguration.maxAge() <= 0) {
						cacheControl =
							headlessAPICacheCompanyConfiguration.cacheControl();
					}
					else {
						cacheControl = StringBundler.concat(
							headlessAPICacheCompanyConfiguration.cacheControl(),
							", max-age=",
							headlessAPICacheCompanyConfiguration.maxAge());
					}

					break;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (cacheControl == null) {
			headers.putSingle("Cache-Control", "no-cache, no-store");

			return;
		}

		headers.putSingle("Cache-Control", cacheControl);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CacheContainerResponseFilter.class);

	private final ConfigurationAdmin _configurationAdmin;

}