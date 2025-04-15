/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.servlet.taglib;

import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.display.context.EditConfigurationDisplayContext;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.util.TensorFlowDownloadHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DynamicInclude.class)
public class EditConfigurationJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		try {
			TensorFlowImageAssetAutoTagProviderCompanyConfiguration
				tensorFlowImageAssetAutoTagProviderCompanyConfiguration =
					_configurationProvider.getCompanyConfiguration(
						TensorFlowImageAssetAutoTagProviderCompanyConfiguration.
							class,
						_portal.getCompanyId(httpServletRequest));

			httpServletRequest.setAttribute(
				EditConfigurationDisplayContext.class.getName(),
				new EditConfigurationDisplayContext(
					_tensorFlowDownloadHelper,
					tensorFlowImageAssetAutoTagProviderCompanyConfiguration));
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			StringBundler.concat(
				"com.liferay.configuration.admin.web#/edit_configuration.jsp#",
				"com.liferay.document.library.asset.auto.tagger.tensorflow.",
				"internal.configuration.TensorFlowImageAssetAutoTagProvider",
				"CompanyConfiguration#pre"));
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/com.liferay.configuration.admin.web" +
			"/edit_configuration.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditConfigurationJSPDynamicInclude.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.asset.auto.tagger.tensorflow)"
	)
	private ServletContext _servletContext;

	@Reference
	private TensorFlowDownloadHelper _tensorFlowDownloadHelper;

}