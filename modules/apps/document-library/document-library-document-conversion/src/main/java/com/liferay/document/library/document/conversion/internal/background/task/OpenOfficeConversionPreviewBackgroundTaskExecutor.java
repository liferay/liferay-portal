/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.document.conversion.internal.background.task;

import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.PDFProcessor;
import com.liferay.document.library.preview.background.task.BasePreviewBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.document.library.document.conversion.internal.background.task.OpenOfficeConversionPreviewBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class OpenOfficeConversionPreviewBackgroundTaskExecutor
	extends BasePreviewBackgroundTaskExecutor {

	@Override
	protected void generatePreview(FileVersion fileVersion) throws Exception {
		PDFProcessor pdfProcessor = (PDFProcessor)_dlProcessor;

		pdfProcessor.generateImages(null, fileVersion);
	}

	@Override
	protected String[] getMimeTypes() {
		return MimeTypesHolder.getMimeTypes();
	}

	@Reference(target = "(type=" + DLProcessorConstants.PDF_PROCESSOR + ")")
	private DLProcessor _dlProcessor;

	private static class MimeTypesHolder {

		public static String[] getMimeTypes() {
			return _mimeTypes.toArray(new String[0]);
		}

		private static void _populateMimeTypeList(String documentFamily) {
			Filter filter = new Filter(documentFamily);

			String[] sourceExtensions = PropsUtil.getArray(
				PropsKeys.OPENOFFICE_CONVERSION_SOURCE_EXTENSIONS, filter);

			for (String sourceExtension : sourceExtensions) {
				String contentType = MimeTypesUtil.getExtensionContentType(
					sourceExtension);

				if (Validator.isNull(contentType)) {
					continue;
				}

				_mimeTypes.add(contentType);
			}
		}

		private static final List<String> _mimeTypes = new ArrayList<>();

		static {
			_populateMimeTypeList("drawing");
			_populateMimeTypeList("presentation");
			_populateMimeTypeList("spreadsheet");
			_populateMimeTypeList("text");
		}

	}

}