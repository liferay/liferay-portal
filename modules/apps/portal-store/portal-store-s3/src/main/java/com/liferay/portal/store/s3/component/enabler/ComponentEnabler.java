/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3.component.enabler;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.osgi.util.ComponentUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.store.s3.IBMS3Store;
import com.liferay.portal.store.s3.S3Store;
import com.liferay.portal.store.s3.scheduler.AbortedMultipartUploadCleanerSchedulerJobConfiguration;

import java.util.Objects;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Hai Yu
 */
@Component(service = {})
public class ComponentEnabler {

	@Activate
	protected void activate(ComponentContext componentContext) {
		if (Objects.equals(
				PropsValues.DL_STORE_IMPL, IBMS3Store.class.getName())) {

			componentContext.enableComponent(IBMS3Store.class.getName());
		}
		else if (Objects.equals(
					PropsValues.DL_STORE_IMPL, S3Store.class.getName())) {

			componentContext.enableComponent(S3Store.class.getName());

			ComponentUtil.enableComponents(
				Store.class, "(store.type=" + PropsValues.DL_STORE_IMPL + ")",
				componentContext,
				AbortedMultipartUploadCleanerSchedulerJobConfiguration.class);
		}
	}

}