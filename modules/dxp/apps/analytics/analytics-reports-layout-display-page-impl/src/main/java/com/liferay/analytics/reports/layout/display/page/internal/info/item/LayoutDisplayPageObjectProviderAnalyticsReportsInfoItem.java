/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.layout.display.page.internal.info.item;

import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.type.WebImage;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = "model.class.name=com.liferay.layout.display.page.LayoutDisplayPageObjectProvider",
	service = AnalyticsReportsInfoItem.class
)
public class LayoutDisplayPageObjectProviderAnalyticsReportsInfoItem
	implements AnalyticsReportsInfoItem<LayoutDisplayPageObjectProvider> {

	@Override
	public String getAuthorName(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		return StringPool.BLANK;
	}

	@Override
	public long getAuthorUserId(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		return 0L;
	}

	@Override
	public WebImage getAuthorWebImage(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
		Locale locale) {

		return null;
	}

	@Override
	public List<Locale> getAvailableLocales(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		Group group = _groupLocalService.fetchGroup(
			layoutDisplayPageObjectProvider.getGroupId());

		if (group == null) {
			return Collections.singletonList(LocaleUtil.getDefault());
		}

		Set<Locale> availableLocales = _language.getAvailableLocales(
			group.getGroupId());

		if (availableLocales == null) {
			return Collections.singletonList(LocaleUtil.getDefault());
		}

		return ListUtil.fromCollection(availableLocales);
	}

	@Override
	public String getCanonicalURL(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
		Locale locale) {

		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (themeDisplay == null) {
			return StringPool.BLANK;
		}

		Layout layout = _getLayout(layoutDisplayPageObjectProvider);

		if (layout == null) {
			return StringPool.BLANK;
		}

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		LayoutDisplayPageObjectProvider<?>
			initialLayoutDisplayPageObjectProvider =
				(LayoutDisplayPageObjectProvider<?>)
					httpServletRequest.getAttribute(
						LayoutDisplayPageWebKeys.
							LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		httpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageObjectProvider);

		String completeURL = _portal.getCurrentCompleteURL(httpServletRequest);

		try {
			String canonicalURL = _portal.getCanonicalURL(
				completeURL, themeDisplay, layout, false, false);

			LayoutSEOLink layoutSEOLink =
				_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
					layout, locale, canonicalURL, themeDisplay);

			String href = layoutSEOLink.getHref();

			if (href == null) {
				return StringPool.BLANK;
			}

			return href;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return StringPool.BLANK;
		}
		finally {
			httpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
				initialLayoutDisplayPageObjectProvider);
		}
	}

	@Override
	public Locale getDefaultLocale(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		try {
			return _portal.getSiteDefaultLocale(
				layoutDisplayPageObjectProvider.getGroupId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return LocaleUtil.getDefault();
	}

	@Override
	public Date getPublishDate(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		Object firstInfoItemService =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class,
				layoutDisplayPageObjectProvider.getClassName());

		Date date = new Date();

		if (firstInfoItemService != null) {
			InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
				(InfoItemFieldValuesProvider<Object>)firstInfoItemService;

			InfoFieldValue<Object> infoFieldValue =
				infoItemFieldValuesProvider.getInfoFieldValue(
					layoutDisplayPageObjectProvider.getDisplayObject(),
					"createDate");

			if (infoFieldValue != null) {
				InfoField infoField = infoFieldValue.getInfoField();

				if (Objects.equals(
						infoField.getInfoFieldType(),
						DateInfoFieldType.INSTANCE)) {

					date = (Date)infoFieldValue.getValue();
				}
			}
		}

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				layoutDisplayPageObjectProvider.getGroupId(),
				layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassPK());

		if ((assetDisplayPageEntry == null) ||
			!date.before(assetDisplayPageEntry.getCreateDate())) {

			return date;
		}

		return assetDisplayPageEntry.getCreateDate();
	}

	@Override
	public String getTitle(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
		Locale locale) {

		InfoItemFieldValuesProvider infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class,
				layoutDisplayPageObjectProvider.getClassName());

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValuesProvider.getInfoFieldValue(
				layoutDisplayPageObjectProvider.getDisplayObject(), "title");

		if (infoFieldValue == null) {
			return StringPool.BLANK;
		}

		InfoField infoField = infoFieldValue.getInfoField();

		if (!Objects.equals(
				infoField.getInfoFieldType(), TextInfoFieldType.INSTANCE)) {

			return StringPool.BLANK;
		}

		return (String)infoFieldValue.getValue(locale);
	}

	@Override
	public boolean isShow(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		Layout layout = _getLayout(layoutDisplayPageObjectProvider);

		if (layout == null) {
			return false;
		}

		if (layout.isTypeAssetDisplay() &&
			!layout.isEmbeddedPersonalApplication()) {

			try {
				return _hasEditPermission(
					layoutDisplayPageObjectProvider, layout,
					PermissionThreadLocal.getPermissionChecker());
			}
			catch (PortalException portalException) {
				_log.error(portalException);

				return false;
			}
		}

		return false;
	}

	private Layout _getLayout(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider) {

		if ((layoutDisplayPageObjectProvider == null) ||
			(layoutDisplayPageObjectProvider.getDisplayObject() == null)) {

			return null;
		}

		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (themeDisplay == null) {
			return null;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				themeDisplay.getScopeGroupId(),
				layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassPK(),
				layoutDisplayPageObjectProvider.getClassTypeId());

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
					layoutDisplayPageObjectProvider.getGroupId(),
					layoutDisplayPageObjectProvider.getClassNameId(),
					layoutDisplayPageObjectProvider.getClassPK(),
					layoutDisplayPageObjectProvider.getClassTypeId());
		}

		if (layoutPageTemplateEntry == null) {
			return null;
		}

		return _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());
	}

	private ThemeDisplay _getThemeDisplay() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		return serviceContext.getThemeDisplay();
	}

	private boolean _hasEditPermission(
			LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
			Layout layout, PermissionChecker permissionChecker)
		throws PortalException {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				layoutDisplayPageObjectProvider.getClassName());

		AssetRenderer<?> assetRenderer = null;

		if (assetRendererFactory != null) {
			assetRenderer = assetRendererFactory.getAssetRenderer(
				layoutDisplayPageObjectProvider.getClassPK());
		}

		if (((assetRenderer == null) ||
			 !assetRenderer.hasEditPermission(permissionChecker)) &&
			!LayoutPermissionUtil.contains(
				permissionChecker, layout, ActionKeys.UPDATE)) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutDisplayPageObjectProviderAnalyticsReportsInfoItem.class);

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Reference
	private Portal _portal;

}