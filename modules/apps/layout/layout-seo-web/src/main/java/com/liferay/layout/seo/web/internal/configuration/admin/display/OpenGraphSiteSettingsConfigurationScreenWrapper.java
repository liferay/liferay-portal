/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.seo.model.LayoutSEOSite;
import com.liferay.layout.seo.open.graph.OpenGraphConfiguration;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.layout.seo.web.internal.display.context.OpenGraphSettingsDisplayContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(service = ConfigurationScreen.class)
public class OpenGraphSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new OpenGraphSiteSettingsConfigurationScreenContributor());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenGraphSiteSettingsConfigurationScreenWrapper.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlurlHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

	@Reference
	private OpenGraphConfiguration _openGraphConfiguration;

	@Reference
	private Portal _portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.seo.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class OpenGraphSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public Dictionary<String, Object> exportProperties(Serializable scopePK)
			throws Exception {

			LayoutSEOSite layoutSEOSite =
				_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
					(long)scopePK);

			if (layoutSEOSite == null) {
				return new HashMapDictionary<>();
			}

			if (layoutSEOSite.getOpenGraphImageFileEntryId() == 0) {
				return HashMapDictionaryBuilder.<String, Object>put(
					"openGraphEnabled", layoutSEOSite.isOpenGraphEnabled()
				).build();
			}

			FileEntry fileEntry = _dlAppLocalService.getFileEntry(
				layoutSEOSite.getOpenGraphImageFileEntryId());

			return HashMapDictionaryBuilder.<String, Object>put(
				"openGraphEnabled", layoutSEOSite.isOpenGraphEnabled()
			).put(
				"openGraphImageAlt",
				() -> {
					Map<String, String> languageIdOpenGraphImageAltMap =
						new HashMap<>();

					Map<Locale, String> openGraphImageAltMap =
						layoutSEOSite.getOpenGraphImageAltMap();

					for (Map.Entry<Locale, String> entry :
							openGraphImageAltMap.entrySet()) {

						languageIdOpenGraphImageAltMap.put(
							LocaleUtil.toLanguageId(entry.getKey()),
							entry.getValue());
					}

					return languageIdOpenGraphImageAltMap;
				}
			).put(
				"openGraphImageFileEntryERC",
				fileEntry.getExternalReferenceCode()
			).put(
				"openGraphImageFileEntryScopeERC",
				() -> ScopeUtil.getItemScopeExternalReferenceCode(
					fileEntry.getGroupId(), (long)scopePK)
			).build();
		}

		@Override
		public String getCategoryKey() {
			return "pages";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/open_graph.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-open-graph";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(_getResourceBundle(locale), "open-graph");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/layout/edit_open_graph_site_settings";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			Group group = _groupLocalService.getGroup((long)scopePK);

			LayoutSEOSite layoutSEOSite =
				_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
					group.getGroupId());

			boolean openGraphEnabled = true;
			Map<Locale, String> openGraphImageAltMap = Collections.emptyMap();
			long openGraphImageFileEntryId = 0;

			if (layoutSEOSite != null) {
				openGraphEnabled = layoutSEOSite.isOpenGraphEnabled();
				openGraphImageAltMap = layoutSEOSite.getOpenGraphImageAltMap();
				openGraphImageFileEntryId =
					layoutSEOSite.getOpenGraphImageFileEntryId();
			}

			String openGraphImageFileEntryERC = GetterUtil.getString(
				properties.get("openGraphImageFileEntryERC"));

			if (Validator.isNotNull(openGraphImageFileEntryERC)) {
				openGraphImageAltMap = new HashMap<>();

				Map<String, Object> openGraphImageAltProperty =
					(Map<String, Object>)properties.get("openGraphImageAlt");

				if (openGraphImageAltProperty != null) {
					for (Map.Entry<String, Object> entry :
							openGraphImageAltProperty.entrySet()) {

						openGraphImageAltMap.put(
							LocaleUtil.fromLanguageId(entry.getKey()),
							GetterUtil.getString(entry.getValue()));
					}
				}

				FileEntry fileEntry = _getFileEntry(
					openGraphImageFileEntryERC, group,
					GetterUtil.getString(
						properties.get("openGraphImageFileEntryScopeERC")));

				openGraphImageFileEntryId = fileEntry.getFileEntryId();
			}

			_layoutSEOSiteLocalService.updateLayoutSEOSite(
				PrincipalThreadLocal.getUserId(), group.getGroupId(),
				GetterUtil.getBoolean(
					properties.get("openGraphEnabled"), openGraphEnabled),
				openGraphImageAltMap, openGraphImageFileEntryId,
				new ServiceContext());
		}

		@Override
		public boolean isVisible(Group group) {
			try {
				if (group.isCompany() ||
					!_openGraphConfiguration.isOpenGraphEnabled(
						group.getCompanyId())) {

					return false;
				}

				return true;
			}
			catch (PortalException portalException) {
				_log.error(portalException);

				return false;
			}
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			SiteSettingsConfigurationScreenContributor.super.setAttributes(
				httpServletRequest, httpServletResponse);

			PortletRequest portletRequest =
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST);
			PortletResponse portletResponse =
				(PortletResponse)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE);

			httpServletRequest.setAttribute(
				OpenGraphSettingsDisplayContext.class.getName(),
				new OpenGraphSettingsDisplayContext(
					_dlAppService, _dlurlHelper, httpServletRequest,
					_itemSelector, _layoutSEOSiteLocalService,
					_portal.getLiferayPortletRequest(portletRequest),
					_portal.getLiferayPortletResponse(portletResponse),
					_openGraphConfiguration));
		}

		private FileEntry _getFileEntry(
				String externalReferenceCode, Group group,
				String scopeExternalReferenceCode)
			throws Exception {

			if (Validator.isNull(scopeExternalReferenceCode)) {
				scopeExternalReferenceCode = group.getExternalReferenceCode();
			}

			Long groupId = ScopeUtil.getItemGroupId(
				group.getCompanyId(), scopeExternalReferenceCode,
				group.getGroupId());

			if (groupId != null) {
				FileEntry fileEntry =
					_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
						groupId, externalReferenceCode);

				if (fileEntry != null) {
					return fileEntry;
				}
			}

			throw new NoSuchFileEntryException(
				StringBundler.concat(
					"No file entry exists with external reference code ",
					externalReferenceCode,
					" in the site or asset library with external reference ",
					"code ", scopeExternalReferenceCode));
		}

		private ResourceBundle _getResourceBundle(Locale locale) {
			return ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());
		}

	}

}