/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.exception.ClientExtensionEntryTypeException;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.configuration.CETConfiguration;
import com.liferay.client.extension.type.factory.CETFactory;
import com.liferay.client.extension.type.factory.CETImplFactory;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = CETFactory.class)
public class CETFactoryImpl implements CETFactory {

	@Override
	public CET create(
			CETConfiguration cetConfiguration, long companyId,
			String externalReferenceCode, boolean replaceVariables)
		throws PortalException {

		CETImplFactory cetImplFactory = _getCETImplFactory(
			companyId, cetConfiguration.type());

		String baseURL = cetConfiguration.baseURL();

		// TODO Use AbsolutePortalURLBuilder

		baseURL = baseURL.replaceAll(
			Pattern.quote("${portalURL}"), _portal.getPathContext());

		if (baseURL.endsWith(StringPool.SLASH)) {
			baseURL = baseURL.substring(0, baseURL.length() - 1);
		}

		Date modifiedDate = new Date(cetConfiguration.buildTimestamp());

		UnicodeProperties typeSettingsUnicodeProperties = _transformURLs(
			baseURL, cetImplFactory,
			_toTypeSettingsUnicodeProperties(cetConfiguration));

		if (replaceVariables) {
			typeSettingsUnicodeProperties = _replaceVariables(
				cetImplFactory, modifiedDate, typeSettingsUnicodeProperties);
		}

		try {
			return cetImplFactory.create(
				baseURL, companyId, modifiedDate,
				cetConfiguration.description(), externalReferenceCode,
				modifiedDate, cetConfiguration.name(),
				_loadProperties(cetConfiguration), true,
				cetConfiguration.sourceCodeURL(),
				WorkflowConstants.STATUS_APPROVED,
				typeSettingsUnicodeProperties);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	@Override
	public CET create(
			ClientExtensionEntry clientExtensionEntry, boolean replaceVariables)
		throws PortalException {

		long companyId = 0;
		Date createDate = null;
		String description = StringPool.BLANK;
		String externalReferenceCode = StringPool.BLANK;
		Date modifiedDate = null;
		String name = StringPool.BLANK;
		Properties properties = null;
		String sourceCodeURL = StringPool.BLANK;
		int status = WorkflowConstants.STATUS_APPROVED;
		UnicodeProperties typeSettingsUnicodeProperties;

		if (clientExtensionEntry != null) {
			companyId = clientExtensionEntry.getCompanyId();
			createDate = clientExtensionEntry.getCreateDate();
			description = clientExtensionEntry.getDescription();
			externalReferenceCode =
				clientExtensionEntry.getExternalReferenceCode();
			modifiedDate = clientExtensionEntry.getModifiedDate();
			name = clientExtensionEntry.getName();

			try {
				properties = PropertiesUtil.load(
					clientExtensionEntry.getProperties());
			}
			catch (IOException ioException) {
				ReflectionUtil.throwException(ioException);
			}

			sourceCodeURL = clientExtensionEntry.getSourceCodeURL();
			status = clientExtensionEntry.getStatus();
			typeSettingsUnicodeProperties = UnicodePropertiesBuilder.create(
				true
			).load(
				clientExtensionEntry.getTypeSettings()
			).build();
		}
		else {
			typeSettingsUnicodeProperties = UnicodePropertiesBuilder.create(
				true
			).build();
		}

		CETImplFactory cetImplFactory = _getCETImplFactory(
			companyId, clientExtensionEntry.getType());

		if (replaceVariables) {
			typeSettingsUnicodeProperties = _replaceVariables(
				cetImplFactory, modifiedDate, typeSettingsUnicodeProperties);
		}

		return cetImplFactory.create(
			StringPool.BLANK, companyId, createDate, description,
			externalReferenceCode, modifiedDate, name, properties, false,
			sourceCodeURL, status,
			_transformURLs(
				StringPool.BLANK, cetImplFactory,
				typeSettingsUnicodeProperties));
	}

	@Override
	public CET create(PortletRequest portletRequest) throws PortalException {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		CETImplFactory cetImplFactory = _getCETImplFactory(
			themeDisplay.getCompanyId(),
			ParamUtil.getString(portletRequest, "type"));

		try {
			return cetImplFactory.create(
				StringPool.BLANK, themeDisplay.getCompanyId(), null,
				ParamUtil.getString(portletRequest, "description"),
				ParamUtil.getString(portletRequest, "externalReferenceCode"),
				null, ParamUtil.getString(portletRequest, "name"),
				PropertiesUtil.load(
					ParamUtil.getString(portletRequest, "properties")),
				false, ParamUtil.getString(portletRequest, "sourceCodeURL"),
				WorkflowConstants.STATUS_APPROVED,
				_transformURLs(
					StringPool.BLANK, cetImplFactory,
					cetImplFactory.getUnicodeProperties(portletRequest)));
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	@Override
	public Collection<String> getTypes() {
		return _types;
	}

	@Override
	public void validate(
			long companyId, UnicodeProperties newTypeSettingsUnicodeProperties,
			UnicodeProperties oldTypeSettingsUnicodeProperties, String type)
		throws PortalException {

		CETImplFactory cetImplFactory = _getCETImplFactory(companyId, type);

		CET oldCET = null;

		if (oldTypeSettingsUnicodeProperties != null) {
			oldCET = cetImplFactory.create(
				StringPool.BLANK, 0, null, StringPool.BLANK, StringPool.BLANK,
				null, StringPool.BLANK, null, false, StringPool.BLANK,
				WorkflowConstants.STATUS_APPROVED,
				_transformURLs(
					StringPool.BLANK, cetImplFactory,
					oldTypeSettingsUnicodeProperties));
		}

		cetImplFactory.validate(
			cetImplFactory.create(
				StringPool.BLANK, 0, null, StringPool.BLANK, StringPool.BLANK,
				null, StringPool.BLANK, null, false, StringPool.BLANK,
				WorkflowConstants.STATUS_APPROVED,
				_transformURLs(
					StringPool.BLANK, cetImplFactory,
					newTypeSettingsUnicodeProperties)),
			oldCET);
	}

	@Activate
	protected void activate() {
		_cetImplFactories = HashMapBuilder.<String, CETImplFactory>put(
			ClientExtensionEntryConstants.TYPE_COMMERCE_CHECKOUT_STEP,
			new CommerceCheckoutStepCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_CUSTOM_ELEMENT,
			new CustomElementCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_EDITOR_CONFIG_CONTRIBUTOR,
			new EditorConfigContributorCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_FDS_CELL_RENDERER,
			new FDSCellRendererCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_FDS_FILTER,
			new FDSFilterCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
			new GlobalCSSCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
			new GlobalJSCETImplFactoryImpl(_jsonFactory)
		).put(
			ClientExtensionEntryConstants.TYPE_IFRAME,
			new IFrameCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_JS_IMPORT_MAPS_ENTRY,
			new JSImportMapsEntryCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_STATIC_CONTENT,
			new StaticContentCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_THEME_CSS,
			new ThemeCSSCETImplFactoryImpl(_jsonFactory)
		).put(
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON,
			new ThemeFaviconCETImplFactoryImpl()
		).put(
			ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP,
			new ThemeSpritemapCETImplFactoryImpl()
		).build();

		_types = Collections.unmodifiableSortedSet(
			new TreeSet<>(_cetImplFactories.keySet()));
	}

	private CETImplFactory _getCETImplFactory(long companyId, String type)
		throws ClientExtensionEntryTypeException {

		CETImplFactory cetImplFactory = _cetImplFactories.get(type);

		if (cetImplFactory != null) {
			String key = FEATURE_FLAG_KEYS.get(type);

			if ((key == null) ||
				FeatureFlagManagerUtil.isEnabled(companyId, key)) {

				return cetImplFactory;
			}
		}

		throw new ClientExtensionEntryTypeException("Unknown type " + type);
	}

	private Properties _loadProperties(CETConfiguration cetConfiguration)
		throws IOException {

		String[] properties = cetConfiguration.properties();

		if (properties == null) {
			return new Properties();
		}

		return PropertiesUtil.load(
			StringUtil.merge(properties, StringPool.NEW_LINE));
	}

	private UnicodeProperties _replaceVariables(
		CETImplFactory<?> cetImplFactory, Date modifiedDate,
		UnicodeProperties unicodeProperties) {

		UnicodeProperties transformedUnicodeProperties = new UnicodeProperties(
			true);

		String modifiedTime = String.valueOf(
			(modifiedDate == null) ? 0 : modifiedDate.getTime());

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();

			if (cetImplFactory.isURLCETPropertyName(name)) {
				value = value.replaceAll(
					Pattern.quote("${modifiedTime}"), modifiedTime);
			}

			transformedUnicodeProperties.put(name, value);
		}

		return transformedUnicodeProperties;
	}

	private UnicodeProperties _toTypeSettingsUnicodeProperties(
		CETConfiguration cetConfiguration) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).build();

		String[] typeSettings = cetConfiguration.typeSettings();

		if (typeSettings == null) {
			return typeSettingsUnicodeProperties;
		}

		for (String typeSetting : typeSettings) {
			typeSettingsUnicodeProperties.put(typeSetting);
		}

		return typeSettingsUnicodeProperties;
	}

	private String _transformURL(String baseURL, String value) {
		if (value.contains(StringPool.NEW_LINE)) {
			List<String> values = new ArrayList<>();

			for (String line : StringUtil.split(value, CharPool.NEW_LINE)) {
				values.add(_transformURL(baseURL, line));
			}

			return StringUtil.merge(values, StringPool.NEW_LINE);
		}

		if (value.contains(StringPool.COLON)) {
			return value;
		}

		if (!value.isEmpty() && !value.startsWith(StringPool.SLASH)) {
			value = StringPool.SLASH + value;
		}

		return baseURL + value;
	}

	private UnicodeProperties _transformURLs(
		String baseURL, CETImplFactory<?> cetImplFactory,
		UnicodeProperties unicodeProperties) {

		UnicodeProperties transformedUnicodeProperties = new UnicodeProperties(
			true);

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();

			if (cetImplFactory.isURLCETPropertyName(name)) {
				value = HtmlUtil.escapeHREF(_transformURL(baseURL, value));
			}

			transformedUnicodeProperties.put(name, value);
		}

		return transformedUnicodeProperties;
	}

	private Map<String, CETImplFactory> _cetImplFactories;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	private Set<String> _types;

}