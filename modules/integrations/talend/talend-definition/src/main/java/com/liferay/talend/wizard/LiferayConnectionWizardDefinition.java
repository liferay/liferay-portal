/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.wizard;

import com.liferay.talend.properties.connection.LiferayConnectionProperties;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.wizard.AbstractComponentWizardDefintion;
import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.WizardImageType;
import org.talend.daikon.definition.DefinitionImageType;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 */
@SuppressWarnings("deprecation")
public class LiferayConnectionWizardDefinition
	extends AbstractComponentWizardDefintion {

	public static final String COMPONENT_WIZARD_NAME =
		"LiferayConnectionWizard";

	@Override
	public ComponentWizard createWizard(
		ComponentProperties componentProperties, String location) {

		if (componentProperties != null) {
			if (!Objects.equals(componentProperties.getName(), "connection")) {
				if (_logger.isDebugEnabled()) {
					_logger.debug(
						"Adjust component properties name to `connection`");
				}

				componentProperties.setName("connection");
			}

			return new LiferayConnectionWizard(
				this, componentProperties, location);
		}

		if (_logger.isInfoEnabled()) {
			_logger.info("Create wizard with own component properties");
		}

		LiferayConnectionProperties liferayConnectionProperties =
			new LiferayConnectionProperties("connection");

		liferayConnectionProperties.init();

		return new LiferayConnectionWizard(
			this, liferayConnectionProperties, location);
	}

	@Override
	public ComponentWizard createWizard(String location) {
		return createWizard(null, location);
	}

	@Override
	public String getIconKey() {
		return null;
	}

	@Override
	public String getImagePath(DefinitionImageType definitionImageType) {
		if (definitionImageType == DefinitionImageType.TREE_ICON_16X16) {
			return "LiferayWizard_icon_16x16.png";
		}
		else if (definitionImageType ==
					DefinitionImageType.WIZARD_BANNER_75X66) {

			return "LiferayWizard_banner_75x66.png";
		}

		return null;
	}

	@Override
	public String getName() {
		return COMPONENT_WIZARD_NAME;
	}

	/**
	 * @see    org.talend.components.api.wizard.ComponentWizardDefinition#getPngImagePath(
	 *         WizardImageType)
	 * @review
	 */
	@Override
	public String getPngImagePath(WizardImageType wizardImageType) {
		if (wizardImageType == WizardImageType.TREE_ICON_16X16) {
			return getImagePath(DefinitionImageType.TREE_ICON_16X16);
		}
		else if (wizardImageType == WizardImageType.WIZARD_BANNER_75X66) {
			return getImagePath(DefinitionImageType.WIZARD_BANNER_75X66);
		}

		return null;
	}

	/**
	 * PropertiesTestUtils reports this as an error, but it is not. See
	 * https://github.com/Talend/daikon/blob/98c09e0a25ff59a8d361c171e0ad1c1866176bc5/daikon/src/test/java/org/talend/daikon/properties/test/PropertiesTestUtils.java#L119-L121
	 *
	 * @review
	 */
	@Override
	public Class getPropertiesClass() {
		return null;
	}

	@Override
	public boolean isTopLevel() {
		return true;
	}

	@Override
	public boolean supportsProperties(
		Class<? extends ComponentProperties> propertiesClass) {

		return propertiesClass.isAssignableFrom(
			LiferayConnectionProperties.class);
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferayConnectionWizardDefinition.class);

}