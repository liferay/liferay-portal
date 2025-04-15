/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.struts;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.struts.FindStrutsAction;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(property = "path=/blogs/find_entry", service = StrutsAction.class)
public class FindEntryStrutsAction extends FindStrutsAction {

	@Override
	public long getGroupId(long primaryKey) throws Exception {
		BlogsEntry entry = _blogsEntryLocalService.getEntry(primaryKey);

		return entry.getGroupId();
	}

	@Override
	public String getPrimaryKeyParameterName() {
		return "entryId";
	}

	@Override
	public void setPrimaryKeyParameter(PortletURL portletURL, long primaryKey)
		throws Exception {

		BlogsEntry entry = _blogsEntryLocalService.getEntry(primaryKey);

		if (Validator.isNotNull(entry.getUrlTitle())) {
			portletURL.setParameter("urlTitle", entry.getUrlTitle());
		}
		else {
			portletURL.setParameter(
				"entryId", String.valueOf(entry.getEntryId()));
		}
	}

	@Override
	protected void addRequiredParameters(
		HttpServletRequest httpServletRequest, String portletId,
		PortletURL portletURL) {

		String mvcRenderCommandName = null;

		if (portletId.equals(BlogsPortletKeys.BLOGS_ADMIN)) {
			mvcRenderCommandName = "/blogs_admin/view_entry";
		}
		else {
			mvcRenderCommandName = "/blogs/view_entry";
		}

		portletURL.setParameter("mvcRenderCommandName", mvcRenderCommandName);
	}

	@Override
	protected PortletLayoutFinder getPortletLayoutFinder() {
		return _portletLayoutFinder;
	}

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference(target = "(model.class.name=com.liferay.blogs.model.BlogsEntry)")
	private PortletLayoutFinder _portletLayoutFinder;

}