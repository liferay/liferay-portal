/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.impl;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.reports.engine.console.exception.SourceDriverClassNameException;
import com.liferay.portal.reports.engine.console.internal.util.SourceDriverPasswordUtil;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.base.SourceLocalServiceBaseImpl;
import com.liferay.portal.reports.engine.console.util.ReportsEngineConsoleUtil;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Gavin Wan
 */
@Component(
	property = "model.class.name=com.liferay.portal.reports.engine.console.model.Source",
	service = AopService.class
)
public class SourceLocalServiceImpl extends SourceLocalServiceBaseImpl {

	@Override
	public Source addSource(
			long userId, long groupId, Map<Locale, String> nameMap,
			String driverClassName, String driverUrl, String driverUserName,
			String driverPassword, ServiceContext serviceContext)
		throws PortalException {

		// Source

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		_validate(driverClassName, driverUrl, driverUserName, driverPassword);

		long sourceId = counterLocalService.increment();

		Source source = sourceLocalService.createSource(sourceId);

		source.setUuid(serviceContext.getUuid());
		source.setGroupId(groupId);
		source.setCompanyId(user.getCompanyId());
		source.setUserId(user.getUserId());
		source.setUserName(user.getFullName());
		source.setCreateDate(serviceContext.getCreateDate(date));
		source.setModifiedDate(serviceContext.getModifiedDate(date));
		source.setNameMap(nameMap);
		source.setDriverClassName(driverClassName);
		source.setDriverUrl(driverUrl);
		source.setDriverUserName(driverUserName);
		source.setDriverPassword(driverPassword);

		source = sourcePersistence.update(source);

		// Resources

		_resourceLocalService.addModelResources(source, serviceContext);

		return source;
	}

	@Override
	public Source deleteSource(long sourceId) throws PortalException {
		Source source = sourcePersistence.findByPrimaryKey(sourceId);

		return sourceLocalService.deleteSource(source);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Source deleteSource(Source source) throws PortalException {

		// Source

		sourcePersistence.remove(source);

		// Resources

		_resourceLocalService.deleteResource(
			source.getCompanyId(), Source.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, source.getSourceId());

		return source;
	}

	@Override
	public void deleteSources(long groupId) throws PortalException {
		List<Source> sources = sourcePersistence.findByGroupId(groupId);

		for (Source source : sources) {
			sourceLocalService.deleteSource(source);
		}
	}

	@Override
	public String[] getAttachmentsFileNames(Source source) {
		return _store.getFileNames(
			source.getCompanyId(), CompanyConstants.SYSTEM,
			source.getAttachmentsDir());
	}

	@Override
	public Source getSource(long sourceId) throws PortalException {
		return sourcePersistence.findByPrimaryKey(sourceId);
	}

	@Override
	public List<Source> getSources(
		long groupId, String name, String driverUrl, boolean andSearch,
		int start, int end, OrderByComparator<Source> orderByComparator) {

		return sourceFinder.findByG_N_DU(
			groupId, name, driverUrl, andSearch, start, end, orderByComparator);
	}

	@Override
	public int getSourcesCount(
		long groupId, String name, String driverUrl, boolean andSearch) {

		return sourceFinder.countByG_N_DU(groupId, name, driverUrl, andSearch);
	}

	@Override
	public Source updateSource(
			long sourceId, Map<Locale, String> nameMap, String driverClassName,
			String driverUrl, String driverUserName, String driverPassword,
			ServiceContext serviceContext)
		throws PortalException {

		// Source

		Source source = sourcePersistence.findByPrimaryKey(sourceId);

		if (Validator.isNull(driverPassword)) {
			driverPassword = source.getDriverPassword();
		}

		_validate(
			driverClassName, driverUrl, driverUserName,
			SourceDriverPasswordUtil.getDriverPassword(
				source.getCompanyId(), driverPassword, sourceId));

		source.setModifiedDate(serviceContext.getModifiedDate(null));
		source.setNameMap(nameMap);
		source.setDriverClassName(driverClassName);
		source.setDriverUrl(driverUrl);
		source.setDriverUserName(driverUserName);
		source.setDriverPassword(driverPassword);

		return sourcePersistence.update(source);
	}

	private void _validate(
			String driverClassName, String driverUrl, String driverUserName,
			String driverPassword)
		throws PortalException {

		ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();

		try {
			Class.forName(driverClassName, true, portalClassLoader);
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new SourceDriverClassNameException(classNotFoundException);
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				portalClassLoader)) {

			ReportsEngineConsoleUtil.validateJDBCConnection(
				driverClassName, driverUrl, driverUserName, driverPassword);
		}
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference
	private UserLocalService _userLocalService;

}