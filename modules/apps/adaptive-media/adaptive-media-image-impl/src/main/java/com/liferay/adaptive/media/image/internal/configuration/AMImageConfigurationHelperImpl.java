/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.configuration;

import com.liferay.adaptive.media.exception.AMImageConfigurationException;
import com.liferay.adaptive.media.exception.AMImageConfigurationException.InvalidStateAMImageConfigurationException;
import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.internal.configuration.util.AMImageConfigurationEntryParserUtil;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.journal.util.JournalContent;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.PortletPreferencesSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ValidatorException;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = AMImageConfigurationHelper.class)
public class AMImageConfigurationHelperImpl
	implements AMImageConfigurationHelper {

	@Override
	public AMImageConfigurationEntry addAMImageConfigurationEntry(
			long companyId, String name, String description, String uuid,
			Map<String, String> properties)
		throws AMImageConfigurationException, IOException {

		_checkName(name);
		_checkProperties(properties);

		_normalizeProperties(properties);

		_checkUuid(uuid);

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			getAMImageConfigurationEntries(
				companyId, amImageConfigurationEntry -> true);

		_checkDuplicatesName(amImageConfigurationEntries, name);

		_checkDuplicatesUuid(amImageConfigurationEntries, uuid);

		List<AMImageConfigurationEntry> updatedAMImageConfigurationEntries =
			new ArrayList<>(amImageConfigurationEntries);

		updatedAMImageConfigurationEntries.removeIf(
			amImageConfigurationEntry -> uuid.equals(
				amImageConfigurationEntry.getUUID()));

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				name, description, uuid, properties, true);

		updatedAMImageConfigurationEntries.add(amImageConfigurationEntry);

		_updateConfiguration(companyId, updatedAMImageConfigurationEntries);

		_journalContent.clearCache();

		return amImageConfigurationEntry;
	}

	@Override
	public void deleteAMImageConfigurationEntry(long companyId, String uuid)
		throws InvalidStateAMImageConfigurationException, IOException {

		AMImageConfigurationEntry amImageConfigurationEntry =
			getAMImageConfigurationEntry(companyId, uuid);

		if (amImageConfigurationEntry == null) {
			return;
		}

		if (amImageConfigurationEntry.isEnabled()) {
			throw new InvalidStateAMImageConfigurationException();
		}

		forceDeleteAMImageConfigurationEntry(companyId, uuid);
	}

	@Override
	public void disableAMImageConfigurationEntry(long companyId, String uuid)
		throws IOException {

		AMImageConfigurationEntry amImageConfigurationEntry =
			getAMImageConfigurationEntry(companyId, uuid);

		if ((amImageConfigurationEntry == null) ||
			!amImageConfigurationEntry.isEnabled()) {

			return;
		}

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			getAMImageConfigurationEntries(
				companyId, curConfigurationEntry -> true);

		List<AMImageConfigurationEntry> updatedAMImageConfigurationEntries =
			new ArrayList<>(amImageConfigurationEntries);

		updatedAMImageConfigurationEntries.removeIf(
			curConfigurationEntry -> uuid.equals(
				curConfigurationEntry.getUUID()));

		AMImageConfigurationEntry newAMImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				amImageConfigurationEntry.getName(),
				amImageConfigurationEntry.getDescription(),
				amImageConfigurationEntry.getUUID(),
				amImageConfigurationEntry.getProperties(), false);

		updatedAMImageConfigurationEntries.add(newAMImageConfigurationEntry);

		_updateConfiguration(companyId, updatedAMImageConfigurationEntries);

		_journalContent.clearCache();
	}

	@Override
	public void enableAMImageConfigurationEntry(long companyId, String uuid)
		throws IOException {

		AMImageConfigurationEntry amImageConfigurationEntry =
			getAMImageConfigurationEntry(companyId, uuid);

		if ((amImageConfigurationEntry == null) ||
			amImageConfigurationEntry.isEnabled()) {

			return;
		}

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			getAMImageConfigurationEntries(
				companyId, curConfigurationEntry -> true);

		List<AMImageConfigurationEntry> updatedAMImageConfigurationEntries =
			new ArrayList<>(amImageConfigurationEntries);

		updatedAMImageConfigurationEntries.removeIf(
			curConfigurationEntry -> uuid.equals(
				curConfigurationEntry.getUUID()));

		AMImageConfigurationEntry newAMImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				amImageConfigurationEntry.getName(),
				amImageConfigurationEntry.getDescription(),
				amImageConfigurationEntry.getUUID(),
				amImageConfigurationEntry.getProperties(), true);

		updatedAMImageConfigurationEntries.add(newAMImageConfigurationEntry);

		_updateConfiguration(companyId, updatedAMImageConfigurationEntries);

		_journalContent.clearCache();
	}

	@Override
	public void forceDeleteAMImageConfigurationEntry(
			long companyId, String uuid)
		throws IOException {

		AMImageConfigurationEntry amImageConfigurationEntry =
			getAMImageConfigurationEntry(companyId, uuid);

		if (amImageConfigurationEntry == null) {
			return;
		}

		_amImageEntryLocalService.deleteAMImageEntries(
			companyId, amImageConfigurationEntry);

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			getAMImageConfigurationEntries(
				companyId, curConfigurationEntry -> true);

		List<AMImageConfigurationEntry> updatedAMImageConfigurationEntries =
			new ArrayList<>(amImageConfigurationEntries);

		updatedAMImageConfigurationEntries.removeIf(
			curConfigurationEntry -> uuid.equals(
				curConfigurationEntry.getUUID()));

		_updateConfiguration(companyId, updatedAMImageConfigurationEntries);

		_journalContent.clearCache();
	}

	@Override
	public Collection<AMImageConfigurationEntry> getAMImageConfigurationEntries(
		long companyId) {

		List<AMImageConfigurationEntry> amImageConfigurationEntries =
			_getAMImageConfigurationEntries(companyId);

		amImageConfigurationEntries = ListUtil.filter(
			amImageConfigurationEntries, AMImageConfigurationEntry::isEnabled);

		amImageConfigurationEntries.sort(
			Comparator.comparing(AMImageConfigurationEntry::getName));

		return amImageConfigurationEntries;
	}

	@Override
	public Collection<AMImageConfigurationEntry> getAMImageConfigurationEntries(
		long companyId,
		Predicate<? super AMImageConfigurationEntry> predicate) {

		List<AMImageConfigurationEntry> amImageConfigurationEntries =
			_getAMImageConfigurationEntries(companyId);

		amImageConfigurationEntries = ListUtil.filter(
			amImageConfigurationEntries,
			(Predicate<AMImageConfigurationEntry>)predicate);

		amImageConfigurationEntries.sort(
			Comparator.comparing(AMImageConfigurationEntry::getName));

		return amImageConfigurationEntries;
	}

	@Override
	public AMImageConfigurationEntry getAMImageConfigurationEntry(
		long companyId, String configurationEntryUUID) {

		List<AMImageConfigurationEntry> amImageConfigurationEntries =
			_getAMImageConfigurationEntries(companyId);

		amImageConfigurationEntries = ListUtil.filter(
			amImageConfigurationEntries,
			amImageConfigurationEntry -> configurationEntryUUID.equals(
				amImageConfigurationEntry.getUUID()));

		if (amImageConfigurationEntries.isEmpty()) {
			return null;
		}

		return amImageConfigurationEntries.get(0);
	}

	@Override
	public AMImageConfigurationEntry updateAMImageConfigurationEntry(
			long companyId, String oldUuid, String name, String description,
			String newUuid, Map<String, String> properties)
		throws AMImageConfigurationException, IOException {

		_checkName(name);
		_checkProperties(properties);

		_normalizeProperties(properties);

		_checkUuid(newUuid);

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			getAMImageConfigurationEntries(
				companyId, amImageConfigurationEntry -> true);

		AMImageConfigurationEntry oldAMImageConfigurationEntry = null;

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			if (oldUuid.equals(amImageConfigurationEntry.getUUID())) {
				oldAMImageConfigurationEntry = amImageConfigurationEntry;

				break;
			}
		}

		if (oldAMImageConfigurationEntry == null) {
			throw new AMImageConfigurationException.
				NoSuchAMImageConfigurationException("{uuid=" + oldUuid + "}");
		}

		if (!name.equals(oldAMImageConfigurationEntry.getName())) {
			_checkDuplicatesName(amImageConfigurationEntries, name);
		}

		if (!oldUuid.equals(newUuid)) {
			_checkDuplicatesUuid(amImageConfigurationEntries, newUuid);
		}

		List<AMImageConfigurationEntry> updatedAMImageConfigurationEntries =
			new ArrayList<>(amImageConfigurationEntries);

		updatedAMImageConfigurationEntries.removeIf(
			amImageConfigurationEntry -> oldUuid.equals(
				amImageConfigurationEntry.getUUID()));

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				name, description, newUuid, properties,
				oldAMImageConfigurationEntry.isEnabled());

		updatedAMImageConfigurationEntries.add(amImageConfigurationEntry);

		_updateConfiguration(companyId, updatedAMImageConfigurationEntries);

		_journalContent.clearCache();

		return amImageConfigurationEntry;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_portalCache =
			(PortalCache<Long, Serializable>)_multiVMPool.getPortalCache(
				AMImageConfigurationHelperImpl.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(
			AMImageConfigurationHelperImpl.class.getName());
	}

	private void _checkDuplicatesName(
			Collection<AMImageConfigurationEntry> amImageConfigurationEntries,
			String name)
		throws AMImageConfigurationException {

		AMImageConfigurationEntry duplicateNameAMImageConfigurationEntry = null;

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			if (name.equals(amImageConfigurationEntry.getName())) {
				duplicateNameAMImageConfigurationEntry =
					amImageConfigurationEntry;

				break;
			}
		}

		if (duplicateNameAMImageConfigurationEntry != null) {
			throw new AMImageConfigurationException.
				DuplicateAMImageConfigurationNameException();
		}
	}

	private void _checkDuplicatesUuid(
			Collection<AMImageConfigurationEntry> amImageConfigurationEntries,
			String uuid)
		throws AMImageConfigurationException {

		AMImageConfigurationEntry duplicateUuidAMImageConfigurationEntry = null;

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			if (uuid.equals(amImageConfigurationEntry.getUUID())) {
				duplicateUuidAMImageConfigurationEntry =
					amImageConfigurationEntry;

				break;
			}
		}

		if (duplicateUuidAMImageConfigurationEntry != null) {
			throw new AMImageConfigurationException.
				DuplicateAMImageConfigurationUuidException();
		}
	}

	private void _checkName(String name) throws AMImageConfigurationException {
		if (Validator.isNull(name)) {
			throw new AMImageConfigurationException.InvalidNameException();
		}
	}

	private void _checkProperties(Map<String, String> properties)
		throws AMImageConfigurationException {

		String maxHeightString = properties.get("max-height");

		if (Validator.isNotNull(maxHeightString) &&
			!maxHeightString.equals("0") &&
			!_isPositiveNumber(maxHeightString)) {

			throw new AMImageConfigurationException.InvalidHeightException();
		}

		String maxWidthString = properties.get("max-width");

		if (Validator.isNotNull(maxWidthString) &&
			!maxWidthString.equals("0") && !_isPositiveNumber(maxWidthString)) {

			throw new AMImageConfigurationException.InvalidWidthException();
		}

		if ((Validator.isNull(maxHeightString) ||
			 maxHeightString.equals("0")) &&
			(Validator.isNull(maxWidthString) || maxWidthString.equals("0"))) {

			throw new AMImageConfigurationException.
				RequiredWidthOrHeightException();
		}
	}

	private void _checkUuid(String uuid) throws AMImageConfigurationException {
		if (Validator.isNull(uuid)) {
			throw new AMImageConfigurationException.InvalidUuidException();
		}

		Matcher matcher = _uuidPattern.matcher(uuid);

		if (!matcher.matches()) {
			throw new AMImageConfigurationException.InvalidUuidException();
		}
	}

	private List<AMImageConfigurationEntry> _getAMImageConfigurationEntries(
		long companyId) {

		ArrayList<AMImageConfigurationEntry> amImageConfigurationEntries =
			(ArrayList<AMImageConfigurationEntry>)_portalCache.get(companyId);

		if (amImageConfigurationEntries != null) {
			return amImageConfigurationEntries;
		}

		try {
			amImageConfigurationEntries =
				(ArrayList<AMImageConfigurationEntry>)
					TransformUtil.transformToList(
						_getImageVariants(
							FallbackKeysSettingsUtil.getSettings(
								new CompanyServiceSettingsLocator(
									companyId,
									AMImageCompanyConfiguration.class.
										getName()))),
						AMImageConfigurationEntryParserUtil::parse);

			PortalCacheHelperUtil.putWithoutReplicator(
				_portalCache, companyId, amImageConfigurationEntries);

			return amImageConfigurationEntries;
		}
		catch (SettingsException settingsException) {
			throw new AMRuntimeException.InvalidConfiguration(
				settingsException);
		}
	}

	private String[] _getImageVariants(Settings settings) {
		PortletPreferencesSettings portletPreferencesSettings =
			(PortletPreferencesSettings)settings;

		PortletPreferences portletPreferences =
			portletPreferencesSettings.getPortletPreferences();

		Map<String, String[]> map = portletPreferences.getMap();

		String[] imageVariants = map.get("imageVariants");

		if (imageVariants == null) {
			imageVariants = settings.getValues("imageVariants", new String[0]);
		}

		return imageVariants;
	}

	private final boolean _isPositiveNumber(String s) {
		Matcher matcher = _positiveNumberPattern.matcher(s);

		return matcher.matches();
	}

	private void _normalizeProperties(Map<String, String> properties) {
		String maxHeightString = properties.get("max-height");
		String maxWidthString = properties.get("max-width");

		if (Validator.isNotNull(maxHeightString) &&
			Validator.isNotNull(maxWidthString)) {

			return;
		}

		if (Validator.isNull(maxHeightString)) {
			properties.put("max-height", "0");
		}

		if (Validator.isNull(maxWidthString)) {
			properties.put("max-width", "0");
		}
	}

	private void _updateConfiguration(
			long companyId,
			List<AMImageConfigurationEntry> amImageConfigurationEntries)
		throws IOException {

		try {
			Settings settings = FallbackKeysSettingsUtil.getSettings(
				new CompanyServiceSettingsLocator(
					companyId, AMImageCompanyConfiguration.class.getName()));

			ModifiableSettings modifiableSettings =
				settings.getModifiableSettings();

			modifiableSettings.setValues(
				"imageVariants",
				TransformUtil.transformToArray(
					amImageConfigurationEntries,
					AMImageConfigurationEntryParserUtil::getConfigurationString,
					String.class));

			modifiableSettings.store();

			_portalCache.put(
				companyId,
				(ArrayList<AMImageConfigurationEntry>)
					amImageConfigurationEntries);
		}
		catch (SettingsException | ValidatorException exception) {
			throw new AMRuntimeException.InvalidConfiguration(exception);
		}
	}

	private static final Pattern _positiveNumberPattern = Pattern.compile(
		"\\d*[1-9]\\d*");
	private static final Pattern _uuidPattern = Pattern.compile("^(?:\\w|-)+$");

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<Long, Serializable> _portalCache;

}