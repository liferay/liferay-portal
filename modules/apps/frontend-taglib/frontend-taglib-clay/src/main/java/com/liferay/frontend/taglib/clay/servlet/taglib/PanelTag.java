/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Map;
import java.util.Set;

/**
 * @author Eduardo Allegrini
 */
public class PanelTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public Boolean getCollapsable() {
		return _collapsable;
	}

	public String getCollapseClassNames() {
		return _collapseClassNames;
	}

	public String getDisplayTitle() {
		return _displayTitle;
	}

	public String getDisplayType() {
		return _displayType;
	}

	public Boolean getExpanded() {
		return _expanded;
	}

	public Boolean getShowCollapseIcon() {
		return _showCollapseIcon;
	}

	public String getSize() {
		return _size;
	}

	public void setCollapsable(Boolean collapsable) {
		_collapsable = collapsable;
	}

	public void setCollapseClassNames(String collapseClassNames) {
		_collapseClassNames = collapseClassNames;
	}

	public void setDisplayTitle(String displayTitle) {
		_displayTitle = displayTitle;
	}

	public void setDisplayType(String displayType) {
		_displayType = displayType;
	}

	public void setExpanded(Boolean expanded) {
		_expanded = expanded;
	}

	public void setShowCollapseIcon(Boolean showCollapseIcon) {
		_showCollapseIcon = showCollapseIcon;
	}

	public void setSize(String size) {
		_size = size;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_collapsable = true;
		_collapseClassNames = null;
		_displayTitle = null;
		_displayType = "unstyled";
		_expanded = false;
		_showCollapseIcon = true;
		_size = null;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{Panel} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("collapsable", _collapsable);
		props.put("collapseClassNames", _collapseClassNames);
		props.put("defaultExpanded", _expanded);
		props.put("displayTitle", _displayTitle);
		props.put("displayType", _displayType);
		props.put("showCollapseIcon", _showCollapseIcon);
		props.put("size", _size);

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("panel");
		cssClasses.add("panel-" + _getType());

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processEndTag() throws Exception {
		if (_collapsable) {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write("</div>");
		}

		return super.processEndTag();
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		StringBundler panelContentCssClassesSB = new StringBundler(5);
		StringBundler panelTitleAttributesSB = new StringBundler(5);
		StringBundler panelTitleCssClassesSB = new StringBundler(4);
		boolean panelTitleIcon = false;
		String panelTitleId = _getId();

		panelTitleCssClassesSB.append("panel-header");
		panelContentCssClassesSB.append("panel-collapse");

		if (_collapsable) {
			panelTitleCssClassesSB.append(
				" btn btn-unstyled panel-header-link");
			panelContentCssClassesSB.append(" collapse");

			if (Validator.isNotNull(_collapseClassNames)) {
				panelContentCssClassesSB.append(" ");
				panelContentCssClassesSB.append(_collapseClassNames);
			}

			if (_showCollapseIcon) {
				panelTitleCssClassesSB.append(
					" collapse-icon collapse-icon-middle");
				panelTitleIcon = true;
			}

			if (_expanded) {
				panelContentCssClassesSB.append(" show");
			}
			else {
				panelTitleCssClassesSB.append(" collapsed");
			}

			panelTitleAttributesSB.append(
				" aria-controls=\"collapsePanel\" aria-expanded=\"");
			panelTitleAttributesSB.append(_expanded);
			panelTitleAttributesSB.append("\" data-target=\"#");
			panelTitleAttributesSB.append(panelTitleId);
			panelTitleAttributesSB.append("\" data-toggle=\"liferay-collapse");
			panelTitleAttributesSB.append("\" role=\"tab\" type=\"button\"");

			jspWriter.write("<button ");
		}
		else {
			jspWriter.write("<div ");
		}

		jspWriter.write("class=\"");
		jspWriter.write(panelTitleCssClassesSB.toString());
		jspWriter.write("\" ");
		jspWriter.write(panelTitleAttributesSB.toString());
		jspWriter.write(">");

		jspWriter.write("<span class=\"panel-title\">");
		jspWriter.write(_displayTitle);
		jspWriter.write("</span>");

		if (panelTitleIcon) {
			IconTag iconTag = new IconTag();

			jspWriter.write("<span class=\"collapse-icon-closed\">");
			iconTag.setSymbol("angle-right");
			iconTag.doTag(pageContext);
			jspWriter.write("</span>");

			jspWriter.write("<span class=\"collapse-icon-open\">");
			iconTag.setSymbol("angle-down");
			iconTag.doTag(pageContext);
			jspWriter.write("</span>");
		}

		if (_collapsable) {
			jspWriter.write("</button><div class=\"");
			jspWriter.write(panelContentCssClassesSB.toString());
			jspWriter.write("\" id=\"");
			jspWriter.write(panelTitleId);
			jspWriter.write("\" role=\"tabpanel\">");
		}
		else {
			jspWriter.write("</div>");
		}

		return EVAL_BODY_INCLUDE;
	}

	private String _getId() {
		String randomKey = PortalUtil.generateRandomKey(
			getRequest(), _ATTRIBUTE_NAMESPACE);

		return randomKey + StringPool.UNDERLINE;
	}

	private String _getType() {
		if (Validator.isNotNull(_displayType)) {
			if (_displayType.equals("block")) {
				return "block";
			}

			if (_displayType.equals("default")) {
				return "default";
			}

			if (_displayType.equals("secondary")) {
				return "secondary";
			}
		}

		return "unstyled";
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:panel:";

	private boolean _collapsable = true;
	private String _collapseClassNames;
	private String _displayTitle;
	private String _displayType = "unstyled";
	private boolean _expanded;
	private boolean _showCollapseIcon = true;
	private String _size;

}