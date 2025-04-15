/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.diff.Diff;
import com.liferay.diff.DiffResult;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/compare_versions"
	},
	service = MVCRenderCommand.class
)
public class CompareVersionsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			_compareVersions(renderRequest);

			return "/document_library/compare_versions.jsp";
		}
		catch (NoSuchFileEntryException | PrincipalException exception) {
			SessionErrors.add(renderRequest, exception.getClass());

			return "/document_library/error.jsp";
		}
		catch (IOException | PortalException exception) {
			throw new PortletException(exception);
		}
	}

	private void _compareVersions(RenderRequest renderRequest)
		throws IOException, PortalException {

		long sourceFileVersionId = ParamUtil.getLong(
			renderRequest, "sourceFileVersionId");
		long targetFileVersionId = ParamUtil.getLong(
			renderRequest, "targetFileVersionId");

		FileVersion sourceFileVersion = _dlAppService.getFileVersion(
			sourceFileVersionId);

		InputStream sourceInputStream = _getFileVersionInputStream(
			sourceFileVersion);

		FileVersion targetFileVersion = _dlAppLocalService.getFileVersion(
			targetFileVersionId);

		InputStream targetInputStream = _getFileVersionInputStream(
			targetFileVersion);

		List<DiffResult>[] diffResults = _diff.diff(
			new InputStreamReader(sourceInputStream),
			new InputStreamReader(targetInputStream));

		renderRequest.setAttribute(WebKeys.DIFF_RESULTS, diffResults);

		renderRequest.setAttribute(
			WebKeys.SOURCE_NAME,
			sourceFileVersion.getTitle() + StringPool.SPACE +
				sourceFileVersion.getVersion());
		renderRequest.setAttribute(
			WebKeys.TARGET_NAME,
			targetFileVersion.getTitle() + StringPool.SPACE +
				targetFileVersion.getVersion());
	}

	private InputStream _getFileVersionInputStream(FileVersion fileVersion)
		throws IOException, PortalException {

		InputStream inputStream = fileVersion.getContentStream(false);

		String extension = fileVersion.getExtension();

		if (extension.equals("css") || extension.equals("htm") ||
			extension.equals("html") || extension.equals("js") ||
			extension.equals("txt") || extension.equals("xml")) {

			String content = HtmlUtil.escape(StringUtil.read(inputStream));

			inputStream = new UnsyncByteArrayInputStream(
				content.getBytes(StandardCharsets.UTF_8));
		}

		if (!DocumentConversionUtil.isEnabled() ||
			!DocumentConversionUtil.isConvertBeforeCompare(extension)) {

			return inputStream;
		}

		String tempFileId = DLUtil.getTempFileId(
			fileVersion.getFileEntryId(), fileVersion.getVersion());

		return new FileInputStream(
			DocumentConversionUtil.convert(
				tempFileId, inputStream, fileVersion.getExtension(), "txt"));
	}

	@Reference
	private Diff _diff;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

}