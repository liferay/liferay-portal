/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.display.context;

import com.liferay.document.library.display.context.BaseDLViewFileVersionDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.google.docs.internal.helper.GoogleDocsMetadataHelper;
import com.liferay.document.library.google.docs.internal.util.constants.GoogleDocsConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * @author Iván Zaera
 */
public class GoogleDocsDLViewFileVersionDisplayContext
	extends BaseDLViewFileVersionDisplayContext {

	public GoogleDocsDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion,
		GoogleDocsMetadataHelper googleDocsMetadataHelper) {

		super(
			_UUID, parentDLDisplayContext, httpServletRequest,
			httpServletResponse, fileVersion);

		_googleDocsMetadataHelper = googleDocsMetadataHelper;

		_googleDocsUIItemsProcessor = new GoogleDocsUIItemsProcessor(
			httpServletRequest, googleDocsMetadataHelper);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		List<DropdownItem> actionDropdownItems = super.getActionDropdownItems();

		if (!isActionsVisible()) {
			return actionDropdownItems;
		}

		// See LPS-79987

		if (Validator.isNull(
				_googleDocsMetadataHelper.getFieldValue(
					GoogleDocsConstants.DDM_FIELD_NAME_URL))) {

			return actionDropdownItems;
		}

		actionDropdownItems.removeIf(
			dropdownItem -> Objects.equals(
				dropdownItem.get("key"), "#edit-with-image-editor"));

		_googleDocsUIItemsProcessor.processDropdownItems(actionDropdownItems);

		return actionDropdownItems;
	}

	@Override
	public void renderPreview(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		if (_googleDocsMetadataHelper.containsField(
				GoogleDocsConstants.DDM_FIELD_NAME_EMBEDDABLE_URL)) {

			String previewURL = _googleDocsMetadataHelper.getFieldValue(
				GoogleDocsConstants.DDM_FIELD_NAME_EMBEDDABLE_URL);

			if (Validator.isNotNull(previewURL)) {
				printWriter.format(
					"<iframe frameborder=\"0\" height=\"664px\" src=\"%s\" " +
						"width=\"100%%\"></iframe>",
					previewURL);

				return;
			}
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", httpServletRequest.getLocale(), getClass());

		printWriter.write("<div class=\"alert alert-info\">");
		printWriter.write(
			LanguageUtil.get(
				resourceBundle,
				"google-docs-does-not-provide-a-preview-for-this-document"));
		printWriter.write("</div>");
	}

	private static final UUID _UUID = UUID.fromString(
		"7B61EA79-83AE-4FFD-A77A-1D47E06EBBE9");

	private final GoogleDocsMetadataHelper _googleDocsMetadataHelper;
	private final GoogleDocsUIItemsProcessor _googleDocsUIItemsProcessor;

}