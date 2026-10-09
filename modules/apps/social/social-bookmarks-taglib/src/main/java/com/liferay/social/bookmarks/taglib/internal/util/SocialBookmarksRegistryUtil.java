/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.bookmarks.taglib.internal.util;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.social.bookmarks.SocialBookmark;
import com.liferay.social.bookmarks.SocialBookmarksRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alejandro Tardín
 */
public class SocialBookmarksRegistryUtil {

	public static SocialBookmark getSocialBookmark(String type) {
		SocialBookmarksRegistry socialBookmarksRegistry =
			_socialBookmarksRegistrySnapshot.get();

		return socialBookmarksRegistry.getSocialBookmark(type);
	}

	public static List<String> getSocialBookmarksTypes() {
		SocialBookmarksRegistry socialBookmarksRegistry =
			_socialBookmarksRegistrySnapshot.get();

		return socialBookmarksRegistry.getSocialBookmarksTypes();
	}

	public static String[] getValidTypes(String[] types) {
		List<String> supportedTypes = getSocialBookmarksTypes();
		List<String> validTypes = new ArrayList<>();

		for (String type : types) {
			if (!supportedTypes.contains(type)) {
				continue;
			}

			validTypes.add(type);
		}

		return validTypes.toArray(new String[0]);
	}

	private static final Snapshot<SocialBookmarksRegistry>
		_socialBookmarksRegistrySnapshot = new Snapshot<>(
			SocialBookmarksRegistryUtil.class, SocialBookmarksRegistry.class);

}