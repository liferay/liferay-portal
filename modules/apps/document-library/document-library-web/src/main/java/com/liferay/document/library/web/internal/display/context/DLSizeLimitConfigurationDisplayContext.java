/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class DLSizeLimitConfigurationDisplayContext {

	public DLSizeLimitConfigurationDisplayContext(
		DLSizeLimitConfigurationProvider dlSizeLimitConfigurationProvider,
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, String scope,
		long scopePK) {

		_dlSizeLimitConfigurationProvider = dlSizeLimitConfigurationProvider;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_scope = scope;
		_scopePK = scopePK;
	}

	public String getEditDLSizeLimitConfigurationURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/instance_settings/edit_size_limits"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).setParameter(
			"scope", _scope
		).setParameter(
			"scopePK", _scopePK
		).buildString();
	}

	public long getFileMaxSize() {
		if (_scope.equals(
				ExtendedObjectClassDefinition.Scope.COMPANY.getValue())) {

			return _dlSizeLimitConfigurationProvider.getCompanyFileMaxSize(
				_scopePK);
		}
		else if (_scope.equals(
					ExtendedObjectClassDefinition.Scope.GROUP.getValue())) {

			return _dlSizeLimitConfigurationProvider.getGroupFileMaxSize(
				_scopePK);
		}
		else if (_scope.equals(
					ExtendedObjectClassDefinition.Scope.SYSTEM.getValue())) {

			return _dlSizeLimitConfigurationProvider.getSystemFileMaxSize();
		}

		throw new IllegalArgumentException("Unsupported scope: " + _scope);
	}

	public String[] getFileMaxSizeHelpArguments() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest);

		return new String[] {
			LanguageUtil.get(
				_httpServletRequest, "overall-maximum-upload-request-size"),
			StringBundler.concat(
				"<a href=\"",
				PortletURLBuilder.create(
					requestBackedPortletURLFactory.createActionURL(
						ConfigurationAdminPortletKeys.SYSTEM_SETTINGS)
				).setMVCRenderCommandName(
					"/configuration_admin/edit_configuration"
				).setParameter(
					"factoryPid",
					"com.liferay.portal.upload.internal.configuration." +
						"UploadServletRequestConfiguration"
				).buildString(),
				"\">",
				LanguageUtil.get(
					_httpServletRequest,
					"upload-servlet-request-configuration-name"),
				"</a>")
		};
	}

	public Map<String, Object> getFileSizePerMimeTypeData() {
		List<Map<String, Object>> sizeList = new ArrayList<>();

		Map<String, Long> mimeTypeSizeLimit = _getMimeTypeSizeLimit();

		mimeTypeSizeLimit.forEach(
			(mimeType, size) -> sizeList.add(
				HashMapBuilder.<String, Object>put(
					"mimeType", mimeType
				).put(
					"size", size
				).build()));

		return HashMapBuilder.<String, Object>put(
			"portletNamespace", _liferayPortletResponse.getNamespace()
		).put(
			"sizeList", sizeList
		).build();
	}

	public long getMaxSizeToCopy() {
		if (_scope.equals(
				ExtendedObjectClassDefinition.Scope.COMPANY.getValue())) {

			return _dlSizeLimitConfigurationProvider.getCompanyMaxSizeToCopy(
				_scopePK);
		}
		else if (_scope.equals(
					ExtendedObjectClassDefinition.Scope.GROUP.getValue())) {

			return _dlSizeLimitConfigurationProvider.getGroupMaxSizeToCopy(
				_scopePK);
		}
		else if (_scope.equals(
					ExtendedObjectClassDefinition.Scope.SYSTEM.getValue())) {

			return _dlSizeLimitConfigurationProvider.getSystemMaxSizeToCopy();
		}

		throw new IllegalArgumentException("Unsupported scope: " + _scope);
	}

	private Map<String, Long> _getMimeTypeSizeLimit() {
		if (_scope.equals(
				ExtendedObjectClassDefinition.Scope.COMPANY.getValue())) {

			return _dlSizeLimitConfigurationProvider.
				getCompanyMimeTypeSizeLimit(_scopePK);
		}
		else if (_scope.equals(
					ExtendedObjectClassDefinition.Scope.GROUP.getValue())) {

			return _dlSizeLimitConfigurationProvider.getGroupMimeTypeSizeLimit(
				_scopePK);
		}
		else if (_scope.equals(
					ExtendedObjectClassDefinition.Scope.SYSTEM.getValue())) {

			return _dlSizeLimitConfigurationProvider.
				getSystemMimeTypeSizeLimit();
		}

		throw new IllegalArgumentException("Unsupported scope: " + _scope);
	}

	private final DLSizeLimitConfigurationProvider
		_dlSizeLimitConfigurationProvider;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final String _scope;
	private final long _scopePK;

}