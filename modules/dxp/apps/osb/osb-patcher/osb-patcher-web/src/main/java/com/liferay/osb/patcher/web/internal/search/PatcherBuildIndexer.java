/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.search;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherAccountLocalService;
import com.liferay.osb.patcher.service.PatcherBuildLocalService;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalService;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zsolt Balogh
 */
@Component(
	configurationPid = "com.liferay.osb.patcher.configuration.PatcherConfiguration",
	service = Indexer.class
)
public class PatcherBuildIndexer extends BaseIndexer<PatcherBuild> {

	public static final String CLASS_NAME = PatcherBuild.class.getName();

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		long patcherProductVersionId = GetterUtil.getLong(
			searchContext.getAttribute("patcherProductVersionId"));

		if (patcherProductVersionId > 0) {
			contextBooleanFilter.addRequiredTerm(
				"patcherProductVersionId", patcherProductVersionId);
		}

		if (GetterUtil.getBoolean(
				searchContext.getAttribute("buildsViewSearch"))) {

			String key = GetterUtil.getString(
				searchContext.getAttribute("key"));

			if (Validator.isNotNull(key)) {
				contextBooleanFilter.addRequiredTerm("key", key);
			}

			return;
		}

		contextBooleanFilter.addRequiredTerm("childBuild", false);

		if (_patcherConfiguration.patcherScanningEnabled()) {
			contextBooleanFilter.addRequiredTerm(
				"latestSupportTicketBuild", true);
		}
		else {
			contextBooleanFilter.addRequiredTerm("latestKeyBuild", true);
		}

		String accountEntryCode = GetterUtil.getString(
			searchContext.getAttribute("accountEntryCode"));

		if (Validator.isNotNull(accountEntryCode)) {
			contextBooleanFilter.addRequiredTerm(
				"accountEntryCode", accountEntryCode);
		}

		int qaStatus = GetterUtil.getInteger(
			searchContext.getAttribute("qaStatus"));

		if (qaStatus != WorkflowConstants.STATUS_ANY) {
			contextBooleanFilter.addRequiredTerm("qaStatus", qaStatus);
		}

		int status = GetterUtil.getInteger(
			searchContext.getAttribute("status"));

		if (status != WorkflowConstants.STATUS_ANY) {
			contextBooleanFilter.addRequiredTerm("status", status);
		}

		int type = GetterUtil.getInteger(searchContext.getAttribute("type"));

		if (type != PatcherBuildConstants.TYPE_ANY) {
			contextBooleanFilter.addRequiredTerm("type", type);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		if (PatcherUtil.isPatcherTickets(searchContext.getKeywords())) {
			addSearchTerm(searchQuery, searchContext, "patcherBuildName", true);

			return;
		}

		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchTerm(searchQuery, searchContext, "accountEntryCode", true);
		addSearchTerm(searchQuery, searchContext, "patcherBuildName", true);
		addSearchTerm(
			searchQuery, searchContext, "patcherProjectVersionName", true);
		addSearchTerm(searchQuery, searchContext, "supportTicket", true);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties)
		throws ConfigurationException {

		_patcherConfiguration = ConfigurableUtil.createConfigurable(
			PatcherConfiguration.class, properties);
	}

	@Override
	protected void doDelete(PatcherBuild patcherBuild) throws Exception {
		deleteDocument(
			patcherBuild.getCompanyId(), patcherBuild.getPatcherAccountId());
	}

	@Override
	protected Document doGetDocument(PatcherBuild patcherBuild)
		throws Exception {

		Document document = getBaseModelDocument(CLASS_NAME, patcherBuild);

		document.addKeyword(Field.USER_ID, patcherBuild.getUserId());
		document.addKeyword(Field.USER_NAME, patcherBuild.getUserName());

		document.addKeyword("childBuild", patcherBuild.isChildBuild());
		document.addText("comments", patcherBuild.getComments());
		document.addText(
			"downloadURL",
			_patcherConfiguration.patcherBuildDownloadURL() + StringPool.SLASH +
				patcherBuild.getFileName());

		if (Validator.isNotNull(patcherBuild.getFileName())) {
			document.addKeyword("hotfixId", patcherBuild.getHotfixId());
		}

		document.addText("key", patcherBuild.getKey());
		document.addKeyword("keyVersion", patcherBuild.getKeyVersion());
		document.addKeyword("latestKeyBuild", patcherBuild.isLatestKeyBuild());
		document.addKeyword(
			"latestSupportTicketBuild",
			patcherBuild.isLatestSupportTicketBuild());

		PatcherAccount patcherAccount =
			_patcherAccountLocalService.getPatcherAccount(
				patcherBuild.getPatcherAccountId());

		document.addKeyword(
			"accountEntryCode", patcherAccount.getAccountEntryCode());

		document.addKeyword("patcherBuildId", patcherBuild.getPatcherBuildId());
		document.addText("patcherBuildName", patcherBuild.getName());
		document.addKeyword("patcherFixId", patcherBuild.getPatcherFixId());
		document.addKeyword(
			"patcherProductVersionId",
			patcherBuild.getPatcherProductVersionId());
		document.addKeyword(
			"patcherProjectVersionId",
			patcherBuild.getPatcherProjectVersionId());

		PatcherProjectVersion patcherProjectVersion =
			_patcherProjectVersionLocalService.getPatcherProjectVersion(
				patcherBuild.getPatcherProjectVersionId());

		document.addText(
			"patcherProjectVersionName", patcherProjectVersion.getName());

		document.addText("qaComments", patcherBuild.getQaComments());
		document.addKeyword("qaStatus", patcherBuild.getQaStatus());
		document.addText(
			"qaStatusLabel",
			WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus()));
		document.addText("requestKey", patcherBuild.getRequestKey());
		document.addKeyword("status", patcherBuild.getStatus());
		document.addDate("statusDate", patcherBuild.getStatusDate());
		document.addText(
			"statusLabel",
			WorkflowConstants.getStatusLabel(patcherBuild.getStatus()));
		document.addText("supportTicket", patcherBuild.getSupportTicket());
		document.addKeyword(
			"supportTicketVersion", patcherBuild.getSupportTicketVersion());
		document.addKeyword("ticketEntryId", patcherBuild.getTicketEntryId());
		document.addKeyword("type", patcherBuild.getType());

		return document;
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return createSummary(document, Field.ENTRY_CLASS_PK, null);
	}

	@Override
	protected void doReindex(PatcherBuild patcherBuild) throws Exception {
		_indexWriterHelper.updateDocument(
			patcherBuild.getCompanyId(), getDocument(patcherBuild));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		PatcherBuild patcherBuild = _patcherBuildLocalService.fetchPatcherBuild(
			classPK);

		if (patcherBuild != null) {
			doReindex(patcherBuild);
		}
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_patcherBuildLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(PatcherBuild patcherBuild) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(patcherBuild));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index patcher build " + patcherBuild,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherBuildIndexer.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private PatcherAccountLocalService _patcherAccountLocalService;

	@Reference
	private PatcherBuildLocalService _patcherBuildLocalService;

	private volatile PatcherConfiguration _patcherConfiguration;

	@Reference
	private PatcherProjectVersionLocalService
		_patcherProjectVersionLocalService;

}