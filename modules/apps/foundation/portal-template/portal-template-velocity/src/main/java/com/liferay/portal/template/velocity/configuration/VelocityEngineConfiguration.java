/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template.velocity.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Peter Fellwock
 */
@ExtendedObjectClassDefinition(category = "foundation")
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
		deflt = "60", name = "resource-modification-check-interval",
		required = false
	)
	public int resourceModificationCheckInterval();

	@Meta.AD(
		deflt = "com.liferay.portal.json.jabsorb.serializer.LiferayJSONDeserializationWhitelist|java.lang.Class|java.lang.ClassLoader|java.lang.Compiler|java.lang.Package|java.lang.Process|java.lang.Runtime|java.lang.RuntimePermission|java.lang.SecurityManager|java.lang.System|java.lang.Thread|java.lang.ThreadGroup|java.lang.ThreadLocal",
		name = "restricted-classes", required = false
	)
	public String[] restrictedClasses();

	@Meta.AD(
		deflt = "java.lang.reflect", name = "restricted-packages",
		required = false
	)
	public String[] restrictedPackages();

	@Meta.AD(
		deflt = "httpUtilUnsafe|serviceLocator|staticFieldGetter|utilLocator",
		name = "restricted-variables", required = false
	)
	public String[] restrictedVariables();

	@Meta.AD(
		deflt = "VM_global_library.vm|VM_liferay.vm",
		name = "velocity-macro-library", required = false
	)
	public String[] velocimacroLibrary();

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

}