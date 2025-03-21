/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import jakarta.servlet.jsp.JspException;

/**
 * @author Julien Castelain
 */
public class FileCardTag extends VerticalCardTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		if (getIcon() == null) {
			setIcon("documents-and-media");
		}

		if (getStickerIcon() == null) {
			setStickerIcon("document-default");
		}

		return super.doStartTag();
	}

	public void setFileCard(FileCard fileCard) {
		setCardModel(fileCard);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:filecard:";

}