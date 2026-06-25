/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.smoke.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Lance Ji
 */
@RunWith(Arquillian.class)
public class ModuleConfigurationLocalizationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testConfigurationLocalization() {
		StringBundler sb = new StringBundler();

		Bundle currentBundle = FrameworkUtil.getBundle(
			ModuleConfigurationLocalizationTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			String bundleError = _collectBundleError(bundle);

			if (bundleError.isEmpty()) {
				continue;
			}

			sb.append("\nBundle {id: ");
			sb.append(bundle.getBundleId());
			sb.append(", name: ");
			sb.append(bundle.getSymbolicName());
			sb.append(", version: ");
			sb.append(bundle.getVersion());
			sb.append(StringPool.CLOSE_CURLY_BRACE);
			sb.append(bundleError);
			sb.append(StringPool.NEW_LINE);
		}

		String message = sb.toString();

		Assert.assertTrue("Test failed due to: " + message, message.isEmpty());
	}

	private String _collectBundleError(Bundle bundle) {
		ExtendedMetaTypeInformation extendedMetaTypeInformation =
			_extendedMetaTypeService.getMetaTypeInformation(bundle);

		List<String> pids = new ArrayList<>();

		Collections.addAll(pids, extendedMetaTypeInformation.getFactoryPids());
		Collections.addAll(pids, extendedMetaTypeInformation.getPids());

		String bundleName = bundle.getSymbolicName();

		if (pids.isEmpty() || !bundleName.startsWith("com.liferay")) {
			return StringPool.BLANK;
		}

		Locale locale = LocaleUtil.getDefault();

		Iterator<String> iterator = pids.iterator();

		while (iterator.hasNext()) {
			ExtendedObjectClassDefinition extendedObjectClassDefinition =
				extendedMetaTypeInformation.getObjectClassDefinition(
					iterator.next(), locale.getLanguage());

			if (!_isGenerateUI(extendedObjectClassDefinition)) {
				iterator.remove();
			}
		}

		if (pids.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		ResourceBundleLoader resourceBundleLoader =
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName(
					bundle.getSymbolicName());

		if (resourceBundleLoader == null) {
			resourceBundleLoader =
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
		}
		else {
			resourceBundleLoader = new AggregateResourceBundleLoader(
				resourceBundleLoader,
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader());
		}

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			LocaleUtil.getDefault());

		for (String pid : pids) {
			if (!pid.startsWith("com.liferay")) {
				continue;
			}

			String configurationError = _collectConfigurationError(
				pid, extendedMetaTypeInformation, resourceBundle);

			if (configurationError.isEmpty()) {
				continue;
			}

			sb.append("\n\tConfiguration {pid:");
			sb.append(pid);
			sb.append(StringPool.CLOSE_CURLY_BRACE);
			sb.append(configurationError);
		}

		return sb.toString();
	}

	private String _collectConfigurationError(
		String pid, ExtendedMetaTypeInformation extendedMetaTypeInformation,
		ResourceBundle resourceBundle) {

		Locale locale = LocaleUtil.getDefault();

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			extendedMetaTypeInformation.getObjectClassDefinition(
				pid, locale.getLanguage());

		StringBundler sb = new StringBundler();

		String extendedObjectClassDefinitionName = ResourceBundleUtil.getString(
			resourceBundle, extendedObjectClassDefinition.getName());

		if (extendedObjectClassDefinitionName == null) {
			sb.append("\n\t\tMissing localization for configuration pid: ");
			sb.append(extendedObjectClassDefinition.getID());
		}

		List<String> missingAttributeNames = new ArrayList<>();

		for (ExtendedAttributeDefinition extendedAttributeDefinition :
				extendedObjectClassDefinition.getAttributeDefinitions(
					ExtendedObjectClassDefinition.ALL)) {

			String extendedAttributeDefinitionName =
				ResourceBundleUtil.getString(
					resourceBundle, extendedAttributeDefinition.getName());

			if (extendedAttributeDefinitionName == null) {
				missingAttributeNames.add(extendedAttributeDefinition.getID());
			}
		}

		if (!missingAttributeNames.isEmpty()) {
			sb.append("\n\t\tMissing localization for attributes: ");

			for (String attributeName : missingAttributeNames) {
				sb.append(attributeName);
				sb.append(StringPool.COMMA);
			}

			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private boolean _isGenerateUI(
		ExtendedObjectClassDefinition extendedObjectClassDefinition) {

		for (String extensionUri :
				extendedObjectClassDefinition.getExtensionUris()) {

			Map<String, String> extensionAttributes =
				extendedObjectClassDefinition.getExtensionAttributes(
					extensionUri);

			boolean generateUI = GetterUtil.getBoolean(
				extensionAttributes.get("generateUI"), true);

			if (!generateUI) {
				return false;
			}
		}

		return true;
	}

	@Inject
	private ExtendedMetaTypeService _extendedMetaTypeService;

}