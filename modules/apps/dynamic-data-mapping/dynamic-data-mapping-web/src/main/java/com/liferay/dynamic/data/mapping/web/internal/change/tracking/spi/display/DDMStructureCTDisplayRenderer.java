/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTDisplayRenderer.class)
public class DDMStructureCTDisplayRenderer
	extends BaseCTDisplayRenderer<DDMStructure> {

	@Override
	public String[] getAvailableLanguageIds(DDMStructure ddmStructure) {
		return ddmStructure.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(DDMStructure ddmStructure) {
		return ddmStructure.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, DDMStructure ddmStructure)
		throws PortalException {

		Group group = _groupLocalService.getGroup(ddmStructure.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, JournalPortletKeys.JOURNAL, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_data_definition.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"classNameId", _portalUtil.getClassNameId(DDMStructure.class)
		).setParameter(
			"classPK", ddmStructure.getStructureId()
		).setParameter(
			"ddmStructureId", ddmStructure.getStructureId()
		).buildString();
	}

	@Override
	public Class<DDMStructure> getModelClass() {
		return DDMStructure.class;
	}

	@Override
	public String getTitle(Locale locale, DDMStructure ddmStructure) {
		return ddmStructure.getName(locale);
	}

	@Override
	protected void buildDisplay(DisplayBuilder<DDMStructure> displayBuilder) {
		DDMStructure ddmStructure = displayBuilder.getModel();

		Locale locale = displayBuilder.getLocale();

		displayBuilder.display(
			"name", ddmStructure.getName(locale)
		).display(
			"created-by", ddmStructure.getUserName()
		).display(
			"create-date", ddmStructure.getCreateDate()
		).display(
			"last-modified", ddmStructure.getModifiedDate()
		).display(
			"version", ddmStructure.getVersion()
		).display(
			"description", ddmStructure.getDescription(locale)
		).display(
			"definition", ddmStructure.getDefinition()
		).display(
			"storage-type", ddmStructure.getStorageType()
		).display(
			"type", ddmStructure.getType()
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortalUtil _portalUtil;

}