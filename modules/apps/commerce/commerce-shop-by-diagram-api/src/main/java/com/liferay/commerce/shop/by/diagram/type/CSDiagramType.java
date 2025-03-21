/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.type;

import aQute.bnd.annotation.ProviderType;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
@ProviderType
public interface CSDiagramType {

	public String getKey();

	public String getLabel(Locale locale);

	public void render(
			CSDiagramSetting csDiagramSetting,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

}