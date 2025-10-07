/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.test.util;

import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

/**
 * @author Alberto Sousa
 */
public abstract class BaseSystemObjectDefinitionManagerTestCase {

	public void setUp() throws Exception {
		systemObjectDefinitionManager =
			systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(
					getSystemObjectDefinitionName());

		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	public void testGetOrAddEmptyBaseModel() throws Exception {

		// Lazy referencing disable

		User user1 = TestPropsValues.getUser();

		setUser(user1);

		String externalReferenceCode = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"No ", getSystemObjectDefinitionName(),
				" exists with the key {externalReferenceCode=",
				externalReferenceCode, ", companyId=",
				TestPropsValues.getCompanyId(), "}"),
			() -> systemObjectDefinitionManager.getOrAddEmptyBaseModel(
				externalReferenceCode, user1));

		// Lazy referecing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			BaseModel<?> baseModel =
				systemObjectDefinitionManager.getOrAddEmptyBaseModel(
					RandomTestUtil.randomString(), user1);

			assertGetOrAddEmptyBaseModelWithPermissions(baseModel);

			// Without permissions

			User user2 = UserTestUtil.addUser();

			setUser(user2);

			assertGetOrAddEmptyBaseModelWithoutPermissions(baseModel, user2);
		}
	}

	protected abstract void assertGetOrAddEmptyBaseModelWithoutPermissions(
			BaseModel<?> baseModel, User user)
		throws PortalException;

	protected abstract void assertGetOrAddEmptyBaseModelWithPermissions(
		BaseModel<?> baseModel);

	protected abstract String getSystemObjectDefinitionName();

	protected void setUser(User user) throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	protected SystemObjectDefinitionManager systemObjectDefinitionManager;

	@Inject
	protected SystemObjectDefinitionManagerRegistry
		systemObjectDefinitionManagerRegistry;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

}