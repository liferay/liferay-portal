/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.servlet.taglib;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.open.graph.OpenGraphConfiguration;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.layout.seo.template.LayoutSEOTemplateProcessor;
import com.liferay.layout.seo.web.internal.configuration.LayoutSEODynamicRenderingConfiguration;
import com.liferay.layout.seo.web.internal.util.OpenGraphImageProvider;
import com.liferay.layout.seo.web.internal.util.TitleProvider;
import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryTypeUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	configurationPid = "com.liferay.layout.seo.web.internal.configuration.LayoutSEODynamicRenderingConfiguration",
	service = DynamicInclude.class
)
public class OpenGraphTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		try {
			String layoutUtilityPageEntryType =
				LayoutUtilityPageEntryTypeUtil.
					getStatusLayoutUtilityPageEntryType(
						httpServletResponse.getStatus());

			if (Validator.isNotNull(layoutUtilityPageEntryType)) {
				return;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = themeDisplay.getLayout();

			if (themeDisplay.isSignedIn() || layout.isPrivateLayout()) {
				return;
			}

			if (_layoutSEODynamicRenderingConfiguration.enabled() &&
				ArrayUtil.contains(
					_layoutSEODynamicRenderingConfiguration.includedPaths(),
					layout.getFriendlyURL())) {

				return;
			}

			Set<Locale> availableLocales = _getAvailableLocales(
				layout, _portal.getSiteDefaultLocale(layout.getGroupId()));

			String completeURL = _portal.getCurrentCompleteURL(
				httpServletRequest);

			String canonicalURL = _portal.getCanonicalURL(
				completeURL, themeDisplay, layout, false, false);

			PrintWriter printWriter = httpServletResponse.getWriter();

			Locale locale = _portal.getLocale(httpServletRequest);

			if (!availableLocales.contains(locale)) {
				locale = LocaleUtil.getSiteDefault();
			}

			for (LayoutSEOLink layoutSEOLink :
					_layoutSEOLinkManager.getLocalizedLayoutSEOLinks(
						layout, locale, canonicalURL, availableLocales)) {

				printWriter.println(
					_addLinkTag(httpServletRequest, layoutSEOLink));
			}

			LayoutSEOEntry layoutSEOEntry =
				_layoutSEOEntryLocalService.fetchLayoutSEOEntry(
					layout.getGroupId(), layout.isPrivateLayout(),
					layout.getLayoutId());

			if (layoutSEOEntry != null) {
				List<LayoutSEOEntryCustomMetaTag> layoutSEOEntryCustomMetaTags =
					_layoutSEOEntryLocalService.getLayoutSEOEntryCustomMetaTags(
						layoutSEOEntry.getGroupId(),
						layoutSEOEntry.getLayoutSEOEntryId());

				for (LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag :
						layoutSEOEntryCustomMetaTags) {

					printWriter.println(
						_getOpenGraphTag(
							layoutSEOEntryCustomMetaTag.getProperty(),
							GetterUtil.get(
								layoutSEOEntryCustomMetaTag.getContent(
									themeDisplay.getLocale()),
								layoutSEOEntryCustomMetaTag.getContent(
									themeDisplay.getSiteDefaultLocale()))));
				}
			}

			if (!_openGraphConfiguration.isOpenGraphEnabled(
					layout.getGroup())) {

				return;
			}

			InfoItemFieldValues infoItemFieldValues = _getInfoItemFieldValues(
				httpServletRequest, layout);

			String description = _getMappedValue(
				layout.getTypeSettingsProperty(
					"mapped-openGraphDescription", "${description}"),
				infoItemFieldValues, themeDisplay.getLocale());

			if (description == null) {
				if ((layoutSEOEntry != null) &&
					layoutSEOEntry.isOpenGraphDescriptionEnabled()) {

					description = layoutSEOEntry.getOpenGraphDescription(
						themeDisplay.getLocale());
				}
				else {
					description = layout.getDescription(
						themeDisplay.getLocale());
				}
			}

			ListMergeable<String> pageDescriptionListMergeable =
				(ListMergeable<String>)httpServletRequest.getAttribute(
					WebKeys.PAGE_DESCRIPTION);

			if (!layout.isTypeAssetDisplay() &&
				(pageDescriptionListMergeable != null)) {

				String pageDescription =
					pageDescriptionListMergeable.mergeToString(
						StringPool.SPACE);

				if (Validator.isNotNull(description) &&
					Validator.isNotNull(pageDescription)) {

					description = StringBundler.concat(
						pageDescription, StringPool.PERIOD, StringPool.SPACE,
						description);
				}
				else if (Validator.isNull(description) &&
						 Validator.isNotNull(pageDescription)) {

					description = pageDescription;
				}
			}

			printWriter.println(
				_getOpenGraphTag(
					"og:description",
					HtmlUtil.unescape(HtmlUtil.stripHtml(description))));

			printWriter.println(
				_getOpenGraphTag("og:locale", themeDisplay.getLanguageId()));

			availableLocales.forEach(
				availableLocale -> printWriter.println(
					_getOpenGraphTag(
						"og:locale:alternate",
						LocaleUtil.toLanguageId(availableLocale))));

			Group group = layout.getGroup();

			printWriter.println(
				_getOpenGraphTag("og:site_name", group.getDescriptiveName()));

			String title = _getMappedValue(
				layout.getTypeSettingsProperty(
					"mapped-openGraphTitle", "${title}"),
				infoItemFieldValues, themeDisplay.getLocale());

			if (title == null) {
				if ((layoutSEOEntry != null) &&
					layoutSEOEntry.isOpenGraphTitleEnabled()) {

					title = layoutSEOEntry.getOpenGraphTitle(
						themeDisplay.getLocale());
				}
				else {
					title = _getTitle(httpServletRequest);
				}
			}

			printWriter.println(_getOpenGraphTag("og:title", title));

			printWriter.println(_getOpenGraphTag("og:type", "website"));

			LayoutSEOLink layoutSEOLink =
				_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
					layout, themeDisplay.getLocale(), canonicalURL,
					themeDisplay);

			printWriter.println(
				_getOpenGraphTag("og:url", layoutSEOLink.getHref()));

			OpenGraphImageProvider.OpenGraphImage openGraphImage =
				_openGraphImageProvider.getOpenGraphImage(
					infoItemFieldValues, layout, layoutSEOEntry, themeDisplay);

			if (openGraphImage != null) {
				printWriter.println(
					_getOpenGraphTag("og:image", openGraphImage.getURL()));

				String alt = openGraphImage.getAlt();

				if (alt != null) {
					printWriter.println(_getOpenGraphTag("og:image:alt", alt));
				}

				if (themeDisplay.isSecure()) {
					printWriter.println(
						_getOpenGraphTag(
							"og:image:secure_url", openGraphImage.getURL()));
				}

				String type = openGraphImage.getMimeType();

				if (type != null) {
					printWriter.println(
						_getOpenGraphTag("og:image:type", type));
				}

				printWriter.println(
					_getOpenGraphTag("og:image:url", openGraphImage.getURL()));

				for (KeyValuePair keyValuePair :
						openGraphImage.getMetadataTagKeyValuePairs()) {

					printWriter.println(
						_getOpenGraphTag(
							keyValuePair.getKey(), keyValuePair.getValue()));
				}
			}
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/top_head.jsp#pre");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_layoutSEODynamicRenderingConfiguration =
			ConfigurableUtil.createConfigurable(
				LayoutSEODynamicRenderingConfiguration.class, properties);
		_openGraphImageProvider = new OpenGraphImageProvider(
			_ddmFieldLocalService, _ddmStructureLocalService,
			_dlAppLocalService, _dlFileEntryMetadataLocalService, _dlurlHelper,
			_layoutSEOSiteLocalService, _layoutSEOTemplateProcessor, _portal);
		_titleProvider = new TitleProvider(_layoutSEOLinkManager);
	}

	private String _addLinkTag(
		HttpServletRequest httpServletRequest, LayoutSEOLink layoutSEOLink) {

		StringBundler sb = new StringBundler(10);

		sb.append("<link data-senna-track=\"temporary\" href=\"");
		sb.append(layoutSEOLink.getHref());
		sb.append(StringPool.QUOTE);

		if (Validator.isNotNull(layoutSEOLink.getHrefLang())) {
			sb.append(" hreflang=\"");
			sb.append(layoutSEOLink.getHrefLang());
			sb.append(StringPool.QUOTE);
		}

		sb.append(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		sb.append(" rel=\"");
		sb.append(layoutSEOLink.getRelationship());
		sb.append("\" />");

		return sb.toString();
	}

	private Set<Locale> _getAvailableLocales(
			Layout layout, Locale siteDefaultLocale)
		throws PortalException {

		Set<Locale> siteAvailableLocales = _language.getAvailableLocales(
			layout.getGroupId());

		if (!_openGraphConfiguration.isLayoutTranslatedLanguagesEnabled(
				layout.getGroup())) {

			return siteAvailableLocales;
		}

		InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, Layout.class.getName());

		if (infoItemLanguagesProvider == null) {
			return siteAvailableLocales;
		}

		Set<Locale> availableLocales = new HashSet<>();

		for (String languageId :
				infoItemLanguagesProvider.getAvailableLanguageIds(layout)) {

			availableLocales.add(LocaleUtil.fromLanguageId(languageId));
		}

		availableLocales.add(siteDefaultLocale);

		return availableLocales;
	}

	private InfoItemFieldValues _getInfoItemFieldValues(
		HttpServletRequest httpServletRequest, Layout layout) {

		if (!layout.isTypeAssetDisplay()) {
			return null;
		}

		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		if (infoItemDetails == null) {
			return null;
		}

		InfoItemFieldValuesProvider infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class,
				infoItemDetails.getClassName());

		if (infoItemFieldValuesProvider == null) {
			return null;
		}

		Object infoItem = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		return infoItemFieldValuesProvider.getInfoItemFieldValues(infoItem);
	}

	private String _getMappedValue(
		String template, InfoItemFieldValues infoItemFieldValues,
		Locale locale) {

		if ((infoItemFieldValues == null) || Validator.isNull(template)) {
			return null;
		}

		return _layoutSEOTemplateProcessor.processTemplate(
			template, infoItemFieldValues, locale);
	}

	private String _getOpenGraphTag(String property, String content) {
		if (Validator.isNull(content) || Validator.isNull(property)) {
			return StringPool.BLANK;
		}

		return StringBundler.concat(
			"<meta property=\"", HtmlUtil.escapeAttribute(property),
			"\" content=\"", HtmlUtil.escapeAttribute(content), "\">");
	}

	private String _getTitle(HttpServletRequest httpServletRequest) {
		try {
			return _titleProvider.getTitle(httpServletRequest);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Reference
	private DDMFieldLocalService _ddmFieldLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLURLHelper _dlurlHelper;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	private volatile LayoutSEODynamicRenderingConfiguration
		_layoutSEODynamicRenderingConfiguration;

	@Reference
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Reference
	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

	@Reference
	private LayoutSEOTemplateProcessor _layoutSEOTemplateProcessor;

	@Reference
	private OpenGraphConfiguration _openGraphConfiguration;

	private volatile OpenGraphImageProvider _openGraphImageProvider;

	@Reference
	private Portal _portal;

	private volatile TitleProvider _titleProvider;

}