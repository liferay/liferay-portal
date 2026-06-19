/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.engine;

import com.liferay.headless.data.masking.engine.DataMaskingEngine;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(service = DataMaskingEngine.class)
public class DataMaskingEngineImpl implements DataMaskingEngine {

	@Override
	public void evictPattern(String regex) {
		if (regex != null) {
			_patterns.remove(regex);
		}
	}

	@Override
	public String redact(
		long companyId, List<String> maskExternalReferenceCodes, String text) {

		if (Validator.isNull(text) ||
			ListUtil.isEmpty(maskExternalReferenceCodes) ||
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-90204")) {

			return text;
		}

		ObjectDefinition maskObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", companyId);

		if (maskObjectDefinition == null) {
			return text;
		}

		List<DataMask> dataMasks = new ArrayList<>();

		for (String externalReferenceCode : maskExternalReferenceCodes) {
			if (Validator.isNull(externalReferenceCode)) {
				continue;
			}

			ObjectEntry maskObjectEntry =
				_objectEntryLocalService.fetchObjectEntry(
					externalReferenceCode, 0,
					maskObjectDefinition.getObjectDefinitionId());

			if (maskObjectEntry == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"No data mask was resolved for external reference ",
							"code \"", externalReferenceCode, "\""));
				}

				continue;
			}

			DataMask dataMask = _buildDataMask(maskObjectEntry);

			if (dataMask != null) {
				dataMasks.add(dataMask);
			}
		}

		if (ListUtil.isEmpty(dataMasks)) {
			return text;
		}

		String redactedText = text;

		for (DataMask dataMask : dataMasks) {
			try {
				redactedText = dataMask.apply(redactedText);
			}
			catch (RuntimeException runtimeException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to apply data mask \"", dataMask.getName(),
							"\" due to runtime error: ",
							runtimeException.getMessage()));
				}
			}
		}

		return redactedText;
	}

	private DataMask _buildDataMask(ObjectEntry maskObjectEntry) {
		Map<String, Serializable> values = maskObjectEntry.getValues();

		String detectionRegex = (String)values.get("detectionRegex");
		String name = (String)values.get("name");
		String replacementRegex = (String)values.get("replacementRegex");
		String replacementValue = (String)values.get("replacementValue");

		if (Validator.isNull(detectionRegex) ||
			Validator.isNull(replacementValue)) {

			return null;
		}

		try {
			Pattern detectionPattern = _getPattern(detectionRegex);

			Pattern replacementPattern = null;

			if (Validator.isNotNull(replacementRegex)) {
				replacementPattern = _getPattern(replacementRegex);
			}

			return new DataMask(
				detectionPattern, name, replacementPattern, replacementValue);
		}
		catch (PatternSyntaxException patternSyntaxException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to compile data mask \"", name,
						"\" due to invalid regex pattern: ",
						patternSyntaxException.getMessage()));
			}

			return null;
		}
	}

	private Pattern _getPattern(String regex) {
		return _patterns.computeIfAbsent(regex, Pattern::compile);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataMaskingEngineImpl.class);

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	private final Map<String, Pattern> _patterns = new ConcurrentHashMap<>();

}