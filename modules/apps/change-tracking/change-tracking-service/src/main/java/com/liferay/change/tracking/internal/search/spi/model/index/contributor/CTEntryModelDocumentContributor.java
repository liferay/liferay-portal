/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.spi.model.index.contributor;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "indexer.class.name=com.liferay.change.tracking.model.CTEntry",
	service = ModelDocumentContributor.class
)
public class CTEntryModelDocumentContributor
	implements ModelDocumentContributor<CTEntry> {

	@Override
	public void contribute(Document document, CTEntry ctEntry) {
		document.addKeyword(Field.COMPANY_ID, ctEntry.getCompanyId());
		document.addDate(Field.CREATE_DATE, ctEntry.getCreateDate());
		document.addDate(Field.MODIFIED_DATE, ctEntry.getModifiedDate());

		User user = _userLocalService.fetchUser(ctEntry.getUserId());

		if (user != null) {
			document.addKeyword(Field.USER_ID, user.getUserId());
			document.addText(Field.USER_NAME, user.getFullName());
		}

		document.addKeyword("ctCollectionId", ctEntry.getCtCollectionId());

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctEntry.getCtCollectionId());

		if (ctCollection != null) {
			document.addKeywordSortable(
				"ctCollectionName", ctCollection.getName());
			document.addKeyword("ctCollectionStatus", ctCollection.getStatus());
			document.addLocalizedKeyword(
				"ctCollectionStatusLabel",
				_localization.getLocalizationMap(
					_language.getAvailableLocales(), LocaleUtil.getDefault(),
					WorkflowConstants.getStatusLabel(ctCollection.getStatus())),
				true, true);

			if ((ctCollection.getStatus() ==
					WorkflowConstants.STATUS_APPROVED) ||
				(ctCollection.getStatus() ==
					WorkflowConstants.STATUS_SCHEDULED)) {

				document.addDateSortable(
					"ctCollectionStatusDate",
					_getCTCollectionStatusDate(ctCollection));

				User ctCollectionStatusUser = _userLocalService.fetchUser(
					ctCollection.getStatusByUserId());

				if (ctCollectionStatusUser != null) {
					document.addKeyword(
						"ctCollectionStatusUserId",
						ctCollectionStatusUser.getUserId());
					document.addKeywordSortable(
						"ctCollectionStatusUserName",
						ctCollectionStatusUser.getFullName());
				}
			}
		}

		document.addKeyword("modelClassNameId", ctEntry.getModelClassNameId());
		document.addKeyword("modelClassPK", ctEntry.getModelClassPK());

		_indexModelAttributes(document, ctEntry);
	}

	private <T extends BaseModel<T>> Locale[] _getAvailableLocales(
		T model, CTDisplayRenderer ctDisplayRenderer) {

		String[] languageIds = new String[0];

		if (model != null) {
			languageIds = ctDisplayRenderer.getAvailableLanguageIds(model);
		}

		if (ArrayUtil.isNotEmpty(languageIds)) {
			return LocaleUtil.fromLanguageIds(languageIds);
		}

		Set<Locale> locales = _language.getAvailableLocales();

		return locales.toArray(new Locale[0]);
	}

	private Map<Locale, String> _getChangeTypeLabelMap(
		Locale[] locales, int changeType) {

		Map<Locale, String> map = new HashMap<>();

		String changeTypeLabel = CTConstants.getCTChangeTypeLabel(changeType);

		for (Locale locale : locales) {
			map.put(locale, _language.get(locale, changeTypeLabel));
		}

		return map;
	}

	private Date _getCTCollectionStatusDate(CTCollection ctCollection) {
		if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			return ctCollection.getStatusDate();
		}

		try {
			SchedulerResponse schedulerResponse =
				_schedulerEngineHelper.getScheduledJob(
					StringBundler.concat(
						ctCollection.getCtCollectionId(), StringPool.AT,
						ctCollection.getCompanyId()),
					CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
					StorageType.PERSISTED);

			if (schedulerResponse != null) {
				return _schedulerEngineHelper.getStartTime(schedulerResponse);
			}
		}
		catch (SchedulerException schedulerException) {
			if (_log.isWarnEnabled()) {
				_log.warn(schedulerException);
			}
		}

		return null;
	}

	private <T extends BaseModel<T>> Group _getGroup(
		long ctCollectionId, T model) {

		long groupId = 0;

		if (model instanceof GroupedModel) {
			GroupedModel groupedModel = (GroupedModel)model;

			groupId = groupedModel.getGroupId();
		}
		else if (model instanceof JournalArticleLocalization) {
			JournalArticleLocalization journalArticleLocalization =
				(JournalArticleLocalization)model;

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollectionId)) {

				JournalArticle journalArticle =
					_journalArticleLocalService.getJournalArticle(
						journalArticleLocalization.getArticlePK());

				groupId = journalArticle.getGroupId();
			}
			catch (PortalException portalException) {
				throw new RuntimeException(portalException);
			}
		}
		else {
			Map<String, Object> modelAttributes = model.getModelAttributes();

			if (modelAttributes.get("groupId") instanceof Long groupIdValue) {
				groupId = groupIdValue;
			}
			else {
				if (!(modelAttributes.get("plid") instanceof Long plid)) {
					return null;
				}

				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								ctCollectionId)) {

					Layout layout = _layoutLocalService.fetchLayout(plid);

					if (layout != null) {
						groupId = layout.getGroupId();
					}
				}
			}
		}

		if (groupId == 0) {
			return null;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			return _groupLocalService.fetchGroup(groupId);
		}
	}

	private <T extends BaseModel<T>> Map<Locale, String> _getTitleMap(
		long ctCollectionId, CTEntry ctEntry, Locale[] locales) {

		Map<Locale, String> map = new HashMap<>();

		if (ctEntry.getChangeType() == CTConstants.CT_CHANGE_TYPE_DELETION) {
			ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;
		}

		for (Locale locale : locales) {
			map.put(
				locale,
				_ctDisplayRendererRegistry.getTitle(
					ctCollectionId, ctEntry, locale));
		}

		return map;
	}

	private Map<Locale, String> _getTypeNameMap(
		Locale[] locales, long modelClassNameId) {

		Map<Locale, String> map = new HashMap<>();

		for (Locale locale : locales) {
			map.put(
				locale,
				_ctDisplayRendererRegistry.getTypeName(
					locale, modelClassNameId));
		}

		return map;
	}

	private <T extends BaseModel<T>> void _indexModelAttributes(
		Document document, CTEntry ctEntry) {

		long ctCollectionId = ctEntry.getCtCollectionId();

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if (ctCollection != null) {
			try {
				ctCollectionId = _ctDisplayRendererRegistry.getCtCollectionId(
					ctCollection, ctEntry);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		T model = _ctDisplayRendererRegistry.fetchCTModel(
			ctCollectionId, CTSQLModeThreadLocal.CTSQLMode.DEFAULT,
			ctEntry.getModelClassNameId(), ctEntry.getModelClassPK());

		int changeType = _ctDisplayRendererRegistry.getChangeType(
			ctEntry, model);

		document.addKeyword("changeType", changeType);

		Locale[] locales = _getAvailableLocales(
			model,
			_ctDisplayRendererRegistry.getCTDisplayRenderer(
				ctEntry.getModelClassNameId()));

		document.addLocalizedText(
			"changeTypeLabel", _getChangeTypeLabelMap(locales, changeType),
			true);

		document.addLocalizedText(
			"typeName", _getTypeNameMap(locales, ctEntry.getModelClassNameId()),
			true);

		if (model == null) {
			return;
		}

		Group group = _getGroup(ctCollectionId, model);

		if (group != null) {
			document.addKeyword(Field.GROUP_ID, group.getGroupId());

			Map<Locale, String> groupNameMap = null;

			try {
				groupNameMap = group.getDescriptiveNameMap();
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				groupNameMap = group.getNameMap();
			}

			document.addLocalizedKeyword(
				"groupName", groupNameMap, false, true);
		}

		document.addLocalizedText(
			Field.TITLE, _getTitleMap(ctCollectionId, ctEntry, locales), true);

		document.addKeyword(
			"hideable",
			_ctDisplayRendererRegistry.isHideable(
				model, ctEntry.getModelClassNameId()));

		Map<String, Object> modelAttributes = model.getModelAttributes();

		if (modelAttributes.get("plid") instanceof Long plid) {
			document.addKeyword("plid", plid);
		}

		if (modelAttributes.get("status") instanceof Integer status) {
			document.addKeyword(Field.STATUS, status);
			document.addLocalizedKeyword(
				"statusLabel",
				_localization.getLocalizationMap(
					_language.getAvailableLocales(), LocaleUtil.getDefault(),
					WorkflowConstants.getStatusLabel(status)),
				true, true);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTEntryModelDocumentContributor.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private UserLocalService _userLocalService;

}