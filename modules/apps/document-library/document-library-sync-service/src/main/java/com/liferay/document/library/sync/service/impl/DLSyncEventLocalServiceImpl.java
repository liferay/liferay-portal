/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.sync.service.impl;

import com.liferay.document.library.sync.service.base.DLSyncEventLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dennis Ju
 */
@Component(
	property = "model.class.name=com.liferay.document.library.sync.model.DLSyncEvent",
	service = AopService.class
)
public class DLSyncEventLocalServiceImpl
	extends DLSyncEventLocalServiceBaseImpl {
}