/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Víctor Galán
 */
public class CompanyActionDropdownItems {

	public CompanyActionDropdownItems(
		Company company, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_company = company;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_defaultCompanyId = PortalInstancePool.getDefaultCompanyId();
	}

	public DropdownItemList getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_liferayPortletResponse
								).setMVCRenderCommandName(
									"/portal_instances/edit_instance"
								).setRedirect(
									PortalUtil.getCurrentURL(
										_httpServletRequest)
								).setParameter(
									"companyId", _company.getCompanyId()
								).buildString());
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			() -> _company.getCompanyId() != _defaultCompanyId,
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> PropsValues.DATABASE_PARTITION_ENABLED,
						dropdownItem -> {
							dropdownItem.putData(
								"action", "copyDBPartitionCompany");
							dropdownItem.putData(
								"copyURL",
								PortletURLBuilder.createRenderURL(
									_liferayPortletResponse
								).setMVCPath(
									"/copy_db_partition_company.jsp"
								).setRedirect(
									PortalUtil.getCurrentURL(
										_httpServletRequest)
								).setParameter(
									"companyId", _company.getCompanyId()
								).setWindowState(
									LiferayWindowState.POP_UP
								).buildString());
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "copy"));
						}
					).add(
						dropdownItem -> {
							dropdownItem.putData("action", "exportInstance");
							dropdownItem.putData(
								"exportURL",
								PortletURLBuilder.createActionURL(
									_liferayPortletResponse
								).setActionName(
									"/portal_instances/export_instance"
								).setParameter(
									"companyId", _company.getCompanyId()
								).buildString());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "export"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			() -> _company.getCompanyId() != _defaultCompanyId,
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "deleteInstance");
							dropdownItem.putData(
								"deleteURL",
								PortletURLBuilder.createActionURL(
									_liferayPortletResponse
								).setActionName(
									"/portal_instances/delete_instance"
								).setParameter(
									"companyId", _company.getCompanyId()
								).buildString());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private final Company _company;
	private final long _defaultCompanyId;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}