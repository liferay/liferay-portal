/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.util.DropdownItemListUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Julien Castelain
 */
public class VerticalCardTag extends BaseCardTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		if (getIcon() == null) {
			setIcon("documents-and-media");
		}

		return super.doStartTag();
	}

	public String getAriaLabel() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_ariaLabel == null) && (verticalCard != null)) {
			return verticalCard.getAriaLabel();
		}

		return _ariaLabel;
	}

	@Override
	public String getIcon() {
		String icon = super.getIcon();

		VerticalCard verticalCard = getVerticalCard();

		if ((icon == null) && (verticalCard != null) &&
			(verticalCard.getIcon() != null)) {

			return verticalCard.getIcon();
		}

		return icon;
	}

	public String getImageAlt() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_imageAlt == null) && (verticalCard != null)) {
			return verticalCard.getImageAlt();
		}

		return _imageAlt;
	}

	public String getImageSrc() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_imageSrc == null) && (verticalCard != null)) {
			return verticalCard.getImageSrc();
		}

		return _imageSrc;
	}

	public List<LabelItem> getLabels() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_labels == null) && (verticalCard != null)) {
			return verticalCard.getLabels();
		}

		return _labels;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public Map<String, String> getLabelStylesMap() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_labelStylesMap == null) && (verticalCard != null)) {
			return verticalCard.getLabelStylesMap();
		}

		return _labelStylesMap;
	}

	public String getStickerCssClass() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_stickerCssClass == null) && (verticalCard != null)) {
			return verticalCard.getStickerCssClass();
		}

		return _stickerCssClass;
	}

	public String getStickerIcon() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_stickerIcon == null) && (verticalCard != null)) {
			return verticalCard.getStickerIcon();
		}

		return _stickerIcon;
	}

	public String getStickerImageAlt() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_stickerImageAlt == null) && (verticalCard != null)) {
			return verticalCard.getStickerImageAlt();
		}

		return _stickerImageAlt;
	}

	public String getStickerImageSrc() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_stickerImageSrc == null) && (verticalCard != null)) {
			return verticalCard.getStickerImageSrc();
		}

		return _stickerImageSrc;
	}

	public String getStickerLabel() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_stickerLabel == null) && (verticalCard != null)) {
			return verticalCard.getStickerLabel();
		}

		return LanguageUtil.get(
			TagResourceBundleUtil.getResourceBundle(pageContext),
			_stickerLabel);
	}

	public String getStickerShape() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_stickerShape == null) && (verticalCard != null)) {
			return verticalCard.getStickerShape();
		}

		return _stickerShape;
	}

	public String getStickerStyle() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_stickerStyle == null) && (verticalCard != null)) {
			return verticalCard.getStickerStyle();
		}

		return _stickerStyle;
	}

	public String getStickerTitle() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_stickerTitle == null) && (verticalCard != null)) {
			return verticalCard.getStickerTitle();
		}

		return _stickerTitle;
	}

	public String getSubtitle() {
		VerticalCard verticalCard = getVerticalCard();

		if ((_subtitle == null) && (verticalCard != null)) {
			return verticalCard.getSubtitle();
		}

		return LanguageUtil.get(
			TagResourceBundleUtil.getResourceBundle(pageContext), _subtitle);
	}

	public String getTitle() {
		String title = _title;

		VerticalCard verticalCard = getVerticalCard();

		if ((_title == null) && (verticalCard != null)) {
			title = verticalCard.getTitle();
		}

		if (_isTranslated()) {
			title = LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext), title);
		}

		return title;
	}

	public VerticalCard getVerticalCard() {
		return (VerticalCard)getCardModel();
	}

	public Boolean isFlushHorizontal() {
		if (_flushHorizontal == null) {
			VerticalCard verticalCard = getVerticalCard();

			if (verticalCard != null) {
				return verticalCard.isFlushHorizontal();
			}

			return false;
		}

		return _flushHorizontal;
	}

	public Boolean isFlushVertical() {
		if (_flushVertical == null) {
			VerticalCard verticalCard = getVerticalCard();

			if (verticalCard != null) {
				return verticalCard.isFlushVertical();
			}

			return false;
		}

		return _flushVertical;
	}

	public Boolean isStickerShown() {
		if (_showSticker == null) {
			VerticalCard verticalCard = getVerticalCard();

			if ((verticalCard != null) &&
				(verticalCard.isStickerShown() != null)) {

				return verticalCard.isStickerShown();
			}
			else if (Validator.isNotNull(getStickerIcon()) ||
					 Validator.isNotNull(getStickerImageSrc())) {

				return true;
			}

			return false;
		}

		return _showSticker;
	}

	public void setAriaLabel(String ariaLabel) {
		_ariaLabel = ariaLabel;
	}

	public void setFlushHorizontal(boolean flushHorizontal) {
		_flushHorizontal = flushHorizontal;
	}

	public void setFlushVertical(boolean flushVertical) {
		_flushVertical = flushVertical;
	}

	public void setImageAlt(String imageAlt) {
		_imageAlt = imageAlt;
	}

	public void setImageSrc(String imageSrc) {
		_imageSrc = imageSrc;
	}

	public void setLabels(List<LabelItem> labels) {
		_labels = labels;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public void setLabelStylesMap(Map<String, String> labelStylesMap) {
		_labelStylesMap = labelStylesMap;
	}

	public void setShowSticker(Boolean showSticker) {
		_showSticker = showSticker;
	}

	public void setStickerCssClass(String stickerCssClass) {
		_stickerCssClass = stickerCssClass;
	}

	public void setStickerIcon(String stickerIcon) {
		_stickerIcon = stickerIcon;
	}

	public void setStickerImageAlt(String stickerImageAlt) {
		_stickerImageAlt = stickerImageAlt;
	}

	public void setStickerImageSrc(String stickerImageSrc) {
		_stickerImageSrc = stickerImageSrc;
	}

	public void setStickerLabel(String stickerLabel) {
		_stickerLabel = stickerLabel;
	}

	public void setStickerShape(String stickerShape) {
		_stickerShape = stickerShape;
	}

	public void setStickerStyle(String stickerStyle) {
		_stickerStyle = stickerStyle;
	}

	public void setStickerTitle(String stickerTitle) {
		_stickerTitle = stickerTitle;
	}

	public void setSubtitle(String subtitle) {
		_subtitle = subtitle;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setTranslated(Boolean translated) {
		_translated = translated;
	}

	public void setVerticalCard(VerticalCard verticalCard) {
		setCardModel(verticalCard);
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_ariaLabel = null;
		_flushHorizontal = null;
		_flushVertical = null;
		_imageAlt = null;
		_imageSrc = null;
		_labels = null;
		_labelStylesMap = null;
		_showSticker = null;
		_stickerCssClass = null;
		_stickerIcon = null;
		_stickerImageAlt = null;
		_stickerImageSrc = null;
		_stickerLabel = null;
		_stickerShape = null;
		_stickerStyle = null;
		_stickerTitle = null;
		_subtitle = null;
		_title = null;
		_translated = null;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{VerticalCard} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("ariaLabel", getAriaLabel());
		props.put("description", getSubtitle());
		props.put("displayType", _getDisplayType());
		props.put("flushHorizontal", isFlushHorizontal());
		props.put("flushVertical", isFlushVertical());
		props.put("imageAlt", getImageAlt());
		props.put("imageSrc", getImageSrc());
		props.put("labels", getLabels());
		props.put("showSticker", isStickerShown());
		props.put("stickerCssClass", getStickerCssClass());
		props.put("stickerIcon", getStickerIcon());
		props.put("stickerImageAlt", getStickerImageAlt());
		props.put("stickerImageSrc", getStickerImageSrc());
		props.put("stickerLabel", getStickerLabel());
		props.put("stickerShape", getStickerShape());
		props.put("stickerStyle", getStickerStyle());
		props.put("stickerTitle", getStickerTitle());
		props.put("title", getTitle());

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("card-type-asset");

		if (Validator.isNotNull(_imageSrc)) {
			cssClasses.add("image-card");
		}
		else {
			cssClasses.add("file-card");
		}

		if (!isSelectable()) {
			cssClasses.add("card");
		}
		else {
			cssClasses.add("form-check");
			cssClasses.add("form-check-card");
			cssClasses.add("form-check-top-left");
		}

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		if (isSelectable()) {
			jspWriter.write("<div class=\"card\"><div class=\"custom-control ");
			jspWriter.write("custom-checkbox\"><label><input ");

			if (isSelected()) {
				jspWriter.write("checked ");
			}

			jspWriter.write("class=\"custom-control-input\" ");

			if (isDisabled()) {
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
				jspWriter.write("\" ");
			}

			jspWriter.write("/><span class=\"custom-control-label\"></span>");
		}

		jspWriter.write("<div class=\"aspect-ratio card-item-first\">");

		if (Validator.isNotNull(getImageSrc())) {
			jspWriter.write("<img");

			String imageAlt = getImageAlt();

			if (Validator.isNotNull(imageAlt)) {
				jspWriter.write(" alt=\"");
				jspWriter.write(imageAlt);
				jspWriter.write("\"");
			}

			jspWriter.write(" class=\"aspect-ratio-item");
			jspWriter.write(" aspect-ratio-item-center-middle");
			jspWriter.write(" aspect-ratio-item-fluid ");

			Boolean flushHorizontal = isFlushHorizontal();

			if (flushHorizontal != null) {
				jspWriter.write("aspect-ratio-item-flush");
			}

			Boolean flushVertical = isFlushVertical();

			if (flushVertical != null) {
				jspWriter.write("aspect-ratio-item-vertical-flush");
			}

			jspWriter.write("\" src=\"");
			jspWriter.write(getImageSrc());
			jspWriter.write("\">");
		}
		else {
			jspWriter.write("<div class=\"aspect-ratio-item");
			jspWriter.write(" aspect-ratio-item-center-middle");
			jspWriter.write(" aspect-ratio-item-fluid card-type-asset-icon\">");

			IconTag icon = new IconTag();

			icon.setSymbol(getIcon());

			icon.doTag(pageContext);

			jspWriter.write("</div>");
		}

		if (isStickerShown()) {
			StickerTag stickerTag = new StickerTag();

			String stickerCssClass = getStickerCssClass();

			if (Validator.isNotNull(stickerCssClass)) {
				stickerTag.setCssClass(stickerCssClass);
			}

			String stickerIcon = getStickerIcon();
			String stickerImageSrc = getStickerImageSrc();
			String stickerLabel = getStickerLabel();

			if (Validator.isNotNull(stickerImageSrc)) {
				String stickerImageAlt = getStickerImageAlt();

				if (Validator.isNotNull(stickerImageAlt)) {
					stickerTag.setImageAlt(stickerImageAlt);
				}

				stickerTag.setImageSrc(stickerImageSrc);
			}
			else if (Validator.isNotNull(stickerIcon)) {
				stickerTag.setIcon(stickerIcon);
			}
			else if (Validator.isNotNull(stickerLabel)) {
				stickerTag.setLabel(stickerLabel);
			}

			String stickerStyle = getStickerStyle();

			if (Validator.isNotNull(stickerStyle)) {
				stickerTag.setDisplayType(stickerStyle);
			}

			stickerTag.setPosition("bottom-left");

			String stickerShape = getStickerShape();

			if (Validator.isNotNull(stickerShape)) {
				stickerTag.setShape(stickerShape);
			}

			stickerTag.doTag(pageContext);
		}

		if (isSelectable()) {
			jspWriter.write("</div></label></div>");
		}
		else {
			jspWriter.write("</div>");
		}

		jspWriter.write("<div class=\"card-body\"><div class=\"card-row\">");
		jspWriter.write("<div class=\"autofit-col autofit-col-expand\">");
		jspWriter.write("<p");

		String ariaLabel = getAriaLabel();

		if (Validator.isNotNull(ariaLabel)) {
			jspWriter.write(" aria-label=\"");
			jspWriter.write(HtmlUtil.escapeAttribute(ariaLabel));
			jspWriter.write("\"");
		}

		jspWriter.write(" class=\"card-title\"");

		String title = getTitle();

		jspWriter.write(" title=\"");

		if (Validator.isNotNull(title)) {
			jspWriter.write(HtmlUtil.escapeAttribute(title));
		}

		jspWriter.write("\"><span class=\"text-truncate-inline\">");

		String href = getHref();

		if (Validator.isNotNull(href) && !isDisabled()) {
			LinkTag linkTag = new LinkTag();

			linkTag.setCssClass("text-truncate");
			linkTag.setHref(href);
			linkTag.setLabel(title);
			linkTag.setTranslated(_isTranslated());

			linkTag.doTag(pageContext);
		}
		else {
			jspWriter.write("<span class=\"text-truncate\">");

			if (Validator.isNotNull(title)) {
				jspWriter.write(HtmlUtil.escape(title));
			}

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

		List<LabelItem> labels = getLabels();

		if (ListUtil.isNotEmpty(labels)) {
			jspWriter.write("<div class=\"card-detail\">");

			for (LabelItem labelItem : labels) {
				LabelTag labelTag = new LabelTag();

				boolean labelIsDismissible = (boolean)labelItem.get(
					"dismissible");
				boolean labelIsLarge = (boolean)labelItem.get("large");
				String labelDisplayType = (String)labelItem.get("displayType");
				String labelLabel = (String)labelItem.get("label");

				if (labelIsDismissible) {
					labelTag.setDismissible(labelIsDismissible);
				}

				if (labelIsLarge) {
					labelTag.setLarge(labelIsLarge);
				}

				if (Validator.isNotNull(labelDisplayType)) {
					labelTag.setDisplayType(labelDisplayType);
				}

				labelTag.setLabel(labelLabel);

				labelTag.doTag(pageContext);
			}

			jspWriter.write("</div>");
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

		jspWriter.write("</div></div>");

		if (isSelectable()) {
			jspWriter.write("</div>");
		}

		return SKIP_BODY;
	}

	private String _getDisplayType() {
		if (Validator.isNotNull(getImageSrc())) {
			return "image";
		}

		return "file";
	}

	private boolean _isTranslated() {
		if (_translated != null) {
			return _translated;
		}

		VerticalCard verticalCard = getVerticalCard();

		if (verticalCard == null) {
			return true;
		}

		return verticalCard.isTranslated();
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:verticalcard:";

	private String _ariaLabel;
	private Boolean _flushHorizontal;
	private Boolean _flushVertical;
	private String _imageAlt;
	private String _imageSrc;
	private List<LabelItem> _labels;
	private Map<String, String> _labelStylesMap;
	private Boolean _showSticker;
	private String _stickerCssClass;
	private String _stickerIcon;
	private String _stickerImageAlt;
	private String _stickerImageSrc;
	private String _stickerLabel;
	private String _stickerShape;
	private String _stickerStyle;
	private String _stickerTitle;
	private String _subtitle;
	private String _title;
	private Boolean _translated;

}