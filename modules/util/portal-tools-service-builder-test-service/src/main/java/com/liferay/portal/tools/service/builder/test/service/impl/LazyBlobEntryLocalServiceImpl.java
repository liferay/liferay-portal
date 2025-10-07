/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.impl;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry;
import com.liferay.portal.tools.service.builder.test.service.base.LazyBlobEntryLocalServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 */
public class LazyBlobEntryLocalServiceImpl
	extends LazyBlobEntryLocalServiceBaseImpl {

	@Override
	public LazyBlobEntry addLazyBlobEntry(
		long groupId, byte[] bytes, ServiceContext serviceContext) {

		long lazyBlobEntryId = counterLocalService.increment();

		LazyBlobEntry lazyBlobEntry = lazyBlobEntryPersistence.create(
			lazyBlobEntryId);

		lazyBlobEntry.setUuid(serviceContext.getUuid());
		lazyBlobEntry.setGroupId(groupId);
		lazyBlobEntry.setBlob1(
			new OutputBlob(
				new UnsyncByteArrayInputStream(bytes), bytes.length));
		lazyBlobEntry.setBlob2(
			new OutputBlob(
				new UnsyncByteArrayInputStream(bytes), bytes.length));

		return lazyBlobEntryPersistence.update(lazyBlobEntry);
	}

}