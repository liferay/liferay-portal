/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.io.IOException;

import java.util.Map;
import java.util.Set;

/**
 * @author Chema Balsas
 */
public class ButtonTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setContainerElement("button");

		Map<String, Object> dynamicAttributes = getDynamicAttributes();

		if (dynamicAttributes.get("title") != null) {
			String title = (String)dynamicAttributes.get("title");

			setDynamicAttribute(
				StringPool.BLANK, "title",
				LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					title));
		}

		if (dynamicAttributes.get("type") == null) {
			setDynamicAttribute(StringPool.BLANK, "type", "button");
		}

		return super.doStartTag();
	}

	public boolean getAlert() {
		return _alert;
	}

	public boolean getBlock() {
		return _block;
	}

	public boolean getBorderless() {
		return _borderless;
	}

	public String getDisplayType() {
		return _displayType;
	}

	public String getIcon() {
		return _icon;
	}

	public String getLabel() {
		return _label;
	}

	public boolean getMonospaced() {
		return _monospaced;
	}

	public boolean getOutline() {
		return _outline;
	}

	public boolean getSmall() {
		return _small;
	}

	public boolean getSwapIconSide() {
		return _swapIconSide;
	}

	public void setAlert(boolean alert) {
		_alert = alert;
	}

	public void setBlock(boolean block) {
		_block = block;
	}

	public void setBorderless(boolean borderless) {
		_borderless = borderless;
	}

	public void setDisplayType(String displayType) {
		_displayType = displayType;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setMonospaced(boolean monospaced) {
		_monospaced = monospaced;
	}

	public void setOutline(boolean outline) {
		_outline = outline;
	}

	public void setSmall(boolean small) {
		_small = small;
	}

	public void setSwapIconSide(boolean swapIconSide) {
		_swapIconSide = swapIconSide;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_alert = false;
		_block = false;
		_borderless = false;
		_displayType = "primary";
		_icon = null;
		_label = null;
		_monospaced = false;
		_outline = false;
		_small = false;
		_swapIconSide = false;
	}

	@Override
	protected String getHydratedModuleName() {
		if ((getAdditionalProps() != null) || (getPropsTransformer() != null)) {
			return "{Button} from frontend-taglib-clay";
		}

		return null;
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("block", _block);
		props.put("borderless", _borderless);
		props.put("displayType", _displayType);
		props.put("icon", _icon);
		props.put("swapIconSide", _swapIconSide);

		if (Validator.isNotNull(_label)) {
			props.put(
				"label",
				LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					_label));
		}

		props.put("monospaced", _monospaced);
		props.put("outline", _outline);
		props.put("small", _small);

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("btn");

		if (_alert) {
			cssClasses.add("alert-btn");
		}

		if (_block) {
			cssClasses.add("btn-block");
		}

		if ((Validator.isNotNull(_icon) && Validator.isNull(_label)) ||
			_monospaced) {

			cssClasses.add("btn-monospaced");
		}

		if (_borderless) {
			cssClasses.add("btn-outline-borderless");
		}

		if (_small) {
			cssClasses.add("btn-sm");
		}

		if (Validator.isNotNull(_displayType)) {
			if (_outline || _borderless) {
				cssClasses.add("btn-outline-" + _displayType);
			}
			else {
				cssClasses.add("btn-" + _displayType);
			}
		}

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		if (Validator.isNotNull(_icon) || Validator.isNotNull(_label)) {
			JspWriter jspWriter = pageContext.getOut();

			if (Validator.isNotNull(_icon) && !_swapIconSide) {
				jspWriter.write("<span class=\"inline-item");

				if (Validator.isNotNull(_label)) {
					jspWriter.write(" inline-item-before");
				}

				writeIcon(jspWriter);
			}

			if (Validator.isNotNull(_label)) {
				writeLabel(jspWriter);
			}

			if (Validator.isNotNull(_icon) && _swapIconSide) {
				jspWriter.write("<span class=\"inline-item");

				if (Validator.isNotNull(_label)) {
					jspWriter.write(" inline-item-after");
				}

				writeIcon(jspWriter);
			}

			return SKIP_BODY;
		}

		return EVAL_BODY_INCLUDE;
	}

	protected void writeIcon(JspWriter jspWriter) throws IOException {
		jspWriter.write("\"><svg class=\"lexicon-icon lexicon-icon-");
		jspWriter.write(_icon);
		jspWriter.write("\" role=\"presentation\"><use xlink:href=\"");

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		jspWriter.write(themeDisplay.getPathThemeSpritemap());

		jspWriter.write("#");
		jspWriter.write(_icon);
		jspWriter.write("\" /></svg></span>");
	}

	protected void writeLabel(JspWriter jspWriter) throws IOException {
		jspWriter.write(
			LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext), _label));
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:button:";

	private boolean _alert;
	private boolean _block;
	private boolean _borderless;
	private String _displayType = "primary";
	private String _icon;
	private String _label;
	private boolean _monospaced;
	private boolean _outline;
	private boolean _small;
	private boolean _swapIconSide;

}