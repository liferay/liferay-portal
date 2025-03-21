/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.uad.display;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.display.UADDisplay;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = UADDisplay.class)
public class BlogsEntryUADDisplay extends BaseBlogsEntryUADDisplay {

	@Override
	public String[] getColumnFieldNames() {
		return new String[] {"title", "subtitle", "description", "content"};
	}

	@Override
	public String getEditURL(
			BlogsEntry blogsEntry, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		if (blogsEntry.isInTrash()) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.createLiferayPortletURL(
			liferayPortletResponse,
			portal.getControlPanelPlid(liferayPortletRequest),
			PortletProviderUtil.getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.VIEW),
			PortletRequest.RENDER_PHASE
		).setMVCRenderCommandName(
			"/blogs/edit_entry"
		).setRedirect(
			portal.getCurrentURL(liferayPortletRequest)
		).setParameter(
			"entryId", blogsEntry.getEntryId()
		).buildString();
	}

	@Reference
	protected Portal portal;

}