/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Peter Fellwock
 */
@ExtendedObjectClassDefinition(category = "template-engines")
@Meta.OCD(
	id = "com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration",
	localization = "content/Language",
	name = "freemarker-engine-configuration-name"
)
public interface FreeMarkerEngineConfiguration {

	@Meta.AD(deflt = "0", name = "async-render-timeout", required = false)
	public long asyncRenderTimeout();

	@Meta.AD(
		deflt = "10", name = "async-render-timeout-threshold", required = false
	)
	public int asyncRenderTimeoutThreshold();

	@Meta.AD(
		deflt = "2147483647", name = "async-render-thread-pool-max-size",
		required = false
	)
	public int asyncRenderThreadPoolMaxSize();

	@Meta.AD(
		deflt = "2147483647", name = "async-render-thread-pool-max-queue-size",
		required = false
	)
	public int asyncRenderThreadPoolMaxQueueSize();

	@Meta.AD(deflt = "false", name = "localized-lookup", required = false)
	public boolean localizedLookup();

	@Meta.AD(deflt = "0", name = "loop-count-threshold", required = false)
	public int loopCountThreshold();

	@Meta.AD(
		deflt = "true", name = "include-navigation-items-in-the-context",
		required = false
	)
	public boolean includeNavItemsInTheContext();

	@Meta.AD(name = "allowed-classes", required = false)
	public String[] allowedClasses();

	@Meta.AD(
		deflt = "com.ibm.*|com.liferay.portal.json.jabsorb.serializer.LiferayJSONDeserializationWhitelist|com.liferay.portal.kernel.service.persistence.BasePersistence|com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence|com.liferay.portal.spring.context.*|io.undertow.*|java.lang.Class|java.lang.ClassLoader|java.lang.Compiler|java.lang.Package|java.lang.Process|java.lang.Runtime|java.lang.RuntimePermission|java.lang.SecurityManager|java.lang.System|java.lang.Thread|java.lang.ThreadGroup|java.lang.ThreadLocal|org.apache.*|org.glassfish.*|org.jboss.*|org.springframework.*|org.wildfly.*|weblogic.*",
		name = "restricted-classes", required = false
	)
	public String[] restrictedClasses();

	@Meta.AD(
		deflt = "com.liferay.portal.model.impl.CompanyImpl#getKey|com.liferay.portal.model.impl.UserImpl#getLastFailedLoginDate|com.liferay.portal.model.impl.UserImpl#getLastLoginDate|com.liferay.portal.model.impl.UserImpl#getLastLoginIP|com.liferay.portal.model.impl.UserImpl#getLoginDate|com.liferay.portal.model.impl.UserImpl#getLoginIP|com.liferay.portal.model.impl.UserImpl#getPassword|com.liferay.portal.model.impl.UserImpl#getPasswordEncrypted|com.liferay.portal.model.impl.UserImpl#getPasswordReset|com.liferay.portal.model.impl.UserImpl#getReminderQueryAnswer|com.liferay.portal.model.impl.UserImpl#getReminderQueryQuestion|com.liferay.portal.model.impl.UserImpl#toString",
		name = "restricted-methods", required = false
	)
	public String[] restrictedMethods();

	@Meta.AD(
		deflt = "httpUtilUnsafe|objectUtil|serviceLocator|staticFieldGetter|staticUtil",
		name = "restricted-variables", required = false
	)
	public String[] restrictedVariables();

	@Meta.AD(
		deflt = "false", name = "log-template-exceptions", required = false
	)
	public boolean logTemplateExceptions();

	@Meta.AD(
		deflt = "rethrow", name = "template-exception-handler", required = false
	)
	public String templateExceptionHandler();

	@Meta.AD(
		deflt = "FTL_liferay.ftl as liferay", name = "macro-library",
		required = false
	)
	public String[] macroLibrary();

}