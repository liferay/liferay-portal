/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.frontend.taglib.form.navigator;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "form.navigator.entry.order:Integer=80",
	service = FormNavigatorEntry.class
)
public class JournalDisplayPageFormNavigatorEntry
	extends BaseJournalFormNavigatorEntry {

	@Override
	public String getKey() {
		return "display-page";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, JournalArticle article) {
		if (isDepotOrGlobalScopeArticle(article) ||
			((article == null) &&
			 _isEditDepotOrGlobalScopeStructureDefaultValues())) {

			return false;
		}

		return true;
	}

	@Override
	protected String getJspPath() {
		return "/article/asset_display_page.jsp";
	}

	private boolean _isEditDepotOrGlobalScopeStructureDefaultValues() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		long classNameId = ParamUtil.getLong(portletRequest, "classNameId");

		if (classNameId != _portal.getClassNameId(DDMStructure.class)) {
			return false;
		}

		long classPK = ParamUtil.getLong(portletRequest, "classPK");

		if (classPK == 0) {
			return false;
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchDDMStructure(
			classPK);

		long ddmStructureId = ParamUtil.getLong(
			portletRequest, "ddmStructureId");

		if ((ddmStructure == null) && (ddmStructureId > 0)) {
			ddmStructure = _ddmStructureLocalService.fetchStructure(
				ddmStructureId);
		}

		if ((ddmStructure == null) ||
			!Objects.equals(
				ParamUtil.getLong(portletRequest, "groupId"),
				ddmStructure.getGroupId()) ||
			!Objects.equals(
				ddmStructure.getClassNameId(),
				_portal.getClassNameId(JournalArticle.class))) {

			return false;
		}

		Group group = _groupLocalService.fetchGroup(ddmStructure.getGroupId());

		if (group == null) {
			return false;
		}

		if (group.isCompany() || group.isDepot()) {
			return true;
		}

		return false;
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.journal.web)")
	private ServletContext _servletContext;

}