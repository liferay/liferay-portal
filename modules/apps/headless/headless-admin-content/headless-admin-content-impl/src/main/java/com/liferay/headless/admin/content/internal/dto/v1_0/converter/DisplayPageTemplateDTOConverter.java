/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.internal.dto.v1_0.converter;

import com.liferay.headless.admin.content.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.content.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.content.internal.dto.v1_0.util.DisplayPageTemplateSettingsUtil;
import com.liferay.headless.delivery.dto.v1_0.PageDefinition;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = DTOConverter.class)
public class DisplayPageTemplateDTOConverter
	implements DTOConverter<LayoutPageTemplateEntry, DisplayPageTemplate> {

	@Override
	public String getContentType() {
		return DisplayPageTemplate.class.getSimpleName();
	}

	@Override
	public DisplayPageTemplate toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		return new DisplayPageTemplate() {
			{
				setActions(dtoConverterContext::getActions);
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						layout.getAvailableLanguageIds()));
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(
							layoutPageTemplateEntry.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						Layout.class.getName(), layout.getPlid(),
						layout.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(layout::getCreateDate);
				setDateModified(layout::getModifiedDate);
				setDisplayPageTemplateKey(
					() ->
						layoutPageTemplateEntry.
							getLayoutPageTemplateEntryKey());
				setDisplayPageTemplateSettings(
					() ->
						DisplayPageTemplateSettingsUtil.
							getDisplayPageTemplateSettings(
								dtoConverterContext, _infoItemServiceRegistry,
								layout, layoutPageTemplateEntry, _portal));
				setMarkedAsDefault(layoutPageTemplateEntry::isDefaultTemplate);
				setPageDefinition(
					() -> {
						dtoConverterContext.setAttribute("layout", layout);

						LayoutPageTemplateStructure
							layoutPageTemplateStructure =
								_layoutPageTemplateStructureLocalService.
									fetchLayoutPageTemplateStructure(
										layout.getGroupId(), layout.getPlid());

						if (layoutPageTemplateStructure == null) {
							return null;
						}

						DTOConverterRegistry dtoConverterRegistry =
							dtoConverterContext.getDTOConverterRegistry();

						DTOConverter<LayoutStructure, PageDefinition>
							dtoConverter =
								(DTOConverter<LayoutStructure, PageDefinition>)
									dtoConverterRegistry.getDTOConverter(
										LayoutStructure.class.getName());

						if (dtoConverter == null) {
							return null;
						}

						LayoutStructure layoutStructure = LayoutStructure.of(
							layoutPageTemplateStructure.
								getDefaultSegmentsExperienceData());

						return dtoConverter.toDTO(
							dtoConverterContext, layoutStructure);
					});
				setSiteId(layout::getGroupId);
				setTitle(layoutPageTemplateEntry::getName);
				setUuid(layoutPageTemplateEntry::getUuid);
			}
		};
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}