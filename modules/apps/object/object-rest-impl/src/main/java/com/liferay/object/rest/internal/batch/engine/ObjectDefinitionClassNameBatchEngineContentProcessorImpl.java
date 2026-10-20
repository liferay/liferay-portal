/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.batch.engine;

import com.liferay.batch.engine.BatchEngineContentProcessor;
import com.liferay.object.definition.processor.ObjectDefinitionClassNameProcessor;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = BatchEngineContentProcessor.class)
public class ObjectDefinitionClassNameBatchEngineContentProcessorImpl
	implements BatchEngineContentProcessor {

	@Override
	public String process(
		String content, Map<String, Object> fieldNameValueMap) {

		return _objectDefinitionClassNameProcessor.
			updateObjectDefinitionReferences(content);
	}

	@Reference
	private ObjectDefinitionClassNameProcessor
		_objectDefinitionClassNameProcessor;

}