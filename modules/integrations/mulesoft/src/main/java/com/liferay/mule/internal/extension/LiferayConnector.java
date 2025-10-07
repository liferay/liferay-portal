/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.extension;

import com.liferay.mule.internal.config.LiferayConfig;
import com.liferay.mule.internal.error.LiferayError;

import org.mule.runtime.api.meta.Category;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.license.RequiresEnterpriseLicense;

/**
 * @author Matija Petanjek
 */
@Configurations(LiferayConfig.class)
@ErrorTypes(LiferayError.class)
@Extension(category = Category.CERTIFIED, name = "Liferay", vendor = "Liferay")
@RequiresEnterpriseLicense(allowEvaluationLicense = true)
@Xml(prefix = "liferay")
public class LiferayConnector {
}