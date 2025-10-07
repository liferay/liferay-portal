/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.indexing;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentHelper;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;

import java.text.SimpleDateFormat;

import org.mockito.Mockito;

/**
 * @author Miguel Angelo Caldas Gallindo
 */
public class DocumentFixture {

	public static Document newDocument(
		long companyId, long groupId, String entryClassName) {

		DocumentImpl documentImpl = new DocumentImpl();

		documentImpl.addKeyword(Field.COMPANY_ID, companyId);
		documentImpl.addKeyword(Field.GROUP_ID, groupId);

		long entryClassPK = RandomTestUtil.randomLong();

		documentImpl.addUID(entryClassName, entryClassPK);

		DocumentHelper documentHelper = new DocumentHelper(documentImpl);

		documentHelper.setEntryKey(entryClassName, entryClassPK);

		return documentImpl;
	}

	public void setUp() {
		setUpFastDateFormatFactoryUtil();
	}

	public void tearDown() {
		tearDownFastDateFormatFactoryUtil();
	}

	protected void setUpFastDateFormatFactoryUtil() {
		_fastDateFormatFactory =
			FastDateFormatFactoryUtil.getFastDateFormatFactory();

		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		FastDateFormatFactory fastDateFormatFactory = Mockito.mock(
			FastDateFormatFactory.class);

		Mockito.when(
			fastDateFormatFactory.getSimpleDateFormat("yyyyMMddHHmmss")
		).thenReturn(
			new SimpleDateFormat("yyyyMMddHHmmss")
		);

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			fastDateFormatFactory);
	}

	protected void tearDownFastDateFormatFactoryUtil() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			_fastDateFormatFactory);

		_fastDateFormatFactory = null;
	}

	private FastDateFormatFactory _fastDateFormatFactory;

}