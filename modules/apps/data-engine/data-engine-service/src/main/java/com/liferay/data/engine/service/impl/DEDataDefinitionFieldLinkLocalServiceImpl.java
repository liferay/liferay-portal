/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service.impl;

import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.data.engine.model.DEDataDefinitionFieldLinkTable;
import com.liferay.data.engine.service.base.DEDataDefinitionFieldLinkLocalServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.data.engine.model.DEDataDefinitionFieldLink",
	service = AopService.class
)
public class DEDataDefinitionFieldLinkLocalServiceImpl
	extends DEDataDefinitionFieldLinkLocalServiceBaseImpl {

	@Override
	public DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
			long groupId, long classNameId, long classPK, long ddmStructureId,
			String fieldName)
		throws PortalException {

		return addDEDataDefinitionFieldLink(
			groupId, classNameId, classPK, ddmStructureId, fieldName,
			new ServiceContext());
	}

	@Override
	public DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
			long groupId, long classNameId, long classPK, long ddmStructureId,
			String fieldName, ServiceContext serviceContext)
		throws PortalException {

		DEDataDefinitionFieldLink deDataDefinitionFieldLink =
			deDataDefinitionFieldLinkPersistence.create(
				counterLocalService.increment());

		deDataDefinitionFieldLink.setUuid(serviceContext.getUuid());
		deDataDefinitionFieldLink.setGroupId(groupId);

		Group group = _groupLocalService.getGroup(groupId);

		deDataDefinitionFieldLink.setCompanyId(group.getCompanyId());

		Date date = new Date();

		deDataDefinitionFieldLink.setCreateDate(
			serviceContext.getCreateDate(date));
		deDataDefinitionFieldLink.setModifiedDate(
			serviceContext.getModifiedDate(date));

		deDataDefinitionFieldLink.setClassNameId(classNameId);
		deDataDefinitionFieldLink.setClassPK(classPK);
		deDataDefinitionFieldLink.setDdmStructureId(ddmStructureId);
		deDataDefinitionFieldLink.setFieldName(fieldName);

		return deDataDefinitionFieldLinkPersistence.update(
			deDataDefinitionFieldLink);
	}

	@Override
	public void deleteDEDataDefinitionFieldLinks(long ddmStructureId) {
		deDataDefinitionFieldLinkPersistence.removeByDDMStructureId(
			ddmStructureId);
	}

	@Override
	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long classPK) {

		deDataDefinitionFieldLinkPersistence.removeByC_C(classNameId, classPK);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             DEDataDefinitionFieldLinkLocalServiceImpl#deleteDEDataDefinitionFieldLinks(
	 *             long, long, String[])}
	 */
	@Deprecated
	@Override
	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String fieldName) {

		deleteDEDataDefinitionFieldLinks(
			classNameId, ddmStructureId, new String[] {fieldName});
	}

	@Override
	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String[] fieldNames) {

		for (String fieldName : fieldNames) {
			deDataDefinitionFieldLinkPersistence.removeByC_DDMSI_F(
				classNameId, ddmStructureId, fieldName);
		}
	}

	@Override
	public DEDataDefinitionFieldLink fetchDEDataDefinitionFieldLinks(
		long classNameId, long classPK, long ddmStructureId, String fieldName) {

		return deDataDefinitionFieldLinkPersistence.fetchByC_C_DDMSI_F(
			classNameId, classPK, ddmStructureId, fieldName);
	}

	@Override
	public List<Long> getClassPKS(long classNameId, long ddmStructureId) {
		return deDataDefinitionFieldLinkPersistence.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				DEDataDefinitionFieldLinkTable.INSTANCE.classPK
			).from(
				DEDataDefinitionFieldLinkTable.INSTANCE
			).where(
				DEDataDefinitionFieldLinkTable.INSTANCE.classNameId.eq(
					classNameId
				).and(
					DEDataDefinitionFieldLinkTable.INSTANCE.ddmStructureId.eq(
						ddmStructureId)
				)
			));
	}

	@Override
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long ddmStructureId) {

		return deDataDefinitionFieldLinkPersistence.findByDDMStructureId(
			ddmStructureId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             DEDataDefinitionFieldLinkLocalServiceImpl#getDEDataDefinitionFieldLinks(
	 *             long, long, String[])}
	 */
	@Deprecated
	@Override
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String fieldName) {

		return getDEDataDefinitionFieldLinks(
			classNameId, ddmStructureId, new String[] {fieldName});
	}

	@Override
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String[] fieldNames) {

		return deDataDefinitionFieldLinkPersistence.findByC_DDMSI_F(
			classNameId, ddmStructureId, fieldNames);
	}

	@Override
	public List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long ddmStructureId, String[] fieldNames) {

		return deDataDefinitionFieldLinkPersistence.findByDDMSI_F(
			ddmStructureId, fieldNames);
	}

	@Override
	public List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByClassNameIdAndClassPK(
			long classNameId, long classPK) {

		return deDataDefinitionFieldLinkPersistence.findByC_C(
			classNameId, classPK);
	}

	@Reference
	private GroupLocalService _groupLocalService;

}