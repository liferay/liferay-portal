/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.velocity.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Peter Fellwock
 */
@ExtendedObjectClassDefinition(category = "template-engines")
@Meta.OCD(
	id = "com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration",
	localization = "content/Language",
	name = "velocity-engine-configuration-name"
)
public interface VelocityEngineConfiguration {

	@Meta.AD(
		deflt = "false", name = "directive-if-to-string-null-check",
		required = false
	)
	public boolean directiveIfToStringNullCheck();

	@Meta.AD(
		deflt = "true", name = "include-navigation-items-in-the-context",
		required = false
	)
	public boolean includeNavItemsInTheContext();

	@Meta.AD(
		deflt = "org.apache.velocity.runtime.log.SimpleLog4JLogSystem",
		name = "logger", required = false
	)
	public String logger();

	@Meta.AD(
		deflt = "org.apache.velocity", name = "logger-category",
		required = false
	)
	public String loggerCategory();

	@Meta.AD(
		deflt = "com.liferay.portal.json.jabsorb.serializer.LiferayJSONDeserializationWhitelist|java.lang.Class|java.lang.ClassLoader|java.lang.Package|java.lang.Process|java.lang.Runtime|java.lang.RuntimePermission|java.lang.SecurityManager|java.lang.System|java.lang.Thread|java.lang.ThreadGroup|java.lang.ThreadLocal",
		name = "restricted-classes", required = false
	)
	public String[] restrictedClasses();

	@Meta.AD(
		deflt = "com.liferay.portal.model.impl.CompanyImpl#getKey",
		name = "restricted-methods", required = false
	)
	public String[] restrictedMethods();

	@Meta.AD(
		deflt = "com.liferay.portal.spring.context|com.ibm|io.undertow|java.lang.reflect|org.apache|org.glassfish|org.jboss|org.springframework|org.wildfly|weblogic",
		name = "restricted-packages", required = false
	)
	public String[] restrictedPackages();

	@Meta.AD(
		deflt = "httpUtil|httpUtilUnsafe|portletConfig|propsUtil|serviceLocator|staticFieldGetter",
		name = "restricted-variables", required = false
	)
	public String[] restrictedVariables();

	@Meta.AD(
		deflt = "VM_global_library.vm|VM_liferay.vm",
		name = "velocity-macro-library", required = false
	)
	public String[] velocimacroLibrary();

}