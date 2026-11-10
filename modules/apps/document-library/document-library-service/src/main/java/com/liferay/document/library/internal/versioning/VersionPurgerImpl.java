/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.versioning;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.versioning.VersionPurger;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = VersionPurger.class
)
public class VersionPurgerImpl implements VersionPurger {

	@Override
	public Collection<FileVersion> getToPurgeFileVersions(FileEntry fileEntry) {
		int maximumNumberOfVersions =
			_dlConfiguration.maximumNumberOfVersions();

		if (maximumNumberOfVersions <= 0) {
			return Collections.emptyList();
		}

		int status = WorkflowConstants.STATUS_ANY;

		int numberOfVersions = fileEntry.getFileVersionsCount(status);

		if (numberOfVersions > maximumNumberOfVersions) {
			List<FileVersion> fileVersions = fileEntry.getFileVersions(status);

			int numberOfVersionsToPurge =
				numberOfVersions - maximumNumberOfVersions;

			return fileVersions.subList(
				fileVersions.size() - numberOfVersionsToPurge,
				fileVersions.size());
		}

		return Collections.emptyList();
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	private DLConfiguration _dlConfiguration;

}