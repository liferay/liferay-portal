/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTEntryTable;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.SelectOption;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Samuel Trong Tran
 */
public class ViewRelatedEntriesDisplayContext {

	public ViewRelatedEntriesDisplayContext(
		CTCollectionLocalService ctCollectionLocalService,
		CTDisplayRendererRegistry ctDisplayRendererRegistry,
		CTEntryLocalService ctEntryLocalService,
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse, UserLocalService userLocalService) {

		_ctCollectionLocalService = ctCollectionLocalService;
		_ctDisplayRendererRegistry = ctDisplayRendererRegistry;
		_ctEntryLocalService = ctEntryLocalService;
		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_userLocalService = userLocalService;

		_ctCollectionId = ParamUtil.getLong(renderRequest, "ctCollectionId");
		_ctEntryIds = StringUtil.split(
			ParamUtil.getString(httpServletRequest, "id"), 0L);
		_modelClassNameId = ParamUtil.getLong(
			renderRequest, "modelClassNameId");
		_modelClassPK = ParamUtil.getLong(renderRequest, "modelClassPK");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getCTCollectionId() {
		return _ctCollectionId;
	}

	public <T extends BaseModel<T>> Map<String, Object> getReactData()
		throws Exception {

		List<Long> ctEntryIds = ListUtil.fromArray(_ctEntryIds);

		if ((_modelClassNameId > 0) && (_modelClassPK > 0)) {
			CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(
				_ctCollectionId, _modelClassNameId, _modelClassPK);

			if (ctEntry != null) {
				ctEntryIds.add(ctEntry.getCtEntryId());
			}
		}

		Map<Long, List<CTEntry>> relatedCTEntriesMap =
			_ctCollectionLocalService.getRelatedCTEntriesMap(
				_ctCollectionId,
				ListUtil.toLongArray(ctEntryIds, GetterUtil::getLong));

		Set<CTEntry> ctEntries = new HashSet<>();

		for (List<CTEntry> value : relatedCTEntriesMap.values()) {
			ctEntries.addAll(value);
		}

		return HashMapBuilder.<String, Object>put(
			"ctEntriesJSONArray",
			() -> {
				JSONArray ctEntriesJSONArray =
					JSONFactoryUtil.createJSONArray();

				for (CTEntry ctEntry : ctEntries) {
					ResourceURL dataURL = _renderResponse.createResourceURL();

					dataURL.setResourceID(
						"/change_tracking/get_entry_render_data");
					dataURL.setParameter(
						"ctEntryId", String.valueOf(ctEntry.getCtEntryId()));

					ctEntriesJSONArray.put(
						JSONUtil.put(
							"ctEntryId", ctEntry.getCtEntryId()
						).put(
							"dataURL", dataURL.toString()
						).put(
							"description",
							_ctDisplayRendererRegistry.getEntryDescription(
								_httpServletRequest, ctEntry)
						).put(
							"modelClassNameId", ctEntry.getModelClassNameId()
						).put(
							"modelClassPK", ctEntry.getModelClassPK()
						).put(
							"title",
							_ctDisplayRendererRegistry.getTitle(
								ctEntry.getCtCollectionId(), ctEntry,
								_themeDisplay.getLocale())
						).put(
							"userId", ctEntry.getUserId()
						));
				}

				return ctEntriesJSONArray;
			}
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).put(
			"typeNames",
			DisplayContextUtil.getTypeNamesJSONObject(
				relatedCTEntriesMap.keySet(), _ctDisplayRendererRegistry,
				_themeDisplay)
		).put(
			"userInfo",
			DisplayContextUtil.getUserInfoJSONObject(
				CTEntryTable.INSTANCE.userId.eq(UserTable.INSTANCE.userId),
				CTEntryTable.INSTANCE, _themeDisplay, _userLocalService,
				_getPredicate(new ArrayList<>(ctEntries)))
		).build();
	}

	public String getRedirectURL() {
		String redirect = ParamUtil.getString(_renderRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/change_tracking/view_changes"
		).setParameter(
			"ctCollectionId", _ctCollectionId
		).buildString();
	}

	public List<SelectOption> getSelectOptions() {
		List<SelectOption> selectOptions = new ArrayList<>();

		List<CTCollection> ctCollections =
			_ctCollectionLocalService.getCTCollections(
				_themeDisplay.getCompanyId(), WorkflowConstants.STATUS_DRAFT,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		selectOptions.add(
			new SelectOption(
				LanguageUtil.get(_themeDisplay.getLocale(), "none"),
				StringPool.BLANK));

		for (CTCollection ctCollection : ctCollections) {
			if (ctCollection.getCtCollectionId() != _ctCollectionId) {
				selectOptions.add(
					new SelectOption(
						ctCollection.getName(),
						String.valueOf(ctCollection.getCtCollectionId())));
			}
		}

		return selectOptions;
	}

	public String getSubmitDiscardURL() {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/change_tracking/discard_changes"
		).setRedirect(
			getRedirectURL()
		).setParameter(
			"ctCollectionId", _ctCollectionId
		).setParameter(
			"ctEntryIds", StringUtil.merge(_ctEntryIds)
		).setParameter(
			"modelClassNameId", _modelClassNameId
		).setParameter(
			"modelClassPK", _modelClassPK
		).buildString();
	}

	public String getSubmitMoveURL() {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/change_tracking/move_changes"
		).setRedirect(
			getRedirectURL()
		).setParameter(
			"ctCollectionId", _ctCollectionId
		).setParameter(
			"ctEntryIds", StringUtil.merge(_ctEntryIds)
		).setParameter(
			"modelClassNameId", _modelClassNameId
		).setParameter(
			"modelClassPK", _modelClassPK
		).buildString();
	}

	private Predicate _getPredicate(List<CTEntry> ctEntries) {
		Predicate predicate = null;

		Long[] ctEntryIds = TransformUtil.transformToArray(
			ctEntries, ctEntry -> ctEntry.getCtEntryId(), Long.class);

		for (int i = 0; i < ctEntryIds.length; i += _BATCH_SIZE) {
			Long[] curCTEntryIds = Arrays.copyOfRange(
				ctEntryIds, i, Math.min(ctEntryIds.length, i + _BATCH_SIZE));

			predicate = Predicate.or(
				predicate, CTEntryTable.INSTANCE.ctEntryId.in(curCTEntryIds));
		}

		return predicate;
	}

	private static final int _BATCH_SIZE = 1000;

	private final long _ctCollectionId;
	private final CTCollectionLocalService _ctCollectionLocalService;
	private final CTDisplayRendererRegistry _ctDisplayRendererRegistry;
	private final long[] _ctEntryIds;
	private final CTEntryLocalService _ctEntryLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final long _modelClassNameId;
	private final long _modelClassPK;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private final UserLocalService _userLocalService;

}