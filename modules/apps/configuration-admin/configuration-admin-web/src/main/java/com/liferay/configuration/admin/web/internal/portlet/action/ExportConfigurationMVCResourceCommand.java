/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.exportimport.ConfigurationExportImportProcessor;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.exporter.ConfigurationExporter;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition.Scope;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.FileInputStream;
import java.io.Serializable;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * @author Brian Wing Shun Chan
 * @author Vilmos Papp
 * @author Eduardo García
 * @author Raymond Augé
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/configuration_admin/export_configuration"
	},
	service = MVCResourceCommand.class
)
public class ExportConfigurationMVCResourceCommand
	implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		String pid = ParamUtil.getString(resourceRequest, "pid");
		String factoryPid = ParamUtil.getString(resourceRequest, "factoryPid");

		try {
			if (Validator.isNotNull(pid)) {
				_exportPid(resourceRequest, resourceResponse);
			}
			else if (Validator.isNotNull(factoryPid)) {
				_exportFactoryPid(resourceRequest, resourceResponse);
			}
			else {
				_exportAll(resourceRequest, resourceResponse);
			}
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return false;
	}

	protected Dictionary<String, Object> getProperties(
			String languageId, String factoryPid, String pid, Scope scope,
			Serializable scopePK)
		throws Exception {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				languageId, scope, scopePK);

		ConfigurationModel configurationModel = configurationModels.get(pid);

		if ((configurationModel == null) && Validator.isNotNull(factoryPid)) {
			configurationModel = configurationModels.get(factoryPid);
		}

		if (configurationModel == null) {
			return properties;
		}

		Configuration configuration =
			_configurationModelRetriever.getConfiguration(pid, scope, scopePK);

		if (configuration == null) {
			return properties;
		}

		Dictionary<String, Object> configurationProperties =
			configuration.getProperties();

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			configurationModel.getExtendedObjectClassDefinition();

		ExtendedAttributeDefinition[] attributeDefinitions =
			extendedObjectClassDefinition.getAttributeDefinitions(
				ConfigurationModel.ALL);

		for (AttributeDefinition attributeDefinition : attributeDefinitions) {
			if (!PropsValues.MODULE_FRAMEWORK_EXPORT_PASSWORD_ATTRIBUTES &&
				(attributeDefinition.getType() ==
					AttributeDefinition.PASSWORD)) {

				continue;
			}

			Object value = configurationProperties.get(
				attributeDefinition.getID());

			if (value == null) {
				continue;
			}

			properties.put(attributeDefinition.getID(), value);
		}

		if (!Scope.SYSTEM.equals(scope)) {
			properties.put(scope.getPropertyKey(), scopePK);

			if (FeatureFlagManagerUtil.isEnabled("LPS-155284")) {
				_configurationExportImportProcessor.prepareForExport(
					pid, properties);
			}
		}

		return properties;
	}

	private void _exportAll(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String languageId = themeDisplay.getLanguageId();

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(resourceRequest);

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				themeDisplay.getLanguageId(),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK());

		for (ConfigurationModel configurationModel :
				configurationModels.values()) {

			String curFactoryPid = configurationModel.getFactoryPid();

			if (configurationModel.isFactory()) {
				List<ConfigurationModel> factoryInstancesConfigurationModels =
					_configurationModelRetriever.getFactoryInstances(
						configurationModel,
						configurationScopeDisplayContext.getScope(),
						configurationScopeDisplayContext.getScopePK());

				for (ConfigurationModel factoryInstancesConfigurationModel :
						factoryInstancesConfigurationModels) {

					String curPid = factoryInstancesConfigurationModel.getID();

					String curFileName = _getFileName(curFactoryPid, curPid);

					zipWriter.addEntry(
						curFileName,
						ConfigurationExporter.getPropertiesAsBytes(
							getProperties(
								languageId, curFactoryPid, curPid,
								configurationScopeDisplayContext.getScope(),
								configurationScopeDisplayContext.
									getScopePK())));
				}
			}
			else if (configurationModel.hasConfiguration()) {
				String curPid = configurationModel.getID();

				String curFileName = _getFileName(null, curPid);

				zipWriter.addEntry(
					curFileName,
					ConfigurationExporter.getPropertiesAsBytes(
						getProperties(
							languageId, curFactoryPid, curPid,
							configurationScopeDisplayContext.getScope(),
							configurationScopeDisplayContext.getScopePK())));
			}
		}

		String fileName = "liferay-site-settings.zip";

		if (Objects.equals(
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				themeDisplay.getPpid())) {

			fileName = "liferay-instance-settings.zip";
		}
		else if (Objects.equals(
					ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
					themeDisplay.getPpid())) {

			fileName = "liferay-system-settings.zip";
		}

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, fileName,
			new FileInputStream(zipWriter.getFile()),
			ContentTypes.APPLICATION_ZIP);
	}

	private void _exportFactoryPid(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String languageId = themeDisplay.getLanguageId();

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		String factoryPid = ParamUtil.getString(resourceRequest, "factoryPid");

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(resourceRequest);

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				themeDisplay.getLanguageId(),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK());

		ConfigurationModel factoryConfigurationModel = configurationModels.get(
			factoryPid);

		List<ConfigurationModel> factoryInstancesConfigurationModels =
			_configurationModelRetriever.getFactoryInstances(
				factoryConfigurationModel,
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK());

		for (ConfigurationModel factoryInstancesConfigurationModel :
				factoryInstancesConfigurationModels) {

			String curPid = factoryInstancesConfigurationModel.getID();

			String curFileName = _getFileName(factoryPid, curPid);

			zipWriter.addEntry(
				curFileName,
				ConfigurationExporter.getPropertiesAsBytes(
					getProperties(
						languageId, factoryPid, curPid,
						configurationScopeDisplayContext.getScope(),
						configurationScopeDisplayContext.getScopePK())));
		}

		String fileName =
			"liferay-system-settings-" +
				factoryConfigurationModel.getFactoryPid() + ".zip";

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, fileName,
			new FileInputStream(zipWriter.getFile()),
			ContentTypes.APPLICATION_ZIP);
	}

	private void _exportPid(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String factoryPid = ParamUtil.getString(resourceRequest, "factoryPid");
		String pid = ParamUtil.getString(resourceRequest, "pid");

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String languageId = themeDisplay.getLanguageId();

		String fileName = _getFileName(factoryPid, pid);

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(resourceRequest);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, fileName,
			ConfigurationExporter.getPropertiesAsBytes(
				getProperties(
					languageId, factoryPid, pid,
					configurationScopeDisplayContext.getScope(),
					configurationScopeDisplayContext.getScopePK())),
			ContentTypes.TEXT_XML_UTF8);
	}

	private String _getFileName(String factoryPid, String pid) {
		String fileName = pid;

		if (Validator.isNotNull(factoryPid) && !factoryPid.equals(pid)) {
			String factoryInstanceId = pid.substring(factoryPid.length() + 1);

			if (factoryInstanceId.startsWith("scoped.")) {
				factoryPid = factoryPid + ".scoped";

				factoryInstanceId = StringUtil.removeSubstring(
					factoryInstanceId, "scoped.");
			}
			else if (factoryInstanceId.startsWith("scoped~")) {
				factoryPid = factoryPid + ".scoped";

				factoryInstanceId = StringUtil.removeSubstring(
					factoryInstanceId, "scoped~");
			}

			fileName = factoryPid + StringPool.TILDE + factoryInstanceId;
		}

		return fileName + ".config";
	}

	@Reference
	private ConfigurationExportImportProcessor
		_configurationExportImportProcessor;

	@Reference(target = "(filter.visibility=*)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}