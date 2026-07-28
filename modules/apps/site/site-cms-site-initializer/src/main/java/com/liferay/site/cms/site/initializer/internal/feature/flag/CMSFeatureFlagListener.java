/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.feature.flag;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.site.cms.site.initializer.util.SiteInitializerUtil;
import com.liferay.site.initializer.SiteInitializer;

import java.lang.reflect.Field;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "feature.flag.key=LPD-17564", service = FeatureFlagListener.class
)
public class CMSFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (!Objects.equals(featureFlagKey, "LPD-17564")) {
			return;
		}

		Group group = _groupLocalService.fetchGroup(
			companyId, GroupConstants.CMS);

		if (enabled) {
			if (group != null) {
				return;
			}

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				_groupLocalService.checkSystemGroups(companyId);

				SiteInitializerUtil.initialize(companyId, _siteInitializer);
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}

			return;
		}

		// Undo what enabling provisioned so the feature flag change leaves no
		// state behind

		if (group == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			_deleteCMSSystemGroup(group);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _deleteCMSSystemGroup(Group group) throws Exception {

		// The CMS group is a system group, so GroupLocalServiceImpl#deleteGroup
		// refuses to delete it. LPD-17564 is a temporary feature flag, so
		// temporarily lift CMS out of the sorted system group array to allow
		// the delete, then restore it.

		Portal portal = PortalUtil.getPortal();

		Field sortedSystemGroupsField = ReflectionUtil.getDeclaredField(
			portal.getClass(), "_sortedSystemGroups");

		String[] sortedSystemGroups = (String[])sortedSystemGroupsField.get(
			portal);

		sortedSystemGroupsField.set(
			portal, ArrayUtil.remove(sortedSystemGroups, GroupConstants.CMS));

		try {
			_groupLocalService.deleteGroup(group);
		}
		finally {
			sortedSystemGroupsField.set(portal, sortedSystemGroups);
		}

		// GroupLocalServiceImpl#_systemGroupsMap caches system groups and is
		// never invalidated on delete, so fetchGroup would keep returning the
		// deleted CMS group as a phantom for the JVM's life

		_evictCachedSystemGroup(group);
	}

	private void _evictCachedSystemGroup(Group group) throws Exception {
		AopInvocationHandler aopInvocationHandler =
			ProxyUtil.fetchInvocationHandler(
				_groupLocalService, AopInvocationHandler.class);

		Object target = aopInvocationHandler.getTarget();

		while (target instanceof ServiceWrapper<?> serviceWrapper) {
			target = serviceWrapper.getWrappedService();
		}

		Field field = ReflectionUtil.getDeclaredField(
			target.getClass(), "_systemGroupsMap");

		Map<String, Group> systemGroupsMap = (Map<String, Group>)field.get(
			target);

		systemGroupsMap.remove(
			StringUtil.toHexString(group.getCompanyId()) + group.getGroupKey());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSFeatureFlagListener.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.site.initializer.cms)"
	)
	private SiteInitializer _siteInitializer;

}