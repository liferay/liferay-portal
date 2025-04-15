/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

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
 * @author Marko Cikos
 */
public class NavigationCardTag extends BaseCardTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		String href = getHref();

		if (href != null) {
			setContainerElement("a");

			setDynamicAttribute(StringPool.BLANK, "href", href);
		}

		return super.doStartTag();
	}

	public String getDescription() {
		String description = _description;

		NavigationCard navigationCard = getNavigationCard();

		if ((_description == null) && (navigationCard != null)) {
			description = navigationCard.getDescription();
		}

		return LanguageUtil.get(
			TagResourceBundleUtil.getResourceBundle(pageContext), description);
	}

	@Override
	public String getIcon() {
		String icon = super.getIcon();

		NavigationCard navigationCard = getNavigationCard();

		if ((icon == null) && (navigationCard != null)) {
			return navigationCard.getIcon();
		}

		return icon;
	}

	public String getImageAlt() {
		String imageAlt = _imageAlt;

		NavigationCard navigationCard = getNavigationCard();

		if ((_imageAlt == null) && (navigationCard != null)) {
			imageAlt = navigationCard.getImageAlt();
		}

		return LanguageUtil.get(
			TagResourceBundleUtil.getResourceBundle(pageContext), imageAlt);
	}

	public String getImageSrc() {
		String imageSrc = _imageSrc;

		NavigationCard navigationCard = getNavigationCard();

		if ((_imageSrc == null) && (navigationCard != null)) {
			imageSrc = navigationCard.getImageSrc();
		}

		return imageSrc;
	}

	public NavigationCard getNavigationCard() {
		return (NavigationCard)getCardModel();
	}

	public String getTitle() {
		String title = _title;

		NavigationCard navigationCard = getNavigationCard();

		if ((_title == null) && (navigationCard != null)) {
			title = navigationCard.getTitle();
		}

		return LanguageUtil.get(
			TagResourceBundleUtil.getResourceBundle(pageContext), title);
	}

	public Boolean isSmall() {
		Boolean small = _small;

		if (_small == null) {
			NavigationCard navigationCard = getNavigationCard();

			if ((navigationCard != null) &&
				(navigationCard.isSmall() != null)) {

				return navigationCard.isSmall();
			}

			return false;
		}

		return small;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setImageAlt(String imageAlt) {
		_imageAlt = imageAlt;
	}

	public void setImageSrc(String imageSrc) {
		_imageSrc = imageSrc;
	}

	public void setNavigationCard(NavigationCard navigationCard) {
		setCardModel(navigationCard);
	}

	public void setSmall(Boolean small) {
		_small = small;
	}

	public void setTitle(String title) {
		_title = title;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_description = null;
		_imageAlt = null;
		_imageSrc = null;
		_small = null;
		_title = null;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{NavigationCard} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("description", getDescription());
		props.put("horizontal", isSmall());
		props.put("imageAlt", getImageAlt());
		props.put("imageSrc", getImageSrc());
		props.put("title", getTitle());

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("card");
		cssClasses.add("card-interactive");
		cssClasses.add("card-interactive-primary");
		cssClasses.add("card-type-template");

		if (isSmall()) {
			cssClasses.add("template-card-horizontal");
		}
		else {
			cssClasses.add("template-card");
		}

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		Boolean small = isSmall();

		if (!small) {
			jspWriter.write("<span class=\"aspect-ratio\"><span class=\"");
			jspWriter.write("aspect-ratio-item ");
			jspWriter.write("aspect-ratio-item-center-middle ");
			jspWriter.write("aspect-ratio-item-flush\">");

			String icon = getIcon();
			String imageSrc = getImageSrc();

			if (imageSrc != null) {
				jspWriter.write("<img");

				String imageAlt = getImageAlt();

				if (Validator.isNotNull(imageAlt)) {
					jspWriter.write(" alt=\"");
					jspWriter.write(imageAlt);
					jspWriter.write(StringPool.QUOTE);
				}

				jspWriter.write(" src=\"");
				jspWriter.write(imageSrc);
				jspWriter.write("\" />");
			}
			else if (icon != null) {
				IconTag iconTag = new IconTag();

				iconTag.setSymbol(icon);

				iconTag.doTag(pageContext);
			}

			jspWriter.write("</span></span>");
		}

		String description = getDescription();
		String title = getTitle();

		if (small || (title != null) || (description != null)) {
			jspWriter.write("<span class=\"card-body\">");

			if (!small) {
				if (title != null) {
					_writeDescription(jspWriter, "title", title);
				}

				if (description != null) {
					_writeDescription(jspWriter, "text", description);
				}
			}

			if (small) {
				jspWriter.write("<span class=\"card-row\"><div class=\"");
				jspWriter.write("autofit-col\">");

				StickerTag stickerTag = new StickerTag();

				stickerTag.setIcon(getIcon());
				stickerTag.setInline(true);

				stickerTag.doTag(pageContext);

				jspWriter.write("</div>");

				if (title != null) {
					jspWriter.write("<div class=\"autofit-col ");
					jspWriter.write("autofit-col-expand\"><div class=\"");
					jspWriter.write("autofit-section\">");

					_writeDescription(jspWriter, "title", title);

					jspWriter.write("</div></div>");
				}

				jspWriter.write("</span>");
			}

			jspWriter.write("</span>");
		}

		return SKIP_BODY;
	}

	private void _writeDescription(
			JspWriter jspWriter, String displayType, String description)
		throws Exception {

		jspWriter.write("<p class=\"card-");
		jspWriter.write(displayType);
		jspWriter.write("\" title=\"");
		jspWriter.write(HtmlUtil.escapeAttribute(description));
		jspWriter.write("\"><span class=\"text-truncate-inline\">");
		jspWriter.write("<span class=\"text-truncate\">");
		jspWriter.write(HtmlUtil.escape(description));
		jspWriter.write("</span></span></p>");
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:navigation-card:";

	private String _description;
	private String _imageAlt;
	private String _imageSrc;
	private Boolean _small;
	private String _title;

}