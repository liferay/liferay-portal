/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.display.context;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.item.selector.ItemSelector;
import com.liferay.object.display.context.ObjectEntryDisplayContext;
import com.liferay.object.display.context.ObjectEntryDisplayContextFactory;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = ObjectEntryDisplayContextFactory.class)
public class ObjectEntryDisplayContextFactoryImpl
	implements ObjectEntryDisplayContextFactory {

	@Override
	public ObjectEntryDisplayContext create(
		HttpServletRequest httpServletRequest) {

		return new ObjectEntryDisplayContextImpl(
			_ddmExpressionFactory, _ddmFormRenderer, httpServletRequest,
			_itemSelector, _objectDefinitionLocalService,
			_objectEntryManagerRegistry, _objectEntryLocalService,
			_objectEntryService, _objectFieldBusinessTypeRegistry,
			_objectFieldLocalService, _objectLayoutLocalService,
			_objectRelationshipLocalService, _objectScopeProviderRegistry);
	}

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private DDMFormRenderer _ddmFormRenderer;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectLayoutLocalService _objectLayoutLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}