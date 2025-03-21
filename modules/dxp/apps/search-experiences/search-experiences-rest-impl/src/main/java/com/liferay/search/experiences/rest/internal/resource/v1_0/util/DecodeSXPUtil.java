/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.resource.v1_0.util;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.search.experiences.rest.dto.v1_0.ElementDefinition;
import com.liferay.search.experiences.rest.dto.v1_0.ElementInstance;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.SXPElement;

import jakarta.validation.Valid;

import java.net.URLDecoder;

/**
 * @author Gustavo Lima
 */
public class DecodeSXPUtil {

	public static void decodeSXPBlueprint(SXPBlueprint sxpBlueprint)
		throws Exception {

		@Valid
		ElementInstance[] elementInstances = sxpBlueprint.getElementInstances();

		if (ArrayUtil.isEmpty(elementInstances)) {
			return;
		}

		for (ElementInstance elementInstance : elementInstances) {
			decodeSXPElement(elementInstance.getSxpElement());
		}
	}

	public static void decodeSXPElement(SXPElement sxpElement)
		throws Exception {

		ElementDefinition elementDefinition = sxpElement.getElementDefinition();

		sxpElement.setElementDefinition(
			() -> ElementDefinition.toDTO(
				URLDecoder.decode(String.valueOf(elementDefinition), "UTF-8")));
	}

}