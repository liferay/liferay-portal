/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.util.DropdownItemListUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Carlos Lancha
 */
public class UserCardTag extends BaseCardTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	@Override
	public String getIcon() {
		String icon = super.getIcon();

		if (icon == null) {
			UserCard userCard = getUserCard();

			if ((userCard != null) && (userCard.getIcon() != null)) {
				return userCard.getIcon();
			}

			return "user";
		}

		return icon;
	}

	public String getImageAlt() {
		UserCard userCard = getUserCard();

		if ((_imageAlt == null) && (userCard != null)) {
			return userCard.getImageAlt();
		}

		return _imageAlt;
	}

	public String getImageSrc() {
		UserCard userCard = getUserCard();

		if ((_imageSrc == null) && (userCard != null)) {
			return userCard.getImageSrc();
		}

		return _imageSrc;
	}

	public String getName() {
		UserCard userCard = getUserCard();

		if ((_name == null) && (userCard != null)) {
			return userCard.getName();
		}

		return _name;
	}

	public String getSubtitle() {
		UserCard userCard = getUserCard();

		if ((_subtitle == null) && (userCard != null)) {
			return userCard.getSubtitle();
		}

		return _subtitle;
	}

	public UserCard getUserCard() {
		return (UserCard)getCardModel();
	}

	public String getUserColorClass() {
		UserCard userCard = getUserCard();

		if ((_userColorClass == null) && (userCard != null)) {
			return userCard.getUserColorClass();
		}

		return _userColorClass;
	}

	public void setImageAlt(String imageAlt) {
		_imageAlt = imageAlt;
	}

	public void setImageSrc(String imageSrc) {
		_imageSrc = imageSrc;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setSubtitle(String subtitle) {
		_subtitle = subtitle;
	}

	public void setUserCard(UserCard userCard) {
		setCardModel(userCard);
	}

	public void setUserColorClass(String userColorClass) {
		_userColorClass = userColorClass;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_imageAlt = null;
		_imageSrc = null;
		_name = null;
		_subtitle = null;
		_userColorClass = null;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{UserCard} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("description", getSubtitle());
		props.put("name", HtmlUtil.unescape(getName()));
		props.put("userDisplayType", getUserColorClass());
		props.put("userImageAlt", getImageAlt());
		props.put("userImageSrc", getImageSrc());

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		Boolean selectable = isSelectable();

		if ((selectable == null) || !selectable) {
			cssClasses.add("card");
		}

		cssClasses.add("card-type-asset");

		if (selectable) {
			cssClasses.add("form-check");
			cssClasses.add("form-check-card");
			cssClasses.add("form-check-top-left");
		}

		cssClasses.add("user-card");

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<div class=\"card\"><div class=\"aspect-ratio ");
		jspWriter.write("card-item-first\">");

		Boolean disabled = isDisabled();
		Boolean selectable = isSelectable();

		if (selectable) {
			jspWriter.write("<div class=\"custom-checkbox custom-control\">");
			jspWriter.write("<label><input ");

			jspWriter.write("class=\"custom-control-input\" ");

			Boolean selected = isSelected();

			if (selected) {
				jspWriter.write("checked ");
			}

			if (disabled) {
				jspWriter.write("disabled ");
			}

			String inputName = getInputName();

			if (Validator.isNotNull(inputName)) {
				jspWriter.write("name=\"");
				jspWriter.write(inputName);
				jspWriter.write("\" ");
			}

			jspWriter.write("type=\"checkbox\" ");

			String inputValue = getInputValue();

			if (Validator.isNotNull(inputValue)) {
				jspWriter.write("value=\"");
				jspWriter.write(inputValue);
				jspWriter.write("\"");
			}

			jspWriter.write("/><span class=\"custom-control-label\"></span>");
		}

		jspWriter.write("<div class=\"aspect-ratio-item-center-middle ");
		jspWriter.write("card-type-asset-icon\">");

		StickerTag stickerTag = new StickerTag();

		stickerTag.setCssClass("sticker-user-icon");
		stickerTag.setDisplayType(getUserColorClass());
		stickerTag.setShape("circle");

		String imageSrc = getImageSrc();

		if (Validator.isNotNull(imageSrc)) {
			stickerTag.setImageAlt(getImageAlt());
			stickerTag.setImageSrc(imageSrc);
		}
		else {
			stickerTag.setIcon(getIcon());
		}

		stickerTag.doTag(pageContext);

		jspWriter.write("</div>");

		if (selectable) {
			jspWriter.write("</label></div>");
		}

		jspWriter.write("</div><div class=\"card-body\"><div ");
		jspWriter.write("class=\"card-row\"><div class=\"autofit-col ");
		jspWriter.write("autofit-col-expand\"><p class=\"card-title\"><span ");
		jspWriter.write("class=\"text-truncate-inline\">");

		String href = getHref();
		String name = getName();

		if (((disabled == null) || !disabled) && Validator.isNotNull(href)) {
			jspWriter.write("<a class=\"text-truncate\" href=\"");
			jspWriter.write(href);
			jspWriter.write("\">");
			jspWriter.write(name);
			jspWriter.write("</a>");
		}
		else {
			jspWriter.write("<span class=\"text-truncate\">");
			jspWriter.write(name);
			jspWriter.write("</span>");
		}

		jspWriter.write("</span></p>");

		String subtitle = getSubtitle();

		if (Validator.isNotNull(subtitle)) {
			jspWriter.write("<p class=\"card-subtitle\"><span class=\"");
			jspWriter.write("text-truncate-inline\"><span class=\"");
			jspWriter.write("text-truncate\">");
			jspWriter.write(subtitle);
			jspWriter.write("</span></span></p>");
		}

		jspWriter.write("</div>");

		List<DropdownItem> actionDropdownItems = getActionDropdownItems();

		if (!DropdownItemListUtil.isEmpty(actionDropdownItems)) {
			jspWriter.write("<div class=\"autofit-col\">");

			DropdownActionsTag dropdownActionsTag = new DropdownActionsTag();

			dropdownActionsTag.setDropdownItems(actionDropdownItems);

			dropdownActionsTag.doTag(pageContext);

			jspWriter.write("</div>");
		}

		jspWriter.write("</div></div></div>");

		return SKIP_BODY;
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:user_card:";

	private String _imageAlt;
	private String _imageSrc;
	private String _name;
	private String _subtitle;
	private String _userColorClass;

}