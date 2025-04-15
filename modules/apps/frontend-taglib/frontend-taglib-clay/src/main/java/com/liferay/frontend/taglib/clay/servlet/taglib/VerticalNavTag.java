/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.IconItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eduardo Allegrini
 * @author Daniel Sanz
 */
public class VerticalNavTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setContainerElement("nav");

		return super.doStartTag();
	}

	public String getActive() {
		if (_active != null) {
			return _active;
		}

		return _getActiveVerticalNavItemKey(getVerticalNavItems());
	}

	public boolean getDecorated() {
		return _decorated;
	}

	public List<String> getDefaultExpandedKeys() {
		if (_defaultExpandedKeys != null) {
			return _defaultExpandedKeys;
		}

		List<String> defaultExpandedKeys = new ArrayList<>();

		_computeDefaultExpandedKeys(defaultExpandedKeys, getVerticalNavItems());

		return defaultExpandedKeys;
	}

	public boolean getLarge() {
		return _large;
	}

	public List<VerticalNavItem> getVerticalNavItems() {
		return _verticalNavItems;
	}

	public void setActive(String active) {
		_active = active;
	}

	public void setDecorated(boolean decorated) {
		_decorated = decorated;
	}

	public void setDefaultExpandedKeys(List<String> defaultExpandedKeys) {
		_defaultExpandedKeys = defaultExpandedKeys;
	}

	public void setLarge(boolean large) {
		_large = large;
	}

	public void setVerticalNavItems(List<VerticalNavItem> verticalNavItems) {
		_verticalNavItems = verticalNavItems;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_active = null;
		_decorated = false;
		_defaultExpandedKeys = null;
		_large = false;
		_verticalNavItems = null;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{VerticalNav} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		String active = getActive();

		if (active != null) {
			props.put("active", getActive());
		}

		props.put("decorated", _decorated);
		props.put("defaultExpandedKeys", getDefaultExpandedKeys());
		props.put("large", _large);
		props.put("items", _verticalNavItems);

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("menubar menubar-transparent");

		if (_decorated) {
			cssClasses.add("menubar-decorated");
		}

		cssClasses.add(
			_large ? "menubar-vertical-expand-lg" :
				"menubar-vertical-expand-md");

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<div class=\"collapse menubar-collapse\">");

		_renderVerticalNavItems(jspWriter, _verticalNavItems, 0);

		jspWriter.write("</div>");

		return EVAL_BODY_INCLUDE;
	}

	private void _computeDefaultExpandedKeys(
		List<String> defaultExpandedKeys,
		List<VerticalNavItem> verticalNavItems) {

		for (VerticalNavItem verticalNavItem : verticalNavItems) {
			VerticalNavItemList items =
				(VerticalNavItemList)verticalNavItem.get("items");

			Boolean expanded = (Boolean)verticalNavItem.get("expanded");

			if (expanded == null) {
				expanded = Boolean.FALSE;
			}

			if (expanded) {
				String itemId = (String)verticalNavItem.get("id");

				if (itemId != null) {
					defaultExpandedKeys.add(itemId);
				}
			}

			if (items != null) {
				_computeDefaultExpandedKeys(defaultExpandedKeys, items);
			}
		}
	}

	private String _getActiveVerticalNavItemKey(
		List<VerticalNavItem> verticalNavItems) {

		for (VerticalNavItem verticalNavItem : verticalNavItems) {
			VerticalNavItemList items =
				(VerticalNavItemList)verticalNavItem.get("items");

			Boolean active = (Boolean)verticalNavItem.get("active");

			if (active == null) {
				active = Boolean.FALSE;
			}

			if (active) {
				return (String)verticalNavItem.get("id");
			}

			if (items != null) {
				String activeKey = _getActiveVerticalNavItemKey(items);

				if (activeKey != null) {
					return activeKey;
				}
			}
		}

		return null;
	}

	private void _renderVerticalNavItems(
			JspWriter jspWriter, List<VerticalNavItem> verticalNavItems,
			int depth)
		throws Exception {

		jspWriter.write("<ul aria-orientation=\"vertical\" class=\"nav ");

		if (depth == 0) {
			jspWriter.write("nav-nested");
		}
		else {
			jspWriter.write("nav-stacked");
		}

		jspWriter.write("\" role=\"menubar\">");

		for (VerticalNavItem verticalNavItem : verticalNavItems) {
			VerticalNavItemList items =
				(VerticalNavItemList)verticalNavItem.get("items");

			Boolean active;

			if (_active != null) {
				active = _active.equals(verticalNavItem.get("id"));
			}
			else {
				active = (Boolean)verticalNavItem.get("active");

				if (active == null) {
					active = Boolean.FALSE;
				}
			}

			Boolean expanded;

			if (_defaultExpandedKeys != null) {
				expanded = _defaultExpandedKeys.contains(
					(String)verticalNavItem.get("id"));
			}
			else {
				expanded = (Boolean)verticalNavItem.get("expanded");

				if (expanded == null) {
					expanded = Boolean.FALSE;
				}
			}

			String href = (String)verticalNavItem.get("href");

			boolean button = false;

			if ((items != null) || Validator.isNull(href)) {
				button = true;
			}

			jspWriter.write("<li role=\"none\" class=\"nav-item\">");

			if (button) {
				jspWriter.write("<button class=\"nav-link collapse-icon");

				if (!expanded) {
					jspWriter.write(" collapsed");
				}

				if (active) {
					jspWriter.write(" active");
				}

				jspWriter.write(" btn btn-unstyled\" type=\"button\"");
				jspWriter.write(" aria-expanded=\"");
				jspWriter.write(expanded.toString());
				jspWriter.write("\" aria-haspopup=\"true\"");
				jspWriter.write(" role=\"button\" tabindex=\"-1\">");
			}
			else {
				jspWriter.write("<a class=\"nav-link");

				if (active) {
					jspWriter.write(" active");
				}

				jspWriter.write("\" href=\"");
				jspWriter.write((String)verticalNavItem.get("href"));
				jspWriter.write("\" role=\"menuitem\" tabindex=\"-1\">");
			}

			jspWriter.write(
				HtmlUtil.escape((String)verticalNavItem.get("label")));

			List<IconItem> iconItems = (List<IconItem>)verticalNavItem.get(
				"icons");

			if (ListUtil.isNotEmpty(iconItems)) {
				for (IconItem iconItem : iconItems) {
					String symbol = (String)iconItem.get("symbol");

					if (Validator.isNull(symbol)) {
						continue;
					}

					IconTag iconTag = new IconTag();

					iconTag.setCssClass("c-ml-2 c-mr-2 text-muted");
					iconTag.setSymbol(symbol);

					iconTag.doTag(pageContext);
				}
			}

			if (GetterUtil.getBoolean(verticalNavItem.get("deprecated"))) {
				jspWriter.write("<span class=\"inline-item ");
				jspWriter.write("inline-item-after\"><span class=\"badge ");
				jspWriter.write("badge-translucent badge-warning ");
				jspWriter.write("text-uppercase\"><span class=\"");
				jspWriter.write("badge-item badge-item-expand\">");
				jspWriter.write(LanguageUtil.get(getRequest(), "deprecated"));
				jspWriter.write("</span></span></span>");
			}

			if (items != null) {
				IconTag iconTag = new IconTag();

				if (expanded) {
					jspWriter.write("<span class=\"collapse-icon-open\">");
					iconTag.setSymbol("caret-bottom");
				}
				else {
					jspWriter.write("<span class=\"collapse-icon-closed\">");
					iconTag.setSymbol("caret-right");
				}

				iconTag.doTag(pageContext);
				jspWriter.write("</span>");
			}

			if (button) {
				jspWriter.write("</button>");
			}
			else {
				jspWriter.write("</a>");
			}

			if ((items != null) && expanded) {
				_renderVerticalNavItems(jspWriter, items, depth++);
			}

			jspWriter.write("</li>");
		}

		jspWriter.write("</ul>");
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:vertical_nav:";

	private String _active;
	private boolean _decorated;
	private List<String> _defaultExpandedKeys;
	private boolean _large;
	private List<VerticalNavItem> _verticalNavItems;

}