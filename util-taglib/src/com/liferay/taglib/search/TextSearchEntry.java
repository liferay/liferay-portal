/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class TextSearchEntry extends SearchEntry {

	@Override
	public Object clone() {
		TextSearchEntry textSearchEntry = new TextSearchEntry();

		BeanPropertiesUtil.copyProperties(this, textSearchEntry);

		return textSearchEntry;
	}

	public Map<String, Object> getData() {
		return _data;
	}

	public String getHref() {
		return _href;
	}

	public String getName() {
		return _name;
	}

	public String getName(HttpServletRequest httpServletRequest) {
		return getName();
	}

	public String getTarget() {
		return _target;
	}

	public String getTitle() {
		return _title;
	}

	@Override
	public void print(
			Writer writer, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (Validator.isNull(_href)) {
			writer.write(getName(httpServletRequest));
		}
		else {
			StringBundler sb = new StringBundler();

			sb.append("<a");

			if (_data != null) {
				for (Map.Entry<String, Object> entry : _data.entrySet()) {
					String value = String.valueOf(entry.getValue());

					sb.append(" data-");
					sb.append(entry.getKey());
					sb.append("=\"");
					sb.append(value);
					sb.append("\"");
				}
			}

			sb.append(" href=\"");

			if (_href.startsWith("javascript:")) {
				sb.append(_href);
			}
			else {
				sb.append(HtmlUtil.escape(_href));
			}

			sb.append("\"");

			if (Validator.isNotNull(_target)) {
				sb.append(" target=\"");
				sb.append(_target);
				sb.append("\"");
			}

			if (Validator.isNotNull(_title)) {
				sb.append(" title=\"");
				sb.append(_title);
				sb.append("\"");
			}

			sb.append(">");

			if (isTruncate()) {
				sb.append("<span class=\"text-truncate\">");
				sb.append(getName(httpServletRequest));
				sb.append("</span>");
			}
			else {
				sb.append(getName(httpServletRequest));
			}

			sb.append("</a>");

			writer.write(sb.toString());
		}
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	public void setHref(String href) {
		_href = href;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setTarget(String target) {
		_target = target;
	}

	public void setTitle(String title) {
		_title = title;
	}

	private Map<String, Object> _data;
	private String _href;
	private String _name;
	private String _target;
	private String _title;

}