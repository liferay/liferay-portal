/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.util.comparator.ObjectEntryVersionVersionComparator;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentContributor;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.internal.search.fields.MissingRequiredFieldsUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = DocumentContributor.class)
public class CMSContentMissingRequiredFieldsModelDocumentContributor
	implements DocumentContributor<ObjectEntry> {

	@Override
	public void contribute(
		Document document, BaseModel<ObjectEntry> baseModel) {

		if (!(baseModel instanceof ObjectEntry)) {
			return;
		}

		ObjectEntry objectEntry = (ObjectEntry)baseModel;

		try {
			if (!FeatureFlagManagerUtil.isEnabled(
					objectEntry.getCompanyId(), "LPD-82226")) {

				return;
			}

			ObjectDefinition objectDefinition =
				objectEntry.getObjectDefinition();

			if (!objectDefinition.isCMS() || objectEntry.isExpired() ||
				!objectEntry.isHead() || objectEntry.isInTrash() ||
				objectEntry.isRootDescendantNode()) {

				return;
			}

			ObjectEntryVersion objectEntryVersion =
				_getApprovedObjectEntryVersion(objectEntry);

			if (objectEntryVersion == null) {
				return;
			}

			int missingRequiredFieldsCount = _getMissingRequiredFieldsCount(
				objectDefinition, objectEntry, objectEntryVersion);

			if (objectDefinition.isRootNode()) {
				for (ObjectEntry descendantObjectEntry :
						_getDescendantObjectEntries(objectEntry)) {

					ObjectEntryVersion descendantObjectEntryVersion =
						_getApprovedObjectEntryVersion(descendantObjectEntry);

					if (descendantObjectEntryVersion != null) {
						missingRequiredFieldsCount +=
							_getMissingRequiredFieldsCount(
								descendantObjectEntry.getObjectDefinition(),
								descendantObjectEntry,
								descendantObjectEntryVersion);
					}
				}
			}

			if (missingRequiredFieldsCount > 0) {
				document.addNumber(
					"missingRequiredFieldsCount", missingRequiredFieldsCount);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to contribute missing required fields for object " +
						"entry " + objectEntry.getObjectEntryId(),
					exception);
			}
		}
	}

	private ObjectEntryVersion _getApprovedObjectEntryVersion(
		ObjectEntry objectEntry) {

		if (objectEntry.isApproved()) {
			return _objectEntryVersionLocalService.fetchObjectEntryVersion(
				objectEntry.getObjectEntryId(), objectEntry.getVersion());
		}

		ObjectEntry approvedObjectEntry =
			_objectEntryLocalService.fetchObjectEntryByHeadObjectEntryId(
				objectEntry.getObjectEntryId());

		if (approvedObjectEntry == null) {
			return null;
		}

		return _objectEntryVersionLocalService.
			fetchLatestApprovedObjectEntryVersion(
				objectEntry.getObjectEntryId(),
				ObjectEntryVersionVersionComparator.getInstance(false));
	}

	private List<ObjectEntry> _getDescendantObjectEntries(
		ObjectEntry objectEntry) {

		return _objectEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryTable.INSTANCE
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.status.in(
					new Integer[] {
						WorkflowConstants.STATUS_APPROVED,
						WorkflowConstants.STATUS_DRAFT,
						WorkflowConstants.STATUS_PENDING,
						WorkflowConstants.STATUS_SCHEDULED
					}
				).and(
					ObjectEntryTable.INSTANCE.rootObjectEntryId.eq(
						objectEntry.getObjectEntryId())
				).and(
					ObjectEntryTable.INSTANCE.objectEntryId.neq(
						objectEntry.getObjectEntryId())
				).and(
					ObjectEntryTable.INSTANCE.objectEntryId.eq(
						ObjectEntryTable.INSTANCE.headObjectEntryId)
				)
			));
	}

	private int _getMissingRequiredFieldsCount(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ObjectEntryVersion objectEntryVersion)
		throws Exception {

		JSONObject contentJSONObject = _jsonFactory.createJSONObject(
			objectEntryVersion.getContent());

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		return MissingRequiredFieldsUtil.getMissingRequiredFieldsCount(
			objectEntry.getDefaultLanguageId(),
			objectFieldBag.getObjectFields(),
			contentJSONObject.getJSONObject("properties"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSContentMissingRequiredFieldsModelDocumentContributor.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

}