/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.util.PublicationsPortletURLUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.learn.LearnMessage;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * @author Samuel Trong Tran
 */
public class ViewConflictsDisplayContext {

	public ViewConflictsDisplayContext(
		long activeCtCollectionId,
		Map<Long, List<ConflictInfo>> conflictInfoMap,
		CTCollection ctCollection,
		CTCollectionLocalService ctCollectionLocalService,
		CTDisplayRendererRegistry ctDisplayRendererRegistry,
		CTEntryLocalService ctEntryLocalService,
		CTSettingsConfigurationHelper ctSettingsConfigurationHelper,
		boolean hasUnapprovedChanges, Language language, Portal portal,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_activeCtCollectionId = activeCtCollectionId;
		_conflictInfoMap = conflictInfoMap;
		_ctCollection = ctCollection;
		_ctCollectionLocalService = ctCollectionLocalService;
		_ctDisplayRendererRegistry = ctDisplayRendererRegistry;
		_ctEntryLocalService = ctEntryLocalService;
		_ctSettingsConfigurationHelper = ctSettingsConfigurationHelper;
		_hasUnapprovedChanges = hasUnapprovedChanges;
		_language = language;
		_portal = portal;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = portal.getHttpServletRequest(renderRequest);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public CTCollection getCtCollection() {
		return _ctCollection;
	}

	public Map<String, Object> getReactData() {
		JSONArray resolvedConflictsJSONArray =
			JSONFactoryUtil.createJSONArray();
		JSONArray unresolvedConflictsJSONArray =
			JSONFactoryUtil.createJSONArray();

		if (_conflictInfoMap != null) {
			for (Map.Entry<Long, List<ConflictInfo>> entry :
					_conflictInfoMap.entrySet()) {

				for (ConflictInfo conflictInfo : entry.getValue()) {
					JSONObject jsonObject = _getConflictJSONObject(
						conflictInfo, entry.getKey());

					if (conflictInfo.isResolved()) {
						resolvedConflictsJSONArray.put(jsonObject);
					}
					else {
						unresolvedConflictsJSONArray.put(jsonObject);
					}
				}
			}
		}

		return HashMapBuilder.<String, Object>put(
			"hasUnapprovedChanges", _hasUnapprovedChanges
		).put(
			"isEmpty", _ctCollection.isEmpty()
		).put(
			"learnLink",
			() -> {
				LearnMessage learnMessage = LearnMessageUtil.getLearnMessage(
					"manually-resolving-conflicts",
					_themeDisplay.getLanguageId(), "change-tracking-web");

				return JSONUtil.put(
					"message", learnMessage.getMessage()
				).put(
					"url", learnMessage.getURL()
				);
			}
		).put(
			"learnResolvingConflictsLink",
			() -> {
				LearnMessage learnMessage = LearnMessageUtil.getLearnMessage(
					"resolving-conflicts", _themeDisplay.getLanguageId(),
					"change-tracking-web");

				return JSONUtil.put(
					"message", learnMessage.getMessage()
				).put(
					"url", learnMessage.getURL()
				);
			}
		).put(
			"publishURL",
			() -> PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"/change_tracking/publish_ct_collection"
			).setParameter(
				"ctCollectionId", _ctCollection.getCtCollectionId()
			).setParameter(
				"name", _ctCollection.getName()
			).buildString()
		).put(
			"redirect", getRedirect()
		).put(
			"resolvedConflicts", resolvedConflictsJSONArray
		).put(
			"schedule", ParamUtil.getBoolean(_renderRequest, "schedule")
		).put(
			"scheduleURL",
			() -> PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"/change_tracking/schedule_publication"
			).setRedirect(
				getRedirect()
			).setParameter(
				"ctCollectionId", _ctCollection.getCtCollectionId()
			).buildString()
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).put(
			"timeZone",
			() -> {
				TimeZone timeZone = _themeDisplay.getTimeZone();

				return timeZone.getID();
			}
		).put(
			"unapprovedChangesAllowed",
			_ctSettingsConfigurationHelper.isUnapprovedChangesAllowed(
				_themeDisplay.getCompanyId())
		).put(
			"unresolvedConflicts", unresolvedConflictsJSONArray
		).put(
			"unscheduleURL",
			() -> {
				if (_ctCollection.getStatus() !=
						WorkflowConstants.STATUS_SCHEDULED) {

					return null;
				}

				return PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/change_tracking/unschedule_publication"
				).setRedirect(
					() -> {
						String namespace = _portal.getPortletNamespace(
							CTPortletKeys.PUBLICATIONS);

						return HttpComponentsUtil.addParameter(
							_portal.getCurrentURL(_renderRequest),
							namespace + "schedule", true);
					}
				).setParameter(
					"ctCollectionId", _ctCollection.getCtCollectionId()
				).buildString();
			}
		).build();
	}

	public String getRedirect() {
		String redirect = ParamUtil.getString(_renderRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/change_tracking/view_changes"
		).setParameter(
			"ctCollectionId", _ctCollection.getCtCollectionId()
		).buildString();
	}

	private <T extends BaseModel<T>> void _checkModifiedJournalArticlesInTrash(
		ConflictInfo conflictInfo, CTEntry ctEntry, JSONArray jsonArray) {

		if (!Objects.equals(
				_portal.getClassName(ctEntry.getModelClassNameId()),
				JournalArticle.class.getName())) {

			return;
		}

		CTDisplayRenderer<T> ctDisplayRenderer =
			_ctDisplayRendererRegistry.getCTDisplayRenderer(
				ctEntry.getModelClassNameId());

		T model = _ctDisplayRendererRegistry.fetchCTModel(
			ctEntry.getCtCollectionId(),
			_ctDisplayRendererRegistry.getCTSQLMode(
				ctEntry.getCtCollectionId(), ctEntry),
			ctEntry.getModelClassNameId(), conflictInfo.getSourcePrimaryKey());

		if (model == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			model = ctDisplayRenderer.fetchLatestVersionedModel(model);

			if (model == null) {
				return;
			}

			Map<String, Object> modelAttributes = model.getModelAttributes();

			int status = GetterUtil.getInteger(modelAttributes.get("status"));

			if (status != WorkflowConstants.STATUS_IN_TRASH) {
				return;
			}

			jsonArray.put(
				JSONUtil.put(
					"href",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/change_tracking/restore_trash_entries"
					).setRedirect(
						_portal.getCurrentURL(_renderRequest)
					).setParameter(
						"modelClassNameId", ctEntry.getModelClassNameId()
					).setParameter(
						"modelClassPK", modelAttributes.get("resourcePrimKey")
					).buildString()
				).put(
					"label",
					_language.get(
						_httpServletRequest, "restore-from-recycle-bin")
				).put(
					"symbol", "restore"
				));
		}
	}

	private JSONObject _createEditActionJSONObject(
		String confirmationMessage, long ctCollectionId, String editURL,
		String label) {

		JSONObject editActionJSONObject = JSONUtil.put(
			"label", label
		).put(
			"symbol", "pencil"
		);

		if (_activeCtCollectionId != ctCollectionId) {
			editActionJSONObject.put(
				"confirmationMessage", confirmationMessage);

			editURL = PublicationsPortletURLUtil.getHref(
				_renderResponse.createActionURL(), ActionRequest.ACTION_NAME,
				"/change_tracking/checkout_ct_collection", "redirect", editURL,
				"ctCollectionId", String.valueOf(ctCollectionId));
		}

		editActionJSONObject.put("href", editURL);

		return editActionJSONObject;
	}

	private <T extends BaseModel<T>> JSONObject _getConflictJSONObject(
		ConflictInfo conflictInfo, long modelClassNameId) {

		ResourceBundle resourceBundle = conflictInfo.getResourceBundle(
			_themeDisplay.getLocale());

		JSONObject jsonObject = JSONUtil.put(
			"alertType", conflictInfo.isResolved() ? "success" : "warning"
		).put(
			"conflictDescription",
			conflictInfo.getConflictDescription(resourceBundle)
		).put(
			"conflictResolution",
			conflictInfo.getResolutionDescription(resourceBundle)
		).put(
			"dismissURL",
			() -> {
				if (!conflictInfo.isResolved()) {
					return null;
				}

				return PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/change_tracking/delete_ct_auto_resolution_info"
				).setRedirect(
					_portal.getCurrentURL(_renderRequest)
				).setParameter(
					"ctAutoResolutionInfoId",
					conflictInfo.getCTAutoResolutionInfoId()
				).buildString();
			}
		);

		ResourceURL dataURL = _renderResponse.createResourceURL();

		dataURL.setResourceID("/change_tracking/get_entry_render_data");

		CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(
			_ctCollection.getCtCollectionId(), modelClassNameId,
			conflictInfo.getSourcePrimaryKey());

		T model = _ctDisplayRendererRegistry.fetchCTModel(
			modelClassNameId, conflictInfo.getTargetPrimaryKey());

		if (ctEntry != null) {
			dataURL.setParameter(
				"ctEntryId", String.valueOf(ctEntry.getCtEntryId()));

			jsonObject.put(
				"description",
				_ctDisplayRendererRegistry.getEntryDescription(
					_httpServletRequest, ctEntry)
			).put(
				"title",
				_ctDisplayRendererRegistry.getTitle(
					_ctCollection.getCtCollectionId(), ctEntry,
					_themeDisplay.getLocale())
			);

			if (!conflictInfo.isResolved() &&
				(_ctCollection.getStatus() !=
					WorkflowConstants.STATUS_SCHEDULED)) {

				JSONArray actionsJSONArray = JSONFactoryUtil.createJSONArray();

				String conflictDescription =
					conflictInfo.getConflictDescription(resourceBundle);

				if (!conflictDescription.equals(
						LanguageUtil.get(
							resourceBundle,
							"deletion-modification-conflict"))) {

					String editURL = _ctDisplayRendererRegistry.getEditURL(
						_httpServletRequest, ctEntry);

					if (Validator.isNotNull(editURL)) {
						actionsJSONArray.put(
							_createEditActionJSONObject(
								_language.format(
									_httpServletRequest,
									"you-are-currently-working-on-" +
										"production.-work-on-x",
									new Object[] {_ctCollection.getName()},
									false),
								_ctCollection.getCtCollectionId(), editURL,
								_language.format(
									_httpServletRequest, "edit-in-x",
									new Object[] {_ctCollection.getName()},
									false)));
					}
				}

				_checkModifiedJournalArticlesInTrash(
					conflictInfo, ctEntry, actionsJSONArray);

				if ((model != null) &&
					!Objects.equals(
						conflictInfo.getResolutionDescription(resourceBundle),
						LanguageUtil.get(
							resourceBundle,
							"deletion-conflicts-with-modifications-in-" +
								"another-publication"))) {

					actionsJSONArray.put(
						_createEditActionJSONObject(
							_language.format(
								_httpServletRequest,
								"you-are-currently-working-on-x.-work-on-" +
									"production",
								new Object[] {_ctCollection.getName()}, false),
							CTConstants.CT_COLLECTION_ID_PRODUCTION,
							_ctDisplayRendererRegistry.getEditURL(
								CTConstants.CT_COLLECTION_ID_PRODUCTION,
								CTSQLModeThreadLocal.CTSQLMode.DEFAULT,
								_httpServletRequest, model, modelClassNameId),
							_language.get(
								_httpServletRequest, "edit-in-production")));
				}

				actionsJSONArray.put(
					JSONUtil.put(
						"href",
						PortletURLBuilder.createRenderURL(
							_renderResponse
						).setMVCRenderCommandName(
							"/change_tracking/view_discard"
						).setRedirect(
							_portal.getCurrentURL(_renderRequest)
						).setParameter(
							"ctCollectionId", ctEntry.getCtCollectionId()
						).setParameter(
							"modelClassNameId", ctEntry.getModelClassNameId()
						).setParameter(
							"modelClassPK", ctEntry.getModelClassPK()
						).buildString()
					).put(
						"label",
						_language.get(_httpServletRequest, "discard-change")
					).put(
						"symbol", "times-circle"
					));

				jsonObject.put("actions", actionsJSONArray);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Unresolved conflict with change tracking entry ",
							"ID, ", ctEntry.getCtEntryId(),
							", model class name ID ",
							ctEntry.getModelClassNameId(),
							", and model class PK ", ctEntry.getModelClassPK(),
							": ", jsonObject));
				}
			}
		}
		else {
			dataURL.setParameter(
				"modelClassNameId", String.valueOf(modelClassNameId));
			dataURL.setParameter(
				"modelClassPK",
				String.valueOf(conflictInfo.getTargetPrimaryKey()));

			String title = null;

			if (model != null) {
				title = _ctDisplayRendererRegistry.getTitle(
					CTConstants.CT_COLLECTION_ID_PRODUCTION,
					CTSQLModeThreadLocal.CTSQLMode.DEFAULT,
					_themeDisplay.getLocale(), model, modelClassNameId);
			}
			else {
				title = _ctDisplayRendererRegistry.getTypeName(
					_themeDisplay.getLocale(), modelClassNameId);
			}

			jsonObject.put(
				"description",
				_ctDisplayRendererRegistry.getTypeName(
					_themeDisplay.getLocale(), modelClassNameId)
			).put(
				"title", title
			);
		}

		jsonObject.put("dataURL", dataURL.toString());

		return jsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewConflictsDisplayContext.class);

	private final long _activeCtCollectionId;
	private final Map<Long, List<ConflictInfo>> _conflictInfoMap;
	private final CTCollection _ctCollection;
	private final CTCollectionLocalService _ctCollectionLocalService;
	private final CTDisplayRendererRegistry _ctDisplayRendererRegistry;
	private final CTEntryLocalService _ctEntryLocalService;
	private final CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;
	private final boolean _hasUnapprovedChanges;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}