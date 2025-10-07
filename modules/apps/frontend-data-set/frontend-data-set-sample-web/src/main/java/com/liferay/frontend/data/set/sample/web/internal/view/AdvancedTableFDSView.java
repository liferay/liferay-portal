/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.view;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.FDSCellRendererCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.data.set.constants.FDSTimeZoneBehaviorConstants;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.DateTimeFDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 * @author Javier de Arcos
 */
@Component(
	enabled = true,
	property = "frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
	service = FDSView.class
)
public class AdvancedTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"id", "id",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"sampleEditMessage"
			).setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			"title", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			"creator.name", "author",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"customAuthorTableCellRenderer")
		).add(
			"description", "description"
		).add(
			_addDateTimeFDSTableSchemaField()
		).add(
			"color", "color",
			fdsTableSchemaField -> {
				boolean clientExtension = false;
				String moduleName = null;

				List<FDSCellRendererCET> fdsCellRendererCETs =
					(List)_cetManager.getCETs(
						CompanyThreadLocal.getCompanyId(), null,
						ClientExtensionEntryConstants.TYPE_FDS_CELL_RENDERER,
						Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS),
						null);

				// Use the UI client extension if available

				for (FDSCellRendererCET fdsCellRendererCET :
						fdsCellRendererCETs) {

					if (!fdsCellRendererCET.isReadOnly()) {
						clientExtension = true;
						moduleName =
							"default from " + fdsCellRendererCET.getURL();

						break;
					}
				}

				// Use the workspace client extension if available

				if (moduleName == null) {
					for (FDSCellRendererCET fdsCellRendererCET :
							fdsCellRendererCETs) {

						if (Objects.equals(
								fdsCellRendererCET.getExternalReferenceCode(),
								"LXC:liferay-sample-fds-cell-renderer")) {

							clientExtension = true;
							moduleName =
								"default from " + fdsCellRendererCET.getURL();
						}
					}
				}

				// Use the built-in AMD provided sample as a last resort

				if (moduleName == null) {
					ServiceContext serviceContext =
						ServiceContextThreadLocal.getServiceContext();

					AbsolutePortalURLBuilder absolutePortalURLBuilder =
						_absolutePortalURLBuilderFactory.
							getAbsolutePortalURLBuilder(
								serviceContext.getRequest());

					String moduleURL = absolutePortalURLBuilder.forESModule(
						"frontend-data-set-sample-web", "index.js"
					).build();

					moduleName = "{GreenCheckColorTableCell} from " + moduleURL;
				}

				fdsTableSchemaField.setContentRendererClientExtension(
					clientExtension);
				fdsTableSchemaField.setContentRendererModuleURL(moduleName);
			}
		).add(
			"size", "size",
			fdsTableSchemaField -> {
				String purposefullyInvalidURL = StringUtil.randomString();

				fdsTableSchemaField.setContentRendererClientExtension(true);
				fdsTableSchemaField.setContentRendererModuleURL(
					"default from " + purposefullyInvalidURL);
			}
		).add(
			"status", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"status")
		).build();
	}

	@Override
	public String getName() {
		return "customizedTable";
	}

	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public boolean isQuickActionsEnabled() {
		return true;
	}

	private DateTimeFDSTableSchemaField _addDateTimeFDSTableSchemaField() {
		DateTimeFDSTableSchemaField dateFDSTableSchemaField =
			new DateTimeFDSTableSchemaField();

		dateFDSTableSchemaField.setContentRenderer(
			"dateTime"
		).setFieldName(
			"date"
		).setLabel(
			"date"
		);

		dateFDSTableSchemaField.setTimeZoneBehavior(
			FDSTimeZoneBehaviorConstants.APPLY_THEME_DISPLAY_TIME_ZONE);

		return dateFDSTableSchemaField;
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private CETManager _cetManager;

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}