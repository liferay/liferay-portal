/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.site.generator.web.internal.frontend.data.set.view;

import com.liferay.content.site.generator.web.internal.constants.ContentSiteGeneratorFDSNames;
import com.liferay.frontend.data.set.constants.FDSTimeZoneBehaviorConstants;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.DateTimeFDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mylena Monte
 */
@Component(
	property = "frontend.data.set.name=" + ContentSiteGeneratorFDSNames.GENERATIONS,
	service = FDSView.class
)
public class ContentSiteGeneratorTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		String moduleURL = null;

		if ((serviceContext != null) && (serviceContext.getRequest() != null)) {
			moduleURL =
				_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
					serviceContext.getRequest()
				).forESModule(
					"content-site-generator-web", "index.js"
				).build();
		}

		String finalModuleURL = moduleURL;

		return fdsTableSchemaBuilder.add(
			"title", "name",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"view"
			).setContentRenderer(
				"actionLink"
			)
		).add(
			"itemCount", "items"
		).add(
			"targetLanguages", "languages",
			fdsTableSchemaField -> {
				fdsTableSchemaField.setContentRendererClientExtension(true);
				fdsTableSchemaField.setContentRendererModuleURL(
					"{CSGGenerationLanguagesDataRenderer} from " +
						finalModuleURL);
			}
		).add(
			"creator.name", "created-by"
		).add(
			"generationStatus", "status",
			fdsTableSchemaField -> {
				fdsTableSchemaField.setContentRendererClientExtension(true);
				fdsTableSchemaField.setContentRendererModuleURL(
					"{CSGGenerationStatusDataRenderer} from " + finalModuleURL);
			}
		).add(
			_dateTimeField("dateModified", "modified")
		).build();
	}

	@Override
	public boolean isDefault(String fdsName) {
		return true;
	}

	private DateTimeFDSTableSchemaField _dateTimeField(
		String fieldName, String label) {

		DateTimeFDSTableSchemaField dateTimeFDSTableSchemaField =
			new DateTimeFDSTableSchemaField();

		dateTimeFDSTableSchemaField.setContentRenderer(
			"dateTime"
		).setFieldName(
			fieldName
		).setLabel(
			label
		);

		dateTimeFDSTableSchemaField.setTimeZoneBehavior(
			FDSTimeZoneBehaviorConstants.APPLY_THEME_DISPLAY_TIME_ZONE);

		return dateTimeFDSTableSchemaField;
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}