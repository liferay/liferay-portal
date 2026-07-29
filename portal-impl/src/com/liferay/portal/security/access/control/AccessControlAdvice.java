/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.access.control;

import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.auth.AccessControlContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Tomas Polesovsky
 * @author Igor Spasic
 * @author Michael C. Han
 * @author Raymond Augé
 * @author Shuyang Zhou
 */
public class AccessControlAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		return annotations.get(AccessControlled.class);
	}

	@Override
	protected Object before(
		AopMethodInvocation aopMethodInvocation, Object[] arguments) {

		incrementServiceDepth();

		AccessControlled accessControlled =
			aopMethodInvocation.getAdviceMethodContext();

		_accessControlAdvisor.accept(
			aopMethodInvocation.getMethod(), arguments, accessControlled);

		return null;
	}

	protected void decrementServiceDepth() {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext == null) {
			return;
		}

		Map<String, Object> settings = accessControlContext.getSettings();

		Integer serviceDepth = (Integer)settings.get(
			AccessControlContext.Settings.SERVICE_DEPTH.toString());

		if (serviceDepth == null) {
			return;
		}

		serviceDepth--;

		settings.put(
			AccessControlContext.Settings.SERVICE_DEPTH.toString(),
			serviceDepth);
	}

	@Override
	protected void duringFinally(
		AopMethodInvocation aopMethodInvocation, Object[] arguments) {

		decrementServiceDepth();
	}

	protected void incrementServiceDepth() {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext == null) {
			return;
		}

		Map<String, Object> settings = accessControlContext.getSettings();

		Integer serviceDepth = (Integer)settings.get(
			AccessControlContext.Settings.SERVICE_DEPTH.toString());

		if (serviceDepth == null) {
			serviceDepth = Integer.valueOf(1);
		}
		else {
			serviceDepth++;
		}

		settings.put(
			AccessControlContext.Settings.SERVICE_DEPTH.toString(),
			serviceDepth);
	}

	private final AccessControlAdvisor _accessControlAdvisor =
		new AccessControlAdvisorImpl();

}