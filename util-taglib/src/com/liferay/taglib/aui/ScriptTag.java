/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.servlet.FileAvailabilityUtil;
import com.liferay.portal.kernel.servlet.taglib.BodyContentWrapper;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.base.BaseScriptTag;
import com.liferay.taglib.util.PortalIncludeUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;

import java.io.IOException;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class ScriptTag extends BaseScriptTag {

	public static void doTag(
			String position, String require, String use,
			String bodyContentString, BodyContent previousBodyContent,
			PageContext pageContext)
		throws Exception {

		String previousBodyContentString = null;

		if ((previousBodyContent != null) &&
			!(previousBodyContent instanceof BodyContentWrapper)) {

			// LPS-22413

			previousBodyContentString = previousBodyContent.getString();
		}

		ScriptTag scriptTag = new ScriptTag();

		scriptTag.setPageContext(pageContext);
		scriptTag.setPosition(position);
		scriptTag.setRequire(require);
		scriptTag.setUse(use);

		BodyContent bodyContent = pageContext.pushBody();

		scriptTag.setBodyContent(bodyContent);

		bodyContent.write(bodyContentString);

		pageContext.popBody();

		scriptTag.doEndTag();

		scriptTag.release();

		if (previousBodyContentString != null) {

			// LPS-22413

			previousBodyContent.clear();

			previousBodyContent.append(previousBodyContentString);
		}
	}

	public static void flushScriptData(PageContext pageContext)
		throws Exception {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		ScriptData scriptData = (ScriptData)httpServletRequest.getAttribute(
			WebKeys.AUI_SCRIPT_DATA);

		if (scriptData == null) {
			return;
		}

		httpServletRequest.removeAttribute(WebKeys.AUI_SCRIPT_DATA);

		scriptData.writeTo(pageContext.getOut());
	}

	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		try {
			if (getAsync() || Validator.isNotNull(getBlocking()) ||
				Validator.isNotNull(getCrossOrigin()) || getDefer() ||
				Validator.isNotNull(getFetchPriority()) ||
				Validator.isNotNull(getId()) ||
				Validator.isNotNull(getIntegrity()) ||
				Validator.isNotNull(getReferrerPolicy()) ||
				Validator.isNotNull(getSenna()) ||
				Validator.isNotNull(getSrc()) ||
				(Validator.isNotNull(getType()) &&
				 !Objects.equals(getType(), "text/javascript"))) {

				return _endTag();
			}

			String portletId = null;

			Portlet portlet = (Portlet)httpServletRequest.getAttribute(
				WebKeys.RENDER_PORTLET);

			if (portlet != null) {
				portletId = portlet.getPortletId();
			}

			String require = getRequire();
			String use = getUse();

			if ((use != null) && (require != null)) {
				throw new JspException(
					"Attribute \"use\" cannot be used with \"require\"");
			}

			StringBundler bodyContentSB = getBodyContentAsStringBundler();

			if (getSandbox() || (require != null) || (use != null)) {
				StringBundler sb = new StringBundler(4);

				if ((require == null) && (use == null)) {
					sb.append("(function() {");
				}

				sb.append("var $ = AUI.$;var _ = AUI._;");
				sb.append(bodyContentSB);

				if ((require == null) && (use == null)) {
					sb.append("})();");
				}

				bodyContentSB = sb;
			}

			if (isPositionInLine()) {
				ScriptData scriptData = new ScriptData();

				if (require != null) {
					scriptData.append(
						portletId, bodyContentSB, require,
						ScriptData.ModulesType.ES6);
				}
				else {
					scriptData.append(
						portletId, bodyContentSB, use,
						ScriptData.ModulesType.AUI);
				}

				String page = getPage();

				if (FileAvailabilityUtil.isAvailable(
						pageContext.getServletContext(), page)) {

					PortalIncludeUtil.include(pageContext, page);
				}
				else {
					scriptData.writeTo(pageContext.getOut());
				}
			}
			else {
				ScriptData scriptData =
					(ScriptData)httpServletRequest.getAttribute(
						WebKeys.AUI_SCRIPT_DATA);

				if (scriptData == null) {
					scriptData = new ScriptData();

					httpServletRequest.setAttribute(
						WebKeys.AUI_SCRIPT_DATA, scriptData);
				}

				if (require != null) {
					scriptData.append(
						portletId, bodyContentSB, require,
						ScriptData.ModulesType.ES6);
				}
				else {
					scriptData.append(
						portletId, bodyContentSB, use,
						ScriptData.ModulesType.AUI);
				}
			}

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			cleanUp();

			httpServletRequest.removeAttribute(WebKeys.JAVASCRIPT_CONTEXT);
		}
	}

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		httpServletRequest.setAttribute(
			WebKeys.JAVASCRIPT_CONTEXT, Boolean.TRUE);

		return super.doStartTag();
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		setPosition(null);
		setRequire(null);
		setUse(null);
	}

	private int _endTag() throws IOException, JspException {
		if (Validator.isNotNull(getRequire())) {
			throw new JspException(
				"Attribute \"require\" may not be used with direct rendering");
		}

		if (getSandbox()) {
			throw new JspException(
				"Attribute \"sandbox\" can only be false with direct " +
					"rendering");
		}

		if (Validator.isNotNull(getUse())) {
			throw new JspException(
				"Attribute \"use\" may not be used with direct rendering");
		}

		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<script");
		jspWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				getRequest()));

		_write(jspWriter, "async", getAsync());
		_write(jspWriter, "blocking", getBlocking());
		_write(jspWriter, "crossorigin", getCrossOrigin());
		_write(jspWriter, "defer", getDefer());
		_write(jspWriter, "fetchpriority", getFetchPriority());
		_write(jspWriter, "id", getId());
		_write(jspWriter, "integrity", getIntegrity());
		_write(jspWriter, "referrerpolicy", getReferrerPolicy());
		_write(jspWriter, "src", getSrc());
		_write(jspWriter, "type", getType());

		String senna = getSenna();

		if (Objects.equals(senna, "off")) {
			_write(jspWriter, "data-senna-off", "true");
		}
		else if (Validator.isNotNull(senna)) {
			_write(jspWriter, "data-senna-track", senna);
		}

		jspWriter.write(">");

		StringBundler sb = getBodyContentAsStringBundler();

		jspWriter.write(sb.toString());

		jspWriter.write("</script>");

		return EVAL_PAGE;
	}

	private void _write(JspWriter jspWriter, String name, boolean value)
		throws IOException {

		if (value) {
			jspWriter.write(StringPool.SPACE);
			jspWriter.write(name);
		}
	}

	private void _write(JspWriter jspWriter, String name, String value)
		throws IOException {

		if (Validator.isNotNull(value)) {
			jspWriter.write(StringPool.SPACE);
			jspWriter.write(name);
			jspWriter.write("=\"");
			jspWriter.write(value);
			jspWriter.write(StringPool.QUOTE);
		}
	}

}