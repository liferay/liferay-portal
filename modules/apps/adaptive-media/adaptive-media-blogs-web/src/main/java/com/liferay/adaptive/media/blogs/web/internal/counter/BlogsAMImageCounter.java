/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.blogs.web.internal.counter;

import com.liferay.adaptive.media.image.counter.AMImageCounter;
import com.liferay.adaptive.media.image.counter.BaseAMImageCounter;
import com.liferay.blogs.model.BlogsEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = "adaptive.media.key=blogs", service = AMImageCounter.class
)
public class BlogsAMImageCounter extends BaseAMImageCounter {

	@Override
	protected String getClassName() {
		return BlogsEntry.class.getName();
	}

}