/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test.util;

import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

/**
 * @author Pedro Leite
 */
public class ObjectFieldTestUtil {

	public static FileEntry addTempFileEntry(
			String fileName, ObjectDefinition objectDefinition)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(fileName),
			FileUtil.createTempFile(DLTestUtil.randomTextFileBytes()),
			ContentTypes.TEXT_PLAIN);
	}

	public static String generateKey(String algorithm)
		throws NoSuchAlgorithmException {

		KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);

		keyGenerator.init(128);

		Key key = keyGenerator.generateKey();

		return Base64.encode(key.getEncoded());
	}

	public static void withEncryptedObjectFieldProperties(
			String algorithm, Boolean enabled, String key,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"OBJECT_ENCRYPTION_ALGORITHM", algorithm);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"OBJECT_ENCRYPTION_ENABLED", enabled);
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"OBJECT_ENCRYPTION_KEY", key)) {

			unsafeRunnable.run();
		}
	}

}