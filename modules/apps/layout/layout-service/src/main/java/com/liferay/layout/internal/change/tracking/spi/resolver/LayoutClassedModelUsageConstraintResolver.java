/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.change.tracking.spi.resolver;

import com.liferay.change.tracking.spi.resolver.ConstraintResolver;
import com.liferay.change.tracking.spi.resolver.context.ConstraintResolverContext;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.language.LanguageResources;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = ConstraintResolver.class)
public class LayoutClassedModelUsageConstraintResolver
	implements ConstraintResolver<LayoutClassedModelUsage> {

	@Override
	public String getConflictDescriptionKey() {
		return "duplicate-layout-classed-model-usages";
	}

	@Override
	public Class<LayoutClassedModelUsage> getModelClass() {
		return LayoutClassedModelUsage.class;
	}

	@Override
	public String getResolutionDescriptionKey() {
		return "duplicate-layout-classed-model-usages-were-removed";
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return LanguageResources.getResourceBundle(locale);
	}

	@Override
	public String[] getUniqueIndexColumnNames() {
		return new String[] {
			"groupId", "classExternalReferenceCode", "classNameId", "classPK",
			"containerKey", "containerType", "plid"
		};
	}

	@Override
	public void resolveConflict(
			ConstraintResolverContext<LayoutClassedModelUsage>
				constraintResolverContext)
		throws PortalException {

		_layoutClassedModelUsageLocalService.deleteLayoutClassedModelUsage(
			constraintResolverContext.getSourceCTModel());
	}

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

}