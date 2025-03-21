/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.configuration.provider;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.configuration.SegmentsCompanyConfiguration;
import com.liferay.segments.configuration.SegmentsConfiguration;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	configurationPid = "com.liferay.segments.configuration.SegmentsConfiguration",
	service = SegmentsConfigurationProvider.class
)
public class SegmentsConfigurationProviderImpl
	implements SegmentsConfigurationProvider {

	@Override
	public String getCompanyConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_portal.getUser(httpServletRequest));

		if (permissionChecker.isCompanyAdmin()) {
			String factoryPid = SegmentsCompanyConfiguration.class.getName();

			String pid = factoryPid;

			Configuration configuration = _getSegmentsCompanyConfiguration(
				_portal.getCompanyId(httpServletRequest));

			if (configuration != null) {
				pid = configuration.getPid();
			}

			return PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest,
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/configuration_admin/edit_configuration"
			).setRedirect(
				ParamUtil.getString(
					httpServletRequest, "backURL",
					_portal.getCurrentCompleteURL(httpServletRequest))
			).setParameter(
				"factoryPid", factoryPid
			).setParameter(
				"pid", pid
			).buildString();
		}

		return null;
	}

	@Override
	public String getConfigurationURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			_portal.getUser(httpServletRequest));

		if (!permissionChecker.isOmniadmin()) {
			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/configuration_admin/edit_configuration"
		).setRedirect(
			_portal.getCurrentCompleteURL(httpServletRequest)
		).setParameter(
			"factoryPid", SegmentsConfiguration.class.getName()
		).buildString();
	}

	@Override
	public boolean isRoleSegmentationEnabled() throws ConfigurationException {
		return _segmentsConfiguration.roleSegmentationEnabled();
	}

	@Override
	public boolean isRoleSegmentationEnabled(long companyId)
		throws ConfigurationException {

		if (!_segmentsConfiguration.roleSegmentationEnabled()) {
			return false;
		}

		if (!isSegmentsCompanyConfigurationDefined(companyId)) {
			return _segmentsConfiguration.roleSegmentationEnabled();
		}

		SegmentsCompanyConfiguration segmentsCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SegmentsCompanyConfiguration.class, companyId);

		return segmentsCompanyConfiguration.roleSegmentationEnabled();
	}

	@Override
	public boolean isSegmentationEnabled() throws ConfigurationException {
		return _segmentsConfiguration.segmentationEnabled();
	}

	@Override
	public boolean isSegmentationEnabled(long companyId)
		throws ConfigurationException {

		if (!_segmentsConfiguration.segmentationEnabled()) {
			return false;
		}

		if (!isSegmentsCompanyConfigurationDefined(companyId)) {
			return _segmentsConfiguration.segmentationEnabled();
		}

		SegmentsCompanyConfiguration segmentsCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SegmentsCompanyConfiguration.class, companyId);

		return segmentsCompanyConfiguration.segmentationEnabled();
	}

	@Override
	public boolean isSegmentsCompanyConfigurationDefined(long companyId)
		throws ConfigurationException {

		if (_getSegmentsCompanyConfiguration(companyId) != null) {
			return true;
		}

		return false;
	}

	@Override
	public void resetSegmentsCompanyConfiguration(long companyId)
		throws ConfigurationException {

		Configuration configuration = _getSegmentsCompanyConfiguration(
			companyId);

		while (configuration != null) {
			try {
				configuration.delete();
			}
			catch (IOException ioException) {
				throw new ConfigurationException(ioException);
			}

			configuration = _getSegmentsCompanyConfiguration(companyId);
		}
	}

	@Override
	public void updateSegmentsCompanyConfiguration(
			long companyId,
			SegmentsCompanyConfiguration segmentsCompanyConfiguration)
		throws ConfigurationException {

		Dictionary<String, Object> properties = null;

		Configuration configuration = _getSegmentsCompanyConfiguration(
			companyId);

		if (configuration == null) {
			try {
				configuration = _configurationAdmin.createFactoryConfiguration(
					SegmentsCompanyConfiguration.class.getName() + ".scoped",
					StringPool.QUESTION);
			}
			catch (IOException ioException) {
				throw new ConfigurationException(ioException);
			}

			properties = HashMapDictionaryBuilder.<String, Object>put(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				companyId
			).build();
		}
		else {
			properties = configuration.getProperties();
		}

		properties.put(
			"roleSegmentationEnabled",
			segmentsCompanyConfiguration.roleSegmentationEnabled());
		properties.put(
			"segmentationEnabled",
			segmentsCompanyConfiguration.segmentationEnabled());

		try {
			configuration.update(properties);
		}
		catch (IOException ioException) {
			throw new ConfigurationException(ioException);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_segmentsConfiguration = ConfigurableUtil.createConfigurable(
			SegmentsConfiguration.class, properties);
	}

	private Configuration _getSegmentsCompanyConfiguration(long companyId)
		throws ConfigurationException {

		try {
			String filterString = StringBundler.concat(
				"(&(", ConfigurationAdmin.SERVICE_FACTORYPID, StringPool.EQUAL,
				SegmentsCompanyConfiguration.class.getName(), ".scoped",
				")(companyId=", companyId, "))");

			Configuration[] configuration =
				_configurationAdmin.listConfigurations(filterString);

			if (configuration != null) {
				return configuration[0];
			}

			return null;
		}
		catch (InvalidSyntaxException | IOException exception) {
			throw new ConfigurationException(exception);
		}
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	private volatile SegmentsConfiguration _segmentsConfiguration;

}