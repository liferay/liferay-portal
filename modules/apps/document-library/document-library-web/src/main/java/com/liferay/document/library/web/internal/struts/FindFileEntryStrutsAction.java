/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.struts;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.struts.FindStrutsAction;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 * @author Ryan Park
 */
@Component(
	property = "path=/document_library/find_file_entry",
	service = StrutsAction.class
)
public class FindFileEntryStrutsAction extends FindStrutsAction {

	@Override
	public long getGroupId(long primaryKey) throws Exception {
		FileEntry fileEntry = _dlAppLocalService.getFileEntry(primaryKey);

		return fileEntry.getGroupId();
	}

	@Override
	public String getPrimaryKeyParameterName() {
		return "fileEntryId";
	}

	@Override
	public void setPrimaryKeyParameter(PortletURL portletURL, long primaryKey) {
		portletURL.setParameter(
			getPrimaryKeyParameterName(), String.valueOf(primaryKey));
	}

	@Override
	protected void addRequiredParameters(
		HttpServletRequest httpServletRequest, String portletId,
		PortletURL portletURL) {

		portletURL.setParameter(
			"mvcRenderCommandName", "/document_library/view_file_entry");
	}

	@Override
	protected PortletLayoutFinder getPortletLayoutFinder() {
		return _portletPageFinder;
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private PortletLayoutFinder _portletPageFinder;

}