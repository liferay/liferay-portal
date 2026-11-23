/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.zip.internal.reader.factory;

import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.zip.internal.reader.ZipReaderImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Raymond Augé
 */
@Component(service = ZipReaderFactory.class)
public class ZipReaderFactoryImpl implements ZipReaderFactory {

	@Override
	public ZipReader getZipReader(File file) {
		return new ZipReaderImpl(file);
	}

	@Override
	public ZipReader getZipReader(InputStream inputStream) throws IOException {
		return new ZipReaderImpl(inputStream);
	}

}