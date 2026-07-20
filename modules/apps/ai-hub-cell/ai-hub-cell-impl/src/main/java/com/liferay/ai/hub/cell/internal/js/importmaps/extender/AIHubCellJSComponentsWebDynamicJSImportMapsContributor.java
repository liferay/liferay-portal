/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.internal.js.importmaps.extender;

import com.liferay.frontend.js.importmaps.extender.DynamicJSImportMapsContributor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.ESModuleAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Writer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = DynamicJSImportMapsContributor.class)
public class AIHubCellJSComponentsWebDynamicJSImportMapsContributor
	implements DynamicJSImportMapsContributor {

	@Override
	public void writeGlobalImports(
			HttpServletRequest httpServletRequest, Writer writer)
		throws IOException {

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		writer.write("\"@liferay/ai-hub-cell-js-components-web\": \"");

		ESModuleAbsolutePortalURLBuilder esModuleAbsolutePortalURLBuilder =
			absolutePortalURLBuilder.forESModule(
				"ai-hub-cell-js-components-web", "index.js");

		writer.write(esModuleAbsolutePortalURLBuilder.build());

		writer.write("\", ");

		writer.write(
			"\"@liferay/ai-hub-cell-js-components-web" +
				"/renderAIAssistantChat\": \"");

		esModuleAbsolutePortalURLBuilder = absolutePortalURLBuilder.forESModule(
			"ai-hub-cell-js-components-web", "renderAIAssistantChat.js");

		writer.write(esModuleAbsolutePortalURLBuilder.build());

		writer.write(StringPool.QUOTE);
	}

	@Override
	public void writeScopedImports(
		HttpServletRequest httpServletRequest, Writer writer) {
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

}