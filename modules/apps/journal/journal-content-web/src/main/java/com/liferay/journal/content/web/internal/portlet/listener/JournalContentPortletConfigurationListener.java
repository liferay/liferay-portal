/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.portlet.listener;

import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletConfigurationListener;
import com.liferay.portal.kernel.portlet.PortletConfigurationListenerException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "jakarta.portlet.name=" + JournalContentPortletKeys.JOURNAL_CONTENT,
	service = PortletConfigurationListener.class
)
public class JournalContentPortletConfigurationListener
	implements PortletConfigurationListener {

	@Override
	public void onUpdateScope(
			String portletId, PortletPreferences portletPreferences)
		throws PortletConfigurationListenerException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Portlet ", portletId, " with portlet preferences ",
					MapUtil.toString(portletPreferences.getMap())));
		}

		try {
			portletPreferences.reset("portletSetupUseCustomTitle");

			if (_resetValues(portletPreferences)) {
				portletPreferences.reset("articleExternalReferenceCode");
				portletPreferences.reset("ddmTemplateExternalReferenceCode");
				portletPreferences.reset("groupExternalReferenceCode");
			}

			portletPreferences.store();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortletConfigurationListenerException(exception);
		}
	}

	private boolean _resetValues(PortletPreferences portletPreferences) {
		String groupExternalReferenceCode = portletPreferences.getValue(
			"groupExternalReferenceCode", null);

		if (Validator.isNull(groupExternalReferenceCode)) {
			return false;
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			groupExternalReferenceCode, CompanyThreadLocal.getCompanyId());

		if (group == null) {
			return false;
		}

		String lfrScopeType = portletPreferences.getValue(
			"lfrScopeType", StringPool.BLANK);

		if (group.isCompany() && Objects.equals(lfrScopeType, "company")) {
			return false;
		}

		if (group.isLayout() && Objects.equals(lfrScopeType, "layout")) {
			return false;
		}

		if (!group.isCompany() && !group.isLayout() &&
			Validator.isNull(lfrScopeType)) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentPortletConfigurationListener.class);

	@Reference
	private GroupLocalService _groupLocalService;

}