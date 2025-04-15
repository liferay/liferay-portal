/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Map;
import java.util.Set;

/**
 * @author Chema Balsas
 */
public class LinkTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setContainerElement("a");

		if (Validator.isNotNull(_download)) {
			setDynamicAttribute(StringPool.BLANK, "download", _download);
		}

		if (Validator.isNotNull(_href)) {
			setDynamicAttribute(StringPool.BLANK, "href", _href);
		}

		Map<String, Object> dynamicAttributes = getDynamicAttributes();

		if ((dynamicAttributes.get("rel") == null) &&
			(dynamicAttributes.get("target") != null)) {

			setDynamicAttribute(StringPool.BLANK, "rel", "noreferrer noopener");
		}

		if (Validator.isNotNull(_type) &&
			!(_type.equals("link") || _type.equals("button"))) {

			setDynamicAttribute(StringPool.BLANK, "type", _type);
		}

		return super.doStartTag();
	}

	public boolean getBlock() {
		return _block;
	}

	public boolean getBorderless() {
		return _borderless;
	}

	public String getDecoration() {
		return _decoration;
	}

	public String getDisplayType() {
		return _displayType;
	}

	public String getDownload() {
		return _download;
	}

	public String getFontSize() {
		return _fontSize;
	}

	public String getHref() {
		return _href;
	}

	public String getIcon() {
		return _icon;
	}

	public String getIconAfter() {
		return _iconAfter;
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

	public boolean getTranslated() {
		return _translated;
	}

	public String getType() {
		return _type;
	}

	public String getWeight() {
		return _weight;
	}

	public void setBlock(boolean block) {
		_block = block;
	}

	public void setBorderless(boolean borderless) {
		_borderless = borderless;
	}

	public void setDecoration(String decoration) {
		_decoration = decoration;
	}

	public void setDisplayType(String displayType) {
		_displayType = displayType;
	}

	public void setDownload(String download) {
		_download = download;
	}

	public void setFontSize(String fontSize) {
		_fontSize = fontSize;
	}

	public void setHref(String href) {
		_href = href;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setIconAfter(String iconAfter) {
		_iconAfter = iconAfter;
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

	public void setTranslated(boolean translated) {
		_translated = translated;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setWeight(String weight) {
		_weight = weight;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_block = false;
		_borderless = false;
		_decoration = null;
		_displayType = null;
		_download = null;
		_fontSize = null;
		_href = null;
		_icon = null;
		_iconAfter = null;
		_label = null;
		_monospaced = false;
		_outline = false;
		_small = false;
		_translated = true;
		_type = "link";
		_weight = null;
	}

	@Override
	protected String getHydratedModuleName() {
		if ((getAdditionalProps() != null) || (getPropsTransformer() != null)) {
			return "{Link} from frontend-taglib-clay";
		}

		return null;
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("block", _block);
		props.put("borderless", _borderless);
		props.put("button", _type.equals("button"));
		props.put("decoration", _decoration);
		props.put("displayType", _displayType);
		props.put("fontSize", _fontSize);
		props.put("icon", _icon);
		props.put("iconAfter", _iconAfter);
		props.put("weight", _weight);

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
		String cssPrefix = "link-";

		if (Validator.isNotNull(_type) && _type.equals("button")) {
			cssPrefix = "btn-";

			cssClasses.add("btn");
		}

		if (_block) {
			cssClasses.add(cssPrefix + "block");
		}

		if (_borderless) {
			cssClasses.add(cssPrefix + "outline-borderless");
		}

		if (_monospaced) {
			cssClasses.add(cssPrefix + "monospaced");
		}

		if (_small) {
			cssClasses.add(cssPrefix + "sm");
		}

		if (Validator.isNotNull(_decoration)) {
			cssClasses.add("text-decoration-" + _decoration);
		}

		if (Validator.isNotNull(_displayType)) {
			if (_outline || _borderless) {
				cssClasses.add(cssPrefix + "outline-" + _displayType);
			}
			else {
				cssClasses.add(cssPrefix + _displayType);
			}
		}

		if (Validator.isNotNull(_fontSize)) {
			cssClasses.add("text-" + _fontSize);
		}

		if (Validator.isNotNull(_weight)) {
			cssClasses.add("font-weight-" + _weight);
		}

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		if (Validator.isNotNull(_icon) || Validator.isNotNull(_iconAfter) ||
			Validator.isNotNull(_label)) {

			JspWriter jspWriter = pageContext.getOut();

			if (Validator.isNotNull(_icon)) {
				IconTag iconTag = new IconTag();

				if (Validator.isNotNull(_label)) {
					iconTag.setCssClass("inline-item inline-item-before");
				}

				iconTag.setSymbol(_icon);

				iconTag.doTag(pageContext);
			}

			if (Validator.isNotNull(_label)) {
				String label = getLabel();

				if (_translated) {
					label = LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						_label);
				}

				jspWriter.write(HtmlUtil.escape(label));
			}

			if (Validator.isNotNull(_iconAfter)) {
				IconTag iconAfterTag = new IconTag();

				if (Validator.isNotNull(_label)) {
					iconAfterTag.setCssClass("inline-item inline-item-after");
				}

				iconAfterTag.setSymbol(_iconAfter);

				iconAfterTag.doTag(pageContext);
			}

			return SKIP_BODY;
		}

		return EVAL_BODY_INCLUDE;
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:link:";

	private boolean _block;
	private boolean _borderless;
	private String _decoration;
	private String _displayType;
	private String _download;
	private String _fontSize;
	private String _href;
	private String _icon;
	private String _iconAfter;
	private String _label;
	private boolean _monospaced;
	private boolean _outline;
	private boolean _small;
	private boolean _translated = true;
	private String _type = "link";
	private String _weight;

}