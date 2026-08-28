/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.model.listener;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTRequiredModelException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kiana Suetani
 */
@Component(service = ModelListener.class)
public class FragmentEntryLinkModelListener
	extends BaseModelListener<FragmentEntryLink> {

	@Override
	public void onBeforeUpdate(
		FragmentEntryLink originalFragmentEntryLink,
		FragmentEntryLink fragmentEntryLink) {

		if (originalFragmentEntryLink.isDeleted() ||
			!fragmentEntryLink.isDeleted()) {

			return;
		}

		long ctCollectionId = CTCollectionThreadLocal.getCTCollectionId();

		if ((ctCollectionId != CTConstants.CT_COLLECTION_ID_PRODUCTION) ||
			!GetterUtil.getBoolean(
				PropsUtil.get(
					PropsKeys.CHANGE_TRACKING_DELETION_PROTECTION_ENABLED))) {

			return;
		}

		long modelClassNameId = _classNameLocalService.getClassNameId(
			fragmentEntryLink.getModelClass());
		long fragmentEntryLinkId = fragmentEntryLink.getFragmentEntryLinkId();

		if (_ctEntryLocalService.hasUnpublishedCTEntries(
				modelClassNameId, fragmentEntryLinkId,
				CTConstants.CT_CHANGE_TYPE_MODIFICATION)) {

			throw new CTRequiredModelException(
				String.format(
					"Model %s %s cannot be deleted because it is being " +
						"modified in one or more publications",
					fragmentEntryLink.getModelClassName(),
					fragmentEntryLinkId));
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

}