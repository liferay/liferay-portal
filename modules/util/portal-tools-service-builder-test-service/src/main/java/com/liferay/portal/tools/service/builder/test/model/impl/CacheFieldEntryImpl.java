/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.portal.kernel.model.cache.CacheField;

/**
 * @author Brian Wing Shun Chan
 */
public class CacheFieldEntryImpl extends CacheFieldEntryBaseImpl {

	@Override
	public String getNickname() {
		if (_nickname == null) {
			_nickname = "Nickname_" + getName();
		}

		nicknameUpdateEntityCacheConsumer.accept(_nickname);

		return _nickname;
	}

	@Override
	public void setNickname(String nickname) {
		_nickname = nickname;
	}

	@CacheField(propagateToInterface = true)
	private String _nickname;

}