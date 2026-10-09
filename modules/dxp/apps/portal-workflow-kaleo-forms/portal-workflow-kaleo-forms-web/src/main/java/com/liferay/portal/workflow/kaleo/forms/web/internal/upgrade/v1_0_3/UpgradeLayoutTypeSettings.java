/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.upgrade.v1_0_3;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Inácio Nery
 */
public class UpgradeLayoutTypeSettings extends BasePortletIdUpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_deleteLayoutTypeSettingsColumnKeyWithoutValue();
	}

	private void _deleteLayoutTypeSettingsColumnKeyWithoutValue()
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			LayoutLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

				Property typeSettingsProperty = PropertyFactoryUtil.forName(
					"typeSettings");

				disjunction.add(
					typeSettingsProperty.like(
						"%" + LayoutTypePortletConstants.COLUMN_PREFIX +
							"%=,%"));
				disjunction.add(
					typeSettingsProperty.like(
						"%" + LayoutTypePortletConstants.NESTED_COLUMN_IDS +
							"%=,%"));

				dynamicQuery.add(disjunction);
			});
		actionableDynamicQuery.setParallel(true);
		actionableDynamicQuery.setPerformActionMethod(
			(Layout layout) -> {
				try {
					UnicodeProperties oldtypeSettingsUnicodeProperties =
						layout.getTypeSettingsProperties();
					UnicodeProperties newTypeSettingsUnicodeProperties =
						_getNewTypeSettingsUnicodeProperties(
							layout.getTypeSettingsProperties());

					if (!oldtypeSettingsUnicodeProperties.equals(
							newTypeSettingsUnicodeProperties)) {

						updateLayout(
							layout.getPlid(),
							newTypeSettingsUnicodeProperties.toString());
					}
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to update layout " + layout.getPlid(),
							exception);
					}
				}
			});

		actionableDynamicQuery.performActions();
	}

	private UnicodeProperties _getNewTypeSettingsUnicodeProperties(
		UnicodeProperties oldtypeSettingsUnicodeProperties) {

		UnicodeProperties newtypeSettingsUnicodeProperties =
			(UnicodeProperties)oldtypeSettingsUnicodeProperties.clone();

		for (String key : oldtypeSettingsUnicodeProperties.keySet()) {
			if (!StringUtil.startsWith(
					key, LayoutTypePortletConstants.COLUMN_PREFIX) &&
				!StringUtil.startsWith(
					key, LayoutTypePortletConstants.NESTED_COLUMN_IDS)) {

				continue;
			}

			String[] portletIds = StringUtil.split(
				oldtypeSettingsUnicodeProperties.getProperty(key));

			if (ArrayUtil.isNotEmpty(portletIds) &&
				Validator.isNotNull(portletIds[0])) {

				continue;
			}

			newtypeSettingsUnicodeProperties.remove(key);
		}

		return newtypeSettingsUnicodeProperties;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeLayoutTypeSettings.class);

}