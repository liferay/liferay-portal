/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.journal.web.internal.counter;

import com.liferay.adaptive.media.image.counter.AMImageCounter;
import com.liferay.adaptive.media.image.counter.BaseAMImageCounter;
import com.liferay.journal.model.JournalArticle;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "adaptive.media.key=journal-article",
	service = AMImageCounter.class
)
public class JournalArticleAMImageCounter extends BaseAMImageCounter {

	@Override
	protected String getClassName() {
		return JournalArticle.class.getName();
	}

}