/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.dto.v1_0.converter;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.rest.dto.v1_0.CTProcess;
import com.liferay.change.tracking.rest.dto.v1_0.Status;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "dto.class.name=com.liferay.change.tracking.model.CTProcess",
	service = DTOConverter.class
)
public class CTProcessDTOConverter
	implements DTOConverter
		<com.liferay.change.tracking.model.CTProcess, CTProcess> {

	@Override
	public String getContentType() {
		return CTProcess.class.getSimpleName();
	}

	@Override
	public CTProcess toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.change.tracking.model.CTProcess ctProcess)
		throws Exception {

		if (ctProcess == null) {
			return null;
		}

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			ctProcess.getCtCollectionId());

		return new CTProcess() {
			{
				setActions(dtoConverterContext::getActions);
				setCtCollectionId(ctCollection::getCtCollectionId);
				setDatePublished(ctProcess::getCreateDate);
				setDescription(ctCollection::getDescription);
				setId(ctProcess::getCtProcessId);
				setName(ctCollection::getName);
				setOwnerName(() -> _getUserName(ctProcess.getUserId()));
				setStatus(
					() -> _toStatus(
						dtoConverterContext.getLocale(),
						ctProcess.getBackgroundTaskId()));
			}
		};
	}

	private String _getUserName(long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return StringPool.BLANK;
		}

		return user.getFullName();
	}

	private Status _toStatus(Locale locale, long backgroundTaskId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		int status = backgroundTask.getStatus();

		String statusLabel;

		if (status == BackgroundTaskConstants.STATUS_FAILED) {
			statusLabel = "failed";
		}
		else if (status == BackgroundTaskConstants.STATUS_IN_PROGRESS) {
			statusLabel = "in-progress";
		}
		else if (status == BackgroundTaskConstants.STATUS_QUEUED) {
			statusLabel = "queued";
		}
		else if (status == BackgroundTaskConstants.STATUS_SUCCESSFUL) {
			statusLabel = "published";
		}
		else {
			statusLabel = StringPool.BLANK;
		}

		return new Status() {
			{
				setCode(() -> status);
				setLabel(() -> statusLabel);
				setLabel_i18n(() -> _language.get(locale, statusLabel));
			}
		};
	}

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private Language _language;

	@Reference
	private UserLocalService _userLocalService;

}