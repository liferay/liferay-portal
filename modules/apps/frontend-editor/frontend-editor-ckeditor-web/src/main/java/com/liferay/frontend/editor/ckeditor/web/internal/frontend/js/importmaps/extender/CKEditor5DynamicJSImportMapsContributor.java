/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.web.internal.frontend.js.importmaps.extender;

import com.liferay.frontend.js.importmaps.extender.DynamicJSImportMapsContributor;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.ESModuleAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Writer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Čikoš
 */
@Component(service = DynamicJSImportMapsContributor.class)
public class CKEditor5DynamicJSImportMapsContributor
	implements DynamicJSImportMapsContributor {

	@Override
	public void writeGlobalImports(
			HttpServletRequest httpServletRequest, Writer writer)
		throws IOException {

		boolean first = true;

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		for (String moduleName : _MODULE_NAMES) {
			if (!first) {
				writer.write(", ");
			}
			else {
				first = false;
			}

			writer.write(StringPool.QUOTE);
			writer.write(moduleName);
			writer.write("\": \"");

			String escapedModuleName = StringUtil.replace(
				moduleName, CharPool.FORWARD_SLASH, CharPool.DOLLAR);

			ESModuleAbsolutePortalURLBuilder esModuleAbsolutePortalURLBuilder =
				absolutePortalURLBuilder.forESModule(
					"frontend-editor-ckeditor-web",
					"exports/" + escapedModuleName + ".js");

			writer.write(esModuleAbsolutePortalURLBuilder.build());

			writer.write(StringPool.QUOTE);
		}
	}

	@Override
	public void writeScopedImports(
		HttpServletRequest httpServletRequest, Writer writer) {
	}

	private static final String[] _MODULE_NAMES = {
		"@ckeditor/ckeditor5-adapter-ckfinder", "@ckeditor/ckeditor5-alignment",
		"@ckeditor/ckeditor5-autoformat", "@ckeditor/ckeditor5-autosave",
		"@ckeditor/ckeditor5-basic-styles", "@ckeditor/ckeditor5-block-quote",
		"@ckeditor/ckeditor5-bookmark", "@ckeditor/ckeditor5-case-change",
		"@ckeditor/ckeditor5-ckbox", "@ckeditor/ckeditor5-ckfinder",
		"@ckeditor/ckeditor5-clipboard", "@ckeditor/ckeditor5-cloud-services",
		"@ckeditor/ckeditor5-code-block", "@ckeditor/ckeditor5-core",
		"@ckeditor/ckeditor5-document-outline",
		"@ckeditor/ckeditor5-easy-image", "@ckeditor/ckeditor5-editor-balloon",
		"@ckeditor/ckeditor5-editor-classic",
		"@ckeditor/ckeditor5-editor-decoupled",
		"@ckeditor/ckeditor5-editor-inline",
		"@ckeditor/ckeditor5-editor-multi-root", "@ckeditor/ckeditor5-email",
		"@ckeditor/ckeditor5-emoji", "@ckeditor/ckeditor5-engine",
		"@ckeditor/ckeditor5-enter", "@ckeditor/ckeditor5-essentials",
		"@ckeditor/ckeditor5-export-inline-styles",
		"@ckeditor/ckeditor5-find-and-replace", "@ckeditor/ckeditor5-font",
		"@ckeditor/ckeditor5-format-painter", "@ckeditor/ckeditor5-fullscreen",
		"@ckeditor/ckeditor5-heading", "@ckeditor/ckeditor5-highlight",
		"@ckeditor/ckeditor5-horizontal-line", "@ckeditor/ckeditor5-html-embed",
		"@ckeditor/ckeditor5-html-support", "@ckeditor/ckeditor5-icons",
		"@ckeditor/ckeditor5-image", "@ckeditor/ckeditor5-indent",
		"@ckeditor/ckeditor5-language", "@ckeditor/ckeditor5-line-height",
		"@ckeditor/ckeditor5-link", "@ckeditor/ckeditor5-list",
		"@ckeditor/ckeditor5-markdown-gfm", "@ckeditor/ckeditor5-media-embed",
		"@ckeditor/ckeditor5-mention", "@ckeditor/ckeditor5-merge-fields",
		"@ckeditor/ckeditor5-minimap", "@ckeditor/ckeditor5-page-break",
		"@ckeditor/ckeditor5-paragraph",
		"@ckeditor/ckeditor5-paste-from-office",
		"@ckeditor/ckeditor5-paste-from-office-enhanced",
		"@ckeditor/ckeditor5-react", "@ckeditor/ckeditor5-remove-format",
		"@ckeditor/ckeditor5-restricted-editing",
		"@ckeditor/ckeditor5-select-all", "@ckeditor/ckeditor5-show-blocks",
		"@ckeditor/ckeditor5-slash-command",
		"@ckeditor/ckeditor5-source-editing",
		"@ckeditor/ckeditor5-source-editing-enhanced",
		"@ckeditor/ckeditor5-special-characters", "@ckeditor/ckeditor5-style",
		"@ckeditor/ckeditor5-table", "@ckeditor/ckeditor5-template",
		"@ckeditor/ckeditor5-typing", "@ckeditor/ckeditor5-ui",
		"@ckeditor/ckeditor5-undo", "@ckeditor/ckeditor5-upload",
		"@ckeditor/ckeditor5-utils", "@ckeditor/ckeditor5-widget",
		"@ckeditor/ckeditor5-word-count", "@codemirror/autocomplete",
		"@codemirror/commands", "@codemirror/lang-html",
		"@codemirror/lang-markdown", "@codemirror/language",
		"@codemirror/state", "@codemirror/theme-one-dark", "@codemirror/view",
		"frontend-editor-ckeditor-web/plugins/DocumentLinkSelector"
	};

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

}