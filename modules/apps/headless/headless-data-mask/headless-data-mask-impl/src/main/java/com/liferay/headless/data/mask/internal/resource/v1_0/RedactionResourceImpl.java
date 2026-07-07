/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.resource.v1_0;

import com.liferay.headless.data.mask.dto.v1_0.Redaction;
import com.liferay.headless.data.mask.internal.engine.RedactUtil;
import com.liferay.headless.data.mask.resource.v1_0.RedactionResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jose Luis Navarro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/redaction.properties",
	scope = ServiceScope.PROTOTYPE, service = RedactionResource.class
)
public class RedactionResourceImpl extends BaseRedactionResourceImpl {

	@Override
	public Redaction getRedaction(
		String detectionRegex, String replacementRegex, String replacementValue,
		String text) {

		Redaction redaction = new Redaction();

		try {
			String output = RedactUtil.redact(
				detectionRegex, replacementRegex, replacementValue, text);

			redaction.setOutput(() -> output);
		}
		catch (RuntimeException runtimeException) {
			redaction.setError(runtimeException::getMessage);
			redaction.setOutput(() -> text);
		}

		return redaction;
	}

}