/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.content.web.internal.configuration.JournalContentWebConfigurationValues;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * <p>
 * Provides the Journal Content portlet export and import functionality, which
 * is to clone the article, structure, and template referenced in the Journal
 * Content portlet if the article is associated with the layout's group. Upon
 * import, a new instance of the corresponding article, structure, and template
 * will be created or updated. The author of the newly created objects are
 * determined by the JournalCreationStrategy class defined in
 * <i>portal.properties</i>.
 * </p>
 *
 * <p>
 * This <code>PortletDataHandler</code> differs from from
 * <code>JournalPortletDataHandlerImpl</code> in that it only exports articles
 * referenced in Journal Content portlets. Articles not displayed in Journal
 * Content portlets will not be exported unless
 * <code>JournalPortletDataHandlerImpl</code> is activated.
 * </p>
 *
 * @author Joel Kozikowski
 * @author Raymond Augé
 * @author Bruno Farache
 * @author Daniel Kocsis
 * @author Máté Thurzó
 * @see    com.liferay.journal.internal.exportimport.creation.strategy.JournalCreationStrategy
 * @see    PortletDataHandler
 */
@Component(
	property = "jakarta.portlet.name=" + JournalContentPortletKeys.JOURNAL_CONTENT,
	service = PortletDataHandler.class
)
public class JournalContentPortletDataHandler extends BasePortletDataHandler {

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getNamespace() {
		return _journalPortletDataHandler.getNamespace();
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTLET_INSTANCE);

		setDataPortletPreferences(
			"articleExternalReferenceCode", "articleId",
			"ddmTemplateExternalReferenceCode", "ddmTemplateKey",
			"groupExternalReferenceCode", "groupId");

		setExportControls(
			new PortletDataHandlerBoolean(
				null, "selected-web-content", true, true, null,
				JournalArticle.class.getName()));
		setPublishToLiveByDefault(
			JournalContentWebConfigurationValues.PUBLISH_TO_LIVE_BY_DEFAULT);
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletPreferences == null) {
			return portletPreferences;
		}

		portletPreferences.setValue(
			"articleExternalReferenceCode", StringPool.BLANK);
		portletPreferences.setValue(
			"ddmTemplateExternalReferenceCode", StringPool.BLANK);
		portletPreferences.setValue(
			"groupExternalReferenceCode", StringPool.BLANK);

		return portletPreferences;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + JournalPortletKeys.JOURNAL + ")"
	)
	private PortletDataHandler _journalPortletDataHandler;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}