/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.impl;

import com.liferay.layout.content.service.base.LayoutContentVersionPreviewLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "model.class.name=com.liferay.layout.content.model.LayoutContentVersionPreview",
	service = AopService.class
)
public class LayoutContentVersionPreviewLocalServiceImpl
	extends LayoutContentVersionPreviewLocalServiceBaseImpl {
}