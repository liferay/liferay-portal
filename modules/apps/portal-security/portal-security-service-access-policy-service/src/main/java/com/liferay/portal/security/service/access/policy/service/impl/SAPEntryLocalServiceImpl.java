/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.service.access.policy.configuration.SAPConfiguration;
import com.liferay.portal.security.service.access.policy.constants.SAPEntryConstants;
import com.liferay.portal.security.service.access.policy.exception.DuplicateSAPEntryNameException;
import com.liferay.portal.security.service.access.policy.exception.RequiredSAPEntryException;
import com.liferay.portal.security.service.access.policy.exception.SAPEntryNameException;
import com.liferay.portal.security.service.access.policy.exception.SAPEntryTitleException;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.base.SAPEntryLocalServiceBaseImpl;
import com.liferay.portal.util.PortalInstances;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.portal.security.service.access.policy.configuration.SAPConfiguration",
	property = "model.class.name=com.liferay.portal.security.service.access.policy.model.SAPEntry",
	service = AopService.class
)
public class SAPEntryLocalServiceImpl extends SAPEntryLocalServiceBaseImpl {

	@Override
	public SAPEntry addSAPEntry(
			long userId, String allowedServiceSignatures,
			boolean defaultSAPEntry, boolean enabled, String name,
			Map<Locale, String> titleMap, ServiceContext serviceContext)
		throws PortalException {

		// Service access policy entry

		User user = _userLocalService.getUser(userId);
		allowedServiceSignatures = _normalizeServiceSignatures(
			allowedServiceSignatures);

		name = StringUtil.trim(name);

		_validate(name, titleMap);

		if (sapEntryPersistence.fetchByC_N(user.getCompanyId(), name) != null) {
			throw new DuplicateSAPEntryNameException();
		}

		long sapEntryId = counterLocalService.increment();

		SAPEntry sapEntry = sapEntryPersistence.create(sapEntryId);

		sapEntry.setUuid(serviceContext.getUuid());
		sapEntry.setCompanyId(user.getCompanyId());
		sapEntry.setUserId(userId);
		sapEntry.setUserName(user.getFullName());
		sapEntry.setAllowedServiceSignatures(allowedServiceSignatures);
		sapEntry.setDefaultSAPEntry(defaultSAPEntry);
		sapEntry.setEnabled(enabled);
		sapEntry.setName(name);
		sapEntry.setTitleMap(titleMap);

		sapEntry = sapEntryPersistence.update(sapEntry, serviceContext);

		// Resources

		_resourceLocalService.addResources(
			sapEntry.getCompanyId(), 0, userId, SAPEntry.class.getName(),
			sapEntry.getSapEntryId(), false, false, false);

		return sapEntry;
	}

	@Override
	public void checkSystemSAPEntries(long companyId) throws PortalException {
		SAPEntry systemDefaultSAPEntry = sapEntryPersistence.fetchByC_N(
			companyId, _sapConfiguration.systemDefaultSAPEntryName());
		SAPEntry systemRESTClientTemplateObjectSAPEntry =
			sapEntryPersistence.fetchByC_N(
				companyId,
				_sapConfiguration.systemRESTClientTemplateObjectSAPEntryName());
		SAPEntry systemUserPasswordSAPEntry = sapEntryPersistence.fetchByC_N(
			companyId, _sapConfiguration.systemUserPasswordSAPEntryName());

		if ((systemDefaultSAPEntry != null) &&
			(systemRESTClientTemplateObjectSAPEntry != null) &&
			(systemUserPasswordSAPEntry != null)) {

			return;
		}

		long guestUserId = _userLocalService.getGuestUserId(companyId);
		Role guestRole = _roleLocalService.getRole(
			companyId, RoleConstants.GUEST);

		if (systemDefaultSAPEntry == null) {
			Map<Locale, String> titleMap = HashMapBuilder.put(
				LocaleUtil.getDefault(),
				_sapConfiguration.systemDefaultSAPEntryDescription()
			).build();

			systemDefaultSAPEntry = addSAPEntry(
				guestUserId,
				_sapConfiguration.systemDefaultSAPEntryServiceSignatures(),
				true, true, _sapConfiguration.systemDefaultSAPEntryName(),
				titleMap, new ServiceContext());

			_resourcePermissionLocalService.setResourcePermissions(
				systemDefaultSAPEntry.getCompanyId(), SAPEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(systemDefaultSAPEntry.getSapEntryId()),
				guestRole.getRoleId(), new String[] {ActionKeys.VIEW});
		}

		if (systemRESTClientTemplateObjectSAPEntry == null) {
			Map<Locale, String> titleMap = HashMapBuilder.put(
				LocaleUtil.getDefault(),
				_sapConfiguration.
					systemRESTClientTemplateObjectSAPEntryDescription()
			).build();

			systemRESTClientTemplateObjectSAPEntry = addSAPEntry(
				guestUserId,
				_sapConfiguration.
					systemRESTClientTemplateObjectSAPEntryServiceSignatures(),
				false, true,
				_sapConfiguration.systemRESTClientTemplateObjectSAPEntryName(),
				titleMap, new ServiceContext());

			_resourcePermissionLocalService.setResourcePermissions(
				systemRESTClientTemplateObjectSAPEntry.getCompanyId(),
				SAPEntry.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(
					systemRESTClientTemplateObjectSAPEntry.getSapEntryId()),
				guestRole.getRoleId(), new String[] {ActionKeys.VIEW});
		}

		if (systemUserPasswordSAPEntry == null) {
			Map<Locale, String> titleMap = HashMapBuilder.put(
				LocaleUtil.getDefault(),
				_sapConfiguration.systemUserPasswordSAPEntryDescription()
			).build();

			systemUserPasswordSAPEntry = addSAPEntry(
				guestUserId,
				_sapConfiguration.systemUserPasswordSAPEntryServiceSignatures(),
				false, true, _sapConfiguration.systemUserPasswordSAPEntryName(),
				titleMap, new ServiceContext());

			_resourcePermissionLocalService.setResourcePermissions(
				systemUserPasswordSAPEntry.getCompanyId(),
				SAPEntry.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(systemUserPasswordSAPEntry.getSapEntryId()),
				guestRole.getRoleId(), new String[] {ActionKeys.VIEW});
		}
	}

	@Override
	public SAPEntry deleteSAPEntry(long sapEntryId) throws PortalException {
		SAPEntry sapEntry = sapEntryPersistence.findByPrimaryKey(sapEntryId);

		return deleteSAPEntry(sapEntry);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SAPEntry deleteSAPEntry(SAPEntry sapEntry) throws PortalException {
		if (sapEntry.isSystem() &&
			!PortalInstances.isCurrentCompanyInDeletionProcess()) {

			throw new RequiredSAPEntryException();
		}

		sapEntry = super.deleteSAPEntry(sapEntry);

		_resourceLocalService.deleteResource(
			sapEntry.getCompanyId(), SAPEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, sapEntry.getSapEntryId());

		return sapEntry;
	}

	@Override
	public SAPEntry fetchSAPEntry(long companyId, String name) {
		return sapEntryPersistence.fetchByC_N(companyId, name);
	}

	@Override
	public List<SAPEntry> getCompanySAPEntries(
		long companyId, int start, int end) {

		return sapEntryPersistence.findByCompanyId(companyId, start, end);
	}

	@Override
	public List<SAPEntry> getCompanySAPEntries(
		long companyId, int start, int end,
		OrderByComparator<SAPEntry> orderByComparator) {

		return sapEntryPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCompanySAPEntriesCount(long companyId) {
		return sapEntryPersistence.countByCompanyId(companyId);
	}

	@Override
	public List<SAPEntry> getDefaultSAPEntries(
		long companyId, boolean defaultSAPEntry) {

		return sapEntryPersistence.findByC_D(companyId, defaultSAPEntry);
	}

	@Override
	public SAPEntry getSAPEntry(long companyId, String name)
		throws PortalException {

		return sapEntryPersistence.findByC_N(companyId, name);
	}

	@Override
	public SAPEntry updateSAPEntry(
			long sapEntryId, String allowedServiceSignatures,
			boolean defaultSAPEntry, boolean enabled, String name,
			Map<Locale, String> titleMap, ServiceContext serviceContext)
		throws PortalException {

		SAPEntry sapEntry = sapEntryPersistence.findByPrimaryKey(sapEntryId);

		SAPEntry existingSAPEntry = sapEntryPersistence.fetchByC_N(
			sapEntry.getCompanyId(), name);

		if ((existingSAPEntry != null) &&
			(existingSAPEntry.getSapEntryId() != sapEntryId)) {

			throw new DuplicateSAPEntryNameException();
		}

		allowedServiceSignatures = _normalizeServiceSignatures(
			allowedServiceSignatures);

		if (sapEntry.isSystem()) {
			defaultSAPEntry = sapEntry.isDefaultSAPEntry();
			name = sapEntry.getName();
		}

		name = StringUtil.trim(name);

		_validate(name, titleMap);

		sapEntry.setAllowedServiceSignatures(allowedServiceSignatures);
		sapEntry.setDefaultSAPEntry(defaultSAPEntry);
		sapEntry.setEnabled(enabled);
		sapEntry.setName(name);
		sapEntry.setTitleMap(titleMap);

		return sapEntryPersistence.update(sapEntry, serviceContext);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_sapConfiguration = ConfigurableUtil.createConfigurable(
			SAPConfiguration.class, properties);
	}

	private String _normalizeServiceSignatures(String serviceSignatures) {
		String[] serviceSignaturesArray = serviceSignatures.split(
			StringPool.NEW_LINE);

		Set<String> sortedServiceSignatures = new TreeSet<>();

		for (String serviceSignature : serviceSignaturesArray) {
			String[] serviceSignatureArray = serviceSignature.split(
				StringPool.POUND);

			StringBundler sb = new StringBundler(
				serviceSignatureArray.length * 2);

			boolean empty = true;

			for (int i = 0; i < serviceSignatureArray.length; i++) {
				serviceSignatureArray[i] = StringUtil.trim(
					serviceSignatureArray[i]);

				if (serviceSignatureArray[i].length() > 0) {
					empty = false;
				}

				sb.append(serviceSignatureArray[i]);
				sb.append(StringPool.POUND);
			}

			if (empty) {
				continue;
			}

			sb.setIndex(sb.index() - 1);

			sortedServiceSignatures.add(sb.toString());
		}

		StringBundler sb = new StringBundler(
			sortedServiceSignatures.size() * 2);

		for (String sortedServiceSignature : sortedServiceSignatures) {
			sb.append(sortedServiceSignature);
			sb.append(StringPool.NEW_LINE);
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private void _validate(String name, Map<Locale, String> titleMap)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new SAPEntryNameException();
		}

		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);

			if (SAPEntryConstants.NAME_ALLOWED_CHARACTERS.indexOf(c) < 0) {
				throw new SAPEntryNameException("Invalid character " + c);
			}
		}

		boolean titleExists = false;

		if (titleMap != null) {
			Locale defaultLocale = LocaleUtil.getDefault();

			String defaultTitle = titleMap.get(defaultLocale);

			if (Validator.isNotNull(defaultTitle)) {
				titleExists = true;
			}
		}

		if (!titleExists) {
			throw new SAPEntryTitleException();
		}
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	private volatile SAPConfiguration _sapConfiguration;

	@Reference
	private UserLocalService _userLocalService;

}