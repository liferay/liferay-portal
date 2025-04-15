/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRegistry;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = CTDisplayRenderer.class)
public class DDMDataProviderInstanceCTDisplayRenderer
	extends BaseCTDisplayRenderer<DDMDataProviderInstance> {

	@Override
	public String[] getAvailableLanguageIds(
		DDMDataProviderInstance ddmDataProviderInstance) {

		return ddmDataProviderInstance.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(
		DDMDataProviderInstance ddmDataProviderInstance) {

		return ddmDataProviderInstance.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			DDMDataProviderInstance ddmDataProviderInstance)
		throws PortalException {

		Group group = _groupLocalService.getGroup(
			ddmDataProviderInstance.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group,
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_DATA_PROVIDER, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_data_provider.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"dataProviderInstanceId",
			ddmDataProviderInstance.getDataProviderInstanceId()
		).buildString();
	}

	@Override
	public Class<DDMDataProviderInstance> getModelClass() {
		return DDMDataProviderInstance.class;
	}

	@Override
	public String getTitle(
		Locale locale, DDMDataProviderInstance ddmDataProviderInstance) {

		return ddmDataProviderInstance.getName(locale);
	}

	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<DDMDataProviderInstance> displayBuilder) {

		DDMDataProviderInstance ddmDataProviderInstance =
			displayBuilder.getModel();

		displayBuilder.display(
			"name", ddmDataProviderInstance.getName(displayBuilder.getLocale())
		).display(
			"description",
			ddmDataProviderInstance.getDescription(displayBuilder.getLocale())
		).display(
			"created-by",
			() -> {
				String userName = ddmDataProviderInstance.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", ddmDataProviderInstance.getCreateDate()
		).display(
			"modified-date", ddmDataProviderInstance.getModifiedDate()
		).display(
			"type", ddmDataProviderInstance.getType()
		);
	}

	@Reference
	protected DDMDataProviderRegistry ddmDataProviderRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}