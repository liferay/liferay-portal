/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.module.configuration.internal.model.listener;

import com.liferay.asset.kernel.util.NotifiedAssetEntryThreadLocal;
import com.liferay.layout.util.UpdateLayoutModifiedDateThreadLocal;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ModelListener.class)
public class PortletPreferencesModelListener
	extends BaseModelListener<PortletPreferences> {

	@Override
	public void onAfterUpdate(
			PortletPreferences originalPortletPreferences,
			PortletPreferences portletPreferences)
		throws ModelListenerException {

		_updateLayout(portletPreferences);
	}

	private void _updateLayout(PortletPreferences portletPreferences) {
		if (!UpdateLayoutModifiedDateThreadLocal.isUpdateLayoutModifiedDate()) {
			return;
		}

		try {
			if ((portletPreferences.getOwnerType() ==
					PortletKeys.PREFS_OWNER_TYPE_GROUP) &&
				(portletPreferences.getOwnerId() > 0)) {

				Group group = _groupLocalService.fetchGroup(
					portletPreferences.getOwnerId());

				if (group == null) {
					return;
				}

				String className = group.getClassName();

				if (!className.equals(LayoutSetPrototype.class.getName())) {
					return;
				}

				LayoutSetPrototype layoutSetPrototype =
					_layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
						group.getClassPK());

				if (layoutSetPrototype == null) {
					return;
				}

				layoutSetPrototype.setModifiedDate(new Date());

				_layoutSetPrototypeLocalService.updateLayoutSetPrototype(
					layoutSetPrototype);
			}
			else if ((portletPreferences.getOwnerType() ==
						PortletKeys.PREFS_OWNER_TYPE_LAYOUT) &&
					 (portletPreferences.getPlid() > 0)) {

				Layout layout = _layoutLocalService.fetchLayout(
					portletPreferences.getPlid());

				if ((layout == null) ||
					NotifiedAssetEntryThreadLocal.
						isNotifiedAssetEntryIdsModified()) {

					return;
				}

				if (layout.isDraftLayout()) {
					long userId = layout.getUserId();

					ServiceContext serviceContext =
						ServiceContextThreadLocal.getServiceContext();

					if ((serviceContext != null) &&
						(serviceContext.getUserId() != 0)) {

						userId = serviceContext.getUserId();
					}

					_layoutLocalService.updateStatus(
						userId, layout.getPlid(),
						WorkflowConstants.STATUS_DRAFT, serviceContext);
				}
				else {
					layout.setModifiedDate(new Date());

					_layoutLocalService.updateTypeSettings(
						layout, layout.getTypeSettings());
				}
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to update the layout's modified date", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesModelListener.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

}