/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR
 * LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.change.tracking.spi.history;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionTable;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTEntryTable;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.history.CTCollectionHistoryProvider;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBArticleTable;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTCollectionHistoryProvider.class)
public class KBArticleCTCollectionHistoryProvider
	implements CTCollectionHistoryProvider<KBArticle> {

	@Override
	public List<CTCollection> getCTCollections(long classNameId, long classPK)
		throws PortalException {

		KBArticle kbArticle = _kbArticleLocalService.getKBArticle(classPK);

		return _ctCollectionLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				CTCollectionTable.INSTANCE
			).from(
				CTCollectionTable.INSTANCE
			).innerJoinON(
				CTEntryTable.INSTANCE,
				CTEntryTable.INSTANCE.ctCollectionId.eq(
					CTCollectionTable.INSTANCE.ctCollectionId
				).and(
					CTEntryTable.INSTANCE.modelClassNameId.eq(
						classNameId
					).and(
						CTEntryTable.INSTANCE.modelClassPK.in(
							DSLQueryFactoryUtil.select(
								KBArticleTable.INSTANCE.kbArticleId
							).from(
								KBArticleTable.INSTANCE
							).where(
								KBArticleTable.INSTANCE.resourcePrimKey.eq(
									kbArticle.getResourcePrimKey())
							))
					)
				)
			).where(
				CTCollectionTable.INSTANCE.ctCollectionId.neq(
					CTCollectionThreadLocal.getCTCollectionId()
				).and(
					CTCollectionTable.INSTANCE.status.neq(
						WorkflowConstants.STATUS_EXPIRED)
				)
			).orderBy(
				CTCollectionTable.INSTANCE.status.descending(),
				CTCollectionTable.INSTANCE.statusDate.descending()
			));
	}

	@Override
	public CTEntry getCTEntry(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		List<Long> resourcePrimKeys = _ctEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				KBArticleTable.INSTANCE.resourcePrimKey
			).from(
				KBArticleTable.INSTANCE
			).where(
				KBArticleTable.INSTANCE.kbArticleId.eq(modelClassPK)
			));

		if (resourcePrimKeys.isEmpty()) {
			return null;
		}

		List<Long> kbArticleIds = _ctEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				KBArticleTable.INSTANCE.kbArticleId
			).from(
				KBArticleTable.INSTANCE
			).where(
				KBArticleTable.INSTANCE.resourcePrimKey.eq(
					resourcePrimKeys.get(0)
				).and(
					KBArticleTable.INSTANCE.ctCollectionId.eq(ctCollectionId)
				)
			).orderBy(
				KBArticleTable.INSTANCE.modifiedDate.descending()
			));

		if (kbArticleIds.isEmpty()) {
			return null;
		}

		return _ctEntryLocalService.fetchCTEntry(
			ctCollectionId, modelClassNameId, kbArticleIds.get(0));
	}

	@Override
	public Class<KBArticle> getModelClass() {
		return KBArticle.class;
	}

	@Override
	public UnsafeConsumer<SearchUtil.SearchContext, Exception>
		getSearchContextUnsafeConsumer(long classNameId, long classPK) {

		return searchContext -> {
			searchContext.setAttribute(
				"modelClassNameId", new Long[] {classNameId});

			if (classPK <= 0) {
				return;
			}

			List<Long> resourcePrimKeys = _ctEntryLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					KBArticleTable.INSTANCE.resourcePrimKey
				).from(
					KBArticleTable.INSTANCE
				).where(
					KBArticleTable.INSTANCE.kbArticleId.eq(classPK)
				));

			if (resourcePrimKeys.isEmpty()) {
				searchContext.setAttribute(
					"modelClassPK", new Long[] {classPK});

				return;
			}

			List<Long> modelClassPKs = _ctCollectionLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					CTEntryTable.INSTANCE.modelClassPK
				).from(
					CTEntryTable.INSTANCE
				).where(
					CTEntryTable.INSTANCE.modelClassPK.in(
						DSLQueryFactoryUtil.select(
							KBArticleTable.INSTANCE.kbArticleId
						).from(
							KBArticleTable.INSTANCE
						).where(
							KBArticleTable.INSTANCE.resourcePrimKey.eq(
								resourcePrimKeys.get(0))
						))
				));

			searchContext.setAttribute(
				"modelClassPK", modelClassPKs.toArray(new Long[0]));
		};
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

}