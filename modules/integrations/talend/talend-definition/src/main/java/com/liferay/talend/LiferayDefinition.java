/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend;

import com.liferay.talend.common.oas.OASSource;
import com.liferay.talend.internal.oas.LiferayOASSource;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.runtime.RuntimeUtil;
import org.talend.daikon.sandbox.SandboxedInstance;

/**
 * @author Igor Beslic
 */
public abstract class LiferayDefinition extends AbstractComponentDefinition {

	public static RuntimeInfo getCommonRuntimeInfo(String className) {
		return new JarRuntimeInfo(
			_MAVEN_RUNTIME_URI,
			DependenciesReader.computeDependenciesFilePath(
				_MAVEN_GROUP_ID, _MAVEN_RUNTIME_ARTIFACT_ID),
			className);
	}

	public static LiferayOASSource getLiferayOASSource(
		ComponentProperties componentProperties) {

		try (SandboxedInstance sandboxedInstance = _getSandboxedInstance(
				SOURCE_OR_SINK_CLASS_NAME, false)) {

			OASSource oasSource = (OASSource)sandboxedInstance.getInstance();

			return new LiferayOASSource(
				oasSource, oasSource.initialize(componentProperties));
		}
		catch (Exception exception) {
			return new LiferayOASSource(
				null,
				new ValidationResult(
					ValidationResult.Result.ERROR, exception.getMessage()));
		}
	}

	public LiferayDefinition(
		String componentName, ExecutionEngine primaryExecutionEngine,
		ExecutionEngine... secondaryExecutionEngines) {

		super(componentName, primaryExecutionEngine, secondaryExecutionEngines);
	}

	@Override
	public String[] getFamilies() {
		return new String[] {"Business/Liferay"};
	}

	protected static final String BATCH_FILE_SINK_CLASS_NAME =
		"com.liferay.talend.runtime.LiferayBatchFileSink";

	protected static final String BATCH_OUTPUT_SINK_CLASS_NAME =
		"com.liferay.talend.runtime.LiferayBatchOutputSink";

	protected static final String SINK_CLASS_NAME =
		"com.liferay.talend.runtime.LiferaySink";

	protected static final String SOURCE_CLASS_NAME =
		"com.liferay.talend.runtime.LiferaySource";

	protected static final String SOURCE_OR_SINK_CLASS_NAME =
		"com.liferay.talend.runtime.LiferaySourceOrSink";

	private static SandboxedInstance _getSandboxedInstance(
		String runtimeClassName, boolean useCurrentJvmProperties) {

		Class<LiferayDefinition> liferayDefinitionClass =
			LiferayDefinition.class;

		if (useCurrentJvmProperties) {
			return RuntimeUtil.createRuntimeClassWithCurrentJVMProperties(
				getCommonRuntimeInfo(runtimeClassName),
				liferayDefinitionClass.getClassLoader());
		}

		return RuntimeUtil.createRuntimeClass(
			getCommonRuntimeInfo(runtimeClassName),
			liferayDefinitionClass.getClassLoader());
	}

	private static final String _MAVEN_GROUP_ID = "com.liferay";

	private static final String _MAVEN_RUNTIME_ARTIFACT_ID =
		"com.liferay.talend.runtime";

	private static final String _MAVEN_RUNTIME_URI =
		"mvn:" + _MAVEN_GROUP_ID + "/" + _MAVEN_RUNTIME_ARTIFACT_ID;

}