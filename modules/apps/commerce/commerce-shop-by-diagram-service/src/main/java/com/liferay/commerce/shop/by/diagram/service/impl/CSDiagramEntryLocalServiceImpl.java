/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.impl;

import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.shop.by.diagram.exception.DuplicateCSDiagramEntryException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramPinLocalService;
import com.liferay.commerce.shop.by.diagram.service.base.CSDiagramEntryLocalServiceBaseImpl;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramPinPersistence;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry",
	service = AopService.class
)
public class CSDiagramEntryLocalServiceImpl
	extends CSDiagramEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CSDiagramEntry addCSDiagramEntry(
			long userId, long cpDefinitionId, long cpInstanceId,
			long cProductId, boolean diagram, int quantity, String sequence,
			String sku, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(null, cpDefinitionId, sequence);

		long csDiagramEntryId = counterLocalService.increment();

		CSDiagramEntry csDiagramEntry = csDiagramEntryPersistence.create(
			csDiagramEntryId);

		csDiagramEntry.setCompanyId(user.getCompanyId());
		csDiagramEntry.setUserId(user.getUserId());
		csDiagramEntry.setUserName(user.getFullName());
		csDiagramEntry.setCPDefinitionId(cpDefinitionId);
		csDiagramEntry.setCPInstanceId(cpInstanceId);
		csDiagramEntry.setCProductId(cProductId);
		csDiagramEntry.setDiagram(diagram);
		csDiagramEntry.setQuantity(quantity);
		csDiagramEntry.setSequence(sequence);
		csDiagramEntry.setSku(sku);
		csDiagramEntry.setExpandoBridgeAttributes(serviceContext);

		return csDiagramEntryPersistence.update(csDiagramEntry);
	}

	@Override
	public void deleteCSDiagramEntries(long cpDefinitionId)
		throws PortalException {

		List<CSDiagramEntry> csDiagramEntries =
			csDiagramEntryPersistence.findByCPDefinitionId(cpDefinitionId);

		for (CSDiagramEntry csDiagramEntry : csDiagramEntries) {
			csDiagramEntryLocalService.deleteCSDiagramEntry(csDiagramEntry);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CSDiagramEntry deleteCSDiagramEntry(CSDiagramEntry csDiagramEntry)
		throws PortalException {

		csDiagramEntry = csDiagramEntryPersistence.remove(csDiagramEntry);

		List<CSDiagramPin> csDiagramPins =
			_csDiagramPinPersistence.findByCPDefinitionId(
				csDiagramEntry.getCPDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (CSDiagramPin csDiagramPin : csDiagramPins) {
			if ((csDiagramEntry.getCPDefinitionId() ==
					csDiagramPin.getCPDefinitionId()) &&
				Objects.equals(
					csDiagramEntry.getSequence(), csDiagramPin.getSequence())) {

				_csDiagramPinLocalService.deleteCSDiagramPin(
					csDiagramPin.getCSDiagramPinId());
			}
		}

		_expandoRowLocalService.deleteRows(
			csDiagramEntry.getCSDiagramEntryId());

		return csDiagramEntry;
	}

	@Override
	public CSDiagramEntry fetchCSDiagramEntry(
		long cpDefinitionId, String sequence) {

		return csDiagramEntryPersistence.fetchByCPDI_S(
			cpDefinitionId, sequence);
	}

	@Override
	public List<CSDiagramEntry> getCPDefinitionRelatedCSDiagramEntries(
		long cpDefinitionId) {

		Set<CSDiagramEntry> csDiagramEntries = new HashSet<>();

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinitionId);

		for (CPInstance cpInstance : cpInstances) {
			csDiagramEntries.addAll(
				csDiagramEntryPersistence.findByCPInstanceId(
					cpInstance.getCPInstanceId()));
		}

		return new ArrayList<>(csDiagramEntries);
	}

	@Override
	public List<CSDiagramEntry> getCProductCSDiagramEntries(
			long cProductId, int start, int end,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws PortalException {

		return csDiagramEntryPersistence.findByCProductId(
			cProductId, start, end, orderByComparator);
	}

	@Override
	public List<CSDiagramEntry> getCSDiagramEntries(
		long cpDefinitionId, int start, int end) {

		return csDiagramEntryPersistence.findByCPDefinitionId(
			cpDefinitionId, start, end);
	}

	@Override
	public int getCSDiagramEntriesCount(long cpDefinitionId) {
		return csDiagramEntryPersistence.countByCPDefinitionId(cpDefinitionId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CSDiagramEntry getCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws PortalException {

		return csDiagramEntryPersistence.findByCPDI_S(cpDefinitionId, sequence);
	}

	@Override
	public CSDiagramEntry updateCSDiagramEntry(
			long csDiagramEntryId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			ServiceContext serviceContext)
		throws PortalException {

		CSDiagramEntry csDiagramEntry =
			csDiagramEntryPersistence.findByPrimaryKey(csDiagramEntryId);

		_validate(csDiagramEntry, csDiagramEntry.getCPDefinitionId(), sequence);

		csDiagramEntry.setCPInstanceId(cpInstanceId);
		csDiagramEntry.setCProductId(cProductId);
		csDiagramEntry.setDiagram(diagram);
		csDiagramEntry.setQuantity(quantity);
		csDiagramEntry.setSequence(sequence);
		csDiagramEntry.setSku(sku);
		csDiagramEntry.setExpandoBridgeAttributes(serviceContext);

		return csDiagramEntryPersistence.update(csDiagramEntry);
	}

	private void _validate(
			CSDiagramEntry oldCSDiagramEntry, long cpDefinitionId,
			String sequence)
		throws PortalException {

		CSDiagramEntry csDiagramEntry = csDiagramEntryPersistence.fetchByCPDI_S(
			cpDefinitionId, sequence);

		if (!Objects.equals(oldCSDiagramEntry, csDiagramEntry)) {
			throw new DuplicateCSDiagramEntryException();
		}
	}

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CSDiagramPinLocalService _csDiagramPinLocalService;

	@Reference
	private CSDiagramPinPersistence _csDiagramPinPersistence;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private UserLocalService _userLocalService;

}