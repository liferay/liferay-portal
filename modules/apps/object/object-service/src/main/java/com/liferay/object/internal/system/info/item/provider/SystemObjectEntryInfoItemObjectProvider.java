/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.BaseInfoItemObjectProvider;
import com.liferay.object.entry.util.ObjectEntryDTOConverterUtil;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.util.Collections;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class SystemObjectEntryInfoItemObjectProvider
	extends BaseInfoItemObjectProvider<SystemObjectEntry> {

	public SystemObjectEntryInfoItemObjectProvider(
		DTOConverterRegistry dtoConverterRegistry,
		SystemObjectDefinitionManager systemObjectDefinitionManager) {

		_dtoConverterRegistry = dtoConverterRegistry;
		_systemObjectDefinitionManager = systemObjectDefinitionManager;
	}

	@Override
	protected SystemObjectEntry doGetInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			try {
				if (themeDisplay == null) {
					return new SystemObjectEntry(
						classPKInfoItemIdentifier.getClassPK(),
						StringPool.BLANK, Collections.emptyMap());
				}

				DTOConverter<?, ?> dtoConverter =
					ObjectEntryDTOConverterUtil.getDTOConverter(
						_dtoConverterRegistry, _systemObjectDefinitionManager);

				Object dto = dtoConverter.toDTO(
					new DefaultDTOConverterContext(
						false, Collections.emptyMap(), _dtoConverterRegistry,
						classPKInfoItemIdentifier.getClassPK(),
						themeDisplay.getLocale(), null,
						themeDisplay.getUser()));

				if (dto == null) {
					return new SystemObjectEntry(
						classPKInfoItemIdentifier.getClassPK(),
						StringPool.BLANK, Collections.emptyMap());
				}

				return new SystemObjectEntry(
					classPKInfoItemIdentifier.getClassPK(), StringPool.BLANK,
					ObjectMapperUtil.readValue(Map.class, dto.toString()));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				throw new NoSuchInfoItemException(
					"Unable to get info item for " +
						classPKInfoItemIdentifier.getClassPK());
			}
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;

		try {
			Object dto = ObjectEntryDTOConverterUtil.toDTO(
				_systemObjectDefinitionManager.
					getBaseModelByExternalReferenceCode(
						ercInfoItemIdentifier.getExternalReferenceCode(),
						serviceContext.getCompanyId()),
				_dtoConverterRegistry, _systemObjectDefinitionManager,
				themeDisplay.getUser());

			return new SystemObjectEntry(
				0L, ercInfoItemIdentifier.getExternalReferenceCode(),
				ObjectMapperUtil.readValue(Map.class, dto.toString()));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new NoSuchInfoItemException(
				StringBundler.concat(
					"No info item found with company ID ",
					serviceContext.getCompanyId(),
					" and external reference code ",
					ercInfoItemIdentifier.getExternalReferenceCode()),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SystemObjectEntryInfoItemObjectProvider.class);

	private final DTOConverterRegistry _dtoConverterRegistry;
	private final SystemObjectDefinitionManager _systemObjectDefinitionManager;

}