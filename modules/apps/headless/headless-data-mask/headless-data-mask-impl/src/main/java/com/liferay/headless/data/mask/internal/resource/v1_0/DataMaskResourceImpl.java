/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.resource.v1_0;

import com.liferay.headless.data.mask.dto.v1_0.DataMaskPreviewRequest;
import com.liferay.headless.data.mask.dto.v1_0.DataMaskPreviewResult;
import com.liferay.headless.data.mask.internal.engine.DataMaskEngineUtil;
import com.liferay.headless.data.mask.resource.v1_0.DataMaskResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jose Luis Navarro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/data-mask.properties",
	scope = ServiceScope.PROTOTYPE, service = DataMaskResource.class
)
public class DataMaskResourceImpl extends BaseDataMaskResourceImpl {

	@Override
	public DataMaskPreviewResult postDataMaskPreview(
		DataMaskPreviewRequest dataMaskPreviewRequest) {

		String sampleText = dataMaskPreviewRequest.getSampleText();

		DataMaskPreviewResult dataMaskPreviewResult =
			new DataMaskPreviewResult();

		try {
			String output = DataMaskEngineUtil.redact(
				dataMaskPreviewRequest.getDetectionRegex(),
				dataMaskPreviewRequest.getReplacementRegex(),
				dataMaskPreviewRequest.getReplacementValue(), sampleText);

			dataMaskPreviewResult.setOutput(() -> output);
		}
		catch (RuntimeException runtimeException) {
			dataMaskPreviewResult.setError(runtimeException::getMessage);
			dataMaskPreviewResult.setOutput(() -> sampleText);
		}

		return dataMaskPreviewResult;
	}

}