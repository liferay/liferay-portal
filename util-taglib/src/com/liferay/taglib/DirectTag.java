/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.taglib.servlet.PageContextFactoryUtil;
import com.liferay.taglib.servlet.PipingPageContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;
import jakarta.servlet.jsp.tagext.Tag;

import java.util.function.Consumer;

/**
 * @author Shuyang Zhou
 */
public interface DirectTag extends Tag {

	public default void doBodyTag(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			Consumer<PageContext> consumer)
		throws JspException {

		doBodyTag(
			PageContextFactoryUtil.create(
				httpServletRequest, httpServletResponse),
			consumer);
	}

	public default void doBodyTag(
			PageContext pageContext, Consumer<PageContext> consumer)
		throws JspException {

		setPageContext(pageContext);

		int start = doStartTag();

		if (start == SKIP_BODY) {
			doEndTag();

			return;
		}

		if (this instanceof BodyTag) {
			BodyTag bodyTag = (BodyTag)this;

			if (start == BodyTag.EVAL_BODY_BUFFERED) {
				JspWriter jspWriter = pageContext.pushBody();

				bodyTag.setBodyContent((BodyContent)jspWriter);

				bodyTag.doInitBody();
			}

			do {
				consumer.accept(pageContext);
			}
			while (bodyTag.doAfterBody() == BodyTag.EVAL_BODY_AGAIN);

			if (start == BodyTag.EVAL_BODY_BUFFERED) {
				pageContext.popBody();
			}
		}
		else {
			consumer.accept(pageContext);
		}

		doEndTag();
	}

	public default String doBodyTagAsString(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			Consumer<PageContext> consumer)
		throws JspException {

		return doBodyTagAsString(
			PageContextFactoryUtil.create(
				httpServletRequest, httpServletResponse),
			consumer);
	}

	public default String doBodyTagAsString(
			PageContext pageContext, Consumer<PageContext> consumer)
		throws JspException {

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		doBodyTag(
			new PipingPageContext(pageContext, unsyncStringWriter), consumer);

		return unsyncStringWriter.toString();
	}

	public default void doTag(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws JspException {

		doTag(
			PageContextFactoryUtil.create(
				httpServletRequest, httpServletResponse));
	}

	public default void doTag(PageContext pageContext) throws JspException {
		setPageContext(pageContext);

		doStartTag();
		doEndTag();
	}

	public default String doTagAsString(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws JspException {

		return doTagAsString(
			PageContextFactoryUtil.create(
				httpServletRequest, httpServletResponse));
	}

	public default String doTagAsString(PageContext pageContext)
		throws JspException {

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		doTag(new PipingPageContext(pageContext, unsyncStringWriter));

		return unsyncStringWriter.toString();
	}

}