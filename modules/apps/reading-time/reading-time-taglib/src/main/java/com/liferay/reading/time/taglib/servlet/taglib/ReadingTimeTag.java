/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.reading.time.taglib.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.reading.time.message.ReadingTimeMessageProvider;
import com.liferay.reading.time.model.ReadingTimeEntry;
import com.liferay.reading.time.service.ReadingTimeEntryLocalServiceUtil;
import com.liferay.reading.time.taglib.internal.servlet.servlet.reading.time.ReadingTimeUtil;
import com.liferay.taglib.util.AttributesTagSupport;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.io.IOException;

import java.time.Duration;

/**
 * @author Alejandro Tardín
 */
public class ReadingTimeTag extends AttributesTagSupport implements BodyTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			Duration readingTimeDuration = _getReadingTimeDuration();

			if (readingTimeDuration != null) {
				String tag = _buildTag(readingTimeDuration);

				if (tag != null) {
					JspWriter jspWriter = pageContext.getOut();

					jspWriter.write(tag);
				}
			}

			return EVAL_PAGE;
		}
		catch (IOException ioException) {
			throw new JspException(ioException);
		}
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setModel(GroupedModel groupedModel) {
		_groupedModel = groupedModel;
	}

	private String _buildTag(Duration readingTimeDuration) {
		String readingTimeMessage = _getReadingTimeMessage(readingTimeDuration);

		if (Validator.isNotNull(readingTimeMessage)) {
			StringBundler sb = new StringBundler(10);

			sb.append("<time class=\"reading-time\" datetime=\"");
			sb.append(String.valueOf(readingTimeDuration.getSeconds()));
			sb.append("s\"");

			if (Validator.isNotNull(_id)) {
				sb.append(" id=\"");
				sb.append(_getNamespace());
				sb.append(_id);
				sb.append("\"");
			}

			sb.append(">");
			sb.append(readingTimeMessage);
			sb.append("</time>");

			return sb.toString();
		}

		return null;
	}

	private String _getNamespace() {
		HttpServletRequest httpServletRequest = getRequest();

		RenderResponse renderResponse =
			(RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		return renderResponse.getNamespace();
	}

	private Duration _getReadingTimeDuration() {
		if (_groupedModel == null) {
			return Duration.ZERO;
		}

		ReadingTimeEntry readingTimeEntry =
			ReadingTimeEntryLocalServiceUtil.fetchOrAddReadingTimeEntry(
				_groupedModel);

		if (readingTimeEntry != null) {
			return Duration.ofMillis(readingTimeEntry.getReadingTime());
		}

		return null;
	}

	private String _getReadingTimeMessage(Duration readingTimeDuration) {
		ReadingTimeMessageProvider readingTimeMessageProvider =
			ReadingTimeUtil.getReadingTimeMessageProvider(_displayStyle);

		if (readingTimeMessageProvider == null) {
			return null;
		}

		return readingTimeMessageProvider.provide(
			readingTimeDuration, PortalUtil.getLocale(getRequest()));
	}

	private String _displayStyle = "simple";
	private GroupedModel _groupedModel;
	private String _id;

}