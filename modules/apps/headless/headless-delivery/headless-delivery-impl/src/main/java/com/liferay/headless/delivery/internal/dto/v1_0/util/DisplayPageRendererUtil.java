/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.util;

import com.liferay.headless.delivery.dto.v1_0.RenderedContent;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateEntryException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Jürgen Kappler
 */
public class DisplayPageRendererUtil {

	public static RenderedContent[] getRenderedContent(
		Class<?> baseClass, String itemClassName, long itemClassPK,
		long itemClassTypeId, DTOConverterContext dtoConverterContext,
		long groupId, Object item,
		InfoItemServiceRegistry infoItemServiceRegistry,
		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry,
		LayoutService layoutService,
		LayoutPageTemplateEntryService layoutPageTemplateEntryService,
		LayoutServiceContextHelper layoutServiceContextHelper,
		String methodName) {

		UriInfo uriInfo = dtoConverterContext.getUriInfo();

		if (uriInfo == null) {
			return null;
		}

		return TransformUtil.transformToArray(
			layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
				groupId, PortalUtil.getClassNameId(itemClassName),
				itemClassTypeId,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE),
			layoutPageTemplateEntry -> new RenderedContent() {
				{
					setContentTemplateId(
						() ->
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryKey());
					setContentTemplateName(layoutPageTemplateEntry::getName);
					setMarkedAsDefault(
						layoutPageTemplateEntry::isDefaultTemplate);
					setRenderedContentURL(
						() -> JaxRsLinkUtil.getJaxRsLink(
							"headless-delivery", baseClass, methodName, uriInfo,
							itemClassPK,
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryKey()));
					setRenderedContentValue(
						() -> {
							if (!dtoConverterContext.containsNestedFieldsValue(
									"renderedContentValue")) {

								return null;
							}

							return toHTML(
								itemClassName, itemClassTypeId,
								layoutPageTemplateEntry.
									getLayoutPageTemplateEntryKey(),
								groupId, new DummyHttpServletResponse(), item,
								dtoConverterContext.getLocale(),
								dtoConverterContext.getUser(),
								infoItemServiceRegistry,
								layoutDisplayPageProviderRegistry,
								layoutService, layoutPageTemplateEntryService,
								layoutServiceContextHelper);
						});
				}
			},
			RenderedContent.class);
	}

	public static String toHTML(
			String itemClassName, long itemClassTypeId, String displayPageKey,
			long groupId, HttpServletResponse httpServletResponse, Object item,
			Locale locale, User user,
			InfoItemServiceRegistry infoItemServiceRegistry,
			LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry,
			LayoutService layoutService,
			LayoutPageTemplateEntryService layoutPageTemplateEntryService,
			LayoutServiceContextHelper layoutServiceContextHelper)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryService.getLayoutPageTemplateEntry(
				groupId, displayPageKey);

		if ((layoutPageTemplateEntry.getType() !=
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) &&
			(layoutPageTemplateEntry.getClassNameId() !=
				PortalUtil.getClassNameId(itemClassName)) &&
			(layoutPageTemplateEntry.getClassTypeId() != itemClassTypeId)) {

			throw new NoSuchPageTemplateEntryException();
		}

		Layout layout = layoutService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Locale originalThemeDisplayLocale =
			LocaleThreadLocal.getThemeDisplayLocale();

		try (AutoCloseable autoCloseable =
				layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout, user)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
			themeDisplay.setLocale(locale);

			HttpServletRequest httpServletRequest =
				PortalUtil.getOriginalServletRequest(
					serviceContext.getRequest());

			httpServletRequest.setAttribute(WebKeys.LOCALE, locale);

			httpServletRequest = DynamicServletRequest.addQueryString(
				httpServletRequest, "p_l_id=" + layout.getPlid(), false);

			httpServletRequest.setAttribute(InfoDisplayWebKeys.INFO_ITEM, item);

			InfoItemDetailsProvider infoItemDetailsProvider =
				infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemDetailsProvider.class, itemClassName);

			InfoItemDetails infoItemDetails =
				infoItemDetailsProvider.getInfoItemDetails(item);

			httpServletRequest.setAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS, infoItemDetails);
			httpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
				_getLayoutDisplayPageObjectProvider(
					layout.getCompanyId(),
					infoItemDetails.getInfoItemReference(),
					layoutDisplayPageProviderRegistry));

			httpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY,
				_getThemeDisplay(httpServletRequest, layout));

			layout.includeLayoutContent(
				httpServletRequest, httpServletResponse);

			StringBundler sb = (StringBundler)httpServletRequest.getAttribute(
				WebKeys.LAYOUT_CONTENT);

			LayoutSet layoutSet = layout.getLayoutSet();

			Document document = Jsoup.parse(
				ThemeUtil.include(
					ServletContextPool.get(StringPool.BLANK),
					httpServletRequest, httpServletResponse,
					"portal_normal.ftl", layoutSet.getTheme(), false));

			Element bodyElement = document.body();

			bodyElement.html(sb.toString());

			return document.html();
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(originalThemeDisplayLocale);
		}
	}

	private static LayoutDisplayPageObjectProvider<?>
		_getLayoutDisplayPageObjectProvider(
			long companyId, InfoItemReference infoItemReference,
			LayoutDisplayPageProviderRegistry
				layoutDisplayPageProviderRegistry) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					companyId, infoItemReference.getClassName());

		return layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
			infoItemReference);
	}

	private static ThemeDisplay _getThemeDisplay(
			HttpServletRequest httpServletRequest, Layout layout)
		throws Exception {

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			httpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(httpServletRequest, httpServletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLayout(layout);
		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setSiteGroupId(layout.getGroupId());

		return themeDisplay;
	}

}