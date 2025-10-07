/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.oas;

import javax.json.JsonObject;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.ValidationResult;

/**
 * @author Igor Beslic
 */
public interface OASSource {

	public JsonObject getOASJsonObject();

	public JsonObject getOASJsonObject(String oasUrl);

	public ValidationResult initialize(ComponentProperties componentProperties);

}