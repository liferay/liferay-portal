/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.closure.CTClosure;
import com.liferay.change.tracking.closure.CTClosureFactory;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.mapping.CTMappingTableInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTEntryTable;
import com.liferay.change.tracking.scheduler.PublishScheduler;
import com.liferay.change.tracking.scheduler.ScheduledPublishInfo;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.display.BasePersistenceRegistry;
import com.liferay.change.tracking.web.internal.display.CTClosureUtil;
import com.liferay.change.tracking.web.internal.display.CTModelDisplayRendererAdapter;
import com.liferay.change.tracking.web.internal.frontend.data.set.filter.ChangeTypeSelectionFDSFilter;
import com.liferay.change.tracking.web.internal.frontend.data.set.filter.SiteSelectionFDSFilter;
import com.liferay.change.tracking.web.internal.frontend.data.set.filter.TypeNameSelectionFDSFilter;
import com.liferay.change.tracking.web.internal.frontend.data.set.filter.UserSelectionFDSFilter;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTPermission;
import com.liferay.change.tracking.web.internal.util.PublicationsPortletURLUtil;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.knowledge.base.model.KBArticleModel;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.ExistsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author Samuel Trong Tran
 */
public class ViewChangesDisplayContext {

	public ViewChangesDisplayContext(
		long activeCTCollectionId,
		BasePersistenceRegistry basePersistenceRegistry,
		CTClosureFactory ctClosureFactory, CTCollection ctCollection,
		CTCollectionLocalService ctCollectionLocalService,
		CTDisplayRendererRegistry ctDisplayRendererRegistry,
		CTEntryLocalService ctEntryLocalService,
		CTSchemaVersionLocalService ctSchemaVersionLocalService,
		GroupLocalService groupLocalService, Language language, Portal portal,
		PublicationsDisplayContext publicationsDisplayContext,
		PublishScheduler publishScheduler, RenderRequest renderRequest,
		RenderResponse renderResponse, UserLocalService userLocalService,
		WorkflowInstanceLinkLocalService workflowInstanceLinkLocalService,
		WorkflowTaskManager workflowTaskManager) {

		_activeCTCollectionId = activeCTCollectionId;
		_basePersistenceRegistry = basePersistenceRegistry;
		_ctClosureFactory = ctClosureFactory;
		_ctCollection = ctCollection;
		_ctCollectionLocalService = ctCollectionLocalService;
		_ctDisplayRendererRegistry = ctDisplayRendererRegistry;
		_ctEntryLocalService = ctEntryLocalService;
		_ctSchemaVersionLocalService = ctSchemaVersionLocalService;
		_groupLocalService = groupLocalService;
		_language = language;
		_portal = portal;
		_publicationsDisplayContext = publicationsDisplayContext;
		_publishScheduler = publishScheduler;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_userLocalService = userLocalService;
		_workflowInstanceLinkLocalService = workflowInstanceLinkLocalService;
		_workflowTaskManager = workflowTaskManager;

		_httpServletRequest = portal.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_user = _themeDisplay.getUser();

		long modelClassNameId = ParamUtil.getLong(
			renderRequest, "modelClassNameId");
		long modelClassPK = ParamUtil.getLong(renderRequest, "modelClassPK");

		if ((modelClassNameId != 0) && (modelClassPK != 0)) {
			_modelClassNameId = modelClassNameId;
			_modelClassPK = modelClassPK;
		}
		else {
			long ctEntryId = ParamUtil.getLong(renderRequest, "ctEntryId");

			CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(ctEntryId);

			if (ctEntry != null) {
				_modelClassNameId = ctEntry.getModelClassNameId();
				_modelClassPK = ctEntry.getModelClassPK();
			}
			else {
				_modelClassNameId = 0;
				_modelClassPK = 0;
			}
		}
	}

	public String getAPIURL() {
		boolean showHideable = ParamUtil.getBoolean(
			_renderRequest, "showHideable");

		return StringBundler.concat(
			"/o/change-tracking-rest/v1.0/ct-collections/",
			_ctCollection.getCtCollectionId(), "/ct-entries?showHideable=",
			showHideable);
	}

	public String getBackURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		if (_ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/change_tracking/view_history");
		}
		else if (_ctCollection.getStatus() ==
					WorkflowConstants.STATUS_SCHEDULED) {

			portletURL.setParameter(
				"mvcRenderCommandName", "/change_tracking/view_scheduled");
		}

		return portletURL.toString();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		List<DropdownItem> bulkActionDropdownItems = new ArrayList<>();

		if (FeatureFlagManagerUtil.isEnabled("LPD-20183")) {
			if ((_ctCollection.getStatus() == WorkflowConstants.STATUS_DRAFT) ||
				(_ctCollection.getStatus() ==
					WorkflowConstants.STATUS_EXPIRED)) {

				bulkActionDropdownItems.add(
					new FDSActionDropdownItem(
						PortletURLBuilder.createRenderURL(
							_renderResponse
						).setMVCRenderCommandName(
							"/change_tracking/view_move_changes"
						).setRedirect(
							_themeDisplay.getURLCurrent()
						).setParameter(
							"ctCollectionId", _ctCollection.getCtCollectionId()
						).buildString(),
						"move-folder", "move-changes",
						_language.get(_httpServletRequest, "move-changes"),
						"post", "move-changes", null));
			}

			if (_ctCollection.getStatus() == WorkflowConstants.STATUS_DRAFT) {
				bulkActionDropdownItems.add(
					new FDSActionDropdownItem(
						PortletURLBuilder.createRenderURL(
							_renderResponse
						).setMVCRenderCommandName(
							"/change_tracking/view_discard"
						).setRedirect(
							_themeDisplay.getURLCurrent()
						).setParameter(
							"ctCollectionId", _ctCollection.getCtCollectionId()
						).buildString(),
						"trash", "view-discard",
						_language.get(_httpServletRequest, "discard-changes"),
						"delete", "view-discard", null));
			}
		}

		return bulkActionDropdownItems;
	}

	public long getCtCollectionId() {
		return _ctCollection.getCtCollectionId();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		List<FDSActionDropdownItem> fdsActionDropdownItems = ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_change"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{ctCollectionId}"
				).setParameter(
					"ctEntryId", "{id}"
				).buildString(),
				"list-ul", "view-change",
				_language.get(_httpServletRequest, "review-change"), "get",
				"get", null));

		if ((_ctCollection.getStatus() == WorkflowConstants.STATUS_DRAFT) ||
			(_ctCollection.getStatus() == WorkflowConstants.STATUS_EXPIRED)) {

			fdsActionDropdownItems.add(
				new FDSActionDropdownItem(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/change_tracking/view_move_changes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"ctCollectionId", "{ctCollectionId}"
					).setParameter(
						"modelClassNameId", "{modelClassNameId}"
					).setParameter(
						"modelClassPK", "{modelClassPK}"
					).buildString(),
					"move-folder", "move-changes",
					_language.get(_httpServletRequest, "move-changes"), "post",
					"move-changes", null));
		}

		if (_ctCollection.getStatus() == WorkflowConstants.STATUS_DRAFT) {
			fdsActionDropdownItems.add(
				new FDSActionDropdownItem(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/change_tracking/view_discard"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"ctCollectionId", "{ctCollectionId}"
					).setParameter(
						"modelClassNameId", "{modelClassNameId}"
					).setParameter(
						"modelClassPK", "{modelClassPK}"
					).buildString(),
					"times-circle", "view-discard",
					_language.get(_httpServletRequest, "discard"), "get",
					"view-discard", null));
		}

		return fdsActionDropdownItems;
	}

	public List<FDSFilter> getFDSFilters() throws PortalException {
		long groupId = ParamUtil.getLong(_renderRequest, "groupId");
		long modelClassNameId = ParamUtil.getLong(
			_renderRequest, "modelClassNameId");

		boolean showHideable = ParamUtil.getBoolean(
			_renderRequest, "showHideable");

		Map<Long, String> siteNames = DisplayContextUtil.getSiteNames(
			_ctCollection.getCtCollectionId(), showHideable, _themeDisplay);
		Map<Long, String> typeNames = DisplayContextUtil.getTypeNames(
			_ctCollection.getCtCollectionId(), showHideable, _themeDisplay);

		JSONObject usersJSONObject = DisplayContextUtil.getUserInfoJSONObject(
			CTEntryTable.INSTANCE.userId.eq(UserTable.INSTANCE.userId),
			CTEntryTable.INSTANCE, _themeDisplay, _userLocalService,
			CTEntryTable.INSTANCE.ctCollectionId.eq(
				_ctCollection.getCtCollectionId()));

		return ListUtil.fromArray(
			new ChangeTypeSelectionFDSFilter(),
			new SiteSelectionFDSFilter(groupId, siteNames),
			new TypeNameSelectionFDSFilter(modelClassNameId, typeNames),
			new UserSelectionFDSFilter(usersJSONObject.toMap()));
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"typeName"
			).build()
		).build();
	}

	public Map<String, Object> getItemsOverview() {
		boolean showHideable = ParamUtil.getBoolean(
			_renderRequest, "showHideable");

		Map<Long, String> siteNames = HashMapBuilder.put(
			-1L, _language.get(_httpServletRequest, "system")
		).putAll(
			DisplayContextUtil.getSiteNames(
				_ctCollection.getCtCollectionId(), showHideable, _themeDisplay)
		).build();

		JSONArray itemsOverviewJSONArray = JSONFactoryUtil.createJSONArray();

		for (Map.Entry<Long, String> siteName : siteNames.entrySet()) {
			Map<Long, ObjectValuePair<String, Integer>> objectValuePairs =
				_getObjectValuePairs(
					_ctCollection.getCtCollectionId(), siteName.getKey(),
					showHideable, _themeDisplay);

			if (objectValuePairs.isEmpty()) {
				continue;
			}

			int siteCount = 0;
			JSONArray typeNameAndCountJSONArray =
				JSONFactoryUtil.createJSONArray();

			for (Map.Entry<Long, ObjectValuePair<String, Integer>> entry :
					objectValuePairs.entrySet()) {

				ObjectValuePair<String, Integer> objectValuePair =
					entry.getValue();

				typeNameAndCountJSONArray.put(
					JSONUtil.put(
						"href",
						PortletURLBuilder.createRenderURL(
							_renderResponse
						).setMVCRenderCommandName(
							"/change_tracking/view_changes"
						).setParameter(
							"ctCollectionId", _ctCollection.getCtCollectionId()
						).setParameter(
							"groupId", siteName.getKey()
						).setParameter(
							"modelClassNameId", entry.getKey()
						).setParameter(
							"showHideable", showHideable
						).buildString()
					).put(
						"label",
						StringBundler.concat(
							objectValuePair.getKey(), " (",
							objectValuePair.getValue(), ") ")
					));

				siteCount = siteCount + objectValuePair.getValue();
			}

			itemsOverviewJSONArray.put(
				JSONUtil.put(
					"siteCount", siteCount
				).put(
					"siteName", siteName.getValue()
				).put(
					"typeNameAndCount", typeNameAndCountJSONArray
				));
		}

		return HashMapBuilder.<String, Object>put(
			"itemsOverview", itemsOverviewJSONArray
		).put(
			"publicationSizeClassification",
			_ctCollection.getScoreSizeClassification()
		).build();
	}

	public String getMyWorkflowTaskPortletNamespace() {
		return PortalUtil.getPortletNamespace(PortletKeys.MY_WORKFLOW_TASK);
	}

	public Map<String, Object> getReactData() throws Exception {
		if (_reactData != null) {
			return _reactData;
		}

		JSONObject contextViewJSONObject = null;

		CTClosure ctClosure = null;

		if (_ctCollection.getStatus() != WorkflowConstants.STATUS_APPROVED) {
			try {
				if (!_user.isOnDemandUser()) {
					ctClosure = _ctClosureFactory.create(
						_ctCollection.getCtCollectionId());
				}
				else {
					ctClosure = _ctClosureFactory.create(
						_ctCollection.getCtCollectionId(),
						new HashSet<>(
							_ctEntryLocalService.dslQuery(
								DSLQueryFactoryUtil.selectDistinct(
									CTEntryTable.INSTANCE.modelClassNameId
								).from(
									CTEntryTable.INSTANCE
								).where(
									CTEntryTable.INSTANCE.ctCollectionId.eq(
										_ctCollection.getCtCollectionId())
								))));
				}
			}
			catch (Exception exception) {
				contextViewJSONObject = JSONUtil.put(
					"errorMessage",
					_language.get(
						_httpServletRequest, "context-view-is-unavailable"));

				_log.error(exception);
			}
		}

		Map<Long, Set<Long>> classNameIdClassPKsMap = new HashMap<>();
		Map<ModelInfoKey, ModelInfo> modelInfoMap = new HashMap<>();

		if (ctClosure == null) {
			List<CTEntry> ctEntries =
				_ctEntryLocalService.getCTCollectionCTEntries(
					_ctCollection.getCtCollectionId());

			int modelKeyCounter = 1;

			for (CTEntry ctEntry : ctEntries) {
				modelInfoMap.put(
					new ModelInfoKey(
						ctEntry.getModelClassNameId(),
						ctEntry.getModelClassPK()),
					new ModelInfo(modelKeyCounter++));

				Set<Long> classPKs = classNameIdClassPKsMap.computeIfAbsent(
					ctEntry.getModelClassNameId(), key -> new HashSet<>());

				classPKs.add(ctEntry.getModelClassPK());
			}
		}
		else {
			Map.Entry<Long, List<Long>> entry = null;
			int[] modelKeyCounterHolder = {1};

			Map<Long, List<Long>> rootPKsMap = _getRootPKsMap(ctClosure);

			Queue<Map.Entry<Long, List<Long>>> queue = new LinkedList<>(
				rootPKsMap.entrySet());

			while ((entry = queue.poll()) != null) {
				long classNameId = entry.getKey();

				Set<Long> classPKs = classNameIdClassPKsMap.computeIfAbsent(
					classNameId, key -> new HashSet<>());

				classPKs.addAll(entry.getValue());

				for (long classPK : entry.getValue()) {
					ModelInfoKey modelInfoKey = new ModelInfoKey(
						classNameId, classPK);

					if (!modelInfoMap.containsKey(modelInfoKey)) {
						modelInfoMap.put(
							modelInfoKey,
							new ModelInfo(modelKeyCounterHolder[0]++));
					}
				}
			}
		}

		Map<Long, String> typeNameCacheMap = new HashMap<>();

		for (Map.Entry<Long, Set<Long>> entry :
				classNameIdClassPKsMap.entrySet()) {

			if (entry.getKey() == 0) {
				continue;
			}

			_populateEntryValues(
				modelInfoMap, entry.getKey(), entry.getValue(),
				typeNameCacheMap);
		}

		if (ctClosure != null) {
			long groupClassNameId = _portal.getClassNameId(Group.class);

			for (long groupId :
					classNameIdClassPKsMap.getOrDefault(
						groupClassNameId, Collections.emptySet())) {

				_populateModelInfoGroupIds(
					ctClosure, modelInfoMap, groupClassNameId, groupId);
			}
		}

		_reactData = HashMapBuilder.<String, Object>put(
			"changes",
			() -> {
				JSONArray changesJSONArray = JSONFactoryUtil.createJSONArray();

				for (ModelInfo modelInfo : modelInfoMap.values()) {
					if (modelInfo._ctEntry) {
						changesJSONArray.put(modelInfo._modelKey);
					}
				}

				return changesJSONArray;
			}
		).put(
			"changeURL",
			PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				"/change_tracking/view_change"
			).setParameter(
				"ctCollectionId", _ctCollection.getCtCollectionId()
			).buildString()
		).put(
			"contextView",
			_getContextViewJSONObject(
				ctClosure, modelInfoMap, contextViewJSONObject,
				typeNameCacheMap)
		).put(
			"dataURL",
			() -> {
				ResourceURL dataURL = _renderResponse.createResourceURL();

				dataURL.setParameter(
					"activeCTCollectionId",
					String.valueOf(_activeCTCollectionId));
				dataURL.setParameter("localize", Boolean.TRUE.toString());
				dataURL.setResourceID("/change_tracking/get_entry_render_data");

				return dataURL.toString();
			}
		).put(
			"defaultLocale",
			JSONUtil.put(
				"label", _themeDisplay.getLanguageId()
			).put(
				"symbol",
				StringUtil.replace(
					StringUtil.toLowerCase(_themeDisplay.getLanguageId()),
					CharPool.UNDERLINE, CharPool.DASH)
			)
		).put(
			"discardURL",
			() -> {
				if ((_ctCollection.getStatus() !=
						WorkflowConstants.STATUS_DRAFT) ||
					!CTCollectionPermission.contains(
						_themeDisplay.getPermissionChecker(), _ctCollection,
						ActionKeys.DELETE)) {

					return null;
				}

				return PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_discard"
				).setRedirect(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/change_tracking/view_changes"
					).setParameter(
						"ctCollectionId", _ctCollection.getCtCollectionId()
					).buildString()
				).setBackURL(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", _ctCollection.getCtCollectionId()
				).buildString();
			}
		).put(
			"entryFromURL", ParamUtil.getString(_renderRequest, "entry")
		).put(
			"modelData",
			() -> {
				JSONObject modelDataJSONObject =
					JSONFactoryUtil.createJSONObject();

				for (ModelInfo modelInfo : modelInfoMap.values()) {
					if (modelInfo._jsonObject != null) {
						modelDataJSONObject.put(
							String.valueOf(modelInfo._modelKey),
							modelInfo._jsonObject);
					}
				}

				return modelDataJSONObject;
			}
		).put(
			"moveChangesURL",
			() -> {
				if ((_ctCollection.getStatus() !=
						WorkflowConstants.STATUS_DRAFT) ||
					!CTCollectionPermission.contains(
						_themeDisplay.getPermissionChecker(), _ctCollection,
						ActionKeys.UPDATE)) {

					return null;
				}

				return PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_move_changes"
				).setRedirect(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/change_tracking/view_changes"
					).setParameter(
						"ctCollectionId", _ctCollection.getCtCollectionId()
					).buildString()
				).setParameter(
					"ctCollectionId", _ctCollection.getCtCollectionId()
				).buildString();
			}
		).put(
			"siteNames",
			() -> {
				JSONObject siteNamesJSONObject =
					JSONFactoryUtil.createJSONObject();

				for (ModelInfo modelInfo : modelInfoMap.values()) {
					if (modelInfo._jsonObject == null) {
						continue;
					}

					long groupId = modelInfo._jsonObject.getLong("groupId");

					String groupIdString = String.valueOf(groupId);

					if (!siteNamesJSONObject.has(groupIdString)) {
						Group group = _groupLocalService.fetchGroup(groupId);

						if (group == null) {
							siteNamesJSONObject.put(
								groupIdString,
								_language.get(
									_themeDisplay.getLocale(), "global"));
						}
						else {
							siteNamesJSONObject.put(
								groupIdString,
								group.getName(_themeDisplay.getLocale()));
						}
					}
				}

				return siteNamesJSONObject;
			}
		).put(
			"typeNames",
			() -> {
				JSONObject typeNamesJSONObject =
					JSONFactoryUtil.createJSONObject();

				for (long classNameId : classNameIdClassPKsMap.keySet()) {
					String typeName = _getTypeName(
						_themeDisplay.getLocale(), classNameId,
						typeNameCacheMap);

					typeNamesJSONObject.put(
						String.valueOf(classNameId), typeName);
				}

				return typeNamesJSONObject;
			}
		).put(
			"userInfo",
			DisplayContextUtil.getUserInfoJSONObject(
				CTEntryTable.INSTANCE.userId.eq(UserTable.INSTANCE.userId),
				CTEntryTable.INSTANCE, _themeDisplay, _userLocalService,
				CTEntryTable.INSTANCE.ctCollectionId.eq(
					_ctCollection.getCtCollectionId()))
		).putAll(
			getToolbarReactData()
		).build();

		return _reactData;
	}

	public Map<String, Object> getToolbarReactData() throws Exception {
		if (_toolbarReactData != null) {
			return _toolbarReactData;
		}

		int ctEntriesCount = _ctEntryLocalService.getCTCollectionCTEntriesCount(
			_ctCollection.getCtCollectionId());

		_toolbarReactData = HashMapBuilder.<String, Object>put(
			"collaboratorsData",
			_publicationsDisplayContext.getCollaboratorsReactData(
				_ctCollection.getCtCollectionId(), false)
		).put(
			"ctMappingInfos",
			() -> {
				JSONArray ctMappingInfosJSONArray =
					JSONFactoryUtil.createJSONArray();

				List<CTMappingTableInfo> ctMappingTableInfos =
					_ctCollectionLocalService.getCTMappingTableInfos(
						_ctCollection.getCtCollectionId());

				for (CTMappingTableInfo ctMappingTableInfo :
						ctMappingTableInfos) {

					String description = StringPool.BLANK;

					List<Map.Entry<Long, Long>> addedMappings =
						ctMappingTableInfo.getAddedMappings();

					if (!addedMappings.isEmpty()) {
						description = StringBundler.concat(
							addedMappings.size(), StringPool.SPACE,
							_language.get(_themeDisplay.getLocale(), "added"));
					}

					List<Map.Entry<Long, Long>> removedMappings =
						ctMappingTableInfo.getRemovedMappings();

					if (!removedMappings.isEmpty()) {
						if (Validator.isNotNull(description)) {
							description = description.concat(", ");
						}

						description = StringBundler.concat(
							description, removedMappings.size(),
							StringPool.SPACE,
							_language.get(
								_themeDisplay.getLocale(), "removed"));
					}

					ctMappingInfosJSONArray.put(
						JSONUtil.put(
							"description", description
						).put(
							"name",
							StringBundler.concat(
								_ctDisplayRendererRegistry.getTypeName(
									_themeDisplay.getLocale(),
									_portal.getClassNameId(
										ctMappingTableInfo.
											getLeftModelClass())),
								" & ",
								_ctDisplayRendererRegistry.getTypeName(
									_themeDisplay.getLocale(),
									_portal.getClassNameId(
										ctMappingTableInfo.
											getRightModelClass())))
						).put(
							"tableName", ctMappingTableInfo.getTableName()
						));
				}

				return ctMappingInfosJSONArray;
			}
		).put(
			"currentUserId", _themeDisplay.getUserId()
		).put(
			"deleteCTCommentURL",
			() -> {
				ResourceURL deleteCTCommentURL =
					_renderResponse.createResourceURL();

				deleteCTCommentURL.setParameter(
					"ctCollectionId",
					String.valueOf(_ctCollection.getCtCollectionId()));
				deleteCTCommentURL.setResourceID(
					"/change_tracking/delete_ct_comment");

				return deleteCTCommentURL.toString();
			}
		).put(
			"description",
			() -> {
				if (_ctCollection.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) {

					String description = _ctCollection.getDescription();

					if (Validator.isNotNull(description)) {
						description = description.concat(" | ");
					}

					Format format = FastDateFormatFactoryUtil.getDateTime(
						_themeDisplay.getLocale(), _themeDisplay.getTimeZone());

					return description.concat(
						_language.format(
							_httpServletRequest, "published-by-x-on-x",
							new Object[] {
								_ctCollection.getUserName(),
								format.format(_ctCollection.getStatusDate())
							},
							false));
				}
				else if (_ctCollection.getStatus() ==
							WorkflowConstants.STATUS_SCHEDULED) {

					String description = _ctCollection.getDescription();

					if (_publishScheduler == null) {
						return description;
					}

					ScheduledPublishInfo scheduledPublishInfo =
						_publishScheduler.getScheduledPublishInfo(
							_ctCollection);

					if (scheduledPublishInfo != null) {
						Format format = FastDateFormatFactoryUtil.getDateTime(
							_themeDisplay.getLocale(),
							_themeDisplay.getTimeZone());

						if (Validator.isNotNull(description)) {
							description = description.concat(" | ");
						}

						description = description.concat(
							_language.format(
								_httpServletRequest, "publishing-x",
								new Object[] {
									format.format(
										scheduledPublishInfo.getStartDate())
								},
								false));

						User user = _userLocalService.fetchUser(
							scheduledPublishInfo.getUserId());

						if (user != null) {
							return StringBundler.concat(
								description, " | ",
								_language.format(
									_httpServletRequest, "scheduled-by-x",
									new Object[] {user.getFullName()}, false));
						}

						return description;
					}

					return StringPool.BLANK;
				}

				return _ctCollection.getDescription();
			}
		).put(
			"dropdownItems",
			_getDropdownItemsJSONArray(_themeDisplay.getPermissionChecker())
		).put(
			"expired",
			(_ctCollection.getStatus() == WorkflowConstants.STATUS_EXPIRED) ||
			((_ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) &&
			 !_ctSchemaVersionLocalService.isLatestCTSchemaVersion(
				 _ctCollection.getSchemaVersionId()))
		).put(
			"getCTCommentsURL",
			() -> {
				ResourceURL getCTCommentsURL =
					_renderResponse.createResourceURL();

				getCTCommentsURL.setParameter(
					"ctCollectionId",
					String.valueOf(_ctCollection.getCtCollectionId()));
				getCTCommentsURL.setResourceID(
					"/change_tracking/get_ct_comments");

				return getCTCommentsURL.toString();
			}
		).put(
			"name", _ctCollection.getName()
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"orderByTypeFromURL",
			ParamUtil.getString(_renderRequest, "orderByType")
		).put(
			"publishURL",
			() -> {
				if ((_ctCollection.getStatus() !=
						WorkflowConstants.STATUS_DRAFT) ||
					!CTCollectionPermission.contains(
						_themeDisplay.getPermissionChecker(), _ctCollection,
						CTActionKeys.PUBLISH)) {

					return null;
				}

				return PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_conflicts"
				).setParameter(
					"ctCollectionId", _ctCollection.getCtCollectionId()
				).buildString();
			}
		).put(
			"rescheduleURL",
			() -> {
				if ((_ctCollection.getStatus() !=
						WorkflowConstants.STATUS_SCHEDULED) ||
					!CTCollectionPermission.contains(
						_themeDisplay.getPermissionChecker(), _ctCollection,
						CTActionKeys.PUBLISH)) {

					return null;
				}

				return PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/reschedule_publication"
				).setParameter(
					"ctCollectionId", _ctCollection.getCtCollectionId()
				).buildString();
			}
		).put(
			"revertURL",
			() -> {
				if ((_ctCollection.getStatus() !=
						WorkflowConstants.STATUS_APPROVED) ||
					!CTPermission.contains(
						_themeDisplay.getPermissionChecker(),
						CTActionKeys.ADD_PUBLICATION)) {

					return null;
				}

				return PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/undo_ct_collection"
				).setParameter(
					"ctCollectionId", _ctCollection.getCtCollectionId()
				).setParameter(
					"revert", true
				).buildString();
			}
		).put(
			"scheduleURL",
			() -> {
				if ((_ctCollection.getStatus() !=
						WorkflowConstants.STATUS_DRAFT) ||
					!PropsValues.SCHEDULER_ENABLED ||
					!CTCollectionPermission.contains(
						_themeDisplay.getPermissionChecker(), _ctCollection,
						CTActionKeys.PUBLISH)) {

					return null;
				}

				return PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_conflicts"
				).setParameter(
					"ctCollectionId", _ctCollection.getCtCollectionId()
				).setParameter(
					"schedule", true
				).buildString();
			}
		).put(
			"showActionItems", !_user.isOnDemandUser()
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).put(
			"statusLabel",
			_language.get(
				_themeDisplay.getLocale(),
				_publicationsDisplayContext.getStatusLabel(
					_ctCollection.getStatus()))
		).put(
			"statusStyle",
			_publicationsDisplayContext.getStatusStyle(
				_ctCollection.getStatus())
		).put(
			"total", ctEntriesCount
		).put(
			"unscheduleURL",
			() -> {
				if ((_ctCollection.getStatus() !=
						WorkflowConstants.STATUS_SCHEDULED) ||
					!CTCollectionPermission.contains(
						_themeDisplay.getPermissionChecker(), _ctCollection,
						CTActionKeys.PUBLISH)) {

					return null;
				}

				return PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/change_tracking/unschedule_publication"
				).setParameter(
					"ctCollectionId", _ctCollection.getCtCollectionId()
				).buildString();
			}
		).put(
			"updateCTCommentURL",
			() -> {
				ResourceURL updateCTCommentURL =
					_renderResponse.createResourceURL();

				updateCTCommentURL.setParameter(
					"ctCollectionId",
					String.valueOf(_ctCollection.getCtCollectionId()));
				updateCTCommentURL.setResourceID(
					"/change_tracking/update_ct_comment");

				return updateCTCommentURL.toString();
			}
		).build();

		return _toolbarReactData;
	}

	public List<NavigationItem> getViewNavigationItems() {
		boolean relationshipsActive = GetterUtil.getBoolean(
			_httpServletRequest.getParameter("relationships"));

		List<CTMappingTableInfo> ctMappingTableInfosList =
			_ctCollectionLocalService.getCTMappingTableInfos(
				_ctCollection.getCtCollectionId());

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(!relationshipsActive);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/change_tracking/view_changes", "ctCollectionId",
					String.valueOf(_ctCollection.getCtCollectionId()));
				navigationItem.setLabel(
					_language.get(_httpServletRequest, "data"));
			}
		).add(
			() -> !ctMappingTableInfosList.isEmpty(),
			navigationItem -> {
				navigationItem.setActive(relationshipsActive);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/change_tracking/view_changes", "ctCollectionId",
					String.valueOf(_ctCollection.getCtCollectionId()),
					"relationships", true);
				navigationItem.setLabel(
					_language.get(_httpServletRequest, "relationships"));
			}
		).build();
	}

	private JSONObject _getContextViewJSONObject(
		CTClosure ctClosure, Map<ModelInfoKey, ModelInfo> modelInfoMap,
		JSONObject defaultContextViewJSONObject,
		Map<Long, String> typeNameCacheMap) {

		if (ctClosure == null) {
			return defaultContextViewJSONObject;
		}

		JSONObject everythingJSONObject = JSONUtil.put("nodeId", 0);

		Set<Integer> rootModelKeys = new HashSet<>();
		Map<Long, JSONArray> rootDisplayMap = new HashMap<>();

		int nodeIdCounter = 1;

		Queue<ParentModel> queue = new LinkedList<>();

		queue.add(
			new ParentModel(everythingJSONObject, _getRootPKsMap(ctClosure)));

		ParentModel parentModel = null;

		while ((parentModel = queue.poll()) != null) {
			if (parentModel._jsonObject == null) {
				continue;
			}

			JSONArray childrenJSONArray = JSONFactoryUtil.createJSONArray();

			for (Map.Entry<Long, List<Long>> entry :
					parentModel._childPKsMap.entrySet()) {

				long modelClassNameId = entry.getKey();

				for (long modelClassPK : entry.getValue()) {
					ModelInfo modelInfo = modelInfoMap.get(
						new ModelInfoKey(modelClassNameId, modelClassPK));

					if (modelInfo == null) {
						continue;
					}

					int modelKey = modelInfo._modelKey;

					int nodeId = nodeIdCounter++;

					JSONObject jsonObject = JSONUtil.put(
						"modelKey", modelKey
					).put(
						"nodeId", nodeId
					);

					childrenJSONArray.put(jsonObject);

					if (rootModelKeys.add(modelKey)) {
						JSONArray jsonArray = rootDisplayMap.computeIfAbsent(
							modelClassNameId,
							key -> JSONFactoryUtil.createJSONArray());

						// Copy JSON object to prevent appending children

						jsonArray.put(
							JSONUtil.put(
								"modelKey", modelKey
							).put(
								"nodeId", nodeId
							));
					}

					Map<Long, List<Long>> childPKsMap =
						ctClosure.getChildPKsMap(
							modelClassNameId, modelClassPK);

					if (!childPKsMap.isEmpty()) {
						queue.add(new ParentModel(jsonObject, childPKsMap));
					}
				}
			}

			parentModel._jsonObject.put("children", childrenJSONArray);
		}

		JSONObject contextViewJSONObject = JSONUtil.put(
			"everything", everythingJSONObject);

		for (Map.Entry<Long, JSONArray> entry : rootDisplayMap.entrySet()) {
			contextViewJSONObject.put(
				_getTypeName(
					_themeDisplay.getLocale(), entry.getKey(),
					typeNameCacheMap),
				JSONUtil.put("children", entry.getValue()));
		}

		return contextViewJSONObject;
	}

	private JSONArray _getDropdownItemsJSONArray(
			PermissionChecker permissionChecker)
		throws Exception {

		if ((_ctCollection.getStatus() != WorkflowConstants.STATUS_DRAFT) &&
			(_ctCollection.getStatus() != WorkflowConstants.STATUS_EXPIRED)) {

			return null;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (StringUtil.equals(
				ParamUtil.getString(_renderRequest, "mvcRenderCommandName"),
				"/change_tracking/view_changes")) {

			boolean showHideable = ParamUtil.getBoolean(
				_renderRequest, "showHideable");

			jsonArray.put(
				JSONUtil.put(
					"href",
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/change_tracking/view_changes"
					).setParameter(
						"ctCollectionId", _ctCollection.getCtCollectionId()
					).setParameter(
						"showHideable", !showHideable
					).buildString()
				).put(
					"label",
					() -> {
						if (showHideable) {
							return _language.get(
								_httpServletRequest, "hide-system-changes");
						}

						return _language.get(
							_httpServletRequest, "show-system-changes");
					}
				).put(
					"symbolLeft",
					() -> {
						if (showHideable) {
							return "hidden";
						}

						return "view";
					}
				));
		}

		if (CTCollectionPermission.contains(
				permissionChecker, _ctCollection, ActionKeys.UPDATE)) {

			if (_ctCollection.getCtCollectionId() != _activeCTCollectionId) {
				jsonArray.put(
					JSONUtil.put(
						"disabled",
						_ctCollection.getStatus() ==
							WorkflowConstants.STATUS_EXPIRED
					).put(
						"href",
						PublicationsPortletURLUtil.getHref(
							_renderResponse.createActionURL(),
							ActionRequest.ACTION_NAME,
							"/change_tracking/checkout_ct_collection",
							"redirect", _themeDisplay.getURLCurrent(),
							"ctCollectionId",
							String.valueOf(_ctCollection.getCtCollectionId()))
					).put(
						"label",
						_language.get(
							_httpServletRequest, "work-on-publication")
					).put(
						"symbolLeft", "radio-button"
					));
			}

			if (_ctCollection.getStatus() != WorkflowConstants.STATUS_EXPIRED) {
				jsonArray.put(
					JSONUtil.put(
						"href",
						PublicationsPortletURLUtil.getHref(
							_renderResponse.createRenderURL(),
							"mvcRenderCommandName",
							"/change_tracking/edit_ct_collection", "redirect",
							_themeDisplay.getURLCurrent(), "ctCollectionId",
							String.valueOf(_ctCollection.getCtCollectionId()))
					).put(
						"label", _language.get(_httpServletRequest, "edit")
					).put(
						"symbolLeft", "pencil"
					));
			}
		}

		if ((_ctCollection.getStatus() != WorkflowConstants.STATUS_EXPIRED) &&
			CTCollectionPermission.contains(
				permissionChecker, _ctCollection, ActionKeys.PERMISSIONS)) {

			jsonArray.put(
				JSONUtil.put(
					"href",
					PublicationsPortletURLUtil.getPermissionsHref(
						_httpServletRequest, _ctCollection, _language)
				).put(
					"label", _language.get(_httpServletRequest, "permissions")
				).put(
					"symbolLeft", "password-policies"
				));
		}

		if ((_ctCollection.getStatus() == WorkflowConstants.STATUS_EXPIRED) &&
			CTCollectionPermission.contains(
				_themeDisplay.getPermissionChecker(), _ctCollection,
				CTActionKeys.PUBLISH)) {

			jsonArray.put(
				JSONUtil.put(
					"href",
					PublicationsPortletURLUtil.getHref(
						_renderResponse.createActionURL(),
						ActionRequest.ACTION_NAME,
						"/change_tracking/reactivate_ct_collection", "redirect",
						_themeDisplay.getURLCurrent(), "ctCollectionId",
						String.valueOf(_ctCollection.getCtCollectionId()))
				).put(
					"label", _language.get(_httpServletRequest, "reactivate")
				).put(
					"symbolLeft", "reset"
				));
		}

		if (CTCollectionPermission.contains(
				permissionChecker, _ctCollection, ActionKeys.DELETE)) {

			jsonArray.put(
				JSONUtil.put("type", "divider")
			).put(
				JSONUtil.put(
					"href",
					PublicationsPortletURLUtil.getDeleteHref(
						_httpServletRequest, _renderResponse, getBackURL(),
						_ctCollection.getCtCollectionId(), _language)
				).put(
					"label", _language.get(_httpServletRequest, "delete")
				).put(
					"symbolLeft", "times-circle"
				)
			);
		}

		return jsonArray;
	}

	private String _getMissingModelMessage(
		long classPK, long modelClassNameId) {

		return StringBundler.concat(
			"Missing model from ", _ctCollection.getName(), ": {classPK=",
			classPK, ", ctCollectionId=", _ctCollection.getCtCollectionId(),
			", modelClassNameId=", modelClassNameId, "}");
	}

	private Map<Long, ObjectValuePair<String, Integer>> _getObjectValuePairs(
		long ctCollectionId, long groupId, boolean showHideable,
		ThemeDisplay themeDisplay) {

		Map<Long, ObjectValuePair<String, Integer>> objectValuePairs =
			new LinkedHashMap<>();

		Searcher searcher = _searcherSnapshot.get();
		Sorts sorts = _sortsSnapshot.get();

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			_searchRequestBuilderFactorySnapshot.get();

		SearchRequestBuilder searchRequestBuilder =
			searchRequestBuilderFactory.builder(
			).companyId(
				themeDisplay.getCompanyId()
			).entryClassNames(
				CTEntry.class.getName()
			).emptySearchEnabled(
				true
			).fields(
				"modelClassNameId", "typeName"
			).sorts(
				sorts.field(
					Field.getSortableFieldName(
						"typeName_".concat(
							LocaleUtil.toLanguageId(themeDisplay.getLocale()))),
					SortOrder.ASC)
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(
						"ctCollectionId", ctCollectionId);
					searchContext.setAttribute("showHideable", showHideable);

					if (groupId == -1) {
						BooleanQueryImpl booleanQueryImpl =
							new BooleanQueryImpl();

						BooleanFilter booleanFilter = new BooleanFilter();

						booleanFilter.add(
							new ExistsFilter(Field.GROUP_ID),
							BooleanClauseOccur.MUST_NOT);

						booleanQueryImpl.setPreBooleanFilter(booleanFilter);

						searchContext.setBooleanClauses(
							new BooleanClause[] {
								BooleanClauseFactoryUtil.create(
									booleanQueryImpl,
									BooleanClauseOccur.MUST.getName())
							});
					}
					else {
						searchContext.setAttribute(
							Field.GROUP_ID, new long[] {groupId});
					}
				}
			);

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilder.build());

		for (Document document : searchResponse.getDocuments()) {
			ObjectValuePair<String, Integer> objectValuePair =
				objectValuePairs.get(document.getLong("modelClassNameId"));

			if (objectValuePair != null) {
				objectValuePair.setValue(objectValuePair.getValue() + 1);
			}
			else {
				objectValuePairs.put(
					document.getLong("modelClassNameId"),
					new ObjectValuePair(document.getString("typeName"), 1));
			}
		}

		return objectValuePairs;
	}

	private Map<Long, List<Long>> _getRootPKsMap(CTClosure ctClosure) {
		if ((_modelClassNameId > 0) && (_modelClassPK > 0)) {
			return CTClosureUtil.getFamilyPKsMap(
				ctClosure, _modelClassNameId, _modelClassPK);
		}

		return ctClosure.getRootPKsMap();
	}

	private <T extends BaseModel<T>> String _getTitle(
		long ctCollectionId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode,
		Locale locale, T model, long modelClassNameId,
		Map<Long, String> typeNameCacheMap) {

		CTDisplayRenderer<T> ctDisplayRenderer =
			_ctDisplayRendererRegistry.getCTDisplayRenderer(modelClassNameId);

		if (ctDisplayRenderer instanceof CTModelDisplayRendererAdapter) {
			return StringBundler.concat(
				_getTypeName(locale, modelClassNameId, typeNameCacheMap),
				StringPool.SPACE, model.getPrimaryKeyObj());
		}

		return _ctDisplayRendererRegistry.getTitle(
			ctCollectionId, ctSQLMode, locale, model, modelClassNameId);
	}

	private <T extends BaseModel<T>> String _getTypeName(
		Locale locale, long modelClassNameId,
		Map<Long, String> typeNameCacheMap) {

		CTDisplayRenderer<T> ctDisplayRenderer =
			_ctDisplayRendererRegistry.getCTDisplayRenderer(modelClassNameId);

		if (ctDisplayRenderer instanceof CTModelDisplayRendererAdapter) {
			return typeNameCacheMap.computeIfAbsent(
				modelClassNameId,
				key -> _ctDisplayRendererRegistry.getTypeName(
					locale, modelClassNameId));
		}

		return ctDisplayRenderer.getTypeName(locale);
	}

	private List<WorkflowTask> _getWorkflowTasks(
			CTEntry ctEntry, long classPK, long groupId)
		throws WorkflowException {

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
				ctEntry.getCompanyId(), groupId,
				_portal.getClassName(ctEntry.getModelClassNameId()), classPK);

		if (workflowInstanceLink == null) {
			return null;
		}

		return _workflowTaskManager.getWorkflowTasksByWorkflowInstance(
			workflowInstanceLink.getCompanyId(), null,
			workflowInstanceLink.getWorkflowInstanceId(), null, 0, 1, null);
	}

	private <T extends BaseModel<T>> boolean _isSite(T model) {
		if (model instanceof Group) {
			Group group = (Group)model;

			if (group.isCompany()) {
				return false;
			}

			return group.isSite();
		}

		return false;
	}

	private <T extends BaseModel<T>> boolean _isWorkflowTasksEmpty(
			CTEntry ctEntry, long groupId, T model)
		throws Exception {

		long classPK = ctEntry.getModelClassPK();

		if (model instanceof KBArticleModel) {
			Map<String, Object> modelAttributes = model.getModelAttributes();

			classPK = GetterUtil.getLong(
				modelAttributes.get("resourcePrimKey"));
		}

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			ctEntry.getCtCollectionId());

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			List<WorkflowTask> workflowTasks = _getWorkflowTasks(
				ctEntry, classPK, groupId);

			if (workflowTasks == null) {
				return true;
			}

			WorkflowTask workflowTask = workflowTasks.get(0);

			return !workflowTask.isCompleted();
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctEntry.getCtCollectionId())) {

			List<WorkflowTask> workflowTasks = _getWorkflowTasks(
				ctEntry, classPK, groupId);

			if (workflowTasks == null) {
				return true;
			}

			return false;
		}
	}

	private <T extends BaseModel<T>> void _populateEntryValues(
			Map<ModelInfoKey, ModelInfo> modelInfoMap, long modelClassNameId,
			Set<Long> classPKs, Map<Long, String> typeNameCacheMap)
		throws Exception {

		Map<Serializable, T> baseModelMap = null;
		Map<Serializable, T> ctModelMap = null;

		Map<Serializable, CTEntry> ctEntryMap = new HashMap<>();

		for (CTEntry ctEntry :
				_ctEntryLocalService.getCTEntries(
					_ctCollection.getCtCollectionId(), modelClassNameId)) {

			ctEntryMap.put(ctEntry.getModelClassPK(), ctEntry);
		}

		for (long classPK : classPKs) {
			ModelInfo modelInfo = modelInfoMap.get(
				new ModelInfoKey(modelClassNameId, classPK));

			CTEntry ctEntry = ctEntryMap.get(classPK);

			if (ctEntry == null) {
				if (modelClassNameId == _portal.getClassNameId(
						PortletPreferences.class)) {

					continue;
				}

				if (baseModelMap == null) {
					baseModelMap = _basePersistenceRegistry.fetchBaseModelMap(
						modelClassNameId, classPKs);
				}

				T model = baseModelMap.get(classPK);

				if (model == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Missing model from production: {classPK=",
								classPK, ", modelClassNameId=",
								modelClassNameId, "}"));
					}

					continue;
				}

				modelInfo._jsonObject = JSONUtil.put(
					"modelClassNameId", modelClassNameId
				).put(
					"modelClassPK", classPK
				).put(
					"modelKey", modelInfo._modelKey
				).put(
					"title",
					_getTitle(
						CTConstants.CT_COLLECTION_ID_PRODUCTION,
						CTSQLModeThreadLocal.CTSQLMode.DEFAULT,
						_themeDisplay.getLocale(), model, modelClassNameId,
						typeNameCacheMap)
				);

				modelInfo._site = _isSite(model);
			}
			else {
				long ctCollectionId =
					_ctDisplayRendererRegistry.getCtCollectionId(
						_ctCollection, ctEntry);

				CTSQLModeThreadLocal.CTSQLMode ctSQLMode =
					_ctDisplayRendererRegistry.getCTSQLMode(
						ctCollectionId, ctEntry);

				T model;

				try {
					if ((ctCollectionId == _ctCollection.getCtCollectionId()) &&
						(ctSQLMode == CTSQLModeThreadLocal.CTSQLMode.DEFAULT)) {

						if (ctModelMap == null) {
							ctModelMap =
								_ctDisplayRendererRegistry.fetchCTModelMap(
									_ctCollection.getCtCollectionId(),
									CTSQLModeThreadLocal.CTSQLMode.DEFAULT,
									modelClassNameId, classPKs);
						}

						if (ctModelMap != null) {
							model = ctModelMap.get(classPK);
						}
						else {
							model = null;
						}
					}
					else {
						model = _ctDisplayRendererRegistry.fetchCTModel(
							ctCollectionId, ctSQLMode, modelClassNameId,
							classPK);
					}
				}
				catch (SystemException systemException) {
					if (systemException.getCause() instanceof ORMException) {
						if (_ctCollection.getStatus() !=
								WorkflowConstants.STATUS_EXPIRED) {

							_log.error(
								_getMissingModelMessage(
									classPK, modelClassNameId),
								systemException.getCause());
						}
						else if (_log.isDebugEnabled()) {
							_log.debug(
								_getMissingModelMessage(
									classPK, modelClassNameId),
								systemException.getCause());
						}

						continue;
					}

					throw systemException;
				}

				if (model == null) {
					if ((ctEntry.getChangeType() !=
							CTConstants.CT_CHANGE_TYPE_DELETION) &&
						_log.isWarnEnabled()) {

						_log.warn(
							_getMissingModelMessage(classPK, modelClassNameId));
					}

					continue;
				}

				Map<String, Object> modelAttributes =
					model.getModelAttributes();

				Date modifiedDate = ctEntry.getModifiedDate();

				modelInfo._ctEntry = true;

				modelInfo._jsonObject = JSONUtil.put(
					"changeType",
					_ctDisplayRendererRegistry.getChangeType(ctEntry, model)
				).put(
					"ctEntryId", ctEntry.getCtEntryId()
				).put(
					"modelClassNameId", ctEntry.getModelClassNameId()
				).put(
					"modelClassPK", ctEntry.getModelClassPK()
				).put(
					"modelKey", modelInfo._modelKey
				).put(
					"modifiedTime", modifiedDate.getTime()
				).put(
					"timeDescription",
					_language.getTimeDescription(
						_httpServletRequest,
						System.currentTimeMillis() - modifiedDate.getTime(),
						true)
				).put(
					"title",
					_getTitle(
						ctCollectionId, ctSQLMode, _themeDisplay.getLocale(),
						model, modelClassNameId, typeNameCacheMap)
				).put(
					"userId", ctEntry.getUserId()
				).put(
					"workflowStatus", (Integer)modelAttributes.get("status")
				);

				long groupId = 0;

				if (model instanceof GroupedModel) {
					GroupedModel groupedModel = (GroupedModel)model;

					groupId = groupedModel.getGroupId();

					modelInfo._jsonObject.put("groupId", groupId);
				}

				int changeType = _ctDisplayRendererRegistry.getChangeType(
					ctEntry, model);

				if (_ctDisplayRendererRegistry.isWorkflowEnabled(
						ctEntry, model) &&
					(changeType != CTConstants.CT_CHANGE_TYPE_DELETION) &&
					((Integer)modelAttributes.get("status") !=
						WorkflowConstants.STATUS_DRAFT)) {

					modelInfo._jsonObject.put(
						"showWorkflow",
						!_isWorkflowTasksEmpty(ctEntry, groupId, model));
				}

				modelInfo._site = _isSite(model);
			}
		}
	}

	private void _populateModelInfoGroupIds(
		CTClosure ctClosure, Map<ModelInfoKey, ModelInfo> modelInfoMap,
		long groupClassNameId, long groupId) {

		ModelInfo groupModelInfo = modelInfoMap.get(
			new ModelInfoKey(groupClassNameId, groupId));

		if (!groupModelInfo._site) {
			return;
		}

		Map<Long, List<Long>> pksMap = ctClosure.getChildPKsMap(
			groupClassNameId, groupId);

		Deque<Map.Entry<Long, ? extends Collection<Long>>> queue =
			new LinkedList<>(pksMap.entrySet());

		Map.Entry<Long, ? extends Collection<Long>> entry = null;

		while ((entry = queue.poll()) != null) {
			long classNameId = entry.getKey();

			for (long classPK : entry.getValue()) {
				ModelInfo modelInfo = modelInfoMap.get(
					new ModelInfoKey(classNameId, classPK));

				if (modelInfo == null) {
					continue;
				}

				if (modelInfo._jsonObject != null) {
					modelInfo._jsonObject.put("groupId", groupId);
				}

				Map<Long, ? extends Collection<Long>> childPKsMap =
					ctClosure.getChildPKsMap(classNameId, classPK);

				if (!childPKsMap.isEmpty()) {
					queue.addAll(childPKsMap.entrySet());
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewChangesDisplayContext.class);

	private static final Snapshot<Searcher> _searcherSnapshot = new Snapshot<>(
		ViewChangesDisplayContext.class, Searcher.class);
	private static final Snapshot<SearchRequestBuilderFactory>
		_searchRequestBuilderFactorySnapshot = new Snapshot<>(
			ViewChangesDisplayContext.class, SearchRequestBuilderFactory.class);
	private static final Snapshot<Sorts> _sortsSnapshot = new Snapshot<>(
		ViewChangesDisplayContext.class, Sorts.class);

	private final long _activeCTCollectionId;
	private final BasePersistenceRegistry _basePersistenceRegistry;
	private final CTClosureFactory _ctClosureFactory;
	private final CTCollection _ctCollection;
	private final CTCollectionLocalService _ctCollectionLocalService;
	private final CTDisplayRendererRegistry _ctDisplayRendererRegistry;
	private final CTEntryLocalService _ctEntryLocalService;
	private final CTSchemaVersionLocalService _ctSchemaVersionLocalService;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final long _modelClassNameId;
	private final long _modelClassPK;
	private final Portal _portal;
	private final PublicationsDisplayContext _publicationsDisplayContext;
	private final PublishScheduler _publishScheduler;
	private Map<String, Object> _reactData;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private Map<String, Object> _toolbarReactData;
	private final User _user;
	private final UserLocalService _userLocalService;
	private final WorkflowInstanceLinkLocalService
		_workflowInstanceLinkLocalService;
	private final WorkflowTaskManager _workflowTaskManager;

	private static class ModelInfo {

		private ModelInfo(int modelKey) {
			_modelKey = modelKey;
		}

		private boolean _ctEntry;
		private JSONObject _jsonObject;
		private final int _modelKey;
		private boolean _site;

	}

	private static class ModelInfoKey {

		@Override
		public boolean equals(Object object) {
			if (object instanceof ModelInfoKey) {
				ModelInfoKey modelInfoKey = (ModelInfoKey)object;

				if ((modelInfoKey._classNameId == _classNameId) &&
					(modelInfoKey._classPK == _classPK)) {

					return true;
				}
			}

			return false;
		}

		@Override
		public int hashCode() {
			return HashUtil.hash((int)_classNameId, _classPK);
		}

		private ModelInfoKey(long classNameId, long classPK) {
			_classNameId = classNameId;
			_classPK = classPK;
		}

		private final long _classNameId;
		private final long _classPK;

	}

	private static class ParentModel {

		private ParentModel(
			JSONObject jsonObject, Map<Long, List<Long>> childPKsMap) {

			_jsonObject = jsonObject;
			_childPKsMap = childPKsMap;
		}

		private final Map<Long, List<Long>> _childPKsMap;
		private final JSONObject _jsonObject;

	}

}