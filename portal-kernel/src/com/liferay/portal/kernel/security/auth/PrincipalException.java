/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class PrincipalException extends PortalException {

	public static Class<?>[] getNestedClasses() {
		return _NESTED_CLASSES;
	}

	public PrincipalException() {
	}

	public PrincipalException(String msg) {
		super(msg);
	}

	public PrincipalException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public PrincipalException(Throwable throwable) {
		super(throwable);
	}

	public static class MustBeAuthenticated extends PrincipalException {

		public MustBeAuthenticated(long userId) {
			this(String.valueOf(userId), null);
		}

		public MustBeAuthenticated(long userId, Throwable throwable) {
			this(String.valueOf(userId), throwable);
		}

		public MustBeAuthenticated(String login) {
			this(login, null);
		}

		public MustBeAuthenticated(String login, Throwable throwable) {
			super(
				String.format("User %s must be authenticated", login),
				throwable);

			this.login = login;
		}

		public final String login;

	}

	public static class MustBeCompanyAdmin extends PrincipalException {

		public MustBeCompanyAdmin(long userId) {
			super(
				String.format(
					"User %s must be the company administrator to perform " +
						"the action",
					userId));

			this.userId = userId;
		}

		public MustBeCompanyAdmin(PermissionChecker permissionChecker) {
			this(permissionChecker.getUserId());
		}

		public final long userId;

	}

	public static class MustBeEnabled extends PrincipalException {

		public MustBeEnabled(long companyId, String... resourceName) {
			super(
				String.format(
					"%s must be enabled for company %s",
					StringUtil.merge(resourceName, ","), companyId));

			this.companyId = companyId;
			this.resourceName = resourceName;
		}

		public final long companyId;
		public final String[] resourceName;

	}

	public static class MustBeGroupAdmin extends PrincipalException {

		public MustBeGroupAdmin(long userId) {
			super(
				String.format(
					"User %s must be the site administrator to perform the " +
						"action",
					userId));

			this.userId = userId;
		}

		public MustBeGroupAdmin(PermissionChecker permissionChecker) {
			this(permissionChecker.getUserId());
		}

		public final long userId;

	}

	public static class MustBeInvokedUsingPost extends PrincipalException {

		public MustBeInvokedUsingPost(String url) {
			super(String.format("URL %s must be invoked using POST", url));

			this.url = url;
		}

		public final String url;

	}

	public static class MustBeOmniadmin extends PrincipalException {

		public MustBeOmniadmin(long userId) {
			super(
				String.format(
					"User %s must be a universal administrator to perform " +
						"the action",
					userId));

			this.userId = userId;
		}

		public MustBeOmniadmin(PermissionChecker permissionChecker) {
			this(permissionChecker.getUserId());
		}

		public final long userId;

	}

	public static class MustBePortletStrutsPath extends PrincipalException {

		public MustBePortletStrutsPath(String strutsPath, String portletId) {
			super(
				String.format(
					"Struts path %s must be struts path of portlet %s",
					strutsPath, portletId));

			this.strutsPath = strutsPath;
			this.portletId = portletId;
		}

		public final String portletId;
		public final String strutsPath;

	}

	public static class MustHavePermission extends PrincipalException {

		public MustHavePermission(long userId, String... actionIds) {
			this(userId, null, 0, null, actionIds);
		}

		public MustHavePermission(
			long userId, String resourceName, long resourceId,
			String... actionIds) {

			this(userId, resourceName, resourceId, null, actionIds);
		}

		public MustHavePermission(
			long userId, String resourceName, long resourceId,
			Throwable throwable, String... actionIds) {

			super(
				String.format(
					"User %s must have %s permission for %s %s", userId,
					StringUtil.merge(ArrayUtil.sortedUnique(actionIds), ", "),
					resourceName, (resourceId == 0) ? "" : resourceId),
				throwable);

			this.userId = userId;
			this.resourceName = resourceName;
			this.resourceId = resourceId;

			actionId = actionIds;
		}

		public MustHavePermission(
			long userId, Throwable throwable, String... actionIds) {

			this(userId, null, 0, throwable, actionIds);
		}

		public MustHavePermission(
			PermissionChecker permissionChecker, String... actionIds) {

			this(permissionChecker.getUserId(), null, 0, null, actionIds);
		}

		public MustHavePermission(
			PermissionChecker permissionChecker, String resourceName,
			long resourceId, String... actionIds) {

			this(
				permissionChecker.getUserId(), resourceName, resourceId, null,
				actionIds);
		}

		public MustHavePermission(
			PermissionChecker permissionChecker, String resourceName,
			long resourceId, Throwable throwable, String... actionIds) {

			this(
				permissionChecker.getUserId(), resourceName, resourceId,
				throwable, actionIds);
		}

		public MustHavePermission(
			PermissionChecker permissionChecker, Throwable throwable,
			String... actionIds) {

			this(permissionChecker.getUserId(), null, 0, throwable, actionIds);
		}

		public final String[] actionId;
		public final long resourceId;
		public final String resourceName;
		public final long userId;

	}

	public static class MustHaveSessionCSRFToken extends PrincipalException {

		public MustHaveSessionCSRFToken(long userId, String origin) {
			this(userId, origin, null);
		}

		public MustHaveSessionCSRFToken(
			long userId, String origin, Throwable throwable) {

			super(
				String.format(
					"User %s session does not have a CSRF token for %s", userId,
					origin),
				throwable);

			this.userId = userId;
			this.origin = origin;
		}

		public final String origin;
		public final long userId;

	}

	public static class MustHaveValidCSRFToken extends PrincipalException {

		public MustHaveValidCSRFToken(long userId, String origin) {
			this(userId, origin, null);
		}

		public MustHaveValidCSRFToken(
			long userId, String origin, Throwable throwable) {

			super(
				String.format(
					"User %s did not provide a valid CSRF token for %s", userId,
					origin),
				throwable);

			this.userId = userId;
			this.origin = origin;
		}

		public final String origin;
		public final long userId;

	}

	private static final Class<?>[] _NESTED_CLASSES = {
		PrincipalException.class, PrincipalException.MustBeAuthenticated.class,
		PrincipalException.MustBeCompanyAdmin.class,
		PrincipalException.MustBeEnabled.class,
		PrincipalException.MustBeInvokedUsingPost.class,
		PrincipalException.MustBeOmniadmin.class,
		PrincipalException.MustBePortletStrutsPath.class,
		PrincipalException.MustHavePermission.class,
		PrincipalException.MustHaveValidCSRFToken.class,
		PrincipalException.MustHaveSessionCSRFToken.class
	};

}