/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTEntryTable;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.history.CTCollectionHistoryProvider;
import com.liferay.change.tracking.spi.history.CTCollectionHistoryProviderRegistry;
import com.liferay.change.tracking.spi.history.DefaultCTCollectionHistoryProvider;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/get_conflict_info"
	},
	service = MVCResourceCommand.class
)
public class GetConflictInfoMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			getConflictInfoJSONObject(resourceRequest));
	}

	protected JSONObject getConflictInfoJSONObject(
			ResourceRequest resourceRequest)
		throws PortalException {

		long currentCTCollectionId = ParamUtil.getLong(
			resourceRequest, "currentCTCollectionId");

		CTCollection currentCTCollection =
			_ctCollectionLocalService.getCTCollection(currentCTCollectionId);

		if (currentCTCollection == null) {
			return _jsonFactory.createJSONObject();
		}

		long classPK = ParamUtil.getLong(resourceRequest, "classPK");

		if (classPK == 0) {
			return _jsonFactory.createJSONObject();
		}

		long classNameId = ParamUtil.getLong(resourceRequest, "classNameId");

		List<CTEntry> ctEntries = _ctEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				CTEntryTable.INSTANCE
			).from(
				CTEntryTable.INSTANCE
			).where(
				CTEntryTable.INSTANCE.ctCollectionId.eq(
					currentCTCollection.getCtCollectionId()
				).and(
					CTEntryTable.INSTANCE.modelClassNameId.eq(
						classNameId
					).and(
						CTEntryTable.INSTANCE.modelClassPK.eq(classPK)
					)
				)
			));

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONObject conflictInfoJSONObject = _jsonFactory.createJSONObject();

		Map<Long, List<ConflictInfo>> conflictInfoMap =
			_ctCollectionLocalService.checkConflicts(
				currentCTCollection.getCompanyId(), ctEntries,
				currentCTCollection.getCtCollectionId(),
				currentCTCollection.getName(),
				CTConstants.CT_COLLECTION_ID_PRODUCTION,
				_language.get(themeDisplay.getLocale(), "production"));

		if (!conflictInfoMap.isEmpty()) {
			conflictInfoJSONObject.put(
				"danger",
				JSONUtil.put(
					"conflictIconClass", "change-tracking-conflict-icon-danger"
				).put(
					"conflictIconLabel",
					_language.get(
						themeDisplay.getLocale(), "conflict-detected-help")
				).put(
					"conflictIconName", "warning-full"
				));
		}

		CTCollectionHistoryProvider<?> ctCollectionHistoryProvider =
			_ctCollectionHistoryProviderRegistry.getCTCollectionHistoryProvider(
				classNameId);

		if (ctCollectionHistoryProvider == null) {
			ctCollectionHistoryProvider =
				new DefaultCTCollectionHistoryProvider<>();
		}

		CTCollection possibleConflictCollection = null;

		List<CTCollection> ctCollections =
			ctCollectionHistoryProvider.getCTCollections(classNameId, classPK);

		for (CTCollection ctCollection : ctCollections) {
			if ((currentCTCollection != null) &&
				((ctCollection.getStatus() ==
					WorkflowConstants.STATUS_PENDING) ||
				 ((ctCollection.getStatus() ==
					 WorkflowConstants.STATUS_DRAFT) &&
				  (ctCollection.getCtCollectionId() !=
					  currentCTCollection.getCtCollectionId())))) {

				possibleConflictCollection = ctCollection;

				break;
			}
		}

		if (possibleConflictCollection != null) {
			conflictInfoJSONObject.put(
				"warning",
				JSONUtil.put(
					"conflictIconClass", "change-tracking-conflict-icon-warning"
				).put(
					"conflictIconLabel",
					_language.get(
						themeDisplay.getLocale(),
						"concurrent-modification-help")
				).put(
					"conflictIconName", "warning-full"
				));
		}

		if (ListUtil.isEmpty(ctEntries) &&
			(possibleConflictCollection == null)) {

			return _jsonFactory.createJSONObject();
		}

		return conflictInfoJSONObject;
	}

	@Reference
	private CTCollectionHistoryProviderRegistry
		_ctCollectionHistoryProviderRegistry;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}