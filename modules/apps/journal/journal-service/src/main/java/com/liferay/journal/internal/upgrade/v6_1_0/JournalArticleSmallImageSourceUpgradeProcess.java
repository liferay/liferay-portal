/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_0;

import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Eudaldo Alonso
 */
public class JournalArticleSmallImageSourceUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			"update JournalArticle set smallImageSource = " +
				JournalArticleConstants.SMALL_IMAGE_SOURCE_NONE +
					" where smallImage = [$FALSE$]");
		runSQL(
			"update JournalArticle set smallImageSource = " +
				JournalArticleConstants.SMALL_IMAGE_SOURCE_USER_COMPUTER +
					" where smallImage = [$TRUE$] and smallImageId > 0");

		// See LPD-25796 and LPD-107081. Oracle stores empty strings as null,
		// so empty small image URLs must be reset after the URL update.

		runSQL(
			StringBundler.concat(
				"update JournalArticle set smallImageSource = ",
				JournalArticleConstants.SMALL_IMAGE_SOURCE_URL,
				" where smallImage = [$TRUE$] and smallImageURL is not null"));
		runSQL(
			StringBundler.concat(
				"update JournalArticle set smallImageSource = ",
				JournalArticleConstants.SMALL_IMAGE_SOURCE_USER_COMPUTER,
				" where smallImage = [$TRUE$] and (smallImageURL is null or ",
				"smallImageURL = '')"));
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"JournalArticle", "smallImageSource INTEGER")
		};
	}

}