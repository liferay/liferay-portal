/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.manager;

import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;

import java.util.List;

/**
 * @author Drew Brokke
 */
public class WebsiteContactInfoManager extends BaseContactInfoManager<Website> {

	public WebsiteContactInfoManager(
		String className, long classPK, WebsiteLocalService websiteLocalService,
		WebsiteService websiteService) {

		_className = className;
		_classPK = classPK;
		_websiteLocalService = websiteLocalService;
		_websiteService = websiteService;
	}

	@Override
	protected Website construct(ActionRequest actionRequest) throws Exception {
		long websiteId = ParamUtil.getLong(actionRequest, "primaryKey");

		String url = ParamUtil.getString(actionRequest, "websiteUrl");
		long listTypeId = ParamUtil.getLong(actionRequest, "websiteListTypeId");
		boolean primary = ParamUtil.getBoolean(actionRequest, "websitePrimary");

		Website website = _websiteLocalService.createWebsite(websiteId);

		website.setUrl(url);
		website.setListTypeId(listTypeId);
		website.setPrimary(primary);

		return website;
	}

	@Override
	protected Website doAdd(Website website) throws Exception {
		return _websiteService.addWebsite(
			website.getExternalReferenceCode(), _className, _classPK,
			website.getUrl(), website.getListTypeId(), website.isPrimary(),
			new ServiceContext());
	}

	@Override
	protected void doDelete(long websiteId) throws Exception {
		_websiteService.deleteWebsite(websiteId);
	}

	@Override
	protected void doUpdate(Website website) throws Exception {
		_websiteService.updateWebsite(
			website.getExternalReferenceCode(), website.getWebsiteId(),
			website.getUrl(), website.getListTypeId(), website.isPrimary());
	}

	@Override
	protected Website get(long websiteId) throws Exception {
		return _websiteService.getWebsite(websiteId);
	}

	@Override
	protected List<Website> getAll() throws Exception {
		return _websiteService.getWebsites(_className, _classPK);
	}

	@Override
	protected long getPrimaryKey(Website website) {
		return website.getWebsiteId();
	}

	@Override
	protected boolean isPrimary(Website website) {
		return website.isPrimary();
	}

	@Override
	protected void setPrimary(Website website, boolean primary) {
		website.setPrimary(primary);
	}

	private final String _className;
	private final long _classPK;
	private final WebsiteLocalService _websiteLocalService;
	private final WebsiteService _websiteService;

}