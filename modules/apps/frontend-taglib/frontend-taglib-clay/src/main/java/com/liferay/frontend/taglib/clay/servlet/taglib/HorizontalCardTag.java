/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.util.DropdownItemListUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Map;
import java.util.Set;

/**
 * @author Marko Cikos
 */
public class HorizontalCardTag extends BaseCardTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		Map<String, String> data = getData();

		if (data != null) {
			for (Map.Entry<String, String> entry : data.entrySet()) {
				setDynamicAttribute(
					StringPool.BLANK, "data-" + entry.getKey(),
					entry.getValue());
			}
		}

		return super.doStartTag();
	}

	public HorizontalCard getHorizontalCard() {
		return (HorizontalCard)getCardModel();
	}

	@Override
	public String getIcon() {
		String icon = super.getIcon();

		if (icon == null) {
			HorizontalCard horizontalCard = getHorizontalCard();

			if ((horizontalCard != null) &&
				(horizontalCard.getIcon() != null)) {

				return horizontalCard.getIcon();
			}

			return "folder";
		}

		return icon;
	}

	public String getTitle() {
		String title = _title;

		HorizontalCard horizontalCard = getHorizontalCard();

		if ((_title == null) && (horizontalCard != null)) {
			title = horizontalCard.getTitle();
		}

		if (_isTranslated()) {
			title = LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext), title);
		}

		return title;
	}

	public void setHorizontalCard(HorizontalCard horizontalCard) {
		setCardModel(horizontalCard);
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setTranslated(Boolean translated) {
		_translated = translated;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_title = null;
		_translated = null;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{HorizontalCard} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("title", getTitle());

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		if (isSelectable()) {
			cssClasses.add("card-type-directory");
			cssClasses.add("form-check");
			cssClasses.add("form-check-card");
			cssClasses.add("form-check-middle-left");
		}

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		if (isSelectable()) {
			jspWriter.write("<div class=\"custom-control custom-checkbox\">");
			jspWriter.write("<label><input");

			if (isSelected()) {
				jspWriter.write(" checked");
			}

			jspWriter.write(" class=\"custom-control-input\"");

			if (isDisabled()) {
				jspWriter.write(" disabled");
			}

			String inputName = getInputName();

			if (inputName != null) {
				jspWriter.write(" name=\"");
				jspWriter.write(inputName);
				jspWriter.write("\"");
			}

			jspWriter.write(" type=\"checkbox\"");

			String inputValue = getInputValue();

			if (inputValue != null) {
				jspWriter.write(" value=\"");
				jspWriter.write(inputValue);
				jspWriter.write("\"");
			}

			jspWriter.write(" /><span class=\"custom-control-label\"></span>");

			_writeBody(jspWriter);

			jspWriter.write("</label></div>");
		}
		else {
			_writeBody(jspWriter);
		}

		return SKIP_BODY;
	}

	private boolean _isTranslated() {
		if (_translated != null) {
			return _translated;
		}

		HorizontalCard horizontalCard = getHorizontalCard();

		if (horizontalCard == null) {
			return true;
		}

		return horizontalCard.isTranslated();
	}

	private void _writeBody(JspWriter jspWriter) throws Exception {
		jspWriter.write("<div class=\"card card-horizontal\"><div class=\"");
		jspWriter.write("card-body\"><div class=\"card-row\"><div class=\"");
		jspWriter.write("autofit-col\">");

		StickerTag stickerTag = new StickerTag();

		stickerTag.setIcon(getIcon());
		stickerTag.setInline(true);

		stickerTag.doTag(pageContext);

		jspWriter.write("</div><div class=\"autofit-col autofit-col-expand");
		jspWriter.write(" autofit-col-gutters\"><p class=\"card-title\"");

		String title = getTitle();

		if (title != null) {
			jspWriter.write(" title=\"");
			jspWriter.write(HtmlUtil.escapeAttribute(title));
			jspWriter.write("\"");
		}

		jspWriter.write("><span class=\"text-truncate-inline\">");

		String href = getHref();
		Boolean disabled = isDisabled();

		if ((href != null) && !disabled) {
			LinkTag linkTag = new LinkTag();

			linkTag.setCssClass("text-truncate");
			linkTag.setHref(href);

			if (title != null) {
				linkTag.setLabel(title);
			}

			linkTag.setTranslated(_isTranslated());

			linkTag.doTag(pageContext);
		}
		else {
			jspWriter.write("<span class=\"text-truncate\">");

			if (title != null) {
				jspWriter.write(HtmlUtil.escape(title));
			}

			jspWriter.write("</span>");
		}

		jspWriter.write("</span></p></div>");

		if (!DropdownItemListUtil.isEmpty(getActionDropdownItems())) {
			jspWriter.write("<div class=\"autofit-col\">");

			DropdownActionsTag dropdownActionsTag = new DropdownActionsTag();

			dropdownActionsTag.setDropdownItems(getActionDropdownItems());

			if (disabled) {
				dropdownActionsTag.setDynamicAttribute(
					StringPool.BLANK, "disabled", disabled);
			}

			dropdownActionsTag.doTag(pageContext);

			jspWriter.write("</div>");
		}

		jspWriter.write("</div></div></div>");
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:horizontal-card:";

	private String _title;
	private Boolean _translated;

}