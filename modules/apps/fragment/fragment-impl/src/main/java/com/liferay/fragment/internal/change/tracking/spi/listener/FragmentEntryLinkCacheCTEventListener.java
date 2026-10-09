/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.change.tracking.spi.listener;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.exception.CTEventException;
import com.liferay.change.tracking.spi.listener.CTEventListener;
import com.liferay.fragment.cache.FragmentEntryLinkCache;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = CTEventListener.class)
public class FragmentEntryLinkCacheCTEventListener implements CTEventListener {

	@Override
	public void onAfterPublish(long ctCollectionId) throws CTEventException {
		for (CTEntry ctEntry :
				_ctEntryLocalService.getCTEntries(
					ctCollectionId,
					_portal.getClassNameId(
						FragmentEntryLink.class.getName()))) {

			if (ctEntry.getChangeType() !=
					CTConstants.CT_CHANGE_TYPE_MODIFICATION) {

				continue;
			}

			_fragmentEntryLinkCache.removeFragmentEntryLinkCache(
				ctEntry.getModelClassPK());
		}
	}

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private FragmentEntryLinkCache _fragmentEntryLinkCache;

	@Reference
	private Portal _portal;

}