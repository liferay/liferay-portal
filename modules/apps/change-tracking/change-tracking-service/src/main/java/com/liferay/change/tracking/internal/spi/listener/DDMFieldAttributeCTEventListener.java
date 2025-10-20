/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.spi.listener;

import com.liferay.change.tracking.internal.CTServiceRegistry;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.listener.CTEventListener;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = CTEventListener.class)
public class DDMFieldAttributeCTEventListener implements CTEventListener {

	@Override
	public void onAfterPublish(long ctCollectionId) {
		List<CTEntry> ctEntries = _ctEntryLocalService.getCTEntries(
			ctCollectionId,
			_portal.getClassNameId(DDMFieldAttribute.class.getName()));

		if (ctEntries.isEmpty()) {
			return;
		}

		CTService<DDMFieldAttribute> ctService =
			(CTService<DDMFieldAttribute>)_ctServiceRegistry.getCTService(
				_portal.getClassNameId(DDMFieldAttribute.class.getName()));

		ctService.updateWithUnsafeFunction(
			ddmFieldAttributeCTPersistence -> {
				for (CTEntry ctEntry : ctEntries) {
					DDMFieldAttribute ddmFieldAttribute =
						ddmFieldAttributeCTPersistence.fetchByPrimaryKey(
							ctEntry.getModelClassPK());

					if (ddmFieldAttribute == null) {
						continue;
					}

					String value = ddmFieldAttribute.getAttributeValue();

					String parameter =
						"previewCTCollectionId=" + ctCollectionId;

					while (value.contains(parameter)) {
						int index = value.indexOf(parameter);

						value = StringUtil.removeSubstring(
							value,
							value.substring(
								index - 1, index + parameter.length()));
					}

					ddmFieldAttribute.setAttributeValue(value);

					ddmFieldAttributeCTPersistence.update(ddmFieldAttribute);
				}

				return null;
			});
	}

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTServiceRegistry _ctServiceRegistry;

	@Reference
	private Portal _portal;

}