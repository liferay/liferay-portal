/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.spi.model.index.contributor;

import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = "indexer.class.name=com.liferay.change.tracking.model.CTCollection",
	service = ModelDocumentContributor.class
)
public class CTCollectionModelDocumentContributor
	implements ModelDocumentContributor<CTCollection> {

	@Override
	public void contribute(Document document, CTCollection ctCollection) {
		document.addKeyword(Field.COMPANY_ID, ctCollection.getCompanyId());
		document.addDate(Field.CREATE_DATE, ctCollection.getCreateDate());
		document.addText(Field.DESCRIPTION, ctCollection.getDescription());
		document.addDate(Field.MODIFIED_DATE, ctCollection.getModifiedDate());
		document.addText(Field.NAME, ctCollection.getName());
		document.addKeyword(Field.STATUS, ctCollection.getStatus());

		User user = _userLocalService.fetchUser(ctCollection.getUserId());

		if (user != null) {
			document.addKeyword(Field.USER_ID, user.getUserId());
			document.addText(Field.USER_NAME, user.getFullName());
		}

		document.addDate("scheduledDate", _getScheduledDate(ctCollection));
	}

	private Date _getScheduledDate(CTCollection ctCollection) {
		if (ctCollection.getStatus() != WorkflowConstants.STATUS_SCHEDULED) {
			return null;
		}

		try {
			SchedulerResponse schedulerResponse =
				_schedulerEngineHelper.getScheduledJob(
					StringBundler.concat(
						ctCollection.getCtCollectionId(), StringPool.AT,
						ctCollection.getCompanyId()),
					CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
					StorageType.PERSISTED);

			if (schedulerResponse == null) {
				return null;
			}

			return _schedulerEngineHelper.getStartDate(schedulerResponse);
		}
		catch (SchedulerException schedulerException) {
			if (_log.isWarnEnabled()) {
				_log.warn(schedulerException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTCollectionModelDocumentContributor.class);

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private UserLocalService _userLocalService;

}