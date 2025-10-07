/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime;

import com.liferay.talend.properties.batch.LiferayBatchFileProperties;
import com.liferay.talend.properties.batch.LiferayBatchOutputProperties;
import com.liferay.talend.runtime.client.LiferayClient;
import com.liferay.talend.runtime.reader.LiferayBatchFileReader;
import com.liferay.talend.tliferaybatchfile.TLiferayBatchFileDefinition;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.apache.avro.Schema;

import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

/**
 * @author Igor Beslic
 */
public class LiferayBatchOutputSink
	extends LiferaySourceOrSink implements Source {

	@Override
	public Reader createReader(RuntimeContainer container) {
		return new LiferayBatchFileReader(this);
	}

	public String getBatchFilePath() {
		return _batchFilePath;
	}

	@Override
	public Schema getEndpointSchema(
		RuntimeContainer runtimeContainer, String schemaName) {

		return null;
	}

	@Override
	public List<NamedThing> getSchemaNames(RuntimeContainer runtimeContainer) {
		return Collections.emptyList();
	}

	@Override
	public ValidationResult initialize(
		RuntimeContainer runtimeContainer,
		ComponentProperties componentProperties) {

		super.initialize(runtimeContainer, componentProperties);

		ValidationResult validationResult = _initializeBatchFile(
			componentProperties);

		if (validationResult.getStatus() == ValidationResult.Result.ERROR) {
			return validationResult;
		}

		return ValidationResult.OK;
	}

	public void submit(File batchFile) {
		LiferayClient liferayClient = getLiferayClient();

		liferayClient.executePostRequest(_getBatchOutputURL(), batchFile);
	}

	@Override
	public ValidationResult validate(RuntimeContainer runtimeContainer) {
		return ValidationResult.OK;
	}

	@Override
	protected String getLiferayConnectionPropertiesPath() {
		return "liferayBatchFileProperties.resource." +
			super.getLiferayConnectionPropertiesPath();
	}

	private String _getBatchOutputURL() {
		StringBuilder sb = new StringBuilder();

		sb.append("/o/headless-batch-engine/v1.0/import-task/");
		sb.append(_entityClass);

		return sb.toString();
	}

	private ValidationResult _initializeBatchFile(
		ComponentProperties componentProperties) {

		if (!(componentProperties instanceof LiferayBatchOutputProperties)) {
			return new ValidationResult(
				ValidationResult.Result.ERROR,
				String.format(
					"Unable to initialize %s with %s", getClass(),
					componentProperties));
		}

		LiferayBatchOutputProperties liferayBatchOutputProperties =
			(LiferayBatchOutputProperties)componentProperties;

		LiferayBatchFileProperties liferayBatchFileProperties =
			liferayBatchOutputProperties.
				getEffectiveLiferayBatchFileProperties();

		if (liferayBatchFileProperties == null) {
			return new ValidationResult(
				ValidationResult.Result.ERROR,
				String.format(
					"Unable to initialize %s without properly selected %s",
					getClass(), TLiferayBatchFileDefinition.COMPONENT_NAME));
		}

		_batchFilePath = liferayBatchFileProperties.getBatchFilePath();
		_entityClass = liferayBatchFileProperties.getEntityClassName();

		return ValidationResult.OK;
	}

	private transient String _batchFilePath;
	private transient String _entityClass;

}