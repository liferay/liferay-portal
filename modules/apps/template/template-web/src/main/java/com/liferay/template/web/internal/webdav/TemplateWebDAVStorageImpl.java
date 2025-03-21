/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.webdav;

import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.webdav.DDMWebDAV;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.webdav.Resource;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.webdav.BaseWebDAVStorageImpl;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.model.TemplateEntry;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"webdav.storage.token=template"
	},
	service = WebDAVStorage.class
)
public class TemplateWebDAVStorageImpl extends BaseWebDAVStorageImpl {

	@Override
	public int deleteResource(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		return _ddmWebDAV.deleteResource(
			webDAVRequest, getRootPath(), getToken(), 0);
	}

	@Override
	public Resource getResource(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		return _ddmWebDAV.getResource(
			webDAVRequest, getRootPath(), getToken(), 0);
	}

	@Override
	public List<Resource> getResources(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		try {
			String[] pathArray = webDAVRequest.getPathArray();

			if (pathArray.length == 2) {
				return _getFolders(webDAVRequest);
			}
			else if (pathArray.length == 3) {
				return _getTemplates(webDAVRequest);
			}

			return new ArrayList<>();
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public int putResource(WebDAVRequest webDAVRequest) throws WebDAVException {
		return _ddmWebDAV.putResource(
			webDAVRequest, getRootPath(), getToken(), 0);
	}

	private List<Resource> _getFolders(WebDAVRequest webDAVRequest) {
		return ListUtil.fromArray(
			_ddmWebDAV.toResource(
				webDAVRequest, DDMWebDAV.TYPE_TEMPLATES, getRootPath(), true));
	}

	private List<Resource> _getTemplates(WebDAVRequest webDAVRequest) {
		return TransformUtil.transform(
			ListUtil.concat(
				_ddmTemplateLocalService.getTemplates(
					webDAVRequest.getCompanyId(),
					new long[] {webDAVRequest.getGroupId()}, null, null,
					_portal.getClassNameId(TemplateEntry.class), -1, -1, null),
				_ddmTemplateLocalService.getTemplates(
					webDAVRequest.getCompanyId(),
					new long[] {webDAVRequest.getGroupId()}, null, null,
					_portal.getClassNameId(PortletDisplayTemplate.class), -1,
					-1, null)),
			ddmTemplate -> _ddmWebDAV.toResource(
				webDAVRequest, ddmTemplate, getRootPath(), true));
	}

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DDMWebDAV _ddmWebDAV;

	@Reference
	private Portal _portal;

}