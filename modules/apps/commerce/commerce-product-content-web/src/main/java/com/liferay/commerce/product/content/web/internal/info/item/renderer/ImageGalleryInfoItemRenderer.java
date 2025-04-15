/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.renderer;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.content.util.CPMedia;
import com.liferay.commerce.product.content.web.internal.util.AdaptiveMediaCPMediaImpl;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = InfoItemRenderer.class)
public class ImageGalleryInfoItemRenderer
	implements InfoItemRenderer<CPDefinition> {

	@Override
	public String getKey() {
		return "cpDefinition-image-gallery";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "image-gallery");
	}

	@Override
	public void render(
		CPDefinition cpDefinition, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (cpDefinition == null) {
			return;
		}

		try {
			String randomKey = _portal.generateRandomKey(
				httpServletRequest, "product.gallery.info.item.renderer");

			String componentId = randomKey + "GalleryComponent";

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_reactRenderer.renderReact(
				new ComponentDescriptor(
					"{GalleryComponent} from commerce-frontend-js",
					componentId),
				HashMapBuilder.<String, Object>put(
					"images",
					() -> {
						List<CPMedia> images = _cpContentHelper.getImages(
							cpDefinition.getCPDefinitionId(), true,
							themeDisplay);

						JSONArray jsonArray = _jsonFactory.createJSONArray();

						for (CPMedia cpMedia : images) {
							jsonArray.put(
								JSONUtil.put(
									"adaptiveMediaImageHTMLTag",
									() -> {
										if (cpMedia instanceof
												AdaptiveMediaCPMediaImpl) {

											AdaptiveMediaCPMediaImpl
												adaptiveMediaCPMediaImpl =
													(AdaptiveMediaCPMediaImpl)
														cpMedia;

											return adaptiveMediaCPMediaImpl.
												getAdaptiveMediaImageHTMLTag();
										}

										return StringPool.BLANK;
									}
								).put(
									"thumbnailURL", cpMedia.getThumbnailURL()
								).put(
									"title", cpMedia.getTitle()
								).put(
									"URL", cpMedia.getURL()
								));
						}

						return jsonArray;
					}
				).put(
					"namespace",
					() -> {
						PortletDisplay portletDisplay =
							themeDisplay.getPortletDisplay();

						return portletDisplay.getNamespace();
					}
				).put(
					"portletId",
					() -> {
						PortletDisplay portletDisplay =
							themeDisplay.getPortletDisplay();

						return portletDisplay.getRootPortletId();
					}
				).put(
					"viewCPAttachmentURL",
					() -> ResourceURLBuilder.createResourceURL(
						_portletURLFactory.create(
							httpServletRequest, CPPortletKeys.CP_CONTENT_WEB,
							PortletRequest.RESOURCE_PHASE)
					).setParameter(
						"cpDefinitionId", cpDefinition.getCPDefinitionId()
					).setResourceID(
						"/cp_content_web/view_cp_attachments"
					).buildString()
				).build(),
				httpServletRequest, httpServletResponse.getWriter());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	private CPContentHelper _cpContentHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private ReactRenderer _reactRenderer;

}