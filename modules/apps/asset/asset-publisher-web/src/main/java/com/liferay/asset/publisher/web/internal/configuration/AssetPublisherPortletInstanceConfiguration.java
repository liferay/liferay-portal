/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Juergen Kappler
 */
@ExtendedObjectClassDefinition(
	category = "assets",
	scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
)
@Meta.OCD(
	id = "com.liferay.asset.publisher.web.internal.configuration.AssetPublisherPortletInstanceConfiguration",
	localization = "content/Language",
	name = "asset-publisher-portlet-instance-configuration-name"
)
public interface AssetPublisherPortletInstanceConfiguration {

	/**
	 * Set the name of the display style which will be used by default.
	 *
	 * @return default display style.
	 */
	@Meta.AD(
		deflt = "abstracts",
		description = "default-display-style-key-description",
		name = "default-display-style", required = false
	)
	public String defaultDisplayStyle();

	/**
	 * Input a list of comma delimited display styles that will be available in
	 * the configuration screen of the Asset Publisher portlet.
	 *
	 * @return available display styles.
	 */
	@Meta.AD(
		deflt = "table|title-list|abstracts|full-content",
		description = "display-styles-key-description", name = "display-styles",
		required = false
	)
	public String[] displayStyles();

	/**
	 * Localized template for new asset entry added email message body.
	 *
	 * @return message body template for asset entry added email.
	 */
	@Meta.AD(
		deflt = "${resource:com/liferay/asset/publisher/web/portlet/email/dependencies/email_asset_entry_added_body.tmpl}",
		description = "email-asset-entry-added-body-description",
		name = "email-asset-entry-added-body", required = false
	)
	public LocalizedValuesMap emailAssetEntryAddedBody();

	/**
	 * Set this to <code>true</code> if you want to enable asset entry added
	 * email.
	 *
	 * @return <code>true</code> if asset entry added email is enabled.
	 */
	@Meta.AD(
		deflt = "true",
		description = "email-asset-entry-added-enabled-description",
		name = "email-asset-entry-added-enabled", required = false
	)
	public boolean emailAssetEntryAddedEnabled();

	/**
	 * Localized template for new asset entry added email message subject.
	 *
	 * @return message subject template for asset entry added email.
	 */
	@Meta.AD(
		deflt = "${resource:com/liferay/asset/publisher/web/portlet/email/dependencies/email_asset_entry_added_subject.tmpl}",
		description = "email-asset-entry-added-subject-description",
		name = "email-asset-entry-added-subject", required = false
	)
	public LocalizedValuesMap emailAssetEntryAddedSubject();

	/**
	 * Set an email address to use in asset entry added email.
	 *
	 * @return default email address to use in asset entry added email.
	 */
	@Meta.AD(
		deflt = "",
		description = "set-the-address-from-which-asset-entry-added-emails-will-be-sent",
		name = "email-from-address", required = false
	)
	public String emailFromAddress();

	/**
	 * Set a sender name to use in asset entry added email.
	 *
	 * @return default sender name to use in asset entry added email.
	 */
	@Meta.AD(
		deflt = "", description = "email-from-name-description",
		name = "email-from-name", required = false
	)
	public String emailFromName();

}