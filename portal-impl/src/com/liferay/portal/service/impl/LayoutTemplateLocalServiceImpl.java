/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.aop.SkipAop;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.DummyWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTemplateConstants;
import com.liferay.portal.kernel.model.PluginSetting;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.service.PluginSettingLocalService;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResourceLoaderUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.layoutconfiguration.util.velocity.InitColumnProcessor;
import com.liferay.portal.model.impl.LayoutTemplateImpl;
import com.liferay.portal.service.base.LayoutTemplateLocalServiceBaseImpl;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.ServletContext;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivica Cardic
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
@SkipAop
public class LayoutTemplateLocalServiceImpl
	extends LayoutTemplateLocalServiceBaseImpl {

	public static final Set<String> supportedLangTypes = new HashSet<>(
		Arrays.asList(
			TemplateConstants.LANG_TYPE_VM, TemplateConstants.LANG_TYPE_FTL));

	@Override
	public String getContent(
		String layoutTemplateId, boolean standard, String themeId) {

		LayoutTemplate layoutTemplate = getLayoutTemplate(
			layoutTemplateId, standard, themeId);

		if (layoutTemplate == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Layout template " + layoutTemplateId + " does not exist");
			}

			layoutTemplate = getLayoutTemplate(
				PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID, standard, themeId);

			if (layoutTemplate == null) {
				_log.error(
					StringBundler.concat(
						"Layout template ", layoutTemplateId,
						" and default layout template ",
						PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID,
						" do not exist"));

				return StringPool.BLANK;
			}
		}

		if (PropsValues.LAYOUT_TEMPLATE_CACHE_ENABLED) {
			return layoutTemplate.getContent();
		}

		try {
			return layoutTemplate.getUncachedContent();
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public String getLangType(
		String layoutTemplateId, boolean standard, String themeId) {

		LayoutTemplate layoutTemplate = getLayoutTemplate(
			layoutTemplateId, standard, themeId);

		if (layoutTemplate == null) {
			return TemplateConstants.LANG_TYPE_VM;
		}

		return _getSupportedLangType(layoutTemplate);
	}

	@Override
	public LayoutTemplate getLayoutTemplate(
		String layoutTemplateId, boolean standard, String themeId) {

		if (Validator.isNull(layoutTemplateId)) {
			return null;
		}

		LayoutTemplate layoutTemplate = null;

		if (themeId != null) {
			if (standard) {
				Map<String, LayoutTemplate> themesStandard = _getThemesStandard(
					themeId);

				layoutTemplate = themesStandard.get(layoutTemplateId);
			}
			else {
				Map<String, LayoutTemplate> themesCustom = _getThemesCustom(
					themeId);

				layoutTemplate = themesCustom.get(layoutTemplateId);
			}

			if (layoutTemplate != null) {
				return layoutTemplate;
			}
		}

		if (standard) {
			layoutTemplate = _warStandard.get(layoutTemplateId);

			if (layoutTemplate == null) {
				layoutTemplate = _portalStandard.get(layoutTemplateId);
			}
		}
		else {
			layoutTemplate = _warCustom.get(layoutTemplateId);

			if (layoutTemplate == null) {
				layoutTemplate = _portalCustom.get(layoutTemplateId);
			}
		}

		return layoutTemplate;
	}

	@Override
	public List<LayoutTemplate> getLayoutTemplates() {
		List<LayoutTemplate> customLayoutTemplates = new ArrayList<>(
			_portalCustom.size() + _warCustom.size());

		customLayoutTemplates.addAll(_portalCustom.values());
		customLayoutTemplates.addAll(_warCustom.values());

		return customLayoutTemplates;
	}

	@Override
	public List<LayoutTemplate> getLayoutTemplates(String themeId) {
		Map<String, LayoutTemplate> themesCustom = _getThemesCustom(themeId);

		List<LayoutTemplate> customLayoutTemplates = new ArrayList<>(
			_portalCustom.size() + _warCustom.size() + themesCustom.size());

		for (Map.Entry<String, LayoutTemplate> entry :
				_portalCustom.entrySet()) {

			String layoutTemplateId = entry.getKey();

			LayoutTemplate themeCustomLayoutTemplate = themesCustom.get(
				layoutTemplateId);

			if (themeCustomLayoutTemplate != null) {
				customLayoutTemplates.add(themeCustomLayoutTemplate);
			}
			else {
				LayoutTemplate warCustomLayoutTemplate = _warCustom.get(
					layoutTemplateId);

				if (warCustomLayoutTemplate != null) {
					customLayoutTemplates.add(warCustomLayoutTemplate);
				}
				else {
					customLayoutTemplates.add(entry.getValue());
				}
			}
		}

		for (Map.Entry<String, LayoutTemplate> entry : _warCustom.entrySet()) {
			String layoutTemplateId = entry.getKey();

			if (!_portalCustom.containsKey(layoutTemplateId) &&
				!themesCustom.containsKey(layoutTemplateId)) {

				customLayoutTemplates.add(_warCustom.get(layoutTemplateId));
			}
		}

		for (Map.Entry<String, LayoutTemplate> entry :
				themesCustom.entrySet()) {

			String layoutTemplateId = entry.getKey();

			if (!_portalCustom.containsKey(layoutTemplateId) &&
				!_warCustom.containsKey(layoutTemplateId)) {

				customLayoutTemplates.add(themesCustom.get(layoutTemplateId));
			}
		}

		return customLayoutTemplates;
	}

	@Override
	public List<LayoutTemplate> init(
		ServletContext servletContext, String[] xmls,
		PluginPackage pluginPackage) {

		return init(null, servletContext, xmls, pluginPackage);
	}

	@Override
	public List<LayoutTemplate> init(
		String servletContextName, ServletContext servletContext, String[] xmls,
		PluginPackage pluginPackage) {

		Set<LayoutTemplate> layoutTemplates = new LinkedHashSet<>();

		try {
			for (String xml : xmls) {
				layoutTemplates.addAll(
					_readLayoutTemplates(
						servletContextName, servletContext, xml,
						pluginPackage));
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return new ArrayList<>(layoutTemplates);
	}

	@Override
	public void readLayoutTemplate(
		String servletContextName, ServletContext servletContext,
		Set<LayoutTemplate> layoutTemplates, Element element, boolean standard,
		String themeId, PluginPackage pluginPackage) {

		Map<String, LayoutTemplate> installedLayoutTemplates = null;

		if (themeId != null) {
			if (standard) {
				installedLayoutTemplates = _getThemesStandard(themeId);
			}
			else {
				installedLayoutTemplates = _getThemesCustom(themeId);
			}
		}
		else if (servletContextName != null) {
			if (standard) {
				installedLayoutTemplates = _warStandard;
			}
			else {
				installedLayoutTemplates = _warCustom;
			}
		}
		else {
			if (standard) {
				installedLayoutTemplates = _portalStandard;
			}
			else {
				installedLayoutTemplates = _portalCustom;
			}
		}

		List<Element> layoutTemplateElements = element.elements(
			"layout-template");

		for (Element layoutTemplateElement : layoutTemplateElements) {
			String layoutTemplateId = layoutTemplateElement.attributeValue(
				"id");

			LayoutTemplate layoutTemplateModel = installedLayoutTemplates.get(
				layoutTemplateId);

			if (layoutTemplateModel == null) {
				layoutTemplateModel = new LayoutTemplateImpl(layoutTemplateId);

				installedLayoutTemplates.put(
					layoutTemplateId, layoutTemplateModel);
			}

			PluginSetting pluginSetting =
				_pluginSettingLocalService.getDefaultPluginSetting();

			layoutTemplateModel.setPluginPackage(pluginPackage);
			layoutTemplateModel.setServletContext(servletContext);

			if (servletContextName != null) {
				layoutTemplateModel.setServletContextName(servletContextName);
			}

			layoutTemplateModel.setStandard(standard);
			layoutTemplateModel.setThemeId(themeId);

			String templateName = GetterUtil.getString(
				layoutTemplateElement.attributeValue("name"));

			if (Validator.isNotNull(templateName)) {
				layoutTemplateModel.setName(templateName);
			}

			layoutTemplateModel.setTemplatePath(
				GetterUtil.getString(
					layoutTemplateElement.elementText("template-path"),
					layoutTemplateModel.getTemplatePath()));
			layoutTemplateModel.setThumbnailPath(
				GetterUtil.getString(
					layoutTemplateElement.elementText("thumbnail-path"),
					layoutTemplateModel.getThumbnailPath()));

			String content = null;

			try {
				content = StreamUtil.toString(
					servletContext.getResourceAsStream(
						layoutTemplateModel.getTemplatePath()));
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to get content at template path ",
						layoutTemplateModel.getTemplatePath(), ": ",
						exception.getMessage()));
			}

			if (Validator.isNull(content)) {
				_log.error(
					"No content found at template path " +
						layoutTemplateModel.getTemplatePath());
			}
			else {
				StringBundler sb = new StringBundler(3);

				sb.append(themeId);

				if (standard) {
					sb.append(LayoutTemplateConstants.STANDARD_SEPARATOR);
				}
				else {
					sb.append(LayoutTemplateConstants.CUSTOM_SEPARATOR);
				}

				sb.append(layoutTemplateId);

				String velocityTemplateId = sb.toString();

				layoutTemplateModel.setContent(content);
				layoutTemplateModel.setColumns(
					_getColumns(
						velocityTemplateId, content,
						_getSupportedLangType(layoutTemplateModel)));
			}

			Element rolesElement = layoutTemplateElement.element("roles");

			if (rolesElement != null) {
				List<Element> roleNameElements = rolesElement.elements(
					"role-name");

				for (Element roleNameElement : roleNameElements) {
					pluginSetting.addRole(roleNameElement.getText());
				}
			}

			layoutTemplateModel.setDefaultPluginSetting(pluginSetting);

			if (layoutTemplates != null) {
				layoutTemplates.add(layoutTemplateModel);
			}
		}
	}

	@Override
	public void uninstallLayoutTemplate(
		String layoutTemplateId, boolean standard) {

		String templateId = null;

		LayoutTemplate layoutTemplate = getLayoutTemplate(
			layoutTemplateId, standard, null);

		try {
			if (standard) {
				templateId =
					"null" + LayoutTemplateConstants.STANDARD_SEPARATOR +
						layoutTemplateId;

				TemplateResourceLoaderUtil.clearCache(
					_getSupportedLangType(layoutTemplate), templateId);

				_warStandard.remove(layoutTemplateId);
			}
			else {
				templateId =
					"null" + LayoutTemplateConstants.CUSTOM_SEPARATOR +
						layoutTemplateId;

				TemplateResourceLoaderUtil.clearCache(
					_getSupportedLangType(layoutTemplate), templateId);

				_warCustom.remove(layoutTemplateId);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to uninstall layout template " + layoutTemplateId,
				exception);
		}
	}

	@Override
	public void uninstallLayoutTemplates(String themeId) {
		Map<String, LayoutTemplate> themesStandard = _getThemesStandard(
			themeId);

		for (Map.Entry<String, LayoutTemplate> entry :
				themesStandard.entrySet()) {

			LayoutTemplate layoutTemplate = entry.getValue();

			String templateId =
				themeId + LayoutTemplateConstants.STANDARD_SEPARATOR +
					layoutTemplate.getLayoutTemplateId();

			try {
				TemplateResourceLoaderUtil.clearCache(
					_getSupportedLangType(layoutTemplate), templateId);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to uninstall layout template " +
						layoutTemplate.getLayoutTemplateId(),
					exception);
			}
		}

		themesStandard.clear();

		Map<String, LayoutTemplate> themesCustom = _getThemesCustom(themeId);

		for (Map.Entry<String, LayoutTemplate> entry :
				themesCustom.entrySet()) {

			LayoutTemplate layoutTemplate = entry.getValue();

			String templateId =
				themeId + LayoutTemplateConstants.CUSTOM_SEPARATOR +
					layoutTemplate.getLayoutTemplateId();

			try {
				TemplateResourceLoaderUtil.clearCache(
					_getSupportedLangType(layoutTemplate), templateId);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to uninstall layout template " +
						layoutTemplate.getLayoutTemplateId(),
					exception);
			}
		}

		themesCustom.clear();
	}

	private List<String> _getColumns(
		String templateId, String templateContent, String langType) {

		try {
			InitColumnProcessor processor = new InitColumnProcessor();

			Template template = TemplateManagerUtil.getTemplate(
				langType,
				new StringTemplateResource(templateId, templateContent), false);

			template.put("processor", processor);

			template.processTemplate(new DummyWriter());

			return ListUtil.sort(processor.getColumns());
		}
		catch (Exception exception) {
			_log.error("Unable to get layout template columns", exception);

			return new ArrayList<>();
		}
	}

	private String _getSupportedLangType(LayoutTemplate layoutTemplate) {
		String templatePath = layoutTemplate.getTemplatePath();

		int index = templatePath.lastIndexOf(StringPool.PERIOD);

		if (index != -1) {
			String langType = templatePath.substring(index + 1);

			if (supportedLangTypes.contains(langType)) {
				return langType;
			}
		}

		return TemplateConstants.LANG_TYPE_VM;
	}

	private Map<String, LayoutTemplate> _getThemesCustom(String themeId) {
		Map<String, LayoutTemplate> layoutTemplates = _customThemes.get(
			themeId);

		if (layoutTemplates == null) {
			layoutTemplates = new LinkedHashMap<>();

			_customThemes.put(themeId, layoutTemplates);
		}

		return layoutTemplates;
	}

	private Map<String, LayoutTemplate> _getThemesStandard(String themeId) {
		Map<String, LayoutTemplate> layoutTemplates = _standardThemes.get(
			themeId);

		if (layoutTemplates == null) {
			layoutTemplates = new LinkedHashMap<>();

			_standardThemes.put(themeId, layoutTemplates);
		}

		return layoutTemplates;
	}

	private Set<LayoutTemplate> _readLayoutTemplates(
			String servletContextName, ServletContext servletContext,
			String xml, PluginPackage pluginPackage)
		throws Exception {

		Set<LayoutTemplate> layoutTemplates = new HashSet<>();

		if (xml == null) {
			return layoutTemplates;
		}

		Document document = UnsecureSAXReaderUtil.read(xml, true);

		Element rootElement = document.getRootElement();

		Element standardElement = rootElement.element("standard");

		if (standardElement != null) {
			readLayoutTemplate(
				servletContextName, servletContext, layoutTemplates,
				standardElement, true, null, pluginPackage);
		}

		Element customElement = rootElement.element("custom");

		if (customElement != null) {
			readLayoutTemplate(
				servletContextName, servletContext, layoutTemplates,
				customElement, false, null, pluginPackage);
		}

		return layoutTemplates;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutTemplateLocalServiceImpl.class);

	private static final Map<String, Map<String, LayoutTemplate>>
		_customThemes = new HashMap<>();
	private static final Map<String, LayoutTemplate> _portalCustom =
		new LinkedHashMap<>();
	private static final Map<String, LayoutTemplate> _portalStandard =
		new HashMap<>();
	private static final Map<String, Map<String, LayoutTemplate>>
		_standardThemes = new HashMap<>();
	private static final Map<String, LayoutTemplate> _warCustom =
		new LinkedHashMap<>();
	private static final Map<String, LayoutTemplate> _warStandard =
		new HashMap<>();

	@BeanReference(type = PluginSettingLocalService.class)
	private PluginSettingLocalService _pluginSettingLocalService;

}