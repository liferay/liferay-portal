/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.tree.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspTag;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

/**
 * @author Carlos Sierra Andrés
 */
public class LeafTag extends SimpleTagSupport {

	@Override
	public void doTag() throws IOException, JspException {
		JspTag jspTag = findAncestorWithClass(this, TreeTag.class);

		if (jspTag instanceof TreeTag) {
			TreeTag treeTag = (TreeTag)jspTag;

			treeTag.setLeafJspFragment(getJspBody());
		}
		else {
			throw new IllegalStateException(
				"Leaf must be used inside a tree tag");
		}
	}

}