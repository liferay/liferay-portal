/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0.util;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.rest.dto.v1_0.ElementDefinition;
import com.liferay.search.experiences.rest.dto.v1_0.ElementInstance;
import com.liferay.search.experiences.rest.dto.v1_0.SXPElement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author André de Oliveira
 */
public class ElementInstanceUtil {

	public static ElementInstance toElementInstance(String json) {
		return unpack(ElementInstance.unsafeToDTO(json));
	}

	public static ElementInstance[] toElementInstances(String json) {
		if (Validator.isNull(json)) {
			return null;
		}

		try {
			return JSONUtil.toArray(
				JSONFactoryUtil.createJSONArray(json),
				jsonObject -> toElementInstance(jsonObject.toString()),
				ElementInstance.class);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static ElementInstance unpack(ElementInstance elementInstance) {
		if (elementInstance == null) {
			return null;
		}

		Object customSXPElement = null;

		Map<String, Object> uiConfigurationValues =
			elementInstance.getUiConfigurationValues();

		if (MapUtil.isNotEmpty(uiConfigurationValues)) {
			customSXPElement = uiConfigurationValues.get("sxpElement");
		}

		if (customSXPElement instanceof String) {
			SXPElement sxpElement = SXPElementUtil.toSXPElement(
				(String)customSXPElement);

			elementInstance.setSxpElement(() -> sxpElement);

			ElementDefinition elementDefinition =
				sxpElement.getElementDefinition();

			if (elementDefinition != null) {
				elementInstance.setConfigurationEntry(
					elementDefinition::getConfiguration);
			}
		}
		else {
			SXPElement sxpElement = elementInstance.getSxpElement();

			if (sxpElement != null) {
				elementInstance.setSxpElement(
					() -> SXPElementUtil.unpack(sxpElement));
			}
		}

		ConfigurationUtil.unpack(elementInstance.getConfigurationEntry());

		if (MapUtil.isNotEmpty(uiConfigurationValues)) {
			Map<String, Object> unpackedUiConfigurationValues = new HashMap<>(
				uiConfigurationValues);

			unpackedUiConfigurationValues.forEach(
				(name, value) -> uiConfigurationValues.put(
					name, UnpackUtil.unpack(value)));
		}

		return elementInstance;
	}

	public static ElementInstance[] unpack(ElementInstance[] elementInstances) {
		for (int i = 0; i < elementInstances.length; i++) {
			elementInstances[i] = unpack(elementInstances[i]);
		}

		return elementInstances;
	}

}