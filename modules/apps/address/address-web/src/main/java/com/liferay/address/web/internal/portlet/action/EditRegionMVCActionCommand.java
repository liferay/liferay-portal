/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.web.internal.portlet.action;

import com.liferay.address.web.internal.constants.AddressPortletKeys;
import com.liferay.portal.kernel.exception.DuplicateRegionException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.exception.RegionCodeException;
import com.liferay.portal.kernel.exception.RegionNameException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AddressPortletKeys.COUNTRIES_MANAGEMENT_ADMIN,
		"mvc.command.name=/address/edit_region"
	},
	service = MVCActionCommand.class
)
public class EditRegionMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long regionId = ParamUtil.getLong(actionRequest, "regionId");

			boolean active = ParamUtil.getBoolean(actionRequest, "active");
			String name = ParamUtil.getString(actionRequest, "name");
			double position = ParamUtil.getDouble(actionRequest, "position");
			String regionCode = ParamUtil.getString(
				actionRequest, "regionCode");

			Region region = null;

			if (regionId <= 0) {
				long countryId = ParamUtil.getLong(actionRequest, "countryId");

				region = _regionService.addRegion(
					countryId, active, name, position, regionCode,
					ServiceContextFactory.getInstance(
						Region.class.getName(), actionRequest));

				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					HttpComponentsUtil.setParameter(
						_portal.escapeRedirect(
							ParamUtil.getString(actionRequest, "redirect")),
						actionResponse.getNamespace() + "regionId",
						region.getRegionId()));
			}
			else {
				region = _regionService.updateRegion(
					regionId, active, name, position, regionCode);
			}

			if (region != null) {
				Map<String, String> titleMap = new HashMap<>();

				Map<Locale, String> titleLocalizationMap =
					_localization.getLocalizationMap(actionRequest, "title");

				for (Map.Entry<Locale, String> entry :
						titleLocalizationMap.entrySet()) {

					titleMap.put(
						_language.getLanguageId(entry.getKey()),
						entry.getValue());
				}

				_regionLocalService.updateRegionLocalizations(region, titleMap);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof NoSuchRegionException ||
				throwable instanceof PrincipalException) {

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (throwable instanceof DuplicateRegionException ||
					 throwable instanceof RegionCodeException ||
					 throwable instanceof RegionNameException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				sendRedirect(actionRequest, actionResponse);
			}

			throw new PortletException(throwable);
		}
	}

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private RegionLocalService _regionLocalService;

	@Reference
	private RegionService _regionService;

}