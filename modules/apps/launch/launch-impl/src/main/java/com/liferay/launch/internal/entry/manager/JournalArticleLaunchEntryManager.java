/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.manager;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "launch.entry.manager.class.name=com.liferay.journal.model.JournalArticle",
	service = LaunchEntryManager.class
)
public class JournalArticleLaunchEntryManager implements LaunchEntryManager {

	@Override
	public Draft addDraft(long classPK, long launchSetId, String versionKey)
		throws PortalException {

		JournalArticleResource journalArticleResource =
			_journalArticleResourceLocalService.getJournalArticleResource(
				classPK);

		JournalArticle journalArticle = _journalArticleLocalService.getArticle(
			journalArticleResource.getGroupId(),
			journalArticleResource.getArticleId(),
			GetterUtil.getDouble(versionKey));

		ServiceContext serviceContext = _getServiceContext(
			WorkflowConstants.ACTION_SAVE_DRAFT);

		return _toDraft(
			_journalArticleLocalService.addArticle(
				null, serviceContext.getUserId(), journalArticle.getGroupId(),
				journalArticle.getFolderId(), journalArticle.getTitleMap(),
				journalArticle.getDescriptionMap(), journalArticle.getContent(),
				journalArticle.getDDMStructureId(),
				journalArticle.getDDMTemplateKey(), serviceContext));
	}

	@Override
	public void deleteDraft(long classPK, long draftId) throws PortalException {
		JournalArticle journalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				draftId, WorkflowConstants.STATUS_ANY, false);

		if (journalArticle == null) {
			return;
		}

		_journalArticleLocalService.deleteArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			ServiceContextThreadLocal.getServiceContext());
	}

	@Override
	public Draft fetchDraft(long classPK, long draftId) {
		return _toDraft(
			_journalArticleLocalService.fetchLatestArticle(
				draftId, WorkflowConstants.STATUS_ANY, false));
	}

	@Override
	public Item fetchItem(long classPK) {
		JournalArticle journalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				classPK, WorkflowConstants.STATUS_APPROVED);

		if (journalArticle == null) {
			return null;
		}

		return new Item() {

			@Override
			public long getGroupId() {
				return journalArticle.getGroupId();
			}

			@Override
			public Serializable getPrimaryKey() {
				return journalArticle.getId();
			}

			@Override
			public String getTitle(Locale locale) {
				return journalArticle.getTitle(locale);
			}

			@Override
			public String getTypeName(Locale locale) {
				DDMStructure ddmStructure = journalArticle.getDDMStructure();

				return ddmStructure.getName(locale);
			}

		};
	}

	@Override
	public Version fetchPublishedVersion(long classPK) {
		return _toVersion(
			_journalArticleLocalService.fetchLatestArticle(
				classPK, WorkflowConstants.STATUS_APPROVED));
	}

	@Override
	public Version fetchVersion(long classPK, String versionKey) {
		JournalArticleResource journalArticleResource =
			_journalArticleResourceLocalService.fetchJournalArticleResource(
				classPK);

		if (journalArticleResource == null) {
			return null;
		}

		return _toVersion(
			_journalArticleLocalService.fetchArticle(
				journalArticleResource.getGroupId(),
				journalArticleResource.getArticleId(),
				GetterUtil.getDouble(versionKey)));
	}

	@Override
	public long getClassPK(Map<String, String> selectedItemData) {
		return GetterUtil.getLong(selectedItemData.get("classPK"));
	}

	@Override
	public ItemSelectorCriterion getItemSelectorCriterion() {
		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		infoItemItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		infoItemItemSelectorCriterion.setItemType(
			JournalArticle.class.getName());

		return infoItemItemSelectorCriterion;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "web-content");
	}

	@Override
	public Class<?> getModelClass() {
		return JournalArticle.class;
	}

	@Override
	public List<Version> getVersions(long classPK) {
		return TransformUtil.transform(
			_journalArticleLocalService.getArticlesByResourcePrimKey(classPK),
			this::_toVersion);
	}

	@Override
	public Version publishDraft(long classPK, long draftId)
		throws PortalException {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				classPK, WorkflowConstants.STATUS_APPROVED);

		JournalArticle draftJournalArticle =
			_journalArticleLocalService.getLatestArticle(
				draftId, WorkflowConstants.STATUS_ANY, false);

		ServiceContext serviceContext = _getServiceContext(
			WorkflowConstants.ACTION_PUBLISH);

		journalArticle = _journalArticleLocalService.updateArticle(
			serviceContext.getUserId(), journalArticle.getGroupId(),
			journalArticle.getFolderId(), journalArticle.getArticleId(),
			journalArticle.getVersion(), draftJournalArticle.getTitleMap(),
			draftJournalArticle.getDescriptionMap(),
			draftJournalArticle.getContent(), journalArticle.getLayoutUuid(),
			serviceContext);

		_journalArticleLocalService.deleteArticle(
			draftJournalArticle.getGroupId(),
			draftJournalArticle.getArticleId(), serviceContext);

		return _toVersion(journalArticle);
	}

	private String _getEditURL(
		JournalArticle journalArticle, String redirect,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				JournalPortletKeys.JOURNAL)
		).setMVCRenderCommandName(
			"/journal/edit_article"
		).setRedirect(
			redirect
		).setBackURL(
			redirect
		).setParameter(
			"articleId", journalArticle.getArticleId()
		).setParameter(
			"backURLTitle",
			_language.get(LocaleThreadLocal.getThemeDisplayLocale(), "launches")
		).setParameter(
			"folderId", journalArticle.getFolderId()
		).setParameter(
			"groupId", journalArticle.getGroupId()
		).setParameter(
			"version", journalArticle.getVersion()
		).buildString();
	}

	private ServiceContext _getServiceContext(int workflowAction) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		serviceContext = (ServiceContext)serviceContext.clone();

		serviceContext.setWorkflowAction(workflowAction);

		return serviceContext;
	}

	private Draft _toDraft(JournalArticle journalArticle) {
		if (journalArticle == null) {
			return null;
		}

		return new Draft() {

			@Override
			public long getDraftId() {
				return journalArticle.getResourcePrimKey();
			}

			@Override
			public String getEditURL(
				String redirect,
				RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

				return _getEditURL(
					journalArticle, redirect, requestBackedPortletURLFactory);
			}

			@Override
			public Date getModifiedDate() {
				return journalArticle.getModifiedDate();
			}

			@Override
			public BaseModel<?> getPreviewBaseModel() {
				return journalArticle;
			}

			@Override
			public int getStatus() {
				return journalArticle.getStatus();
			}

			@Override
			public String getUserName() {
				return journalArticle.getUserName();
			}

		};
	}

	private Version _toVersion(JournalArticle journalArticle) {
		if (journalArticle == null) {
			return null;
		}

		return new Version() {

			@Override
			public String getLabel(Locale locale) {
				return String.valueOf(journalArticle.getVersion());
			}

			@Override
			public Date getModifiedDate() {
				return journalArticle.getModifiedDate();
			}

			@Override
			public BaseModel<?> getPreviewBaseModel() {
				return journalArticle;
			}

			@Override
			public int getStatus() {
				return journalArticle.getStatus();
			}

			@Override
			public String getUserName() {
				return journalArticle.getUserName();
			}

			@Override
			public String getVersionKey() {
				return String.valueOf(journalArticle.getVersion());
			}

		};
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

	@Reference
	private Language _language;

}