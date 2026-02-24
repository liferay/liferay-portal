/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.list;

import com.liferay.frontend.data.set.constants.FDSConstants;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.petra.string.StringPool;

import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public abstract class BaseListFDSView implements FDSView {

	@Override
	public String getContentRenderer() {
		return FDSConstants.LIST;
	}

	public abstract String getDescription();

	public FDSListSchema getFDSListSchema(Locale locale) {
		return null;
	}

	public String getImage() {
		return StringPool.BLANK;
	}

	@Override
	public String getLabel() {
		return FDSConstants.LIST;
	}

	@Override
	public String getName() {
		return FDSConstants.LIST;
	}

	public String getSticker() {
		return StringPool.BLANK;
	}

	public String getSymbol() {
		return StringPool.BLANK;
	}

	@Override
	public String getThumbnail() {
		return FDSConstants.LIST;
	}

	public abstract String getTitle();

}