/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR
 * LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.display.DisplayContextImpl;
import com.liferay.diff.DiffHtml;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/get_layout_content_changes"
	},
	service = MVCResourceCommand.class
)
public class GetLayoutContentChangesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-75671")) {

			throw new UnsupportedOperationException();
		}

		try {
			long ctEntryId = ParamUtil.getLong(resourceRequest, "ctEntryId");

			CTEntry ctEntry = _ctEntryLocalService.getCTEntry(ctEntryId);

			if ((ctEntry == null) ||
				!Objects.equals(
					_portal.getClassNameId(Layout.class),
					ctEntry.getModelClassNameId())) {

				return;
			}

			SearchResponse searchResponse =
				_getLayoutContentChangesSearchResponse(
					themeDisplay.getCompanyId(), ctEntry.getCtCollectionId(),
					ParamUtil.getInteger(resourceRequest, "cur"),
					themeDisplay.getLocale(), ctEntry.getModelClassPK());

			List<Document> documents = searchResponse.getDocuments();

			if (documents.isEmpty()) {
				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					JSONUtil.put(
						"layoutContentChanges", new ArrayList<>()
					).put(
						"total", 0
					));

				return;
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"layoutContentChanges",
					_getLayoutContentChangesJSONArray(
						_ctCollectionLocalService.getCTCollection(
							ctEntry.getCtCollectionId()),
						documents,
						_portal.getHttpServletRequest(resourceRequest),
						_portal.getHttpServletResponse(resourceResponse),
						themeDisplay.getLocale())
				).put(
					"total", searchResponse.getTotalHits()
				));
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"errorMessage",
					_language.get(
						_portal.getHttpServletRequest(resourceRequest),
						"an-unexpected-error-occurred")));
		}
	}

	private <T extends BaseModel<T>> JSONArray
		_getLayoutContentChangesJSONArray(
			CTCollection ctCollection, List<Document> documents,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Locale locale) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (Document document : documents) {
			jsonArray = jsonArray.put(
				JSONUtil.put(
					Field.TITLE,
					document.getString(
						Field.getLocalizedName(locale, Field.TITLE))
				).put(
					"ctEntryId", document.getLong(Field.ENTRY_CLASS_PK)
				).put(
					"preview",
					() -> {
						CTEntry ctEntry = _ctEntryLocalService.getCTEntry(
							document.getLong(Field.ENTRY_CLASS_PK));

						CTDisplayRenderer<T> ctDisplayRenderer =
							_ctDisplayRendererRegistry.getCTDisplayRenderer(
								ctEntry.getModelClassNameId());

						return _diffHtml.diff(
							new UnsyncStringReader(
								_getProductionPreview(
									ctCollection, ctDisplayRenderer, ctEntry,
									httpServletRequest, httpServletResponse)),
							new UnsyncStringReader(
								_getPublicationPreview(
									ctCollection, ctDisplayRenderer, ctEntry,
									httpServletRequest, httpServletResponse)));
					}
				).put(
					"viewChangeURL",
					PortletURLBuilder.create(
						PortletURLFactoryUtil.create(
							httpServletRequest, CTPortletKeys.PUBLICATIONS,
							PortletRequest.RENDER_PHASE)
					).setMVCRenderCommandName(
						"/change_tracking/view_change"
					).setParameter(
						"ctCollectionId", document.getLong("ctCollectionId")
					).setParameter(
						"ctEntryId", document.getLong(Field.ENTRY_CLASS_PK)
					).buildString()
				));
		}

		return jsonArray;
	}

	private SearchResponse _getLayoutContentChangesSearchResponse(
		long companyId, long ctCollectionId, int cur, Locale locale,
		long plid) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				companyId
			).emptySearchEnabled(
				true
			).fields(
				Field.ENTRY_CLASS_PK,
				Field.getLocalizedName(locale, Field.TITLE), "ctCollectionId"
			).from(
				cur
			).modelIndexerClasses(
				CTEntry.class
			).size(
				20
			).sorts(
				_sorts.field(
					Field.getSortableFieldName(Field.MODIFIED_DATE),
					SortOrder.DESC)
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(
						"ctCollectionId", ctCollectionId);
					searchContext.setAttribute(
						"modelClassNameId",
						new long[] {
							_portal.getClassNameId(FragmentEntryLink.class)
						});
					searchContext.setAttribute("showHideable", Boolean.TRUE);

					BooleanQuery booleanQuery = new BooleanQuery();

					booleanQuery.addRequiredTerm("plid", plid);

					searchContext.setBooleanClauses(
						new BooleanClause[] {
							new BooleanClause<>(
								booleanQuery, BooleanClauseOccur.MUST)
						});
				}
			);

		return _searcher.search(searchRequestBuilder.build());
	}

	private <T extends BaseModel<T>> String _getPreview(
		long ctCollectionId, CTDisplayRenderer<T> ctDisplayRenderer,
		long ctEntryId, CTSQLModeThreadLocal.CTSQLMode ctSQLMode,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, T model, String type) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId);
			SafeCloseable safeCloseable2 =
				CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(ctSQLMode)) {

			return ctDisplayRenderer.renderPreview(
				new DisplayContextImpl<>(
					httpServletRequest, httpServletResponse,
					_classNameLocalService, _ctDisplayRendererRegistry,
					ctEntryId, themeDisplay.getLocale(), model, type));
		}
		catch (Exception exception) {
			_log.error(exception);

			return StringPool.BLANK;
		}
	}

	private <T extends BaseModel<T>> String _getProductionPreview(
			CTCollection ctCollection, CTDisplayRenderer<T> ctDisplayRenderer,
			CTEntry ctEntry, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long targetCTCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			targetCTCollectionId = ctEntry.getCtCollectionId();
		}

		CTSQLModeThreadLocal.CTSQLMode targetCTSQLMode =
			_ctDisplayRendererRegistry.getCTSQLMode(
				targetCTCollectionId, ctEntry);

		T targetModel = null;

		if (ctEntry.getChangeType() == CTConstants.CT_CHANGE_TYPE_ADDITION) {
			long ctCollectionId = ctCollection.getCtCollectionId();

			if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
				ctCollectionId = _ctEntryLocalService.getCTRowCTCollectionId(
					ctEntry);
			}

			T ctModel = _ctDisplayRendererRegistry.fetchCTModel(
				ctCollectionId,
				_ctDisplayRendererRegistry.getCTSQLMode(
					ctCollectionId, ctEntry),
				ctEntry.getModelClassNameId(), ctEntry.getModelClassPK());

			if (ctModel == null) {
				return StringPool.BLANK;
			}

			try (SafeCloseable safeCloseable1 =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						targetCTCollectionId);
				SafeCloseable safeCloseable2 =
					CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(
						targetCTSQLMode)) {

				targetModel = ctDisplayRenderer.fetchLatestVersionedModel(
					ctModel);
			}

			if (targetModel == null) {
				return StringPool.BLANK;
			}

			return _getPreview(
				targetCTCollectionId, ctDisplayRenderer, ctEntry.getCtEntryId(),
				targetCTSQLMode, httpServletRequest, httpServletResponse,
				targetModel, CTConstants.TYPE_LATEST);
		}

		targetModel = _ctDisplayRendererRegistry.fetchCTModel(
			targetCTCollectionId, targetCTSQLMode,
			ctEntry.getModelClassNameId(), ctEntry.getModelClassPK());

		return _getPreview(
			targetCTCollectionId, ctDisplayRenderer, ctEntry.getCtEntryId(),
			targetCTSQLMode, httpServletRequest, httpServletResponse,
			targetModel, CTConstants.TYPE_BEFORE);
	}

	private <T extends BaseModel<T>> String _getPublicationPreview(
			CTCollection ctCollection, CTDisplayRenderer<T> ctDisplayRenderer,
			CTEntry ctEntry, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long targetCTCollectionId = ctCollection.getCtCollectionId();

		if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			targetCTCollectionId = _ctEntryLocalService.getCTRowCTCollectionId(
				ctEntry);
		}

		CTSQLModeThreadLocal.CTSQLMode targetCTSQLMode =
			_ctDisplayRendererRegistry.getCTSQLMode(
				targetCTCollectionId, ctEntry);

		T targetModel = null;

		if (ctEntry.getChangeType() == CTConstants.CT_CHANGE_TYPE_DELETION) {
			long ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;

			if (ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED) {
				ctCollectionId = ctEntry.getCtCollectionId();
			}

			T productionModel = _ctDisplayRendererRegistry.fetchCTModel(
				ctCollectionId,
				_ctDisplayRendererRegistry.getCTSQLMode(
					ctCollectionId, ctEntry),
				ctEntry.getModelClassNameId(), ctEntry.getModelClassPK());

			targetCTSQLMode = CTSQLModeThreadLocal.CTSQLMode.DEFAULT;

			if (productionModel != null) {
				try (SafeCloseable safeCloseable1 =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								targetCTCollectionId);
					SafeCloseable safeCloseable2 =
						CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(
							targetCTSQLMode)) {

					targetModel = ctDisplayRenderer.fetchLatestVersionedModel(
						productionModel);
				}
			}

			if (targetModel == null) {
				return StringPool.BLANK;
			}

			return _getPreview(
				targetCTCollectionId, ctDisplayRenderer, ctEntry.getCtEntryId(),
				targetCTSQLMode, httpServletRequest, httpServletResponse,
				targetModel, CTConstants.TYPE_LATEST);
		}

		targetModel = _ctDisplayRendererRegistry.fetchCTModel(
			targetCTCollectionId, targetCTSQLMode,
			ctEntry.getModelClassNameId(), ctEntry.getModelClassPK());

		if (targetModel == null) {
			return StringPool.BLANK;
		}

		return _getPreview(
			targetCTCollectionId, ctDisplayRenderer, ctEntry.getCtEntryId(),
			targetCTSQLMode, httpServletRequest, httpServletResponse,
			targetModel, CTConstants.TYPE_AFTER);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetLayoutContentChangesMVCResourceCommand.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

	@Reference
	private Sorts _sorts;

}