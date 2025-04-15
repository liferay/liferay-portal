/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.upgrade.v1_0_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;

import jakarta.portlet.PortletPreferences;

/**
 * @author Iván Zaera
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {WikiPortletKeys.WIKI};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		_upgradeEmailSignature(
			portletPreferences, "emailPageAddedBody",
			"emailPageAddedSignature");
		_upgradeEmailSignature(
			portletPreferences, "emailPageUpdatedBody",
			"emailPageUpdatedSignature");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private String _getEmailSignatureSeparator() {
		return StringPool.NEW_LINE;
	}

	private void _upgradeEmailSignature(
			PortletPreferences portletPreferences,
			String emailMessageBodyPortletPreferencesKey,
			String emailMessageSignaturePortletPreferencesKey)
		throws Exception {

		String emailMessageSignature = portletPreferences.getValue(
			emailMessageSignaturePortletPreferencesKey, StringPool.BLANK);

		if (Validator.isNotNull(emailMessageSignature)) {
			String emailMessageBody = portletPreferences.getValue(
				emailMessageBodyPortletPreferencesKey, StringPool.BLANK);

			String signatureSeparator = _getEmailSignatureSeparator();

			emailMessageBody += signatureSeparator + emailMessageSignature;

			portletPreferences.setValue(
				emailMessageBodyPortletPreferencesKey, emailMessageBody);
		}

		portletPreferences.reset(emailMessageSignaturePortletPreferencesKey);
	}

}