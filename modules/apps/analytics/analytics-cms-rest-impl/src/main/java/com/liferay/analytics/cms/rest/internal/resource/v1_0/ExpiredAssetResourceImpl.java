/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.ExpiredAsset;
import com.liferay.analytics.cms.rest.internal.depot.entry.util.DepotEntryUtil;
import com.liferay.analytics.cms.rest.internal.resource.v1_0.util.ObjectEntryVersionTitleExpressionUtil;
import com.liferay.analytics.cms.rest.resource.v1_0.ExpiredAssetResource;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinitionTable;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectEntryVersionTable;
import com.liferay.object.model.ObjectFolderTable;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Thiago Buarque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/expired-asset.properties",
	scope = ServiceScope.PROTOTYPE, service = ExpiredAssetResource.class
)
public class ExpiredAssetResourceImpl extends BaseExpiredAssetResourceImpl {

	@Override
	public Page<ExpiredAsset> getExpiredAssetsPage(
			Long depotEntryId, String languageId, Pagination pagination)
		throws Exception {

		Long[] groupIds = DepotEntryUtil.getGroupIds(
			DepotEntryUtil.getDepotEntries(
				contextCompany.getCompanyId(), depotEntryId));

		Locale locale = LocaleUtil.fromLanguageId(languageId, true, false);

		if (locale == null) {
			locale = contextUser.getLocale();
		}

		return Page.of(
			transform(
				_getExpiredObjectsList(
					languageId, groupIds, LanguageUtil.getLanguageId(locale),
					pagination),
				objects -> {
					ExpiredAsset expiredAsset = new ExpiredAsset();

					long objectEntryId = (Long)objects[3];

					expiredAsset.setHref(
						() -> StringBundler.concat(
							_portal.getPortalURL(contextHttpServletRequest),
							_portal.getPathMain(),
							GroupConstants.CMS_FRIENDLY_URL,
							"/edit_content_item?&p_l_mode=read&p_p_state=",
							LiferayWindowState.POP_UP, "&objectEntryId=",
							objectEntryId));

					expiredAsset.setTitle(
						() -> {
							String localizedTitle = String.valueOf(objects[1]);

							if (Validator.isNotNull(localizedTitle)) {
								return localizedTitle;
							}

							return String.valueOf(objects[4]);
						});
					expiredAsset.setUsages(
						() -> _getUsagesCount(
							String.valueOf(objects[0]), (Long)objects[2],
							objectEntryId));

					return expiredAsset;
				}),
			pagination, _getExpiredObjectsListTotalCount(languageId, groupIds));
	}

	private List<Object[]> _getExpiredObjectsList(
		String filterLanguageId, Long[] groupIds, String languageId,
		Pagination pagination) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			_objectDefinitionTable.className,
			DSLFunctionFactoryUtil.castClobText(
				ObjectEntryVersionTitleExpressionUtil.
					getLocalizedTitleExpression(languageId)
			).as(
				"localized_title"
			),
			_objectEntryTable.objectDefinitionId,
			_objectEntryTable.objectEntryId,
			DSLFunctionFactoryUtil.castClobText(
				ObjectEntryVersionTitleExpressionUtil.getTitleExpression()
			).as(
				"title"
			)
		).from(
			ObjectEntryTable.INSTANCE
		).innerJoinON(
			_objectDefinitionTable,
			_objectDefinitionTable.objectDefinitionId.eq(
				_objectEntryTable.objectDefinitionId)
		).innerJoinON(
			_objectFolderTable,
			_objectDefinitionTable.objectFolderId.eq(
				_objectFolderTable.objectFolderId)
		).innerJoinON(
			_objectEntryVersionTable,
			_objectEntryVersionTable.objectEntryId.eq(
				_objectEntryTable.objectEntryId
			).and(
				_objectEntryVersionTable.version.eq(_objectEntryTable.version)
			)
		).where(
			_getPredicate(groupIds, filterLanguageId)
		).orderBy(
			_objectEntryVersionTable.statusDate.descending()
		).limit(
			pagination.getStartPosition(), pagination.getEndPosition()
		);

		return _objectEntryLocalService.dslQuery(dslQuery);
	}

	private long _getExpiredObjectsListTotalCount(
		String filterLanguageId, Long[] groupIds) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			DSLFunctionFactoryUtil.count(
				_objectEntryTable.objectEntryId
			).as(
				"totalCount"
			)
		).from(
			ObjectEntryTable.INSTANCE
		).innerJoinON(
			_objectDefinitionTable,
			_objectDefinitionTable.objectDefinitionId.eq(
				_objectEntryTable.objectDefinitionId)
		).innerJoinON(
			_objectFolderTable,
			_objectDefinitionTable.objectFolderId.eq(
				_objectFolderTable.objectFolderId)
		).innerJoinON(
			_objectEntryVersionTable,
			_objectEntryVersionTable.objectEntryId.eq(
				_objectEntryTable.objectEntryId
			).and(
				_objectEntryVersionTable.version.eq(_objectEntryTable.version)
			)
		).where(
			_getPredicate(groupIds, filterLanguageId)
		);

		List<Object[]> results = _objectEntryLocalService.dslQuery(dslQuery);

		return GetterUtil.getLong(results.get(0));
	}

	private Predicate _getPredicate(Long[] groupIds, String languageId) {
		Predicate predicate = _objectEntryTable.groupId.in(
			groupIds
		).and(
			_objectEntryTable.status.eq(WorkflowConstants.STATUS_EXPIRED)
		).and(
			_objectFolderTable.externalReferenceCode.in(
				new String[] {"L_CMS_CONTENT_STRUCTURES", "L_CMS_FILE_TYPES"})
		);

		if (Validator.isNotNull(languageId)) {
			predicate = predicate.and(
				DSLFunctionFactoryUtil.castClobText(
					ObjectEntryVersionTitleExpressionUtil.
						getLocalizedTitleExpression(languageId)
				).isNotNull());
		}

		return predicate;
	}

	private long _getUsagesCount(
			String className, long objectDefinitionId, long objectEntryId)
		throws Exception {

		int usagesCount =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount(
					_portal.getClassNameId(className), objectEntryId);

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		try {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

			List<ObjectRelationship> objectRelationships =
				_objectRelationshipLocalService.getObjectRelationships(
					objectDefinitionId);

			for (ObjectRelationship objectRelationship : objectRelationships) {
				ObjectRelatedModelsProvider objectRelatedModelsProvider =
					_objectRelatedModelsProviderRegistry.
						getObjectRelatedModelsProvider(
							className, contextCompany.getCompanyId(),
							objectRelationship.getType());

				usagesCount +=
					objectRelatedModelsProvider.getRelatedModelsCount(
						0, objectRelationship.getObjectRelationshipId(), null,
						objectEntryId, null);
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}

		return usagesCount;
	}

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	private final ObjectDefinitionTable _objectDefinitionTable =
		ObjectDefinitionTable.INSTANCE;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	private final ObjectEntryTable _objectEntryTable =
		ObjectEntryTable.INSTANCE;
	private final ObjectEntryVersionTable _objectEntryVersionTable =
		ObjectEntryVersionTable.INSTANCE;
	private final ObjectFolderTable _objectFolderTable =
		ObjectFolderTable.INSTANCE;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

}