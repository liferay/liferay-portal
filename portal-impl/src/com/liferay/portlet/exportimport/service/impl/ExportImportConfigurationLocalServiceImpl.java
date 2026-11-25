/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.exportimport.service.impl;

import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.ExportImportConfigurationNameException;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.exportimport.service.base.ExportImportConfigurationLocalServiceBaseImpl;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Daniel Kocsis
 * @author Akos Thurzo
 */
public class ExportImportConfigurationLocalServiceImpl
	extends ExportImportConfigurationLocalServiceBaseImpl {

	@CTAware
	@Override
	public ExportImportConfiguration addDraftExportImportConfiguration(
			long userId, int type, Map<String, Serializable> settingsMap)
		throws PortalException {

		return exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(
				userId, GetterUtil.getString(settingsMap.get("portletId")),
				type, settingsMap);
	}

	@CTAware
	@Override
	public ExportImportConfiguration addDraftExportImportConfiguration(
			long userId, String name, int type,
			Map<String, Serializable> settingsMap)
		throws PortalException {

		long groupId = GetterUtil.getLong(settingsMap.get("sourceGroupId"));

		if ((type == ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT) ||
			(type == ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET)) {

			groupId = GetterUtil.getLong(settingsMap.get("targetGroupId"));
		}

		return exportImportConfigurationLocalService.
			addExportImportConfiguration(
				userId, groupId, name, StringPool.BLANK, type, settingsMap,
				WorkflowConstants.STATUS_DRAFT, new ServiceContext());
	}

	@CTAware
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration addExportImportConfiguration(
			long userId, long groupId, String name, String description,
			int type, Map<String, Serializable> settingsMap, int status,
			ServiceContext serviceContext)
		throws PortalException {

		_validate(name);

		User user = _userPersistence.findByPrimaryKey(userId);

		long exportImportConfigurationId = counterLocalService.increment();

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.create(
				exportImportConfigurationId);

		exportImportConfiguration.setGroupId(groupId);
		exportImportConfiguration.setCompanyId(user.getCompanyId());
		exportImportConfiguration.setUserId(userId);
		exportImportConfiguration.setUserName(user.getFullName());
		exportImportConfiguration.setName(name);
		exportImportConfiguration.setDescription(description);
		exportImportConfiguration.setType(type);

		if (settingsMap != null) {
			exportImportConfiguration.setSettings(
				JSONFactoryUtil.serialize(settingsMap));
		}

		exportImportConfiguration.setStatus(status);
		exportImportConfiguration.setStatusByUserId(userId);
		exportImportConfiguration.setStatusByUserName(user.getScreenName());
		exportImportConfiguration.setStatusDate(new Date());

		return exportImportConfigurationPersistence.update(
			exportImportConfiguration);
	}

	@CTAware
	@Override
	public ExportImportConfiguration addExportImportConfiguration(
			long userId, long groupId, String name, String description,
			int type, Map<String, Serializable> settingsMap,
			ServiceContext serviceContext)
		throws PortalException {

		return exportImportConfigurationLocalService.
			addExportImportConfiguration(
				userId, groupId, name, description, type, settingsMap,
				WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@CTAware
	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ExportImportConfiguration deleteExportImportConfiguration(
		ExportImportConfiguration exportImportConfiguration) {

		exportImportConfigurationPersistence.remove(exportImportConfiguration);

		return exportImportConfiguration;
	}

	@CTAware
	@Override
	public ExportImportConfiguration deleteExportImportConfiguration(
			long exportImportConfigurationId)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		return exportImportConfigurationLocalService.
			deleteExportImportConfiguration(exportImportConfiguration);
	}

	@CTAware
	@Override
	public void deleteExportImportConfigurations(long groupId) {
		List<ExportImportConfiguration> exportImportConfigurations =
			exportImportConfigurationPersistence.findByGroupId(groupId);

		for (ExportImportConfiguration exportImportConfiguration :
				exportImportConfigurations) {

			exportImportConfigurationLocalService.
				deleteExportImportConfiguration(exportImportConfiguration);
		}
	}

	@Override
	public List<ExportImportConfiguration> getExportImportConfigurations(
		long groupId, int type) {

		return exportImportConfigurationPersistence.findByG_T_S(
			groupId, type, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public List<ExportImportConfiguration> getExportImportConfigurations(
		long companyId, long groupId, String keywords, int type, int start,
		int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return exportImportConfigurationFinder.findByKeywords(
			companyId, groupId, keywords, type,
			WorkflowConstants.STATUS_APPROVED, start, end, orderByComparator);
	}

	@Override
	public int getExportImportConfigurationsCount(long groupId, int type) {
		return exportImportConfigurationPersistence.countByG_T_S(
			groupId, type, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getExportImportConfigurationsCount(
		long companyId, long groupId, String keywords, int type) {

		return exportImportConfigurationFinder.countByKeywords(
			companyId, groupId, keywords, type,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration moveExportImportConfigurationToTrash(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		return exportImportConfigurationPersistence.findByPrimaryKey(
			exportImportConfigurationId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration restoreExportImportConfigurationFromTrash(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		return exportImportConfigurationPersistence.findByPrimaryKey(
			exportImportConfigurationId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration updateExportImportConfiguration(
			long userId, long exportImportConfigurationId, String name,
			String description, Map<String, Serializable> settingsMap,
			ServiceContext serviceContext)
		throws PortalException {

		_validate(name);

		User user = _userPersistence.findByPrimaryKey(userId);

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		exportImportConfiguration.setUserId(userId);
		exportImportConfiguration.setUserName(user.getFullName());
		exportImportConfiguration.setName(name);
		exportImportConfiguration.setDescription(description);

		if (settingsMap != null) {
			exportImportConfiguration.setSettings(
				JSONFactoryUtil.serialize(settingsMap));
		}

		return exportImportConfigurationPersistence.update(
			exportImportConfiguration);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ExportImportConfiguration updateStatus(
			long userId, long exportImportConfigurationId, int status)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationPersistence.findByPrimaryKey(
				exportImportConfigurationId);

		exportImportConfiguration.setStatus(status);
		exportImportConfiguration.setStatusByUserId(userId);
		exportImportConfiguration.setStatusByUserName(user.getScreenName());
		exportImportConfiguration.setStatusDate(new Date());

		return exportImportConfigurationPersistence.update(
			exportImportConfiguration);
	}

	private void _validate(String name)
		throws ExportImportConfigurationNameException.
			MustNotExceedMaximumLength {

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			ExportImportConfiguration.class.getName(), "name");

		if (Validator.isNotNull(name) && (name.length() > nameMaxLength)) {
			throw new ExportImportConfigurationNameException.
				MustNotExceedMaximumLength(name, nameMaxLength);
		}
	}

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}