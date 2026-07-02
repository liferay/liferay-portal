/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.test.util;

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Alejandro Tardín
 */
public class TranslationTestUtil {

	public static JournalArticle getJournalArticle(
			Group group, DDMFormDeserializer ddmFormDeserializer)
		throws Exception {

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				readFileToString("test-ddm-form.json"));

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				ddmFormDeserializer.deserialize(builder.build());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), JournalArticle.class.getName(),
			ddmFormDeserializerDeserializeResponse.getDDMForm());

		return JournalTestUtil.addArticleWithXMLContent(
			group.getGroupId(), readFileToString("test-journal-content.xml"),
			ddmStructure.getStructureKey(), null);
	}

	public static InputStream readFileToInputStream(String fileName)
		throws Exception {

		return new ByteArrayInputStream(_getBytes(fileName));
	}

	public static String readFileToString(String fileName) throws Exception {
		return new String(_getBytes(fileName));
	}

	public static String toFormattedString(String xml) throws Exception {
		Document document = SAXReaderUtil.read(xml);

		return document.formattedString();
	}

	public static void withRegularUser(
			UnsafeBiConsumer<User, Role, Exception> unsafeBiConsumer)
		throws Exception {

		_withUser(unsafeBiConsumer, RoleConstants.TYPE_REGULAR);
	}

	private static byte[] _getBytes(String fileName) throws Exception {
		return FileUtil.getBytes(
			TranslationTestUtil.class,
			"/com/liferay/translation/dependencies/" + fileName);
	}

	private static void _withUser(
			UnsafeBiConsumer<User, Role, Exception> unsafeBiConsumer,
			int roleType)
		throws Exception {

		Role role = RoleTestUtil.addRole(roleType);
		User user = UserTestUtil.addUser();

		UserLocalServiceUtil.addRoleUser(role.getRoleId(), user);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			unsafeBiConsumer.accept(user, role);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			RoleLocalServiceUtil.deleteRole(role);
			UserLocalServiceUtil.deleteUser(user);
		}
	}

}