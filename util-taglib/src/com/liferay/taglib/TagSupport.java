/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.Tag;

/**
 * <p>
 * See https://issues.liferay.com/browse/LPS-13878.
 * </p>
 *
 * @author Shuyang Zhou
 */
public class TagSupport implements DirectTag, Tag {

	public static Tag findAncestorWithClass(Tag fromTag, Class<?> clazz) {
		if ((fromTag == null) || (clazz == null) ||
			(!Tag.class.isAssignableFrom(clazz) && !clazz.isInterface())) {

			return null;
		}

		while (true) {
			Tag parentTag = fromTag.getParent();

			if (parentTag == null) {
				return null;
			}

			if (clazz.isInstance(parentTag)) {
				return parentTag;
			}

			fromTag = parentTag;
		}
	}

	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	@Override
	public Tag getParent() {
		return _parent;
	}

	@Override
	public void release() {
		_parent = null;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	@Override
	public void setParent(Tag tag) {
		_parent = tag;
	}

	protected PageContext pageContext;

	private Tag _parent;

}