/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionTable;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTEntryTable;
import com.liferay.change.tracking.service.base.CTEntryLocalServiceBaseImpl;
import com.liferay.change.tracking.service.persistence.CTCollectionPersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Daniel Kocsis
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.model.CTEntry",
	service = AopService.class
)
@CTAware
public class CTEntryLocalServiceImpl extends CTEntryLocalServiceBaseImpl {

	@Override
	public CTEntry addCTEntry(
			String externalReferenceCode, long ctCollectionId,
			long modelClassNameId, CTModel<?> ctModel, long userId,
			int changeType)
		throws PortalException {

		CTCollection ctCollection = _ctCollectionPersistence.findByPrimaryKey(
			ctCollectionId);

		if (ctCollection.isReadOnly()) {
			throw new PortalException(
				"Change tracking collection " + ctCollection + " is read only");
		}

		long ctEntryId = counterLocalService.increment(CTEntry.class.getName());

		CTEntry ctEntry = ctEntryPersistence.create(ctEntryId);

		ctEntry.setExternalReferenceCode(externalReferenceCode);
		ctEntry.setCompanyId(ctCollection.getCompanyId());
		ctEntry.setUserId(userId);
		ctEntry.setCtCollectionId(ctCollectionId);
		ctEntry.setModelClassNameId(modelClassNameId);
		ctEntry.setModelClassPK(ctModel.getPrimaryKey());
		ctEntry.setModelMvccVersion(ctModel.getMvccVersion());
		ctEntry.setChangeType(changeType);

		return ctEntryPersistence.update(ctEntry);
	}

	@Override
	public CTEntry deleteCTEntry(CTEntry ctEntry) throws PortalException {
		return deleteCTEntry(ctEntry, false);
	}

	@Override
	public CTEntry deleteCTEntry(CTEntry ctEntry, boolean force)
		throws PortalException {

		CTCollection ctCollection = _ctCollectionPersistence.findByPrimaryKey(
			ctEntry.getCtCollectionId());

		if (!force && ctCollection.isReadOnly()) {
			throw new PortalException(
				"Change tracking collection " + ctCollection + " is read only");
		}

		return ctEntryPersistence.remove(ctEntry);
	}

	@Override
	public CTEntry fetchCTEntry(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		return ctEntryPersistence.fetchByC_MCNI_MCPK(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	@Override
	public List<CTEntry> getCTCollectionCTEntries(long ctCollectionId) {
		return getCTCollectionCTEntries(
			ctCollectionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	@Override
	public List<CTEntry> getCTCollectionCTEntries(
		long ctCollectionId, int start, int end,
		OrderByComparator<CTEntry> orderByComparator) {

		if (ctCollectionId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			return Collections.emptyList();
		}

		return ctEntryPersistence.findByCtCollectionId(
			ctCollectionId, start, end, orderByComparator);
	}

	@Override
	public int getCTCollectionCTEntriesCount(long ctCollectionId) {
		if (ctCollectionId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			return 0;
		}

		return ctEntryPersistence.countByCtCollectionId(ctCollectionId);
	}

	@Override
	public List<CTEntry> getCTEntries(
		long ctCollectionId, long modelClassNameId) {

		return ctEntryPersistence.findByC_MCNI(
			ctCollectionId, modelClassNameId);
	}

	@Override
	public List<CTEntry> getCTEntries(long[] ctEntryIds) {
		Set<Serializable> primaryKeys = new HashSet<>(
			TransformUtil.transformToList(ctEntryIds, GetterUtil::getLong));

		Map<Serializable, CTEntry> ctEntriesMap =
			ctEntryPersistence.fetchByPrimaryKeys(primaryKeys);

		return new ArrayList<>(ctEntriesMap.values());
	}

	@Override
	public long getCTRowCTCollectionId(CTEntry ctEntry) throws PortalException {
		CTCollection ctCollection = _ctCollectionPersistence.findByPrimaryKey(
			ctEntry.getCtCollectionId());

		if ((ctCollection.getStatus() == WorkflowConstants.STATUS_DRAFT) ||
			(ctCollection.getStatus() == WorkflowConstants.STATUS_PENDING)) {

			return ctCollection.getCtCollectionId();
		}

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			CTEntryTable.INSTANCE.ctCollectionId
		).from(
			CTEntryTable.INSTANCE
		).innerJoinON(
			CTCollectionTable.INSTANCE,
			CTCollectionTable.INSTANCE.ctCollectionId.eq(
				CTEntryTable.INSTANCE.ctCollectionId
			).and(
				CTCollectionTable.INSTANCE.status.eq(
					WorkflowConstants.STATUS_APPROVED)
			)
		).where(
			CTEntryTable.INSTANCE.modelClassNameId.eq(
				ctEntry.getModelClassNameId()
			).and(
				CTEntryTable.INSTANCE.modelClassPK.eq(ctEntry.getModelClassPK())
			).and(
				CTCollectionTable.INSTANCE.statusDate.gt(
					ctCollection.getStatusDate())
			)
		).orderBy(
			CTCollectionTable.INSTANCE.statusDate.ascending()
		).limit(
			0, 1
		);

		List<Long> ctCollectionIds = ctEntryPersistence.dslQuery(dslQuery);

		if (ctCollectionIds.isEmpty()) {
			return CTConstants.CT_COLLECTION_ID_PRODUCTION;
		}

		return ctCollectionIds.get(0);
	}

	@Override
	public List<Long> getExclusiveModelClassPKs(
		long ctCollectionId, long modelClassNameId) {

		List<CTEntry> ctEntries = ctEntryPersistence.findByC_MCNI(
			ctCollectionId, modelClassNameId);

		if (ctEntries.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> modelClassPKs = ListUtil.toList(
			ctEntries, CTEntry::getModelClassPK);

		for (CTEntry ctEntry :
				ctEntryPersistence.findByNotC_MCNI_MCPK(
					ctCollectionId, modelClassNameId,
					ArrayUtil.toArray(modelClassPKs.toArray(new Long[0])))) {

			modelClassPKs.remove(ctEntry.getModelClassPK());
		}

		return modelClassPKs;
	}

	@Override
	public boolean hasCTEntries(long ctCollectionId, long modelClassNameId) {
		int count = ctEntryPersistence.countByC_MCNI(
			ctCollectionId, modelClassNameId);

		if (count == 0) {
			return false;
		}

		return true;
	}

	@Override
	public boolean hasCTEntry(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		int count = ctEntryPersistence.countByC_MCNI_MCPK(
			ctCollectionId, modelClassNameId, modelClassPK);

		if (count == 0) {
			return false;
		}

		return true;
	}

	@Override
	public boolean hasUnpublishedCTEntries(
		long modelClassNameId, long modelClassPK, int changeType) {

		int count = ctEntryLocalService.dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				CTEntryTable.INSTANCE.ctEntryId
			).from(
				CTEntryTable.INSTANCE
			).innerJoinON(
				CTCollectionTable.INSTANCE,
				CTCollectionTable.INSTANCE.ctCollectionId.eq(
					CTEntryTable.INSTANCE.ctCollectionId)
			).where(
				CTCollectionTable.INSTANCE.status.eq(
					WorkflowConstants.STATUS_DRAFT
				).and(
					CTEntryTable.INSTANCE.modelClassNameId.eq(modelClassNameId)
				).and(
					CTEntryTable.INSTANCE.modelClassPK.eq(modelClassPK)
				).and(
					CTEntryTable.INSTANCE.changeType.eq(changeType)
				)
			));

		if (count == 0) {
			return false;
		}

		return true;
	}

	@Override
	public CTEntry updateCTEntry(CTEntry ctEntry) {
		CTCollection ctCollection = _ctCollectionPersistence.fetchByPrimaryKey(
			ctEntry.getCtCollectionId());

		if (ctCollection == null) {
			throw new SystemException(
				"No change tracking collection exists for " + ctEntry);
		}

		int status = ctCollection.getStatus();

		if ((status != WorkflowConstants.STATUS_DRAFT) &&
			(status != WorkflowConstants.STATUS_PENDING)) {

			throw new SystemException(
				"Change tracking collection " + ctCollection + " is read only");
		}

		return ctEntryPersistence.update(ctEntry);
	}

	@Override
	public CTEntry updateModelMvccVersion(
		long ctEntryId, long modelMvccVersion) {

		CTEntry ctEntry = ctEntryPersistence.fetchByPrimaryKey(ctEntryId);

		ctEntry.setModelMvccVersion(modelMvccVersion);

		return ctEntryPersistence.update(ctEntry);
	}

	@Reference
	private CTCollectionPersistence _ctCollectionPersistence;

}