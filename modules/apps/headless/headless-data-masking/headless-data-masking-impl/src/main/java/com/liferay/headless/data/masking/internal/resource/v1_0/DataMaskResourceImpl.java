/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.resource.v1_0;

import com.liferay.headless.data.masking.dto.v1_0.DataMaskPreviewRequest;
import com.liferay.headless.data.masking.dto.v1_0.DataMaskPreviewResult;
import com.liferay.headless.data.masking.internal.engine.DataMask;
import com.liferay.headless.data.masking.resource.v1_0.DataMaskResource;
import com.liferay.portal.kernel.util.Validator;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

		DataMaskPreviewResult dataMaskPreviewResult =
			new DataMaskPreviewResult();

		String sampleText = dataMaskPreviewRequest.getSampleText();

		dataMaskPreviewResult.setOutput(() -> sampleText);

		String detectionRegex = dataMaskPreviewRequest.getDetectionRegex();
		String replacementValue = dataMaskPreviewRequest.getReplacementValue();

		if (Validator.isNull(detectionRegex) ||
			Validator.isNull(replacementValue) ||
			Validator.isNull(sampleText)) {

			dataMaskPreviewResult.setError(
				() ->
					"\"detectionRegex\", \"replacementValue\", and " +
						"\"sampleText\" are required");

			return dataMaskPreviewResult;
		}

		try {
			Pattern detectionPattern = Pattern.compile(detectionRegex);

			Pattern replacementPattern = _getReplacementPattern(
				dataMaskPreviewRequest);

			dataMaskPreviewResult.setOutput(
				() -> {
					DataMask dataMask = new DataMask(
						detectionPattern, "preview", replacementPattern,
						replacementValue);

					return dataMask.apply(sampleText);
				});
		}
		catch (PatternSyntaxException patternSyntaxException) {
			dataMaskPreviewResult.setError(patternSyntaxException::getMessage);
		}
		catch (RuntimeException runtimeException) {
			dataMaskPreviewResult.setError(runtimeException::getMessage);
		}

		return dataMaskPreviewResult;
	}

	private Pattern _getReplacementPattern(
		DataMaskPreviewRequest dataMaskPreviewRequest) {

		String replacementRegex = dataMaskPreviewRequest.getReplacementRegex();

		if (Validator.isNull(replacementRegex)) {
			return null;
		}

		return Pattern.compile(replacementRegex);
	}

}