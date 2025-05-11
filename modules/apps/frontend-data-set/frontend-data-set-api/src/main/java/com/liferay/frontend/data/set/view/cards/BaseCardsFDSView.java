/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.cards;

import com.liferay.frontend.data.set.constants.FDSConstants;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.petra.string.StringPool;

import java.util.Locale;

/**
 * @author Bruno Basto
 */
public abstract class BaseCardsFDSView implements FDSView {

	@Override
	public String getContentRenderer() {
		return FDSConstants.CARDS;
	}

	public abstract String getDescription();

	public abstract FDSCardSchema getFDSCardSchema(Locale locale);

	public String getImage() {
		return StringPool.BLANK;
	}

	@Override
	public String getLabel() {
		return FDSConstants.CARDS;
	}

	public String getLink() {
		return StringPool.BLANK;
	}

	@Override
	public String getName() {
		return FDSConstants.CARDS;
	}

	public String getSticker() {
		return StringPool.BLANK;
	}

	public String getSymbol() {
		return StringPool.BLANK;
	}

	@Override
	public String getThumbnail() {
		return "cards2";
	}

	public abstract String getTitle();

}