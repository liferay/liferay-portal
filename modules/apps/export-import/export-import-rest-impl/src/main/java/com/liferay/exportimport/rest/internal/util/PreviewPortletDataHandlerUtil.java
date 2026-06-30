/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerChoice;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.rest.dto.v1_0.Choice;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandlerBoolean;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandlerChoice;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandlerControl;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandlerSection;
import com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandlerSetting;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Daniel Raposo
 */
public class PreviewPortletDataHandlerUtil {

	public static final String PRIVATE_PAGES_CONTROL_NAME =
		"privateLayoutPages";

	public static final String PUBLIC_PAGES_CONTROL_NAME = "publicLayoutPages";

	public static void addConfigurationPreviewPortletDataHandler(
		Locale locale, Portlet portlet,
		PortletDataHandlerControl[] portletDataHandlerControls,
		Map<String, List<PreviewPortletDataHandler>>
			previewPortletDataHandlersMap) {

		if (ArrayUtil.isEmpty(portletDataHandlerControls)) {
			return;
		}

		List<PreviewPortletDataHandler> previewPortletDataHandlers =
			previewPortletDataHandlersMap.computeIfAbsent(
				ExportImportConstants.SECTION_KEY_CONFIGURATION,
				key -> new ArrayList<>());

		previewPortletDataHandlers.add(
			new PreviewPortletDataHandler() {
				{
					setAdditionCount(() -> 0L);
					setDeletionCount(() -> 0L);
					setLabel(() -> PortalUtil.getPortletTitle(portlet, locale));
					setName(
						() ->
							PortletDataHandlerKeys.PORTLET_CONFIGURATION +
								StringPool.UNDERLINE +
									portlet.getRootPortletId());
					setPreviewPortletDataHandlerControls(
						() -> _toPreviewPortletDataHandlerControls(
							locale, portletDataHandlerControls,
							portlet.getRootPortletId()));
				}
			});
	}

	public static void addLayoutSetPreviewPortletDataHandler(
		long companyId, Locale locale, Portlet portlet,
		PortletDataHandler portletDataHandler,
		Map<String, List<PreviewPortletDataHandler>>
			previewPortletDataHandlersMap,
		long privateLayoutDeletionCount, ManifestSummary privateManifestSummary,
		long publicLayoutDeletionCount, ManifestSummary publicManifestSummary) {

		if ((portletDataHandler == null) ||
			!portletDataHandler.isEnabled(companyId)) {

			return;
		}

		long publicLayoutAdditionCount = Math.max(
			0L, portletDataHandler.getExportModelCount(publicManifestSummary));

		long privateLayoutAdditionCount = 0;

		if (privateManifestSummary != null) {
			privateLayoutAdditionCount = Math.max(
				0L,
				portletDataHandler.getExportModelCount(privateManifestSummary));
		}

		if ((privateLayoutAdditionCount == 0) &&
			(privateLayoutDeletionCount == 0) &&
			(publicLayoutAdditionCount == 0) &&
			(publicLayoutDeletionCount == 0)) {

			return;
		}

		long finalPrivateLayoutAdditionCount = privateLayoutAdditionCount;

		String sectionKey = portletDataHandler.getSectionKey();

		if (sectionKey == null) {
			sectionKey = ExportImportConstants.SECTION_KEY_OTHER;
		}

		List<PreviewPortletDataHandler> previewPortletDataHandlers =
			previewPortletDataHandlersMap.computeIfAbsent(
				sectionKey, key -> new ArrayList<>());

		previewPortletDataHandlers.add(
			new PreviewPortletDataHandler() {
				{
					setAdditionCount(() -> publicLayoutAdditionCount);
					setDeletionCount(() -> publicLayoutDeletionCount);
					setDescription(
						() -> portletDataHandler.getDescription(locale));
					setLabel(
						() -> {
							String portletTitle = portletDataHandler.getTitle(
								locale);

							if (portletTitle != null) {
								return portletTitle;
							}

							return PortalUtil.getPortletTitle(portlet, locale);
						});
					setName(
						() ->
							PortletDataHandlerKeys.PORTLET_DATA + "_" +
								portlet.getPortletId());
					setPreviewPortletDataHandlerControls(
						() -> new PreviewPortletDataHandlerControl[] {
							_toPreviewPortletDataHandlerChoice(
								locale, portlet,
								finalPrivateLayoutAdditionCount,
								privateLayoutDeletionCount,
								privateManifestSummary != null,
								publicLayoutAdditionCount,
								publicLayoutDeletionCount)
						});
					setTag(() -> portletDataHandler.getTag(locale));
				}
			});
	}

	public static void addPreviewPortletDataHandler(
			long companyId, Locale locale, ManifestSummary manifestSummary,
			Portlet portlet, PortletDataHandler portletDataHandler,
			UnsafeFunction
				<PortletDataHandler, PortletDataHandlerControl[], Exception>
					portletDataHandlerControlsUnsafeFunction,
			boolean portletScoped,
			Map<String, List<PreviewPortletDataHandler>>
				previewPortletDataHandlersMap)
		throws Exception {

		if ((portletDataHandler == null) ||
			!portletDataHandler.isEnabled(companyId)) {

			return;
		}

		long modelAdditionCount = Math.max(
			0L, portletDataHandler.getExportModelCount(manifestSummary));
		long modelDeletionCount = Math.max(
			0L,
			manifestSummary.getModelDeletionCount(
				portletDataHandler.getDeletionSystemEventStagedModelTypes()));

		if ((modelAdditionCount == 0) && (modelDeletionCount == 0)) {
			return;
		}

		String sectionKey = portletDataHandler.getSectionKey();

		if (portletScoped) {
			sectionKey = ExportImportConstants.SECTION_KEY_CONTENT;
		}

		if (sectionKey == null) {
			sectionKey = ExportImportConstants.SECTION_KEY_OTHER;
		}

		List<PreviewPortletDataHandler> previewPortletDataHandlers =
			previewPortletDataHandlersMap.computeIfAbsent(
				sectionKey, key -> new ArrayList<>());

		previewPortletDataHandlers.add(
			_toPreviewPortletDataHandler(
				modelAdditionCount, locale, manifestSummary, modelDeletionCount,
				portlet, portletDataHandler,
				portletDataHandlerControlsUnsafeFunction.apply(
					portletDataHandler)));
	}

	public static long getAdditionCount(
		Map<String, List<PreviewPortletDataHandler>>
			previewPortletDataHandlersMap) {

		long additionCount = 0;

		for (List<PreviewPortletDataHandler> previewPortletDataHandlers :
				previewPortletDataHandlersMap.values()) {

			for (PreviewPortletDataHandler previewPortletDataHandler :
					previewPortletDataHandlers) {

				additionCount += previewPortletDataHandler.getAdditionCount();
			}
		}

		return additionCount;
	}

	public static long getDeletionCount(
		Map<String, List<PreviewPortletDataHandler>>
			previewPortletDataHandlersMap) {

		long deletionCount = 0;

		for (List<PreviewPortletDataHandler> previewPortletDataHandlers :
				previewPortletDataHandlersMap.values()) {

			for (PreviewPortletDataHandler previewPortletDataHandler :
					previewPortletDataHandlers) {

				deletionCount += previewPortletDataHandler.getDeletionCount();
			}
		}

		return deletionCount;
	}

	public static PreviewPortletDataHandlerSection[]
		toPreviewPortletDataHandlerSections(
			Locale locale,
			Map<String, List<PreviewPortletDataHandler>>
				previewPortletDataHandlersMap) {

		return TransformUtil.transformToArray(
			previewPortletDataHandlersMap.entrySet(),
			entry -> {
				long additionCount = 0;
				long deletionCount = 0;

				List<PreviewPortletDataHandler> previewPortletDataHandlerList =
					entry.getValue();

				for (PreviewPortletDataHandler previewPortletDataHandler :
						previewPortletDataHandlerList) {

					additionCount +=
						previewPortletDataHandler.getAdditionCount();
					deletionCount +=
						previewPortletDataHandler.getDeletionCount();
				}

				long finalAdditionCount = additionCount;
				long finalDeletionCount = deletionCount;

				return new PreviewPortletDataHandlerSection() {
					{
						setAdditionCount(() -> finalAdditionCount);
						setDeletionCount(() -> finalDeletionCount);
						setLabel(
							() -> LanguageUtil.get(locale, entry.getKey()));
						setName(entry::getKey);
						setPreviewPortletDataHandlers(
							() -> previewPortletDataHandlerList.toArray(
								new PreviewPortletDataHandler[0]));
					}
				};
			},
			PreviewPortletDataHandlerSection.class);
	}

	private static PreviewPortletDataHandler _toPreviewPortletDataHandler(
		long modelAdditionCount, Locale locale, ManifestSummary manifestSummary,
		long modelDeletionCount, Portlet portlet,
		PortletDataHandler portletDataHandler,
		PortletDataHandlerControl[] sourcePortletDataHandlerControls) {

		return new PreviewPortletDataHandler() {
			{
				setAdditionCount(() -> modelAdditionCount);
				setDeletionCount(() -> modelDeletionCount);
				setDescription(() -> portletDataHandler.getDescription(locale));
				setLabel(
					() -> {
						String portletTitle = portletDataHandler.getTitle(
							locale);

						if (portletTitle != null) {
							return portletTitle;
						}

						return PortalUtil.getPortletTitle(portlet, locale);
					});
				setName(
					() ->
						PortletDataHandlerKeys.PORTLET_DATA + "_" +
							portlet.getPortletId());
				setPreviewPortletDataHandlerControls(
					() -> _toPreviewPortletDataHandlerControls(
						locale, manifestSummary,
						sourcePortletDataHandlerControls));
				setTag(() -> portletDataHandler.getTag(locale));
			}
		};
	}

	private static PreviewPortletDataHandlerChoice
		_toPreviewPortletDataHandlerChoice(
			Locale locale, Portlet portlet, long privateLayoutAdditionCount,
			long privateLayoutDeletionCount, boolean privateLayoutsEnabled,
			long publicLayoutAdditionCount, long publicLayoutDeletionCount) {

		return new PreviewPortletDataHandlerChoice() {
			{
				setChoices(
					() -> {
						List<Choice> choices = new ArrayList<>();

						choices.add(
							new Choice() {
								{
									setAdditionCount(
										() -> publicLayoutAdditionCount);
									setDeletionCount(
										() -> publicLayoutDeletionCount);
									setLabel(
										() -> LanguageUtil.get(
											locale, "public-pages"));
									setName(() -> PUBLIC_PAGES_CONTROL_NAME);
								}
							});

						if (privateLayoutsEnabled) {
							choices.add(
								new Choice() {
									{
										setAdditionCount(
											() -> privateLayoutAdditionCount);
										setDeletionCount(
											() -> privateLayoutDeletionCount);
										setLabel(
											() -> LanguageUtil.get(
												locale, "private-pages"));
										setName(
											() -> PRIVATE_PAGES_CONTROL_NAME);
									}
								});
						}

						return choices.toArray(new Choice[0]);
					});
				setDefaultChoice(() -> PUBLIC_PAGES_CONTROL_NAME);
				setLabel(() -> PortalUtil.getPortletTitle(portlet, locale));
				setName(() -> "layoutSet");
				setType(() -> PreviewPortletDataHandlerControl.Type.CHOICE);
			}
		};
	}

	private static PreviewPortletDataHandlerControl
		_toPreviewPortletDataHandlerControl(
			Locale locale, ManifestSummary manifestSummary,
			PortletDataHandlerControl portletDataHandlerControl) {

		if (portletDataHandlerControl instanceof
				PortletDataHandlerBoolean portletDataHandlerBoolean) {

			if (portletDataHandlerBoolean.getClassName() == null) {
				return new PreviewPortletDataHandlerSetting() {
					{
						setDefaultState(
							portletDataHandlerBoolean::getDefaultState);
						setDisabled(portletDataHandlerBoolean::isDisabled);
						setLabel(
							() -> LanguageUtil.get(
								locale, portletDataHandlerBoolean.getLabel()));
						setName(
							PortletDataHandlerControl.getNamespacedName(
								portletDataHandlerBoolean.getNamespace(),
								portletDataHandlerBoolean.getName()));
						setPreviewPortletDataHandlerControls(
							() -> _toPreviewPortletDataHandlerControls(
								locale, manifestSummary,
								portletDataHandlerBoolean.
									getChildrenPortletDataHandlerControls()));
						setType(
							() ->
								PreviewPortletDataHandlerControl.Type.SETTING);
					}
				};
			}

			long modelAdditionCount = Math.max(
				0L,
				manifestSummary.getModelAdditionCount(
					new StagedModelType(
						portletDataHandlerBoolean.getClassName(),
						portletDataHandlerBoolean.getReferrerClassName())));
			long modelDeletionCount = Math.max(
				0L,
				manifestSummary.getModelDeletionCount(
					new StagedModelType(
						portletDataHandlerBoolean.getClassName(),
						portletDataHandlerBoolean.getReferrerClassName())));

			if ((modelAdditionCount == 0) && (modelDeletionCount == 0)) {
				return null;
			}

			return new PreviewPortletDataHandlerBoolean() {
				{
					setAdditionCount(() -> modelAdditionCount);
					setDefaultState(portletDataHandlerBoolean::getDefaultState);
					setDeletionCount(() -> modelDeletionCount);
					setDisabled(portletDataHandlerBoolean::isDisabled);
					setLabel(
						() -> LanguageUtil.get(
							locale, portletDataHandlerBoolean.getLabel()));
					setName(
						PortletDataHandlerControl.getNamespacedName(
							portletDataHandlerBoolean.getNamespace(),
							portletDataHandlerBoolean.getName()));
					setPreviewPortletDataHandlerControls(
						() -> _toPreviewPortletDataHandlerControls(
							locale, manifestSummary,
							portletDataHandlerBoolean.
								getChildrenPortletDataHandlerControls()));
					setType(
						() -> PreviewPortletDataHandlerControl.Type.BOOLEAN);
				}
			};
		}

		if (portletDataHandlerControl instanceof
				PortletDataHandlerChoice portletDataHandlerChoice) {

			return new PreviewPortletDataHandlerChoice() {
				{
					setChoices(
						() -> TransformUtil.transform(
							portletDataHandlerChoice.getChoices(),
							choice -> new Choice() {
								{
									setLabel(
										() -> LanguageUtil.get(locale, choice));
									setName(() -> choice);
								}
							},
							Choice.class));
					setDefaultChoice(
						portletDataHandlerChoice::getDefaultChoice);
					setDisabled(portletDataHandlerChoice::isDisabled);
					setLabel(
						() -> LanguageUtil.get(
							locale, portletDataHandlerChoice.getLabel()));
					setName(
						PortletDataHandlerControl.getNamespacedName(
							portletDataHandlerChoice.getNamespace(),
							portletDataHandlerChoice.getName()));
					setType(() -> PreviewPortletDataHandlerControl.Type.CHOICE);
				}
			};
		}

		return null;
	}

	private static PreviewPortletDataHandlerControl[]
		_toPreviewPortletDataHandlerControls(
			Locale locale, ManifestSummary manifestSummary,
			PortletDataHandlerControl[] portletDataHandlerControls) {

		if (ArrayUtil.isEmpty(portletDataHandlerControls)) {
			return null;
		}

		return TransformUtil.transform(
			portletDataHandlerControls,
			portletDataHandlerControl -> _toPreviewPortletDataHandlerControl(
				locale, manifestSummary, portletDataHandlerControl),
			PreviewPortletDataHandlerControl.class);
	}

	private static PreviewPortletDataHandlerControl[]
		_toPreviewPortletDataHandlerControls(
			Locale locale,
			PortletDataHandlerControl[] portletDataHandlerControls,
			String rootPortletId) {

		return TransformUtil.transform(
			portletDataHandlerControls,
			portletDataHandlerControl ->
				new PreviewPortletDataHandlerSetting() {
					{
						setDefaultState(() -> true);
						setDisabled(portletDataHandlerControl::isDisabled);
						setLabel(
							() -> LanguageUtil.get(
								locale, portletDataHandlerControl.getLabel()));
						setName(
							() ->
								portletDataHandlerControl.getName() +
									StringPool.UNDERLINE + rootPortletId);
						setType(
							() ->
								PreviewPortletDataHandlerControl.Type.SETTING);
					}
				},
			PreviewPortletDataHandlerControl.class);
	}

}