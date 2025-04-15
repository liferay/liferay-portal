/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.webdav;

import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.webdav.DDMWebDAV;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.webdav.Resource;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.webdav.BaseWebDAVStorageImpl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Juan Fernández
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"webdav.storage.token=journal"
	},
	service = WebDAVStorage.class
)
public class JournalWebDAVStorageImpl extends BaseWebDAVStorageImpl {

	@Override
	public int deleteResource(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		return _ddmWebDAV.deleteResource(
			webDAVRequest, getRootPath(), getToken(),
			_portal.getClassNameId(JournalArticle.class));
	}

	@Override
	public Resource getResource(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		return _ddmWebDAV.getResource(
			webDAVRequest, getRootPath(), getToken(),
			_portal.getClassNameId(JournalArticle.class));
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
				String type = pathArray[2];

				if (type.equals(DDMWebDAV.TYPE_STRUCTURES)) {
					return _getStructures(webDAVRequest);
				}
				else if (type.equals(DDMWebDAV.TYPE_TEMPLATES)) {
					return _getTemplates(webDAVRequest);
				}
			}

			return new ArrayList<>();
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public String getToken() {
		return "journal";
	}

	@Override
	public int putResource(WebDAVRequest webDAVRequest) throws WebDAVException {
		return _ddmWebDAV.putResource(
			webDAVRequest, getRootPath(), getToken(),
			_portal.getClassNameId(JournalArticle.class));
	}

	private List<Resource> _getFolders(WebDAVRequest webDAVRequest)
		throws Exception {

		return ListUtil.fromArray(
			_ddmWebDAV.toResource(
				webDAVRequest, DDMWebDAV.TYPE_STRUCTURES, getRootPath(), true),
			_ddmWebDAV.toResource(
				webDAVRequest, DDMWebDAV.TYPE_TEMPLATES, getRootPath(), true));
	}

	private List<Resource> _getStructures(WebDAVRequest webDAVRequest)
		throws Exception {

		return TransformUtil.transform(
			_journalFolderService.getDDMStructures(
				new long[] {webDAVRequest.getGroupId()},
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalFolderConstants.RESTRICTION_TYPE_INHERIT),
			ddmStructure -> _ddmWebDAV.toResource(
				webDAVRequest, ddmStructure, getRootPath(), true));
	}

	private List<Resource> _getTemplates(WebDAVRequest webDAVRequest)
		throws Exception {

		return TransformUtil.transform(
			_ddmTemplateLocalService.getTemplatesByStructureClassNameId(
				webDAVRequest.getGroupId(),
				_portal.getClassNameId(JournalArticle.class),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null),
			ddmTemplate -> _ddmWebDAV.toResource(
				webDAVRequest, ddmTemplate, getRootPath(), true));
	}

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DDMWebDAV _ddmWebDAV;

	@Reference
	private JournalFolderService _journalFolderService;

	@Reference
	private Portal _portal;

}