/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.model.listener;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kiana Suetani
 */
@Component(service = ModelListener.class)
public class SEOStudioIntegrationObjectEntryModelListener
	extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if ((objectDefinition == null) ||
			!Objects.equals(
				objectDefinition.getExternalReferenceCode(),
				"L_SEO_STUDIO_INTEGRATION")) {

			return;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		String type = GetterUtil.getString(values.get("type"));

		if (!Objects.equals(type, "pageSpeed")) {
			return;
		}

		try {
			long seoStudioInstanceId = GetterUtil.getLong(
				values.get(
					"r_seoStudioInstanceToSEOStudioIntegrations_" +
						"seoStudioInstanceId"));

			_clearGooglePageSpeedAPIKey(seoStudioInstanceId);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private void _clearGooglePageSpeedAPIKey(long seoStudioInstanceId)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			seoStudioInstanceId);

		if (objectEntry == null) {
			return;
		}

		_objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"googlePageSpeedAPIKey", StringPool.BLANK
			).build(),
			new ServiceContext());
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}