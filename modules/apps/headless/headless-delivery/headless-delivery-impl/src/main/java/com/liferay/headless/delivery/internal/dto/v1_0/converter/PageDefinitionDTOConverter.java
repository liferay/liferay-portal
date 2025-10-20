/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.headless.delivery.dto.v1_0.ClientExtension;
import com.liferay.headless.delivery.dto.v1_0.MasterPage;
import com.liferay.headless.delivery.dto.v1_0.PageDefinition;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.headless.delivery.dto.v1_0.Settings;
import com.liferay.headless.delivery.dto.v1_0.StyleBook;
import com.liferay.headless.delivery.dto.v1_0.util.ContentDocumentUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.PageRulesUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.util.constants.LayoutStructureConstants;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 * @author Javier de Arcos
 */
@Component(
	property = "dto.class.name=com.liferay.layout.util.structure.LayoutStructure",
	service = DTOConverter.class
)
public class PageDefinitionDTOConverter
	implements DTOConverter<LayoutStructure, PageDefinition> {

	@Override
	public String getContentType() {
		return PageDefinition.class.getSimpleName();
	}

	@Override
	public PageDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutStructure layoutStructure)
		throws Exception {

		Layout layout = (Layout)dtoConverterContext.getAttribute("layout");

		if (layout == null) {
			throw new IllegalArgumentException(
				"Layout is not defined for layout structure item " +
					layoutStructure.getMainItemId());
		}

		return new PageDefinition() {
			{
				setPageElement(
					() -> {
						LayoutStructureItem mainLayoutStructureItem =
							layoutStructure.getMainLayoutStructureItem();
						boolean saveInlineContent = GetterUtil.getBoolean(
							dtoConverterContext.getAttribute(
								"saveInlineContent"),
							true);
						boolean saveMappingConfiguration =
							GetterUtil.getBoolean(
								dtoConverterContext.getAttribute(
									"saveMappingConfiguration"),
								true);

						return _pageElementDTOConverter.toDTO(
							new AttributesDTOConverterContext(
								HashMapBuilder.put(
									"groupId", (Object)layout.getGroupId()
								).put(
									"layoutStructure", layoutStructure
								).put(
									"saveInlineContent", saveInlineContent
								).put(
									"saveMappingConfiguration",
									saveMappingConfiguration
								).build()),
							mainLayoutStructureItem);
					});
				setPageRules(
					() -> PageRulesUtil.toPageRules(
						layoutStructure.getLayoutStructureRules()));
				setSettings(() -> _toSettings(dtoConverterContext, layout));
				setVersion(
					() ->
						LayoutStructureConstants.
							LATEST_PAGE_DEFINITION_VERSION);
			}
		};
	}

	private CET _getCET(
		long classNameId, long classPK, long companyId, String type) {

		ClientExtensionEntryRel clientExtensionEntryRel =
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				classNameId, classPK, type);

		if (clientExtensionEntryRel == null) {
			return null;
		}

		return _cetManager.getCET(
			companyId, clientExtensionEntryRel.getCETExternalReferenceCode());
	}

	private Map<String, String> _getClientExtensionConfig(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		if (clientExtensionEntryRel == null) {
			return null;
		}

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.fastLoad(
			clientExtensionEntryRel.getTypeSettings()
		).build();

		if (unicodeProperties.isEmpty()) {
			return null;
		}

		Map<String, String> clientExtensionConfig = new HashMap<>();

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			clientExtensionConfig.put(entry.getKey(), entry.getValue());
		}

		return clientExtensionConfig;
	}

	private ClientExtension[] _getClientExtensions(
		long classNameId, DTOConverterContext dtoConverterContext,
		Layout layout, String type) {

		ClientExtension[] clientExtensions = TransformUtil.transformToArray(
			_clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
				classNameId, layout.getPlid(), type),
			clientExtensionEntryRel -> {
				CET cet = _cetManager.getCET(
					layout.getCompanyId(),
					clientExtensionEntryRel.getCETExternalReferenceCode());

				if (cet == null) {
					return null;
				}

				return new ClientExtension() {
					{
						setClientExtensionConfig(
							() -> _getClientExtensionConfig(
								clientExtensionEntryRel));
						setExternalReferenceCode(cet::getExternalReferenceCode);
						setName(
							() -> cet.getName(dtoConverterContext.getLocale()));
					}
				};
			},
			ClientExtension.class);

		if (ArrayUtil.isEmpty(clientExtensions)) {
			return null;
		}

		return clientExtensions;
	}

	private ClientExtension _getThemeCSSClientExtension(
		long classNameId, Layout layout,
		DTOConverterContext dtoConverterContext) {

		CET cet = _getCET(
			classNameId, layout.getPlid(), layout.getCompanyId(),
			ClientExtensionEntryConstants.TYPE_THEME_CSS);

		if (cet == null) {
			return null;
		}

		return new ClientExtension() {
			{
				setExternalReferenceCode(cet::getExternalReferenceCode);
				setName(() -> cet.getName(dtoConverterContext.getLocale()));
			}
		};
	}

	private ClientExtension _getThemeSpritemapClientExtension(
		long classNameId, Layout layout,
		DTOConverterContext dtoConverterContext) {

		CET cet = _getCET(
			classNameId, layout.getPlid(), layout.getCompanyId(),
			ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP);

		if (cet == null) {
			return null;
		}

		return new ClientExtension() {
			{
				setExternalReferenceCode(cet::getExternalReferenceCode);
				setName(() -> cet.getName(dtoConverterContext.getLocale()));
			}
		};
	}

	private Settings _toSettings(
		DTOConverterContext dtoConverterContext, Layout layout) {

		long classNameId = _portal.getClassNameId(Layout.class.getName());
		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		return new Settings() {
			{
				setColorSchemeName(
					() -> {
						ColorScheme colorScheme = null;

						try {
							colorScheme = layout.getColorScheme();
						}
						catch (PortalException portalException) {
							if (_log.isWarnEnabled()) {
								_log.warn(portalException);
							}
						}

						if (colorScheme == null) {
							return null;
						}

						return colorScheme.getName();
					});
				setCss(
					() -> {
						if (Validator.isNull(layout.getCss())) {
							return null;
						}

						return layout.getCss();
					});
				setFavIcon(
					() -> {
						CET cet = _getCET(
							classNameId, layout.getPlid(),
							layout.getCompanyId(),
							ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

						if (cet != null) {
							return new ClientExtension() {
								{
									setExternalReferenceCode(
										cet::getExternalReferenceCode);
									setName(
										() -> cet.getName(
											dtoConverterContext.getLocale()));
								}
							};
						}

						long faviconFileEntryId =
							layout.getFaviconFileEntryId();

						if (faviconFileEntryId == 0) {
							return null;
						}

						return ContentDocumentUtil.toContentDocument(
							_dlURLHelper, "settings.favIcon.image",
							_dlAppService.getFileEntry(faviconFileEntryId),
							dtoConverterContext.getUriInfo());
					});
				setGlobalCSSClientExtensions(
					() -> _getClientExtensions(
						classNameId, dtoConverterContext, layout,
						ClientExtensionEntryConstants.TYPE_GLOBAL_CSS));
				setGlobalJSClientExtensions(
					() -> _getClientExtensions(
						classNameId, dtoConverterContext, layout,
						ClientExtensionEntryConstants.TYPE_GLOBAL_JS));
				setJavascript(
					() -> {
						for (Map.Entry<String, String> entry :
								unicodeProperties.entrySet()) {

							String key = entry.getKey();

							if (key.equals("javascript")) {
								return entry.getValue();
							}
						}

						return null;
					});
				setMasterPage(
					() -> {
						LayoutPageTemplateEntry layoutPageTemplateEntry =
							_layoutPageTemplateEntryLocalService.
								fetchLayoutPageTemplateEntryByPlid(
									layout.getMasterLayoutPlid());

						if (layoutPageTemplateEntry == null) {
							return null;
						}

						return new MasterPage() {
							{
								setKey(
									() ->
										layoutPageTemplateEntry.
											getLayoutPageTemplateEntryKey());
							}
						};
					});
				setStyleBook(
					() -> {
						if (Validator.isNull(layout.getStyleBookEntryERC())) {
							return null;
						}

						StyleBookEntry styleBookEntry =
							_styleBookEntryLocalService.
								fetchStyleBookEntryByExternalReferenceCode(
									layout.getStyleBookEntryERC(),
									layout.getGroupId());

						return new StyleBook() {
							{
								setKey(
									() ->
										styleBookEntry.getStyleBookEntryKey());
								setName(styleBookEntry::getName);
							}
						};
					});
				setThemeCSSClientExtension(
					() -> _getThemeCSSClientExtension(
						classNameId, layout, dtoConverterContext));
				setThemeName(
					() -> {
						Theme theme = layout.getTheme();

						if (theme == null) {
							return null;
						}

						return theme.getName();
					});
				setThemeSettings(
					() -> {
						UnicodeProperties themeSettingsUnicodeProperties =
							new UnicodeProperties();

						for (Map.Entry<String, String> entry :
								unicodeProperties.entrySet()) {

							String key = entry.getKey();

							if (key.startsWith("lfr-theme:")) {
								themeSettingsUnicodeProperties.setProperty(
									key, entry.getValue());
							}
						}

						if (themeSettingsUnicodeProperties.isEmpty()) {
							return null;
						}

						return themeSettingsUnicodeProperties;
					});
				setThemeSpritemapClientExtension(
					() -> _getThemeSpritemapClientExtension(
						classNameId, layout, dtoConverterContext));
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PageDefinitionDTOConverter.class);

	@Reference
	private CETManager _cetManager;

	@Reference
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.PageElementDTOConverter)"
	)
	private DTOConverter<LayoutStructureItem, PageElement>
		_pageElementDTOConverter;

	@Reference
	private Portal _portal;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private static class AttributesDTOConverterContext
		implements DTOConverterContext {

		@Override
		public Object getAttribute(String name) {
			return _attributes.get(name);
		}

		private AttributesDTOConverterContext(Map<String, Object> attributes) {
			_attributes = attributes;
		}

		private final Map<String, Object> _attributes;

	}

}