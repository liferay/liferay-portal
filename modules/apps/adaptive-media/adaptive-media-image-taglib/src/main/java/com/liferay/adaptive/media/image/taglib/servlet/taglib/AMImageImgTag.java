/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.taglib.servlet.taglib;

import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.AttributesTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.io.IOException;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Adolfo Pérez
 */
public class AMImageImgTag extends AttributesTagSupport implements BodyTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(_getHTMLTag());

			return EVAL_PAGE;
		}
		catch (IOException | PortalException exception) {
			throw new JspException(exception);
		}
	}

	public void setFileVersion(FileVersion fileVersion) {
		_fileVersion = fileVersion;
	}

	private String _getFallbackTag() throws PortalException {
		Map<String, Object> dynamicAttributes = getDynamicAttributes();

		StringBundler sb = new StringBundler(
			4 + (4 * dynamicAttributes.size()));

		sb.append("<img ");

		for (Map.Entry<String, Object> entry : dynamicAttributes.entrySet()) {
			sb.append(entry.getKey());
			sb.append("=\"");

			sb.append(
				HtmlUtil.escapeAttribute(String.valueOf(entry.getValue())));

			sb.append("\" ");
		}

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String downloadURL = DLURLHelperUtil.getPreviewURL(
			_fileVersion.getFileEntry(), _fileVersion, themeDisplay,
			StringPool.BLANK);

		sb.append("src=\"");
		sb.append(downloadURL);
		sb.append("\" />");

		return sb.toString();
	}

	private String _getHTMLTag() throws PortalException {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<AMImageHTMLTagFactory> serviceReference =
			bundleContext.getServiceReference(AMImageHTMLTagFactory.class);

		if (serviceReference == null) {
			return _getFallbackTag();
		}

		try {
			AMImageHTMLTagFactory amImageHTMLTagFactory =
				bundleContext.getService(serviceReference);

			if (amImageHTMLTagFactory == null) {
				return _getFallbackTag();
			}

			return amImageHTMLTagFactory.create(
				_getFallbackTag(), _fileVersion.getFileEntry());
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	private FileVersion _fileVersion;

}