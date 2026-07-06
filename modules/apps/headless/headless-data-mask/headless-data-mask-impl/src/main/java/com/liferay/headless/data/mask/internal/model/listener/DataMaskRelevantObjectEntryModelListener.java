/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.model.listener;

import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.headless.data.mask.engine.DataMaskEngine;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class DataMaskRelevantObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return "L_DATA_MASK";
	}

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		_evictPatterns(objectEntry.getValues());
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		_evictPatterns(originalObjectEntry.getValues());
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		if (_isDataMaskSeedImport()) {
			return;
		}

		if (_isSystemMask(objectEntry.getValues())) {
			throw new ModelListenerException(
				new PrincipalException("Unable to create system data masks"));
		}

		_validateRegexes(objectEntry.getValues());
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		if (_isDataMaskSeedImport()) {
			return;
		}

		if (_isSystemMask(objectEntry.getValues())) {
			throw new ModelListenerException(
				new PrincipalException("Unable to delete system data masks"));
		}
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		if (_isDataMaskSeedImport()) {
			return;
		}

		if (_isSystemMask(originalObjectEntry.getValues())) {
			throw new ModelListenerException(
				new PrincipalException("Unable to update system data masks"));
		}

		if (_isSystemMask(objectEntry.getValues())) {
			throw new ModelListenerException(
				new PrincipalException(
					"Unable to convert data masks to system data masks"));
		}

		_validateRegexes(objectEntry.getValues());
	}

	private void _evictPatterns(Map<String, Serializable> values) {
		_dataMaskEngine.evictPattern(
			MapUtil.getString(values, "detectionRegex"));
		_dataMaskEngine.evictPattern(
			MapUtil.getString(values, "replacementRegex"));
	}

	private boolean _isDataMaskSeedImport() {
		String fileName = BatchEngineUnitThreadLocal.getFileName();

		return fileName.startsWith("com.liferay.headless.data.mask.impl_");
	}

	private boolean _isSystemMask(Map<String, Serializable> values) {
		return Objects.equals(values.get("maskType"), "system");
	}

	private void _validateRegex(String fieldLabel, String regex)
		throws ModelListenerException {

		try {
			Pattern.compile(regex);
		}
		catch (PatternSyntaxException patternSyntaxException) {
			throw new ModelListenerException(
				StringBundler.concat(
					"Invalid \"", fieldLabel, "\": ",
					patternSyntaxException.getMessage()));
		}
	}

	private void _validateRegexes(Map<String, Serializable> values)
		throws ModelListenerException {

		String detectionRegex = MapUtil.getString(values, "detectionRegex");

		if (Validator.isNotNull(detectionRegex)) {
			_validateRegex("detectionRegex", detectionRegex);
		}

		String replacementRegex = MapUtil.getString(values, "replacementRegex");

		if (Validator.isNotNull(replacementRegex)) {
			_validateRegex("replacementRegex", replacementRegex);
		}
	}

	@Reference
	private DataMaskEngine _dataMaskEngine;

}