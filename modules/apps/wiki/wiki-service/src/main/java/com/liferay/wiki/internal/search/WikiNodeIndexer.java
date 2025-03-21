/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.search;

import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = Indexer.class)
public class WikiNodeIndexer extends BaseIndexer<WikiNode> {

	public static final String CLASS_NAME = WikiNode.class.getName();

	public WikiNodeIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.UID);
		setFilterSearch(false);
		setPermissionAware(false);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, String entryClassName,
			long entryClassPK, String actionId)
		throws Exception {

		return _wikiNodeModelResourcePermission.contains(
			permissionChecker, _wikiNodeLocalService.getNode(entryClassPK),
			ActionKeys.VIEW);
	}

	@Override
	protected void doDelete(WikiNode wikiNode) throws Exception {
		deleteDocument(
			wikiNode.getCompanyId(), "UID=" + uidFactory.getUID(wikiNode));
	}

	@Override
	protected Document doGetDocument(WikiNode wikiNode) throws Exception {
		Document document = getBaseModelDocument(CLASS_NAME, wikiNode);

		uidFactory.setUID(wikiNode, document);

		document.addText(Field.DESCRIPTION, wikiNode.getDescription());

		String title = wikiNode.getName();

		if (wikiNode.isInTrash()) {
			title = _trashHelper.getOriginalTitle(title);
		}

		document.addText(Field.TITLE, title);

		return document;
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return null;
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_wikiNodeLocalService.getNode(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexEntries(companyId);
	}

	@Override
	protected void doReindex(WikiNode wikiNode) throws Exception {
		Document document = getDocument(wikiNode);

		if (wikiNode.isInTrash()) {
			_deleteDocument(wikiNode);

			return;
		}

		_indexWriterHelper.updateDocument(wikiNode.getCompanyId(), document);
	}

	@Reference
	protected UIDFactory uidFactory;

	private void _deleteDocument(WikiNode wikiNode) throws Exception {
		_indexWriterHelper.deleteDocument(
			wikiNode.getCompanyId(), uidFactory.getUID(wikiNode),
			isCommitImmediately());
	}

	private void _reindexEntries(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_wikiNodeLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property property = PropertyFactoryUtil.forName("status");

				dynamicQuery.add(
					property.eq(WorkflowConstants.STATUS_APPROVED));
			});
		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(WikiNode node) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(node));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index wiki node " + node.getNodeId(),
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiNodeIndexer.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiNode)")
	private ModelResourcePermission<WikiNode> _wikiNodeModelResourcePermission;

}