/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.model.listener;

import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.headless.data.mask.internal.engine.RedactUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalInstances;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.osgi.service.component.annotations.Component;

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

		_evict(objectEntry);
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		_evict(originalObjectEntry);
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		if (_isBatchEngineBundle()) {
			return;
		}

		if (_isSystem(objectEntry)) {
			throw new ModelListenerException(
				"Unable to create system data masks");
		}

		_validate(objectEntry);
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		if (_isBatchEngineBundle() ||
			PortalInstances.isCurrentCompanyInDeletionProcess()) {

			return;
		}

		if (_isSystem(objectEntry)) {
			throw new ModelListenerException(
				"Unable to delete system data masks");
		}
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		if (_isBatchEngineBundle()) {
			return;
		}

		if (_isSystem(originalObjectEntry)) {
			throw new ModelListenerException(
				"Unable to update system data masks");
		}

		if (_isSystem(objectEntry)) {
			throw new ModelListenerException(
				"Unable to convert data mask to system data mask");
		}

		_validate(objectEntry);
	}

	private void _evict(ObjectEntry objectEntry) {
		Map<String, Serializable> values = objectEntry.getValues();

		RedactUtil.evict(MapUtil.getString(values, "detectionRegex"));
		RedactUtil.evict(MapUtil.getString(values, "replacementRegex"));
	}

	private boolean _isBatchEngineBundle() {
		String fileName = BatchEngineUnitThreadLocal.getFileName();

		return fileName.startsWith("com.liferay.headless.data.mask.impl_");
	}

	private boolean _isSystem(ObjectEntry objectEntry) {
		Map<String, Serializable> values = objectEntry.getValues();

		return Objects.equals(values.get("maskType"), "system");
	}

	private void _validate(ObjectEntry objectEntry)
		throws ModelListenerException {

		Map<String, Serializable> values = objectEntry.getValues();

		_validateRegex("detectionRegex", values);
		_validateRegex("replacementRegex", values);
	}

	private void _validateRegex(String name, Map<String, Serializable> values)
		throws ModelListenerException {

		String regex = MapUtil.getString(values, name);

		if (Validator.isNull(regex)) {
			return;
		}

		try {
			Pattern.compile(regex);
		}
		catch (PatternSyntaxException patternSyntaxException) {
			throw new ModelListenerException(
				StringBundler.concat(
					"Invalid \"", name, "\": ",
					patternSyntaxException.getMessage()));
		}
	}

}