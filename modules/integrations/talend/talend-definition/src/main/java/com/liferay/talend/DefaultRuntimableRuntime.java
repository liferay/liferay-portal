/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend;

import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

/**
 * @author Igor Beslic
 */
public class DefaultRuntimableRuntime implements RuntimableRuntime {

	@Override
	public ValidationResult initialize(
		RuntimeContainer runtimeContainer, Properties properties) {

		if (properties != null) {
			return ValidationResult.OK;
		}

		return new ValidationResult(
			ValidationResult.Result.ERROR, "Properties are required");
	}

}