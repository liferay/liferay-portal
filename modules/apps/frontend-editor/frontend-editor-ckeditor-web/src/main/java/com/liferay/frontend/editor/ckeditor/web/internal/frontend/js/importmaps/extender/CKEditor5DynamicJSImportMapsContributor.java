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
		"@ckeditor/ckeditor5-adapter-ckfinder/dist/index.js",
		"@ckeditor/ckeditor5-alignment/dist/index.js",
		"@ckeditor/ckeditor5-autoformat/dist/index.js",
		"@ckeditor/ckeditor5-autosave/dist/index.js",
		"@ckeditor/ckeditor5-basic-styles/dist/index.js",
		"@ckeditor/ckeditor5-block-quote/dist/index.js",
		"@ckeditor/ckeditor5-bookmark/dist/index.js",
		"@ckeditor/ckeditor5-case-change/dist/index.js",
		"@ckeditor/ckeditor5-ckbox/dist/index.js",
		"@ckeditor/ckeditor5-ckfinder/dist/index.js",
		"@ckeditor/ckeditor5-clipboard/dist/index.js",
		"@ckeditor/ckeditor5-cloud-services/dist/index.js",
		"@ckeditor/ckeditor5-code-block/dist/index.js",
		"@ckeditor/ckeditor5-core/dist/index.js",
		"@ckeditor/ckeditor5-document-outline/dist/index.js",
		"@ckeditor/ckeditor5-easy-image/dist/index.js",
		"@ckeditor/ckeditor5-editor-balloon/dist/index.js",
		"@ckeditor/ckeditor5-editor-classic/dist/index.js",
		"@ckeditor/ckeditor5-editor-decoupled/dist/index.js",
		"@ckeditor/ckeditor5-editor-inline/dist/index.js",
		"@ckeditor/ckeditor5-editor-multi-root/dist/index.js",
		"@ckeditor/ckeditor5-email/dist/index.js",
		"@ckeditor/ckeditor5-emoji/dist/index.js",
		"@ckeditor/ckeditor5-engine/dist/index.js",
		"@ckeditor/ckeditor5-enter/dist/index.js",
		"@ckeditor/ckeditor5-essentials/dist/index.js",
		"@ckeditor/ckeditor5-export-inline-styles/dist/index.js",
		"@ckeditor/ckeditor5-find-and-replace/dist/index.js",
		"@ckeditor/ckeditor5-font/dist/index.js",
		"@ckeditor/ckeditor5-format-painter/dist/index.js",
		"@ckeditor/ckeditor5-fullscreen/dist/index.js",
		"@ckeditor/ckeditor5-heading/dist/index.js",
		"@ckeditor/ckeditor5-highlight/dist/index.js",
		"@ckeditor/ckeditor5-horizontal-line/dist/index.js",
		"@ckeditor/ckeditor5-html-embed/dist/index.js",
		"@ckeditor/ckeditor5-html-support/dist/index.js",
		"@ckeditor/ckeditor5-icons/dist/index.js",
		"@ckeditor/ckeditor5-image/dist/index.js",
		"@ckeditor/ckeditor5-indent/dist/index.js",
		"@ckeditor/ckeditor5-language/dist/index.js",
		"@ckeditor/ckeditor5-line-height/dist/index.js",
		"@ckeditor/ckeditor5-link/dist/index.js",
		"@ckeditor/ckeditor5-list/dist/index.js",
		"@ckeditor/ckeditor5-markdown-gfm/dist/index.js",
		"@ckeditor/ckeditor5-media-embed/dist/index.js",
		"@ckeditor/ckeditor5-mention/dist/index.js",
		"@ckeditor/ckeditor5-merge-fields/dist/index.js",
		"@ckeditor/ckeditor5-minimap/dist/index.js",
		"@ckeditor/ckeditor5-page-break/dist/index.js",
		"@ckeditor/ckeditor5-paragraph/dist/index.js",
		"@ckeditor/ckeditor5-paste-from-office/dist/index.js",
		"@ckeditor/ckeditor5-paste-from-office-enhanced/dist/index.js",
		"@ckeditor/ckeditor5-react",
		"@ckeditor/ckeditor5-remove-format/dist/index.js",
		"@ckeditor/ckeditor5-restricted-editing/dist/index.js",
		"@ckeditor/ckeditor5-select-all/dist/index.js",
		"@ckeditor/ckeditor5-show-blocks/dist/index.js",
		"@ckeditor/ckeditor5-slash-command/dist/index.js",
		"@ckeditor/ckeditor5-source-editing/dist/index.js",
		"@ckeditor/ckeditor5-source-editing-enhanced/dist/index.js",
		"@ckeditor/ckeditor5-special-characters/dist/index.js",
		"@ckeditor/ckeditor5-style/dist/index.js",
		"@ckeditor/ckeditor5-table/dist/index.js",
		"@ckeditor/ckeditor5-template/dist/index.js",
		"@ckeditor/ckeditor5-typing/dist/index.js",
		"@ckeditor/ckeditor5-ui/dist/index.js",
		"@ckeditor/ckeditor5-undo/dist/index.js",
		"@ckeditor/ckeditor5-upload/dist/index.js",
		"@ckeditor/ckeditor5-utils/dist/index.js",
		"@ckeditor/ckeditor5-watchdog/dist/index.js",
		"@ckeditor/ckeditor5-widget/dist/index.js",
		"@ckeditor/ckeditor5-word-count/dist/index.js",
		"frontend-editor-ckeditor-web/plugins/DocumentLinkSelector"
	};

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

}